<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Button } from '@/components/ui/button'
import type { RegisterDtd } from '@/documentTypes/dtds/RegisterDtd.ts'
import { register } from '@/services/authService.ts'
import { useAlertStore } from '@/stores/useAlertStore.ts'

const router = useRouter()
const alertStore = useAlertStore()

const username = ref('')
const email = ref('')
const password = ref('')
const errorMsg = ref('')

async function handleRegister(event: Event) {
  event.preventDefault()
  errorMsg.value = ''

  try {
    await register({
      username: username.value,
      email: email.value,
      password: password.value,
    } as RegisterDtd)

    alertStore.show('Erfolgreich registriert', 'success')
    router.push('/login')
  } catch (error: any) {
    alertStore.show(error.response?.data || 'Unbekannter Fehler', 'error')
    errorMsg.value = 'Registrierung fehlgeschlagen'
  }
}
</script>

<template>
  <div class="flex min-h-screen items-center justify-center bg-gray-50 p-4">
    <form @submit="handleRegister" class="w-full max-w-sm space-y-6 bg-white p-8 rounded shadow">
      <div v-if="errorMsg" class="text-red-500 mb-4">{{ errorMsg }}</div>
      <div>
        <Label for="username">Benutzername</Label>
        <Input id="username" v-model="username" name="username" required class="mt-2" />
      </div>
      <div>
        <Label for="email">E-Mail</Label>
        <Input id="email" v-model="email" name="email" type="email" required class="mt-2" />
      </div>
      <div>
        <Label for="password">Passwort</Label>
        <Input
          id="password"
          v-model="password"
          name="password"
          type="password"
          required
          class="mt-2"
        />
      </div>
      <Button type="submit" class="w-full mt-4">Registrieren</Button>
    </form>
  </div>
</template>
