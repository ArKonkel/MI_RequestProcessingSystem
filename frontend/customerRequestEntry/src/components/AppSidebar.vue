<script setup lang="ts">
import { Calendar, Home, Inbox, Search, Settings, Mail, ClipboardList } from 'lucide-vue-next'

import {
  Sidebar,
  SidebarContent,
  SidebarFooter,
  SidebarGroup,
  SidebarGroupContent,
  SidebarGroupLabel,
  SidebarHeader,
  SidebarMenu,
  SidebarMenuButton,
  SidebarMenuItem,
} from '@/components/ui/sidebar'
import LoggedInCard from '@/components/LoggedInCard.vue'
import { useRoute } from 'vue-router'

const route = useRoute()

const items = [
  {
    title: 'Anfragen',
    url: '/requests',
    icon: Inbox,
  },
]
</script>

<template>
  <Sidebar>
    <SidebarContent>
      <SidebarGroup>
        <SidebarGroupLabel>Navigation</SidebarGroupLabel>
        <SidebarGroupContent>
          <SidebarMenu>
            <SidebarMenuItem v-for="item in items" :key="item.title">
              <SidebarMenuButton asChild>
                <router-link
                  :to="item.url"
                  class="flex items-center gap-2 rounded-md px-3 py-2 transition-colors"
                  :class="{
                    'bg-gray-200 text-gray-900': route.path.startsWith(item.url),
                    'text-gray-600 hover:bg-gray-100': !route.path.startsWith(item.url),
                  }"
                >
                  <component :is="item.icon" class="w-5 h-5" />
                  <span>{{ item.title }}</span>
                </router-link>
              </SidebarMenuButton>
            </SidebarMenuItem>
          </SidebarMenu>
        </SidebarGroupContent>
      </SidebarGroup>
    </SidebarContent>
    <SidebarFooter class="border-t">
      <LoggedInCard />
    </SidebarFooter>
  </Sidebar>
</template>
