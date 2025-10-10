import {createRouter, createWebHistory} from 'vue-router'
import axios from 'axios' // 💡 Axios importieren

import TaskView from '@/views/TaskViews/TaskView.vue'
import TaskCapacityPlanningView
  from '@/views/CapacityPlanningViews/TaskCapacityPlanningView.vue'
import RequestView from '@/views/CustomerRequestViews/RequestView.vue'
import RequestDetailView from '@/views/CustomerRequestViews/RequestDetailView.vue'
import TaskDetailView from '@/views/TaskViews/TaskDetailView.vue'
import ProjectDetailView from "@/views/ProjectViews/ProjectDetailView.vue";
import ProjectView from "@/views/ProjectViews/ProjectView.vue";
import EmployeeView from "@/views/CapacityPlanningViews/EmployeeView.vue";
import EmployeeDetailView from "@/views/CapacityPlanningViews/EmployeeDetailView.vue";
import LoginView from "@/views/AuthenticationViews/LoginView.vue";

const securityEnabled = import.meta.env.VITE_SECURITY_ENABLED === 'true';

if (securityEnabled) {

// 💡 AXIOS REQUEST INTERCEPTOR --> Adds to every requests the Authorization header with the JWT token
  axios.interceptors.request.use(
    (config) => {
      const token = localStorage.getItem('token');

      if (token) {
        config.headers['Authorization'] = `Bearer ${token}`;
      }

      return config;
    },
    (error) => {
      return Promise.reject(error);
    }
  );
}
// ------------------------------------

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'Login',
      component: LoginView
    },
    {
      path: '/',
      redirect: '/requests',
    },
    {
      path: '/requests',
      name: 'requestView',
      component: RequestView,
      children: [
        {
          path: ':requestId',
          name: 'requestDetailView',
          component: RequestDetailView,
        },
      ],
    },
    {
      path: '/projects',
      name: 'projectView',
      component: ProjectView,
      children: [
        {
          path: ':projectId',
          name: 'projectDetailView',
          component: ProjectDetailView,
        },
      ],
    },
    {
      path: '/tasks',
      name: 'taskView',
      component: TaskView,
      children: [
        {
          path: ':taskId',
          name: 'taskDetailView',
          component: TaskDetailView,
        },
      ],
    },
    {
      path: '/employees',
      name: 'employeeView',
      component: EmployeeView,
      children: [
        {
          path: ':employeeId',
          name: 'employeeDetailView',
          component: EmployeeDetailView,
        },
      ],
    },
    {
      path: '/capacityPlanning/:taskId',
      name: 'capacityPlanningView',
      component: TaskCapacityPlanningView,
    },
  ],
})

router.beforeEach((to, from, next) => {
  if (!securityEnabled) {
    console.log('Security is disabled. Skipping authentication check.');
    return next();
  }

  const token = localStorage.getItem('token') // JWT
  const publicPages = ['/login']
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
