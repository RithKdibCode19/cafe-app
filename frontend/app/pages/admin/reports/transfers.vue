<template>
  <NuxtLayout name="admin">
    <div class="space-y-6">
      <UiBreadcrumb
        :items="[
          { label: 'Reports', href: '/admin/reports' },
          { label: 'Stock Transfer History' },
        ]"
      />

      <!-- Header -->
      <div
        class="flex flex-col md:flex-row md:items-center justify-between gap-4"
      >
        <div>
          <h1 class="text-2xl font-bold text-neutral-900 dark:text-white">
            Stock Transfer History
          </h1>
          <p class="text-neutral-500 dark:text-neutral-400">
            Audit movements of ingredients between branches.
          </p>
        </div>

        <div class="flex items-center gap-2">
          <input
            type="date"
            v-model="startDate"
            class="bg-white dark:bg-neutral-800 border border-neutral-200 dark:border-neutral-700 rounded-xl px-4 py-2 text-sm"
          />
          <span class="text-neutral-400">to</span>
          <input
            type="date"
            v-model="endDate"
            class="bg-white dark:bg-neutral-800 border border-neutral-200 dark:border-neutral-700 rounded-xl px-4 py-2 text-sm"
          />
          <button
            @click="fetchTransfers"
            class="btn-primary"
            :disabled="loading"
          >
            Update
          </button>
        </div>
      </div>

      <!-- Content -->
      <div v-if="loading" class="card p-12 flex justify-center">
        <div
          class="animate-spin rounded-full h-8 w-8 border-b-2 border-primary-500"
        ></div>
      </div>

      <div v-else-if="transfers.length > 0" class="card overflow-hidden">
        <div class="overflow-x-auto">
          <table class="w-full text-left font-sans">
            <thead
              class="bg-neutral-50 dark:bg-neutral-800/50 text-xs font-bold text-neutral-500 uppercase tracking-widest"
            >
              <tr>
                <th class="px-6 py-4">Date & Time</th>
                <th class="px-6 py-4">Ingredient</th>
                <th class="px-6 py-4">From Branch</th>
                <th class="px-6 py-4">To Branch</th>
                <th class="px-6 py-4 text-right">Quantity</th>
                <th class="px-6 py-4">Transferred By</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-neutral-100 dark:divide-neutral-800">
              <tr
                v-for="t in transfers"
                :key="t.transferId"
                class="hover:bg-neutral-50 dark:hover:bg-neutral-800/50 transition-colors"
              >
                <td class="px-6 py-4 whitespace-nowrap text-sm">
                  {{ formatDate(t.transferDate) }}
                </td>
                <td class="px-6 py-4">
                  <div class="font-bold text-neutral-900 dark:text-white">
                    {{ t.ingredientName }}
                  </div>
                </td>
                <td class="px-6 py-4 whitespace-nowrap">
                  <span
                    class="px-3 py-1 bg-neutral-100 dark:bg-neutral-800 rounded-full text-xs font-medium"
                  >
                    {{ t.fromBranchName }}
                  </span>
                </td>
                <td
                  class="px-6 py-4 whitespace-nowrap text-primary-600 dark:text-primary-400 font-bold"
                >
                  → {{ t.toBranchName }}
                </td>
                <td class="px-6 py-4 text-right font-mono font-bold">
                  {{ t.quantity }}
                  <span
                    class="text-[10px] text-neutral-400 uppercase tracking-tighter"
                    >{{ t.unit }}</span
                  >
                </td>
                <td
                  class="px-6 py-4 text-sm text-neutral-600 dark:text-neutral-400"
                >
                  {{ t.transferredByName }}
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <!-- Empty State -->
      <div v-else class="card p-20 text-center space-y-4">
        <div
          class="w-20 h-20 bg-neutral-100 dark:bg-neutral-800 rounded-full flex items-center justify-center mx-auto text-neutral-400"
        >
          <svg
            xmlns="http://www.w3.org/2000/svg"
            class="w-10 h-10"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            stroke-width="2"
          >
            <path d="m16 3 4 4-4 4" />
            <path d="M20 7H4" />
            <path d="m8 21-4-4 4-4" />
            <path d="M4 17h16" />
          </svg>
        </div>
        <div>
          <h3 class="text-lg font-bold">No Transfer History</h3>
          <p class="text-neutral-500">
            No stock transfers found for the selected period.
          </p>
        </div>
      </div>
    </div>
  </NuxtLayout>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";

definePageMeta({
  layout: false,
});

const { get } = useApi();

const startDate = ref(
  new Date(Date.now() - 30 * 24 * 60 * 60 * 1000).toISOString().split("T")[0],
);
const endDate = ref(new Date().toISOString().split("T")[0]);
const loading = ref(true);
const transfers = ref<any[]>([]);

const fetchTransfers = async () => {
  loading.value = true;
  try {
    const res = await get<any>(
      `/reports/transfers?startDate=${startDate.value}&endDate=${endDate.value}`,
    );
    if (res?.data) {
      transfers.value = res.data;
    }
  } catch (err) {
    console.error("Failed to fetch transfers", err);
  } finally {
    loading.value = false;
  }
};

const formatDate = (dateStr: string) => {
  const date = new Date(dateStr);
  return new Intl.DateTimeFormat("en-GB", {
    day: "2-digit",
    month: "short",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  }).format(date);
};

onMounted(() => {
  fetchTransfers();
});
</script>

<style scoped>
.btn-primary {
  @apply bg-primary-600 hover:bg-primary-700 text-white rounded-xl px-6 py-2 transition-all active:scale-95 disabled:opacity-50 font-bold text-sm;
}
</style>
