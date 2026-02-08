<template>
  <NuxtLayout name="admin">
    <div class="space-y-6">
      <UiBreadcrumb :items="[{ label: 'Inventory', href: '/admin/inventory' }, { label: 'Suppliers' }]" />
      
      <!-- Header -->
      <div class="flex items-center justify-between">
        <div>
          <h2 class="text-2xl font-bold text-neutral-900 dark:text-white">Supplier Management</h2>
          <p class="text-sm text-neutral-500 mt-1">Manage ingredient vendors, contacts, and delivery terms.</p>
        </div>
        <button 
          @click="openModal()"
          class="bg-accent-600 hover:bg-accent-700 text-white px-4 py-2 rounded-xl text-sm font-medium transition-colors flex items-center gap-2 shadow-lg shadow-accent-500/20"
        >
          <svg xmlns="http://www.w3.org/2000/svg" class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><line x1="19" y1="8" x2="19" y2="14"/><line x1="16" y1="11" x2="22" y2="11"/></svg>
          Add Supplier
        </button>
      </div>

      <!-- Stats -->
      <div class="grid grid-cols-1 md:grid-cols-4 gap-6">
          <div class="card p-6 border-l-4 border-l-accent-500">
              <p class="text-[10px] font-black text-neutral-500 uppercase tracking-widest mb-1">Total Suppliers</p>
              <p class="text-3xl font-black text-neutral-900 dark:text-white">{{ suppliers.length }}</p>
          </div>
          <div class="card p-6 border-l-4 border-l-success-500">
              <p class="text-[10px] font-black text-neutral-500 uppercase tracking-widest mb-1">Active Partners</p>
              <p class="text-3xl font-black text-neutral-900 dark:text-white">{{ suppliers.filter(s => s.status === 'ACTIVE').length }}</p>
          </div>
      </div>

      <!-- Table -->
      <div class="card overflow-hidden">
        <div class="overflow-x-auto">
          <table class="w-full text-left">
            <thead>
              <tr class="bg-neutral-50 dark:bg-neutral-800/50 border-b border-neutral-200 dark:border-neutral-700">
                <th class="px-6 py-4 text-xs font-bold text-neutral-500 uppercase">Supplier</th>
                <th class="px-6 py-4 text-xs font-bold text-neutral-500 uppercase">Contact Details</th>
                <th class="px-6 py-4 text-xs font-bold text-neutral-500 uppercase">Location/Address</th>
                <th class="px-6 py-4 text-xs font-bold text-neutral-500 uppercase">Status</th>
                <th class="px-6 py-4 text-right text-xs font-bold text-neutral-500 uppercase">Actions</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-neutral-100 dark:divide-neutral-800">
              <template v-if="loading">
                <tr v-for="i in 5" :key="i" class="animate-pulse">
                   <td v-for="j in 5" :key="j" class="px-6 py-4"><div class="h-4 bg-neutral-100 dark:bg-neutral-800 rounded w-full"></div></td>
                </tr>
              </template>
              <tr v-for="s in suppliers" :key="s.supplierId" class="hover:bg-neutral-50 dark:hover:bg-neutral-800/30 transition-colors">
                <td class="px-6 py-4">
                  <div class="flex items-center gap-3">
                    <div class="w-10 h-10 rounded-xl gradient-accent flex items-center justify-center text-white font-black">
                      {{ s.name.charAt(0) }}
                    </div>
                    <div class="text-sm font-bold text-neutral-900 dark:text-white">{{ s.name }}</div>
                  </div>
                </td>
                <td class="px-6 py-4">
                  <div class="text-sm text-neutral-600 dark:text-neutral-400">{{ s.phone }}</div>
                  <div class="text-xs text-neutral-500">{{ s.email }}</div>
                </td>
                <td class="px-6 py-4">
                  <p class="text-sm text-neutral-600 dark:text-neutral-400 truncate max-w-xs">{{ s.address || 'Global' }}</p>
                </td>
                <td class="px-6 py-4">
                  <span :class="s.status === 'ACTIVE' ? 'bg-success-100 text-success-700 dark:bg-success-900/30 dark:text-success-400' : 'bg-neutral-100 text-neutral-500'" class="text-[10px] px-2 py-0.5 rounded-full font-bold uppercase">
                    {{ s.status }}
                  </span>
                </td>
                <td class="px-6 py-4 text-right">
                  <div class="flex items-center justify-end gap-2 text-neutral-400">
                    <button @click="openModal(s)" class="p-2 hover:text-accent-600 transition-colors">
                      <svg xmlns="http://www.w3.org/2000/svg" class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M17 3a2.828 2.828 0 1 1 4 4L7.5 20.5 2 22l1.5-5.5L17 3z"/></svg>
                    </button>
                    <button @click="deleteSupplier(s.supplierId)" class="p-2 hover:text-error-600 transition-colors">
                      <svg xmlns="http://www.w3.org/2000/svg" class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M3 6h18m-2 0v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/></svg>
                    </button>
                  </div>
                </td>
              </tr>
              <tr v-if="suppliers.length === 0 && !loading">
                  <td colspan="5" class="px-6 py-12 text-center text-neutral-400 italic">No suppliers registered yet.</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <!-- Modal -->
      <div v-if="showModal" class="fixed inset-0 z-[100] flex items-center justify-center bg-black/60 backdrop-blur-sm p-4">
        <div class="bg-white dark:bg-neutral-900 rounded-[40px] shadow-2xl w-full max-w-xl overflow-hidden border border-neutral-200 dark:border-neutral-800 animate-in fade-in zoom-in duration-300">
           <div class="p-8 border-b border-neutral-100 dark:border-neutral-800 flex justify-between items-center bg-neutral-50/50 dark:bg-neutral-800/30">
              <h3 class="text-2xl font-black text-neutral-900 dark:text-white uppercase tracking-tight">{{ editingId ? 'Update Partner' : 'New Supplier' }}</h3>
              <button @click="showModal = false" class="text-neutral-400 hover:text-neutral-600 transition-colors">
                  <svg xmlns="http://www.w3.org/2000/svg" class="w-6 h-6" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
              </button>
           </div>
           
           <form @submit.prevent="saveSupplier" class="p-8 space-y-6">
              <div class="grid grid-cols-2 gap-6">
                  <div class="col-span-2">
                      <label class="text-[10px] font-black text-neutral-400 uppercase tracking-widest ml-1 mb-2 block">Company Name</label>
                      <input v-model="form.name" type="text" required class="input-modern w-full" placeholder="e.g. Arabica Roast Co." />
                  </div>
                  <div>
                      <label class="text-[10px] font-black text-neutral-400 uppercase tracking-widest ml-1 mb-2 block">Phone Number</label>
                      <input v-model="form.phone" type="text" required class="input-modern w-full" placeholder="+855 ..." />
                  </div>
                  <div>
                      <label class="text-[10px] font-black text-neutral-400 uppercase tracking-widest ml-1 mb-2 block">Email Address</label>
                      <input v-model="form.email" type="email" required class="input-modern w-full" placeholder="vendor@example.com" />
                  </div>
                  <div>
                      <label class="text-[10px] font-black text-neutral-400 uppercase tracking-widest ml-1 mb-2 block">Status</label>
                      <select v-model="form.status" class="input-modern w-full">
                          <option value="ACTIVE">Active</option>
                          <option value="INACTIVE">Inactive</option>
                      </select>
                  </div>
                  <div>
                      <label class="text-[10px] font-black text-neutral-400 uppercase tracking-widest ml-1 mb-2 block">Contact Person Gender</label>
                      <select v-model="form.gender" class="input-modern w-full">
                        <option value="Mr">Mr</option>
                        <option value="Ms">Ms</option>
                        <option value="Other">Other</option>
                    </select>
                  </div>
                  <div class="col-span-2">
                       <label class="text-[10px] font-black text-neutral-400 uppercase tracking-widest ml-1 mb-2 block">Base Office / Address</label>
                       <textarea v-model="form.address" class="input-modern w-full" rows="2" placeholder="Full business address..."></textarea>
                  </div>
                  <div class="col-span-2">
                       <label class="text-[10px] font-black text-neutral-400 uppercase tracking-widest ml-1 mb-2 block">Terms / Notes</label>
                       <textarea v-model="form.notes" class="input-modern w-full" rows="2" placeholder="Delivery terms, lead times, etc."></textarea>
                  </div>
              </div>

              <div class="flex gap-4 pt-4">
                  <button type="button" @click="showModal = false" class="flex-1 py-4 text-sm font-bold text-neutral-500 hover:text-neutral-800 dark:hover:text-white transition-colors">Cancel</button>
                  <button type="submit" class="flex-1 btn-accent py-4 rounded-3xl font-black uppercase tracking-widest shadow-xl shadow-accent-500/20" :disabled="saving">
                     {{ saving ? 'Saving...' : (editingId ? 'Update Partner' : 'Register Supplier') }}
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

interface Supplier {
    supplierId: number
    name: string
    phone: string
    email: string
    gender: string
    address: string
    notes: string
    status: string
}

const suppliers = ref<Supplier[]>([])
const loading = ref(true)
const saving = ref(false)
const showModal = ref(false)
const editingId = ref<number | null>(null)

const form = reactive({
    name: '',
    phone: '',
    email: '',
    gender: 'Mr',
    address: '',
    notes: '',
    status: 'ACTIVE'
})

const fetchSuppliers = async () => {
    loading.value = true
    try {
        const data = await get<Supplier[]>('/suppliers')
        suppliers.value = data || []
    } catch (e) {
        console.error(e)
    } finally {
        loading.value = false
    }
}

const openModal = (supplier: Supplier | null = null) => {
    if (supplier) {
        editingId.value = supplier.supplierId
        form.name = supplier.name
        form.phone = supplier.phone
        form.email = supplier.email
        form.gender = supplier.gender
        form.address = supplier.address
        form.notes = supplier.notes
        form.status = supplier.status
    } else {
        editingId.value = null
        form.name = ''
        form.phone = ''
        form.email = ''
        form.gender = 'Mr'
        form.address = ''
        form.notes = ''
        form.status = 'ACTIVE'
    }
    showModal.value = true
}

const saveSupplier = async () => {
    saving.value = true
    try {
        if (editingId.value) {
            await put(`/suppliers/${editingId.value}`, form)
            toast.success('Supplier partner updated')
        } else {
            await post('/suppliers', form)
            toast.success('New supplier registered')
        }
        showModal.value = false
        fetchSuppliers()
    } catch (err) {
        toast.error('Failed to save supplier details')
    } finally {
        saving.value = false
    }
}

const deleteSupplier = async (id: number) => {
    if(!confirm('Are you sure you want to delete this supplier?')) return
    try {
        await del(`/suppliers/${id}`)
        toast.success('Supplier removed')
        fetchSuppliers()
    } catch (e) {
        toast.error('Failed to delete supplier')
    }
}

onMounted(fetchSuppliers)
</script>

<style scoped>
.input-modern {
  @apply bg-neutral-100 dark:bg-neutral-800 border-none rounded-2xl px-5 py-4 text-sm ring-1 ring-neutral-200 dark:ring-neutral-700 focus:ring-2 focus:ring-accent-500 placeholder-neutral-400 transition-all text-neutral-900 dark:text-white;
}
.btn-accent {
  @apply bg-accent-600 hover:bg-accent-700 text-white rounded-3xl px-6 py-3 transition-all active:scale-95 disabled:opacity-50 font-bold text-sm shadow-lg shadow-accent-500/20;
}
.gradient-accent {
  @apply bg-gradient-to-br from-accent-500 to-accent-700;
}
</style>
