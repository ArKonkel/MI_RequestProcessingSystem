import { createRouter, createWebHistory } from 'vue-router'
import RequestCreationView from '@/views/RequestEntryView/RequestCreationView.vue'
import SentRequestView from "@/views/RequestEntryView/SentRequestView.vue";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'requestCreationView',
      component: RequestCreationView,
    },
    {
      path: '/requests',
      name: 'sentRequestView',
      component: SentRequestView,
    },
  ],
})

export default router
