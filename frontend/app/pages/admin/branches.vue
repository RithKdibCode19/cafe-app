<template>
  <NuxtLayout name="admin">
    <div class="space-y-6">
      <!-- Header -->
      <div class="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <h2 class="text-2xl font-bold text-neutral-900 dark:text-white">Branch Management</h2>
          <p class="text-neutral-500 dark:text-neutral-400">Manage your cafe branches and locations</p>
        </div>
        <button
          @click="openAddModal"
          class="flex items-center gap-2 px-4 py-2 bg-primary-600 hover:bg-primary-700 text-white rounded-xl font-medium transition-colors"
        >
          <svg xmlns="http://www.w3.org/2000/svg" class="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M5 12h14" />
            <path d="M12 5v14" />
          </svg>
          Add Branch
        </button>
      </div>

      <!-- Branch List -->
      <div class="bg-white dark:bg-neutral-900 rounded-2xl border border-neutral-200 dark:border-neutral-800 overflow-hidden">
        <div class="overflow-x-auto">
          <table class="w-full text-left border-collapse">
            <thead>
              <tr class="border-b border-neutral-200 dark:border-neutral-800 bg-neutral-50 dark:bg-neutral-900/50">
                <th class="p-4 font-semibold text-neutral-600 dark:text-neutral-400 text-sm">Branch Code</th>
                <th class="p-4 font-semibold text-neutral-600 dark:text-neutral-400 text-sm">Name</th>
                <th class="p-4 font-semibold text-neutral-600 dark:text-neutral-400 text-sm">Location</th>
                <th class="p-4 font-semibold text-neutral-600 dark:text-neutral-400 text-sm">Phone</th>
                <th class="p-4 font-semibold text-neutral-600 dark:text-neutral-400 text-sm text-right">Actions</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-neutral-200 dark:divide-neutral-800">
              <tr v-if="loading" class="animate-pulse">
                  <td colspan="5" class="p-4 text-center text-neutral-500">Loading branches...</td>
              </tr>
              <tr v-else-if="branches.length === 0">
                  <td colspan="5" class="p-8 text-center text-neutral-500">No branches found. Add one to get started.</td>
              </tr>
              <tr
                v-for="branch in branches"
                :key="branch.branchId"
                class="group hover:bg-neutral-50 dark:hover:bg-neutral-800/50 transition-colors"
              >
                <td class="p-4 font-medium text-neutral-900 dark:text-white">{{ branch.code }}</td>
                <td class="p-4 text-neutral-600 dark:text-neutral-300">{{ branch.name }}</td>
                <td class="p-4 text-neutral-600 dark:text-neutral-300">{{ branch.location }}</td>
                <td class="p-4 text-neutral-600 dark:text-neutral-300">{{ branch.phone }}</td>
                <td class="p-4 flex items-center justify-end gap-2">
                  <button
                    @click="openEditModal(branch)"
                    class="p-2 text-neutral-400 hover:text-primary-600 hover:bg-primary-50 dark:hover:bg-primary-900/20 rounded-lg transition-colors"
                    title="Edit"
                  >
                    <svg xmlns="http://www.w3.org/2000/svg" class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                      <path d="M17 3a2.85 2.83 0 1 1 4 4L7.5 20.5 2 22l1.5-5.5Z" />
                      <path d="m15 5 4 4" />
                    </svg>
                  </button>
                  <button
                    @click="confirmDelete(branch)"
                    class="p-2 text-neutral-400 hover:text-error-600 hover:bg-error-50 dark:hover:bg-error-900/20 rounded-lg transition-colors"
                    title="Delete"
                  >
                    <svg xmlns="http://www.w3.org/2000/svg" class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                      <path d="M3 6h18" />
                      <path d="M19 6v14c0 1-1 2-2 2H7c-1 0-2-1-2-2V6" />
                      <path d="M8 6V4c0-1 1-2 2-2h4c1 0 2 1 2 2v2" />
                    </svg>
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <!-- Modal -->
      <UiModal v-model="showModal" :title="isEditing ? 'Edit Branch' : 'Add Branch'">
        <form @submit.prevent="saveBranch" class="space-y-4">
          <div>
            <label class="block text-sm font-medium text-neutral-700 dark:text-neutral-300 mb-1">Branch Code</label>
            <input
              v-model="form.code"
              type="text"
              required
              class="w-full px-4 py-2 bg-white dark:bg-neutral-900 border border-neutral-200 dark:border-neutral-700 rounded-xl focus:border-primary-500 focus:ring-2 focus:ring-primary-500/20 outline-none transition-all"
              placeholder="e.g. BR001"
            />
          </div>
          <div>
              <label class="block text-sm font-medium text-neutral-700 dark:text-neutral-300 mb-1">Name</label>
              <input
                v-model="form.name"
                type="text"
                required
                class="w-full px-4 py-2 bg-white dark:bg-neutral-900 border border-neutral-200 dark:border-neutral-700 rounded-xl focus:border-primary-500 focus:ring-2 focus:ring-primary-500/20 outline-none transition-all"
                placeholder="e.g. Main Street Cafe"
              />
            </div>
          <div>
            <label class="block text-sm font-medium text-neutral-700 dark:text-neutral-300 mb-1">Location</label>
            <input
              v-model="form.location"
              type="text"
              class="w-full px-4 py-2 bg-white dark:bg-neutral-900 border border-neutral-200 dark:border-neutral-700 rounded-xl focus:border-primary-500 focus:ring-2 focus:ring-primary-500/20 outline-none transition-all"
              placeholder="e.g. 123 Main St"
            />
          </div>
          <div>
            <label class="block text-sm font-medium text-neutral-700 dark:text-neutral-300 mb-1">Phone</label>
            <input
              v-model="form.phone"
              type="tel"
              class="w-full px-4 py-2 bg-white dark:bg-neutral-900 border border-neutral-200 dark:border-neutral-700 rounded-xl focus:border-primary-500 focus:ring-2 focus:ring-primary-500/20 outline-none transition-all"
              placeholder="e.g. 012-345-6789"
            />
          </div>

          <div class="flex justify-end gap-3 mt-6">
            <button
              type="button"
              @click="showModal = false"
              class="px-4 py-2 text-neutral-600 dark:text-neutral-300 font-medium hover:bg-neutral-100 dark:hover:bg-neutral-800 rounded-xl transition-colors"
            >
              Cancel
            </button>
            <button
              type="submit"
              :disabled="saving"
              class="px-4 py-2 bg-primary-600 hover:bg-primary-700 text-white rounded-xl font-medium transition-colors disabled:opacity-50 disabled:cursor-not-allowed flex items-center gap-2"
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
                      class="px-4 py-2 text-neutral-600 dark:text-neutral-300 font-medium hover:bg-neutral-100 dark:hover:bg-neutral-800 rounded-xl transition-colors"
                  >
                      Cancel
                  </button>
                  <button
                      @click="deleteBranch"
                      :disabled="deleting"
                      class="px-4 py-2 bg-error-600 hover:bg-error-700 text-white rounded-xl font-medium transition-colors disabled:opacity-50 disabled:cursor-not-allowed flex items-center gap-2"
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
}

const { get, post, put, del } = useApi()

const branches = ref<Branch[]>([])
const loading = ref(true)
const saving = ref(false)
const deleting = ref(false)
const showModal = ref(false)
const showDeleteModal = ref(false)
const editId = ref<number | null>(null)
const branchToDelete = ref<Branch | null>(null)

const form = ref({
  code: '',
  name: '',
  location: '',
  phone: ''
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
  form.value = { code: '', name: '', location: '', phone: '' }
  showModal.value = true
}

const openEditModal = (branch: Branch) => {
  editId.value = branch.branchId
  form.value = { ...branch }
  showModal.value = true
}

const confirmDelete = (branch: Branch) => {
    branchToDelete.value = branch
    showDeleteModal.value = true
}

const saveBranch = async () => {
  saving.value = true
  try {
    if (isEditing.value) {
      await put(`/branches/update/${editId.value}`, form.value)
    } else {
      await post('/branches/add', form.value)
    }
    await fetchBranches()
    showModal.value = false
  } catch (error) {
    console.error('Failed to save branch', error)
    // Here you might want to show a toast notification
  } finally {
    saving.value = false
  }
}

const deleteBranch = async () => {
    if(!branchToDelete.value) return
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
