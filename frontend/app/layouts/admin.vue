<template>
  <div
    class="min-h-screen bg-[#F2F2F7] dark:bg-[#000000] font-main selection:bg-primary-500/30"
  >
    <!-- Sidebar -->
    <aside
      :class="[
        'fixed inset-y-0 left-0 z-50 flex flex-col transition-all duration-500 ease-[cubic-bezier(0.23,1,0.32,1)]',
        'glass-sidebar',
        sidebarOpen ? 'w-[260px]' : 'w-[80px]',
      ]"
    >
      <!-- Logo Section -->
      <div class="flex items-center h-[72px] px-6">
        <div class="flex items-center gap-3">
          <div
            class="w-9 h-9 rounded-xl overflow-hidden shadow-macos bg-white flex items-center justify-center"
          >
            <img
              src="~/assets/images/cofeoshop.jpg"
              alt="Logo"
              class="w-full h-full object-cover"
            />
          </div>
          <span
            v-if="sidebarOpen"
            class="font-bold text-[17px] text-neutral-900 dark:text-white tracking-tight animate-in fade-in duration-500"
          >
            Cofeoshop
          </span>
        </div>
      </div>

      <!-- Navigation -->
      <nav
        class="flex-1 px-3 py-4 space-y-1.5 overflow-y-auto custom-scrollbar"
      >
        <div
          v-for="category in navigation"
          :key="category.name"
          class="space-y-1"
        >
          <!-- Category Item -->
          <button
            @click="toggleCategory(category.name)"
            :class="[
              'w-full flex items-center justify-between px-3 py-2.5 rounded-xl transition-all duration-300 group relative',
              isCategoryActive(category)
                ? 'bg-primary-500/10 text-primary-600 dark:text-primary-400 font-bold'
                : 'text-neutral-500 hover:bg-black/5 dark:hover:bg-white/5',
            ]"
          >
            <div class="flex items-center gap-3">
              <div
                :class="[
                  'p-1.5 rounded-lg transition-colors',
                  isCategoryActive(category)
                    ? 'text-primary-600'
                    : 'text-neutral-400 group-hover:text-neutral-900 dark:group-hover:text-white',
                ]"
              >
                <component :is="category.icon" class="w-[20px] h-[20px]" />
              </div>
              <span
                v-if="sidebarOpen"
                class="text-[14px] font-medium tracking-tight whitespace-nowrap animate-in fade-in slide-in-from-left-2 duration-300"
              >
                {{ category.name }}
              </span>
            </div>

            <svg
              v-if="sidebarOpen"
              xmlns="http://www.w3.org/2000/svg"
              class="w-3.5 h-3.5 transition-transform duration-300"
              :class="{
                'rotate-180': expandedCategories.includes(category.name),
              }"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              stroke-width="2.5"
            >
              <path d="m6 9 6 6 6-6" />
            </svg>

            <!-- Active Indicator Pill -->
            <div
              v-if="isActive(category.href) && !sidebarOpen"
              class="absolute left-0 w-1 h-5 bg-primary-500 rounded-r-full"
            ></div>
          </button>

          <!-- Sub-menu Items -->
          <div
            v-if="sidebarOpen && expandedCategories.includes(category.name)"
            class="pl-11 pr-2 space-y-1 animate-in slide-in-from-top-2 duration-300"
          >
            <NuxtLink
              v-for="child in category.children"
              :key="child.name"
              :to="child.href"
              :class="[
                'block px-3 py-2 rounded-lg text-[13px] font-medium transition-all duration-200',
                isActive(child.href)
                  ? 'text-primary-600 dark:text-primary-400 bg-primary-500/5'
                  : 'text-neutral-500 hover:text-neutral-900 dark:hover:text-white hover:bg-black/5 dark:hover:bg-white/5',
              ]"
            >
              {{ child.name }}
            </NuxtLink>
          </div>
        </div>
      </nav>

      <!-- Sidebar Toggle -->
      <div class="p-4 border-t border-black/5 dark:border-white/5">
        <button
          @click="sidebarOpen = !sidebarOpen"
          class="w-full h-10 flex items-center justify-center rounded-xl text-neutral-400 hover:bg-black/5 dark:hover:bg-white/5 transition-all active:scale-95"
        >
          <svg
            xmlns="http://www.w3.org/2000/svg"
            class="w-5 h-5 transition-transform duration-500 ease-out-back"
            :class="{ 'rotate-180': sidebarOpen }"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            stroke-width="2"
          >
            <path d="m9 18 6-6-6-6" />
          </svg>
        </button>
      </div>
    </aside>

    <!-- Main Content Wrapper -->
    <main
      :class="[
        'min-h-screen transition-all duration-500 ease-[cubic-bezier(0.23,1,0.32,1)]',
        sidebarOpen ? 'ml-[260px]' : 'ml-[80px]',
      ]"
    >
      <!-- Top Navigation Bar -->
      <header
        class="sticky top-0 z-40 h-[72px] px-8 flex items-center justify-between glass-topbar transition-all duration-300"
      >
        <div class="flex items-center gap-4">
          <h1
            class="text-[19px] font-bold text-neutral-900 dark:text-white tracking-tight"
          >
            {{ pageTitle }}
          </h1>
        </div>

        <div class="flex items-center gap-4">
          <div
            class="flex items-center bg-black/5 dark:bg-white/5 rounded-2xl p-1 gap-1"
          >
            <LanguageSwitcher />
          </div>

          <!-- Utility Icons Group -->
          <div
            class="flex items-center gap-1.5 px-2 border-x border-black/5 dark:border-white/5"
          >
            <NuxtLink
              to="/admin/inventory"
              class="w-9 h-9 flex items-center justify-center rounded-xl text-neutral-500 hover:bg-black/5 dark:hover:bg-white/5 transition-all relative group"
              title="Inventory Alerts"
            >
              <svg
                xmlns="http://www.w3.org/2000/svg"
                class="w-5 h-5 group-hover:scale-110 transition-transform"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                stroke-width="2"
              >
                <path d="M6 8a6 6 0 0 1 12 0c0 7 3 9 3 9H3s3-2 3-9" />
                <path d="M10.3 21a1.94 1.94 0 0 0 3.4 0" />
              </svg>
              <span
                v-if="lowStockCount > 0"
                class="absolute -top-1 -right-1 w-4 h-4 bg-primary-500 text-white text-[9px] font-black rounded-full flex items-center justify-center border-2 border-white dark:border-black"
              >
                {{ lowStockCount }}
              </span>
            </NuxtLink>

            <button
              @click="toggleDarkMode"
              class="w-9 h-9 flex items-center justify-center rounded-xl text-neutral-500 hover:bg-black/5 dark:hover:bg-white/5 transition-all group"
            >
              <svg
                v-if="isDark"
                xmlns="http://www.w3.org/2000/svg"
                class="w-5 h-5 group-hover:rotate-45 transition-transform"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                stroke-width="2"
              >
                <circle cx="12" cy="12" r="4" />
                <path
                  d="M12 2v2M12 20v2M4.93 4.93l1.41 1.41M17.66 17.66l1.41 1.41M2 12h2M20 12h2M6.34 17.66l-1.41 1.41M19.07 4.93l-1.41 1.41"
                />
              </svg>
              <svg
                v-else
                xmlns="http://www.w3.org/2000/svg"
                class="w-5 h-5 group-hover:-rotate-12 transition-transform"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                stroke-width="2"
              >
                <path d="M12 3a6 6 0 0 0 9 9 9 9 0 1 1-9-9Z" />
              </svg>
            </button>
          </div>

          <!-- Profile / Account -->
          <div class="flex items-center gap-3 pl-2">
            <div class="hidden sm:flex flex-col items-end">
              <span
                class="text-[13px] font-bold text-neutral-900 dark:text-white leading-none mb-0.5"
              >
                {{ authUser?.employeeName || authUser?.username }}
              </span>
              <span
                class="text-[10px] font-bold text-neutral-400 uppercase tracking-widest leading-none"
              >
                {{ authUser?.roleName }}
              </span>
            </div>

            <button
              @click="logout"
              class="w-10 h-10 rounded-2xl bg-primary-500 text-white shadow-macos-lg flex items-center justify-center hover:bg-error-500 transition-all duration-300 active:scale-90 group relative"
            >
              <span
                class="text-[14px] font-bold tracking-tighter group-hover:hidden"
              >
                {{
                  (authUser?.username?.substring(0, 2) || "AD").toUpperCase()
                }}
              </span>
              <svg
                xmlns="http://www.w3.org/2000/svg"
                class="w-5 h-5 hidden group-hover:block"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                stroke-width="2.5"
              >
                <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4" />
                <polyline points="16 17 21 12 16 7" />
                <line x1="21" x2="9" y1="12" y2="12" />
              </svg>
            </button>
          </div>
        </div>
      </header>

      <!-- Main Dynamic Content Area -->
      <div
        class="p-8 max-w-[1600px] mx-auto animate-in slide-in-from-bottom-4 fade-in duration-700"
      >
        <slot />
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, h } from "vue";

const route = useRoute();

const sidebarOpen = ref(true);
const isDark = ref(false);
const lowStockCount = ref(0);
const { get } = useApi();
const { user: authUser, logout: authLogout } = useAuth();
const toast = useToast();

const logout = async () => {
  await authLogout();
  toast.success("Logged out successfully");
};

const fetchLowStockCount = async () => {
  try {
    const data = await get<any[]>("/ingredients/low-stock");
    lowStockCount.value = data?.length || 0;
  } catch (error) {
    console.error("Failed to fetch low stock count", error);
  }
};

const pageTitle = computed(() => {
  const titles: Record<string, string> = {
    "/admin": "Dashboard",
    "/admin/menu": "Product Menu",
    "/admin/categories": "Categories",
    "/admin/addons": "Menu Add-ons",
    "/admin/orders": "Order History",
    "/admin/customers": "Customer Database",
    "/admin/reports": "Business Reports",
    "/admin/inventory": "Inventory Stock",
    "/admin/inventory/recipes": "Recipe Management",
    "/admin/inventory/suppliers": "Suppliers",
    "/admin/expenses": "Expense Tracking",
    "/admin/branches": "Branch Management",
    "/admin/settings": "System Settings",
    "/admin/staff": "Employee List",
    "/admin/staff/performance": "Staff Performance",
    "/admin/staff/roles": "Security Roles",
    "/admin/qr-codes": "QR Table Codes",
  };
  return titles[route.path] || "Dashboard";
});

// Icon components
const DashboardIcon = () =>
  h(
    "svg",
    {
      xmlns: "http://www.w3.org/2000/svg",
      viewBox: "0 0 24 24",
      fill: "none",
      stroke: "currentColor",
      "stroke-width": "2",
      "stroke-linecap": "round",
      "stroke-linejoin": "round",
    },
    [
      h("rect", { width: "7", height: "9", x: "3", y: "3", rx: "1" }),
      h("rect", { width: "7", height: "5", x: "14", y: "3", rx: "1" }),
      h("rect", { width: "7", height: "9", x: "14", y: "12", rx: "1" }),
      h("rect", { width: "7", height: "5", x: "3", y: "16", rx: "1" }),
    ],
  );

const MenuIcon = () =>
  h(
    "svg",
    {
      xmlns: "http://www.w3.org/2000/svg",
      viewBox: "0 0 24 24",
      fill: "none",
      stroke: "currentColor",
      "stroke-width": "2",
      "stroke-linecap": "round",
      "stroke-linejoin": "round",
    },
    [
      h("path", { d: "M17 8h1a4 4 0 1 1 0 8h-1" }),
      h("path", { d: "M3 8h14v9a4 4 0 0 1-4 4H7a4 4 0 0 1-4-4Z" }),
    ],
  );

const OrdersIcon = () =>
  h(
    "svg",
    {
      xmlns: "http://www.w3.org/2000/svg",
      viewBox: "0 0 24 24",
      fill: "none",
      stroke: "currentColor",
      "stroke-width": "2",
      "stroke-linecap": "round",
      "stroke-linejoin": "round",
    },
    [
      h("path", { d: "M16 3h5v5" }),
      h("path", { d: "M8 3H3v5" }),
      h("path", { d: "M12 22v-8.3a4 4 0 0 0-1.172-2.872L3 3" }),
      h("path", { d: "m15 9 6-6" }),
    ],
  );

const CustomersIcon = () =>
  h(
    "svg",
    {
      xmlns: "http://www.w3.org/2000/svg",
      viewBox: "0 0 24 24",
      fill: "none",
      stroke: "currentColor",
      "stroke-width": "2",
      "stroke-linecap": "round",
      "stroke-linejoin": "round",
    },
    [
      h("path", { d: "M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2" }),
      h("circle", { cx: "9", cy: "7", r: "4" }),
      h("path", { d: "M22 21v-2a4 4 0 0 0-3-3.87" }),
      h("path", { d: "M16 3.13a4 4 0 0 1 0 7.75" }),
    ],
  );

const ReportsIcon = () =>
  h(
    "svg",
    {
      xmlns: "http://www.w3.org/2000/svg",
      viewBox: "0 0 24 24",
      fill: "none",
      stroke: "currentColor",
      "stroke-width": "2",
      "stroke-linecap": "round",
      "stroke-linejoin": "round",
    },
    [h("path", { d: "M3 3v18h18" }), h("path", { d: "m19 9-5 5-4-4-3 3" })],
  );

const SettingsIcon = () =>
  h(
    "svg",
    {
      xmlns: "http://www.w3.org/2000/svg",
      viewBox: "0 0 24 24",
      fill: "none",
      stroke: "currentColor",
      "stroke-width": "2",
      "stroke-linecap": "round",
      "stroke-linejoin": "round",
    },
    [
      h("path", {
        d: "M12.22 2h-.44a2 2 0 0 0-2 2v.18a2 2 0 0 1-1 1.73l-.43.25a2 2 0 0 1-2 0l-.15-.08a2 2 0 0 0-2.73.73l-.22.38a2 2 0 0 0 .73 2.73l.15.1a2 2 0 0 1 1 1.72v.51a2 2 0 0 1-1 1.74l-.15.09a2 2 0 0 0-.73 2.73l.22.38a2 2 0 0 0 2.73.73l.15-.08a2 2 0 0 1 2 0l.43.25a2 2 0 0 1 1 1.73V20a2 2 0 0 0 2 2h.44a2 2 0 0 0 2-2v-.18a2 2 0 0 1 1-1.73l.43-.25a2 2 0 0 1 2 0l.15.08a2 2 0 0 0 2.73-.73l.22-.39a2 2 0 0 0-.73-2.73l-.15-.08a2 2 0 0 1-1-1.74v-.5a2 2 0 0 1 1-1.74l.15-.09a2 2 0 0 0 .73-2.73l-.22-.38a2 2 0 0 0-2.73-.73l-.15.08a2 2 0 0 1-2 0l-.43-.25a2 2 0 0 1-1-1.73V4a2 2 0 0 0-2-2z",
      }),
      h("circle", { cx: "12", cy: "12", r: "3" }),
    ],
  );

const AddOnsIcon = () =>
  h(
    "svg",
    {
      xmlns: "http://www.w3.org/2000/svg",
      viewBox: "0 0 24 24",
      fill: "none",
      stroke: "currentColor",
      "stroke-width": "2",
      "stroke-linecap": "round",
      "stroke-linejoin": "round",
    },
    [
      h("path", { d: "M12 2v20" }),
      h("path", { d: "M2 12h20" }),
      h("circle", { cx: "12", cy: "12", r: "4" }),
    ],
  );

const InventoryIcon = () =>
  h(
    "svg",
    {
      xmlns: "http://www.w3.org/2000/svg",
      viewBox: "0 0 24 24",
      fill: "none",
      stroke: "currentColor",
      "stroke-width": "2",
      "stroke-linecap": "round",
      "stroke-linejoin": "round",
    },
    [
      h("path", {
        d: "M21 8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16Z",
      }),
      h("path", { d: "m3.3 7 8.7 5 8.7-5" }),
      h("path", { d: "M12 22V12" }),
    ],
  );

const KitchenIcon = () =>
  h(
    "svg",
    {
      xmlns: "http://www.w3.org/2000/svg",
      viewBox: "0 0 24 24",
      fill: "none",
      stroke: "currentColor",
      "stroke-width": "2",
      "stroke-linecap": "round",
      "stroke-linejoin": "round",
    },
    [
      h("path", { d: "M3 2v7c0 1.1.9 2 2 2h4a2 2 0 0 0 2-2V2" }),
      h("path", { d: "M7 2v20" }),
      h("path", { d: "M21 15V2v0a5 5 0 0 0-5 5v6c0 1.1.9 2 2 2h3zm0 0v7" }),
    ],
  );

const StaffIcon = () =>
  h(
    "svg",
    {
      xmlns: "http://www.w3.org/2000/svg",
      viewBox: "0 0 24 24",
      fill: "none",
      stroke: "currentColor",
      "stroke-width": "2",
      "stroke-linecap": "round",
      "stroke-linejoin": "round",
    },
    [
      h("path", { d: "M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2" }),
      h("circle", { cx: "9", cy: "7", r: "4" }),
      h("path", { d: "M22 21v-2a4 4 0 0 0-3-3.87" }),
      h("path", { d: "M16 3.13a4 4 0 0 1 0 7.75" }),
    ],
  );

const BranchIcon = () =>
  h(
    "svg",
    {
      xmlns: "http://www.w3.org/2000/svg",
      viewBox: "0 0 24 24",
      fill: "none",
      stroke: "currentColor",
      "stroke-width": "2",
      "stroke-linecap": "round",
      "stroke-linejoin": "round",
    },
    [
      h("rect", {
        width: "16",
        height: "20",
        x: "4",
        y: "2",
        rx: "2",
        ry: "2",
      }),
      h("path", { d: "M9 22v-4h6v4" }),
      h("path", { d: "M8 6h.01" }),
      h("path", { d: "M16 6h.01" }),
      h("path", { d: "M12 6h.01" }),
      h("path", { d: "M12 10h.01" }),
      h("path", { d: "M12 14h.01" }),
      h("path", { d: "M16 10h.01" }),
      h("path", { d: "M16 14h.01" }),
      h("path", { d: "M8 10h.01" }),
      h("path", { d: "M8 14h.01" }),
    ],
  );

const DigitalIcon = () =>
  h(
    "svg",
    {
      xmlns: "http://www.w3.org/2000/svg",
      viewBox: "0 0 24 24",
      fill: "none",
      stroke: "currentColor",
      "stroke-width": "2",
      "stroke-linecap": "round",
      "stroke-linejoin": "round",
    },
    [
      h("rect", { width: "18", height: "18", x: "3", y: "3", rx: "2" }),
      h("path", { d: "M7 7h.01" }),
      h("path", { d: "M17 7h.01" }),
      h("path", { d: "M7 17h.01" }),
      h("path", { d: "M17 17h.01" }),
    ],
  );

const {
  canAccessPOS,
  canAccessInventory,
  canAccessEmployees,
  canAccessMenu,
  canAccessReports,
  isSuperAdmin,
} = usePermissions();

const expandedCategories = ref<string[]>([]);

const toggleCategory = (category: string) => {
  if (!sidebarOpen.value) {
    sidebarOpen.value = true;
    if (!expandedCategories.value.includes(category)) {
      expandedCategories.value.push(category);
    }
    return;
  }
  const index = expandedCategories.value.indexOf(category);
  if (index === -1) {
    expandedCategories.value.push(category);
  } else {
    expandedCategories.value.splice(index, 1);
  }
};

const rawNavigation = [
  {
    name: "Operations",
    icon: DashboardIcon,
    show: true,
    children: [
      { name: "Dashboard", href: "/admin", show: true },
      { name: "Orders", href: "/admin/orders", show: canAccessPOS.value },
      { name: "Customers", href: "/admin/customers", show: canAccessPOS.value },
      { name: "Kitchen", href: "/kitchen", show: true },
    ],
  },
  {
    name: "Digital Menu",
    icon: DigitalIcon,
    show: isSuperAdmin.value || canAccessMenu.value,
    children: [{ name: "QR Table Codes", href: "/admin/qr-codes", show: true }],
  },
  {
    name: "Catalog",
    icon: MenuIcon,
    show: canAccessMenu.value,
    children: [
      { name: "Products", href: "/admin/menu", show: canAccessMenu.value },
      {
        name: "Categories",
        href: "/admin/categories",
        show: canAccessMenu.value,
      },
      { name: "Add-ons", href: "/admin/addons", show: canAccessMenu.value },
    ],
  },
  {
    name: "Inventory",
    icon: InventoryIcon,
    show: canAccessInventory.value,
    children: [
      {
        name: "Stock",
        href: "/admin/inventory",
        show: canAccessInventory.value,
      },
      {
        name: "Recipes",
        href: "/admin/inventory/recipes",
        show: canAccessInventory.value,
      },
      {
        name: "Suppliers",
        href: "/admin/inventory/suppliers",
        show: canAccessInventory.value,
      },
      {
        name: "Expenses",
        href: "/admin/expenses",
        show: canAccessReports.value,
      },
    ],
  },
  {
    name: "Management",
    icon: StaffIcon,
    show: canAccessEmployees.value || isSuperAdmin.value,
    children: [
      {
        name: "Staff List",
        href: "/admin/staff",
        show: canAccessEmployees.value,
      },
      {
        name: "Performance",
        href: "/admin/staff/performance",
        show: canAccessEmployees.value,
      },
      { name: "Reports", href: "/admin/reports", show: canAccessReports.value },
      { name: "Branches", href: "/admin/branches", show: isSuperAdmin.value },
      { name: "Settings", href: "/admin/settings", show: isSuperAdmin.value },
    ],
  },
];

const navigation = computed(() => {
  return rawNavigation
    .filter((category) => category.show)
    .map((category) => ({
      ...category,
      children: category.children?.filter((child) => child.show) || [],
    }))
    .filter((category) => category.children.length > 0 || !category.children);
});

// Auto-expand categories that contain the active route
watch(
  () => route.fullPath,
  (fullPath) => {
    navigation.value.forEach((category) => {
      if (
        category.children?.some((child) => fullPath.startsWith(child.href)) &&
        !expandedCategories.value.includes(category.name)
      ) {
        expandedCategories.value.push(category.name);
      }
    });
  },
  { immediate: true },
);

const isActive = (href: string) => {
  if (!href) return false;
  if (href === "/admin") {
    return route.path === "/admin";
  }
  // If href contains query, use fullPath for comparison
  if (href.includes("?")) {
    return route.fullPath === href;
  }
  // Otherwise default to path comparison
  return route.path === href || route.path.startsWith(href + "/");
};

const isCategoryActive = (category: any) => {
  return category.children?.some((child: any) => isActive(child.href));
};

const toggleDarkMode = () => {
  isDark.value = !isDark.value;
  document.documentElement.classList.toggle("dark", isDark.value);
};

// Initialize dark mode from system preference
onMounted(() => {
  isDark.value = window.matchMedia("(prefers-color-scheme: dark)").matches;
  document.documentElement.classList.toggle("dark", isDark.value);
  fetchLowStockCount();
});
</script>
