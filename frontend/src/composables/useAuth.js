"use client"

import { reactive } from "vue"

const state = reactive({
  user: null,
  role: localStorage.getItem("role") || null,
  token: localStorage.getItem("token") || null,
  firstName: localStorage.getItem("firstName") || null,
  lastName: localStorage.getItem("lastName") || null,
  email: localStorage.getItem("email") || null,
})

function login(userData) {
  state.user = userData
  state.role = userData.role
  state.token = userData.token
  state.firstName = userData.firstName
  state.lastName = userData.lastName
  state.email = userData.email

  localStorage.setItem("role", userData.role)
  localStorage.setItem("token", userData.token)
  localStorage.setItem("firstName", userData.firstName)
  localStorage.setItem("lastName", userData.lastName)
  localStorage.setItem("email", userData.email)
}

function logout() {
  state.user = null
  state.role = null
  state.token = null
  state.firstName = null
  state.lastName = null
  state.email = null

  localStorage.removeItem("role")
  localStorage.removeItem("token")
  localStorage.removeItem("firstName")
  localStorage.removeItem("lastName")
  localStorage.removeItem("email")
}

function getRole() {
  return state.role
}

function getFullName() {
  if (state.firstName && state.lastName) {
    return `${state.firstName} ${state.lastName}`
  }
  return null
}

function isAuthenticated() {
  return !!state.token
}

// The previous implementation wrapped primitive values (strings/null) in
// readonly(), which Vue warns about because readonly() only accepts objects.
// Instead we expose plain getters that read straight from the reactive state:
// consumers still get live values as primitives (so `auth.email.trim()` keeps
// working) and reactivity is preserved when accessed inside templates or
// computed properties. The returned object is frozen so consumers can't
// overwrite the accessors.
const auth = Object.freeze({
  get user() {
    return state.user
  },
  get role() {
    return state.role
  },
  get token() {
    return state.token
  },
  get firstName() {
    return state.firstName
  },
  get lastName() {
    return state.lastName
  },
  get email() {
    return state.email
  },
  login,
  logout,
  getRole,
  getFullName,
  isAuthenticated,
})

export function useAuth() {
  return auth
}
