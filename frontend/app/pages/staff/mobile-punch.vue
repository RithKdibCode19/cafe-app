<template>
  <div class="min-h-screen bg-neutral-950 p-4 flex flex-col items-center">
    <div class="w-full max-w-sm space-y-6">
      <!-- Header -->
      <div class="text-center py-6">
        <h1 class="text-2xl font-black text-white uppercase tracking-tighter">Mobile Punch</h1>
        <p class="text-neutral-500 text-xs font-bold uppercase tracking-widest">Secure Geolocation Check-in</p>
      </div>

      <!-- Step 1: Employee ID -->
      <div v-if="!employeeId" class="card bg-neutral-900 border border-neutral-800 p-6 rounded-3xl animate-in zoom-in">
         <label class="text-[10px] font-bold text-neutral-500 uppercase tracking-widest mb-2 block">Enter Your Staff ID</label>
         <div class="flex gap-2">
            <input v-model="idInput" type="number" class="flex-1 bg-neutral-800 text-white p-3 rounded-xl border border-neutral-700 font-mono text-center text-lg" placeholder="123" />
            <button @click="confirmId" :disabled="!idInput" class="bg-primary-600 text-white px-6 rounded-xl font-bold uppercase text-xs">Next</button>
         </div>
      </div>

      <!-- Main Flow -->
      <div v-else class="space-y-6 animate-in slide-in-from-bottom-4">
          
          <!-- Location Status -->
          <div :class="['p-4 rounded-2xl border flex items-center gap-3', location ? 'bg-success-900/20 border-success-900/50' : 'bg-neutral-900 border-neutral-800']">
              <div :class="['w-10 h-10 rounded-full flex items-center justify-center', location ? 'bg-success-500 text-white' : 'bg-neutral-800 text-neutral-500']">
                  <svg xmlns="http://www.w3.org/2000/svg" class="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0Z"/><circle cx="12" cy="10" r="3"/></svg>
              </div>
              <div>
                  <p class="text-xs font-bold uppercase tracking-widest text-neutral-400">Location Status</p>
                  <p v-if="location" class="text-white font-bold text-sm">Acquired ({{ location.latitude.toFixed(4) }}, {{ location.longitude.toFixed(4) }})</p>
                  <button v-else @click="getLocation" class="text-primary-500 font-bold text-sm underline hover:text-primary-400">Tap to Get Location</button>
              </div>
          </div>

          <!-- Scanner -->
          <div class="bg-black rounded-3xl overflow-hidden border border-neutral-800 aspect-square relative group">
              <div id="reader" class="w-full h-full"></div>
              
              <div v-if="!scanning" class="absolute inset-0 flex flex-col items-center justify-center bg-neutral-900/80 z-10">
                  <svg xmlns="http://www.w3.org/2000/svg" class="w-12 h-12 text-neutral-600 mb-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M3 7V5a2 2 0 0 1 2-2h2"/><path d="M17 3h2a2 2 0 0 1 2 2v2"/><path d="M21 17v2a2 2 0 0 1-2 2h-2"/><path d="M7 21H5a2 2 0 0 1-2-2v-2"/></svg>
                  <button @click="startScanner" :disabled="!location" class="bg-white text-black px-6 py-3 rounded-full font-black uppercase tracking-widest text-xs hover:scale-105 transition-transform disabled:opacity-50 disabled:cursor-not-allowed">
                      {{ location ? 'Start Camera' : 'Get Location First' }}
                  </button>
              </div>
          </div>

          <!-- Status Message -->
          <div v-if="status" :class="['text-center p-4 rounded-2xl text-xs font-bold uppercase tracking-widest animate-pulse', status.type === 'error' ? 'text-error-500 bg-error-500/10' : 'text-success-500 bg-success-500/10']">
              {{ status.msg }}
          </div>
          
          <button @click="reset" class="w-full py-4 text-neutral-600 hover:text-white text-xs font-bold uppercase tracking-widest">Switch User</button>
      </div>

    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { Html5Qrcode } from "html5-qrcode"

definePageMeta({
    layout: false
})

const { post } = useApi()

const idInput = ref('')
const employeeId = ref<number | null>(null)
const location = ref<any>(null)
const scanning = ref(false)
const status = ref<{type: 'error' | 'success', msg: string} | null>(null)
let html5QrCode: Html5Qrcode | null = null

const confirmId = () => {
    if (idInput.value) employeeId.value = parseInt(idInput.value)
}

const reset = () => {
    stopScanner()
    employeeId.value = null
    idInput.value = ''
    location.value = null
    status.value = null
}

const getLocation = () => {
    if (!navigator.geolocation) {
        status.value = { type: 'error', msg: 'Geolocation not supported' }
        return
    }
    status.value = { type: 'success', msg: 'Requesting location...' }
    navigator.geolocation.getCurrentPosition(
        (pos) => {
            location.value = {
                latitude: pos.coords.latitude,
                longitude: pos.coords.longitude
            }
            status.value = null
        },
        (err) => {
            status.value = { type: 'error', msg: 'Location denied: ' + err.message }
        }
    )
}

const startScanner = async () => {
    scanning.value = true
    html5QrCode = new Html5Qrcode("reader")
    try {
        await html5QrCode.start(
            { facingMode: "environment" },
            { fps: 10, qrbox: { width: 250, height: 250 } },
            onScanSuccess,
            (errorMessage) => { /* ignore parse errors */ }
        )
    } catch (err) {
        status.value = { type: 'error', msg: 'Camera failed. HTTPS required?' }
        scanning.value = false
    }
}

const stopScanner = () => {
    if (html5QrCode && html5QrCode.isScanning) {
        html5QrCode.stop().catch(err => console.error(err))
        scanning.value = false
    }
}

const onScanSuccess = async (decodedText: string, decodedResult: any) => {
    stopScanner()
    status.value = { type: 'success', msg: 'QR Scanned! Verifying...' }
    
    try {
        await post(`/attendance/mobile-check-in/${employeeId.value}`, {
            token: decodedText,
            latitude: location.value.latitude,
            longitude: location.value.longitude
        })
        status.value = { type: 'success', msg: 'Clock-In Successful!' }
        // Vibrate success
        if (navigator.vibrate) navigator.vibrate(200)
    } catch (err: any) {
        status.value = { type: 'error', msg: err.data?.message || 'Check-in Failed' }
        if (navigator.vibrate) navigator.vibrate([100, 50, 100])
    }
}

onUnmounted(() => {
    stopScanner()
})
</script>

<style scoped>
/* Custom override for scanner overlay */
:deep(#reader__scan_region) {
    background: transparent !important;
}
</style>
