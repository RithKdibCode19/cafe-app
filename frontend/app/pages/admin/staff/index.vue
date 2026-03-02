<template>
  <NuxtLayout name="admin">
    <div class="space-y-6">
      <UiBreadcrumb :items="[{ label: 'Staff' }]" />

      <!-- Header -->
      <div
        class="flex flex-col md:flex-row md:items-center md:justify-between gap-4"
      >
        <div>
          <h1 class="text-2xl font-bold text-neutral-900 dark:text-white">
            Staff & Operations
          </h1>
          <p class="text-neutral-500 dark:text-neutral-400">
            Manage your team, schedules, and payroll
          </p>
        </div>

        <div class="flex items-center gap-3">
          <button
            v-if="activeTab === 'employees'"
            @click="openEmployeeModal"
            class="bg-primary-600 hover:bg-primary-700 text-white px-4 py-2 rounded-xl text-sm font-medium transition-all shadow-lg shadow-primary-500/20 flex items-center gap-2"
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
            Add Employee
          </button>
          <button
            v-if="activeTab === 'shifts'"
            @click="openShiftModal"
            class="bg-primary-600 hover:bg-primary-700 text-white px-4 py-2 rounded-xl text-sm font-medium transition-all shadow-lg shadow-primary-500/20 flex items-center gap-2"
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
            New Shift
          </button>
        </div>
      </div>

      <!-- Tabs Navigation -->
      <div
        class="flex items-center gap-1 p-1 bg-neutral-100 dark:bg-neutral-800/50 rounded-2xl w-fit"
      >
        <button
          v-for="tab in tabs"
          :key="tab.id"
          @click="activeTab = tab.id"
          :class="[
            'px-6 py-2 rounded-xl text-sm font-bold transition-all',
            activeTab === tab.id
              ? 'bg-white dark:bg-neutral-800 text-primary-600 dark:text-primary-400 shadow-sm'
              : 'text-neutral-500 hover:text-neutral-700 dark:hover:text-neutral-300',
          ]"
        >
          {{ tab.name }}
        </button>
      </div>

      <!-- -- TAB CONTENT: OVERVIEW -- -->
      <div v-if="activeTab === 'overview'" class="space-y-6">
        <!-- Stats Grid -->
        <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
          <div
            class="card p-6 bg-gradient-to-br from-primary-500/10 to-transparent border-primary-100 dark:border-primary-900/20"
          >
            <p
              class="text-xs font-black text-primary-600 uppercase tracking-widest mb-1"
            >
              Total Team
            </p>
            <h3 class="text-3xl font-black text-neutral-900 dark:text-white">
              {{ employees.length }}
            </h3>
            <p class="text-[10px] text-neutral-500 mt-1">
              Active staff members
            </p>
          </div>
          <div
            class="card p-6 bg-gradient-to-br from-success-500/10 to-transparent border-success-100 dark:border-success-900/20"
          >
            <p
              class="text-xs font-black text-success-600 uppercase tracking-widest mb-1"
            >
              On Duty Now
            </p>
            <h3 class="text-3xl font-black text-neutral-900 dark:text-white">
              {{ activeAttendance.length }}
            </h3>
            <p class="text-[10px] text-neutral-500 mt-1">
              Currently clocked in
            </p>
          </div>
          <div
            class="card p-6 bg-gradient-to-br from-warning-500/10 to-transparent border-warning-100 dark:border-warning-900/20"
          >
            <p
              class="text-xs font-black text-warning-600 uppercase tracking-widest mb-1"
            >
              Late Today
            </p>
            <h3 class="text-3xl font-black text-neutral-900 dark:text-white">
              {{ lateTodayCount }}
            </h3>
            <p class="text-[10px] text-neutral-500 mt-1">
              Arrivals after shift start
            </p>
          </div>
        </div>

        <!-- Currently Working List -->
        <div class="card overflow-hidden">
          <div
            class="p-4 border-b border-neutral-200 dark:border-neutral-700 bg-neutral-50/50 dark:bg-neutral-800/50"
          >
            <h3 class="text-sm font-bold text-neutral-900 dark:text-white">
              Currently On Duty
            </h3>
          </div>
          <div class="overflow-x-auto">
            <table class="w-full text-sm">
              <thead>
                <tr
                  class="text-left text-neutral-500 border-b border-neutral-200 dark:border-neutral-700"
                >
                  <th class="px-6 py-3 font-medium">Employee</th>
                  <th class="px-6 py-3 font-medium">Clocked In At</th>
                  <th class="px-6 py-3 font-medium">Duration</th>
                  <th class="px-6 py-3 font-medium">Status</th>
                </tr>
              </thead>
              <tbody
                class="divide-y divide-neutral-200 dark:divide-neutral-700"
              >
                <tr
                  v-for="att in activeAttendance"
                  :key="att.attendanceId"
                  class="hover:bg-neutral-50 dark:hover:bg-neutral-800/30"
                >
                  <td
                    class="px-6 py-4 font-bold text-neutral-900 dark:text-white"
                  >
                    {{ att.employee?.fullName }}
                  </td>
                  <td class="px-6 py-4 text-neutral-500">
                    {{ formatTime(att.checkIn) }}
                  </td>
                  <td
                    class="px-6 py-4 font-mono text-neutral-600 dark:text-neutral-400"
                  >
                    {{ calculateDuration(att.checkIn) }}
                  </td>
                  <td class="px-6 py-4">
                    <span
                      :class="
                        att.status === 'LATE'
                          ? 'bg-warning-100 text-warning-700'
                          : 'bg-success-100 text-success-700'
                      "
                      class="text-[10px] px-2 py-0.5 rounded-full font-bold uppercase"
                    >
                      {{ att.status }}
                    </span>
                  </td>
                </tr>
                <tr v-if="activeAttendance.length === 0">
                  <td
                    colspan="4"
                    class="px-6 py-12 text-center text-neutral-400 bg-neutral-50/30 dark:bg-neutral-900/30"
                  >
                    <svg
                      xmlns="http://www.w3.org/2000/svg"
                      class="w-12 h-12 mx-auto mb-2 opacity-10"
                      viewBox="0 0 24 24"
                      fill="none"
                      stroke="currentColor"
                      stroke-width="2"
                    >
                      <path d="M12 8v4l3 3" />
                      <circle cx="12" cy="12" r="10" />
                    </svg>
                    No staff currently clocked in.
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>

      <!-- -- TAB CONTENT: EMPLOYEES -- -->
      <div v-if="activeTab === 'employees'" class="space-y-4">
        <div class="card overflow-hidden">
          <div class="overflow-x-auto">
            <table class="w-full text-sm">
              <thead>
                <tr
                  class="bg-neutral-50 dark:bg-neutral-800/50 text-left text-neutral-500 border-b border-neutral-200 dark:border-neutral-700"
                >
                  <th
                    class="px-6 py-4 font-bold uppercase tracking-wider text-[10px]"
                  >
                    Staff Member
                  </th>
                  <th
                    class="px-6 py-4 font-bold uppercase tracking-wider text-[10px]"
                  >
                    Position/Branch
                  </th>
                  <th
                    class="px-6 py-4 font-bold uppercase tracking-wider text-[10px]"
                  >
                    Security Role
                  </th>
                  <th
                    class="px-6 py-4 font-bold uppercase tracking-wider text-[10px]"
                  >
                    Salary Settings
                  </th>
                  <th
                    class="px-6 py-4 font-bold uppercase tracking-wider text-[10px] text-right"
                  >
                    Actions
                  </th>
                </tr>
              </thead>
              <tbody
                class="divide-y divide-neutral-200 dark:divide-neutral-700"
              >
                <template v-if="loadingEmployees">
                  <tr v-for="i in 5" :key="i" class="animate-pulse">
                    <td v-for="j in 5" :key="j" class="px-6 py-4">
                      <div
                        class="h-4 bg-neutral-100 dark:bg-neutral-800 rounded"
                      ></div>
                    </td>
                  </tr>
                </template>
                <template v-else>
                  <tr
                    v-for="emp in employees"
                    :key="emp.employeeId"
                    class="hover:bg-neutral-50 dark:hover:bg-neutral-800/30"
                  >
                    <td class="px-6 py-4">
                      <div class="flex items-center gap-3">
                        <div
                          class="w-10 h-10 rounded-2xl bg-primary-100 dark:bg-primary-900/30 flex items-center justify-center font-black text-primary-600"
                        >
                          {{ getInitials(emp.fullName) }}
                        </div>
                        <div>
                          <p
                            class="font-black text-neutral-900 dark:text-white leading-tight"
                          >
                            {{ emp.fullName }}
                          </p>
                          <span
                            :class="
                              emp.status === 'ACTIVE'
                                ? 'text-success-600'
                                : 'text-neutral-400'
                            "
                            class="text-[10px] font-bold uppercase tracking-widest"
                          >
                            {{ emp.status }}
                          </span>
                        </div>
                      </div>
                    </td>
                    <td class="px-6 py-4">
                      <p
                        class="font-bold text-neutral-700 dark:text-neutral-300"
                      >
                        {{ emp.position }}
                      </p>
                      <p class="text-xs text-neutral-500">
                        {{ emp.branch?.name || "Global" }}
                      </p>
                    </td>
                    <td class="px-6 py-4">
                      <div
                        v-if="emp.roleName"
                        class="flex items-center gap-1.5"
                      >
                        <div
                          class="w-1.5 h-1.5 rounded-full bg-primary-500"
                        ></div>
                        <span
                          class="font-bold text-neutral-900 dark:text-white uppercase tracking-tighter text-xs"
                          >{{ emp.roleName }}</span
                        >
                      </div>
                      <span v-else class="text-[10px] text-neutral-400 italic"
                        >No account assigned</span
                      >
                    </td>
                    <td class="px-6 py-4">
                      <div class="flex flex-col gap-1">
                        <span
                          class="text-[10px] font-bold text-neutral-500 uppercase tracking-widest"
                          >Base:
                          <span class="text-neutral-900 dark:text-white"
                            >${{ emp.baseSalary?.toFixed(2) || "0.00" }}</span
                          ></span
                        >
                        <span
                          class="text-[10px] font-bold text-neutral-500 uppercase tracking-widest"
                          >Hourly:
                          <span class="text-neutral-900 dark:text-white"
                            >${{ emp.hourlyRate?.toFixed(2) || "0.00" }}</span
                          ></span
                        >
                      </div>
                    </td>
                    <td class="px-6 py-4 text-right">
                      <button
                        @click="editEmployee(emp)"
                        class="p-2 text-neutral-400 hover:text-primary-600 rounded-xl hover:bg-primary-50 dark:hover:bg-primary-900/20 transition-all"
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
                            d="M17 3a2.828 2.828 0 1 1 4 4L7.5 20.5 2 22l1.5-5.5L17 3z"
                          />
                        </svg>
                      </button>
                    </td>
                  </tr>
                </template>
              </tbody>
            </table>
          </div>
        </div>
      </div>

      <!-- -- TAB CONTENT: PAYROLL -- -->
      <div v-if="activeTab === 'payroll'" class="space-y-6">
        <!-- Date Filter -->
        <div
          class="flex items-end gap-4 p-6 bg-white dark:bg-neutral-900 rounded-3xl border border-neutral-200 dark:border-neutral-800 shadow-sm"
        >
          <div class="flex-1 max-w-xs">
            <label
              class="text-[10px] font-bold text-neutral-500 uppercase tracking-widest mb-2 block"
              >Start Date</label
            >
            <input
              type="date"
              v-model="payrollDates.start"
              class="input w-full"
            />
          </div>
          <div class="flex-1 max-w-xs">
            <label
              class="text-[10px] font-bold text-neutral-500 uppercase tracking-widest mb-2 block"
              >End Date</label
            >
            <input
              type="date"
              v-model="payrollDates.end"
              class="input w-full"
            />
          </div>
          <button
            @click="fetchPayroll"
            class="btn-primary py-2.5 px-6"
            :disabled="loadingPayroll"
          >
            {{ loadingPayroll ? "Calculating..." : "Generate Payroll" }}
          </button>
        </div>

        <!-- Payroll Summary Cards -->
        <div
          v-if="payrollData.length > 0"
          class="grid grid-cols-1 md:grid-cols-4 gap-6"
        >
          <div class="card p-6 border-l-4 border-l-primary-500">
            <p
              class="text-[10px] font-bold text-neutral-500 uppercase tracking-widest mb-1"
            >
              Total Payout
            </p>
            <h3 class="text-2xl font-black text-neutral-900 dark:text-white">
              ${{ totalPayrollPayout.toFixed(2) }}
            </h3>
          </div>
          <div class="card p-6 border-l-4 border-l-accent-500">
            <p
              class="text-[10px] font-bold text-neutral-500 uppercase tracking-widest mb-1"
            >
              Total Hours
            </p>
            <h3 class="text-2xl font-black text-neutral-900 dark:text-white">
              {{ totalPayrollHours.toFixed(1) }}h
            </h3>
          </div>
        </div>

        <!-- Payroll Table -->
        <div class="card overflow-hidden">
          <div class="overflow-x-auto">
            <table class="w-full text-sm">
              <thead>
                <tr
                  class="bg-neutral-50 dark:bg-neutral-800/50 text-left text-neutral-500 border-b border-neutral-200 dark:border-neutral-700"
                >
                  <th
                    class="px-6 py-4 font-bold uppercase tracking-wider text-[10px]"
                  >
                    Employee
                  </th>
                  <th
                    class="px-6 py-4 font-bold uppercase tracking-wider text-[10px]"
                  >
                    Worked
                  </th>
                  <th
                    class="px-6 py-4 font-bold uppercase tracking-wider text-[10px]"
                  >
                    Attendance
                  </th>
                  <th
                    class="px-6 py-4 font-bold uppercase tracking-wider text-[10px]"
                  >
                    Earnings Breakdown
                  </th>
                  <th
                    class="px-6 py-4 font-bold uppercase tracking-wider text-[10px] text-right"
                  >
                    Net Pay
                  </th>
                </tr>
              </thead>
              <tbody
                class="divide-y divide-neutral-200 dark:divide-neutral-700"
              >
                <tr
                  v-for="p in payrollData"
                  :key="p.employeeId"
                  class="hover:bg-neutral-50 dark:hover:bg-neutral-800/30"
                >
                  <td class="px-6 py-4">
                    <p class="font-black text-neutral-900 dark:text-white">
                      {{ p.fullName }}
                    </p>
                    <p
                      class="text-[10px] text-neutral-500 font-bold uppercase tracking-widest"
                    >
                      {{ p.position }}
                    </p>
                  </td>
                  <td class="px-6 py-4 text-xs">
                    <p class="font-bold">{{ p.daysWorked }} Days</p>
                    <p class="text-neutral-500">
                      {{ p.totalHoursWorked.toFixed(1) }} Hours
                    </p>
                  </td>
                  <td class="px-6 py-4">
                    <div
                      v-if="p.lateOccurrences > 0"
                      class="flex items-center gap-1.5 text-error-600 font-bold text-[10px] uppercase"
                    >
                      <svg
                        xmlns="http://www.w3.org/2000/svg"
                        class="w-3 h-3"
                        viewBox="0 0 24 24"
                        fill="none"
                        stroke="currentColor"
                        stroke-width="3"
                      >
                        <circle cx="12" cy="12" r="10" />
                        <path d="M12 8v4l3 3" />
                      </svg>
                      {{ p.lateOccurrences }} Lates ({{ p.totalLateMinutes }}m)
                    </div>
                    <div
                      v-else
                      class="text-success-600 font-bold text-[10px] uppercase"
                    >
                      Perfect Schedule
                    </div>
                  </td>
                  <td class="px-6 py-4 text-[10px] font-bold text-neutral-500">
                    <div class="flex justify-between max-w-[150px]">
                      <span>Base Salary:</span>
                      <span class="text-neutral-900 dark:text-white"
                        >${{ p.baseSalary.toFixed(2) }}</span
                      >
                    </div>
                    <div class="flex justify-between max-w-[150px]">
                      <span
                        >Hourly ({{ p.totalHoursWorked.toFixed(1) }}h):</span
                      >
                      <span class="text-neutral-900 dark:text-white"
                        >${{ p.hourlyEarnings.toFixed(2) }}</span
                      >
                    </div>
                  </td>
                  <td class="px-6 py-4 text-right">
                    <span class="text-lg font-black text-primary-600"
                      >${{ p.totalEarnings.toFixed(2) }}</span
                    >
                  </td>
                </tr>
                <tr v-if="payrollData.length === 0">
                  <td
                    colspan="5"
                    class="px-6 py-12 text-center text-neutral-400 italic"
                  >
                    No payroll data generated yet. Choose dates and click
                    "Generate".
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>

      <!-- -- MODALS (Remaining tabs like shifts/attendance use previous simple designs but wrapped in NuxtLayout) -- -->
      <div
        v-if="activeTab === 'shifts' || activeTab === 'attendance'"
        class="space-y-4"
      >
        <!-- Reuse your existing tables for Shifts and Attendance but styled slightly more premium -->
        <!-- (Omitting full implementation here to save space, but keeping placeholders) -->
        <div v-if="activeTab === 'shifts'" class="space-y-6">

          <!-- Today's Schedule Summary -->
          <div v-if="todayShifts.length > 0" class="relative overflow-hidden rounded-3xl">
            <div class="absolute inset-0 bg-gradient-to-br from-primary-600 via-primary-700 to-accent-700"></div>
            <div class="absolute inset-0 opacity-10" style="background-image: radial-gradient(circle at 20% 50%, rgba(255,255,255,0.15) 0%, transparent 50%)"></div>
            <div class="relative p-6">
              <div class="flex items-center justify-between mb-4">
                <div class="flex items-center gap-3">
                  <div class="w-10 h-10 rounded-xl bg-white/15 backdrop-blur-sm flex items-center justify-center">
                    <svg xmlns="http://www.w3.org/2000/svg" class="w-5 h-5 text-white" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>
                  </div>
                  <div>
                    <h3 class="text-sm font-black text-white uppercase tracking-wider">Today's Schedule</h3>
                    <p class="text-xs text-white/60">{{ new Date().toLocaleDateString([], { weekday: 'long', month: 'long', day: 'numeric' }) }}</p>
                  </div>
                </div>
                <div class="flex items-center gap-3">
                  <div class="text-center px-4 py-2 bg-white/10 backdrop-blur-sm rounded-xl border border-white/10">
                    <p class="text-2xl font-black text-white">{{ todayShifts.length }}</p>
                    <p class="text-[9px] text-white/60 font-bold uppercase tracking-widest">Shifts</p>
                  </div>
                  <div class="text-center px-4 py-2 bg-white/10 backdrop-blur-sm rounded-xl border border-white/10">
                    <p class="text-2xl font-black text-white">{{ todayOnDutyCount }}</p>
                    <p class="text-[9px] text-white/60 font-bold uppercase tracking-widest">On Duty</p>
                  </div>
                </div>
              </div>
              <div class="flex gap-3 overflow-x-auto pb-1 custom-scrollbar">
                <div v-for="ts in todayShifts" :key="ts.shiftId" class="flex-shrink-0 bg-white/10 backdrop-blur-sm rounded-2xl p-3.5 border border-white/10 min-w-[180px]">
                  <div class="flex items-center gap-2 mb-2">
                    <div class="w-7 h-7 rounded-lg bg-white/20 flex items-center justify-center text-white text-[10px] font-black">
                      {{ getInitials(ts.employee?.fullName || '?') }}
                    </div>
                    <span class="text-white font-bold text-xs truncate">{{ ts.employee?.fullName }}</span>
                  </div>
                  <div class="flex items-center justify-between">
                    <span class="text-white/70 font-mono text-[11px]">{{ formatShiftTime(ts.shiftStart) }} — {{ formatShiftTime(ts.shiftEnd) }}</span>
                    <span :class="getTodayShiftBadge(ts.shiftStart)" class="text-[9px] font-black px-1.5 py-0.5 rounded uppercase">{{ getShiftType(ts.shiftStart) }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Weekly Schedule Grid -->
          <div id="weekly-schedule-grid" class="bg-white dark:bg-neutral-900 rounded-3xl border border-neutral-200 dark:border-neutral-800 overflow-hidden shadow-sm">
            <div class="p-5 bg-neutral-50/50 dark:bg-neutral-800/50 border-b border-neutral-200 dark:border-neutral-800 flex justify-between items-center">
              <div class="flex items-center gap-3">
                <div class="w-9 h-9 rounded-xl bg-accent-100 dark:bg-accent-900/30 flex items-center justify-center text-accent-600">
                  <svg xmlns="http://www.w3.org/2000/svg" class="w-4.5 h-4.5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><rect x="3" y="4" width="18" height="18" rx="2" ry="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/></svg>
                </div>
                <div>
                  <h3 class="text-sm font-black text-neutral-900 dark:text-white">Weekly Schedule</h3>
                  <p class="text-[10px] text-neutral-400 font-bold uppercase tracking-widest">{{ weekLabel }}</p>
                </div>
              </div>
              <div class="flex items-center gap-2">
                <button @click="weekOffset--" class="p-2 rounded-xl hover:bg-neutral-100 dark:hover:bg-neutral-800 transition-colors text-neutral-500">
                  <svg xmlns="http://www.w3.org/2000/svg" class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="m15 18-6-6 6-6"/></svg>
                </button>
                <button @click="weekOffset = 0" class="px-3 py-1 rounded-lg text-xs font-bold" :class="weekOffset === 0 ? 'bg-primary-100 text-primary-600 dark:bg-primary-900/30' : 'text-neutral-500 hover:bg-neutral-100 dark:hover:bg-neutral-800'">Today</button>
                <button @click="weekOffset++" class="p-2 rounded-xl hover:bg-neutral-100 dark:hover:bg-neutral-800 transition-colors text-neutral-500">
                  <svg xmlns="http://www.w3.org/2000/svg" class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="m9 18 6-6-6-6"/></svg>
                </button>
                <div class="w-px h-6 bg-neutral-200 dark:bg-neutral-700 mx-1"></div>
                <button @click="printSchedule" class="p-2 rounded-xl hover:bg-neutral-100 dark:hover:bg-neutral-800 transition-colors text-neutral-500" title="Print Schedule">
                  <svg xmlns="http://www.w3.org/2000/svg" class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="6 9 6 2 18 2 18 9"/><path d="M6 18H4a2 2 0 0 1-2-2v-5a2 2 0 0 1 2-2h16a2 2 0 0 1 2 2v5a2 2 0 0 1-2 2h-2"/><rect width="12" height="8" x="6" y="14"/></svg>
                </button>
              </div>
            </div>
            <div class="overflow-x-auto">
              <table class="w-full text-sm">
                <thead>
                  <tr class="border-b border-neutral-200 dark:border-neutral-700">
                    <th class="px-4 py-3 text-left text-[10px] font-black text-neutral-400 uppercase tracking-widest min-w-[160px] sticky left-0 bg-white dark:bg-neutral-900 z-10">Employee</th>
                    <th class="px-2 py-3 text-left text-[10px] font-black text-neutral-400 uppercase tracking-widest min-w-[80px]">Position</th>
                    <th v-for="day in weekDays" :key="day.key" class="px-2 py-3 text-center text-[10px] font-black uppercase tracking-widest min-w-[70px]" :class="day.isToday ? 'text-primary-600 bg-primary-50/50 dark:bg-primary-900/10' : 'text-neutral-400'">
                      <div>{{ day.label }}</div>
                      <div class="text-[9px] font-medium" :class="day.isToday ? 'text-primary-500' : 'text-neutral-300 dark:text-neutral-600'">{{ day.date }}</div>
                    </th>
                    <th class="px-2 py-3 text-center text-[10px] font-black text-neutral-400 uppercase tracking-widest min-w-[55px]">Hrs</th>
                    <th class="px-2 py-3 text-right text-[10px] font-black text-neutral-400 uppercase tracking-widest min-w-[70px]">Cost</th>
                  </tr>
                </thead>
                <tbody class="divide-y divide-neutral-100 dark:divide-neutral-800">
                  <tr v-for="row in weekScheduleGrid" :key="row.employeeId" class="hover:bg-neutral-50/50 dark:hover:bg-neutral-800/20 transition-colors">
                    <td class="px-4 py-3 sticky left-0 bg-white dark:bg-neutral-900 z-10">
                      <div class="flex items-center gap-2">
                        <div class="w-7 h-7 rounded-lg bg-gradient-to-br from-primary-500 to-accent-500 flex items-center justify-center text-white text-[10px] font-black">
                          {{ getInitials(row.name) }}
                        </div>
                        <span class="font-bold text-neutral-900 dark:text-white text-xs truncate max-w-[100px]">{{ row.name }}</span>
                      </div>
                    </td>
                    <td class="px-2 py-3">
                      <span class="text-[10px] text-neutral-500 font-medium">{{ row.position }}</span>
                    </td>
                    <td v-for="day in weekDays" :key="day.key" class="px-1 py-2 text-center" :class="day.isToday ? 'bg-primary-50/30 dark:bg-primary-900/5' : ''">
                      <template v-if="row.days[day.key] && row.days[day.key].length">
                        <div class="flex flex-col items-center gap-0.5">
                          <span v-for="sn in row.days[day.key]" :key="sn" :class="getShiftCellClass(sn)" class="inline-flex items-center justify-center w-8 h-6 rounded text-[10px] font-black cursor-default" :title="sn === 1 ? 'Morning' : sn === 2 ? 'Afternoon' : 'Night'">
                            {{ sn }}
                          </span>
                          <button @click="quickAssignShift(row.employeeId, day.key)" class="inline-flex items-center justify-center w-8 h-5 rounded text-[10px] font-bold text-neutral-400 hover:text-primary-500 hover:bg-primary-50 dark:hover:bg-primary-900/20 transition-colors cursor-pointer border border-dashed border-neutral-200 dark:border-neutral-700" title="Add another shift">+</button>
                        </div>
                      </template>
                      <template v-else>
                        <button @click="quickAssignShift(row.employeeId, day.key)" class="inline-flex items-center justify-center w-8 h-8 rounded-lg text-[10px] font-bold text-success-500 bg-success-50 dark:bg-success-900/10 hover:bg-primary-100 hover:text-primary-600 dark:hover:bg-primary-900/20 transition-colors cursor-pointer" title="Click to assign shift">Off</button>
                      </template>
                    </td>
                    <td class="px-2 py-3 text-center">
                      <span class="font-mono text-xs font-bold text-neutral-600 dark:text-neutral-400">{{ getEmployeeWeekHours(row) }}h</span>
                    </td>
                    <td class="px-2 py-3 text-right">
                      <span class="font-mono text-xs font-bold text-success-600">${{ getEmployeeWeekCost(row).toFixed(0) }}</span>
                    </td>
                  </tr>
                </tbody>
                <!-- Totals Footer -->
                <tfoot class="border-t-2 border-neutral-200 dark:border-neutral-700">
                  <tr v-for="shiftType in [1, 2, 3]" :key="shiftType" class="bg-neutral-50/50 dark:bg-neutral-800/20">
                    <td class="px-4 py-2 sticky left-0 bg-neutral-50 dark:bg-neutral-800/50 z-10" :colspan="2">
                      <span class="text-[10px] font-black text-neutral-500 uppercase tracking-widest">
                        {{ shiftType === 1 ? '☀️ Morning' : shiftType === 2 ? '🌤 Afternoon' : '🌙 Night' }}
                      </span>
                    </td>
                    <td v-for="day in weekDays" :key="day.key" class="px-1 py-2 text-center" :class="day.isToday ? 'bg-primary-50/30 dark:bg-primary-900/5' : ''">
                      <span class="text-xs font-black text-neutral-600 dark:text-neutral-400">{{ getWeekShiftCount(day.key, shiftType) }}</span>
                    </td>
                    <td colspan="2"></td>
                  </tr>
                  <!-- Total Labor Cost Row -->
                  <tr class="bg-primary-50/50 dark:bg-primary-900/10 border-t border-primary-200 dark:border-primary-800">
                    <td class="px-4 py-3 sticky left-0 bg-primary-50 dark:bg-primary-900/20 z-10" colspan="2">
                      <span class="text-[10px] font-black text-primary-700 dark:text-primary-400 uppercase tracking-widest">💰 Est. Weekly Labor Cost</span>
                    </td>
                    <td :colspan="weekDays.length" class="px-2 py-3 text-right">
                      <span class="text-lg font-black text-primary-700 dark:text-primary-400">${{ totalWeekLaborCost.toFixed(2) }}</span>
                    </td>
                  </tr>
                </tfoot>
              </table>
              <div v-if="weekScheduleGrid.length === 0" class="py-12 text-center text-neutral-400 text-sm italic">
                No employees with shifts this week
              </div>
            </div>
            <div class="px-5 py-3 bg-neutral-50/50 dark:bg-neutral-800/30 border-t border-neutral-200 dark:border-neutral-800 flex items-center gap-4 text-[10px] font-bold uppercase tracking-widest text-neutral-400">
              <span class="flex items-center gap-1.5"><span class="w-4 h-4 rounded bg-warning-100 text-warning-700 inline-flex items-center justify-center text-[9px] font-black">1</span> Morning</span>
              <span class="flex items-center gap-1.5"><span class="w-4 h-4 rounded bg-primary-100 text-primary-700 inline-flex items-center justify-center text-[9px] font-black">2</span> Afternoon</span>
              <span class="flex items-center gap-1.5"><span class="w-4 h-4 rounded bg-accent-100 text-accent-700 inline-flex items-center justify-center text-[9px] font-black">3</span> Night</span>
              <span class="flex items-center gap-1.5"><span class="w-4 h-4 rounded bg-success-50 text-success-500 inline-flex items-center justify-center text-[9px] font-bold">Off</span> Day Off</span>
            </div>
          </div>
        </div>

        <div v-if="activeTab === 'attendance'" class="space-y-6">
          <div class="bg-white dark:bg-neutral-900 rounded-3xl border border-neutral-200 dark:border-neutral-800 overflow-hidden shadow-sm">
            <div class="p-5 bg-neutral-50/50 dark:bg-neutral-800/50 border-b border-neutral-200 dark:border-neutral-800 flex justify-between items-center">
              <div class="flex items-center gap-3">
                <div class="w-9 h-9 rounded-xl bg-success-100 dark:bg-success-900/30 flex items-center justify-center text-success-600">
                  <svg xmlns="http://www.w3.org/2000/svg" class="w-4.5 h-4.5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>
                </div>
                <div>
                  <h3 class="text-sm font-black text-neutral-900 dark:text-white">Attendance Records</h3>
                  <p class="text-[10px] text-neutral-400 font-bold uppercase tracking-widest">{{ attendance.length }} records</p>
                </div>
              </div>
            </div>
            <div class="overflow-x-auto">
              <table class="w-full text-sm">
                <thead>
                  <tr class="border-b border-neutral-200 dark:border-neutral-700">
                    <th class="px-6 py-3 text-left text-[10px] font-black text-neutral-400 uppercase tracking-widest">Employee</th>
                    <th class="px-6 py-3 text-left text-[10px] font-black text-neutral-400 uppercase tracking-widest">Date</th>
                    <th class="px-6 py-3 text-left text-[10px] font-black text-neutral-400 uppercase tracking-widest">Clock In</th>
                    <th class="px-6 py-3 text-left text-[10px] font-black text-neutral-400 uppercase tracking-widest">Clock Out</th>
                    <th class="px-6 py-3 text-left text-[10px] font-black text-neutral-400 uppercase tracking-widest">Duration</th>
                    <th class="px-6 py-3 text-left text-[10px] font-black text-neutral-400 uppercase tracking-widest">Status</th>
                  </tr>
                </thead>
                <tbody class="divide-y divide-neutral-100 dark:divide-neutral-800">
                  <tr
                    v-for="att in attendance"
                    :key="att.attendanceId"
                    class="hover:bg-neutral-50 dark:hover:bg-neutral-800/30 transition-colors"
                  >
                    <td class="px-6 py-4">
                      <div class="flex items-center gap-3">
                        <div class="w-9 h-9 rounded-xl bg-gradient-to-br from-success-500 to-primary-500 flex items-center justify-center text-white text-xs font-black shadow-md shadow-success-500/20">
                          {{ getInitials(att.employee?.fullName || '?') }}
                        </div>
                        <span class="font-bold text-neutral-900 dark:text-white text-sm">{{ att.employee?.fullName }}</span>
                      </div>
                    </td>
                    <td class="px-6 py-4">
                      <span class="font-bold text-neutral-700 dark:text-neutral-300 text-sm">{{ formatShiftDate(att.checkIn) }}</span>
                    </td>
                    <td class="px-6 py-4">
                      <div class="flex items-center gap-1.5">
                        <div class="w-1.5 h-1.5 rounded-full bg-success-500"></div>
                        <span class="font-mono text-xs text-neutral-600 dark:text-neutral-400">{{ formatShiftTime(att.checkIn) }}</span>
                      </div>
                    </td>
                    <td class="px-6 py-4">
                      <template v-if="att.checkOut">
                        <div class="flex items-center gap-1.5">
                          <div class="w-1.5 h-1.5 rounded-full bg-error-500"></div>
                          <span class="font-mono text-xs text-neutral-600 dark:text-neutral-400">{{ formatShiftTime(att.checkOut) }}</span>
                        </div>
                      </template>
                      <span v-else class="text-[10px] font-bold text-success-600 bg-success-50 dark:bg-success-900/20 px-2 py-0.5 rounded-lg uppercase tracking-widest">Active</span>
                    </td>
                    <td class="px-6 py-4">
                      <span class="font-mono text-xs text-neutral-600 dark:text-neutral-400">{{ att.checkOut ? calculateDuration(att.checkIn, att.checkOut) : calculateDuration(att.checkIn) }}</span>
                    </td>
                    <td class="px-6 py-4">
                      <div class="flex items-center gap-2">
                        <span
                          :class="getStatusClass(att.status)"
                          class="text-[10px] px-2 py-0.5 rounded-lg font-black uppercase tracking-wider"
                        >
                          {{ att.status }}
                        </span>
                        <span v-if="att.lateMinute && att.lateMinute > 0" class="text-[10px] font-bold text-warning-600">
                          +{{ att.lateMinute }}min
                        </span>
                      </div>
                    </td>
                  </tr>
                </tbody>
              </table>
              <!-- Empty State -->
              <div v-if="attendance.length === 0" class="flex flex-col items-center justify-center py-16 text-center">
                <div class="w-16 h-16 rounded-2xl bg-neutral-100 dark:bg-neutral-800 flex items-center justify-center mb-4">
                  <svg xmlns="http://www.w3.org/2000/svg" class="w-8 h-8 text-neutral-300 dark:text-neutral-600" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>
                </div>
                <p class="font-bold text-neutral-500 dark:text-neutral-400 mb-1">No attendance records</p>
                <p class="text-xs text-neutral-400">Records will appear when staff clock in</p>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Modals (Employee & Shift) -->
      <!-- (Integrated with salary fields) -->
      <div
        v-if="showEmployeeModal"
        class="fixed inset-0 z-50 flex items-center justify-center bg-black/80 backdrop-blur-sm p-4"
      >
        <div
          class="bg-white dark:bg-neutral-900 rounded-3xl shadow-2xl w-full max-w-xl overflow-hidden border border-neutral-200 dark:border-neutral-800 animate-in fade-in zoom-in duration-300"
        >
          <div
            class="p-8 border-b border-neutral-200 dark:border-neutral-800 flex justify-between items-center"
          >
            <h3
              class="text-2xl font-black text-neutral-900 dark:text-white uppercase tracking-tight"
            >
              {{ editingEmployeeId ? "Edit Staff Member" : "Add Staff Member" }}
            </h3>
            <button
              @click="showEmployeeModal = false"
              class="text-neutral-400 hover:text-neutral-600 transition-colors"
            >
              <svg
                xmlns="http://www.w3.org/2000/svg"
                class="w-6 h-6"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                stroke-width="2.5"
              >
                <path d="M18 6 6 18" />
                <path d="m6 6 12 12" />
              </svg>
            </button>
          </div>
          <div class="p-8 space-y-6">
            <div class="grid grid-cols-2 gap-6">
              <div>
                <label
                  class="text-[10px] font-bold text-neutral-500 uppercase tracking-widest mb-2 block"
                  >Full Name</label
                >
                <input
                  type="text"
                  v-model="newEmployee.fullName"
                  class="input w-full"
                  placeholder="e.g. Emma S."
                />
              </div>
              <div>
                <label
                  class="text-[10px] font-bold text-neutral-500 uppercase tracking-widest mb-2 block"
                  >Phone Number</label
                >
                <input
                  type="text"
                  v-model="newEmployee.phone"
                  class="input w-full"
                />
              </div>
            </div>
            <div class="grid grid-cols-2 gap-6">
              <div>
                <label
                  class="text-[10px] font-bold text-neutral-500 uppercase tracking-widest mb-2 block"
                  >Position</label
                >
                <select
                  v-model="newEmployee.position"
                  class="input w-full bg-neutral-50 dark:bg-neutral-800"
                >
                  <option value="Barista">Barista</option>
                  <option value="Chef">Chef</option>
                  <option value="Manager">Manager</option>
                  <option value="Server">Server</option>
                </select>
              </div>
              <div>
                <label
                  class="text-[10px] font-bold text-neutral-500 uppercase tracking-widest mb-2 block"
                  >Branch</label
                >
                <select
                  v-model="newEmployee.branchId"
                  class="input w-full bg-neutral-50 dark:bg-neutral-800"
                >
                  <option
                    v-for="b in branches"
                    :key="b.branchId"
                    :value="b.branchId"
                  >
                    {{ b.name }}
                  </option>
                </select>
              </div>
            </div>

            <!-- Security Role -->
            <div class="p-6 bg-neutral-100 dark:bg-neutral-800 rounded-3xl space-y-4">
               <h4 class="text-xs font-black text-neutral-500 uppercase tracking-widest">Access Control</h4>
               <div>
                 <label class="text-[10px] font-bold text-neutral-500 uppercase tracking-widest mb-2 block">System Role</label>
                 <select v-model="newEmployee.roleId" class="input w-full bg-white dark:bg-neutral-900 border-neutral-200 dark:border-neutral-700">
                   <option :value="null">No System Account</option>
                   <option v-for="role in roles" :key="role.roleId" :value="role.roleId">{{ role.roleName }}</option>
                 </select>
                 <p class="text-[10px] text-neutral-400 mt-2 italic">Assigning a role grants this employee access to the dashboard or POS.</p>
               </div>
            </div>

            <!-- Salary Info -->
            <div
              class="p-6 bg-primary-500/5 rounded-3xl border border-primary-500/10 space-y-4"
            >
              <h4
                class="text-xs font-black text-primary-600 uppercase tracking-widest"
              >
                Remuneration
              </h4>
              <div class="grid grid-cols-2 gap-6">
                <div>
                  <label
                    class="text-[10px] font-bold text-neutral-500 uppercase tracking-widest mb-2 block"
                    >Base Salary ($)</label
                  >
                  <input
                    type="number"
                    v-model="newEmployee.baseSalary"
                    class="input w-full bg-white dark:bg-neutral-900"
                    step="0.01"
                  />
                </div>
                <div>
                  <label
                    class="text-[10px] font-bold text-neutral-500 uppercase tracking-widest mb-2 block"
                    >Hourly Rate ($)</label
                  >
                  <input
                    type="number"
                    v-model="newEmployee.hourlyRate"
                    class="input w-full bg-white dark:bg-neutral-900"
                    step="0.01"
                  />
                </div>
              </div>
            </div>
          </div>
          <div
            class="p-8 bg-neutral-50 dark:bg-neutral-800/50 flex justify-end gap-4"
          >
            <button
              @click="showEmployeeModal = false"
              class="btn-secondary px-8"
            >
              Cancel
            </button>
            <button @click="createEmployee" class="btn-primary px-8">
              Confirm Staff
            </button>
          </div>
        </div>
      </div>

      <!-- Shift Modal -->
      <div
        v-if="showShiftModal"
        class="fixed inset-0 z-50 flex items-center justify-center bg-black/80 backdrop-blur-sm p-4"
      >
        <div
          class="bg-white dark:bg-neutral-900 rounded-3xl shadow-2xl w-full max-w-lg overflow-hidden border border-neutral-200 dark:border-neutral-800 animate-in fade-in zoom-in duration-300"
        >
          <div
            class="p-8 border-b border-neutral-200 dark:border-neutral-800 flex justify-between items-center bg-neutral-50/50 dark:bg-neutral-800/50"
          >
            <h3
              class="text-xl font-black text-neutral-900 dark:text-white uppercase tracking-tight"
            >
              Assign New Shift
            </h3>
          </div>
          <div class="p-8 space-y-6">
            <div>
              <label
                class="text-[10px] font-bold text-neutral-500 uppercase tracking-widest mb-2 block"
                >Select Employee</label
              >
              <select v-model="newShift.employeeId" class="input w-full">
                <option
                  v-for="emp in employees"
                  :key="emp.employeeId"
                  :value="emp.employeeId"
                >
                  {{ emp.fullName }}
                </option>
              </select>
            </div>

            <!-- Shift Templates -->
            <div>
              <label class="text-[10px] font-bold text-neutral-500 uppercase tracking-widest mb-2 block">Quick Template</label>
              <div class="grid grid-cols-3 gap-2">
                <button
                  v-for="tpl in shiftTemplates"
                  :key="tpl.name"
                  @click="applyShiftTemplate(tpl)"
                  class="p-3 rounded-xl border text-center transition-all hover:shadow-md"
                  :class="selectedTemplate === tpl.name
                    ? 'border-primary-500 bg-primary-50 dark:bg-primary-900/20 text-primary-600'
                    : 'border-neutral-200 dark:border-neutral-700 text-neutral-600 dark:text-neutral-400 hover:border-primary-300'"
                >
                  <span class="text-lg block mb-1">{{ tpl.icon }}</span>
                  <span class="text-xs font-bold block">{{ tpl.name }}</span>
                  <span class="text-[10px] text-neutral-400 block">{{ tpl.label }}</span>
                </button>
              </div>
            </div>

            <!-- Shift Date -->
            <div>
              <label class="text-[10px] font-bold text-neutral-500 uppercase tracking-widest mb-2 block">Shift Date</label>
              <input type="date" v-model="shiftDate" class="input w-full" />
            </div>

            <div class="grid grid-cols-2 gap-4">
              <div>
                <label
                  class="text-[10px] font-bold text-neutral-500 uppercase tracking-widest mb-2 block"
                  >Start</label
                >
                <input
                  type="datetime-local"
                  v-model="newShift.shiftStart"
                  class="input w-full"
                />
              </div>
              <div>
                <label
                  class="text-[10px] font-bold text-neutral-500 uppercase tracking-widest mb-2 block"
                  >End</label
                >
                <input
                  type="datetime-local"
                  v-model="newShift.shiftEnd"
                  class="input w-full"
                />
              </div>
            </div>
          </div>
          <div
            class="p-8 bg-neutral-50 dark:bg-neutral-800/50 flex justify-end gap-3"
          >
            <button @click="showShiftModal = false" class="btn-secondary">
              Cancel
            </button>
            <button @click="createShift" class="btn-primary">
              Schedule Shift
            </button>
          </div>
        </div>
      </div>
    </div>
  </NuxtLayout>
</template>

<script setup lang="ts">
import { ref, reactive, watch, computed, onMounted } from "vue";

definePageMeta({
  layout: false,
});

const { get, post, del } = useApi();
const toast = useToast();

// Tabs
const activeTab = ref("overview");
const tabs = [
  { id: "overview", name: "Overview" },
  { id: "employees", name: "Employees" },
  { id: "shifts", name: "Schedule" },
  { id: "attendance", name: "Log" },
  { id: "payroll", name: "Payroll & Earnings" },
];

// Data State
const employees = ref<any[]>([]);
const activeAttendance = ref<any[]>([]);
const attendance = ref<any[]>([]);
const shifts = ref<any[]>([]);
const branches = ref<any[]>([]);
const roles = ref<any[]>([]);

// Payroll State
const payrollData = ref<any[]>([]);
const loadingPayroll = ref(false);
const payrollDates = reactive({
  start: new Date(new Date().getFullYear(), new Date().getMonth(), 1)
    .toISOString()
    .split("T")[0],
  end: new Date().toISOString().split("T")[0],
});

// UI State
const loadingEmployees = ref(false);
const showEmployeeModal = ref(false);
const showShiftModal = ref(false);
const editingEmployeeId = ref<number | null>(null);

const newEmployee = reactive({
  fullName: "",
  phone: "",
  branchId: null as number | null,
  position: "Barista",
  status: "ACTIVE",
  baseSalary: 0,
  hourlyRate: 0,
  roleId: null as number | null,
});

const newShift = reactive({
  employeeId: null as number | null,
  branchId: 1, // Default
  shiftStart: "",
  shiftEnd: "",
});

// Shift Templates
const shiftTemplates = [
  { name: "Morning", icon: "☀️", label: "7:00 AM – 3:00 PM", startHour: 7, endHour: 15 },
  { name: "Afternoon", icon: "🌅", label: "11:00 AM – 7:00 PM", startHour: 11, endHour: 19 },
  { name: "Evening", icon: "🌙", label: "3:00 PM – 11:00 PM", startHour: 15, endHour: 23 },
];
const selectedTemplate = ref("");
const shiftDate = ref(new Date().toISOString().split("T")[0]); // Today's date

// Shift Filters
const shiftFilterBranch = ref("");
const shiftFilterEmployee = ref("");

const filteredShifts = computed(() => {
  return shifts.value.filter((s: any) => {
    if (shiftFilterBranch.value && s.branch?.branchId != shiftFilterBranch.value) return false;
    if (shiftFilterEmployee.value && s.employee?.employeeId != shiftFilterEmployee.value) return false;
    return true;
  });
});

// Today's Schedule
const todayShifts = computed(() => {
  const today = new Date().toISOString().split("T")[0];
  return shifts.value.filter((s: any) => s.shiftStart?.startsWith(today));
});

const todayOnDutyCount = computed(() => {
  const now = new Date();
  return todayShifts.value.filter((s: any) => {
    const start = new Date(s.shiftStart);
    const end = new Date(s.shiftEnd);
    return now >= start && now <= end;
  }).length;
});

const getTodayShiftBadge = (dateStr: string) => {
  const hour = new Date(dateStr).getHours();
  if (hour < 12) return 'bg-warning-400/30 text-warning-200';
  if (hour < 17) return 'bg-white/20 text-white/90';
  return 'bg-accent-400/30 text-accent-200';
};

// Weekly Schedule Grid
const weekOffset = ref(0);

const weekDays = computed(() => {
  const today = new Date();
  const dayOfWeek = today.getDay(); // 0=Sun, 1=Mon
  const monday = new Date(today);
  monday.setDate(today.getDate() - (dayOfWeek === 0 ? 6 : dayOfWeek - 1) + (weekOffset.value * 7));
  const days = [];
  const labels = ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'];
  for (let i = 0; i < 7; i++) {
    const d = new Date(monday);
    d.setDate(monday.getDate() + i);
    const key = d.toISOString().split('T')[0];
    days.push({
      key,
      label: labels[i],
      date: d.toLocaleDateString([], { month: 'short', day: 'numeric' }),
      isToday: key === new Date().toISOString().split('T')[0],
    });
  }
  return days;
});

const weekLabel = computed(() => {
  if (weekDays.value.length === 0) return '';
  const first = weekDays.value[0].date;
  const last = weekDays.value[6].date;
  return `${first} – ${last}`;
});

const weekScheduleGrid = computed(() => {
  const empMap: Record<number, { employeeId: number; name: string; position: string; days: Record<string, number[]> }> = {};
  for (const s of shifts.value) {
    const empId = s.employee?.employeeId;
    if (!empId) continue;
    if (!empMap[empId]) {
      empMap[empId] = { employeeId: empId, name: s.employee.fullName, position: s.employee.position || '', days: {} };
    }
    const dateKey = s.shiftStart?.split('T')[0];
    if (!dateKey) continue;
    const inWeek = weekDays.value.some((d: any) => d.key === dateKey);
    if (inWeek) {
      const hour = new Date(s.shiftStart).getHours();
      const shiftNum = hour < 11 ? 1 : hour < 15 ? 2 : 3;
      if (!empMap[empId].days[dateKey]) empMap[empId].days[dateKey] = [];
      if (!empMap[empId].days[dateKey].includes(shiftNum)) empMap[empId].days[dateKey].push(shiftNum);
    }
  }
  return Object.values(empMap).sort((a, b) => a.name.localeCompare(b.name));
});

const getShiftCellClass = (shiftNum: number) => {
  if (shiftNum === 1) return 'bg-warning-100 text-warning-700 dark:bg-warning-900/30 dark:text-warning-400';
  if (shiftNum === 2) return 'bg-primary-100 text-primary-700 dark:bg-primary-900/30 dark:text-primary-400';
  return 'bg-accent-100 text-accent-700 dark:bg-accent-900/30 dark:text-accent-400';
};

const getWeekShiftCount = (dayKey: string, shiftType: number) => {
  return weekScheduleGrid.value.filter((row: any) => row.days[dayKey]?.includes(shiftType)).length;
};

// Labor cost calculation
const SHIFT_HOURS: Record<number, number> = { 1: 8, 2: 8, 3: 8 }; // Standard 8h shifts

const getEmployeeWeekHours = (row: any) => {
  let hours = 0;
  for (const day of weekDays.value) {
    const shifts = row.days[day.key];
    if (shifts && shifts.length) {
      for (const s of shifts) hours += SHIFT_HOURS[s] || 8;
    }
  }
  return hours;
};

const getEmployeeWeekCost = (row: any) => {
  const emp = employees.value.find((e: any) => e.employeeId === row.employeeId);
  const rate = emp?.hourlyRate || 0;
  return getEmployeeWeekHours(row) * rate;
};

const totalWeekLaborCost = computed(() => {
  return weekScheduleGrid.value.reduce((sum: number, row: any) => sum + getEmployeeWeekCost(row), 0);
});

// Schedule Publish
const weekPublished = ref(false);
watch(weekOffset, () => { weekPublished.value = false; });

const publishSchedule = () => {
  weekPublished.value = true;
  toast.success(`Schedule published for ${weekLabel.value}`);
};

// Quick assign shift from grid cell click
const quickAssignShift = (employeeId: number, dateKey: string) => {
  newShift.employeeId = employeeId;
  shiftDate.value = dateKey;
  // Apply morning template by default
  const tpl = shiftTemplates[0];
  selectedTemplate.value = tpl.name;
  const pad = (n: number) => String(n).padStart(2, '0');
  newShift.shiftStart = `${dateKey}T${pad(tpl.startHour)}:00`;
  newShift.shiftEnd = `${dateKey}T${pad(tpl.endHour)}:00`;
  showShiftModal.value = true;
};

const applyShiftTemplate = (tpl: { name: string; startHour: number; endHour: number }) => {
  selectedTemplate.value = tpl.name;
  const date = shiftDate.value || new Date().toISOString().split("T")[0];
  const pad = (n: number) => String(n).padStart(2, "0");
  newShift.shiftStart = `${date}T${pad(tpl.startHour)}:00`;
  newShift.shiftEnd = `${date}T${pad(tpl.endHour)}:00`;
};

// Watch date changes to re-apply template
watch(shiftDate, (newDate) => {
  if (selectedTemplate.value && newDate) {
    const tpl = shiftTemplates.find(t => t.name === selectedTemplate.value);
    if (tpl) applyShiftTemplate(tpl);
  }
});

// -- Lifecycle --
onMounted(() => {
  fetchOverview();
  fetchRoles();
});

watch(activeTab, (val) => {
  if (val === "overview") fetchOverview();
  if (val === "employees") fetchEmployees();
  if (val === "shifts") fetchShifts();
  if (val === "attendance") fetchAttendance();
  if (val === "payroll") fetchPayroll();
});

// -- Fetch Logic --
const fetchOverview = async () => {
  try {
    const activeRes = await get<any[]>("/attendance/active");
    activeAttendance.value = activeRes || [];

    // Also need all employees for stats
    if (employees.value.length === 0) fetchEmployees();

    // Fetch all attendance to count lates today
    const allAtt = await get<any[]>("/attendance");
    attendance.value = allAtt || [];
  } catch (err) {
    console.error(err);
  }
};

const fetchEmployees = async () => {
  loadingEmployees.value = true;
  try {
    const data = await get<any[]>("/employees");
    employees.value = data || [];

    const branchData = await get<any[]>("/branches");
    branches.value = branchData || [];
  } catch (err) {
    console.error(err);
  } finally {
    loadingEmployees.value = false;
  }
};

const fetchShifts = async () => {
  try {
    const data = await get<any[]>("/shifts");
    shifts.value = data || [];
  } catch (err) {
    console.error(err);
  }
};

const fetchAttendance = async () => {
  try {
    const data = await get<any[]>("/attendance");
    attendance.value = data || [];
  } catch (err) {
    console.error(err);
  }
};

const fetchPayroll = async () => {
  loadingPayroll.value = true;
  try {
    const data = await get<any[]>(
      `/payroll/report?startDate=${payrollDates.start}&endDate=${payrollDates.end}`,
    );
    payrollData.value = data || [];
  } catch (err) {
    console.error(err);
  } finally {
    loadingPayroll.value = false;
  }
};

const fetchRoles = async () => {
  try {
    const data = await get<any[]>("/roles");
    roles.value = data || [];
  } catch (err) {
    console.error("Failed to fetch roles:", err);
  }
};

// -- Actions --
const createEmployee = async () => {
  try {
    if (editingEmployeeId.value) {
      await useApi().put(`/employees/${editingEmployeeId.value}`, newEmployee);
    } else {
      await post("/employees/add", newEmployee);
    }
    showEmployeeModal.value = false;
    toast.success(
      editingEmployeeId.value
        ? "Staff member updated"
        : "New staff member added",
    );
    fetchEmployees();
  } catch (err) {
    toast.error("Failed to save employee");
  }
};

const editEmployee = (emp: any) => {
  editingEmployeeId.value = emp.employeeId;
  newEmployee.fullName = emp.fullName;
  newEmployee.phone = emp.phone;
  newEmployee.position = emp.position;
  newEmployee.branchId = emp.branch?.branchId || null;
  newEmployee.status = emp.status;
  newEmployee.baseSalary = emp.baseSalary;
  newEmployee.hourlyRate = emp.hourlyRate;
  newEmployee.roleId = emp.roleId || null;
  showEmployeeModal.value = true;
};

const createShift = async () => {
  try {
    await post("/shifts", newShift);
    showShiftModal.value = false;
    toast.success("Shift scheduled successfully");
    fetchShifts();
  } catch (err) {
    toast.error("Failed to create shift");
  }
};

const deleteShift = async (id: number) => {
  if (!confirm("Delete shift?")) return;
  try {
    await del(`/shifts/${id}`);
    toast.success("Shift deleted");
    fetchShifts();
  } catch (err) {
    toast.error("Failed to delete shift");
  }
};

// -- Computed --
const lateTodayCount = computed(() => {
  const today = new Date().toISOString().split("T")[0];
  return attendance.value.filter(
    (a) => a.status === "LATE" && a.checkIn.startsWith(today),
  ).length;
});

const totalPayrollPayout = computed(() =>
  payrollData.value.reduce((acc, p) => acc + p.totalEarnings, 0),
);
const totalPayrollHours = computed(() =>
  payrollData.value.reduce((acc, p) => acc + p.totalHoursWorked, 0),
);

// -- Helpers --
const getInitials = (name: string) =>
  name
    ? name
        .split(" ")
        .map((n) => n[0])
        .join("")
        .substring(0, 2)
        .toUpperCase()
    : "??";
const formatTime = (dateStr: string) =>
  new Date(dateStr).toLocaleTimeString([], {
    hour: "2-digit",
    minute: "2-digit",
  });
const formatDate = (dateStr: string) =>
  new Date(dateStr).toLocaleDateString([], {
    month: "short",
    day: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  });

// Shift display helpers
const getShiftType = (dateStr: string) => {
  const hour = new Date(dateStr).getHours();
  if (hour < 12) return 'Morning';
  if (hour < 17) return 'Afternoon';
  return 'Evening';
};
const getShiftTypeBadge = (dateStr: string) => {
  const hour = new Date(dateStr).getHours();
  if (hour < 12) return 'bg-warning-100 text-warning-700 dark:bg-warning-900/30 dark:text-warning-400';
  if (hour < 17) return 'bg-primary-100 text-primary-700 dark:bg-primary-900/30 dark:text-primary-400';
  return 'bg-accent-100 text-accent-700 dark:bg-accent-900/30 dark:text-accent-400';
};
const formatShiftDate = (dateStr: string) =>
  new Date(dateStr).toLocaleDateString([], { month: 'short', day: 'numeric', year: 'numeric' });
const formatShiftTime = (dateStr: string) =>
  new Date(dateStr).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });

const calculateDuration = (checkIn: string, checkOut?: string) => {
  const start = new Date(checkIn).getTime();
  const end = checkOut ? new Date(checkOut).getTime() : new Date().getTime();
  const diff = end - start;
  const hours = Math.floor(diff / 3600000);
  const minutes = Math.floor((diff % 3600000) / 60000);
  return `${hours}h ${minutes}m`;
};

const getStatusClass = (status: string) => {
  const classes: any = {
    PRESENT:
      "bg-success-100 text-success-700 dark:bg-success-900/30 dark:text-success-400",
    LATE: "bg-warning-100 text-warning-700 dark:bg-warning-900/30 dark:text-warning-400",
  };
  return classes[status] || "bg-neutral-100 text-neutral-500";
};

const openEmployeeModal = () => {
  editingEmployeeId.value = null;
  newEmployee.fullName = "";
  newEmployee.phone = "";
  newEmployee.position = "Barista";
  newEmployee.branchId =
    branches.value.length > 0 ? branches.value[0].branchId : null;
  newEmployee.status = "ACTIVE";
  newEmployee.baseSalary = 0;
  newEmployee.hourlyRate = 0;
  newEmployee.roleId = null;
  showEmployeeModal.value = true;
};
const openShiftModal = () => {
  showShiftModal.value = true;
};

const printSchedule = () => {
  document.body.classList.add('printing-schedule');
  const header = document.createElement('div');
  header.id = 'print-schedule-header';
  header.innerHTML = `<h1 style="font-size:18px;font-weight:900;text-align:center;margin-bottom:4px">Weekly Schedule</h1><p style="font-size:12px;color:#888;text-align:center;margin-bottom:12px">${weekLabel.value}</p>`;
  const grid = document.getElementById('weekly-schedule-grid');
  grid?.parentNode?.insertBefore(header, grid);
  const legend = document.createElement('div');
  legend.id = 'print-schedule-legend';
  legend.innerHTML = `<p style="font-size:10px;color:#888;margin-top:8px">1 = Morning Shift &nbsp; 2 = Afternoon Shift &nbsp; 3 = Night Shift &nbsp; Off = Day Off</p>`;
  grid?.parentNode?.insertBefore(legend, grid.nextSibling);
  setTimeout(() => {
    window.print();
    document.body.classList.remove('printing-schedule');
    document.getElementById('print-schedule-header')?.remove();
    document.getElementById('print-schedule-legend')?.remove();
  }, 100);
};
</script>

<style>
@media print {
  body.printing-schedule * { visibility: hidden !important; }
  body.printing-schedule #weekly-schedule-grid,
  body.printing-schedule #weekly-schedule-grid *,
  body.printing-schedule #print-schedule-header,
  body.printing-schedule #print-schedule-header *,
  body.printing-schedule #print-schedule-legend,
  body.printing-schedule #print-schedule-legend * { visibility: visible !important; }
  body.printing-schedule #weekly-schedule-grid {
    position: absolute; left: 0; top: 60px; width: 100%;
    border-radius: 0 !important; box-shadow: none !important; border: none !important;
  }
  body.printing-schedule #print-schedule-header { position: absolute; left: 0; top: 0; width: 100%; }
  body.printing-schedule #print-schedule-legend { position: absolute; left: 0; bottom: 0; width: 100%; }
  body.printing-schedule button { display: none !important; }
  body.printing-schedule table { border-collapse: collapse; width: 100%; }
  body.printing-schedule th, body.printing-schedule td { border: 1px solid #ccc !important; padding: 4px 6px !important; }
  @page { size: landscape; margin: 1cm; }
}
</style>
