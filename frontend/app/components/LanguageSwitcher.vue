<template>
  <div class="relative">
    <button
      @click="toggleDropdown"
      class="flex items-center gap-2 px-3 py-1.5 rounded-xl transition-all duration-300 active:scale-95 group"
      :class="{ 'bg-black/5 dark:bg-white/10': isOpen }"
    >
      <div class="w-5 h-5 rounded-full overflow-hidden border border-black/5 dark:border-white/10 shadow-sm flex-shrink-0">
        <img
          v-if="currentLocale.code === 'en'"
          src="https://flagcdn.com/w40/gb.png"
          alt="English"
          class="w-full h-full object-cover scale-125"
        />
        <img
          v-else
          src="https://flagcdn.com/w40/kh.png"
          alt="Khmer"
          class="w-full h-full object-cover scale-125"
        />
      </div>
      <span class="text-[13px] font-bold text-neutral-900 dark:text-white group-hover:text-primary-500 transition-colors uppercase tracking-tight">
        {{ currentLocale.code }}
      </span>
      <svg
        xmlns="http://www.w3.org/2000/svg"
        class="w-3.5 h-3.5 text-neutral-400 group-hover:text-neutral-900 dark:group-hover:text-white transition-all"
        :class="{ 'rotate-180': isOpen }"
        viewBox="0 0 24 24"
        fill="none"
        stroke="currentColor"
        stroke-width="3"
      >
        <path d="m6 9 6 6 6-6" />
      </svg>
    </button>

    <!-- Dropdown -->
    <div
      v-if="isOpen"
      class="absolute right-0 top-full mt-2 w-[160px] glass-card rounded-[18px] shadow-macos-lg overflow-hidden z-[100] animate-in fade-in zoom-in-95 duration-200 p-1.5"
    >
      <div class="space-y-1">
        <button
          v-for="locale in availableLocales"
          :key="locale.code"
          @click="switchLanguage(locale.code)"
          class="w-full flex items-center justify-between gap-3 px-3 py-2 rounded-xl text-[13px] font-bold transition-all"
          :class="[
            currentLocale.code === locale.code
              ? 'bg-primary-500 text-white shadow-lg shadow-primary-500/20 scale-[1.02]'
              : 'text-neutral-600 dark:text-neutral-400 hover:bg-black/5 dark:hover:bg-white/10 hover:text-neutral-900 dark:hover:text-white',
          ]"
        >
          <div class="flex items-center gap-3">
             <div class="w-5 h-5 rounded-full overflow-hidden border border-black/5 dark:border-white/10">
                <img
                  :src="locale.code === 'en' ? 'https://flagcdn.com/w40/gb.png' : 'https://flagcdn.com/w40/kh.png'"
                  :alt="locale.name"
                  class="w-full h-full object-cover scale-125"
                />
             </div>
             {{ locale.name }}
          </div>
          <svg v-if="currentLocale.code === locale.code" xmlns="http://www.w3.org/2000/svg" class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3.5">
            <path d="M20 6 9 17l-5-5"/>
          </svg>
        </button>
      </div>
    </div>
    
    <!-- Backdrop for closing -->
    <div v-if="isOpen" @click="isOpen = false" class="fixed inset-0 z-[90]"></div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
const { locale, locales, setLocale } = useI18n();
const isOpen = ref(false);

const currentLocale = computed(() => {
  return (locales.value as any[]).find((l) => l.code === locale.value) || {};
});

const availableLocales = computed(() => {
  return (locales.value as any[]);
});

const toggleDropdown = () => {
  isOpen.value = !isOpen.value;
};

const switchLanguage = (code: string) => {
  setLocale(code as 'en' | 'km');
  isOpen.value = false;
};
</script>
