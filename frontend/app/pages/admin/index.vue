<template>
  <NuxtLayout name="admin">
      <UiBreadcrumb :items="[{ label: 'Dashboard' }]" class="mb-6" />
      
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
      <!-- Loading State -->
      <template v-if="loading">
        <div v-for="i in 4" :key="i" class="card p-6 animate-pulse h-32 bg-neutral-100 dark:bg-neutral-800 rounded-3xl"></div>
      </template>
      <!-- Data State -->
      <template v-else>
        <div v-for="stat in stats" :key="stat.label" class="card p-6 hover:shadow-md transition-shadow">
          <div class="flex items-start justify-between">
            <div>
              <p class="text-sm text-neutral-500 dark:text-neutral-400 mb-1">{{ stat.label }}</p>
              <p class="text-2xl font-bold text-neutral-900 dark:text-white">{{ stat.value }}</p>
              <p :class="[
                'text-sm mt-1 flex items-center gap-1',
                stat.trend > 0 ? 'text-success-600' : 'text-error-600'
              ]">
                <svg v-if="stat.trend > 0" xmlns="http://www.w3.org/2000/svg" class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <path d="m18 15-6-6-6 6" />
                </svg>
                <svg v-else xmlns="http://www.w3.org/2000/svg" class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <path d="m6 9 6 6 6-6" />
                </svg>
                {{ Math.abs(stat.trend) }}% from yesterday
              </p>
            </div>
            <div :class="['p-3 rounded-xl', stat.iconBg]">
              <component :is="stat.icon" :class="['w-6 h-6', stat.iconColor]" />
            </div>
          </div>
        </div>
      </template>
    </div>

    <!-- Charts Row -->
    <div class="grid lg:grid-cols-3 gap-6 mb-8">
      <!-- Sales Chart -->
      <div class="lg:col-span-2 card p-6">
        <div class="flex items-center justify-between mb-6">
          <h3 class="text-lg font-semibold text-neutral-900 dark:text-white">Sales Overview</h3>
          <select class="input w-36 text-sm py-1.5 h-auto">
            <option>Last 7 Days</option>
            <option>Last 30 Days</option>
          </select>
        </div>
        
        <!-- Interactive Chart -->
        <div v-if="loading" class="h-64 skeleton bg-neutral-100 dark:bg-neutral-800 rounded-3xl animate-pulse"></div>
        <div v-else class="h-64">
           <AdminDashboardChart :data="dailySales" />
        </div>
      </div>

      <!-- Top Products & Low Stock Alerts -->
      <div class="space-y-6">
        <!-- Top Products -->
        <div class="card p-6">
          <h3 class="text-lg font-semibold text-neutral-900 dark:text-white mb-4">Top Products</h3>
          <div class="space-y-4">
            <template v-if="loading">
              <div v-for="i in 3" :key="i" class="flex items-center gap-3 animate-pulse">
                <div class="w-10 h-10 rounded-2xl bg-neutral-100 dark:bg-neutral-800"></div>
                <div class="flex-1 space-y-2">
                  <div class="h-3 w-24 bg-neutral-100 dark:bg-neutral-800 rounded-full"></div>
                  <div class="h-2 w-12 bg-neutral-100 dark:bg-neutral-800 rounded-full"></div>
                </div>
                <div class="h-4 w-12 bg-neutral-100 dark:bg-neutral-800 rounded-full"></div>
              </div>
            </template>
            <template v-else>
              <div v-for="product in topProducts" :key="product.name" class="flex items-center gap-3">
                <div class="w-10 h-10 rounded-lg bg-primary-100 dark:bg-primary-900/30 flex items-center justify-center">
                  <span class="text-sm font-semibold text-primary-600 dark:text-primary-400">{{ product.initial }}</span>
                </div>
                <div class="flex-1 min-w-0">
                  <p class="text-sm font-medium text-neutral-900 dark:text-white truncate">{{ product.name }}</p>
                  <p class="text-xs text-neutral-500">{{ product.sold }} sold</p>
                </div>
                <span class="text-sm font-semibold text-neutral-900 dark:text-white font-mono">${{ product.revenue }}</span>
              </div>
              <div v-if="topProducts.length === 0" class="text-center py-4 text-neutral-400 text-sm italic">
                  No products sold today
              </div>
            </template>
          </div>
        </div>

        <!-- Low Stock Alerts -->
        <div class="card p-6 border-l-4 border-l-error-500">
          <div class="flex items-center justify-between mb-4">
            <h3 class="text-lg font-semibold text-neutral-900 dark:text-white">Low Stock</h3>
            <span class="px-2 py-0.5 rounded-full bg-error-100 text-error-700 text-[10px] font-bold uppercase">{{ lowStockIngredients.length }} Items</span>
          </div>
          <div class="space-y-4">
            <div v-for="item in lowStockIngredients.slice(0, 5)" :key="item.ingredientId" class="flex items-center justify-between">
              <div class="min-w-0">
                <p class="text-sm font-medium text-neutral-900 dark:text-white truncate">{{ item.name }}</p>
                <p class="text-[10px] text-error-600 font-bold">Stock: {{ item.currentStock }} {{ item.unit }}</p>
              </div>
              <NuxtLink to="/admin/inventory" class="p-1.5 text-neutral-400 hover:text-primary-600 transition-colors">
                <svg xmlns="http://www.w3.org/2000/svg" class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M5 12h14"/><path d="M12 5v14"/></svg>
              </NuxtLink>
            </div>
            <div v-if="lowStockIngredients.length === 0" class="text-center py-4 text-success-600 text-sm">
                <svg xmlns="http://www.w3.org/2000/svg" class="w-8 h-8 mx-auto mb-2 opacity-20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/><polyline points="22 4 12 14.01 9 11.01"/></svg>
                <p class="font-medium text-xs">All items in stock!</p>
            </div>
            <NuxtLink v-if="lowStockIngredients.length > 5" to="/admin/inventory" class="block text-center text-xs text-primary-600 hover:underline pt-2">
                View all {{ lowStockIngredients.length }} alerts
            </NuxtLink>
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
              <tr v-for="i in 5" :key="i" class="animate-pulse">
                <td v-for="j in 6" :key="j" class="px-6 py-4">
                  <div class="h-4 bg-neutral-100 dark:bg-neutral-800 rounded-full w-full"></div>
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
                revenue: item.revenue?.toFixed(2) || '0.00'
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
