<template>
  <div class="p-6">
    <!-- Added notification banner for conflicts -->
    <div v-if="conflictMessage" class="mb-6 bg-yellow-100 border-l-4 border-yellow-500 text-yellow-700 p-4 rounded">
      <div class="flex items-start">
        <div class="flex-1">
          <p class="font-bold">Tidskonflikt!</p>
          <p class="text-sm">{{ conflictMessage }}</p>
        </div>
        <button @click="conflictMessage = ''" class="ml-4 text-yellow-700 hover:text-yellow-900">
          ✕
        </button>
      </div>
    </div>

    <h1 class="text-3xl font-bold mb-4">Deltaker Dashboard</h1>
    <button @click="logout" class="mb-6 bg-red-600 text-white px-4 py-2 rounded hover:bg-red-700">Logg ut</button>

    <div v-if="activities.length === 0" class="text-gray-500">Ingen aktiviteter tilgjengelig.</div>

    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
      <div v-for="activity in activities" :key="activity.activityId" class="border p-6 rounded-lg shadow-md hover:shadow-lg transition-shadow bg-white">
        <!-- Added badge showing registration status -->
        <div class="flex justify-between items-start mb-3">
          <h2 class="text-xl font-semibold text-blue-700">{{ activity.activityType }}</h2>
          <span v-if="isUserRegistered(activity.activityId)" class="bg-green-200 text-green-800 text-xs font-semibold px-3 py-1 rounded-full">
            Du er påmeldt
          </span>
        </div>
        <div class="space-y-2 mb-4">
          <p class="text-gray-700"><strong>Sted:</strong> {{ activity.holdPlace }}</p>
          <p class="text-gray-600">{{ activity.description }}</p>
          <p class="text-sm text-gray-500"><strong>Start:</strong> {{ formatDate(activity.startTime) }}</p>
          <p class="text-sm text-gray-500"><strong>Slutt:</strong> {{ formatDate(activity.endTime) }}</p>
        </div>
        
        <!-- Show different buttons based on registration status -->
        <div class="flex gap-2">
          <button 
            v-if="!isUserRegistered(activity.activityId)"
            @click="meldPå(activity.activityId)" 
            class="flex-1 bg-green-600 text-white px-4 py-2 rounded hover:bg-green-700 transition-colors"
            :disabled="loadingIds.includes(activity.activityId)">
            {{ loadingIds.includes(activity.activityId) ? 'Melder på...' : 'Meld på' }}
          </button>
          <button 
            v-else
            @click="meldAv(activity.activityId)" 
            class="flex-1 bg-red-600 text-white px-4 py-2 rounded hover:bg-red-700 transition-colors"
            :disabled="loadingIds.includes(activity.activityId)">
            {{ loadingIds.includes(activity.activityId) ? 'Melder av...' : 'Meld av' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from '@/services/api'
import { useRouter } from 'vue-router'
import { useAuth } from '@/composables/useAuth'

const activities = ref([])
const loadingIds = ref([])
const conflictMessage = ref('')
const registeredActivityIds = ref([])
const router = useRouter()
const auth = useAuth()

function formatDate(dateStr) {
  return new Date(dateStr).toLocaleString('nb-NO')
}

const isUserRegistered = (activityId) => {
  return registeredActivityIds.value.includes(activityId)
}

async function fetchActivities() {
  try {
    console.log('[v0] Fetching activities from /api/activities')
    const response = await axios.get('/api/activities')
    console.log('[v0] Activities response status:', response.status)
    console.log('[v0] Activities count:', response.data?.length || 0)
    console.log('[v0] First activity:', response.data?.[0])
    activities.value = response.data || []
  } catch (error) {
    console.error('[v0] Error fetching activities:')
    console.error('[v0] Status:', error.response?.status)
    console.error('[v0] Data:', error.response?.data)
    console.error('[v0] Message:', error.message)
  }
}

async function fetchRegisteredActivities() {
  try {
    console.log('[v0] Fetching registered activities from /api/activities/mine')
    const response = await axios.get('/api/activities/mine')
    console.log('[v0] Registered activities:', response.data)
    const myActivities = response.data || []
    registeredActivityIds.value = myActivities.map(a => a.activityId)
  } catch (error) {
    console.error('[v0] Error fetching registered activities:')
    console.error('[v0] Status:', error.response?.status)
    console.error('[v0] Data:', error.response?.data)
    console.error('[v0] This might be expected if user has no registered activities')
  }
}

async function meldPå(activityId) {
  if (loadingIds.value.includes(activityId)) return
  loadingIds.value.push(activityId)
  conflictMessage.value = ''

  try {
    await axios.post(`/api/activities/${activityId}/register`)
    if (!registeredActivityIds.value.includes(activityId)) {
      registeredActivityIds.value.push(activityId)
    }
    alert('Du er påmeldt aktiviteten!')
  } catch (error) {
    console.error('Error registering for activity:', error)
    const errorMessage = error.response?.data?.message || error.message
    if (errorMessage.includes('allerede påmeldt') || errorMessage.includes('already')) {
      if (!registeredActivityIds.value.includes(activityId)) {
        registeredActivityIds.value.push(activityId)
      }
      // Don't show an error - treat as success since they're registered
      alert('Du er allerede påmeldt denne aktiviteten')
    } else if (errorMessage.includes('kolliderer')) {
      conflictMessage.value = errorMessage
      window.scrollTo({ top: 0, behavior: 'smooth' })
    } else {
      alert('Kunne ikke melde på aktiviteten: ' + errorMessage)
    }
  } finally {
    loadingIds.value = loadingIds.value.filter(id => id !== activityId)
  }
}

async function meldAv(activityId) {
  if (loadingIds.value.includes(activityId)) return
  loadingIds.value.push(activityId)

  try {
    await axios.delete(`/api/activities/${activityId}/register`)
    registeredActivityIds.value = registeredActivityIds.value.filter(id => id !== activityId)
    alert('Du er avmeldt aktiviteten!')
  } catch (error) {
    console.error('Error unregistering from activity:', error)
    const errorMessage = error.response?.data?.message || error.message
    alert('Kunne ikke melde av aktiviteten: ' + errorMessage)
  } finally {
    loadingIds.value = loadingIds.value.filter(id => id !== activityId)
  }
}

function logout() {
  auth.logout()
  router.push('/login')
}

onMounted(() => {
  console.log('[v0] DeltakerView mounted, auth state:', { email: auth.email, role: auth.role })
  fetchActivities()
  fetchRegisteredActivities()
})
</script>
