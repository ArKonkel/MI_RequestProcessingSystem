import {createRouter, createWebHistory} from 'vue-router'
import axios from 'axios' // 💡 Axios importieren

import TaskView from '@/views/TaskViews/TaskView.vue'
import TaskCapacityPlanningView from '@/views/CapacityPlanningViews/TaskCapacityPlanningView.vue'
import RequestView from '@/views/CustomerRequestViews/RequestView.vue'
import RequestDetailView from '@/views/CustomerRequestViews/RequestDetailView.vue'
import TaskDetailView from '@/views/TaskViews/TaskDetailView.vue'
import ProjectDetailView from '@/views/ProjectViews/ProjectDetailView.vue'
import ProjectView from '@/views/ProjectViews/ProjectView.vue'
import EmployeeView from '@/views/CapacityPlanningViews/EmployeeView.vue'
import EmployeeDetailView from '@/views/CapacityPlanningViews/EmployeeDetailView.vue'
import LoginView from '@/views/AuthenticationViews/LoginView.vue'
import {useUserStore} from "@/stores/userStore.ts";
import type {UserDtd} from "@/documentTypes/dtds/UserDtd.ts";
import {Role} from "@/documentTypes/types/Role.ts";
import ForbiddenView from "@/views/AuthenticationViews/ForbiddenView.vue";
import EmployeeCalendarView from "@/views/CapacityPlanningViews/EmployeeCalendarView.vue";

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
// ------------------------------------

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/forbidden',
      name: 'Forbidden',
      component: ForbiddenView,

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
      name: 'requestView',
      component: RequestView,
      meta: {roles: [Role.ADMIN, Role.CUSTOMER_REQUEST_REVISER, Role.TASK_REVISER, Role.CAPACITY_PLANNER, Role.PROJECT_PLANNER]},
      children: [
        {
          path: ':requestId',
          name: 'requestDetailView',
          component: RequestDetailView,
          meta: {roles: [Role.ADMIN, Role.CUSTOMER_REQUEST_REVISER, Role.TASK_REVISER, Role.CAPACITY_PLANNER, Role.PROJECT_PLANNER]},
        },
      ],
    },
    {
      path: '/projects',
      name: 'projectView',
      component: ProjectView,
      meta: {roles: [Role.ADMIN, Role.PROJECT_PLANNER]},
      children: [
        {
          path: ':projectId',
          name: 'projectDetailView',
          component: ProjectDetailView,
          meta: {roles: [Role.ADMIN, Role.PROJECT_PLANNER]},
        },
      ],
    },
    {
      path: '/tasks',
      name: 'taskView',
      component: TaskView,
      meta: {roles: [Role.ADMIN, Role.CUSTOMER_REQUEST_REVISER, Role.TASK_REVISER, Role.CAPACITY_PLANNER, Role.PROJECT_PLANNER]},
      children: [
        {
          path: ':taskId',
          name: 'taskDetailView',
          component: TaskDetailView,
          meta: {roles: [Role.ADMIN, Role.CUSTOMER_REQUEST_REVISER, Role.TASK_REVISER, Role.CAPACITY_PLANNER, Role.PROJECT_PLANNER]},
        },
      ],
    },
    {
      path: '/employees',
      name: 'employeeView',
      component: EmployeeView,
      meta: {roles: [Role.ADMIN, Role.CAPACITY_PLANNER]},
      children: [
        {
          path: ':employeeId',
          name: 'employeeDetailView',
          component: EmployeeDetailView,
          meta: {roles: [Role.ADMIN, Role.CAPACITY_PLANNER]},
        },
      ],
    },
    {
      path: '/employeeCalendar/',
      name: 'EmployeeCalendarView',
      component: EmployeeCalendarView,
      meta: {roles: [Role.ADMIN, Role.CAPACITY_PLANNER]},
    },
    {
      path: '/capacityPlanning/:taskId',
      name: 'capacityPlanningView',
      component: TaskCapacityPlanningView,
      meta: {roles: [Role.ADMIN, Role.CAPACITY_PLANNER]},
    },
  ],
})

router.beforeEach((to, from, next) => {
  if (!securityEnabled) {
    return next()
  }

  const token = localStorage.getItem('token') // JWT
  const publicPages = ['/login']
  const authRequired = !publicPages.includes(to.path) // check if route is public page

  if (authRequired && !token) {
    return next('/login')
  } else if (to.path === '/login' && token) {
    return next('/')
  }

  const userStore = useUserStore()
  const user = userStore.user as UserDtd
  const allowedRoles = to.meta.roles as Role[]

  if (!allowedRoles || allowedRoles.length === 0) {
    return next()
  }

  //admin is allowed to do everything
  const isAdmin = user?.roles?.some(role => role.name === Role.ADMIN)
  if (isAdmin) {
    return next()
  }


 // Check if user has role
  const hasRole = user?.roles?.some(role => allowedRoles.includes(role.name))
  if (!hasRole) {
    console.error("FORBIDDEN")
    return next('/forbidden')
  }

  next()
})

export default router
