<template>
  <div class="min-h-screen flex items-center justify-center bg-gradient-to-br from-primary-50 via-white to-accent-50 dark:from-neutral-950 dark:via-neutral-900 dark:to-neutral-950">
    <!-- Decorative background -->
    <div class="absolute inset-0 overflow-hidden pointer-events-none">
      <div class="absolute -top-40 -right-40 w-80 h-80 bg-primary-200/30 dark:bg-primary-900/20 rounded-full blur-3xl"></div>
      <div class="absolute -bottom-40 -left-40 w-80 h-80 bg-accent-200/30 dark:bg-accent-900/20 rounded-full blur-3xl"></div>
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
