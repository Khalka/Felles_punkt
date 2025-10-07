<template>
  <div class="max-w-md mx-auto mt-20 p-6 border rounded shadow">
    <h1 class="text-2xl font-bold mb-4">Glemt passord</h1>
    <p class="text-gray-600 mb-4">
      Skriv inn e-postadressen din, så sender vi deg en lenke for å tilbakestille passordet.
    </p>
    
    <div v-if="successMessage" class="mb-4 p-3 bg-green-100 text-green-700 rounded">
      {{ successMessage }}
    </div>
    
    <div v-if="errorMessage" class="mb-4 p-3 bg-red-100 text-red-700 rounded">
      {{ errorMessage }}
    </div>

    <form @submit.prevent="requestReset" v-if="!successMessage">
      <div class="mb-4">
        <label for="email" class="block mb-1">E-post</label>
        <input 
          id="email" 
          v-model="email" 
          type="email" 
          required 
          class="w-full border p-2 rounded" 
          placeholder="din@epost.no"
        />
      </div>
      <button 
        type="submit" 
        :disabled="loading"
        class="w-full bg-blue-600 text-white py-2 rounded hover:bg-blue-700 disabled:bg-gray-400"
      >
        {{ loading ? 'Sender...' : 'Send tilbakestillingslenke' }}
      </button>
    </form>

    <div class="mt-4 text-center">
      <router-link to="/login" class="text-blue-600 hover:underline">
        Tilbake til innlogging
      </router-link>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import axios from '@/services/api';

const email = ref('');
const loading = ref(false);
const successMessage = ref('');
const errorMessage = ref('');

async function requestReset() {
  loading.value = true;
  errorMessage.value = '';
  successMessage.value = '';

  try {
    const response = await axios.post('/api/auth/forgot-password', {
      email: email.value,
    });

    successMessage.value = response.data.message;
    
    // For development: show the token (remove in production)
    if (response.data.token) {
      console.log('Reset token:', response.data.token);
      successMessage.value += ` Token: ${response.data.token}`;
    }
  } catch (error) {
    errorMessage.value = error.response?.data?.message || 'En feil oppstod. Prøv igjen senere.';
  } finally {
    loading.value = false;
  }
}
</script>
