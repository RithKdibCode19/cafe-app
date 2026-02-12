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
          <div class="w-8 h-8 md:w-10 md:h-10 rounded-full bg-neutral-700/30 flex items-center justify-center mb-1 group-hover:bg-neutral-600/30 transition-colors">
            <span class="text-sm md:text-base">{{ cat.name.charAt(0) }}</span>
          </div>
          <span class="text-center line-clamp-2">{{ cat.name }}</span>
        </button>
      </aside>

      <!-- Right Column: Sub Categories, Search & Items -->
      <div class="flex-1 flex flex-col min-w-0 bg-neutral-900/50">
        <!-- Breadcrumbs & Search -->
        <div class="flex items-center justify-between px-6 py-4 gap-6 bg-neutral-900 border-b border-neutral-800 shrink-0">
          <!-- Breadcrumbs -->
          <nav class="flex items-center gap-2 overflow-x-auto scrollbar-hide py-1">
            <button
              @click="handleRootSelect('all')"
              class="text-neutral-500 hover:text-white transition-colors flex items-center gap-1 shrink-0"
            >
              <svg xmlns="http://www.w3.org/2000/svg" class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="m3 9 9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/><polyline points="9 22 9 12 15 12 15 22"/></svg>
            </button>

            <template v-for="(cat, index) in categoryPath" :key="cat.categoryId">
              <span class="text-neutral-700 shrink-0">/</span>
              <button
                @click="navigateToPathLevel(index)"
                :class="[
                  'text-sm font-medium transition-colors whitespace-nowrap px-2 py-1 rounded-lg',
                  index === categoryPath.length - 1
                    ? 'text-white bg-neutral-800'
                    : 'text-neutral-500 hover:text-neutral-300 hover:bg-neutral-800/50'
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
        <div v-if="currentChildCategories.length > 0" class="flex items-center px-6 py-4 gap-3 bg-neutral-900 border-b border-neutral-800/50 overflow-x-auto scrollbar-hide shrink-0">
          <div class="text-[10px] font-bold text-neutral-600 uppercase tracking-widest mr-2 shrink-0">Explore</div>
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
      <div class="flex-1 overflow-y-auto scrollbar-thin">
        <!-- Loading State -->
        <div
          v-if="loading"
          class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 gap-6"
        >
          <div
            v-for="i in 10"
            :key="i"
            class="bg-neutral-800/30 rounded-3xl overflow-hidden border border-neutral-700/20"
          >
            <div class="aspect-square bg-neutral-700/30 animate-pulse"></div>
            <div class="p-5 space-y-3">
              <div
                class="h-5 bg-neutral-700/30 rounded-xl animate-pulse w-3/4"
              ></div>
              <div
                class="h-6 bg-neutral-700/30 rounded-xl animate-pulse w-1/2"
              ></div>
            </div>
          </div>
        </div>

        <!-- Empty State -->
        <div
          v-else-if="filteredItems.length === 0"
          class="flex flex-col items-center justify-center h-96 text-neutral-500"
        >
          <div
            class="w-28 h-28 rounded-3xl bg-gradient-to-br from-neutral-800/80 to-neutral-900/80 flex items-center justify-center mb-8 border border-neutral-700/30"
          >
            <svg
              xmlns="http://www.w3.org/2000/svg"
              class="w-14 h-14 opacity-20"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              stroke-width="1.5"
              stroke-linecap="round"
              stroke-linejoin="round"
            >
              <path d="M17 8h1a4 4 0 1 1 0 8h-1" />
              <path d="M3 8h14v9a4 4 0 0 1-4 4H7a4 4 0 0 1-4-4Z" />
              <line x1="6" x2="6" y1="2" y2="4" />
              <line x1="10" x2="10" y1="2" y2="4" />
            </svg>
          </div>
          <p class="text-xl font-bold text-neutral-300 mb-2">No items found</p>
          <p class="text-sm text-neutral-500">
            Try selecting a different category or search term
          </p>
        </div>

        <!-- Items Grid -->
        <div
          v-else
          class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 gap-6 pb-8"
        >
          <button
            v-for="(item, idx) in filteredItems"
            :key="item.menuItemId"
            @click="handleAddToCart(item)"
            :disabled="!item.isAvailable"
            class="group relative bg-neutral-800 rounded-2xl overflow-hidden border border-neutral-700 hover:border-primary-500 text-left disabled:opacity-50 disabled:cursor-not-allowed transition-all duration-500 reveal-item"
            :style="{ '--delay': (idx % 10) * 50 + 'ms' }"
          >
            <!-- Image Container -->
            <div class="aspect-square bg-neutral-800 relative overflow-hidden">
              <img
                v-if="item.imageUrl"
                :src="item.imageUrl"
                :alt="item.name"
                loading="lazy"
                decoding="async"
                class="w-full h-full object-cover"
              />
              <div
                v-else
                class="absolute inset-0 flex flex-col items-center justify-center bg-gradient-to-br from-neutral-800 via-neutral-850 to-neutral-900 gap-3"
              >
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  class="w-12 h-12 text-neutral-700/50"
                  viewBox="0 0 24 24"
                  fill="none"
                  stroke="currentColor"
                  stroke-width="1.5"
                  stroke-linecap="round"
                  stroke-linejoin="round"
                >
                  <path d="M17 8h1a4 4 0 1 1 0 8h-1" />
                  <path d="M3 8h14v9a4 4 0 0 1-4 4H7a4 4 0 0 1-4-4Z" />
                </svg>
                <span
                  class="text-xs text-neutral-600 font-medium px-3 text-center"
                  >{{ item.name }}</span
                >
              </div>

              <!-- Enhanced Gradient Overlay -->
              <div
                class="absolute inset-0 bg-gradient-to-t from-neutral-900 via-transparent to-transparent"
              ></div>

              <!-- Price Badge -->
              <div class="absolute bottom-4 left-4">
                <span
                  class="bg-primary-500 text-white px-3 py-1.5 rounded-xl text-sm font-bold"
                >
                  ${{ item.basePrice.toFixed(2) }}
                </span>
              </div>

              <!-- Availability Badge -->
              <div
                v-if="!item.isAvailable"
                class="absolute inset-0 bg-neutral-900/90 flex items-center justify-center"
              >
                <span
                  class="bg-red-500/20 text-red-400 border border-red-500/40 px-4 py-2 rounded-xl text-xs font-bold uppercase"
                >
                  Sold Out
                </span>
              </div>

            <!-- Add Button Overlay -->
            <div
              v-if="item.isAvailable"
              class="absolute bottom-3 right-3 opacity-0 group-hover:opacity-100 transform translate-y-2 group-hover:translate-y-0 transition-all duration-300"
            >
              <div class="bg-white text-primary-600 p-2.5 rounded-xl shadow-lg">
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  class="w-6 h-6"
                  viewBox="0 0 24 24"
                  fill="none"
                  stroke="currentColor"
                  stroke-width="2.5"
                  stroke-linecap="round"
                  stroke-linejoin="round"
                >
                  <path d="M12 5v14" />
                  <path d="M5 12h14" />
                </svg>
              </div>
            </div>
          </div>

          <!-- Content -->
          <div class="p-5 flex flex-col items-start gap-1">
            <h3
              class="font-bold text-neutral-100 text-base leading-snug line-clamp-2 h-12"
            >
              {{ item.name }}
            </h3>
            <div class="flex items-center gap-2 mt-2">
              <span class="w-1.5 h-1.5 rounded-full bg-primary-500"></span>
              <span class="text-[10px] font-bold text-neutral-500 uppercase tracking-widest">{{ item.category?.name || 'Item' }}</span>
            </div>
          </div>
        </button>
      </div>
    </div>
  </div>
</div>

    <!-- Variant Selection Modal -->
    <div
      v-if="showVariantModal && selectedItem"
      class="fixed inset-0 z-50 flex items-center justify-center bg-black/80 p-4"
    >
      <div
        class="bg-neutral-800 rounded-2xl shadow-xl w-full max-w-sm overflow-hidden border border-neutral-700 flex flex-col transform transition-all"
      >
        <div
          class="p-5 border-b border-neutral-700/50 text-center bg-neutral-900/50"
        >
          <h3 class="text-xl font-bold text-white">Select Size</h3>
          <p class="text-neutral-400 text-sm mt-1">
            for <span class="text-primary-400">{{ selectedItem.name }}</span>
          </p>
        </div>

        <div class="p-6">
          <div class="grid grid-cols-3 gap-3">
            <button
              v-for="variant in selectedItem.variants"
              :key="variant.variantId"
              @click="selectedVariant = variant"
              :class="[
                'aspect-square rounded-2xl flex flex-col items-center justify-center gap-1 border-2 transition-all relative overflow-hidden group',
                selectedVariant?.variantId === variant.variantId
                  ? 'bg-primary-600 border-primary-500 text-white shadow-lg shadow-primary-900/50'
                  : 'bg-neutral-800 border-neutral-700 text-neutral-300 hover:border-neutral-500 hover:bg-neutral-750',
              ]"
            >
              <span class="text-2xl font-bold">{{ variant.size }}</span>
              <span
                class="text-xs opacity-80 group-hover:opacity-100 transition-opacity"
                >${{ variant.price.toFixed(2) }}</span
              >

              <!-- Selected Checkmark -->
              <div
                v-if="selectedVariant?.variantId === variant.variantId"
                class="absolute top-2 right-2 text-white"
              >
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  class="w-4 h-4"
                  viewBox="0 0 24 24"
                  fill="none"
                  stroke="currentColor"
                  stroke-width="3"
                  stroke-linecap="round"
                  stroke-linejoin="round"
                >
                  <polyline points="20 6 9 17 4 12"></polyline>
                </svg>
              </div>
            </button>
          </div>
        </div>

        <div
          class="p-5 border-t border-neutral-700 bg-neutral-900/30 flex gap-3"
        >
          <button
            @click="showVariantModal = false"
            class="flex-1 py-3.5 rounded-xl border border-neutral-600 text-neutral-300 font-medium hover:bg-neutral-800 hover:text-white transition-colors"
          >
            Cancel
          </button>
          <button
            @click="confirmVariantSelection"
            :disabled="!selectedVariant"
            class="flex-1 py-3.5 rounded-xl bg-primary-600 text-white font-bold hover:bg-primary-500 disabled:opacity-50 disabled:cursor-not-allowed shadow-lg shadow-primary-900/20 transition-all"
          >
            Continue
            <span v-if="selectedVariant"
              >(${{ selectedVariant.price.toFixed(2) }})</span
            >
          </button>
        </div>
      </div>
    </div>

    <!-- Add-On Modal -->
    <div
      v-if="showAddOnModal && selectedItem"
      class="fixed inset-0 z-50 flex items-center justify-center bg-black/80 p-4"
      @click.self="closeAddOnModal"
    >
      <div
        class="bg-neutral-800 rounded-2xl shadow-xl w-full max-w-lg max-h-[85vh] overflow-hidden border border-neutral-700 flex flex-col"
      >
        <!-- Header -->
        <div class="p-4 border-b border-neutral-700 flex items-center gap-4">
          <div
            class="w-16 h-16 rounded-xl bg-neutral-700 overflow-hidden flex-shrink-0"
          >
            <img
              v-if="selectedItem.imageUrl"
              :src="selectedItem.imageUrl"
              class="w-full h-full object-cover"
            />
            <div
              v-else
              class="w-full h-full flex items-center justify-center text-neutral-500"
            >
              <svg
                xmlns="http://www.w3.org/2000/svg"
                class="w-8 h-8"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                stroke-width="1.5"
              >
                <path d="M17 8h1a4 4 0 1 1 0 8h-1" />
                <path d="M3 8h14v9a4 4 0 0 1-4 4H7a4 4 0 0 1-4-4Z" />
              </svg>
            </div>
          </div>
          <div class="flex-1">
            <h3 class="text-lg font-bold text-white">
              {{ selectedItem.name }}
              <span v-if="selectedVariant">({{ selectedVariant.size }})</span>
            </h3>
            <p class="text-primary-400 font-semibold">
              ${{
                (selectedVariant
                  ? selectedVariant.price
                  : selectedItem.basePrice
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
              class="w-5 h-5"
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

        <!-- Add-ons List -->
        <div class="flex-1 overflow-y-auto p-6">
          <div
            v-if="addOnsLoading"
            class="flex flex-col items-center justify-center py-12 gap-4"
          >
            <div
              class="w-10 h-10 border-2 border-primary-500/20 border-t-primary-500 rounded-full animate-spin"
            ></div>
            <span class="text-xs font-bold text-neutral-500 uppercase tracking-widest">Loading options...</span>
          </div>

          <div v-else-if="addOnList.length > 0" class="space-y-6">
            <div>
              <div class="flex items-center gap-2 mb-4">
                <span class="w-1 h-4 bg-primary-500 rounded-full"></span>
                <h4 class="text-sm font-bold text-white uppercase tracking-wider">Customize Your Order</h4>
              </div>
              <div class="grid grid-cols-2 gap-3">
                <button
                  v-for="addOn in addOnList"
                  :key="addOn.addonId"
                  @click="toggleAddOn(addOn)"
                  :class="[
                    'relative p-4 rounded-2xl border-2 text-left transition-all duration-200 group overflow-hidden',
                    isAddOnSelected(addOn.addonId)
                      ? 'bg-primary-600/10 border-primary-500 shadow-[0_0_20px_rgba(249,115,22,0.1)] scale-[1.02]'
                      : 'bg-neutral-800/50 border-neutral-700/50 text-neutral-400 hover:border-neutral-600 hover:bg-neutral-800 scale-100',
                  ]"
                >
                  <!-- Selection Badge -->
                  <div 
                    v-if="isAddOnSelected(addOn.addonId)"
                    class="absolute -top-1 -right-1"
                  >
                    <div class="bg-primary-500 text-white p-1 rounded-bl-xl shadow-lg">
                      <svg xmlns="http://www.w3.org/2000/svg" class="w-3 h-3" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="4" stroke-linecap="round" stroke-linejoin="round"><polyline points="20 6 9 17 4 12"/></svg>
                    </div>
                  </div>

                  <div class="flex flex-col gap-1">
                    <span :class="['text-sm font-bold transition-colors', isAddOnSelected(addOn.addonId) ? 'text-white' : 'text-neutral-300 group-hover:text-white']">
                      {{ addOn.name }}
                    </span>
                    <span
                      :class="[
                        'text-xs font-black font-mono',
                        isAddOnSelected(addOn.addonId)
                          ? 'text-primary-400'
                          : 'text-neutral-500',
                      ]"
                    >
                      +${{ addOn.price.toFixed(2) }}
                    </span>
                  </div>
                </button>
              </div>
            </div>

            <div class="pt-2">
              <div class="flex items-center gap-2 mb-4">
                <span class="w-1 h-4 bg-primary-500 rounded-full"></span>
                <h4 class="text-sm font-bold text-white uppercase tracking-wider">Special Instructions</h4>
              </div>
              <textarea
                v-model="itemNotes"
                placeholder="Ex: Less ice, extra hot, no sugar..."
                rows="3"
                class="w-full px-5 py-4 rounded-2xl bg-neutral-900 border-2 border-neutral-700/50 text-white placeholder-neutral-600 focus:outline-none focus:ring-4 focus:ring-primary-500/10 focus:border-primary-500 transition-all font-medium text-sm resize-none"
              ></textarea>
            </div>
          </div>

          <div v-else class="text-center py-16">
            <div class="w-16 h-16 bg-neutral-900 rounded-full flex items-center justify-center mx-auto mb-4 border border-neutral-800">
               <svg xmlns="http://www.w3.org/2000/svg" class="w-6 h-6 text-neutral-700" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 20h9"/><path d="M16.5 3.5a2.121 2.121 0 0 1 3 3L7 19l-4 1 1-4L16.5 3.5z"/></svg>
            </div>
            <p class="text-neutral-500 text-sm font-bold uppercase tracking-widest">No add-ons available</p>
          </div>
        </div>

        <!-- Sticky Footer -->
        <div class="p-6 border-t border-neutral-700/50 bg-neutral-900/80 backdrop-blur-md">
          <div class="flex items-center justify-between mb-6">
            <div class="flex flex-col">
              <span class="text-[10px] font-black text-neutral-500 uppercase tracking-widest leading-none mb-1">Current Balance</span>
              <div class="flex items-baseline gap-2">
                <span class="text-3xl font-black text-white">${{ ((selectedVariant?.price || selectedItem.basePrice) + addOnTotal).toFixed(2) }}</span>
                <span v-if="selectedAddOns.length > 0" class="text-xs font-bold text-primary-400">+ {{ selectedAddOns.length }} items</span>
              </div>
            </div>
            <div class="flex gap-2">
               <button
                @click="closeAddOnModal"
                class="px-6 py-4 rounded-2xl border-2 border-neutral-700 text-sm font-bold text-neutral-400 hover:text-white hover:bg-neutral-800 transition-all"
              >
                Cancel
              </button>
              <button
                @click="confirmAddToCart"
                class="px-8 py-4 rounded-2xl bg-primary-600 text-white text-sm font-black uppercase tracking-wider hover:bg-primary-500 shadow-lg shadow-primary-900/30 active:scale-95 transition-all"
              >
                Add to Cart
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </NuxtLayout>
</template>

<script setup lang="ts">
import { h, ref, computed, onMounted, shallowRef, watch } from "vue";
import { useDebounceFn } from "@vueuse/core";

definePageMeta({
  layout: false,
});

const { get } = useApi();
const { addToCart, addToCartWithAddOns } = useCart();

// Add-on Types
interface AddOn {
  addonId: number;
  name: string;
  price: number;
}

// Types
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
  categoryId: number;
  category?: Category;
  variants?: Variant[];
}

// Use useState for persistent caching across navigation
const cachedCategories = useState<Category[]>("pos-categories", () => []);
const cachedMenuItems = useState<MenuItem[]>("pos-menu-items", () => []);

const selectedCategory = ref<number | "all">("all");
const expandedIds = ref<number[]>([]);
const searchQuery = ref("");
const debouncedSearchQuery = ref("");
const loading = ref(true);
const showToast = ref(false);
const toastMessage = ref("");

const updateDebouncedSearch = useDebounceFn((val: string) => {
  debouncedSearchQuery.value = val;
}, 300);

watch(searchQuery, (newVal) => {
  updateDebouncedSearch(newVal);
});

// Add-on Modal State
const showAddOnModal = ref(false);
const showVariantModal = ref(false);
const selectedItem = ref<MenuItem | null>(null);
const selectedVariant = ref<Variant | null>(null);
const addOnList = shallowRef<AddOn[]>([]);
const selectedAddOns = ref<AddOn[]>([]);
const itemNotes = ref("");
const addOnsLoading = ref(false);

// Basic data refs
const rootCategories = shallowRef<Category[]>([]);
const menuItems = shallowRef<MenuItem[]>([]);

// Navigation State
const selectedRootId = ref<number | "all">("all");
const categoryPath = ref<Category[]>([]);

// Computed Navigation Properties
const currentCategory = computed(() => {
  if (categoryPath.value.length === 0) return null;
  return categoryPath.value[categoryPath.value.length - 1];
});

const currentChildCategories = computed(() => {
  if (selectedRootId.value === "all" && categoryPath.value.length === 0) return [];
  return currentCategory.value?.children || [];
});

const activeCategoryId = computed(() => {
  return currentCategory.value?.categoryId || "all";
});

// Navigation handlers
const handleRootSelect = (id: number | "all") => {
  if (id === "all") {
    selectedRootId.value = "all";
    categoryPath.value = [];
    return;
  }

  const root = rootCategories.value.find((c) => c.categoryId === id);
  if (root) {
    selectedRootId.value = id;
    categoryPath.value = [root];
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

// Memoized descendant ID cache (keep existing logic)
const descendantCache = new Map<number, number[]>();

// Update filteredItems to use activeCategoryId
const filteredItems = computed(() => {
  let result = menuItems.value;

  // 1. Category Filter
  if (activeCategoryId.value !== "all") {
    const targetId = Number(activeCategoryId.value);
    let validIds: number[] = [targetId];

    // Reuse existing efficient caching logic...
    if (descendantCache.has(targetId)) {
      validIds = descendantCache.get(targetId)!;
    } else {
      const getDescendants = (cats: Category[], id: number): number[] => {
        let ids: number[] = [id]; // include self
        const findCat = (list: Category[]): Category | null => {
          for (const c of list) {
            if (c.categoryId === id) return c;
            if (c.children) {
              const found = findCat(c.children);
              if (found) return found;
            }
          }
          return null;
        };

        const target = findCat(rootCategories.value);
        if (!target) return ids;

        const collect = (c: Category) => {
          ids.push(c.categoryId);
          if (c.children) c.children.forEach(collect);
        };
        if (target.children) target.children.forEach(collect);

        return ids;
      };
      validIds = getDescendants(rootCategories.value, targetId);
      descendantCache.set(targetId, validIds);
    }

    result = result.filter(
      (item) =>
        validIds.includes(item.categoryId) ||
        (item.category && validIds.includes(item.category.categoryId)),
    );
  }

  // 2. Text Search
  if (debouncedSearchQuery.value) {
    const q = debouncedSearchQuery.value.toLowerCase();
    result = result.filter((item) => item.name.toLowerCase().includes(q));
  }

  return result;
});

const handleAddToCart = (item: MenuItem) => {
  selectedItem.value = item;
  selectedAddOns.value = [];
  itemNotes.value = "";
  selectedVariant.value = null;

  // Check for variants
  if (item.variants && item.variants.length > 0) {
    // Select middle size or first as default logic if needed, or just let user pick
    // For now, no default selection or select first
    selectedVariant.value = item.variants?.[0] || null;
    showVariantModal.value = true;
  } else {
    // No variants, go to add-ons
    showAddOnModal.value = true;
  }
};

const confirmVariantSelection = () => {
  showVariantModal.value = false;
  showAddOnModal.value = true;
};

const handleQuickAdd = (item: MenuItem) => {
  // Quick add without add-ons (e.g., via modifier key)
  addToCart(item);
  toastMessage.value = `${item.name} added to cart`;
  showToast.value = true;
  setTimeout(() => {
    showToast.value = false;
  }, 2500);
};

const toggleAddOn = (addOn: AddOn) => {
  const index = selectedAddOns.value.findIndex(
    (a) => a.addonId === addOn.addonId,
  );
  if (index === -1) {
    selectedAddOns.value.push(addOn);
  } else {
    selectedAddOns.value.splice(index, 1);
  }
};

const isAddOnSelected = (addOnId: number) => {
  return selectedAddOns.value.some((a) => a.addonId === addOnId);
};

const addOnTotal = computed(() => {
  return selectedAddOns.value.reduce((sum, a) => sum + a.price, 0);
});

const confirmAddToCart = () => {
  if (!selectedItem.value) return;
  const itemToAdd = { ...selectedItem.value };

  if (
    selectedAddOns.value.length > 0 ||
    itemNotes.value ||
    selectedVariant.value
  ) {
    if (selectedVariant.value) {
      itemToAdd.basePrice = selectedVariant.value.price;
      itemToAdd.name = `${itemToAdd.name} (${selectedVariant.value.size})`;
    }
    addToCartWithAddOns(itemToAdd, [...selectedAddOns.value], itemNotes.value);
  } else {
    addToCart(selectedItem.value);
  }

  toastMessage.value = `${itemToAdd.name} added to cart`;
  showToast.value = true;
  setTimeout(() => {
    showToast.value = false;
  }, 2500);

  showAddOnModal.value = false;
  selectedItem.value = null;
  selectedAddOns.value = [];
  itemNotes.value = "";
};

const closeAddOnModal = () => {
  showAddOnModal.value = false;
  selectedItem.value = null;
  selectedAddOns.value = [];
  itemNotes.value = "";
};

const fetchData = async () => {
  // Use cached data if available for instant display
  if (cachedCategories.value.length > 0 && cachedMenuItems.value.length > 0) {
    rootCategories.value = cachedCategories.value;
    menuItems.value = cachedMenuItems.value;
    loading.value = false;
    // Refresh in background
    refreshDataInBackground();
    return;
  }

  loading.value = true;
  try {
    const [cats, items] = await Promise.all([
      get<Category[]>("/categories"),
      get<MenuItem[]>("/menu-items"),
    ]);
    rootCategories.value = cats || [];
    menuItems.value = items || [];
    // Cache for future visits
    cachedCategories.value = cats || [];
    cachedMenuItems.value = items || [];
  } catch (err) {
    console.error("Failed to fetch POS data", err);
  } finally {
    loading.value = false;
  }
};

const refreshDataInBackground = async () => {
  try {
    const [cats, items] = await Promise.all([
      get<Category[]>("/categories"),
      get<MenuItem[]>("/menu-items"),
    ]);
    if (cats) {
      rootCategories.value = cats;
      cachedCategories.value = cats;
      descendantCache.clear();
    }
    if (items) {
      menuItems.value = items;
      cachedMenuItems.value = items;
    }
  } catch (err) {
    // Silent fail for background refresh
  }
};

const fetchAddOns = async () => {
  addOnsLoading.value = true;
  try {
    const data = await get<AddOn[]>("/addons");
    addOnList.value = data || [];
  } catch (e) {
    console.error("Failed to fetch add-ons:", e);
  } finally {
    addOnsLoading.value = false;
  }
};

onMounted(() => {
  fetchData();
  fetchAddOns();
});
</script>

<style scoped>
.reveal-item {
  contain: content;
  opacity: 0;
  transform: translateY(20px);
  animation: revealIn 0.6s cubic-bezier(0.2, 0.8, 0.2, 1) forwards;
  animation-delay: var(--delay);
}

@keyframes revealIn {
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* Optimize scrolling on heavy lists */
.scrollbar-thin {
  content-visibility: auto;
  contain-intrinsic-size: 0 500px;
}

.toast-enter-active {
  animation: toast-in 0.5s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.toast-leave-active {
  animation: toast-out 0.3s cubic-bezier(0.4, 0, 1, 1);
}

@keyframes toast-in {
  from {
    opacity: 0;
    transform: translate(-50%, -30px) scale(0.9);
  }
  to {
    opacity: 1;
    transform: translate(-50%, 0) scale(1);
  }
}

@keyframes toast-out {
  from {
    opacity: 1;
    transform: translate(-50%, 0) scale(1);
  }
  to {
    opacity: 0;
    transform: translate(-50%, -20px) scale(0.95);
  }
}
</style>
