<script setup lang="ts">
import {computed, onMounted, onUnmounted, ref, watch} from 'vue'
import {addDays, format, parseISO, subDays} from 'date-fns'
import {de} from 'date-fns/locale'
import {Star, CircleGauge, Clock} from 'lucide-vue-next'
import {useRoute, useRouter} from 'vue-router'

import {Badge} from '@/components/ui/badge'
import {Button} from '@/components/ui/button'
import {Card, CardHeader, CardTitle} from '@/components/ui/card'
import {Avatar, AvatarFallback} from '@/components/ui/avatar'

import type {
  CalculatedCapacitiesOfMatchDto
} from '@/documentTypes/dtds/CalculatedCapacitiesOfMatchDto.ts'
import type {
  MatchingEmployeeCapacitiesDtd
} from '@/documentTypes/dtds/MatchingEmployeeCapacitiesDtd.ts'
import type {CalendarDtd} from '@/documentTypes/dtds/CalendarDtd.ts'

import {
  assignTaskToEmployee,
  getMatchingEmployees,
  calculateFreeCapacity,
} from '@/services/capacityService.ts'
import {getEmployeeCalendar} from '@/services/calendarService.ts'
import type {EmployeeDtd} from '@/documentTypes/dtds/EmployeeDtd.ts'
import type {ExpertiseDtd} from '@/documentTypes/dtds/ExpertiseDtd.ts'
import type {EmployeeExpertiseDtd} from '@/documentTypes/dtds/EmployeeExpertiseDtd.ts'
import type {
  CalculatedCapacityCalendarEntryDtd
} from '@/documentTypes/dtds/CalculatedCapacityCalendarEntryDtd .ts'
import type {TaskDtd} from '@/documentTypes/dtds/TaskDtd.ts'
import {getTask} from '@/services/taskService.ts'
import {useAlertStore} from '@/stores/useAlertStore.ts'
import type {UserDtd} from "@/documentTypes/dtds/UserDtd.ts";
import UserSelect from "@/components/UserSelect.vue";
import Modal from "@/components/Modal.vue";

const route = useRoute()
const router = useRouter()
const alertStore = useAlertStore()
const taskId = Number(route.params.taskId)
const matchResults = ref<MatchingEmployeeCapacitiesDtd | null>(null)
const task = ref<TaskDtd | null>(null)

const showOverbookingModal = ref(false);

const selectedUser = ref<UserDtd | null>(null)

const visibleDays = 8
const startDate = ref(new Date())
const selectedMatchResult = ref<CalculatedCapacitiesOfMatchDto | null>(null)
const bestMatchPoints = ref<number | null>(null)

// Drag & Drop State
const draggedEntry = ref<CalculatedCapacityCalendarEntryDtd | null>(null)

const showContextMenu = ref(false)
const contextMenuPosition = ref({ x: 0, y: 0 })
const contextMenuTarget = ref<{
  day: { date: string; label: string }
  matchResult: CalculatedCapacitiesOfMatchDto
  entry?: CalculatedCapacityCalendarEntryDtd
} | null>(null)

const showSplitModal = ref(false)
const splitAmount = ref<number | null>(null)

onUnmounted(() => {
  document.removeEventListener('click', () => (showContextMenu.value = false))
})

onMounted(async () => {
  if (!taskId) {
    console.error('Kein Task ausgewählt')
    return
  }

  //Eventlistener needed for richtclick.
  document.addEventListener('click', () => (showContextMenu.value = false))

  try {
    matchResults.value = await getMatchingEmployees(taskId)
    await loadTask()

    await loadCalendars() // initial Kalender laden
    calculateBestMatchPoints()
  } catch (err: any) {
    if (err.response?.data.includes('Task not ready')) {
      router.back()
      alertStore.show(err.response?.data || 'Unbekannter Fehler', 'error')
    } else {
      alertStore.show('Keine Mitarbeiter mit passender Expertise gefunden.', 'info')
      await loadTask()
    }
  }
})

watch(startDate, async () => {
  await loadCalendars()
})

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

// --- functions

async function loadTask() {
  try {
    task.value = await getTask(taskId)
  } catch (err: any) {
    router.back()
    alertStore.show(err.response?.data || 'Unbekannter Fehler', 'error')
  }
}

async function loadCalendars() {
  if (!matchResults.value) return

  const matchesWithCalendar = await Promise.all(
    matchResults.value.matchCalculationResult.map(async (match) => {
      const calendar: CalendarDtd = await getEmployeeCalendar(
        match.employee.id,
        format(startDate.value, 'yyyy-MM-dd'),
        format(addDays(startDate.value, visibleDays), 'yyyy-MM-dd'),
      )

      return {
        ...match,
        calendar,
      }
    }),
  )

  matchResults.value = {
    ...matchResults.value,
    matchCalculationResult: matchesWithCalendar,
  }
}

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

function selectMatchResult(matchResult: CalculatedCapacitiesOfMatchDto) {
  selectedMatchResult.value = matchResult
}

function isFridayToMonday(index: number) {
  if (index === 0) return false
  const prevDate = new Date(days.value[index - 1].date)
  const currDate = new Date(days.value[index].date)
  return prevDate.getDay() === 5 && currDate.getDay() === 1
}

function calculateBestMatchPoints() {
  if (!matchResults.value || matchResults.value.matchCalculationResult.length === 0) {
    bestMatchPoints.value = null
    return
  }

  bestMatchPoints.value = Math.max(
    ...matchResults.value.matchCalculationResult.map((match) => match.expertisePoints),
  )
}

function getMatchingExpertise(
  employee: EmployeeDtd,
  taskExpertise: ExpertiseDtd[],
): EmployeeExpertiseDtd[] {
  return employee.employeeExpertise.filter((employeeExpertise) =>
    taskExpertise.some((taskExpertise) => taskExpertise.id === employeeExpertise.expertise.id),
  )
}

// --- Drag and Drop
function onDragStart(entry: CalculatedCapacityCalendarEntryDtd) {
  draggedEntry.value = entry
}

function onDrop(dayDate: string, matchResult: CalculatedCapacitiesOfMatchDto) {
  if (!draggedEntry.value) return

  const entry = draggedEntry.value
  const oldDate = entry.date
  entry.date = dayDate // new date

  // triggers rerendering
  matchResult.calculatedCalendarCapacities = [...matchResult.calculatedCalendarCapacities]

  //console.log(`Eintrag "${entry.title}" von ${oldDate} nach ${dayDate} verschoben`)
  draggedEntry.value = null
}

function isOverbooking() {
  if (!selectedMatchResult.value) return;

  const checkedDates: string[] = [];

  for (const calcEntry of selectedMatchResult.value.calculatedCalendarCapacities) {
    if (checkedDates.includes(calcEntry.date)) continue; //if date already checked, skip it

    let sumMinutes = 0;

    // sum all calculatedEntries with same date
    for (const calculatedEntry of selectedMatchResult.value.calculatedCalendarCapacities) {
      if (calculatedEntry.date === calcEntry.date) sumMinutes += calculatedEntry.durationInMinutes;
    }

    // sum all calendarEntries with same date
    if (selectedMatchResult.value.calendar) {
      for (const calendarEntry of selectedMatchResult.value.calendar.entries) {
        if (calendarEntry.date === calcEntry.date) sumMinutes += calendarEntry.durationInMinutes;
      }
    }

    checkedDates.push(calcEntry.date);

    const employeeWorkingMinutes = selectedMatchResult.value.employee.workingHoursPerDay * 60;

    if (sumMinutes > employeeWorkingMinutes) {
      return true
    }
  }
  return false
}


async function modalContinue() {
  showOverbookingModal.value = false;
  await performAssignment();
}

function modalAbort() {
  showOverbookingModal.value = false;
}

async function performAssignment() {
  if (!selectedMatchResult.value) return;

  try {
    await assignTaskToEmployee(taskId, selectedMatchResult.value);
    alertStore.show('Aufgabe erfolgreich zugewiesen', 'success');
    await router.push({ name: 'taskDetailView', params: { taskId: task.value?.processItem.id } });
  } catch (error: any) {
    alertStore.show(error.response?.data || 'Unbekannter Fehler', 'error');
  }
}


async function assignEmployee() {
  if (!selectedMatchResult.value) return;

  if (isOverbooking()) {
    showOverbookingModal.value = true;
    return;
  }

  await performAssignment();
}

async function addCapacityOfSelectedEmployee() {
  if (!selectedUser.value) return
  if (!selectedUser.value.employeeId) {
    console.error("No employeeId set")
    return
  }

  try {
    const freeCapacityOfEmployee: MatchingEmployeeCapacitiesDtd = await calculateFreeCapacity(taskId, selectedUser.value?.employeeId)

    if (!matchResults.value) {
      matchResults.value = {
        ...freeCapacityOfEmployee,
      }
    } else {
      matchResults.value?.matchCalculationResult.push(
        ...freeCapacityOfEmployee.matchCalculationResult
      )
    }


    await loadCalendars()

  } catch (error: any) {
    alertStore.show(error.response?.data || 'Hinzufügen fehlgeschlagen.', 'error')
  }
}

function sumEntriesForDay(
  matchResult: CalculatedCapacitiesOfMatchDto,
  date: string
): number {
  let sum = 0;

  for (const calculatedEntry of matchResult.calculatedCalendarCapacities) {
    if (calculatedEntry.date === date) sum += calculatedEntry.durationInMinutes;
  }
  if (matchResult.calendar) {
    for (const calendarEntry of matchResult.calendar.entries) {
      if (calendarEntry.date === date) sum += calendarEntry.durationInMinutes;
    }
  }

  return sum;
}

function isCurrentDay(date: string): boolean {
  const inputDate = new Date(date);
  const now = new Date();

  return (
    inputDate.getFullYear() === now.getFullYear() &&
    inputDate.getMonth() === now.getMonth() &&
    inputDate.getDate() === now.getDate()
  );
}

function formatDate(date: string | undefined | null): string {
  if (!date) return '';
  const parsedDate = parseISO(date);
  return format(parsedDate, 'dd.MM.yyyy');
}

function handleRightClick(day: { date: string; label: string }, matchResult: CalculatedCapacitiesOfMatchDto, event: MouseEvent, entry?: CalculatedCapacityCalendarEntryDtd) {
  event.preventDefault()
  console.log('RIGHTCLICK ENTRY', entry)
  showContextMenu.value = true
  contextMenuPosition.value = { x: event.clientX, y: event.clientY }
  contextMenuTarget.value = { day, matchResult, entry }
}

function openSplitModal() {
  showContextMenu.value = false
  showSplitModal.value = true
  splitAmount.value = null
}

async function confirmSplit() {
  showSplitModal.value = false

  if (!contextMenuTarget.value?.entry || !splitAmount.value) return

  const original = contextMenuTarget.value.entry
  const splitMinutes = splitAmount.value * 60


  if (splitMinutes >= original.durationInMinutes) {
    alertStore.show('Der Wert zur Aufteilung ist zu groß.', 'error')
    return
  }

  // subtract original entry
  original.durationInMinutes -= splitMinutes

  // crete new entry
  const newEntry = {
    ...original,
    title: original.title,
    durationInMinutes: splitMinutes,
  }

  contextMenuTarget.value.matchResult.calculatedCalendarCapacities.push(newEntry)

  // Trigger re-render
  contextMenuTarget.value.matchResult.calculatedCalendarCapacities = [
    ...contextMenuTarget.value.matchResult.calculatedCalendarCapacities,
  ]
}

</script>

<template>
  <div class="p-4 space-y-4">
    <!-- Header -->
    <Card>
      <CardHeader class="flex flex-row justify-between">
        <div>
          <CardTitle class="text-xl"
          >{{ task?.processItem.id }} -
            {{ task?.processItem.title }}
          </CardTitle>
          <div class="flex gap-2 mt-2">
            <Badge v-for="expertise in task?.expertise" variant="secondary"
            >{{ expertise.name }}
            </Badge>
          </div>
        </div>
        <div class="flex flex-col text-sm text-right">
          <span>Geschätzte Zeit: <strong>{{ task?.estimatedTime }}h</strong></span>
          <span>Geplant bis: <strong>{{ formatDate(task?.dueDate) }}</strong></span>
        </div>
      </CardHeader>
    </Card>

    <!-- Navigation -->
    <div class="flex justify-between mb-2">
      <Button class="cursor-pointer" @click="prevDay">← Tag zurück</Button>
      <Button class="cursor-pointer" @click="nextDay">Tag vor →</Button>
    </div>

    <!-- Kalender -->
    <div class="border rounded-md">
      <!-- Kopfzeile -->
      <div class="grid grid-cols-[200px_repeat(8,1fr)] border-b text-sm font-medium">
        <div class="p-2"></div>
        <div
          v-for="(day, index) in days"
          :key="day.date"
          class="p-2 text-center"
          :class="{
            'border-l': index !== 0,
            'border-l-2 border-accent-foreground': isFridayToMonday(index),
            'bg-gray-200': isCurrentDay(day.date),
          }"
        >
          {{ day.label }}<br/>
          <span class="text-xs text-muted-foreground">{{ formatDate(day.date) }}</span>
        </div>
      </div>

      <!-- Personen Kalender -->
      <div
        v-for="matchResult in matchResults?.matchCalculationResult"
        :key="matchResult.employee.id"
        class="grid grid-cols-[200px_repeat(8,1fr)] cursor-pointer border-b"
        :class="selectedMatchResult === matchResult ? 'border-2 border-b-2 border-accent-foreground' : ''"
        @click="selectMatchResult(matchResult)"
      >
        <!-- Person -->
        <div class="relative flex flex-col items-center justify-center gap-2 p-2 border-r">
          <Star
            v-if="matchResult.expertisePoints === bestMatchPoints"
            class="absolute top-1 left-1 w-5 h-5"
          />

          <CircleGauge
            v-if="matchResult.canCompleteTaskEarliest"
            :class="[
              'absolute top-1 w-5 h-5',
              matchResult.expertisePoints === bestMatchPoints ? 'left-7' : 'left-1',
            ]"
          />

          <Avatar>
            <AvatarFallback>MM</AvatarFallback>
          </Avatar>
          <div class="text-sm font-medium">
            {{ matchResult.employee.firstName }}
            {{ matchResult.employee.lastName }}
          </div>
          <div class="flex flex-wrap gap-1 justify-center">
            <Badge
              v-if="task"
              v-for="expertise in getMatchingExpertise(matchResult.employee, task!.expertise)"
              :key="expertise.id"
              variant="outline"
              class="text-[10px]"
            >
              {{ expertise.expertise.name }}
            </Badge>
          </div>
        </div>

        <div
          v-for="(day, index) in days"
          :key="day.date + '-' + matchResult.calculatedCalendarCapacities"
          class="flex flex-col p-1 w-full min-w-0"
          :class="{
            'border-l': index !== 0,
            'border-l-2 border-accent-foreground': isFridayToMonday(index),
            'bg-gray-100': isCurrentDay(day.date),
            'bg-green-200': (sumEntriesForDay(matchResult, day.date) < matchResult.employee.workingHoursPerDay * 60),
            'bg-yellow-200': (sumEntriesForDay(matchResult, day.date) === matchResult.employee.workingHoursPerDay * 60),
            'bg-red-300': (sumEntriesForDay(matchResult, day.date) > matchResult.employee.workingHoursPerDay * 60)
          }"
          @dragover.prevent
          @drop="onDrop(day.date, matchResult)"
        >

          <div class="flex items-center justify-end text-xs pb-1">
            {{
              matchResult.employee.workingHoursPerDay - (sumEntriesForDay(matchResult, day.date) / 60)
            }}h
            <div class="pl-1">
            <Clock class="w-3 h-3"/>
            </div>
          </div>
          <!-- calculated entries -->
          <div
            v-for="entry in matchResult.calculatedCalendarCapacities.filter(
              (entry) => entry.date === day.date,
            )"
            :key="entry.title"
            class="bg-blue-300 text-xs rounded px-2 py-1 border shadow-sm mb-1 cursor-grab w-full truncate"
            draggable="true"
            @dragstart="onDragStart(entry)"
            @contextmenu.prevent="handleRightClick(day, matchResult, $event, entry)"
          >
            <strong :title="entry.title">{{ entry.title }}</strong>
            <div class="text-[10px]">{{ entry.durationInMinutes / 60 }}h</div>
          </div>

          <!-- calendar entries -->
          <div
            v-for="entry in matchResult.calendar?.entries.filter((e) => e.date === day.date) || []"
            :key="entry.title"
            class="bg-accent text-xs rounded px-2 py-1 border shadow-sm mb-1 w-full truncate"
          >
            <strong :title="entry.title">{{ entry.title }}</strong>
            <div class="text-[10px]">{{ entry.durationInMinutes / 60 }}h</div>
          </div>
        </div>
      </div>
    </div>

    <!-- Buttons -->
    <div class="flex justify-between">
      <div class="flex space-x-2">
        <UserSelect v-model="selectedUser" placeholder="Weitere hinzufügen" label=""
                    not-selected-text="Keinen"/>
        <Button class="cursor-pointer" @click="addCapacityOfSelectedEmployee">+</Button>
      </div>
      <Button class="cursor-pointer" @click="assignEmployee">Zuweisen</Button>
    </div>
  </div>

  <Modal
    title="Achtung: Überbuchung"
    :show="showOverbookingModal"
    message="Sie sind dabei einen Mitarbeitenden zu überbuchen. Möchten Sie wirklich fortfahren?"
    variant="warning"
    @_continue="modalContinue"
    @abort="modalAbort"
  >
  </Modal>

  <Modal
    title="Eintrag aufteilen"
    :show="showSplitModal"
    :message="`Wie viele Stunden sollen von ${contextMenuTarget?.entry!.durationInMinutes! / 60}h aufgeteilt werden?`"
    variant="info"
    @_continue="confirmSplit"
    @abort="() => showSplitModal = false"
  >
    <div class="p-4">
      <label class="block mb-2 text-sm font-medium">Stunden aufteilen:</label>
      <input
        type="number"
        v-model.number="splitAmount"
        class="border bg-white rounded px-2 py-1 w-full"
      />
    </div>
  </Modal>

  <div
    v-if="showContextMenu"
    class="absolute z-50 bg-white border rounded shadow-md p-1"
    :style="{ top: contextMenuPosition.y + 'px', left: contextMenuPosition.x + 'px' }"
  >
    <button
      class="block px-3 py-1 text-sm hover:bg-gray-100 w-full text-left"
      @click="openSplitModal"
    >
      Aufteilen
    </button>
  </div>

</template>
