<template>
  <div class="p-6">
    <h1 class="text-3xl font-bold mb-4">Arrangør Dashboard</h1>
    <button @click="logout" class="mb-6 bg-red-600 text-white px-4 py-2 rounded hover:bg-red-700">Logg ut</button>

    <button @click="startCreate" class="mb-4 bg-green-600 text-white px-4 py-2 rounded hover:bg-green-700">Ny aktivitet</button>

    <activity-form v-if="showForm" 
                   :activity="selectedActivity" 
                   :editMode="isEditMode" 
                   @save="saveActivity" 
                   @cancel="cancelEdit" />

    <div v-if="activities.length === 0">Ingen aktiviteter funnet.</div>

    <ul>
      <li v-for="activity in activities" :key="activity.activityId" class="border p-4 mb-2 rounded shadow">
        <h2 class="text-xl font-semibold">{{ activity.activityType }}</h2>
        <p><strong>Sted:</strong> {{ activity.holdPlace }}</p>
        <p><strong>Beskrivelse:</strong> {{ activity.description }}</p>
        <p><strong>Start:</strong> {{ formatDate(activity.startTime) }}</p>
        <p><strong>Slutt:</strong> {{ formatDate(activity.endTime) }}</p>
        <p><strong>Lokasjon ID:</strong> {{ activity.location?.id || 'N/A' }}</p>

        <button @click="editActivity(activity)" class="mr-2 bg-yellow-500 px-3 py-1 rounded hover:bg-yellow-600">Endre</button>
        <button @click="deleteActivity(activity.activityId)" class="bg-red-600 px-3 py-1 rounded hover:bg-red-700 text-white">Slett</button>
      </li>
    </ul>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from '@/services/api';
import ActivityForm from '@/components/ActivityForm.vue';
import { useRouter } from 'vue-router';

const activities = ref([]);
const showForm = ref(false);
const selectedActivity = ref(null);
const isEditMode = ref(false);
const router = useRouter();

function formatDate(dateStr) {
  return new Date(dateStr).toLocaleString();
}

async function fetchActivities() {
  try {
    const response = await axios.get('/api/activities/mine');
    activities.value = response.data;
  } catch (error) {
    alert('Kunne ikke hente aktiviteter');
  }
}

function startCreate() {
  selectedActivity.value = null;
  isEditMode.value = false;
  showForm.value = true;
}

function editActivity(activity) {
  selectedActivity.value = activity;
  isEditMode.value = true;
  showForm.value = true;
}

function cancelEdit() {
  showForm.value = false;
  selectedActivity.value = null;
}

async function saveActivity(activityData) {
  try {
    if (isEditMode.value && selectedActivity.value) {
      await axios.put(`/api/activities/${selectedActivity.value.activityId}`, activityData);
    } else {
      await axios.post('/api/activities', activityData);
    }
    showForm.value = false;
    selectedActivity.value = null;
    await fetchActivities();
  } catch (error) {
    alert('Noe gikk galt ved lagring av aktivitet');
  }
}

async function deleteActivity(id) {
  if (!confirm('Er du sikker på at du vil slette denne aktiviteten?')) return;
  try {
    await axios.delete(`/api/activities/${id}`);
    await fetchActivities();
  } catch {
    alert('Kunne ikke slette aktiviteten');
  }
}

function logout() {
  localStorage.clear();
  router.push('/login');
}

onMounted(fetchActivities);
</script>
