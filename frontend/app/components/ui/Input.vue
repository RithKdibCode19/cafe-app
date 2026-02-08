<template>
  <div class="relative">
    <label
      v-if="label"
      :for="id"
      class="label"
    >
      {{ label }}
      <span v-if="required" class="text-error-500 ml-1">*</span>
    </label>

    <div class="relative">
      <div
        v-if="$slots.prefix"
        class="absolute left-3 top-1/2 -translate-y-1/2 text-neutral-400"
      >
        <slot name="prefix" />
      </div>

      <input
        :id="id"
        v-model="model"
        :type="type"
        :placeholder="placeholder"
        :disabled="disabled"
        :readonly="readonly"
        :required="required"
        :class="inputClasses"
      />

      <div
        v-if="$slots.suffix"
        class="absolute right-3 top-1/2 -translate-y-1/2 text-neutral-400"
      >
        <slot name="suffix" />
      </div>
    </div>

    <p
      v-if="error"
      class="text-sm text-error-600 mt-1"
    >
      {{ error }}
    </p>

    <p
      v-if="hint && !error"
      class="text-sm text-neutral-500 mt-1"
    >
      {{ hint }}
    </p>
  </div>
</template>

<script setup lang="ts">
import { computed, useSlots } from 'vue'

interface Props {
  id?: string
  label?: string
  type?: 'text' | 'email' | 'password' | 'number' | 'tel' | 'url' | 'search'
  placeholder?: string
  disabled?: boolean
  readonly?: boolean
  required?: boolean
  error?: string
  hint?: string
}

const props = withDefaults(defineProps<Props>(), {
  id: () => `input-${Math.random().toString(36).substr(2, 9)}`,
  type: 'text',
  disabled: false,
  readonly: false,
  required: false
})

const model = defineModel<string | number>()
const slots = useSlots()

const inputClasses = computed(() => [
  'input',
  props.error ? 'input-error' : '',
  slots.prefix ? 'pl-10' : '',
  slots.suffix ? 'pr-10' : ''
])
</script>
