<script setup lang="ts">
import {ref, computed} from "vue";
import {addDays, format} from "date-fns";
import {de} from "date-fns/locale";
import {Badge} from "@/components/ui/badge";
import {Button} from "@/components/ui/button";
import {Card, CardHeader, CardTitle} from "@/components/ui/card";
import {Avatar, AvatarFallback} from "@/components/ui/avatar";

interface CalendarEntryDto {
  id: number;
  title: string;
  description: string;
  date: string; // YYYY-MM-DD
  duration: number;
}

interface Person {
  id: number;
  name: string;
  role: string;
  avatar: string;
  competences: string[];
  entries: CalendarEntryDto[];
}

// Personen und Entries
const persons = ref<Person[]>([
  {
    id: 1,
    name: "Lorem Ipsum",
    role: "Entwickler",
    avatar: "LI",
    competences: ["Finanzbuchhaltung", "Linux"],
    entries: [
      {id: 1, title: "A-01", description: "Meeting", date: "2025-09-02", duration: 0},
      {id: 2, title: "A-03", description: "Code Review", date: "2025-09-03", duration: 0},
      {id: 3, title: "A-05", description: "Sprint Planning", date: "2025-09-05", duration: 0},
      {id: 4, title: "Urlaub", description: "Ferien", date: "2025-09-04", duration: 20},
    ],
  },
  {
    id: 2,
    name: "Dolor Set",
    role: "Projektleiter",
    avatar: "DS",
    competences: ["Finanzbuchhaltung"],
    entries: [
      {id: 5, title: "A-18", description: "Task Meeting", date: "2025-09-02", duration: 10},
      {id: 6, title: "A-13", description: "Task Review", date: "2025-09-03", duration: 10},
      {id: 7, title: "A-47", description: "Projektplanung", date: "2025-09-08", duration: 10},
      {id: 8, title: "A-32", description: "Team Meeting", date: "2025-09-09", duration: 10},
    ],
  },
]);

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

// Selected Person
const activePersonId = ref<number | null>(null); //null, because at start no one is selected

function selectPerson(person: Person) {
  activePersonId.value = person.id;
  console.log(person.name);
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
          <CardTitle class="text-xl">Titel: Lorem Ipsum elit ed diam</CardTitle>
          <div class="flex gap-2 mt-2">
            <Badge variant="secondary">Finanzbuchhaltung</Badge>
            <Badge variant="secondary">API-Design</Badge>
          </div>
        </div>
        <div class="flex flex-col text-sm text-right">
          <span>Geschätzte Zeit: <strong>4h</strong></span>
          <span>Geplant bis <strong>01.01.2026</strong></span>
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
        v-for="person in persons"
        :key="person.id"
        class="grid grid-cols-[200px_repeat(8,1fr)] cursor-pointer border-b"
        :class="activePersonId === person.id ? 'border-2 border-accent-foreground' : ''"
        @click="selectPerson(person)"
      >
        <!-- Person -->
        <div class="flex flex-col items-center justify-center gap-2 p-2 border-r">
          <Avatar>
            <AvatarFallback>{{ person.avatar }}</AvatarFallback>
          </Avatar>
          <div class="text-sm font-medium">{{ person.name }}</div>
          <div class="text-xs text-muted-foreground">{{ person.role }}</div>
          <div class="flex flex-wrap gap-1 justify-center">
            <Badge v-for="competence in person.competences" :key="competence" variant="outline"
                   class="text-[10px]">
              {{ competence }}
            </Badge>
          </div>
        </div>

        <!-- Kalender-Entries -->
        <div
          v-for="(day, index) in days"
          :key="day.date + '-' + person.id"
          class="relative"
          :class="{
            'border-l': index !== 0,
            'border-l-2 border-accent-foreground': isFridayToMonday(index)
          }"
        >
          <div
            v-for="entry in person.entries.filter(e => e.date === day.date)"
            :key="entry.id"
            class="absolute top-1 left-1 right-1 bg-accent text-xs rounded px-2 py-1 border shadow-sm"
          >
            <strong>{{ entry.title }}</strong>
            <div class="text-[10px]">{{ entry.description }}</div>
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
