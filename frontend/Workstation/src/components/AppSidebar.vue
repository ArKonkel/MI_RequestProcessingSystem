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
const securityEnabled = import.meta.env.VITE_SECURITY_ENABLED === 'true'

const router = useRouter()

const userStore = useUserStore()

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

function handleTestLogin() {
  userStore.setDefaultUser()
}

function handleLogout() {
  // Delete tokens
  localStorage.removeItem('token')
  sessionStorage.removeItem('token')

  userStore.removeUser()
  router.push('/login')
}
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
            <SidebarMenuItem>
              <Button @click="handleLogout" class="cursor-pointer w-full mt-4">Logout</Button>
              <Button v-if="!securityEnabled" variant="secondary" @click="handleTestLogin" class="cursor-pointer w-full mt-4">Login</Button>
            </SidebarMenuItem>
          </SidebarMenu>
        </SidebarGroupContent>
      </SidebarGroup>
    </SidebarContent>
  </Sidebar>
</template>
