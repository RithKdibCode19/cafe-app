<template>
  <div class="min-h-screen bg-neutral-900 flex items-center justify-center p-4">
    <div class="bg-neutral-800 rounded-2xl border border-neutral-700 w-full max-w-md p-8 shadow-2xl">
      <!-- Logo -->
      <div class="flex flex-col items-center mb-8">
        <div class="w-16 h-16 rounded-2xl bg-primary-600 flex items-center justify-center mb-4 shadow-lg shadow-primary-900/50">
           <svg xmlns="http://www.w3.org/2000/svg" class="w-8 h-8 text-white" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M17 8h1a4 4 0 1 1 0 8h-1"/><path d="M3 8h14v9a4 4 0 0 1-4 4H7a4 4 0 0 1-4-4Z"/></svg>
        </div>
        <h1 class="text-2xl font-bold text-white">Cafe POS</h1>
        <p class="text-neutral-400">Sign in to your account</p>
      </div>

      <!-- Form -->
      <form @submit.prevent="handleLogin" class="space-y-6">
        <div>
           <label class="block text-sm font-medium text-neutral-300 mb-1.5">Username</label>
           <div class="relative">
             <input 
                v-model="username"
                type="text" 
                class="w-full bg-neutral-700 border border-neutral-600 text-white placeholder-neutral-500 rounded-xl px-4 py-2.5 focus:ring-2 focus:ring-primary-500 focus:border-transparent transition-all pl-10"
                placeholder="Enter username"
                required
             />
             <svg xmlns="http://www.w3.org/2000/svg" class="w-5 h-5 text-neutral-500 absolute left-3 top-1/2 -translate-y-1/2" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M19 21v-2a4 4 0 0 0-4-4H9a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
           </div>
        </div>

        <div>
           <label class="block text-sm font-medium text-neutral-300 mb-1.5">Password</label>
           <div class="relative">
             <input 
                v-model="password"
                type="password" 
                class="w-full bg-neutral-700 border border-neutral-600 text-white placeholder-neutral-500 rounded-xl px-4 py-2.5 focus:ring-2 focus:ring-primary-500 focus:border-transparent transition-all pl-10"
                placeholder="Enter password"
                required
             />
             <svg xmlns="http://www.w3.org/2000/svg" class="w-5 h-5 text-neutral-500 absolute left-3 top-1/2 -translate-y-1/2" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect width="18" height="11" x="3" y="11" rx="2" ry="2"/><path d="M7 11V7a5 5 0 0 1 10 0v4"/></svg>
           </div>
        </div>
        
        <div v-if="error" class="bg-error-900/30 border border-error-800 text-error-400 text-sm p-3 rounded-lg flex items-center gap-2">
            <svg xmlns="http://www.w3.org/2000/svg" class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><line x1="12" x2="12" y1="8" y2="12"/><line x1="12" x2="12.01" y1="16" y2="16"/></svg>
            {{ error }}
        </div>

        <button 
            type="submit" 
            :disabled="loading"
            class="w-full bg-primary-600 hover:bg-primary-700 disabled:opacity-50 disabled:cursor-not-allowed text-white font-medium py-2.5 rounded-xl transition-colors flex items-center justify-center gap-2"
        >
            <span v-if="loading" class="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin"></span>
            {{ loading ? 'Signing in...' : 'Sign In' }}
        </button>
      </form>
      
      <!-- Footer -->
      <div class="mt-8 text-center text-xs text-neutral-500">
        &copy; 2024 Cafe POS System. All rights reserved.
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

definePageMeta({
    layout: false
})

const username = ref('')
const password = ref('')
const error = ref('')
const loading = ref(false)
const { post } = useApi()
const router = useRouter()

const { setAuth } = useAuth()

const handleLogin = async () => {
    loading.value = true
    error.value = ''
    
    try {
        const response: any = await post('/auth/login', {
            username: username.value,
            password: password.value
        })
        
        setAuth(response)
        
        // Redirect based on role (simple logic)
        if (response.roleName === 'ADMIN' || response.roleName === 'MANAGER') {
            router.push('/admin')
        } else {
            router.push('/pos')
        }
    } catch (err: any) {
        error.value = err.message || 'Invalid username or password'
    } finally {
        loading.value = false
    }
}
</script>
