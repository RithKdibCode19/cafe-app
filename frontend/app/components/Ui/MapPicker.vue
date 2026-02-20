<template>
  <div>
    <Teleport to="body" :disabled="!isMaximized">
      <div
        class="relative"
        :class="{
          'fixed inset-0 z-[9999] bg-white dark:bg-neutral-900 p-4 flex flex-col':
            isMaximized,
        }"
      >
        <div
          ref="mapContainer"
          class="w-full rounded-xl overflow-hidden border border-neutral-200 dark:border-neutral-700 transition-all duration-300"
          :class="isMaximized ? 'flex-1 h-full' : 'h-64'"
        ></div>

        <!-- Controls -->
        <div class="absolute top-2 right-2 z-[1000] flex gap-2">
          <button
            @click.prevent="toggleMaximize"
            class="bg-white dark:bg-neutral-800 text-neutral-600 dark:text-neutral-300 p-2 rounded-lg shadow-lg hover:bg-neutral-50 dark:hover:bg-neutral-700 transition-colors"
            :title="isMaximized ? 'Minimize' : 'Maximize'"
          >
            <svg
              v-if="isMaximized"
              xmlns="http://www.w3.org/2000/svg"
              class="w-5 h-5"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              stroke-width="2"
            >
              <path d="M8 3v3a2 2 0 0 1-2 2H3" />
              <path d="M21 8h-3a2 2 0 0 1-2-2V3" />
              <path d="M3 16h3a2 2 0 0 1 2 2v3" />
              <path d="M16 21v-3a2 2 0 0 1 2-2h3" />
            </svg>
            <svg
              v-else
              xmlns="http://www.w3.org/2000/svg"
              class="w-5 h-5"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              stroke-width="2"
            >
              <path d="M15 3h6v6" />
              <path d="M9 21H3v-6" />
              <path d="M21 3l-7 7" />
              <path d="M3 21l7-7" />
            </svg>
          </button>
        </div>

        <!-- Coordinates display -->
        <div
          v-if="modelValue.lat && modelValue.lng"
          class="absolute bottom-2 left-2 z-[1000] bg-black/70 text-white text-xs px-2 py-1 rounded-lg pointer-events-none"
        >
          {{ modelValue.lat.toFixed(6) }}, {{ modelValue.lng.toFixed(6) }}
        </div>

        <!-- Instructions - pointer-events-none so clicks pass through to map -->
        <div
          v-else
          class="absolute inset-0 flex items-center justify-center bg-neutral-900/30 rounded-xl pointer-events-none z-[999]"
        >
          <p class="text-white text-sm font-medium drop-shadow-lg">
            Click on map to set location
          </p>
        </div>

        <!-- Close button (only in maximized mode) -->
        <button
          v-if="isMaximized"
          @click="toggleMaximize"
          class="absolute bottom-4 right-4 z-[1000] btn-primary shadow-xl"
        >
          Done
        </button>
      </div>
    </Teleport>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch, onUnmounted, nextTick } from "vue";

interface Location {
  lat: number | null;
  lng: number | null;
}

const props = defineProps<{
  modelValue: Location;
  radius?: number;
}>();

const emit = defineEmits<{
  (e: "update:modelValue", value: Location): void;
}>();

const mapContainer = ref<HTMLElement | null>(null);
const isMaximized = ref(false);
let L: any = null;
let map: any = null;
let marker: any = null;
let radiusCircle: any = null;
let defaultIcon: any = null;

const toggleMaximize = async () => {
  isMaximized.value = !isMaximized.value;

  // Wait for the DOM to update and transition to complete
  await nextTick();
  
  // Give time for the teleport and CSS transitions to finish
  setTimeout(() => {
    if (map && mapContainer.value) {
      // Force map to recalculate its size multiple times
      map.invalidateSize(true);
      
      // Force a browser reflow
      const _ = mapContainer.value.offsetHeight;
      
      // Invalidate again after reflow
      setTimeout(() => {
        if (map) {
          map.invalidateSize(true);
          
          // Re-center the map
          const lat = props.modelValue.lat || 11.5564;
          const lng = props.modelValue.lng || 104.9282;
          map.setView([lat, lng], map.getZoom());
        }
      }, 50);
    }
  }, 350);
};

const reinitMap = () => {
  if (map) {
    map.remove();
    map = null;
    marker = null;
    radiusCircle = null;
  }
  initMap();
};

const initMap = async () => {
  if (!mapContainer.value || typeof window === "undefined") return;

  // Dynamically import Leaflet (client-side only)
  L = await import("leaflet");
  await import("leaflet/dist/leaflet.css");

  // Fix Leaflet default marker icon
  defaultIcon = L.icon({
    iconUrl: "https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon.png",
    iconRetinaUrl:
      "https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon-2x.png",
    shadowUrl: "https://unpkg.com/leaflet@1.9.4/dist/images/marker-shadow.png",
    iconSize: [25, 41],
    iconAnchor: [12, 41],
    popupAnchor: [1, -34],
    shadowSize: [41, 41],
  });

  // Default center (Phnom Penh, Cambodia)
  const defaultLat = props.modelValue.lat || 11.5564;
  const defaultLng = props.modelValue.lng || 104.9282;

  map = L.map(mapContainer.value).setView([defaultLat, defaultLng], 15);

  // Add OpenStreetMap tiles
  L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
    attribution: "© OpenStreetMap",
  }).addTo(map);

  // Add marker if coordinates exist
  if (props.modelValue.lat && props.modelValue.lng) {
    addMarker(props.modelValue.lat, props.modelValue.lng);
  }

  // Click handler
  map.on("click", (e: any) => {
    const lat = Number(e.latlng.lat.toFixed(6));
    const lng = Number(e.latlng.lng.toFixed(6));
    emit("update:modelValue", { lat, lng });
    addMarker(lat, lng);
  });
};

const addMarker = (lat: number, lng: number) => {
  if (!map || !L) return;

  // Remove existing marker
  if (marker) {
    map.removeLayer(marker);
  }
  if (radiusCircle) {
    map.removeLayer(radiusCircle);
  }

  // Add new marker
  marker = L.marker([lat, lng], {
    icon: defaultIcon,
    draggable: true,
  }).addTo(map);

  // Drag handler
  marker.on("dragend", () => {
    const pos = marker?.getLatLng();
    if (pos) {
      const lat = Number(pos.lat.toFixed(6));
      const lng = Number(pos.lng.toFixed(6));
      emit("update:modelValue", { lat, lng });
      updateRadiusCircle(lat, lng);
    }
  });

  // Add radius circle
  updateRadiusCircle(lat, lng);
};

const updateRadiusCircle = (lat: number, lng: number) => {
  if (!map || !L) return;

  if (radiusCircle) {
    map.removeLayer(radiusCircle);
  }

  const radius = props.radius || 100;
  radiusCircle = L.circle([lat, lng], {
    radius,
    color: "#3b82f6",
    fillColor: "#3b82f6",
    fillOpacity: 0.15,
    weight: 2,
  }).addTo(map);
};

// Watch for external changes
watch(
  () => props.modelValue,
  (newVal) => {
    if (newVal.lat && newVal.lng && map) {
      addMarker(newVal.lat, newVal.lng);
      map.setView([newVal.lat, newVal.lng], map.getZoom());
    }
  },
  { deep: true },
);

watch(
  () => props.radius,
  () => {
    if (props.modelValue.lat && props.modelValue.lng) {
      updateRadiusCircle(props.modelValue.lat, props.modelValue.lng);
    }
  },
);

onMounted(() => {
  initMap();
});

onUnmounted(() => {
  if (map) {
    map.remove();
    map = null;
  }
});
</script>

<style>
.leaflet-control-attribution {
  font-size: 8px !important;
}
</style>
