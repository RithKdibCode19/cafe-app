<template>
  <div class="min-h-screen bg-neutral-950 flex flex-col items-center justify-center p-4 relative overflow-hidden selection:bg-primary-500/30">
    <!-- Background Abstract Shapes -->
    <div class="absolute top-0 left-0 w-full h-full overflow-hidden pointer-events-none">
      <div class="absolute top-[-10%] left-[-10%] w-[50%] h-[50%] bg-primary-900/20 rounded-full blur-[120px]"></div>
      <div class="absolute bottom-[-10%] right-[-10%] w-[50%] h-[50%] bg-blue-900/10 rounded-full blur-[120px]"></div>
    </div>

    <!-- Main Content -->
    <div class="relative z-10 text-center max-w-2xl mx-auto backdrop-blur-sm">
      <!-- Glitchy 404 Effect -->
      <div class="relative font-bold text-[12rem] md:text-[15rem] leading-none tracking-tighter text-transparent bg-clip-text bg-gradient-to-b from-neutral-800 to-neutral-950 select-none">
        <span>404</span>
        <div class="absolute inset-0 text-white/5 blur-sm transform translate-x-1" aria-hidden="true">404</div>
      </div>

      <div class="-mt-12 md:-mt-20 relative">
        <h1 class="text-4xl md:text-5xl font-bold text-white mb-6">
          Page Not Found
        </h1>
        
        <p class="text-lg md:text-xl text-neutral-400 mb-10 max-w-md mx-auto leading-relaxed">
          Oops! It seems you've wandered into the void. The page you're looking for doesn't exist or has been moved.
        </p>

        <div class="flex flex-col sm:flex-row items-center justify-center gap-4">
          <button 
            @click="handleError"
            class="btn-primary btn-lg min-w-[200px] group shadow-lg shadow-primary-900/20 hover:shadow-primary-600/40"
          >
            <svg xmlns="http://www.w3.org/2000/svg" class="w-5 h-5 group-hover:-translate-x-1 transition-transform" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="m12 19-7-7 7-7"/>
              <path d="M19 12H5"/>
            </svg>
            Back to Home
          </button>
        </div>
      </div>
    </div>

    <!-- Footer decoration -->
    <div class="absolute bottom-8 text-neutral-600 text-sm font-mono">
      Error Code: {{ error?.statusCode || 404 }}
    </div>
  </div>
</template>

<script setup lang="ts">
import type { NuxtError } from '#app'

const props = defineProps({
  error: Object as () => NuxtError
})

const handleError = () => {
  clearError({ redirect: '/' })
}

// Set layout to false to ensure full screen error page
definePageMeta({
  layout: false
})
</script>
