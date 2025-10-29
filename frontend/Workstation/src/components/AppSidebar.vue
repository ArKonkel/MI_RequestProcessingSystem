<script setup lang="ts">
import {Calendar, ClipboardList, Inbox, User, CalendarCog} from 'lucide-vue-next'

import {
  Sidebar,
  SidebarContent,
  SidebarFooter,
  SidebarGroup,
  SidebarGroupContent,
  SidebarGroupLabel,
  SidebarMenu,
  SidebarMenuButton,
  SidebarMenuItem,
} from '@/components/ui/sidebar'
import {useRoute} from 'vue-router'
import {useUserStore} from "@/stores/userStore.ts";
import LoggedInCard from "@/components/LoggedInCard.vue";
import {Role} from "@/documentTypes/types/Role.ts";


const route = useRoute()
const userStore = useUserStore()
const {hasAnyRole} = userStore

interface SidebarItem {
  title: string
  url: string
  icon: any
  allowedRoles: Role[]
}

const items: SidebarItem[] = [
  {
    title: 'Kundenanfragen',
    url: '/requests',
    icon: Inbox,
    allowedRoles: [Role.ADMIN, Role.CAPACITY_PLANNER, Role.PROJECT_PLANNER, Role.CUSTOMER_REQUEST_REVISER, Role.TASK_REVISER],
  },
  {
    title: 'Aufgaben',
    url: '/tasks',
    icon: ClipboardList,
    allowedRoles: [Role.ADMIN, Role.CAPACITY_PLANNER, Role.TASK_REVISER, Role.PROJECT_PLANNER, Role.CUSTOMER_REQUEST_REVISER],
  },
  {
    title: 'Projekte',
    url: '/projects',
    icon: CalendarCog,
    allowedRoles: [Role.ADMIN, Role.PROJECT_PLANNER, Role.CUSTOMER_REQUEST_REVISER],
  },
  {
    title: 'Mitarbeitende',
    url: '/employees',
    icon: User,
    allowedRoles: [Role.ADMIN, Role.CAPACITY_PLANNER],
  },
  {
    title: 'Kalender',
    url: '/employeeCalendar',
    icon: Calendar,
    allowedRoles: [Role.ADMIN, Role.CAPACITY_PLANNER],
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
            <SidebarMenuItem
              v-for="item in items"
              :key="item.title"
            >
              <SidebarMenuButton v-if="hasAnyRole(item.allowedRoles)" asChild>
                <router-link
                  :to="item.url"
                  class="flex items-center gap-2 rounded-md px-3 py-2 transition-colors"
                  :class="{
                    'bg-gray-200 text-gray-900': route.path.startsWith(item.url),
                    'text-gray-600 hover:bg-gray-100': !route.path.startsWith(item.url)
                  }"
                >
                  <component :is="item.icon" class="w-5 h-5"/>
                  <span>{{ item.title }}</span>
                </router-link>
              </SidebarMenuButton>
            </SidebarMenuItem>
          </SidebarMenu>
        </SidebarGroupContent>
      </SidebarGroup>
    </SidebarContent>

    <SidebarFooter class="border-t">
      <LoggedInCard/>
    </SidebarFooter>
  </Sidebar>
</template>
