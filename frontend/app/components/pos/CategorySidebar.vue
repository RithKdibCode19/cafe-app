<template>
  <div class="space-y-1">
    <div v-for="category in categories" :key="category.categoryId">
      <button
        @click="$emit('select', category)"
        :class="[
          'w-full flex items-center justify-between px-4 py-3 rounded-xl transition-all duration-200 group',
          selectedCategory === category.categoryId
            ? 'bg-primary-600 text-white shadow-lg shadow-primary-900/20'
            : 'text-neutral-400 hover:bg-neutral-800 hover:text-white',
        ]"
      >
        <div class="flex items-center gap-3">
          <!-- Icon -->
          <component
            :is="getIcon(category.name)"
            :class="[
              'w-5 h-5 transition-colors',
              selectedCategory === category.categoryId
                ? 'text-white'
                : 'text-neutral-500 group-hover:text-white',
            ]"
          />

          <span class="font-medium text-sm">{{ category.name }}</span>
        </div>

        <div class="flex items-center gap-2">
          <!-- Count Badge -->
          <span
            v-if="category.children && category.children.length > 0"
            :class="[
              'text-xs font-bold px-2 py-0.5 rounded-md',
              selectedCategory === category.categoryId
                ? 'bg-white/20 text-white'
                : 'bg-neutral-800 text-neutral-500 group-hover:bg-neutral-700 group-hover:text-neutral-300',
            ]"
          >
            {{ category.children.length }}
          </span>

          <!-- Chevron for collapse/expand -->
          <svg
            v-if="category.children && category.children.length > 0"
            xmlns="http://www.w3.org/2000/svg"
            class="w-4 h-4 transition-transform duration-200"
            :class="{ 'rotate-90': isExpanded(category.categoryId) }"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            stroke-width="2"
            stroke-linecap="round"
            stroke-linejoin="round"
          >
            <path d="m9 18 6-6-6-6" />
          </svg>
        </div>
      </button>

      <!-- Recursive Children -->
      <div
        v-if="
          category.children &&
          category.children.length > 0 &&
          isExpanded(category.categoryId)
        "
        class="pl-4 mt-1 space-y-1 relative"
      >
        <!-- Connector Line -->
        <div
          class="absolute left-0 top-0 bottom-0 w-px bg-neutral-800 ml-6"
        ></div>

        <PosCategorySidebar
          :categories="category.children"
          :selected-category="selectedCategory"
          :expanded-ids="expandedIds"
          @select="$emit('select', $event)"
          @toggle="$emit('toggle', $event)"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { h } from "vue";

interface Category {
  categoryId: number;
  name: string;
  parentId?: number;
  children?: Category[];
}

const props = defineProps<{
  categories: Category[];
  selectedCategory: number | "all";
  expandedIds: number[];
}>();

const emit = defineEmits(["select", "toggle"]);

const isExpanded = (id: number) => props.expandedIds.includes(id);

// Icons Mapping (Same as index.vue for consistency)
const categoryIcons: Record<string, any> = {
  Beverages: h(
    "svg",
    {
      xmlns: "http://www.w3.org/2000/svg",
      viewBox: "0 0 24 24",
      fill: "none",
      stroke: "currentColor",
      strokeWidth: "2",
      strokeLinecap: "round",
      strokeLinejoin: "round",
    },
    [
      h("path", { d: "M17 8h1a4 4 0 1 1 0 8h-1" }),
      h("path", { d: "M3 8h14v9a4 4 0 0 1-4 4H7a4 4 0 0 1-4-4Z" }),
    ],
  ),
  Coffee: h(
    "svg",
    {
      xmlns: "http://www.w3.org/2000/svg",
      viewBox: "0 0 24 24",
      fill: "none",
      stroke: "currentColor",
      strokeWidth: "2",
      strokeLinecap: "round",
      strokeLinejoin: "round",
    },
    [
      h("path", { d: "M17 8h1a4 4 0 1 1 0 8h-1" }),
      h("path", { d: "M3 8h14v9a4 4 0 0 1-4 4H7a4 4 0 0 1-4-4Z" }),
      h("line", { x1: "6", x2: "6", y1: "2", y2: "4" }),
      h("line", { x1: "10", x2: "10", y1: "2", y2: "4" }),
    ],
  ),
  // Defaults...
  Default: h(
    "svg",
    {
      xmlns: "http://www.w3.org/2000/svg",
      viewBox: "0 0 24 24",
      fill: "none",
      stroke: "currentColor",
      strokeWidth: "2",
      strokeLinecap: "round",
      strokeLinejoin: "round",
    },
    [
      h("circle", { cx: "12", cy: "12", r: "10" }),
      h("path", { d: "M12 6v6l4 2" }),
    ],
  ),
};

const getIcon = (name: string) =>
  categoryIcons[name] || categoryIcons["Default"];
</script>
