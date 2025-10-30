"use client"

import { createRouter, createWebHistory } from "vue-router"

import LoginPage from "./components/LoginPage.vue"
import RegisterView from "./components/RegisterView.vue"
import AdminView from "./components/AdminView.vue"
import ArrangorView from "./components/ArrangorView.vue"
import DeltakerView from "./components/DeltakerView.vue"
import MyActivities from "./components/MyActivities.vue"
import ForgotPasswordPage from "./components/ForgotPasswordPage.vue"
import ResetPasswordPage from "./components/ResetPasswordPage.vue"

const routes = [
  { path: "/", component: LoginPage },
  { path: "/login", component: LoginPage },
  { path: "/register", component: RegisterView },
  { path: "/forgot-password", component: ForgotPasswordPage },
  { path: "/reset-password", component: ResetPasswordPage },
  {
    path: "/admin",
    component: AdminView,
    meta: { requiresAuth: true, roles: ["ADMIN"] },
  },
  {
    path: "/arrangor",
    component: ArrangorView,
    meta: { requiresAuth: true, roles: ["ARANGOR"] },
  },
  {
    path: "/deltaker",
    component: DeltakerView,
    meta: { requiresAuth: true, roles: ["USER", "DELTAKER"] },
  },
  {
    path: "/mine-aktiviteter",
    component: MyActivities,
    meta: { requiresAuth: true, roles: ["ARANGOR", "USER", "DELTAKER"] },
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem("token")
  const userRole = localStorage.getItem("role")

  if ((to.path === "/" || to.path === "/login" || to.path === "/register") && token && userRole) {
    redirectToDashboard(userRole, next)
    return
  }

  if (to.meta.requiresAuth) {
    if (!token || !userRole) {
      // Not logged in - redirect to login
      next("/login")
    } else if (to.meta.roles && !to.meta.roles.includes(userRole)) {
      // Role not allowed - redirect to appropriate dashboard
      alert("Du har ikke tilgang til denne siden.")
      redirectToDashboard(userRole, next)
    } else {
      // Access granted
      next()
    }
  } else {
    // No auth required
    next()
  }
})

function redirectToDashboard(role, next) {
  if (role === "ADMIN") {
    next("/admin")
  } else if (role === "ARANGOR") {
    next("/arrangor")
  } else if (role === "USER" || role === "DELTAKER") {
    next("/deltaker")
  } else {
    next("/login")
  }
}

export default router
