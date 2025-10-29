<script setup lang="ts">
import { defineProps, defineEmits, computed } from 'vue'
import { Card, CardHeader, CardTitle, CardContent } from '@/components/ui/card'
import { Button } from '@/components/ui/button'

const props = defineProps<{
  show: boolean
  message: string
  title: string
  variant?: 'error' | 'success' | 'info' | 'warning'
}>()

const variantClasses: Record<string, string> = {
  error: 'bg-red-100 border border-red-300',
  success: 'bg-green-100  border border-green-300',
  info: 'bg-blue-100 border border-blue-300',
  warning: 'bg-yellow-100 border border-yellow-300',
}

const emits = defineEmits<{
  (e: 'abort'): void
  (e: '_continue'): void
}>()

function abort() {
  emits('abort')
}

function _continue() {
  emits('_continue')
}


const cardClass = computed(() => {
  return props.variant ? variantClasses[props.variant] : 'bg-green-200'
})
</script>

<template>
  <transition name="fade">
    <div v-if="props.show" class="fixed inset-0 z-50 flex items-center bg-black/50">
      <Card :class="cardClass" class="p-6 mx-auto max-w-150">
        <CardHeader>
          <CardTitle class="text-2xl">{{ props.title }}</CardTitle>
        </CardHeader>

        <CardContent class="px-6 py-4 break-words">
          <div v-if="props.message" class="mb-2">{{ props.message }}</div>
          <slot v-if="$slots.default"></slot>
        </CardContent>

        <div class="flex gap-4 justify-end mt-4">
          <Button class="cursor-pointer" @click="abort">Abbrechen</Button>
          <Button class="cursor-pointer" @click="_continue">Fortfahren</Button>
        </div>
      </Card>
    </div>
  </transition>
</template>
