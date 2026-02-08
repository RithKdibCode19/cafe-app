<template>
  <NuxtLayout name="admin">
    <div class="space-y-6 relative">
      <UiBreadcrumb :items="[{ label: 'Menu' }]" />
      
      <!-- Header -->
      <div class="flex items-center justify-between">
        <h2 class="text-2xl font-bold text-neutral-900 dark:text-white">Menu Management</h2>
        <button 
          @click="openCreateModal"
          class="bg-primary-600 hover:bg-primary-700 text-white px-4 py-2 rounded-xl text-sm font-medium transition-colors flex items-center gap-2"
        >
          <svg xmlns="http://www.w3.org/2000/svg" class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M5 12h14" />
            <path d="M12 5v14" />
          </svg>
          Add Item
        </button>
      </div>

      <!-- Filters -->
      <div class="flex items-center gap-4 bg-white dark:bg-neutral-900 p-4 rounded-xl border border-neutral-200 dark:border-neutral-800">
        <div class="relative flex-1 max-w-md">
          <svg xmlns="http://www.w3.org/2000/svg" class="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-neutral-400" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <circle cx="11" cy="11" r="8" />
            <path d="m21 21-4.3-4.3" />
          </svg>
          <input 
            v-model="searchQuery"
            type="text" 
            placeholder="Search menu items..." 
            class="w-full pl-10 pr-4 py-2 bg-neutral-100 dark:bg-neutral-800 border-none rounded-lg text-sm focus:ring-2 focus:ring-primary-500 placeholder-neutral-500"
          />
        </div>
         <select v-model="filterCategory" class="bg-neutral-100 dark:bg-neutral-800 border-none rounded-lg text-sm px-4 py-2 focus:ring-2 focus:ring-primary-500">
            <option value="">All Categories</option>
            <option v-for="cat in categories" :key="cat.categoryId" :value="cat.categoryId">{{ cat.name }}</option>
         </select>
      </div>

      <!-- Grid -->
      <div v-if="loading" class="text-center py-12 text-neutral-500">
          Loading menu items...
      </div>
      <div v-else-if="filteredItems.length === 0" class="text-center py-12 text-neutral-500">
          No menu items found.
      </div>
      <div v-else class="grid grid-cols-1 md:grid-cols-3 lg:grid-cols-4 gap-6">
          <div v-for="item in filteredItems" :key="item.menuItemId" class="bg-white dark:bg-neutral-900 rounded-xl border border-neutral-200 dark:border-neutral-800 overflow-hidden hover:shadow-lg transition-shadow group">
              <div class="h-48 bg-neutral-100 dark:bg-neutral-800 flex items-center justify-center relative overflow-hidden">
                   <img v-if="item.imageUrl" :src="item.imageUrl" class="w-full h-full object-cover transition-transform duration-500 group-hover:scale-110" />
                   <svg v-else xmlns="http://www.w3.org/2000/svg" class="w-12 h-12 text-neutral-300" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect width="18" height="18" x="3" y="3" rx="2" ry="2"/><circle cx="9" cy="9" r="2"/><path d="m21 15-3.086-3.086a2 2 0 0 0-2.828 0L6 21"/></svg>
                  
                   <!-- Absolute status badge -->
                   <div class="absolute top-2 right-2">
                      <span :class="[
                          'px-2 py-1 rounded text-xs font-bold uppercase tracking-wide backdrop-blur-md',
                          item.isAvailable ? 'bg-success-500/90 text-white' : 'bg-error-500/90 text-white'
                      ]">
                          {{ item.isAvailable ? 'Active' : 'Inactive' }}
                      </span>
                   </div>
              </div>
              <div class="p-4">
                  <div class="flex justify-between items-start mb-2">
                      <h3 class="font-semibold text-neutral-900 dark:text-white truncate pr-2" :title="item.name">{{ item.name }}</h3>
                      <span class="text-sm font-bold text-primary-600 dark:text-primary-400 whitespace-nowrap">${{ item.basePrice.toFixed(2) }}</span>
                  </div>
                  <div class="text-xs text-neutral-500 dark:text-neutral-400 mb-4">{{ item.category?.name || 'Uncategorized' }}</div>
                  
                  <div class="flex items-center justify-between pt-2 border-t border-neutral-100 dark:border-neutral-800">
                      <button @click="deleteItem(item.menuItemId)" class="text-xs text-neutral-400 hover:text-error-600 transition-colors">Delete</button>
                      <button @click="openEditModal(item)" class="text-xs font-medium text-primary-600 hover:text-primary-700 dark:text-primary-400 dark:hover:text-primary-300 transition-colors">Edit Details</button>
                  </div>
              </div>
          </div>
      </div>

      <!-- Create/Edit Modal -->
      <div v-if="showModal" class="fixed inset-0 z-50 flex items-center justify-center bg-black/50 backdrop-blur-sm p-4">
        <div class="bg-white dark:bg-neutral-900 rounded-2xl shadow-xl w-full max-w-2xl overflow-hidden border border-neutral-200 dark:border-neutral-800 flex flex-col max-h-[90vh]">
          
          <!-- Modal Header -->
          <div class="p-6 border-b border-neutral-200 dark:border-neutral-800 flex justify-between items-center bg-white dark:bg-neutral-900 sticky top-0">
              <h3 class="text-xl font-bold text-neutral-900 dark:text-white">
                  {{ isEditing ? 'Edit Menu Item' : 'New Menu Item' }}
              </h3>
              <button @click="closeModal" class="text-neutral-400 hover:text-neutral-600">
                  <svg xmlns="http://www.w3.org/2000/svg" class="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
              </button>
          </div>

          <!-- Modal Body -->
          <div class="flex-1 overflow-y-auto p-6 scrollbar-thin">
              <!-- Tabs (Only if Editing) -->
              <div v-if="isEditing" class="flex gap-4 mb-6 border-b border-neutral-200 dark:border-neutral-800">
                  <button 
                      @click="activeModalTab = 'details'"
                      :class="[
                          'pb-2 text-sm font-medium transition-colors relative',
                          activeModalTab === 'details' ? 'text-primary-600 dark:text-primary-400' : 'text-neutral-500 hover:text-neutral-700 dark:text-neutral-400'
                      ]"
                  >
                      Details
                      <div v-if="activeModalTab === 'details'" class="absolute bottom-0 left-0 w-full h-0.5 bg-primary-600 dark:bg-primary-400 rounded-full"></div>
                  </button>
                  <button 
                      @click="activeModalTab = 'recipe'"
                      :class="[
                          'pb-2 text-sm font-medium transition-colors relative',
                          activeModalTab === 'recipe' ? 'text-primary-600 dark:text-primary-400' : 'text-neutral-500 hover:text-neutral-700 dark:text-neutral-400'
                      ]"
                  >
                      Recipe & Ingredients
                      <div v-if="activeModalTab === 'recipe'" class="absolute bottom-0 left-0 w-full h-0.5 bg-primary-600 dark:bg-primary-400 rounded-full"></div>
                  </button>
                  <button 
                      @click="activeModalTab = 'variants'"
                      :class="[
                          'pb-2 text-sm font-medium transition-colors relative',
                          activeModalTab === 'variants' ? 'text-primary-600 dark:text-primary-400' : 'text-neutral-500 hover:text-neutral-700 dark:text-neutral-400'
                      ]"
                  >
                      Variants
                      <div v-if="activeModalTab === 'variants'" class="absolute bottom-0 left-0 w-full h-0.5 bg-primary-600 dark:bg-primary-400 rounded-full"></div>
                  </button>
              </div>

              <!-- Tab: Details -->
              <div v-if="activeModalTab === 'details'" class="space-y-4">
                  <div>
                       <label class="block text-sm font-medium text-neutral-700 dark:text-neutral-300 mb-1">Item Name</label>
                       <input type="text" v-model="form.name" class="w-full bg-neutral-50 dark:bg-neutral-800 border-none rounded-lg p-2.5 text-sm ring-1 ring-neutral-200 dark:ring-neutral-700 focus:ring-2 focus:ring-primary-500" placeholder="e.g. Latte">
                  </div>
                  
                  <div class="grid grid-cols-2 gap-4">
                       <div>
                           <label class="block text-sm font-medium text-neutral-700 dark:text-neutral-300 mb-1">Category</label>
                           <select v-model="form.categoryId" class="w-full bg-neutral-50 dark:bg-neutral-800 border-none rounded-lg p-2.5 text-sm ring-1 ring-neutral-200 dark:ring-neutral-700 focus:ring-2 focus:ring-primary-500">
                                <option v-for="cat in categories" :key="cat.categoryId" :value="cat.categoryId">{{ cat.name }}</option>
                           </select>
                       </div>
                       <div>
                           <label class="block text-sm font-medium text-neutral-700 dark:text-neutral-300 mb-1">Base Price ($)</label>
                           <input type="number" v-model="form.basePrice" class="w-full bg-neutral-50 dark:bg-neutral-800 border-none rounded-lg p-2.5 text-sm ring-1 ring-neutral-200 dark:ring-neutral-700 focus:ring-2 focus:ring-primary-500" step="0.01">
                       </div>
                  </div>

                   <div>
                       <label class="block text-sm font-medium text-neutral-700 dark:text-neutral-300 mb-1">Image URL</label>
                       <input type="text" v-model="form.imageUrl" class="w-full bg-neutral-50 dark:bg-neutral-800 border-none rounded-lg p-2.5 text-sm ring-1 ring-neutral-200 dark:ring-neutral-700 focus:ring-2 focus:ring-primary-500" placeholder="https://...">
                  </div>

                  <div class="flex items-center gap-3">
                       <label class="relative inline-flex items-center cursor-pointer">
                          <input type="checkbox" v-model="form.isAvailable" class="sr-only peer">
                          <div class="w-11 h-6 bg-neutral-200 peer-focus:outline-none peer-focus:ring-4 peer-focus:ring-primary-300 dark:peer-focus:ring-primary-800 rounded-full peer dark:bg-neutral-700 peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-neutral-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all dark:border-neutral-600 peer-checked:bg-primary-600"></div>
                          <span class="ml-3 text-sm font-medium text-neutral-900 dark:text-neutral-300">Available for Sale</span>
                      </label>
                  </div>
              </div>

              <!-- Tab: Recipe -->
               <div v-if="activeModalTab === 'recipe'" class="space-y-6">
                  
                  <!-- Add Ingredient Form -->
                  <div class="bg-neutral-50 dark:bg-neutral-800/50 p-4 rounded-xl border border-neutral-200 dark:border-neutral-800 space-y-3">
                      <h4 class="text-sm font-semibold text-neutral-900 dark:text-white">Add Ingredient to Recipe</h4>
                      <div class="flex gap-3">
                          <div class="flex-1">
                               <select v-model="newRecipe.ingredientId" class="w-full bg-white dark:bg-neutral-800 border border-neutral-200 dark:border-neutral-700 rounded-lg p-2 text-sm focus:ring-2 focus:ring-primary-500">
                                    <option :value="null" disabled>Select Ingredient</option>
                                    <option v-for="ing in ingredients" :key="ing.ingredientId" :value="ing.ingredientId">
                                        {{ ing.name }} ({{ ing.unit }})
                                    </option>
                               </select>
                          </div>
                          <div class="w-24">
                               <input type="number" v-model="newRecipe.quantityNeeded" placeholder="Qty" class="w-full bg-white dark:bg-neutral-800 border border-neutral-200 dark:border-neutral-700 rounded-lg p-2 text-sm focus:ring-2 focus:ring-primary-500" step="0.1">
                          </div>
                          <button @click="addRecipeIngredient" class="bg-primary-600 hover:bg-primary-700 text-white px-3 py-2 rounded-lg text-sm font-medium transition-colors">
                              Add
                          </button>
                      </div>
                  </div>

                  <!-- Recipe List -->
                  <div>
                       <h4 class="text-sm font-semibold text-neutral-900 dark:text-white mb-3">Current Recipe</h4>
                       <div v-if="recipes.length === 0" class="text-sm text-neutral-500 italic text-center py-4 bg-neutral-50 dark:bg-neutral-800/30 rounded-lg">
                          No ingredients added yet.
                       </div>
                       <div v-else class="space-y-2">
                          <div v-for="rec in recipes" :key="rec.recipeId" class="flex items-center justify-between p-3 bg-white dark:bg-neutral-800/50 border border-neutral-100 dark:border-neutral-800 rounded-lg">
                               <div class="flex items-center gap-3">
                                   <div class="w-8 h-8 rounded-full bg-orange-100 dark:bg-orange-900/30 flex items-center justify-center text-orange-600 dark:text-orange-400">
                                       <svg xmlns="http://www.w3.org/2000/svg" class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M17 3a2.828 2.828 0 1 1 4 4L7.5 20.5 2 22l1.5-5.5L17 3z"/></svg>
                                   </div>
                                   <div class="text-sm">
                                       <div class="font-medium text-neutral-900 dark:text-white">{{ rec.ingredientName }}</div>
                                       <div class="text-xs text-neutral-500">{{ rec.quantityNeeded }} {{ rec.ingredientUnit }}</div>
                                   </div>
                               </div>
                               <button @click="removeRecipe(rec.recipeId)" class="text-neutral-400 hover:text-error-600 p-1">
                                   <svg xmlns="http://www.w3.org/2000/svg" class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
                               </button>
                          </div>
                       </div>
                  </div>

               </div>

               <!-- Tab: Variants -->
                <div v-if="activeModalTab === 'variants'" class="space-y-6">
                  
                  <!-- Add Variant Form -->
                  <div class="bg-neutral-50 dark:bg-neutral-800/50 p-4 rounded-xl border border-neutral-200 dark:border-neutral-800 space-y-3">
                      <h4 class="text-sm font-semibold text-neutral-900 dark:text-white">Add Variant</h4>
                      <div class="flex gap-3">
                          <div class="flex-1">
                               <input type="text" v-model="newVariant.name" placeholder="Name (e.g. Large, Almond Milk)" class="w-full bg-white dark:bg-neutral-800 border border-neutral-200 dark:border-neutral-700 rounded-lg p-2 text-sm focus:ring-2 focus:ring-primary-500" />
                          </div>
                          <div class="w-32">
                               <input type="number" v-model="newVariant.priceAdjustment" placeholder="Price (+/-)" class="w-full bg-white dark:bg-neutral-800 border border-neutral-200 dark:border-neutral-700 rounded-lg p-2 text-sm focus:ring-2 focus:ring-primary-500" step="0.5">
                          </div>
                          <button @click="addVariant" class="bg-primary-600 hover:bg-primary-700 text-white px-3 py-2 rounded-lg text-sm font-medium transition-colors">
                              Add
                          </button>
                      </div>
                       <p class="text-xs text-neutral-500">Price adjustment is added to the base price. Use negative values for discounts.</p>
                  </div>

                  <!-- Variant List -->
                  <div>
                       <h4 class="text-sm font-semibold text-neutral-900 dark:text-white mb-3">Current Variants</h4>
                       <div v-if="variants.length === 0" class="text-sm text-neutral-500 italic text-center py-4 bg-neutral-50 dark:bg-neutral-800/30 rounded-lg">
                          No variants added yet.
                       </div>
                       <div v-else class="space-y-2">
                          <div v-for="variant in variants" :key="variant.variantId" class="flex items-center justify-between p-3 bg-white dark:bg-neutral-800/50 border border-neutral-100 dark:border-neutral-800 rounded-lg">
                               <div class="flex items-center gap-3">
                                   <div class="w-8 h-8 rounded-full bg-blue-100 dark:bg-blue-900/30 flex items-center justify-center text-blue-600 dark:text-blue-400">
                                       <svg xmlns="http://www.w3.org/2000/svg" class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z"/></svg>
                                   </div>
                                   <div class="text-sm">
                                       <div class="font-medium text-neutral-900 dark:text-white">{{ variant.name }}</div>
                                       <div :class="variant.priceAdjustment >= 0 ? 'text-success-600' : 'text-error-600'" class="text-xs font-bold">
                                           {{ variant.priceAdjustment >= 0 ? '+' : '' }}${{ variant.priceAdjustment.toFixed(2) }}
                                       </div>
                                   </div>
                               </div>
                               <button @click="removeVariant(variant.variantId)" class="text-neutral-400 hover:text-error-600 p-1">
                                   <svg xmlns="http://www.w3.org/2000/svg" class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
                               </button>
                          </div>
                       </div>
                  </div>

               </div>

          </div>

          <!-- Modal Footer -->
          <div class="p-6 bg-neutral-50 dark:bg-neutral-800/50 flex justify-end gap-3 sticky bottom-0">
              <button @click="closeModal" class="px-4 py-2 text-sm font-medium text-neutral-600 hover:text-neutral-900 dark:text-neutral-400 dark:hover:text-white transition-colors">Cancel</button>
              <button v-if="activeModalTab === 'details'" @click="saveItem" class="px-4 py-2 text-sm font-medium bg-primary-600 hover:bg-primary-700 text-white rounded-lg transition-colors">
                  {{ isEditing ? 'Save Changes' : 'Create Item' }}
              </button>
              <button v-else @click="closeModal" class="px-4 py-2 text-sm font-medium bg-primary-600 hover:bg-primary-700 text-white rounded-lg transition-colors">
                  Done
              </button>
          </div>
        </div>
      </div>

    </div>
  </NuxtLayout>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, reactive } from 'vue'

definePageMeta({
  layout: false
})
const { get, post, put, del } = useApi()
const toast = useToast()
interface Category {
    categoryId: number
    name: string
}

interface MenuItem {
    menuItemId: number
    name: string
    description: string
    basePrice: number
    imageUrl: string
    isAvailable: boolean
    categoryId: number
    category?: Category
}

interface Ingredient {
    ingredientId: number
    name: string
    unit: string
}

interface Recipe {
    recipeId: number
    menuItemId: number
    ingredientId: number
    ingredientName: string
    ingredientUnit: string
    quantityNeeded: number
}

interface Variant {
    variantId: number
    menuItemId: number
    name: string
    priceAdjustment: number
}

// State
const menuItems = ref<MenuItem[]>([])
const categories = ref<Category[]>([])
const ingredients = ref<Ingredient[]>([])
const recipes = ref<Recipe[]>([])
const variants = ref<Variant[]>([])

const loading = ref(true)
const searchQuery = ref('')
const filterCategory = ref('')

const showModal = ref(false)
const isEditing = ref(false)
const activeModalTab = ref('details')
const currentItemId = ref<number | null>(null)

// Form Data
const form = reactive({
    name: '',
    categoryId: null as number | null,
    basePrice: 0,
    imageUrl: '',
    isAvailable: true
})

const newRecipe = reactive({
    ingredientId: null as number | null,
    quantityNeeded: 0
})

const newVariant = reactive({
    name: '',
    priceAdjustment: 0
})

// Validation for recipe
const hasRecipes = computed(() => recipes.value.length > 0)

// Computed
const filteredItems = computed(() => {
    let result = menuItems.value

    if (searchQuery.value) {
        const q = searchQuery.value.toLowerCase()
        result = result.filter(item => item.name.toLowerCase().includes(q))
    }

    if (filterCategory.value) {
        result = result.filter(item => item.category?.categoryId === Number(filterCategory.value))
    }

    return result
})

// Fetch Data
const fetchMenu = async () => {
    loading.value = true
    try {
        const data = await get<MenuItem[]>('/menu-items')
        menuItems.value = data || []
    } catch(err) {
        console.error(err)
    } finally {
        loading.value = false
    }
}

const fetchCategories = async () => {
     try {
        const data = await get<Category[]>('/categories')
        categories.value = data || []
    } catch(err) { console.error(err) }
}

const fetchIngredients = async () => {
     try {
        const data = await get<Ingredient[]>('/ingredients')
        ingredients.value = data || []
    } catch(err) { console.error(err) }
}

const fetchRecipes = async (menuItemId: number) => {
    try {
        const data = await get<Recipe[]>('/recipes/menu-item/' + menuItemId)
        recipes.value = data || []
    } catch(err) { console.error(err) }
}

const fetchVariants = async (menuItemId: number) => {
    try {
        const data = await get<Variant[]>('/variants/menu-item/' + menuItemId)
        variants.value = data || []
    } catch(err) { console.error(err) }
}

// Actions
const openCreateModal = () => {
    isEditing.value = false
    activeModalTab.value = 'details'
    currentItemId.value = null
    // Reset form
    form.name = ''
    form.categoryId = categories.value.length > 0 ? (categories.value[0]?.categoryId ?? null) : null
    form.basePrice = 0
    form.imageUrl = ''
    form.isAvailable = true
    
    showModal.value = true
}

const openEditModal = async (item: MenuItem) => {
    isEditing.value = true
    activeModalTab.value = 'details'
    currentItemId.value = item.menuItemId
    
    // Fill form
    form.name = item.name
    form.categoryId = item.category?.categoryId || null
    form.basePrice = item.basePrice
    form.imageUrl = item.imageUrl
    form.isAvailable = item.isAvailable

    // Fetch recipes and variants for this item
    await fetchRecipes(item.menuItemId)
    await fetchVariants(item.menuItemId)
    await fetchIngredients() // Ensure we have list

    showModal.value = true
}

const closeModal = () => {
    showModal.value = false
}

const saveItem = async () => {
    try {
        const payload = { ...form }
        
        if (isEditing.value && currentItemId.value) {
            await put(`/menu-items/${currentItemId.value}`, payload)
        } else {
            await post('/menu-items/add', payload)
        }
        
        await fetchMenu()
        toast.success(isEditing.value ? 'Menu item updated' : 'Menu item created')
        if (!isEditing.value) closeModal() // Close on create
    } catch(err) {
        console.error(err)
        toast.error('Failed to save menu item')
    }
}

const deleteItem = async (id: number) => {
    if(!confirm("Are you sure? This will delete the item.")) return
    try {
        await del(`/menu-items/${id}`)
        toast.success('Menu item deleted')
        fetchMenu()
    } catch(err) {
        toast.error('Failed to delete menu item')
    }
}

const addRecipeIngredient = async () => {
    if (!newRecipe.ingredientId || newRecipe.quantityNeeded <= 0 || !currentItemId.value) return

    try {
        await post('/recipes', {
            menuItemId: currentItemId.value,
            ingredientId: newRecipe.ingredientId,
            quantityNeeded: newRecipe.quantityNeeded
        })
        
        // Refresh recipes
        await fetchRecipes(currentItemId.value)
        
        // Reset sub-form
        newRecipe.quantityNeeded = 0
        toast.success('Ingredient added to recipe')
        // newRecipe.ingredientId = null // Keep selection maybe?
    } catch(err) {
        console.error(err)
        toast.error('Failed to add ingredient')
    }
}

const removeRecipe = async (recipeId: number) => {
    if(!currentItemId.value) return
    try {
        await del(`/recipes/${recipeId}`)
        toast.success('Ingredient removed from recipe')
        await fetchRecipes(currentItemId.value)
    } catch(err) {
        toast.error('Failed to remove ingredient')
    }
}

const addVariant = async () => {
    if (!newVariant.name || !currentItemId.value) return

    try {
        await post('/variants', {
            menuItemId: currentItemId.value,
            name: newVariant.name,
            priceAdjustment: newVariant.priceAdjustment
        })
        
        // Refresh variants
        await fetchVariants(currentItemId.value)
        
        // Reset sub-form
        newVariant.name = ''
        newVariant.priceAdjustment = 0
        toast.success('Variant added')
    } catch(err) {
        console.error(err)
        toast.error('Failed to add variant')
    }
}

const removeVariant = async (variantId: number) => {
    if(!currentItemId.value) return
    try {
        await del(`/variants/${variantId}`)
        toast.success('Variant removed')
        await fetchVariants(currentItemId.value)
    } catch(err) {
        toast.error('Failed to remove variant')
    }
}

onMounted(() => {
    fetchMenu()
    fetchCategories()
    fetchIngredients()
})
</script>
