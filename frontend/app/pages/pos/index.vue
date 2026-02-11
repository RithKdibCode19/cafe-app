<template>
  <NuxtLayout name="pos">
    <div class="h-full flex flex-col p-6 lg:p-8">
      <!-- Toast Notification -->
      <Transition name="toast">
        <div 
          v-if="showToast" 
          class="fixed top-8 left-1/2 -translate-x-1/2 z-50 bg-emerald-600 text-white px-6 py-3 rounded-2xl shadow-lg flex items-center gap-3"
        >
          <div class="w-6 h-6 rounded-full bg-white/20 flex items-center justify-center">
            <svg xmlns="http://www.w3.org/2000/svg" class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3" stroke-linecap="round" stroke-linejoin="round">
              <path d="M20 6 9 17l-5-5"/>
            </svg>
          </div>
          <span class="font-semibold text-base">{{ toastMessage }}</span>
        </div>
      </Transition>

      <!-- Header Section -->
      <div class="flex flex-col lg:flex-row justify-between items-start lg:items-center mb-10 gap-6">
        <!-- Breadcrumbs -->
        <div class="flex items-center gap-3 text-sm">
          <button 
            @click="handleMainAllClick"
            class="flex items-center gap-2 text-neutral-400 hover:text-white"
          >
            <svg xmlns="http://www.w3.org/2000/svg" class="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="m3 9 9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/>
              <polyline points="9 22 9 12 15 12 15 22"/>
            </svg>
            <span :class="categoryPath.length === 0 ? 'text-white font-bold' : 'font-medium'">Menu</span>
          </button>
          <template v-for="(cat, index) in categoryPath" :key="cat.categoryId">
            <svg xmlns="http://www.w3.org/2000/svg" class="w-4 h-4 text-neutral-600" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="m9 18 6-6-6-6"/>
            </svg>
            <button 
              @click="handleBreadcrumbClick(index)"
              :class="[
                'flex items-center gap-2',
                index === categoryPath.length - 1 ? 'text-white font-bold' : 'text-neutral-400 hover:text-white font-medium'
              ]"
            >
              <component :is="getCategoryIcon(cat.name)" class="w-5 h-5" />
              {{ cat.name }}
            </button>
          </template>
        </div>

        <!-- Search Bar -->
        <div class="relative w-full lg:w-96">
          <div class="absolute left-5 top-1/2 -translate-y-1/2 text-neutral-500">
            <svg xmlns="http://www.w3.org/2000/svg" class="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <circle cx="11" cy="11" r="8" />
              <path d="m21 21-4.3-4.3" />
            </svg>
          </div>
          <input 
            v-model="searchQuery"
            type="text" 
            placeholder="Search menu..." 
            class="w-full pl-14 pr-5 py-3.5 bg-neutral-800 border border-neutral-700 rounded-2xl text-sm focus:ring-2 focus:ring-primary-500/50 focus:border-primary-500 text-white placeholder-neutral-500"
          />
        </div>
      </div>

      <!-- Categories Navigation -->
      <div class="mb-10">
        <div class="flex gap-4 overflow-x-auto pb-3 scrollbar-hide">
          <button
            v-if="categoryPath.length > 0"
            @click="handleBack()"
            class="flex items-center gap-2 px-5 py-3 rounded-2xl font-medium whitespace-nowrap bg-neutral-800 text-neutral-300 hover:bg-neutral-700 hover:text-white border border-neutral-700"
          >
            <svg xmlns="http://www.w3.org/2000/svg" class="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><path d="m15 18-6-6 6-6"/></svg>
            Back
          </button>
          
          <button
            v-for="category in currentViewCategories"
            :key="category.categoryId"
            @click="handleCategoryClick(category)"
            :class="[
              'flex items-center gap-2 px-5 py-3 rounded-2xl font-medium whitespace-nowrap border',
              selectedCategory === category.categoryId
                ? 'bg-primary-600 border-primary-500 text-white'
                : 'bg-neutral-800 border-neutral-700 text-neutral-400 hover:bg-neutral-700 hover:text-white'
            ]"
          >
            <component :is="getCategoryIcon(category.name)" class="w-5 h-5" />
            {{ category.name }}
            <span v-if="category.children && category.children.length > 0" class="bg-white/15 rounded-xl px-2.5 py-1 text-xs font-bold">
              {{ category.children.length }}
            </span>
          </button>
        </div>
      </div>

      <!-- Items Grid -->
      <div class="flex-1 overflow-y-auto scrollbar-thin">
        <!-- Loading State -->
        <div v-if="loading" class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 gap-6">
          <div v-for="i in 10" :key="i" class="bg-neutral-800/30 rounded-3xl overflow-hidden border border-neutral-700/20">
            <div class="aspect-square bg-neutral-700/30 animate-pulse"></div>
            <div class="p-5 space-y-3">
              <div class="h-5 bg-neutral-700/30 rounded-xl animate-pulse w-3/4"></div>
              <div class="h-6 bg-neutral-700/30 rounded-xl animate-pulse w-1/2"></div>
            </div>
          </div>
        </div>

        <!-- Empty State -->
        <div v-else-if="filteredItems.length === 0" class="flex flex-col items-center justify-center h-96 text-neutral-500">
          <div class="w-28 h-28 rounded-3xl bg-gradient-to-br from-neutral-800/80 to-neutral-900/80 flex items-center justify-center mb-8 border border-neutral-700/30">
            <svg xmlns="http://www.w3.org/2000/svg" class="w-14 h-14 opacity-20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
              <path d="M17 8h1a4 4 0 1 1 0 8h-1" />
              <path d="M3 8h14v9a4 4 0 0 1-4 4H7a4 4 0 0 1-4-4Z" />
              <line x1="6" x2="6" y1="2" y2="4" />
              <line x1="10" x2="10" y1="2" y2="4" />
            </svg>
          </div>
          <p class="text-xl font-bold text-neutral-300 mb-2">No items found</p>
          <p class="text-sm text-neutral-500">Try selecting a different category or search term</p>
        </div>

        <!-- Items Grid -->
        <div v-else class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 gap-6 pb-8">
          <button
            v-for="item in filteredItems"
            :key="item.menuItemId"
            @click="handleAddToCart(item)"
            :disabled="!item.isAvailable"
            class="group relative bg-neutral-800 rounded-2xl overflow-hidden border border-neutral-700 hover:border-primary-500 text-left disabled:opacity-50 disabled:cursor-not-allowed"
          >
            <!-- Image Container -->
            <div class="aspect-square bg-neutral-800 relative overflow-hidden">
              <img 
                v-if="item.imageUrl" 
                :src="item.imageUrl" 
                :alt="item.name"
                loading="lazy"
                decoding="async"
                class="w-full h-full object-cover" 
              />
              <div v-else class="absolute inset-0 flex flex-col items-center justify-center bg-gradient-to-br from-neutral-800 via-neutral-850 to-neutral-900 gap-3">
                <svg xmlns="http://www.w3.org/2000/svg" class="w-12 h-12 text-neutral-700/50" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
                  <path d="M17 8h1a4 4 0 1 1 0 8h-1" />
                  <path d="M3 8h14v9a4 4 0 0 1-4 4H7a4 4 0 0 1-4-4Z" />
                </svg>
                <span class="text-xs text-neutral-600 font-medium px-3 text-center">{{ item.name }}</span>
              </div>
              
              <!-- Enhanced Gradient Overlay -->
              <div class="absolute inset-0 bg-gradient-to-t from-neutral-900 via-transparent to-transparent"></div>

              <!-- Price Badge -->
              <div class="absolute bottom-4 left-4">
                <span class="bg-primary-500 text-white px-3 py-1.5 rounded-xl text-sm font-bold">
                  ${{ item.basePrice.toFixed(2) }}
                </span>
              </div>

              <!-- Availability Badge -->
              <div v-if="!item.isAvailable" class="absolute inset-0 bg-neutral-900/90 flex items-center justify-center">
                <span class="bg-red-500/20 text-red-400 border border-red-500/40 px-4 py-2 rounded-xl text-xs font-bold uppercase">
                  Sold Out
                </span>
              </div>
              
              <!-- Add Button Overlay -->
              <div v-if="item.isAvailable" class="absolute bottom-3 right-3 opacity-0 group-hover:opacity-100">
                <div class="bg-white text-primary-600 p-2.5 rounded-xl">
                  <svg xmlns="http://www.w3.org/2000/svg" class="w-6 h-6" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M12 5v14" />
                    <path d="M5 12h14" />
                  </svg>
                </div>
              </div>
            </div>

            <!-- Content -->
            <div class="p-5">
              <h3 class="font-bold text-neutral-100 text-base leading-snug line-clamp-2 h-12">{{ item.name }}</h3>
            </div>
          </button>
        </div>
      </div>
    </div>

    <!-- Variant Selection Modal -->
    <div
      v-if="showVariantModal && selectedItem"
      class="fixed inset-0 z-50 flex items-center justify-center bg-black/80 p-4"
    >
      <div class="bg-neutral-800 rounded-2xl shadow-xl w-full max-w-sm overflow-hidden border border-neutral-700 flex flex-col">
        <div class="p-4 border-b border-neutral-700 text-center">
             <h3 class="text-lg font-bold text-white">Select Size</h3>
             <p class="text-neutral-400 text-sm">for {{ selectedItem.name }}</p>
        </div>
        <div class="p-4 space-y-3">
            <button
                v-for="variant in selectedItem.variants"
                :key="variant.variantId"
                @click="selectedVariant = variant"
                :class="[
                    'w-full p-4 rounded-xl flex items-center justify-between border transition-all',
                    selectedVariant?.variantId === variant.variantId
                        ? 'bg-primary-600/20 border-primary-500 text-white'
                        : 'bg-neutral-700/50 border-neutral-600 text-neutral-300 hover:bg-neutral-700 hover:text-white'
                ]"
            >
                <span class="font-bold text-lg">{{ variant.size }}</span>
                <span :class="selectedVariant?.variantId === variant.variantId ? 'text-primary-400' : 'text-neutral-400'">${{ variant.price.toFixed(2) }}</span>
            </button>
        </div>
        <div class="p-4 border-t border-neutral-700 flex gap-3">
             <button @click="showVariantModal = false" class="flex-1 py-3 rounded-xl border border-neutral-600 text-neutral-400 hover:text-white">Cancel</button>
             <button @click="confirmVariantSelection" :disabled="!selectedVariant" class="flex-1 py-3 rounded-xl bg-primary-600 text-white font-bold hover:bg-primary-500 disabled:opacity-50">Continue</button>
        </div>
      </div>
    </div>

    <!-- Add-On Modal -->
    <div
      v-if="showAddOnModal && selectedItem"
      class="fixed inset-0 z-50 flex items-center justify-center bg-black/80 p-4"
      @click.self="closeAddOnModal"
    >
      <div class="bg-neutral-800 rounded-2xl shadow-xl w-full max-w-lg max-h-[85vh] overflow-hidden border border-neutral-700 flex flex-col">
        <!-- Header -->
        <div class="p-4 border-b border-neutral-700 flex items-center gap-4">
          <div class="w-16 h-16 rounded-xl bg-neutral-700 overflow-hidden flex-shrink-0">
            <img v-if="selectedItem.imageUrl" :src="selectedItem.imageUrl" class="w-full h-full object-cover" />
            <div v-else class="w-full h-full flex items-center justify-center text-neutral-500">
              <svg xmlns="http://www.w3.org/2000/svg" class="w-8 h-8" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                <path d="M17 8h1a4 4 0 1 1 0 8h-1" /><path d="M3 8h14v9a4 4 0 0 1-4 4H7a4 4 0 0 1-4-4Z" />
              </svg>
            </div>
          </div>
          <div class="flex-1">
            <h3 class="text-lg font-bold text-white">{{ selectedItem.name }} <span v-if="selectedVariant">({{ selectedVariant.size }})</span></h3>
            <p class="text-primary-400 font-semibold">${{ (selectedVariant ? selectedVariant.price : selectedItem.basePrice).toFixed(2) }}</p>
          </div>
          <button @click="closeAddOnModal" class="p-2 text-neutral-400 hover:text-white">
            <svg xmlns="http://www.w3.org/2000/svg" class="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M18 6 6 18"/><path d="m6 6 12 12"/>
            </svg>
          </button>
        </div>

        <!-- Add-ons List -->
        <div class="flex-1 overflow-y-auto p-4">
          <div v-if="addOnsLoading" class="flex items-center justify-center py-8">
            <div class="w-8 h-8 border-2 border-primary-500/30 border-t-primary-500 rounded-full animate-spin"></div>
          </div>
          
          <div v-else-if="addOnList.length > 0" class="space-y-3">
            <h4 class="text-sm font-bold text-neutral-400 uppercase tracking-wider mb-3">Customize Your Order</h4>
            <div class="grid grid-cols-2 gap-2">
              <button
                v-for="addOn in addOnList"
                :key="addOn.addonId"
                @click="toggleAddOn(addOn)"
                :class="[
                  'p-3 rounded-xl border text-left transition-colors',
                  isAddOnSelected(addOn.addonId)
                    ? 'bg-primary-600/20 border-primary-500 text-white'
                    : 'bg-neutral-700/50 border-neutral-600 text-neutral-300 hover:border-neutral-500'
                ]"
              >
                <div class="flex items-center justify-between">
                  <span class="text-sm font-medium">{{ addOn.name }}</span>
                  <span :class="['text-xs font-bold', isAddOnSelected(addOn.addonId) ? 'text-primary-400' : 'text-neutral-500']">
                    +${{ addOn.price.toFixed(2) }}
                  </span>
                </div>
              </button>
            </div>
          </div>

          <div v-else class="text-center py-8 text-neutral-500">
            <p>No add-ons available</p>
          </div>

          <!-- Notes Input -->
          <div class="mt-4">
            <label class="block text-sm font-bold text-neutral-400 uppercase tracking-wider mb-2">Special Instructions</label>
            <textarea
              v-model="itemNotes"
              placeholder="e.g., Less ice, extra hot..."
              rows="2"
              class="w-full px-4 py-3 rounded-xl bg-neutral-700 border border-neutral-600 text-white placeholder-neutral-500 focus:outline-none focus:ring-2 focus:ring-primary-500 resize-none"
            ></textarea>
          </div>
        </div>

        <!-- Footer -->
        <div class="p-4 border-t border-neutral-700 bg-neutral-900/50">
          <div class="flex items-center justify-between mb-3 text-sm">
            <span class="text-neutral-400">
              {{ selectedAddOns.length > 0 ? `${selectedAddOns.length} add-on(s)` : 'No add-ons' }}
            </span>
            <span class="text-white font-bold">
              Total: ${{ (selectedItem.basePrice + addOnTotal).toFixed(2) }}
            </span>
          </div>
          <div class="grid grid-cols-2 gap-3">
            <button 
              @click="closeAddOnModal" 
              class="px-4 py-3 rounded-xl border border-neutral-600 text-neutral-400 hover:text-white hover:border-neutral-500"
            >
              Cancel
            </button>
            <button 
              @click="confirmAddToCart" 
              class="px-4 py-3 rounded-xl bg-primary-600 text-white font-bold hover:bg-primary-500"
            >
              Add to Cart
            </button>
          </div>
        </div>
      </div>
    </div>
  </NuxtLayout>
</template>

<script setup lang="ts">
import { h, ref, computed, onMounted, shallowRef, watch } from 'vue'
import { useDebounceFn } from '@vueuse/core'

definePageMeta({
  layout: false
})

const { get } = useApi()
const { addToCart, addToCartWithAddOns } = useCart()

// Add-on Types
interface AddOn {
  addonId: number
  name: string
  price: number
}

// Types
interface Category {
  categoryId: number
  name: string
  parentId?: number
  children?: Category[]
}

interface Variant {
  variantId: number
  size: 'S' | 'M' | 'L'
  price: number
}

interface MenuItem {
  menuItemId: number
  name: string
  basePrice: number
  imageUrl: string
  isAvailable: boolean
  categoryId: number
  category?: Category
  variants?: Variant[]
}

// Use useState for persistent caching across navigation
const cachedCategories = useState<Category[]>('pos-categories', () => [])
const cachedMenuItems = useState<MenuItem[]>('pos-menu-items', () => [])

const selectedCategory = ref<number | 'all'>('all')
const searchQuery = ref('')
const debouncedSearchQuery = ref('')
const loading = ref(true)
const showToast = ref(false)
const toastMessage = ref('')

// Add-on Modal State
const showAddOnModal = ref(false)
const showVariantModal = ref(false)
const selectedItem = ref<MenuItem | null>(null)
const selectedVariant = ref<Variant | null>(null)
const addOnList = shallowRef<AddOn[]>([])
const selectedAddOns = ref<AddOn[]>([])
const itemNotes = ref('')
const addOnsLoading = ref(false)

// Use shallowRef for large arrays to reduce reactivity overhead
const rootCategories = shallowRef<Category[]>([])
const menuItems = shallowRef<MenuItem[]>([])

// Debounce search to reduce recomputations
const updateDebouncedSearch = useDebounceFn((val: string) => {
  debouncedSearchQuery.value = val
}, 150)

// Watch searchQuery and debounce updates
watch(searchQuery, (newVal) => {
  updateDebouncedSearch(newVal)
})

// Navigation Stack for Breadcrumbs/Back functionality
const categoryPath = ref<Category[]>([])

// Memoized descendant ID cache
const descendantCache = new Map<number, number[]>()

// Category Icons mapping (static, no reactivity needed)
const categoryIcons: Record<string, () => ReturnType<typeof h>> = {
  'Beverages': () => h('svg', { xmlns: 'http://www.w3.org/2000/svg', viewBox: '0 0 24 24', fill: 'none', stroke: 'currentColor', 'stroke-width': '2', 'stroke-linecap': 'round', 'stroke-linejoin': 'round' }, [
    h('path', { d: 'M17 8h1a4 4 0 1 1 0 8h-1' }),
    h('path', { d: 'M3 8h14v9a4 4 0 0 1-4 4H7a4 4 0 0 1-4-4Z' })
  ]),
  'Coffee': () => h('svg', { xmlns: 'http://www.w3.org/2000/svg', viewBox: '0 0 24 24', fill: 'none', stroke: 'currentColor', 'stroke-width': '2', 'stroke-linecap': 'round', 'stroke-linejoin': 'round' }, [
    h('path', { d: 'M17 8h1a4 4 0 1 1 0 8h-1' }),
    h('path', { d: 'M3 8h14v9a4 4 0 0 1-4 4H7a4 4 0 0 1-4-4Z' }),
    h('line', { x1: '6', x2: '6', y1: '2', y2: '4' }),
    h('line', { x1: '10', x2: '10', y1: '2', y2: '4' })
  ]),
  'Tea & Milk': () => h('svg', { xmlns: 'http://www.w3.org/2000/svg', viewBox: '0 0 24 24', fill: 'none', stroke: 'currentColor', 'stroke-width': '2', 'stroke-linecap': 'round', 'stroke-linejoin': 'round' }, [
    h('path', { d: 'M7 21h10' }),
    h('path', { d: 'M5 3h14' }),
    h('path', { d: 'M6 3v18' }),
    h('path', { d: 'M18 3v18' }),
    h('path', { d: 'M9 10h6' })
  ]),
  'Food': () => h('svg', { xmlns: 'http://www.w3.org/2000/svg', viewBox: '0 0 24 24', fill: 'none', stroke: 'currentColor', 'stroke-width': '2', 'stroke-linecap': 'round', 'stroke-linejoin': 'round' }, [
    h('path', { d: 'M3 2v7c0 1.1.9 2 2 2h4a2 2 0 0 0 2-2V2' }),
    h('path', { d: 'M7 2v20' }),
    h('path', { d: 'M21 15V2v0a5 5 0 0 0-5 5v6c0 1.1.9 2 2 2h3Zm0 0v7' })
  ]),
  'Bakery': () => h('svg', { xmlns: 'http://www.w3.org/2000/svg', viewBox: '0 0 24 24', fill: 'none', stroke: 'currentColor', 'stroke-width': '2', 'stroke-linecap': 'round', 'stroke-linejoin': 'round' }, [
    h('path', { d: 'M20 10S17 3 12 3 4 10 4 10' }),
    h('path', { d: 'M4 10v10h16V10' }),
    h('circle', { cx: '12', cy: '15', r: '3' })
  ])
}

const getCategoryIcon = (name: string) => {
  return categoryIcons[name] || categoryIcons['Beverages']
}

const currentViewCategories = computed(() => {
  if (categoryPath.value.length === 0) {
    return rootCategories.value
  }
  const currentParent = categoryPath.value[categoryPath.value.length - 1]
  return currentParent.children || []
})

const handleCategoryClick = (category: Category) => {
  selectedCategory.value = category.categoryId
  if (category.children && category.children.length > 0) {
    categoryPath.value.push(category)
  }
}

const handleBack = () => {
  categoryPath.value.pop()
  if (categoryPath.value.length > 0) {
    selectedCategory.value = categoryPath.value[categoryPath.value.length - 1].categoryId
  } else {
    selectedCategory.value = 'all'
  }
}

const handleMainAllClick = () => {
  categoryPath.value = []
  selectedCategory.value = 'all'
}

const handleBreadcrumbClick = (index: number) => {
  categoryPath.value = categoryPath.value.slice(0, index + 1)
  selectedCategory.value = categoryPath.value[categoryPath.value.length - 1].categoryId
}

const handleAddToCart = (item: MenuItem) => {
  selectedItem.value = item
  selectedAddOns.value = []
  itemNotes.value = ''
  selectedVariant.value = null

  // Check for variants
  if (item.variants && item.variants.length > 0) {
    // Select middle size or first as default logic if needed, or just let user pick
    // For now, no default selection or select first
    selectedVariant.value = item.variants[0]
    showVariantModal.value = true
  } else {
    // No variants, go to add-ons
    showAddOnModal.value = true
  }
}

const confirmVariantSelection = () => {
    showVariantModal.value = false
    showAddOnModal.value = true
}

const handleQuickAdd = (item: MenuItem) => {
  // Quick add without add-ons (e.g., via modifier key)
  addToCart(item)
  toastMessage.value = `${item.name} added to cart`
  showToast.value = true
  setTimeout(() => {
    showToast.value = false
  }, 2500)
}

const toggleAddOn = (addOn: AddOn) => {
  const index = selectedAddOns.value.findIndex(a => a.addonId === addOn.addonId)
  if (index === -1) {
    selectedAddOns.value.push(addOn)
  } else {
    selectedAddOns.value.splice(index, 1)
  }
}

const isAddOnSelected = (addOnId: number) => {
  return selectedAddOns.value.some(a => a.addonId === addOnId)
}

const addOnTotal = computed(() => {
  return selectedAddOns.value.reduce((sum, a) => sum + a.price, 0)
})

const confirmAddToCart = () => {
  if (!selectedItem.value) return
  
  if (selectedAddOns.value.length > 0 || itemNotes.value || selectedVariant.value) {
    // If variant selected, override price and name for the cart item
    const itemToAdd = { ...selectedItem.value }
    if (selectedVariant.value) {
        itemToAdd.basePrice = selectedVariant.value.price
        itemToAdd.name = `${itemToAdd.name} (${selectedVariant.value.size})`
    }
    addToCartWithAddOns(itemToAdd, [...selectedAddOns.value], itemNotes.value)
  } else {
    addToCart(selectedItem.value)
  }
  
  toastMessage.value = `${selectedItem.value.name} added to cart`
  showToast.value = true
  setTimeout(() => {
    showToast.value = false
  }, 2500)
  
  showAddOnModal.value = false
  selectedItem.value = null
  selectedAddOns.value = []
  itemNotes.value = ''
}

const closeAddOnModal = () => {
  showAddOnModal.value = false
  selectedItem.value = null
  selectedAddOns.value = []
  itemNotes.value = ''
}

const fetchData = async () => {
  // Use cached data if available for instant display
  if (cachedCategories.value.length > 0 && cachedMenuItems.value.length > 0) {
    rootCategories.value = cachedCategories.value
    menuItems.value = cachedMenuItems.value
    loading.value = false
    // Refresh in background
    refreshDataInBackground()
    return
  }

  loading.value = true
  try {
    const [cats, items] = await Promise.all([
      get<Category[]>('/categories'),
      get<MenuItem[]>('/menu-items')
    ])
    rootCategories.value = cats || []
    menuItems.value = items || []
    // Cache for future visits
    cachedCategories.value = cats || []
    cachedMenuItems.value = items || []
    // Build descendant cache
    buildDescendantCache(rootCategories.value)
  } catch (err) {
    console.error('Failed to fetch POS data', err)
  } finally {
    loading.value = false
  }
}

const refreshDataInBackground = async () => {
  try {
    const [cats, items] = await Promise.all([
      get<Category[]>('/categories'),
      get<MenuItem[]>('/menu-items')
    ])
    if (cats) {
      rootCategories.value = cats
      cachedCategories.value = cats
      descendantCache.clear()
      buildDescendantCache(cats)
    }
    if (items) {
      menuItems.value = items
      cachedMenuItems.value = items
    }
  } catch (err) {
    // Silent fail for background refresh
  }
}

// Build descendant cache once when data loads
const buildDescendantCache = (categories: Category[]) => {
  const traverse = (cat: Category) => {
    const ids: number[] = [cat.categoryId]
    if (cat.children) {
      cat.children.forEach(child => {
        ids.push(...getDescendantIds(child.categoryId, categories))
      })
    }
    descendantCache.set(cat.categoryId, ids)
  }

  const traverseAll = (cats: Category[]) => {
    cats.forEach(cat => {
      traverse(cat)
      if (cat.children) traverseAll(cat.children)
    })
  }

  traverseAll(categories)
}

// Memoized helper to get all descendant IDs
const getDescendantIds = (catId: number, categories: Category[]): number[] => {
  // Check cache first
  if (descendantCache.has(catId)) {
    return descendantCache.get(catId)!
  }

  const ids: number[] = [catId]
  const findCat = (list: Category[]): Category | null => {
    for (const c of list) {
      if (c.categoryId === catId) return c
      if (c.children) {
        const found = findCat(c.children)
        if (found) return found
      }
    }
    return null
  }
  
  const targetCat = findCat(categories)
  if (!targetCat) return ids

  const collectIds = (c: Category) => {
    ids.push(c.categoryId)
    if (c.children) {
      c.children.forEach(collectIds)
    }
  }
  
  if (targetCat.children) {
    targetCat.children.forEach(collectIds)
  }
  
  // Cache result
  descendantCache.set(catId, ids)
  return ids
}

const filteredItems = computed(() => {
  let result = menuItems.value

  if (selectedCategory.value !== 'all') {
    const allowedIds = getDescendantIds(selectedCategory.value, rootCategories.value)
    result = result.filter(item => 
      allowedIds.includes(item.categoryId) || allowedIds.includes(item.category?.categoryId || -1)
    )
  }

  // Use debounced search query
  if (debouncedSearchQuery.value) {
    const q = debouncedSearchQuery.value.toLowerCase()
    result = result.filter(item => 
      item.name.toLowerCase().includes(q)
    )
  }

  return result
})

const fetchAddOns = async () => {
  addOnsLoading.value = true
  try {
    const data = await get<AddOn[]>('/addons')
    addOnList.value = data || []
  } catch (e) {
    console.error('Failed to fetch add-ons:', e)
  } finally {
    addOnsLoading.value = false
  }
}

onMounted(() => {
  fetchData()
  fetchAddOns()
})
</script>

<style scoped>
.toast-enter-active {
  animation: toast-in 0.5s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.toast-leave-active {
  animation: toast-out 0.3s cubic-bezier(0.4, 0, 1, 1);
}

@keyframes toast-in {
  from {
    opacity: 0;
    transform: translate(-50%, -30px) scale(0.9);
  }
  to {
    opacity: 1;
    transform: translate(-50%, 0) scale(1);
  }
}

@keyframes toast-out {
  from {
    opacity: 1;
    transform: translate(-50%, 0) scale(1);
  }
  to {
    opacity: 0;
    transform: translate(-50%, -20px) scale(0.95);
  }
}
</style>
