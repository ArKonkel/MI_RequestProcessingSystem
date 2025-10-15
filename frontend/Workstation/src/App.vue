<script setup lang="ts">
import { RouterLink, RouterView } from 'vue-router'
import { SidebarProvider, SidebarTrigger } from '@/components/ui/sidebar'
import AppSidebar from '@/components/AppSidebar.vue'
import ErrorAlert from '@/components/ErrorAlert.vue'

import {onMounted} from "vue";
import {startListeningToNotifications} from "@/services/notificationService.ts";

onMounted(async () => {
  await startListeningToNotifications()
})
</script>

<template>
  <SidebarProvider>
    <AppSidebar />
    <main class="flex-1 h-screen">
      <SidebarTrigger />
      <RouterView />
    </main>
  </SidebarProvider>
  <ErrorAlert />
</template>
