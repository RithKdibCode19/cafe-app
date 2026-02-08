<template>
  <NuxtLayout name="admin">
    <div class="space-y-6">
      <UiBreadcrumb :items="[{ label: 'Orders' }]" />
      
      <!-- Header -->
      <div class="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
        <div>
          <h1 class="text-2xl font-bold text-neutral-900 dark:text-white">Order Management</h1>
          <p class="text-neutral-500 dark:text-neutral-400">View and manage all POS orders</p>
        </div>
        
        <div class="flex items-center gap-3">
          <div class="relative">
            <input 
              v-model="searchQuery"
              type="text" 
              placeholder="Search by order #..."
              class="w-64 px-4 py-2 pl-10 rounded-xl bg-white dark:bg-neutral-800 border border-neutral-200 dark:border-neutral-700 text-neutral-900 dark:text-white focus:outline-none focus:ring-2 focus:ring-primary-500"
            />
            <svg xmlns="http://www.w3.org/2000/svg" class="w-5 h-5 absolute left-3 top-1/2 -translate-y-1/2 text-neutral-400" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="11" cy="11" r="8"/><path d="m21 21-4.3-4.3"/></svg>
          </div>
          <button @click="fetchOrders" class="p-2 text-neutral-500 hover:text-primary-600 transition-colors">
            <svg xmlns="http://www.w3.org/2000/svg" class="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 12a9 9 0 0 0-9-9 9.75 9.75 0 0 0-6.74 2.74L3 8"/><path d="M3 3v5h5"/><path d="M3 12a9 9 0 0 0 9 9 9.75 9.75 0 0 0 6.74-2.74L21 16"/><path d="M16 16h5v5"/></svg>
          </button>
        </div>
      </div>

      <!-- Filters -->
      <div class="flex flex-wrap items-center gap-3 pb-2 overflow-x-auto">
        <button 
          v-for="status in ['ALL', 'PAID', 'PENDING', 'VOID', 'REFUND']" 
          :key="status"
          @click="selectedStatus = status"
          :class="[
            'px-4 py-2 rounded-xl text-sm font-medium transition-all',
            selectedStatus === status 
              ? 'bg-primary-600 text-white shadow-lg shadow-primary-900/20' 
              : 'bg-white dark:bg-neutral-800 text-neutral-600 dark:text-neutral-400 hover:bg-neutral-50 dark:hover:bg-neutral-700 border border-neutral-200 dark:border-neutral-700'
          ]"
        >
          {{ status }}
        </button>
      </div>

      <div class="card overflow-hidden">
        <div class="overflow-x-auto">
          <table class="w-full">
            <thead>
              <tr class="bg-neutral-50 dark:bg-neutral-800/50 border-b border-neutral-200 dark:border-neutral-700">
                 <th v-for="h in ['Order No', 'Date', 'Customer', 'Type', 'Total', 'Status', 'Actions']" :key="h" class="px-6 py-4 text-left text-xs font-semibold text-neutral-500 uppercase tracking-wider">
                   {{ h === 'Actions' ? '' : h }}
                 </th>
              </tr>
            </thead>
            <tbody class="divide-y divide-neutral-200 dark:divide-neutral-700">
              <template v-if="loading">
                <tr v-for="i in 5" :key="i" class="animate-pulse">
                   <td v-for="j in 7" :key="j" class="px-6 py-4"><div class="h-4 bg-neutral-100 dark:bg-neutral-800 rounded"></div></td>
                </tr>
              </template>
              <tr v-for="order in filteredOrders" :key="order.orderId" class="hover:bg-neutral-50 dark:hover:bg-neutral-800/30 transition-colors">
                <td class="px-6 py-4 whitespace-nowrap">
                  <span class="text-sm font-mono font-bold text-neutral-900 dark:text-white">{{ order.orderNo }}</span>
                </td>
                <td class="px-6 py-4 whitespace-nowrap">
                  <div class="text-sm text-neutral-600 dark:text-neutral-400">
                      {{ new Date(order.createdAt).toLocaleDateString() }}
                  </div>
                  <div class="text-[10px] text-neutral-500">
                      {{ new Date(order.createdAt).toLocaleTimeString() }}
                  </div>
                </td>
                <td class="px-6 py-4 whitespace-nowrap">
                   <div class="text-sm text-neutral-900 dark:text-white">{{ order.customer?.name || 'Walk-in' }}</div>
                   <div class="text-[10px] text-neutral-500">Cashier: {{ order.cashierUser?.userName || 'System' }}</div>
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-neutral-500">
                  {{ order.orderType }}
                </td>
                <td class="px-6 py-4 whitespace-nowrap">
                  <span class="text-sm font-bold text-neutral-900 dark:text-white">${{ order.totalAmount?.toFixed(2) }}</span>
                </td>
                <td class="px-6 py-4 whitespace-nowrap">
                  <span :class="getStatusClass(order.status)" class="text-[10px] px-2 py-1 rounded-full font-bold uppercase">
                    {{ order.status }}
                  </span>
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                  <div class="flex items-center justify-end gap-2">
                    <button @click="viewOrder(order)" class="p-2 text-primary-600 hover:bg-primary-50 dark:hover:bg-primary-900/20 rounded-lg transition-colors" title="View Details">
                      <svg xmlns="http://www.w3.org/2000/svg" class="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M2.062 12.348a1 1 0 0 1 0-.696 10.75 10.75 0 0 1 19.876 0 1 1 0 0 1 0 .696 10.75 10.75 0 0 1-19.876 0z"/><circle cx="12" cy="12" r="3"/></svg>
                    </button>
                    <button 
                      v-if="order.status === 'PENDING'"
                      @click="initiateAdjustment(order, 'VOID')" 
                      class="p-2 text-warning-600 hover:bg-warning-50 dark:hover:bg-warning-900/20 rounded-lg transition-colors" 
                      title="Void Order"
                    >
                      <svg xmlns="http://www.w3.org/2000/svg" class="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M18.36 6.64a9 9 0 1 1-12.73 0"/><line x1="12" x2="12" y1="2" y2="12"/></svg>
                    </button>
                    <button 
                      v-if="order.status === 'PAID'"
                      @click="initiateAdjustment(order, 'REFUND')" 
                      class="p-2 text-error-600 hover:bg-error-50 dark:hover:bg-error-900/20 rounded-lg transition-colors" 
                      title="Refund Order"
                    >
                      <svg xmlns="http://www.w3.org/2000/svg" class="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M11 15h2a2 2 0 1 0 0-4h-3c-1.1 0-2-.9-2-2s.9-2 2-2h2"/><path d="M12 5v14"/><path d="M3 12a9 9 0 0 1 15-6.7L21 8"/><path d="M21 3v5h-5"/></svg>
                    </button>
                  </div>
                </td>
              </tr>
              <tr v-if="filteredOrders.length === 0">
                <td colspan="7" class="px-6 py-12 text-center">
                  <div class="flex flex-col items-center justify-center text-neutral-400">
                    <svg xmlns="http://www.w3.org/2000/svg" class="w-12 h-12 mb-2 opacity-20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M6 2 3 6v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2V6l-3-4Z"/><path d="M3 6h18"/></svg>
                    <p>No orders matching your criteria</p>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <!-- Order Details Modal -->
      <div v-if="selectedOrder" class="fixed inset-0 z-50 flex items-center justify-center bg-black/80 backdrop-blur-sm p-4">
        <div class="bg-white dark:bg-neutral-800 rounded-2xl shadow-xl max-w-2xl w-full max-h-[90vh] overflow-hidden border border-neutral-200 dark:border-neutral-700 flex flex-col">
          <div class="p-4 border-b border-neutral-200 dark:border-neutral-700 flex items-center justify-between">
            <h3 class="text-lg font-bold text-neutral-900 dark:text-white">Order Details: {{ selectedOrder.orderNo }}</h3>
            <button @click="selectedOrder = null" class="text-neutral-400 hover:text-white">
              <svg xmlns="http://www.w3.org/2000/svg" class="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M18 6 6 18"/><path d="m6 6 12 12"/></svg>
            </button>
          </div>
          
          <div class="p-6 overflow-y-auto flex-1 space-y-6">
            <!-- Summary Info -->
            <div class="grid grid-cols-2 md:grid-cols-4 gap-4">
              <div>
                <p class="text-[10px] text-neutral-500 uppercase tracking-widest font-bold">Date</p>
                <p class="text-sm font-medium dark:text-white">{{ new Date(selectedOrder.createdAt).toLocaleString() }}</p>
              </div>
              <div>
                <p class="text-[10px] text-neutral-500 uppercase tracking-widest font-bold">Customer</p>
                <p class="text-sm font-medium dark:text-white">{{ selectedOrder.customer?.name || 'Walk-in' }}</p>
              </div>
              <div>
                <p class="text-[10px] text-neutral-500 uppercase tracking-widest font-bold">Cashier</p>
                <p class="text-sm font-medium dark:text-white">{{ selectedOrder.cashierUser?.userName }}</p>
              </div>
              <div>
                <p class="text-[10px] text-neutral-500 uppercase tracking-widest font-bold">Status</p>
                <span :class="getStatusClass(selectedOrder.status)" class="text-[10px] px-2 py-0.5 rounded-full font-bold uppercase">
                  {{ selectedOrder.status }}
                </span>
              </div>
            </div>

            <!-- Items -->
            <div class="space-y-3">
              <h4 class="text-xs font-bold text-neutral-900 dark:text-white border-b border-neutral-200 dark:border-neutral-700 pb-2 uppercase tracking-widest">Order Items</h4>
              <div v-for="item in selectedOrder.items" :key="item.orderItemId" class="flex justify-between items-center text-sm py-2 group">
                <div class="flex-1">
                  <div class="flex items-center gap-2">
                    <span class="w-6 h-6 rounded bg-neutral-100 dark:bg-neutral-800 flex items-center justify-center text-[10px] font-bold text-primary-600">{{ item.qty }}x</span>
                    <p class="font-medium dark:text-white">{{ item.menuItemName || item.menuItem?.name }} <span v-if="item.variantName || item.variant?.name" class="text-xs text-neutral-500">({{ item.variantName || item.variant?.name }})</span></p>
                  </div>
                  <div v-if="item.addOnItems?.length" class="pl-8 mt-1 space-x-1">
                      <span v-for="addon in item.addOnItems" :key="addon.id" class="text-[10px] px-1.5 py-0.5 bg-accent-50 text-accent-700 rounded-md">+ {{ addon.name }}</span>
                  </div>
                </div>
                <p class="font-bold dark:text-white">${{ ((item.unitPrice || 0) * item.qty).toFixed(2) }}</p>
              </div>
            </div>

            <!-- Totals -->
            <div class="space-y-1 pt-4 border-t border-neutral-200 dark:border-neutral-700">
              <div class="flex justify-between text-sm text-neutral-500">
                <span>Subtotal</span>
                <span>${{ selectedOrder.subTotal?.toFixed(2) }}</span>
              </div>
              <div v-if="selectedOrder.discountAmount > 0" class="flex justify-between text-sm text-error-500 font-medium">
                <span>Discount</span>
                <span>-${{ selectedOrder.discountAmount?.toFixed(2) }}</span>
              </div>
              <div v-if="selectedOrder.taxAmount > 0" class="flex justify-between text-sm text-neutral-500">
                <span>Tax</span>
                <span>${{ selectedOrder.taxAmount?.toFixed(2) }}</span>
              </div>
              <div class="flex justify-between text-lg font-bold text-neutral-900 dark:text-white pt-2">
                <span>Grand Total</span>
                <span class="text-primary-600">${{ selectedOrder.totalAmount?.toFixed(2) }}</span>
              </div>
            </div>

            <!-- Note -->
            <div v-if="selectedOrder.note" class="bg-neutral-50 dark:bg-neutral-900/50 p-4 rounded-xl border border-neutral-200 dark:border-neutral-700">
              <p class="text-[10px] text-neutral-500 uppercase font-black mb-1">Audit Log / Note</p>
              <p class="text-sm dark:text-neutral-300 italic leading-relaxed">{{ selectedOrder.note }}</p>
            </div>
          </div>
          
          <div class="p-4 border-t border-neutral-200 dark:border-neutral-700 flex justify-end gap-3 bg-neutral-50 dark:bg-neutral-900/50">
             <button @click="selectedOrder = null" class="btn-secondary">Close</button>
             <button 
              v-if="selectedOrder.status === 'PAID'"
              @click="initiateAdjustment(selectedOrder, 'REFUND')" 
              class="btn-primary bg-error-600 border-error-600 hover:bg-error-700 shadow-lg shadow-error-500/20"
             >
                Initiate Refund
             </button>
             <button 
              v-if="selectedOrder.status === 'PENDING'"
              @click="initiateAdjustment(selectedOrder, 'VOID')" 
              class="btn-primary bg-warning-600 border-warning-600 hover:bg-warning-700 shadow-lg shadow-warning-500/20"
             >
                Void Order
             </button>
          </div>
        </div>
      </div>

      <!-- Adjustment (Void/Refund) Confirmation Modal -->
      <div v-if="adjustmentTarget" class="fixed inset-0 z-[60] flex items-center justify-center bg-black/80 backdrop-blur-sm p-4">
        <div class="bg-white dark:bg-neutral-800 rounded-2xl shadow-2xl max-w-md w-full p-8 border border-neutral-200 dark:border-neutral-700 animate-in fade-in zoom-in duration-300">
          <div class="w-16 h-16 rounded-full bg-error-100 dark:bg-error-900/30 flex items-center justify-center mx-auto mb-6">
            <svg xmlns="http://www.w3.org/2000/svg" class="w-8 h-8 text-error-600" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 9v4"/><path d="M12 17h.01"/><path d="m12.8 2.8 8.1 14.2c.4.7.4 1.5 0 2.2-.4.7-1.1 1.1-1.9 1.1H5.1c-.8 0-1.5-.4-1.9-1.1-.4-.7-.4-1.5 0-2.2L11.2 2.8c.4-.7 1.2-.7 1.6 0Z"/></svg>
          </div>
          
          <h3 class="text-xl font-black text-neutral-900 dark:text-white mb-2 text-center uppercase tracking-tight">Confirm {{ adjustmentTarget.type }}</h3>
          <p class="text-neutral-500 mb-8 text-center leading-relaxed">Are you sure you want to {{ adjustmentTarget.type.toLowerCase() }} order <span class="font-mono font-bold text-neutral-900 dark:text-white px-1.5 py-0.5 bg-neutral-100 dark:bg-neutral-700 rounded">{{ adjustmentTarget.order.orderNo }}</span>? This will update the inventory and balance.</p>
          
          <div class="space-y-4">
            <div>
              <label class="text-xs font-black text-neutral-500 uppercase tracking-widest mb-2 block">Reason for adjustment</label>
              <textarea 
                v-model="adjustmentReason"
                rows="3"
                class="input w-full bg-neutral-50 dark:bg-neutral-900"
                placeholder="Required: why is this being adjusted?"
              ></textarea>
            </div>
          </div>

          <div class="grid grid-cols-2 gap-4 mt-10">
            <button @click="adjustmentTarget = null" class="btn-secondary py-3" :disabled="submittingAdjustment">
              Cancel
            </button>
            <button 
              @click="confirmAdjustment" 
              class="btn-primary py-3" 
              :class="adjustmentTarget.type === 'REFUND' ? 'bg-error-600 border-error-600 hover:bg-error-700 shadow-xl shadow-error-500/20' : 'bg-warning-600 border-warning-600 hover:bg-warning-700 shadow-xl shadow-warning-500/20'"
              :disabled="submittingAdjustment || !adjustmentReason.trim()"
            >
              {{ submittingAdjustment ? 'Processing...' : 'Proceed' }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </NuxtLayout>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'

definePageMeta({
  layout: false
})

const { get, post } = useApi()
const toast = useToast()

// -- State --
const loading = ref(true)
const orders = ref<any[]>([])
const searchQuery = ref('')
const selectedStatus = ref('ALL')
const selectedOrder = ref<any | null>(null)

// Adjustment State
const adjustmentTarget = ref<any | null>(null)
const adjustmentReason = ref('')
const submittingAdjustment = ref(false)

// -- Fetch Logic --
const fetchOrders = async () => {
  loading.value = true
  try {
    const data = await get<any[]>('/orders')
    orders.value = data || []
  } catch(err) {
    console.error("Failed to fetch orders", err)
  } finally {
    loading.value = false
  }
}

const filteredOrders = computed(() => {
  return orders.value.filter(o => {
    const q = searchQuery.value.toLowerCase()
    const matchesSearch = o.orderNo?.toLowerCase().includes(q) || 
                          (o.customer?.name || '').toLowerCase().includes(q)
    const matchesStatus = selectedStatus.value === 'ALL' || o.status === selectedStatus.value
    return matchesSearch && matchesStatus
  }).sort((a,b) => b.orderId - a.orderId)
})

// -- Actions --
const viewOrder = (order: any) => {
  selectedOrder.value = order
}

const initiateAdjustment = (order: any, type: 'VOID' | 'REFUND') => {
  adjustmentTarget.value = { order, type }
  adjustmentReason.value = ''
  selectedOrder.value = null
}

const confirmAdjustment = async () => {
  if (!adjustmentTarget.value || !adjustmentReason.value.trim()) return
  
  submittingAdjustment.value = true
  try {
    // We use the specialized PosOrderAdjustment API for high-audit tracking
    const payload = {
      orderId: adjustmentTarget.value.order.orderId,
      type: adjustmentTarget.value.type,
      amount: adjustmentTarget.value.order.totalAmount,
      reason: adjustmentReason.value,
      requestedBy: 1, // Default user ID for demonstration
      approvedBy: 1   // Admin auto-approval for demonstration
    }
    
    await post('/pos-order-adjustments', payload)
    
    await fetchOrders()
    
    adjustmentTarget.value = null
    toast.success(`Order ${payload.type} successfully processed`)
  } catch(err: any) {
    console.error("Adjustment failed", err)
    toast.error(err.response?.data?.message || "Failed to process adjustment")
  } finally {
    submittingAdjustment.value = false
  }
}

// -- Helpers --
const getStatusClass = (status: string) => {
  const classes: any = {
    'PAID': 'bg-success-100 text-success-700 dark:bg-success-900/30 dark:text-success-400',
    'PENDING': 'bg-warning-100 text-warning-700 dark:bg-warning-900/30 dark:text-warning-400',
    'VOID': 'bg-neutral-100 text-neutral-500 dark:bg-neutral-700 dark:text-neutral-400',
    'REFUND': 'bg-error-100 text-error-700 dark:bg-error-900/30 dark:text-error-400'
  }
  return classes[status] || 'bg-neutral-100 text-neutral-700'
}

onMounted(() => {
  fetchOrders()
})
</script>
