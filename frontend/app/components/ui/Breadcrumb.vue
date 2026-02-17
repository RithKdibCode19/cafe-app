<template>
  <nav class="flex" aria-label="Breadcrumb">
    <ol class="inline-flex items-center gap-1.5 px-3 py-1.5 bg-black/5 dark:bg-white/5 rounded-full border border-black/5 dark:border-white/5 shadow-inner">
      <li class="inline-flex items-center">
        <NuxtLink to="/admin" class="flex items-center text-[11px] font-black text-neutral-500 hover:text-primary-600 dark:text-neutral-500 dark:hover:text-primary-400 transition-colors uppercase tracking-widest">
          <svg class="w-3.5 h-3.5 mr-1" fill="none" stroke="currentColor" stroke-width="2.5" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg"><path d="m3 9 9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/><polyline points="9 22 9 12 15 12 15 22"/></svg>
          Admin
        </NuxtLink>
      </li>
      <li v-for="(item, index) in items" :key="index" class="flex items-center gap-1.5">
        <svg class="w-3 h-3 text-neutral-300 dark:text-neutral-700" fill="none" stroke="currentColor" stroke-width="3" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg"><path d="m9 18 6-6-6-6"/></svg>
        <NuxtLink v-if="item.href" :to="item.href" class="text-[11px] font-black text-neutral-500 hover:text-primary-600 dark:text-neutral-500 dark:hover:text-primary-400 transition-colors uppercase tracking-widest">
          {{ item.label }}
        </NuxtLink>
        <span v-else class="text-[11px] font-black text-neutral-400 dark:text-neutral-600 opacity-60 uppercase tracking-widest">
          {{ item.label }}
        </span>
      </li>
    </ol>
  </nav>
</template>

<script setup lang="ts">
interface BreadcrumbItem {
  label: string
  href?: string
}

defineProps<{
  items: BreadcrumbItem[]
}>()
</script>
