<script setup lang="ts">
import {useAlertStore} from '@/stores/useAlertStore'
import {Alert, AlertDescription, AlertTitle} from '@/components/ui/alert'
import {useRouter} from 'vue-router'

const alertStore = useAlertStore()
const router = useRouter()

const variantClasses: Record<string, string> = {
  error: 'bg-red-100 text-red-800 border border-red-300',
  success: 'bg-green-100 text-green-800 border border-green-300',
  info: 'bg-blue-100 text-blue-800 border border-blue-300',
  warning: 'bg-yellow-100 text-yellow-800 border border-yellow-300',
}

function navigate(link?: string) {
  if (link) router.push(link)
}
</script>

<template>
  <div class="fixed bottom-6 right-6 z-50 w-100 max-w-150 space-y-4">
    <Alert
      v-for="notification in alertStore.notifications"
      :key="notification.id"
      :class="variantClasses[notification.variant]"
      class="shadow-lg p-4 relative"
    >
      <button
        type="button"
        class="absolute top-2 right-2 text-gray-500 cursor-pointer"
        @click.stop="alertStore.remove(notification.id)"
      >
        ×
      </button>
      <AlertTitle>
        {{
          notification.variant === 'error'
            ? 'Fehler'
            : notification.variant === 'success'
              ? 'Erfolg'
              : notification.variant === 'warning'
                ? 'Warnung'
                : 'Info'
        }}
      </AlertTitle>
      <AlertDescription class="block w-full">
        {{ notification.message }}
      </AlertDescription>
      <div class="cursor-pointer flex">
        <a href="#" v-if="notification.link" class="underline text-sm text-blue-700"
           @click.prevent="navigate(notification.link)">
          dorthin
        </a>
      </div>
    </Alert>
  </div>
</template>
