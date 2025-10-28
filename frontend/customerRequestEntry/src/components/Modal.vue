<script setup lang="ts">
import { defineProps, defineEmits } from 'vue'
import { Card, CardHeader, CardTitle, CardContent } from '@/components/ui/card'
import { Button } from '@/components/ui/button'

const props = defineProps<{
  show: boolean
  message: string
  title: string
}>()

const emits = defineEmits<{
  (e: 'close'): void
}>()

function close() {
  emits('close')
}
</script>

<template>
  <transition name="fade">
    <div v-if="props.show" class="fixed inset-0 z-50 flex items-center bg-black/50">
      <Card class="p-6 mx-auto  max-w-150 bg-green-200">
        <CardHeader>
          <CardTitle>{{ props.title }}</CardTitle>
        </CardHeader>
        <CardContent class="px-6 py-4 break-words">
          {{ props.message }}
        </CardContent>
        <div class="flex justify-end mt-4">
          <Button @click="close">Schließen</Button>
        </div>
      </Card>
    </div>
  </transition>
</template>
