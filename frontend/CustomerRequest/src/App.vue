<script setup lang="ts">
import { RouterLink, RouterView } from 'vue-router'
import {SidebarProvider, SidebarTrigger} from "@/components/ui/sidebar";
import AppSidebar from "@/components/AppSidebar.vue";

</script>

<template>
  <SidebarProvider>
    <AppSidebar />
    <main class="flex-1 h-screen">
      <SidebarTrigger />
      <RouterView />
    </main>
  </SidebarProvider>
</template>
