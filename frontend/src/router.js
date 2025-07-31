import { createRouter, createWebHistory } from 'vue-router'
import { useAuth } from './composables/useAuth'

import LoginPage from './components/LoginPage.vue'
import RegisterView from './components/RegisterView.vue'
import AdminView from './components/AdminView.vue'
import ArrangorView from './components/ArrangorView.vue'
import DeltakerView from './components/DeltakerView.vue'
import MyActivities from './components/MyActivities.vue'

const routes = [
  { path: '/', component: LoginPage },
  { path: '/login', component: LoginPage },
  { path: '/register', component: RegisterView },
  {
    path: '/admin',
    component: AdminView,
    meta: { requiresAuth: true, roles: ['admin'] },
  },
  {
    path: '/arrangor',
    component: ArrangorView,
    meta: { requiresAuth: true, roles: ['arrangor'] },
  },
  {
    path: '/deltaker',
    component: DeltakerView,
    meta: { requiresAuth: true, roles: ['deltaker'] },
  },
  {
    path: '/mine-aktiviteter',
    component: MyActivities,
    meta: { requiresAuth: true, roles: ['arrangor', 'deltaker'] },
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

// **Navigasjonsvakt for rolle-tilgang:**
router.beforeEach((to, from, next) => {
  const auth = useAuth()
  const userRole = auth.getRole()

  if (to.meta.requiresAuth) {
    if (!userRole) {
      // Ikke logget inn - send til login
      next('/login')
    } else if (to.meta.roles && !to.meta.roles.includes(userRole)) {
      // Rolle ikke tillatt - kan sende til "ikke-tilgang"-side eller login
      alert('Du har ikke tilgang til denne siden.')
      next('/login')
    } else {
      // Tilgang OK
      next()
    }
  } else {
    // Side uten auth krav
    next()
  }
})

export default router
