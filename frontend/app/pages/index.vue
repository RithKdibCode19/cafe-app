End of Shift (Clock Out & Cash Up):
<template>
  <div
    class="min-h-screen bg-[#0D0D0D] text-white selection:bg-primary-500/30 overflow-hidden font-sans"
  >
    <!-- Hero Section -->
    <header
      class="relative min-h-screen flex flex-col justify-center items-center overflow-hidden"
    >
      <!-- Premium Background Effects -->
      <div class="absolute inset-0 pointer-events-none">
        <!-- Main Gradient Mesh -->
        <div
          class="absolute top-[-20%] left-[-10%] w-[70%] h-[70%] bg-primary-600/20 rounded-full blur-[120px] animate-pulse-soft"
        ></div>
        <div
          class="absolute bottom-[-10%] right-[-5%] w-[60%] h-[60%] bg-indigo-600/10 rounded-full blur-[100px] animate-pulse-soft"
          style="animation-delay: 1s"
        ></div>

        <!-- Grid Pattern Overlay -->
        <div
          class="absolute inset-0 bg-[url('https://grainy-gradients.vercel.app/noise.svg')] opacity-20 brightness-100 contrast-150 mix-blend-overlay"
        ></div>
      </div>

      <div class="relative z-10 max-w-7xl mx-auto px-6 text-center -mt-20">
        <!-- Logo Container -->
        <div
          class="inline-flex items-center justify-center p-3 rounded-[28px] bg-white/5 backdrop-blur-2xl border border-white/10 shadow-macos-lg mb-10 animate-fade-in ring-1 ring-white/20"
        >
          <div class="w-20 h-20 rounded-2xl overflow-hidden shadow-inner">
            <img
              src="~/assets/images/cofeoshop.jpg"
              alt="Cofeoshop Logo"
              class="w-full h-full object-cover scale-105"
            />
          </div>
        </div>

        <!-- Typography -->
        <h1
          class="text-6xl md:text-8xl font-bold tracking-tight mb-8 animate-slide-up bg-clip-text text-transparent bg-gradient-to-b from-white via-white to-white/50 drop-shadow-sm"
        >
          Cofeoshop
        </h1>

        <p
          class="text-xl md:text-2xl text-neutral-400 max-w-2xl mx-auto mb-12 animate-slide-up leading-relaxed"
          style="animation-delay: 0.1s"
        >
          The premium point of sale experience. <br />
          <span class="text-neutral-500"
            >Designed for modern cafes. Perfected for speed.</span
          >
        </p>

        <!-- Premium Actions -->
        <div
          class="flex flex-col sm:flex-row items-center justify-center gap-4 animate-slide-up"
          style="animation-delay: 0.2s"
        >
          <NuxtLink
            to="/pos"
            class="group relative px-8 py-4 bg-white text-black rounded-2xl font-bold text-lg hover:bg-neutral-200 transition-all duration-300 shadow-[0_0_40px_-10px_rgba(255,255,255,0.3)] hover:shadow-[0_0_60px_-15px_rgba(255,255,255,0.5)] hover:-translate-y-1"
          >
            <span class="relative z-10 flex items-center gap-2">
              Launch POS
              <svg
                xmlns="http://www.w3.org/2000/svg"
                class="w-5 h-5 transition-transform group-hover:translate-x-1"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                stroke-width="2.5"
                stroke-linecap="round"
                stroke-linejoin="round"
              >
                <path d="M5 12h14" />
                <path d="m12 5 7 7-7 7" />
              </svg>
            </span>
          </NuxtLink>

          <NuxtLink
            to="/admin"
            class="group px-8 py-4 bg-white/5 text-white rounded-2xl font-medium text-lg backdrop-blur-md border border-white/10 hover:bg-white/10 transition-all duration-300 hover:-translate-y-1"
          >
            Dashboard
          </NuxtLink>
        </div>
      </div>
    </header>

    <!-- Essential Features Grid -->
    <section class="py-32 relative">
      <div class="max-w-7xl mx-auto px-6">
        <div class="grid md:grid-cols-3 gap-8">
          <!-- Card 1 -->
          <div
            class="group p-8 rounded-[32px] bg-neutral-900/50 border border-white/5 hover:border-white/10 transition-all duration-500 hover:bg-neutral-800/50 cursor-default relative overflow-hidden"
          >
            <div
              class="absolute inset-0 bg-gradient-to-br from-primary-500/5 to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-500"
            ></div>
            <div
              class="w-14 h-14 rounded-2xl bg-primary-500/10 flex items-center justify-center mb-6 text-primary-400 group-hover:scale-110 transition-transform duration-500"
            >
              <svg
                xmlns="http://www.w3.org/2000/svg"
                class="w-7 h-7"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                stroke-width="2"
              >
                <path d="M13 2L3 14h9l-1 8 10-12h-9l1-8z" />
              </svg>
            </div>
            <h3 class="text-2xl font-semibold text-white mb-3">
              Lightning Fast
            </h3>
            <p class="text-neutral-400 leading-relaxed">
              Built for high-volume environments. Process transactions instantly
              with zero lag.
            </p>
          </div>

          <!-- Card 2 -->
          <div
            class="group p-8 rounded-[32px] bg-neutral-900/50 border border-white/5 hover:border-white/10 transition-all duration-500 hover:bg-neutral-800/50 cursor-default relative overflow-hidden"
          >
            <div
              class="absolute inset-0 bg-gradient-to-br from-indigo-500/5 to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-500"
            ></div>
            <div
              class="w-14 h-14 rounded-2xl bg-indigo-500/10 flex items-center justify-center mb-6 text-indigo-400 group-hover:scale-110 transition-transform duration-500"
            >
              <svg
                xmlns="http://www.w3.org/2000/svg"
                class="w-7 h-7"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                stroke-width="2"
              >
                <path
                  d="M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z"
                />
                <polyline points="3.27 6.96 12 12.01 20.73 6.96" />
                <line x1="12" y1="22.08" x2="12" y2="12" />
                <line x1="3.27" y1="6.96" x2="12" y2="12.01" />
                <line x1="20.73" y1="6.96" x2="12" y2="12.01" />
              </svg>
            </div>
            <h3 class="text-2xl font-semibold text-white mb-3">
              Inventory Sync
            </h3>
            <p class="text-neutral-400 leading-relaxed">
              Real-time stock tracking across all branches. Never run out of
              your best-sellers.
            </p>
          </div>

          <!-- Card 3 -->
          <div
            class="group p-8 rounded-[32px] bg-neutral-900/50 border border-white/5 hover:border-white/10 transition-all duration-500 hover:bg-neutral-800/50 cursor-default relative overflow-hidden"
          >
            <div
              class="absolute inset-0 bg-gradient-to-br from-emerald-500/5 to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-500"
            ></div>
            <div
              class="w-14 h-14 rounded-2xl bg-emerald-500/10 flex items-center justify-center mb-6 text-emerald-400 group-hover:scale-110 transition-transform duration-500"
            >
              <svg
                xmlns="http://www.w3.org/2000/svg"
                class="w-7 h-7"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                stroke-width="2"
              >
                <path d="M12 20h9" />
                <path
                  d="M16.5 3.5a2.121 2.121 0 0 1 3 3L7 19l-4 1 1-4L16.5 3.5z"
                />
              </svg>
            </div>
            <h3 class="text-2xl font-semibold text-white mb-3">
              Beautiful Reports
            </h3>
            <p class="text-neutral-400 leading-relaxed">
              Visualize your venue's performance with stunning, interactive
              charts and insights.
            </p>
          </div>
        </div>
      </div>
    </section>

    <!-- Footer -->
    <footer class="py-12 text-center border-t border-white/5">
      <p class="text-neutral-500 text-sm">
        &copy; 2026 Cofeoshop. Crafted with
        <span class="text-red-500">♥</span> for coffee lovers.
      </p>
    </footer>
  </div>
</template>

<script setup lang="ts">
definePageMeta({
  layout: "default",
});
</script>
