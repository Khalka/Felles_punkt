<template>
  <div class="max-w-md mx-auto mt-10 p-6 bg-white rounded shadow">
    <h2 class="text-2xl font-bold mb-6">Registrer ny bruker</h2>

    <form @submit.prevent="handleSubmit" class="space-y-4">
      <div>
        <label class="block mb-1 font-medium">Fornavn</label>
        <input v-model="form.firstName" type="text" required class="input" />
      </div>

      <div>
        <label class="block mb-1 font-medium">Etternavn</label>
        <input v-model="form.lastName" type="text" required class="input" />
      </div>

      <div>
        <label class="block mb-1 font-medium">Telefon</label>
        <input v-model="form.telephone" type="tel" required class="input" />
      </div>

      <div>
        <label class="block mb-1 font-medium">E-post</label>
        <input v-model="form.email" type="email" required class="input" />
      </div>

      <div>
        <label class="block mb-1 font-medium">Passord</label>
        <input v-model="form.password" type="password" required class="input" />
      </div>

      <div>
        <label class="block mb-1 font-medium">Rolle</label>
        <select v-model="form.role" required class="input">
          <option value="" disabled>Velg rolle</option>
          <option value="ADMIN">Admin</option>
          <option value="ARRANGOR">Arrangør</option>
          <option value="DELTAGER">Deltaker</option>
        </select>
      </div>

      <div>
        <label class="block mb-1 font-medium">Adresse</label>
        <select v-model="form.addressId" required class="input">
          <option value="" disabled>Velg adresse</option>
          <option v-for="address in addresses" :key="address.id" :value="address.id">
            {{ address.street }}, {{ address.city }}
          </option>
        </select>
      </div>

      <button
        type="submit"
        class="w-full py-2 px-4 bg-blue-600 text-white font-semibold rounded hover:bg-blue-700 transition"
      >
        Registrer
      </button>
    </form>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'

const form = ref({
  firstName: '',
  lastName: '',
  telephone: '',
  email: '',
  password: '',
  role: '',
  addressId: '',
})

const addresses = ref([])

onMounted(async () => {
  try {
    const response = await axios.get('/api/addresses')
    addresses.value = response.data
  } catch (error) {
    console.error('Feil ved lasting av adresser:', error)
  }
})

async function handleSubmit() {
  try {
    await axios.post('/api/register', {
      ...form.value,
      telephone: Number(form.value.telephone), // konverter til number
    })
    alert('Registrering vellykket!')
    // Nullstill skjema
    form.value = {
      firstName: '',
      lastName: '',
      telephone: '',
      email: '',
      password: '',
      role: '',
      addressId: '',
    }
  } catch (error) {
    alert('Registrering feilet: ' + (error.response?.data || error.message))
  }
}
</script>

<style scoped>
.input {
  width: 100%;
  padding: 0.5rem 0.75rem;
  border: 1px solid #cbd5e1; /* Tailwind slate-300 */
  border-radius: 0.375rem;
  font-size: 1rem;
  transition: border-color 0.3s;
}
.input:focus {
  outline: none;
  border-color: #2563eb; /* Tailwind blue-600 */
  box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.3);
}
</style>
