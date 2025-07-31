<template>
  <div class="p-6">
    <h1 class="text-3xl font-bold mb-4">Deltaker Dashboard</h1>
    <button @click="logout" class="mb-6 bg-red-600 text-white px-4 py-2 rounded hover:bg-red-700">Logg ut</button>

    <div v-if="activities.length === 0">Ingen aktiviteter tilgjengelig.</div>

    <ul>
      <li v-for="activity in activities" :key="activity.activityId" class="border p-4 mb-2 rounded shadow">
        <h2 class="text-xl font-semibold">{{ activity.activityType }}</h2>
        <p><strong>Sted:</strong> {{ activity.holdPlace }}</p>
        <p><strong>Beskrivelse:</strong> {{ activity.description }}</p>
        <p><strong>Start:</strong> {{ formatDate(activity.startTime) }}</p>
        <p><strong>Slutt:</strong> {{ formatDate(activity.endTime) }}</p>
        <button @click="meldPå(activity.activityId)" 
                class="bg-green-600 text-white px-3 py-1 rounded hover:bg-green-700"
                :disabled="loadingIds.includes(activity.activityId)">
          Meld på
        </button>
      </li>
    </ul>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from '@/services/api';
import { useRouter } from 'vue-router';

const activities = ref([]);
const loadingIds = ref([]);
const router = useRouter();

function formatDate(dateStr) {
  return new Date(dateStr).toLocaleString();
}

async function fetchActivities() {
  try {
    const response = await axios.get('/api/activities'); // Husk at backend må ha endpoint som returnerer alle aktiviteter
    activities.value = response.data;
  } catch {
    alert('Kunne ikke hente aktiviteter');
  }
}

async function meldPå(activityId) {
  if (loadingIds.value.includes(activityId)) return;
  loadingIds.value.push(activityId);

  try {
    await axios.post(`/api/activities/${activityId}/register`); // Implementer dette endpointet i backend
    alert('Du er påmeldt aktiviteten!');
  } catch {
    alert('Kunne ikke melde på aktiviteten');
  } finally {
    loadingIds.value = loadingIds.value.filter(id => id !== activityId);
  }
}

function logout() {
  localStorage.clear();
  router.push('/login');
}

onMounted(fetchActivities);
</script>
