<template>
  <NuxtLayout name="admin">
    <UiBreadcrumb :items="[{ label: 'Inventory', href: '/admin/inventory' }, { label: 'Recipes' }]" />
    <div class="h-[calc(100vh-180px)] flex gap-6 overflow-hidden mt-4">
    <!-- Menu Item List -->
    <div class="w-1/3 bg-neutral-800 rounded-2xl p-4 flex flex-col border border-neutral-700 shadow-xl">
       <div class="mb-4">
         <div class="relative">
            <input v-model="search" placeholder="Search menu..." class="w-full bg-neutral-900 border border-neutral-700 rounded-xl px-4 py-3 pl-10 text-white focus:ring-2 focus:ring-primary-500" />
            <svg xmlns="http://www.w3.org/2000/svg" class="w-5 h-5 absolute left-3 top-1/2 -translate-y-1/2 text-neutral-500" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="11" cy="11" r="8"></circle><path d="m21 21-4.3-4.3"></path></svg>
         </div>
       </div>
       <div class="flex-1 overflow-y-auto space-y-2 pr-2 scrollbar-thin">
          <div v-for="item in filteredItems" :key="item.menuItemId" 
               @click="selectItem(item)"
               class="p-4 rounded-xl cursor-pointer hover:bg-neutral-700 transition-colors border border-transparent group"
               :class="{'bg-primary-900/20 border-primary-500/50': selectedItem?.menuItemId === item.menuItemId, 'bg-neutral-900/50': selectedItem?.menuItemId !== item.menuItemId}">
             <div class="flex justify-between items-start">
                 <div>
                    <div class="font-bold text-white mb-1 group-hover:text-primary-400 transition-colors">{{ item.name }}</div>
                    <div class="text-xs text-neutral-500 uppercase tracking-wider font-bold">{{ item.categoryName || 'No Category' }}</div>
                 </div>
                 <div class="text-xs font-mono text-neutral-400 bg-neutral-800 px-2 py-1 rounded border border-neutral-700">${{ item.basePrice?.toFixed(2) }}</div>
             </div>
          </div>
       </div>
    </div>

    <!-- Recipe Details -->
    <div class="w-2/3 bg-neutral-800 rounded-2xl p-6 flex flex-col border border-neutral-700 shadow-xl relative overflow-hidden">
       <div v-if="selectedItem" class="flex-1 flex flex-col h-full">
          <div class="flex justify-between items-start mb-6 pb-6 border-b border-neutral-700">
             <div>
                <h2 class="text-3xl font-bold text-white mb-2">{{ selectedItem.name }}</h2>
                <div class="flex items-center gap-2 text-sm text-neutral-400">
                    <span class="bg-neutral-700 px-2 py-0.5 rounded text-white">Recipe Management</span>
                    <span>•</span>
                    <span>Manage ingredients deducted when this item is sold.</span>
                </div>
             </div>
             <button @click="openAddModal" class="bg-primary-600 hover:bg-primary-500 text-white px-4 py-2 rounded-lg font-bold transition-colors shadow-lg shadow-primary-900/20 flex items-center gap-2">
                <svg xmlns="http://www.w3.org/2000/svg" class="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="12" y1="5" x2="12" y2="19"></line><line x1="5" y1="12" x2="19" y2="12"></line></svg>
                Link Ingredient
             </button>
          </div>

          <div class="flex-1 overflow-y-auto">
            <table class="w-full text-left">
                <thead>
                    <tr class="text-neutral-500 border-b border-neutral-700 text-xs uppercase tracking-wider">
                    <th class="pb-4 font-bold">Ingredient</th>
                    <th class="pb-4 font-bold">Unit</th>
                    <th class="pb-4 font-bold">Quantity Needed</th>
                    <th class="pb-4 font-bold text-right">Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <tr v-if="recipes.length === 0">
                        <td colspan="4" class="text-center py-12">
                            <div class="text-4xl mb-4 opacity-50">🥣</div>
                            <p class="text-neutral-400 font-medium">No ingredients linked yet.</p>
                            <p class="text-neutral-500 text-sm mt-1">This item will not deduct any stock when sold.</p>
                        </td>
                    </tr>
                    <tr v-for="rec in recipes" :key="rec.recipeId" class="border-b border-neutral-700 hover:bg-neutral-700/30 transition-colors group">
                    <td class="py-4 font-medium text-white">{{ rec.ingredientName || 'Unknown Ingredient' }}</td>
                    <td class="py-4 text-neutral-400 text-sm">{{ rec.ingredientUnit }}</td>
                    <td class="py-4 font-mono font-bold text-primary-400 text-lg">{{ rec.quantityNeeded }}</td>
                    <td class="py-4 text-right opacity-60 group-hover:opacity-100 transition-opacity">
                        <button @click="deleteRecipe(rec.recipeId)" class="text-red-400 hover:text-red-300 hover:bg-red-900/30 px-3 py-1.5 rounded-lg text-xs font-bold transition-colors border border-transparent hover:border-red-800">
                            Unlink
                        </button>
                    </td>
                    </tr>
                </tbody>
            </table>
          </div>
       </div>
       <div v-else class="flex-1 flex flex-col items-center justify-center text-center p-12">
          <div class="w-24 h-24 bg-neutral-700 rounded-full flex items-center justify-center mb-6 text-4xl shadow-inner text-neutral-500">
            📋
          </div>
          <h3 class="text-xl font-bold text-white mb-2">Select a Menu Item</h3>
          <p class="text-neutral-400 max-w-sm">Choose an item from the list on the left to manage its recipe and ingredients.</p>
       </div>
    </div>
    
    <!-- Modal for adding ingredient -->
    <div v-if="showAddModal" class="fixed inset-0 z-50 flex items-center justify-center bg-black/80 backdrop-blur-sm p-4">
        <div class="bg-neutral-800 p-6 rounded-2xl w-full max-w-md border border-neutral-700 shadow-2xl animate-in zoom-in duration-200">
            <h3 class="text-xl font-bold mb-6 text-white">Link Ingredient</h3>
            
            <form @submit.prevent="addRecipe" class="space-y-4">
                <div>
                    <label class="block text-xs font-medium text-neutral-400 mb-1">Select Ingredient</label>
                    <select v-model="newRecipe.ingredientId" class="w-full bg-neutral-900 border border-neutral-700 rounded-lg px-3 py-2 text-white focus:ring-2 focus:ring-primary-500 text-sm appearance-none" required>
                        <option :value="null" disabled>Select an ingredient...</option>
                        <option v-for="ing in ingredients" :key="ing.ingredientId" :value="ing.ingredientId">
                            {{ ing.name }} ({{ ing.unit }}) - Stock: {{ ing.currentStock }}
                        </option>
                    </select>
                </div>
                <div>
                    <label class="block text-xs font-medium text-neutral-400 mb-1">Quantity Needed</label>
                     <div class="relative">
                        <input v-model="newRecipe.quantityNeeded" type="number" step="0.01" min="0.01" class="w-full bg-neutral-900 border border-neutral-700 rounded-lg px-3 py-2 text-white focus:ring-2 focus:ring-primary-500 text-lg font-bold font-mono" required placeholder="0.00" />
                        <span class="absolute right-3 top-1/2 -translate-y-1/2 text-neutral-500 text-xs font-bold uppercase">{{ getSelectedIngredientUnit }}</span>
                    </div>
                    <p class="text-[10px] text-neutral-500 mt-1">Amount to deduct per 1 unit sold.</p>
                </div>
                
                <div class="flex justify-end gap-3 mt-8 pt-4 border-t border-neutral-700">
                    <button type="button" @click="showAddModal = false" class="px-4 py-2 rounded-lg text-neutral-400 hover:text-white hover:bg-neutral-700 font-bold text-sm transition-colors">Cancel</button>
                    <button type="submit" class="px-6 py-2 bg-primary-600 hover:bg-primary-500 text-white rounded-lg font-bold text-sm transition-colors shadow-lg shadow-primary-900/20">Link Ingredient</button>
                </div>
            </form>
        </div>
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

const filteredItems = computed(() => {
    if (!search.value) return menuItems.value;
    const q = search.value.toLowerCase();
    return menuItems.value.filter(item => 
        item.name.toLowerCase().includes(q) || 
        item.categoryName?.toLowerCase().includes(q)
    );
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
