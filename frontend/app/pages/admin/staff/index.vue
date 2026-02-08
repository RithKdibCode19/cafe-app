<template>
  <NuxtLayout name="admin">
    <div class="space-y-6">
      <UiBreadcrumb :items="[{ label: 'Staff' }]" />

      <!-- Header -->
      <div class="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
        <div>
          <h1 class="text-2xl font-bold text-neutral-900 dark:text-white">Staff & Operations</h1>
          <p class="text-neutral-500 dark:text-neutral-400">Manage your team, schedules, and payroll</p>
        </div>
        
        <div class="flex items-center gap-3">
          <button 
            v-if="activeTab === 'employees'"
            @click="openEmployeeModal"
            class="bg-primary-600 hover:bg-primary-700 text-white px-4 py-2 rounded-xl text-sm font-medium transition-all shadow-lg shadow-primary-500/20 flex items-center gap-2"
          >
            <svg xmlns="http://www.w3.org/2000/svg" class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M5 12h14"/><path d="M12 5v14"/></svg>
            Add Employee
          </button>
          <button 
            v-if="activeTab === 'shifts'"
            @click="openShiftModal"
            class="bg-primary-600 hover:bg-primary-700 text-white px-4 py-2 rounded-xl text-sm font-medium transition-all shadow-lg shadow-primary-500/20 flex items-center gap-2"
          >
            <svg xmlns="http://www.w3.org/2000/svg" class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M5 12h14"/><path d="M12 5v14"/></svg>
            New Shift
          </button>
        </div>
      </div>

      <!-- Tabs Navigation -->
      <div class="flex items-center gap-1 p-1 bg-neutral-100 dark:bg-neutral-800/50 rounded-2xl w-fit">
        <button 
          v-for="tab in tabs" 
          :key="tab.id"
          @click="activeTab = tab.id"
          :class="[
            'px-6 py-2 rounded-xl text-sm font-bold transition-all',
            activeTab === tab.id 
              ? 'bg-white dark:bg-neutral-800 text-primary-600 dark:text-primary-400 shadow-sm' 
              : 'text-neutral-500 hover:text-neutral-700 dark:hover:text-neutral-300'
          ]"
        >
          {{ tab.name }}
        </button>
      </div>

      <!-- -- TAB CONTENT: OVERVIEW -- -->
      <div v-if="activeTab === 'overview'" class="space-y-6">
        <!-- Stats Grid -->
        <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
          <div class="card p-6 bg-gradient-to-br from-primary-500/10 to-transparent border-primary-100 dark:border-primary-900/20">
            <p class="text-xs font-black text-primary-600 uppercase tracking-widest mb-1">Total Team</p>
            <h3 class="text-3xl font-black text-neutral-900 dark:text-white">{{ employees.length }}</h3>
            <p class="text-[10px] text-neutral-500 mt-1">Active staff members</p>
          </div>
          <div class="card p-6 bg-gradient-to-br from-success-500/10 to-transparent border-success-100 dark:border-success-900/20">
            <p class="text-xs font-black text-success-600 uppercase tracking-widest mb-1">On Duty Now</p>
            <h3 class="text-3xl font-black text-neutral-900 dark:text-white">{{ activeAttendance.length }}</h3>
            <p class="text-[10px] text-neutral-500 mt-1">Currently clocked in</p>
          </div>
          <div class="card p-6 bg-gradient-to-br from-warning-500/10 to-transparent border-warning-100 dark:border-warning-900/20">
            <p class="text-xs font-black text-warning-600 uppercase tracking-widest mb-1">Late Today</p>
            <h3 class="text-3xl font-black text-neutral-900 dark:text-white">{{ lateTodayCount }}</h3>
            <p class="text-[10px] text-neutral-500 mt-1">Arrivals after shift start</p>
          </div>
        </div>

        <!-- Currently Working List -->
        <div class="card overflow-hidden">
          <div class="p-4 border-b border-neutral-200 dark:border-neutral-700 bg-neutral-50/50 dark:bg-neutral-800/50">
            <h3 class="text-sm font-bold text-neutral-900 dark:text-white">Currently On Duty</h3>
          </div>
          <div class="overflow-x-auto">
            <table class="w-full text-sm">
                <thead>
                  <tr class="text-left text-neutral-500 border-b border-neutral-200 dark:border-neutral-700">
                    <th class="px-6 py-3 font-medium">Employee</th>
                    <th class="px-6 py-3 font-medium">Clocked In At</th>
                    <th class="px-6 py-3 font-medium">Duration</th>
                    <th class="px-6 py-3 font-medium">Status</th>
                  </tr>
                </thead>
                <tbody class="divide-y divide-neutral-200 dark:divide-neutral-700">
                  <tr v-for="att in activeAttendance" :key="att.attendanceId" class="hover:bg-neutral-50 dark:hover:bg-neutral-800/30">
                    <td class="px-6 py-4 font-bold text-neutral-900 dark:text-white">{{ att.employee?.fullName }}</td>
                    <td class="px-6 py-4 text-neutral-500">{{ formatTime(att.checkIn) }}</td>
                    <td class="px-6 py-4 font-mono text-neutral-600 dark:text-neutral-400">{{ calculateDuration(att.checkIn) }}</td>
                    <td class="px-6 py-4">
                        <span :class="att.status === 'LATE' ? 'bg-warning-100 text-warning-700' : 'bg-success-100 text-success-700'" class="text-[10px] px-2 py-0.5 rounded-full font-bold uppercase">
                          {{ att.status }}
                        </span>
                    </td>
                  </tr>
                  <tr v-if="activeAttendance.length === 0">
                    <td colspan="4" class="px-6 py-12 text-center text-neutral-400 bg-neutral-50/30 dark:bg-neutral-900/30">
                       <svg xmlns="http://www.w3.org/2000/svg" class="w-12 h-12 mx-auto mb-2 opacity-10" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 8v4l3 3"/><circle cx="12" cy="12" r="10"/></svg>
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
                <tr class="bg-neutral-50 dark:bg-neutral-800/50 text-left text-neutral-500 border-b border-neutral-200 dark:border-neutral-700">
                  <th class="px-6 py-4 font-bold uppercase tracking-wider text-[10px]">Staff Member</th>
                  <th class="px-6 py-4 font-bold uppercase tracking-wider text-[10px]">Position/Branch</th>
                  <th class="px-6 py-4 font-bold uppercase tracking-wider text-[10px]">Contact</th>
                  <th class="px-6 py-4 font-bold uppercase tracking-wider text-[10px]">Salary Settings</th>
                  <th class="px-6 py-4 font-bold uppercase tracking-wider text-[10px] text-right">Actions</th>
                </tr>
              </thead>
                <tbody class="divide-y divide-neutral-200 dark:divide-neutral-700">
                  <template v-if="loadingEmployees">
                    <tr v-for="i in 5" :key="i" class="animate-pulse">
                      <td v-for="j in 5" :key="j" class="px-6 py-4"><div class="h-4 bg-neutral-100 dark:bg-neutral-800 rounded"></div></td>
                    </tr>
                  </template>
                  <template v-else>
                    <tr v-for="emp in employees" :key="emp.employeeId" class="hover:bg-neutral-50 dark:hover:bg-neutral-800/30">
                  <td class="px-6 py-4">
                    <div class="flex items-center gap-3">
                      <div class="w-10 h-10 rounded-2xl bg-primary-100 dark:bg-primary-900/30 flex items-center justify-center font-black text-primary-600">
                        {{ getInitials(emp.fullName) }}
                      </div>
                      <div>
                        <p class="font-black text-neutral-900 dark:text-white leading-tight">{{ emp.fullName }}</p>
                        <span :class="emp.status === 'ACTIVE' ? 'text-success-600' : 'text-neutral-400'" class="text-[10px] font-bold uppercase tracking-widest">
                          {{ emp.status }}
                        </span>
                      </div>
                    </div>
                  </td>
                  <td class="px-6 py-4">
                    <p class="font-bold text-neutral-700 dark:text-neutral-300">{{ emp.position }}</p>
                    <p class="text-xs text-neutral-500">{{ emp.branch?.name || 'Global' }}</p>
                  </td>
                  <td class="px-6 py-4 text-neutral-500">{{ emp.phone }}</td>
                  <td class="px-6 py-4">
                    <div class="flex flex-col gap-1">
                      <span class="text-[10px] font-bold text-neutral-500 uppercase tracking-widest">Base: <span class="text-neutral-900 dark:text-white">${{ emp.baseSalary?.toFixed(2) || '0.00' }}</span></span>
                      <span class="text-[10px] font-bold text-neutral-500 uppercase tracking-widest">Hourly: <span class="text-neutral-900 dark:text-white">${{ emp.hourlyRate?.toFixed(2) || '0.00' }}</span></span>
                    </div>
                  </td>
                  <td class="px-6 py-4 text-right">
                    <button @click="editEmployee(emp)" class="p-2 text-neutral-400 hover:text-primary-600 rounded-xl hover:bg-primary-50 dark:hover:bg-primary-900/20 transition-all">
                      <svg xmlns="http://www.w3.org/2000/svg" class="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M17 3a2.828 2.828 0 1 1 4 4L7.5 20.5 2 22l1.5-5.5L17 3z"/></svg>
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
        <div class="flex items-end gap-4 p-6 bg-white dark:bg-neutral-900 rounded-3xl border border-neutral-200 dark:border-neutral-800 shadow-sm">
            <div class="flex-1 max-w-xs">
              <label class="text-[10px] font-bold text-neutral-500 uppercase tracking-widest mb-2 block">Start Date</label>
              <input type="date" v-model="payrollDates.start" class="input w-full" />
            </div>
            <div class="flex-1 max-w-xs">
              <label class="text-[10px] font-bold text-neutral-500 uppercase tracking-widest mb-2 block">End Date</label>
              <input type="date" v-model="payrollDates.end" class="input w-full" />
            </div>
            <button @click="fetchPayroll" class="btn-primary py-2.5 px-6" :disabled="loadingPayroll">
              {{ loadingPayroll ? 'Calculating...' : 'Generate Payroll' }}
            </button>
        </div>

        <!-- Payroll Summary Cards -->
        <div v-if="payrollData.length > 0" class="grid grid-cols-1 md:grid-cols-4 gap-6">
            <div class="card p-6 border-l-4 border-l-primary-500">
               <p class="text-[10px] font-bold text-neutral-500 uppercase tracking-widest mb-1">Total Payout</p>
               <h3 class="text-2xl font-black text-neutral-900 dark:text-white">${{ totalPayrollPayout.toFixed(2) }}</h3>
            </div>
             <div class="card p-6 border-l-4 border-l-accent-500">
               <p class="text-[10px] font-bold text-neutral-500 uppercase tracking-widest mb-1">Total Hours</p>
               <h3 class="text-2xl font-black text-neutral-900 dark:text-white">{{ totalPayrollHours.toFixed(1) }}h</h3>
            </div>
        </div>

        <!-- Payroll Table -->
        <div class="card overflow-hidden">
           <div class="overflow-x-auto">
             <table class="w-full text-sm">
                <thead>
                  <tr class="bg-neutral-50 dark:bg-neutral-800/50 text-left text-neutral-500 border-b border-neutral-200 dark:border-neutral-700">
                    <th class="px-6 py-4 font-bold uppercase tracking-wider text-[10px]">Employee</th>
                    <th class="px-6 py-4 font-bold uppercase tracking-wider text-[10px]">Worked</th>
                    <th class="px-6 py-4 font-bold uppercase tracking-wider text-[10px]">Attendance</th>
                    <th class="px-6 py-4 font-bold uppercase tracking-wider text-[10px]">Earnings Breakdown</th>
                    <th class="px-6 py-4 font-bold uppercase tracking-wider text-[10px] text-right">Net Pay</th>
                  </tr>
                </thead>
                <tbody class="divide-y divide-neutral-200 dark:divide-neutral-700">
                  <tr v-for="p in payrollData" :key="p.employeeId" class="hover:bg-neutral-50 dark:hover:bg-neutral-800/30">
                    <td class="px-6 py-4">
                       <p class="font-black text-neutral-900 dark:text-white">{{ p.fullName }}</p>
                       <p class="text-[10px] text-neutral-500 font-bold uppercase tracking-widest">{{ p.position }}</p>
                    </td>
                    <td class="px-6 py-4 text-xs">
                        <p class="font-bold">{{ p.daysWorked }} Days</p>
                        <p class="text-neutral-500">{{ p.totalHoursWorked.toFixed(1) }} Hours</p>
                    </td>
                    <td class="px-6 py-4">
                        <div v-if="p.lateOccurrences > 0" class="flex items-center gap-1.5 text-error-600 font-bold text-[10px] uppercase">
                            <svg xmlns="http://www.w3.org/2000/svg" class="w-3 h-3" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3"><circle cx="12" cy="12" r="10"/><path d="M12 8v4l3 3"/></svg>
                            {{ p.lateOccurrences }} Lates ({{ p.totalLateMinutes }}m)
                        </div>
                        <div v-else class="text-success-600 font-bold text-[10px] uppercase">
                            Perfect Schedule
                        </div>
                    </td>
                    <td class="px-6 py-4 text-[10px] font-bold text-neutral-500">
                        <div class="flex justify-between max-w-[150px]">
                           <span>Base Salary:</span>
                           <span class="text-neutral-900 dark:text-white">${{ p.baseSalary.toFixed(2) }}</span>
                        </div>
                        <div class="flex justify-between max-w-[150px]">
                           <span>Hourly ({{ p.totalHoursWorked.toFixed(1) }}h):</span>
                           <span class="text-neutral-900 dark:text-white">${{ p.hourlyEarnings.toFixed(2) }}</span>
                        </div>
                    </td>
                    <td class="px-6 py-4 text-right">
                        <span class="text-lg font-black text-primary-600">${{ p.totalEarnings.toFixed(2) }}</span>
                    </td>
                  </tr>
                  <tr v-if="payrollData.length === 0">
                    <td colspan="5" class="px-6 py-12 text-center text-neutral-400 italic">No payroll data generated yet. Choose dates and click "Generate".</td>
                  </tr>
                </tbody>
             </table>
           </div>
        </div>
      </div>

      <!-- -- MODALS (Remaining tabs like shifts/attendance use previous simple designs but wrapped in NuxtLayout) -- -->
      <div v-if="activeTab === 'shifts' || activeTab === 'attendance'" class="space-y-4">
         <!-- Reuse your existing tables for Shifts and Attendance but styled slightly more premium -->
         <!-- (Omitting full implementation here to save space, but keeping placeholders) -->
         <div v-if="activeTab === 'shifts'" class="card overflow-hidden">
            <div class="p-4 bg-neutral-50 dark:bg-neutral-800/50 border-b border-neutral-200 dark:border-neutral-700 flex justify-between items-center">
                <h3 class="text-sm font-bold">Upcoming Shift Schedule</h3>
            </div>
            <!-- ... Shift Table from original ... -->
             <div class="overflow-x-auto">
               <table class="w-full text-sm">
                  <tbody class="divide-y divide-neutral-200 dark:divide-neutral-700">
                    <!-- Mapping existing shifts here -->
                    <tr v-for="shift in shifts" :key="shift.shiftId" class="hover:bg-neutral-50 dark:hover:bg-neutral-800/30 transition-colors border-b border-neutral-200 dark:border-neutral-700">
                      <td class="px-6 py-4 font-bold">{{ shift.employee?.fullName }}</td>
                      <td class="px-6 py-4">{{ shift.branch?.name }}</td>
                      <td class="px-6 py-4 font-mono text-xs uppercase">{{ formatDate(shift.shiftStart) }} - {{ formatDate(shift.shiftEnd) }}</td>
                      <td class="px-6 py-4 text-right">
                         <button @click="deleteShift(shift.shiftId)" class="p-1.5 text-error-500 hover:bg-error-50 rounded-lg">
                           <svg xmlns="http://www.w3.org/2000/svg" class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M3 6h18"/><path d="M19 6v14c0 1-1 2-2 2H7c-1 0-2-1-2-2V6"/><path d="M8 6V4c0-1 1-2 2-2h4c1 0 2 1 2 2v2"/></svg>
                         </button>
                      </td>
                    </tr>
                  </tbody>
               </table>
             </div>
         </div>

         <div v-if="activeTab === 'attendance'" class="card overflow-hidden">
             <!-- ... Attendance Table from original ... -->
             <div class="overflow-x-auto">
               <table class="w-full text-sm">
                  <thead>
                     <tr class="bg-neutral-50 dark:bg-neutral-800/50 text-left border-b border-neutral-200 dark:border-neutral-700">
                        <th class="px-6 py-3 font-bold text-[10px] uppercase">Employee</th>
                        <th class="px-6 py-3 font-bold text-[10px] uppercase">Clock In</th>
                        <th class="px-6 py-3 font-bold text-[10px] uppercase">Clock Out</th>
                        <th class="px-6 py-3 font-bold text-[10px] uppercase">Status</th>
                     </tr>
                  </thead>
                  <tbody class="divide-y divide-neutral-200 dark:divide-neutral-700">
                    <tr v-for="att in attendance" :key="att.attendanceId" class="hover:bg-neutral-50 dark:hover:bg-neutral-800/30">
                      <td class="px-6 py-4 font-bold">{{ att.employee?.fullName }}</td>
                      <td class="px-6 py-4 font-mono text-xs">{{ new Date(att.checkIn).toLocaleString() }}</td>
                      <td class="px-6 py-4 font-mono text-xs">{{ att.checkOut ? new Date(att.checkOut).toLocaleString() : '-' }}</td>
                      <td class="px-6 py-4">
                         <span :class="getStatusClass(att.status)" class="text-[10px] px-2 py-0.5 rounded-full font-bold uppercase">
                           {{ att.status }}
                         </span>
                      </td>
                    </tr>
                  </tbody>
               </table>
             </div>
         </div>
      </div>

      <!-- Modals (Employee & Shift) -->
      <!-- (Integrated with salary fields) -->
      <div v-if="showEmployeeModal" class="fixed inset-0 z-50 flex items-center justify-center bg-black/80 backdrop-blur-sm p-4">
        <div class="bg-white dark:bg-neutral-900 rounded-3xl shadow-2xl w-full max-w-xl overflow-hidden border border-neutral-200 dark:border-neutral-800 animate-in fade-in zoom-in duration-300">
           <div class="p-8 border-b border-neutral-200 dark:border-neutral-800 flex justify-between items-center">
              <h3 class="text-2xl font-black text-neutral-900 dark:text-white uppercase tracking-tight">
                {{ editingEmployeeId ? 'Edit Staff Member' : 'Add Staff Member' }}
              </h3>
              <button @click="showEmployeeModal = false" class="text-neutral-400 hover:text-neutral-600 transition-colors">
                  <svg xmlns="http://www.w3.org/2000/svg" class="w-6 h-6" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M18 6 6 18"/><path d="m6 6 12 12"/></svg>
              </button>
           </div>
           <div class="p-8 space-y-6">
              <div class="grid grid-cols-2 gap-6">
                <div>
                  <label class="text-[10px] font-bold text-neutral-500 uppercase tracking-widest mb-2 block">Full Name</label>
                  <input type="text" v-model="newEmployee.fullName" class="input w-full" placeholder="e.g. Emma S." />
                </div>
                <div>
                  <label class="text-[10px] font-bold text-neutral-500 uppercase tracking-widest mb-2 block">Phone Number</label>
                  <input type="text" v-model="newEmployee.phone" class="input w-full" />
                </div>
              </div>
              <div class="grid grid-cols-2 gap-6">
                 <div>
                    <label class="text-[10px] font-bold text-neutral-500 uppercase tracking-widest mb-2 block">Position</label>
                    <select v-model="newEmployee.position" class="input w-full bg-neutral-50 dark:bg-neutral-800">
                      <option value="Barista">Barista</option>
                      <option value="Chef">Chef</option>
                      <option value="Manager">Manager</option>
                      <option value="Server">Server</option>
                    </select>
                 </div>
                 <div>
                    <label class="text-[10px] font-bold text-neutral-500 uppercase tracking-widest mb-2 block">Branch</label>
                    <select v-model="newEmployee.branchId" class="input w-full bg-neutral-50 dark:bg-neutral-800">
                      <option v-for="b in branches" :key="b.branchId" :value="b.branchId">{{ b.name }}</option>
                    </select>
                 </div>
              </div>

              <!-- Salary Info -->
              <div class="p-6 bg-primary-500/5 rounded-3xl border border-primary-500/10 space-y-4">
                 <h4 class="text-xs font-black text-primary-600 uppercase tracking-widest">Remuneration</h4>
                 <div class="grid grid-cols-2 gap-6">
                    <div>
                      <label class="text-[10px] font-bold text-neutral-500 uppercase tracking-widest mb-2 block">Base Salary ($)</label>
                      <input type="number" v-model="newEmployee.baseSalary" class="input w-full bg-white dark:bg-neutral-900" step="0.01" />
                    </div>
                    <div>
                      <label class="text-[10px] font-bold text-neutral-500 uppercase tracking-widest mb-2 block">Hourly Rate ($)</label>
                      <input type="number" v-model="newEmployee.hourlyRate" class="input w-full bg-white dark:bg-neutral-900" step="0.01" />
                    </div>
                 </div>
              </div>
           </div>
           <div class="p-8 bg-neutral-50 dark:bg-neutral-800/50 flex justify-end gap-4">
              <button @click="showEmployeeModal = false" class="btn-secondary px-8">Cancel</button>
              <button @click="createEmployee" class="btn-primary px-8">Confirm Staff</button>
           </div>
        </div>
      </div>

       <!-- Shift Modal -->
      <div v-if="showShiftModal" class="fixed inset-0 z-50 flex items-center justify-center bg-black/80 backdrop-blur-sm p-4">
        <div class="bg-white dark:bg-neutral-900 rounded-3xl shadow-2xl w-full max-w-lg overflow-hidden border border-neutral-200 dark:border-neutral-800 animate-in fade-in zoom-in duration-300">
           <div class="p-8 border-b border-neutral-200 dark:border-neutral-800 flex justify-between items-center bg-neutral-50/50 dark:bg-neutral-800/50">
              <h3 class="text-xl font-black text-neutral-900 dark:text-white uppercase tracking-tight">Assign New Shift</h3>
           </div>
           <div class="p-8 space-y-6">
              <div>
                <label class="text-[10px] font-bold text-neutral-500 uppercase tracking-widest mb-2 block">Select Employee</label>
                <select v-model="newShift.employeeId" class="input w-full">
                  <option v-for="emp in employees" :key="emp.employeeId" :value="emp.employeeId">{{ emp.fullName }}</option>
                </select>
              </div>
              <div class="grid grid-cols-2 gap-4">
                 <div>
                    <label class="text-[10px] font-bold text-neutral-500 uppercase tracking-widest mb-2 block">Start</label>
                    <input type="datetime-local" v-model="newShift.shiftStart" class="input w-full" />
                 </div>
                 <div>
                    <label class="text-[10px] font-bold text-neutral-500 uppercase tracking-widest mb-2 block">End</label>
                    <input type="datetime-local" v-model="newShift.shiftEnd" class="input w-full" />
                 </div>
              </div>
           </div>
           <div class="p-8 bg-neutral-50 dark:bg-neutral-800/50 flex justify-end gap-3">
              <button @click="showShiftModal = false" class="btn-secondary">Cancel</button>
              <button @click="createShift" class="btn-primary">Schedule Shift</button>
           </div>
        </div>
      </div>
    </div>
  </NuxtLayout>
</template>

<script setup lang="ts">
import { ref, reactive, watch, computed, onMounted } from 'vue'

definePageMeta({
  layout: false
})

const { get, post, del } = useApi()
const toast = useToast()

// Tabs
const activeTab = ref('overview')
const tabs = [
  { id: 'overview', name: 'Overview' },
  { id: 'employees', name: 'Employees' },
  { id: 'shifts', name: 'Schedule' },
  { id: 'attendance', name: 'Log' },
  { id: 'payroll', name: 'Payroll & Earnings' }
]

// Data State
const employees = ref<any[]>([])
const activeAttendance = ref<any[]>([])
const attendance = ref<any[]>([])
const shifts = ref<any[]>([])
const branches = ref<any[]>([])

// Payroll State
const payrollData = ref<any[]>([])
const loadingPayroll = ref(false)
const payrollDates = reactive({
  start: new Date(new Date().getFullYear(), new Date().getMonth(), 1).toISOString().split('T')[0],
  end: new Date().toISOString().split('T')[0]
})

// UI State
const loadingEmployees = ref(false)
const showEmployeeModal = ref(false)
const showShiftModal = ref(false)
const editingEmployeeId = ref<number | null>(null)

const newEmployee = reactive({
    fullName: '',
    phone: '',
    branchId: null as number | null,
    position: 'Barista',
    status: 'ACTIVE',
    baseSalary: 0,
    hourlyRate: 0
})

const newShift = reactive({
    employeeId: null as number | null,
    branchId: 1, // Default
    shiftStart: '',
    shiftEnd: ''
})

// -- Lifecycle --
onMounted(() => {
  fetchOverview()
})

watch(activeTab, (val) => {
  if (val === 'overview') fetchOverview()
  if (val === 'employees') fetchEmployees()
  if (val === 'shifts') fetchShifts()
  if (val === 'attendance') fetchAttendance()
  if (val === 'payroll') fetchPayroll()
})

// -- Fetch Logic --
const fetchOverview = async () => {
  try {
    const activeRes = await get<any[]>('/attendance/active')
    activeAttendance.value = activeRes || []
    
    // Also need all employees for stats
    if (employees.value.length === 0) fetchEmployees()
    
    // Fetch all attendance to count lates today
    const allAtt = await get<any[]>('/attendance')
    attendance.value = allAtt || []
  } catch (err) { console.error(err) }
}

const fetchEmployees = async () => {
  loadingEmployees.value = true
  try {
    const data = await get<any[]>('/employees')
    employees.value = data || []
    
    const branchData = await get<any[]>('/branches')
    branches.value = branchData || []
  } catch (err) { console.error(err) }
  finally { loadingEmployees.value = false }
}

const fetchShifts = async () => {
  try {
    const data = await get<any[]>('/shifts')
    shifts.value = data || []
  } catch (err) { console.error(err) }
}

const fetchAttendance = async () => {
  try {
    const data = await get<any[]>('/attendance')
    attendance.value = data || []
  } catch (err) { console.error(err) }
}

const fetchPayroll = async () => {
  loadingPayroll.value = true
  try {
    const data = await get<any[]>(`/payroll/report?startDate=${payrollDates.start}&endDate=${payrollDates.end}`)
    payrollData.value = data || []
  } catch (err) { console.error(err) }
  finally { loadingPayroll.value = false }
}

// -- Actions --
const createEmployee = async () => {
  try {
    if (editingEmployeeId.value) {
      await useApi().put(`/employees/${editingEmployeeId.value}`, newEmployee)
    } else {
      await post('/employees/add', newEmployee)
    }
    showEmployeeModal.value = false
    toast.success(editingEmployeeId.value ? 'Staff member updated' : 'New staff member added')
    fetchEmployees()
  } catch (err) { 
    toast.error('Failed to save employee') 
  }
}

const editEmployee = (emp: any) => {
    editingEmployeeId.value = emp.employeeId
    newEmployee.fullName = emp.fullName
    newEmployee.phone = emp.phone
    newEmployee.position = emp.position
    newEmployee.branchId = emp.branch?.branchId || null
    newEmployee.status = emp.status
    newEmployee.baseSalary = emp.baseSalary
    newEmployee.hourlyRate = emp.hourlyRate
    showEmployeeModal.value = true
}

const createShift = async () => {
     try {
        await post('/shifts', newShift)
        showShiftModal.value = false
        toast.success('Shift scheduled successfully')
        fetchShifts()
    } catch (err) { toast.error('Failed to create shift') }
}

const deleteShift = async (id: number) => {
  if (!confirm('Delete shift?')) return
  try {
    await del(`/shifts/${id}`)
    toast.success('Shift deleted')
    fetchShifts()
  } catch (err) { 
    toast.error('Failed to delete shift')
  }
}

// -- Computed --
const lateTodayCount = computed(() => {
  const today = new Date().toISOString().split('T')[0]
  return attendance.value.filter(a => a.status === 'LATE' && a.checkIn.startsWith(today)).length
})

const totalPayrollPayout = computed(() => payrollData.value.reduce((acc, p) => acc + p.totalEarnings, 0))
const totalPayrollHours = computed(() => payrollData.value.reduce((acc, p) => acc + p.totalHoursWorked, 0))

// -- Helpers --
const getInitials = (name: string) => name ? name.split(' ').map(n => n[0]).join('').substring(0, 2).toUpperCase() : '??'
const formatTime = (dateStr: string) => new Date(dateStr).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
const formatDate = (dateStr: string) => new Date(dateStr).toLocaleDateString([], { month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' })

const calculateDuration = (checkIn: string) => {
    const start = new Date(checkIn).getTime()
    const now = new Date().getTime()
    const diff = now - start
    const hours = Math.floor(diff / 3600000)
    const minutes = Math.floor((diff % 3600000) / 60000)
    return `${hours}h ${minutes}m`
}

const getStatusClass = (status: string) => {
  const classes: any = {
    'PRESENT': 'bg-success-100 text-success-700 dark:bg-success-900/30 dark:text-success-400',
    'LATE': 'bg-warning-100 text-warning-700 dark:bg-warning-900/30 dark:text-warning-400',
  }
  return classes[status] || 'bg-neutral-100 text-neutral-500'
}

const openEmployeeModal = () => { 
    editingEmployeeId.value = null
    newEmployee.fullName = ''
    newEmployee.phone = ''
    newEmployee.position = 'Barista'
    newEmployee.branchId = branches.value.length > 0 ? branches.value[0].branchId : null
    newEmployee.status = 'ACTIVE'
    newEmployee.baseSalary = 0
    newEmployee.hourlyRate = 0
    showEmployeeModal.value = true 
}
const openShiftModal = () => { showShiftModal.value = true }
</script>
