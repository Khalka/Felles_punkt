import axios from './api'

export async function login(email, password) {
  const res = await axios.post('/api/auth/login', {
    mailaddress: email,
    password: password
  })
  const { token, role } = res.data
  localStorage.setItem('token', token)
  localStorage.setItem('role', role)
  return role
}

export function getRole() {
  return localStorage.getItem('role')
}

export function logout() {
  localStorage.clear()
}
