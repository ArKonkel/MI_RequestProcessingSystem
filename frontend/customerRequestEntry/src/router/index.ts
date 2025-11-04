import { createRouter, createWebHistory } from 'vue-router'
import RequestCreationView from '@/views/RequestEntryViews/RequestCreationView.vue'
import SentRequestView from '@/views/RequestEntryViews/SentRequestView.vue'
import axios from 'axios'
import LoginView from '@/views/AuthenticationViews/LoginView.vue'
import RegisterView from '@/views/AuthenticationViews/RegisterView.vue'

const securityEnabled = import.meta.env.VITE_SECURITY_ENABLED === 'true'

if (securityEnabled) {
  // 💡 AXIOS REQUEST INTERCEPTOR --> Adds to every requests the Authorization header with the JWT token
  axios.interceptors.request.use(
    (config) => {
      const token = localStorage.getItem('token')

      if (token) {
        config.headers['Authorization'] = `Bearer ${token}`
      }

      return config
    },
    (error) => {
      return Promise.reject(error)
    },
  )
}

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/register',
      name: 'Register',
      component: RegisterView,
    },
    {
      path: '/login',
      name: 'Login',
      component: LoginView,
    },
    {
      path: '/',
      redirect: '/requests',
    },
    {
      path: '/requests',
      name: 'sentRequestView',
      component: SentRequestView,
    },
    {
      path: '/requests/create',
      name: 'requestCreationView',
      component: RequestCreationView,
    },
  ],
})

router.beforeEach((to, from, next) => {
  if (!securityEnabled) {
    return next()
  }

  const token = localStorage.getItem('token') // JWT
  const publicPages = ['/login', '/register']
  const authRequired = !publicPages.includes(to.path) // check if route is public page

  if (authRequired && !token) {
    next('/login')
  } else if (to.path === '/login' && token) {
    next('/')
  } else {
    next()
  }
})

export default router
