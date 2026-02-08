<template>
  <NuxtLayout name="admin">
    <div class="space-y-6">
      <UiBreadcrumb :items="[{ label: 'Expenses' }]" />
      
      <!-- Header -->
      <div class="flex items-center justify-between">
        <div>
          <h2 class="text-2xl font-bold text-neutral-900 dark:text-white">Expense Tracking</h2>
          <p class="text-sm text-neutral-500 mt-1">Monitor daily operations costs and utility bills.</p>
        </div>
        <button 
          @click="showModal = true"
          class="bg-error-600 hover:bg-error-700 text-white px-4 py-2 rounded-xl text-sm font-medium transition-colors flex items-center gap-2 shadow-lg shadow-error-500/20"
        >
          <svg xmlns="http://www.w3.org/2000/svg" class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 5v14M5 12h14"/></svg>
          New Expense
        </button>
      </div>

      <!-- Stats -->
      <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
          <div class="bg-white dark:bg-neutral-900 p-6 rounded-2xl border border-neutral-200 dark:border-neutral-800 shadow-sm transition-transform hover:-translate-y-1">
              <p class="text-[10px] font-black text-neutral-500 uppercase tracking-widest mb-1">Total Expenses (MTD)</p>
              <p class="text-3xl font-black text-error-600">${{ totalExpenses.toFixed(2) }}</p>
          </div>
          <div class="bg-white dark:bg-neutral-900 p-6 rounded-2xl border border-neutral-200 dark:border-neutral-800 shadow-sm transition-transform hover:-translate-y-1">
              <p class="text-[10px] font-black text-neutral-500 uppercase tracking-widest mb-1">Largest Category</p>
              <p class="text-3xl font-black text-neutral-900 dark:text-white">{{ topCategory || '---' }}</p>
          </div>
          <div class="bg-white dark:bg-neutral-900 p-6 rounded-2xl border border-neutral-200 dark:border-neutral-800 shadow-sm transition-transform hover:-translate-y-1">
              <p class="text-[10px] font-black text-neutral-500 uppercase tracking-widest mb-1">Bills Pending</p>
              <p class="text-3xl font-black text-warning-500">0</p>
          </div>
      </div>

      <!-- Table -->
      <div class="bg-white dark:bg-neutral-900 border border-neutral-200 dark:border-neutral-800 rounded-3xl overflow-hidden shadow-sm">
        <div class="overflow-x-auto">
          <table class="w-full">
            <thead>
              <tr class="bg-neutral-50 dark:bg-neutral-800/50 border-b border-neutral-200 dark:border-neutral-800">
                <th class="px-6 py-4 text-left text-[10px] font-black text-neutral-400 uppercase tracking-widest">Date</th>
                <th class="px-6 py-4 text-left text-[10px] font-black text-neutral-400 uppercase tracking-widest">Expense / Title</th>
                <th class="px-6 py-4 text-left text-[10px] font-black text-neutral-400 uppercase tracking-widest">Category</th>
                <th class="px-6 py-4 text-left text-[10px] font-black text-neutral-400 uppercase tracking-widest">Amount</th>
                <th class="px-6 py-4 text-right text-[10px] font-black text-neutral-400 uppercase tracking-widest">Actions</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-neutral-100 dark:divide-neutral-800">
              <template v-if="loading">
                <tr v-for="i in 5" :key="i" class="animate-pulse">
                   <td v-for="j in 5" :key="j" class="px-6 py-4"><div class="h-4 bg-neutral-100 dark:bg-neutral-800 rounded"></div></td>
                </tr>
              </template>
              <tr v-for="item in expenses" :key="item.expenseId" class="hover:bg-neutral-50/50 dark:hover:bg-neutral-800/30 transition-colors group">
                <td class="px-6 py-4">
                  <div class="text-sm font-bold text-neutral-900 dark:text-white">{{ formatDate(item.date) }}</div>
                </td>
                <td class="px-6 py-4">
                  <div class="text-sm font-bold text-neutral-900 dark:text-white">{{ item.title }}</div>
                  <div class="text-[10px] text-neutral-500">{{ item.description || 'No description' }}</div>
                </td>
                <td class="px-6 py-4">
                  <span class="px-2 py-1 rounded-md bg-neutral-100 dark:bg-neutral-800 text-[10px] font-bold text-neutral-600 dark:text-neutral-400 uppercase tracking-wider">
                    {{ item.category }}
                  </span>
                </td>
                <td class="px-6 py-4 font-black text-error-600">
                  ${{ item.amount.toFixed(2) }}
                </td>
                <td class="px-6 py-4 text-right">
                  <button @click="deleteExpense(item.expenseId)" class="p-2 text-neutral-300 hover:text-error-600 transition-colors">
                      <svg xmlns="http://www.w3.org/2000/svg" class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M3 6h18M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/></svg>
                  </button>
                </td>
              </tr>
              <tr v-if="expenses.length === 0 && !loading">
                  <td colspan="5" class="px-6 py-12 text-center text-neutral-400 italic">No expenses recorded yet.</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <!-- Modal -->
      <div v-if="showModal" class="fixed inset-0 z-[100] flex items-center justify-center bg-black/60 backdrop-blur-sm p-4">
           <div class="bg-white dark:bg-neutral-900 rounded-[40px] shadow-2xl w-full max-w-lg overflow-hidden border border-neutral-200 dark:border-neutral-800 animate-in fade-in zoom-in duration-300">
              <div class="p-8 border-b border-neutral-100 dark:border-neutral-800 flex justify-between items-center bg-neutral-50/50 dark:bg-neutral-800/30">
                 <h3 class="text-2xl font-black text-neutral-900 dark:text-white uppercase tracking-tight">Record Expense</h3>
                 <button @click="showModal = false" class="text-neutral-400 hover:text-neutral-600 transition-colors">
                     <svg xmlns="http://www.w3.org/2000/svg" class="w-6 h-6" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
                 </button>
              </div>
              
              <form @submit.prevent="saveExpense" class="p-8 space-y-6">
                  <div class="grid grid-cols-2 gap-4">
                      <div class="col-span-2">
                          <label class="text-[10px] font-black text-neutral-400 uppercase tracking-widest ml-1 mb-2 block">Title / Payee</label>
                          <input v-model="form.title" type="text" required class="input-modern w-full" placeholder="e.g. Electric Bill Jan 2024" />
                      </div>
                      <div>
                          <label class="text-[10px] font-black text-neutral-400 uppercase tracking-widest ml-1 mb-2 block">Amount ($)</label>
                          <input v-model="form.amount" type="number" step="0.01" required class="input-modern w-full" placeholder="0.00" />
                      </div>
                      <div>
                          <label class="text-[10px] font-black text-neutral-400 uppercase tracking-widest ml-1 mb-2 block">Category</label>
                          <select v-model="form.category" class="input-modern w-full">
                              <option value="UTILITIES">Utilities</option>
                              <option value="MARKETING">Marketing</option>
                              <option value="RAPP">Rent</option>
                              <option value="SALARY">Salary</option>
                              <option value="SUPPLIES">Supplies</option>
                              <option value="OTHER">Other</option>
                          </select>
                      </div>
                      <div class="col-span-2">
                           <label class="text-[10px] font-black text-neutral-400 uppercase tracking-widest ml-1 mb-2 block">Notes</label>
                           <textarea v-model="form.description" class="input-modern w-full" rows="3" placeholder="Additional details..."></textarea>
                      </div>
                  </div>

                  <div class="flex gap-4 pt-4">
                      <button type="button" @click="showModal = false" class="flex-1 py-4 text-sm font-bold text-neutral-500 hover:text-neutral-800 dark:hover:text-white transition-colors">Cancel</button>
                      <button type="submit" class="flex-1 btn-error py-4 rounded-2xl font-black uppercase tracking-widest shadow-xl shadow-error-500/20" :disabled="saving">
                         {{ saving ? 'Saving...' : 'Log Expense' }}
                      </button>
                  </div>
              </form>
           </div>
      </div>
    </div>
  </NuxtLayout>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'

definePageMeta({
  layout: false
})
const { get, post, del } = useApi()
const toast = useToast()

interface Expense {
    expenseId: number
    title: string
    description: string
    amount: number
    date: string
    category: string
}

const expenses = ref<Expense[]>([])
const loading = ref(true)
const saving = ref(false)
const showModal = ref(false)

const form = reactive({
    title: '',
    description: '',
    amount: null as number | null,
    category: 'UTILITIES',
    branchId: 1, // Default
    recordedById: 1 // Default
})

const totalExpenses = computed(() => expenses.value.reduce((s, e) => s + e.amount, 0))
const topCategory = computed(() => {
    if (expenses.value.length === 0) return ''
    const cats: any = {}
    expenses.value.forEach(e => cats[e.category] = (cats[e.category] || 0) + e.amount)
    return Object.keys(cats).reduce((a, b) => cats[a] > cats[b] ? a : b)
})

const fetchExpenses = async () => {
    loading.value = true
    try {
        const data = await get<Expense[]>('/expenses')
        expenses.value = data || []
    } catch (e) {
        console.error(e)
    } finally {
        loading.value = false
    }
}

const saveExpense = async () => {
    saving.value = true
    try {
        await post('/expenses', form)
        showModal.value = false
        toast.success('Expense recorded successfully')
        fetchExpenses()
        // Reset form
        form.title = ''
        form.description = ''
        form.amount = null
    } catch (err) {
        toast.error('Failed to save expense')
    } finally {
        saving.value = false
    }
}

const deleteExpense = async (id: number) => {
    if (!confirm('Delete this expense?')) return
    try {
        await del(`/expenses/${id}`)
        toast.success('Expense deleted')
        fetchExpenses()
    } catch (e) {
        toast.error('Delete failed')
    }
}

const formatDate = (d: string) => new Date(d).toLocaleDateString()

onMounted(fetchExpenses)
</script>

<style scoped>
.input-modern {
  @apply bg-neutral-100 dark:bg-neutral-800 border-none rounded-2xl px-5 py-4 text-sm ring-1 ring-neutral-200 dark:ring-neutral-700 focus:ring-2 focus:ring-primary-500 placeholder-neutral-400 transition-all text-neutral-900 dark:text-white;
}
.btn-error {
  @apply bg-error-600 hover:bg-error-700 text-white rounded-2xl px-6 py-3 transition-all active:scale-95 disabled:opacity-50 font-bold text-sm shadow-lg shadow-error-500/20;
}
</style>
