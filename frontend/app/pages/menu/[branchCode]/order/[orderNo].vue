<template>
  <div class="min-h-screen bg-neutral-50 dark:bg-neutral-950">
    <!-- Header -->
    <header class="bg-white/80 dark:bg-neutral-900/80 backdrop-blur-xl border-b border-neutral-200/50 dark:border-neutral-800/50">
      <div class="max-w-lg mx-auto px-4 py-4">
        <div class="text-center">
          <div class="w-16 h-16 mx-auto mb-3 rounded-2xl bg-success-100 dark:bg-success-900/30 flex items-center justify-center">
            <svg class="w-8 h-8 text-success-600 dark:text-success-400" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"/></svg>
          </div>
          <h1 class="text-xl font-bold text-neutral-900 dark:text-white">Order Placed!</h1>
        </div>
      </div>
    </header>

    <main class="max-w-lg mx-auto px-4 pt-6 pb-24">
      <!-- Loading -->
      <div v-if="loading && !orderData" class="py-8 text-center">
        <div class="animate-spin w-8 h-8 border-2 border-primary-600 border-t-transparent rounded-full mx-auto mb-3" />
        <p class="text-neutral-500 dark:text-neutral-400">Loading order...</p>
      </div>

      <!-- Error -->
      <div v-else-if="error" class="py-16 text-center">
        <div class="text-6xl mb-4">😵</div>
        <h2 class="text-xl font-bold text-neutral-900 dark:text-white mb-2">Order not found</h2>
        <p class="text-neutral-500 dark:text-neutral-400">This order number may be invalid.</p>
      </div>

      <!-- Order details -->
      <div v-else-if="orderData" class="space-y-4">
        <!-- Order number -->
        <div class="bg-white dark:bg-neutral-900 rounded-2xl p-5 border border-neutral-200/50 dark:border-neutral-800/50 text-center">
          <p class="text-sm text-neutral-500 dark:text-neutral-400 mb-1">Order Number</p>
          <p class="text-2xl font-mono font-bold text-neutral-900 dark:text-white tracking-wide">{{ orderData.orderNo }}</p>
          <p v-if="orderData.tableNo" class="text-sm text-primary-600 dark:text-primary-400 font-medium mt-2">
            📍 Table {{ orderData.tableNo }}
          </p>
        </div>

        <!-- Status -->
        <div class="bg-white dark:bg-neutral-900 rounded-2xl p-5 border border-neutral-200/50 dark:border-neutral-800/50">
          <p class="text-sm text-neutral-500 dark:text-neutral-400 mb-3">Status</p>
          <div class="flex items-center gap-6 justify-center">
            <div v-for="(step, i) in statusSteps" :key="step.key" class="flex flex-col items-center relative">
              <!-- Connector line -->
              <div
                v-if="i > 0"
                class="absolute -left-8 top-4 w-10 h-0.5"
                :class="isStepReached(step.key) ? 'bg-primary-500' : 'bg-neutral-200 dark:bg-neutral-700'"
              />
              <!-- Circle -->
              <div
                class="relative z-10 w-8 h-8 rounded-full flex items-center justify-center text-sm font-bold transition-all"
                :class="currentStatus === step.key
                  ? 'bg-primary-600 text-white ring-4 ring-primary-200 dark:ring-primary-800 animate-pulse'
                  : isStepReached(step.key)
                    ? 'bg-primary-600 text-white'
                    : 'bg-neutral-200 dark:bg-neutral-700 text-neutral-400 dark:text-neutral-500'"
              >
                <svg v-if="isStepReached(step.key) && currentStatus !== step.key" class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"/></svg>
                <span v-else>{{ step.icon }}</span>
              </div>
              <!-- Label -->
              <span
                class="text-xs mt-1.5 font-medium"
                :class="isStepReached(step.key) ? 'text-primary-600 dark:text-primary-400' : 'text-neutral-400 dark:text-neutral-500'"
              >{{ step.label }}</span>
            </div>
          </div>

          <!-- Status message -->
          <div class="text-center mt-4 p-3 rounded-xl bg-neutral-50 dark:bg-neutral-800">
            <p class="text-sm font-medium" :class="statusColor">{{ statusMessage }}</p>
          </div>
        </div>

        <!-- Order items -->
        <div class="bg-white dark:bg-neutral-900 rounded-2xl p-5 border border-neutral-200/50 dark:border-neutral-800/50">
          <p class="text-sm font-semibold text-neutral-700 dark:text-neutral-300 mb-3">Items</p>
          <div class="space-y-2">
            <div v-for="item in orderData.items" :key="item.orderItemId" class="flex justify-between items-center py-2 border-b border-neutral-100 dark:border-neutral-800 last:border-0">
              <div>
                <p class="text-sm font-medium text-neutral-900 dark:text-white">
                  {{ item.qty }}× {{ item.menuItemName }}
                </p>
                <p v-if="item.variantSize" class="text-xs text-neutral-500">Size: {{ item.variantSize }}</p>
                <p v-if="item.addonNames && item.addonNames.length" class="text-xs text-primary-500">
                  + {{ item.addonNames.join(', ') }}
                </p>
              </div>
              <span class="text-sm font-semibold text-neutral-900 dark:text-white">
                ${{ (item.unitPrice * item.qty).toFixed(2) }}
              </span>
            </div>
          </div>

          <div class="flex justify-between items-center pt-3 mt-3 border-t border-neutral-200 dark:border-neutral-800">
            <span class="font-bold text-neutral-900 dark:text-white">Total</span>
            <span class="font-bold text-lg text-primary-600 dark:text-primary-400">${{ orderData.totalAmount?.toFixed(2) }}</span>
          </div>
        </div>

        <!-- Info banner -->
        <div class="bg-primary-50 dark:bg-primary-900/20 rounded-2xl p-4 border border-primary-200/50 dark:border-primary-800/50">
          <div class="flex gap-3 items-start">
            <span class="text-2xl flex-shrink-0">💡</span>
            <div>
              <p class="text-sm font-medium text-primary-800 dark:text-primary-300">
                Please pay at the counter
              </p>
              <p class="text-xs text-primary-600/70 dark:text-primary-400/70 mt-0.5">
                Show your order number to the cashier when ready.
              </p>
            </div>
          </div>
        </div>

        <!-- Order more button -->
        <NuxtLink
          :to="`/menu/${branchCode}${tableNo ? '?table=' + tableNo : ''}`"
          class="block text-center py-3 px-6 rounded-2xl border-2 border-primary-200 dark:border-primary-800 text-primary-600 dark:text-primary-400 font-semibold hover:bg-primary-50 dark:hover:bg-primary-900/30 transition-all"
        >
          Order More
        </NuxtLink>
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
definePageMeta({ layout: 'menu' })

const route = useRoute()
const branchCode = route.params.branchCode as string
const orderNo = route.params.orderNo as string
const { get } = usePublicApi()
const { tableNo: cartTableNo } = useMenuCart()

const loading = ref(true)
const error = ref(false)
const orderData = ref<any>(null)
const currentStatus = ref('PENDING')
const tableNo = ref(cartTableNo.value || '')

const statusSteps = [
  { key: 'PENDING', label: 'Placed', icon: '📝' },
  { key: 'PREPARING', label: 'Preparing', icon: '🔥' },
  { key: 'READY', label: 'Ready', icon: '✅' }
]

const statusOrder = ['PENDING', 'PAID', 'PREPARING', 'READY', 'COMPLETED']

const isStepReached = (stepKey: string) => {
  const currentIdx = statusOrder.indexOf(currentStatus.value)
  const stepIdx = statusOrder.indexOf(stepKey)
  return stepIdx <= currentIdx
}

const statusMessage = computed(() => {
  switch (currentStatus.value) {
    case 'PENDING': return 'Your order has been received'
    case 'PAID': return 'Payment confirmed, preparing soon'
    case 'PREPARING': return 'Your order is being prepared'
    case 'READY': return 'Your order is ready for pickup!'
    case 'COMPLETED': return 'Order completed. Thank you!'
    case 'VOID': return 'This order has been cancelled'
    default: return 'Processing...'
  }
})

const statusColor = computed(() => {
  switch (currentStatus.value) {
    case 'READY': return 'text-success-600 dark:text-success-400'
    case 'PREPARING': return 'text-warning-600 dark:text-warning-400'
    case 'VOID': return 'text-error-600 dark:text-error-400'
    default: return 'text-primary-600 dark:text-primary-400'
  }
})

// Fetch order status
const fetchOrder = async () => {
  try {
    loading.value = true
    const data = await get<any>(`/public/orders/${orderNo}`)
    if (data) {
      orderData.value = data
      currentStatus.value = data.status
      if (data.tableNo) tableNo.value = data.tableNo
    } else {
      error.value = true
    }
  } catch (e) {
    console.error('Failed to load order:', e)
    error.value = true
  } finally {
    loading.value = false
  }
}

// Polling for status updates
let pollInterval: ReturnType<typeof setInterval> | null = null

const pollStatus = async () => {
  try {
    const data = await get<any>(`/public/orders/${orderNo}/status`)
    if (data) {
      currentStatus.value = data.status
      // Stop polling if order is complete or void
      if (['COMPLETED', 'VOID', 'READY'].includes(data.status) && pollInterval) {
        clearInterval(pollInterval)
        pollInterval = null
      }
    }
  } catch (e) {
    // Silently ignore polling errors
  }
}

onMounted(async () => {
  await fetchOrder()
  // Start polling every 10 seconds
  pollInterval = setInterval(pollStatus, 10000)
})

onUnmounted(() => {
  if (pollInterval) clearInterval(pollInterval)
})

useHead({ title: `Order ${orderNo}` })
</script>
