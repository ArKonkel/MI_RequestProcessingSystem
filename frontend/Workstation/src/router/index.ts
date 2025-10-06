import { createRouter, createWebHistory } from 'vue-router'
import TaskView from '@/views/TaskManagerView/TaskView.vue'
import TaskCapacityPlanningView from '@/views/ResourceCapacityPlanningView/TaskCapacityPlanningView.vue'
import RequestView from '@/views/RequestManagerView/RequestView.vue'
import RequestDetailView from '@/views/RequestManagerView/RequestDetailView.vue'
import TaskDetailView from '@/views/TaskManagerView/TaskDetailView.vue'
import ProjectDetailView from "@/views/ProjectPlannerView/ProjectDetailView.vue";
import ProjectView from "@/views/ProjectPlannerView/ProjectView.vue";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
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
      path: '/capacityPlanning/:taskId',
      name: 'capacityPlanningView',
      component: TaskCapacityPlanningView,
    },
  ],
})

export default router
