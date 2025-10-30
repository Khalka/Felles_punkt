<script setup>
import { ref, computed } from 'vue'
import { useAuth } from '../composables/useAuth'
import { useRouter } from 'vue-router'

const showMenu = ref(false)
const auth = useAuth()
const router = useRouter()

const userRole = computed(() => auth.getRole()?.toUpperCase())
const userName = computed(() => auth.getFullName())

function toggleMenu() {
  showMenu.value = !showMenu.value
}

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
        <!-- Only show login/register when NOT authenticated -->
        <router-link v-if="!userRole" to="/login" class="hover:underline">Logg inn</router-link>
        <router-link v-if="!userRole" to="/register" class="hover:underline">Registrer deg</router-link>

        <!-- Show user's name for all logged-in users instead of role labels -->
        <router-link v-if="userRole === 'ADMIN'" to="/admin" class="hover:underline">{{ userName }}</router-link>
        <router-link v-if="userRole === 'ARANGOR'" to="/arrangor" class="hover:underline">{{ userName }}</router-link>
        <router-link v-if="userRole === 'USER'" to="/deltaker" class="hover:underline">{{ userName }}</router-link>
        <router-link v-if="userRole === 'ARANGOR' || userRole === 'USER'" to="/mine-aktiviteter" class="hover:underline">Mine Aktiviteter</router-link>

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
      <!-- Only show login/register when NOT authenticated -->
      <router-link v-if="!userRole" @click="toggleMenu" to="/login" class="block hover:underline">Logg inn</router-link>
      <router-link v-if="!userRole" @click="toggleMenu" to="/register" class="block hover:underline">Registrer deg</router-link>

      <!-- Show user's name for all logged-in users instead of role labels -->
      <router-link v-if="userRole === 'ADMIN'" @click="toggleMenu" to="/admin" class="block hover:underline">{{ userName }}</router-link>
      <router-link v-if="userRole === 'ARANGOR'" @click="toggleMenu" to="/arrangor" class="block hover:underline">{{ userName }}</router-link>
      <router-link v-if="userRole === 'USER'" @click="toggleMenu" to="/deltaker" class="block hover:underline">{{ userName }}</router-link>
      <router-link v-if="userRole === 'ARANGOR' || userRole === 'USER'" @click="toggleMenu" to="/mine-aktiviteter" class="block hover:underline">Mine Aktiviteter</router-link>

      <button v-if="userRole" @click="logout" class="block w-full text-left bg-red-500 hover:bg-red-700 px-3 py-1 rounded">
        Logg ut
      </button>
    </div>
  </nav>
</template>
