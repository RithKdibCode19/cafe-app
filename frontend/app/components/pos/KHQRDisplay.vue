<template>
  <div class="khqr-container relative bg-white rounded-[2rem] overflow-hidden shadow-[0_30px_60px_-15px_rgba(0,0,0,0.3)] w-full max-w-[320px] mx-auto animate-in fade-in zoom-in duration-700 select-none border border-neutral-100">
    <!-- Compact Header with Branding -->
    <div class="relative bg-[#E11D48] px-5 py-3 flex items-center justify-between overflow-hidden">
      <div class="absolute inset-0 bg-gradient-to-tr from-white/10 to-transparent pointer-events-none"></div>
      
      <div class="z-10 flex items-center gap-2">
        <div class="bg-white p-1 rounded-lg">
           <svg width="16" height="16" viewBox="0 0 40 40" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M20 0L37.3205 10V30L20 40L2.67949 30V10L20 0Z" fill="#E11D48"/>
              <path d="M20 8L30.3923 14V26L20 32L9.6077 26V14L20 8Z" fill="white"/>
            </svg>
        </div>
        <div class="leading-none">
          <h2 class="text-white font-black text-base tracking-tighter uppercase italic">KHQR</h2>
          <p class="text-[6px] text-white/70 font-black uppercase tracking-[0.2em]">Bakong Network</p>
        </div>
      </div>

      <div class="z-10 bg-white/20 backdrop-blur-md px-2 py-0.5 rounded-md border border-white/20">
         <span class="text-[7px] text-white font-black uppercase tracking-widest">USD</span>
      </div>
    </div>

    <!-- Main Content Area: Compacted -->
    <div class="px-5 pt-4 pb-0 space-y-3">
      <!-- QR Stage: Optimized -->
      <div class="relative group">
        <div class="relative bg-white p-2.5 rounded-[1.75rem] shadow-[inset_0_2px_8px_rgba(0,0,0,0.03)] border border-neutral-100 flex items-center justify-center transition-all">
          <qrcode-vue
            :value="qrValue"
            :size="180"
            level="H"
            render-as="svg"
            class="qr-code relative z-10"
          />
          <!-- Integrated Center Logo -->
          <div class="absolute inset-0 flex items-center justify-center pointer-events-none z-20">
              <div class="bg-white p-1 rounded-lg shadow-md border border-neutral-50 transform group-hover:scale-110 transition-transform">
                  <div class="w-8 h-8 rounded-md bg-[#E11D48] flex flex-col items-center justify-center text-white shadow-inner">
                      <span class="font-black text-[5px] uppercase tracking-tighter leading-none mb-0.5">Bakong</span>
                      <span class="font-black text-[8px] uppercase tracking-tighter leading-none">POS</span>
                  </div>
              </div>
          </div>
        </div>
        <div class="text-center mt-1.5">
           <p class="text-[8px] text-[#E11D48] font-black uppercase tracking-[0.3em] animate-pulse">Scan to Pay</p>
        </div>
      </div>

      <!-- Integrated Summary & Merchant -->
      <div class="bg-neutral-900 rounded-2xl p-4 shadow-inner relative overflow-hidden group">
         <div class="absolute top-0 right-0 w-20 h-20 bg-[#E11D48]/10 blur-2xl"></div>
         
         <div class="relative z-10 flex flex-col items-center">
            <div class="flex items-center gap-1.5 mb-0.5 opacity-60">
              <span class="w-1 h-1 rounded-full bg-green-500"></span>
              <p class="text-[8px] text-neutral-400 font-bold uppercase tracking-widest">{{ merchantName }}</p>
            </div>
            <div class="flex items-start text-white">
               <span class="text-primary-500 text-base font-bold mt-1 mr-1 opacity-80">$</span>
               <span class="text-[2.5rem] font-black tabular-nums tracking-tighter leading-none">{{ amount.toFixed(2) }}</span>
            </div>
         </div>
      </div>
    </div>
    
    <!-- Ultra-thin Bottom Trust Bar -->
    <div class="px-5 py-2 mt-3 bg-neutral-50/50 border-t border-neutral-100 flex items-center justify-between">
       <span class="text-[6px] text-neutral-400 font-black uppercase tracking-[0.3em]">Universal QR Acceptance</span>
       <div class="flex gap-0.5">
          <div v-for="i in 3" :key="i" class="w-0.5 h-2 bg-[#E11D48]/20 rounded-full" :style="{ opacity: i * 0.3 }"></div>
       </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import QrcodeVue from 'qrcode.vue'

defineProps({
  qrValue: {
    type: String,
    required: true
  },
  amount: {
    type: Number,
    required: true
  },
  merchantName: {
    type: String,
    default: 'Cafe POS System'
  }
})
</script>

<style scoped>
.khqr-container {
  background: white;
}

.qr-code {
  filter: drop-shadow(0 4px 10px rgba(0, 0, 0, 0.05));
}

@keyframes pulse-soft {
  0%, 100% { opacity: 0.5; }
  50% { opacity: 1; }
}

.animate-pulse {
  animation: pulse-soft 2s infinite ease-in-out;
}
</style>
