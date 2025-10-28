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

    <!-- Added search input for filtering activities by title -->
    <div v-if="!showForm" class="mb-6">
      <input 
        v-model="searchQuery"
        type="text"
        placeholder="Søk etter aktivitet (tittel)..."
        class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
      />
    </div>

    <!-- Updated to show filtered activities count -->
    <div v-if="!showForm && filteredActivities.length === 0 && searchQuery" class="text-gray-600 mb-4">
      Ingen aktiviteter funnet for søket "{{ searchQuery }}".
    </div>
    <div v-if="!showForm && activities.length === 0" class="text-gray-600 mb-4">
      Ingen aktiviteter funnet.
    </div>

    <!-- Converted list to responsive grid layout -->
    <div v-if="!showForm && filteredActivities.length > 0" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
      <div v-for="activity in filteredActivities" :key="activity.activityId" 
           class="border border-gray-200 rounded-lg shadow-md p-5 hover:shadow-lg transition-shadow bg-white">
        <h2 class="text-xl font-semibold mb-3 text-gray-800">{{ activity.activityType }}</h2>
        <div class="space-y-2 mb-4">
          <p class="text-gray-700"><strong>Sted:</strong> {{ activity.holdPlace }}</p>
          <p class="text-gray-600"><strong>Beskrivelse:</strong> {{ activity.description }}</p>
          <p class="text-gray-700"><strong>Start:</strong> {{ formatDate(activity.startTime) }}</p>
          <p class="text-gray-700"><strong>Slutt:</strong> {{ formatDate(activity.endTime) }}</p>
          <p class="text-gray-600"><strong>Lokasjon ID:</strong> {{ activity.location?.id || 'N/A' }}</p>
        </div>

        <div class="flex gap-2 mt-4">
          <button @click="editActivity(activity)" 
                  class="flex-1 bg-yellow-500 text-white px-3 py-2 rounded hover:bg-yellow-600 transition-colors">
            Endre
          </button>
          <button @click="deleteActivity(activity.activityId)" 
                  class="flex-1 bg-red-600 text-white px-3 py-2 rounded hover:bg-red-700 transition-colors">
            Slett
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import axios from '@/services/api';
import ActivityForm from '@/components/ActivityForm.vue';
import { useRouter } from 'vue-router';

const activities = ref([]);
const showForm = ref(false);
const selectedActivity = ref(null);
const isEditMode = ref(false);
const router = useRouter();
const searchQuery = ref('');

const filteredActivities = computed(() => {
  if (!searchQuery.value) {
    return activities.value;
  }
  const query = searchQuery.value.toLowerCase();
  return activities.value.filter(activity => 
    activity.activityType.toLowerCase().includes(query)
  );
});

function formatDate(dateStr) {
  return new Date(dateStr).toLocaleString();
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

const fetchActivities = async () => {
  try {
    const response = await axios.get('/api/activities');
    activities.value = response.data;
  } catch {
    alert('Kunne ikke hente aktiviteter');
  }
};

onMounted(fetchActivities);
</script>
