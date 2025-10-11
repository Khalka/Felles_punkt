<template>
  <div class="max-w-md mx-auto mt-20 p-6 border rounded shadow">
    <h1 class="text-2xl font-bold mb-4">Logg inn</h1>
    <form @submit.prevent="login">
      <div class="mb-4">
        <label for="email" class="block mb-1">E-post</label>
        <input id="email" v-model="email" type="email" required class="w-full border p-2 rounded" />
      </div>
      <div class="mb-4">
        <label for="password" class="block mb-1">Passord</label>
        <input id="password" v-model="password" type="password" required class="w-full border p-2 rounded" />
      </div>
      
      <div class="mb-4 text-right">
        <router-link to="/forgot-password" class="text-sm text-blue-600 hover:underline">
          Glemt passord?
        </router-link>
      </div>

      <button type="submit" class="w-full bg-blue-600 text-white py-2 rounded hover:bg-blue-700">
        Logg inn
      </button>
    </form>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import axios from '@/services/api';
import { useRouter } from 'vue-router';

const email = ref('');
const password = ref('');
const router = useRouter();

async function login() {
  try {
    const response = await axios.post('/api/auth/login', {
      username: email.value,
      password: password.value,
    });

    const token = response.data.token;
    const role = response.data.role;

    localStorage.setItem('token', token);
    localStorage.setItem('role', role);

    if (role === 'ADMIN') {
      router.push('/admin');
    } else if (role === 'ARANGOR') {
      router.push('/arrangor');
    } else {
      router.push('/deltaker');
    }
  } catch (error) {
    alert('Feil brukernavn eller passord');
  }
}
</script>
