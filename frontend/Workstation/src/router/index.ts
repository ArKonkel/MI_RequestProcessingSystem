import { createRouter, createWebHistory } from 'vue-router'
import TaskManagerView from "@/views/TaskManagerView/TaskManagerView.vue";
import TaskCapacityPlanningView
  from "@/views/ResourceCapacityPlanningView/TaskCapacityPlanningView.vue";
import RequestManagerView from "@/views/RequestManagerView/RequestManagerView.vue";
import ProjectPlanningView from "@/views/ProjectPlannerView/ProjectPlanningView.vue";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/requests',
      name: 'requestManagerView',
      component: RequestManagerView,
    },
    {
      path: '/projects',
      name: 'projectsPlanningView',
      component: ProjectPlanningView,
    },
    {
      path: '/tasks',
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
