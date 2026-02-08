<template>
  <div :class="cardClasses">
    <div
      v-if="$slots.header || title"
      class="px-6 py-4 border-b border-neutral-200 dark:border-neutral-800"
    >
      <slot name="header">
        <div class="flex items-center justify-between">
          <div>
            <h3 class="text-lg font-semibold text-neutral-900 dark:text-white">
              {{ title }}
            </h3>
            <p v-if="subtitle" class="text-sm text-neutral-500 mt-0.5">
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
      class="px-6 py-4 border-t border-neutral-200 dark:border-neutral-800 bg-neutral-50 dark:bg-neutral-800/50 rounded-b-2xl"
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
  props.hoverable ? 'card-hover' : 'card'
])
</script>
