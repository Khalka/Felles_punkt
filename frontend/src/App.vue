<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import NavBar from './components/NavBar.vue'

const route = useRoute()

const showNavBar = computed(() => {
  return route.path !== '/'
})
</script>

<template>
  <div>
    <NavBar v-if="showNavBar" />
    <router-view />
  </div>
</template>
