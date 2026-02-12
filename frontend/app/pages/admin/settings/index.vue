<template>
  <NuxtLayout name="admin">
    <div class="space-y-6">
      <UiBreadcrumb :items="[{ label: 'Settings' }]" />

      <!-- Header -->
      <div class="flex items-center justify-between">
        <div>
          <h2 class="text-2xl font-bold text-neutral-900 dark:text-white">Settings & Access Control</h2>
          <p class="text-neutral-500 text-sm">Manage user accounts, system roles, and permissions.</p>
        </div>
      </div>

      <!-- Tabs -->
      <div class="flex border-b border-neutral-200 dark:border-neutral-800">
        <button 
          v-for="tab in tabs" 
          :key="tab.id"
          @click="activeTab = tab.id"
          :class="[
            'px-6 py-3 text-sm font-medium transition-all relative',
            activeTab === tab.id 
              ? 'text-primary-600 dark:text-primary-400' 
              : 'text-neutral-500 hover:text-neutral-700 dark:hover:text-neutral-300'
          ]"
        >
          {{ tab.name }}
          <div v-if="activeTab === tab.id" class="absolute bottom-0 left-0 right-0 h-0.5 bg-primary-600 dark:bg-primary-400"></div>
        </button>
      </div>

      <!-- Tab Content: Users -->
      <div v-if="activeTab === 'users'" class="space-y-4">
        <div class="flex justify-between items-center">
          <div class="text-sm text-neutral-500">Total Users: {{ users.length }}</div>
          <button 
            @click="openUserModal()"
            class="btn-primary py-2 px-4 text-xs font-bold uppercase tracking-wider flex items-center gap-2"
          >
            <svg xmlns="http://www.w3.org/2000/svg" class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><line x1="19" y1="8" x2="19" y2="14"/><line x1="16" y1="11" x2="22" y2="11"/></svg>
            Add User
          </button>
        </div>

        <div class="bg-white dark:bg-neutral-900 border border-neutral-200 dark:border-neutral-800 rounded-2xl overflow-hidden shadow-sm">
          <table class="w-full text-left">
            <thead>
              <tr class="bg-neutral-50 dark:bg-neutral-800/50 border-b border-neutral-200 dark:border-neutral-800">
                <th class="px-6 py-4 text-xs font-bold text-neutral-500 uppercase">Username</th>
                <th class="px-6 py-4 text-xs font-bold text-neutral-500 uppercase">Linked Employee</th>
                <th class="px-6 py-4 text-xs font-bold text-neutral-500 uppercase">Role</th>
                <th class="px-6 py-4 text-xs font-bold text-neutral-500 uppercase text-right">Actions</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-neutral-100 dark:divide-neutral-800">
              <template v-if="loading">
                <tr v-for="i in 5" :key="i" class="animate-pulse">
                  <td v-for="j in 4" :key="j" class="px-6 py-4"><div class="h-4 bg-neutral-100 dark:bg-neutral-800 rounded"></div></td>
                </tr>
              </template>
              <template v-else>
                <tr v-for="user in users" :key="user.userId" class="hover:bg-neutral-50 dark:hover:bg-neutral-800/50 transition-colors">
                <td class="px-6 py-4">
                  <div class="flex items-center gap-3">
                    <div class="w-8 h-8 rounded-full bg-neutral-100 dark:bg-neutral-800 flex items-center justify-center text-sm font-bold text-neutral-600 dark:text-neutral-400">
                      {{ user.userName.charAt(0).toUpperCase() }}
                    </div>
                    <span class="font-medium text-neutral-900 dark:text-white">{{ user.userName }}</span>
                  </div>
                </td>
                <td class="px-6 py-4">
                  <span v-if="user.employee" class="text-neutral-600 dark:text-neutral-400">{{ user.employee.fullName }}</span>
                  <span v-else class="text-neutral-400 italic">No Employee linked</span>
                </td>
                <td class="px-6 py-4">
                  <span class="px-2 py-1 rounded-lg text-[10px] font-bold uppercase bg-primary-100 text-primary-700 dark:bg-primary-900/30 dark:text-primary-400">
                    {{ user.role?.roleName || 'No Role' }}
                  </span>
                </td>
                <td class="px-6 py-4 text-right">
                  <div class="flex items-center justify-end gap-2">
                    <button @click="openUserModal(user)" class="p-2 text-neutral-400 hover:text-primary-600 dark:hover:text-primary-400 transition-colors">
                      <svg xmlns="http://www.w3.org/2000/svg" class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M17 3a2.828 2.828 0 1 1 4 4L7.5 20.5 2 22l1.5-5.5L17 3z"/></svg>
                    </button>
                    <button @click="deleteUser(user.userId)" class="p-2 text-neutral-400 hover:text-error-600 transition-colors">
                      <svg xmlns="http://www.w3.org/2000/svg" class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M3 6h18m-2 0v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/></svg>
                    </button>
                  </div>
                </td>
              </tr>
              </template>
            </tbody>
          </table>
        </div>
      </div>

      <!-- Tab Content: Roles -->
      <div v-if="activeTab === 'roles'" class="space-y-4">
        <div class="flex justify-between items-center">
          <div class="text-sm text-neutral-500">Roles: {{ roles.length }}</div>
          <button 
            @click="openRoleModal()"
            class="btn-primary py-2 px-4 text-xs font-bold uppercase tracking-wider flex items-center gap-2"
          >
            <svg xmlns="http://www.w3.org/2000/svg" class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="3"/><path d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 0 1 0 2.83 2 2 0 0 1-2.83 0l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-2 2 2 2 0 0 1-2-2v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 0 1-2.83 0 2 2 0 0 1 0-2.83l.06-.06a1.65 1.65 0 0 0 .33-1.82 1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1-2-2 2 2 0 0 1 2-2h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 0 1 0-2.83 2 2 0 0 1 2.83 0l.06.06a1.65 1.65 0 0 0 1.82.33H9a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 2-2 2 2 0 0 1 2 2v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 0 1 2.83 0 2 2 0 0 1 0 2.83l-.06.06a1.65 1.65 0 0 0-.33 1.82V9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 2 2 2 2 0 0 1-2 2h-.09a1.65 1.65 0 0 0-1.51 1z"/></svg>
            New Role
          </button>
        </div>

        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          <div v-for="role in roles" :key="role.roleId" class="bg-white dark:bg-neutral-900 border border-neutral-200 dark:border-neutral-800 rounded-2xl p-6 shadow-sm hover:shadow-md transition-shadow">
            <div class="flex items-center justify-between mb-4">
              <h4 class="text-lg font-bold text-neutral-900 dark:text-white">{{ role.roleName }}</h4>
              <div class="flex gap-1">
                 <button @click="openRoleModal(role)" class="p-1.5 text-neutral-400 hover:text-primary-600 transition-colors">
                    <svg xmlns="http://www.w3.org/2000/svg" class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M17 3a2.828 2.828 0 1 1 4 4L7.5 20.5 2 22l1.5-5.5L17 3z"/></svg>
                 </button>
                 <button @click="deleteRole(role.roleId)" class="p-1.5 text-neutral-400 hover:text-error-600 transition-colors">
                    <svg xmlns="http://www.w3.org/2000/svg" class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M3 6h18m-2 0v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/></svg>
                 </button>
            </div>
            </div>
            <p class="text-sm text-neutral-500 line-clamp-2 mb-4">{{ role.description || 'No description provided.' }}</p>
            <div class="pt-4 border-t border-neutral-100 dark:border-neutral-800 flex items-center justify-between">
              <span class="text-xs font-bold text-neutral-400 tracking-widest uppercase">Permissions</span>
              <span class="text-xs font-bold text-primary-500">
                 {{ role.permissions && role.permissions.length > 0 ? role.permissions.length + ' active' : 'None' }}
              </span>
            </div>
          </div>
        </div>
      </div>

      <!-- Tab Content: System Config -->
      <div v-if="activeTab === 'config'" class="space-y-6">
         <div class="bg-white dark:bg-neutral-900 border border-neutral-200 dark:border-neutral-800 rounded-2xl p-6 shadow-sm">
             <div class="flex justify-between items-center mb-6">
                  <h3 class="text-lg font-bold text-neutral-900 dark:text-white">General Configuration</h3>
                  <button 
                    @click="saveSettings" 
                    class="btn-primary py-2 px-4 text-xs font-bold uppercase tracking-wider flex items-center gap-2"
                    :disabled="saving"
                  >
                    <svg xmlns="http://www.w3.org/2000/svg" class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M19 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11l5 5v11a2 2 0 0 1-2 2z"></path><polyline points="17 21 17 13 7 13 7 21"></polyline><polyline points="7 3 7 8 15 8"></polyline></svg>
                    Save Changes
                  </button>
             </div>
             
             <div v-if="settings.length === 0" class="text-center py-8 text-neutral-500">
                 No settings found.
             </div>

             <div v-else class="grid grid-cols-1 md:grid-cols-2 gap-6">
                 <div v-for="setting in settings" :key="setting.id">
                     <label class="block text-xs font-bold text-neutral-500 uppercase tracking-widest mb-2" :title="setting.key">{{ setting.description || setting.key }}</label>
                     <input 
                         v-if="setting.key !== 'THEME'"
                         type="text" 
                         v-model="setting.value"
                         class="w-full bg-neutral-50 dark:bg-neutral-800 border-none rounded-xl px-4 py-3 text-sm ring-1 ring-neutral-200 dark:ring-neutral-700 focus:ring-2 focus:ring-primary-500"
                     >
                     <select 
                         v-else
                         v-model="setting.value"
                         class="w-full bg-neutral-50 dark:bg-neutral-800 border-none rounded-xl px-4 py-3 text-sm ring-1 ring-neutral-200 dark:ring-neutral-700 focus:ring-2 focus:ring-primary-500"
                     >
                         <option value="LIGHT">Light</option>
                         <option value="DARK">Dark</option>
                     </select>
                 </div>
             </div>
         </div>
      </div>

      <!-- Tab Content: Loyalty Program -->
      <div v-if="activeTab === 'loyalty'" class="space-y-6">
        <div class="bg-white dark:bg-neutral-900 border border-neutral-200 dark:border-neutral-800 rounded-2xl p-6 shadow-sm">
           <div class="flex items-center gap-4 mb-6 pb-6 border-b border-neutral-200 dark:border-neutral-800">
              <div class="w-12 h-12 rounded-xl bg-amber-100 dark:bg-amber-900/30 flex items-center justify-center">
                 <svg xmlns="http://www.w3.org/2000/svg" class="w-6 h-6 text-amber-600" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"/></svg>
              </div>
              <div>
                 <h3 class="text-lg font-bold text-neutral-900 dark:text-white">Loyalty Rewards Program</h3>
                 <p class="text-sm text-neutral-500">Configure how points are earned and redeemed</p>
              </div>
           </div>

           <div class="grid grid-cols-1 md:grid-cols-2 gap-8 mb-8">
              <!-- Point Calculation -->
              <div class="space-y-4">
                 <h4 class="text-sm font-bold text-neutral-900 dark:text-white flex items-center gap-2">
                    <svg xmlns="http://www.w3.org/2000/svg" class="w-4 h-4 text-primary-500" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 1v22M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6"/></svg>
                    Earning & Redemption
                 </h4>
                 
                 <div class="space-y-4 p-4 bg-neutral-50 dark:bg-neutral-800/50 rounded-xl">
                    <div>
                       <label class="block text-xs font-bold text-neutral-500 uppercase tracking-widest mb-2">Point Earn Rate</label>
                       <div class="flex items-center gap-3">
                          <input 
                              v-model="loyaltyConfig.earnRate" 
                              type="number" 
                              step="0.1"
                              class="w-full bg-white dark:bg-neutral-800 border-none rounded-xl px-4 py-3 text-sm ring-1 ring-neutral-200 dark:ring-neutral-700 focus:ring-2 focus:ring-primary-500"
                          >
                          <span class="text-sm text-neutral-500 whitespace-nowrap">Points per $1</span>
                       </div>
                       <p class="text-[10px] text-neutral-400 mt-1 italic">Example: 1 means $10 spent = 10 points</p>
                    </div>

                    <div>
                       <label class="block text-xs font-bold text-neutral-500 uppercase tracking-widest mb-2">Point Value ($)</label>
                       <div class="flex items-center gap-3">
                          <input 
                              v-model="loyaltyConfig.redeemRate" 
                              type="number" 
                              step="0.01"
                              class="w-full bg-white dark:bg-neutral-800 border-none rounded-xl px-4 py-3 text-sm ring-1 ring-neutral-200 dark:ring-neutral-700 focus:ring-2 focus:ring-primary-500"
                          >
                          <span class="text-sm text-neutral-500 whitespace-nowrap">Discount per point</span>
                       </div>
                       <p class="text-[10px] text-neutral-400 mt-1 italic">Example: 0.1 means 10 points = $1.00 discount</p>
                    </div>
                 </div>
              </div>

              <!-- Membership Tiers -->
              <div class="space-y-4">
                 <h4 class="text-sm font-bold text-neutral-900 dark:text-white flex items-center gap-2">
                    <svg xmlns="http://www.w3.org/2000/svg" class="w-4 h-4 text-amber-500" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M6 9H4.5a2.5 2.5 0 0 1 0-5H6M18 9h1.5a2.5 2.5 0 0 0 0-5H18M4 22h16M10 14.66V17c0 .55.45 1 1 1h2c.55 0 1-.45 1-1v-2.34M12 11c1.33 0 2.3-1.63 1.4-2.8a1.3 1.3 0 0 0-1.4-.2 1.3 1.3 0 0 0-1.4.2c-.9 1.17.07 2.8 1.4 2.8z"/><path d="M10 2c0 .55.45 1 1 1h2c.55 0 1-.45 1-1V1c0-.55-.45-1-1-1h-2c-.55 0-1 .45-1 1v1z"/><path d="M5.5 10c.28 0 .5.22.5.5V14c0 1.1.9 2 2 2h8c1.1 0 2-.9 2-2v-3.5c0-.28.22-.5.5-.5h.5c.55 0 1-.45 1-1V5c0-.55-.45-1-1-1h-15c-.55 0-1 .45-1 1v4c0 .55.45 1 1 1h.5z"/></svg>
                    Membership Tiers
                 </h4>
                 
                 <div class="space-y-4 p-4 bg-neutral-50 dark:bg-neutral-800/50 rounded-xl">
                    <div>
                       <label class="block text-xs font-bold text-neutral-500 uppercase tracking-widest mb-2">Silver Tier Threshold</label>
                       <div class="flex items-center gap-3">
                          <input 
                              v-model="loyaltyConfig.silverThreshold" 
                              type="number" 
                              class="w-full bg-white dark:bg-neutral-800 border-none rounded-xl px-4 py-3 text-sm ring-1 ring-neutral-200 dark:ring-neutral-700 focus:ring-2 focus:ring-primary-500"
                          >
                          <span class="text-sm text-neutral-500 whitespace-nowrap">Points</span>
                       </div>
                    </div>

                    <div>
                       <label class="block text-xs font-bold text-neutral-500 uppercase tracking-widest mb-2">Gold Tier Threshold</label>
                       <div class="flex items-center gap-3">
                          <input 
                              v-model="loyaltyConfig.goldThreshold" 
                              type="number" 
                              class="w-full bg-white dark:bg-neutral-800 border-none rounded-xl px-4 py-3 text-sm ring-1 ring-neutral-200 dark:ring-neutral-700 focus:ring-2 focus:ring-primary-500"
                          >
                          <span class="text-sm text-neutral-500 whitespace-nowrap">Points</span>
                       </div>
                    </div>
                 </div>
              </div>
           </div>

           <div class="flex justify-end p-6 border-t border-neutral-100 dark:border-neutral-800 -mx-6 -mb-6">
              <button 
                 @click="saveLoyaltyConfig" 
                 :disabled="savingLoyalty"
                 class="btn-primary py-3 px-8 text-xs font-bold uppercase tracking-widest flex items-center gap-2 shadow-xl shadow-primary-500/20"
              >
                 <div v-if="savingLoyalty" class="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin"></div>
                 <svg v-else xmlns="http://www.w3.org/2000/svg" class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M19 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11l5 5v11a2 2 0 0 1-2 2z"></path><polyline points="17 21 17 13 7 13 7 21"></polyline><polyline points="7 3 7 8 15 8"></polyline></svg>
                 Apply Settings
              </button>
           </div>
        </div>
      </div>

      <!-- Tab Content: Notifications -->
      <div v-if="activeTab === 'notifications'" class="space-y-6">
         <div class="bg-white dark:bg-neutral-900 border border-neutral-200 dark:border-neutral-800 rounded-2xl p-6 shadow-sm">
             <div class="flex items-center gap-4 mb-6 pb-6 border-b border-neutral-200 dark:border-neutral-800">
                <div class="w-12 h-12 rounded-xl bg-[#0088cc]/10 flex items-center justify-center">
                   <svg xmlns="http://www.w3.org/2000/svg" class="w-6 h-6 text-[#0088cc]" viewBox="0 0 24 24" fill="currentColor"><path d="m20.665 3.717-17.73 6.837c-1.21.486-1.203 1.161-.222 1.462l4.552 1.42 10.532-6.645c.498-.303.953-.14.579.192l-8.533 7.701h-.002l.002.001-.314 4.692c.46 0 .663-.211.921-.46l2.211-2.15 4.599 3.397c.848.467 1.457.227 1.668-.787l3.019-14.228c.309-1.239-.473-1.8-1.282-1.432z"/></svg>
                </div>
                <div>
                   <h3 class="text-lg font-bold text-neutral-900 dark:text-white">Telegram Notifications</h3>
                   <p class="text-sm text-neutral-500">Receive alerts via Telegram Bot</p>
                </div>
             </div>
             
             <div class="grid grid-cols-1 md:grid-cols-2 gap-6 mb-6">
                <div>
                   <label class="block text-xs font-bold text-neutral-500 uppercase tracking-widest mb-2">Bot Token</label>
                   <input 
                       v-model="telegramConfig.botToken" 
                       type="password" 
                       placeholder="123456789:ABCdefGHIjklMNOpqrsTUVwxyz"
                       class="w-full bg-neutral-50 dark:bg-neutral-800 border-none rounded-xl px-4 py-3 text-sm ring-1 ring-neutral-200 dark:ring-neutral-700 focus:ring-2 focus:ring-primary-500 font-mono"
                   >
                   <p class="text-[10px] text-neutral-400 mt-1">Get from @BotFather on Telegram</p>
                </div>
                <div>
                   <label class="block text-xs font-bold text-neutral-500 uppercase tracking-widest mb-2">Chat ID</label>
                   <input 
                       v-model="telegramConfig.chatId" 
                       type="text" 
                       placeholder="-1001234567890"
                       class="w-full bg-neutral-50 dark:bg-neutral-800 border-none rounded-xl px-4 py-3 text-sm ring-1 ring-neutral-200 dark:ring-neutral-700 focus:ring-2 focus:ring-primary-500 font-mono"
                   >
                   <p class="text-[10px] text-neutral-400 mt-1">Your user or group chat ID</p>
                </div>
             </div>

             <div class="flex gap-3 mb-8">
                <button 
                   @click="saveTelegramConfig" 
                   :disabled="savingTelegram"
                   class="btn-primary py-2 px-4 text-xs font-bold uppercase tracking-wider"
                >
                   Save Configuration
                </button>
                <button 
                   @click="testTelegram" 
                   :disabled="testingTelegram"
                   class="py-2 px-4 text-xs font-bold uppercase tracking-wider bg-[#0088cc] hover:bg-[#0077b5] text-white rounded-2xl transition-colors flex items-center gap-2"
                >
                   <div v-if="testingTelegram" class="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin"></div>
                   <svg v-else xmlns="http://www.w3.org/2000/svg" class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M22 2 11 13"/><path d="m22 2-7 20-4-9-9-4 20-7z"/></svg>
                   Test Connection
                </button>
             </div>

             <h4 class="text-sm font-bold text-neutral-700 dark:text-neutral-300 mb-4">Alert Types</h4>
             <div class="space-y-4">
                <div class="flex items-center justify-between p-4 bg-neutral-50 dark:bg-neutral-800 rounded-xl">
                   <div>
                      <p class="font-medium text-neutral-900 dark:text-white">Low Stock Alerts</p>
                      <p class="text-xs text-neutral-500">Notify when ingredients are below reorder level</p>
                   </div>
                   <label class="relative inline-flex items-center cursor-pointer">
                      <input type="checkbox" v-model="telegramConfig.lowStock" class="sr-only peer">
                      <div class="w-11 h-6 bg-neutral-200 peer-focus:outline-none peer-focus:ring-4 peer-focus:ring-primary-300 dark:peer-focus:ring-primary-800 rounded-full peer dark:bg-neutral-700 peer-checked:after:translate-x-full rtl:peer-checked:after:-translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:start-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all dark:border-gray-600 peer-checked:bg-primary-600"></div>
                   </label>
                </div>

                <div class="flex items-center justify-between p-4 bg-neutral-50 dark:bg-neutral-800 rounded-xl">
                   <div class="flex-1">
                      <p class="font-medium text-neutral-900 dark:text-white">Large Order Alerts</p>
                      <p class="text-xs text-neutral-500">Notify for orders above threshold</p>
                   </div>
                   <div class="flex items-center gap-3">
                      <div class="flex items-center gap-1">
                         <span class="text-sm text-neutral-500">$</span>
                         <input 
                            v-model="telegramConfig.largeOrderThreshold" 
                            type="number" 
                            class="w-20 bg-white dark:bg-neutral-700 border-none rounded-lg px-2 py-1 text-sm text-center ring-1 ring-neutral-200 dark:ring-neutral-600"
                         >
                      </div>
                      <label class="relative inline-flex items-center cursor-pointer">
                         <input type="checkbox" v-model="telegramConfig.largeOrder" class="sr-only peer">
                         <div class="w-11 h-6 bg-neutral-200 peer-focus:outline-none rounded-full peer dark:bg-neutral-700 peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:start-[2px] after:bg-white after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-primary-600"></div>
                      </label>
                   </div>
                </div>

                <div class="flex items-center justify-between p-4 bg-neutral-50 dark:bg-neutral-800 rounded-xl">
                   <div>
                      <p class="font-medium text-neutral-900 dark:text-white">Shift Discrepancy Alerts</p>
                      <p class="text-xs text-neutral-500">Notify when cash doesn't match expected amount</p>
                   </div>
                   <label class="relative inline-flex items-center cursor-pointer">
                      <input type="checkbox" v-model="telegramConfig.shiftDiscrepancy" class="sr-only peer">
                      <div class="w-11 h-6 bg-neutral-200 peer-focus:outline-none rounded-full peer dark:bg-neutral-700 peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:start-[2px] after:bg-white after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-primary-600"></div>
                   </label>
                </div>
             </div>
         </div>
      </div>

      <!-- User Modal -->
      <div v-show="showUserModal" class="fixed inset-0 z-50 flex items-center justify-center bg-black/50 backdrop-blur-sm p-4">
        <div class="bg-white dark:bg-neutral-900 rounded-3xl shadow-2xl w-full max-w-md overflow-hidden border border-neutral-200 dark:border-neutral-800">
          <div class="p-8 border-b border-neutral-200 dark:border-neutral-800 flex justify-between items-center bg-neutral-50/50 dark:bg-neutral-800/50">
            <h3 class="text-xl font-bold text-neutral-900 dark:text-white">{{ editingUser ? 'Edit User Account' : 'New User Account' }}</h3>
            <button @click="showUserModal = false" class="text-neutral-400 hover:text-neutral-600 dark:hover:text-white transition-colors">
              <svg xmlns="http://www.w3.org/2000/svg" class="w-6 h-6" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
            </button>
          </div>
          
          <form @submit.prevent="saveUser" class="p-8 space-y-6">
            <div class="space-y-4">
              <div>
                <label class="block text-xs font-bold text-neutral-500 uppercase tracking-widest mb-2">Username</label>
                <input 
                  v-model="userForm.userName" 
                  type="text" 
                  required
                  class="w-full bg-neutral-50 dark:bg-neutral-800 border-none rounded-2xl px-5 py-4 text-sm ring-1 ring-neutral-200 dark:ring-neutral-700 focus:ring-2 focus:ring-primary-500 placeholder-neutral-400"
                  placeholder="Enter username"
                >
              </div>

              <div>
                <label class="block text-xs font-bold text-neutral-500 uppercase tracking-widest mb-2">Password</label>
                <input 
                  v-model="userForm.password" 
                  type="password" 
                  :required="!editingUser"
                  class="w-full bg-neutral-50 dark:bg-neutral-800 border-none rounded-2xl px-5 py-4 text-sm ring-1 ring-neutral-200 dark:ring-neutral-700 focus:ring-2 focus:ring-primary-500 placeholder-neutral-400"
                  :placeholder="editingUser ? 'Leave blank to keep current' : 'Enter password'"
                >
              </div>

              <div class="grid grid-cols-1 gap-4">
                <div>
                  <label class="block text-xs font-bold text-neutral-500 uppercase tracking-widest mb-2">Link Employee</label>
                  <select 
                    v-model="userForm.employeeId" 
                    required
                    class="w-full bg-neutral-50 dark:bg-neutral-800 border-none rounded-2xl px-5 py-4 text-sm ring-1 ring-neutral-200 dark:ring-neutral-700 focus:ring-2 focus:ring-primary-500"
                  >
                    <option :value="null" disabled>Select an employee</option>
                    <option v-for="emp in employees" :key="emp.employeeId" :value="emp.employeeId">
                      {{ emp.fullName }} ({{ emp.position }})
                    </option>
                  </select>
                </div>

                <div>
                  <label class="block text-xs font-bold text-neutral-500 uppercase tracking-widest mb-2">Assign Role</label>
                  <select 
                    v-model="userForm.roleId" 
                    required
                    class="w-full bg-neutral-50 dark:bg-neutral-800 border-none rounded-2xl px-5 py-4 text-sm ring-1 ring-neutral-200 dark:ring-neutral-700 focus:ring-2 focus:ring-primary-500"
                  >
                    <option :value="null" disabled>Select a role</option>
                    <option v-for="role in roles" :key="role.roleId" :value="role.roleId">
                      {{ role.roleName }}
                    </option>
                  </select>
                </div>
              </div>
            </div>

            <div class="flex gap-3 pt-4">
              <button 
                type="button" 
                @click="showUserModal = false"
                class="flex-1 py-4 text-sm font-bold text-neutral-500 hover:text-neutral-800 dark:hover:text-white transition-colors"
              >
                Cancel
              </button>
              <button 
                type="submit" 
                class="flex-1 btn-primary py-4 rounded-2xl font-bold uppercase tracking-widest shadow-xl shadow-primary-500/20"
                :disabled="saving"
              >
                {{ editingUser ? 'Update User' : 'Create User' }}
              </button>
            </div>
          </form>
        </div>
      </div>

      <!-- Role Modal -->
      <div v-show="showRoleModal" class="fixed inset-0 z-50 flex items-center justify-center bg-black/50 backdrop-blur-sm p-4">
        <div class="bg-white dark:bg-neutral-900 rounded-3xl shadow-2xl w-full max-w-md overflow-hidden border border-neutral-200 dark:border-neutral-800">
          <div class="p-8 border-b border-neutral-200 dark:border-neutral-800 flex justify-between items-center">
            <h3 class="text-xl font-bold text-neutral-900 dark:text-white">{{ editingRole ? 'Edit Role' : 'New System Role' }}</h3>
            <button @click="showRoleModal = false" class="text-neutral-400 hover:text-neutral-600">
              <svg xmlns="http://www.w3.org/2000/svg" class="w-6 h-6" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
            </button>
          </div>
          
          <form @submit.prevent="saveRole" class="p-8 space-y-6">
            <div class="space-y-4">
              <div>
                <label class="block text-xs font-bold text-neutral-500 uppercase tracking-widest mb-2">Role Name</label>
                <input 
                  v-model="roleForm.roleName" 
                  type="text" 
                  required
                  class="w-full bg-neutral-50 dark:bg-neutral-800 border-none rounded-2xl px-5 py-4 text-sm ring-1 ring-neutral-200 dark:ring-neutral-700 focus:ring-2 focus:ring-primary-500 placeholder-neutral-400"
                  placeholder="e.g. Store Manager"
                >
              </div>

              <div>
                <label class="block text-xs font-bold text-neutral-500 uppercase tracking-widest mb-2">Description</label>
                <textarea 
                  v-model="roleForm.description" 
                  rows="4"
                  class="w-full bg-neutral-50 dark:bg-neutral-800 border-none rounded-2xl px-5 py-4 text-sm ring-1 ring-neutral-200 dark:ring-neutral-700 focus:ring-2 focus:ring-primary-500 placeholder-neutral-400"
                  placeholder="Briefly describe the responsibilities of this role"
                ></textarea>
              </div>

              <div>
                   <label class="block text-xs font-bold text-neutral-500 uppercase tracking-widest mb-2">Permissions</label>
                   <div class="space-y-2 max-h-48 overflow-y-auto pr-2 scrollbar-thin">
                       <div v-for="perm in permissions" :key="perm.permissionId" class="flex items-center gap-3 p-3 rounded-xl bg-neutral-50 dark:bg-neutral-800 border border-neutral-100 dark:border-neutral-700">
                           <input 
                               type="checkbox" 
                               :id="'perm-' + perm.permissionId" 
                               :value="perm.permissionId"
                               v-model="roleForm.permissionIds"
                               class="w-5 h-5 rounded text-primary-600 focus:ring-primary-500 border-gray-300 dark:border-gray-600 dark:bg-gray-700"
                           >
                           <label :for="'perm-' + perm.permissionId" class="text-sm text-neutral-700 dark:text-neutral-300 select-none cursor-pointer flex-1">
                               <div class="font-medium">{{ perm.code }}</div>
                               <div class="text-xs text-neutral-500">{{ perm.description }}</div>
                           </label>
                       </div>
                   </div>
              </div>
            </div>

            <div class="flex gap-3 pt-4">
              <button 
                type="button" 
                @click="showRoleModal = false"
                class="flex-1 py-4 text-sm font-bold text-neutral-500 hover:text-neutral-800 dark:hover:text-white transition-colors"
              >
                Cancel
              </button>
              <button 
                type="submit" 
                class="flex-1 btn-primary py-4 rounded-2xl font-bold uppercase tracking-widest shadow-xl shadow-primary-500/20"
                :disabled="saving"
              >
                {{ editingRole ? 'Update Role' : 'Create Role' }}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </NuxtLayout>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'

definePageMeta({
  layout: false
})

const { get, post, put, del } = useApi()
const toast = useToast()

// Tabs
const tabs = [
  { id: 'users', name: 'User Accounts' },
  { id: 'roles', name: 'Roles & Access' },
  { id: 'loyalty', name: 'Loyalty Program' },
  { id: 'notifications', name: 'Notifications' },
  { id: 'config', name: 'System Config' }
]
const activeTab = ref('users')

// Data
const users = ref<any[]>([])
const roles = ref<any[]>([])
const employees = ref<any[]>([])
const permissions = ref<any[]>([])
const settings = ref<any[]>([])
const loading = ref(true)
const saving = ref(false)

// Modals & Forms
const showUserModal = ref(false)
const showRoleModal = ref(false)
const editingUser = ref<any>(null)
const editingRole = ref<any>(null)

const userForm = reactive({
  userName: '',
  password: '',
  employeeId: null as number | null,
  roleId: null as number | null,
  isActive: true
})

const roleForm = reactive({
  roleName: '',
  description: '',
  permissionIds: [] as number[]
})

// Telegram Configuration
const telegramConfig = reactive({
  botToken: '',
  chatId: '',
  lowStock: false,
  largeOrder: false,
  largeOrderThreshold: 100,
  shiftDiscrepancy: false
})

// Loyalty Configuration
const loyaltyConfig = reactive({
  earnRate: 1.0,
  redeemRate: 0.1,
  silverThreshold: 300,
  goldThreshold: 1000
})
const savingLoyalty = ref(false)
const savingTelegram = ref(false)
const testingTelegram = ref(false)

const loadTelegramConfig = () => {
  // Load from settings array
  const findSetting = (key: string) => settings.value.find((s: any) => s.key === key)?.value || ''
  telegramConfig.botToken = findSetting('TELEGRAM_BOT_TOKEN')
  telegramConfig.chatId = findSetting('TELEGRAM_CHAT_ID')
  telegramConfig.lowStock = findSetting('NOTIFY_LOW_STOCK') === 'true'
  telegramConfig.largeOrder = findSetting('NOTIFY_LARGE_ORDER') === 'true'
  telegramConfig.largeOrderThreshold = parseInt(findSetting('LARGE_ORDER_THRESHOLD')) || 100
  telegramConfig.shiftDiscrepancy = findSetting('NOTIFY_SHIFT_DISCREPANCY') === 'true'

  // Loyalty
  loyaltyConfig.earnRate = parseFloat(findSetting('LOYALTY_EARN_RATE')) || 1.0
  loyaltyConfig.redeemRate = parseFloat(findSetting('LOYALTY_REDEEM_RATE')) || 0.1
  loyaltyConfig.silverThreshold = parseInt(findSetting('LOYALTY_SILVER_THRESHOLD')) || 300
  loyaltyConfig.goldThreshold = parseInt(findSetting('LOYALTY_GOLD_THRESHOLD')) || 1000
}

const saveTelegramConfig = async () => {
  savingTelegram.value = true
  try {
    const payload: Record<string, string> = {
      'TELEGRAM_BOT_TOKEN': telegramConfig.botToken,
      'TELEGRAM_CHAT_ID': telegramConfig.chatId,
      'NOTIFY_LOW_STOCK': telegramConfig.lowStock ? 'true' : 'false',
      'NOTIFY_LARGE_ORDER': telegramConfig.largeOrder ? 'true' : 'false',
      'LARGE_ORDER_THRESHOLD': telegramConfig.largeOrderThreshold.toString(),
      'NOTIFY_SHIFT_DISCREPANCY': telegramConfig.shiftDiscrepancy ? 'true' : 'false'
    }
    await post('/settings/batch', payload)
    toast.success('Telegram configuration saved!')
  } catch (err) {
    toast.error('Failed to save Telegram config')
  } finally {
    savingTelegram.value = false
  }
}

const testTelegram = async () => {
  testingTelegram.value = true
  try {
    await post('/notifications/test', {})
    toast.success('Test notification sent! Check your Telegram.')
  } catch (err) {
    toast.error('Failed to send test notification. Check your configuration.')
  } finally {
    testingTelegram.value = false
  }
}

const saveLoyaltyConfig = async () => {
  savingLoyalty.value = true
  try {
    const payload: Record<string, string> = {
      'LOYALTY_EARN_RATE': loyaltyConfig.earnRate.toString(),
      'LOYALTY_REDEEM_RATE': loyaltyConfig.redeemRate.toString(),
      'LOYALTY_SILVER_THRESHOLD': loyaltyConfig.silverThreshold.toString(),
      'LOYALTY_GOLD_THRESHOLD': loyaltyConfig.goldThreshold.toString()
    }
    await post('/settings/batch', payload)
    toast.success('Loyalty program settings updated!')
    fetchData() // Refresh settings from server
  } catch (err) {
    toast.error('Failed to update loyalty settings')
  } finally {
    savingLoyalty.value = false
  }
}

const fetchData = async () => {
    loading.value = true
    try {
        const [userData, roleData, empData, permData, settingsData] = await Promise.all([
            get<any[]>('/users'),
            get<any[]>('/roles'),
            get<any[]>('/employees'),
            get<any[]>('/permissions'),
            get<any[]>('/settings')
        ])
        users.value = userData || []
        roles.value = roleData || []
        employees.value = empData || []
        permissions.value = permData || []
        settings.value = settingsData || []
        loadTelegramConfig()
    } catch (err) {
        console.error('Failed to fetch access control data', err)
    } finally {
        loading.value = false
    }
}

// User Actions
const openUserModal = (user: any = null) => {
    editingUser.value = user
    if (user) {
        userForm.userName = user.userName
        userForm.password = '' // Don't show password
        userForm.employeeId = user.employee?.employeeId || null
        userForm.roleId = user.role?.roleId || null
        userForm.isActive = user.isActive ?? true
    } else {
        userForm.userName = ''
        userForm.password = ''
        userForm.employeeId = null
        userForm.roleId = null
        userForm.isActive = true
    }
    showUserModal.value = true
}

const saveUser = async () => {
    saving.value = true
    try {
        const payload = { ...userForm }
        if (editingUser.value) {
            await put(`/users/${editingUser.value.userId}`, payload)
            toast.success('User updated successfully')
        } else {
            await post('/users/add', payload)
            toast.success('User created successfully')
        }
        showUserModal.value = false
        fetchData()
    } catch (err: any) {
        toast.error(err.data?.message || 'Failed to save user')
    } finally {
        saving.value = false
    }
}

const deleteUser = async (id: number) => {
    if(!confirm('Are you sure you want to delete this user?')) return
    try {
        await del(`/users/${id}`)
        toast.success('User deleted')
        fetchData()
    } catch (err) {
        toast.error('Failed to delete user')
    }
}

// Role Actions
const openRoleModal = (role: any = null) => {
    editingRole.value = role
    if (role) {
        roleForm.roleName = role.roleName
        roleForm.description = role.description
        roleForm.permissionIds = role.permissions ? role.permissions.map((p: any) => p.permissionId) : []
    } else {
        roleForm.roleName = ''
        roleForm.description = ''
        roleForm.permissionIds = []
    }
    showRoleModal.value = true
}

const saveRole = async () => {
    saving.value = true
    try {
        const payload = { ...roleForm }
        if (editingRole.value) {
            await put(`/roles/${editingRole.value.roleId}`, payload)
            toast.success('Role updated')
        } else {
            await post('/roles', payload)
            toast.success('Role created')
        }
        showRoleModal.value = false
        fetchData()
    } catch (err: any) {
        toast.error(err.data?.message || 'Failed to save role')
    } finally {
        saving.value = false
    }
}

const deleteRole = async (id: number) => {
    if(!confirm('Are you sure you want to delete this role?')) return
    try {
        await del(`/roles/${id}`)
        toast.success('Role removed')
        fetchData()
    } catch (err) {
        toast.error('Failed to delete role')
    }
}

const saveSettings = async () => {
    saving.value = true
    try {
        // Convert array to map
        const payload = settings.value.reduce((acc, curr) => {
            acc[curr.key] = curr.value
            return acc
        }, {} as Record<string, string>)

        await post('/settings/batch', payload)
        toast.success('Settings saved successfully')
    } catch (err) {
        toast.error('Failed to save settings')
    } finally {
        saving.value = false
    }
}

onMounted(() => {
    fetchData()
})
</script>

<style scoped>
.btn-primary {
  @apply bg-primary-600 hover:bg-primary-700 text-white rounded-2xl transition-all active:scale-95 disabled:opacity-50;
}
</style>
