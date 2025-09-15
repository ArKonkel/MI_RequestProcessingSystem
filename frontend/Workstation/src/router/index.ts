import { createRouter, createWebHistory } from 'vue-router'
import TaskOverviewView from "@/views/TaskManagerView/TaskOverviewView.vue";
import TaskManagerView from "@/views/TaskManagerView/TaskManagerView.vue";
import TaskCapacityPlanningView
  from "@/views/ResourceCapacityPlanningView/TaskCapacityPlanningView.vue";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'taskManagerView',
      component: TaskManagerView,
    },
    {
      path: '/capacityPlanning/:id',
      name: 'capacityPlanningView',
      component: TaskCapacityPlanningView,
    },
  ],
})

export default router
