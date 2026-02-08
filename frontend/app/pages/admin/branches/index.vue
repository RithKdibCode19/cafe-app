<template>
  <NuxtLayout name="admin">
    <div class="space-y-6">
      <UiBreadcrumb :items="[{ label: 'Branches' }]" />
      
      <!-- Header -->
      <div class="flex items-center justify-between">
        <div>
          <h2 class="text-2xl font-bold text-neutral-900 dark:text-white">Branch Management</h2>
          <p class="text-sm text-neutral-500 mt-1">Manage physical cafe locations and their contact details.</p>
        </div>
        <button 
          @click="openCreateModal"
          class="bg-primary-600 hover:bg-primary-700 text-white px-4 py-2 rounded-xl text-sm font-medium transition-colors flex items-center gap-2 shadow-lg shadow-primary-500/20"
        >
          <svg xmlns="http://www.w3.org/2000/svg" class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M5 12h14M12 5v14"/></svg>
          Add Branch
        </button>
      </div>

      <!-- Stats -->
      <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
          <div class="bg-white dark:bg-neutral-900 p-6 rounded-2xl border border-neutral-200 dark:border-neutral-800 shadow-sm transition-transform hover:-translate-y-1">
              <p class="text-[10px] font-black text-neutral-500 uppercase tracking-widest mb-1">Total Branches</p>
              <p class="text-3xl font-black text-primary-600">{{ branches.length }}</p>
          </div>
          <div class="bg-white dark:bg-neutral-900 p-6 rounded-2xl border border-neutral-200 dark:border-neutral-800 shadow-sm transition-transform hover:-translate-y-1">
              <p class="text-[10px] font-black text-neutral-500 uppercase tracking-widest mb-1">Operating Status</p>
              <p class="text-3xl font-black text-success-600">Active</p>
          </div>
          <div class="bg-white dark:bg-neutral-900 p-6 rounded-2xl border border-neutral-200 dark:border-neutral-800 shadow-sm transition-transform hover:-translate-y-1">
              <p class="text-[10px] font-black text-neutral-500 uppercase tracking-widest mb-1">Regional Scope</p>
              <p class="text-3xl font-black text-accent-600">Local</p>
          </div>
      </div>

      <!-- Table -->
      <div class="bg-white dark:bg-neutral-900 border border-neutral-200 dark:border-neutral-800 rounded-3xl overflow-hidden shadow-sm">
        <div class="overflow-x-auto">
          <table class="w-full">
            <thead>
              <tr class="bg-neutral-50 dark:bg-neutral-800/50 border-b border-neutral-200 dark:border-neutral-800">
                <th class="px-6 py-4 text-left text-[10px] font-black text-neutral-400 uppercase tracking-widest">Code</th>
                <th class="px-6 py-4 text-left text-[10px] font-black text-neutral-400 uppercase tracking-widest">Branch Name</th>
                <th class="px-6 py-4 text-left text-[10px] font-black text-neutral-400 uppercase tracking-widest">Phone</th>
                <th class="px-6 py-4 text-left text-[10px] font-black text-neutral-400 uppercase tracking-widest">Location</th>
                <th class="px-6 py-4 text-right text-[10px] font-black text-neutral-400 uppercase tracking-widest">Actions</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-neutral-100 dark:divide-neutral-800">
              <template v-if="loading">
                <tr v-for="i in 3" :key="i" class="animate-pulse">
                   <td v-for="j in 5" :key="j" class="px-6 py-4"><div class="h-4 bg-neutral-100 dark:bg-neutral-800 rounded"></div></td>
                </tr>
              </template>
              <tr v-for="branch in branches" :key="branch.branchId" class="hover:bg-neutral-50/50 dark:hover:bg-neutral-800/30 transition-colors group">
                <td class="px-6 py-4">
                  <span class="px-2 py-1 rounded bg-neutral-100 dark:bg-neutral-800 text-[10px] font-bold text-neutral-600 dark:text-neutral-400 font-mono">
                    {{ branch.code }}
                  </span>
                </td>
                <td class="px-6 py-4">
                  <div class="text-sm font-bold text-neutral-900 dark:text-white">{{ branch.name }}</div>
                </td>
                <td class="px-6 py-4">
                  <div class="text-sm text-neutral-600 dark:text-neutral-400">{{ branch.phone }}</div>
                </td>
                <td class="px-6 py-4">
                  <div class="text-sm text-neutral-600 dark:text-neutral-400 truncate max-w-xs">{{ branch.location }}</div>
                </td>
                <td class="px-6 py-4 text-right">
                  <div class="flex items-center justify-end gap-2">
                    <button @click="editBranch(branch)" class="p-2 text-neutral-300 hover:text-primary-600 transition-colors">
                        <svg xmlns="http://www.w3.org/2000/svg" class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M17 3a2.828 2.828 0 1 1 4 4L7.5 20.5 2 22l1.5-5.5L17 3z"/></svg>
                    </button>
                    <button @click="deleteBranch(branch.branchId)" class="p-2 text-neutral-300 hover:text-error-600 transition-colors">
                        <svg xmlns="http://www.w3.org/2000/svg" class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M3 6h18M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/></svg>
                    </button>
                  </div>
                </td>
              </tr>
              <tr v-if="branches.length === 0 && !loading">
                  <td colspan="5" class="px-6 py-12 text-center text-neutral-400 italic">No branches found.</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <!-- Modal -->
      <div v-if="showModal" class="fixed inset-0 z-[100] flex items-center justify-center bg-black/60 backdrop-blur-sm p-4">
           <div class="bg-white dark:bg-neutral-900 rounded-[40px] shadow-2xl w-full max-w-lg overflow-hidden border border-neutral-200 dark:border-neutral-800 animate-in fade-in zoom-in duration-300">
              <div class="p-8 border-b border-neutral-100 dark:border-neutral-800 flex justify-between items-center bg-neutral-50/50 dark:bg-neutral-800/30">
                 <h3 class="text-2xl font-black text-neutral-900 dark:text-white uppercase tracking-tight">{{ editingBranchId ? 'Update Branch' : 'New Branch' }}</h3>
                 <button @click="showModal = false" class="text-neutral-400 hover:text-neutral-600 transition-colors">
                     <svg xmlns="http://www.w3.org/2000/svg" class="w-6 h-6" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
                 </button>
              </div>
              
              <form @submit.prevent="saveBranch" class="p-8 space-y-6">
                  <div class="grid grid-cols-2 gap-4">
                      <div>
                          <label class="text-[10px] font-black text-neutral-400 uppercase tracking-widest ml-1 mb-2 block">Branch Code</label>
                          <input v-model="form.code" type="text" required class="input-modern w-full" placeholder="e.g. BR001" />
                      </div>
                      <div>
                          <label class="text-[10px] font-black text-neutral-400 uppercase tracking-widest ml-1 mb-2 block">Branch Name</label>
                          <input v-model="form.name" type="text" required class="input-modern w-full" placeholder="e.g. Main Street" />
                      </div>
                      <div class="col-span-2">
                          <label class="text-[10px] font-black text-neutral-400 uppercase tracking-widest ml-1 mb-2 block">Phone Number</label>
                          <input v-model="form.phone" type="text" required class="input-modern w-full" placeholder="+855 ..." />
                      </div>
                      <div class="col-span-2">
                           <label class="text-[10px] font-black text-neutral-400 uppercase tracking-widest ml-1 mb-2 block">Location Address</label>
                           <textarea v-model="form.location" class="input-modern w-full" rows="3" placeholder="Full address..."></textarea>
                      </div>
                  </div>

                  <div class="flex gap-4 pt-4">
                      <button type="button" @click="showModal = false" class="flex-1 py-4 text-sm font-bold text-neutral-500 hover:text-neutral-800 dark:hover:text-white transition-colors">Cancel</button>
                      <button type="submit" class="flex-1 btn-primary py-4 rounded-2xl font-black uppercase tracking-widest shadow-xl shadow-primary-500/20" :disabled="saving">
                         {{ saving ? 'Saving...' : 'Save Branch' }}
                      </button>
                  </div>
              </form>
           </div>
      </div>
    </div>
  </NuxtLayout>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'

definePageMeta({
  layout: false
})
const { get, post, put, del } = useApi()
const toast = useToast()

interface Branch {
    branchId: number
    code: string
    name: string
    phone: string
    location: string
}

const branches = ref<Branch[]>([])
const loading = ref(true)
const saving = ref(false)
const showModal = ref(false)
const editingBranchId = ref<number | null>(null)

const form = reactive({
    code: '',
    name: '',
    phone: '',
    location: ''
})

const fetchBranches = async () => {
    loading.value = true
    try {
        const data = await get<Branch[]>('/branches')
        branches.value = data || []
    } catch (e) {
        console.error(e)
    } finally {
        loading.value = false
    }
}

const openCreateModal = () => {
    editingBranchId.value = null
    form.code = ''
    form.name = ''
    form.phone = ''
    form.location = ''
    showModal.value = true
}

const editBranch = (branch: Branch) => {
    editingBranchId.value = branch.branchId
    form.code = branch.code
    form.name = branch.name
    form.phone = branch.phone
    form.location = branch.location
    showModal.value = true
}

const saveBranch = async () => {
    saving.value = true
    try {
        if (editingBranchId.value) {
            await put(`/branches/update/${editingBranchId.value}`, form)
            toast.success('Branch updated successfully')
        } else {
            await post('/branches/add', form)
            toast.success('Branch added successfully')
        }
        showModal.value = false
        fetchBranches()
    } catch (err) {
        toast.error('Failed to save branch')
    } finally {
        saving.value = false
    }
}

const deleteBranch = async (id: number) => {
    if (!confirm('Are you sure you want to delete this branch?')) return
    try {
        // Backend uses PUT /delete/{id}
        await put(`/branches/delete/${id}`, {})
        toast.success('Branch deleted successfully')
        fetchBranches()
    } catch (e) {
        toast.error('Failed to delete branch')
    }
}

onMounted(fetchBranches)
</script>

<style scoped>
.input-modern {
  @apply bg-neutral-100 dark:bg-neutral-800 border-none rounded-2xl px-5 py-4 text-sm ring-1 ring-neutral-200 dark:ring-neutral-700 focus:ring-2 focus:ring-primary-500 placeholder-neutral-400 transition-all text-neutral-900 dark:text-white;
}
.btn-primary {
  @apply bg-primary-600 hover:bg-primary-700 text-white rounded-2xl px-6 py-3 transition-all active:scale-95 disabled:opacity-50 font-bold text-sm shadow-lg shadow-primary-500/20;
}
</style>
