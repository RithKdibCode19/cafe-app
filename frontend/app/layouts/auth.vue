<template>
  <div
    class="min-h-screen flex items-center justify-center bg-[#0D0D0D] relative overflow-hidden font-sans selection:bg-primary-500/30"
  >
    <!-- Premium Background Effects -->
    <div class="absolute inset-0 pointer-events-none">
      <!-- Main Gradient Mesh -->
      <div
        class="absolute top-[-10%] left-[-10%] w-[60%] h-[60%] bg-primary-900/10 rounded-full blur-[120px] animate-pulse-soft"
      ></div>
      <div
        class="absolute bottom-[-10%] right-[-5%] w-[50%] h-[50%] bg-indigo-900/10 rounded-full blur-[100px] animate-pulse-soft"
        style="animation-delay: 1s"
      ></div>

      <!-- Grid Pattern Overlay -->
      <div
        class="absolute inset-0 bg-[url('https://grainy-gradients.vercel.app/noise.svg')] opacity-20 brightness-100 contrast-150 mix-blend-overlay"
      ></div>
    </div>

    <!-- Content -->
    <div class="relative z-10 w-full max-w-md px-4">
      <slot />
    </div>
  </div>
</template>

<script setup lang="ts">
// Auth layout for login/register pages
</script>
