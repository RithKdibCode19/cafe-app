<script setup lang="ts">
const props = defineProps<{
  modelValue?: string;
  label?: string;
}>();

const emit = defineEmits<{
  (e: 'update:modelValue', value: string): void;
}>();

const uploading = ref(false);
const previewUrl = ref(props.modelValue || '');
const fileInput = ref<HTMLInputElement | null>(null);

watch(() => props.modelValue, (newVal) => {
  if (newVal) previewUrl.value = newVal;
});

const handleFileSelect = async (event: Event) => {
  const target = event.target as HTMLInputElement;
  if (!target.files?.length) return;

  const file = target.files[0];
  await uploadFile(file);
};

const handleDrop = async (event: DragEvent) => {
  const file = event.dataTransfer?.files[0];
  if (file && file.type.startsWith('image/')) {
    await uploadFile(file);
  }
};

const uploadFile = async (file: File) => {
  uploading.value = true;
  
  // Local preview
  const reader = new FileReader();
  reader.onload = (e) => {
    previewUrl.value = e.target?.result as string;
  };
  reader.readAsDataURL(file);

  try {
    const formData = new FormData();
    formData.append('file', file);

    const { fetchApi } = useApi();
    const { url } = await fetchApi<{ url: string }>('/upload', {
      method: 'POST',
      body: formData,
    });

    emit('update:modelValue', url);
    previewUrl.value = url;
  } catch (error) {
    console.error('Upload failed:', error);
    // Reset preview on failure if needed
  } finally {
    uploading.value = false;
  }
};

const triggerSelect = () => {
  fileInput.value?.click();
};

const removeImage = () => {
  emit('update:modelValue', '');
  previewUrl.value = '';
};
</script>

<template>
  <div class="space-y-2">
    <label v-if="label" class="text-sm font-semibold text-neutral-300">{{ label }}</label>
    
    <div 
      class="relative group cursor-pointer"
      @dragover.prevent
      @drop.prevent="handleDrop"
      @click="triggerSelect"
    >
      <!-- Upload Box -->
      <div 
        class="w-full aspect-video rounded-2xl border-2 border-dashed transition-all duration-300 flex flex-col items-center justify-center gap-3 overflow-hidden bg-neutral-900/50"
        :class="[
          previewUrl ? 'border-transparent' : 'border-neutral-700 hover:border-primary-500/50 hover:bg-neutral-800/50'
        ]"
      >
        <template v-if="previewUrl">
          <img :src="previewUrl" class="w-full h-full object-cover transition-transform duration-500 group-hover:scale-110" />
          <div class="absolute inset-0 bg-black/40 opacity-0 group-hover:opacity-100 transition-opacity flex items-center justify-center gap-4">
            <div class="bg-white/10 backdrop-blur-md p-3 rounded-xl border border-white/20">
              <svg xmlns="http://www.w3.org/2000/svg" class="w-6 h-6 text-white" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/><polyline points="17 8 12 3 7 8"/><line x1="12" x2="12" y1="3" y2="15"/></svg>
            </div>
            <button 
              @click.stop="removeImage"
              class="bg-red-500/80 backdrop-blur-md p-3 rounded-xl border border-red-400/20 hover:bg-red-500 transition-colors"
            >
              <svg xmlns="http://www.w3.org/2000/svg" class="w-6 h-6 text-white" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M3 6h18"/><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6"/><path d="M8 6V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/><line x1="10" x2="10" y1="11" y2="17"/><line x1="14" x2="14" y1="11" y2="17"/></svg>
            </button>
          </div>
        </template>
        
        <template v-else>
          <div class="w-16 h-16 rounded-full bg-neutral-800 flex items-center justify-center group-hover:bg-primary-500/10 transition-colors">
            <svg xmlns="http://www.w3.org/2000/svg" class="w-8 h-8 text-neutral-500 group-hover:text-primary-500" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><rect width="18" height="18" x="3" y="3" rx="2" ry="2"/><circle cx="9" cy="9" r="2"/><path d="m21 15-3.086-3.086a2 2 0 0 0-2.828 0L6 21"/></svg>
          </div>
          <div class="text-center">
            <p class="text-sm font-bold text-neutral-300">Click or drag image to upload</p>
            <p class="text-xs text-neutral-500 mt-1">PNG, JPG or WEBP (Max. 10MB)</p>
          </div>
        </template>

        <!-- Loading Overlay -->
        <div v-if="uploading" class="absolute inset-0 bg-neutral-900/80 backdrop-blur-sm flex flex-col items-center justify-center gap-3 z-10">
          <div class="w-10 h-10 border-2 border-primary-500/20 border-t-primary-500 rounded-full animate-spin"></div>
          <span class="text-xs font-bold text-white uppercase tracking-widest">Uploading to Cloudinary...</span>
        </div>
      </div>
    </div>

    <input 
      ref="fileInput"
      type="file" 
      class="hidden" 
      accept="image/*"
      @change="handleFileSelect"
    />
  </div>
</template>
