<template>
  <div class="p-6">
    <h1 class="text-3xl font-bold mb-4">Arrangør Dashboard</h1>

    <button @click="startCreate" class="mb-4 bg-green-600 text-white px-4 py-2 rounded hover:bg-green-700">Ny aktivitet</button>

    <activity-form v-if="showForm" 
                   :activity="selectedActivity" 
                   :editMode="isEditMode" 
                   @save="handleSave" 
                   @cancel="cancelEdit" />

    <div v-if="!showForm" class="mb-6">
      <input 
        v-model="searchQuery"
        type="text"
        placeholder="Søk etter aktivitet (tittel)..."
        class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
      />
    </div>

    <div v-if="!showForm && filteredActivities.length === 0 && searchQuery" class="text-gray-600 mb-4">
      Ingen aktiviteter funnet for søket "{{ searchQuery }}".
    </div>
    <div v-if="!showForm && activities.length === 0" class="text-gray-600 mb-4">
      Ingen aktiviteter funnet.
    </div>

    <div v-if="!showForm && filteredActivities.length > 0" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
      <div v-for="activity in filteredActivities" :key="activity.activityId" 
           class="border border-gray-200 rounded-lg shadow-md p-5 hover:shadow-lg transition-shadow bg-white">
        <!-- Updated to show organizer name or indicator -->
        <div v-if="!isOwnActivity(activity)" class="mb-2">
          <span class="inline-block bg-blue-100 text-blue-800 text-xs px-2 py-1 rounded">
            Arrangør: {{ activity.organizer?.firstName }} {{ activity.organizer?.lastName }}
          </span>
        </div>
        <div v-else class="mb-2">
          <span class="inline-block bg-green-100 text-green-800 text-xs px-2 py-1 rounded">
            Din aktivitet
          </span>
        </div>

        <h2 class="text-xl font-semibold mb-3 text-gray-800">{{ activity.activityType }}</h2>
        <div class="space-y-2 mb-4">
          <p class="text-gray-700"><strong>Sted:</strong> {{ activity.holdPlace }}</p>
          <p class="text-gray-600"><strong>Beskrivelse:</strong> {{ activity.description }}</p>
          <p class="text-gray-700"><strong>Start:</strong> {{ formatDate(activity.startTime) }}</p>
          <p class="text-gray-700"><strong>Slutt:</strong> {{ formatDate(activity.endTime) }}</p>
          <p class="text-gray-600"><strong>Lokasjon ID:</strong> {{ activity.location?.id || 'N/A' }}</p>
        </div>

        <!-- Only show edit/delete buttons for activities owned by current user -->
        <div v-if="isOwnActivity(activity)" class="flex gap-2 mt-4">
          <button @click="editActivity(activity)" 
                  class="flex-1 bg-yellow-500 text-white px-3 py-2 rounded hover:bg-yellow-600 transition-colors">
            Endre
          </button>
          <button @click="deleteActivity(activity.activityId)" 
                  class="flex-1 bg-red-600 text-white px-3 py-2 rounded hover:bg-red-700 transition-colors">
            Slett
          </button>
        </div>
        <!-- Show read-only message for other organizers' activities -->
        <div v-else class="mt-4 text-sm text-gray-500 italic">
          Kun synlig - du kan ikke endre denne aktiviteten
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
import { useAuth } from '@/composables/useAuth';

const activities = ref([]);
const showForm = ref(false);
const selectedActivity = ref(null);
const isEditMode = ref(false);
const router = useRouter();
const searchQuery = ref('');
const auth = useAuth();

const fetchActivities = async () => {
  try {
    const response = await axios.get('/api/activities');
    activities.value = response.data;
  } catch {
    alert('Kunne ikke hente aktiviteter');
  }
};

const computedCurrentEmail = computed(() => {
  return (auth.email || '').trim().toLowerCase();
});

function isOwnActivity(activity) {
  const userEmail = computedCurrentEmail.value;
  const organizerEmail = (activity.organizer?.email || '').trim().toLowerCase();
  return userEmail && organizerEmail && userEmail === organizerEmail;
}

function formatDate(dateStr) {
  return new Date(dateStr).toLocaleString();
}

function startCreate() {
  selectedActivity.value = null;
  isEditMode.value = false;
  showForm.value = true;
}

function editActivity(activity) {
  if (!isOwnActivity(activity)) {
    alert('Du kan ikke endre denne aktiviteten');
    return;
  }
  selectedActivity.value = activity;
  isEditMode.value = true;
  showForm.value = true;
}

function cancelEdit() {
  showForm.value = false;
  selectedActivity.value = null;
}

async function handleSave(activityData) {
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
    const errorMessage = error.response?.data?.message || error.response?.data || 'Noe gikk galt ved lagring av aktivitet';
    alert(errorMessage);
  }
}

async function deleteActivity(id) {
  if (!confirm('Er du sikker på at du vil slette denne aktiviteten?')) return;
  try {
    await axios.delete(`/api/activities/${id}`);
    await fetchActivities();
  } catch (error) {
    const errorMessage = error.response?.data?.message || error.response?.data || 'Kunne ikke slette aktiviteten';
    alert(errorMessage);
  }
}

onMounted(() => {
  fetchActivities();
});

const filteredActivities = computed(() => {
  if (!searchQuery.value) {
    return activities.value;
  }
  const query = searchQuery.value.toLowerCase();
  return activities.value.filter(activity => 
    activity.activityType.toLowerCase().includes(query)
  );
});
</script>
