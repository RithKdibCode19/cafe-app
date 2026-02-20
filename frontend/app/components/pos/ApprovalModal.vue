<template>
  <div v-if="modelValue" class="fixed inset-0 z-[60] flex items-center justify-center bg-black/80 backdrop-blur-sm p-4">
    <div class="bg-neutral-800 rounded-3xl shadow-2xl w-full max-w-md border border-neutral-700 overflow-hidden flex flex-col max-h-[90vh]">
      <!-- Header -->
      <div class="p-6 border-b border-neutral-700 flex justify-between items-center bg-neutral-800/50">
        <div>
          <h3 class="text-xl font-black text-white uppercase tracking-tight">Manager Approval</h3>
          <p class="text-neutral-400 text-xs font-bold uppercase tracking-widest mt-1">Required for {{ actionType }}</p>
        </div>
        <button @click="$emit('update:modelValue', false)" class="p-2 rounded-xl hover:bg-neutral-700 text-neutral-400 transition-colors">
          <svg xmlns="http://www.w3.org/2000/svg" class="w-6 h-6" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M18 6L6 18M6 6l12 12"/></svg>
        </button>
      </div>

      <div class="p-6 space-y-6 overflow-y-auto custom-scrollbar">
        <!-- Reason Selection -->
        <div>
          <label class="block text-xs font-black text-neutral-500 uppercase tracking-widest mb-3 italic">Reason for {{ actionType }}</label>
          <div class="grid grid-cols-2 gap-2">
            <button 
              v-for="r in commonReasons" 
              :key="r"
              @click="reason = r"
              :class="[
                'py-3 px-4 rounded-xl text-xs font-bold border transition-all duration-200',
                reason === r 
                  ? 'bg-primary-500/20 border-primary-500 text-primary-400 shadow-lg shadow-primary-500/10' 
                  : 'bg-neutral-900 border-neutral-700 text-neutral-400 hover:border-neutral-500 hover:text-white'
              ]"
            >
              {{ r }}
            </button>
          </div>
          
          <input 
            v-if="reason === 'Other'"
            v-model="otherReason"
            type="text"
            placeholder="Type specific reason..."
            class="w-full bg-neutral-900 border border-neutral-700 rounded-xl py-4 px-4 mt-3 text-white text-sm focus:outline-none focus:border-primary-500 transition-colors"
          />
        </div>

        <!-- PIN Entry Display -->
        <div>
          <label class="block text-xs font-black text-neutral-500 uppercase tracking-widest mb-3 italic text-center">Enter Manager PIN Code</label>
          <div class="flex justify-center gap-4 mb-4">
            <div v-for="i in 4" :key="i" 
              :class="[
                'w-12 h-16 rounded-2xl border-2 flex items-center justify-center text-2xl font-black transition-all duration-300',
                pin.length >= i 
                  ? 'border-primary-500 text-white bg-primary-500/10 shadow-lg shadow-primary-500/10 scale-105' 
                  : 'border-neutral-700 text-neutral-600 bg-neutral-900'
              ]"
            >
              <span v-if="pin.length >= i">•</span>
            </div>
          </div>
        </div>

        <!-- Numeric Keypad -->
        <div class="grid grid-cols-3 gap-3">
          <button 
            v-for="n in [1,2,3,4,5,6,7,8,9]" 
            :key="n"
            @click="addNumber(n)"
            class="h-16 rounded-2xl bg-neutral-900 border border-neutral-700 text-xl font-bold text-white hover:bg-neutral-700 active:scale-95 transition-all"
          >
            {{ n }}
          </button>
          <button @click="clear" class="h-16 rounded-2xl bg-neutral-900 border border-neutral-700 text-sm font-black text-red-500 uppercase hover:bg-red-500/10 active:scale-95 transition-all">Clear</button>
          <button @click="addNumber(0)" class="h-16 rounded-2xl bg-neutral-900 border border-neutral-700 text-xl font-bold text-white hover:bg-neutral-700 active:scale-95 transition-all">0</button>
          <button @click="deleteNumber" class="h-16 rounded-2xl bg-neutral-900 border border-neutral-700 flex items-center justify-center text-neutral-400 hover:bg-neutral-700 active:scale-95 transition-all">
            <svg xmlns="http://www.w3.org/2000/svg" class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5"><path d="M12 19l-7-7 7-7m5 14l-7-7 7-7"/></svg>
          </button>
        </div>
      </div>

      <!-- Footer Actions -->
      <div class="p-6 bg-neutral-800/80 border-t border-neutral-700">
        <button 
          @click="submit"
          :disabled="pin.length < 4 || (reason === 'Other' && !otherReason) || loading"
          :class="[
            'w-full py-5 rounded-2xl text-white font-black uppercase tracking-widest shadow-xl transition-all duration-300 active:scale-[0.98]',
            pin.length === 4 && (reason !== 'Other' || otherReason) && !loading
              ? 'bg-gradient-to-r from-primary-600 to-accent-600 hover:shadow-primary-500/20' 
              : 'bg-neutral-700 text-neutral-500 cursor-not-allowed opacity-50'
          ]"
        >
          <span v-if="loading" class="flex items-center justify-center gap-2">
            <svg class="animate-spin h-5 w-5 text-current" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24"><circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle><path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path></svg>
            Verifying...
          </span>
          <span v-else>Authorize {{ actionType }}</span>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'

const props = defineProps<{
  modelValue: boolean
  actionType: 'VOID' | 'REFUND'
  loading?: boolean
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', value: boolean): void
  (e: 'approve', data: { pin: string, reason: string }): void
}>()

const pin = ref('')
const reason = ref('Customer Request')
const otherReason = ref('')
const commonReasons = ['Customer Request', 'Wrong Item', 'Kitchen Error', 'Kitchen Slow', 'Spillage', 'Other']

watch(() => props.modelValue, (isOpen) => {
  if (isOpen) {
    pin.value = ''
    reason.value = 'Customer Request'
    otherReason.value = ''
  }
})

const addNumber = (n: number) => {
  if (pin.value.length < 4) {
    pin.value += n.toString()
  }
}

const deleteNumber = () => {
  pin.value = pin.value.slice(0, -1)
}

const clear = () => {
  pin.value = ''
}

const submit = () => {
  const finalReason = reason.value === 'Other' ? otherReason.value : reason.value
  emit('approve', { pin: pin.value, reason: finalReason })
}
</script>

<style scoped>
.custom-scrollbar::-webkit-scrollbar {
  width: 4px;
}
.custom-scrollbar::-webkit-scrollbar-track {
  background: transparent;
}
.custom-scrollbar::-webkit-scrollbar-thumb {
  background: #404040;
  border-radius: 10px;
}
</style>
