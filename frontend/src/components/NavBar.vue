<script setup>
import { ref, computed } from 'vue'
import { useAuth } from '../composables/useAuth'
import { useRouter } from 'vue-router'

const showMenu = ref(false)
function toggleMenu() {
  showMenu.value = !showMenu.value
}

const auth = useAuth()
const userRole = computed(() => auth.getRole())

const router = useRouter()

function logout() {
  auth.logout()
  router.push('/login')
}
</script>

<template>
  <nav class="bg-blue-600 text-white px-4 py-3 shadow-md">
    <div class="max-w-7xl mx-auto flex items-center justify-between">
      <div class="text-xl font-semibold">
        <router-link to="/" class="hover:underline">MinApp</router-link>
      </div>

      <div class="hidden md:flex space-x-4 items-center">
        <router-link v-if="!userRole" to="/login" class="hover:underline">Logg inn</router-link>
        <router-link v-if="!userRole" to="/register" class="hover:underline">Registrer deg</router-link>

        <router-link v-if="userRole === 'admin'" to="/admin" class="hover:underline">Admin</router-link>
        <router-link v-if="userRole === 'arrangor'" to="/arrangor" class="hover:underline">Arrangør</router-link>
        <router-link v-if="userRole === 'deltaker'" to="/deltaker" class="hover:underline">Deltaker</router-link>
        <router-link v-if="userRole === 'arrangor' || userRole === 'deltaker'" to="/mine-aktiviteter" class="hover:underline">Mine Aktiviteter</router-link>

        <button v-if="userRole" @click="logout" class="ml-4 bg-red-500 hover:bg-red-700 px-3 py-1 rounded">
          Logg ut
        </button>
      </div>

      <button class="md:hidden" @click="toggleMenu">
        <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none"
             viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                d="M4 6h16M4 12h16M4 18h16" />
        </svg>
      </button>
    </div>

    <div v-if="showMenu" class="md:hidden mt-2 space-y-2 px-4">
      <router-link v-if="!userRole" @click="toggleMenu" to="/login" class="block hover:underline">Logg inn</router-link>
      <router-link v-if="!userRole" @click="toggleMenu" to="/register" class="block hover:underline">Registrer deg</router-link>

      <router-link v-if="userRole === 'admin'" @click="toggleMenu" to="/admin" class="block hover:underline">Admin</router-link>
      <router-link v-if="userRole === 'arrangor'" @click="toggleMenu" to="/arrangor" class="block hover:underline">Arrangør</router-link>
      <router-link v-if="userRole === 'deltaker'" @click="toggleMenu" to="/deltaker" class="block hover:underline">Deltaker</router-link>
      <router-link v-if="userRole === 'arrangor' || userRole === 'deltaker'" @click="toggleMenu" to="/mine-aktiviteter" class="block hover:underline">Mine Aktiviteter</router-link>

      <button v-if="userRole" @click="logout" class="block w-full text-left bg-red-500 hover:bg-red-700 px-3 py-1 rounded">
        Logg ut
      </button>
    </div>
  </nav>
</template>
