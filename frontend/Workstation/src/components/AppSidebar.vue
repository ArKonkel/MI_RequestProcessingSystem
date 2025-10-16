<script setup lang="ts">
import {Calendar, User, Inbox, Search, Settings, Mail, ClipboardList} from 'lucide-vue-next'

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
import {Button} from '@/components/ui/button'
import {useRouter} from 'vue-router'
import {useUserStore} from "@/stores/userStore.ts";
import LoggedInCard from "@/components/LoggedInCard.vue";




const items = [
  {
    title: 'Requests',
    url: '/requests',
    icon: Inbox,
  },
  {
    title: 'Tasks',
    url: '/tasks',
    icon: ClipboardList,
  },
  {
    title: 'Projects',
    url: '/projects',
    icon: Calendar,
  },
  {
    title: 'Employees',
    url: '/employees',
    icon: User,
  },
]


</script>

<template>
  <Sidebar>
    <SidebarContent>
      <SidebarGroup>
        <SidebarGroupLabel>Application</SidebarGroupLabel>
        <SidebarGroupContent>
          <SidebarMenu>
            <SidebarMenuItem v-for="item in items" :key="item.title">
              <SidebarMenuButton asChild>
                <a :href="item.url">
                  <component :is="item.icon"/>
                  <span>{{ item.title }}</span>
                </a>
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
