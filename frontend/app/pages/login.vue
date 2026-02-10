<template>
  <div
    class="dark min-h-screen bg-neutral-900 flex items-center justify-center p-4 relative overflow-hidden"
  >
    <!-- Gradient Background -->
    <div class="absolute inset-0 gradient-mesh opacity-50"></div>
    <div
      class="absolute inset-0 bg-gradient-to-br from-primary-900/20 via-transparent to-accent-900/20"
    ></div>

    <!-- Floating particles (decorative) -->
    <div
      class="absolute top-1/4 left-1/4 w-64 h-64 bg-primary-500/10 rounded-full blur-3xl animate-float"
    ></div>
    <div
      class="absolute bottom-1/4 right-1/4 w-96 h-96 bg-accent-500/10 rounded-full blur-3xl animate-float"
      style="animation-delay: 1.5s"
    ></div>

    <!-- Login Card -->
    <div
      v-if="!showBranchSelector"
      class="card-glass rounded-3xl w-full max-w-md p-8 shadow-2xl relative z-10 animate-scale-in"
    >
      <!-- Logo -->
      <div class="flex flex-col items-center mb-8">
        <div
          class="w-24 h-24 rounded-2xl overflow-hidden flex items-center justify-center mb-4 shadow-xl shadow-primary-900/50 animate-float"
        >
          <img
            src="~/assets/images/cofeoshop.jpg"
            alt="Cafe POS Logo"
            class="w-full h-full object-cover"
          />
        </div>
        <h1 class="text-2xl font-bold text-white">Cofeoshop</h1>
        <p class="text-neutral-400">Sign in to your account</p>
      </div>

      <!-- Form -->
      <form @submit.prevent="handleLogin" class="space-y-6">
        <div class="animate-fade-in stagger-1 animate-hidden">
          <label class="block text-sm font-medium text-neutral-300 mb-1.5"
            >Username</label
          >
          <div class="relative group">
            <input
              v-model="username"
              type="text"
              class="w-full bg-neutral-800/50 border border-neutral-700 text-white placeholder-neutral-500 rounded-xl px-4 py-3 focus:ring-2 focus:ring-primary-500 focus:border-transparent focus:shadow-lg focus:shadow-primary-500/20 transition-all duration-300 pl-11"
              placeholder="Enter username"
              required
            />
            <svg
              xmlns="http://www.w3.org/2000/svg"
              class="w-5 h-5 text-neutral-500 absolute left-4 top-1/2 -translate-y-1/2 group-focus-within:text-primary-400 transition-colors"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              stroke-width="2"
              stroke-linecap="round"
              stroke-linejoin="round"
            >
              <path d="M19 21v-2a4 4 0 0 0-4-4H9a4 4 0 0 0-4 4v2" />
              <circle cx="12" cy="7" r="4" />
            </svg>
          </div>
        </div>

        <div class="animate-fade-in stagger-2 animate-hidden">
          <label class="block text-sm font-medium text-neutral-300 mb-1.5"
            >Password</label
          >
          <div class="relative group">
            <input
              v-model="password"
              type="password"
              class="w-full bg-neutral-800/50 border border-neutral-700 text-white placeholder-neutral-500 rounded-xl px-4 py-3 focus:ring-2 focus:ring-primary-500 focus:border-transparent focus:shadow-lg focus:shadow-primary-500/20 transition-all duration-300 pl-11"
              placeholder="Enter password"
              required
            />
            <svg
              xmlns="http://www.w3.org/2000/svg"
              class="w-5 h-5 text-neutral-500 absolute left-4 top-1/2 -translate-y-1/2 group-focus-within:text-primary-400 transition-colors"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              stroke-width="2"
              stroke-linecap="round"
              stroke-linejoin="round"
            >
              <rect width="18" height="11" x="3" y="11" rx="2" ry="2" />
              <path d="M7 11V7a5 5 0 0 1 10 0v4" />
            </svg>
          </div>
        </div>

        <Transition name="shake">
          <div
            v-if="error"
            class="bg-error-900/30 border border-error-800 text-error-400 text-sm p-3 rounded-xl flex items-center gap-2 animate-scale-in"
          >
            <svg
              xmlns="http://www.w3.org/2000/svg"
              class="w-4 h-4 flex-shrink-0"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              stroke-width="2"
              stroke-linecap="round"
              stroke-linejoin="round"
            >
              <circle cx="12" cy="12" r="10" />
              <line x1="12" x2="12" y1="8" y2="12" />
              <line x1="12" x2="12.01" y1="16" y2="16" />
            </svg>
            {{ error }}
          </div>
        </Transition>

        <button
          type="submit"
          :disabled="loading"
          class="w-full bg-gradient-to-r from-primary-600 to-primary-500 hover:from-primary-500 hover:to-primary-400 disabled:opacity-50 disabled:cursor-not-allowed text-white font-semibold py-3 rounded-xl transition-all duration-300 flex items-center justify-center gap-2 shadow-lg shadow-primary-900/30 hover:shadow-xl hover:shadow-primary-500/30 hover:-translate-y-0.5 active:translate-y-0 animate-fade-in stagger-3 animate-hidden"
        >
          <span
            v-if="loading"
            class="w-5 h-5 border-2 border-white/30 border-t-white rounded-full animate-spin"
          ></span>
          {{ loading ? "Signing in..." : "Sign In" }}
        </button>
      </form>

      <!-- Footer -->
      <div class="mt-8 text-center text-xs text-neutral-500">
        &copy; 2024 Cafe POS System. All rights reserved.
      </div>
    </div>

    <!-- Branch Selector Card -->
    <div
      v-else
      class="card-glass rounded-3xl w-full max-w-md p-8 shadow-2xl relative z-10 animate-scale-in"
    >
      <div class="flex flex-col items-center mb-6">
        <div
          class="w-16 h-16 rounded-2xl bg-gradient-to-br from-primary-500 to-primary-700 flex items-center justify-center mb-4 shadow-xl shadow-primary-900/50"
        >
          <svg
            xmlns="http://www.w3.org/2000/svg"
            class="w-8 h-8 text-white"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            stroke-width="2"
          >
            <path d="M3 21h18" />
            <path d="M5 21V7l8-4v18" />
            <path d="M19 21V11l-6-4" />
          </svg>
        </div>
        <h1 class="text-xl font-bold text-white">Select Branch</h1>
        <p class="text-neutral-400 text-sm">
          Choose the branch you're working at
        </p>
      </div>

      <div class="space-y-3 mb-6 max-h-64 overflow-y-auto scrollbar-thin">
        <button
          v-for="branch in availableBranches"
          :key="branch.branchId"
          @click="selectBranch(branch)"
          class="w-full p-4 bg-neutral-800/50 hover:bg-neutral-700/50 border border-neutral-700 hover:border-primary-500 rounded-xl text-left transition-all group"
        >
          <div class="flex items-center justify-between">
            <div>
              <span class="text-xs font-bold text-primary-400">{{
                branch.code
              }}</span>
              <h3 class="text-white font-medium">{{ branch.name }}</h3>
              <p v-if="branch.location" class="text-xs text-neutral-400 mt-1">
                {{ branch.location }}
              </p>
            </div>
            <svg
              xmlns="http://www.w3.org/2000/svg"
              class="w-5 h-5 text-neutral-600 group-hover:text-primary-400 transition-colors"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              stroke-width="2"
            >
              <path d="m9 18 6-6-6-6" />
            </svg>
          </div>
        </button>
      </div>

      <button
        @click="logout"
        class="w-full text-neutral-400 hover:text-white text-sm py-2 transition-colors"
      >
        ← Sign in with a different account
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";

definePageMeta({
  layout: false,
});

interface Branch {
  branchId: number;
  code: string;
  name: string;
  location?: string;
}

const username = ref("");
const password = ref("");
const error = ref("");
const loading = ref(false);
const showBranchSelector = ref(false);
const availableBranches = ref<Branch[]>([]);
const pendingAuthResponse = ref<any>(null);

const { get, post } = useApi();
const router = useRouter();
const { setAuth, clearAuth } = useAuth();
const { setBranch } = useBranch();

const handleLogin = async () => {
  loading.value = true;
  error.value = "";

  try {
    const response: any = await post("/auth/login", {
      username: username.value,
      password: password.value,
    });

    // Store auth temporarily
    setAuth(response);
    pendingAuthResponse.value = response;

    // Wait for state to propagate
    await nextTick();
        
    // Check if we need branch selection
    const branches = await get<Branch[]>("/branches");

    if (branches && branches.length > 1) {
      // Show branch selector
      availableBranches.value = branches;
      showBranchSelector.value = true;
    } else if (branches && branches.length === 1) {
      // Auto-select only branch
      setBranch(branches[0]);
      redirectUser(response);
    } else {
      // No branches, proceed normally
      redirectUser(response);
    }
  } catch (err: any) {
    error.value = err.message || "Invalid username or password";
  } finally {
    loading.value = false;
  }
};

const selectBranch = (branch: Branch) => {
  setBranch(branch);
  redirectUser(pendingAuthResponse.value);
};

const redirectUser = (response: any) => {
  if (response.roleName === "ADMIN" || response.roleName === "MANAGER") {
    router.push("/admin");
  } else {
    router.push("/pos");
  }
};

const logout = () => {
  clearAuth();
  showBranchSelector.value = false;
  pendingAuthResponse.value = null;
  username.value = "";
  password.value = "";
};
</script>

<style scoped>
.shake-enter-active {
  animation: shake 0.5s ease-out;
}

@keyframes shake {
  0%,
  100% {
    transform: translateX(0);
  }
  20% {
    transform: translateX(-8px);
  }
  40% {
    transform: translateX(8px);
  }
  60% {
    transform: translateX(-4px);
  }
  80% {
    transform: translateX(4px);
  }
}
</style>
