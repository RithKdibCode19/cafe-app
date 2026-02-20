<template>
  <NuxtLayout name="admin">
    <div class="space-y-6">
      <UiBreadcrumb
        :items="[
          { label: 'Reports', href: '/admin/reports' },
          { label: 'Sales Report' },
        ]"
      />

      <!-- Header -->
      <div
        class="flex flex-col md:flex-row md:items-center md:justify-between gap-4"
      >
        <div>
          <h1 class="text-2xl font-bold text-neutral-900 dark:text-white">
            Sales Report
          </h1>
          <p class="text-neutral-500 dark:text-neutral-400">
            Analyze your sales performance
          </p>
        </div>

        <!-- Date Range Picker -->
        <div class="flex flex-wrap items-center gap-3">
          <div
            class="flex items-center gap-2 bg-white dark:bg-neutral-800 rounded-xl px-4 py-2 border border-neutral-200 dark:border-neutral-700"
          >
            <label class="text-sm text-neutral-500">From:</label>
            <input
              v-model="startDate"
              type="date"
              class="bg-transparent border-none text-sm text-neutral-900 dark:text-white focus:outline-none"
            />
          </div>
          <div
            class="flex items-center gap-2 bg-white dark:bg-neutral-800 rounded-xl px-4 py-2 border border-neutral-200 dark:border-neutral-700"
          >
            <label class="text-sm text-neutral-500">To:</label>
            <input
              v-model="endDate"
              type="date"
              class="bg-transparent border-none text-sm text-neutral-900 dark:text-white focus:outline-none"
            />
          </div>
          <button @click="fetchReport" class="btn-primary">
            <svg
              xmlns="http://www.w3.org/2000/svg"
              class="w-4 h-4"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              stroke-width="2"
            >
              <path d="M21 12a9 9 0 1 1-6.219-8.56" />
            </svg>
            Generate
          </button>

          <button
            @click="downloadReport"
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

      <!-- Loading State -->
      <div v-if="loading" class="space-y-6">
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
          <div
            v-for="i in 4"
            :key="i"
            class="card h-24 animate-pulse bg-neutral-100 dark:bg-neutral-800 rounded-3xl"
          ></div>
        </div>
        <div class="grid lg:grid-cols-2 gap-6">
          <div
            v-for="i in 2"
            :key="i"
            class="card h-64 animate-pulse bg-neutral-100 dark:bg-neutral-800 rounded-3xl"
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

      <template v-else-if="report">
        <!-- Summary Cards -->
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
          <div class="card p-6">
            <div class="flex items-center justify-between">
              <div>
                <p class="text-sm text-neutral-500 dark:text-neutral-400">
                  Total Revenue
                </p>
                <p
                  class="text-2xl font-bold text-neutral-900 dark:text-white mt-1"
                >
                  ${{ report.totalRevenue?.toFixed(2) }}
                </p>
              </div>
              <div
                class="w-12 h-12 rounded-xl bg-success-100 dark:bg-success-900/30 flex items-center justify-center"
              >
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  class="w-6 h-6 text-success-600"
                  viewBox="0 0 24 24"
                  fill="none"
                  stroke="currentColor"
                  stroke-width="2"
                >
                  <line x1="12" x2="12" y1="2" y2="22" />
                  <path d="M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6" />
                </svg>
              </div>
            </div>
          </div>

          <div class="card p-6">
            <div class="flex items-center justify-between">
              <div>
                <p class="text-sm text-neutral-500 dark:text-neutral-400">
                  Total Orders
                </p>
                <p
                  class="text-2xl font-bold text-neutral-900 dark:text-white mt-1"
                >
                  {{ report.totalOrders }}
                </p>
              </div>
              <div
                class="w-12 h-12 rounded-xl bg-primary-100 dark:bg-primary-900/30 flex items-center justify-center"
              >
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  class="w-6 h-6 text-primary-600"
                  viewBox="0 0 24 24"
                  fill="none"
                  stroke="currentColor"
                  stroke-width="2"
                >
                  <path
                    d="M6 2 3 6v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2V6l-3-4Z"
                  />
                  <path d="M3 6h18" />
                  <path d="M16 10a4 4 0 0 1-8 0" />
                </svg>
              </div>
            </div>
          </div>

          <div class="card p-6">
            <div class="flex items-center justify-between">
              <div>
                <p class="text-sm text-neutral-500 dark:text-neutral-400">
                  Average Order
                </p>
                <p
                  class="text-2xl font-bold text-neutral-900 dark:text-white mt-1"
                >
                  ${{ report.averageOrderValue?.toFixed(2) }}
                </p>
              </div>
              <div
                class="w-12 h-12 rounded-xl bg-warning-100 dark:bg-warning-900/30 flex items-center justify-center"
              >
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  class="w-6 h-6 text-warning-600"
                  viewBox="0 0 24 24"
                  fill="none"
                  stroke="currentColor"
                  stroke-width="2"
                >
                  <path d="M3 3v18h18" />
                  <path d="m19 9-5 5-4-4-3 3" />
                </svg>
              </div>
            </div>
          </div>

          <div class="card p-6">
            <div class="flex items-center justify-between">
              <div>
                <p class="text-sm text-neutral-500 dark:text-neutral-400">
                  Items Sold
                </p>
                <p
                  class="text-2xl font-bold text-neutral-900 dark:text-white mt-1"
                >
                  {{ report.totalItemsSold }}
                </p>
              </div>
              <div
                class="w-12 h-12 rounded-xl bg-accent-100 dark:bg-accent-900/30 flex items-center justify-center"
              >
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  class="w-6 h-6 text-accent-600"
                  viewBox="0 0 24 24"
                  fill="none"
                  stroke="currentColor"
                  stroke-width="2"
                >
                  <path d="M17 8h1a4 4 0 1 1 0 8h-1" />
                  <path d="M3 8h14v9a4 4 0 0 1-4 4H7a4 4 0 0 1-4-4Z" />
                </svg>
              </div>
            </div>
          </div>
        </div>

        <!-- Charts Row -->
        <div class="grid lg:grid-cols-2 gap-6">
          <!-- Daily Sales Chart -->
          <div class="card p-6">
            <h3
              class="text-lg font-semibold text-neutral-900 dark:text-white mb-4"
            >
              Daily Sales
            </h3>
            <div class="h-64">
              <div class="flex items-end justify-between h-full gap-1">
                <div
                  v-for="day in report.dailySales"
                  :key="day.date"
                  class="flex-1 flex flex-col items-center justify-end h-full"
                >
                  <div
                    class="w-full bg-gradient-to-t from-primary-600 to-primary-400 rounded-t-md transition-all hover:from-primary-500 hover:to-primary-300"
                    :style="{ height: getBarHeight(day.revenue) + '%' }"
                    :title="`${day.date}: $${day.revenue?.toFixed(2)}`"
                  ></div>
                  <span
                    class="text-[10px] text-neutral-500 mt-2 rotate-45 origin-left"
                    >{{ formatDate(day.date) }}</span
                  >
                </div>
              </div>
            </div>
          </div>

          <!-- Payment Methods -->
          <div class="card p-6">
            <h3
              class="text-lg font-semibold text-neutral-900 dark:text-white mb-4"
            >
              Payment Methods
            </h3>
            <div class="space-y-4">
              <div
                v-for="method in report.paymentMethodSummary"
                :key="method.method"
                class="space-y-2"
              >
                <div class="flex items-center justify-between text-sm">
                  <span
                    class="font-medium text-neutral-700 dark:text-neutral-300"
                    >{{ method.method }}</span
                  >
                  <span class="text-neutral-500"
                    >${{ method.amount?.toFixed(2) }} ({{
                      method.percentage?.toFixed(1)
                    }}%)</span
                  >
                </div>
                <div
                  class="h-2 bg-neutral-200 dark:bg-neutral-700 rounded-full overflow-hidden"
                >
                  <div
                    class="h-full rounded-full transition-all duration-500"
                    :class="getMethodColor(method.method)"
                    :style="{ width: method.percentage + '%' }"
                  ></div>
                </div>
              </div>
              <div
                v-if="!report.paymentMethodSummary?.length"
                class="text-center py-8 text-neutral-500"
              >
                No payment data available
              </div>
            </div>
          </div>
        </div>

        <!-- Category Sales & Top Items -->
        <div class="grid lg:grid-cols-2 gap-6">
          <!-- Category Sales -->
          <div class="card p-6">
            <h3
              class="text-lg font-semibold text-neutral-900 dark:text-white mb-4"
            >
              Sales by Category
            </h3>
            <div class="space-y-3">
              <div
                v-for="cat in report.categorySales"
                :key="cat.categoryId"
                class="flex items-center justify-between p-3 bg-neutral-50 dark:bg-neutral-800/50 rounded-xl"
              >
                <div class="flex items-center gap-3">
                  <div
                    class="w-10 h-10 rounded-lg bg-primary-100 dark:bg-primary-900/30 flex items-center justify-center"
                  >
                    <span class="text-sm font-bold text-primary-600">{{
                      cat.categoryName?.charAt(0)
                    }}</span>
                  </div>
                  <div>
                    <p class="font-medium text-neutral-900 dark:text-white">
                      {{ cat.categoryName }}
                    </p>
                    <p class="text-sm text-neutral-500">
                      {{ cat.itemsSold }} items sold
                    </p>
                  </div>
                </div>
                <span class="text-lg font-bold text-neutral-900 dark:text-white"
                  >${{ cat.revenue?.toFixed(2) }}</span
                >
              </div>
              <div
                v-if="!report.categorySales?.length"
                class="text-center py-8 text-neutral-500"
              >
                No category data available
              </div>
            </div>
          </div>

          <!-- Top Selling Items -->
          <div class="card p-6">
            <h3
              class="text-lg font-semibold text-neutral-900 dark:text-white mb-4"
            >
              Top Selling Items
            </h3>
            <div class="overflow-x-auto">
              <table class="w-full">
                <thead>
                  <tr class="text-left text-xs text-neutral-500 uppercase">
                    <th class="pb-3">#</th>
                    <th class="pb-3">Item</th>
                    <th class="pb-3 text-right">Qty</th>
                    <th class="pb-3 text-right">Revenue</th>
                  </tr>
                </thead>
                <tbody
                  class="divide-y divide-neutral-200 dark:divide-neutral-700"
                >
                  <tr
                    v-for="(item, idx) in report.topSellingItems"
                    :key="item.menuItemId"
                    class="text-sm"
                  >
                    <td class="py-3 text-neutral-500">{{ idx + 1 }}</td>
                    <td class="py-3">
                      <p class="font-medium text-neutral-900 dark:text-white">
                        {{ item.name }}
                      </p>
                      <p class="text-xs text-neutral-500">
                        {{ item.categoryName }}
                      </p>
                    </td>
                    <td
                      class="py-3 text-right text-neutral-700 dark:text-neutral-300"
                    >
                      {{ item.quantitySold }}
                    </td>
                    <td
                      class="py-3 text-right font-semibold text-neutral-900 dark:text-white"
                    >
                      ${{ item.revenue?.toFixed(2) }}
                    </td>
                  </tr>
                </tbody>
              </table>
              <div
                v-if="!report.topSellingItems?.length"
                class="text-center py-8 text-neutral-500"
              >
                No sales data available
              </div>
            </div>
          </div>
        </div>
      </template>

      <!-- Empty State -->
      <div v-else class="card p-12 text-center">
        <svg
          xmlns="http://www.w3.org/2000/svg"
          class="w-16 h-16 mx-auto text-neutral-300 dark:text-neutral-600 mb-4"
          viewBox="0 0 24 24"
          fill="none"
          stroke="currentColor"
          stroke-width="1.5"
        >
          <path d="M3 3v18h18" />
          <path d="m19 9-5 5-4-4-3 3" />
        </svg>
        <h3 class="text-lg font-medium text-neutral-900 dark:text-white mb-2">
          Generate a Report
        </h3>
        <p class="text-neutral-500">
          Select a date range and click Generate to view your sales data
        </p>
      </div>
    </div>
  </NuxtLayout>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";

definePageMeta({
  layout: false,
});

const config = useRuntimeConfig();
const { get, download } = useApi();

interface SalesReport {
  startDate: string;
  endDate: string;
  totalRevenue: number;
  totalOrders: number;
  averageOrderValue: number;
  totalItemsSold: number;
  dailySales: { date: string; revenue: number; orderCount: number }[];
  paymentMethodSummary: {
    method: string;
    amount: number;
    count: number;
    percentage: number;
  }[];
  categorySales: {
    categoryId: number;
    categoryName: string;
    revenue: number;
    itemsSold: number;
  }[];
  topSellingItems: {
    menuItemId: number;
    name: string;
    categoryName: string;
    quantitySold: number;
    revenue: number;
  }[];
}

const loading = ref(false);
const report = ref<SalesReport | null>(null);

// Default to last 30 days
const today = new Date();
const thirtyDaysAgo = new Date(today);
thirtyDaysAgo.setDate(thirtyDaysAgo.getDate() - 30);

const startDate = ref(thirtyDaysAgo.toISOString().split("T")[0]);
const endDate = ref(today.toISOString().split("T")[0]);

const fetchReport = async () => {
  loading.value = true;
  try {
    const response = await get<{ success: boolean; data: SalesReport }>(
      `/reports/sales?startDate=${startDate.value}&endDate=${endDate.value}`,
    );
    if (response?.data) {
      report.value = response.data;
    }
  } catch (error) {
    console.error("Failed to fetch sales report:", error);
  } finally {
    loading.value = false;
  }
};

const getBarHeight = (revenue: number) => {
  if (!report.value?.dailySales?.length) return 0;
  const maxRevenue = Math.max(
    ...report.value.dailySales.map((d) => d.revenue || 0),
  );
  if (maxRevenue === 0) return 0;
  return Math.max(5, (revenue / maxRevenue) * 100);
};

const formatDate = (dateStr: string) => {
  const date = new Date(dateStr);
  return `${date.getMonth() + 1}/${date.getDate()}`;
};

const getMethodColor = (method: string) => {
  const colors: Record<string, string> = {
    CASH: "bg-emerald-500",
    KHQR: "bg-red-600",
    BAKONG: "bg-red-700",
    CARD: "bg-sky-500",
  };
  return colors[method] || "bg-neutral-500";
};

onMounted(() => {
  fetchReport();
});

const downloadReport = () => {
  // Backend expects ISO LocalDateTime (e.g. 2024-01-01T00:00:00)
  const start = `${startDate.value}T00:00:00`;
  const end = `${endDate.value}T23:59:59`;
  const endpoint = `/api/import-export/export/sales?start=${start}&end=${end}`;
  download(
    endpoint,
    `sales_report_${startDate.value}_to_${endDate.value}.xlsx`,
  );
};
</script>
