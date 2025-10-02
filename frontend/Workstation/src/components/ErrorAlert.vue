<script setup lang="ts">
import { useAlertStore } from '@/stores/useAlertStore'
import { Alert, AlertDescription, AlertTitle } from '@/components/ui/alert'

const alertStore = useAlertStore()

const variantClasses: Record<string, string> = {
  error: 'bg-red-100 text-red-800 border border-red-300',
  success: 'bg-green-100 text-green-800 border border-green-300',
  info: 'bg-blue-100 text-blue-800 border border-blue-300',
  warning: 'bg-yellow-100 text-yellow-800 border border-yellow-300',
}
</script>

<template>
  <Alert
    v-if="alertStore.visible"
    class="fixed bottom-6 right-6 z-50 w-100 max-w-150 shadow-lg p-4"
    :class="variantClasses[alertStore.variant]"
  >
    <button
      type="button"
      class="absolute top-2 right-2 text-gray-500"
      @click="alertStore.visible = false"
    >
      x
    </button>
    <AlertTitle>
      {{
        alertStore.variant === 'error'
          ? 'Fehler'
          : alertStore.variant === 'success'
            ? 'Erfolg'
            : alertStore.variant === 'warning'
              ? 'Warnung'
              : 'Info'
      }}
    </AlertTitle>
    <AlertDescription class="block w-full">
      {{ alertStore.message }}
    </AlertDescription>
  </Alert>
</template>
