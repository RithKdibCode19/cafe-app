<template>
  <div v-if="modelValue" class="fixed inset-0 z-50 flex items-center justify-center bg-black/80 backdrop-blur-sm p-4">
    <div class="bg-neutral-800 rounded-2xl shadow-xl w-full max-w-sm border border-neutral-700 p-6">
      <h3 class="text-xl font-bold text-white mb-4">
          {{ shiftAction === 'OPEN' ? 'Start Shift' : 'End Shift' }}
      </h3>
      
      <div v-if="shiftAction === 'CLOSE' && summary" class="mb-4 text-sm text-neutral-400 bg-neutral-900/50 p-3 rounded-lg border border-neutral-700 space-y-2">
          <div class="flex justify-between">
              <span>Shift Started:</span>
              <span class="text-white">{{ summary.checkInTime ? new Date(summary.checkInTime).toLocaleTimeString() : '-' }}</span>
          </div>
          <div class="flex justify-between">
              <span>Starting Cash:</span>
              <span class="text-white font-mono">${{ summary.startingCash?.toFixed(2) }}</span>
          </div>
          <div class="flex justify-between">
              <span>Cash Sales:</span>
              <span class="text-success-400 font-mono">+${{ summary.salesByMethod?.CASH?.toFixed(2) || '0.00' }}</span>
          </div>
          <div class="border-t border-neutral-600 my-1"></div>
          <div class="flex justify-between font-bold text-white">
              <span>Expected Cash:</span>
              <span class="font-mono">${{ summary.expectedCash?.toFixed(2) }}</span>
          </div>
          
          <div class="flex justify-between font-bold" :class="[(shiftAmount - (summary.expectedCash || 0)) >= 0 ? 'text-success-500' : 'text-red-500']">
              <span>Difference:</span>
              <span class="font-mono">{{ (shiftAmount - (summary.expectedCash || 0)) >= 0 ? '+' : '' }}{{ (shiftAmount - (summary.expectedCash || 0)).toFixed(2) }}</span>
          </div>
      </div>

      <p class="text-neutral-400 text-sm mb-4">
          {{ shiftAction === 'OPEN' ? 'Enter starting cash amount.' : 'Count cash in drawer and enter amount.' }}
      </p>
      
      <div class="relative mb-6">
        <span class="absolute left-4 top-1/2 -translate-y-1/2 text-neutral-400 font-bold">$</span>
        <input 
          v-model="shiftAmount" 
          type="number" 
          min="0"
          step="0.01"
          class="w-full bg-neutral-900 border border-neutral-700 rounded-xl py-4 pl-8 pr-4 text-white text-xl font-bold focus:outline-none focus:border-primary-500 transition-colors"
          placeholder="0.00"
          @keyup.enter="performAction" 
        />
      </div>
      
      <div v-if="error" class="mb-4 text-sm text-red-500 bg-red-500/10 p-2 rounded-lg border border-red-500/20">
          {{ error }}
      </div>

      <div class="flex gap-3">
        <button @click="$emit('update:modelValue', false)" class="flex-1 py-3 rounded-xl border border-neutral-600 text-neutral-400 hover:text-white hover:border-neutral-500 font-bold">
          Cancel
        </button>
        <button @click="performAction" :disabled="loading" :class="['flex-1 py-3 rounded-xl text-white font-bold shadow-lg transition-colors', loading ? 'opacity-50 cursor-not-allowed' : '', shiftAction === 'OPEN' ? 'bg-success-600 hover:bg-success-500 shadow-success-900/20' : 'bg-red-600 hover:bg-red-500 shadow-red-900/20']">
          <span v-if="loading">Processing...</span>
          <span v-else>{{ shiftAction === 'OPEN' ? 'Start Shift' : 'End Shift' }}</span>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { useApi } from '~/composables/useApi'
import { useAuth } from '~/composables/useAuth'
import type { Shift, ShiftSummary } from '~/types/api'

const props = defineProps<{
  modelValue: boolean
  shift?: Shift | null
  summary?: ShiftSummary | null
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', value: boolean): void
  (e: 'success', shift: Shift | null): void
}>()

const { post } = useApi()
const { user } = useAuth()
const toast = useToast()

const shiftAmount = ref(0)
const shiftError = ref('')
const loading = ref(false)
const shiftAction = ref<'OPEN' | 'CLOSE'>('OPEN')

watch(() => props.modelValue, (isOpen) => {
  if (isOpen) {
    shiftAmount.value = 0
    shiftError.value = ''
    shiftAction.value = props.shift ? 'CLOSE' : 'OPEN'
  }
})

const performAction = async () => {
    if (!user.value?.userId) return
    loading.value = true
    shiftError.value = ''
    
    try {
        if (shiftAction.value === 'OPEN') {
            const data = await post<Shift>(`/shifts/open?userId=${user.value.userId}&startingCash=${shiftAmount.value}`, {})
            toast.success("Shift opened successfully")
            emit('success', data)
        } else {
            await post<void>(`/shifts/close?userId=${user.value.userId}&endingCash=${shiftAmount.value}`, {})
            toast.success("Shift closed successfully")
            emit('success', null)
        }
        emit('update:modelValue', false)
    } catch (e: any) {
        // Global error handler will catch most, but we might want local feedback too
        shiftError.value = e.message || "Failed to update shift"
    } finally {
        loading.value = false
    }
}
</script>
