<template>
  <NuxtLayout name="admin">
    <div class="space-y-6 relative">
      <UiBreadcrumb :items="[{ label: 'Inventory' }]" />

      <!-- Header -->
      <div class="flex items-center justify-between">
        <div>
          <h2 class="text-2xl font-bold text-neutral-900 dark:text-white">
            Inventory Management
          </h2>
          <p class="text-xs text-neutral-500 mt-1">
            Monitor stock levels and manage ingredient adjustments.
          </p>
        </div>
        <div class="flex items-center gap-3">
          <div class="flex bg-neutral-100 dark:bg-neutral-800 p-1 rounded-xl">
            <button
              @click="activeTab = 'items'"
              :class="[
                'px-4 py-1.5 rounded-lg text-xs font-bold transition-all',
                activeTab === 'items'
                  ? 'bg-white dark:bg-neutral-700 text-primary-600 shadow-sm'
                  : 'text-neutral-500 hover:text-neutral-700',
              ]"
            >
              Stock Items
            </button>
            <button
              @click="activeTab = 'adjustments'"
              :class="[
                'px-4 py-1.5 rounded-lg text-xs font-bold transition-all',
                activeTab === 'adjustments'
                  ? 'bg-white dark:bg-neutral-700 text-primary-600 shadow-sm'
                  : 'text-neutral-500 hover:text-neutral-700',
              ]"
            >
              Audit History
            </button>
          </div>
          <button
            @click="openCreateModal"
            class="bg-primary-600 hover:bg-primary-700 text-white px-4 py-2 rounded-xl text-sm font-medium transition-colors flex items-center gap-2"
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
            Add Ingredient
          </button>
        </div>
      </div>

      <!-- Table Section -->
      <div v-if="activeTab === 'items'" class="space-y-6">
        <!-- Filters -->
        <div
          class="flex items-center gap-4 bg-white dark:bg-neutral-900 p-4 rounded-xl border border-neutral-200 dark:border-neutral-800"
        >
          <div class="relative flex-1 max-w-md">
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
              placeholder="Search ingredients..."
              class="w-full pl-10 pr-4 py-2 bg-neutral-100 dark:bg-neutral-800 border-none rounded-lg text-sm focus:ring-2 focus:ring-primary-500 placeholder-neutral-500"
            />
          </div>
          <div class="flex items-center gap-2">
            <select
              v-model="filterStatus"
              class="bg-neutral-100 dark:bg-neutral-800 border-none rounded-lg text-sm px-4 py-2 focus:ring-2 focus:ring-primary-500"
            >
              <option value="all">All Status</option>
              <option value="low">Low Stock</option>
              <option value="sufficient">Sufficient</option>
            </select>
          </div>
        </div>

        <!-- Stock Table -->
        <div
          class="bg-white dark:bg-neutral-900 border border-neutral-200 dark:border-neutral-800 rounded-xl overflow-hidden"
        >
          <div class="overflow-x-auto">
            <table class="w-full">
              <thead>
                <tr
                  class="bg-neutral-50 dark:bg-neutral-800/50 border-b border-neutral-200 dark:border-neutral-800"
                >
                  <th
                    class="px-6 py-3 text-left text-xs font-semibold text-neutral-500 uppercase tracking-wider"
                  >
                    Name
                  </th>
                  <th
                    class="px-6 py-3 text-left text-xs font-semibold text-neutral-500 uppercase tracking-wider"
                  >
                    Stock Level
                  </th>
                  <th
                    class="px-6 py-3 text-left text-xs font-semibold text-neutral-500 uppercase tracking-wider"
                  >
                    Unit Cost
                  </th>
                  <th
                    class="px-6 py-3 text-left text-xs font-semibold text-neutral-500 uppercase tracking-wider"
                  >
                    Reorder Point
                  </th>
                  <th
                    class="px-6 py-3 text-left text-xs font-semibold text-neutral-500 uppercase tracking-wider"
                  >
                    Last Stock In
                  </th>
                  <th
                    class="px-6 py-3 text-right text-xs font-semibold text-neutral-500 uppercase tracking-wider"
                  >
                    Actions
                  </th>
                </tr>
              </thead>
              <tbody
                class="divide-y divide-neutral-200 dark:divide-neutral-800"
              >
                <template v-if="loading">
                  <tr v-for="i in 5" :key="i" class="animate-pulse">
                    <td v-for="j in 6" :key="j" class="px-6 py-4">
                      <div
                        class="h-4 bg-neutral-200 dark:bg-neutral-800 rounded"
                      ></div>
                    </td>
                  </tr>
                </template>
                <tr v-else-if="filteredIngredients.length === 0">
                  <td colspan="6" class="px-6 py-12 text-center">
                    <div class="flex flex-col items-center text-neutral-500">
                      <svg
                        xmlns="http://www.w3.org/2000/svg"
                        class="w-12 h-12 mb-3 opacity-20"
                        viewBox="0 0 24 24"
                        fill="none"
                        stroke="currentColor"
                        stroke-width="2"
                      >
                        <path d="m7.5 4.27 9 5.15" />
                        <path
                          d="M21 8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16Z"
                        />
                        <path d="m3.3 7 8.7 5 8.7-5" />
                        <path d="M12 22V12" />
                      </svg>
                      <p>No ingredients found.</p>
                    </div>
                  </td>
                </tr>
                <tr
                  v-for="item in filteredIngredients"
                  :key="item.ingredientId"
                  class="hover:bg-neutral-50 dark:hover:bg-neutral-800/50 transition-colors"
                >
                  <td class="px-6 py-4">
                    <div class="font-medium text-neutral-900 dark:text-white">
                      {{ item.name }}
                    </div>
                    <div class="text-xs text-neutral-500">
                      {{ item.sku || "No SKU" }}
                    </div>
                  </td>
                  <td class="px-6 py-4">
                    <div class="flex items-center gap-2">
                      <span
                        :class="[
                          'font-mono font-medium',
                          item.currentStock <= item.reorderLevel
                            ? 'text-error-600 dark:text-error-400'
                            : 'text-neutral-900 dark:text-white',
                        ]"
                      >
                        {{ item.currentStock }} {{ item.unit }}
                      </span>
                      <span
                        v-if="item.currentStock <= item.reorderLevel"
                        class="px-1.5 py-0.5 rounded text-[10px] font-bold bg-error-100 text-error-700 dark:bg-error-900/30 dark:text-error-400 uppercase tracking-wide"
                      >
                        Low
                      </span>
                    </div>
                  </td>
                  <td
                    class="px-6 py-4 text-sm text-neutral-600 dark:text-neutral-400"
                  >
                    ${{ item.costPerUnit?.toFixed(2) }}
                  </td>
                  <td
                    class="px-6 py-4 text-sm text-neutral-600 dark:text-neutral-400"
                  >
                    {{ item.reorderLevel }} {{ item.unit }}
                  </td>
                  <td
                    class="px-6 py-4 text-sm text-neutral-600 dark:text-neutral-400"
                  >
                    {{
                      item.updatedAt
                        ? new Date(item.updatedAt).toLocaleDateString()
                        : "-"
                    }}
                  </td>
                  <td class="px-6 py-4 text-right">
                    <div class="flex items-center justify-end gap-2">
                      <button
                        @click="openStockIn(item)"
                        title="Stock In"
                        class="p-1.5 text-success-600 hover:bg-success-50 rounded-lg transition-colors"
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
                          <path d="M12 5v14m-7-7h14" />
                        </svg>
                      </button>
                      <button
                        @click="openAdjustment(item)"
                        title="Adjust Stock"
                        class="p-1.5 text-warning-600 hover:bg-warning-50 rounded-lg transition-colors"
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
                          <path d="m3 3 7.07 16.97 2.51-7.39 7.39-2.51L3 3z" />
                          <path d="m13 13 6 6" />
                        </svg>
                      </button>
                      <button
                        @click="editIngredient(item)"
                        class="p-1.5 text-primary-600 hover:bg-primary-50 rounded-lg transition-colors"
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
                      </button>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>

      <!-- History Section -->
      <div
        v-if="activeTab === 'adjustments'"
        class="space-y-6 animate-in fade-in duration-300"
      >
        <div
          class="bg-white dark:bg-neutral-900 border border-neutral-200 dark:border-neutral-800 rounded-xl overflow-hidden"
        >
          <div class="overflow-x-auto">
            <table class="w-full">
              <thead>
                <tr
                  class="bg-neutral-50 dark:bg-neutral-800/50 border-b border-neutral-200 dark:border-neutral-800"
                >
                  <th
                    class="px-6 py-3 text-left text-xs font-semibold text-neutral-500 uppercase tracking-wider"
                  >
                    Date
                  </th>
                  <th
                    class="px-6 py-3 text-left text-xs font-semibold text-neutral-500 uppercase tracking-wider"
                  >
                    Ingredient
                  </th>
                  <th
                    class="px-6 py-3 text-left text-xs font-semibold text-neutral-500 uppercase tracking-wider"
                  >
                    Type
                  </th>
                  <th
                    class="px-6 py-3 text-left text-xs font-semibold text-neutral-500 uppercase tracking-wider"
                  >
                    Qty Change
                  </th>
                  <th
                    class="px-6 py-3 text-left text-xs font-semibold text-neutral-500 uppercase tracking-wider"
                  >
                    Status
                  </th>
                  <th
                    class="px-6 py-3 text-right text-xs font-semibold text-neutral-500 uppercase tracking-wider"
                  >
                    Action
                  </th>
                </tr>
              </thead>
              <tbody
                class="divide-y divide-neutral-200 dark:divide-neutral-800"
              >
                <tr v-if="adjLoading">
                  <td colspan="6" class="px-6 py-8 text-center">
                    <div
                      class="animate-spin h-6 w-6 border-2 border-primary-500 border-t-transparent rounded-full mx-auto"
                    ></div>
                  </td>
                </tr>
                <tr v-else-if="adjustments.length === 0">
                  <td
                    colspan="6"
                    class="px-6 py-12 text-center text-neutral-500"
                  >
                    No adjustment history found.
                  </td>
                </tr>
                <tr
                  v-for="adj in adjustments"
                  :key="adj.adjustmentId"
                  class="text-sm"
                >
                  <td
                    class="px-6 py-4 whitespace-nowrap text-neutral-500 font-mono text-[10px]"
                  >
                    {{ new Date(adj.date).toLocaleString() }}
                  </td>
                  <td
                    class="px-6 py-4 font-bold text-neutral-900 dark:text-white"
                  >
                    {{ adj.ingredient?.name }}
                  </td>
                  <td class="px-6 py-4">
                    <span
                      class="text-[10px] font-black uppercase tracking-tighter"
                      >{{ adj.reasonType }}</span
                    >
                  </td>
                  <td
                    :class="[
                      'px-6 py-4 font-mono font-bold',
                      adj.qtyChange < 0 ? 'text-red-500' : 'text-success-500',
                    ]"
                  >
                    {{ adj.qtyChange > 0 ? "+" : "" }}{{ adj.qtyChange }}
                  </td>
                  <td class="px-6 py-4">
                    <span
                      :class="[
                        'px-2 py-0.5 rounded-full text-[10px] font-black uppercase',
                        adj.status === 'APPROVED'
                          ? 'bg-success-100 text-success-700'
                          : adj.status === 'PENDING'
                            ? 'bg-warning-100 text-warning-700'
                            : 'bg-error-100 text-error-700',
                      ]"
                    >
                      {{ adj.status }}
                    </span>
                  </td>
                  <td class="px-6 py-4 text-right">
                    <button
                      v-if="adj.status === 'PENDING'"
                      @click="initiateApproval(adj)"
                      class="text-xs bg-primary-600 hover:bg-primary-700 text-white px-3 py-1 rounded-lg font-bold"
                    >
                      Approve
                    </button>
                    <div v-else class="text-[10px] text-neutral-500 italic">
                      By: {{ adj.approvedByName }}
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>

      <!-- Create Ingredient Modal -->
      <div
        v-if="showCreateModal"
        class="fixed inset-0 z-50 flex items-center justify-center bg-black/50 backdrop-blur-sm p-4"
      >
        <div
          class="bg-white dark:bg-neutral-900 rounded-2xl shadow-xl w-full max-w-lg overflow-hidden border border-neutral-200 dark:border-neutral-800"
        >
          <div
            class="p-6 border-b border-neutral-200 dark:border-neutral-800 flex justify-between items-center"
          >
            <h3 class="text-xl font-bold text-neutral-900 dark:text-white">
              {{ isEditing ? "Edit Ingredient" : "New Ingredient" }}
            </h3>
            <button
              @click="showCreateModal = false"
              class="text-neutral-400 hover:text-neutral-600"
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
          <div class="p-6 space-y-4">
            <div>
              <label
                class="block text-sm font-medium text-neutral-700 dark:text-neutral-300 mb-1"
                >Name</label
              >
              <input
                type="text"
                v-model="newIngredient.name"
                class="w-full bg-neutral-50 dark:bg-neutral-800 border-none rounded-lg p-2.5 text-sm ring-1 ring-neutral-200 dark:ring-neutral-700 focus:ring-2 focus:ring-primary-500"
                placeholder="e.g. Milk"
              />
            </div>
            <div class="grid grid-cols-2 gap-4">
              <div>
                <label
                  class="block text-sm font-medium text-neutral-700 dark:text-neutral-300 mb-1"
                  >Unit</label
                >
                <input
                  type="text"
                  v-model="newIngredient.unit"
                  class="w-full bg-neutral-50 dark:bg-neutral-800 border-none rounded-lg p-2.5 text-sm ring-1 ring-neutral-200 dark:ring-neutral-700 focus:ring-2 focus:ring-primary-500"
                  placeholder="e.g. Liters"
                />
              </div>
              <div>
                <label
                  class="block text-sm font-medium text-neutral-700 dark:text-neutral-300 mb-1"
                  >Cost Per Unit</label
                >
                <input
                  type="number"
                  v-model="newIngredient.costPerUnit"
                  class="w-full bg-neutral-50 dark:bg-neutral-800 border-none rounded-lg p-2.5 text-sm ring-1 ring-neutral-200 dark:ring-neutral-700 focus:ring-2 focus:ring-primary-500"
                  step="0.01"
                />
              </div>
            </div>
            <div class="grid grid-cols-2 gap-4">
              <div>
                <label
                  class="block text-sm font-medium text-neutral-700 dark:text-neutral-300 mb-1"
                  >Initial Stock</label
                >
                <input
                  type="number"
                  v-model="newIngredient.currentStock"
                  class="w-full bg-neutral-50 dark:bg-neutral-800 border-none rounded-lg p-2.5 text-sm ring-1 ring-neutral-200 dark:ring-neutral-700 focus:ring-2 focus:ring-primary-500"
                />
              </div>
              <div>
                <label
                  class="block text-sm font-medium text-neutral-700 dark:text-neutral-300 mb-1"
                  >Reorder Level</label
                >
                <input
                  type="number"
                  v-model="newIngredient.reorderLevel"
                  class="w-full bg-neutral-50 dark:bg-neutral-800 border-none rounded-lg p-2.5 text-sm ring-1 ring-neutral-200 dark:ring-neutral-700 focus:ring-2 focus:ring-primary-500"
                />
              </div>
            </div>
            <div>
              <label
                class="block text-sm font-medium text-neutral-700 dark:text-neutral-300 mb-1"
                >SKU</label
              >
              <input
                type="text"
                v-model="newIngredient.sku"
                class="w-full bg-neutral-50 dark:bg-neutral-800 border-none rounded-lg p-2.5 text-sm ring-1 ring-neutral-200 dark:ring-neutral-700 focus:ring-2 focus:ring-primary-500"
                placeholder="Optional"
              />
            </div>
          </div>
          <div
            class="p-6 bg-neutral-50 dark:bg-neutral-800/50 flex justify-end gap-3"
          >
            <button
              @click="showCreateModal = false"
              class="px-4 py-2 text-sm font-medium text-neutral-600 hover:text-neutral-900 dark:text-neutral-400 dark:hover:text-white transition-colors"
            >
              Cancel
            </button>
            <button
              @click="saveIngredient"
              class="px-4 py-2 text-sm font-medium bg-primary-600 hover:bg-primary-700 text-white rounded-lg transition-colors"
            >
              {{ isEditing ? "Update" : "Save" }} Ingredient
            </button>
          </div>
        </div>
      </div>

      <!-- Stock In Modal -->
      <div
        v-if="showStockInModal"
        class="fixed inset-0 z-50 flex items-center justify-center bg-black/50 backdrop-blur-sm p-4"
      >
        <div
          class="bg-white dark:bg-neutral-900 rounded-2xl shadow-xl w-full max-w-md overflow-hidden border border-neutral-200 dark:border-neutral-800"
        >
          <div
            class="p-6 border-b border-neutral-200 dark:border-neutral-800 flex justify-between items-center"
          >
            <h3 class="text-xl font-bold text-neutral-900 dark:text-white">
              Stock In: {{ selectedIngredient?.name }}
            </h3>
            <button
              @click="showStockInModal = false"
              class="text-neutral-400 hover:text-neutral-600"
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
          <div class="p-6 space-y-4">
            <div>
              <label
                class="block text-sm font-medium text-neutral-700 dark:text-neutral-300 mb-1"
                >Supplier</label
              >
              <select
                v-model="newStockIn.supplierId"
                class="w-full bg-neutral-50 dark:bg-neutral-800 border-none rounded-lg p-2.5 text-sm ring-1 ring-neutral-200 dark:ring-neutral-700 focus:ring-2 focus:ring-primary-500"
              >
                <option
                  v-for="sup in suppliers"
                  :key="sup.supplierId"
                  :value="sup.supplierId"
                >
                  {{ sup.name }}
                </option>
              </select>
            </div>
            <div class="grid grid-cols-2 gap-4">
              <div>
                <label
                  class="block text-sm font-medium text-neutral-700 dark:text-neutral-300 mb-1"
                  >Quantity</label
                >
                <input
                  type="number"
                  v-model="newStockIn.qtyIn"
                  class="w-full bg-neutral-50 dark:bg-neutral-800 border-none rounded-lg p-2.5 text-sm ring-1 ring-neutral-200 dark:ring-neutral-700 focus:ring-2 focus:ring-primary-500"
                  step="1"
                />
              </div>
              <div>
                <label
                  class="block text-sm font-medium text-neutral-700 dark:text-neutral-300 mb-1"
                  >Unit Cost</label
                >
                <input
                  type="number"
                  v-model="newStockIn.unitCost"
                  class="w-full bg-neutral-50 dark:bg-neutral-800 border-none rounded-lg p-2.5 text-sm ring-1 ring-neutral-200 dark:ring-neutral-700 focus:ring-2 focus:ring-primary-500"
                  step="0.01"
                />
              </div>
            </div>
            <div>
              <label
                class="block text-sm font-medium text-neutral-700 dark:text-neutral-300 mb-1"
                >Invoice No</label
              >
              <input
                type="text"
                v-model="newStockIn.invoiceNo"
                class="w-full bg-neutral-50 dark:bg-neutral-800 border-none rounded-lg p-2.5 text-sm ring-1 ring-neutral-200 dark:ring-neutral-700 focus:ring-2 focus:ring-primary-500"
              />
            </div>
            <div>
              <label
                class="block text-sm font-medium text-neutral-700 dark:text-neutral-300 mb-1"
                >Received By</label
              >
              <select
                v-model="newStockIn.receivedBy"
                class="w-full bg-neutral-50 dark:bg-neutral-800 border-none rounded-lg p-2.5 text-sm ring-1 ring-neutral-200 dark:ring-neutral-700 focus:ring-2 focus:ring-primary-500"
              >
                <option
                  v-for="user in users"
                  :key="user.userId"
                  :value="user.userId"
                >
                  {{ user.username }}
                </option>
              </select>
            </div>
          </div>
          <div
            class="p-6 bg-neutral-50 dark:bg-neutral-800/50 flex justify-end gap-3"
          >
            <button
              @click="showStockInModal = false"
              class="px-4 py-2 text-sm font-medium text-neutral-600 hover:text-neutral-900 dark:text-neutral-400 dark:hover:text-white transition-colors"
            >
              Cancel
            </button>
            <button
              @click="createStockIn"
              class="px-4 py-2 text-sm font-medium bg-success-600 hover:bg-success-700 text-white rounded-lg transition-colors"
            >
              Confirm Stock In
            </button>
          </div>
        </div>
      </div>

      <!-- Adjust Stock Modal -->
      <div
        v-if="showAdjustmentModal"
        class="fixed inset-0 z-50 flex items-center justify-center bg-black/50 backdrop-blur-sm p-4"
      >
        <div
          class="bg-white dark:bg-neutral-900 rounded-2xl shadow-xl w-full max-w-md overflow-hidden border border-neutral-200 dark:border-neutral-800"
        >
          <div
            class="p-6 border-b border-neutral-200 dark:border-neutral-800 flex justify-between items-center"
          >
            <h3 class="text-xl font-bold text-neutral-900 dark:text-white">
              Adjust Stock: {{ selectedIngredient?.name }}
            </h3>
            <button
              @click="showAdjustmentModal = false"
              class="text-neutral-400 hover:text-neutral-600"
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
          <div class="p-6 space-y-4">
            <div>
              <label
                class="block text-sm font-medium text-neutral-700 dark:text-neutral-300 mb-1"
                >Branch</label
              >
              <select
                v-model="newAdjustment.branchId"
                class="w-full bg-neutral-50 dark:bg-neutral-800 border-none rounded-lg p-2.5 text-sm ring-1 ring-neutral-200 dark:ring-neutral-700 focus:ring-2 focus:ring-primary-500"
              >
                <option
                  v-for="branch in branches"
                  :key="branch.branchId"
                  :value="branch.branchId"
                >
                  {{ branch.name }}
                </option>
              </select>
            </div>
            <div>
              <label
                class="block text-sm font-medium text-neutral-700 dark:text-neutral-300 mb-1"
                >Adjustment Qty (+/-)</label
              >
              <input
                type="number"
                v-model="newAdjustment.qtyChange"
                class="w-full bg-neutral-50 dark:bg-neutral-800 border-none rounded-lg p-2.5 text-sm ring-1 ring-neutral-200 dark:ring-neutral-700 focus:ring-2 focus:ring-primary-500"
                step="0.1"
                placeholder="-2 or 5"
              />
              <p class="text-xs text-neutral-500 mt-1">
                Use negative values for wastage/loss, positive for corrections.
              </p>
            </div>
            <div>
              <label
                class="block text-sm font-medium text-neutral-700 dark:text-neutral-300 mb-1"
                >Reason</label
              >
              <select
                v-model="newAdjustment.reasonType"
                class="w-full bg-neutral-50 dark:bg-neutral-800 border-none rounded-lg p-2.5 text-sm ring-1 ring-neutral-200 dark:ring-neutral-700 focus:ring-2 focus:ring-primary-500"
              >
                <option value="WASTAGE">Wastage / Spoiled</option>
                <option value="CORRECTION">Inventory Correction</option>
                <option value="THEFT">Theft / Loss</option>
                <option value="OTHER">Other</option>
              </select>
            </div>
            <div>
              <label
                class="block text-sm font-medium text-neutral-700 dark:text-neutral-300 mb-1"
                >Note</label
              >
              <textarea
                v-model="newAdjustment.note"
                rows="3"
                class="w-full bg-neutral-50 dark:bg-neutral-800 border-none rounded-lg p-2.5 text-sm ring-1 ring-neutral-200 dark:ring-neutral-700 focus:ring-2 focus:ring-primary-500"
              ></textarea>
            </div>
            <div>
              <label
                class="block text-sm font-medium text-neutral-700 dark:text-neutral-300 mb-1"
                >Created By</label
              >
              <select
                v-model="newAdjustment.createdBy"
                class="w-full bg-neutral-50 dark:bg-neutral-800 border-none rounded-lg p-2.5 text-sm ring-1 ring-neutral-200 dark:ring-neutral-700 focus:ring-2 focus:ring-primary-500"
              >
                <option
                  v-for="user in users"
                  :key="user.userId"
                  :value="user.userId"
                >
                  {{ user.username }}
                </option>
              </select>
            </div>
          </div>
          <div
            class="p-6 bg-neutral-50 dark:bg-neutral-800/50 flex justify-end gap-3"
          >
            <button
              @click="showAdjustmentModal = false"
              class="px-4 py-2 text-sm font-medium text-neutral-600 hover:text-neutral-900 dark:text-neutral-400 dark:hover:text-white transition-colors"
            >
              Cancel
            </button>
            <button
              @click="createAdjustment"
              class="px-4 py-2 text-sm font-medium bg-warning-600 hover:bg-warning-700 text-white rounded-lg transition-colors"
            >
              Submit Adjustment
            </button>
          </div>
        </div>
      </div>

      <!-- Approval Modal -->
      <ApprovalModal
        v-model="showApprovalModal"
        action-type="VOID"
        :loading="approving"
        @approve="confirmApproval"
      />
    </div>
  </NuxtLayout>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, reactive, watch } from "vue";
import ApprovalModal from "~/components/pos/ApprovalModal.vue";

definePageMeta({
  layout: false,
});
const { get, post, put } = useApi();
const toast = useToast();

interface Ingredient {
  ingredientId: number;
  name: string;
  sku?: string;
  unit: string;
  currentStock: number;
  costPerUnit: number;
  reorderLevel: number;
  updatedAt: string;
}

interface Supplier {
  supplierId: number;
  name: string;
}

interface User {
  userId: number;
  username: string;
}

// State
const activeTab = ref("items");
const ingredients = ref<Ingredient[]>([]);
const adjustments = ref<any[]>([]);
const adjLoading = ref(false);
const suppliers = ref<Supplier[]>([]);
const users = ref<User[]>([]);
const loading = ref(true);
const searchQuery = ref("");
const filterStatus = ref("all");
const branches = ref<any[]>([]);

const showCreateModal = ref(false);
const showStockInModal = ref(false);
const showAdjustmentModal = ref(false);
const showApprovalModal = ref(false);
const approving = ref(false);
const isEditing = ref(false);
const editingId = ref<number | null>(null);
const selectedAdjustment = ref<any | null>(null);

const selectedIngredient = ref<Ingredient | null>(null);

// Forms
const newIngredient = reactive({
  name: "",
  unit: "G",
  currentStock: 0,
  costPerUnit: 0,
  reorderLevel: 5,
  sku: "",
});

const newStockIn = reactive({
  supplierId: null as number | null,
  ingredientId: null as number | null,
  qtyIn: 0,
  unitCost: 0,
  totalCost: 0,
  invoiceNo: "",
  receivedDate: "",
  receivedBy: null as number | null,
});

const newAdjustment = reactive({
  ingredientId: null as number | null,
  branchId: null as number | null,
  qtyChange: 0,
  reasonType: "WASTAGE",
  note: "",
  createdBy: null as number | null,
  date: "",
});

// Fetching
const fetchIngredients = async () => {
  loading.value = true;
  try {
    const data = await get<Ingredient[]>("/ingredients");
    ingredients.value = data || [];
  } catch (err) {
    console.error("Failed to fetch ingredients", err);
  } finally {
    loading.value = false;
  }
};

const fetchAdjustments = async () => {
  adjLoading.value = true;
  try {
    const data = await get<any[]>("/stock-adjustments");
    adjustments.value = data || [];
  } catch (e) {
    toast.error("Failed to load adjustment history");
  } finally {
    adjLoading.value = false;
  }
};

watch(activeTab, (val) => {
  if (val === "adjustments") fetchAdjustments();
  else fetchIngredients();
});

const fetchSuppliers = async () => {
  try {
    // Mocking if endpoint not ready, or implement
    const data = await get<Supplier[]>("/suppliers");
    suppliers.value = data || [];
  } catch (err) {
    console.error("Failed fetch suppliers");
    // Mock
    suppliers.value = [
      { supplierId: 1, name: "Local Farm" },
      { supplierId: 2, name: "Wholesale Depot" },
    ];
  }
};

const fetchUsers = async () => {
  try {
    const data = await get<User[]>("/users");
    users.value = data || [];
  } catch (err) {
    console.error("Failed fetch users");
    // Mock
    users.value = [
      { userId: 1, username: "admin" },
      { userId: 2, username: "staff" },
    ];
  }
};

const fetchBranches = async () => {
  try {
    const data = await get<any[]>("/branches");
    branches.value = data || [];
  } catch (err) {
    console.error("Failed to fetch branches", err);
  }
};

const filteredIngredients = computed(() => {
  let result = ingredients.value;

  if (searchQuery.value) {
    const q = searchQuery.value.toLowerCase();
    result = result.filter(
      (i) =>
        i.name.toLowerCase().includes(q) ||
        (i.sku && i.sku.toLowerCase().includes(q)),
    );
  }

  if (filterStatus.value === "low") {
    result = result.filter((i) => i.currentStock <= i.reorderLevel);
  } else if (filterStatus.value === "sufficient") {
    result = result.filter((i) => i.currentStock > i.reorderLevel);
  }

  return result;
});

// Actions
const openCreateModal = () => {
  isEditing.value = false;
  editingId.value = null;
  Object.assign(newIngredient, {
    name: "",
    unit: "pcs",
    currentStock: 0,
    costPerUnit: 0,
    reorderLevel: 5,
    sku: "",
  });
  showCreateModal.value = true;
};

const saveIngredient = async () => {
  try {
    if (isEditing.value && editingId.value) {
      await put(`/ingredients/${editingId.value}`, newIngredient);
      toast.success("Ingredient updated successfully");
    } else {
      await post("/ingredients", newIngredient);
      toast.success("Ingredient created successfully");
    }
    showCreateModal.value = false;
    fetchIngredients();
  } catch (err) {
    toast.error("Failed to save ingredient");
  }
};

const editIngredient = (item: Ingredient) => {
  isEditing.value = true;
  editingId.value = item.ingredientId;
  Object.assign(newIngredient, {
    name: item.name,
    unit: item.unit,
    currentStock: item.currentStock,
    costPerUnit: item.costPerUnit,
    reorderLevel: item.reorderLevel,
    sku: item.sku || "",
  });
  showCreateModal.value = true;
};

const openStockIn = (item: Ingredient) => {
  selectedIngredient.value = item;
  newStockIn.ingredientId = item.ingredientId;
  newStockIn.unitCost = item.costPerUnit || 0;
  newStockIn.qtyIn = 0;
  newStockIn.receivedDate = new Date().toISOString();
  showStockInModal.value = true;
};

const createStockIn = async () => {
  // Calculate total cost
  newStockIn.totalCost = newStockIn.qtyIn * newStockIn.unitCost;

  try {
    await post("/stock-in", newStockIn);
    showStockInModal.value = false;
    toast.success("Stock-in recorded successfully");
    fetchIngredients();
  } catch (err) {
    console.error(err);
    toast.error("Failed to record stock in");
  }
};

const openAdjustment = (item: Ingredient) => {
  selectedIngredient.value = item;
  newAdjustment.ingredientId = item.ingredientId;
  newAdjustment.branchId =
    branches.value.length > 0 ? branches.value[0].branchId : null;
  newAdjustment.qtyChange = 0;
  newAdjustment.note = "";
  newAdjustment.date = new Date().toISOString();
  showAdjustmentModal.value = true;
};

const createAdjustment = async () => {
  try {
    const { user } = useAuth();
    newAdjustment.createdBy = user.value?.userId || 1;
    newAdjustment.date = new Date().toISOString();
    await post("/stock-adjustments", newAdjustment);
    showAdjustmentModal.value = false;
    toast.success("Inventory adjustment submitted for approval");
    fetchIngredients();
  } catch (err) {
    console.error(err);
    toast.error("Failed to submit adjustment");
  }
};

const initiateApproval = (adj: any) => {
  selectedAdjustment.value = adj;
  showApprovalModal.value = true;
};

const confirmApproval = async (data: { pin: string }) => {
  if (!selectedAdjustment.value) return;
  approving.value = true;
  try {
    await put(
      `/stock-adjustments/${selectedAdjustment.value.adjustmentId}/approve`,
      null,
      {
        pinCode: data.pin,
      },
    );
    toast.success("Adjustment approved");
    showApprovalModal.value = false;
    fetchAdjustments();
  } catch (e: any) {
    toast.error(e.response?.data?.message || "Approval failed");
  } finally {
    approving.value = false;
  }
};

onMounted(() => {
  fetchIngredients();
  fetchSuppliers();
  fetchUsers();
  fetchBranches();
});
</script>
