<template>
  <div class="relative">
    <button
      @click="toggleDropdown"
      class="flex items-center gap-2 px-3 py-2 rounded-lg hover:bg-white/10 transition-colors"
      :class="{ 'text-primary-400': isOpen, 'text-neutral-400': !isOpen }"
    >
      <img
        v-if="currentLocale.code === 'en'"
        src="https://flagcdn.com/w40/gb.png"
        alt="English"
        class="w-5 h-auto rounded shadow-sm"
      />
      <img
        v-else
        src="https://flagcdn.com/w40/kh.png"
        alt="Khmer"
        class="w-5 h-auto rounded shadow-sm"
      />
      <span class="text-sm font-medium hidden md:block">{{ currentLocale.name }}</span>
      <svg
        xmlns="http://www.w3.org/2000/svg"
        class="w-4 h-4 transition-transform duration-200"
        :class="{ 'rotate-180': isOpen }"
        viewBox="0 0 24 24"
        fill="none"
        stroke="currentColor"
        stroke-width="2"
        stroke-linecap="round"
        stroke-linejoin="round"
      >
        <path d="m6 9 6 6 6-6" />
      </svg>
    </button>

    <!-- Dropdown -->
    <div
      v-if="isOpen"
      class="absolute right-0 top-full mt-2 w-40 bg-neutral-800 border border-neutral-700 rounded-xl shadow-xl overflow-hidden z-50"
    >
      <div class="p-1">
        <button
          v-for="locale in availableLocales"
          :key="locale.code"
          @click="switchLanguage(locale.code)"
          class="w-full flex items-center gap-3 px-3 py-2 rounded-lg text-sm transition-colors"
          :class="[
            currentLocale.code === locale.code
              ? 'bg-primary-600 text-white'
              : 'text-neutral-300 hover:bg-neutral-700 hover:text-white',
          ]"
        >
          <img
            :src="locale.code === 'en' ? 'https://flagcdn.com/w40/gb.png' : 'https://flagcdn.com/w40/kh.png'"
            :alt="locale.name"
            class="w-5 h-auto rounded shadow-sm"
          />
          {{ locale.name }}
        </button>
      </div>
    </div>
    
    <!-- Backdrop for closing -->
    <div v-if="isOpen" @click="isOpen = false" class="fixed inset-0 z-40"></div>
  </div>
</template>

<script setup lang="ts">
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
  setLocale(code);
  isOpen.value = false;
};
</script>
