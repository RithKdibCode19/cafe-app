<template>
  <div>
    <!-- Page header -->
    <div class="flex items-center justify-between mb-8">
      <div>
        <h1 class="text-2xl font-bold text-neutral-900 dark:text-white">
          QR Table Codes
        </h1>
        <p class="text-neutral-500 dark:text-neutral-400 mt-1">
          Generate QR codes for customer table ordering
        </p>
      </div>
    </div>

    <!-- Config section -->
    <div class="card p-6 mb-6">
      <h2 class="text-lg font-semibold text-neutral-900 dark:text-white mb-4">
        Configuration
      </h2>

      <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
        <!-- Branch selector -->
        <div>
          <label class="label">Branch</label>
          <select v-model="selectedBranch" class="input">
            <option value="" disabled>Select a branch</option>
            <option
              v-for="branch in branches"
              :key="branch.branchId"
              :value="branch"
            >
              {{ branch.name }} ({{ branch.code }})
            </option>
          </select>
        </div>

        <!-- Domain -->
        <div>
          <label class="label">Menu Base URL</label>
          <input
            v-model="baseUrl"
            type="text"
            class="input"
            placeholder="https://yourdomain.com"
          />
          <p class="text-xs text-neutral-400 mt-1">
            URL where customers can access the menu
          </p>
        </div>

        <!-- Number of tables -->
        <div>
          <label class="label">Number of Tables</label>
          <input
            v-model.number="numberOfTables"
            type="number"
            min="1"
            max="100"
            class="input"
          />
        </div>
      </div>

      <div class="flex gap-3 mt-6">
        <button
          @click="generateQRCodes"
          :disabled="!selectedBranch || !baseUrl"
          class="btn-primary"
        >
          <svg
            class="w-4 h-4"
            fill="none"
            stroke="currentColor"
            viewBox="0 0 24 24"
          >
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="2"
              d="M12 4v16m8-8H4"
            />
          </svg>
          Generate QR Codes
        </button>
        <button
          v-if="qrCodes.length > 0"
          @click="printAll"
          class="btn-secondary"
        >
          <svg
            class="w-4 h-4"
            fill="none"
            stroke="currentColor"
            viewBox="0 0 24 24"
          >
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="2"
              d="M17 17h2a2 2 0 002-2v-4a2 2 0 00-2-2H5a2 2 0 00-2 2v4a2 2 0 002 2h2m2 4h6a2 2 0 002-2v-4a2 2 0 00-2-2H9a2 2 0 00-2 2v4a2 2 0 002 2zm8-12V5a2 2 0 00-2-2H9a2 2 0 00-2 2v4h10z"
            />
          </svg>
          Print All
        </button>
      </div>
    </div>

    <!-- QR Codes grid -->
    <div
      v-if="qrCodes.length > 0"
      id="qr-print-area"
      class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4"
    >
      <div
        v-for="qr in qrCodes"
        :key="qr.tableNo"
        class="card p-6 text-center hover:shadow-lg transition-shadow print:break-inside-avoid print:shadow-none print:border print:border-neutral-300"
      >
        <div class="mb-3">
          <span
            class="text-xs font-semibold uppercase tracking-wider text-primary-600 dark:text-primary-400"
          >
            {{ selectedBranch?.name }}
          </span>
        </div>

        <!-- QR Code Component -->
        <div class="flex justify-center mb-3">
            <!-- Wrapped in a div to capture the canvas from the component if needed, 
                 but QrcodeVue renders a canvas we can ref directly via :ref but it might be a component ref -->
             <qrcode-vue
                :value="qr.url"
                :size="200"
                level="H"
                class="rounded-lg"
                :id="'qr-canvas-' + qr.tableNo"
             />
        </div>

        <h3 class="text-lg font-bold text-neutral-900 dark:text-white">
          Table {{ qr.tableNo }}
        </h3>
        <p class="text-xs text-neutral-400 mt-1 break-all">{{ qr.url }}</p>

        <div class="flex gap-2 mt-4 print:hidden">
          <button
            @click="downloadQR(qr.tableNo)"
            class="btn-ghost btn-sm flex-1 text-xs"
          >
            <svg
              class="w-3.5 h-3.5"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-4l-4 4m0 0l-4-4m4 4V4"
              />
            </svg>
            Download
          </button>
          <button
            @click="printSingle(qr.tableNo)"
            class="btn-ghost btn-sm flex-1 text-xs"
          >
            <svg
              class="w-3.5 h-3.5"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M17 17h2a2 2 0 002-2v-4a2 2 0 00-2-2H5a2 2 0 00-2 2v4a2 2 0 002 2h2m2 4h6a2 2 0 002-2v-4a2 2 0 00-2-2H9a2 2 0 00-2 2v4a2 2 0 002 2zm8-12V5a2 2 0 00-2-2H9a2 2 0 00-2 2v4h10z"
              />
            </svg>
            Print
          </button>
        </div>
      </div>
    </div>

    <!-- Empty state -->
    <div v-else class="card p-12 text-center">
      <div class="text-6xl mb-4">📱</div>
      <h3 class="text-lg font-bold text-neutral-900 dark:text-white mb-2">
        No QR codes generated yet
      </h3>
      <p class="text-neutral-500 dark:text-neutral-400">
        Select a branch and click "Generate QR Codes" to create table QR codes.
      </p>
    </div>
  </div>
</template>

<script setup lang="ts">
import QrcodeVue from 'qrcode.vue';
definePageMeta({ layout: "admin" });

const { get } = useApi();

// State
const branches = ref<any[]>([]);
const selectedBranch = ref<any>(null);
const baseUrl = ref("");
const numberOfTables = ref(10);
const qrCodes = ref<{ tableNo: number; url: string }[]>([]);

// Fetch branches
const fetchBranches = async () => {
  try {
    const data = await get<any>("/branches");
    branches.value = Array.isArray(data) ? data : data?.content || [];
  } catch (e) {
    console.error("Failed to fetch branches:", e);
  }
};

onMounted(() => {
  fetchBranches();
  // Default to current origin
  if (typeof window !== "undefined") {
    baseUrl.value = window.location.origin;
  }
});

const generateQRCodes = async () => {
  if (!selectedBranch.value || !baseUrl.value) return;

  // Generate QR code data
  qrCodes.value = Array.from({ length: numberOfTables.value }, (_, i) => ({
    tableNo: i + 1,
    url: `${baseUrl.value}/menu/${selectedBranch.value.code}?table=${i + 1}`,
  }));
};

// Helper to get canvas from the rendered component (QrcodeVue renders a canvas)
const getCanvasFromId = (tableNo: number): HTMLCanvasElement | null => {
    // We are using :id on the QrcodeVue component which passes it to the canvas
    const canvas = document.getElementById(`qr-canvas-${tableNo}`) as HTMLCanvasElement;
    return canvas;
}

const downloadQR = (tableNo: number) => {
  const canvas = getCanvasFromId(tableNo);
  if (!canvas) return;

  const link = document.createElement("a");
  link.download = `table-${tableNo}-qr.png`;
  link.href = canvas.toDataURL("image/png");
  link.click();
};

const printSingle = (tableNo: number) => {
  const canvas = getCanvasFromId(tableNo);
  if (!canvas) return;

  const win = window.open("");
  if (!win) return;
  win.document.write(`
    <html><head><title>Table ${tableNo} QR Code</title>
    <style>body { display: flex; flex-direction: column; align-items: center; justify-content: center; min-height: 100vh; font-family: system-ui; }
    h2 { margin-bottom: 8px; } p { color: #666; font-size: 12px; }</style></head>
    <body>
      <h2>${selectedBranch.value?.name || "Cafe"}</h2>
      <img src="${canvas.toDataURL()}" width="250" height="250" />
      <h3>Table ${tableNo}</h3>
      <p>Scan to view menu and order</p>
    </body></html>
  `);
  win.document.close();
  win.print();
};

const printAll = () => {
  window.print();
};

useHead({ title: "QR Table Codes" });
</script>

<style scoped>
@media print {
  :deep(.print\\:hidden) {
    display: none !important;
  }
}
</style>
