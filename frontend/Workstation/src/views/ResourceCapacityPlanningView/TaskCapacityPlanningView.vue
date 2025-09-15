<script setup lang="ts">
import {computed, onMounted, ref} from "vue";
import {addDays, format} from "date-fns";
import {de} from "date-fns/locale";
import {Badge} from "@/components/ui/badge";
import {Button} from "@/components/ui/button";
import {Card, CardHeader, CardTitle} from "@/components/ui/card";
import {Avatar, AvatarFallback} from "@/components/ui/avatar";
import type {MatchCalculationResultDtd} from "@/documentTypes/dtds/MatchCalculationResultDtd.ts";
import type {MatchingEmployeeForTaskDtd} from "@/documentTypes/dtds/MatchingEmployeeForTaskDtd.ts";
import {getMatchingEmployees} from "@/services/capacityPlanningsService.ts";
import {useRoute} from "vue-router";

const route = useRoute()
const taskId = Number(route.params.id)
const matchResults = ref<MatchingEmployeeForTaskDtd | null>(null)

onMounted(async () => {
  if (!taskId) {
    console.error('Kein Task ausgewählt')
    return
  }

  try {
    matchResults.value = await getMatchingEmployees(taskId)
    console.log('Match Results:', matchResults.value)
  } catch (err: any) {
    console.error(err)
  }
})

// sichtbare Werktage
const visibleDays = 8;
const startDate = ref(new Date());

// Berechne die Tage dynamisch (MO-FR)
const days = computed(() => {
  const result = [];
  let currentDay = new Date(startDate.value);
  while (result.length < visibleDays) {
    const dayOfWeek = currentDay.getDay(); // 0=So,6= Sa
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

// Navigation
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

// Selected Match
const selectedMatchResultId = ref<number | null>(null); //null, because at start no one is selected

function selectMatchResult(matchResult: MatchCalculationResultDtd) {
  selectedMatchResultId.value = matchResult.employee.id;
  console.log(matchResult.employee.id);
}

// Check if it is Friday to Monday - Skip weekend
function isFridayToMonday(index: number) {
  if (index === 0) return false;
  const prevDate = new Date(days.value[index - 1].date);
  const currDate = new Date(days.value[index].date);
  return prevDate.getDay() === 5 && currDate.getDay() === 1; // 5=Fr,1=Mo
}
</script>

<template>
  <div class="p-4 space-y-4">
    <!-- Header -->
    <Card>
      <CardHeader class="flex flex-row justify-between">
        <div>
          <CardTitle class="text-xl">{{ matchResults?.task.processItem.id }} -
            {{ matchResults?.task.processItem.title }}
          </CardTitle>
          <div class="flex gap-2 mt-2">
            <Badge v-for="(competence) in matchResults?.task.competences"
                   variant="secondary">{{ competence.name }}
            </Badge>
          </div>
        </div>
        <div class="flex flex-col text-sm text-right">
          <span>Geschätzte Zeit: <strong>{{ matchResults?.task.estimatedTime }}</strong></span>
          <span>Geplant bis <strong>{{ matchResults?.task.dueDate }}</strong></span>
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
        :class="selectedMatchResultId === matchResult.employee.id ? 'border-2 border-accent-foreground' : ''"
        @click="selectMatchResult(matchResult)"
      >
        <!-- Person -->
        <div class="flex flex-col items-center justify-center gap-2 p-2 border-r">
          <Avatar>
            <AvatarFallback>MM</AvatarFallback>
          </Avatar>
          <div class="text-sm font-medium">{{ matchResult.employee.firstName }}
            {{ matchResult.employee.lastName }}
          </div>
          <div class="flex flex-wrap gap-1 justify-center">
            <Badge v-for="expertise in matchResult.employee.employeeExpertise" :key="expertise.id"
                   variant="outline"
                   class="text-[10px]">
              {{ expertise.expertise.name }}
            </Badge>
          </div>
        </div>

        <!-- Kalender-Entries -->
        <div
          v-for="(day, index) in days"
          :key="day.date + '-' + matchResult.calculatedCalendarCapacities"
          class="relative"
          :class="{
            'border-l': index !== 0,
            'border-l-2 border-accent-foreground': isFridayToMonday(index)
          }"
        >
          <div
            v-for="entry in matchResult.calculatedCalendarCapacities.filter(e => e.date === day.date)"
            :key="entry.title"
            class="absolute top-1 left-1 right-1 bg-blue-400/50 text-xs rounded px-2 py-1 border shadow-sm"
          >
            <strong class="truncate block max-w-xs" title="{{ entry.title }}">
              {{ entry.title }}
            </strong>
            <div class="text-[10px]">{{ entry.duration / 60 }}h</div>
          </div>
        </div>
      </div>
    </div>

    <!-- Buttons -->
    <div class="flex justify-end">
      <Button>Zuweisen</Button>
    </div>
  </div>
</template>
