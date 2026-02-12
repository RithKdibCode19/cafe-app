<template>
  <NuxtLayout name="admin">
    <div class="space-y-6">
      <UiBreadcrumb :items="[{ label: 'Categories' }]" />

      <!-- Header -->
      <div class="flex items-center justify-between">
        <div>
          <h2 class="text-2xl font-bold text-neutral-900 dark:text-white">
            Category Management
          </h2>
          <p class="text-sm text-neutral-500 mt-1">
            Manage your product categories and hierarchy
          </p>
        </div>
        <button
          @click="openCreateModal"
          class="bg-primary-600 hover:bg-primary-700 text-white px-4 py-2 rounded-xl text-sm font-medium transition-all flex items-center gap-2 shadow-lg shadow-primary-500/20 hover:shadow-xl hover:shadow-primary-500/30 active:scale-95"
        >
          <svg
            xmlns="http://www.w3.org/2000/svg"
            class="w-4 h-4"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            stroke-width="2"
            stroke-linecap="round"
            stroke-linejoin="round"
          >
            <path d="M5 12h14" />
            <path d="M12 5v14" />
          </svg>
          Add Category
        </button>
      </div>

      <!-- Category Tree -->
      <div v-if="loading" class="space-y-4">
        <div
          v-for="i in 3"
          :key="i"
          class="h-16 bg-neutral-100 dark:bg-neutral-800 rounded-lg animate-pulse"
        />
      </div>

      <div
        v-else-if="categories.length === 0"
        class="text-center py-12 bg-white dark:bg-neutral-900 rounded-xl border border-neutral-200 dark:border-neutral-800"
      >
        <div
          class="w-16 h-16 bg-neutral-100 dark:bg-neutral-800 rounded-full flex items-center justify-center mx-auto mb-4"
        >
          <svg
            xmlns="http://www.w3.org/2000/svg"
            class="w-8 h-8 text-neutral-400"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            stroke-width="2"
          >
            <path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20" />
            <path
              d="M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z"
            />
          </svg>
        </div>
        <h3 class="text-lg font-medium text-neutral-900 dark:text-white">
          No categories found
        </h3>
        <p class="text-neutral-500 mt-1">
          Get started by creating your first category.
        </p>
        <button
          @click="openCreateModal"
          class="mt-4 text-primary-600 hover:text-primary-700 font-medium"
        >
          Create Category
        </button>
      </div>

      <div
        v-else
        class="bg-white dark:bg-neutral-900 rounded-xl border border-neutral-200 dark:border-neutral-800 p-6"
      >
        <div class="space-y-2">
          <!-- Recursive Tree Items -->
          <CategoryTreeItem
            v-for="category in categories"
            :key="category.categoryId"
            :category="category"
            @edit="openEditModal"
            @delete="deleteCategory"
          />
        </div>
      </div>

      <!-- Create/Edit Modal -->
      <div
        v-if="showModal"
        class="fixed inset-0 z-50 flex items-center justify-center bg-black/50 backdrop-blur-sm p-4 animate-in fade-in duration-200"
        @click.self="closeModal"
      >
        <div
          class="bg-white dark:bg-neutral-900 rounded-2xl shadow-2xl w-full max-w-lg overflow-hidden border border-neutral-200 dark:border-neutral-800 flex flex-col animate-in zoom-in-95 duration-200"
        >
          <div
            class="p-6 border-b border-neutral-200 dark:border-neutral-800 flex justify-between items-center bg-white dark:bg-neutral-900"
          >
            <div>
              <h3 class="text-xl font-bold text-neutral-900 dark:text-white">
                {{ isEditing ? "Edit Category" : "New Category" }}
              </h3>
            </div>
            <button
              @click="closeModal"
              class="text-neutral-400 hover:text-neutral-600 p-2 hover:bg-neutral-100 dark:hover:bg-neutral-800 rounded-lg transition-colors"
            >
              <svg
                xmlns="http://www.w3.org/2000/svg"
                class="w-5 h-5"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                stroke-width="2"
              >
                <line x1="18" y1="6" x2="6" y2="18"></line>
                <line x1="6" y1="6" x2="18" y2="18"></line>
              </svg>
            </button>
          </div>

          <form @submit.prevent="saveCategory" class="p-6 space-y-4">
            <div>
              <label
                class="block text-sm font-medium text-neutral-700 dark:text-neutral-300 mb-1"
              >
                Category Name <span class="text-error-500">*</span>
              </label>
              <input
                v-model="form.name"
                type="text"
                required
                class="w-full bg-neutral-50 dark:bg-neutral-800 border-none rounded-lg p-2.5 text-sm ring-1 ring-neutral-200 dark:ring-neutral-700 focus:ring-2 focus:ring-primary-500 transition-shadow"
                placeholder="e.g. Beverages"
              />
            </div>

            <div>
              <label
                class="block text-sm font-medium text-neutral-700 dark:text-neutral-300 mb-1"
              >
                Description
              </label>
              <textarea
                v-model="form.description"
                rows="3"
                class="w-full bg-neutral-50 dark:bg-neutral-800 border-none rounded-lg p-2.5 text-sm ring-1 ring-neutral-200 dark:ring-neutral-700 focus:ring-2 focus:ring-primary-500 transition-shadow"
                placeholder="Optional description..."
              ></textarea>
            </div>

            <div>
              <label
                class="block text-sm font-medium text-neutral-700 dark:text-neutral-300 mb-1"
              >
                Parent Category
              </label>
              <select
                v-model="form.parentId"
                class="w-full bg-neutral-50 dark:bg-neutral-800 border-none rounded-lg p-2.5 text-sm ring-1 ring-neutral-200 dark:ring-neutral-700 focus:ring-2 focus:ring-primary-500 transition-shadow"
              >
                <option :value="null">None (Root Category)</option>
                <option
                  v-for="cat in flattenedCategories"
                  :key="cat.id"
                  :value="cat.id"
                  :disabled="
                    isEditing &&
                    (cat.id === currentId || isDescendant(cat.id, currentId))
                  "
                >
                  {{ cat.name }}
                </option>
              </select>
              <p v-if="isEditing" class="text-xs text-neutral-500 mt-1">
                Cannot select itself or its descendants as parent.
              </p>
            </div>

            <div class="pt-4 flex gap-3">
              <button
                type="button"
                @click="closeModal"
                class="flex-1 py-2.5 text-sm font-medium text-neutral-600 hover:text-neutral-900 dark:text-neutral-400 dark:hover:text-white transition-colors hover:bg-neutral-100 dark:hover:bg-neutral-800 rounded-lg"
              >
                Cancel
              </button>
              <button
                type="submit"
                :disabled="saving"
                class="flex-1 py-2.5 text-sm font-medium bg-primary-600 hover:bg-primary-700 disabled:bg-neutral-300 disabled:cursor-not-allowed text-white rounded-lg transition-all shadow-lg shadow-primary-500/20"
              >
                {{ saving ? "Saving..." : isEditing ? "Update" : "Create" }}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </NuxtLayout>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from "vue";
import CategoryTreeItem from "~/components/admin/CategoryTreeItem.vue";

definePageMeta({
  layout: false,
});

const { get, post, put, del } = useApi();
const toast = useToast();

interface Category {
  categoryId: number;
  name: string;
  description?: string;
  parentId?: number;
  children?: Category[];
}

const categories = ref<Category[]>([]);
const loading = ref(true);
const showModal = ref(false);
const isEditing = ref(false);
const saving = ref(false);
const currentId = ref<number | null>(null);

const form = reactive({
  name: "",
  description: "",
  parentId: null as number | null,
});

const fetchCategories = async () => {
  loading.value = true;
  try {
    const data = await get<Category[]>("/categories");
    categories.value = data || [];
  } catch (err) {
    console.error(err);
    toast.error("Failed to load categories");
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  fetchCategories();
});

// Flatten categories for dropdown
const flattenedCategories = computed(() => {
  const result: { id: number; name: string; parentId?: number }[] = [];

  const traverse = (cats: Category[], prefix = "") => {
    for (const cat of cats) {
      result.push({
        id: cat.categoryId,
        name: prefix + cat.name,
        parentId: cat.parentId,
      });
      if (cat.children && cat.children.length > 0) {
        traverse(cat.children, prefix + "— ");
      }
    }
  };

  traverse(categories.value);
  return result;
});

// Check if a category is a descendant of another (to prevent circular references)
const isDescendant = (
  childId: number,
  potentialAncestorId: number | null,
): boolean => {
  if (!potentialAncestorId) return false;
  // Find the child in flattened list to get its parent
  // This logic is a bit complex with just flattened list if we don't have full tree access easily by ID.
  // However, for the dropdown disable logic:
  // We want to disable X if X is a child of Current being edited.
  // Actually, we want to disable selecting `Current` or any of `Current`'s children as the `Parent`.

  // So: is `cat.id` (the option being rendered) equal to `currentId` OR is it a child/grandchild of `currentId`?

  if (childId === potentialAncestorId) return true;

  const findChildrenIds = (cats: Category[], targetId: number): number[] => {
    let ids: number[] = [];
    for (const cat of cats) {
      if (cat.categoryId === targetId) {
        // Found the target, collect all its children IDs recursively
        const collectIds = (c: Category[]) => {
          for (const child of c) {
            ids.push(child.categoryId);
            if (child.children) collectIds(child.children);
          }
        };
        if (cat.children) collectIds(cat.children);
        return ids;
      }
      if (cat.children) {
        const found = findChildrenIds(cat.children, targetId);
        if (found.length > 0) return found;
      }
    }
    return ids;
  };

  const descendants = findChildrenIds(categories.value, potentialAncestorId);
  return descendants.includes(childId);
};

const openCreateModal = () => {
  isEditing.value = false;
  currentId.value = null;
  form.name = "";
  form.description = "";
  form.parentId = null;
  showModal.value = true;
};

const openEditModal = (category: Category) => {
  isEditing.value = true;
  currentId.value = category.categoryId;
  form.name = category.name;
  form.description = category.description || "";
  form.parentId = category.parentId || null; // This might be undefined in the object if not populated, but DTO should have it. Keep in mind backend response DTO.
  showModal.value = true;
};

const closeModal = () => {
  showModal.value = false;
};

const saveCategory = async () => {
  if (!form.name) return;

  saving.value = true;
  try {
    const payload = { ...form };

    if (isEditing.value && currentId.value) {
      await put(`/categories/${currentId.value}`, payload);
      toast.success("Category updated");
    } else {
      await post("/categories/add", payload);
      toast.success("Category created");
    }

    await fetchCategories();
    closeModal();
  } catch (err) {
    console.error(err);
    toast.error("Failed to save category");
  } finally {
    saving.value = false;
  }
};

const deleteCategory = async (id: number) => {
  if (
    !confirm(
      "Are you sure? This will delete the category and all sub-categories.",
    )
  )
    return;

  try {
    await del(`/categories/${id}`);
    toast.success("Category deleted");
    await fetchCategories();
  } catch (err) {
    console.error(err);
    toast.error("Failed to delete category");
  }
};
</script>
