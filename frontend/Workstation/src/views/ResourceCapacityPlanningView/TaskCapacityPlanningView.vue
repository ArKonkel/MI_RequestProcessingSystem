<script setup lang="ts">
import {computed, onMounted, ref, watch} from "vue";
import {addDays, format, subDays} from "date-fns";
import {de} from "date-fns/locale";
import {Star, CircleGauge} from "lucide-vue-next";
import {useRoute} from "vue-router";

import {Badge} from "@/components/ui/badge";
import {Button} from "@/components/ui/button";
import {Card, CardHeader, CardTitle} from "@/components/ui/card";
import {Avatar, AvatarFallback} from "@/components/ui/avatar";

import type {
  CalculatedCapacitiesOfMatchDto
} from "@/documentTypes/dtds/CalculatedCapacitiesOfMatchDto.ts";
import type {
  MatchingEmployeeCapacitiesDtd
} from "@/documentTypes/dtds/MatchingEmployeeCapacitiesDtd.ts";
import type {CalendarDtd} from "@/documentTypes/dtds/CalendarDtd.ts";

import {assignTaskToEmployee, getMatchingEmployees} from "@/services/capacityPlanningsService.ts";
import {getEmployeeCalendar} from "@/services/calendarService.ts";
import type {EmployeeDtd} from "@/documentTypes/dtds/EmployeeDtd.ts";
import type {ExpertiseDtd} from "@/documentTypes/dtds/ExpertiseDtd.ts";
import type {EmployeeExpertiseDtd} from "@/documentTypes/dtds/EmployeeExpertiseDtd.ts";
import type {
  CalculatedCapacityCalendarEntryDtd
} from "@/documentTypes/dtds/CalculatedCapacityCalendarEntryDtd .ts";
import type {TaskDtd} from "@/documentTypes/dtds/TaskDtd.ts";
import {getTask} from "@/services/taskService.ts";

const route = useRoute();
const taskId = Number(route.params.id);
const matchResults = ref<MatchingEmployeeCapacitiesDtd | null>(null);
const task = ref<TaskDtd | null>(null)

const visibleDays = 8;
const startDate = ref(new Date());
const selectedMatchResult = ref<CalculatedCapacitiesOfMatchDto | null>(null);
const bestMatchPoints = ref<number | null>(null);

// Drag & Drop State
const draggedEntry = ref<CalculatedCapacityCalendarEntryDtd | null>(null);

onMounted(async () => {
  if (!taskId) {
    console.error("Kein Task ausgewählt");
    return;
  }

  try {
    matchResults.value = await getMatchingEmployees(taskId);
    task.value = await getTask(taskId);

    await loadCalendars(); // initial Kalender laden
    calculateBestMatchPoints()
  } catch (err: any) {
    console.error(err);
  }
});

watch(startDate, async () => {
  await loadCalendars();
});


const days = computed(() => {
  const result = [];
  let currentDay = new Date(startDate.value);

  while (result.length < visibleDays) {
    const dayOfWeek = currentDay.getDay(); // 0=So,6=Sa
    if (dayOfWeek !== 0 && dayOfWeek !== 6) {
      result.push({
        date: format(currentDay, "yyyy-MM-dd"),
        label: format(currentDay, "EE", {locale: de}).toUpperCase().substring(0, 2),
      });
    }
    currentDay = addDays(currentDay, 1);
  }

  return result;
});

// --- functions

async function loadCalendars() {
  if (!matchResults.value) return;

  const matchesWithCalendar = await Promise.all(
    matchResults.value.matchCalculationResult.map(async (match) => {
      const calendar: CalendarDtd = await getEmployeeCalendar(
        match.employee.id,
        format(startDate.value, "yyyy-MM-dd"),
        format(addDays(startDate.value, visibleDays), "yyyy-MM-dd")
      );

      return {
        ...match,
        calendar,
      };
    })
  );

  matchResults.value = {
    ...matchResults.value,
    matchCalculationResult: matchesWithCalendar,
  };
}

function prevDay() {
  let newDate = addDays(startDate.value, -1);
  while (newDate.getDay() === 0 || newDate.getDay() === 6) {
    newDate = addDays(newDate, -1);
  }
  startDate.value = newDate;
}

function nextDay() {
  let newDate = addDays(startDate.value, 1);
  while (newDate.getDay() === 0 || newDate.getDay() === 6) {
    newDate = addDays(newDate, 1);
  }
  startDate.value = newDate;
}

function selectMatchResult(matchResult: CalculatedCapacitiesOfMatchDto) {
  selectedMatchResult.value = matchResult;
}

function isFridayToMonday(index: number) {
  if (index === 0) return false;
  const prevDate = new Date(days.value[index - 1].date);
  const currDate = new Date(days.value[index].date);
  return prevDate.getDay() === 5 && currDate.getDay() === 1;
}

function calculateBestMatchPoints() {
  if (!matchResults.value || matchResults.value.matchCalculationResult.length === 0) {
    bestMatchPoints.value = null;
    return;
  }

  bestMatchPoints.value = Math.max(
    ...matchResults.value.matchCalculationResult.map(match => match.expertisePoints)
  );
}

function getMatchingExpertise(
  employee: EmployeeDtd,
  taskExpertise: ExpertiseDtd[]
): EmployeeExpertiseDtd[] {

  return employee.employeeExpertise.filter(employeeExpertise =>
    taskExpertise.some(taskExpertise => taskExpertise.id === employeeExpertise.expertise.id)
  );
}

// --- Drag and Drop
function onDragStart(entry: CalculatedCapacityCalendarEntryDtd) {
  draggedEntry.value = entry;
}

function onDrop(dayDate: string, matchResult: CalculatedCapacitiesOfMatchDto) {
  if (!draggedEntry.value) return;

  const entry = draggedEntry.value;
  const oldDate = entry.date;
  entry.date = dayDate;  // new date

  // triggers rerendering
  matchResult.calculatedCalendarCapacities = [
    ...matchResult.calculatedCalendarCapacities
  ];

  console.log(`Eintrag "${entry.title}" von ${oldDate} nach ${dayDate} verschoben`);
  draggedEntry.value = null;
}

async function assignEmployee() {
  if (!selectedMatchResult.value) return;

  try {
    await assignTaskToEmployee(taskId, selectedMatchResult.value);
    console.log('Employee assigned');
  } catch (error) {
    console.error('Failed to assign employee:', error);
  }
}

</script>

<template>
  <div class="p-4 space-y-4">
    <!-- Header -->
    <Card>
      <CardHeader class="flex flex-row justify-between">
        <div>
          <CardTitle class="text-xl">{{ task?.processItem.id }} -
            {{ task?.processItem.title }}
          </CardTitle>
          <div class="flex gap-2 mt-2">
            <Badge v-for="(expertise) in task?.expertise"
                   variant="secondary">{{ expertise.name }}
            </Badge>
          </div>
        </div>
        <div class="flex flex-col text-sm text-right">
          <span>Geschätzte Zeit: <strong>{{ task?.estimatedTime }}</strong></span>
          <span>Geplant bis <strong>{{ task?.dueDate }}</strong></span>
        </div>
      </CardHeader>
    </Card>

    <!-- Navigation -->
    <div class="flex justify-between mb-2">
      <Button @click="prevDay">← Tag zurück</Button>
      <Button @click="nextDay">Tag vor →</Button>
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
            'border-l-2 border-accent-foreground': isFridayToMonday(index)
          }"
        >
          {{ day.label }}<br/>
          <span class="text-xs text-muted-foreground">{{ day.date }}</span>
        </div>
      </div>

      <!-- Personen Kalender -->
      <div
        v-for="matchResult in matchResults?.matchCalculationResult"
        :key="matchResult.employee.id"
        class="grid grid-cols-[200px_repeat(8,1fr)] cursor-pointer border-b"
        :class="selectedMatchResult === matchResult ? 'border-2 border-accent-foreground' : ''"
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
            :class="['absolute top-1 w-5 h-5',
             matchResult.expertisePoints === bestMatchPoints ? 'left-7' : 'left-1']"
          />

          <Avatar>
            <AvatarFallback>MM</AvatarFallback>
          </Avatar>
          <div class="text-sm font-medium">{{ matchResult.employee.firstName }}
            {{ matchResult.employee.lastName }}
          </div>
          <div class="flex flex-wrap gap-1 justify-center">
            <Badge
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
      'border-l-2 border-accent-foreground': isFridayToMonday(index)
    }"
          @dragover.prevent
          @drop="onDrop(day.date, matchResult)"
        >
          <!-- calculated entries -->
          <div
            v-for="entry in matchResult.calculatedCalendarCapacities.filter(e => e.date === day.date)"
            :key="entry.title"
            class="bg-blue-400/50 text-xs rounded px-2 py-1 border shadow-sm mb-1 cursor-grab w-full truncate"
            draggable="true"
            @dragstart="onDragStart(entry)"
          >
            <strong :title="entry.title">{{ entry.title }}</strong>
            <div class="text-[10px]">{{ entry.durationInMinutes / 60 }}h</div>
          </div>

          <!-- calendar entries -->
          <div
            v-for="entry in matchResult.calendar?.entries.filter(e => e.date === day.date) || []"
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
    <div class="flex justify-end">
      <Button
        @click="assignEmployee"
      >Zuweisen
      </Button>
    </div>
  </div>
</template>
