<template>
  <NuxtLayout name="admin">
    <div class="space-y-6 relative">
      <UiBreadcrumb :items="[{ label: 'Menu' }]" />

      <!-- Header with Stats -->
      <div class="flex items-center justify-between">
        <div>
          <h2 class="text-2xl font-bold text-neutral-900 dark:text-white">
            Menu Management
          </h2>
          <p class="text-sm text-neutral-500 mt-1" v-if="!loading">
            {{ filteredItems.length }} of {{ menuItems.length }} items
            <span v-if="filterCategory" class="text-primary-600"
              >• Filtered by category</span
            >
          </p>
        </div>
        <div class="flex items-center gap-3">
          <!-- Export/Import Actions -->
          <div class="flex items-center gap-2">
            <button
              @click="downloadMenu('excel')"
              class="p-2 hover:bg-neutral-100 dark:hover:bg-neutral-800 rounded-xl text-neutral-500 transition-colors"
              title="Export Menu as Excel"
            >
              <svg
                xmlns="http://www.w3.org/2000/svg"
                class="w-5 h-5"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                stroke-width="2"
              >
                <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4" />
                <polyline points="7 10 12 15 17 10" />
                <line x1="12" y1="15" x2="12" y2="3" />
              </svg>
            </button>
            <button
              @click="triggerFileInput"
              class="p-2 hover:bg-neutral-100 dark:hover:bg-neutral-800 rounded-xl text-neutral-500 transition-colors"
              title="Bulk Import Menu Items"
            >
              <svg
                xmlns="http://www.w3.org/2000/svg"
                class="w-5 h-5"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                stroke-width="2"
              >
                <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4" />
                <polyline points="17 8 12 3 7 8" />
                <line x1="12" y1="3" x2="12" y2="15" />
              </svg>
            </button>
            <input
              type="file"
              ref="fileInput"
              class="hidden"
              accept=".xlsx,.csv"
              @change="onFileChange"
            />
          </div>

          <button
            @click="openCreateModal"
            class="bg-primary-600 hover:bg-primary-700 text-white px-4 py-2 rounded-xl text-sm font-medium transition-all flex items-center gap-2 shadow-lg shadow-primary-500/20 hover:shadow-xl hover:shadow-primary-500/30 active:scale-95"
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
              <path d="M5 12h14" />
              <path d="M12 5v14" />
            </svg>
            Add Item
          </button>
        </div>
      </div>

      <!-- Filters with Quick Stats -->
      <div
        class="bg-white dark:bg-neutral-900 p-4 rounded-xl border border-neutral-200 dark:border-neutral-800 shadow-sm"
      >
        <div
          class="flex flex-col lg:flex-row items-start lg:items-center gap-4"
        >
          <!-- Search -->
          <div class="relative flex-1 w-full lg:max-w-md">
            <svg
              xmlns="http://www.w3.org/2000/svg"
              class="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-neutral-400"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              stroke-width="2"
              stroke-linecap="round"
              stroke-linejoin="round"
            >
              <circle cx="11" cy="11" r="8" />
              <path d="m21 21-4.3-4.3" />
            </svg>
            <input
              v-model="searchQuery"
              type="text"
              placeholder="Search menu items..."
              class="w-full pl-10 pr-10 py-2 bg-neutral-100 dark:bg-neutral-800 border-none rounded-lg text-sm focus:ring-2 focus:ring-primary-500 placeholder-neutral-500 transition-shadow"
            />
            <button
              v-if="searchQuery"
              @click="searchQuery = ''"
              class="absolute right-3 top-1/2 -translate-y-1/2 text-neutral-400 hover:text-neutral-600"
            >
              <svg
                xmlns="http://www.w3.org/2000/svg"
                class="w-4 h-4"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                stroke-width="2"
              >
                <line x1="18" y1="6" x2="6" y2="18"></line>
                <line x1="6" y1="6" x2="18" y2="18"></line>
              </svg>
            </button>
          </div>

          <!-- Category Filter -->
          <div class="flex items-center gap-3 w-full lg:w-auto">
            <select
              v-model="filterCategory"
              class="flex-1 lg:flex-none bg-neutral-100 dark:bg-neutral-800 border-none rounded-lg text-sm px-4 py-2 focus:ring-2 focus:ring-primary-500 min-w-[200px] transition-shadow"
            >
              <option value="">All Categories</option>
              <option
                v-for="cat in flattenedCategories"
                :key="cat.id"
                :value="cat.id"
              >
                {{ cat.name }}
              </option>
            </select>

            <!-- View Toggle -->
            <div
              class="flex items-center gap-1 bg-neutral-100 dark:bg-neutral-800 p-1 rounded-lg"
            >
              <button
                @click="viewMode = 'grid'"
                :class="[
                  'p-1.5 rounded transition-colors',
                  viewMode === 'grid'
                    ? 'bg-white dark:bg-neutral-700 text-primary-600'
                    : 'text-neutral-400 hover:text-neutral-600',
                ]"
                title="Grid View"
              >
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  class="w-4 h-4"
                  viewBox="0 0 24 24"
                  fill="none"
                  stroke="currentColor"
                  stroke-width="2"
                >
                  <rect x="3" y="3" width="7" height="7"></rect>
                  <rect x="14" y="3" width="7" height="7"></rect>
                  <rect x="14" y="14" width="7" height="7"></rect>
                  <rect x="3" y="14" width="7" height="7"></rect>
                </svg>
              </button>
              <button
                @click="viewMode = 'list'"
                :class="[
                  'p-1.5 rounded transition-colors',
                  viewMode === 'list'
                    ? 'bg-white dark:bg-neutral-700 text-primary-600'
                    : 'text-neutral-400 hover:text-neutral-600',
                ]"
                title="List View"
              >
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  class="w-4 h-4"
                  viewBox="0 0 24 24"
                  fill="none"
                  stroke="currentColor"
                  stroke-width="2"
                >
                  <line x1="8" y1="6" x2="21" y2="6"></line>
                  <line x1="8" y1="12" x2="21" y2="12"></line>
                  <line x1="8" y1="18" x2="21" y2="18"></line>
                  <line x1="3" y1="6" x2="3.01" y2="6"></line>
                  <line x1="3" y1="12" x2="3.01" y2="12"></line>
                  <line x1="3" y1="18" x2="3.01" y2="18"></line>
                </svg>
              </button>
            </div>
          </div>
        </div>

        <!-- Quick Stats Bar -->
        <div
          v-if="!loading"
          class="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4 mt-4 pt-4 border-t border-neutral-200 dark:border-neutral-800"
        >
          <div class="flex items-center gap-4">
            <div class="flex items-center gap-2 text-xs">
              <div class="w-2 h-2 rounded-full bg-success-500"></div>
              <span class="text-neutral-600 dark:text-neutral-400"
                >{{ activeItemsCount }} Active</span
              >
            </div>
            <div class="flex items-center gap-2 text-xs">
              <div class="w-2 h-2 rounded-full bg-error-500"></div>
              <span class="text-neutral-600 dark:text-neutral-400"
                >{{ inactiveItemsCount }} Inactive</span
              >
            </div>
            <div class="flex items-center gap-2 text-xs">
              <div class="w-2 h-2 rounded-full bg-blue-500"></div>
              <span class="text-neutral-600 dark:text-neutral-400"
                >{{ categoriesCount }} Categories</span
              >
            </div>
          </div>

          <!-- Items Per Page Selector -->
          <div class="flex items-center gap-2">
            <label class="text-xs text-neutral-600 dark:text-neutral-400"
              >Show:</label
            >
            <select
              v-model.number="itemsPerPage"
              class="text-xs bg-neutral-100 dark:bg-neutral-800 border-none rounded-lg px-2 py-1 focus:ring-2 focus:ring-primary-500"
            >
              <option :value="12">12</option>
              <option :value="24">24</option>
              <option :value="48">48</option>
              <option :value="96">96</option>
            </select>
            <span class="text-xs text-neutral-600 dark:text-neutral-400"
              >per page</span
            >
          </div>
        </div>
      </div>

      <!-- Loading State with Skeletons -->
      <div
        v-if="loading"
        :class="
          viewMode === 'grid'
            ? 'grid grid-cols-1 md:grid-cols-3 lg:grid-cols-4 gap-6'
            : 'space-y-3'
        "
      >
        <div
          v-for="i in 8"
          :key="i"
          class="bg-white dark:bg-neutral-900 rounded-xl border border-neutral-200 dark:border-neutral-800 overflow-hidden"
        >
          <div
            v-if="viewMode === 'grid'"
            class="h-48 bg-neutral-200 dark:bg-neutral-800 animate-pulse"
          ></div>
          <div class="p-4">
            <div
              class="h-4 bg-neutral-200 dark:bg-neutral-800 rounded animate-pulse mb-2"
            ></div>
            <div
              class="h-3 bg-neutral-200 dark:bg-neutral-800 rounded animate-pulse w-2/3"
            ></div>
          </div>
        </div>
      </div>

      <!-- Empty State -->
      <div
        v-else-if="filteredItems.length === 0"
        class="bg-white dark:bg-neutral-900 rounded-xl border border-neutral-200 dark:border-neutral-800 p-12 text-center"
      >
        <div
          class="w-20 h-20 mx-auto mb-4 rounded-full bg-neutral-100 dark:bg-neutral-800 flex items-center justify-center"
        >
          <svg
            xmlns="http://www.w3.org/2000/svg"
            class="w-10 h-10 text-neutral-400"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            stroke-width="2"
          >
            <circle cx="11" cy="11" r="8" />
            <path d="m21 21-4.3-4.3" />
          </svg>
        </div>
        <h3 class="text-lg font-semibold text-neutral-900 dark:text-white mb-2">
          No menu items found
        </h3>
        <p class="text-sm text-neutral-500 mb-4">
          {{
            searchQuery || filterCategory
              ? "Try adjusting your filters"
              : "Get started by adding your first menu item"
          }}
        </p>
        <button
          v-if="!searchQuery && !filterCategory"
          @click="openCreateModal"
          class="inline-flex items-center gap-2 bg-primary-600 hover:bg-primary-700 text-white px-4 py-2 rounded-lg text-sm font-medium transition-colors"
        >
          <svg
            xmlns="http://www.w3.org/2000/svg"
            class="w-4 h-4"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            stroke-width="2"
          >
            <path d="M5 12h14" />
            <path d="M12 5v14" />
          </svg>
          Add First Item
        </button>
        <button
          v-else
          @click="clearFilters"
          class="inline-flex items-center gap-2 bg-neutral-200 dark:bg-neutral-800 hover:bg-neutral-300 dark:hover:bg-neutral-700 text-neutral-900 dark:text-white px-4 py-2 rounded-lg text-sm font-medium transition-colors"
        >
          Clear Filters
        </button>
      </div>

      <!-- Grid View -->
      <div
        v-else-if="viewMode === 'grid'"
        class="grid grid-cols-1 md:grid-cols-3 lg:grid-cols-4 gap-6"
      >
        <div
          v-for="item in paginatedItems"
          :key="item.menuItemId"
          class="bg-white dark:bg-neutral-900 rounded-xl border border-neutral-200 dark:border-neutral-800 overflow-hidden hover:shadow-xl hover:border-primary-500/50 transition-all duration-300 group cursor-pointer"
          @click="openEditModal(item)"
        >
          <div
            class="h-48 bg-neutral-100 dark:bg-neutral-800 flex items-center justify-center relative overflow-hidden"
          >
            <img
              v-if="item.imageUrl"
              :src="item.imageUrl"
              :alt="item.name"
              loading="lazy"
              class="w-full h-full object-cover transition-transform duration-500 group-hover:scale-110"
            />
            <svg
              v-else
              xmlns="http://www.w3.org/2000/svg"
              class="w-12 h-12 text-neutral-300 transition-transform group-hover:scale-110"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              stroke-width="2"
              stroke-linecap="round"
              stroke-linejoin="round"
            >
              <rect width="18" height="18" x="3" y="3" rx="2" ry="2" />
              <circle cx="9" cy="9" r="2" />
              <path d="m21 15-3.086-3.086a2 2 0 0 0-2.828 0L6 21" />
            </svg>

            <!-- Status Badge -->
            <div class="absolute top-2 right-2">
              <span
                :class="[
                  'px-2 py-1 rounded-lg text-xs font-bold uppercase tracking-wide backdrop-blur-md shadow-lg',
                  item.isAvailable
                    ? 'bg-success-500/90 text-white'
                    : 'bg-error-500/90 text-white',
                ]"
              >
                {{ item.isAvailable ? "Active" : "Inactive" }}
              </span>
            </div>
          </div>
          <div class="p-4">
            <div class="flex justify-between items-start mb-2">
              <h3
                class="font-semibold text-neutral-900 dark:text-white truncate pr-2 group-hover:text-primary-600 transition-colors"
                :title="item.name"
              >
                {{ item.name }}
              </h3>
              <span
                class="text-sm font-bold text-primary-600 dark:text-primary-400 whitespace-nowrap"
                >${{ item.basePrice.toFixed(2) }}</span
              >
            </div>
            <div
              class="flex items-center gap-2 text-xs text-neutral-500 dark:text-neutral-400 mb-4"
            >
              <svg
                xmlns="http://www.w3.org/2000/svg"
                class="w-3 h-3"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                stroke-width="2"
              >
                <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"></path>
              </svg>
              {{ item.category?.name || "Uncategorized" }}
            </div>

            <div
              class="flex items-center justify-between pt-3 border-t border-neutral-100 dark:border-neutral-800"
            >
              <button
                @click.stop="deleteItem(item.menuItemId)"
                class="text-xs text-neutral-400 hover:text-error-600 transition-colors font-medium px-2 py-1 rounded hover:bg-error-50 dark:hover:bg-error-900/20"
              >
                Delete
              </button>
              <button
                @click.stop="openEditModal(item)"
                class="text-xs font-medium text-primary-600 hover:text-primary-700 dark:text-primary-400 dark:hover:text-primary-300 transition-colors px-2 py-1 rounded hover:bg-primary-50 dark:hover:bg-primary-900/20"
              >
                Edit Details →
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- List View -->
      <div
        v-else
        class="bg-white dark:bg-neutral-900 rounded-xl border border-neutral-200 dark:border-neutral-800 overflow-hidden"
      >
        <table class="w-full">
          <thead
            class="bg-neutral-50 dark:bg-neutral-800/50 border-b border-neutral-200 dark:border-neutral-800"
          >
            <tr>
              <th
                class="px-6 py-3 text-left text-xs font-semibold text-neutral-500 uppercase tracking-wider"
              >
                Item
              </th>
              <th
                class="px-6 py-3 text-left text-xs font-semibold text-neutral-500 uppercase tracking-wider"
              >
                Category
              </th>
              <th
                class="px-6 py-3 text-left text-xs font-semibold text-neutral-500 uppercase tracking-wider"
              >
                Price
              </th>
              <th
                class="px-6 py-3 text-left text-xs font-semibold text-neutral-500 uppercase tracking-wider"
              >
                Status
              </th>
              <th
                class="px-6 py-3 text-right text-xs font-semibold text-neutral-500 uppercase tracking-wider"
              >
                Actions
              </th>
            </tr>
          </thead>
          <tbody class="divide-y divide-neutral-200 dark:divide-neutral-800">
            <tr
              v-for="item in paginatedItems"
              :key="item.menuItemId"
              class="hover:bg-neutral-50 dark:hover:bg-neutral-800/50 transition-colors cursor-pointer"
              @click="openEditModal(item)"
            >
              <td class="px-6 py-4">
                <div class="flex items-center gap-3">
                  <div
                    class="w-12 h-12 rounded-lg bg-neutral-100 dark:bg-neutral-800 flex items-center justify-center overflow-hidden flex-shrink-0"
                  >
                    <img
                      v-if="item.imageUrl"
                      :src="item.imageUrl"
                      :alt="item.name"
                      class="w-full h-full object-cover"
                      loading="lazy"
                    />
                    <svg
                      v-else
                      xmlns="http://www.w3.org/2000/svg"
                      class="w-6 h-6 text-neutral-400"
                      viewBox="0 0 24 24"
                      fill="none"
                      stroke="currentColor"
                      stroke-width="2"
                    >
                      <rect width="18" height="18" x="3" y="3" rx="2" ry="2" />
                      <circle cx="9" cy="9" r="2" />
                      <path d="m21 15-3.086-3.086a2 2 0 0 0-2.828 0L6 21" />
                    </svg>
                  </div>
                  <div class="min-w-0">
                    <div
                      class="font-semibold text-neutral-900 dark:text-white truncate"
                    >
                      {{ item.name }}
                    </div>
                  </div>
                </div>
              </td>
              <td class="px-6 py-4">
                <span class="text-sm text-neutral-600 dark:text-neutral-400">{{
                  item.category?.name || "Uncategorized"
                }}</span>
              </td>
              <td class="px-6 py-4">
                <span
                  class="text-sm font-bold text-neutral-900 dark:text-white font-mono"
                  >${{ item.basePrice.toFixed(2) }}</span
                >
              </td>
              <td class="px-6 py-4">
                <span
                  :class="[
                    'px-2 py-1 rounded-lg text-xs font-bold uppercase tracking-wide',
                    item.isAvailable
                      ? 'bg-success-100 dark:bg-success-900/30 text-success-700 dark:text-success-400'
                      : 'bg-error-100 dark:bg-error-900/30 text-error-700 dark:text-error-400',
                  ]"
                >
                  {{ item.isAvailable ? "Active" : "Inactive" }}
                </span>
              </td>
              <td class="px-6 py-4 text-right">
                <div class="flex items-center justify-end gap-2">
                  <button
                    @click.stop="deleteItem(item.menuItemId)"
                    class="text-xs text-neutral-400 hover:text-error-600 transition-colors font-medium px-2 py-1 rounded hover:bg-error-50 dark:hover:bg-error-900/20"
                  >
                    Delete
                  </button>
                  <button
                    @click.stop="openEditModal(item)"
                    class="text-xs font-medium text-primary-600 hover:text-primary-700 dark:text-primary-400 dark:hover:text-primary-300 transition-colors px-2 py-1 rounded hover:bg-primary-50 dark:hover:bg-primary-900/20"
                  >
                    Edit
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- Pagination Controls -->
      <div
        v-if="!loading && filteredItems.length > 0 && totalPages > 1"
        class="flex flex-col sm:flex-row items-center justify-between gap-4 bg-white dark:bg-neutral-900 rounded-xl border border-neutral-200 dark:border-neutral-800 p-4"
      >
        <!-- Pagination Info -->
        <div class="text-sm text-neutral-600 dark:text-neutral-400">
          Showing
          <span class="font-bold text-neutral-900 dark:text-white">{{
            (currentPage - 1) * itemsPerPage + 1
          }}</span>
          to
          <span class="font-bold text-neutral-900 dark:text-white">{{
            Math.min(currentPage * itemsPerPage, filteredItems.length)
          }}</span>
          of
          <span class="font-bold text-neutral-900 dark:text-white">{{
            filteredItems.length
          }}</span>
          items
        </div>

        <!-- Pagination Buttons -->
        <div class="flex items-center gap-2">
          <!-- Previous Button -->
          <button
            @click="prevPage"
            :disabled="currentPage === 1"
            class="px-3 py-2 rounded-lg border border-neutral-200 dark:border-neutral-700 text-neutral-700 dark:text-neutral-300 hover:bg-neutral-50 dark:hover:bg-neutral-800 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
            title="Previous Page"
          >
            <svg
              xmlns="http://www.w3.org/2000/svg"
              class="w-4 h-4"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              stroke-width="2"
            >
              <path d="m15 18-6-6 6-6" />
            </svg>
          </button>

          <!-- Page Numbers -->
          <div class="flex items-center gap-1">
            <!-- First Page -->
            <button
              v-if="pageNumbers[0] > 1"
              @click="goToPage(1)"
              class="px-3 py-2 rounded-lg border border-neutral-200 dark:border-neutral-700 text-neutral-700 dark:text-neutral-300 hover:bg-neutral-50 dark:hover:bg-neutral-800 transition-colors text-sm font-medium"
            >
              1
            </button>
            <span v-if="pageNumbers[0] > 2" class="text-neutral-400 px-1"
              >...</span
            >

            <!-- Visible Pages -->
            <button
              v-for="page in pageNumbers"
              :key="page"
              @click="goToPage(page)"
              :class="[
                'px-3 py-2 rounded-lg border text-sm font-medium transition-colors',
                page === currentPage
                  ? 'bg-primary-600 border-primary-600 text-white'
                  : 'border-neutral-200 dark:border-neutral-700 text-neutral-700 dark:text-neutral-300 hover:bg-neutral-50 dark:hover:bg-neutral-800',
              ]"
            >
              {{ page }}
            </button>

            <!-- Last Page -->
            <span
              v-if="pageNumbers[pageNumbers.length - 1] < totalPages - 1"
              class="text-neutral-400 px-1"
              >...</span
            >
            <button
              v-if="pageNumbers[pageNumbers.length - 1] < totalPages"
              @click="goToPage(totalPages)"
              class="px-3 py-2 rounded-lg border border-neutral-200 dark:border-neutral-700 text-neutral-700 dark:text-neutral-300 hover:bg-neutral-50 dark:hover:bg-neutral-800 transition-colors text-sm font-medium"
            >
              {{ totalPages }}
            </button>
          </div>

          <!-- Next Button -->
          <button
            @click="nextPage"
            :disabled="currentPage === totalPages"
            class="px-3 py-2 rounded-lg border border-neutral-200 dark:border-neutral-700 text-neutral-700 dark:text-neutral-300 hover:bg-neutral-50 dark:hover:bg-neutral-800 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
            title="Next Page"
          >
            <svg
              xmlns="http://www.w3.org/2000/svg"
              class="w-4 h-4"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              stroke-width="2"
            >
              <path d="m9 18 6-6-6-6" />
            </svg>
          </button>
        </div>
      </div>

      <!-- Create/Edit Modal -->
      <div
        v-if="showModal"
        class="fixed inset-0 z-50 flex items-center justify-center bg-black/50 backdrop-blur-sm p-4 animate-in fade-in duration-200"
        @click.self="closeModal"
      >
        <div
          class="bg-white dark:bg-neutral-900 rounded-2xl shadow-2xl w-full max-w-2xl overflow-hidden border border-neutral-200 dark:border-neutral-800 flex flex-col max-h-[90vh] animate-in zoom-in-95 duration-200"
        >
          <!-- Modal Header -->
          <div
            class="p-6 border-b border-neutral-200 dark:border-neutral-800 flex justify-between items-center bg-white dark:bg-neutral-900 sticky top-0 z-10"
          >
            <div>
              <h3 class="text-xl font-bold text-neutral-900 dark:text-white">
                {{ isEditing ? "Edit Menu Item" : "New Menu Item" }}
              </h3>
              <p class="text-xs text-neutral-500 mt-1">
                {{
                  isEditing
                    ? "Update item details and manage recipe"
                    : "Add a new item to your menu"
                }}
              </p>
            </div>
            <button
              @click="closeModal"
              class="text-neutral-400 hover:text-neutral-600 p-2 hover:bg-neutral-100 dark:hover:bg-neutral-800 rounded-lg transition-colors"
            >
              <svg
                xmlns="http://www.w3.org/2000/svg"
                class="w-5 h-5"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                stroke-width="2"
                stroke-linecap="round"
                stroke-linejoin="round"
              >
                <line x1="18" y1="6" x2="6" y2="18"></line>
                <line x1="6" y1="6" x2="18" y2="18"></line>
              </svg>
            </button>
          </div>

          <!-- Modal Body -->
          <div class="flex-1 overflow-y-auto p-6 scrollbar-thin">
            <!-- Tabs (Only if Editing) -->
            <div
              v-if="isEditing"
              class="flex gap-1 mb-6 p-1 bg-neutral-100 dark:bg-neutral-800 rounded-lg"
            >
              <button
                @click="activeModalTab = 'details'"
                :class="[
                  'flex-1 py-2 px-4 text-sm font-medium rounded-lg transition-all',
                  activeModalTab === 'details'
                    ? 'bg-white dark:bg-neutral-700 text-primary-600 shadow-sm'
                    : 'text-neutral-500 hover:text-neutral-700 dark:text-neutral-400',
                ]"
              >
                Details
              </button>
              <button
                @click="activeModalTab = 'recipe'"
                :class="[
                  'flex-1 py-2 px-4 text-sm font-medium rounded-lg transition-all',
                  activeModalTab === 'recipe'
                    ? 'bg-white dark:bg-neutral-700 text-primary-600 shadow-sm'
                    : 'text-neutral-500 hover:text-neutral-700 dark:text-neutral-400',
                ]"
              >
                Recipe ({{ recipes.length }})
              </button>
              <button
                @click="activeModalTab = 'variants'"
                :class="[
                  'flex-1 py-2 px-4 text-sm font-medium rounded-lg transition-all',
                  activeModalTab === 'variants'
                    ? 'bg-white dark:bg-neutral-700 text-primary-600 shadow-sm'
                    : 'text-neutral-500 hover:text-neutral-700 dark:text-neutral-400',
                ]"
              >
                Variants ({{ variants.length }})
              </button>
            </div>

            <!-- Tab: Details -->
            <div v-if="activeModalTab === 'details'" class="space-y-4">
              <!-- Bilingual Name Section -->
              <div class="grid grid-cols-2 gap-4">
                <div>
                  <label
                    class="block text-sm font-medium text-neutral-700 dark:text-neutral-300 mb-1"
                    >🇺🇸 Item Name <span class="text-error-500">*</span></label
                  >
                  <input
                    type="text"
                    v-model="form.name"
                    class="w-full bg-neutral-50 dark:bg-neutral-800 border-none rounded-lg p-2.5 text-sm ring-1 ring-neutral-200 dark:ring-neutral-700 focus:ring-2 focus:ring-primary-500 transition-shadow"
                    placeholder="e.g. Latte"
                    required
                  />
                </div>
                <div>
                  <label
                    class="block text-sm font-medium text-neutral-700 dark:text-neutral-300 mb-1"
                    >🇰🇭 Khmer Name (Optional)</label
                  >
                  <input
                    type="text"
                    v-model="form.nameKh"
                    class="w-full bg-neutral-50 dark:bg-neutral-800 border-none rounded-lg p-2.5 text-sm ring-1 ring-neutral-200 dark:ring-neutral-700 focus:ring-2 focus:ring-primary-500 transition-shadow font-khmer"
                    placeholder="ឧទាហរណ៍៖ ឡាតេ"
                  />
                </div>
              </div>

              <!-- Bilingual Description Section -->
              <div class="grid grid-cols-2 gap-4">
                <div>
                  <label
                    class="block text-sm font-medium text-neutral-700 dark:text-neutral-300 mb-1"
                    >🇺🇸 Description</label
                  >
                  <textarea
                    v-model="form.description"
                    rows="2"
                    class="w-full bg-neutral-50 dark:bg-neutral-800 border-none rounded-lg p-2.5 text-sm ring-1 ring-neutral-200 dark:ring-neutral-700 focus:ring-2 focus:ring-primary-500 transition-shadow"
                    placeholder="Optional details..."
                  ></textarea>
                </div>
                <div>
                  <label
                    class="block text-sm font-medium text-neutral-700 dark:text-neutral-300 mb-1"
                    >🇰🇭 Khmer Description</label
                  >
                  <textarea
                    v-model="form.descriptionKh"
                    rows="2"
                    class="w-full bg-neutral-50 dark:bg-neutral-800 border-none rounded-lg p-2.5 text-sm ring-1 ring-neutral-200 dark:ring-neutral-700 focus:ring-2 focus:ring-primary-500 transition-shadow font-khmer"
                    placeholder="ព័ត៌មានលម្អិត..."
                  ></textarea>
                </div>
              </div>

              <div class="grid grid-cols-2 gap-4">
                <div>
                  <label
                    class="block text-sm font-medium text-neutral-700 dark:text-neutral-300 mb-1"
                    >Category <span class="text-error-500">*</span></label
                  >
                  <select
                    v-model="form.categoryId"
                    class="w-full bg-neutral-50 dark:bg-neutral-800 border-none rounded-lg p-2.5 text-sm ring-1 ring-neutral-200 dark:ring-neutral-700 focus:ring-2 focus:ring-primary-500 transition-shadow"
                    required
                  >
                    <option
                      v-for="cat in flattenedCategories"
                      :key="cat.id"
                      :value="cat.id"
                    >
                      {{ cat.name }}
                    </option>
                  </select>
                </div>
                <div>
                  <label
                    class="block text-sm font-medium text-neutral-700 dark:text-neutral-300 mb-1"
                    >Base Price ($) <span class="text-error-500">*</span></label
                  >
                  <input
                    type="number"
                    v-model="form.basePrice"
                    class="w-full bg-neutral-50 dark:bg-neutral-800 border-none rounded-lg p-2.5 text-sm ring-1 ring-neutral-200 dark:ring-neutral-700 focus:ring-2 focus:ring-primary-500 transition-shadow"
                    step="0.01"
                    min="0"
                    required
                  />
                </div>
              </div>

              <div>
                <UiImageUpload v-model="form.imageUrl" label="Item Image" />
              </div>

              <div
                class="flex items-center gap-3 p-4 bg-neutral-50 dark:bg-neutral-800 rounded-lg"
              >
                <label class="relative inline-flex items-center cursor-pointer">
                  <input
                    type="checkbox"
                    v-model="form.isAvailable"
                    class="sr-only peer"
                  />
                  <div
                    class="w-11 h-6 bg-neutral-200 peer-focus:outline-none peer-focus:ring-4 peer-focus:ring-primary-300 dark:peer-focus:ring-primary-800 rounded-full peer dark:bg-neutral-700 peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-neutral-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all dark:border-neutral-600 peer-checked:bg-primary-600"
                  ></div>
                  <span
                    class="ml-3 text-sm font-medium text-neutral-900 dark:text-neutral-300"
                    >Available for Sale</span
                  >
                </label>
                <div class="ml-auto">
                  <span
                    v-if="form.isAvailable"
                    class="text-xs px-2 py-1 bg-success-100 dark:bg-success-900/30 text-success-700 dark:text-success-400 rounded-lg font-bold"
                    >ACTIVE</span
                  >
                  <span
                    v-else
                    class="text-xs px-2 py-1 bg-error-100 dark:bg-error-900/30 text-error-700 dark:text-error-400 rounded-lg font-bold"
                    >INACTIVE</span
                  >
                </div>
              </div>
            </div>

            <!-- Tab: Recipe -->
            <div v-if="activeModalTab === 'recipe'" class="space-y-6">
              <!-- Add Ingredient Form -->
              <div
                class="bg-neutral-50 dark:bg-neutral-800/50 p-4 rounded-xl border border-neutral-200 dark:border-neutral-800 space-y-3"
              >
                <h4
                  class="text-sm font-semibold text-neutral-900 dark:text-white flex items-center gap-2"
                >
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    class="w-4 h-4 text-primary-600"
                    viewBox="0 0 24 24"
                    fill="none"
                    stroke="currentColor"
                    stroke-width="2"
                  >
                    <path d="M5 12h14" />
                    <path d="M12 5v14" />
                  </svg>
                  Add Ingredient to Recipe
                </h4>
                <div class="flex gap-3">
                  <div class="flex-1">
                    <select
                      v-model="newRecipe.ingredientId"
                      class="w-full bg-white dark:bg-neutral-800 border border-neutral-200 dark:border-neutral-700 rounded-lg p-2 text-sm focus:ring-2 focus:ring-primary-500 transition-shadow"
                    >
                      <option :value="null" disabled>Select Ingredient</option>
                      <option
                        v-for="ing in ingredients"
                        :key="ing.ingredientId"
                        :value="ing.ingredientId"
                      >
                        {{ ing.name }} ({{ ing.unit }})
                      </option>
                    </select>
                  </div>
                  <div class="w-24">
                    <input
                      type="number"
                      v-model="newRecipe.quantityNeeded"
                      placeholder="Qty"
                      class="w-full bg-white dark:bg-neutral-800 border border-neutral-200 dark:border-neutral-700 rounded-lg p-2 text-sm focus:ring-2 focus:ring-primary-500 transition-shadow"
                      step="0.1"
                      min="0.1"
                    />
                  </div>
                  <button
                    @click="addRecipeIngredient"
                    :disabled="
                      !newRecipe.ingredientId || newRecipe.quantityNeeded <= 0
                    "
                    class="bg-primary-600 hover:bg-primary-700 disabled:bg-neutral-300 disabled:cursor-not-allowed text-white px-4 py-2 rounded-lg text-sm font-medium transition-colors"
                  >
                    Add
                  </button>
                </div>
              </div>

              <!-- Recipe List -->
              <div>
                <h4
                  class="text-sm font-semibold text-neutral-900 dark:text-white mb-3"
                >
                  Current Recipe ({{ recipes.length }} ingredients)
                </h4>
                <div
                  v-if="recipes.length === 0"
                  class="text-sm text-neutral-500 italic text-center py-8 bg-neutral-50 dark:bg-neutral-800/30 rounded-lg"
                >
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    class="w-12 h-12 mx-auto mb-3 text-neutral-400"
                    viewBox="0 0 24 24"
                    fill="none"
                    stroke="currentColor"
                    stroke-width="2"
                  >
                    <path
                      d="M17 3a2.828 2.828 0 1 1 4 4L7.5 20.5 2 22l1.5-5.5L17 3z"
                    />
                  </svg>
                  <p>No ingredients added yet.</p>
                  <p class="text-xs mt-1">
                    Add ingredients to track inventory usage
                  </p>
                </div>
                <div v-else class="space-y-2">
                  <div
                    v-for="rec in recipes"
                    :key="rec.recipeId"
                    class="flex items-center justify-between p-3 bg-white dark:bg-neutral-800/50 border border-neutral-100 dark:border-neutral-800 rounded-lg hover:border-primary-500/50 transition-colors"
                  >
                    <div class="flex items-center gap-3">
                      <div
                        class="w-8 h-8 rounded-full bg-orange-100 dark:bg-orange-900/30 flex items-center justify-center text-orange-600 dark:text-orange-400"
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
                          <path
                            d="M17 3a2.828 2.828 0 1 1 4 4L7.5 20.5 2 22l1.5-5.5L17 3z"
                          />
                        </svg>
                      </div>
                      <div class="text-sm">
                        <div
                          class="font-medium text-neutral-900 dark:text-white"
                        >
                          {{ rec.ingredientName }}
                        </div>
                        <div class="text-xs text-neutral-500 font-mono">
                          {{ rec.quantityNeeded }} {{ rec.ingredientUnit }}
                        </div>
                      </div>
                    </div>
                    <button
                      @click="removeRecipe(rec.recipeId)"
                      class="text-neutral-400 hover:text-error-600 p-2 rounded-lg hover:bg-error-50 dark:hover:bg-error-900/20 transition-colors"
                      title="Remove ingredient"
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
                        <line x1="18" y1="6" x2="6" y2="18"></line>
                        <line x1="6" y1="6" x2="18" y2="18"></line>
                      </svg>
                    </button>
                  </div>
                </div>
              </div>
            </div>

            <!-- Tab: Variants -->
            <div v-if="activeModalTab === 'variants'" class="space-y-6">
              <!-- Add Variant Form -->
              <div
                class="bg-neutral-50 dark:bg-neutral-800/50 p-4 rounded-xl border border-neutral-200 dark:border-neutral-800 space-y-3"
              >
                <h4
                  class="text-sm font-semibold text-neutral-900 dark:text-white flex items-center gap-2"
                >
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    class="w-4 h-4 text-primary-600"
                    viewBox="0 0 24 24"
                    fill="none"
                    stroke="currentColor"
                    stroke-width="2"
                  >
                    <path d="M5 12h14" />
                    <path d="M12 5v14" />
                  </svg>
                  Add Variant
                </h4>
                <div class="flex gap-3">
                  <div class="flex-1">
                    <select
                      v-model="newVariant.name"
                      class="w-full bg-white dark:bg-neutral-800 border border-neutral-200 dark:border-neutral-700 rounded-lg p-2 text-sm focus:ring-2 focus:ring-primary-500 transition-shadow"
                    >
                      <option value="" disabled>Select Size</option>
                      <option value="S">Small (S)</option>
                      <option value="M">Medium (M)</option>
                      <option value="L">Large (L)</option>
                    </select>
                  </div>
                  <div class="w-32">
                    <input
                      type="number"
                      v-model="newVariant.priceAdjustment"
                      placeholder="Price (+/-)"
                      class="w-full bg-white dark:bg-neutral-800 border border-neutral-200 dark:border-neutral-700 rounded-lg p-2 text-sm focus:ring-2 focus:ring-primary-500 transition-shadow"
                      step="0.5"
                    />
                  </div>
                  <button
                    @click="addVariant"
                    :disabled="!newVariant.name"
                    class="bg-primary-600 hover:bg-primary-700 disabled:bg-neutral-300 disabled:cursor-not-allowed text-white px-4 py-2 rounded-lg text-sm font-medium transition-colors"
                  >
                    Add
                  </button>
                </div>
                <div
                  class="flex items-center justify-between text-xs text-neutral-600 dark:text-neutral-400 pt-2 border-t border-neutral-200 dark:border-neutral-700"
                >
                  <span
                    >Base Price:
                    <span class="font-bold font-mono"
                      >${{ form.basePrice.toFixed(2) }}</span
                    ></span
                  >
                  <span
                    v-if="newVariant.priceAdjustment !== 0"
                    class="font-bold text-primary-600"
                  >
                    Final:
                    <span class="font-mono"
                      >${{
                        (
                          Number(form.basePrice) +
                          Number(newVariant.priceAdjustment)
                        ).toFixed(2)
                      }}</span
                    >
                  </span>
                </div>
              </div>

              <!-- Variant List -->
              <div>
                <h4
                  class="text-sm font-semibold text-neutral-900 dark:text-white mb-3"
                >
                  Current Variants ({{ variants.length }})
                </h4>
                <div
                  v-if="variants.length === 0"
                  class="text-sm text-neutral-500 italic text-center py-8 bg-neutral-50 dark:bg-neutral-800/30 rounded-lg"
                >
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    class="w-12 h-12 mx-auto mb-3 text-neutral-400"
                    viewBox="0 0 24 24"
                    fill="none"
                    stroke="currentColor"
                    stroke-width="2"
                  >
                    <path
                      d="M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z"
                    />
                  </svg>
                  <p>No variants added yet.</p>
                  <p class="text-xs mt-1">
                    Add size variants with price adjustments
                  </p>
                </div>
                <div v-else class="space-y-2">
                  <div
                    v-for="variant in variants"
                    :key="variant.variantId"
                    class="flex items-center justify-between p-3 bg-white dark:bg-neutral-800/50 border border-neutral-100 dark:border-neutral-800 rounded-lg hover:border-primary-500/50 transition-colors"
                  >
                    <div class="flex items-center gap-3">
                      <div
                        class="w-8 h-8 rounded-full bg-blue-100 dark:bg-blue-900/30 flex items-center justify-center text-blue-600 dark:text-blue-400"
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
                          <path
                            d="M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z"
                          />
                        </svg>
                      </div>
                      <div class="text-sm">
                        <div
                          class="font-medium text-neutral-900 dark:text-white"
                        >
                          {{ variant.name }}
                        </div>
                        <div
                          :class="
                            variant.priceAdjustment >= 0
                              ? 'text-success-600'
                              : 'text-error-600'
                          "
                          class="text-xs font-bold font-mono"
                        >
                          {{ variant.priceAdjustment >= 0 ? "+" : "" }}${{
                            variant.priceAdjustment.toFixed(2)
                          }}
                          = ${{
                            (form.basePrice + variant.priceAdjustment).toFixed(
                              2,
                            )
                          }}
                        </div>
                      </div>
                    </div>
                    <button
                      @click="removeVariant(variant.variantId)"
                      class="text-neutral-400 hover:text-error-600 p-2 rounded-lg hover:bg-error-50 dark:hover:bg-error-900/20 transition-colors"
                      title="Remove variant"
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
                        <line x1="18" y1="6" x2="6" y2="18"></line>
                        <line x1="6" y1="6" x2="18" y2="18"></line>
                      </svg>
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Modal Footer -->
          <div
            class="p-6 bg-neutral-50 dark:bg-neutral-800/50 border-t border-neutral-200 dark:border-neutral-800 flex justify-between items-center sticky bottom-0 z-10"
          >
            <button
              @click="closeModal"
              class="px-4 py-2 text-sm font-medium text-neutral-600 hover:text-neutral-900 dark:text-neutral-400 dark:hover:text-white transition-colors hover:bg-neutral-200 dark:hover:bg-neutral-700 rounded-lg"
            >
              Cancel
            </button>
            <div class="flex gap-3">
              <button
                v-if="activeModalTab === 'details'"
                @click="saveItem"
                :disabled="
                  !form.name || !form.categoryId || form.basePrice <= 0
                "
                class="px-6 py-2 text-sm font-medium bg-primary-600 hover:bg-primary-700 disabled:bg-neutral-300 disabled:cursor-not-allowed text-white rounded-lg transition-all shadow-lg shadow-primary-500/20 hover:shadow-xl hover:shadow-primary-500/30 active:scale-95"
              >
                {{ isEditing ? "Save Changes" : "Create Item" }}
              </button>
              <button
                v-else
                @click="closeModal"
                class="px-6 py-2 text-sm font-medium bg-primary-600 hover:bg-primary-700 text-white rounded-lg transition-all shadow-lg shadow-primary-500/20 hover:shadow-xl hover:shadow-primary-500/30 active:scale-95"
              >
                Done
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </NuxtLayout>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, reactive, watch } from "vue";

definePageMeta({
  layout: false,
});

const config = useRuntimeConfig();
const { get, post, put, del, download } = useApi();
const toast = useToast();

interface Category {
  categoryId: number;
  name: string;
  children?: Category[];
}

interface MenuItem {
  menuItemId: number;
  name: string;
  nameKh?: string;
  description: string;
  descriptionKh?: string;
  basePrice: number;
  imageUrl: string;
  isAvailable: boolean;
  categoryId: number;
  category?: Category;
}

interface Ingredient {
  ingredientId: number;
  name: string;
  unit: string;
}

interface Recipe {
  recipeId: number;
  menuItemId: number;
  ingredientId: number;
  ingredientName: string;
  ingredientUnit: string;
  quantityNeeded: number;
}

interface Variant {
  variantId: number;
  menuItemId: number;
  name: string;
  priceAdjustment: number;
}

// State
const menuItems = ref<MenuItem[]>([]);
const categories = ref<Category[]>([]);
const ingredients = ref<Ingredient[]>([]);
const recipes = ref<Recipe[]>([]);
const variants = ref<Variant[]>([]);

const loading = ref(true);
const searchQuery = ref("");
const filterCategory = ref("");
const viewMode = ref<"grid" | "list">("grid");
const fileInput = ref<HTMLInputElement | null>(null);

// Pagination
const currentPage = ref(1);
const itemsPerPage = ref(12);

const showModal = ref(false);
const isEditing = ref(false);
const activeModalTab = ref("details");
const currentItemId = ref<number | null>(null);
const imageLoadError = ref(false);

// Form Data
const form = reactive({
  menuItemId: null as number | null,
  name: "",
  nameKh: "",
  description: "",
  descriptionKh: "",
  categoryId: null as number | null,
  basePrice: 0,
  imageUrl: "",
  isAvailable: true,
});

const newRecipe = reactive({
  ingredientId: null as number | null,
  quantityNeeded: 0,
});

const newVariant = reactive({
  name: "",
  priceAdjustment: 0,
});

// Watch for image URL changes to reset error state
watch(
  () => form.imageUrl,
  () => {
    imageLoadError.value = false;
  },
);

// Computed
const flattenedCategories = computed(() => {
  const result: { id: number; name: string }[] = [];

  const traverse = (cats: Category[], prefix = "") => {
    for (const cat of cats) {
      result.push({ id: cat.categoryId, name: prefix + cat.name });
      if (cat.children && cat.children.length > 0) {
        traverse(cat.children, prefix + "— ");
      }
    }
  };

  traverse(categories.value);
  return result;
});

const filteredItems = computed(() => {
  let result = menuItems.value;

  if (searchQuery.value) {
    const q = searchQuery.value.toLowerCase();
    result = result.filter(
      (item) =>
        item.name.toLowerCase().includes(q) ||
        item.category?.name.toLowerCase().includes(q),
    );
  }

  if (filterCategory.value) {
    // When filtering by a category, we might want to include sub-categories too.
    // But for now, let's stick to exact match or check if the implementation needs recursive matching.
    // If the backend filters by exact ID, we do exact ID.
    // If we want "Select Beverages -> Show all Coffees", we need recursive check.

    // Recursive check logic:
    const targetCatId = Number(filterCategory.value);

    // Helper to find all descendant IDs of a category
    const getDescendants = (cats: Category[], id: number): number[] => {
      let ids: number[] = [];
      for (const cat of cats) {
        if (cat.categoryId === id) {
          // Found it, collect all children
          const collect = (c: Category[]) => {
            for (const child of c) {
              ids.push(child.categoryId);
              if (child.children) collect(child.children);
            }
          };
          if (cat.children) collect(cat.children);
          return ids;
        }
        if (cat.children) {
          const found = getDescendants(cat.children, id);
          if (found.length > 0) return found;
        }
      }
      return ids;
    };

    const childIds = getDescendants(categories.value, targetCatId);
    const validIds = [targetCatId, ...childIds];

    result = result.filter(
      (item) =>
        item.category?.categoryId &&
        validIds.includes(item.category.categoryId),
    );
  }

  return result;
});

// Pagination computed properties
const totalPages = computed(() =>
  Math.ceil(filteredItems.value.length / itemsPerPage.value),
);
const paginatedItems = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage.value;
  const end = start + itemsPerPage.value;
  return filteredItems.value.slice(start, end);
});

const pageNumbers = computed(() => {
  const pages = [];
  const maxVisiblePages = 5;
  let startPage = Math.max(
    1,
    currentPage.value - Math.floor(maxVisiblePages / 2),
  );
  let endPage = Math.min(totalPages.value, startPage + maxVisiblePages - 1);

  if (endPage - startPage < maxVisiblePages - 1) {
    startPage = Math.max(1, endPage - maxVisiblePages + 1);
  }

  for (let i = startPage; i <= endPage; i++) {
    pages.push(i);
  }

  return pages;
});

const activeItemsCount = computed(
  () => menuItems.value.filter((item) => item.isAvailable).length,
);
const inactiveItemsCount = computed(
  () => menuItems.value.filter((item) => !item.isAvailable).length,
);
const categoriesCount = computed(() => categories.value.length);

// Pagination methods
const goToPage = (page: number) => {
  if (page >= 1 && page <= totalPages.value) {
    currentPage.value = page;
    window.scrollTo({ top: 0, behavior: "smooth" });
  }
};

const nextPage = () => goToPage(currentPage.value + 1);
const prevPage = () => goToPage(currentPage.value - 1);

// Reset to page 1 when filters change
watch([searchQuery, filterCategory], () => {
  currentPage.value = 1;
});

// Clear filters
const clearFilters = () => {
  searchQuery.value = "";
  filterCategory.value = "";
  currentPage.value = 1;
};

// Fetch Data
const fetchMenu = async () => {
  loading.value = true;
  try {
    const data = await get<MenuItem[]>("/menu-items");
    menuItems.value = data || [];
  } catch (err) {
    console.error(err);
    toast.error("Failed to load menu items");
  } finally {
    loading.value = false;
  }
};

const fetchCategories = async () => {
  try {
    const data = await get<Category[]>("/categories");
    categories.value = data || [];
  } catch (err) {
    console.error(err);
  }
};

const fetchIngredients = async () => {
  try {
    const data = await get<Ingredient[]>("/ingredients");
    ingredients.value = data || [];
  } catch (err) {
    console.error(err);
  }
};

const fetchRecipes = async (menuItemId: number) => {
  try {
    const data = await get<Recipe[]>("/recipes/menu-item/" + menuItemId);
    recipes.value = data || [];
  } catch (err) {
    console.error(err);
  }
};

const fetchVariants = async (menuItemId: number) => {
  try {
    const data = await get<Variant[]>("/variants/menu-item/" + menuItemId);
    variants.value = data || [];
  } catch (err) {
    console.error(err);
  }
};

// Actions
const openCreateModal = () => {
  isEditing.value = false;
  activeModalTab.value = "details";
  currentItemId.value = null;
  imageLoadError.value = false;
  // Reset form
  form.name = "";
  form.nameKh = "";
  form.description = "";
  form.descriptionKh = "";

  // Smart default: Select first category from flattened list if available
  form.categoryId =
    flattenedCategories.value.length > 0
      ? flattenedCategories.value[0].id
      : null;

  form.basePrice = 0;
  form.imageUrl = "";
  form.isAvailable = true;

  showModal.value = true;
};

const openEditModal = async (item: MenuItem) => {
  isEditing.value = true;
  activeModalTab.value = "details";
  currentItemId.value = item.menuItemId;
  imageLoadError.value = false;

  // Fill form
  form.name = item.name;
  form.nameKh = item.nameKh || "";
  form.description = item.description || "";
  form.descriptionKh = item.descriptionKh || "";
  form.categoryId = item.category?.categoryId || null;
  form.basePrice = item.basePrice;
  form.imageUrl = item.imageUrl;
  form.isAvailable = item.isAvailable;

  // Fetch recipes and variants for this item
  await Promise.all([
    fetchRecipes(item.menuItemId),
    fetchVariants(item.menuItemId),
    fetchIngredients(), // Ensure we have list
  ]);

  showModal.value = true;
};

const closeModal = () => {
  showModal.value = false;
};

const saveItem = async () => {
  if (!form.name || !form.categoryId || form.basePrice <= 0) {
    toast.error("Please fill in all required fields");
    return;
  }

  try {
    const payload = { ...form };

    if (isEditing.value && currentItemId.value) {
      await put(`/menu-items/${currentItemId.value}`, payload);
    } else {
      await post("/menu-items/add", payload);
    }

    await fetchMenu();

    // Invalidate POS cache so it re-fetches with the new image
    const posCache = useState<any[]>("pos-menu-items");
    const posCatCache = useState<any[]>("pos-categories");
    if (posCache.value) posCache.value = [];
    if (posCatCache.value) posCatCache.value = [];

    toast.success(isEditing.value ? "Menu item updated" : "Menu item created");
    closeModal(); // Always close on success (create or edit)
  } catch (err) {
    console.error(err);
    toast.error("Failed to save menu item");
  }
};

const deleteItem = async (id: number) => {
  if (
    !confirm(
      "Are you sure? This will delete the item and all its recipes and variants.",
    )
  )
    return;
  try {
    await del(`/menu-items/${id}`);
    toast.success("Menu item deleted");
    fetchMenu();
  } catch (err) {
    toast.error("Failed to delete menu item");
  }
};

const addRecipeIngredient = async () => {
  if (
    !newRecipe.ingredientId ||
    newRecipe.quantityNeeded <= 0 ||
    !currentItemId.value
  ) {
    toast.error("Please select an ingredient and enter a valid quantity");
    return;
  }

  try {
    await post("/recipes", {
      menuItemId: currentItemId.value,
      ingredientId: newRecipe.ingredientId,
      quantityNeeded: newRecipe.quantityNeeded,
    });

    // Refresh recipes
    await fetchRecipes(currentItemId.value);

    // Reset sub-form
    newRecipe.quantityNeeded = 0;
    toast.success("Ingredient added to recipe");
  } catch (err) {
    console.error(err);
    toast.error("Failed to add ingredient");
  }
};

const removeRecipe = async (recipeId: number) => {
  if (!currentItemId.value) return;
  try {
    await del(`/recipes/${recipeId}`);
    toast.success("Ingredient removed from recipe");
    await fetchRecipes(currentItemId.value);
  } catch (err) {
    toast.error("Failed to remove ingredient");
  }
};

const addVariant = async () => {
  if (!newVariant.name || !currentItemId.value) {
    toast.error("Please select a variant size");
    return;
  }

  try {
    await post("/variants", {
      menuItemId: currentItemId.value,
      name: newVariant.name,
      priceAdjustment: newVariant.priceAdjustment,
    });

    // Refresh variants
    await fetchVariants(currentItemId.value);

    // Reset sub-form
    newVariant.name = "";
    newVariant.priceAdjustment = 0;
    toast.success("Variant added");
  } catch (err) {
    console.error(err);
    toast.error("Failed to add variant");
  }
};

const removeVariant = async (variantId: number) => {
  if (!currentItemId.value) return;
  try {
    await del(`/variants/${variantId}`);
    toast.success("Variant removed");
    await fetchVariants(currentItemId.value);
  } catch (err) {
    toast.error("Failed to remove variant");
  }
};

// Load view mode from localStorage
onMounted(() => {
  const savedViewMode = localStorage.getItem("menuViewMode");
  if (savedViewMode === "list" || savedViewMode === "grid") {
    viewMode.value = savedViewMode;
  }

  const savedItemsPerPage = localStorage.getItem("menuItemsPerPage");
  if (savedItemsPerPage) {
    const parsed = parseInt(savedItemsPerPage);
    if ([12, 24, 48, 96].includes(parsed)) {
      itemsPerPage.value = parsed;
    }
  }

  fetchMenu();
  fetchCategories();
  fetchIngredients();
});

// Save view mode to localStorage
watch(viewMode, (newMode) => {
  localStorage.setItem("menuViewMode", newMode);
});

// Save items per page to localStorage
watch(itemsPerPage, (newValue) => {
  localStorage.setItem("menuItemsPerPage", newValue.toString());
  currentPage.value = 1; // Reset to first page when changing items per page
});

const downloadMenu = (format: "excel" | "csv") => {
  const endpoint = `/api/import-export/export/menu-items/${format}`;
  download(endpoint, `menu_items.${format === "excel" ? "xlsx" : "csv"}`);
};

const triggerFileInput = () => {
  fileInput.value?.click();
};

const onFileChange = async (event: any) => {
  const file = event.target.files[0];
  if (!file) return;

  loading.value = true;
  const formData = new FormData();
  formData.append("file", file);

  try {
    await $fetch(
      config.public.apiBase + "/api/import-export/import/menu-items",
      {
        method: "POST",
        body: formData,
        headers: {
          Authorization: "Bearer " + useCookie("auth_token").value,
        },
      },
    );

    toast.success("Menu items imported successfully!");
    fetchMenu();
  } catch (err: any) {
    console.error(err);
    toast.error(err.response?._data?.message || "Import failed");
  } finally {
    loading.value = false;
    if (fileInput.value) fileInput.value.value = "";
  }
};
</script>
