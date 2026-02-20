<template>
  <NuxtLayout name="admin">
    <div class="space-y-6">
      <UiBreadcrumb :items="[{ label: 'Customers' }]" />
      
      <!-- Header -->
      <div class="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
        <div>
          <h1 class="text-2xl font-bold text-neutral-900 dark:text-white">Customer Relationship Management</h1>
          <p class="text-neutral-500 dark:text-neutral-400">Manage customer profiles and purchase history</p>
        </div>
        
        <div class="flex items-center gap-3">
          <div class="relative">
            <input 
              v-model="searchQuery"
              type="text" 
              placeholder="Search by phone or name..."
              class="w-64 md:w-80 px-4 py-2.5 pl-10 rounded-xl bg-white dark:bg-neutral-800 border border-neutral-200 dark:border-neutral-700 text-neutral-900 dark:text-white focus:outline-none focus:ring-2 focus:ring-primary-500 transition-all font-medium"
              @keyup.enter="searchCustomer"
            />
            <svg xmlns="http://www.w3.org/2000/svg" class="w-5 h-5 absolute left-3 top-1/2 -translate-y-1/2 text-neutral-400" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="11" cy="11" r="8"/><path d="m21 21-4.3-4.3"/></svg>
          </div>
          <button @click="openCreateModal" class="btn-primary flex items-center gap-2">
            <svg xmlns="http://www.w3.org/2000/svg" class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><line x1="19" y1="8" x2="19" y2="14"/><line x1="16" y1="11" x2="22" y2="11"/></svg>
            Add Customer
          </button>
        </div>
      </div>

      <!-- Main Content Area -->
      <div class="grid lg:grid-cols-12 gap-6">
        
        <!-- Left Sidebar -->
        <div class="lg:col-span-4 space-y-4">
           <div class="bg-white dark:bg-neutral-900 rounded-3xl border border-neutral-200 dark:border-neutral-800 overflow-hidden shadow-sm">
              <div class="p-4 bg-neutral-50/50 dark:bg-neutral-800/50 border-b border-neutral-200 dark:border-neutral-800 flex justify-between items-center">
                 <h3 class="text-xs font-black text-neutral-500 uppercase tracking-widest">{{ searchActive ? 'Search Results' : 'All Customers' }}</h3>
              </div>
              <div class="divide-y divide-neutral-100 dark:divide-neutral-800 max-h-[600px] overflow-y-auto custom-scrollbar">
                 <template v-if="loading && customers.length === 0">
                    <div v-for="i in 5" :key="i" class="p-4 flex items-center gap-3 animate-pulse">
                        <div class="w-10 h-10 rounded-xl bg-neutral-100 dark:bg-neutral-800"></div>
                        <div class="flex-1 space-y-2">
                           <div class="h-3 bg-neutral-100 dark:bg-neutral-800 rounded w-24"></div>
                           <div class="h-2 bg-neutral-100 dark:bg-neutral-800 rounded w-32"></div>
                        </div>
                    </div>
                 </template>
                 <template v-else>
                    <div 
                      v-for="cust in displayCustomers" 
                      :key="cust.customerId"
                      @click="selectCustomer(cust)"
                      :class="[
                        'p-4 cursor-pointer transition-all hover:bg-neutral-50 dark:hover:bg-neutral-800/50',
                        selectedCustomerId === cust.customerId ? 'bg-primary-50 dark:bg-primary-900/10 border-l-4 border-l-primary-500' : ''
                      ]"
                    >
                       <div class="flex items-center gap-3">
                          <div class="w-10 h-10 rounded-xl bg-neutral-100 dark:bg-neutral-800 flex items-center justify-center text-sm font-black text-primary-600">
                             {{ cust.name?.charAt(0) }}
                          </div>
                          <div class="flex-1 overflow-hidden">
                             <p class="font-bold text-neutral-900 dark:text-white truncate">{{ cust.name }}</p>
                             <p class="text-xs text-neutral-500">{{ cust.phone }}</p>
                          </div>
                          <div class="text-right">
                             <p class="text-[10px] font-bold text-primary-600 bg-primary-50 dark:bg-primary-900/30 px-1.5 py-0.5 rounded">{{ cust.loyaltyPoints || 0 }} pt</p>
                          </div>
                       </div>
                    </div>
                    <div v-if="displayCustomers.length === 0" class="p-8 text-center text-neutral-400 italic text-sm">
                       No customers found.
                    </div>
                 </template>
              </div>
           </div>
        </div>

        <!-- Right Content -->
        <div class="lg:col-span-8">
           <div v-if="loading && selectedCustomerId" class="space-y-6">
              <div class="card h-32 animate-pulse bg-neutral-100 dark:bg-neutral-800 rounded-3xl"></div>
              <div class="grid grid-cols-3 gap-6">
                 <div v-for="i in 3" :key="i" class="card h-24 animate-pulse bg-neutral-100 dark:bg-neutral-800 rounded-3xl"></div>
              </div>
           </div>
           <template v-else-if="history">
              <div class="card p-6 border-l-4 border-l-primary-500 animate-in fade-in slide-in-from-right duration-300">
                <div class="flex flex-col md:flex-row md:items-center gap-6">
                  <div class="w-20 h-20 rounded-3xl bg-gradient-to-br from-primary-500 to-accent-500 flex items-center justify-center text-white text-3xl font-bold shadow-xl shadow-primary-500/20">
                    {{ history.customerName?.charAt(0)?.toUpperCase() }}
                  </div>
                  <div class="flex-1">
                    <div class="flex items-center gap-3">
                       <h2 class="text-2xl font-black text-neutral-900 dark:text-white">{{ history.customerName }}</h2>
                       <span :class="[
                         'px-2 py-0.5 rounded text-[10px] font-black uppercase tracking-widest',
                         history.membershipLevel === 'GOLD' ? 'bg-warning-100 text-warning-700 border border-warning-200' :
                         history.membershipLevel === 'SILVER' ? 'bg-neutral-100 text-neutral-700 border border-neutral-300' :
                         'bg-orange-100 text-orange-700 border border-orange-200'
                       ]">
                         {{ history.membershipLevel || 'BRONZE' }} MEMBER
                       </span>
                       <button @click="openEditModal(history)" class="p-1 px-2 rounded-lg bg-neutral-100 dark:bg-neutral-800 text-neutral-400 hover:text-primary-600 transition-colors text-[10px] font-bold uppercase tracking-widest">Edit Profile</button>
                    </div>
                    <div class="flex flex-wrap items-center gap-4 mt-2 text-sm text-neutral-500">
                      <span class="flex items-center gap-1">
                        <svg xmlns="http://www.w3.org/2000/svg" class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07 19.5 19.5 0 0 1-6-6 19.79 19.79 0 0 1-3.07-8.67A2 2 0 0 1 4.11 2h3a2 2 0 0 1 2 1.72 12.84 12.84 0 0 0 .7 2.81 2 2 0 0 1-.45 2.11L8.09 9.91a16 16 0 0 0 6 6l1.27-1.27a2 2 0 0 1 2.11-.45 12.84 12.84 0 0 0 2.81.7A2 2 0 0 1 22 16.92z"/></svg>
                        {{ history.phone }}
                      </span>
                      <span class="flex items-center gap-1">
                        <svg xmlns="http://www.w3.org/2000/svg" class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="4" width="18" height="18" rx="2" ry="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/></svg>
                        Since {{ formatDate(history.memberSince) }}
                      </span>
                    </div>
                  </div>
                  <div class="flex items-center gap-2 px-6 py-3 bg-primary-100 dark:bg-primary-900/30 rounded-2xl border border-primary-200 dark:border-primary-800">
                    <svg xmlns="http://www.w3.org/2000/svg" class="w-5 h-5 text-primary-600" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/></svg>
                    <span class="text-primary-700 dark:text-primary-400 font-black text-xl">{{ history.loyaltyPoints }} <span class="text-xs uppercase ml-1">pts</span></span>
                  </div>
                </div>
              </div>

              <!-- Stats Cards -->
              <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
                <div class="card p-6 bg-white dark:bg-neutral-900 border border-neutral-200 dark:border-neutral-800 shadow-sm transition-transform hover:-translate-y-1">
                  <p class="text-xs font-black text-neutral-400 uppercase tracking-widest mb-1">Total Orders</p>
                  <p class="text-4xl font-black text-primary-600">{{ history.totalOrders }}</p>
                </div>
                <div class="card p-6 bg-white dark:bg-neutral-900 border border-neutral-200 dark:border-neutral-800 shadow-sm transition-transform hover:-translate-y-1">
                  <p class="text-xs font-black text-neutral-400 uppercase tracking-widest mb-1">Total Lifetime Spend</p>
                  <p class="text-4xl font-black text-success-600">${{ history.totalSpent?.toFixed(2) }}</p>
                </div>
                <div class="card p-6 bg-white dark:bg-neutral-900 border border-neutral-200 dark:border-neutral-800 shadow-sm transition-transform hover:-translate-y-1">
                  <p class="text-xs font-black text-neutral-400 uppercase tracking-widest mb-1">Average Ticket</p>
                  <p class="text-4xl font-black text-accent-600">${{ (history.totalSpent / (history.totalOrders || 1)).toFixed(2) }}</p>
                </div>
              </div>

              <!-- Content Grid -->
              <div class="grid lg:grid-cols-3 gap-6">
                <!-- Favorite Items -->
                <div class="card p-6 bg-neutral-50/50 dark:bg-neutral-800/30 border-none">
                  <h3 class="text-sm font-black text-neutral-500 uppercase tracking-widest mb-6 flex items-center gap-2">
                    <svg xmlns="http://www.w3.org/2000/svg" class="w-4 h-4 text-red-500" viewBox="0 0 24 24" fill="currentColor"><path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"/></svg>
                    Top Favorites
                  </h3>
                  <div class="space-y-3">
                    <div v-for="item in history.favoriteItems" :key="item.menuItemId" class="flex items-center justify-between p-4 bg-white dark:bg-neutral-900 rounded-2xl border border-neutral-100 dark:border-neutral-800 shadow-sm">
                      <div class="overflow-hidden">
                        <p class="font-bold text-neutral-900 dark:text-white truncate">{{ item.name }}</p>
                        <p class="text-[10px] font-bold text-neutral-400 uppercase tracking-widest mt-0.5">Ordered {{ item.orderCount }}x</p>
                      </div>
                      <span class="text-sm font-black text-primary-600">${{ item.totalSpent?.toFixed(2) }}</span>
                    </div>
                    <div v-if="!history.favoriteItems?.length" class="text-center py-12 text-neutral-400 italic text-xs">
                      No purchase history available.
                    </div>
                  </div>
                </div>

                <!-- Recent Orders -->
                <div class="lg:col-span-2 card p-0 overflow-hidden">
                   <div class="p-6 border-b border-neutral-100 dark:border-neutral-800 flex items-center justify-between">
                      <h3 class="text-sm font-black text-neutral-500 uppercase tracking-widest">Order History</h3>
                      <span class="text-[10px] font-bold text-neutral-400 uppercase tracking-widest">{{ history.recentOrders?.length || 0 }} items</span>
                   </div>
                   <div class="divide-y divide-neutral-100 dark:divide-neutral-800 max-h-[400px] overflow-y-auto custom-scrollbar">
                      <div 
                        v-for="order in history.recentOrders" 
                        :key="order.orderId" 
                        class="p-4 hover:bg-neutral-50 dark:hover:bg-neutral-800/50 transition-colors cursor-pointer"
                        @click="toggleOrderDetails(order.orderId)"
                      >
                        <div class="flex items-center justify-between">
                          <div class="flex items-center gap-4">
                            <div class="w-12 h-12 rounded-2xl bg-primary-100 dark:bg-primary-900/30 flex items-center justify-center text-primary-600">
                               <svg xmlns="http://www.w3.org/2000/svg" class="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M6 2 3 6v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2V6l-3-4Z"/><path d="M3 6h18"/><path d="M16 10a4 4 0 0 1-8 0"/></svg>
                            </div>
                            <div>
                              <p class="font-black text-neutral-900 dark:text-white">{{ order.orderNo }}</p>
                              <p class="text-[10px] text-neutral-500 font-bold uppercase tracking-wider">{{ order.date }} at {{ order.time }}</p>
                            </div>
                          </div>
                          <div class="text-right">
                            <p class="text-lg font-black text-neutral-900 dark:text-white">${{ order.total?.toFixed(2) }}</p>
                            <span :class="getStatusClass(order.status)" class="text-[10px] font-black px-2 py-0.5 rounded-full uppercase tracking-widest">{{ order.status }}</span>
                          </div>
                        </div>
                        
                        <!-- Expanded Details -->
                        <div v-if="expandedOrderId === order.orderId" class="mt-6 pt-6 border-t border-neutral-200 dark:border-neutral-700 animate-in fade-in slide-in-from-top-2 duration-300">
                          <div class="flex items-center gap-4 text-[10px] font-black text-neutral-400 uppercase tracking-widest mb-4">
                            <span>{{ order.orderType }}</span>
                            <span v-if="order.paymentMethod" class="flex items-center gap-1"><div class="w-1 h-1 rounded-full bg-neutral-300"></div> {{ order.paymentMethod }}</span>
                          </div>
                          <div class="space-y-3 bg-neutral-50 dark:bg-neutral-800/50 p-4 rounded-2xl">
                            <div v-for="(item, idx) in order.items" :key="idx" class="flex justify-between items-center text-sm">
                              <span class="text-neutral-700 dark:text-neutral-300 font-medium">
                                <span class="w-6 h-6 inline-flex items-center justify-center bg-white dark:bg-neutral-800 rounded mr-2 text-[10px] font-black border border-neutral-200 dark:border-neutral-700">{{ item.qty }}</span>
                                {{ item.name }}
                              </span>
                              <span class="font-bold text-neutral-500">${{ (item.price * item.qty).toFixed(2) }}</span>
                            </div>
                          </div>
                        </div>
                      </div>
                      
                      <div v-if="!history.recentOrders?.length" class="text-center py-20 bg-neutral-50/20">
                        <svg xmlns="http://www.w3.org/2000/svg" class="w-12 h-12 mx-auto mb-4 text-neutral-200" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 12A9 9 0 1 1 12 3a9 9 0 0 1 9 9Z"/><path d="M12 8v4l3 3"/></svg>
                        <p class="text-sm text-neutral-400 font-medium">No order data available.</p>
                      </div>
                   </div>
                </div>
              </div>
           </template>

           <!-- Empty State: Choose a Customer -->
           <div v-else class="h-full flex flex-col items-center justify-center p-12 bg-neutral-50 dark:bg-neutral-800/30 rounded-[40px] text-center border-2 border-dashed border-neutral-200 dark:border-neutral-800">
              <div class="w-24 h-24 rounded-full bg-white dark:bg-neutral-800 flex items-center justify-center shadow-2xl shadow-primary-500/10 mb-8">
                 <svg xmlns="http://www.w3.org/2000/svg" class="w-12 h-12 text-neutral-300 dark:text-neutral-600" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M19 21v-2a4 4 0 0 0-4-4H9a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
              </div>
              <h3 class="text-2xl font-black text-neutral-900 dark:text-white mb-3">Select a Customer</h3>
              <p class="text-neutral-500 max-w-sm">Browse the list on the left or search to view detailed profiles, purchase habits, and loyalty history.</p>
           </div>
        </div>
      </div>

      <!-- Customer Modal (Add/Edit) -->
      <div v-if="showModal" class="fixed inset-0 z-[100] flex items-center justify-center bg-black/60 backdrop-blur-sm p-4">
         <div class="bg-white dark:bg-neutral-900 rounded-[32px] shadow-2xl w-full max-w-md overflow-hidden border border-neutral-200 dark:border-neutral-800 animate-in fade-in zoom-in duration-300">
            <div class="p-8 border-b border-neutral-100 dark:border-neutral-800 flex justify-between items-center bg-neutral-50/50 dark:bg-neutral-800/30">
               <h3 class="text-xl font-black text-neutral-900 dark:text-white uppercase tracking-tight">{{ editingCustomer ? 'Update CRM Profile' : 'New Customer' }}</h3>
               <button @click="closeModal" class="text-neutral-400 hover:text-neutral-600 transition-colors">
                  <svg xmlns="http://www.w3.org/2000/svg" class="w-6 h-6" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
               </button>
            </div>
            
            <form @submit.prevent="saveCustomer" class="p-8 space-y-6">
               <div class="space-y-4">
                  <div>
                    <label class="text-[10px] font-black text-neutral-400 uppercase tracking-widest ml-1 mb-2 block">Full Name</label>
                    <input v-model="form.name" type="text" required class="input-modern w-full" placeholder="Customer Name" />
                  </div>
                  <div>
                    <label class="text-[10px] font-black text-neutral-400 uppercase tracking-widest ml-1 mb-2 block">Phone Number</label>
                    <input v-model="form.phone" type="text" required class="input-modern w-full" placeholder="+855 ..." />
                  </div>
                  <div>
                    <label class="text-[10px] font-black text-neutral-400 uppercase tracking-widest ml-1 mb-2 block">Email (Optional)</label>
                    <input v-model="form.email" type="email" class="input-modern w-full" placeholder="email@example.com" />
                  </div>
               </div>

               <div class="flex gap-4 pt-4">
                  <button type="button" @click="closeModal" class="flex-1 py-4 text-sm font-bold text-neutral-500 hover:text-neutral-800 dark:hover:text-white transition-colors">Cancel</button>
                  <button type="submit" class="flex-1 btn-primary py-4 rounded-2xl font-black uppercase tracking-widest shadow-xl shadow-primary-500/20" :disabled="saving">
                     {{ editingCustomer ? 'Save Profile' : 'Register Member' }}
                  </button>
               </div>
            </form>
         </div>
      </div>

    </div>
  </NuxtLayout>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'

definePageMeta({
  layout: false
})

const { get, post, put } = useApi()
const toast = useToast()

// Interfaces
interface Customer {
  customerId: number
  name: string
  phone: string
  email?: string
  loyaltyPoints: number
  createdAt: string
}

interface CustomerHistory {
  customerId: number
  customerName: string
  phone: string
  totalOrders: number
  totalSpent: number
  loyaltyPoints: number
  memberSince: string
  membershipLevel: string
  recentOrders: any[]
  favoriteItems: any[]
}

// State
const loading = ref(true)
const saving = ref(false)
const searchQuery = ref('')
const searchActive = ref(false)
const customers = ref<Customer[]>([])
const searchResults = ref<Customer[]>([])
const history = ref<CustomerHistory | null>(null)
const selectedCustomerId = ref<number | null>(null)
const expandedOrderId = ref<number | null>(null)

// Modal State
const showModal = ref(false)
const editingCustomer = ref<any>(null)
const form = reactive({
  name: '',
  phone: '',
  email: ''
})

// Computed
const displayCustomers = computed(() => searchActive.value ? searchResults.value : customers.value)

// Actions
const fetchAllCustomers = async () => {
  loading.value = true
  try {
    const data = await get<Customer[]>('/customers')
    customers.value = data || []
  } catch (err) {
    console.error('Failed to fetch customers', err)
  } finally {
    loading.value = false
  }
}

const selectCustomer = async (cust: Customer) => {
  selectedCustomerId.value = cust.customerId
  loading.value = true
  try {
    const response = await get<{ data: CustomerHistory }>(`/customers/${cust.customerId}/history`)
    if (response?.data) {
      history.value = response.data
    }
  } catch (err) {
    console.error('Failed to fetch history', err)
    history.value = null
  } finally {
    loading.value = false
  }
}

const searchCustomer = async () => {
  if (!searchQuery.value.trim()) {
    clearSearch()
    return
  }
  
  loading.value = true
  searchActive.value = true
  try {
    const data = await get<Customer[]>(`/customers/search?query=${encodeURIComponent(searchQuery.value)}`)
    searchResults.value = data || []
    if (searchResults.value.length > 0) {
       selectCustomer(searchResults.value[0])
    } else {
       history.value = null
       selectedCustomerId.value = null
    }
  } catch (error) {
    console.error('Search failed:', error)
  } finally {
    loading.value = false
  }
}

const clearSearch = () => {
  searchQuery.value = ''
  searchActive.value = false
  searchResults.value = []
}

const openCreateModal = () => {
  editingCustomer.value = null
  form.name = ''
  form.phone = ''
  form.email = ''
  showModal.value = true
}

const openEditModal = (cust: any) => {
  editingCustomer.value = cust
  form.name = cust.customerName || cust.name
  form.phone = cust.phone
  form.email = cust.email || ''
  showModal.value = true
}

const closeModal = () => {
  showModal.value = false
}

const saveCustomer = async () => {
  saving.value = true
  try {
    if (editingCustomer.value) {
      await put(`/customers/${editingCustomer.value.customerId}`, form)
    } else {
      await post('/customers/add', form)
    }
    await fetchAllCustomers()
    if (editingCustomer.value) toast.success('Customer profile updated')
    else toast.success('New customer registered')
  } catch (err: any) {
    toast.error(err.data?.message || 'Failed to save customer')
  } finally {
    saving.value = false
  }
}

const toggleOrderDetails = (orderId: number) => {
  expandedOrderId.value = expandedOrderId.value === orderId ? null : orderId
}

// Helpers
const formatDate = (dateStr: string) => {
  if (!dateStr) return 'N/A'
  return new Date(dateStr).toLocaleDateString(undefined, { year: 'numeric', month: 'short', day: 'numeric' })
}

const getStatusClass = (status: string) => {
  const classes: Record<string, string> = {
    PAID: 'bg-success-100 text-success-700 dark:bg-success-900/30 dark:text-success-400',
    PENDING: 'bg-warning-100 text-warning-700 dark:bg-warning-900/30 dark:text-warning-400',
    CANCELLED: 'bg-error-100 text-error-700 dark:bg-error-900/30 dark:text-error-400'
  }
  return classes[status] || 'bg-neutral-100 text-neutral-700'
}

onMounted(() => {
  fetchAllCustomers()
})
</script>

<style scoped>
.btn-primary {
  @apply bg-primary-600 hover:bg-primary-700 text-white rounded-xl px-6 py-3 transition-all active:scale-95 disabled:opacity-50 font-bold text-sm shadow-lg shadow-primary-500/20;
}
.input-modern {
  @apply bg-neutral-50 dark:bg-neutral-800 border-none rounded-2xl px-5 py-4 text-sm ring-1 ring-neutral-200 dark:ring-neutral-700 focus:ring-2 focus:ring-primary-500 placeholder-neutral-400 transition-all;
}
.custom-scrollbar::-webkit-scrollbar {
  width: 4px;
}
.custom-scrollbar::-webkit-scrollbar-track {
  @apply bg-transparent;
}
.custom-scrollbar::-webkit-scrollbar-thumb {
  @apply bg-neutral-200 dark:bg-neutral-800 rounded-full;
}
</style>
