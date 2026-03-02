<template>
  <NuxtLayout name="admin">
    <UiBreadcrumb :items="[{ label: 'Inventory', href: '/admin/inventory' }, { label: 'Recipes' }]" />

    <div class="h-[calc(100vh-180px)] flex gap-6 overflow-hidden mt-4">
      <!-- Left: Menu Item List -->
      <div class="w-[340px] flex-shrink-0 bg-white dark:bg-neutral-900 rounded-3xl border border-neutral-200 dark:border-neutral-800 shadow-sm flex flex-col overflow-hidden">
        <!-- Search Header -->
        <div class="p-4 border-b border-neutral-100 dark:border-neutral-800">
          <div class="relative">
            <svg xmlns="http://www.w3.org/2000/svg" class="w-4 h-4 absolute left-3 top-1/2 -translate-y-1/2 text-neutral-400" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="11" cy="11" r="8"/><path d="m21 21-4.3-4.3"/></svg>
            <input v-model="search" placeholder="Search menu items..." class="w-full bg-neutral-50 dark:bg-neutral-800 border border-neutral-200 dark:border-neutral-700 rounded-xl px-4 py-2.5 pl-10 text-sm text-neutral-900 dark:text-white placeholder:text-neutral-400 focus:ring-2 focus:ring-primary-500 focus:outline-none transition-all" />
          </div>
          <p class="text-[10px] font-bold text-neutral-400 uppercase tracking-widest mt-2">{{ filteredItems.length }} items</p>
        </div>

        <!-- Category Filter Pills -->
        <div class="px-3 pb-2 flex flex-wrap gap-1.5">
          <button
            @click="selectedCategory = ''"
            class="px-2.5 py-1 rounded-lg text-[10px] font-bold uppercase tracking-wider transition-all"
            :class="selectedCategory === '' ? 'bg-primary-500 text-white shadow-sm' : 'bg-neutral-100 dark:bg-neutral-800 text-neutral-500 hover:bg-neutral-200 dark:hover:bg-neutral-700'"
          >All</button>
          <button
            v-for="cat in categories"
            :key="cat"
            @click="selectedCategory = cat"
            class="px-2.5 py-1 rounded-lg text-[10px] font-bold uppercase tracking-wider transition-all"
            :class="selectedCategory === cat ? 'bg-primary-500 text-white shadow-sm' : 'bg-neutral-100 dark:bg-neutral-800 text-neutral-500 hover:bg-neutral-200 dark:hover:bg-neutral-700'"
          >{{ cat }}</button>
        </div>

        <!-- Items List -->
        <div class="flex-1 overflow-y-auto p-3 space-y-1">
          <button
            v-for="item in filteredItems"
            :key="item.menuItemId"
            @click="selectItem(item)"
            class="w-full text-left p-3 rounded-2xl transition-all duration-200 group"
            :class="selectedItem?.menuItemId === item.menuItemId
              ? 'bg-primary-50 dark:bg-primary-900/20 border border-primary-200 dark:border-primary-800 shadow-sm'
              : 'hover:bg-neutral-50 dark:hover:bg-neutral-800/50 border border-transparent'"
          >
            <div class="flex items-center gap-3">
              <div class="w-10 h-10 rounded-xl flex items-center justify-center text-sm font-black shadow-sm"
                :class="selectedItem?.menuItemId === item.menuItemId
                  ? 'bg-primary-500 text-white'
                  : 'bg-neutral-100 dark:bg-neutral-800 text-neutral-500 group-hover:bg-primary-100 group-hover:text-primary-600 dark:group-hover:bg-primary-900/30'"
              >
                {{ item.name?.charAt(0)?.toUpperCase() }}
              </div>
              <div class="flex-1 min-w-0">
                <div class="font-bold text-sm text-neutral-900 dark:text-white truncate">{{ item.name }}</div>
                <div class="text-[10px] text-neutral-500 font-bold uppercase tracking-wider">{{ item.categoryName || 'Uncategorized' }}</div>
              </div>
              <div class="text-xs font-mono font-bold text-neutral-500 bg-neutral-100 dark:bg-neutral-800 px-2 py-1 rounded-lg">
                ${{ item.basePrice?.toFixed(2) }}
              </div>
            </div>
          </button>

          <!-- Empty -->
          <div v-if="filteredItems.length === 0" class="flex flex-col items-center justify-center py-12 text-center">
            <div class="w-12 h-12 rounded-2xl bg-neutral-100 dark:bg-neutral-800 flex items-center justify-center mb-3">
              <svg xmlns="http://www.w3.org/2000/svg" class="w-6 h-6 text-neutral-300" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><circle cx="11" cy="11" r="8"/><path d="m21 21-4.3-4.3"/></svg>
            </div>
            <p class="text-sm font-bold text-neutral-400">No items found</p>
          </div>
        </div>
      </div>

      <!-- Right: Recipe Detail -->
      <div class="flex-1 bg-white dark:bg-neutral-900 rounded-3xl border border-neutral-200 dark:border-neutral-800 shadow-sm flex flex-col overflow-hidden">
        <!-- Selected Item View -->
        <div v-if="selectedItem" class="flex-1 flex flex-col h-full">
          <!-- Header -->
          <div class="p-6 border-b border-neutral-100 dark:border-neutral-800 bg-neutral-50/50 dark:bg-neutral-800/30">
            <div class="flex justify-between items-center">
              <div class="flex items-center gap-4">
                <div class="w-12 h-12 rounded-2xl bg-gradient-to-br from-primary-500 to-accent-500 flex items-center justify-center text-white text-lg font-black shadow-lg shadow-primary-500/20">
                  {{ selectedItem.name?.charAt(0)?.toUpperCase() }}
                </div>
                <div>
                  <h2 class="text-xl font-black text-neutral-900 dark:text-white">{{ selectedItem.name }}</h2>
                  <div class="flex items-center gap-2 mt-0.5">
                    <span class="text-[10px] font-bold text-neutral-500 uppercase tracking-widest">{{ selectedItem.categoryName || 'Uncategorized' }}</span>
                    <span class="text-neutral-300">•</span>
                    <span class="text-[10px] font-bold uppercase tracking-widest" :class="recipes.length > 0 ? 'text-success-500' : 'text-warning-500'">
                      {{ recipes.length }} ingredient{{ recipes.length !== 1 ? 's' : '' }}
                    </span>
                  </div>
                </div>
              </div>
              <button @click="openAddModal" class="bg-primary-600 hover:bg-primary-700 text-white px-4 py-2.5 rounded-xl font-bold text-xs transition-all shadow-lg shadow-primary-500/20 flex items-center gap-2">
                <svg xmlns="http://www.w3.org/2000/svg" class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M5 12h14"/><path d="M12 5v14"/></svg>
                Link Ingredient
              </button>
            </div>
          </div>

          <!-- Ingredients Table -->
          <div class="flex-1 overflow-y-auto p-6">
            <div v-if="recipes.length === 0" class="flex flex-col items-center justify-center py-16 text-center">
              <div class="w-20 h-20 rounded-3xl bg-neutral-100 dark:bg-neutral-800 flex items-center justify-center mb-5">
                <svg xmlns="http://www.w3.org/2000/svg" class="w-10 h-10 text-neutral-300 dark:text-neutral-600" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M12 2a10 10 0 1 0 10 10A10 10 0 0 0 12 2z"/><path d="M8 12h8"/></svg>
              </div>
              <h3 class="text-lg font-black text-neutral-900 dark:text-white mb-1">No Ingredients Linked</h3>
              <p class="text-sm text-neutral-400 max-w-xs">This item won't deduct any stock when sold. Click "Link Ingredient" to add a recipe.</p>
            </div>

            <div v-else class="space-y-3">
              <div
                v-for="rec in recipes"
                :key="rec.recipeId"
                class="flex items-center gap-4 p-4 rounded-2xl bg-neutral-50 dark:bg-neutral-800/50 border border-neutral-100 dark:border-neutral-800 hover:border-neutral-200 dark:hover:border-neutral-700 transition-all group"
              >
                <!-- Ingredient Icon -->
                <div class="w-10 h-10 rounded-xl bg-accent-100 dark:bg-accent-900/30 flex items-center justify-center flex-shrink-0">
                  <svg xmlns="http://www.w3.org/2000/svg" class="w-5 h-5 text-accent-600" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M6 13.87A4 4 0 0 1 7.41 6a5.11 5.11 0 0 1 1.05-1.54 5 5 0 0 1 7.08 0A5.11 5.11 0 0 1 16.59 6 4 4 0 0 1 18 13.87V21H6Z"/><line x1="6" x2="18" y1="17" y2="17"/></svg>
                </div>

                <!-- Info -->
                <div class="flex-1 min-w-0">
                  <div class="font-bold text-neutral-900 dark:text-white text-sm">{{ rec.ingredientName || 'Unknown Ingredient' }}</div>
                  <div class="text-[10px] font-bold text-neutral-400 uppercase tracking-wider mt-0.5">{{ rec.ingredientUnit }}</div>
                </div>

                <!-- Quantity Badge -->
                <div class="flex items-center gap-2">
                  <div class="bg-primary-50 dark:bg-primary-900/20 border border-primary-200 dark:border-primary-800 rounded-xl px-4 py-2">
                    <span class="text-lg font-black font-mono text-primary-600 dark:text-primary-400">{{ rec.quantityNeeded }}</span>
                    <span class="text-[10px] font-bold text-primary-400 ml-1 uppercase">{{ rec.ingredientUnit }}</span>
                  </div>
                </div>

                <!-- Delete Button -->
                <button
                  @click="deleteRecipe(rec.recipeId)"
                  class="p-2.5 rounded-xl text-neutral-300 hover:text-error-500 hover:bg-error-50 dark:hover:bg-error-900/20 transition-all opacity-0 group-hover:opacity-100"
                  title="Unlink ingredient"
                >
                  <svg xmlns="http://www.w3.org/2000/svg" class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M3 6h18"/><path d="M19 6v14c0 1-1 2-2 2H7c-1 0-2-1-2-2V6"/><path d="M8 6V4c0-1 1-2 2-2h4c1 0 2 1 2 2v2"/></svg>
                </button>
              </div>
            </div>
          </div>
        </div>

        <!-- Placeholder: No Item Selected -->
        <div v-else class="flex-1 flex flex-col items-center justify-center text-center p-12">
          <div class="w-24 h-24 rounded-3xl bg-neutral-100 dark:bg-neutral-800 flex items-center justify-center mb-6">
            <svg xmlns="http://www.w3.org/2000/svg" class="w-12 h-12 text-neutral-300 dark:text-neutral-600" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M6 13.87A4 4 0 0 1 7.41 6a5.11 5.11 0 0 1 1.05-1.54 5 5 0 0 1 7.08 0A5.11 5.11 0 0 1 16.59 6 4 4 0 0 1 18 13.87V21H6Z"/><line x1="6" x2="18" y1="17" y2="17"/></svg>
          </div>
          <h3 class="text-xl font-black text-neutral-900 dark:text-white mb-2">Select a Menu Item</h3>
          <p class="text-sm text-neutral-400 max-w-sm">Choose an item from the list to manage its recipe — the ingredients that get deducted when it's sold.</p>
        </div>
      </div>
    </div>

    <!-- Modal: Link Ingredient -->
    <div v-if="showAddModal" class="fixed inset-0 z-50 flex items-center justify-center bg-black/50 backdrop-blur-sm p-4">
      <div class="bg-white dark:bg-neutral-900 rounded-3xl w-full max-w-md border border-neutral-200 dark:border-neutral-800 shadow-2xl animate-in zoom-in duration-200 overflow-hidden">
        <!-- Modal Header -->
        <div class="p-6 bg-neutral-50 dark:bg-neutral-800/50 border-b border-neutral-200 dark:border-neutral-800">
          <div class="flex items-center gap-3">
            <div class="w-10 h-10 rounded-xl bg-primary-100 dark:bg-primary-900/30 flex items-center justify-center">
              <svg xmlns="http://www.w3.org/2000/svg" class="w-5 h-5 text-primary-600" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M5 12h14"/><path d="M12 5v14"/></svg>
            </div>
            <div>
              <h3 class="text-lg font-black text-neutral-900 dark:text-white">Link Ingredient</h3>
              <p class="text-[10px] font-bold text-neutral-400 uppercase tracking-widest">{{ selectedItem?.name }}</p>
            </div>
          </div>
        </div>

        <!-- Modal Body -->
        <form @submit.prevent="addRecipe" class="p-6 space-y-5">
          <div>
            <label class="block text-xs font-bold text-neutral-500 dark:text-neutral-400 mb-2 uppercase tracking-wider">Ingredient</label>
            <select v-model="newRecipe.ingredientId" class="w-full bg-neutral-50 dark:bg-neutral-800 border border-neutral-200 dark:border-neutral-700 rounded-xl px-4 py-3 text-sm text-neutral-900 dark:text-white focus:ring-2 focus:ring-primary-500 focus:outline-none appearance-none" required>
              <option :value="null" disabled>Select an ingredient...</option>
              <option v-for="ing in ingredients" :key="ing.ingredientId" :value="ing.ingredientId">
                {{ ing.name }} ({{ ing.unit }}) — Stock: {{ ing.currentStock }}
              </option>
            </select>
          </div>

          <div>
            <label class="block text-xs font-bold text-neutral-500 dark:text-neutral-400 mb-2 uppercase tracking-wider">Quantity per Sale</label>
            <div class="relative">
              <input v-model="newRecipe.quantityNeeded" type="number" step="0.01" min="0.01" class="w-full bg-neutral-50 dark:bg-neutral-800 border border-neutral-200 dark:border-neutral-700 rounded-xl px-4 py-3 text-lg font-black font-mono text-neutral-900 dark:text-white focus:ring-2 focus:ring-primary-500 focus:outline-none" required placeholder="0.00" />
              <span class="absolute right-4 top-1/2 -translate-y-1/2 text-neutral-400 text-xs font-bold uppercase">{{ getSelectedIngredientUnit }}</span>
            </div>
            <p class="text-[10px] text-neutral-400 mt-1.5">Amount deducted from stock each time this item is sold.</p>
          </div>

          <div class="flex justify-end gap-3 pt-4 border-t border-neutral-100 dark:border-neutral-800">
            <button type="button" @click="showAddModal = false" class="px-5 py-2.5 rounded-xl text-sm font-bold text-neutral-500 hover:text-neutral-700 hover:bg-neutral-100 dark:hover:bg-neutral-800 transition-all">Cancel</button>
            <button type="submit" class="px-6 py-2.5 bg-primary-600 hover:bg-primary-700 text-white rounded-xl text-sm font-bold transition-all shadow-lg shadow-primary-500/20">Link Ingredient</button>
          </div>
        </form>
      </div>
    </div>
  </NuxtLayout>
</template>

<script setup lang="ts">
definePageMeta({
  layout: false
})

const { get, post, del } = useApi();
const toast = useToast();

const menuItems = ref<any[]>([]);
const search = ref('');
const selectedCategory = ref('');
const selectedItem = ref<any>(null);
const recipes = ref<any[]>([]);
const ingredients = ref<any[]>([]);

const showAddModal = ref(false);
const newRecipe = reactive({ ingredientId: null, quantityNeeded: 0 });

onMounted(async () => {
    try {
        const [itemsData, ingData] = await Promise.all([
            get<any[]>('/menu-items'),
            get<any[]>('/ingredients')
        ]);
        menuItems.value = itemsData || [];
        ingredients.value = ingData || [];
    } catch (e) {
        console.error(e);
        toast.error("Failed to load data");
    }
});

const categories = computed(() => {
    const cats = new Set<string>();
    menuItems.value.forEach(item => {
        if (item.categoryName) cats.add(item.categoryName);
    });
    return Array.from(cats).sort();
});

const filteredItems = computed(() => {
    let items = menuItems.value;
    if (selectedCategory.value) {
        items = items.filter(item => item.categoryName === selectedCategory.value);
    }
    if (search.value) {
        const q = search.value.toLowerCase();
        items = items.filter(item =>
            item.name.toLowerCase().includes(q) ||
            item.categoryName?.toLowerCase().includes(q)
        );
    }
    return items;
});

const getSelectedIngredientUnit = computed(() => {
    if (!newRecipe.ingredientId) return '';
    const ing = ingredients.value.find(i => i.ingredientId === newRecipe.ingredientId);
    return ing ? ing.unit : '';
});

const selectItem = async (item: any) => {
    selectedItem.value = item;
    await fetchRecipes(item.menuItemId);
};

const fetchRecipes = async (menuItemId: number) => {
    try {
        const data = await get<any[]>(`/recipes/menu-item/${menuItemId}`);
        recipes.value = data || [];
    } catch (e) {
        console.error(e);
        toast.error("Failed to fetch recipes");
    }
};

const openAddModal = () => {
    newRecipe.ingredientId = null;
    newRecipe.quantityNeeded = 0;
    showAddModal.value = true;
};

const addRecipe = async () => {
    if (!selectedItem.value || !newRecipe.ingredientId || newRecipe.quantityNeeded <= 0) {
        toast.error("Invalid Input");
        return;
    }
    try {
        const payload = {
            menuItemId: selectedItem.value.menuItemId,
            ingredientId: newRecipe.ingredientId,
            quantityNeeded: newRecipe.quantityNeeded
        };
        await post('/recipes', payload);
        toast.success("Ingredient linked successfully");
        showAddModal.value = false;
        fetchRecipes(selectedItem.value.menuItemId);
    } catch (e) {
        console.error(e);
        toast.error("Failed to add recipe");
    }
};

const deleteRecipe = async (recipeId: number) => {
    if(!confirm("Unlink this ingredient?")) return;
    try {
        await del(`/recipes/${recipeId}`);
        toast.success("Unlinked successfully");
        if (selectedItem.value) {
            fetchRecipes(selectedItem.value.menuItemId);
        }
    } catch (e) {
        toast.error("Failed to unlink");
    }
};
</script>
