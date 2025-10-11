<template>
  <div class="p-6">
    <h1 class="text-3xl font-bold mb-6">Admin Dashboard</h1>
    
    <div class="mb-6 flex gap-4">
      <button @click="activeTab = 'users'" 
              :class="['px-4 py-2 rounded', activeTab === 'users' ? 'bg-blue-600 text-white' : 'bg-gray-200']">
        Brukere
      </button>
      <button @click="activeTab = 'activities'" 
              :class="['px-4 py-2 rounded', activeTab === 'activities' ? 'bg-blue-600 text-white' : 'bg-gray-200']">
        Aktiviteter
      </button>
      <button @click="logout" class="ml-auto bg-red-600 text-white px-4 py-2 rounded hover:bg-red-700">
        Logg ut
      </button>
    </div>

    <!-- Users Tab -->
    <div v-if="activeTab === 'users'" class="bg-white p-4 rounded shadow">
      <h2 class="text-2xl font-semibold mb-4">Alle Brukere</h2>
      <div v-if="users.length === 0">Ingen brukere funnet.</div>
      <table v-else class="w-full border-collapse">
        <thead>
          <tr class="bg-gray-100">
            <th class="border p-2 text-left">ID</th>
            <th class="border p-2 text-left">Navn</th>
            <th class="border p-2 text-left">E-post</th>
            <th class="border p-2 text-left">Telefon</th>
            <th class="border p-2 text-left">Rolle</th>
            <th class="border p-2 text-left">Handlinger</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="user in users" :key="user.userId">
            <td class="border p-2">{{ user.userId }}</td>
            <td class="border p-2">{{ user.firstName }} {{ user.lastName }}</td>
            <td class="border p-2">{{ user.email }}</td>
            <td class="border p-2">{{ user.telephone }}</td>
            <td class="border p-2">
              <span :class="getRoleBadgeClass(user.role)" class="px-2 py-1 rounded text-sm">
                {{ user.role }}
              </span>
            </td>
            <td class="border p-2">
              <button @click="changeUserRole(user)" class="bg-blue-500 text-white px-3 py-1 rounded hover:bg-blue-600 text-sm">
                Endre rolle
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Activities Tab -->
    <div v-if="activeTab === 'activities'" class="bg-white p-4 rounded shadow">
      <h2 class="text-2xl font-semibold mb-4">Alle Aktiviteter</h2>
      <div v-if="activities.length === 0">Ingen aktiviteter funnet.</div>
      <ul v-else>
        <li v-for="activity in activities" :key="activity.activityId" class="border p-4 mb-2 rounded">
          <h3 class="text-xl font-semibold">{{ activity.activityType }}</h3>
          <p><strong>Sted:</strong> {{ activity.holdPlace }}</p>
          <p><strong>Beskrivelse:</strong> {{ activity.description }}</p>
          <p><strong>Start:</strong> {{ formatDate(activity.startTime) }}</p>
          <p><strong>Slutt:</strong> {{ formatDate(activity.endTime) }}</p>
          <p><strong>Påmeldte:</strong> {{ activity.registeredUsers?.length || 0 }}</p>
        </li>
      </ul>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from '@/services/api';
import { useRouter } from 'vue-router';

const router = useRouter();
const activeTab = ref('users');
const users = ref([]);
const activities = ref([]);

function formatDate(dateStr) {
  return new Date(dateStr).toLocaleString('nb-NO');
}

function getRoleBadgeClass(role) {
  if (role === 'ADMIN') return 'bg-purple-100 text-purple-800';
  if (role === 'ARANGOR') return 'bg-green-100 text-green-800';
  return 'bg-blue-100 text-blue-800';
}

async function fetchUsers() {
  try {
    const response = await axios.get('/api/users');
    users.value = response.data;
  } catch (error) {
    console.error('Kunne ikke hente brukere:', error);
    alert('Kunne ikke hente brukere');
  }
}

async function fetchActivities() {
  try {
    const response = await axios.get('/api/activities');
    activities.value = response.data;
  } catch (error) {
    console.error('Kunne ikke hente aktiviteter:', error);
    alert('Kunne ikke hente aktiviteter');
  }
}

async function changeUserRole(user) {
  const newRole = prompt(`Endre rolle for ${user.firstName} ${user.lastName}.\nNåværende rolle: ${user.role}\n\nSkriv inn ny rolle (ADMIN, ARANGOR, USER):`, user.role);
  
  if (!newRole || !['ADMIN', 'ARANGOR', 'USER'].includes(newRole.toUpperCase())) {
    alert('Ugyldig rolle');
    return;
  }

  try {
    await axios.put(`/api/users/${user.userId}/role`, { role: newRole.toUpperCase() });
    alert('Rolle oppdatert');
    await fetchUsers();
  } catch (error) {
    console.error('Kunne ikke oppdatere rolle:', error);
    alert('Kunne ikke oppdatere rolle');
  }
}

function logout() {
  localStorage.clear();
  router.push('/login');
}

onMounted(() => {
  fetchUsers();
  fetchActivities();
});
</script>
