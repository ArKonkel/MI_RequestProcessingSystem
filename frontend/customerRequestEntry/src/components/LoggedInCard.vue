<script setup lang="ts">

import { LogOut } from "lucide-vue-next"
import {
  Card,
  CardContent,
  CardDescription,
  CardFooter,
  CardHeader,
  CardTitle,
} from '@/components/ui/card'
import {useUserStore} from "@/stores/userStore.ts";
import {Button} from "@/components/ui/button";
import {useRouter} from "vue-router";
import {Avatar, AvatarFallback, AvatarImage} from '@/components/ui/avatar'

const securityEnabled = import.meta.env.VITE_SECURITY_ENABLED === 'true'

const router = useRouter()
const userStore = useUserStore()

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
  <Card class="relative">
    <LogOut
      class="absolute top-4 right-4 w-4 h-4 text-muted-foreground hover:text-foreground cursor-pointer"
      @click="handleLogout"
    />

    <CardHeader class="pb-0">
      <CardTitle>Eingeloggt als:</CardTitle>
      <CardDescription class="flex items-center gap-3 mt-2">
        <Avatar>
          <AvatarFallback>CN</AvatarFallback>
        </Avatar>
        <div class="text-sm break-words">
          <span>{{ userStore.user?.name }}</span>
        </div>
      </CardDescription>
    </CardHeader>

    <CardFooter>
      <div class="text-sm break-words" v-if="userStore.user?.customer">Kunde: {{userStore.user?.customer.firstName}}</div>
      <Button
        v-if="!securityEnabled"
        variant="secondary"
        @click="handleTestLogin"
        class="cursor-pointer w-full mt-4"
      >
        Login
      </Button>
    </CardFooter>
  </Card>
</template>
