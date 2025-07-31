<template> 
  <div class="border p-4 rounded shadow mb-4">
    <h2 class="text-xl font-semibold mb-2">{{ editMode ? 'Rediger aktivitet' : 'Ny aktivitet' }}</h2>

    <form @submit.prevent="submitForm">
      <div class="mb-2">
        <label>Type aktivitet:</label>
        <input v-model="form.activityType" required class="w-full border p-2 rounded" />
      </div>
      <div class="mb-2">
        <label>Adresse (holdPlace):</label>
        <input v-model="form.holdPlace" required class="w-full border p-2 rounded" placeholder="Skriv adresse her" />
      </div>
      <div class="mb-2">
        <label>Beskrivelse:</label>
        <textarea v-model="form.description" required class="w-full border p-2 rounded"></textarea>
      </div>
      <div class="mb-2">
        <label>Starttid:</label>
        <input type="datetime-local" v-model="form.startTime" required class="w-full border p-2 rounded" />
      </div>
      <div class="mb-2">
        <label>Sluttid:</label>
        <input type="datetime-local" v-model="form.endTime" required class="w-full border p-2 rounded" />
      </div>
      <div class="mb-4">
        <label>Lokasjon ID:</label>
        <input v-model.number="form.locationId" type="number" required class="w-full border p-2 rounded" />
      </div>

      <button type="submit" class="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700 mr-2">
        {{ editMode ? 'Oppdater' : 'Lagre' }}
      </button>
      <button type="button" @click="$emit('cancel')" class="bg-gray-400 text-white px-4 py-2 rounded hover:bg-gray-500">
        Avbryt
      </button>
    </form>
  </div>
</template>

<script setup>
import { reactive, watch } from 'vue';

const props = defineProps({
  activity: Object,
  editMode: Boolean
});

const emit = defineEmits(['save', 'cancel']);

const form = reactive({
  activityType: '',
  holdPlace: '',
  description: '',
  startTime: '',
  endTime: '',
  locationId: null
});

watch(
  () => props.activity,
  (newActivity) => {
    if (newActivity) {
      form.activityType = newActivity.activityType || '';

      // HoldPlace som string, bruk teksten direkte hvis mulig
      form.holdPlace = typeof newActivity.holdPlace === 'string' ? newActivity.holdPlace : '';

      form.description = newActivity.description || '';
      form.startTime = newActivity.startTime ? newActivity.startTime.slice(0, 16) : '';
      form.endTime = newActivity.endTime ? newActivity.endTime.slice(0, 16) : '';
      form.locationId = newActivity.location?.id || null;
    } else {
      form.activityType = '';
      form.holdPlace = '';
      form.description = '';
      form.startTime = '';
      form.endTime = '';
      form.locationId = null;
    }
  },
  { immediate: true }
);

function submitForm() {
  emit('save', {
    activityType: form.activityType,
    holdPlace: form.holdPlace,
    description: form.description,
    startTime: form.startTime,
    endTime: form.endTime,
    locationId: form.locationId
  });
}
</script>
