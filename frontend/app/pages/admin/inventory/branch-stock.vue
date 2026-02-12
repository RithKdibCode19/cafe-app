<template>
  <div class="space-y-6">
    <!-- Header -->
    <div
      class="flex flex-col md:flex-row md:items-center justify-between gap-4"
    >
      <div>
        <h1 class="text-2xl font-bold text-white tracking-tight">
          Branch Inventory
        </h1>
        <p class="text-neutral-500 text-sm">
          Monitor and manage stock levels by location
        </p>
      </div>

      <div class="flex items-center gap-3">
        <select
          v-model="selectedBranchId"
          class="bg-neutral-800 border border-neutral-700 text-white text-sm rounded-xl px-4 py-2.5 focus:ring-2 focus:ring-primary-500 transition-all outline-none min-w-[200px]"
        >
          <option value="" disabled>Select Branch</option>
          <option v-for="b in branches" :key="b.branchId" :value="b.branchId">
            {{ b.name }}
          </option>
        </select>

        <button
          @click="showTransferModal = true"
          class="bg-primary-600 hover:bg-primary-500 text-white px-4 py-2.5 rounded-xl font-bold text-sm transition-all shadow-lg shadow-primary-900/20 flex items-center gap-2"
        >
          <svg
            xmlns="http://www.w3.org/2000/svg"
            class="w-4 h-4"
            fill="none"
            viewBox="0 0 24 24"
            stroke="currentColor"
          >
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="2"
              d="M8 7h12m0 0l-4-4m4 4l-4 4m0 6H4m0 0l4 4m-4-4l4-4"
            />
          </svg>
          Transfer Stock
        </button>
      </div>
    </div>

    <!-- Inventory Table -->
    <div
      class="bg-neutral-800 rounded-3xl border border-neutral-700 overflow-hidden"
    >
      <div
        v-if="loading"
        class="p-12 flex flex-col items-center justify-center gap-4"
      >
        <div
          class="w-10 h-10 border-4 border-primary-500/20 border-t-primary-500 rounded-full animate-spin"
        ></div>
        <span
          class="text-neutral-500 font-bold text-xs uppercase tracking-widest"
          >Loading branch stock...</span
        >
      </div>

      <div v-else-if="!selectedBranchId" class="p-20 text-center">
        <div
          class="w-20 h-20 bg-neutral-900 rounded-full flex items-center justify-center mx-auto mb-6 border border-neutral-700"
        >
          <svg
            xmlns="http://www.w3.org/2000/svg"
            class="w-10 h-10 text-neutral-600"
            fill="none"
            viewBox="0 0 24 24"
            stroke="currentColor"
          >
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="2"
              d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4"
            />
          </svg>
        </div>
        <h3 class="text-xl font-bold text-white mb-2">No Branch Selected</h3>
        <p class="text-neutral-500 text-sm max-w-xs mx-auto">
          Please select a branch from the dropdown above to view its current
          inventory levels.
        </p>
      </div>

      <div v-else class="overflow-x-auto">
        <table class="w-full text-left">
          <thead>
            <tr class="bg-neutral-900/50">
              <th
                class="px-6 py-4 text-[10px] font-black text-neutral-500 uppercase tracking-widest"
              >
                Ingredient
              </th>
              <th
                class="px-6 py-4 text-[10px] font-black text-neutral-500 uppercase tracking-widest text-right"
              >
                Current Stock
              </th>
              <th
                class="px-6 py-4 text-[10px] font-black text-neutral-500 uppercase tracking-widest text-right"
              >
                Reorder Level
              </th>
              <th
                class="px-6 py-4 text-[10px] font-black text-neutral-500 uppercase tracking-widest text-center"
              >
                Status
              </th>
            </tr>
          </thead>
          <tbody class="divide-y divide-neutral-700/50">
            <tr
              v-for="item in inventory"
              :key="item.branchStockId"
              class="hover:bg-neutral-700/30 transition-colors group"
            >
              <td class="px-6 py-4">
                <div class="flex items-center gap-3">
                  <div
                    class="w-10 h-10 rounded-xl bg-neutral-900 flex items-center justify-center border border-neutral-700 group-hover:border-primary-500/30 transition-colors"
                  >
                    <span class="text-lg font-bold text-neutral-500">{{
                      item.ingredient.name.charAt(0)
                    }}</span>
                  </div>
                  <div>
                    <div class="text-sm font-bold text-white">
                      {{ item.ingredient.name }}
                    </div>
                    <div
                      class="text-[10px] text-neutral-500 font-medium uppercase tracking-tighter"
                    >
                      {{ item.ingredient.sku || "No SKU" }}
                    </div>
                  </div>
                </div>
              </td>
              <td class="px-6 py-4 text-right">
                <span
                  :class="[
                    'text-sm font-black',
                    item.currentStock <= item.reorderLevel
                      ? 'text-red-400'
                      : 'text-success-400',
                  ]"
                >
                  {{ item.currentStock }} {{ item.ingredient.unit }}
                </span>
              </td>
              <td class="px-6 py-4 text-right">
                <span class="text-xs font-bold text-neutral-400"
                  >{{ item.reorderLevel }} {{ item.ingredient.unit }}</span
                >
              </td>
              <td class="px-6 py-4 text-center">
                <span
                  v-if="item.currentStock <= 0"
                  class="text-[10px] bg-red-500/10 text-red-500 px-2 py-0.5 rounded-full font-black uppercase border border-red-500/20"
                  >Out of Stock</span
                >
                <span
                  v-else-if="item.currentStock <= item.reorderLevel"
                  class="text-[10px] bg-warning-500/10 text-warning-500 px-2 py-0.5 rounded-full font-black uppercase border border-warning-500/20"
                  >Low Stock</span
                >
                <span
                  v-else
                  class="text-[10px] bg-success-500/10 text-success-500 px-2 py-0.5 rounded-full font-black uppercase border border-success-500/20"
                  >Healthy</span
                >
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Transfer Modal -->
    <div
      v-if="showTransferModal"
      class="fixed inset-0 z-50 flex items-center justify-center bg-black/80 backdrop-blur-sm p-4"
    >
      <div
        class="bg-neutral-800 rounded-3xl shadow-2xl w-full max-w-md border border-neutral-700 overflow-hidden animate-in zoom-in duration-200"
      >
        <div
          class="p-6 border-b border-neutral-700 flex justify-between items-center bg-neutral-900/50"
        >
          <h3 class="text-lg font-bold text-white tracking-tight">
            Inter-Branch Transfer
          </h3>
          <button
            @click="showTransferModal = false"
            class="text-neutral-400 hover:text-white transition-colors"
          >
            <svg
              xmlns="http://www.w3.org/2000/svg"
              class="w-6 h-6"
              fill="none"
              viewBox="0 0 24 24"
              stroke="currentColor"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M6 18L18 6M6 6l12 12"
              />
            </svg>
          </button>
        </div>

        <div class="p-8 space-y-6">
          <div class="grid grid-cols-2 gap-4">
            <div>
              <label
                class="text-[10px] text-neutral-500 font-black uppercase tracking-widest block mb-2"
                >From Branch</label
              >
              <select
                v-model="transferForm.fromBranchId"
                class="w-full bg-neutral-900 border border-neutral-700 rounded-xl px-4 py-3 text-white text-sm focus:ring-2 focus:ring-primary-500 transition-all outline-none"
              >
                <option
                  v-for="b in branches"
                  :key="b.branchId"
                  :value="b.branchId"
                >
                  {{ b.name }}
                </option>
              </select>
            </div>
            <div>
              <label
                class="text-[10px] text-neutral-500 font-black uppercase tracking-widest block mb-2"
                >To Branch</label
              >
              <select
                v-model="transferForm.toBranchId"
                class="w-full bg-neutral-900 border border-neutral-700 rounded-xl px-4 py-3 text-white text-sm focus:ring-2 focus:ring-primary-500 transition-all outline-none"
              >
                <option
                  v-for="b in branches"
                  :key="b.branchId"
                  :value="b.branchId"
                >
                  {{ b.name }}
                </option>
              </select>
            </div>
          </div>

          <div>
            <label
              class="text-[10px] text-neutral-500 font-black uppercase tracking-widest block mb-2"
              >Ingredient</label
            >
            <select
              v-model="transferForm.ingredientId"
              class="w-full bg-neutral-900 border border-neutral-700 rounded-xl px-4 py-3 text-white text-sm focus:ring-2 focus:ring-primary-500 transition-all outline-none text-center"
            >
              <optgroup label="Search for items...">
                <option
                  v-for="item in allIngredients"
                  :key="item.ingredientId"
                  :value="item.ingredientId"
                >
                  {{ item.name }} ({{ item.unit }})
                </option>
              </optgroup>
            </select>
          </div>

          <div>
            <label
              class="text-[10px] text-neutral-500 font-black uppercase tracking-widest block mb-2 text-center"
              >Amount to Transfer</label
            >
            <div class="relative">
              <input
                v-model.number="transferForm.amount"
                type="number"
                class="w-full bg-neutral-900 border-2 border-primary-500/20 rounded-2xl p-6 text-center text-3xl font-black text-white focus:border-primary-500 transition-all outline-none"
                placeholder="0.00"
              />
            </div>
          </div>

          <button
            @click="submitTransfer"
            :disabled="
              submitting ||
              !transferForm.amount ||
              transferForm.fromBranchId === transferForm.toBranchId
            "
            class="w-full py-4 bg-primary-600 hover:bg-primary-500 disabled:bg-neutral-700 text-white rounded-2xl font-black uppercase tracking-widest transition-all shadow-xl shadow-primary-900/20 flex items-center justify-center gap-3"
          >
            <span
              v-if="submitting"
              class="w-5 h-5 border-2 border-white/20 border-t-white rounded-full animate-spin"
            ></span>
            {{ submitting ? "Processing..." : "Execute Transfer" }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from "vue";

const { get, post } = useApi();
const toast = useToast();

const branches = ref<any[]>([]);
const selectedBranchId = ref<number | "">("");
const inventory = ref<any[]>([]);
const loading = ref(false);
const showTransferModal = ref(false);
const submitting = ref(false);
const allIngredients = ref<any[]>([]);

const transferForm = ref({
  fromBranchId: "",
  toBranchId: "",
  ingredientId: "",
  amount: 0,
});

const fetchBranches = async () => {
  try {
    const data = await get<any[]>("/branches");
    branches.value = data || [];
    if (branches.value.length > 0) {
      selectedBranchId.value = branches.value[0].branchId;
    }
  } catch (e) {
    console.error("Failed to fetch branches", e);
  }
};

const fetchAllIngredients = async () => {
  try {
    const data = await get<any[]>("/ingredients");
    allIngredients.value = data || [];
  } catch (e) {
    console.error("Failed to fetch ingredients", e);
  }
};

const fetchInventory = async () => {
  if (!selectedBranchId.value) return;
  loading.value = true;
  try {
    const data = await get<any[]>(
      `/inventory/branch/${selectedBranchId.value}`,
    );
    inventory.value = data || [];
  } catch (e) {
    console.error("Failed to fetch inventory", e);
  } finally {
    loading.value = false;
  }
};

const submitTransfer = async () => {
  submitting.value = true;
  try {
    await post("/inventory/branch/transfer", null, {
      fromBranchId: transferForm.value.fromBranchId,
      toBranchId: transferForm.value.toBranchId,
      ingredientId: transferForm.value.ingredientId,
      amount: transferForm.value.amount,
    });
    toast.success("Stock transfer successful");
    showTransferModal.value = false;
    fetchInventory();
  } catch (e: any) {
    toast.error(e.response?.data?.message || "Transfer failed");
  } finally {
    submitting.value = false;
  }
};

watch(selectedBranchId, fetchInventory);

onMounted(() => {
  fetchBranches();
  fetchAllIngredients();
});
</script>
