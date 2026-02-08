<template>
  <NuxtLayout name="admin">
    <div class="space-y-6">
      <UiBreadcrumb :items="[{ label: 'Reports', href: '/admin/reports' }, { label: 'Staff Productivity' }]" />
      
      <!-- Header -->
      <div class="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <h1 class="text-2xl font-bold text-neutral-900 dark:text-white">Staff Productivity</h1>
          <p class="text-neutral-500 dark:text-neutral-400">Monitor employee performance</p>
        </div>
        
        <div class="flex items-center gap-2">
          <input type="date" v-model="startDate" class="bg-white dark:bg-neutral-800 border border-neutral-200 dark:border-neutral-700 rounded-xl px-4 py-2 text-sm" />
          <span class="text-neutral-400">to</span>
          <input type="date" v-model="endDate" class="bg-white dark:bg-neutral-800 border border-neutral-200 dark:border-neutral-700 rounded-xl px-4 py-2 text-sm" />
          <button @click="fetchReport" class="btn-primary" :disabled="loading">Update</button>
        </div>
      </div>

      <div v-if="loading" class="space-y-6">
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
           <div v-for="i in 4" :key="i" class="card h-64 animate-pulse bg-neutral-100 dark:bg-neutral-800 rounded-3xl"></div>
        </div>
        <div class="card h-80 animate-pulse bg-neutral-100 dark:bg-neutral-800 rounded-3xl"></div>
      </div>

      <template v-else-if="report && report.employeeStats">
        <!-- Stats Grid -->
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
           <div v-for="stat in report.employeeStats" :key="stat.employeeId" class="card p-6 flex flex-col items-center text-center">
              <div class="w-16 h-16 rounded-2xl bg-neutral-100 dark:bg-neutral-800 flex items-center justify-center text-2xl font-bold text-neutral-400 mb-4">
                 {{ stat.fullName.charAt(0) }}
              </div>
              <h3 class="font-bold text-neutral-900 dark:text-white">{{ stat.fullName }}</h3>
              <p class="text-xs text-neutral-500 uppercase tracking-widest mt-1">{{ stat.position }}</p>
              
              <div class="mt-6 w-full space-y-3 pt-6 border-t border-neutral-100 dark:border-neutral-800">
                 <div class="flex justify-between text-sm">
                    <span class="text-neutral-500">Sales</span>
                    <span class="font-bold text-success-600">${{ stat.totalSalesGenerated?.toFixed(2) }}</span>
                 </div>
                 <div class="flex justify-between text-sm">
                    <span class="text-neutral-500">Orders</span>
                    <span class="font-bold text-neutral-900 dark:text-white">{{ stat.totalOrdersHandled }}</span>
                 </div>
                 <div class="flex justify-between text-sm">
                    <span class="text-neutral-500">Punctuality</span>
                    <span :class="stat.punctualityRate >= 90 ? 'text-success-600' : 'text-warning-600'" class="font-bold">
                       {{ stat.punctualityRate?.toFixed(1) }}%
                    </span>
                 </div>
              </div>
           </div>
        </div>

        <!-- Detail Table -->
        <div class="bg-white dark:bg-neutral-900 border border-neutral-200 dark:border-neutral-800 rounded-3xl overflow-hidden shadow-sm">
          <table class="w-full text-left">
            <thead>
              <tr class="bg-neutral-50 dark:bg-neutral-800/50 border-b border-neutral-200 dark:border-neutral-800">
                <th class="px-6 py-4 text-xs font-bold text-neutral-500 uppercase">Employee</th>
                <th class="px-6 py-4 text-xs font-bold text-neutral-500 uppercase">Total Hours</th>
                <th class="px-6 py-4 text-xs font-bold text-neutral-500 uppercase text-center">Late Clock-ins</th>
                <th class="px-6 py-4 text-xs font-bold text-neutral-500 uppercase text-right">Avg Order Value</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-neutral-100 dark:divide-neutral-800">
              <tr v-for="stat in report.employeeStats" :key="stat.employeeId" class="hover:bg-neutral-50 dark:hover:bg-neutral-800 transition-colors">
                <td class="px-6 py-4">
                  <span class="font-medium text-neutral-900 dark:text-white">{{ stat.fullName }}</span>
                </td>
                <td class="px-6 py-4 text-neutral-600 dark:text-neutral-400">
                  {{ (stat.totalMinutesWorked / 60).toFixed(1) }} hrs
                </td>
                <td class="px-6 py-4 text-center">
                  <span :class="stat.lateOccurrences > 2 ? 'text-error-600 font-bold' : 'text-neutral-600'" class="px-2 py-1 rounded-lg">
                    {{ stat.lateOccurrences }}
                  </span>
                </td>
                <td class="px-6 py-4 text-right font-bold text-neutral-900 dark:text-white">
                  ${{ stat.averageOrderValue?.toFixed(2) }}
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </template>

      <!-- Empty State -->
      <div v-else class="card p-12 text-center text-neutral-500">
        No report data available. Try selecting a different date range.
      </div>
    </div>
  </NuxtLayout>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'

definePageMeta({
  layout: false
})

const { get } = useApi()

const startDate = ref(new Date(Date.now() - 30 * 24 * 60 * 60 * 1000).toISOString().split('T')[0])
const endDate = ref(new Date().toISOString().split('T')[0])
const loading = ref(true)
const report = ref<any>(null)

const fetchReport = async () => {
    loading.value = true
    try {
        const response = await get<any>(`/reports/staff?startDate=${startDate.value}&endDate=${endDate.value}`)
        if (response?.data) {
            report.value = response.data
        }
    } catch (err) {
        console.error('Failed to fetch staff report', err)
    } finally {
        loading.value = false
    }
}

onMounted(() => {
    fetchReport()
})
</script>

<style scoped>
.btn-primary {
  @apply bg-primary-600 hover:bg-primary-700 text-white rounded-xl px-6 py-2 transition-all active:scale-95 disabled:opacity-50 font-bold text-sm;
}
</style>
