import { createRouter, createWebHistory } from 'vue-router'
import RequestCreationView from '@/views/RequestEntryViews/RequestCreationView.vue'
import SentRequestView from "@/views/RequestEntryViews/SentRequestView.vue";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
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

export default router
