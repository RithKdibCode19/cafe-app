<template>
  <NuxtLayout name="kitchen">
    <div class="flex flex-col h-full gap-6">
      <!-- Top Summary Bar -->
      <div
        v-if="!loading && totalPendingItems.length > 0"
        class="flex gap-3 overflow-x-auto pb-2 custom-scrollbar no-scrollbar"
      >
        <div
          v-for="summary in totalPendingItems"
          :key="summary.name"
          class="flex-shrink-0 bg-neutral-800 border border-neutral-700 px-4 py-2 rounded-2xl flex items-center gap-3 animate-in fade-in slide-in-from-left-4 duration-500"
        >
          <span
            class="w-8 h-8 rounded-full bg-primary-500/20 text-primary-500 flex items-center justify-center font-black text-sm"
          >
            {{ summary.qty }}
          </span>
          <span class="text-sm font-bold text-neutral-300 whitespace-nowrap">{{
            summary.name
          }}</span>
        </div>
      </div>

      <div class="grid grid-cols-1 lg:grid-cols-3 gap-6 flex-1 overflow-hidden">
        <!-- New Orders (Pending) -->
        <div
          class="flex flex-col h-full bg-neutral-800/50 rounded-3xl border border-neutral-800 overflow-hidden"
        >
          <div
            class="p-4 bg-neutral-800 border-b border-neutral-700 flex justify-between items-center sticky top-0 z-10"
          >
            <div class="flex items-center gap-3">
              <div
                class="w-3 h-3 rounded-full bg-primary-500 animate-pulse"
              ></div>
              <h2
                class="uppercase tracking-widest font-black text-sm text-neutral-300"
              >
                New Orders
              </h2>
            </div>
            <span
              class="bg-neutral-900 text-neutral-400 px-2 py-1 rounded text-xs font-bold"
              >{{ pendingOrders.length }}</span
            >
          </div>

          <div class="p-4 space-y-4 overflow-y-auto custom-scrollbar flex-1">
            <template v-if="loading">
              <div
                v-for="i in 3"
                :key="i"
                class="bg-neutral-900 rounded-2xl p-4 h-48 animate-pulse"
              ></div>
            </template>
            <template v-else>
              <TransitionGroup name="order-list">
                <div
                  v-for="order in pendingOrders"
                  :key="order.orderId"
                  :class="[
                    'bg-neutral-900 rounded-2xl p-5 border border-neutral-800 shadow-lg group hover:border-primary-500/50 transition-all duration-300',
                    isNewOrder(order.createdAt)
                      ? 'ring-2 ring-primary-500/50 scale-[1.02] shadow-primary-900/20'
                      : '',
                  ]"
                >
                  <div class="flex justify-between items-start mb-4">
                    <div>
                      <h3 class="font-black text-2xl text-white">
                        #{{ order.orderNo?.slice(-4) || order.orderId }}
                      </h3>
                      <span
                        class="text-[10px] font-bold text-neutral-500 uppercase tracking-widest"
                        >{{ order.orderType }} •
                        {{ formatTime(order.createdAt) }}</span
                      >
                    </div>
                    <div class="text-right">
                      <div
                        :class="[
                          'text-xs font-bold px-2 py-1 rounded-lg mb-1 transition-colors duration-500',
                          getTimerColor(order.createdAt),
                        ]"
                      >
                        {{ getDuration(order.createdAt) }}
                      </div>
                    </div>
                  </div>

                  <div class="space-y-3 mb-6">
                    <div
                      v-for="(item, idx) in order.items"
                      :key="idx"
                      class="flex justify-between items-start text-sm p-2 rounded-lg bg-neutral-800/30"
                    >
                      <div class="flex gap-3">
                        <span class="font-black text-primary-500 w-5"
                          >{{ item.qty }}x</span
                        >
                        <div>
                          <p class="text-neutral-200 font-bold leading-tight">
                            {{ item.name }}
                          </p>
                          <div
                            v-if="item.variants"
                            class="flex flex-wrap gap-1 mt-1"
                          >
                            <span
                              class="text-[10px] bg-neutral-800 text-neutral-400 px-1.5 py-0.5 rounded border border-neutral-700"
                              >{{ item.variants }}</span
                            >
                          </div>
                          <div
                            v-if="item.addOnItems?.length"
                            class="flex flex-wrap gap-1 mt-1"
                          >
                            <span
                              v-for="addon in item.addOnItems"
                              :key="addon.addonId"
                              class="text-[10px] bg-primary-900/20 text-primary-400 px-1.5 py-0.5 rounded border border-primary-900/30"
                            >
                              + {{ addon.name }}
                            </span>
                          </div>
                          <p
                            v-if="item.notes"
                            class="text-xs text-orange-400 mt-1.5 p-1.5 bg-orange-950/20 border-l-2 border-orange-500 italic"
                          >
                            <span class="font-bold mr-1">Note:</span
                            >{{ item.notes }}
                          </p>
                        </div>
                      </div>
                    </div>
                  </div>

                  <button
                    @click="updateStatus(order, 'PREPARING')"
                    class="w-full py-4 bg-neutral-800 hover:bg-primary-600 text-neutral-400 hover:text-white font-black uppercase tracking-widest rounded-xl transition-all active:scale-95 text-xs"
                  >
                    Start Preparing
                  </button>
                </div>
              </TransitionGroup>
              <div
                v-if="pendingOrders.length === 0"
                class="text-center py-12 opacity-30"
              >
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  class="w-16 h-16 mx-auto mb-4 text-neutral-600"
                  viewBox="0 0 24 24"
                  fill="none"
                  stroke="currentColor"
                  stroke-width="2"
                >
                  <circle cx="12" cy="12" r="10" />
                  <path d="M12 6v6l4 2" />
                </svg>
                <p class="font-bold uppercase tracking-widest text-sm">
                  No Pending Orders
                </p>
              </div>
            </template>
          </div>
        </div>

        <!-- In Progress (Preparing) -->
        <div
          class="flex flex-col h-full bg-neutral-800/50 rounded-3xl border border-neutral-800 overflow-hidden"
        >
          <div
            class="p-4 bg-neutral-800 border-b border-neutral-700 flex justify-between items-center sticky top-0 z-10"
          >
            <div class="flex items-center gap-3">
              <div
                class="w-3 h-3 rounded-full bg-orange-500 animate-pulse"
              ></div>
              <h2
                class="uppercase tracking-widest font-black text-sm text-neutral-300"
              >
                Preparing
              </h2>
            </div>
            <span
              class="bg-neutral-900 text-neutral-400 px-2 py-1 rounded text-xs font-bold"
              >{{ preparingOrders.length }}</span
            >
          </div>

          <div class="p-4 space-y-4 overflow-y-auto custom-scrollbar flex-1">
            <TransitionGroup name="order-list">
              <div
                v-for="order in preparingOrders"
                :key="order.orderId"
                class="bg-neutral-900 rounded-2xl p-5 border-l-4 border-orange-500 shadow-lg relative overflow-hidden group"
              >
                <div
                  class="absolute top-0 right-0 p-4 opacity-10 group-hover:opacity-20 transition-opacity"
                >
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    class="w-24 h-24 text-orange-500"
                    viewBox="0 0 24 24"
                    fill="none"
                    stroke="currentColor"
                    stroke-width="2"
                  >
                    <path
                      d="M6 13.87A4 4 0 0 1 7.41 6a5.11 5.11 0 0 1 10.59 0A4.09 4.09 0 0 1 24 10a4 4 0 0 1-4 4"
                    />
                  </svg>
                </div>

                <div
                  class="flex justify-between items-start mb-4 relative z-10"
                >
                  <div>
                    <h3 class="font-black text-2xl text-white">
                      #{{ order.orderNo?.slice(-4) || order.orderId }}
                    </h3>
                    <span
                      class="text-[10px] font-bold text-neutral-500 uppercase tracking-widest"
                      >{{ order.orderType }}</span
                    >
                  </div>
                  <div
                    :class="[
                      'text-xs font-bold px-2 py-1 rounded-lg transition-colors duration-500',
                      getTimerColor(order.createdAt),
                    ]"
                  >
                    {{ getDuration(order.createdAt) }}
                  </div>
                </div>

                <div class="space-y-3 mb-6 relative z-10">
                  <div
                    v-for="(item, idx) in order.items"
                    :key="idx"
                    class="flex justify-between items-start text-sm p-2 rounded-lg bg-neutral-800/20"
                  >
                    <div class="flex gap-3">
                      <span class="font-black text-orange-500 w-5"
                        >{{ item.qty }}x</span
                      >
                      <div>
                        <p class="text-white font-bold leading-tight">
                          {{ item.name }}
                        </p>
                        <div
                          v-if="item.variants"
                          class="flex flex-wrap gap-1 mt-1"
                        >
                          <span
                            class="text-[10px] bg-neutral-800 text-neutral-400 px-1.5 py-0.5 rounded border border-neutral-700"
                            >{{ item.variants }}</span
                          >
                        </div>
                        <div
                          v-if="item.addOnItems?.length"
                          class="flex flex-wrap gap-1 mt-1"
                        >
                          <span
                            v-for="addon in item.addOnItems"
                            :key="addon.addonId"
                            class="text-[10px] bg-orange-900/20 text-orange-400 px-1.5 py-0.5 rounded border border-orange-900/30"
                          >
                            + {{ addon.name }}
                          </span>
                        </div>
                        <p
                          v-if="item.notes"
                          class="text-xs text-orange-400 mt-1.5 p-1.5 bg-orange-950/20 border-l-2 border-orange-500 italic"
                        >
                          Note: {{ item.notes }}
                        </p>
                      </div>
                    </div>
                  </div>
                </div>

                <button
                  @click="updateStatus(order, 'READY')"
                  class="w-full py-4 bg-success-600 hover:bg-success-500 text-white font-black uppercase tracking-widest rounded-xl transition-all active:scale-95 text-xs shadow-lg shadow-success-900/20 relative z-10"
                >
                  Mark Ready
                </button>
              </div>
            </TransitionGroup>

            <div
              v-if="preparingOrders.length === 0"
              class="text-center py-12 opacity-30"
            >
              <p class="font-bold uppercase tracking-widest text-sm">
                Station Clear
              </p>
            </div>
          </div>
        </div>

        <!-- Ready -->
        <div
          class="flex flex-col h-full bg-neutral-800/50 rounded-3xl border border-neutral-800 overflow-hidden"
        >
          <div
            class="p-4 bg-neutral-800 border-b border-neutral-700 flex justify-between items-center sticky top-0 z-10"
          >
            <div class="flex items-center gap-3">
              <div class="w-3 h-3 rounded-full bg-success-500"></div>
              <h2
                class="uppercase tracking-widest font-black text-sm text-neutral-300"
              >
                Ready for Pickup
              </h2>
            </div>
            <span
              class="bg-neutral-900 text-neutral-400 px-2 py-1 rounded text-xs font-bold"
              >{{ readyOrders.length }}</span
            >
          </div>

          <div class="p-4 space-y-4 overflow-y-auto custom-scrollbar flex-1">
            <TransitionGroup name="order-list">
              <div
                v-for="order in readyOrders"
                :key="order.orderId"
                class="bg-neutral-900/50 rounded-2xl p-5 border border-success-900/30 opacity-75 hover:opacity-100 transition-all duration-300"
              >
                <div class="flex justify-between items-center mb-2">
                  <h3 class="font-black text-xl text-success-500">
                    #{{ order.orderNo?.slice(-4) || order.orderId }}
                  </h3>
                  <span
                    class="text-xs font-bold text-white bg-success-900/50 px-2 py-1 rounded"
                    >READY</span
                  >
                </div>
                <p class="text-xs text-neutral-400 mb-4">
                  {{ order.items.length }} Items •
                  {{ formatTime(order.updatedAt || order.createdAt) }}
                </p>

                <button
                  @click="updateStatus(order, 'COMPLETED')"
                  class="w-full py-3 bg-neutral-800 hover:bg-neutral-700 text-neutral-400 hover:text-white font-bold uppercase tracking-widest rounded-xl transition-all text-[10px]"
                >
                  Complete / Picked Up
                </button>
              </div>
            </TransitionGroup>

            <div
              v-if="readyOrders.length === 0"
              class="text-center py-12 opacity-30"
            >
              <p class="font-bold uppercase tracking-widest text-sm">
                No Completed Orders
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </NuxtLayout>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, watch } from "vue";

definePageMeta({
  layout: false,
});

const { get, put } = useApi();
const toast = useToast();

const loading = ref(true);
const pendingOrders = ref<any[]>([]);
const preparingOrders = ref<any[]>([]);
const readyOrders = ref<any[]>([]);
const now = ref(Date.now());
let refreshInterval: any;
let timerInterval: any;

// Audio for new orders
let newOrderSound: HTMLAudioElement | null = null;

const initAudio = () => {
  newOrderSound = new Audio(
    "https://assets.mixkit.co/active_storage/sfx/2869/2869-preview.mp3",
  ); // Subtle ping
  newOrderSound.volume = 0.5;
};

const totalPendingItems = computed(() => {
  const allOrders = [...pendingOrders.value, ...preparingOrders.value];
  const summary: Record<string, number> = {};

  allOrders.forEach((order) => {
    order.items.forEach((item: any) => {
      summary[item.name] = (summary[item.name] || 0) + item.qty;
    });
  });

  return Object.entries(summary)
    .map(([name, qty]) => ({ name, qty }))
    .sort((a, b) => b.qty - a.qty);
});

const fetchOrders = async () => {
  try {
    const [pendingRes, preparingRes, readyRes] = await Promise.all([
      get<any[]>("/orders/status/PENDING"),
      get<any[]>("/orders/status/PREPARING"),
      get<any[]>("/orders/status/READY"),
    ]);

    const oldPendingIds = new Set(pendingOrders.value.map((o) => o.orderId));
    const newPending = pendingRes || [];

    // Check for new orders to play sound
    if (
      !loading.value &&
      newPending.some((o) => !oldPendingIds.has(o.orderId))
    ) {
      playNewOrderSound();
    }

    pendingOrders.value = newPending;
    preparingOrders.value = preparingRes || [];
    readyOrders.value = readyRes || [];
  } catch (err) {
    console.error("Failed to fetch kitchen orders", err);
  } finally {
    loading.value = false;
  }
};

const playNewOrderSound = () => {
  if (newOrderSound) {
    newOrderSound.play().catch((e) => console.warn("Audio play blocked", e));
  }
};

const isNewOrder = (createdAt: string) => {
  const created = new Date(createdAt).getTime();
  return now.value - created < 30000; // Highlight if created in last 30s
};

const updateStatus = async (order: any, status: string) => {
  try {
    await put(`/orders/${order.orderId}/status?status=${status}`);
    // Optimistic update handled by fetchOrders normally, but we can do it here too
    toast.success(
      `Order #${order.orderNo?.slice(-4) || order.orderId} updated`,
    );
    fetchOrders();
  } catch (err) {
    toast.error("Failed to update order status");
  }
};

// -- Helpers --
const formatTime = (iso: string) => {
  if (!iso) return "--:--";
  return new Date(iso).toLocaleTimeString([], {
    hour: "2-digit",
    minute: "2-digit",
  });
};

const getDuration = (iso: string) => {
  if (!iso) return "0 min";
  const start = new Date(iso).getTime();
  const diffMins = Math.floor((now.value - start) / 60000);
  return diffMins + " min";
};

const getTimerColor = (iso: string) => {
  if (!iso) return "bg-success-500/20 text-success-500";
  const start = new Date(iso).getTime();
  const diffMins = (now.value - start) / 60000;
  if (diffMins > 20) return "bg-error-500 text-white animate-pulse";
  if (diffMins > 10) return "bg-warning-500 text-neutral-900 font-black";
  return "bg-success-500/20 text-success-500 font-bold";
};

onMounted(() => {
  initAudio();
  fetchOrders();

  refreshInterval = setInterval(() => {
    fetchOrders();
  }, 10000); // Poll every 10s

  timerInterval = setInterval(() => {
    now.value = Date.now();
  }, 1000); // Update 'now' every second for smooth timers
});

onUnmounted(() => {
  clearInterval(refreshInterval);
  clearInterval(timerInterval);
});
</script>

<style scoped>
.custom-scrollbar::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}
.custom-scrollbar::-webkit-scrollbar-track {
  @apply bg-neutral-900/50;
}
.custom-scrollbar::-webkit-scrollbar-thumb {
  @apply bg-neutral-700 rounded-full hover:bg-neutral-600 transition-colors;
}

.no-scrollbar::-webkit-scrollbar {
  display: none;
}
.no-scrollbar {
  -ms-overflow-style: none;
  scrollbar-width: none;
}

/* Transitions for list updates */
.order-list-enter-active,
.order-list-leave-active {
  transition: all 0.5s ease;
}
.order-list-enter-from {
  opacity: 0;
  transform: translateY(30px);
}
.order-list-leave-to {
  opacity: 0;
  transform: scale(0.9);
}
</style>
