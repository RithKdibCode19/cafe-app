<template>
  <NuxtLayout name="admin">
    <div class="space-y-8">
      <UiBreadcrumb :items="[{ label: 'Reports' }]" />

      <!-- Header -->
      <div>
        <h1
          class="text-3xl font-black text-neutral-900 dark:text-white tracking-tight"
        >
          Analytics & Reports
        </h1>
        <p class="text-neutral-500 dark:text-neutral-400 mt-1">
          Select a report to view detailed business performance insights.
        </p>
      </div>

      <!-- Report Grid -->
      <div class="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
        <NuxtLink
          v-for="report in reports"
          :key="report.title"
          :to="report.href"
          class="group bg-white dark:bg-neutral-900 rounded-3xl p-8 border border-neutral-200 dark:border-neutral-800 hover:border-primary-500 dark:hover:border-primary-500 transition-all duration-300 shadow-sm hover:shadow-2xl hover:shadow-primary-500/10"
        >
          <div
            :class="[
              'w-14 h-14 rounded-2xl flex items-center justify-center mb-6 transition-transform group-hover:scale-110 duration-300',
              report.bg,
            ]"
          >
            <component :is="report.icon" :class="['w-7 h-7', report.color]" />
          </div>

          <h3 class="text-xl font-bold text-neutral-900 dark:text-white mb-2">
            {{ report.title }}
          </h3>
          <p
            class="text-sm text-neutral-500 dark:text-neutral-400 leading-relaxed mb-6"
          >
            {{ report.description }}
          </p>

          <div
            class="flex items-center text-sm font-bold text-primary-600 group-hover:translate-x-1 transition-transform"
          >
            View Report
            <svg
              xmlns="http://www.w3.org/2000/svg"
              class="w-4 h-4 ml-2"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              stroke-width="2.5"
            >
              <path d="M5 12h14" />
              <path d="m12 5 7 7-7 7" />
            </svg>
          </div>
        </NuxtLink>
      </div>

      <!-- Quick Metrics Background -->
      <div
        class="card p-8 bg-gradient-to-br from-primary-600 to-accent-600 text-white overflow-hidden relative"
      >
        <div class="relative z-10">
          <h2 class="text-2xl font-black mb-2">Need a Custom Report?</h2>
          <p class="text-white/80 max-w-sm mb-6 leading-relaxed">
            Our advanced analytics engine can generate specialized reports for
            inventory auditing, employee performance, and more.
          </p>
          <button
            class="bg-white text-primary-600 px-6 py-3 rounded-xl font-bold text-sm hover:bg-neutral-100 transition-colors shadow-lg"
          >
            Request Extension
          </button>
        </div>
        <!-- Decorative SVG -->
        <svg
          class="absolute right-0 bottom-0 w-64 h-64 text-white/10 -mr-16 -mb-16"
          viewBox="0 0 24 24"
          fill="currentColor"
        >
          <path
            d="M21 16.5c0 .38-.21.71-.53.88l-7.9 4.44a.99.99 0 0 1-.94 0l-7.9-4.44A.993.993 0 0 1 3 16.5v-9c0-.38.21-.71.53-.88l7.9-4.44c.16-.09.34-.14.52-.14s.36.05.52.14l7.9 4.44c.32.17.53.5.53.88v9z"
          />
        </svg>
      </div>
    </div>
  </NuxtLayout>
</template>

<script setup lang="ts">
import { h } from "vue";

definePageMeta({
  layout: false,
});

const SalesIcon = () =>
  h(
    "svg",
    {
      xmlns: "http://www.w3.org/2000/svg",
      viewBox: "0 0 24 24",
      fill: "none",
      stroke: "currentColor",
      "stroke-width": "2",
    },
    [
      h("path", { d: "M12 2v20" }),
      h("path", { d: "m17 5-5-3-5 3" }),
      h("path", { d: "m17 19-5 3-5-3" }),
      h("path", { d: "M2 12h20" }),
      h("path", { d: "m5 7-3 5 3 5" }),
      h("path", { d: "m19 7 3 5-3 5" }),
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
    },
    [
      h("path", {
        d: "M21 8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16Z",
      }),
      h("path", { d: "m3.3 7 8.7 5 8.7-5" }),
      h("path", { d: "M12 22V12" }),
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
    },
    [
      h("path", { d: "M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2" }),
      h("circle", { cx: "9", cy: "7", r: "4" }),
      h("path", { d: "M22 21v-2a4 4 0 0 0-3-3.87" }),
      h("path", { d: "M16 3.13a4 4 0 0 1 0 7.75" }),
    ],
  );
const TransferIcon = () =>
  h(
    "svg",
    {
      xmlns: "http://www.w3.org/2000/svg",
      viewBox: "0 0 24 24",
      fill: "none",
      stroke: "currentColor",
      "stroke-width": "2.5",
    },
    [
      h("path", { d: "m16 3 4 4-4 4" }),
      h("path", { d: "M20 7H4" }),
      h("path", { d: "m8 21-4-4 4-4" }),
      h("path", { d: "M4 17h16" }),
    ],
  );

const reports = [
  {
    title: "Sales Performance",
    description:
      "Track revenue, order volume, and top-selling items over any date range.",
    href: "/admin/reports/sales",
    icon: SalesIcon,
    bg: "bg-primary-100 dark:bg-primary-900/30",
    color: "text-primary-600 dark:text-primary-400",
  },
  {
    title: "Inventory Audit",
    description:
      "Analyze stock movements, wastage, and identify high-cost ingredients.",
    href: "/admin/reports/inventory",
    icon: InventoryIcon,
    bg: "bg-accent-100 dark:bg-accent-900/30",
    color: "text-accent-600 dark:text-accent-400",
  },
  {
    title: "Staff Productivity",
    description: "Compare sales by cashier and monitor attendance patterns.",
    href: "/admin/reports/staff",
    icon: StaffIcon,
    bg: "bg-success-100 dark:bg-success-900/30",
    color: "text-success-600 dark:text-success-400",
  },
  {
    title: "Stock Transfer History",
    description:
      "Monitor stock movements between branches to ensure accountability.",
    href: "/admin/reports/transfers",
    icon: TransferIcon,
    bg: "bg-warning-100 dark:bg-warning-900/30",
    color: "text-warning-600 dark:text-warning-400",
  },
];
</script>
