<template>
  <div class="category-item">
    <div
      class="flex items-center justify-between p-3 bg-white dark:bg-neutral-800 border border-neutral-200 dark:border-neutral-700 rounded-lg mb-2 hover:border-primary-500 transition-colors"
    >
      <div class="flex items-center gap-3">
        <button
          v-if="category.children && category.children.length > 0"
          @click="isOpen = !isOpen"
          class="text-neutral-500 hover:text-neutral-700 dark:text-neutral-400"
        >
          <svg
            xmlns="http://www.w3.org/2000/svg"
            class="w-4 h-4 transition-transform duration-200"
            :class="{ 'rotate-90': isOpen }"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            stroke-width="2"
            stroke-linecap="round"
            stroke-linejoin="round"
          >
            <path d="m9 18 6-6-6-6" />
          </svg>
        </button>
        <div v-else class="w-4"></div>
        <!-- Spacer -->

        <span class="font-medium text-neutral-900 dark:text-white">{{
          category.name
        }}</span>
        <span
          v-if="category.description"
          class="text-xs text-neutral-500 truncate max-w-[200px]"
          >{{ category.description }}</span
        >
      </div>

      <div class="flex items-center gap-2">
        <button
          @click="$emit('edit', category)"
          class="p-1.5 text-neutral-400 hover:text-primary-600 transition-colors"
          title="Edit"
        >
          <svg
            xmlns="http://www.w3.org/2000/svg"
            class="w-4 h-4"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            stroke-width="2"
          >
            <path d="M17 3a2.828 2.828 0 1 1 4 4L7.5 20.5 2 22l1.5-5.5L17 3z" />
          </svg>
        </button>
        <button
          @click="$emit('delete', category.categoryId)"
          class="p-1.5 text-neutral-400 hover:text-error-600 transition-colors"
          title="Delete"
        >
          <svg
            xmlns="http://www.w3.org/2000/svg"
            class="w-4 h-4"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            stroke-width="2"
          >
            <path
              d="M3 6h18m-2 0v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"
            />
          </svg>
        </button>
      </div>
    </div>

    <!-- Recursive Children -->
    <div
      v-if="isOpen && category.children && category.children.length > 0"
      class="pl-8 border-l border-neutral-200 dark:border-neutral-700 ml-4"
    >
      <AdminCategoryTreeItem
        v-for="child in category.children"
        :key="child.categoryId"
        :category="child"
        @edit="$emit('edit', $event)"
        @delete="$emit('delete', $event)"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";

interface Category {
  categoryId: number;
  name: string;
  description?: string;
  parentId?: number;
  children?: Category[];
}

const props = defineProps<{
  category: Category;
}>();

const emit = defineEmits(["edit", "delete"]);

const isOpen = ref(true);
</script>
