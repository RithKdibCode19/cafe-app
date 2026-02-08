<template>
  <NuxtLayout name="kitchen">
    <div class="grid grid-cols-1 lg:grid-cols-3 gap-6 h-full">
      
      <!-- New Orders (Pending) -->
      <div class="flex flex-col h-full bg-neutral-800/50 rounded-3xl border border-neutral-800 overflow-hidden">
        <div class="p-4 bg-neutral-800 border-b border-neutral-700 flex justify-between items-center sticky top-0 z-10">
          <div class="flex items-center gap-3">
             <div class="w-3 h-3 rounded-full bg-primary-500 animate-pulse"></div>
             <h2 class="uppercase tracking-widest font-black text-sm text-neutral-300">New Orders</h2>
          </div>
          <span class="bg-neutral-900 text-neutral-400 px-2 py-1 rounded text-xs font-bold">{{ pendingOrders.length }}</span>
        </div>
        
        <div class="p-4 space-y-4 overflow-y-auto custom-scrollbar flex-1">
           <template v-if="loading">
             <div v-for="i in 3" :key="i" class="bg-neutral-900 rounded-2xl p-4 h-48 animate-pulse"></div>
           </template>
           <template v-else>
             <div v-for="order in pendingOrders" :key="order.orderId" class="bg-neutral-900 rounded-2xl p-5 border border-neutral-800 shadow-lg group hover:border-primary-500/50 transition-colors">
               <div class="flex justify-between items-start mb-4">
                 <div>
                   <h3 class="font-black text-2xl text-white">#{{ order.orderNo?.slice(-4) || order.orderId }}</h3>
                   <span class="text-[10px] font-bold text-neutral-500 uppercase tracking-widest">{{ order.orderType }} • {{ formatTime(order.createdAt) }}</span>
                 </div>
                 <div class="text-right">
                    <div :class="['text-xs font-bold px-2 py-1 rounded-lg mb-1', getTimerColor(order.createdAt)]">
                      {{ getDuration(order.createdAt) }}
                    </div>
                 </div>
               </div>
               
               <div class="space-y-3 mb-6">
                 <div v-for="(item, idx) in order.items" :key="idx" class="flex justify-between items-start text-sm">
                    <div class="flex gap-3">
                      <span class="font-black text-primary-500 w-5">{{ item.qty }}x</span>
                      <div>
                        <p class="text-neutral-300 font-bold leading-tight">{{ item.name }}</p>
                        <p v-if="item.notes" class="text-xs text-orange-400 mt-1 italic">Note: {{ item.notes }}</p>
                        <p v-if="item.variants" class="text-xs text-neutral-500">{{ item.variants }}</p>
                      </div>
                    </div>
                 </div>
               </div>
               
               <button @click="updateStatus(order, 'PREPARING')" class="w-full py-4 bg-neutral-800 hover:bg-primary-600 text-neutral-400 hover:text-white font-black uppercase tracking-widest rounded-xl transition-all active:scale-95 text-xs">
                 Start Preparing
               </button>
             </div>
             <div v-if="pendingOrders.length === 0" class="text-center py-12 opacity-30">
                <svg xmlns="http://www.w3.org/2000/svg" class="w-16 h-16 mx-auto mb-4 text-neutral-600" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><path d="M12 6v6l4 2"/></svg>
                <p class="font-bold uppercase tracking-widest text-sm">No Pending Orders</p>
             </div>
           </template>
        </div>
      </div>

      <!-- In Progress (Preparing) -->
      <div class="flex flex-col h-full bg-neutral-800/50 rounded-3xl border border-neutral-800 overflow-hidden">
        <div class="p-4 bg-neutral-800 border-b border-neutral-700 flex justify-between items-center sticky top-0 z-10">
          <div class="flex items-center gap-3">
             <div class="w-3 h-3 rounded-full bg-orange-500 animate-pulse"></div>
             <h2 class="uppercase tracking-widest font-black text-sm text-neutral-300">Preparing</h2>
          </div>
          <span class="bg-neutral-900 text-neutral-400 px-2 py-1 rounded text-xs font-bold">{{ preparingOrders.length }}</span>
        </div>
        
        <div class="p-4 space-y-4 overflow-y-auto custom-scrollbar flex-1">
             <div v-for="order in preparingOrders" :key="order.orderId" class="bg-neutral-900 rounded-2xl p-5 border-l-4 border-orange-500 shadow-lg relative overflow-hidden">
               <div class="absolute top-0 right-0 p-4 opacity-10">
                  <svg xmlns="http://www.w3.org/2000/svg" class="w-24 h-24 text-orange-500" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M6 13.87A4 4 0 0 1 7.41 6a5.11 5.11 0 0 1 10.59 0A4.09 4.09 0 0 1 24 10a4 4 0 0 1-4 4"/></svg>
               </div>
               
               <div class="flex justify-between items-start mb-4 relative z-10">
                 <div>
                   <h3 class="font-black text-2xl text-white">#{{ order.orderNo?.slice(-4) || order.orderId }}</h3>
                   <span class="text-[10px] font-bold text-neutral-500 uppercase tracking-widest">{{ order.orderType }}</span>
                 </div>
                 <div :class="['text-xs font-bold px-2 py-1 rounded-lg', getTimerColor(order.createdAt)]">
                      {{ getDuration(order.createdAt) }}
                 </div>
               </div>
               
               <div class="space-y-3 mb-6 relative z-10">
                 <div v-for="(item, idx) in order.items" :key="idx" class="flex justify-between items-start text-sm">
                    <div class="flex gap-3">
                      <span class="font-black text-orange-500 w-5">{{ item.qty }}x</span>
                      <div>
                        <p class="text-white font-bold leading-tight">{{ item.name }}</p>
                         <p v-if="item.notes" class="text-xs text-orange-400 mt-1 italic">Note: {{ item.notes }}</p>
                      </div>
                    </div>
                 </div>
               </div>
               
               <button @click="updateStatus(order, 'READY')" class="w-full py-4 bg-success-600 hover:bg-success-500 text-white font-black uppercase tracking-widest rounded-xl transition-all active:scale-95 text-xs shadow-lg shadow-success-900/20 relative z-10">
                 Mark Ready
               </button>
             </div>
             
             <div v-if="preparingOrders.length === 0" class="text-center py-12 opacity-30">
                <p class="font-bold uppercase tracking-widest text-sm">Station Clear</p>
             </div>
        </div>
      </div>

      <!-- Ready -->
      <div class="flex flex-col h-full bg-neutral-800/50 rounded-3xl border border-neutral-800 overflow-hidden">
        <div class="p-4 bg-neutral-800 border-b border-neutral-700 flex justify-between items-center sticky top-0 z-10">
          <div class="flex items-center gap-3">
             <div class="w-3 h-3 rounded-full bg-success-500"></div>
             <h2 class="uppercase tracking-widest font-black text-sm text-neutral-300">Ready for Pickup</h2>
          </div>
          <span class="bg-neutral-900 text-neutral-400 px-2 py-1 rounded text-xs font-bold">{{ readyOrders.length }}</span>
        </div>
        
        <div class="p-4 space-y-4 overflow-y-auto custom-scrollbar flex-1">
             <div v-for="order in readyOrders" :key="order.orderId" class="bg-neutral-900/50 rounded-2xl p-5 border border-success-900/30 opacity-75 hover:opacity-100 transition-opacity">
               <div class="flex justify-between items-center mb-2">
                 <h3 class="font-black text-xl text-success-500">#{{ order.orderNo?.slice(-4) || order.orderId }}</h3>
                 <span class="text-xs font-bold text-white bg-success-900/50 px-2 py-1 rounded">READY</span>
               </div>
               <p class="text-xs text-neutral-400 mb-4">{{ order.items.length }} Items • {{ formatTime(order.updatedAt || order.createdAt) }}</p>
               
               <button @click="updateStatus(order, 'COMPLETED')" class="w-full py-3 bg-neutral-800 hover:bg-neutral-700 text-neutral-400 hover:text-white font-bold uppercase tracking-widest rounded-xl transition-all text-[10px]">
                 Complete / Picked Up
               </button>
             </div>

             <div v-if="readyOrders.length === 0" class="text-center py-12 opacity-30">
                <p class="font-bold uppercase tracking-widest text-sm">No Completed Orders</p>
             </div>
        </div>
      </div>

    </div>
  </NuxtLayout>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'

definePageMeta({
  layout: false // Using custom layout in template
})

const { get, put } = useApi()
const toast = useToast()

const loading = ref(true)
const pendingOrders = ref<any[]>([])
const preparingOrders = ref<any[]>([])
const readyOrders = ref<any[]>([])
let refreshInterval: NodeJS.Timer

const fetchOrders = async () => {
    try {
        // Fetch by status - parallel requests
        const [pendingRes, preparingRes, readyRes] = await Promise.all([
            get<any[]>('/orders/status/PENDING'),
            get<any[]>('/orders/status/PREPARING'),
            get<any[]>('/orders/status/READY')
        ])
        
        pendingOrders.value = pendingRes || []
        preparingOrders.value = preparingRes || []
        readyOrders.value = readyRes || []
    } catch (err) {
        console.error("Failed to fetch kitchen orders", err)
    } finally {
        loading.value = false
    }
}

const updateStatus = async (order: any, status: string) => {
    try {
        await put(`/orders/${order.orderId}/status?status=${status}`)
        // Optimistic update
        if (status === 'PREPARING') {
            pendingOrders.value = pendingOrders.value.filter(o => o.orderId !== order.orderId)
            preparingOrders.value.unshift({ ...order, status: 'PREPARING', updatedAt: new Date().toISOString() })
        } else if (status === 'READY') {
             preparingOrders.value = preparingOrders.value.filter(o => o.orderId !== order.orderId)
             readyOrders.value.unshift({ ...order, status: 'READY', updated: new Date().toISOString() })
        } else if (status === 'COMPLETED') {
             readyOrders.value = readyOrders.value.filter(o => o.orderId !== order.orderId)
        }
        toast.success(`Order #${order.orderNo?.slice(-4) || order.orderId} updated`)
        // Refresh to be safe
        fetchOrders()
    } catch (err) {
        toast.error('Failed to update order status')
    }
}

// -- Helpers --
const formatTime = (iso: string) => new Date(iso).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })

const getDuration = (iso: string) => {
    const start = new Date(iso).getTime()
    const now = new Date().getTime() // This needs to be reactive ideally, but for now it renders once per refresh
    const diffMins = Math.floor((now - start) / 60000)
    return diffMins + ' min'
}

const getTimerColor = (iso: string) => {
    const start = new Date(iso).getTime()
    const now = new Date().getTime()
    const diffMins = (now - start) / 60000
    if (diffMins > 20) return 'bg-error-500/20 text-error-500'
    if (diffMins > 10) return 'bg-warning-500/20 text-warning-500'
    return 'bg-success-500/20 text-success-500'
}

onMounted(() => {
    fetchOrders()
    refreshInterval = setInterval(() => {
        fetchOrders()
        // Force update for timers (ugly hack but works for simple view)
        pendingOrders.value = [...pendingOrders.value]
        preparingOrders.value = [...preparingOrders.value]
    }, 15000) // Poll every 15s
})

onUnmounted(() => {
    clearInterval(refreshInterval)
})
</script>

<style scoped>
.custom-scrollbar::-webkit-scrollbar {
  width: 6px;
}
.custom-scrollbar::-webkit-scrollbar-track {
  @apply bg-neutral-900;
}
.custom-scrollbar::-webkit-scrollbar-thumb {
  @apply bg-neutral-700 rounded-full;
}
</style>
