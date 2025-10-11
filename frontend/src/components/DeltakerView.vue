<template>
  <div class="p-6">
    <h1 class="text-3xl font-bold mb-4">Deltaker Dashboard</h1>
    <button @click="logout" class="mb-6 bg-red-600 text-white px-4 py-2 rounded hover:bg-red-700">Logg ut</button>

    <!-- Added notification banner for conflicts -->
    <div v-if="conflictMessage" class="mb-6 bg-yellow-100 border-l-4 border-yellow-500 text-yellow-700 p-4 rounded">
      <div class="flex items-start">
        <svg class="w-6 h-6 mr-3 flex-shrink-0" fill="currentColor" viewBox="0 0 20 20">
          <path fill-rule="evenodd" d="M8.257 3.099c.765-1.36 2.722-1.36 3.486 0l5.58 9.92c.75 1.334-.213 2.98-1.742 2.98H4.42c-1.53 0-2.493-1.646-1.743-2.98l5.58-9.92zM11 13a1 1 0 11-2 0 1 1 0 012 0zm-1-8a1 1 0 00-1 1v3a1 1 0 002 0V6a1 1 0 00-1-1z" clip-rule="evenodd"/>
        </svg>
        <div class="flex-1">
          <p class="font-bold">Tidskonflikt!</p>
          <p class="text-sm">{{ conflictMessage }}</p>
        </div>
        <button @click="conflictMessage = ''" class="ml-4 text-yellow-700 hover:text-yellow-900">
          <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 20 20">
            <path fill-rule="evenodd" d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 011.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z" clip-rule="evenodd"/>
          </svg>
        </button>
      </div>
    </div>

    <div v-if="activities.length === 0" class="text-gray-500">Ingen aktiviteter tilgjengelig.</div>

    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
      <div v-for="activity in activities" :key="activity.activityId" class="border p-6 rounded-lg shadow-md hover:shadow-lg transition-shadow bg-white">
        <h2 class="text-xl font-semibold mb-3 text-blue-700">{{ activity.activityType }}</h2>
        <div class="space-y-2 mb-4">
          <p class="text-gray-700"><strong>Sted:</strong> {{ activity.holdPlace }}</p>
          <p class="text-gray-600">{{ activity.description }}</p>
          <p class="text-sm text-gray-500"><strong>Start:</strong> {{ formatDate(activity.startTime) }}</p>
          <p class="text-sm text-gray-500"><strong>Slutt:</strong> {{ formatDate(activity.endTime) }}</p>
        </div>
        <button @click="meldPå(activity.activityId)" 
                class="w-full bg-green-600 text-white px-4 py-2 rounded hover:bg-green-700 transition-colors"
                :disabled="loadingIds.includes(activity.activityId)">
          {{ loadingIds.includes(activity.activityId) ? 'Melder på...' : 'Meld på' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from '@/services/api';
import { useRouter } from 'vue-router';

const activities = ref([]);
const loadingIds = ref([]);
const conflictMessage = ref('');
const router = useRouter();

function formatDate(dateStr) {
  return new Date(dateStr).toLocaleString('nb-NO');
}

async function fetchActivities() {
  console.log('[v0] Fetching activities from /api/activities');
  console.log('[v0] Token:', localStorage.getItem('token') ? 'Present' : 'Missing');
  
  try {
    const response = await axios.get('/api/activities');
    console.log('[v0] Activities response:', response.data);
    activities.value = response.data;
    console.log('[v0] Activities loaded:', activities.value.length);
  } catch (error) {
    console.error('[v0] Error fetching activities:', error);
    console.error('[v0] Error response:', error.response?.data);
    console.error('[v0] Error status:', error.response?.status);
    alert('Kunne ikke hente aktiviteter: ' + (error.response?.data?.message || error.message));
  }
}

async function meldPå(activityId) {
  if (loadingIds.value.includes(activityId)) return;
  loadingIds.value.push(activityId);

  conflictMessage.value = '';

  console.log('[v0] Registering for activity:', activityId);

  try {
    await axios.post(`/api/activities/${activityId}/register`);
    console.log('[v0] Successfully registered for activity:', activityId);
    alert('Du er påmeldt aktiviteten!');
  } catch (error) {
    console.error('[v0] Error registering for activity:', error);
    console.error('[v0] Error response:', error.response?.data);
    
    const errorMessage = error.response?.data?.message || error.message;
    if (errorMessage.includes('kolliderer')) {
      conflictMessage.value = errorMessage;
      // Scroll to top to show the notification
      window.scrollTo({ top: 0, behavior: 'smooth' });
    } else {
      alert('Kunne ikke melde på aktiviteten: ' + errorMessage);
    }
  } finally {
    loadingIds.value = loadingIds.value.filter(id => id !== activityId);
  }
}

function logout() {
  localStorage.clear();
  router.push('/login');
}

onMounted(() => {
  console.log('[v0] DeltakerView mounted, fetching activities');
  fetchActivities();
});
</script>
