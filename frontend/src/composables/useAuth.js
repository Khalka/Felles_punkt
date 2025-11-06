"use client"

import { reactive, readonly } from "vue"

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

export function useAuth() {
  return {
    user: readonly(state.user),
    role: readonly(state.role),
    token: readonly(state.token),
    firstName: readonly(state.firstName),
    lastName: readonly(state.lastName),
    email: readonly(state.email),
    login,
    logout,
    getRole,
    getFullName,
    isAuthenticated,
  }
}
