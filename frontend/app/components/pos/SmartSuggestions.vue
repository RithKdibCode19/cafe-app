<template>
  <div
    v-if="suggestions.length > 0"
    class="mt-4 animate-in slide-in-from-bottom duration-300"
  >
    <div class="flex items-center justify-between mb-3 px-1">
      <h4
        class="text-[10px] font-black uppercase tracking-widest text-primary-400"
      >
        Smart Suggestions
      </h4>
      <span class="text-[10px] text-neutral-500 font-medium"
        >Frequently bought with {{ lastItemName }}</span
      >
    </div>
    <div class="grid grid-cols-2 gap-2">
      <button
        v-for="item in suggestions"
        :key="item.menuItemId"
        @click="$emit('add', item)"
        class="group relative flex flex-col bg-neutral-800/50 hover:bg-primary-500/10 border border-neutral-700 hover:border-primary-500/30 rounded-xl overflow-hidden transition-all text-left"
      >
        <div class="aspect-video w-full relative overflow-hidden">
          <img
            v-if="item.imageUrl"
            :src="item.imageUrl"
            class="w-full h-full object-cover group-hover:scale-110 transition-transform duration-500"
          />
          <div
            v-else
            class="w-full h-full bg-neutral-700 flex items-center justify-center"
          >
            <svg
              xmlns="http://www.w3.org/2000/svg"
              class="w-6 h-6 text-neutral-500"
              fill="none"
              viewBox="0 0 24 24"
              stroke="currentColor"
            >
              <path
                d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z"
              />
            </svg>
          </div>
          <div
            class="absolute inset-x-0 bottom-0 p-2 bg-gradient-to-t from-neutral-900/90 to-transparent"
          >
            <span class="text-xs font-bold text-white"
              >${{ item.basePrice.toFixed(2) }}</span
            >
          </div>
        </div>
        <div class="p-2">
          <div
            class="text-[10px] font-bold text-white truncate group-hover:text-primary-400 transition-colors"
          >
            {{ item.name }}
          </div>
          <div class="text-[9px] text-neutral-500 uppercase font-black mt-0.5">
            Add to Order
          </div>
        </div>
        <div
          class="absolute top-1 right-1 opacity-0 group-hover:opacity-100 transition-opacity"
        >
          <div
            class="w-5 h-5 bg-primary-500 rounded-full flex items-center justify-center shadow-lg"
          >
            <svg
              xmlns="http://www.w3.org/2000/svg"
              class="w-3 h-3 text-white"
              fill="none"
              viewBox="0 0 24 24"
              stroke="currentColor"
              stroke-width="4"
            >
              <path d="M12 5v14M5 12h14" />
            </svg>
          </div>
        </div>
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from "vue";

const props = defineProps<{
  lastItemId: number | null;
  lastItemName: string;
}>();

const emit = defineEmits(["add"]);
const { get } = useApi();
const suggestions = ref<any[]>([]);

const fetchSuggestions = async (id: number) => {
  try {
    const data = await get<any[]>(`/recommendations/${id}`);
    suggestions.value = data || [];
  } catch (e) {
    console.error("Failed to fetch suggestions", e);
    suggestions.value = [];
  }
};

watch(
  () => props.lastItemId,
  (newId) => {
    if (newId) {
      fetchSuggestions(newId);
    } else {
      suggestions.value = [];
    }
  },
);
</script>
