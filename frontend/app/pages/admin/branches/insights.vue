<template>
  <div class="space-y-6">
    <!-- Header -->
    <div
      class="flex flex-col md:flex-row md:items-center justify-between gap-4"
    >
      <div>
        <h1 class="text-2xl font-bold text-white tracking-tight">
          Branch Comparison
        </h1>
        <p class="text-neutral-500 text-sm">
          Performance analytics across all locations
        </p>
      </div>

      <div
        class="flex items-center gap-2 bg-neutral-800 p-1 rounded-xl border border-neutral-700"
      >
        <button
          v-for="r in ranges"
          :key="r.value"
          @click="selectedRange = r.value"
          :class="[
            'px-4 py-1.5 rounded-lg text-xs font-bold transition-all',
            selectedRange === r.value
              ? 'bg-primary-600 text-white shadow-lg shadow-primary-900/20'
              : 'text-neutral-400 hover:text-white',
          ]"
        >
          {{ r.label }}
        </button>
      </div>
    </div>

    <!-- Stats Overview -->
    <div
      v-if="loading"
      class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6"
    >
      <div
        v-for="i in 4"
        :key="i"
        class="h-32 bg-neutral-800 rounded-3xl animate-pulse border border-neutral-700/50"
      ></div>
    </div>

    <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
      <!-- Performance Cards -->
      <div
        v-for="branch in comparison"
        :key="branch.branchId"
        class="bg-neutral-800 rounded-3xl p-6 border border-neutral-700 hover:border-primary-500/50 transition-all group overflow-hidden relative"
      >
        <!-- Background Accent -->
        <div
          class="absolute -top-12 -right-12 w-32 h-32 bg-primary-600/10 rounded-full blur-3xl group-hover:bg-primary-600/20 transition-all"
        ></div>

        <div class="flex justify-between items-start mb-6">
          <div
            class="w-12 h-12 rounded-2xl bg-neutral-900 flex items-center justify-center border border-neutral-700 group-hover:border-primary-500/30 transition-colors"
          >
            <svg
              xmlns="http://www.w3.org/2000/svg"
              class="w-6 h-6 text-primary-500"
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
          <div class="text-right">
            <span
              class="text-[10px] bg-neutral-900 text-neutral-500 px-2 py-0.5 rounded-full font-bold uppercase tracking-widest border border-neutral-700"
              >Branch ID: {{ branch.branchId }}</span
            >
          </div>
        </div>

        <h3
          class="text-xl font-black text-white mb-1 group-hover:text-primary-400 transition-colors"
        >
          {{ branch.branchName }}
        </h3>
        <p
          class="text-xs text-neutral-500 font-medium uppercase tracking-widest mb-6"
        >
          Overall Performance
        </p>

        <div class="space-y-4">
          <div
            class="flex justify-between items-center bg-neutral-900/50 p-3 rounded-2xl border border-neutral-700/30"
          >
            <div class="flex flex-col">
              <span
                class="text-[10px] text-neutral-500 font-bold uppercase tracking-wider"
                >Total Sales</span
              >
              <span class="text-lg font-black text-white"
                >${{ (branch.totalSales || 0).toLocaleString() }}</span
              >
            </div>
            <div
              class="w-10 h-10 rounded-full bg-success-500/10 flex items-center justify-center"
            >
              <svg
                xmlns="http://www.w3.org/2000/svg"
                class="w-5 h-5 text-success-500"
                fill="none"
                viewBox="0 0 24 24"
                stroke="currentColor"
              >
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z"
                />
              </svg>
            </div>
          </div>

          <div class="grid grid-cols-2 gap-3">
            <div
              class="bg-neutral-900/50 p-3 rounded-2xl border border-neutral-700/30"
            >
              <span
                class="text-[10px] text-neutral-500 font-bold uppercase tracking-wider block mb-1"
                >Orders</span
              >
              <span class="text-base font-black text-white">{{
                branch.orderCount
              }}</span>
            </div>
            <div
              class="bg-neutral-900/50 p-3 rounded-2xl border border-neutral-700/30"
            >
              <span
                class="text-[10px] text-neutral-500 font-bold uppercase tracking-wider block mb-1"
                >ATV</span
              >
              <span class="text-base font-black text-white"
                >${{ (branch.avgTransactionValue || 0).toFixed(2) }}</span
              >
            </div>
          </div>

          <div
            class="bg-neutral-900/50 p-3 rounded-2xl border border-neutral-700/30 flex items-center justify-between group/item"
          >
            <div>
              <span
                class="text-[10px] text-neutral-500 font-bold uppercase tracking-wider block"
                >Top Selling Item</span
              >
              <span
                class="text-xs font-bold text-white group-hover/item:text-warning-400 transition-colors"
                >{{ branch.topMenuItem }}</span
              >
            </div>
            <svg
              xmlns="http://www.w3.org/2000/svg"
              class="w-5 h-5 text-warning-500 opacity-50 group-hover/item:scale-110 transition-all"
              fill="none"
              viewBox="0 0 24 24"
              stroke="currentColor"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M5 3v4M3 5h4M6 17v4m-2-2h4m5-16l2.286 6.857L21 12l-5.714 2.143L13 21l-2.286-6.857L5 12l5.714-2.143L13 3z"
              />
            </svg>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from "vue";

const { get } = useApi();

const ranges = [
  { label: "Today", value: "TODAY" },
  { label: "Week", value: "WEEK" },
  { label: "Month", value: "MONTH" },
];

const selectedRange = ref("TODAY");
const comparison = ref<any[]>([]);
const loading = ref(true);

const fetchComparison = async () => {
  loading.value = true;
  try {
    const data = await get<any[]>("/branches/insights/comparison", {
      range: selectedRange.value,
    });
    comparison.value = data || [];
  } catch (e) {
    console.error("Failed to fetch branch comparison", e);
  } finally {
    loading.value = false;
  }
};

watch(selectedRange, fetchComparison);

onMounted(fetchComparison);
</script>
