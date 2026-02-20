<template>
  <div class="relative w-full h-full min-h-[250px] flex flex-col">
    <div class="flex-1 relative flex items-end justify-between gap-2 px-2 pb-8 pt-4">
      <!-- Grid Lines -->
      <div class="absolute inset-0 flex flex-col justify-between pointer-events-none pb-8">
        <div v-for="i in 4" :key="i" class="border-b border-neutral-100 dark:border-neutral-800 w-full h-0"></div>
      </div>

      <!-- Bars -->
      <div 
        v-for="(item, index) in data" 
        :key="index"
        class="group relative flex-1 flex flex-col items-center justify-end h-full"
      >
        <!-- Tooltip -->
        <div class="absolute -top-8 left-1/2 -translate-x-1/2 bg-neutral-900 text-white text-[10px] px-2 py-1 rounded opacity-0 group-hover:opacity-100 transition-opacity whitespace-nowrap z-10 pointer-events-none">
          {{ formatCurrency(item.revenue) }} ({{ item.orderCount }} orders)
        </div>

        <!-- Bar Column -->
        <div 
          class="w-full max-w-[40px] bg-primary-500/20 dark:bg-primary-500/10 rounded-t-lg transition-all duration-500 ease-out group-hover:bg-primary-500/40"
          :style="{ height: `${(item.revenue / maxRevenue) * 100}%` }"
        >
          <!-- Active Part -->
          <div 
            class="h-full bg-primary-600 rounded-t-lg shadow-lg shadow-primary-500/20"
            :style="{ height: '100%' }"
          ></div>
        </div>

        <!-- Label -->
        <span class="absolute -bottom-6 text-[10px] font-medium text-neutral-500 dark:text-neutral-400">
          {{ formatDate(item.date) }}
        </span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
interface DailySales {
  date: string
  revenue: number
  orderCount: number
}

const props = defineProps<{
  data: DailySales[]
}>()

const maxRevenue = computed(() => {
  const max = Math.max(...props.data.map(d => d.revenue), 0)
  return max === 0 ? 100 : max * 1.2 // Add 20% breathing room
})

const formatDate = (dateStr: string) => {
  try {
    const date = new Date(dateStr)
    return date.toLocaleDateString('en-US', { weekday: 'short' })
  } catch {
    return dateStr
  }
}

const formatCurrency = (val: number) => {
  return new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'USD'
  }).format(val)
}
</script>
