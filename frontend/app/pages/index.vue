End of Shift (Clock Out & Cash Up):<template>
  <div class="min-h-screen bg-gradient-to-br from-neutral-900 via-neutral-800 to-neutral-900 flex flex-col">
    <!-- Hero Section -->
    <header class="relative overflow-hidden">
      <!-- Background pattern -->
      <div class="absolute inset-0 opacity-10">
        <div class="absolute inset-0" style="background-image: url('data:image/svg+xml,%3Csvg width=&quot;60&quot; height=&quot;60&quot; viewBox=&quot;0 0 60 60&quot; xmlns=&quot;http://www.w3.org/2000/svg&quot;%3E%3Cg fill=&quot;none&quot; fill-rule=&quot;evenodd&quot;%3E%3Cg fill=&quot;%23ffffff&quot; fill-opacity=&quot;0.4&quot;%3E%3Cpath d=&quot;M36 34v-4h-2v4h-4v2h4v4h2v-4h4v-2h-4zm0-30V0h-2v4h-4v2h4v4h2V6h4V4h-4zM6 34v-4H4v4H0v2h4v4h2v-4h4v-2H6zM6 4V0H4v4H0v2h4v4h2V6h4V4H6z&quot;/%3E%3C/g%3E%3C/g%3E%3C/svg%3E');"></div>
      </div>

      <div class="relative max-w-7xl mx-auto px-4 py-20 text-center">
        <!-- Logo -->
        <div class="inline-flex items-center justify-center w-20 h-20 rounded-2xl gradient-primary mb-8 animate-fade-in">
          <svg xmlns="http://www.w3.org/2000/svg" class="w-10 h-10 text-white" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M17 8h1a4 4 0 1 1 0 8h-1" />
            <path d="M3 8h14v9a4 4 0 0 1-4 4H7a4 4 0 0 1-4-4Z" />
            <line x1="6" x2="6" y1="2" y2="4" />
            <line x1="10" x2="10" y1="2" y2="4" />
            <line x1="14" x2="14" y1="2" y2="4" />
          </svg>
        </div>

        <h1 class="text-5xl md:text-6xl font-bold text-white mb-4 animate-slide-up">
          Welcome to <span class="text-gradient">Cafe POS</span>
        </h1>
        <p class="text-xl text-neutral-400 max-w-2xl mx-auto mb-10 animate-slide-up" style="animation-delay: 0.1s;">
          Modern point of sale system designed for cafes and restaurants. Fast, intuitive, and beautiful.
        </p>

        <!-- Action buttons -->
        <div class="flex flex-wrap justify-center gap-4 animate-slide-up" style="animation-delay: 0.2s;">
          <NuxtLink to="/pos" class="btn-primary btn-lg group">
            <svg xmlns="http://www.w3.org/2000/svg" class="w-5 h-5 group-hover:scale-110 transition-transform" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="m12 3-1.912 5.813a2 2 0 0 1-1.275 1.275L3 12l5.813 1.912a2 2 0 0 1 1.275 1.275L12 21l1.912-5.813a2 2 0 0 1 1.275-1.275L21 12l-5.813-1.912a2 2 0 0 1-1.275-1.275L12 3Z" />
            </svg>
            Open POS Terminal
          </NuxtLink>
          <NuxtLink to="/admin" class="btn bg-white/10 text-white hover:bg-white/20 btn-lg backdrop-blur-sm border border-white/20">
            <svg xmlns="http://www.w3.org/2000/svg" class="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <rect width="7" height="9" x="3" y="3" rx="1" />
              <rect width="7" height="5" x="14" y="3" rx="1" />
              <rect width="7" height="9" x="14" y="12" rx="1" />
              <rect width="7" height="5" x="3" y="16" rx="1" />
            </svg>
            Admin Dashboard
          </NuxtLink>
          <NuxtLink to="/staff/terminal" class="btn bg-neutral-800/50 text-neutral-400 hover:text-white hover:bg-neutral-800 btn-lg border border-neutral-700">
            <svg xmlns="http://www.w3.org/2000/svg" class="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/></svg>
            Staff Terminal
          </NuxtLink>
        </div>
      </div>
    </header>

    <!-- Features Section -->
    <section class="py-20 px-4">
      <div class="max-w-6xl mx-auto">
        <h2 class="text-3xl font-bold text-white text-center mb-12">
          Everything you need to run your cafe
        </h2>

        <div class="grid md:grid-cols-3 gap-6">
          <!-- Feature 1 -->
          <div class="card-hover bg-neutral-800/50 border-neutral-700 p-6">
            <div class="w-12 h-12 rounded-xl bg-primary-500/20 flex items-center justify-center mb-4">
              <svg xmlns="http://www.w3.org/2000/svg" class="w-6 h-6 text-primary-400" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="m12 3-1.912 5.813a2 2 0 0 1-1.275 1.275L3 12l5.813 1.912a2 2 0 0 1 1.275 1.275L12 21l1.912-5.813a2 2 0 0 1 1.275-1.275L21 12l-5.813-1.912a2 2 0 0 1-1.275-1.275L12 3Z" />
              </svg>
            </div>
            <h3 class="text-lg font-semibold text-white mb-2">Lightning Fast</h3>
            <p class="text-neutral-400">Process orders in seconds with our intuitive touch-friendly interface designed for speed.</p>
          </div>

          <!-- Feature 2 -->
          <div class="card-hover bg-neutral-800/50 border-neutral-700 p-6">
            <div class="w-12 h-12 rounded-xl bg-success-500/20 flex items-center justify-center mb-4">
              <svg xmlns="http://www.w3.org/2000/svg" class="w-6 h-6 text-success-400" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M3 3v18h18" />
                <path d="m19 9-5 5-4-4-3 3" />
              </svg>
            </div>
            <h3 class="text-lg font-semibold text-white mb-2">Real-time Analytics</h3>
            <p class="text-neutral-400">Track sales, inventory, and performance with beautiful dashboards and reports.</p>
          </div>

          <!-- Feature 3 -->
          <div class="card-hover bg-neutral-800/50 border-neutral-700 p-6">
            <div class="w-12 h-12 rounded-xl bg-warning-500/20 flex items-center justify-center mb-4">
              <svg xmlns="http://www.w3.org/2000/svg" class="w-6 h-6 text-warning-400" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M17 8h1a4 4 0 1 1 0 8h-1" />
                <path d="M3 8h14v9a4 4 0 0 1-4 4H7a4 4 0 0 1-4-4Z" />
              </svg>
            </div>
            <h3 class="text-lg font-semibold text-white mb-2">Menu Management</h3>
            <p class="text-neutral-400">Easily manage your menu items, categories, variants, and pricing all in one place.</p>
          </div>
        </div>
      </div>
    </section>

    <!-- Quick Stats -->
    <section class="py-16 px-4 border-t border-neutral-800">
      <div class="max-w-4xl mx-auto">
        <div class="grid grid-cols-2 md:grid-cols-4 gap-8 text-center">
          <div>
            <div class="text-3xl font-bold text-primary-400 mb-1">500+</div>
            <div class="text-sm text-neutral-500">Daily Orders</div>
          </div>
          <div>
            <div class="text-3xl font-bold text-success-400 mb-1">98%</div>
            <div class="text-sm text-neutral-500">Uptime</div>
          </div>
          <div>
            <div class="text-3xl font-bold text-warning-400 mb-1">2s</div>
            <div class="text-sm text-neutral-500">Avg. Order Time</div>
          </div>
          <div>
            <div class="text-3xl font-bold text-accent-400 mb-1">24/7</div>
            <div class="text-sm text-neutral-500">Support</div>
          </div>
        </div>
      </div>
    </section>

    <!-- Footer -->
    <footer class="mt-auto py-6 px-4 border-t border-neutral-800 text-center">
      <p class="text-sm text-neutral-500">
        &copy; 2026 Cafe POS. Built using Nuxt & Spring Boot
      </p>
    </footer>
  </div>
</template>

<script setup lang="ts">
definePageMeta({
  layout: 'default'
})
</script>
