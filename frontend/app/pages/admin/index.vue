<template>
  <NuxtLayout name="admin">
    <div class="space-y-8 animate-in fade-in slide-in-from-bottom-6 duration-1000">
      <!-- Welcome Header -->
      <div class="flex items-end justify-between">
        <div>
          <h2 class="text-[28px] font-black text-neutral-900 dark:text-white tracking-tight leading-none mb-2">
            Good Morning, {{ authUser?.employeeName?.split(' ')[0] || 'Admin' }}
          </h2>
          <p class="text-neutral-500 dark:text-neutral-400 font-medium">Here's what's happening at your cafe today.</p>
        </div>
        <div class="hidden md:block">
           <UiBreadcrumb :items="[{ label: 'Dashboard' }]" />
        </div>
      </div>

      <!-- Stats Grid -->
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        <div 
          v-for="(card, index) in stats" 
          :key="card.label"
          :class="[
            'card-premium group p-6 transition-all duration-500 hover:scale-[1.02]',
            `stagger-${index + 1}`
          ]"
        >
          <div class="flex items-start justify-between mb-4">
            <div :class="['w-12 h-12 rounded-2xl flex items-center justify-center shadow-lg transition-transform group-hover:rotate-6', card.iconBg, card.iconColor]">
              <component :is="card.icon" class="w-6 h-6" />
            </div>
            <div :class="['flex items-center gap-1 px-2 py-1 rounded-full text-[11px] font-bold tracking-tight', card.trend >= 0 ? 'bg-success-500/10 text-success-600' : 'bg-error-500/10 text-error-600']">
              {{ card.trend >= 0 ? '↑' : '↓' }} {{ Math.abs(card.trend) }}%
            </div>
          </div>
          <div class="space-y-1">
            <p class="text-neutral-500 dark:text-neutral-400 text-sm font-bold uppercase tracking-widest">{{ card.label }}</p>
            <h3 class="text-3xl font-black text-neutral-900 dark:text-white tracking-tighter">{{ card.value }}</h3>
          </div>
        </div>
      </div>

      <!-- Quick Actions (Mac-style Widgets) -->
      <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
        <NuxtLink 
          to="/admin/qr-codes" 
          class="card-premium group relative overflow-hidden p-6 bg-gradient-to-br from-primary-600/90 to-primary-800/90 dark:from-primary-600/20 dark:to-primary-900/40 border-none shadow-macos-lg hover:scale-[1.03] transition-all duration-500"
        >
          <div class="relative z-10 flex flex-col h-full justify-between gap-8">
            <div class="w-14 h-14 bg-white/20 rounded-[20px] backdrop-blur-md flex items-center justify-center group-hover:rotate-12 transition-transform duration-500">
               <svg xmlns="http://www.w3.org/2000/svg" class="w-8 h-8 text-white" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
                  <rect width="5" height="5" x="3" y="3" rx="1.5"/><rect width="5" height="5" x="16" y="3" rx="1.5"/><rect width="5" height="5" x="3" y="16" rx="1.5"/><path d="M21 16h-3a2 2 0 0 0-2 2v3"/><path d="M21 21v.01"/><path d="M12 7v3a2 2 0 0 1-2 2H7"/><path d="M3 12h.01"/><path d="M12 3h.01"/><path d="M12 12h.01"/><path d="M12 21h.01"/><path d="M21 12h.01"/><path d="M7 21h.01"/>
               </svg>
            </div>
            <div>
              <h4 class="text-[19px] font-black text-white tracking-tight mb-1">QR Setup</h4>
              <p class="text-white/70 text-sm font-medium">Create and print table menus</p>
            </div>
          </div>
          <!-- Ambient lighting -->
          <div class="absolute -right-10 -bottom-10 w-40 h-40 bg-white/20 rounded-full blur-[60px] group-hover:blur-[80px] transition-all"></div>
        </NuxtLink>

        <NuxtLink 
          to="/admin/menu" 
          class="card-premium group p-6 hover:scale-[1.03] transition-all duration-500 border-primary-500/10"
        >
          <div class="flex flex-col h-full justify-between gap-8">
            <div class="w-14 h-14 bg-primary-500/10 rounded-[20px] flex items-center justify-center text-primary-600 group-hover:shadow-lg group-hover:shadow-primary-500/20 transition-all duration-500">
              <svg xmlns="http://www.w3.org/2000/svg" class="w-8 h-8" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
                <path d="M17 8h1a4 4 0 1 1 0 8h-1"/><path d="M3 8h14v9a4 4 0 0 1-4 4H7a4 4 0 0 1-4-4Z"/>
              </svg>
            </div>
            <div>
              <h4 class="text-[19px] font-black text-neutral-900 dark:text-white tracking-tight mb-1">Catalogue</h4>
              <p class="text-neutral-500 dark:text-neutral-400 text-sm font-medium">Update products and pricing</p>
            </div>
          </div>
        </NuxtLink>

        <NuxtLink 
          to="/admin/reports" 
          class="card-premium group p-6 hover:scale-[1.03] transition-all duration-500 border-accent-500/10"
        >
          <div class="flex flex-col h-full justify-between gap-8">
            <div class="w-14 h-14 bg-accent-500/10 rounded-[20px] flex items-center justify-center text-accent-600 group-hover:shadow-lg group-hover:shadow-accent-500/20 transition-all duration-500">
              <svg xmlns="http://www.w3.org/2000/svg" class="w-8 h-8" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
                <path d="M3 3v18h18"/><path d="m19 9-5 5-4-4-3 3"/>
              </svg>
            </div>
            <div>
              <h4 class="text-[19px] font-black text-neutral-900 dark:text-white tracking-tight mb-1">Analytics</h4>
              <p class="text-neutral-500 dark:text-neutral-400 text-sm font-medium">Monitor store performance</p>
            </div>
          </div>
        </NuxtLink>
      </div>

      <!-- Charts & Content Row -->
      <div class="grid lg:grid-cols-3 gap-6">
        <!-- Sales Overview -->
        <div class="lg:col-span-2 card-premium p-8 relative overflow-hidden group">
          <div class="flex items-center justify-between mb-8 relative z-10">
            <div>
              <h3 class="text-[20px] font-black text-neutral-900 dark:text-white tracking-tight">Sales Overview</h3>
              <p class="text-sm text-neutral-500 font-medium">Daily revenue track record</p>
            </div>
            <div class="flex items-center gap-2 p-1 bg-black/5 dark:bg-white/5 rounded-xl">
              <button class="px-3 py-1.5 text-xs font-bold bg-white dark:bg-neutral-800 rounded-lg shadow-sm">Weekly</button>
              <button class="px-3 py-1.5 text-xs font-bold text-neutral-500 hover:text-neutral-900 transition-colors">Monthly</button>
            </div>
          </div>
          
          <div class="relative z-10">
            <div v-if="loading" class="h-72 skeleton-shimmer !bg-neutral-200/50 dark:!bg-white/5"></div>
            <div v-else class="h-72">
               <AdminDashboardChart :data="dailySales" />
            </div>
          </div>

          <!-- Glass light leak -->
          <div class="absolute -top-20 -left-20 w-64 h-64 bg-primary-500/5 rounded-full blur-[80px] group-hover:bg-primary-500/10 transition-colors"></div>
        </div>

        <!-- Sidebar Widgets -->
        <div class="space-y-6">
          <!-- Top Products Widget -->
          <div class="card-premium p-6">
            <div class="flex items-center justify-between mb-6">
              <h3 class="text-[17px] font-bold text-neutral-900 dark:text-white tracking-tight">Hottest Items</h3>
              <div v-if="!loading && topProducts.length > 0" class="w-8 h-8 rounded-full bg-success-500/10 flex items-center justify-center text-success-600">
                <svg xmlns="http://www.w3.org/2000/svg" class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3"><path d="M12 19V5"/><path d="m5 12 7-7 7 7"/></svg>
              </div>
            </div>

            <div class="space-y-4">
              <template v-if="loading">
                <div v-for="i in 3" :key="i" class="flex items-center gap-4">
                  <div class="w-12 h-12 rounded-2xl skeleton-shimmer"></div>
                  <div class="flex-1 space-y-2">
                    <div class="h-4 w-3/4 skeleton-shimmer rounded-full"></div>
                    <div class="h-3 w-1/2 skeleton-shimmer rounded-full"></div>
                  </div>
                </div>
              </template>
              <template v-else>
                <div 
                  v-for="(product, index) in topProducts.slice(0, 4)" 
                  :key="product.name" 
                  class="flex items-center gap-4 group cursor-default"
                >
                  <div class="relative">
                    <div class="w-12 h-12 rounded-2xl bg-[#F2F2F7] dark:bg-white/5 flex items-center justify-center font-black text-neutral-900 dark:text-white transition-all group-hover:scale-110 group-hover:rotate-3 shadow-sm border border-black/5 dark:border-white/5">
                      {{ product.initial }}
                    </div>
                    <div class="absolute -top-1.5 -right-1.5 w-5 h-5 rounded-full bg-white dark:bg-neutral-900 shadow-sm border border-black/5 dark:border-white/5 flex items-center justify-center text-[10px] font-black">
                      {{ index + 1 }}
                    </div>
                  </div>
                  <div class="flex-1 min-w-0">
                    <h5 class="text-sm font-bold text-neutral-900 dark:text-white truncate">{{ product.name }}</h5>
                    <p class="text-[11px] font-bold text-neutral-400 dark:text-neutral-500 uppercase tracking-wider">{{ product.category }} • {{ product.sold }} sold</p>
                  </div>
                  <div class="text-right">
                    <p class="text-sm font-black text-neutral-900 dark:text-white">${{ product.revenue }}</p>
                  </div>
                </div>
              </template>
            </div>
          </div>

          <!-- Inventory Health -->
          <div class="card-premium p-6 border-l-4 border-l-primary-500 overflow-hidden relative">
            <h3 class="text-[17px] font-bold text-neutral-900 dark:text-white tracking-tight mb-4">Inventory Health</h3>
            
            <div v-if="loading" class="space-y-3">
              <div class="h-10 w-full skeleton-shimmer"></div>
              <div class="h-4 w-3/4 skeleton-shimmer"></div>
            </div>
            <div v-else-if="lowStockIngredients.length > 0" class="space-y-4">
              <div class="flex items-center gap-3 p-3 bg-primary-500/5 rounded-xl border border-primary-500/10">
                <div class="w-8 h-8 rounded-lg bg-primary-500 text-white flex items-center justify-center">
                  <svg xmlns="http://www.w3.org/2000/svg" class="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3"><circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/></svg>
                </div>
                <div class="flex-1">
                  <p class="text-xs font-black text-primary-600 dark:text-primary-400 uppercase tracking-widest leading-none mb-1">Attention Required</p>
                  <p class="text-sm font-bold text-neutral-900 dark:text-white leading-none">{{ lowStockIngredients.length }} items running low</p>
                </div>
              </div>
              <NuxtLink to="/admin/inventory" class="block w-full text-center py-2 text-xs font-black text-primary-600 dark:text-primary-400 hover:underline uppercase tracking-widest">Manage Stock</NuxtLink>
            </div>
            <div v-else class="text-center py-4">
               <div class="w-12 h-12 mx-auto mb-2 rounded-2xl bg-success-500/10 flex items-center justify-center text-success-600">
                  <svg xmlns="http://www.w3.org/2000/svg" class="w-6 h-6" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M20 6 9 17l-5-5"/></svg>
               </div>
               <p class="text-sm font-bold text-neutral-900 dark:text-white">All levels optimal</p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Recent Orders -->
    <div class="card">
      <div class="p-6 border-b border-neutral-200 dark:border-neutral-800">
        <div class="flex items-center justify-between">
          <h3 class="text-lg font-semibold text-neutral-900 dark:text-white">Recent Orders</h3>
          <NuxtLink to="/admin/orders" class="text-sm text-primary-600 hover:text-primary-700 font-medium">
            View all →
          </NuxtLink>
        </div>
      </div>
      <div class="overflow-x-auto">
        <table class="w-full">
          <thead>
            <tr class="bg-neutral-50 dark:bg-neutral-800/50">
              <th class="px-6 py-3 text-left text-xs font-semibold text-neutral-500 uppercase tracking-wider">Order ID</th>
              <th class="px-6 py-3 text-left text-xs font-semibold text-neutral-500 uppercase tracking-wider">Customer</th>
              <th class="px-6 py-3 text-left text-xs font-semibold text-neutral-500 uppercase tracking-wider">Items</th>
              <th class="px-6 py-3 text-left text-xs font-semibold text-neutral-500 uppercase tracking-wider">Total</th>
              <th class="px-6 py-3 text-left text-xs font-semibold text-neutral-500 uppercase tracking-wider">Status</th>
              <th class="px-6 py-3 text-left text-xs font-semibold text-neutral-500 uppercase tracking-wider">Time</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-neutral-200 dark:divide-neutral-800">
            <template v-if="loading">
              <tr v-for="i in 5" :key="i">
                <td v-for="j in 6" :key="j" class="px-6 py-4">
                  <div class="h-4 skeleton-shimmer rounded-full w-full"></div>
                </td>
              </tr>
            </template>
            <template v-else>
              <tr v-for="order in recentOrders" :key="order.id" class="hover:bg-neutral-50 dark:hover:bg-neutral-800/50 transition-colors">
                <td class="px-6 py-4 whitespace-nowrap">
                  <span class="text-sm font-mono text-neutral-900 dark:text-white font-bold">{{ order.id }}</span>
                </td>
                <td class="px-6 py-4 whitespace-nowrap">
                  <div class="flex items-center gap-3">
                    <div class="w-8 h-8 rounded-full bg-primary-100 dark:bg-primary-900/30 flex items-center justify-center border border-primary-200 dark:border-primary-800">
                      <span class="text-[10px] font-bold text-primary-600 dark:text-primary-400">{{ order.customer?.charAt(0).toUpperCase() }}</span>
                    </div>
                    <span class="text-sm font-semibold text-neutral-700 dark:text-neutral-300">{{ order.customer }}</span>
                  </div>
                </td>
                <td class="px-6 py-4 whitespace-nowrap">
                  <span class="text-sm text-neutral-500 font-medium">{{ order.items }} items</span>
                </td>
                <td class="px-6 py-4 whitespace-nowrap">
                  <span class="text-sm font-bold text-neutral-900 dark:text-white font-mono">${{ order.total }}</span>
                </td>
                <td class="px-6 py-4 whitespace-nowrap">
                  <span :class="[
                    'badge font-bold text-[10px] uppercase tracking-wider',
                    order.status === 'Completed' ? 'badge-success' :
                    order.status === 'Pending' ? 'badge-warning' : 'badge-neutral'
                  ]">
                    {{ order.status }}
                  </span>
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-right">
                  <span class="text-xs text-neutral-500 font-medium">{{ formatDate(order.time) }}</span>
                </td>
              </tr>
            </template>
          </tbody>
        </table>
      </div>
    </div>
  </NuxtLayout>
</template>

<script setup lang="ts">
import { h, ref, onMounted, computed } from 'vue'
const { get } = useApi()

definePageMeta({
  layout: false
})

// -- Interfaces --
interface DashboardStats {
    todayRevenue: number
    todayOrderCount: number
    averageOrderValue: number
    todayCustomerCount: number
    paymentBreakdown: { method: string, amount: number, count: number }[]
    topSellingItems: { menuItemId: number, name: string, quantitySold: number, revenue: number }[]
    recentOrders: { orderId: number, orderNo: string, total: number, status: string, customerName: string, createdAt: string }[]
    dailySales: { date: string, revenue: number, orderCount: number }[]
}

interface StatCard {
    label: string
    value: string
    trend: number
    icon: any
    iconBg: string
    iconColor: string
}

// -- State --
const loading = ref(true)
const recentOrders = ref<any[]>([])
const stats = ref<StatCard[]>([
  { label: "Today's Revenue", value: '$0.00', trend: 0, icon: 'DollarIcon', iconBg: 'bg-success-100', iconColor: 'text-success-600' },
  { label: 'Orders Today', value: '0', trend: 0, icon: 'OrderIcon', iconBg: 'bg-primary-100', iconColor: 'text-primary-600' },
  { label: 'Avg Order Value', value: '$0.00', trend: 0, icon: 'CustomerIcon', iconBg: 'bg-warning-100', iconColor: 'text-warning-600' },
  { label: 'Customers Today', value: '0', trend: 0, icon: 'ProductIcon', iconBg: 'bg-accent-100', iconColor: 'text-accent-600' }
])

const topProducts = ref<any[]>([])
const paymentBreakdown = ref<any[]>([])
const lowStockIngredients = ref<any[]>([])
const dailySales = ref<any[]>([])

// -- Fetch Logic --
const fetchLowStock = async () => {
    try {
        const data = await get<any[]>('/ingredients/low-stock')
        lowStockIngredients.value = data || []
    } catch(err) {
        console.error("Low stock fetch error", err)
    }
}

// -- Icons (Render Functions) --
const DollarIcon = () => h('svg', { xmlns: 'http://www.w3.org/2000/svg', viewBox: '0 0 24 24', fill: 'none', stroke: 'currentColor', 'stroke-width': '2', 'stroke-linecap': 'round', 'stroke-linejoin': 'round' }, [ h('line', { x1: '12', x2: '12', y1: '2', y2: '22' }), h('path', { d: 'M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6' }) ])
const OrderIcon = () => h('svg', { xmlns: 'http://www.w3.org/2000/svg', viewBox: '0 0 24 24', fill: 'none', stroke: 'currentColor', 'stroke-width': '2', 'stroke-linecap': 'round', 'stroke-linejoin': 'round' }, [ h('path', { d: 'M16 3h5v5' }), h('path', { d: 'M8 3H3v5' }), h('path', { d: 'M12 22v-8.3a4 4 0 0 0-1.172-2.872L3 3' }), h('path', { d: 'm15 9 6-6' }) ])
const CustomerIcon = () => h('svg', { xmlns: 'http://www.w3.org/2000/svg', viewBox: '0 0 24 24', fill: 'none', stroke: 'currentColor', 'stroke-width': '2', 'stroke-linecap': 'round', 'stroke-linejoin': 'round' }, [ h('path', { d: 'M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2' }), h('circle', { cx: '9', cy: '7', r: '4' }), h('path', { d: 'M22 21v-2a4 4 0 0 0-3-3.87' }), h('path', { d: 'M16 3.13a4 4 0 0 1 0 7.75' }) ])
const ProductIcon = () => h('svg', { xmlns: 'http://www.w3.org/2000/svg', viewBox: '0 0 24 24', fill: 'none', stroke: 'currentColor', 'stroke-width': '2', 'stroke-linecap': 'round', 'stroke-linejoin': 'round' }, [ h('path', { d: 'M17 8h1a4 4 0 1 1 0 8h-1' }), h('path', { d: 'M3 8h14v9a4 4 0 0 1-4 4H7a4 4 0 0 1-4-4Z' }) ])

const iconMap: any = { DollarIcon, OrderIcon, CustomerIcon, ProductIcon }

// -- Fetch Logic --
const fetchDashboardData = async () => {
    loading.value = true
    try {
        const response = await get<{ success: boolean, data: DashboardStats }>('/reports/dashboard')
        const data = response?.data
        
        if (data) {
            // Update Stats Cards
            stats.value = [
                { label: "Today's Revenue", value: '$' + (data.todayRevenue || 0).toFixed(2), trend: 12, icon: DollarIcon, iconBg: 'bg-success-100 dark:bg-success-900/30', iconColor: 'text-success-600 dark:text-success-400' },
                { label: 'Orders Today', value: (data.todayOrderCount || 0).toString(), trend: 8, icon: OrderIcon, iconBg: 'bg-primary-100 dark:bg-primary-900/30', iconColor: 'text-primary-600 dark:text-primary-400' },
                { label: 'Avg Order', value: '$' + (data.averageOrderValue || 0).toFixed(2), trend: -3, icon: CustomerIcon, iconBg: 'bg-warning-100 dark:bg-warning-900/30', iconColor: 'text-warning-600 dark:text-warning-400' },
                { label: 'Customers', value: (data.todayCustomerCount || 0).toString(), trend: 5, icon: ProductIcon, iconBg: 'bg-accent-100 dark:bg-accent-900/30', iconColor: 'text-accent-600 dark:text-accent-400' }
            ]
            
            // Top Products
            topProducts.value = (data.topSellingItems || []).map(item => ({
                name: item.name,
                initial: item.name?.charAt(0).toUpperCase() || '?',
                sold: item.quantitySold,
                revenue: item.revenue?.toFixed(2) || '0.00',
                category: item.categoryName || null
            }))
            
            // Payment Breakdown
            paymentBreakdown.value = data.paymentBreakdown || []
            
            // Recent Orders
            recentOrders.value = (data.recentOrders || []).map(o => ({
                id: o.orderNo || `#${o.orderId}`,
                customer: o.customerName || 'Guest',
                items: '-',
                total: (o.total || 0).toFixed(2),
                status: o.status === 'PAID' || o.status === 'COMPLETED' ? 'Completed' : o.status === 'PENDING' ? 'Pending' : o.status,
                time: o.createdAt || ''
            }))

            // Daily Sales Chart
            dailySales.value = data.dailySales || []
        }
    } catch(err) {
        console.error("Dashboard fetch error", err)
    } finally {
        loading.value = false
    }
}

const formatDate = (dateStr: string) => {
    if (!dateStr) return '-'
    try {
        const date = new Date(dateStr)
        return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
    } catch {
        return dateStr
    }
}

onMounted(() => {
    fetchDashboardData()
    fetchLowStock()
})
</script>
