<template>
  <div class="min-h-screen bg-neutral-100 dark:bg-neutral-950">
    <!-- Sidebar -->
    <aside
      :class="[
        'fixed inset-y-0 left-0 z-50 flex flex-col transition-all duration-300 ease-out',
        'bg-white dark:bg-neutral-900 border-r border-neutral-200 dark:border-neutral-800',
        sidebarOpen ? 'w-64' : 'w-20'
      ]"
    >
      <!-- Logo -->
      <div class="flex items-center h-16 px-4 border-b border-neutral-200 dark:border-neutral-800">
        <div class="flex items-center gap-3">
          <div class="w-10 h-10 rounded-xl gradient-primary flex items-center justify-center">
            <svg xmlns="http://www.w3.org/2000/svg" class="w-6 h-6 text-white" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M17 8h1a4 4 0 1 1 0 8h-1" />
              <path d="M3 8h14v9a4 4 0 0 1-4 4H7a4 4 0 0 1-4-4Z" />
              <line x1="6" x2="6" y1="2" y2="4" />
              <line x1="10" x2="10" y1="2" y2="4" />
              <line x1="14" x2="14" y1="2" y2="4" />
            </svg>
          </div>
          <span
            :class="[
              'font-bold text-lg text-neutral-900 dark:text-white transition-opacity duration-200',
              sidebarOpen ? 'opacity-100' : 'opacity-0 w-0 overflow-hidden'
            ]"
          >
            Cafe POS
          </span>
        </div>
      </div>

      <!-- Navigation -->
      <nav class="flex-1 px-3 py-4 space-y-1 overflow-y-auto scrollbar-thin">
        <NuxtLink
          v-for="item in navigation"
          :key="item.name"
          :to="item.href"
          :class="[
            'flex items-center gap-3 px-3 py-2.5 rounded-xl transition-all duration-200',
            isActive(item.href)
              ? 'bg-primary-100 text-primary-700 dark:bg-primary-900/30 dark:text-primary-400'
              : 'text-neutral-600 hover:bg-neutral-100 dark:text-neutral-400 dark:hover:bg-neutral-800'
          ]"
        >
          <component :is="item.icon" class="w-5 h-5 flex-shrink-0" />
          <span
            :class="[
              'font-medium transition-opacity duration-200',
              sidebarOpen ? 'opacity-100' : 'opacity-0 w-0 overflow-hidden'
            ]"
          >
            {{ item.name }}
          </span>
        </NuxtLink>
      </nav>

      <!-- Toggle button -->
      <div class="p-3 border-t border-neutral-200 dark:border-neutral-800">
        <button
          @click="sidebarOpen = !sidebarOpen"
          class="w-full flex items-center justify-center p-2 rounded-xl text-neutral-500 hover:bg-neutral-100 dark:hover:bg-neutral-800 transition-colors"
        >
          <svg
            xmlns="http://www.w3.org/2000/svg"
            class="w-5 h-5 transition-transform duration-300"
            :class="{ 'rotate-180': sidebarOpen }"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            stroke-width="2"
            stroke-linecap="round"
            stroke-linejoin="round"
          >
            <path d="m9 18 6-6-6-6" />
          </svg>
        </button>
      </div>
    </aside>

    <!-- Main content -->
    <main
      :class="[
        'min-h-screen transition-all duration-300 ease-out',
        sidebarOpen ? 'ml-64' : 'ml-20'
      ]"
    >
      <!-- Top bar -->
      <header class="sticky top-0 z-40 h-16 px-6 flex items-center justify-between bg-white/80 dark:bg-neutral-900/80 backdrop-blur-lg border-b border-neutral-200 dark:border-neutral-800">
        <div>
          <h1 class="text-lg font-semibold text-neutral-900 dark:text-white">
            {{ pageTitle }}
          </h1>
        </div>

        <div class="flex items-center gap-4">
          <!-- Low Stock Alert -->
          <NuxtLink 
            to="/admin/inventory" 
            class="relative p-2 rounded-xl text-neutral-500 hover:bg-neutral-100 dark:hover:bg-neutral-800 transition-colors"
            title="Low Stock Alerts"
          >
            <svg xmlns="http://www.w3.org/2000/svg" class="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M6 8a6 6 0 0 1 12 0c0 7 3 9 3 9H3s3-2 3-9"/><path d="M10.3 21a1.94 1.94 0 0 0 3.4 0"/>
            </svg>
            <span v-if="lowStockCount > 0" class="absolute top-1.5 right-1.5 w-4 h-4 bg-error-500 text-white text-[10px] font-bold rounded-full flex items-center justify-center border-2 border-white dark:border-neutral-900">
              {{ lowStockCount }}
            </span>
          </NuxtLink>

          <!-- Dark mode toggle -->
          <button
            @click="toggleDarkMode"
            class="p-2 rounded-xl text-neutral-500 hover:bg-neutral-100 dark:hover:bg-neutral-800 transition-colors"
          >
            <svg v-if="isDark" xmlns="http://www.w3.org/2000/svg" class="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <circle cx="12" cy="12" r="4" />
              <path d="M12 2v2" />
              <path d="M12 20v2" />
              <path d="m4.93 4.93 1.41 1.41" />
              <path d="m17.66 17.66 1.41 1.41" />
              <path d="M2 12h2" />
              <path d="M20 12h2" />
              <path d="m6.34 17.66-1.41 1.41" />
              <path d="m19.07 4.93-1.41 1.41" />
            </svg>
            <svg v-else xmlns="http://www.w3.org/2000/svg" class="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M12 3a6 6 0 0 0 9 9 9 9 0 1 1-9-9Z" />
            </svg>
          </button>

          <!-- User menu -->
          <div class="flex items-center gap-3">
             <div class="flex flex-col items-end mr-1">
                <span class="text-xs font-bold text-neutral-900 dark:text-white">{{ authUser?.employeeName || authUser?.username }}</span>
                <span class="text-[10px] text-neutral-500 uppercase tracking-wider">{{ authUser?.roleName }}</span>
             </div>
             <button 
                @click="logout"
                class="w-9 h-9 rounded-full bg-primary-100 dark:bg-primary-900/30 flex items-center justify-center hover:bg-error-100 dark:hover:bg-error-900/30 transition-colors group"
                title="Logout"
             >
               <span class="text-sm font-semibold text-primary-700 dark:text-primary-400 group-hover:hidden">
                 {{ (authUser?.username?.substring(0,2) || 'AD').toUpperCase() }}
               </span>
               <svg xmlns="http://www.w3.org/2000/svg" class="w-5 h-5 text-red-500 hidden group-hover:block" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4" />
                  <polyline points="16 17 21 12 16 7" />
                  <line x1="21" x2="9" y1="12" y2="12" />
               </svg>
             </button>
          </div>
        </div>
      </header>

      <!-- Page content -->
      <div class="p-6">
        <slot />
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, h } from 'vue'

const route = useRoute()

const sidebarOpen = ref(true)
const isDark = ref(false)
const lowStockCount = ref(0)
const { get } = useApi()
const { user: authUser, logout: authLogout } = useAuth()
const toast = useToast()

const logout = async () => {
  await authLogout()
  toast.success('Logged out successfully')
}

const fetchLowStockCount = async () => {
  try {
    const data = await get<any[]>('/ingredients/low-stock')
    lowStockCount.value = data?.length || 0
  } catch (error) {
    console.error('Failed to fetch low stock count', error)
  }
}

const pageTitle = computed(() => {
  const titles: Record<string, string> = {
    '/admin': 'Dashboard',
    '/admin/menu': 'Menu Management',
    '/admin/orders': 'Orders',
    '/admin/customers': 'Customers',
    '/admin/reports': 'Reports',
    '/admin/branches': 'Branch Management',
    '/admin/settings': 'Settings'
  }
  return titles[route.path] || 'Dashboard'
})

// Icon components
const DashboardIcon = () => h('svg', {
  xmlns: 'http://www.w3.org/2000/svg',
  viewBox: '0 0 24 24',
  fill: 'none',
  stroke: 'currentColor',
  'stroke-width': '2',
  'stroke-linecap': 'round',
  'stroke-linejoin': 'round'
}, [
  h('rect', { width: '7', height: '9', x: '3', y: '3', rx: '1' }),
  h('rect', { width: '7', height: '5', x: '14', y: '3', rx: '1' }),
  h('rect', { width: '7', height: '9', x: '14', y: '12', rx: '1' }),
  h('rect', { width: '7', height: '5', x: '3', y: '16', rx: '1' })
])

const MenuIcon = () => h('svg', {
  xmlns: 'http://www.w3.org/2000/svg',
  viewBox: '0 0 24 24',
  fill: 'none',
  stroke: 'currentColor',
  'stroke-width': '2',
  'stroke-linecap': 'round',
  'stroke-linejoin': 'round'
}, [
  h('path', { d: 'M17 8h1a4 4 0 1 1 0 8h-1' }),
  h('path', { d: 'M3 8h14v9a4 4 0 0 1-4 4H7a4 4 0 0 1-4-4Z' })
])

const OrdersIcon = () => h('svg', {
  xmlns: 'http://www.w3.org/2000/svg',
  viewBox: '0 0 24 24',
  fill: 'none',
  stroke: 'currentColor',
  'stroke-width': '2',
  'stroke-linecap': 'round',
  'stroke-linejoin': 'round'
}, [
  h('path', { d: 'M16 3h5v5' }),
  h('path', { d: 'M8 3H3v5' }),
  h('path', { d: 'M12 22v-8.3a4 4 0 0 0-1.172-2.872L3 3' }),
  h('path', { d: 'm15 9 6-6' })
])

const CustomersIcon = () => h('svg', {
  xmlns: 'http://www.w3.org/2000/svg',
  viewBox: '0 0 24 24',
  fill: 'none',
  stroke: 'currentColor',
  'stroke-width': '2',
  'stroke-linecap': 'round',
  'stroke-linejoin': 'round'
}, [
  h('path', { d: 'M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2' }),
  h('circle', { cx: '9', cy: '7', r: '4' }),
  h('path', { d: 'M22 21v-2a4 4 0 0 0-3-3.87' }),
  h('path', { d: 'M16 3.13a4 4 0 0 1 0 7.75' })
])

const ReportsIcon = () => h('svg', {
  xmlns: 'http://www.w3.org/2000/svg',
  viewBox: '0 0 24 24',
  fill: 'none',
  stroke: 'currentColor',
  'stroke-width': '2',
  'stroke-linecap': 'round',
  'stroke-linejoin': 'round'
}, [
  h('path', { d: 'M3 3v18h18' }),
  h('path', { d: 'm19 9-5 5-4-4-3 3' })
])

const SettingsIcon = () => h('svg', {
  xmlns: 'http://www.w3.org/2000/svg',
  viewBox: '0 0 24 24',
  fill: 'none',
  stroke: 'currentColor',
  'stroke-width': '2',
  'stroke-linecap': 'round',
  'stroke-linejoin': 'round'
}, [
  h('path', { d: 'M12.22 2h-.44a2 2 0 0 0-2 2v.18a2 2 0 0 1-1 1.73l-.43.25a2 2 0 0 1-2 0l-.15-.08a2 2 0 0 0-2.73.73l-.22.38a2 2 0 0 0 .73 2.73l.15.1a2 2 0 0 1 1 1.72v.51a2 2 0 0 1-1 1.74l-.15.09a2 2 0 0 0-.73 2.73l.22.38a2 2 0 0 0 2.73.73l.15-.08a2 2 0 0 1 2 0l.43.25a2 2 0 0 1 1 1.73V20a2 2 0 0 0 2 2h.44a2 2 0 0 0 2-2v-.18a2 2 0 0 1 1-1.73l.43-.25a2 2 0 0 1 2 0l.15.08a2 2 0 0 0 2.73-.73l.22-.39a2 2 0 0 0-.73-2.73l-.15-.08a2 2 0 0 1-1-1.74v-.5a2 2 0 0 1 1-1.74l.15-.09a2 2 0 0 0 .73-2.73l-.22-.38a2 2 0 0 0-2.73-.73l-.15.08a2 2 0 0 1-2 0l-.43-.25a2 2 0 0 1-1-1.73V4a2 2 0 0 0-2-2z' }),
  h('circle', { cx: '12', cy: '12', r: '3' })
])

const InventoryIcon = () => h('svg', {
  xmlns: 'http://www.w3.org/2000/svg',
  viewBox: '0 0 24 24',
  fill: 'none',
  stroke: 'currentColor',
  'stroke-width': '2',
  'stroke-linecap': 'round',
  'stroke-linejoin': 'round'
}, [
  h('path', { d: 'M21 8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16Z' }),
  h('path', { d: 'm3.3 7 8.7 5 8.7-5' }),
  h('path', { d: 'M12 22V12' })
])

const KitchenIcon = () => h('svg', {
  xmlns: 'http://www.w3.org/2000/svg',
  viewBox: '0 0 24 24',
  fill: 'none',
  stroke: 'currentColor',
  'stroke-width': '2',
  'stroke-linecap': 'round',
  'stroke-linejoin': 'round'
}, [
  h('path', { d: 'M3 2v7c0 1.1.9 2 2 2h4a2 2 0 0 0 2-2V2' }),
  h('path', { d: 'M7 2v20' }),
  h('path', { d: 'M21 15V2v0a5 5 0 0 0-5 5v6c0 1.1.9 2 2 2h3zm0 0v7' })
])

const StaffIcon = () => h('svg', {
  xmlns: 'http://www.w3.org/2000/svg',
  viewBox: '0 0 24 24',
  fill: 'none',
  stroke: 'currentColor',
  'stroke-width': '2',
  'stroke-linecap': 'round',
  'stroke-linejoin': 'round'
}, [
  h('path', { d: 'M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2' }),
  h('circle', { cx: '9', cy: '7', r: '4' }),
  h('path', { d: 'M22 21v-2a4 4 0 0 0-3-3.87' }),
  h('path', { d: 'M16 3.13a4 4 0 0 1 0 7.75' })
])

const BranchIcon = () => h('svg', {
  xmlns: 'http://www.w3.org/2000/svg',
  viewBox: '0 0 24 24',
  fill: 'none',
  stroke: 'currentColor',
  'stroke-width': '2',
  'stroke-linecap': 'round',
  'stroke-linejoin': 'round'
}, [
  h('rect', { width: '16', height: '20', x: '4', y: '2', rx: '2', ry: '2' }),
  h('path', { d: 'M9 22v-4h6v4' }),
  h('path', { d: 'M8 6h.01' }),
  h('path', { d: 'M16 6h.01' }),
  h('path', { d: 'M12 6h.01' }),
  h('path', { d: 'M12 10h.01' }),
  h('path', { d: 'M12 14h.01' }),
  h('path', { d: 'M16 10h.01' }),
  h('path', { d: 'M16 14h.01' }),
  h('path', { d: 'M8 10h.01' }),
  h('path', { d: 'M8 14h.01' })
])

const navigation = [
  { name: 'Dashboard', href: '/admin', icon: DashboardIcon },
  { name: 'Menu', href: '/admin/menu', icon: MenuIcon },
  { name: 'Orders', href: '/admin/orders', icon: OrdersIcon },
  { name: 'Staff', href: '/admin/staff', icon: StaffIcon },
  { name: 'Customers', href: '/admin/customers', icon: CustomersIcon },
  { name: 'Inventory', href: '/admin/inventory', icon: InventoryIcon },
  { name: 'Recipes', href: '/admin/inventory/recipes', icon: MenuIcon },
  { name: 'Expenses', href: '/admin/expenses', icon: ReportsIcon }, // Using ReportsIcon as placeholder
  { name: 'Suppliers', href: '/admin/inventory/suppliers', icon: StaffIcon },
  { name: 'Kitchen', href: '/kitchen', icon: KitchenIcon },
  { name: 'Reports', href: '/admin/reports', icon: ReportsIcon },
  { name: 'Branches', href: '/admin/branches', icon: BranchIcon },
  { name: 'Settings', href: '/admin/settings', icon: SettingsIcon }
]

const isActive = (href: string) => {
  if (href === '/admin') {
    return route.path === '/admin'
  }
  return route.path.startsWith(href)
}

const toggleDarkMode = () => {
  isDark.value = !isDark.value
  document.documentElement.classList.toggle('dark', isDark.value)
}

// Initialize dark mode from system preference
onMounted(() => {
  isDark.value = window.matchMedia('(prefers-color-scheme: dark)').matches
  document.documentElement.classList.toggle('dark', isDark.value)
  fetchLowStockCount()
})
</script>
