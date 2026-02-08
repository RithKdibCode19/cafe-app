<template>
  <NuxtLayout name="admin">
    <div class="space-y-6 relative">
      <UiBreadcrumb :items="[{ label: 'Add-ons' }]" />
      
      <!-- Header -->
      <div class="flex items-center justify-between">
        <h2 class="text-2xl font-bold text-neutral-900 dark:text-white">Add-ons Management</h2>
        <button 
          @click="openCreateModal"
          class="bg-primary-600 hover:bg-primary-700 text-white px-4 py-2 rounded-xl text-sm font-medium transition-colors flex items-center gap-2"
        >
          <svg xmlns="http://www.w3.org/2000/svg" class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M5 12h14" />
            <path d="M12 5v14" />
          </svg>
          Add Add-on
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
            placeholder="Search add-ons..." 
            class="w-full pl-10 pr-4 py-2 bg-neutral-100 dark:bg-neutral-800 border-none rounded-lg text-sm focus:ring-2 focus:ring-primary-500 placeholder-neutral-500"
          />
        </div>
      </div>

      <!-- Loading State -->
      <div v-if="loading" class="text-center py-12 text-neutral-500">
          Loading add-ons...
      </div>
      
      <!-- Empty State -->
      <div v-else-if="filteredAddOns.length === 0" class="text-center py-12 text-neutral-500">
          No add-ons found.
      </div>
      
      <!-- Add-ons Table -->
      <div v-else class="bg-white dark:bg-neutral-900 rounded-xl border border-neutral-200 dark:border-neutral-800 overflow-hidden">
        <table class="w-full">
          <thead class="bg-neutral-50 dark:bg-neutral-800/50">
            <tr>
              <th class="text-left text-xs font-semibold text-neutral-500 dark:text-neutral-400 uppercase tracking-wider px-6 py-4">Name</th>
              <th class="text-left text-xs font-semibold text-neutral-500 dark:text-neutral-400 uppercase tracking-wider px-6 py-4">Price</th>
              <th class="text-right text-xs font-semibold text-neutral-500 dark:text-neutral-400 uppercase tracking-wider px-6 py-4">Actions</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-neutral-100 dark:divide-neutral-800">
            <tr v-for="addon in filteredAddOns" :key="addon.id" class="hover:bg-neutral-50 dark:hover:bg-neutral-800/30 transition-colors">
              <td class="px-6 py-4">
                <div class="flex items-center gap-3">
                  <div class="w-10 h-10 rounded-lg bg-accent-100 dark:bg-accent-900/30 flex items-center justify-center">
                    <svg xmlns="http://www.w3.org/2000/svg" class="w-5 h-5 text-accent-600 dark:text-accent-400" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                      <circle cx="12" cy="12" r="10"/>
                      <path d="M8 12h8"/>
                      <path d="M12 8v8"/>
                    </svg>
                  </div>
                  <span class="font-medium text-neutral-900 dark:text-white">{{ addon.name }}</span>
                </div>
              </td>
              <td class="px-6 py-4">
                <span class="text-sm font-bold text-primary-600 dark:text-primary-400">${{ addon.price.toFixed(2) }}</span>
              </td>
              <td class="px-6 py-4 text-right">
                <div class="flex items-center justify-end gap-2">
                  <button @click="openEditModal(addon)" class="p-2 text-neutral-400 hover:text-primary-600 transition-colors rounded-lg hover:bg-neutral-100 dark:hover:bg-neutral-800">
                    <svg xmlns="http://www.w3.org/2000/svg" class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                      <path d="M17 3a2.828 2.828 0 1 1 4 4L7.5 20.5 2 22l1.5-5.5L17 3z"/>
                    </svg>
                  </button>
                  <button @click="deleteAddOn(addon.id)" class="p-2 text-neutral-400 hover:text-error-600 transition-colors rounded-lg hover:bg-neutral-100 dark:hover:bg-neutral-800">
                    <svg xmlns="http://www.w3.org/2000/svg" class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                      <path d="M3 6h18"/><path d="M19 6v14c0 1-1 2-2 2H7c-1 0-2-1-2-2V6"/><path d="M8 6V4c0-1 1-2 2-2h4c1 0 2 1 2 2v2"/>
                    </svg>
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- Create/Edit Modal -->
      <div v-if="showModal" class="fixed inset-0 z-50 flex items-center justify-center bg-black/50 backdrop-blur-sm p-4">
        <div class="bg-white dark:bg-neutral-900 rounded-2xl shadow-xl w-full max-w-md overflow-hidden border border-neutral-200 dark:border-neutral-800">
          
          <!-- Modal Header -->
          <div class="p-6 border-b border-neutral-200 dark:border-neutral-800 flex justify-between items-center">
              <h3 class="text-xl font-bold text-neutral-900 dark:text-white">
                  {{ isEditing ? 'Edit Add-on' : 'New Add-on' }}
              </h3>
              <button @click="closeModal" class="text-neutral-400 hover:text-neutral-600">
                  <svg xmlns="http://www.w3.org/2000/svg" class="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
              </button>
          </div>

          <!-- Modal Body -->
          <div class="p-6 space-y-4">
              <div>
                   <label class="block text-sm font-medium text-neutral-700 dark:text-neutral-300 mb-1">Name</label>
                   <input type="text" v-model="form.name" class="w-full bg-neutral-50 dark:bg-neutral-800 border-none rounded-lg p-2.5 text-sm ring-1 ring-neutral-200 dark:ring-neutral-700 focus:ring-2 focus:ring-primary-500" placeholder="e.g. Extra Shot">
              </div>
              
              <div>
                   <label class="block text-sm font-medium text-neutral-700 dark:text-neutral-300 mb-1">Price ($)</label>
                   <input type="number" v-model="form.price" class="w-full bg-neutral-50 dark:bg-neutral-800 border-none rounded-lg p-2.5 text-sm ring-1 ring-neutral-200 dark:ring-neutral-700 focus:ring-2 focus:ring-primary-500" step="0.01" min="0" placeholder="0.50">
              </div>
          </div>

          <!-- Modal Footer -->
          <div class="p-6 bg-neutral-50 dark:bg-neutral-800/50 flex justify-end gap-3">
              <button @click="closeModal" class="px-4 py-2 text-sm font-medium text-neutral-600 hover:text-neutral-900 dark:text-neutral-400 dark:hover:text-white transition-colors">Cancel</button>
              <button @click="saveAddOn" class="px-4 py-2 text-sm font-medium bg-primary-600 hover:bg-primary-700 text-white rounded-lg transition-colors">
                  {{ isEditing ? 'Save Changes' : 'Create Add-on' }}
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

interface AddOn {
    id: number
    name: string
    price: number
}

// State
const addOns = ref<AddOn[]>([])
const loading = ref(true)
const searchQuery = ref('')

const showModal = ref(false)
const isEditing = ref(false)
const currentAddOnId = ref<number | null>(null)

// Form Data
const form = reactive({
    name: '',
    price: 0
})

// Computed
const filteredAddOns = computed(() => {
    if (!searchQuery.value) return addOns.value
    
    const q = searchQuery.value.toLowerCase()
    return addOns.value.filter(addon => addon.name.toLowerCase().includes(q))
})

// Fetch Data
const fetchAddOns = async () => {
    loading.value = true
    try {
        const data = await get<AddOn[]>('/addons')
        addOns.value = data || []
    } catch(err) {
        console.error(err)
        toast.error('Failed to load add-ons')
    } finally {
        loading.value = false
    }
}

// Actions
const openCreateModal = () => {
    isEditing.value = false
    currentAddOnId.value = null
    form.name = ''
    form.price = 0
    showModal.value = true
}

const openEditModal = (addon: AddOn) => {
    isEditing.value = true
    currentAddOnId.value = addon.id
    form.name = addon.name
    form.price = addon.price
    showModal.value = true
}

const closeModal = () => {
    showModal.value = false
}

const saveAddOn = async () => {
    if (!form.name.trim()) {
        toast.error('Please enter a name')
        return
    }
    
    try {
        const payload = { name: form.name, price: form.price }
        
        if (isEditing.value && currentAddOnId.value) {
            await put(`/addons/${currentAddOnId.value}`, payload)
            toast.success('Add-on updated')
        } else {
            await post('/addons', payload)
            toast.success('Add-on created')
        }
        
        await fetchAddOns()
        closeModal()
    } catch(err) {
        console.error(err)
        toast.error('Failed to save add-on')
    }
}

const deleteAddOn = async (id: number) => {
    if(!confirm("Are you sure you want to delete this add-on?")) return
    
    try {
        await del(`/addons/${id}`)
        toast.success('Add-on deleted')
        await fetchAddOns()
    } catch(err) {
        console.error(err)
        toast.error('Failed to delete add-on')
    }
}

onMounted(() => {
    fetchAddOns()
})
</script>
