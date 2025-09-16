import { createRouter, createWebHistory } from 'vue-router'
import RequestCreationView from "@/views/RequestEntryView/RequestCreationView.vue";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'requestCreationView',
      component: RequestCreationView,
    },
  ],
})

export default router
