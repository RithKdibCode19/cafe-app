<template>
  <div class="min-h-screen bg-neutral-900 flex">
    <!-- Left panel - Categories & Menu Items -->
    <div class="flex-1 flex flex-col">
      <!-- Header -->
      <header
        class="h-16 px-4 flex items-center justify-between bg-neutral-800 border-b border-neutral-700"
      >
        <div class="flex items-center gap-4">
          <NuxtLink
            to="/"
            class="flex items-center gap-2 text-white hover:text-primary-400 transition-colors"
          >
            <div
              class="w-10 h-10 rounded-xl overflow-hidden flex items-center justify-center"
            >
              <img
                src="~/assets/images/cofeoshop.jpg"
                alt="Cafe POS"
                class="w-full h-full object-cover"
              />
            </div>
            <span class="font-bold text-lg">Cofeoshop</span>
          </NuxtLink>
        </div>

        <div class="flex items-center gap-3">
          <LanguageSwitcher />

          <!-- Shift Status -->
          <button
            @click="openShiftModal"
            :class="[
              'px-3 py-1.5 rounded-xl text-sm font-bold flex items-center gap-2 transition-colors',
              currentShift
                ? 'bg-success-500/10 text-success-500 border border-success-500/20 hover:bg-success-500/20'
                : 'bg-red-500/10 text-red-500 border border-red-500/20 hover:bg-red-500/20',
            ]"
          >
            <div
              :class="[
                'w-2 h-2 rounded-full',
                currentShift ? 'bg-success-500 animate-pulse' : 'bg-red-500',
              ]"
            ></div>
            {{ currentShift ? "Shift Open" : "Shift Closed" }}
          </button>

          <!-- Order History -->
          <button
            @click="openOrderHistory"
            class="p-2 rounded-xl text-neutral-400 hover:text-white hover:bg-neutral-700 transition-colors"
            title="Order History"
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
              <path d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
          </button>

          <!-- Drawer Action -->
          <button
            @click="showDrawerModal = true"
            class="p-2 rounded-xl text-neutral-400 hover:text-white hover:bg-neutral-700 transition-colors"
            title="Cash Management"
          >
            <svg
              xmlns="http://www.w3.org/2000/svg"
              class="w-5 h-5"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              stroke-width="2"
            >
              <rect width="20" height="12" x="2" y="6" rx="2" />
              <path d="M12 12h.01" />
              <path d="M17 12h.01" />
              <path d="M7 12h.01" />
            </svg>
          </button>

          <!-- Admin link -->
          <NuxtLink
            to="/admin"
            class="p-2 rounded-xl text-neutral-400 hover:text-white hover:bg-neutral-700 transition-colors"
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
              <path
                d="M12.22 2h-.44a2 2 0 0 0-2 2v.18a2 2 0 0 1-1 1.73l-.43.25a2 2 0 0 1-2 0l-.15-.08a2 2 0 0 0-2.73.73l-.22.38a2 2 0 0 0 .73 2.73l.15.1a2 2 0 0 1 1 1.72v.51a2 2 0 0 1-1 1.74l-.15.09a2 2 0 0 0-.73 2.73l.22.38a2 2 0 0 0 2.73.73l.15-.08a2 2 0 0 1 2 0l.43.25a2 2 0 0 1 1 1.73V20a2 2 0 0 0 2 2h.44a2 2 0 0 0 2-2v-.18a2 2 0 0 1 1-1.73l.43-.25a2 2 0 0 1 2 0l.15.08a2 2 0 0 0 2.73-.73l.22-.39a2 2 0 0 0-.73-2.73l-.15-.08a2 2 0 0 1-1-1.74v-.5a2 2 0 0 1 1-1.74l.15-.09a2 2 0 0 0 .73-2.73l-.22-.38a2 2 0 0 0-2.73-.73l-.15.08a2 2 0 0 1-2 0l-.43-.25a2 2 0 0 1-1-1.73V4a2 2 0 0 0-2-2z"
              />
              <circle cx="12" cy="12" r="3" />
            </svg>
          </NuxtLink>

          <!-- Logout button -->
          <button
            @click="logout"
            class="p-2 rounded-xl text-neutral-400 hover:text-red-400 hover:bg-neutral-700 transition-colors"
            title="Logout"
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
              <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4" />
              <polyline points="16 17 21 12 16 7" />
              <line x1="21" x2="9" y1="12" y2="12" />
            </svg>
          </button>
        </div>
      </header>

      <!-- Content -->
      <div class="flex-1 overflow-hidden">
        <slot />
      </div>
    </div>

    <!-- Right panel - Order Summary (fixed width) -->
    <aside
      class="w-96 bg-neutral-800 border-l border-neutral-700 flex flex-col h-screen sticky top-0"
    >
      <!-- Order header -->
      <div class="p-4 border-b border-neutral-700">
        <div class="flex items-center justify-between">
          <h2 class="text-lg font-semibold text-white">Current Order</h2>
          <span
            :class="[
              'px-2 py-0.5 rounded text-[10px] font-bold uppercase',
              customer.id
                ? 'bg-indigo-600 text-white'
                : 'bg-primary-600 text-white',
            ]"
          >
            {{ customer.id ? customer.level : "#NEW" }}
          </span>
        </div>
        <div
          @click="openCustomerModal"
          class="mt-2 flex items-center justify-between p-2 rounded-lg bg-neutral-700/50 hover:bg-neutral-700 cursor-pointer transition-colors group"
        >
          <div
            class="flex items-center gap-2 text-sm text-neutral-300 group-hover:text-white"
          >
            <svg
              xmlns="http://www.w3.org/2000/svg"
              class="w-4 h-4"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              stroke-width="2"
            >
              <path d="M19 21v-2a4 4 0 0 0-4-4H9a4 4 0 0 0-4 4v2" />
              <circle cx="12" cy="7" r="4" />
            </svg>
            <span class="truncate max-w-[150px]">{{ customer.name }}</span>
          </div>
          <svg
            xmlns="http://www.w3.org/2000/svg"
            class="w-4 h-4 text-neutral-500"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            stroke-width="2"
          >
            <path d="M9 18l6-6-6-6" />
          </svg>
        </div>
        <div
          v-if="customer.id"
          class="mt-2 text-[10px] text-neutral-400 flex justify-between px-1"
        >
          <span
            >Available Points:
            <strong class="text-warning-500">{{
              customer.points
            }}</strong></span
          >
          <button
            v-if="customer.points > 0 && pointsDiscount === 0"
            @click="applyPoints(customer.points)"
            class="text-primary-400 hover:underline"
          >
            Redeem All
          </button>
          <span v-else-if="pointsDiscount > 0" class="text-success-400"
            >Discount Applied!</span
          >
        </div>
      </div>

      <!-- Held Orders Panel -->
      <div
        v-if="heldOrders.length > 0"
        class="px-4 py-2 border-b border-neutral-700 bg-neutral-900/50"
      >
        <div class="flex items-center justify-between mb-2">
          <span
            class="text-xs font-bold text-neutral-400 uppercase tracking-wider"
            >Held Orders</span
          >
          <span
            class="text-xs bg-yellow-500/20 text-yellow-400 px-2 py-0.5 rounded-full font-bold"
            >{{ heldOrders.length }}</span
          >
        </div>
        <div class="space-y-2 max-h-32 overflow-y-auto scrollbar-thin">
          <div
            v-for="order in heldOrders"
            :key="order.id"
            class="flex items-center justify-between p-2 rounded-lg bg-neutral-800 hover:bg-neutral-700 group"
          >
            <div class="flex-1 min-w-0">
              <div class="text-sm text-white font-medium truncate">
                {{ order.note }}
              </div>
              <div class="text-xs text-neutral-400">
                {{ order.items.length }} items · {{ order.customer.name }}
              </div>
            </div>
            <div class="flex gap-1">
              <button
                @click="resumeOrder(order.id)"
                class="p-1.5 rounded-lg text-primary-400 hover:bg-primary-500/20"
                title="Resume Order"
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
                  <polyline points="9 10 4 15 9 20" />
                  <path d="M20 4v7a4 4 0 0 1-4 4H4" />
                </svg>
              </button>
              <button
                @click="removeHeldOrder(order.id)"
                class="p-1.5 rounded-lg text-red-400 hover:bg-red-500/20 opacity-0 group-hover:opacity-100"
                title="Remove"
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
                  <path d="M18 6 6 18" />
                  <path d="m6 6 12 12" />
                </svg>
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- Order items -->
      <div class="flex-1 overflow-y-auto p-4 space-y-3 scrollbar-thin">
        <!-- Order Type Toggle -->
        <div class="flex p-1 bg-neutral-700/50 rounded-lg mb-2">
          <button
            @click="orderType = 'DINE_IN'"
            :class="[
              'flex-1 py-1.5 rounded-md text-xs font-bold transition-all',
              orderType === 'DINE_IN'
                ? 'bg-primary-600 text-white shadow'
                : 'text-neutral-400 hover:text-white',
            ]"
          >
            Dine In
          </button>
          <button
            @click="orderType = 'TAKEAWAY'"
            :class="[
              'flex-1 py-1.5 rounded-md text-xs font-bold transition-all',
              orderType === 'TAKEAWAY'
                ? 'bg-orange-600 text-white shadow'
                : 'text-neutral-400 hover:text-white',
            ]"
          >
            Takeaway
          </button>
        </div>
        <div
          v-if="cartItems.length === 0"
          class="text-center py-12 text-neutral-500"
        >
          <svg
            xmlns="http://www.w3.org/2000/svg"
            class="w-12 h-12 mx-auto mb-3 opacity-50"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            stroke-width="1.5"
            stroke-linecap="round"
            stroke-linejoin="round"
          >
            <circle cx="8" cy="21" r="1" />
            <circle cx="19" cy="21" r="1" />
            <path
              d="M2.05 2.05h2l2.66 12.42a2 2 0 0 0 2 1.58h9.78a2 2 0 0 0 1.95-1.57l1.65-7.43H5.12"
            />
          </svg>
          <p>No items in cart</p>
          <p class="text-sm mt-1">Select items from the menu</p>
        </div>

        <div v-else class="space-y-3">
          <div
            v-for="(item, index) in cartItems"
            :key="index"
            class="bg-neutral-700/50 p-3 rounded-xl flex gap-3 group"
          >
            <div
              class="w-12 h-12 rounded-lg bg-neutral-600 flex-shrink-0 overflow-hidden"
            >
              <img
                v-if="item.imageUrl"
                :src="item.imageUrl"
                class="w-full h-full object-cover"
              />
            </div>
            <div class="flex-1 min-w-0">
              <div class="flex justify-between items-start">
                <div class="min-w-0">
                  <h4 class="text-white font-medium truncate">
                    {{ item.name }}
                  </h4>
                  <!-- Show add-ons if any -->
                  <div
                    v-if="item.addOns && item.addOns.length > 0"
                    class="flex flex-wrap gap-1 mt-1"
                  >
                    <span
                      v-for="addon in item.addOns"
                      :key="addon.addonId"
                      class="text-[10px] px-1.5 py-0.5 bg-primary-500/20 text-primary-400 rounded"
                    >
                      + {{ addon.name }}
                    </span>
                  </div>
                  <div
                    v-if="item.notes"
                    class="text-[10px] text-neutral-500 mt-1 italic truncate"
                  >
                    {{ item.notes }}
                  </div>
                </div>
                <span class="text-primary-400 font-bold flex-shrink-0 ml-2"
                  >${{
                    ((item.price + (item.addOnTotal || 0)) * item.qty).toFixed(
                      2,
                    )
                  }}</span
                >
              </div>
              <div class="flex items-center justify-between mt-2">
                <span class="text-xs text-neutral-400"
                  >${{ (item.price + (item.addOnTotal || 0)).toFixed(2) }} /
                  unit</span
                >

                <div
                  class="flex items-center gap-2 bg-neutral-800 rounded-lg p-0.5"
                >
                  <button
                    @click="updateQty(index, -1)"
                    class="w-6 h-6 flex items-center justify-center text-neutral-400 hover:text-white hover:bg-neutral-700 rounded transition-colors"
                  >
                    -
                  </button>
                  <span
                    class="text-sm font-medium w-4 text-center text-white"
                    >{{ item.qty }}</span
                  >
                  <button
                    @click="updateQty(index, 1)"
                    class="w-6 h-6 flex items-center justify-center text-neutral-400 hover:text-white hover:bg-neutral-700 rounded transition-colors"
                  >
                    +
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Smart Suggestions -->
      <div class="px-4 pb-4">
        <PosSmartSuggestions
          :last-item-id="lastAddedItem?.id || null"
          :last-item-name="lastAddedItem?.name || ''"
          @add="addToCart"
        />
      </div>

      <!-- Order totals -->
      <div class="p-4 border-t border-neutral-700 space-y-3 bg-neutral-800">
        <div class="flex justify-between text-sm text-neutral-400">
          <span>Subtotal</span>
          <span>${{ subtotal.toFixed(2) }}</span>
        </div>

        <!-- Manual Discount -->
        <div class="flex justify-between text-sm text-neutral-400 items-center">
          <span>Discount</span>
          <button
            @click="openDiscountModal"
            class="text-primary-400 hover:text-primary-300 text-xs font-bold px-2 py-1 bg-primary-500/10 rounded border border-primary-500/20 transition-colors"
          >
            {{
              manualDiscount > 0
                ? "-$" + manualDiscount.toFixed(2)
                : "Add Discount"
            }}
          </button>
        </div>
        <div
          v-if="pointsDiscount > 0"
          class="flex justify-between text-sm text-success-500 font-medium italic"
        >
          <span>Loyalty Discount</span>
          <span>-${{ pointsDiscount.toFixed(2) }}</span>
        </div>
        <div class="flex justify-between text-sm text-neutral-400">
          <span>Tax (10%)</span>
          <span>${{ tax.toFixed(2) }}</span>
        </div>
        <div
          class="flex justify-between text-lg font-semibold text-white pt-2 border-t border-neutral-700"
        >
          <span>Total</span>
          <span class="text-primary-400">${{ total.toFixed(2) }}</span>
        </div>
      </div>

      <!-- Action buttons -->
      <div class="p-4 border-t border-neutral-700 space-y-2 bg-neutral-800">
        <button
          @click="openPaymentModal"
          :disabled="cartItems.length === 0"
          class="w-full btn-primary btn-lg disabled:opacity-50 disabled:cursor-not-allowed"
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
            <rect width="20" height="14" x="2" y="5" rx="2" />
            <line x1="2" x2="22" y1="10" y2="10" />
          </svg>
          Checkout
        </button>
        <div class="grid grid-cols-2 gap-2">
          <button
            @click="openHoldOrderModal()"
            :disabled="cartItems.length === 0"
            class="btn-secondary text-neutral-300 disabled:opacity-50 disabled:cursor-not-allowed"
          >
            Hold Order
          </button>
          <button
            @click="clearCart"
            class="btn-ghost text-error-400 hover:bg-error-500/10"
          >
            Clear Cart
          </button>
        </div>
      </div>
    </aside>

    <!-- Payment Modal -->
    <!-- Order History Modal -->
    <div
      v-if="showOrderHistoryModal"
      class="fixed inset-0 z-50 flex items-center justify-center bg-black/80 backdrop-blur-sm p-4"
    >
      <div
        class="bg-neutral-800 rounded-2xl shadow-xl w-full max-w-2xl border border-neutral-700 flex flex-col max-h-[80vh]"
      >
        <div
          class="p-4 border-b border-neutral-700 flex justify-between items-center"
        >
          <h3 class="text-lg font-bold text-white tracking-tight uppercase">
            Order History
          </h3>
          <button
            @click="showOrderHistoryModal = false"
            class="text-neutral-400 hover:text-white transition-colors"
          >
            <svg
              xmlns="http://www.w3.org/2000/svg"
              class="w-6 h-6"
              fill="none"
              viewBox="0 0 24 24"
              stroke="currentColor"
              stroke-width="2"
            >
              <path d="M18 6L6 18M6 6l12 12" />
            </svg>
          </button>
        </div>
        <div class="p-4 overflow-y-auto custom-scrollbar flex-1">
          <div v-if="orderLoading" class="flex justify-center p-8">
            <div
              class="animate-spin rounded-full h-8 w-8 border-b-2 border-primary-500"
            ></div>
          </div>
          <div
            v-else-if="recentOrders.length === 0"
            class="text-center p-8 text-neutral-500"
          >
            No recent orders found.
          </div>
          <div v-else class="space-y-3">
            <div
              v-for="order in recentOrders"
              :key="order.orderId"
              class="bg-neutral-900 border border-neutral-700 rounded-xl p-4 hover:border-neutral-500 transition-colors group"
            >
              <div class="flex justify-between items-start mb-2">
                <div>
                  <div
                    class="text-sm font-black text-white font-mono tracking-tight"
                  >
                    {{ order.orderNo }}
                  </div>
                  <div class="text-[10px] text-neutral-500 font-bold uppercase">
                    {{ new Date(order.createdAt).toLocaleTimeString() }} ·
                    {{ order.customer?.name || "Walk-in" }}
                  </div>
                </div>
                <div class="flex items-center gap-2">
                  <span
                    :class="[
                      'text-[10px] px-2 py-0.5 rounded-full font-black uppercase',
                      order.status === 'PAID'
                        ? 'bg-success-500/10 text-success-400'
                        : order.status === 'PENDING'
                          ? 'bg-warning-500/10 text-warning-400'
                          : 'bg-neutral-800 text-neutral-500',
                    ]"
                    >{{ order.status }}</span
                  >
                  <span class="text-white font-black"
                    >${{ (order.totalAmount || 0).toFixed(2) }}</span
                  >
                </div>
              </div>
              <div
                class="flex justify-end gap-2 mt-2 pt-2 border-t border-neutral-800"
              >
                <button
                  v-if="order.status === 'PENDING'"
                  @click="initiateAdjustment(order, 'VOID')"
                  class="px-3 py-1.5 rounded-lg text-[10px] font-black uppercase tracking-widest bg-orange-500/10 text-orange-400 border border-orange-500/20 hover:bg-orange-500/20"
                >
                  Void
                </button>
                <button
                  v-if="order.status === 'PAID'"
                  @click="initiateAdjustment(order, 'REFUND')"
                  class="px-3 py-1.5 rounded-lg text-[10px] font-black uppercase tracking-widest bg-red-500/10 text-red-400 border border-red-500/20 hover:bg-red-500/20"
                >
                  Refund
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Approval Modal (PIN + Reason) -->
    <PosApprovalModal
      v-model="showApprovalModal"
      :action-type="approvalActionType"
      :loading="adjustmentLoading"
      @approve="handleApprovedAdjustment"
    />
    <div
      v-if="showPaymentModal"
      class="fixed inset-0 z-50 flex items-center justify-center bg-black/80 backdrop-blur-sm p-4"
    >
      <div
        class="bg-neutral-800 rounded-2xl shadow-xl w-full max-w-4xl h-[600px] flex overflow-hidden border border-neutral-700"
      >
        <!-- Payment Methods (Left) -->
        <div class="w-1/3 border-r border-neutral-700 p-6 bg-neutral-900/50">
          <h3 class="text-xl font-bold text-white mb-6">Payment Method</h3>
          <div class="space-y-3">
            <button
              @click="selectMethod('CASH')"
              :class="[
                'w-full p-4 rounded-xl flex items-center gap-3 transition-all',
                selectedMethod === 'CASH'
                  ? 'bg-primary-600 text-white shadow-lg shadow-primary-900/50'
                  : 'bg-neutral-800 text-neutral-400 hover:bg-neutral-700',
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
                <rect width="20" height="12" x="2" y="6" rx="2" />
                <circle cx="12" cy="12" r="2" />
                <path d="M6 12h.01M18 12h.01" />
              </svg>
              <span class="font-medium text-lg">Cash</span>
            </button>

            <button
              @click="selectMethod('KHQR')"
              :class="[
                'w-full p-4 rounded-xl flex items-center gap-3 transition-all',
                selectedMethod === 'KHQR'
                  ? 'bg-red-600 text-white shadow-lg shadow-red-900/50'
                  : 'bg-neutral-800 text-neutral-400 hover:bg-neutral-700',
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
                <rect width="18" height="18" x="3" y="3" rx="2" ry="2" />
                <path d="M7 7h3v3H7z" />
                <path d="M14 7h3v3h-3z" />
                <path d="M7 14h3v3H7z" />
                <path d="M14 14v3" />
              </svg>
              <span class="font-medium text-lg">Bakong KHQR</span>
            </button>
          </div>

          <div class="mt-auto pt-8">
            <div class="flex justify-between text-neutral-400 text-sm mb-2">
              <span>Total Due</span>
            </div>
            <div class="text-3xl font-bold text-white mb-8">
              ${{ total.toFixed(2) }}
            </div>

            <button
              @click="closePaymentModal"
              class="w-full py-3 rounded-xl border border-neutral-600 text-neutral-400 hover:text-white hover:border-neutral-500"
            >
              Cancel
            </button>
          </div>
        </div>

        <!-- Payment Content (Right) -->
        <div
          class="flex-1 p-8 flex flex-col items-center justify-center relative"
        >
          <!-- Cash Interface -->
          <div
            v-if="selectedMethod === 'CASH'"
            class="w-full max-w-md bg-neutral-900/80 p-5 rounded-3xl border border-neutral-700 flex flex-col h-full overflow-hidden"
          >
            <div class="text-center mb-2 flex-shrink-0">
              <p class="text-neutral-400 text-xs mb-1">Total Due</p>
              <h3 class="text-3xl font-black text-white tracking-tight">
                ${{ total.toFixed(2) }}
              </h3>
            </div>

            <!-- Cash Input Display -->
            <div
              class="bg-neutral-800 rounded-xl p-3 mb-3 border border-neutral-700 relative overflow-hidden flex-shrink-0"
            >
              <div class="flex justify-between items-end mb-1">
                <span
                  class="text-neutral-500 text-[10px] font-bold uppercase tracking-widest"
                  >Cash Received</span
                >
              </div>
              <!-- Input field (readonly) -->
              <div class="flex items-center justify-between">
                <span class="text-xl font-bold text-neutral-500">$</span>
                <span
                  :class="[
                    'text-3xl font-mono font-bold tracking-tight',
                    cashReceived ? 'text-white' : 'text-neutral-600',
                  ]"
                >
                  {{ cashReceived ? cashReceived : "0.00" }}
                </span>
              </div>
              <!-- Quick add buttons -->
              <div
                class="flex gap-2 mt-2 pt-2 border-t border-neutral-700 overflow-x-auto scrollbar-hide"
              >
                <button
                  @click="setCash(total)"
                  class="px-2 py-1.5 bg-neutral-700 hover:bg-neutral-600 rounded-lg text-[10px] text-white font-bold whitespace-nowrap"
                >
                  Exact
                </button>
                <button
                  @click="setCash(Math.ceil(total))"
                  class="px-2 py-1.5 bg-neutral-700 hover:bg-neutral-600 rounded-lg text-[10px] text-white font-bold whitespace-nowrap"
                >
                  ${{ Math.ceil(total) }}
                </button>
                <button
                  @click="setCash(10)"
                  class="px-2 py-1.5 bg-neutral-700 hover:bg-neutral-600 rounded-lg text-[10px] text-white font-bold whitespace-nowrap"
                >
                  $10
                </button>
                <button
                  @click="setCash(20)"
                  class="px-2 py-1.5 bg-neutral-700 hover:bg-neutral-600 rounded-lg text-[10px] text-white font-bold whitespace-nowrap"
                >
                  $20
                </button>
                <button
                  @click="setCash(50)"
                  class="px-2 py-1.5 bg-neutral-700 hover:bg-neutral-600 rounded-lg text-[10px] text-white font-bold whitespace-nowrap"
                >
                  $50
                </button>
              </div>
            </div>

            <!-- Change Display -->
            <div
              class="flex justify-between items-center px-4 py-3 bg-neutral-800 rounded-xl border border-neutral-700 mb-3 flex-shrink-0"
            >
              <span class="text-neutral-400 font-bold uppercase text-[10px]"
                >Change Due</span
              >
              <span
                :class="[
                  'text-xl font-black font-mono',
                  changeDue >= 0 ? 'text-success-400' : 'text-error-500',
                ]"
              >
                {{
                  changeDue >= 0
                    ? "$" + changeDue.toFixed(2)
                    : "-$" + Math.abs(changeDue).toFixed(2)
                }}
              </span>
            </div>

            <!-- Keypad -->
            <div class="grid grid-cols-3 gap-2 flex-1 min-h-0">
              <button
                v-for="n in [1, 2, 3, 4, 5, 6, 7, 8, 9]"
                :key="n"
                @click="addCashDigit(n.toString())"
                class="h-full max-h-14 rounded-lg bg-neutral-800 hover:bg-neutral-700 text-white text-xl font-bold transition-colors shadow-sm flex items-center justify-center"
              >
                {{ n }}
              </button>
              <button
                @click="addCashDigit('.')"
                class="h-full max-h-14 rounded-lg bg-neutral-800 hover:bg-neutral-700 text-white text-xl font-bold transition-colors flex items-center justify-center"
              >
                .
              </button>
              <button
                @click="addCashDigit('0')"
                class="h-full max-h-14 rounded-lg bg-neutral-800 hover:bg-neutral-700 text-white text-xl font-bold transition-colors flex items-center justify-center"
              >
                0
              </button>
              <button
                @click="clearCash"
                class="h-full max-h-14 rounded-lg bg-error-500/10 hover:bg-error-500/20 text-error-500 hover:text-error-400 text-lg font-bold transition-colors flex items-center justify-center"
              >
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  class="w-5 h-5"
                  viewBox="0 0 24 24"
                  fill="none"
                  stroke="currentColor"
                  stroke-width="2"
                >
                  <path
                    d="M21 4H8l-7 8 7 8h13a2 2 0 0 0 2-2V6a2 2 0 0 0-2-2z"
                  />
                  <line x1="18" y1="9" x2="12" y2="15" />
                  <line x1="12" y1="9" x2="18" y2="15" />
                </svg>
              </button>
            </div>

            <button
              @click="completeOrder"
              :disabled="isProcessing || changeDue < 0"
              class="w-full btn-primary py-3 rounded-xl mt-3 text-sm font-bold uppercase tracking-wider disabled:opacity-50 disabled:cursor-not-allowed flex-shrink-0"
            >
              {{
                isProcessing
                  ? "Processing..."
                  : changeDue < 0
                    ? "Insufficient Cash"
                    : "Confirm Payment"
              }}
            </button>
          </div>

          <!-- KHQR Interface -->
          <div
            v-if="selectedMethod === 'KHQR'"
            class="text-center w-full h-full flex flex-col items-center justify-center p-4"
          >
            <div
              v-if="!khqrData && isProcessing"
              class="flex flex-col items-center"
            >
              <div
                class="w-16 h-16 border-4 border-red-500/30 border-t-red-500 rounded-full animate-spin mb-6"
              ></div>
              <p
                class="text-neutral-400 font-bold animate-pulse uppercase tracking-widest text-xs"
              >
                Connecting to Bakong Gateway...
              </p>
            </div>

            <div
              v-else-if="khqrData"
              class="flex flex-col items-center justify-between h-full w-full py-4"
            >
              <div class="flex-1 flex items-center justify-center w-full">
                <PosKHQRDisplay
                  :qr-value="khqrData.qr"
                  :amount="total"
                  merchant-name="Cafe POS System"
                />
              </div>

              <div
                class="mt-8 flex items-center gap-4 px-8 py-4 rounded-2xl bg-neutral-900 border border-neutral-700 shadow-inner"
              >
                <div
                  v-if="paymentStatus === 'PENDING'"
                  class="flex items-center gap-4"
                >
                  <span class="relative flex h-4 w-4">
                    <span
                      class="animate-ping absolute inline-flex h-full w-full rounded-full bg-yellow-400 opacity-75"
                    ></span>
                    <span
                      class="relative inline-flex rounded-full h-4 w-4 bg-yellow-500 shadow-[0_0_10px_rgba(234,179,8,0.5)]"
                    ></span>
                  </span>
                  <div class="text-left">
                    <span
                      class="text-white font-black uppercase text-[10px] block tracking-widest"
                      >Payment Status</span
                    >
                    <span class="text-yellow-500 font-bold text-sm"
                      >Waiting for Scan...</span
                    >
                  </div>
                </div>
                <div
                  v-else-if="paymentStatus === 'COMPLETED'"
                  class="flex items-center gap-4 text-left"
                >
                  <div
                    class="w-8 h-8 rounded-full bg-green-500 flex items-center justify-center shadow-[0_0_15px_rgba(34,197,94,0.5)]"
                  >
                    <svg
                      xmlns="http://www.w3.org/2000/svg"
                      class="w-5 h-5 text-white"
                      viewBox="0 0 24 24"
                      fill="none"
                      stroke="currentColor"
                      stroke-width="3"
                      stroke-linecap="round"
                      stroke-linejoin="round"
                    >
                      <polyline points="20 6 9 17 4 12" />
                    </svg>
                  </div>
                  <div>
                    <span
                      class="text-white font-black uppercase text-[10px] block tracking-widest"
                      >Payment Status</span
                    >
                    <span class="text-green-500 font-bold text-sm uppercase"
                      >Payment Received!</span
                    >
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Receipt Modal -->
    <div
      v-if="showReceiptModal"
      class="fixed inset-0 z-50 flex items-center justify-center bg-black/80 backdrop-blur-sm p-4"
    >
      <div
        class="bg-neutral-800 rounded-2xl shadow-xl max-w-md w-full max-h-[90vh] overflow-hidden border border-neutral-700"
      >
        <div
          class="p-4 border-b border-neutral-700 flex items-center justify-between"
        >
          <h3 class="text-lg font-semibold text-white flex items-center gap-2">
            <svg
              xmlns="http://www.w3.org/2000/svg"
              class="w-5 h-5 text-green-500"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              stroke-width="2"
              stroke-linecap="round"
              stroke-linejoin="round"
            >
              <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14" />
              <polyline points="22 4 12 14.01 9 11.01" />
            </svg>
            Order Complete!
          </h3>
          <button
            @click="closeReceiptModal"
            class="text-neutral-400 hover:text-white"
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
              <path d="M18 6 6 18" />
              <path d="m6 6 12 12" />
            </svg>
          </button>
        </div>

        <div class="p-4 overflow-y-auto max-h-[60vh]" id="receipt-print-area">
          <div
            class="receipt bg-white text-neutral-900 p-6 rounded-xl font-mono text-sm"
          >
            <!-- Header -->
            <div class="text-center mb-4">
              <div class="text-4xl mb-2">☕</div>
              <h4 class="font-bold text-lg">
                {{ receiptData?.branchName || "Cafe POS" }}
              </h4>
              <p
                v-if="receiptData?.branchPhone"
                class="text-xs text-neutral-500"
              >
                {{ receiptData.branchPhone }}
              </p>
              <p
                v-if="receiptData?.branchAddress"
                class="text-xs text-neutral-500"
              >
                {{ receiptData.branchAddress }}
              </p>
            </div>

            <div class="border-t border-dashed border-neutral-300 my-3"></div>

            <!-- Order Info -->
            <div class="space-y-1 text-xs">
              <div class="flex justify-between">
                <span>Order #:</span
                ><span class="font-bold">{{ receiptData?.orderNo }}</span>
              </div>
              <div class="flex justify-between">
                <span>Date:</span><span>{{ receiptData?.orderDate }}</span>
              </div>
              <div class="flex justify-between">
                <span>Time:</span><span>{{ receiptData?.orderTime }}</span>
              </div>
              <div class="flex justify-between">
                <span>Type:</span><span>{{ receiptData?.orderType }}</span>
              </div>
              <div class="flex justify-between">
                <span>Cashier:</span><span>{{ receiptData?.cashierName }}</span>
              </div>
            </div>

            <div class="border-t border-solid border-neutral-400 my-3"></div>

            <!-- Items -->
            <div class="space-y-2">
              <div
                v-for="(item, idx) in receiptData?.items"
                :key="idx"
                class="text-xs"
              >
                <div class="flex justify-between">
                  <span
                    >{{ item.name }}
                    <span v-if="item.variant">({{ item.variant }})</span></span
                  >
                  <span>${{ item.lineTotal?.toFixed(2) }}</span>
                </div>
                <div class="text-neutral-500 pl-2">
                  {{ item.qty }} x ${{ item.unitPrice?.toFixed(2) }}
                </div>
              </div>
            </div>

            <div class="border-t border-solid border-neutral-400 my-3"></div>

            <!-- Totals -->
            <div class="space-y-1 text-xs">
              <div class="flex justify-between">
                <span>Subtotal:</span
                ><span>${{ receiptData?.subTotal?.toFixed(2) }}</span>
              </div>
              <div
                v-if="receiptData?.discountAmount > 0"
                class="flex justify-between text-red-600"
              >
                <span>Discount:</span
                ><span>-${{ receiptData?.discountAmount?.toFixed(2) }}</span>
              </div>
              <div
                v-if="receiptData?.taxAmount > 0"
                class="flex justify-between"
              >
                <span>Tax:</span
                ><span>${{ receiptData?.taxAmount?.toFixed(2) }}</span>
              </div>
              <div class="border-t border-dashed border-neutral-300 my-2"></div>
              <div class="flex justify-between font-bold text-base">
                <span>TOTAL:</span
                ><span>${{ receiptData?.totalAmount?.toFixed(2) }}</span>
              </div>
            </div>

            <div class="border-t border-dashed border-neutral-300 my-3"></div>

            <!-- Payment -->
            <div class="space-y-1 text-xs">
              <div class="flex justify-between">
                <span>Payment:</span
                ><span class="font-bold">{{ receiptData?.paymentMethod }}</span>
              </div>
              <div class="flex justify-between">
                <span>Paid:</span
                ><span>${{ receiptData?.amountPaid?.toFixed(2) }}</span>
              </div>
              <div
                v-if="receiptData?.changeAmount > 0"
                class="flex justify-between text-green-600"
              >
                <span>Change:</span
                ><span>${{ receiptData?.changeAmount?.toFixed(2) }}</span>
              </div>
            </div>

            <div class="border-t border-dashed border-neutral-300 my-3"></div>

            <!-- Footer -->
            <div class="text-center text-xs">
              <p class="font-bold">{{ receiptData?.footerMessage }}</p>
              <p class="text-neutral-400 mt-2">Powered by Cafe POS</p>
            </div>
          </div>
        </div>

        <div class="p-4 border-t border-neutral-700 flex gap-3">
          <button
            @click="printReceipt"
            class="flex-1 btn-primary flex items-center justify-center gap-2"
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
              <polyline points="6 9 6 2 18 2 18 9" />
              <path
                d="M6 18H4a2 2 0 0 1-2-2v-5a2 2 0 0 1 2-2h16a2 2 0 0 1 2 2v5a2 2 0 0 1-2 2h-2"
              />
              <rect x="6" y="14" width="12" height="8" />
            </svg>
            Print Receipt
          </button>
          <button @click="closeReceiptModal" class="flex-1 btn-secondary">
            New Order
          </button>
        </div>
      </div>
    </div>
    <!-- Hold Order Modal -->
    <div
      v-if="showHoldOrderModal"
      class="fixed inset-0 z-[60] flex items-center justify-center bg-black/80 backdrop-blur-sm p-4"
    >
      <div
        class="bg-neutral-800 rounded-2xl shadow-xl w-full max-w-sm border border-neutral-700 overflow-hidden animate-in zoom-in duration-200"
      >
        <div class="p-6 border-b border-neutral-700">
          <h3 class="text-xl font-bold text-white">Hold Order</h3>
        </div>
        <div class="p-6 space-y-4">
          <label class="block text-sm font-medium text-neutral-400"
            >Order Note</label
          >
          <input
            v-model="holdOrderNote"
            type="text"
            class="w-full bg-neutral-900 border border-neutral-700 rounded-xl px-4 py-3 text-white focus:ring-2 focus:ring-primary-500"
            placeholder="e.g. Table 5"
            @keyup.enter="confirmHoldOrder"
            autofocus
          />
        </div>
        <div class="p-6 bg-neutral-900/50 flex justify-end gap-3">
          <button
            @click="showHoldOrderModal = false"
            class="px-4 py-2 rounded-xl text-neutral-400 hover:text-white hover:bg-neutral-800 transition-colors"
          >
            Cancel
          </button>
          <button
            @click="confirmHoldOrder"
            class="px-4 py-2 rounded-xl bg-primary-600 hover:bg-primary-500 text-white font-bold transition-colors"
          >
            Hold Order
          </button>
        </div>
      </div>
    </div>

    <!-- Customer Selection Modal -->
    <div
      v-if="showCustomerModal"
      class="fixed inset-0 z-[60] flex items-center justify-center bg-black/80 backdrop-blur-sm p-4"
    >
      <div
        class="bg-neutral-800 rounded-2xl shadow-xl w-full max-w-lg border border-neutral-700 flex flex-col overflow-hidden animate-in zoom-in duration-200"
      >
        <div
          class="p-6 border-b border-neutral-700 flex justify-between items-center"
        >
          <h3 class="text-xl font-bold text-white">Select Customer</h3>
          <button
            @click="toggleCreateCustomer"
            class="ml-4 text-xs font-bold px-3 py-1.5 bg-neutral-700 border border-neutral-600 text-white rounded-lg hover:bg-primary-600 hover:border-primary-500 transition-colors flex items-center gap-1"
          >
            <span v-if="!isCreatingCustomer">
              <svg
                xmlns="http://www.w3.org/2000/svg"
                class="w-3 h-3 inline mr-1"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                stroke-width="3"
              >
                <line x1="12" y1="5" x2="12" y2="19"></line>
                <line x1="5" y1="12" x2="19" y2="12"></line>
              </svg>
              New Customer
            </span>
            <span v-else>Cancel</span>
          </button>
          <button
            @click="showCustomerModal = false"
            class="text-neutral-400 hover:text-white"
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

        <div class="p-6 space-y-6">
          <!-- Create Customer Form -->
          <div
            v-if="isCreatingCustomer"
            class="space-y-4 bg-neutral-900/50 p-4 rounded-xl border border-neutral-700 animate-in fade-in slide-in-from-top-2 duration-200"
          >
            <div>
              <label class="block text-xs font-medium text-neutral-400 mb-1"
                >Customer Name *</label
              >
              <input
                v-model="newCustomer.name"
                type="text"
                class="w-full bg-neutral-800 border border-neutral-700 rounded-lg px-3 py-2 text-white focus:ring-2 focus:ring-primary-500 text-sm"
                placeholder="John Doe"
              />
            </div>
            <div>
              <label class="block text-xs font-medium text-neutral-400 mb-1"
                >Phone (Optional)</label
              >
              <input
                v-model="newCustomer.phone"
                type="text"
                class="w-full bg-neutral-800 border border-neutral-700 rounded-lg px-3 py-2 text-white focus:ring-2 focus:ring-primary-500 text-sm"
                placeholder="012 345 678"
              />
            </div>
            <div class="grid grid-cols-2 gap-3">
              <div>
                <label class="block text-xs font-medium text-neutral-400 mb-1"
                  >Gender (Optional)</label
                >
                <select
                  v-model="newCustomer.gender"
                  class="w-full bg-neutral-800 border border-neutral-700 rounded-lg px-3 py-2 text-white focus:ring-2 focus:ring-primary-500 text-sm"
                >
                  <option value="MALE">Male</option>
                  <option value="FEMALE">Female</option>
                  <option value="OTHER">Other</option>
                </select>
              </div>
              <div>
                <label class="block text-xs font-medium text-neutral-400 mb-1"
                  >Date of Birth (Optional)</label
                >
                <input
                  v-model="newCustomer.dob"
                  type="date"
                  class="w-full bg-neutral-800 border border-neutral-700 rounded-lg px-3 py-2 text-white focus:ring-2 focus:ring-primary-500 text-sm"
                />
              </div>
            </div>
            <button
              @click="createCustomer"
              class="w-full py-2 bg-primary-600 hover:bg-primary-500 text-white rounded-lg font-bold text-sm transition-colors shadow-lg shadow-primary-900/20"
            >
              Create & Select Customer
            </button>
          </div>

          <div v-else class="relative">
            <input
              v-model="customerSearch"
              @input="searchCustomers"
              type="text"
              placeholder="Search by name or phone..."
              class="w-full bg-neutral-900 border border-neutral-700 rounded-xl px-4 py-3 pl-10 text-white focus:ring-2 focus:ring-primary-500"
            />
            <svg
              xmlns="http://www.w3.org/2000/svg"
              class="w-5 h-5 absolute left-3 top-1/2 -translate-y-1/2 text-neutral-500"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              stroke-width="2"
            >
              <circle cx="11" cy="11" r="8" />
              <path d="m21 21-4.3-4.3" />
            </svg>
          </div>

          <div class="max-h-64 overflow-y-auto space-y-2 pr-2 scrollbar-thin">
            <!-- Walk-in button only if not searching or empty search -->
            <button
              v-if="!customerSearch"
              @click="selectCustomerFromModal(null)"
              class="w-full p-3 rounded-xl bg-neutral-700/50 hover:bg-neutral-700 text-left transition-colors flex items-center justify-between mb-2"
            >
              <span class="text-white font-medium">Walk-in Customer</span>
              <span class="text-[10px] text-neutral-500 uppercase font-black"
                >Default</span
              >
            </button>

            <!-- Recent Customers Header -->
            <div v-if="!customerSearch && recentCustomers.length > 0">
              <p
                class="text-[10px] font-bold text-neutral-500 uppercase tracking-widest mb-2"
              >
                Recent Customers
              </p>
              <button
                v-for="res in recentCustomers"
                :key="'recent-' + res.customerId"
                @click="selectCustomerFromModal(res)"
                class="w-full p-3 rounded-xl hover:bg-neutral-700 text-left transition-colors flex items-center justify-between group mb-2 border border-transparent hover:border-neutral-600"
              >
                <div class="flex items-center gap-3">
                  <div
                    class="w-8 h-8 rounded-full bg-neutral-600 flex items-center justify-center text-xs font-bold text-white"
                  >
                    {{ res.fullName.charAt(0).toUpperCase() }}
                  </div>
                  <div>
                    <div
                      class="text-white font-bold group-hover:text-primary-400"
                    >
                      {{ res.fullName }}
                    </div>
                    <div class="text-xs text-neutral-400">
                      {{ res.phone || "No phone" }}
                    </div>
                  </div>
                </div>
                <div class="text-right">
                  <div class="text-xs font-black text-warning-500">
                    {{ res.loyaltyPoints || 0 }} pts
                  </div>
                  <div class="text-[10px] text-neutral-500">
                    {{ res.membershipLevel }}
                  </div>
                </div>
              </button>
            </div>

            <!-- Search Results -->
            <div v-if="selecting" class="py-12 text-center text-neutral-500">
              Searching...
            </div>
            <div
              v-else-if="
                customerSearch.length >= 2 && searchResults.length === 0
              "
              class="py-12 text-center text-neutral-500 italic"
            >
              No customers found
            </div>

            <button
              v-for="res in searchResults"
              :key="'search-' + res.customerId"
              @click="selectCustomerFromModal(res)"
              class="w-full p-3 rounded-xl hover:bg-neutral-700 text-left transition-colors flex items-center justify-between group"
            >
              <div class="flex items-center gap-3">
                <div
                  class="w-8 h-8 rounded-full bg-neutral-600 flex items-center justify-center text-xs font-bold text-white"
                >
                  {{ res.fullName.charAt(0).toUpperCase() }}
                </div>
                <div>
                  <div
                    class="text-white font-bold group-hover:text-primary-400"
                  >
                    {{ res.fullName }}
                  </div>
                  <div class="text-xs text-neutral-400">
                    {{ res.phone || "No phone" }}
                  </div>
                </div>
              </div>
              <div class="text-right">
                <div class="text-xs font-black text-warning-500">
                  {{ res.loyaltyPoints || 0 }} pts
                </div>
                <div class="text-[10px] text-neutral-500">
                  {{ res.membershipLevel }}
                </div>
              </div>
            </button>
          </div>
        </div>

        <div class="p-6 bg-neutral-900/50 flex justify-end">
          <button
            @click="showCustomerModal = false"
            class="text-sm font-bold text-neutral-400 hover:text-white uppercase tracking-widest"
          >
            Close
          </button>
        </div>
      </div>
    </div>

    <!-- Discount Modal -->
    <div
      v-if="showDiscountModal"
      class="fixed inset-0 z-50 flex items-center justify-center bg-black/80 backdrop-blur-sm p-4"
    >
      <div
        class="bg-neutral-800 rounded-2xl shadow-xl w-full max-w-sm border border-neutral-700 p-6"
      >
        <h3 class="text-xl font-bold text-white mb-4">Apply Discount</h3>
        <p class="text-neutral-400 text-sm mb-4">
          Enter a fixed discount amount.
        </p>

        <div class="relative mb-6">
          <span
            class="absolute left-4 top-1/2 -translate-y-1/2 text-neutral-400 font-bold"
            >$</span
          >
          <input
            v-model="manualDiscountInput"
            type="number"
            min="0"
            step="0.01"
            class="w-full bg-neutral-900 border border-neutral-700 rounded-xl py-4 pl-8 pr-4 text-white text-xl font-bold focus:outline-none focus:border-primary-500 transition-colors"
            placeholder="0.00"
            @keyup.enter="applyDiscount"
          />
        </div>

        <div class="flex gap-3">
          <button
            @click="showDiscountModal = false"
            class="flex-1 py-3 rounded-xl border border-neutral-600 text-neutral-400 hover:text-white hover:border-neutral-500 font-bold"
          >
            Cancel
          </button>
          <button
            @click="applyDiscount"
            class="flex-1 py-3 rounded-xl bg-primary-600 text-white font-bold hover:bg-primary-500 shadow-lg shadow-primary-900/20"
          >
            Apply
          </button>
        </div>
      </div>
    </div>
    <!-- Shift Modal -->
    <!-- Shift Modal -->
    <PosShiftModal
      v-model="showShiftModal"
      :shift="currentShift"
      :summary="currentShiftSummary"
      @success="onShiftSuccess"
    />

    <!-- Drawer Action Modal -->
    <div
      v-if="showDrawerModal"
      class="fixed inset-0 z-[60] flex items-center justify-center bg-black/80 backdrop-blur-sm p-4"
    >
      <div
        class="bg-neutral-800 rounded-2xl shadow-xl w-full max-w-sm border border-neutral-700 flex flex-col"
      >
        <div
          class="p-4 border-b border-neutral-700 flex justify-between items-center"
        >
          <h3 class="text-white font-black uppercase tracking-tighter text-sm">
            Cash Management
          </h3>
          <button
            @click="showDrawerModal = false"
            class="text-neutral-400 hover:text-white"
          >
            <svg
              xmlns="http://www.w3.org/2000/svg"
              class="w-6 h-6"
              fill="none"
              viewBox="0 0 24 24"
              stroke="currentColor"
            >
              <path d="M18 6L6 18M6 6l12 12" />
            </svg>
          </button>
        </div>
        <div class="p-6 space-y-5">
          <div>
            <label
              class="text-[10px] text-neutral-500 font-black uppercase tracking-widest mb-2 block"
              >Action Type</label
            >
            <div class="grid grid-cols-3 gap-2">
              <button
                @click="drawerForm.type = 'PAY_OUT'"
                :class="[
                  'py-2 rounded-lg text-xs font-bold border transition-all',
                  drawerForm.type === 'PAY_OUT'
                    ? 'bg-error-500/20 text-error-400 border-error-500/50'
                    : 'bg-neutral-900 text-neutral-500 border-neutral-700 hover:border-neutral-500',
                ]"
              >
                PAY OUT
              </button>
              <button
                @click="drawerForm.type = 'CASH_DROP'"
                :class="[
                  'py-2 rounded-lg text-xs font-bold border transition-all',
                  drawerForm.type === 'CASH_DROP'
                    ? 'bg-primary-500/20 text-primary-400 border-primary-500/50'
                    : 'bg-neutral-900 text-neutral-500 border-neutral-700 hover:border-neutral-500',
                ]"
              >
                DROP
              </button>
              <button
                @click="drawerForm.type = 'NO_SALE_OPEN'"
                :class="[
                  'py-2 rounded-lg text-xs font-bold border transition-all',
                  drawerForm.type === 'NO_SALE_OPEN'
                    ? 'bg-neutral-700 text-white border-neutral-600'
                    : 'bg-neutral-900 text-neutral-500 border-neutral-700 hover:border-neutral-500',
                ]"
              >
                NO SALE
              </button>
            </div>
          </div>
          <div v-if="drawerForm.type !== 'NO_SALE_OPEN'">
            <label
              class="text-[10px] text-neutral-500 font-black uppercase tracking-widest mb-2 block"
              >Amount</label
            >
            <input
              v-model.number="drawerForm.amount"
              type="number"
              step="0.01"
              class="w-full bg-neutral-900 border-neutral-700 rounded-lg text-white font-bold p-3 focus:ring-primary-500"
              placeholder="0.00"
            />
          </div>
          <div>
            <label
              class="text-[10px] text-neutral-500 font-black uppercase tracking-widest mb-2 block"
              >Reason</label
            >
            <textarea
              v-model="drawerForm.reason"
              rows="2"
              class="w-full bg-neutral-900 border-neutral-700 rounded-lg text-white text-sm p-3 focus:ring-primary-500"
              placeholder="Required for audit..."
            ></textarea>
          </div>
          <button
            @click="submitDrawerAction"
            :disabled="
              !drawerForm.reason ||
              (drawerForm.type !== 'NO_SALE_OPEN' && !drawerForm.amount)
            "
            class="w-full py-3 bg-primary-600 hover:bg-primary-700 text-white rounded-xl font-bold uppercase tracking-widest disabled:opacity-50 transition-colors"
          >
            Record Action
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, computed, watch, onMounted } from "vue";

const {
  cartItems,
  heldOrders,
  customer,
  pointsDiscount,
  manualDiscount,
  updateQty,
  subtotal,
  tax,
  total,
  clearCart,
  setCustomer,
  redeemPoints,
  holdOrder,
  resumeOrder,
  removeHeldOrder,
} = useCart();
const { post, get } = useApi();
const { user: authUser, logout: authLogout } = useAuth();
const toast = useToast();

const lastAddedItem = ref<{ id: number; name: string } | null>(null);

watch(
  () => cartItems.value.length,
  (newLen, oldLen) => {
    if (newLen > oldLen) {
      const last = cartItems.value[cartItems.value.length - 1];
      if (last) {
        lastAddedItem.value = { id: last.menuItemId, name: last.name };
      }
    } else if (newLen === 0) {
      lastAddedItem.value = null;
    }
  },
);

const logout = () => {
  authLogout();
  toast.info("Logged out successfully");
};

const showPaymentModal = ref(false);
const showReceiptModal = ref(false);
const showCustomerModal = ref(false);
const customerSearch = ref("");
const searchResults = ref<any[]>([]);
const recentCustomers = ref<any[]>([]);
const selecting = ref(false);

// --- Customer Creation State ---
const isCreatingCustomer = ref(false);
const newCustomer = reactive({ name: "", phone: "", gender: "MALE", dob: "" });

// --- Hold Order State ---
const showHoldOrderModal = ref(false);
const holdOrderNote = ref("");

const openCustomerModal = () => {
  showCustomerModal.value = true;
  customerSearch.value = "";
  searchResults.value = [];
  fetchRecentCustomers();
};

const fetchRecentCustomers = async () => {
  try {
    const data = await get<any[]>("/customers/recent");
    recentCustomers.value = data || [];
  } catch (e) {
    console.error("Failed to fetch recent customers", e);
  }
};

const searchCustomers = async () => {
  if (customerSearch.value.length < 2) return;
  selecting.value = true;
  try {
    const data = await get<any[]>("/customers/search", {
      query: customerSearch.value,
    });
    searchResults.value = data || [];
  } finally {
    selecting.value = false;
  }
};

const selectCustomerFromModal = (cust: any) => {
  setCustomer(cust);
  showCustomerModal.value = false;
};

const applyPoints = (points: number) => {
  const maxPoints = customer.value.points;
  const toRedeem = Math.min(points, maxPoints);
  redeemPoints(toRedeem);
  toast.success(`Redeemed ${toRedeem} points!`);
};

// --- Customer Creation Logic ---
const toggleCreateCustomer = () => {
  isCreatingCustomer.value = !isCreatingCustomer.value;
  newCustomer.name = "";
  newCustomer.phone = "";
  newCustomer.gender = "MALE";
  newCustomer.dob = "";
};

const createCustomer = async () => {
  if (!newCustomer.name) {
    toast.error("Name is required");
    return;
  }
  try {
    const payload = {
      fullName: newCustomer.name,
      phone: newCustomer.phone,
      gender: newCustomer.gender,
      dob: newCustomer.dob || null,
      loyaltyPoints: 0,
      membershipLevel: "BRONZE",
    };
    const data = await post<any>("/customers/add", payload);
    toast.success("Customer created!");
    selectCustomerFromModal(data);
    isCreatingCustomer.value = false;
    customerSearch.value = "";
  } catch (e) {
    console.error(e);
    toast.error("Failed to create customer");
  }
  // Refresh recent list
  fetchRecentCustomers();
};

// --- Hold Order Logic ---
const openHoldOrderModal = () => {
  holdOrderNote.value = `Order #${heldOrders.value.length + 1}`;
  showHoldOrderModal.value = true;
};

const confirmHoldOrder = () => {
  if (holdOrder(holdOrderNote.value)) {
    showHoldOrderModal.value = false;
  }
};

const orderType = ref("DINE_IN");
// discountAmount removed, using manualDiscount from useCart
const showDiscountModal = ref(false);
const manualDiscountInput = ref(0);

const toggleOrderType = () => {
  orderType.value = orderType.value === "DINE_IN" ? "TAKEAWAY" : "DINE_IN";
};

const openDiscountModal = () => {
  manualDiscountInput.value = manualDiscount.value;
  showDiscountModal.value = true;
};

const applyDiscount = () => {
  manualDiscount.value = Number(manualDiscountInput.value);
  showDiscountModal.value = false;
};

// --- Shift Management ---
const currentShift = ref<any>(null);
const showShiftModal = ref(false);
const currentShiftSummary = ref<any>(null);

const checkCurrentShift = async () => {
  if (!authUser.value?.userId) return;
  try {
    const data = await get<any>(
      `/shifts/current?userId=${authUser.value.userId}`,
      {},
    );
    currentShift.value = data;
    if (currentShift.value) {
      shiftAction.value = "CLOSE";
      // Fetch summary if closing
      const summary = await get<any>(
        `/shifts/summary?userId=${authUser.value.userId}`,
        {},
      );
      currentShiftSummary.value = summary;
    } else {
      shiftAction.value = "OPEN";
      currentShiftSummary.value = null;
    }
  } catch (e) {
    console.error("Failed to check shift", e);
  }
};

const openShiftModal = async () => {
  if (currentShift.value) {
    try {
      const summary = await get<any>(
        `/shifts/summary?userId=${authUser.value.userId}`,
        {},
      );
      currentShiftSummary.value = summary;
    } catch (e) {
      console.error(e);
    }
  }
  showShiftModal.value = true;
};

const onShiftSuccess = (shift: any) => {
  currentShift.value = shift;
  if (!shift) currentShiftSummary.value = null;
};

onMounted(() => {
  checkCurrentShift();
});

watch(
  () => authUser.value,
  (newVal) => {
    if (newVal?.userId) {
      checkCurrentShift();
    }
  },
);

// --- Persistence ---
onMounted(() => {
  const saved = localStorage.getItem("heldOrders");
  if (saved) {
    try {
      const parsed = JSON.parse(saved);
      heldOrders.value = parsed.map((o: any) => ({
        ...o,
        createdAt: new Date(o.createdAt),
      }));
    } catch (e) {
      console.error("Failed to load held orders", e);
    }
  }
});

watch(
  heldOrders,
  (newVal) => {
    localStorage.setItem("heldOrders", JSON.stringify(newVal));
  },
  { deep: true },
);
const selectedMethod = ref("CASH");
const khqrData = ref<any>(null);
const pollingInterval = ref<any>(null);
const isProcessing = ref(false);
const paymentStatus = ref("");
const lastOrderId = ref<number | null>(null);
const receiptData = ref<any>(null);

// --- Order History & Adjustment ---
const showOrderHistoryModal = ref(false);
const orderLoading = ref(false);
const recentOrders = ref<any[]>([]);
const showApprovalModal = ref(false);
const approvalActionType = ref<"VOID" | "REFUND">("VOID");
const adjustmentLoading = ref(false);
const adjustmentTarget = ref<any | null>(null);

// --- Drawer Action State ---
const showDrawerModal = ref(false);
const drawerForm = reactive({
  type: "NO_SALE_OPEN",
  amount: 0,
  reason: "",
});

const submitDrawerAction = async () => {
  try {
    await post("/drawer-actions", {
      userId: authUser.value?.userId,
      ...drawerForm,
    });
    toast.success("Drawer action recorded");
    showDrawerModal.value = false;
    // Reset form
    drawerForm.type = "NO_SALE_OPEN";
    drawerForm.amount = 0;
    drawerForm.reason = "";
  } catch (e) {
    toast.error("Failed to record action");
  }
};

const openOrderHistory = async () => {
  showOrderHistoryModal.value = true;
  orderLoading.value = true;
  try {
    const data = await get<any>("/orders", {
      page: 0,
      size: 20,
      sort: "createdAt,desc",
    });
    recentOrders.value = data.content || [];
  } catch (e) {
    toast.error("Failed to load order history");
  } finally {
    orderLoading.value = false;
  }
};

const initiateAdjustment = (order: any, type: "VOID" | "REFUND") => {
  adjustmentTarget.value = order;
  approvalActionType.value = type;
  showApprovalModal.value = true;
};

const handleApprovedAdjustment = async (data: {
  pin: string;
  reason: string;
}) => {
  if (!adjustmentTarget.value) return;
  adjustmentLoading.value = true;
  try {
    const orderId = adjustmentTarget.value.orderId;
    const endpoint = `/orders/${orderId}/${approvalActionType.value.toLowerCase()}`;
    await put(endpoint, null, {
      pinCode: data.pin,
      reason: data.reason,
    });
    toast.success(`Order ${approvalActionType.value} successful`);
    showApprovalModal.value = false;
    // Refresh list
    openOrderHistory();
  } catch (e: any) {
    toast.error(e.response?.data?.message || "Action failed");
  } finally {
    adjustmentLoading.value = false;
  }
};

// -- Cash Payment Logic --
const cashReceived = ref<string>("0");
const changeDue = computed(() => {
  const received = parseFloat(cashReceived.value);
  return received - total.value;
});

const addCashDigit = (d: string) => {
  if (cashReceived.value === "0" && d !== ".") {
    cashReceived.value = d;
  } else {
    if (d === "." && cashReceived.value.includes(".")) return;
    cashReceived.value += d;
  }
};

const clearCash = () => {
  if (cashReceived.value.length === 1) {
    cashReceived.value = "0";
  } else {
    cashReceived.value = cashReceived.value.slice(0, -1);
  }
};

const setCash = (amount: number) => {
  cashReceived.value = amount.toString();
};

const openPaymentModal = () => {
  if (cartItems.value.length === 0) return;
  showPaymentModal.value = true;
  selectedMethod.value = "CASH";
  khqrData.value = null;
  paymentStatus.value = "PENDING";
  // Reset cash control
  cashReceived.value = "0";
};

const closePaymentModal = () => {
  showPaymentModal.value = false;
  stopPolling();
};

const selectMethod = (method: string) => {
  selectedMethod.value = method;
  if (method === "KHQR") {
    generateKHQR();
  } else {
    stopPolling();
  }
};

const generateKHQR = async () => {
  isProcessing.value = true;
  try {
    const payload = {
      orderNo: "QR-" + Date.now(),
      branchId: authUser.value?.branchId || 1,
      cashierUserId: authUser.value?.userId || 1,
      customerId: customer.value.id,
      orderType: "DINE_IN",
      status: "PENDING",
      discountAmount: pointsDiscount.value,
      items: cartItems.value.map((item) => ({
        menuItemId: item.menuItemId,
        quantity: item.qty,
        note: item.notes || "",
      })),
    };
    const response = await post<any>("/payments/bakong/generate", payload);
    khqrData.value = response.data;
    startPolling(response.data.md5);
  } catch (err) {
    console.error(err);
    toast.error("Failed to generate KHQR QR code");
  } finally {
    isProcessing.value = false;
  }
};

const startPolling = (md5: string) => {
  stopPolling();
  pollingInterval.value = setInterval(async () => {
    try {
      const result = await post<any>("/payments/bakong/check", { md5 });
      if (result.success && result.data.status === "COMPLETED") {
        paymentStatus.value = "COMPLETED";
        toast.success("Payment Received via KHQR!");
        // Store the payment details from the successful check
        khqrData.value = { ...khqrData.value, ...result.data };
        stopPolling();
        // Auto complete order after short delay
        setTimeout(() => completeOrder(), 1000);
      }
    } catch (err) {
      console.error("Polling error", err);
    }
  }, 3000);
};

const stopPolling = () => {
  if (pollingInterval.value) {
    clearInterval(pollingInterval.value);
    pollingInterval.value = null;
  }
};

const completeOrder = async () => {
  isProcessing.value = true;

  try {
    // Determine actual paid amount
    let paidAmount = total.value;
    if (selectedMethod.value === "CASH") {
      paidAmount = parseFloat(cashReceived.value);
    } else if (selectedMethod.value === "KHQR") {
      paidAmount = khqrData.value?.payment?.amount || total.value;
    }

    const payload = {
      orderNo: "ORD-" + Date.now(),
      branchId: authUser.value?.branchId || 1,
      cashierUserId: authUser.value?.userId || 1,
      customerId: customer.value.id,
      orderType: orderType.value,
      status: "PAID",
      discountAmount: manualDiscount.value,
      items: cartItems.value.map((item) => ({
        menuItemId: item.menuItemId,
        quantity: item.qty,
        unitPrice: item.price + (item.addOnTotal || 0),
        note: item.notes || "",
        addOnIds: item.addOns?.map((a) => a.addonId) || [],
      })),
      pointsRedeemed: 0,
      paymentMethod: selectedMethod.value,
      receivedAmount: paidAmount, // Send actual cash received
      paymentReference:
        selectedMethod.value === "KHQR"
          ? khqrData.value?.payment?.hash ||
            khqrData.value?.md5 ||
            khqrData.value?.payment?.transactionId
          : null,
    };

    const response = await post<any>("/orders", payload);

    // Fetch receipt data
    if ((response && response.orderData?.orderId) || response?.orderId) {
      const orderId = response.orderData?.orderId || response.orderId;
      lastOrderId.value = orderId;
      try {
        const receiptResponse = await get<any>(`/receipts/${orderId}`);
        receiptData.value = receiptResponse?.data;
      } catch (e) {
        console.error("Failed to fetch receipt", e);
      }
    }

    clearCart();
    showPaymentModal.value = false;
    stopPolling();
    toast.success("Order completed successfully!");

    // Show receipt modal
    showReceiptModal.value = true;
    await nextTick();
    printReceipt();
  } catch (err: any) {
    console.error("Order Completion Error:", err);
    const errorMsg =
      err.response?._data?.message || err.message || "Failed to process order.";
    toast.error("Order Error: " + errorMsg);
  } finally {
    isProcessing.value = false;
  }
};

const closeReceiptModal = () => {
  showReceiptModal.value = false;
  receiptData.value = null;
  lastOrderId.value = null;
};

const printReceipt = () => {
  const receiptEl = document.getElementById("receipt-print-area");
  if (!receiptEl) return;

  const printWindow = window.open("", "_blank");
  if (printWindow) {
    printWindow.document.write(`
            <html>
                <head>
                    <title>Receipt - ${receiptData.value?.orderNo}</title>
                    <style>
                        body { font-family: 'Courier New', monospace; padding: 20px; max-width: 300px; margin: 0 auto; }
                        .receipt { background: white; padding: 20px; }
                        .receipt-header { text-align: center; margin-bottom: 15px; }
                        .logo { font-size: 40px; }
                        .store-name { font-size: 18px; font-weight: bold; margin: 10px 0 5px; }
                        .store-info { font-size: 12px; color: #666; margin: 2px 0; }
                        .divider { border-bottom: 1px dashed #ccc; margin: 10px 0; }
                        .info-row { display: flex; justify-content: space-between; font-size: 12px; margin: 3px 0; }
                        .bold { font-weight: bold; }
                        .items-header { display: flex; font-size: 11px; font-weight: bold; }
                        .item-row { margin: 8px 0; font-size: 12px; }
                        .total-row { display: flex; justify-content: space-between; font-size: 12px; }
                        .grand-total { font-size: 14px; font-weight: bold; }
                        .receipt-footer { text-align: center; margin-top: 15px; font-size: 12px; }
                        @media print { body { padding: 0; } }
                    </style>
                </head>
                <body>
                    ${receiptEl.innerHTML}
                </body>
            </html>
        `);
    printWindow.document.close();
    printWindow.print();
  }
};

// Cleanup
onUnmounted(() => {
  stopPolling();
});
</script>
