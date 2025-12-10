<template>
  <div class="min-h-screen bg-gray-50">
    <!-- Added blue header with menu button -->
    <header class="bg-blue-700 text-white py-4 px-6">
      <div class="max-w-7xl mx-auto flex justify-between items-center">
        <h1 class="text-2xl font-bold">MinApp</h1>
        <div class="relative">
          <button 
            @click="showMenu = !showMenu"
            class="flex items-center gap-2 bg-purple-600 hover:bg-purple-700 px-4 py-2 rounded transition-colors"
          >
            <span class="font-semibold">Menu</span>
            <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h16" />
            </svg>
          </button>
          
          <!-- Dropdown menu for login/register -->
          <div 
            v-if="showMenu" 
            class="absolute right-0 mt-2 w-48 bg-white rounded-lg shadow-lg py-2 z-50"
          >
            <router-link 
              to="/login" 
              class="block px-4 py-2 text-gray-800 hover:bg-gray-100 transition-colors"
              @click="showMenu = false"
            >
              Logg inn
            </router-link>
            <router-link 
              to="/register" 
              class="block px-4 py-2 text-gray-800 hover:bg-gray-100 transition-colors"
              @click="showMenu = false"
            >
              Registrer deg
            </router-link>
          </div>
        </div>
      </div>
    </header>

    <!-- Main content with activity grid -->
    <main class="max-w-7xl mx-auto px-6 py-8">
      <h2 class="text-3xl font-bold text-gray-900 mb-8">Utforsk aktiviteter</h2>
      
      <!-- Loading state -->
      <div v-if="loading" class="text-center py-12">
        <div class="inline-block animate-spin rounded-full h-12 w-12 border-b-2 border-blue-700"></div>
        <p class="mt-4 text-gray-600">Laster aktiviteter...</p>
      </div>

      <!-- Error state -->
      <div v-else-if="error" class="text-center py-12">
        <p class="text-red-600">{{ error }}</p>
      </div>

      <!-- Empty state -->
      <div v-else-if="activities.length === 0" class="text-center py-12">
        <p class="text-gray-600">Ingen aktiviteter tilgjengelig for øyeblikket.</p>
      </div>

      <!-- Activity grid layout -->
      <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        <div 
          v-for="activity in activities" 
          :key="activity.activityId"
          class="bg-white rounded-lg shadow-md overflow-hidden hover:shadow-xl transition-shadow cursor-pointer"
          @click="handleActivityClick(activity)"
        >
          <!-- Display actual activity image or placeholder -->
          <div v-if="activity.image" class="h-48 overflow-hidden">
            <img 
              :src="activity.image" 
              :alt="activity.activityType"
              class="w-full h-full object-cover"
            />
          </div>
          <div v-else class="h-48 bg-gradient-to-br from-blue-500 to-purple-600 flex items-center justify-center">
            <svg class="w-16 h-16 text-white opacity-75" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4" />
            </svg>
          </div>

          <!-- Activity details -->
          <div class="p-4">
            <div class="flex items-start justify-between mb-2">
              <!-- Display activity title (activityType) -->
              <h3 class="text-lg font-semibold text-gray-900">{{ activity.activityType }}</h3>
              <button 
                class="text-gray-400 hover:text-red-500 transition-colors"
                @click.stop="toggleFavorite(activity.activityId)"
              >
                <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z" />
                </svg>
              </button>
            </div>
            
            <p class="text-sm text-gray-600 mb-2">{{ activity.holdPlace }}</p>
            <!-- Display activity description (beskrivelse) -->
            <p class="text-sm text-gray-700 mb-3 line-clamp-2">{{ activity.description }}</p>
            
            <div class="flex items-center justify-between text-sm text-gray-500 mb-3">
              <span>{{ formatDate(activity.startTime) }}</span>
              <span>{{ formatTime(activity.startTime) }} - {{ formatTime(activity.endTime) }}</span>
            </div>

            <button 
              class="w-full bg-green-500 hover:bg-green-600 text-white font-semibold py-2 px-4 rounded transition-colors"
              @click.stop="handleRegister(activity)"
            >
              Se detaljer
            </button>

            <!-- Add comments section below activity details -->
            <div class="border-t pt-3 mt-3">
              <p class="text-xs text-gray-500 font-semibold mb-2">{{ activity.registeredUsers?.length || 0 }} pessoas inscritas</p>
              <p v-if="activity.comments && activity.comments.length > 0" class="text-xs text-gray-600">
                <strong>Último comentário:</strong> "{{ activity.comments[0].text.substring(0, 50) }}..."
              </p>
              <p v-else class="text-xs text-gray-600 italic">Sem comentários ainda</p>
            </div>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import api from '../services/api'

const router = useRouter()
const activities = ref([])
const loading = ref(true)
const error = ref(null)
const showMenu = ref(false)

const fetchActivities = async () => {
  try {
    loading.value = true
    error.value = null
    const response = await api.get('/api/activities')
    activities.value = response.data
  } catch (err) {
    console.error('Error fetching activities:', err)
    error.value = 'Kunne ikke laste aktiviteter. Prøv igjen senere.'
  } finally {
    loading.value = false
  }
}

const handleActivityClick = (activity) => {
  // Redirect to login if not authenticated
  const token = localStorage.getItem('token')
  if (!token) {
    router.push('/login')
  }
}

const handleRegister = (activity) => {
  // Redirect to login to register for activity
  const token = localStorage.getItem('token')
  if (!token) {
    router.push('/login')
  }
}

const toggleFavorite = (activityId) => {
  // Redirect to login for favorites
  const token = localStorage.getItem('token')
  if (!token) {
    router.push('/login')
  }
}

const formatDate = (dateTime) => {
  if (!dateTime) return ''
  const date = new Date(dateTime)
  return date.toLocaleDateString('nb-NO', { day: 'numeric', month: 'long', year: 'numeric' })
}

const formatTime = (dateTime) => {
  if (!dateTime) return ''
  const date = new Date(dateTime)
  return date.toLocaleTimeString('nb-NO', { hour: '2-digit', minute: '2-digit' })
}

onMounted(() => {
  fetchActivities()
})
</script>

<style scoped>
.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
