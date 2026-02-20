<template>
  <div class="min-h-screen bg-neutral-900 flex flex-col items-center justify-center p-4 relative overflow-hidden">
    <!-- Back Button -->
    <div class="absolute top-4 left-4 z-20">
      <NuxtLink to="/staff/terminal" class="p-3 bg-neutral-800 rounded-full text-white shadow-lg">
        <svg xmlns="http://www.w3.org/2000/svg" class="w-6 h-6" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M19 12H5"/><path d="m12 19-7-7 7-7"/></svg>
      </NuxtLink>
    </div>

    <!-- Scanner Viewfinder -->
    <div class="w-full max-w-sm aspect-square relative rounded-3xl overflow-hidden border-2 border-primary-500 shadow-[0_0_30px_rgba(var(--color-primary-500),0.3)] bg-black">
      <QrcodeStream @detect="onDetect" @error="onError">
        <div v-show="loading" class="absolute inset-0 flex items-center justify-center bg-black/50 text-white">
           <div class="w-8 h-8 border-2 border-white/30 border-t-white rounded-full animate-spin"></div>
        </div>
      </QrcodeStream>
        
      <!-- Scan Overlay -->
      <div class="absolute inset-0 pointer-events-none border-[40px] border-black/50"></div>
      <div class="absolute inset-0 flex items-center justify-center pointer-events-none">
          <div class="w-64 h-64 border-2 border-white/50 rounded-xl relative">
              <div class="absolute top-0 left-0 w-8 h-8 border-t-4 border-l-4 border-primary-500 -mt-1 -ml-1 rounded-tl-lg"></div>
              <div class="absolute top-0 right-0 w-8 h-8 border-t-4 border-r-4 border-primary-500 -mt-1 -mr-1 rounded-tr-lg"></div>
              <div class="absolute bottom-0 left-0 w-8 h-8 border-b-4 border-l-4 border-primary-500 -mb-1 -ml-1 rounded-bl-lg"></div>
              <div class="absolute bottom-0 right-0 w-8 h-8 border-b-4 border-r-4 border-primary-500 -mb-1 -mr-1 rounded-br-lg"></div>
          </div>
      </div>
    </div>

    <!-- User Info / Status -->
    <div class="mt-8 text-center max-w-xs">
        <div v-if="authUser" class="mb-4">
             <div class="w-16 h-16 rounded-full bg-neutral-800 mx-auto mb-2 flex items-center justify-center text-2xl font-bold text-primary-500 uppercase border border-neutral-700">
                {{ authUser.username?.substring(0,2) }}
             </div>
             <h2 class="text-white font-bold text-lg">{{ authUser.fullName || authUser.username }}</h2>
             <p class="text-neutral-500 text-sm">Employee ID: {{ authUser.userId }}</p>
        </div>
        <div v-else class="mb-4">
            <p class="text-error-500 font-bold animate-pulse">Please Login First</p>
            <NuxtLink to="/login" class="inline-block mt-2 text-primary-400 hover:text-white underline">Go to Login</NuxtLink>
        </div>

        <p class="text-neutral-400 text-sm font-medium">
            Scan the rotating QR code on the <span class="text-white font-bold">Staff Terminal</span> to Clock In/Out.
        </p>
        
        <p v-if="error" class="mt-4 text-error-500 text-sm font-bold bg-error-500/10 p-3 rounded-xl border border-error-500/20">
            {{ error }}
        </p>

        <p v-if="success" class="mt-4 text-success-500 text-sm font-bold bg-success-500/10 p-3 rounded-xl border border-success-500/20">
            {{ success }}
        </p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { QrcodeStream } from 'vue-qrcode-reader'

const { user: authUser } = useAuth()
const { post } = useApi()
const loading = ref(true)
const error = ref('')
const success = ref('')
const isProcessing = ref(false)

const onDetect = async (detectedCodes: any[]) => {
    if (isProcessing.value || !authUser.value) return
    
    const code = detectedCodes[0]?.rawValue
    if (!code) return

    isProcessing.value = true
    loading.value = true
    error.value = ''
    
    try {
        // Send to backend for validation
        // Expected code format: "TOKEN_TIMESTAMP_BRANCH" or just a JWT token
        const res = await post<any>('/attendance/clock-in/qr', { 
            qrToken: code,
            employeeId: authUser.value.userId // Or get from token if preferred
        })

        success.value = `Successfully ${res.action || 'Clocked In'}!`
        
        // Vibrate for feedback
        if (navigator.vibrate) navigator.vibrate(200)

        setTimeout(() => {
            success.value = ''
            isProcessing.value = false
        }, 3000)

    } catch (err: any) {
        error.value = err.data?.message || 'Invalid or Expired QR Code'
        // Pause briefly before allowing re-scan
        setTimeout(() => {
            error.value = ''
            isProcessing.value = false
        }, 2000)
    } finally {
        loading.value = false
    }
}

const onError = (err: any) => {
    error.value = "Camera Error: " + err.message
    loading.value = false
}
</script>

<style>
/* Override library styles if needed */
</style>
