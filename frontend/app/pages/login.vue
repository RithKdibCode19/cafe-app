<template>
  <div class="w-full">
    <!-- Login Card -->
    <div
      v-if="!showBranchSelector"
      class="bg-neutral-900/60 backdrop-blur-xl border border-white/10 rounded-[32px] p-8 shadow-2xl relative overflow-hidden animate-scale-in"
    >
      <!-- Subtle internal glow -->
      <div
        class="absolute top-0 left-0 w-full h-1 bg-gradient-to-r from-transparent via-primary-500/50 to-transparent opacity-50"
      ></div>

      <!-- Logo -->
      <div class="flex flex-col items-center mb-10">
        <div
          class="w-20 h-20 rounded-2xl overflow-hidden shadow-lg shadow-black/20 mb-5 ring-1 ring-white/10"
        >
          <img
            src="~/assets/images/cofeoshop.jpg"
            alt="Cafe POS Logo"
            class="w-full h-full object-cover scale-105"
          />
        </div>
        <h1 class="text-2xl font-bold text-white tracking-tight">
          Welcome Back
        </h1>
        <p class="text-neutral-400 text-sm mt-1">
          Sign in to access your dashboard
        </p>
      </div>

      <!-- Form -->
      <form @submit.prevent="handleLogin" class="space-y-5">
        <div class="space-y-1.5">
          <label
            class="text-xs font-bold text-neutral-400 uppercase tracking-wider ml-1"
            >Username</label
          >
          <div class="relative group">
            <input
              v-model="username"
              type="text"
              class="w-full bg-black/20 border border-white/5 text-white placeholder-neutral-600 rounded-xl px-4 py-3.5 focus:bg-black/40 focus:border-primary-500/50 focus:ring-4 focus:ring-primary-500/10 transition-all duration-300 outline-none pl-11"
              :class="{
                'border-red-500/50 focus:border-red-500/50 focus:ring-red-500/10':
                  error,
              }"
              placeholder="Enter your username"
              @input="error = ''"
              required
            />
            <svg
              xmlns="http://www.w3.org/2000/svg"
              class="w-5 h-5 text-neutral-600 absolute left-4 top-1/2 -translate-y-1/2 group-focus-within:text-primary-400 transition-colors"
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

        <div class="space-y-1.5">
          <label
            class="text-xs font-bold text-neutral-400 uppercase tracking-wider ml-1"
            >Password</label
          >
          <div class="relative group">
            <input
              v-model="password"
              type="password"
              class="w-full bg-black/20 border border-white/5 text-white placeholder-neutral-600 rounded-xl px-4 py-3.5 focus:bg-black/40 focus:border-primary-500/50 focus:ring-4 focus:ring-primary-500/10 transition-all duration-300 outline-none pl-11"
              :class="{
                'border-red-500/50 focus:border-red-500/50 focus:ring-red-500/10':
                  error,
              }"
              placeholder="Enter your password"
              @input="error = ''"
              required
            />
            <svg
              xmlns="http://www.w3.org/2000/svg"
              class="w-5 h-5 text-neutral-600 absolute left-4 top-1/2 -translate-y-1/2 group-focus-within:text-primary-400 transition-colors"
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

        <Transition name="fade">
          <div
            v-if="error"
            class="bg-red-500/10 border border-red-500/20 text-red-400 text-sm p-3 rounded-xl flex items-center gap-2"
          >
            <svg
              xmlns="http://www.w3.org/2000/svg"
              class="w-4 h-4 flex-shrink-0"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              stroke-width="2"
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
          class="w-full bg-white text-black font-bold py-3.5 rounded-xl hover:bg-neutral-200 disabled:opacity-50 disabled:cursor-not-allowed transition-all duration-300 transform active:scale-[0.98] shadow-lg shadow-white/5 flex items-center justify-center gap-2 mt-2"
        >
          <span
            v-if="loading"
            class="w-4 h-4 border-2 border-black/30 border-t-black rounded-full animate-spin"
          ></span>
          {{ loading ? "Signing in..." : "Sign In" }}
        </button>
      </form>
    </div>

    <!-- Branch Selector Card -->
    <div
      v-else
      class="bg-neutral-900/60 backdrop-blur-xl border border-white/10 rounded-[32px] p-8 shadow-2xl relative overflow-hidden animate-scale-in"
    >
      <!-- Subtle internal glow -->
      <div
        class="absolute top-0 left-0 w-full h-1 bg-gradient-to-r from-transparent via-primary-500/50 to-transparent opacity-50"
      ></div>

      <div class="flex flex-col items-center mb-8">
        <div
          class="w-16 h-16 rounded-2xl bg-gradient-to-br from-primary-500/20 to-primary-600/20 text-primary-400 border border-primary-500/20 flex items-center justify-center mb-4 shadow-lg shadow-primary-900/20"
        >
          <svg
            xmlns="http://www.w3.org/2000/svg"
            class="w-8 h-8"
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
          Choose where you're working today
        </p>
      </div>

      <div
        class="space-y-3 mb-6 max-h-64 overflow-y-auto custom-scrollbar pr-1"
      >
        <button
          v-for="branch in availableBranches"
          :key="branch.branchId"
          @click="selectBranch(branch)"
          class="w-full p-4 bg-white/5 hover:bg-white/10 border border-white/5 hover:border-primary-500/30 rounded-2xl text-left transition-all group relative overflow-hidden"
        >
          <div class="flex items-center justify-between relative z-10">
            <div>
              <span
                class="text-[10px] font-black text-primary-400 tracking-wider uppercase bg-primary-500/10 px-1.5 py-0.5 rounded mb-1 inline-block"
                >{{ branch.code }}</span
              >
              <h3 class="text-white font-medium">{{ branch.name }}</h3>
              <p v-if="branch.location" class="text-xs text-neutral-500 mt-0.5">
                {{ branch.location }}
              </p>
            </div>
            <div
              class="p-2 rounded-full bg-white/5 group-hover:bg-primary-500 group-hover:text-white transition-colors duration-300"
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
            </div>
          </div>
        </button>
      </div>

      <button
        @click="logout"
        class="w-full text-neutral-500 hover:text-white text-sm py-2 transition-colors flex items-center justify-center gap-2"
      >
        <svg
          xmlns="http://www.w3.org/2000/svg"
          class="w-4 h-4"
          viewBox="0 0 24 24"
          fill="none"
          stroke="currentColor"
          stroke-width="2"
        >
          <path d="M19 12H5" />
          <path d="M12 19l-7-7 7-7" />
        </svg>
        Back to login
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";

definePageMeta({
  layout: "auth",
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

  // Simulate network delay for effect
  // await new Promise(resolve => setTimeout(resolve, 800));

  try {
    const response: any = await post("/auth/login", {
      username: username.value,
      password: password.value,
    });

    setAuth(response);
    pendingAuthResponse.value = response;
    await nextTick();

    const branches = await get<Branch[]>("/branches");

    if (branches && branches.length > 1) {
      availableBranches.value = branches;
      showBranchSelector.value = true;
    } else if (branches && branches.length === 1) {
      setBranch(branches[0]!);
      redirectUser(response);
    } else {
      redirectUser(response);
    }
  } catch (err: any) {
    const backendMessage =
      err.response?._data?.message || err.data?.message || err.message;

    if (err.statusCode === 401 || err.response?.status === 401) {
      error.value =
        backendMessage && backendMessage !== "FetchError"
          ? backendMessage
          : "Incorrect username or password";
    } else {
      error.value = backendMessage || "An unexpected error occurred";
    }
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
    // router.push("/admin");
    window.location.href = "/admin"; // Force reload to ensure layout reflow
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
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
