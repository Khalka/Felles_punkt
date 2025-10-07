<template>
  <div class="max-w-md mx-auto p-6 bg-white rounded shadow">
    <h2 class="text-xl font-semibold mb-4">Registrer deg</h2>

    <form @submit.prevent="registerUser">
      <!-- Fornavn -->
      <div class="mb-2">
        <label>Fornavn:</label>
        <input
          v-model="form.firstName"
          required
          class="w-full border p-2 rounded"
        />
      </div>

      <!-- Etternavn -->
      <div class="mb-2">
        <label>Etternavn:</label>
        <input
          v-model="form.lastName"
          required
          class="w-full border p-2 rounded"
        />
      </div>

      <!-- Telefon -->
      <div class="mb-2">
        <label>Telefon:</label>
        <input
          v-model="form.telephone"
          type="text"
          required
          class="w-full border p-2 rounded"
        />
      </div>

      <!-- E-post -->
      <div class="mb-2">
        <label>Email:</label>
        <input
          v-model="form.email"
          type="email"
          required
          class="w-full border p-2 rounded"
        />
      </div>

      <!-- Passord -->
      <div class="mb-2">
        <label>Passord:</label>
        <input
          v-model="form.password"
          type="password"
          required
          class="w-full border p-2 rounded"
        />
      </div>

      <!-- Adresse -->
      <h3 class="text-lg font-semibold mt-4 mb-2">Adresse</h3>

      <div class="mb-2">
        <label>Gate:</label>
        <input 
          id="streetName"
          v-model="form.address.streetName"
          required
          class="w-full border p-2 rounded"
        />
      </div>

      <!-- Added house number field -->
      <div class="mb-2">
        <label>Husnummer:</label>
        <input
          v-model="form.address.houseNumber"
          type="text"
          required
          class="w-full border p-2 rounded"
        />
      </div>

      <div class="mb-2">
        <label for="postcode">Postnummer:</label>
        <input
          id="postcode"
          v-model="form.address.postCode"
          type="text"
          required
          class="w-full border p-2 rounded"
        />
      </div>

      <div class="mb-2">
        <label>By:</label>
        <input
          v-model="form.address.city"
          required
          class="w-full border p-2 rounded"
        />
      </div>

      <!-- Added country field -->
      <div class="mb-2">
        <label>Land:</label>
        <input
          v-model="form.address.country"
          required
          class="w-full border p-2 rounded"
          placeholder="Norge"
        />
      </div>

      <!-- Rolle -->
      <div class="mb-2">
        <label>Rolle:</label>
        <select
          v-model="form.role"
          required
          class="w-full border p-2 rounded"
        >
          <option value="ADMIN">ADMIN</option>
          <option value="ARANGOR">ARANGOR</option>
          <option value="DELTAKER">DELTAKER</option>
          <option value="USER">USER</option>
        </select>
      </div>

      <!-- Knapp -->
      <button
        type="submit"
        class="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700"
      >
        Registrer
      </button>

      <!-- Tilbakemelding -->
      <p v-if="message" class="mt-2 text-green-600">
        {{ message }}
      </p>
    </form>
  </div>
</template>

<script setup>
import { reactive, ref } from "vue";
import axios from "axios";

const form = reactive({
  firstName: "",
  lastName: "",
  telephone: "",
  email: "",
  password: "",
  role: "USER",
  address: {
    streetName: "",
    houseNumber: "",
    postCode: "",
    city: "",
    country: ""
  }
});

const message = ref("");

async function registerUser() {
  try {
    const response = await axios.post("http://localhost:9090/api/auth/register", form);
    message.value = response.data.message || "Registrering vellykket!";
  } catch (error) {
    message.value = error.response?.data?.message || "Noe gikk galt under registrering.";
  }
}
</script>
