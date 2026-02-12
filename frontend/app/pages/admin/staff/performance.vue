<template>
  <NuxtLayout name="admin">
    <div class="space-y-6">
      <div class="flex items-center justify-between">
        <div>
          <h2 class="text-2xl font-bold text-neutral-900 dark:text-white">
            Staff Performance Dashboard
          </h2>
          <p class="text-xs text-neutral-500 mt-1">
            Monitor staff productivity, sales, and punctuality metrics.
          </p>
        </div>
        <div class="flex items-center gap-3">
          <div class="flex bg-neutral-100 dark:bg-neutral-800 p-1 rounded-xl">
            <button
              v-for="range in ranges"
              :key="range.value"
              @click="setRange(range.value)"
              :class="[
                'px-4 py-1.5 rounded-lg text-xs font-bold transition-all',
                activeRange === range.value
                  ? 'bg-white dark:bg-neutral-700 text-primary-600 shadow-sm'
                  : 'text-neutral-500 hover:text-neutral-700',
              ]"
            >
              {{ range.label }}
            </button>
          </div>
        </div>
      </div>

      <div class="grid grid-cols-1 lg:grid-cols-4 gap-6">
        <!-- Summary Cards -->
        <div
          class="lg:col-span-4 grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4"
        >
          <div
            class="bg-white dark:bg-neutral-900 p-4 rounded-2xl border border-neutral-200 dark:border-neutral-800 shadow-sm"
          >
            <div
              class="text-[10px] font-black text-neutral-500 uppercase tracking-widest mb-1"
            >
              Top Sales Volume
            </div>
            <div class="flex items-end justify-between">
              <div class="text-2xl font-black text-neutral-900 dark:text-white">
                ${{ topSales.value.toFixed(2) }}
              </div>
              <div
                class="text-xs text-primary-500 font-bold truncate ml-2 text-right"
              >
                {{ topSales.name }}
              </div>
            </div>
          </div>
          <div
            class="bg-white dark:bg-neutral-900 p-4 rounded-2xl border border-neutral-200 dark:border-neutral-800 shadow-sm"
          >
            <div
              class="text-[10px] font-black text-neutral-500 uppercase tracking-widest mb-1"
            >
              Highest Upsell Rate
            </div>
            <div class="flex items-end justify-between">
              <div class="text-2xl font-black text-neutral-900 dark:text-white">
                {{ topUpsell.value.toFixed(1) }}%
              </div>
              <div
                class="text-xs text-success-500 font-bold truncate ml-2 text-right"
              >
                {{ topUpsell.name }}
              </div>
            </div>
          </div>
          <div
            class="bg-white dark:bg-neutral-900 p-4 rounded-2xl border border-neutral-200 dark:border-neutral-800 shadow-sm"
          >
            <div
              class="text-[10px] font-black text-neutral-500 uppercase tracking-widest mb-1"
            >
              Most Punctual
            </div>
            <div class="flex items-end justify-between">
              <div class="text-2xl font-black text-neutral-900 dark:text-white">
                {{ topPunctual.value.toFixed(0) }}%
              </div>
              <div
                class="text-xs text-indigo-500 font-bold truncate ml-2 text-right"
              >
                {{ topPunctual.name }}
              </div>
            </div>
          </div>
          <div
            class="bg-white dark:bg-neutral-900 p-4 rounded-2xl border border-neutral-200 dark:border-neutral-800 shadow-sm"
          >
            <div
              class="text-[10px] font-black text-neutral-500 uppercase tracking-widest mb-1"
            >
              Highest ATV
            </div>
            <div class="flex items-end justify-between">
              <div class="text-2xl font-black text-neutral-900 dark:text-white">
                ${{ topATV.value.toFixed(2) }}
              </div>
              <div
                class="text-xs text-orange-500 font-bold truncate ml-2 text-right"
              >
                {{ topATV.name }}
              </div>
            </div>
          </div>
        </div>

        <!-- Detailed Table -->
        <div
          class="lg:col-span-4 bg-white dark:bg-neutral-900 border border-neutral-200 dark:border-neutral-800 rounded-2xl overflow-hidden shadow-sm"
        >
          <div
            class="p-4 border-b border-neutral-100 dark:border-neutral-800 flex justify-between items-center"
          >
            <h3
              class="text-sm font-bold text-neutral-900 dark:text-white tracking-tight uppercase"
            >
              Staff Rankings
            </h3>
          </div>
          <div class="overflow-x-auto">
            <table class="w-full">
              <thead>
                <tr class="bg-neutral-50 dark:bg-neutral-800/50 text-left">
                  <th
                    class="px-6 py-3 text-[10px] font-black text-neutral-500 uppercase tracking-widest"
                  >
                    Employee
                  </th>
                  <th
                    class="px-6 py-3 text-[10px] font-black text-neutral-500 uppercase tracking-widest text-center"
                  >
                    Orders
                  </th>
                  <th
                    class="px-6 py-3 text-[10px] font-black text-neutral-500 uppercase tracking-widest text-right"
                  >
                    Sales
                  </th>
                  <th
                    class="px-6 py-3 text-[10px] font-black text-neutral-500 uppercase tracking-widest text-right"
                  >
                    ATV
                  </th>
                  <th
                    class="px-6 py-3 text-[10px] font-black text-neutral-500 uppercase tracking-widest text-center"
                  >
                    Upsell %
                  </th>
                  <th
                    class="px-6 py-3 text-[10px] font-black text-neutral-500 uppercase tracking-widest text-center"
                  >
                    Punctuality
                  </th>
                  <th
                    class="px-6 py-3 text-[10px] font-black text-neutral-500 uppercase tracking-widest text-center"
                  >
                    Drawer Ops
                  </th>
                </tr>
              </thead>
              <tbody
                class="divide-y divide-neutral-100 dark:divide-neutral-800"
              >
                <tr v-if="loading">
                  <td colspan="7" class="px-6 py-12 text-center">
                    <div
                      class="animate-spin h-6 w-6 border-2 border-primary-500 border-t-transparent rounded-full mx-auto"
                    ></div>
                  </td>
                </tr>
                <tr v-else-if="performanceData.length === 0">
                  <td
                    colspan="7"
                    class="px-6 py-12 text-center text-neutral-500"
                  >
                    No data found for this period.
                  </td>
                </tr>
                <tr
                  v-for="staff in performanceData"
                  :key="staff.employeeId"
                  class="hover:bg-neutral-50 dark:hover:bg-neutral-800/50 transition-colors"
                >
                  <td class="px-6 py-4">
                    <div
                      class="text-sm font-bold text-neutral-900 dark:text-white"
                    >
                      {{ staff.fullName }}
                    </div>
                    <div
                      class="text-[10px] text-neutral-500 uppercase font-bold"
                    >
                      Staff ID: {{ staff.employeeId }}
                    </div>
                  </td>
                  <td
                    class="px-6 py-4 text-center font-mono text-sm text-neutral-600 dark:text-neutral-400"
                  >
                    {{ staff.totalOrders }}
                  </td>
                  <td
                    class="px-6 py-4 text-right font-mono font-bold text-neutral-900 dark:text-white"
                  >
                    ${{ staff.totalSales.toFixed(2) }}
                  </td>
                  <td
                    class="px-6 py-4 text-right font-mono text-sm text-neutral-600 dark:text-neutral-400"
                  >
                    ${{ staff.avgTransactionValue.toFixed(2) }}
                  </td>
                  <td class="px-6 py-4 text-center">
                    <div class="flex flex-col items-center">
                      <span
                        :class="[
                          'text-sm font-bold',
                          staff.upsellRate > 20
                            ? 'text-success-500'
                            : 'text-neutral-500',
                        ]"
                        >{{ staff.upsellRate.toFixed(1) }}%</span
                      >
                      <div
                        class="w-16 h-1 bg-neutral-200 dark:bg-neutral-800 rounded-full mt-1 overflow-hidden"
                      >
                        <div
                          class="h-full bg-primary-500"
                          :style="{
                            width: `${Math.min(staff.upsellRate, 100)}%`,
                          }"
                        ></div>
                      </div>
                    </div>
                  </td>
                  <td class="px-6 py-4 text-center">
                    <span
                      :class="[
                        'px-2 py-0.5 rounded text-[10px] font-black uppercase text-white',
                        staff.totalShifts > 0 &&
                        (staff.onTimeShifts / staff.totalShifts) * 100 > 90
                          ? 'bg-success-500'
                          : staff.totalShifts > 0 &&
                              (staff.onTimeShifts / staff.totalShifts) * 100 >
                                70
                            ? 'bg-warning-500'
                            : 'bg-error-500',
                      ]"
                    >
                      {{
                        staff.totalShifts > 0
                          ? (
                              (staff.onTimeShifts / staff.totalShifts) *
                              100
                            ).toFixed(0)
                          : 0
                      }}%
                    </span>
                    <div class="text-[10px] text-neutral-500 mt-1">
                      {{ staff.totalLateMinutes }} mins late
                    </div>
                  </td>
                  <td
                    class="px-6 py-4 text-center font-mono text-sm"
                    :class="
                      staff.manualDrawerOpens > staff.totalOrders * 0.1
                        ? 'text-red-500 font-bold'
                        : 'text-neutral-500'
                    "
                  >
                    {{ staff.manualDrawerOpens }}
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  </NuxtLayout>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from "vue";
import { useApi } from "~/composables/useApi";

const { get } = useApi();

const loading = ref(true);
const performanceData = ref<any[]>([]);
const activeRange = ref("7d");

const ranges = [
  { label: "Today", value: "1d" },
  { label: "This Week", value: "7d" },
  { label: "This Month", value: "30d" },
];

const fetchPerformance = async () => {
  loading.value = true;
  try {
    const end = new Date();
    const start = new Date();
    if (activeRange.value === "1d") start.setHours(0, 0, 0, 0);
    else if (activeRange.value === "7d") start.setDate(start.getDate() - 7);
    else if (activeRange.value === "30d") start.setDate(start.getDate() - 30);

    const startDateStr = start.toISOString().split("T")[0];
    const endDateStr = end.toISOString().split("T")[0];

    const data = await get<any[]>(
      `/staff-performance?startDate=${startDateStr}&endDate=${endDateStr}`,
    );
    performanceData.value = data || [];
  } catch (err) {
    console.error("Failed to fetch performance data", err);
  } finally {
    loading.value = false;
  }
};

const setRange = (val: string) => {
  activeRange.value = val;
  fetchPerformance();
};

onMounted(() => {
  fetchPerformance();
});

// Metrics for cards
const topSales = computed(() => {
  if (performanceData.value.length === 0) return { name: "-", value: 0 };
  const top = performanceData.value.reduce((prev, current) =>
    prev.totalSales > current.totalSales ? prev : current,
  );
  return { name: top.fullName, value: top.totalSales };
});

const topUpsell = computed(() => {
  if (performanceData.value.length === 0) return { name: "-", value: 0 };
  const top = performanceData.value.reduce((prev, current) =>
    prev.upsellRate > current.upsellRate ? prev : current,
  );
  return { name: top.fullName, value: top.upsellRate };
});

const topPunctual = computed(() => {
  if (performanceData.value.length === 0) return { name: "-", value: 0 };
  const top = performanceData.value.reduce((prev, current) => {
    const prevRate =
      prev.totalShifts > 0 ? prev.onTimeShifts / prev.totalShifts : 0;
    const currRate =
      current.totalShifts > 0 ? current.onTimeShifts / current.totalShifts : 0;
    return prevRate > currRate ? prev : current;
  });
  return {
    name: top.fullName,
    value: top.totalShifts > 0 ? (top.onTimeShifts / top.totalShifts) * 100 : 0,
  };
});

const topATV = computed(() => {
  if (performanceData.value.length === 0) return { name: "-", value: 0 };
  const top = performanceData.value.reduce((prev, current) =>
    prev.avgTransactionValue > current.avgTransactionValue ? prev : current,
  );
  return { name: top.fullName, value: top.avgTransactionValue };
});
</script>
