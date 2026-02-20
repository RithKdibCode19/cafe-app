<template>
  <div
    class="min-h-screen bg-neutral-950 flex flex-col items-center justify-center p-4"
  >
    <!-- Main Container -->
    <div
      class="w-full max-w-lg bg-neutral-900 rounded-[40px] shadow-2xl overflow-hidden border border-neutral-800 transition-all duration-500"
    >
      <!-- HEADER: Dynamic based on state -->
      <div
        class="p-8 text-center bg-gradient-to-b from-neutral-800/50 to-transparent"
      >
        <div
          v-if="!activeShift"
          class="w-16 h-16 rounded-3xl gradient-primary mx-auto mb-4 flex items-center justify-center shadow-xl shadow-primary-500/20"
        >
          <svg
            xmlns="http://www.w3.org/2000/svg"
            class="w-8 h-8 text-white"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            stroke-width="2"
          >
            <rect x="3" y="11" width="18" height="11" rx="2" ry="2" />
            <path d="M7 11V7a5 5 0 0 1 10 0v4" />
          </svg>
        </div>
        <div
          v-else
          class="w-16 h-16 rounded-3xl bg-success-500/10 mx-auto mb-4 flex items-center justify-center border border-success-500/20"
        >
          <div class="w-3 h-3 rounded-full bg-success-500 animate-pulse"></div>
        </div>

        <h1 class="text-3xl font-black text-white uppercase tracking-tighter">
          {{ activeShift ? "Active Shift" : "Terminal Access" }}
        </h1>
        <p
          class="text-neutral-500 text-sm mt-1 uppercase tracking-widest font-bold"
        >
          {{ activeShift ? activeShift.employeeName : "Enter PIN to continue" }}
        </p>
      </div>

      <!-- CONTENT AREA -->
      <div class="px-8 pb-10">
        <!-- STATE 1: PIN ENTRY -->
        <div
          v-if="!activeShift && !showHandover"
          class="space-y-8 animate-in fade-in zoom-in duration-300"
        >
          <div class="flex justify-center gap-4">
            <div
              v-for="i in 4"
              :key="i"
              :class="[
                'w-4 h-4 rounded-full transition-all duration-300',
                pin.length >= i
                  ? 'bg-primary-500 scale-125 shadow-[0_0_15px_rgba(var(--color-primary-500),0.5)]'
                  : 'bg-neutral-800',
              ]"
            ></div>
          </div>

          <div class="grid grid-cols-3 gap-4">
            <button
              v-for="n in 9"
              :key="n"
              @click="addDigit(n)"
              class="keypad-btn"
            >
              {{ n }}
            </button>
            <button @click="clear" class="keypad-btn text-neutral-500 text-sm">
              CLR
            </button>
            <button @click="addDigit(0)" class="keypad-btn">0</button>
            <button @click="backspace" class="keypad-btn text-neutral-500">
              <svg
                xmlns="http://www.w3.org/2000/svg"
                class="w-6 h-6 mx-auto"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                stroke-width="2"
              >
                <path d="M21 4H8l-7 8 7 8h13a2 2 0 0 0 2-2V6a2 2 0 0 0-2-2z" />
                <line x1="18" y1="9" x2="12" y2="15" />
                <line x1="12" y1="9" x2="18" y2="15" />
              </svg>
            </button>
          </div>

          <button
            @click="checkAccess"
            :disabled="pin.length < 4 || loading"
            class="w-full btn-primary py-5 rounded-2xl font-black uppercase tracking-widest text-lg disabled:opacity-20 flex items-center justify-center gap-3"
          >
            <div
              v-if="loading"
              class="w-5 h-5 border-2 border-white/30 border-t-white rounded-full animate-spin"
            ></div>
            Access Terminal
          </button>
        </div>

        <!-- STATE 2: ACTIVE SHIFT DASHBOARD -->
        <div
          v-else-if="activeShift && !showHandover"
          class="space-y-6 animate-in slide-in-from-bottom-4 duration-500"
        >
          <div class="grid grid-cols-2 gap-4">
            <div
              class="p-6 rounded-3xl bg-neutral-800 border border-neutral-700"
            >
              <p
                class="text-[10px] font-black text-neutral-500 uppercase tracking-widest mb-1"
              >
                Total Sales
              </p>
              <p class="text-3xl font-black text-white">
                ${{ activeShift.totalSales?.toFixed(2) }}
              </p>
            </div>
            <div
              class="p-6 rounded-3xl bg-neutral-800 border border-neutral-700"
            >
              <p
                class="text-[10px] font-black text-neutral-500 uppercase tracking-widest mb-1"
              >
                Orders
              </p>
              <p class="text-3xl font-black text-white font-mono">
                {{ activeShift.totalOrders }}
              </p>
            </div>
          </div>

          <div
            class="p-6 rounded-3xl bg-neutral-800 border border-neutral-700 space-y-4"
          >
            <h3
              class="text-xs font-black text-neutral-400 uppercase tracking-widest border-b border-neutral-700 pb-3"
            >
              Payment Breakdown
            </h3>
            <div
              v-for="(val, method) in activeShift.salesByMethod"
              :key="method"
              class="flex justify-between items-center"
            >
              <span
                class="text-sm text-neutral-400 font-bold uppercase tracking-tight"
                >{{ method }}</span
              >
              <span class="text-lg font-black text-white"
                >${{ val.toFixed(2) }}</span
              >
            </div>
          </div>

          <div class="flex gap-4">
            <button
              @click="
                activeShift = null;
                pin = '';
              "
              class="flex-1 py-4 text-sm font-bold text-neutral-500 hover:text-white transition-colors"
            >
              Back
            </button>
            <button
              @click="closeShift"
              class="flex-1 bg-warning-600 hover:bg-warning-700 text-white font-black uppercase tracking-widest py-4 rounded-2xl shadow-xl shadow-warning-900/40"
            >
              Clock Out
            </button>
          </div>
        </div>

        <div
          v-if="error"
          class="mt-6 text-error-500 text-center text-xs font-bold uppercase tracking-widest animate-shake"
        >
          {{ error }}
        </div>
      </div>

      <div
        class="p-6 bg-neutral-950/50 text-center border-t border-neutral-800"
      >
        <NuxtLink
          to="/"
          class="text-neutral-600 hover:text-white text-[10px] font-black uppercase tracking-widest transition-colors mr-4"
          >Return to Front of House</NuxtLink
        >
        <button
          @click="toggleQrMode"
          class="text-primary-600 hover:text-primary-500 text-[10px] font-black uppercase tracking-widest transition-colors"
        >
          Scan QR (Mobile)
        </button>
      </div>
    </div>

    <!-- Success Feedback Overlay -->
    <div
      v-if="successMsg"
      class="fixed inset-0 z-[100] flex items-center justify-center bg-neutral-950/90 backdrop-blur-xl"
    >
      <div class="text-center animate-in zoom-in duration-500">
        <div
          class="w-24 h-24 rounded-[32px] bg-success-500 flex items-center justify-center mx-auto mb-8 shadow-2xl shadow-success-500/40"
        >
          <svg
            xmlns="http://www.w3.org/2000/svg"
            class="w-12 h-12 text-white"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            stroke-width="3"
          >
            <path d="M20 6 9 17l-5-5" />
          </svg>
        </div>
        <h2 class="text-5xl font-black text-white mb-2 tracking-tighter">
          {{ successMsg.action }}
        </h2>
        <p
          class="text-xl text-success-400 font-black uppercase tracking-widest"
        >
          {{ successMsg.name }}
        </p>
        <p class="text-neutral-500 mt-6 font-mono">{{ successMsg.time }}</p>
      </div>
    </div>

    <!-- QR Code Overlay (Dynamic) -->
    <div
      v-if="showQrMode"
      class="fixed inset-0 z-[90] flex items-center justify-center bg-neutral-950/95 backdrop-blur-2xl"
    >
      <div class="text-center w-full max-w-md p-8 relative">
        <button
          @click="showQrMode = false"
          class="absolute top-0 right-0 p-4 text-neutral-500 hover:text-white"
        >
          <svg
            xmlns="http://www.w3.org/2000/svg"
            class="w-8 h-8"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            stroke-width="2"
          >
            <path d="M18 6 6 18" />
            <path d="m6 6 12 12" />
          </svg>
        </button>

        <h2 class="text-3xl font-black text-white mb-8 tracking-tight">
          Mobile Check-In
        </h2>

        <div
          class="bg-white p-6 rounded-3xl mx-auto w-fit mb-8 shadow-[0_0_50px_rgba(255,255,255,0.1)]"
        >
          <QrcodeVue
            :value="currentQrToken"
            :size="250"
            level="H"
            v-if="currentQrToken"
          />
          <div
            v-else
            class="w-[250px] h-[250px] flex items-center justify-center text-neutral-400"
          >
            Loading...
          </div>
        </div>

        <p
          class="text-neutral-400 text-sm font-bold uppercase tracking-widest mb-2"
        >
          Scan with your phone
        </p>
        <p class="text-neutral-600 text-xs">
          Code refreshes in {{ secondsToRefresh }}s
        </p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onUnmounted } from "vue";
import QrcodeVue from "qrcode.vue";

const { post, get } = useApi();

const pin = ref("");
const loading = ref(false);
const error = ref("");
const successMsg = ref<any>(null);
const activeShift = ref<any>(null);
// showHandover removed
// actualCash removed
const showQrMode = ref(false);
const currentQrToken = ref("");
const secondsToRefresh = ref(15);
let qrInterval: NodeJS.Timer;
let countdownInterval: NodeJS.Timer;

// discrepancy removed

const addDigit = (n: number) => {
  if (pin.value.length < 4) {
    pin.value += n;
    error.value = "";
    if (pin.value.length === 4) {
      // Auto check could be annoying if they make a mistake, better to have a button
    }
  }
};

const clear = () => {
  pin.value = "";
};
const backspace = () => {
  pin.value = pin.value.slice(0, -1);
};

const checkAccess = async () => {
  loading.value = true;
  error.value = "";
  try {
    const empId = parseInt(pin.value, 10);
    // 1. Try to get shift summary
    try {
      const summaryRes = await get<any>(`/reports/shift/${empId}`);
      if (summaryRes?.data) {
        activeShift.value = summaryRes.data;
        return;
      }
    } catch (e) {
      // If summary fails, it means no active shift, offer clock-in
      handleClockIn(empId);
    }
  } catch (err: any) {
    error.value = err.data?.message || "Access Denied";
    pin.value = "";
  } finally {
    loading.value = false;
  }
};

const handleClockIn = async (empId: number) => {
  try {
    const res = await post<any>(`/attendance/clock-in/${empId}`, {});
    successMsg.value = {
      action: "CLOCKED IN",
      name: res.employee?.fullName || "Staff Member",
      time: new Date().toLocaleTimeString(),
    };
    setTimeout(() => {
      successMsg.value = null;
      pin.value = "";
      checkAccess(); // Refresh to show dashboard
    }, 3000);
  } catch (err: any) {
    error.value = err.data?.message || "Clock-in failed";
    pin.value = "";
  }
};

const closeShift = async () => {
  // No cash check needed
  loading.value = true;
  try {
    const empId = activeShift.value.employeeId;
    // Just clock out
    const res = await post<any>(`/attendance/clock-out/${empId}`, {
      note: "Regular Clock Out",
    });

    successMsg.value = {
      action: "CLOCKED OUT", // Renamed from SHIFT CLOSED
      name: activeShift.value.employeeName,
      time: new Date().toLocaleTimeString(),
    };

    setTimeout(() => {
      successMsg.value = null;
      activeShift.value = null;
      // showHandover removed
      // actualCash removed
      pin.value = "";
    }, 3000);
  } catch (err: any) {
    error.value = err.data?.message || "Clock-out failed";
  } finally {
    loading.value = false;
  }
};

// -- QR Logic --
const toggleQrMode = () => {
  showQrMode.value = !showQrMode.value;
  if (showQrMode.value) {
    fetchQrToken();
    startQrTimer();
  } else {
    stopQrTimer();
  }
};

const fetchQrToken = async () => {
  try {
    const res = await get<string>("/attendance/qr-token/1"); // Hardcoded branch 1 for demo
    currentQrToken.value = res || "";
    secondsToRefresh.value = 15;
  } catch (err) {
    console.error("Failed to get QR token");
  }
};

const startQrTimer = () => {
  stopQrTimer();
  qrInterval = setInterval(fetchQrToken, 15000);
  countdownInterval = setInterval(() => {
    if (secondsToRefresh.value > 0) secondsToRefresh.value--;
  }, 1000);
};

const stopQrTimer = () => {
  clearInterval(qrInterval);
  clearInterval(countdownInterval);
};

onUnmounted(() => stopQrTimer());
</script>

<style scoped>
.keypad-btn {
  @apply h-16 w-full flex items-center justify-center bg-neutral-800/50 hover:bg-neutral-800 text-3xl font-black text-white rounded-3xl transition-all active:scale-90 border border-neutral-800;
}

.btn-primary {
  @apply bg-primary-600 hover:bg-primary-700 text-white shadow-xl shadow-primary-900/40 active:scale-95 transition-all;
}

.animate-shake {
  animation: shake 0.5s cubic-bezier(0.36, 0.07, 0.19, 0.97) both;
}

@keyframes shake {
  10%,
  90% {
    transform: translate3d(-1px, 0, 0);
  }
  20%,
  80% {
    transform: translate3d(2px, 0, 0);
  }
  30%,
  50%,
  70% {
    transform: translate3d(-4px, 0, 0);
  }
  40%,
  60% {
    transform: translate3d(4px, 0, 0);
  }
}

input::-webkit-outer-spin-button,
input::-webkit-inner-spin-button {
  -webkit-appearance: none;
  margin: 0;
}
</style>
