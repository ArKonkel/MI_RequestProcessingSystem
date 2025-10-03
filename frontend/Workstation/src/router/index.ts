import {createRouter, createWebHistory} from 'vue-router'
import TaskManagerView from "@/views/TaskManagerView/TaskManagerView.vue";
import TaskCapacityPlanningView
  from "@/views/ResourceCapacityPlanningView/TaskCapacityPlanningView.vue";
import RequestView from "@/views/RequestManagerView/RequestView.vue";
import RequestDetailView from "@/views/RequestManagerView/RequestDetailView.vue";

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
          component: RequestDetailView
        }
      ]
    },
    /*

  {
    path: '/projects',
    name: 'projectsPlanningView',
    component: ProjectPlanningView,

  },
     */
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
