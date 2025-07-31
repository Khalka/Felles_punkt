<template>
  <div class="p-4">
    <h1 class="text-3xl font-bold mb-4">Admin Dashboard</h1>
    <button @click="logout" class="bg-red-600 text-white px-4 py-2 rounded hover:bg-red-700">Logg ut</button>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router';
const router = useRouter();

function logout() {
  localStorage.clear();
  router.push('/');
}
</script>
