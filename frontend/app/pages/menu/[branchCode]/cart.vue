<template>
  <div class="min-h-screen bg-neutral-50 dark:bg-neutral-950">
    <!-- Header -->
    <header class="sticky top-0 z-40 bg-white/80 dark:bg-neutral-900/80 backdrop-blur-xl border-b border-neutral-200/50 dark:border-neutral-800/50">
      <div class="max-w-lg mx-auto px-4 py-3">
        <div class="flex items-center gap-3">
          <NuxtLink
            :to="`/menu/${branchCode}${tableNo ? '?table=' + tableNo : ''}`"
            class="p-2 -ml-2 rounded-xl hover:bg-neutral-100 dark:hover:bg-neutral-800 transition-colors"
          >
            <svg class="w-5 h-5 text-neutral-600 dark:text-neutral-400" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7"/></svg>
          </NuxtLink>
          <div>
            <h1 class="text-lg font-bold text-neutral-900 dark:text-white">Your Order</h1>
            <p v-if="tableNo" class="text-xs text-primary-600 dark:text-primary-400 font-medium">Table {{ tableNo }}</p>
          </div>
        </div>
      </div>
    </header>

    <main class="max-w-lg mx-auto px-4 pt-4 pb-36">
      <!-- Empty cart -->
      <div v-if="items.length === 0" class="py-16 text-center">
        <div class="text-6xl mb-4">🛒</div>
        <h2 class="text-xl font-bold text-neutral-900 dark:text-white mb-2">Your cart is empty</h2>
        <p class="text-neutral-500 dark:text-neutral-400 mb-6">Browse the menu to add items</p>
        <NuxtLink
          :to="`/menu/${branchCode}${tableNo ? '?table=' + tableNo : ''}`"
          class="btn-primary"
        >
          Back to Menu
        </NuxtLink>
      </div>

      <!-- Cart items -->
      <div v-else class="space-y-3">
        <div
          v-for="(item, index) in items"
          :key="index"
          class="bg-white dark:bg-neutral-900 rounded-2xl p-4 border border-neutral-200/50 dark:border-neutral-800/50"
        >
          <div class="flex gap-3">
            <!-- Image -->
            <div class="w-16 h-16 rounded-xl overflow-hidden bg-neutral-100 dark:bg-neutral-800 flex-shrink-0">
              <img v-if="item.imageUrl" :src="item.imageUrl" :alt="item.name" class="w-full h-full object-cover" />
              <div v-else class="w-full h-full flex items-center justify-center text-2xl">🍽️</div>
            </div>

            <!-- Info -->
            <div class="flex-1 min-w-0">
              <div class="flex items-start justify-between">
                <div>
                  <h3 class="font-semibold text-neutral-900 dark:text-white text-sm">{{ item.name }}</h3>
                  <p v-if="item.variantSize" class="text-xs text-neutral-500 dark:text-neutral-400">Size: {{ item.variantSize }}</p>
                  <p v-if="item.addOns && item.addOns.length" class="text-xs text-primary-500 dark:text-primary-400 mt-0.5">
                    + {{ item.addOns.map(a => a.name).join(', ') }}
                  </p>
                  <p v-if="item.note" class="text-xs text-neutral-400 italic mt-0.5">{{ item.note }}</p>
                </div>
                <button @click="removeItem(index)" class="p-1.5 text-neutral-400 hover:text-error-500 transition-colors">
                  <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"/></svg>
                </button>
              </div>

              <div class="flex items-center justify-between mt-2">
                <div class="flex items-center bg-neutral-100 dark:bg-neutral-800 rounded-lg">
                  <button @click="updateQty(index, -1)" class="p-1.5 text-neutral-600 dark:text-neutral-400 hover:text-neutral-900 dark:hover:text-white transition-colors">
                    <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20 12H4"/></svg>
                  </button>
                  <span class="w-6 text-center text-sm font-bold text-neutral-900 dark:text-white">{{ item.qty }}</span>
                  <button @click="updateQty(index, 1)" class="p-1.5 text-neutral-600 dark:text-neutral-400 hover:text-neutral-900 dark:hover:text-white transition-colors">
                    <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4"/></svg>
                  </button>
                </div>
                <span class="font-bold text-sm text-neutral-900 dark:text-white">
                  ${{ ((item.variantPrice || item.price + item.addOnTotal) * item.qty).toFixed(2) }}
                </span>
              </div>
            </div>
          </div>
        </div>

        <!-- Order note -->
        <div class="bg-white dark:bg-neutral-900 rounded-2xl p-4 border border-neutral-200/50 dark:border-neutral-800/50 mt-4">
          <label class="text-sm font-semibold text-neutral-700 dark:text-neutral-300 mb-2 block">Order Notes</label>
          <textarea
            v-model="orderNote"
            placeholder="Any special requests for the whole order..."
            rows="2"
            class="w-full px-3 py-2 rounded-xl bg-neutral-100 dark:bg-neutral-800 border-0 text-sm text-neutral-900 dark:text-white placeholder-neutral-400 focus:outline-none focus:ring-2 focus:ring-primary-500 resize-none"
          />
        </div>

        <!-- Customer info (optional) -->
        <div class="bg-white dark:bg-neutral-900 rounded-2xl p-4 border border-neutral-200/50 dark:border-neutral-800/50">
          <label class="text-sm font-semibold text-neutral-700 dark:text-neutral-300 mb-2 block">Your Info (Optional)</label>
          <input
            v-model="customerName"
            type="text"
            placeholder="Your name"
            class="w-full px-3 py-2 mb-2 rounded-xl bg-neutral-100 dark:bg-neutral-800 border-0 text-sm text-neutral-900 dark:text-white placeholder-neutral-400 focus:outline-none focus:ring-2 focus:ring-primary-500"
          />
          <input
            v-model="customerPhone"
            type="tel"
            placeholder="Phone number"
            class="w-full px-3 py-2 rounded-xl bg-neutral-100 dark:bg-neutral-800 border-0 text-sm text-neutral-900 dark:text-white placeholder-neutral-400 focus:outline-none focus:ring-2 focus:ring-primary-500"
          />
        </div>

        <!-- Order summary -->
        <div class="bg-white dark:bg-neutral-900 rounded-2xl p-4 border border-neutral-200/50 dark:border-neutral-800/50">
          <div class="flex justify-between text-sm text-neutral-600 dark:text-neutral-400 mb-1">
            <span>Subtotal</span>
            <span>${{ subtotal.toFixed(2) }}</span>
          </div>
          <div class="flex justify-between text-base font-bold text-neutral-900 dark:text-white pt-2 border-t border-neutral-200/50 dark:border-neutral-800/50 mt-2">
            <span>Total</span>
            <span>${{ total.toFixed(2) }}</span>
          </div>
        </div>
      </div>
    </main>

    <!-- Place order button -->
    <div v-if="items.length > 0" class="fixed bottom-0 left-0 right-0 z-50 p-4 max-w-lg mx-auto bg-gradient-to-t from-neutral-50 dark:from-neutral-950 via-neutral-50/80 dark:via-neutral-950/80 to-transparent pt-8">
      <button
        @click="placeOrder"
        :disabled="submitting"
        class="flex items-center justify-center w-full bg-primary-600 hover:bg-primary-700 disabled:opacity-50 disabled:cursor-not-allowed text-white rounded-2xl px-6 py-4 shadow-2xl shadow-primary-500/30 transition-all hover:shadow-primary-500/50 font-semibold text-lg"
      >
        <svg v-if="submitting" class="animate-spin -ml-1 mr-3 h-5 w-5 text-white" fill="none" viewBox="0 0 24 24">
          <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" />
          <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z" />
        </svg>
        {{ submitting ? 'Placing Order...' : `Place Order — $${total.toFixed(2)}` }}
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
definePageMeta({ layout: 'menu' })

const route = useRoute()
const router = useRouter()
const branchCode = route.params.branchCode as string
const { items, tableNo, subtotal, total, removeItem, updateQty, clearCart } = useMenuCart()
const { post } = usePublicApi()

const orderNote = ref('')
const customerName = ref('')
const customerPhone = ref('')
const submitting = ref(false)

const placeOrder = async () => {
  if (items.value.length === 0 || submitting.value) return

  try {
    submitting.value = true

    const orderItems = items.value.map(item => ({
      menuItemId: item.menuItemId,
      variantId: item.variantId || null,
      qty: item.qty,
      addonIds: item.addOns?.map(a => a.addOnId) || [],
      note: item.note || null
    }))

    const request = {
      branchCode,
      tableNo: tableNo.value || null,
      orderType: tableNo.value ? 'DINE_IN' : 'TAKEAWAY',
      note: orderNote.value || null,
      customerName: customerName.value || null,
      customerPhone: customerPhone.value || null,
      items: orderItems
    }

    const response = await post<any>('/api/public/orders', request)

    if (response?.orderNo) {
      clearCart()
      router.push(`/menu/${branchCode}/order/${response.orderNo}`)
    }
  } catch (e: any) {
    alert(e?.data?.error || e?.message || 'Failed to place order. Please try again.')
  } finally {
    submitting.value = false
  }
}

useHead({ title: 'Your Order' })
</script>
