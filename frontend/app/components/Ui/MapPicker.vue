<template>
  <div class="relative">
    <div ref="mapContainer" class="w-full h-64 rounded-xl overflow-hidden border border-neutral-200 dark:border-neutral-700"></div>
    
    <!-- Coordinates display -->
    <div v-if="modelValue.lat && modelValue.lng" class="absolute bottom-2 left-2 bg-black/70 text-white text-xs px-2 py-1 rounded-lg">
      {{ modelValue.lat.toFixed(6) }}, {{ modelValue.lng.toFixed(6) }}
    </div>
    
    <!-- Instructions -->
    <div v-else class="absolute inset-0 flex items-center justify-center bg-neutral-900/50 rounded-xl">
      <p class="text-white text-sm">Click on map to set location</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch, onUnmounted } from 'vue'
import L from 'leaflet'
import 'leaflet/dist/leaflet.css'

interface Location {
  lat: number | null
  lng: number | null
}

const props = defineProps<{
  modelValue: Location
  radius?: number
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', value: Location): void
}>()

const mapContainer = ref<HTMLElement | null>(null)
let map: L.Map | null = null
let marker: L.Marker | null = null
let radiusCircle: L.Circle | null = null

// Fix Leaflet default marker icon
const defaultIcon = L.icon({
  iconUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon.png',
  iconRetinaUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon-2x.png',
  shadowUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-shadow.png',
  iconSize: [25, 41],
  iconAnchor: [12, 41],
  popupAnchor: [1, -34],
  shadowSize: [41, 41]
})

const initMap = () => {
  if (!mapContainer.value) return

  // Default center (Phnom Penh, Cambodia)
  const defaultLat = props.modelValue.lat || 11.5564
  const defaultLng = props.modelValue.lng || 104.9282

  map = L.map(mapContainer.value).setView([defaultLat, defaultLng], 15)

  // Add OpenStreetMap tiles
  L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: '© OpenStreetMap'
  }).addTo(map)

  // Add marker if coordinates exist
  if (props.modelValue.lat && props.modelValue.lng) {
    addMarker(props.modelValue.lat, props.modelValue.lng)
  }

  // Click handler
  map.on('click', (e: L.LeafletMouseEvent) => {
    const { lat, lng } = e.latlng
    emit('update:modelValue', { lat, lng })
    addMarker(lat, lng)
  })
}

const addMarker = (lat: number, lng: number) => {
  if (!map) return

  // Remove existing marker
  if (marker) {
    map.removeLayer(marker)
  }
  if (radiusCircle) {
    map.removeLayer(radiusCircle)
  }

  // Add new marker
  marker = L.marker([lat, lng], { 
    icon: defaultIcon,
    draggable: true 
  }).addTo(map)

  // Drag handler
  marker.on('dragend', () => {
    const pos = marker?.getLatLng()
    if (pos) {
      emit('update:modelValue', { lat: pos.lat, lng: pos.lng })
      updateRadiusCircle(pos.lat, pos.lng)
    }
  })

  // Add radius circle
  updateRadiusCircle(lat, lng)
}

const updateRadiusCircle = (lat: number, lng: number) => {
  if (!map) return

  if (radiusCircle) {
    map.removeLayer(radiusCircle)
  }

  const radius = props.radius || 100
  radiusCircle = L.circle([lat, lng], {
    radius,
    color: '#3b82f6',
    fillColor: '#3b82f6',
    fillOpacity: 0.15,
    weight: 2
  }).addTo(map)
}

// Watch for external changes
watch(() => props.modelValue, (newVal) => {
  if (newVal.lat && newVal.lng && map) {
    addMarker(newVal.lat, newVal.lng)
    map.setView([newVal.lat, newVal.lng], map.getZoom())
  }
}, { deep: true })

watch(() => props.radius, () => {
  if (props.modelValue.lat && props.modelValue.lng) {
    updateRadiusCircle(props.modelValue.lat, props.modelValue.lng)
  }
})

onMounted(() => {
  initMap()
})

onUnmounted(() => {
  if (map) {
    map.remove()
    map = null
  }
})
</script>

<style>
.leaflet-control-attribution {
  font-size: 8px !important;
}
</style>
