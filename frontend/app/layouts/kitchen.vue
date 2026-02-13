<template>
  <div
    class="min-h-screen bg-neutral-900 text-white font-sans selection:bg-primary-500 selection:text-white"
  >
    <!-- Kitchen Header -->
    <header
      class="h-16 px-6 border-b border-neutral-800 flex items-center justify-between bg-neutral-900/90 backdrop-blur sticky top-0 z-50"
    >
      <div class="flex items-center gap-4">
        <NuxtLink
          to="/pos"
          class="flex flex-col items-center justify-center w-10 h-10 rounded-xl bg-orange-600 hover:bg-orange-500 transition-colors shadow-lg shadow-orange-900/50 group"
        >
          <svg
            xmlns="http://www.w3.org/2000/svg"
            class="w-6 h-6 text-white"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            stroke-width="2"
            stroke-linecap="round"
            stroke-linejoin="round"
          >
            <path d="M3 2v7c0 1.1.9 2 2 2h4a2 2 0 0 0 2-2V2" />
            <path d="M7 2v20" />
            <path d="M21 15V2v0a5 5 0 0 0-5 5v6c0 1.1.9 2 2 2h3zm0 0v7" />
          </svg>
        </NuxtLink>
        <div>
          <h1 class="font-black text-xl tracking-tight uppercase">
            Kitchen Display System
          </h1>
          <p
            class="text-xs text-neutral-400 font-medium uppercase tracking-widest ml-0.5"
          >
            Live Orders
          </p>
        </div>
      </div>

      <div class="flex items-center gap-6">
        <NuxtLink
          to="/pos"
          class="hidden md:flex items-center gap-2 px-4 py-2 bg-neutral-800 hover:bg-neutral-700 rounded-lg border border-neutral-700 text-xs font-bold uppercase tracking-widest transition-colors"
        >
          <svg
            xmlns="http://www.w3.org/2000/svg"
            class="w-4 h-4"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            stroke-width="2"
          >
            <rect width="20" height="14" x="2" y="3" rx="2" />
            <line x1="8" x2="16" y1="21" y2="21" />
            <line x1="12" x2="12" y1="17" y2="21" />
          </svg>
          Switch to POS
        </NuxtLink>
        <div
          class="px-4 py-2 bg-neutral-800 rounded-lg border border-neutral-700"
        >
          <span
            class="text-xs text-neutral-400 font-bold uppercase tracking-widest mr-2"
            >Time</span
          >
          <span class="text-xl font-mono font-bold">{{ currentTime }}</span>
        </div>
        <NuxtLink
          to="/admin"
          class="p-2 text-neutral-500 hover:text-white transition-colors"
          title="Admin Dashboard"
        >
          <svg
            xmlns="http://www.w3.org/2000/svg"
            class="w-6 h-6"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            stroke-width="2"
          >
            <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4" />
            <polyline points="16 17 21 12 16 7" />
            <line x1="21" y1="12" x2="9" y2="12" />
          </svg>
        </NuxtLink>
      </div>
    </header>

    <!-- Main Content -->
    <main class="p-6 h-[calc(100vh-64px)] overflow-hidden">
      <slot />
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from "vue";

const currentTime = ref("");
let timer: any;

const updateTime = () => {
  currentTime.value = new Date().toLocaleTimeString([], {
    hour: "2-digit",
    minute: "2-digit",
    second: "2-digit",
  });
};

onMounted(() => {
  updateTime();
  timer = setInterval(updateTime, 1000);
});

onUnmounted(() => {
  clearInterval(timer);
});
</script>
