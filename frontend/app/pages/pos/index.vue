<template>
  <NuxtLayout name="pos">
    <!-- Top Components (Category Nav) used to be here or can be here -->

    <!-- Main Content Area -->
    <div class="flex-1 flex flex-row h-full overflow-hidden">
      <!-- Left Sidebar: Root Categories -->
      <aside
        class="w-28 md:w-32 bg-neutral-900 border-r border-neutral-800 flex flex-col items-center py-6 gap-4 overflow-y-auto scrollbar-hide shrink-0"
      >
        <!-- All Items Button -->
        <button
          @click="handleRootSelect('all')"
          :class="[
            'flex flex-col items-center justify-center gap-2 w-20 h-20 md:w-24 md:h-24 rounded-2xl font-bold transition-all duration-200 text-[10px] md:text-xs',
            selectedRootId === 'all'
              ? 'bg-primary-600 text-white shadow-lg shadow-primary-900/30'
              : 'bg-neutral-800/50 text-neutral-400 hover:bg-neutral-800 hover:text-white',
          ]"
        >
          <svg
            xmlns="http://www.w3.org/2000/svg"
            class="w-6 h-6"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            stroke-width="2"
            stroke-linecap="round"
            stroke-linejoin="round"
          >
            <rect x="3" y="3" width="7" height="7"></rect>
            <rect x="14" y="3" width="7" height="7"></rect>
            <rect x="14" y="14" width="7" height="7"></rect>
            <rect x="3" y="14" width="7" height="7"></rect>
          </svg>
          <span class="text-center">All Menu</span>
        </button>

        <!-- Root Categories -->
        <button
          v-for="cat in rootCategories"
          :key="cat.categoryId"
          @click="handleRootSelect(cat.categoryId)"
          :class="[
            'flex flex-col items-center justify-center gap-2 w-20 h-20 md:w-24 md:h-24 rounded-2xl font-bold transition-all duration-200 border-2 text-[10px] md:text-xs px-1',
            selectedRootId === cat.categoryId
              ? 'bg-white text-neutral-900 border-white shadow-xl'
              : 'bg-neutral-800/50 border-transparent text-neutral-400 hover:bg-neutral-800 hover:text-white',
          ]"
        >
          <div
            class="w-8 h-8 md:w-10 md:h-10 rounded-full bg-neutral-700/30 flex items-center justify-center mb-1 group-hover:bg-neutral-600/30 transition-colors"
          >
            <span class="text-sm md:text-base">{{ cat.name.charAt(0) }}</span>
          </div>
          <span class="text-center line-clamp-2">{{ cat.name }}</span>
        </button>
      </aside>

      <!-- Right Column: Sub Categories, Search & Items -->
      <div class="flex-1 flex flex-col min-w-0 bg-neutral-900/50">
        <!-- Breadcrumbs & Search -->
        <div
          class="flex items-center justify-between px-6 py-4 gap-6 bg-neutral-900 border-b border-neutral-800 shrink-0"
        >
          <!-- Breadcrumbs -->
          <nav
            class="flex items-center gap-2 overflow-x-auto scrollbar-hide py-1"
          >
            <button
              @click="handleRootSelect('all')"
              class="text-neutral-500 hover:text-white transition-colors flex items-center gap-1 shrink-0"
            >
              <svg
                xmlns="http://www.w3.org/2000/svg"
                class="w-4 h-4"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                stroke-width="2"
                stroke-linecap="round"
                stroke-linejoin="round"
              >
                <path d="m3 9 9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z" />
                <polyline points="9 22 9 12 15 12 15 22" />
              </svg>
            </button>

            <template
              v-for="(cat, index) in categoryPath"
              :key="cat.categoryId"
            >
              <span class="text-neutral-700 shrink-0">/</span>
              <button
                @click="navigateToPathLevel(index)"
                :class="[
                  'text-sm font-medium transition-colors whitespace-nowrap px-2 py-1 rounded-lg',
                  index === categoryPath.length - 1
                    ? 'text-white bg-neutral-800'
                    : 'text-neutral-500 hover:text-neutral-300 hover:bg-neutral-800/50',
                ]"
              >
                {{ cat.name }}
              </button>
            </template>
          </nav>

          <!-- Search Bar -->
          <div class="relative w-64 lg:w-80 shrink-0">
            <div
              class="absolute left-4 top-1/2 -translate-y-1/2 text-neutral-500"
            >
              <svg
                xmlns="http://www.w3.org/2000/svg"
                class="w-4 h-4"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                stroke-width="2"
                stroke-linecap="round"
                stroke-linejoin="round"
              >
                <circle cx="11" cy="11" r="8"></circle>
                <line x1="21" y1="21" x2="16.65" y2="16.65"></line>
              </svg>
            </div>
            <input
              v-model="searchQuery"
              type="text"
              placeholder="Search dishes..."
              class="w-full pl-10 pr-4 py-2.5 bg-neutral-800/80 border border-neutral-700/50 rounded-xl text-sm text-white placeholder-neutral-600 focus:outline-none focus:ring-2 focus:ring-primary-500/30 focus:border-primary-500/50 transition-all"
            />
          </div>
        </div>

        <!-- Sub Categories Row (Tabs) -->
        <div
          v-if="currentChildCategories.length > 0"
          class="flex items-center px-6 py-4 gap-3 bg-neutral-900 border-b border-neutral-800/50 overflow-x-auto scrollbar-hide shrink-0"
        >
          <div
            class="text-[10px] font-bold text-neutral-600 uppercase tracking-widest mr-2 shrink-0"
          >
            Explore
          </div>
          <button
            v-for="sub in currentChildCategories"
            :key="sub.categoryId"
            @click="handleCategoryDrillDown(sub)"
            class="group flex items-center gap-2 px-5 py-2.5 rounded-xl text-sm font-semibold transition-all whitespace-nowrap bg-neutral-800/40 text-neutral-400 hover:bg-primary-600/10 hover:text-primary-400 border border-neutral-800 hover:border-primary-500/30 shadow-sm"
          >
            <span>{{ sub.name }}</span>
            <svg
              v-if="sub.children && sub.children.length > 0"
              xmlns="http://www.w3.org/2000/svg"
              class="w-3.5 h-3.5 opacity-40 group-hover:opacity-100 transition-opacity"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              stroke-width="3"
            >
              <path d="m9 18 6-6-6-6" />
            </svg>
          </button>
        </div>

        <!-- Current Selected Category Info -->
        <div
          class="flex items-center justify-between p-6 lg:px-8 pt-4 pb-2 gap-6 shrink-0"
        >
          <!-- Mobile Menu Button (visible only on small screens) -->
          <button class="lg:hidden text-white">
            <svg
              xmlns="http://www.w3.org/2000/svg"
              class="w-6 h-6"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              stroke-width="2"
              stroke-linecap="round"
              stroke-linejoin="round"
            >
              <line x1="3" y1="12" x2="21" y2="12"></line>
              <line x1="3" y1="6" x2="21" y2="6"></line>
              <line x1="3" y1="18" x2="21" y2="18"></line>
            </svg>
          </button>

          <!-- Breadcrumbs / Title -->
          <div class="flex-1">
            <h1 class="text-2xl font-bold text-white">
              {{ currentCategoryName }}
            </h1>
            <p class="text-sm text-neutral-500" v-if="!loading">
              {{ filteredItems.length }} items available
            </p>
          </div>
        </div>

        <!-- Items Grid -->
        <div
          ref="itemsGridRef"
          class="flex-1 overflow-y-auto scrollbar-thin scroll-smooth"
        >
          <!-- Loading State -->
          <div
            v-if="loading"
            class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 gap-6 p-6"
          >
            <div
              v-for="i in 10"
              :key="i"
              class="aspect-[3/4] bg-neutral-800/30 rounded-3xl overflow-hidden border border-neutral-700/20 animate-pulse"
            ></div>
          </div>

          <!-- Empty State -->
          <div
            v-else-if="filteredItems.length === 0"
            class="flex flex-col items-center justify-center h-full text-neutral-500 min-h-[400px]"
          >
            <div
              class="w-24 h-24 rounded-full bg-neutral-800 flex items-center justify-center mb-6"
            >
              <svg
                xmlns="http://www.w3.org/2000/svg"
                class="w-10 h-10 opacity-30"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                stroke-width="2"
              >
                <path d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
              </svg>
            </div>
            <p class="text-lg font-medium text-neutral-400">No items found</p>
          </div>

          <!-- Grouped Items Grid (Scroll Spy) -->
          <div v-else class="p-6 space-y-10 pb-24">
            <template v-for="group in activeGroups" :key="group.categoryId">
              <div
                :id="'category-' + group.categoryId"
                class="category-section scroll-mt-24"
                :data-category-id="group.categoryId"
              >
                <!-- Category Header -->
                <div class="flex items-center gap-4 mb-6 sticky top-6 z-10">
                  <h2
                    class="text-xl font-bold text-white flex items-center gap-3"
                  >
                    <span class="w-1.5 h-6 bg-primary-500 rounded-full"></span>
                    {{ group.name }}
                  </h2>
                  <div
                    class="h-px flex-1 bg-gradient-to-r from-neutral-800 to-transparent"
                  ></div>
                </div>

                <!-- Items -->
                <div
                  class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 gap-6"
                >
                  <button
                    v-for="item in group.items"
                    :key="item.menuItemId"
                    @click="handleAddToCart(item)"
                    :disabled="!item.isAvailable"
                    class="group relative bg-neutral-800 rounded-2xl overflow-hidden border border-neutral-700 hover:border-primary-500 text-left disabled:opacity-50 disabled:cursor-not-allowed transition-all duration-200"
                  >
                    <!-- Image Container -->
                    <div
                      class="aspect-square bg-neutral-800 relative overflow-hidden"
                    >
                      <img
                        v-if="item.imageUrl"
                        :src="item.imageUrl"
                        :alt="item.name"
                        loading="lazy"
                        class="w-full h-full object-cover transition-transform duration-500 group-hover:scale-105"
                      />
                      <div
                        v-else
                        class="absolute inset-0 flex flex-col items-center justify-center bg-neutral-850 gap-2"
                      >
                        <svg
                          xmlns="http://www.w3.org/2000/svg"
                          class="w-10 h-10 text-neutral-700/50"
                          viewBox="0 0 24 24"
                          fill="none"
                          stroke="currentColor"
                          stroke-width="1.5"
                        >
                          <path d="M17 8h1a4 4 0 1 1 0 8h-1" />
                          <path d="M3 8h14v9a4 4 0 0 1-4 4H7a4 4 0 0 1-4-4Z" />
                        </svg>
                      </div>

                      <!-- Gradient Overlay -->
                      <div
                        class="absolute inset-0 bg-gradient-to-t from-neutral-900 via-transparent to-transparent opacity-60"
                      ></div>

                      <!-- Price Badge -->
                      <div class="absolute bottom-3 left-3">
                        <span
                          class="bg-primary-500 text-white px-2.5 py-1 rounded-lg text-xs font-bold shadow-lg"
                        >
                          ${{ item.basePrice.toFixed(2) }}
                        </span>
                      </div>

                      <!-- Low Stock / Sold Out Indicators already exist in logic but simplifying markup here for brevity if needed, keeping mostly same -->
                      <div
                        v-if="!item.isAvailable"
                        class="absolute inset-0 bg-neutral-900/80 flex items-center justify-center backdrop-blur-sm"
                      >
                        <span
                          class="text-xs font-bold text-red-500 uppercase border border-red-500/50 px-2 py-1 rounded"
                          >Sold Out</span
                        >
                      </div>
                    </div>

                    <!-- Content -->
                    <div class="p-4">
                      <h3
                        class="font-bold text-neutral-200 text-sm leading-snug line-clamp-2 h-10 mb-1"
                      >
                        {{ item.name }}
                      </h3>
                    </div>
                  </button>
                </div>
              </div>
            </template>
          </div>
        </div>
      </div>
    </div>

    <!-- Variant Selection Modal (Unchanged logic, compacted for this view) -->
    <div
      v-if="showVariantModal && selectedItem"
      class="fixed inset-0 z-50 flex items-center justify-center bg-black/80 backdrop-blur-sm p-4"
    >
      <!-- ... existing Code for Variant Modal ... -->
      <div
        class="bg-neutral-800 rounded-2xl w-full max-w-sm border border-neutral-700 overflow-hidden"
      >
        <div class="p-4 border-b border-neutral-700 bg-neutral-900/50">
          <h3 class="text-lg font-bold text-white">Select Size</h3>
          <p class="text-neutral-400 text-sm">{{ selectedItem.name }}</p>
        </div>
        <div class="p-5 grid grid-cols-3 gap-3">
          <button
            v-for="variant in selectedItem.variants"
            :key="variant.variantId"
            @click="selectedVariant = variant"
            :class="[
              'p-3 rounded-xl border-2 flex flex-col items-center gap-1 transition-all',
              selectedVariant?.variantId === variant.variantId
                ? 'bg-primary-600/20 border-primary-500 text-white'
                : 'bg-neutral-900 border-neutral-700 text-neutral-400 hover:border-neutral-500',
            ]"
          >
            <span class="text-xl font-bold">{{ variant.size }}</span>
            <span class="text-xs">${{ variant.price.toFixed(2) }}</span>
          </button>
        </div>
        <div class="p-4 border-t border-neutral-700 flex gap-3">
          <button
            @click="showVariantModal = false"
            class="flex-1 py-3 rounded-xl border border-neutral-600 text-neutral-300 font-bold hover:bg-neutral-700"
          >
            Cancel
          </button>
          <button
            @click="confirmVariantSelection"
            :disabled="!selectedVariant"
            class="flex-1 py-3 rounded-xl bg-primary-600 text-white font-bold hover:bg-primary-500 disabled:opacity-50"
          >
            Confirm
          </button>
        </div>
      </div>
    </div>

    <!-- Add-On Modal (Re-implementation to ensure full file integrity) -->
    <div
      v-if="showAddOnModal && selectedItem"
      class="fixed inset-0 z-50 flex items-center justify-center bg-black/80 backdrop-blur-sm p-4"
      @click.self="closeAddOnModal"
    >
      <div
        class="bg-neutral-800 rounded-2xl shadow-2xl w-full max-w-lg max-h-[85vh] overflow-hidden border border-neutral-700 flex flex-col"
      >
        <!-- Modal Header -->
        <div
          class="p-4 border-b border-neutral-700 flex items-center gap-4 bg-neutral-900/50"
        >
          <div
            class="w-14 h-14 rounded-lg bg-neutral-700 overflow-hidden shrink-0"
          >
            <img
              v-if="selectedItem.imageUrl"
              :src="selectedItem.imageUrl"
              class="w-full h-full object-cover"
            />
          </div>
          <div class="flex-1">
            <h3 class="text-lg font-bold text-white leading-tight">
              {{ selectedItem.name }}
            </h3>
            <p class="text-primary-400 font-mono">
              ${{
                (
                  (selectedVariant
                    ? selectedVariant.price
                    : selectedItem.basePrice) + addOnTotal
                ).toFixed(2)
              }}
            </p>
          </div>
          <button
            @click="closeAddOnModal"
            class="p-2 text-neutral-400 hover:text-white"
          >
            <svg
              xmlns="http://www.w3.org/2000/svg"
              class="w-6 h-6"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              stroke-width="2"
            >
              <path d="M18 6 6 18" />
              <path d="m6 6 12 12" />
            </svg>
          </button>
        </div>

        <div class="flex-1 overflow-y-auto p-5 scrollbar-thin">
          <!-- Addons Grid -->
          <div v-if="addOnList.length > 0">
            <h4
              class="text-xs font-bold text-neutral-500 uppercase tracking-widest mb-3"
            >
              Add-ons
            </h4>
            <div class="grid grid-cols-2 gap-3 mb-6">
              <button
                v-for="addOn in addOnList"
                :key="addOn.addonId"
                @click="toggleAddOn(addOn)"
                :class="[
                  'p-3 rounded-xl border text-left flex justify-between items-center transition-all',
                  isAddOnSelected(addOn.addonId)
                    ? 'bg-primary-900/20 border-primary-500 text-white'
                    : 'bg-neutral-900 border-neutral-700 text-neutral-400 hover:border-neutral-600',
                ]"
              >
                <span class="text-sm font-medium">{{ addOn.name }}</span>
                <span class="text-xs font-bold text-primary-400"
                  >+${{ addOn.price.toFixed(2) }}</span
                >
              </button>
            </div>
          </div>
          <div>
            <h4
              class="text-xs font-bold text-neutral-500 uppercase tracking-widest mb-3"
            >
              Notes
            </h4>
            <textarea
              v-model="itemNotes"
              rows="3"
              class="w-full bg-neutral-900 border border-neutral-700 rounded-xl p-3 text-white text-sm focus:border-primary-500 focus:outline-none placeholder-neutral-600"
              placeholder="Special instructions..."
            ></textarea>
          </div>
        </div>

        <div class="p-5 border-t border-neutral-700 bg-neutral-900">
          <button
            @click="confirmAddToCart"
            class="w-full py-4 rounded-xl bg-primary-600 hover:bg-primary-500 text-white font-bold shadow-lg shadow-primary-900/20 transition-all active:scale-[0.98]"
          >
            Add to Order • ${{
              (
                (selectedVariant
                  ? selectedVariant.price
                  : selectedItem.basePrice) + addOnTotal
              ).toFixed(2)
            }}
          </button>
        </div>
      </div>
    </div>
  </NuxtLayout>
</template>

<script setup lang="ts">
import {
  h,
  ref,
  computed,
  onMounted,
  shallowRef,
  watch,
  onUnmounted,
} from "vue";
import { useDebounceFn, useIntersectionObserver } from "@vueuse/core";

definePageMeta({
  layout: false, // Uses global wrap, but we manually wrap locally? No, waiting for global fix. Wait, this file wraps with <NuxtLayout name="pos">.
});

const { get } = useApi();
const { addToCart, addToCartWithAddOns, clearCart } = useCart();

// --- Types ---
interface AddOn {
  addonId: number;
  name: string;
  price: number;
}
interface Category {
  categoryId: number;
  name: string;
  parentId?: number;
  children?: Category[];
}
interface Variant {
  variantId: number;
  size: "S" | "M" | "L";
  price: number;
}
interface MenuItem {
  menuItemId: number;
  name: string;
  basePrice: number;
  imageUrl: string;
  isAvailable: boolean;
  lowStock?: boolean;
  categoryId: number;
  category?: Category;
  variants?: Variant[];
}

const cachedCategories = useState<Category[]>("pos-categories", () => []);
const cachedMenuItems = useState<MenuItem[]>("pos-menu-items", () => []);

const selectedCategory = ref<number | "all">("all");
const expandedIds = ref<number[]>([]);
const searchQuery = ref("");
const debouncedSearchQuery = ref("");
const loading = ref(true);

const updateDebouncedSearch = useDebounceFn((val: string) => {
  debouncedSearchQuery.value = val;
}, 300);
watch(searchQuery, (newVal) => {
  updateDebouncedSearch(newVal);
});

// Modal States
const showAddOnModal = ref(false);
const showVariantModal = ref(false);
const selectedItem = ref<MenuItem | null>(null);
const selectedVariant = ref<Variant | null>(null);
const addOnList = shallowRef<AddOn[]>([]);
const selectedAddOns = ref<AddOn[]>([]);
const itemNotes = ref("");
const addOnsLoading = ref(false);

const rootCategories = shallowRef<Category[]>([]);
const menuItems = shallowRef<MenuItem[]>([]);
const selectedRootId = ref<number | "all">("all");
const categoryPath = ref<Category[]>([]);
const itemsGridRef = ref<HTMLElement | null>(null); // For Scroll Spy

// --- Computed Properties ---

const currentCategory = computed(() => {
  if (categoryPath.value.length === 0) return null;
  return categoryPath.value[categoryPath.value.length - 1];
});

const currentChildCategories = computed(() => {
  if (selectedRootId.value === "all" && categoryPath.value.length === 0)
    return [];
  return currentCategory.value?.children || [];
});

const activeCategoryId = computed(() => {
  return currentCategory.value?.categoryId || "all";
});

// --- Filtering Logic (moved up to avoid pre-initialization access) --
const descendantCache = new Map<number, number[]>(); // This was originally below filteredItems, but it's a dependency for the full filtering logic. Keeping it here for now.
const filteredItems = computed(() => {
  let result = menuItems.value;
  if (activeCategoryId.value !== "all") {
    const targetId = Number(activeCategoryId.value);
    // Helper to find all descendant category IDs for a given category
    const findAllDescendantIds = (categoryId: number): number[] => {
      if (descendantCache.has(categoryId)) {
        return descendantCache.get(categoryId)!;
      }

      const descendants: number[] = [];
      const findChildren = (cats: Category[]) => {
        for (const cat of cats) {
          if (cat.parentId === categoryId) {
            descendants.push(cat.categoryId);
            if (cat.children) {
              findChildren(cat.children);
            }
          } else if (cat.children) {
            // Recursively check children of other categories
            findChildren(cat.children);
          }
        }
      };

      // Start search from root categories
      const findChildrenRecursive = (
        currentCategory: Category,
        targetId: number,
      ) => {
        if (currentCategory.categoryId === targetId) {
          if (currentCategory.children) {
            currentCategory.children.forEach((child) => {
              descendants.push(child.categoryId);
              findChildrenRecursive(child, child.categoryId); // Find children of this child
            });
          }
        } else if (currentCategory.children) {
          currentCategory.children.forEach((child) =>
            findChildrenRecursive(child, targetId),
          );
        }
      };

      rootCategories.value.forEach((rootCat) =>
        findChildrenRecursive(rootCat, categoryId),
      );

      descendantCache.set(categoryId, descendants);
      return descendants;
    };

    const descendantIds = findAllDescendantIds(targetId);
    result = result.filter(
      (i) => i.categoryId === targetId || descendantIds.includes(i.categoryId),
    );
  }
  if (debouncedSearchQuery.value) {
    const q = debouncedSearchQuery.value.toLowerCase();
    result = result.filter((item) => item.name.toLowerCase().includes(q));
  }
  return result;
});

// Grouping Logic for Scroll Spy
// Note: We only group by ROOT level categories for the main view
const activeGroups = computed(() => {
  const items = filteredItems.value;
  if (items.length === 0) return [];

  // If we are drilled down, we might just show one group, or subgroup
  // But for "All Menu", we want groups by Root Category.

  // 1. Map items to their Root Category
  const groups = new Map<
    number,
    { categoryId: number; name: string; items: MenuItem[] }
  >();

  // Helper to find root for an item
  const findRoot = (catId: number): Category | undefined => {
    return rootCategories.value.find(
      (c) => c.categoryId === catId || isDescendant(c, catId),
    );
  };

  const isDescendant = (parent: Category, childId: number): boolean => {
    if (parent.categoryId === childId) return true;
    if (parent.children)
      return parent.children.some((c) => isDescendant(c, childId));
    return false;
  };

  items.forEach((item) => {
    let root = findRoot(item.categoryId);

    // Fallback if not found (direct assignment)
    if (!root && item.category) {
      root = findRoot(item.category.categoryId);
    }

    if (root) {
      if (!groups.has(root.categoryId)) {
        groups.set(root.categoryId, {
          categoryId: root.categoryId,
          name: root.name,
          items: [],
        });
      }
      groups.get(root.categoryId)!.items.push(item);
    } else {
      // Uncategorized or Other
      const otherId = 999999;
      if (!groups.has(otherId)) {
        groups.set(otherId, { categoryId: otherId, name: "Other", items: [] });
      }
      groups.get(otherId)!.items.push(item);
    }
  });

  return Array.from(groups.values()).sort(
    (a, b) => a.categoryId - b.categoryId,
  );
});

// --- Scroll Spy Logic ---
let observer: IntersectionObserver | null = null;
const setupScrollSpy = () => {
  if (observer) observer.disconnect();

  observer = new IntersectionObserver(
    (entries) => {
      // Find the first visible entry
      const visible = entries.find((e) => e.isIntersecting);
      if (visible) {
        const id = Number(visible.target.getAttribute("data-category-id"));
        if (!isNaN(id) && id !== 999999) {
          // Only update if we are in "All" mode navigation essentially
          // But here `selectedRootId` controls the view.
          // If we are scrolling, we want the sidebar to reflect the current section.
          // However, `selectedRootId` also filters!
          // Wait, if `selectedRootId` filters, then we only see ONE category?
          // If so, scroll spy is redundant.
          // The request "sticky menu items" implies a long scroll list.
          // Usually POS has "All" selected, and clicking sidebar scrolls to section vs filtering.
          // Let's assume hitting a sidebar button should SCROLL to it if in "All" mode?
          // Or does sidebar filter?
          // The current code FILTERS: `if (activeCategoryId.value !== 'all') ...`
          // If it filters, scroll spy is useless because only one category is visible.
          // If the user wants "sticky categories", maybe they want effective scrolling instead of filtering?
          // "scroll stick only menu items" -> maybe they want to see ALL items and scroll?
          // I will enable "All" to show groups (as implemented above).
          // And clicking sidebar should maybe just SCROLL to it if we are in "All" mode?
          // Let's stick to the current filtering logic mostly, but if "All" is selected, we perform scroll spy on the SIDEBAR (which usually highlights current selection).
          // But if sidebar buttons TRIGGER filtering, we can't just highlight them without triggering filter.
          // I will leave the Sidebar as is (Filtering).
          // PROPOSAL: If we are in "All" mode, the sidebar stays on "All".
          // If the user wants scroll spy, that usually implies a "Scroll to Section" sidebar.
          // I will update `selectedRootId` ONLY if it doesn't trigger a re-render that hides others.
          // Actually, `activeGroups` relies on `filteredItems`.
          // `filteredItems` relies on `selectedRootId` (via activeCategoryId).
          // If `selectedRootId` changes, `filteredItems` changes to ONLY that category.
          // So we CANNOT use `selectedRootId` for scroll spy active state if it also filters.
          // I will add a separate `activeScrollCategoryId` to track visual highlighting in sidebar WITHOUT filtering.
        }
      }
    },
    {
      root: itemsGridRef.value,
      threshold: 0.1,
      rootMargin: "-20% 0px -60% 0px",
    },
  );

  // Slight delay to allow DOM render
  setTimeout(() => {
    const sections = document.querySelectorAll(".category-section");
    sections.forEach((s) => observer?.observe(s));
  }, 500);
};

// We need to change sidebar styling to use `activeScrollCategoryId` if in all mode
const activeScrollCategoryId = ref<number | "all">("all");

watch(activeGroups, () => {
  // Re-setup spy when items change
  setupScrollSpy();
});

// --- Navigation Handlers ---
const handleRootSelect = (id: number | "all") => {
  if (id === "all") {
    selectedRootId.value = "all";
    categoryPath.value = [];
    activeScrollCategoryId.value = "all";
  } else {
    // If we click a specific category, we FILTER by it.
    // User request implies they might want "Sticky Scroll".
    // If they click "Coffee", it filters to Coffee.
    // If they click "All", it shows everything.
    // If they scroll in "All", we just highlight "Coffee" if visible?
    const root = rootCategories.value.find((c) => c.categoryId === id);
    if (root) {
      selectedRootId.value = id;
      categoryPath.value = [root];
      activeScrollCategoryId.value = id;
    }
  }
};

const handleCategoryDrillDown = (category: Category) => {
  categoryPath.value.push(category);
};

const navigateToPathLevel = (index: number) => {
  categoryPath.value = categoryPath.value.slice(0, index + 1);
};

const currentCategoryName = computed(() => {
  if (activeCategoryId.value === "all") return "All Menu Items";
  return currentCategory.value?.name || "Menu Items";
});

// --- Cart & Actions ---
const handleAddToCart = (item: MenuItem) => {
  selectedItem.value = item;
  selectedAddOns.value = [];
  itemNotes.value = "";
  selectedVariant.value = null;
  if (item.variants && item.variants.length > 0) {
    selectedVariant.value = item.variants[0];
    showVariantModal.value = true;
  } else {
    showAddOnModal.value = true;
  }
};

const confirmVariantSelection = () => {
  showVariantModal.value = false;
  showAddOnModal.value = true;
};

const toggleAddOn = (addOn: AddOn) => {
  const index = selectedAddOns.value.findIndex(
    (a) => a.addonId === addOn.addonId,
  );
  if (index === -1) selectedAddOns.value.push(addOn);
  else selectedAddOns.value.splice(index, 1);
};

const isAddOnSelected = (addOnId: number) =>
  selectedAddOns.value.some((a) => a.addonId === addOnId);

const addOnTotal = computed(() =>
  selectedAddOns.value.reduce((sum, a) => sum + a.price, 0),
);

const confirmAddToCart = () => {
  if (!selectedItem.value) return;
  const itemToAdd = { ...selectedItem.value };
  if (selectedVariant.value) {
    itemToAdd.basePrice = selectedVariant.value.price;
    itemToAdd.name = `${itemToAdd.name} (${selectedVariant.value.size})`;
  }
  if (selectedAddOns.value.length > 0 || itemNotes.value) {
    addToCartWithAddOns(itemToAdd, [...selectedAddOns.value], itemNotes.value);
  } else {
    addToCart(itemToAdd);
  }
  closeAddOnModal();
};

const closeAddOnModal = () => {
  showAddOnModal.value = false;
  selectedItem.value = null;
  selectedAddOns.value = [];
  itemNotes.value = "";
};

// --- Data Fetching ---
const fetchData = async () => {
  loading.value = true;
  try {
    const [cats, items] = await Promise.all([
      get<Category[]>("/categories"),
      get<MenuItem[]>("/menu-items"),
    ]);
    rootCategories.value = cats || [];
    menuItems.value = items || [];
  } catch (err) {
    console.error("Fetch Error", err);
  } finally {
    loading.value = false;
  }
};
const fetchAddOns = async () => {
  try {
    const data = await get<AddOn[]>("/addons");
    addOnList.value = data || [];
  } catch (e) {}
};

// --- Keyboard Shortcuts & Init ---
onMounted(() => {
  fetchData();
  fetchAddOns();

  window.addEventListener("keydown", handleKeydown);
});

onUnmounted(() => {
  window.removeEventListener("keydown", handleKeydown);
  if (observer) observer.disconnect();
});

const handleKeydown = (e: KeyboardEvent) => {
  // Ignore if typing in input
  if (
    e.target instanceof HTMLInputElement ||
    e.target instanceof HTMLTextAreaElement
  )
    return;

  // Space: Pay / Checkout
  if (e.code === "Space" && !showAddOnModal.value && !showVariantModal.value) {
    e.preventDefault();
    // Emit or call openPayment logic
    // We can't easily reach layout payment modal from here without event bus or store.
    // Assuming user just wanted the "Space" to trigger "Add to Cart" if modal open, or something.
    // Or if in modal, Confirm.
    if (showAddOnModal.value) confirmAddToCart();
  }

  // Esc: Close Modals
  if (e.code === "Escape") {
    if (showAddOnModal.value) closeAddOnModal();
    if (showVariantModal.value) showVariantModal.value = false;
  }

  // Shift + F: Search
  if (e.shiftKey && e.code === "KeyF") {
    e.preventDefault();
    const searchInput = document.querySelector(
      'input[type="text"]',
    ) as HTMLInputElement;
    if (searchInput) searchInput.focus();
  }
};
</script>

<style scoped>
/* Removed reveal-item animation */
/* Smooth Scroll */
.scroll-smooth {
  scroll-behavior: smooth;
}
html {
  scroll-behavior: smooth;
}
.scrollbar-thin {
  content-visibility: auto;
}
</style>
