<template>
  <div :class="cardClasses">
    <div
      v-if="$slots.header || title"
      class="px-6 py-5 border-b border-black/5 dark:border-white/5"
    >
      <slot name="header">
        <div class="flex items-center justify-between">
          <div>
            <h3 class="text-[17px] font-black text-neutral-900 dark:text-white tracking-tight">
              {{ title }}
            </h3>
            <p v-if="subtitle" class="text-xs font-bold text-neutral-400 dark:text-neutral-500 uppercase tracking-widest mt-0.5">
              {{ subtitle }}
            </p>
          </div>
          <slot name="header-actions" />
        </div>
      </slot>
    </div>

    <div :class="['p-6', bodyClass]">
      <slot />
    </div>

    <div
      v-if="$slots.footer"
      class="px-6 py-4 border-t border-black/5 dark:border-white/5 bg-black/[0.02] dark:bg-white/[0.02] rounded-b-[18px]"
    >
      <slot name="footer" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

interface Props {
  title?: string
  subtitle?: string
  hoverable?: boolean
  bodyClass?: string
}

const props = withDefaults(defineProps<Props>(), {
  hoverable: false,
  bodyClass: ''
})

const cardClasses = computed(() => [
  'card-premium',
  props.hoverable ? 'hover:-translate-y-1' : ''
])
</script>
