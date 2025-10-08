<script setup>
import {ref} from 'vue'
import axios from 'axios'
import {Input} from "@/components/ui/input";
import {Label} from "@/components/ui/label";
import {Button} from "@/components/ui/button";

const username = ref('')
const password = ref('')
const errorMsg = ref('')

const securityEnabled = import.meta.env.VITE_SECURITY_ENABLED === 'true';

async function handleLogin(e) {
  e.preventDefault()
  errorMsg.value = ''
  if (!securityEnabled) {
    console.log('Security is disabled. Login not needed.')
    return
  }

  try {
    const response = await axios.post('/api/auth/login', {
      username: username.value,
      password: password.value
    }, {
      headers: {'Content-Type': 'application/json'},
      withCredentials: true
    })

    const token = response.data

    localStorage.setItem('token', token); //Set localstorage, so the token is saved in the browser

    window.location.href = '/'
  } catch (err) {
    console.error(err)
    errorMsg.value = 'Login fehlgeschlagen.'
  }
}
</script>

<template>
  <div class="flex min-h-screen items-center justify-center bg-gray-50">
    <form @submit="handleLogin" class="w-full max-w-sm space-y-6 bg-white p-8 rounded shadow">
      <div v-if="errorMsg" class="text-red-500 mb-2">{{ errorMsg }}</div>
      <div>
        <Label for="username">Benutzername</Label>
        <Input id="username" v-model="username" name="username" required class="mt-2"/>
      </div>
      <div>
        <Label for="password">Passwort</Label>
        <Input id="password" v-model="password" type="password" name="password" required
               class="mt-2"/>
      </div>
      <Button type="submit" class="w-full mt-4">Login</Button>
    </form>
  </div>
</template>
