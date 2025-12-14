<template>
  <div class="min-h-screen bg-gray-50">
    <!-- Header -->
    <header class="bg-blue-700 text-white py-4 px-6">
      <div class="max-w-7xl mx-auto flex items-center gap-4">
        <button 
          @click="router.back()"
          class="hover:bg-blue-600 p-2 rounded transition-colors"
        >
          <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7" />
          </svg>
        </button>
        <h1 class="text-2xl font-bold">Aktivitetsdetaljer</h1>
      </div>
    </header>

    <!-- Loading state -->
    <div v-if="loading" class="max-w-4xl mx-auto px-6 py-12 text-center">
      <div class="inline-block animate-spin rounded-full h-12 w-12 border-b-2 border-blue-700"></div>
      <p class="mt-4 text-gray-600">Laster detaljer...</p>
    </div>

    <!-- Error state -->
    <div v-else-if="error" class="max-w-4xl mx-auto px-6 py-12">
      <div class="bg-red-50 border border-red-200 rounded-lg p-4">
        <p class="text-red-600">{{ error }}</p>
      </div>
    </div>

    <!-- Activity details -->
    <main v-else-if="activity" class="max-w-4xl mx-auto px-6 py-8">
      <div class="bg-white rounded-lg shadow-lg overflow-hidden">
        <!-- Activity image -->
        <div v-if="activity.image" class="h-64 overflow-hidden">
          <img 
            :src="activity.image" 
            :alt="activity.activityType"
            class="w-full h-full object-cover"
          />
        </div>
        <div v-else class="h-64 bg-gradient-to-br from-blue-500 to-purple-600 flex items-center justify-center">
          <svg class="w-24 h-24 text-white opacity-75" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z" />
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z" />
          </svg>
        </div>

        <div class="p-6">
          <!-- Title -->
          <h2 class="text-3xl font-bold text-gray-900 mb-4">{{ activity.activityType }}</h2>
          
          <!-- Location -->
          <div class="flex items-center gap-2 text-gray-700 mb-3">
            <svg class="w-5 h-5 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z" />
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z" />
            </svg>
            <span class="font-semibold">Sted:</span>
            <span>{{ activity.holdPlace }}</span>
          </div>

          <!-- Start time -->
          <div class="flex items-center gap-2 text-gray-700 mb-3">
            <svg class="w-5 h-5 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
            <span class="font-semibold">Start:</span>
            <span>{{ formatDateTime(activity.startTime) }}</span>
          </div>

          <!-- End time -->
          <div class="flex items-center gap-2 text-gray-700 mb-4">
            <svg class="w-5 h-5 text-red-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
            <span class="font-semibold">Slutt:</span>
            <span>{{ formatDateTime(activity.endTime) }}</span>
          </div>

          <!-- Description -->
          <div class="border-t pt-4 mb-4">
            <h3 class="font-semibold text-lg mb-2">Beskrivelse</h3>
            <p class="text-gray-700">{{ activity.description }}</p>
          </div>

          <!-- Registered users count -->
          <div class="border-t pt-4 mb-4">
            <p class="text-gray-600">
              <span class="font-semibold">{{ activity.registeredUsers?.length || 0 }}</span> personer påmeldt
            </p>
          </div>

          <!-- Comments section -->
          <div class="border-t pt-4">
            <h3 class="font-semibold text-lg mb-4">Kommentarer</h3>
            
            <div v-if="comments.length === 0" class="text-gray-500 italic">
              Ingen kommentarer ennå. Vær den første til å kommentere!
            </div>

            <div v-else class="space-y-4">
              <div 
                v-for="comment in comments" 
                :key="comment.commentId"
                class="bg-gray-50 rounded-lg p-4"
              >
                <div class="flex justify-between items-start mb-2">
                  <div>
                    <p class="font-semibold text-gray-900">{{ comment.userEmail }}</p>
                    <p class="text-xs text-gray-500">{{ formatDateTime(comment.createdAt) }}</p>
                  </div>
                </div>
                <p class="text-gray-700">{{ comment.text }}</p>
              </div>
            </div>

            <!-- Add comment form (only if logged in) -->
            <div v-if="isLoggedIn" class="mt-6">
              <textarea
                v-model="newComment"
                placeholder="Skriv en kommentar..."
                class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent resize-none"
                rows="3"
              ></textarea>
              <button
                @click="addComment"
                :disabled="!newComment.trim()"
                class="mt-2 bg-blue-600 hover:bg-blue-700 disabled:bg-gray-400 text-white font-semibold py-2 px-6 rounded transition-colors"
              >
                Legg til kommentar
              </button>
            </div>
            <div v-else class="mt-6 bg-blue-50 border border-blue-200 rounded-lg p-4">
              <p class="text-blue-800">
                <router-link to="/login" class="font-semibold underline">Logg inn</router-link> for å legge til kommentarer
              </p>
            </div>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import api from '../services/api'

const router = useRouter()
const route = useRoute()

const activity = ref(null)
const comments = ref([])
const loading = ref(true)
const error = ref(null)
const newComment = ref('')
const isLoggedIn = ref(false)

const fetchActivityDetails = async () => {
  try {
    loading.value = true
    error.value = null
    
    const activityId = route.params.id
    console.log('[v0] Fetching activity with ID:', activityId)
    
    const response = await api.get(`/api/activities/${activityId}`)
    console.log('[v0] Activity response:', response.data)
    activity.value = response.data

    // Fetch comments for this activity
    try {
      console.log('[v0] Fetching comments for activity:', activityId)
      const commentsResponse = await api.get(`/api/comments/activity/${activityId}`)
      console.log('[v0] Comments response:', commentsResponse.data)
      comments.value = commentsResponse.data
    } catch (err) {
      console.error('[v0] Error fetching comments:', err)
      // Comments not critical, just set to empty
      comments.value = []
    }
  } catch (err) {
    console.error('[v0] Error fetching activity details:', err)
    console.error('[v0] Error response:', err.response)
    error.value = 'Kunne ikke laste aktivitetsdetaljer.'
  } finally {
    loading.value = false
    console.log('[v0] Loading complete. Activity:', activity.value)
  }
}

const addComment = async () => {
  if (!newComment.value.trim()) return

  try {
    const activityId = route.params.id
    await api.post(`/api/comments/activity/${activityId}`, {
      text: newComment.value
    })
    
    newComment.value = ''
    // Refresh comments
    const commentsResponse = await api.get(`/api/comments/activity/${activityId}`)
    comments.value = commentsResponse.data
  } catch (err) {
    console.error('Error adding comment:', err)
    alert('Kunne ikke legge til kommentar. Prøv igjen.')
  }
}

const formatDateTime = (dateTime) => {
  if (!dateTime) return ''
  const date = new Date(dateTime)
  return date.toLocaleString('nb-NO', { 
    day: 'numeric', 
    month: 'long', 
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

onMounted(() => {
  const token = localStorage.getItem('token')
  isLoggedIn.value = !!token
  console.log('[v0] ActivityDetail mounted. Route params:', route.params)
  console.log('[v0] Is logged in:', isLoggedIn.value)
  fetchActivityDetails()
})
</script>
