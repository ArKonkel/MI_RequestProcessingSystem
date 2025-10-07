<script setup lang="ts">
import {ref, watch, computed, onMounted} from 'vue'
import {addDays, format, getYear} from 'date-fns'
import {de} from 'date-fns/locale'
import {useEmployeeStore} from '@/stores/employeeStore.ts'
import {useAlertStore} from '@/stores/useAlertStore.ts'
import {ScrollArea} from '@/components/ui/scroll-area'
import {Input} from '@/components/ui/input'
import {Label} from '@/components/ui/label'
import {Badge} from '@/components/ui/badge'
import {Button} from '@/components/ui/button'
import {getEmployeeCalendar, initCalendarOfEmployee} from '@/services/calendarService.ts'
import type {CalendarDtd} from '@/documentTypes/dtds/CalendarDtd.ts'
import type {EmployeeDtd} from '@/documentTypes/dtds/EmployeeDtd.ts'
import {ExpertiseLevelLabel} from "@/documentTypes/types/ExpertiseLevel.ts";
//import { updateEmployee } from '@/services/employeeService.ts'

const employeeStore = useEmployeeStore()
const alertStore = useAlertStore()

const editableEmployee = ref<EmployeeDtd | null>(null)
const calendar = ref<CalendarDtd | null>(null)
const startDate = ref(new Date())
const visibleDays = 8
const ignoreNextUpdate = ref(false)

watch(
  () => employeeStore.selectedEmployees,
  async (newEmp) => {
    if (newEmp) {
      editableEmployee.value = {...newEmp}
      ignoreNextUpdate.value = true
      await loadCalendar()
    } else {
      editableEmployee.value = null
      calendar.value = null
    }
  },
  {immediate: true},
)

const days = computed(() => {
  const result = []
  let current = new Date(startDate.value)
  while (result.length < visibleDays) {
    const dayOfWeek = current.getDay()
    if (dayOfWeek !== 0 && dayOfWeek !== 6) {
      result.push({
        date: format(current, 'yyyy-MM-dd'),
        label: format(current, 'EE', {locale: de}).toUpperCase().substring(0, 2),
      })
    }
    current = addDays(current, 1)
  }
  return result
})

async function loadCalendar() {
  if (!editableEmployee.value) return
  try {
    calendar.value = await getEmployeeCalendar(
      editableEmployee.value.id,
      format(startDate.value, 'yyyy-MM-dd'),
      format(addDays(startDate.value, visibleDays), 'yyyy-MM-dd'),
    )

    alertStore.show('Kalender erfolgreich geladen.', 'success')
  } catch (err: any) {
    console.error(err)
    alertStore.show('Fehler beim Laden des Kalenders', 'error')
  }
}

async function importOutlookCalendar() {
  if (!editableEmployee.value) return

  try {
    await initCalendarOfEmployee(editableEmployee.value?.id, new Date().getFullYear())
    await loadCalendar()

    alertStore.show('Kalender erfolgreich importiert.', 'success')
  } catch (err: any) {
    console.error(err)
    alertStore.show('Fehler beim importieren des Outlook Kalenders', 'error')
  }

}

function prevDay() {
  let newDate = addDays(startDate.value, -1)
  while (newDate.getDay() === 0 || newDate.getDay() === 6) {
    newDate = addDays(newDate, -1)
  }
  startDate.value = newDate
  loadCalendar()
}

function nextDay() {
  let newDate = addDays(startDate.value, 1)
  while (newDate.getDay() === 0 || newDate.getDay() === 6) {
    newDate = addDays(newDate, 1)
  }
  startDate.value = newDate
  loadCalendar()
}

async function saveEmployee() {
  if (!editableEmployee.value) return
  try {
    //await updateEmployee(editableEmployee.value.id, editableEmployee.value)
    alertStore.show('Mitarbeiter gespeichert', 'success')
  } catch (err: any) {
    console.error(err)
    alertStore.show('Fehler beim Speichern des Mitarbeiters', 'error')
  }
}
</script>

<template>
  <div v-if="editableEmployee" class="flex h-screen gap-6 p-6">
    <ScrollArea class="flex-1 overflow-auto">
      <div class="p-6 space-y-6">
        <div>
          <h2 class="text-xl font-bold">
            {{ editableEmployee.firstName }} {{ editableEmployee.lastName }}
          </h2>
          <div class="text-sm text-muted-foreground mt-1">
            Mitarbeiter-Id: {{ editableEmployee.id }}
          </div>
          <span class="text-sm text-muted-foreground mt-1">
            User-Id: {{ editableEmployee.userId }}
          </span>
        </div>

        <div class="grid grid-cols-2 gap-4">
          <div>
            <Label class="mb-1">Vorname</Label>
            <Input v-model="editableEmployee.firstName" class="max-w-80"/>
          </div>
          <div>
            <Label class="mb-1">Nachname</Label>
            <Input v-model="editableEmployee.lastName" class="max-w-80"/>
          </div>
          <div class="col-span-1">
            <Label class="mb-1">E-Mail</Label>
            <Input v-model="editableEmployee.email" class="max-w-80"/>
          </div>
          <div>
            <Label class="mb-1">Arbeitszeit (h/Tag)</Label>
            <Input
              type="number"
              v-model.number="editableEmployee.workingHoursPerDay"
              class="max-w-80"
            />
          </div>
        </div>

        <div class="flex justify-end">
          <Button @click="saveEmployee">Manuell speichern</Button>
        </div>

        <div>
          <h3 class="text-lg font-semibold mt-6 mb-2">Expertisen</h3>
          <div
            v-if="editableEmployee.employeeExpertise.length"
            class="flex flex-col gap-2 text-sm border p-3 rounded-md bg-muted/20"
          >
            <div
              v-for="expertise in editableEmployee.employeeExpertise"
              :key="expertise.id"
              class="flex justify-between border-b border-accent/20 pb-1 last:border-none"
            >
              <div>
                <span class="font-semibold">{{ expertise.expertise.name }}</span>
                <span class="text-muted-foreground text-xs ml-2">
                  {{ expertise.expertise.description }}
                </span>
              </div>
              <Badge variant="secondary">
                {{ ExpertiseLevelLabel[expertise.level] }}
              </Badge>
            </div>
          </div>
          <div v-else class="text-muted-foreground text-sm">Keine Expertisen vorhanden.</div>
        </div>

        <div class="flex justify-end">
          <Button>+</Button>
        </div>

        <!-- Calendar -->
        <div class="space-y-3 mt-8">
          <div class="flex justify-between items-center">
            <h3 class="text-lg font-semibold">Kalender</h3>
            <div class="flex gap-2">
              <Button size="sm" @click="prevDay">← Tag zurück</Button>
              <Button size="sm" @click="nextDay">Tag vor →</Button>
            </div>
          </div>

          <div v-if="calendar" class="border rounded-lg overflow-x-auto shadow-sm">
            <div class="min-w-[1000px] grid grid-cols-8">
              <div
                v-for="day in days"
                :key="day.date"
                class="p-2 text-center border-b border-r last:border-r-0 bg-secondary/20 text-sm font-medium"
              >
                <span class="font-bold">{{ day.label }}</span>
                <br/>
                <span class="text-xs text-muted-foreground">{{ day.date.substring(5) }}</span>
              </div>

              <template v-for="day in days" :key="day.date">
                <div
                  class="p-2 border-r last:border-r-0 border-b align-top min-h-[100px] hover:bg-accent/10 transition-colors"
                >
                  <div
                    v-for="entry in calendar.entries.filter((e) => e.date === day.date)"
                    :key="entry.title"
                    class="bg-blue-400/50 text-xs rounded-sm px-2 py-1 border  shadow-sm mb-1 truncate cursor-pointer hover:bg-sky-200 transition-colors"
                  >
                    <strong :title="entry.title">{{ entry.title }}</strong>
                    <div class="text-[10px]">{{ entry.durationInMinutes / 60 }}h</div>
                  </div>
                  <div
                    v-if="!calendar.entries.some((e) => e.date === day.date)"
                    class="text-muted-foreground text-xs text-center italic mt-4"
                  >
                    Frei
                  </div>
                </div>
              </template>
            </div>
          </div>
          <div v-else class="text-sm text-muted-foreground italic">
            Kein Kalender verfügbar.
          </div>
        </div>
      </div>
      <div class="flex gap-4 justify-end">
        <Button variant="secondary" @click="importOutlookCalendar">Outlook importieren</Button>
        <Button variant="secondary" @click="loadCalendar">Kalender neu laden</Button>
      </div>
    </ScrollArea>
  </div>
</template>
