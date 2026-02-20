<template>
  <NuxtLayout name="admin">
    <div class="space-y-6">
      <UiBreadcrumb
        :items="[
          { label: 'Reports', href: '/admin/reports' },
          { label: 'Inventory Audit' },
        ]"
      />

      <!-- Header -->
      <div
        class="flex flex-col md:flex-row md:items-center justify-between gap-4"
      >
        <div>
          <h1 class="text-2xl font-bold text-neutral-900 dark:text-white">
            Inventory Audit
          </h1>
          <p class="text-neutral-500 dark:text-neutral-400">
            Track stock valuation, movements, and wastage.
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
          <button @click="fetchReport" class="btn-primary" :disabled="loading">
            Update
          </button>
          <button
            @click="downloadAudit"
            class="flex items-center gap-2 px-4 py-2 bg-neutral-100 dark:bg-neutral-800 hover:bg-neutral-200 dark:hover:bg-neutral-700 text-neutral-700 dark:text-neutral-300 rounded-xl text-sm font-bold transition-colors"
          >
            <svg
              xmlns="http://www.w3.org/2000/svg"
              class="w-4 h-4"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              stroke-width="2"
            >
              <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4" />
              <polyline points="7 10 12 15 17 10" />
              <line x1="12" y1="15" x2="12" y2="3" />
            </svg>
            Export
          </button>
        </div>
      </div>

      <div v-if="loading" class="space-y-6">
        <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
          <div
            v-for="i in 3"
            :key="i"
            class="card h-32 animate-pulse bg-neutral-100 dark:bg-neutral-800 rounded-3xl"
          ></div>
        </div>
        <div class="grid lg:grid-cols-2 gap-6">
          <div
            v-for="i in 2"
            :key="i"
            class="card h-80 animate-pulse bg-neutral-100 dark:bg-neutral-800 rounded-3xl"
          ></div>
        </div>
      </div>

      <template v-else-if="report && movementReport">
        <!-- Summary Cards -->
        <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
          <div
            class="card p-6 bg-gradient-to-br from-primary-500 to-primary-600 text-white"
          >
            <h3
              class="text-sm font-medium text-white/80 uppercase tracking-widest"
            >
              Inventory Value
            </h3>
            <p class="text-3xl font-bold mt-2">
              ${{ report.totalInventoryValue?.toFixed(2) }}
            </p>
            <div class="mt-4 flex items-center text-xs text-white/60">
              <svg
                xmlns="http://www.w3.org/2000/svg"
                class="w-4 h-4 mr-1"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                stroke-width="2"
              >
                <path d="M12 2v20" />
                <path d="m17 5-5-3-5 3" />
                <path d="m17 19-5 3-5-3" />
                <path d="M2 12h20" />
                <path d="m5 7-3 5 3 5" />
                <path d="m19 7 3 5-3 5" />
              </svg>
              Total cost of on-hand items
            </div>
          </div>

          <div
            class="card p-6 bg-white dark:bg-neutral-900 border border-neutral-200 dark:border-neutral-800"
          >
            <h3
              class="text-sm font-medium text-neutral-500 uppercase tracking-widest"
            >
              Wastage Loss
            </h3>
            <p class="text-3xl font-bold mt-2 text-error-600">
              ${{ report.totalWasteCost?.toFixed(2) }}
            </p>
            <p class="text-xs text-neutral-400 mt-4">
              {{ report.wastageSummary?.length || 0 }} loss categories
              identified
            </p>
          </div>

          <div
            class="card p-6 bg-white dark:bg-neutral-900 border border-neutral-200 dark:border-neutral-800"
          >
            <h3
              class="text-sm font-medium text-neutral-500 uppercase tracking-widest"
            >
              Low Stock Items
            </h3>
            <p class="text-3xl font-bold mt-2 text-warning-600">
              {{ report.lowStockItemsCount }}
            </p>
            <NuxtLink
              to="/admin/inventory"
              class="text-xs text-primary-500 font-bold mt-4 inline-block hover:underline"
              >Manage Inventory →</NuxtLink
            >
          </div>
        </div>

        <!-- AI Recommendations -->
        <div
          v-if="movementReport.recommendations?.length"
          class="card p-6 border-primary-500/20 bg-primary-500/5"
        >
          <div class="flex items-center gap-3 mb-4">
            <div
              class="w-8 h-8 rounded-lg bg-primary-500 flex items-center justify-center text-white"
            >
              <svg
                xmlns="http://www.w3.org/2000/svg"
                class="w-5 h-5"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                stroke-width="2"
              >
                <path d="M12 2v8" />
                <path d="m4.93 4.93 5.66 5.66" />
                <path d="M2 12h8" />
                <path d="m4.93 19.07 5.66-5.66" />
                <path d="M12 22v-8" />
                <path d="m19.07 19.07-5.66-5.66" />
                <path d="M22 12h-8" />
                <path d="m19.07 4.93-5.66 5.66" />
              </svg>
            </div>
            <h3 class="text-lg font-bold text-neutral-900 dark:text-white">
              Smart Inventory Insights
            </h3>
          </div>
          <div class="grid md:grid-cols-2 gap-4">
            <div
              v-for="(rec, idx) in movementReport.recommendations"
              :key="idx"
              class="flex items-start gap-3 p-3 bg-white dark:bg-neutral-800 rounded-xl border border-neutral-200 dark:border-neutral-700"
            >
              <div
                class="mt-1 w-1.5 h-1.5 rounded-full bg-primary-500 shrink-0"
              ></div>
              <p class="text-sm text-neutral-600 dark:text-neutral-400">
                {{ rec }}
              </p>
            </div>
          </div>
        </div>

        <!-- Stock Movements Table -->
        <div class="card overflow-hidden">
          <div
            class="p-6 border-b border-neutral-200 dark:border-neutral-800 flex justify-between items-center"
          >
            <h3 class="text-lg font-bold">Ingredient Movement Ledger</h3>
            <div class="relative w-64">
              <input
                v-model="searchQuery"
                type="text"
                placeholder="Search ingredient..."
                class="w-full bg-neutral-50 dark:bg-neutral-800 border-none rounded-xl pl-10 pr-4 py-2 text-sm ring-1 ring-neutral-200 dark:ring-neutral-700"
              />
              <svg
                xmlns="http://www.w3.org/2000/svg"
                class="w-4 h-4 absolute left-3 top-2.5 text-neutral-400"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                stroke-width="2"
              >
                <circle cx="11" cy="11" r="8" />
                <path d="m21 21-4.3-4.3" />
              </svg>
            </div>
          </div>
          <div class="overflow-x-auto">
            <table class="w-full text-left">
              <thead
                class="bg-neutral-50 dark:bg-neutral-800/50 text-xs font-bold text-neutral-500 uppercase tracking-widest"
              >
                <tr>
                  <th class="px-6 py-4">Ingredient</th>
                  <th class="px-6 py-4 text-right">Opening</th>
                  <th class="px-6 py-4 text-right text-success-600">
                    Received (+)
                  </th>
                  <th class="px-6 py-4 text-right text-error-600">Sold (-)</th>
                  <th class="px-6 py-4 text-right text-warning-600">
                    Adjust (+/-)
                  </th>
                  <th class="px-6 py-4 text-right font-bold">Closing</th>
                  <th class="px-6 py-4 text-right">Wastage %</th>
                </tr>
              </thead>
              <tbody
                class="divide-y divide-neutral-100 dark:divide-neutral-800"
              >
                <tr
                  v-for="item in filteredMovements"
                  :key="item.ingredientId"
                  class="hover:bg-neutral-50 dark:hover:bg-neutral-800/50 transition-colors"
                >
                  <td class="px-6 py-4">
                    <div class="font-bold text-neutral-900 dark:text-white">
                      {{ item.name }}
                    </div>
                    <div
                      class="text-[10px] text-neutral-500 uppercase tracking-tighter"
                    >
                      {{ item.sku }}
                    </div>
                  </td>
                  <td class="px-6 py-4 text-right text-sm">
                    {{ item.openingStock?.toFixed(2) }} {{ item.unit }}
                  </td>
                  <td class="px-6 py-4 text-right text-sm text-success-600">
                    +{{ item.received?.toFixed(2) }}
                  </td>
                  <td class="px-6 py-4 text-right text-sm text-error-600">
                    -{{ item.sold?.toFixed(2) }}
                  </td>
                  <td
                    class="px-6 py-4 text-right text-sm"
                    :class="
                      item.adjusted >= 0 ? 'text-success-600' : 'text-error-600'
                    "
                  >
                    {{ item.adjusted >= 0 ? "+" : ""
                    }}{{ item.adjusted?.toFixed(2) }}
                  </td>
                  <td class="px-6 py-4 text-right text-sm font-bold">
                    {{ item.closingStock?.toFixed(2) }} {{ item.unit }}
                  </td>
                  <td class="px-6 py-4 text-right">
                    <span
                      :class="[
                        'px-2 py-1 rounded text-[10px] font-bold',
                        item.wastagePercentage > 5
                          ? 'bg-error-100 text-error-700'
                          : 'bg-success-100 text-success-700',
                      ]"
                    >
                      {{ item.wastagePercentage?.toFixed(1) }}%
                    </span>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>

        <div class="grid lg:grid-cols-2 gap-6">
          <!-- Wastage Breakdown -->
          <div class="card p-6">
            <h3 class="text-lg font-bold text-neutral-900 dark:text-white mb-6">
              Wastage by Reason
            </h3>
            <div class="space-y-4">
              <div
                v-for="w in report.wastageSummary"
                :key="w.reasonType"
                class="group"
              >
                <div class="flex items-center justify-between mb-2">
                  <div class="flex items-center gap-3">
                    <div class="w-2 h-2 rounded-full bg-error-500"></div>
                    <span
                      class="font-medium text-neutral-700 dark:text-neutral-300"
                      >{{ w.reasonType }}</span
                    >
                  </div>
                  <span class="font-bold text-neutral-900 dark:text-white"
                    >${{ w.cost?.toFixed(2) }}</span
                  >
                </div>
                <!-- Progress Bar -->
                <div
                  class="h-2 w-full bg-neutral-100 dark:bg-neutral-800 rounded-full overflow-hidden"
                >
                  <div
                    class="h-full bg-error-500 transition-all duration-1000"
                    :style="{
                      width: `${(w.cost / (report.totalWasteCost || 1)) * 100}%`,
                    }"
                  ></div>
                </div>
                <div
                  class="flex justify-between mt-1 text-[10px] text-neutral-400 font-bold uppercase tracking-tighter"
                >
                  <span>{{ w.occurrence }} Occurrences</span>
                  <span>{{ w.quantity }} units lost</span>
                </div>
              </div>
              <div
                v-if="!report.wastageSummary?.length"
                class="text-center py-12 text-neutral-500"
              >
                No wastage recorded in this period.
              </div>
            </div>
          </div>

          <!-- Inventory Alerts -->
          <div class="card p-6">
            <h3 class="text-lg font-bold text-neutral-900 dark:text-white mb-6">
              Critical Actions
            </h3>
            <div class="space-y-3">
              <div
                class="p-4 bg-warning-50 dark:bg-warning-900/20 border border-warning-200 dark:border-warning-800/50 rounded-2xl flex items-start gap-4"
              >
                <div
                  class="w-10 h-10 rounded-full bg-warning-100 dark:bg-warning-800 flex items-center justify-center flex-shrink-0"
                >
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    class="w-5 h-5 text-warning-600"
                    viewBox="0 0 24 24"
                    fill="none"
                    stroke="currentColor"
                    stroke-width="2"
                  >
                    <path
                      d="m21.73 18-8-14a2 2 0 0 0-3.48 0l-8 14A2 2 0 0 0 4 21h16a2 2 0 0 0 1.73-3Z"
                    />
                    <line x1="12" y1="9" x2="12" y2="13" />
                    <line x1="12" y1="17" x2="12.01" y2="17" />
                  </svg>
                </div>
                <div>
                  <h4 class="font-bold text-warning-900 dark:text-warning-400">
                    Replenishment Needed
                  </h4>
                  <p
                    class="text-sm text-warning-700 dark:text-warning-500/80 mt-1"
                  >
                    {{ report.lowStockItemsCount }} items have fallen below
                    their safety stock level.
                  </p>
                </div>
              </div>

              <div
                class="p-4 bg-error-50 dark:bg-error-900/20 border border-error-200 dark:border-error-800/50 rounded-2xl flex items-start gap-4"
              >
                <div
                  class="w-10 h-10 rounded-full bg-error-100 dark:bg-error-800 flex items-center justify-center flex-shrink-0"
                >
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    class="w-5 h-5 text-error-600"
                    viewBox="0 0 24 24"
                    fill="none"
                    stroke="currentColor"
                    stroke-width="2"
                  >
                    <path
                      d="M3 6h18m-2 0v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"
                    />
                  </svg>
                </div>
                <div>
                  <h4 class="font-bold text-error-900 dark:text-error-400">
                    Waste Reduction
                  </h4>
                  <p class="text-sm text-error-700 dark:text-error-500/80 mt-1">
                    Total waste of ${{ report.totalWasteCost?.toFixed(2) }}
                    accounts for approx.
                    {{
                      (
                        (report.totalWasteCost /
                          (report.totalInventoryValue || 1)) *
                        100
                      ).toFixed(1)
                    }}% of inventory holding value.
                  </p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </template>

      <!-- Empty State -->
      <div v-else class="card p-12 text-center text-neutral-500">
        No report data available. Try selecting a different date range.
      </div>
    </div>
  </NuxtLayout>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from "vue";

definePageMeta({
  layout: false,
});

const { get, download } = useApi();
const config = useRuntimeConfig();

const startDate = ref(
  new Date(Date.now() - 30 * 24 * 60 * 60 * 1000).toISOString().split("T")[0],
);
const endDate = ref(new Date().toISOString().split("T")[0]);
const loading = ref(true);
const report = ref<any>(null);
const movementReport = ref<any>(null);
const searchQuery = ref("");

const filteredMovements = computed(() => {
  if (!movementReport.value?.movements) return [];
  if (!searchQuery.value) return movementReport.value.movements;
  const q = searchQuery.value.toLowerCase();
  return movementReport.value.movements.filter(
    (m: any) =>
      m.name?.toLowerCase().includes(q) || m.sku?.toLowerCase().includes(q),
  );
});

const fetchReport = async () => {
  loading.value = true;
  try {
    const [auditRes, moveRes] = await Promise.all([
      get<any>(
        `/reports/inventory?startDate=${startDate.value}&endDate=${endDate.value}`,
      ),
      get<any>(
        `/reports/stock-movement?startDate=${startDate.value}&endDate=${endDate.value}`,
      ),
    ]);

    if (auditRes?.data) report.value = auditRes.data;
    if (moveRes?.data) movementReport.value = moveRes.data;
  } catch (err) {
    console.error("Failed to fetch inventory reports", err);
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  fetchReport();
});

const downloadAudit = () => {
  const endpoint = `/api/import-export/export/inventory?start=${startDate.value}&end=${endDate.value}`;
  download(
    endpoint,
    `inventory_audit_${startDate.value}_to_${endDate.value}.xlsx`,
  );
};
</script>

<style scoped>
.btn-primary {
  @apply bg-primary-600 hover:bg-primary-700 text-white rounded-xl px-6 py-2 transition-all active:scale-95 disabled:opacity-50 font-bold text-sm;
}
</style>
