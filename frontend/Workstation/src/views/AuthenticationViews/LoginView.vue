<script setup lang="ts">
import { ref } from 'vue'
import axios from 'axios'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Button } from '@/components/ui/button'
import {useUserStore} from "@/stores/userStore.js";
import {login} from "@/services/authService.ts";
import type {LoginDtd} from "@/documentTypes/dtds/LoginDtd.ts";

const userStore = useUserStore()

const username = ref('')
const password = ref('')
const errorMsg = ref('')

const securityEnabled = import.meta.env.VITE_SECURITY_ENABLED === 'true'

async function handleLogin(event) {
  event.preventDefault()
  errorMsg.value = ''
  if (!securityEnabled) {
    console.log('Security is disabled. Login not needed.')

    userStore.setDefaultUser() //set Default user
    return
  }

  try {
    const token = await login({
      username: username.value,
      password: password.value,
    } as LoginDtd)

    localStorage.setItem('token', token) //Set localstorage, so the token is saved in the browser

    await userStore.setUser(username.value)

    const userFromStorage = localStorage.getItem('user') //only switch when user setted
    if (userFromStorage) {
      window.location.href = '/'
    }
  } catch (err) {
    console.error(err)
    errorMsg.value = 'Benutzername oder Passwort falsch.'
  }
}
</script>

<template>
  <div class="flex min-h-screen items-center justify-center bg-gray-50">
    <form @submit="handleLogin" class="w-full max-w-sm space-y-6 bg-white p-8 rounded shadow">
      <div v-if="errorMsg" class="text-red-500 mb-2">{{ errorMsg }}</div>
      <div>
        <Label for="username">Benutzername</Label>
        <Input id="username" v-model="username" name="username" required class="mt-2" />
      </div>
      <div>
        <Label for="password">Passwort</Label>
        <Input
          id="password"
          v-model="password"
          type="password"
          name="password"
          required
          class="mt-2"
        />
      </div>
      <Button type="submit" class="w-full mt-4">Login</Button>
    </form>
  </div>
</template>
