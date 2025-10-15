<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { useProjectStore } from '@/stores/projectStore.ts'
import { ScrollArea } from '@/components/ui/scroll-area'
import { Card, CardContent, CardFooter, CardHeader, CardTitle } from '@/components/ui/card'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'
import { Badge } from '@/components/ui/badge'
import type { ProjectDtd } from '@/documentTypes/dtds/ProjectDtd.ts'
import { useRoute, useRouter } from 'vue-router'

import { ProjectStatus, ProjectStatusLabel } from '@/documentTypes/types/ProjectStatus.ts'

const projectStore = useProjectStore()
const selectedStatus = ref<ProjectStatus | null>(null)

const router = useRouter()
const route = useRoute()

onMounted(async () => {
  await projectStore.fetchProjects()
})

watch(
  () => route.params.projectId,
  (projectId) => {
    if (projectId) {
      projectStore.setSelectedProjects(Number(projectId))
    }
  },
  { immediate: true },
)

const filteredProjects = computed(() => {
  const allProjects = projectStore.projectsData?.projects ?? []
  if (!selectedStatus.value) return allProjects
  return allProjects.filter((p) => p.status === selectedStatus.value)
})

function formatDate(date: string | null) {
  if (!date) return 'kein Datum'
  return new Date(date).toLocaleDateString('de-DE', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
  })
}

function selectProject(project: ProjectDtd) {
  projectStore.setSelectedProjects(project.processItem.id)
  router.push({ name: 'projectDetailView', params: { projectId: project.processItem.id } })
}
</script>

<template>
  <div class="flex-1 mb-4 w-60 justify-end">
    <Select v-model="selectedStatus">
      <SelectTrigger>
        <SelectValue placeholder="Alle Status" />
      </SelectTrigger>
      <SelectContent>
        <SelectItem :value="null">Alle Status</SelectItem>
        <SelectItem
          v-for="[status, label] in Object.entries(ProjectStatusLabel)"
          :key="status"
          :value="status"
        >
          {{ label }}
        </SelectItem>
      </SelectContent>
    </Select>
  </div>

  <ScrollArea class="h-screen rounded-md border overflow-y-auto p-4">
    <div class="flex flex-col gap-3">
      <Card
        v-for="project in filteredProjects"
        :key="project.processItem.id"
        @click="selectProject(project)"
        :class="[
          'hover:bg-accent/30 transition-colors cursor-pointer',
          projectStore.selectedProjectsId === project.processItem.id
            ? 'bg-accent border-accent-foreground'
            : '',
        ]"
      >
        <CardHeader>
          <div class="flex w-full justify-between mb-2">
            <Badge variant="outline" class="text-xs">
              {{ ProjectStatusLabel[project.status] }}
            </Badge>
            <Badge variant="secondary" class="text-xs">
              {{ project.incomingDependencies.length }} Abhängigkeiten
            </Badge>
          </div>
          <CardTitle> {{ project.processItem.id }} - {{ project.processItem.title }} </CardTitle>
          <p class="text-sm text-muted-foreground">Request: {{ project.requestTitle }}</p>
        </CardHeader>

        <CardContent class="space-y-2">
          <div class="line-clamp-2 text-xs text-muted-foreground">
            {{ project.processItem.description.substring(0, 200) }}
          </div>
        </CardContent>

        <CardFooter class="flex flex-row justify-between items-start gap-2">
          <Badge variant="secondary" class="text-xs">
            Start: {{ formatDate(project.startDate) }}
          </Badge>
          <p class="text-sm text-muted-foreground self-end">
            Ende: {{ formatDate(project.endDate) }}
          </p>
        </CardFooter>
      </Card>
    </div>
  </ScrollArea>
</template>
