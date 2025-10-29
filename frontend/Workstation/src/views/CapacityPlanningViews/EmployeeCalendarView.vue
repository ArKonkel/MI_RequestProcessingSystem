<script setup lang="ts">

import {computed, onMounted, ref, watch} from "vue";
import {
  getCalendars,
  initCalendarOfEmployee
} from "@/services/calendarService.ts";
import {addDays, format, parseISO} from "date-fns";
import {de} from "date-fns/locale";
import type {CalendarDtd} from "@/documentTypes/dtds/CalendarDtd.ts";
import {useAlertStore} from '@/stores/useAlertStore.ts'
import {Button} from "@/components/ui/button";
import {Avatar} from "@/components/ui/avatar";
import {useRouter} from "vue-router";

const alertStore = useAlertStore()
const router = useRouter()

const visibleDays = 8
const startDate = ref(new Date())
const calendars = ref<CalendarDtd[]>([])


onMounted(async () => {
  await loadCalendars()
})

watch(startDate, async () => {
  await loadCalendars()
})

async function loadCalendars() {
  try {
    calendars.value = await getCalendars(
      format(startDate.value, 'yyyy-MM-dd'),
      format(addDays(startDate.value, visibleDays), 'yyyy-MM-dd'),
    )

  } catch (err: any) {
    const msg = err.response?.data?.message || err.response?.data || err.message || String(err)
    alertStore.show(`Fehler beim Laden der Kalender: ${msg}`, 'error')
  }
}

const days = computed(() => {
  const result = []
  let currentDay = new Date(startDate.value)

  while (result.length < visibleDays) {
    const dayOfWeek = currentDay.getDay() // 0=So,6=Sa
    if (dayOfWeek !== 0 && dayOfWeek !== 6) {
      result.push({
        date: format(currentDay, 'yyyy-MM-dd'),
        label: format(currentDay, 'EE', {locale: de}).toUpperCase().substring(0, 2),
      })
    }
    currentDay = addDays(currentDay, 1)
  }

  return result
})

function prevDay() {
  let newDate = addDays(startDate.value, -1)
  while (newDate.getDay() === 0 || newDate.getDay() === 6) {
    newDate = addDays(newDate, -1)
  }
  startDate.value = newDate
}

function nextDay() {
  let newDate = addDays(startDate.value, 1)
  while (newDate.getDay() === 0 || newDate.getDay() === 6) {
    newDate = addDays(newDate, 1)
  }
  startDate.value = newDate
}

function isFridayToMonday(index: number) {
  if (index === 0) return false
  const prevDate = new Date(days.value[index - 1].date)
  const currDate = new Date(days.value[index].date)
  return prevDate.getDay() === 5 && currDate.getDay() === 1
}

function routeToTask(taskId: number | undefined | null = null) {

  if (taskId) {
    router.push(`/tasks/${taskId}`)
  }
}

async function importOutlookCalendar() {
  try {
    for (const calendar of calendars.value) {
      await initCalendarOfEmployee(calendar.ownerId, new Date().getFullYear())
    }

    await loadCalendars()
    alertStore.show('Kalender erfolgreich importiert.', 'success')
  } catch (err: any) {
    console.error(err)
    alertStore.show('Fehler beim importieren des Outlook Kalenders', 'error')
  }
}

function formatDate(date: string | undefined | null): string {
  if (!date) return '';
  const parsedDate = parseISO(date);
  return format(parsedDate, 'dd.MM.yyyy');
}
</script>

<template>
  <!-- Navigation -->
  <div class="flex justify-between m-2">
    <div class="flex gap-4 justify-end">
      <Button class="cursor-pointer" @click="prevDay">← Tag zurück</Button>
      <Button class="cursor-pointer" @click="nextDay">Tag vor →</Button>
    </div>

    <div class="flex gap-4 justify-end">
      <Button class="cursor-pointer" variant="secondary" @click="importOutlookCalendar">Outlook
        importieren
      </Button>
      <Button class="cursor-pointer" variant="secondary" @click="loadCalendars">Kalender neu laden
      </Button>
    </div>
  </div>


  <!-- Kalender -->
  <div class="border rounded-md">
    <div class="grid grid-cols-[200px_repeat(8,1fr)] border-b text-sm font-medium">
      <div class="p-2"></div>
      <div
        v-for="(day, index) in days"
        :key="day.date"
        class="p-2 text-center"
        :class="{
            'border-l': index !== 0,
            'border-l-2 border-accent-foreground': isFridayToMonday(index),
          }"
      >
        {{ day.label }}<br/>
        <span class="text-xs text-muted-foreground">{{ formatDate(day.date) }}</span>
      </div>
    </div>

    <!-- Personen Kalender -->
    <div
      v-for="calendar in calendars"
      :key="calendar.id"
      class="grid grid-cols-[200px_repeat(8,1fr)] border-b"
    >
      <!-- Person -->
      <div class="relative flex flex-col items-center justify-center gap-2 p-2 border-r">
        <Avatar>
          <AvatarFallback>MM</AvatarFallback>
        </Avatar>
        <div class="text-sm font-medium">
          {{ calendar.ownerFirstName }}
          {{ calendar.ownerLastName }}
        </div>
      </div>

      <div
        v-for="(day, index) in days"
        :key="day.date + '-' + calendar.entries"
        class="flex flex-col p-1 w-full min-w-0"
        :class="{
            'border-l': index !== 0,
            'border-l-2 border-accent-foreground': isFridayToMonday(index),
          }"
      >
        <!-- calendar entries -->
        <div
          v-for="entry in calendar.entries.filter((e) => e.date === day.date) || []"
          :key="entry.title"
          :class="{
          'bg-blue-400/50 text-xs rounded-sm px-2 py-1 border shadow-sm mb-1 truncate cursor-pointer hover:bg-sky-200 transition-colors': entry.taskId,
          'bg-accent text-xs rounded px-2 py-1 border shadow-sm mb-1 truncate': !entry.taskId
            }"
          @click.prevent="routeToTask(entry.taskId)"
        >
          <strong :title="entry.title">{{ entry.title }}</strong>
          <div class="text-[10px]">{{ entry.durationInMinutes / 60 }}h</div>
        </div>
      </div>
    </div>
  </div>
</template>

