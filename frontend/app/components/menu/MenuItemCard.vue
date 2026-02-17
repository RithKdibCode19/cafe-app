<template>
  <button
    @click="$emit('add', item)"
    class="flex gap-3 w-full text-left bg-white dark:bg-neutral-900 rounded-2xl p-3 border border-neutral-200/50 dark:border-neutral-800/50 hover:border-primary-200 dark:hover:border-primary-800 hover:shadow-lg hover:shadow-neutral-900/5 transition-all active:scale-[0.98] group"
  >
    <!-- Image -->
    <div class="w-24 h-24 rounded-xl overflow-hidden bg-neutral-100 dark:bg-neutral-800 flex-shrink-0">
      <img
        v-if="item.imageUrl"
        :src="item.imageUrl"
        :alt="item.name"
        class="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300"
      />
      <div v-else class="w-full h-full flex items-center justify-center text-3xl">
        🍽️
      </div>
    </div>

    <!-- Info -->
    <div class="flex-1 min-w-0 py-0.5 flex flex-col justify-between">
      <div>
        <h3 class="font-semibold text-neutral-900 dark:text-white text-sm leading-tight truncate">
          {{ trans(item, 'name') }}
        </h3>
        <p v-if="item.categoryName" class="text-xs text-neutral-400 dark:text-neutral-500 mt-0.5">
          {{ trans(item, 'categoryName') }}
        </p>
        <p v-if="item.variants && item.variants.length > 0" class="text-xs text-neutral-500 dark:text-neutral-400 mt-1">
          {{ item.variants.map((v: any) => v.size).join(' · ') }}
        </p>
      </div>
      <div class="flex items-center justify-between mt-auto">
        <span class="text-sm font-bold text-primary-600 dark:text-primary-400">
          <template v-if="item.variants && item.variants.length > 0">
            from ${{ Math.min(...item.variants.map((v: any) => v.price)).toFixed(2) }}
          </template>
          <template v-else>
            ${{ item.basePrice.toFixed(2) }}
          </template>
        </span>
        <span class="w-8 h-8 flex items-center justify-center rounded-full bg-primary-50 dark:bg-primary-900/30 text-primary-600 dark:text-primary-400 group-hover:bg-primary-600 group-hover:text-white transition-all">
          <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4"/></svg>
        </span>
      </div>
    </div>
  </button>
</template>

<script setup lang="ts">
import { useTrans } from '~/composables/useTrans'
const { trans } = useTrans()

defineProps<{
  item: {
    menuItemId: number
    name: string
    nameKh?: string
    basePrice: number
    imageUrl?: string
    categoryName?: string
    categoryNameKh?: string
    variants?: { variantId: number; size: string; price: number }[]
  }
}>()

defineEmits(['add'])
</script>
