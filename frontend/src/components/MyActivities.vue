<template>
  <div class="p-6">
    <h1 class="text-3xl font-bold mb-4">Mine Aktiviteter</h1>

    <div v-if="loading">Laster aktiviteter...</div>
    <div v-else-if="activities.length === 0">Ingen aktiviteter funnet.</div>

    <ul v-else>
      <li v-for="activity in activities" :key="activity.activityId" class="border p-4 mb-2 rounded shadow">
        <h2 class="text-xl font-semibold">{{ activity.activityType }}</h2>
        <p><strong>Sted:</strong> {{ activity.holdPlace }}</p>
        <p><strong>Beskrivelse:</strong> {{ activity.description }}</p>
        <p><strong>Start:</strong> {{ formatDate(activity.startTime) }}</p>
        <p><strong>Slutt:</strong> {{ formatDate(activity.endTime) }}</p>
        <p><strong>Lokasjon:</strong> {{ activity.location?.name || 'N/A' }}</p>
      </li>
    </ul>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from '@/services/api';

const activities = ref([]);
const loading = ref(false);

function formatDate(dateStr) {
  return new Date(dateStr).toLocaleString();
}

async function fetchMyActivities() {
  loading.value = true;
  try {
    const res = await axios.get('/api/activities/mine');
    activities.value = res.data;
  } catch (err) {
    alert('Kunne ikke hente aktiviteter');
  } finally {
    loading.value = false;
  }
}

onMounted(fetchMyActivities);
</script>
