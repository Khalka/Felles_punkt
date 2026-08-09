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

    <!-- Demo credentials (visible when running against the built-in mock backend) -->
    <div class="mt-6 border-t pt-4">
      <p class="text-sm font-semibold text-gray-700 mb-2">Demo-innlogging</p>
      <p class="text-xs text-gray-500 mb-2">Passord for alle: <code class="bg-gray-100 px-1 rounded">password</code></p>
      <div class="flex flex-col gap-1">
        <button type="button" @click="useDemo('admin@test.no')" class="text-left text-xs text-blue-600 hover:underline">
          admin@test.no (Admin)
        </button>
        <button type="button" @click="useDemo('arrangor@test.no')" class="text-left text-xs text-blue-600 hover:underline">
          arrangor@test.no (Arrangør)
        </button>
        <button type="button" @click="useDemo('deltaker@test.no')" class="text-left text-xs text-blue-600 hover:underline">
          deltaker@test.no (Deltaker)
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import axios from '@/services/api';
import { useRouter } from 'vue-router';
import { useAuth } from '@/composables/useAuth';

const email = ref('');
const password = ref('');
const router = useRouter();
const auth = useAuth();

function useDemo(demoEmail) {
  email.value = demoEmail;
  password.value = 'password';
}

async function login() {
  try {
    const response = await axios.post('/api/auth/login', {
      username: email.value,
      password: password.value,
    });

    const token = response.data.token;
    const role = response.data.role;
    const firstName = response.data.firstName;
    const lastName = response.data.lastName;

    auth.login({ token, role, firstName, lastName, email: email.value });

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
