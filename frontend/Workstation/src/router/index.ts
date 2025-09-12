import { createRouter, createWebHistory } from 'vue-router'
import TaskOverviewView from "@/views/TaskManagerView/TaskOverviewView.vue";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'taskOverviewView',
      component: TaskOverviewView,
    },
  ],
})

export default router
