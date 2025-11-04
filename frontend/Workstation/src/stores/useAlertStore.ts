import { defineStore } from 'pinia'
import { ref } from 'vue'

type AlertVariant = 'error' | 'success' | 'info' | 'warning'

export interface AlertNotification {
  id: number
  message: string
  variant: AlertVariant
  link?: string
  duration: number
}

export const useAlertStore = defineStore('alertStore', () => {
  const notifications = ref<AlertNotification[]>([])
  const defaultDuration = 5000

  function show(msg: string, type: AlertVariant = 'info', time?: number, link?: string) {
    const id = Date.now()
    const duration = time || defaultDuration
    const notification: AlertNotification = { id, message: msg, variant: type, link, duration }

    notifications.value.push(notification)

    setTimeout(() => {
      remove(id)
    }, duration)
  }

  function remove(id: number) {
    notifications.value = notifications.value.filter((n) => n.id !== id)
  }

  return { notifications, show, remove }
})
