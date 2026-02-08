<template>
  <NuxtLayout name="admin">
    <div class="space-y-6">
      <!-- Header -->
      <UiBreadcrumb :items="[{ label: 'Branches' }]" class="mb-2" />
      <div class="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <h2 class="text-2xl font-bold text-neutral-900 dark:text-white">Branch Management</h2>
          <p class="text-neutral-500 dark:text-neutral-400">Manage your cafe branches and locations</p>
        </div>
        <button
          @click="openAddModal"
          class="btn-primary"
        >
          <svg xmlns="http://www.w3.org/2000/svg" class="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M5 12h14" />
            <path d="M12 5v14" />
          </svg>
          Add Branch
        </button>
      </div>

      <!-- Branch Cards Grid -->
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        <!-- Loading Skeletons -->
        <template v-if="loading">
          <div v-for="i in 3" :key="i" class="card p-6 space-y-4">
            <div class="flex items-start justify-between">
              <div class="space-y-2 flex-1">
                <div class="h-5 w-24 skeleton-shimmer rounded-lg"></div>
                <div class="h-4 w-32 skeleton-shimmer rounded-lg"></div>
              </div>
              <div class="h-10 w-10 skeleton-shimmer rounded-xl"></div>
            </div>
            <div class="space-y-2">
              <div class="h-3 w-full skeleton-shimmer rounded-lg"></div>
              <div class="h-3 w-2/3 skeleton-shimmer rounded-lg"></div>
            </div>
          </div>
        </template>

        <!-- Empty State -->
        <div v-else-if="branches.length === 0" class="col-span-full">
          <div class="card p-12 text-center">
            <div class="w-16 h-16 mx-auto bg-neutral-100 dark:bg-neutral-800 rounded-2xl flex items-center justify-center mb-4">
              <svg xmlns="http://www.w3.org/2000/svg" class="w-8 h-8 text-neutral-400" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                <path d="M3 21h18" />
                <path d="M5 21V7l8-4v18" />
                <path d="M19 21V11l-6-4" />
                <path d="M9 9v.01" />
                <path d="M9 12v.01" />
                <path d="M9 15v.01" />
                <path d="M9 18v.01" />
              </svg>
            </div>
            <h3 class="text-lg font-semibold text-neutral-900 dark:text-white mb-2">No branches yet</h3>
            <p class="text-neutral-500 mb-4">Add your first branch to get started</p>
            <button @click="openAddModal" class="btn-primary">
              Add Branch
            </button>
          </div>
        </div>

        <!-- Branch Cards -->
        <div
          v-for="(branch, index) in branches"
          :key="branch.branchId"
          :class="[
            'card card-hover p-6 animate-hidden animate-slide-up',
            `stagger-${Math.min(index + 1, 5)}`
          ]"
        >
          <div class="flex items-start justify-between mb-4">
            <div>
              <span class="inline-block px-2.5 py-1 bg-primary-100 dark:bg-primary-900/30 text-primary-700 dark:text-primary-400 text-xs font-bold rounded-lg mb-2">
                {{ branch.code }}
              </span>
              <h3 class="text-lg font-semibold text-neutral-900 dark:text-white">{{ branch.name }}</h3>
            </div>
            <div class="flex gap-1">
              <button
                @click="openEditModal(branch)"
                class="p-2 text-neutral-400 hover:text-primary-600 hover:bg-primary-50 dark:hover:bg-primary-900/20 rounded-lg transition-colors"
                title="Edit"
              >
                <svg xmlns="http://www.w3.org/2000/svg" class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M17 3a2.85 2.83 0 1 1 4 4L7.5 20.5 2 22l1.5-5.5Z" />
                </svg>
              </button>
              <button
                @click="confirmDelete(branch)"
                class="p-2 text-neutral-400 hover:text-error-600 hover:bg-error-50 dark:hover:bg-error-900/20 rounded-lg transition-colors"
                title="Delete"
              >
                <svg xmlns="http://www.w3.org/2000/svg" class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M3 6h18" />
                  <path d="M19 6v14c0 1-1 2-2 2H7c-1 0-2-1-2-2V6" />
                  <path d="M8 6V4c0-1 1-2 2-2h4c1 0 2 1 2 2v2" />
                </svg>
              </button>
            </div>
          </div>

          <div class="space-y-2 text-sm text-neutral-600 dark:text-neutral-400">
            <div v-if="branch.location" class="flex items-start gap-2">
              <svg xmlns="http://www.w3.org/2000/svg" class="w-4 h-4 mt-0.5 flex-shrink-0" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M20 10c0 6-8 12-8 12s-8-6-8-12a8 8 0 0 1 16 0Z" />
                <circle cx="12" cy="10" r="3" />
              </svg>
              <span>{{ branch.location }}</span>
            </div>
            <div v-if="branch.phone" class="flex items-center gap-2">
              <svg xmlns="http://www.w3.org/2000/svg" class="w-4 h-4 flex-shrink-0" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07 19.5 19.5 0 0 1-6-6 19.79 19.79 0 0 1-3.07-8.67A2 2 0 0 1 4.11 2h3a2 2 0 0 1 2 1.72 12.84 12.84 0 0 0 .7 2.81 2 2 0 0 1-.45 2.11L8.09 9.91a16 16 0 0 0 6 6l1.27-1.27a2 2 0 0 1 2.11-.45 12.84 12.84 0 0 0 2.81.7A2 2 0 0 1 22 16.92z" />
              </svg>
              <span>{{ branch.phone }}</span>
            </div>
          </div>

          <!-- Geolocation Badge -->
          <div class="mt-4 pt-4 border-t border-neutral-200 dark:border-neutral-700">
            <div v-if="branch.latitude && branch.longitude" class="flex items-center gap-2 text-xs text-success-600 dark:text-success-400">
              <svg xmlns="http://www.w3.org/2000/svg" class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <circle cx="12" cy="12" r="10" />
                <path d="M12 2a14.5 14.5 0 0 0 0 20 14.5 14.5 0 0 0 0-20" />
                <path d="M2 12h20" />
              </svg>
              <span>GPS: {{ branch.latitude.toFixed(4) }}, {{ branch.longitude.toFixed(4) }}</span>
              <span class="text-neutral-400">• {{ branch.radiusMeters || 100 }}m</span>
            </div>
            <div v-else class="flex items-center gap-2 text-xs text-neutral-400">
              <svg xmlns="http://www.w3.org/2000/svg" class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <circle cx="12" cy="12" r="10" />
                <path d="m4.93 4.93 14.14 14.14" />
              </svg>
              <span>No GPS coordinates set</span>
            </div>
          </div>
        </div>
      </div>

      <!-- Add/Edit Modal -->
      <UiModal v-model="showModal" :title="isEditing ? 'Edit Branch' : 'Add Branch'">
        <form @submit.prevent="saveBranch" class="space-y-4">
          <div class="grid grid-cols-2 gap-4">
            <div>
              <label class="label">Branch Code</label>
              <input
                v-model="form.code"
                type="text"
                required
                class="input"
                placeholder="e.g. BR001"
              />
            </div>
            <div>
              <label class="label">Name</label>
              <input
                v-model="form.name"
                type="text"
                required
                class="input"
                placeholder="e.g. Main Street Cafe"
              />
            </div>
          </div>

          <div>
            <label class="label">Location / Address</label>
            <input
              v-model="form.location"
              type="text"
              class="input"
              placeholder="e.g. 123 Main St, City"
            />
          </div>

          <div>
            <label class="label">Phone</label>
            <input
              v-model="form.phone"
              type="tel"
              class="input"
              placeholder="e.g. 012-345-6789"
            />
          </div>

          <!-- Geolocation Section -->
          <div class="pt-4 border-t border-neutral-200 dark:border-neutral-700">
            <div class="flex items-center justify-between mb-3">
              <h4 class="text-sm font-semibold text-neutral-900 dark:text-white">GPS Location</h4>
              <button
                type="button"
                @click="getCurrentLocation"
                :disabled="gettingLocation"
                class="text-xs text-primary-600 hover:text-primary-700 font-medium flex items-center gap-1"
              >
                <svg v-if="gettingLocation" class="w-3 h-3 animate-spin" viewBox="0 0 24 24" fill="none">
                  <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" class="opacity-25"/>
                  <path fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z" class="opacity-75"/>
                </svg>
                <svg v-else xmlns="http://www.w3.org/2000/svg" class="w-3 h-3" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <circle cx="12" cy="12" r="10" />
                  <circle cx="12" cy="12" r="1" />
                </svg>
                {{ gettingLocation ? 'Getting...' : 'Use Current Location' }}
              </button>
            </div>
            <p class="text-xs text-neutral-500 mb-3">Required for mobile check-in geofencing</p>
            <div class="grid grid-cols-3 gap-3">
              <div>
                <label class="text-xs text-neutral-500 mb-1 block">Latitude</label>
                <input
                  v-model.number="form.latitude"
                  type="number"
                  step="0.000001"
                  class="input text-sm py-2"
                  placeholder="11.5564"
                />
              </div>
              <div>
                <label class="text-xs text-neutral-500 mb-1 block">Longitude</label>
                <input
                  v-model.number="form.longitude"
                  type="number"
                  step="0.000001"
                  class="input text-sm py-2"
                  placeholder="104.9282"
                />
              </div>
              <div>
                <label class="text-xs text-neutral-500 mb-1 block">Radius (m)</label>
                <input
                  v-model.number="form.radiusMeters"
                  type="number"
                  min="10"
                  max="1000"
                  class="input text-sm py-2"
                  placeholder="100"
                />
              </div>
            </div>
          </div>

          <div class="flex justify-end gap-3 mt-6">
            <button
              type="button"
              @click="showModal = false"
              class="btn-secondary"
            >
              Cancel
            </button>
            <button
              type="submit"
              :disabled="saving"
              class="btn-primary"
            >
              <span v-if="saving" class="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin"></span>
              {{ isEditing ? 'Save Changes' : 'Create Branch' }}
            </button>
          </div>
        </form>
      </UiModal>

      <!-- Delete Confirmation Modal -->
      <UiModal v-model="showDeleteModal" title="Delete Branch">
        <div class="space-y-4">
          <p class="text-neutral-600 dark:text-neutral-300">
            Are you sure you want to delete <span class="font-bold text-neutral-900 dark:text-white">{{ branchToDelete?.name }}</span>? This action cannot be undone.
          </p>
          <div class="flex justify-end gap-3">
            <button
              @click="showDeleteModal = false"
              class="btn-secondary"
            >
              Cancel
            </button>
            <button
              @click="deleteBranch"
              :disabled="deleting"
              class="btn-danger"
            >
              <span v-if="deleting" class="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin"></span>
              Delete
            </button>
          </div>
        </div>
      </UiModal>
    </div>
  </NuxtLayout>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'

definePageMeta({
  layout: false
})

interface Branch {
  branchId: number
  code: string
  name: string
  location: string
  phone: string
  latitude?: number
  longitude?: number
  radiusMeters?: number
}

const { get, post, put, del } = useApi()

const branches = ref<Branch[]>([])
const loading = ref(true)
const saving = ref(false)
const deleting = ref(false)
const gettingLocation = ref(false)
const showModal = ref(false)
const showDeleteModal = ref(false)
const editId = ref<number | null>(null)
const branchToDelete = ref<Branch | null>(null)

const form = ref({
  code: '',
  name: '',
  location: '',
  phone: '',
  latitude: null as number | null,
  longitude: null as number | null,
  radiusMeters: 100
})

const isEditing = computed(() => !!editId.value)

const fetchBranches = async () => {
  loading.value = true
  try {
    const data = await get<Branch[]>('/branches')
    branches.value = data || []
  } catch (error) {
    console.error('Failed to fetch branches', error)
  } finally {
    loading.value = false
  }
}

const openAddModal = () => {
  editId.value = null
  form.value = { code: '', name: '', location: '', phone: '', latitude: null, longitude: null, radiusMeters: 100 }
  showModal.value = true
}

const openEditModal = (branch: Branch) => {
  editId.value = branch.branchId
  form.value = { 
    code: branch.code,
    name: branch.name,
    location: branch.location || '',
    phone: branch.phone || '',
    latitude: branch.latitude || null,
    longitude: branch.longitude || null,
    radiusMeters: branch.radiusMeters || 100
  }
  showModal.value = true
}

const confirmDelete = (branch: Branch) => {
  branchToDelete.value = branch
  showDeleteModal.value = true
}

const getCurrentLocation = () => {
  if (!navigator.geolocation) {
    alert('Geolocation is not supported by your browser')
    return
  }
  
  gettingLocation.value = true
  navigator.geolocation.getCurrentPosition(
    (position) => {
      form.value.latitude = position.coords.latitude
      form.value.longitude = position.coords.longitude
      gettingLocation.value = false
    },
    (error) => {
      console.error('Geolocation error:', error)
      alert('Unable to get your location. Please enter coordinates manually.')
      gettingLocation.value = false
    },
    { enableHighAccuracy: true, timeout: 10000 }
  )
}

const saveBranch = async () => {
  saving.value = true
  try {
    const payload = {
      ...form.value,
      latitude: form.value.latitude || undefined,
      longitude: form.value.longitude || undefined
    }
    
    if (isEditing.value) {
      await put(`/branches/update/${editId.value}`, payload)
    } else {
      await post('/branches/add', payload)
    }
    await fetchBranches()
    showModal.value = false
  } catch (error) {
    console.error('Failed to save branch', error)
  } finally {
    saving.value = false
  }
}

const deleteBranch = async () => {
  if (!branchToDelete.value) return
  deleting.value = true
  try {
    await put(`/branches/delete/${branchToDelete.value.branchId}`, {})
    await fetchBranches()
    showDeleteModal.value = false
  } catch (error) {
    console.error('Failed to delete branch', error)
  } finally {
    deleting.value = false
    branchToDelete.value = null
  }
}

onMounted(() => {
  fetchBranches()
})
</script>
