<template>
  <div class="min-h-screen bg-neutral-50 dark:bg-neutral-950">
    <!-- Header -->
    <header class="sticky top-0 z-40 bg-white/80 dark:bg-neutral-900/80 backdrop-blur-xl border-b border-neutral-200/50 dark:border-neutral-800/50">
      <div class="max-w-lg mx-auto px-4 py-3">
        <div class="flex items-center justify-between">
          <div>
            <h1 class="text-lg font-bold text-neutral-900 dark:text-white">{{ branchInfo?.name || 'Menu' }}</h1>
            <p v-if="tableNo" class="text-xs text-primary-600 dark:text-primary-400 font-medium">
              📍 Table {{ tableNo }}
            </p>
          </div>
          <button
            v-if="searchMode"
            @click="searchMode = false; searchQuery = ''"
            class="p-2 rounded-xl hover:bg-neutral-100 dark:hover:bg-neutral-800 transition-colors"
          >
            <svg class="w-5 h-5 text-neutral-600 dark:text-neutral-400" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"/></svg>
          </button>
          <button
            v-else
            @click="searchMode = true"
            class="p-2 rounded-xl hover:bg-neutral-100 dark:hover:bg-neutral-800 transition-colors"
          >
            <svg class="w-5 h-5 text-neutral-600 dark:text-neutral-400" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"/></svg>
          </button>
        </div>

        <!-- Search bar -->
        <div v-if="searchMode" class="mt-3">
          <input
            v-model="searchQuery"
            type="text"
            placeholder="Search menu..."
            class="w-full px-4 py-2.5 rounded-xl bg-neutral-100 dark:bg-neutral-800 border-0 text-neutral-900 dark:text-white placeholder-neutral-400 focus:outline-none focus:ring-2 focus:ring-primary-500 transition-all"
            autofocus
          />
        </div>
      </div>

      <!-- Category tabs -->
      <div v-if="!searchMode && categories.length > 0" class="max-w-lg mx-auto">
        <div class="flex overflow-x-auto scrollbar-hide px-4 gap-2 pb-3">
          <button
            v-for="cat in categories"
            :key="cat.categoryId"
            @click="activeCategory = cat.categoryId; scrollToCategory(cat.categoryId)"
            class="flex-shrink-0 px-4 py-1.5 rounded-full text-sm font-medium transition-all whitespace-nowrap"
            :class="activeCategory === cat.categoryId
              ? 'bg-primary-600 text-white shadow-md shadow-primary-500/25'
              : 'bg-neutral-100 dark:bg-neutral-800 text-neutral-600 dark:text-neutral-400 hover:bg-neutral-200 dark:hover:bg-neutral-700'"
          >
            {{ cat.name }}
          </button>
        </div>
      </div>
    </header>

    <!-- Loading state -->
    <div v-if="loading" class="max-w-lg mx-auto px-4 py-8">
      <div class="space-y-4">
        <div v-for="i in 6" :key="i" class="flex gap-3 animate-pulse">
          <div class="w-24 h-24 rounded-2xl bg-neutral-200 dark:bg-neutral-800 flex-shrink-0" />
          <div class="flex-1 space-y-2 py-2">
            <div class="h-4 bg-neutral-200 dark:bg-neutral-800 rounded w-3/4" />
            <div class="h-3 bg-neutral-200 dark:bg-neutral-800 rounded w-1/2" />
            <div class="h-4 bg-neutral-200 dark:bg-neutral-800 rounded w-1/4 mt-2" />
          </div>
        </div>
      </div>
    </div>

    <!-- Error state -->
    <div v-else-if="error" class="max-w-lg mx-auto px-4 py-16 text-center">
      <div class="text-6xl mb-4">😵</div>
      <h2 class="text-xl font-bold text-neutral-900 dark:text-white mb-2">Menu not found</h2>
      <p class="text-neutral-500 dark:text-neutral-400">This menu link may be invalid or the branch is currently unavailable.</p>
    </div>

    <!-- Menu content -->
    <main v-else class="max-w-lg mx-auto px-4 pt-4 pb-28">
      <!-- Search results -->
      <div v-if="searchMode && searchQuery">
        <p class="text-sm text-neutral-500 dark:text-neutral-400 mb-3">
          {{ filteredItems.length }} result{{ filteredItems.length !== 1 ? 's' : '' }}
        </p>
        <div class="space-y-3">
          <MenuItemCard
            v-for="item in filteredItems"
            :key="item.menuItemId"
            :item="item"
            @add="openItemDetail(item)"
          />
        </div>
      </div>

      <!-- Category sections -->
      <div v-else>
        <div
          v-for="cat in categories"
          :key="cat.categoryId"
          :id="`cat-${cat.categoryId}`"
          class="mb-6"
        >
          <h2 class="text-lg font-bold text-neutral-900 dark:text-white mb-3 sticky top-[108px] bg-neutral-50/90 dark:bg-neutral-950/90 backdrop-blur-sm py-2 z-10">
            {{ cat.name }}
          </h2>
          <div class="space-y-3">
            <MenuItemCard
              v-for="item in cat.items"
              :key="item.menuItemId"
              :item="item"
              @add="openItemDetail(item)"
            />
          </div>
        </div>
      </div>
    </main>

    <!-- Floating cart button -->
    <div v-if="itemCount > 0" class="fixed bottom-0 left-0 right-0 z-50 p-4 max-w-lg mx-auto">
      <NuxtLink
        :to="`/menu/${branchCode}/cart`"
        class="flex items-center justify-between w-full bg-primary-600 hover:bg-primary-700 text-white rounded-2xl px-6 py-4 shadow-2xl shadow-primary-500/30 transition-all hover:shadow-primary-500/50 hover:-translate-y-0.5 active:translate-y-0"
      >
        <div class="flex items-center gap-3">
          <div class="bg-primary-500 rounded-xl px-2.5 py-1 text-sm font-bold">{{ itemCount }}</div>
          <span class="font-semibold">View Cart</span>
        </div>
        <span class="font-bold text-lg">${{ total.toFixed(2) }}</span>
      </NuxtLink>
    </div>

    <!-- Item detail modal -->
    <Teleport to="body">
      <Transition name="modal-overlay">
        <div
          v-if="selectedItem"
          class="fixed inset-0 bg-black/50 z-[60] flex items-end sm:items-center justify-center"
          @click.self="selectedItem = null"
        >
          <Transition name="modal">
            <div
              v-if="selectedItem"
              class="bg-white dark:bg-neutral-900 rounded-t-3xl sm:rounded-3xl w-full max-w-lg max-h-[85vh] overflow-y-auto"
            >
              <!-- Item image -->
              <div v-if="selectedItem.imageUrl" class="relative h-56 rounded-t-3xl overflow-hidden">
                <img :src="selectedItem.imageUrl" :alt="selectedItem.name" class="w-full h-full object-cover" />
                <button
                  @click="selectedItem = null"
                  class="absolute top-3 right-3 bg-black/40 backdrop-blur-sm text-white p-2 rounded-full hover:bg-black/60 transition-colors"
                >
                  <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"/></svg>
                </button>
              </div>
              <div v-else class="flex justify-end p-4 pb-0">
                <button
                  @click="selectedItem = null"
                  class="p-2 rounded-full hover:bg-neutral-100 dark:hover:bg-neutral-800 transition-colors"
                >
                  <svg class="w-5 h-5 text-neutral-600 dark:text-neutral-400" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"/></svg>
                </button>
              </div>

              <div class="p-6">
                <h3 class="text-xl font-bold text-neutral-900 dark:text-white">{{ selectedItem.name }}</h3>
                <p v-if="selectedItem.description" class="text-sm text-neutral-500 dark:text-neutral-400 mt-1">
                  {{ selectedItem.description }}
                </p>
                <p class="text-lg font-bold text-primary-600 dark:text-primary-400 mt-2">
                  ${{ selectedVariantPrice.toFixed(2) }}
                </p>

                <!-- Variant selection -->
                <div v-if="selectedItem.variants && selectedItem.variants.length > 0" class="mt-5">
                  <p class="text-sm font-semibold text-neutral-700 dark:text-neutral-300 mb-2">Size</p>
                  <div class="flex gap-2">
                    <button
                      v-for="variant in selectedItem.variants"
                      :key="variant.variantId"
                      @click="selectedVariant = variant"
                      class="flex-1 py-2.5 px-3 rounded-xl text-sm font-medium border-2 transition-all"
                      :class="selectedVariant?.variantId === variant.variantId
                        ? 'border-primary-500 bg-primary-50 dark:bg-primary-900/30 text-primary-700 dark:text-primary-300'
                        : 'border-neutral-200 dark:border-neutral-700 text-neutral-600 dark:text-neutral-400 hover:border-neutral-300'"
                    >
                      <span class="font-bold">{{ variant.size }}</span>
                      <span class="block text-xs mt-0.5">${{ variant.price.toFixed(2) }}</span>
                    </button>
                  </div>
                </div>

                <!-- Add-ons -->
                <div v-if="availableAddOns.length > 0" class="mt-5">
                  <p class="text-sm font-semibold text-neutral-700 dark:text-neutral-300 mb-2">Add-ons</p>
                  <div class="space-y-2">
                    <label
                      v-for="addon in availableAddOns"
                      :key="addon.addOnId"
                      class="flex items-center justify-between p-3 rounded-xl border-2 transition-all cursor-pointer"
                      :class="selectedAddOns.includes(addon.addOnId)
                        ? 'border-primary-500 bg-primary-50 dark:bg-primary-900/30'
                        : 'border-neutral-200 dark:border-neutral-700 hover:border-neutral-300'"
                    >
                      <div class="flex items-center gap-3">
                        <input
                          type="checkbox"
                          :checked="selectedAddOns.includes(addon.addOnId)"
                          @change="toggleAddOn(addon.addOnId)"
                          class="w-4 h-4 rounded border-neutral-300 text-primary-600 focus:ring-primary-500"
                        />
                        <span class="text-sm font-medium text-neutral-700 dark:text-neutral-300">{{ addon.name }}</span>
                      </div>
                      <span class="text-sm font-semibold text-neutral-500 dark:text-neutral-400">+${{ addon.price.toFixed(2) }}</span>
                    </label>
                  </div>
                </div>

                <!-- Note -->
                <div class="mt-5">
                  <p class="text-sm font-semibold text-neutral-700 dark:text-neutral-300 mb-2">Special Instructions</p>
                  <textarea
                    v-model="itemNote"
                    placeholder="e.g. Less sugar, no ice..."
                    rows="2"
                    class="w-full px-4 py-2.5 rounded-xl bg-neutral-100 dark:bg-neutral-800 border-0 text-sm text-neutral-900 dark:text-white placeholder-neutral-400 focus:outline-none focus:ring-2 focus:ring-primary-500 resize-none transition-all"
                  />
                </div>

                <!-- Quantity & Add button -->
                <div class="flex items-center gap-4 mt-6">
                  <div class="flex items-center bg-neutral-100 dark:bg-neutral-800 rounded-xl">
                    <button @click="modalQty = Math.max(1, modalQty - 1)" class="p-3 text-neutral-600 dark:text-neutral-400 hover:text-neutral-900 dark:hover:text-white transition-colors">
                      <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20 12H4"/></svg>
                    </button>
                    <span class="w-8 text-center font-bold text-neutral-900 dark:text-white">{{ modalQty }}</span>
                    <button @click="modalQty++" class="p-3 text-neutral-600 dark:text-neutral-400 hover:text-neutral-900 dark:hover:text-white transition-colors">
                      <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4"/></svg>
                    </button>
                  </div>
                  <button
                    @click="addToCart"
                    class="flex-1 bg-primary-600 hover:bg-primary-700 text-white py-3.5 rounded-xl font-semibold transition-all hover:shadow-lg hover:shadow-primary-500/25 active:scale-[0.98]"
                  >
                    Add to Cart — ${{ (selectedVariantPrice * modalQty + selectedAddOnsTotal * modalQty).toFixed(2) }}
                  </button>
                </div>
              </div>
            </div>
          </Transition>
        </div>
      </Transition>
    </Teleport>
  </div>
</template>

<script setup lang="ts">
definePageMeta({ layout: 'menu' })

const route = useRoute()
const branchCode = route.params.branchCode as string
const tableNo = (route.query.table as string) || ''

const { get } = usePublicApi()
const cart = useMenuCart()

// Set branch and table in cart
cart.branchCode.value = branchCode
if (tableNo) cart.tableNo.value = tableNo

// State
const loading = ref(true)
const error = ref(false)
const branchInfo = ref<any>(null)
const categories = ref<any[]>([])
const allItems = ref<any[]>([])
const availableAddOns = ref<any[]>([])
const activeCategory = ref<number | null>(null)
const searchMode = ref(false)
const searchQuery = ref('')
const selectedItem = ref<any>(null)
const selectedVariant = ref<any>(null)
const selectedAddOns = ref<number[]>([])
const itemNote = ref('')
const modalQty = ref(1)

// Derived
const { itemCount, total } = cart

const filteredItems = computed(() => {
  if (!searchQuery.value) return allItems.value
  const q = searchQuery.value.toLowerCase()
  return allItems.value.filter(item =>
    item.name.toLowerCase().includes(q)
  )
})

const selectedVariantPrice = computed(() => {
  if (selectedVariant.value) return selectedVariant.value.price
  return selectedItem.value?.basePrice || 0
})

const selectedAddOnsTotal = computed(() => {
  return availableAddOns.value
    .filter(a => selectedAddOns.value.includes(a.addOnId))
    .reduce((sum, a) => sum + a.price, 0)
})

// Methods
const scrollToCategory = (catId: number) => {
  const el = document.getElementById(`cat-${catId}`)
  if (el) {
    el.scrollIntoView({ behavior: 'smooth', block: 'start' })
  }
}

const openItemDetail = (item: any) => {
  selectedItem.value = { ...item }
  selectedVariant.value = item.variants?.[0] || null
  selectedAddOns.value = []
  itemNote.value = ''
  modalQty.value = 1
}

const toggleAddOn = (addonId: number) => {
  if (selectedAddOns.value.includes(addonId)) {
    selectedAddOns.value = selectedAddOns.value.filter(id => id !== addonId)
  } else {
    selectedAddOns.value.push(addonId)
  }
}

const addToCart = () => {
  if (!selectedItem.value) return

  const addOns = availableAddOns.value
    .filter(a => selectedAddOns.value.includes(a.addOnId))
    .map(a => ({ addOnId: a.addOnId, name: a.name, price: a.price }))

  cart.addItem({
    menuItemId: selectedItem.value.menuItemId,
    name: selectedItem.value.name,
    price: selectedItem.value.basePrice,
    imageUrl: selectedItem.value.imageUrl,
    qty: modalQty.value,
    note: itemNote.value || undefined,
    variantId: selectedVariant.value?.variantId,
    variantSize: selectedVariant.value?.size,
    variantPrice: selectedVariant.value?.price,
    addOns: addOns.length > 0 ? addOns : undefined,
    addOnTotal: addOns.reduce((sum, a) => sum + a.price, 0)
  })

  selectedItem.value = null
}

// Fetch menu data
const fetchMenu = async () => {
  try {
    loading.value = true
    error.value = false

    const [menuData, addOnsData] = await Promise.all([
      get<any>(`/api/public/branches/${branchCode}/menu`),
      get<any[]>('/api/public/addons')
    ])

    branchInfo.value = menuData.branch
    categories.value = menuData.categories || []

    // Collect all items for search
    allItems.value = categories.value.flatMap(c => c.items || [])
    availableAddOns.value = addOnsData || []

    if (categories.value.length > 0) {
      activeCategory.value = categories.value[0].categoryId
    }
  } catch (e) {
    console.error('Failed to load menu:', e)
    error.value = true
  } finally {
    loading.value = false
  }
}

onMounted(fetchMenu)

// SEO
useHead({
  title: branchInfo.value?.name ? `${branchInfo.value.name} - Menu` : 'Menu'
})
</script>
