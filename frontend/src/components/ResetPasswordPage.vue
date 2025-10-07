<template>
  <div class="max-w-md mx-auto mt-20 p-6 border rounded shadow">
    <h1 class="text-2xl font-bold mb-4">Tilbakestill passord</h1>
    <p class="text-gray-600 mb-4">
      Skriv inn ditt nye passord.
    </p>
    
    <div v-if="successMessage" class="mb-4 p-3 bg-green-100 text-green-700 rounded">
      {{ successMessage }}
      <div class="mt-2">
        <router-link to="/login" class="text-blue-600 hover:underline font-semibold">
          Gå til innlogging
        </router-link>
      </div>
    </div>
    
    <div v-if="errorMessage" class="mb-4 p-3 bg-red-100 text-red-700 rounded">
      {{ errorMessage }}
    </div>

    <form @submit.prevent="resetPassword" v-if="!successMessage">
      <div class="mb-4">
        <label for="password" class="block mb-1">Nytt passord</label>
        <input 
          id="password" 
          v-model="password" 
          type="password" 
          required 
          minlength="6"
          class="w-full border p-2 rounded" 
          placeholder="Minst 6 tegn"
        />
      </div>
      <div class="mb-4">
        <label for="confirmPassword" class="block mb-1">Bekreft passord</label>
        <input 
          id="confirmPassword" 
          v-model="confirmPassword" 
          type="password" 
          required 
          class="w-full border p-2 rounded" 
          placeholder="Skriv inn passordet på nytt"
        />
      </div>
      <button 
        type="submit" 
        :disabled="loading"
        class="w-full bg-blue-600 text-white py-2 rounded hover:bg-blue-700 disabled:bg-gray-400"
      >
        {{ loading ? 'Tilbakestiller...' : 'Tilbakestill passord' }}
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
import { ref, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import axios from '@/services/api';

const route = useRoute();
const password = ref('');
const confirmPassword = ref('');
const loading = ref(false);
const successMessage = ref('');
const errorMessage = ref('');
const token = ref('');

onMounted(() => {
  token.value = route.query.token || '';
  if (!token.value) {
    errorMessage.value = 'Ugyldig tilbakestillingslenke. Vennligst be om en ny lenke.';
  }
});

async function resetPassword() {
  if (password.value !== confirmPassword.value) {
    errorMessage.value = 'Passordene stemmer ikke overens';
    return;
  }

  if (password.value.length < 6) {
    errorMessage.value = 'Passordet må være minst 6 tegn';
    return;
  }

  loading.value = true;
  errorMessage.value = '';
  successMessage.value = '';

  try {
    const response = await axios.post('/api/auth/reset-password', {
      token: token.value,
      newPassword: password.value,
    });

    successMessage.value = response.data.message;
  } catch (error) {
    errorMessage.value = error.response?.data?.message || 'En feil oppstod. Prøv igjen senere.';
  } finally {
    loading.value = false;
  }
}
</script>
