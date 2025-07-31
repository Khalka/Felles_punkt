import { reactive, readonly } from 'vue'

const state = reactive({
  user: null, // Evt: { id, name, role }
})

function login(userData) {
  state.user = userData
}

function logout() {
  state.user = null
}

function getRole() {
  return state.user?.role || null
}

export function useAuth() {
  return {
    user: readonly(state.user),
    login,
    logout,
    getRole,
  }
}
