import { defineStore } from 'pinia'
import { ref } from 'vue'

type AlertVariant = 'error' | 'success' | 'info' | 'warning'

export const useAlertStore = defineStore('alertStore', () => {
  const durationTime = 5000

  const message = ref('')
  const visible = ref(false)
  const duration = ref(durationTime)
  const variant = ref<AlertVariant>('info')

  function show(msg: string, type: AlertVariant = 'info', time?: number) {
    message.value = msg
    variant.value = type
    duration.value = time || durationTime
    visible.value = true

    setTimeout(() => {
      visible.value = false
    }, duration.value)
  }

  return { message, visible, duration, variant, show }
})
