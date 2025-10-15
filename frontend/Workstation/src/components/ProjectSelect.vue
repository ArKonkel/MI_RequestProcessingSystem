<script setup lang="ts">
import { ref, watch } from 'vue'
import { Input } from '@/components/ui/input'
import { ScrollArea } from '@/components/ui/scroll-area'
import type { ProjectDtd } from '@/documentTypes/dtds/ProjectDtd'
import { getAllProjects } from '@/services/projectService.ts'
import { onClickOutside } from '@vueuse/core'

const props = defineProps<{ modelValue: ProjectDtd | null }>()
const emit = defineEmits<{
  (emit: 'update:modelValue', value: ProjectDtd | null): void
}>()

const projects = ref<ProjectDtd[]>([])
const filteredProjects = ref<ProjectDtd[]>([])
const search = ref('')
const dropdownOpen = ref(false)
const dropdownRef = ref<HTMLElement | null>(null)

watch(
  () => props.modelValue,
  (val) => {
    search.value = val?.processItem.title ?? ''
  },
)

async function loadProjects() {
  if (projects.value.length === 0) {
    projects.value = await getAllProjects()
  }
  filteredProjects.value = projects.value
  dropdownOpen.value = true
}

watch(search, (val) => {
  filteredProjects.value = projects.value.filter((project) =>
    project.processItem.title.toLowerCase().includes(val.toLowerCase()),
  )
})

function selectProject(project: ProjectDtd) {
  emit('update:modelValue', project)
  search.value = project.processItem.title
  dropdownOpen.value = false
}

onClickOutside(dropdownRef, () => {
  dropdownOpen.value = false
})
</script>

<template>
  <div class="relative w-full">
    <Input
      v-model="search"
      placeholder="Kein Projekt ausgewählt"
      @focus="loadProjects"
      @click.stop="loadProjects"
      autocomplete="off"
    />

    <!-- add to class "absolute" for more responsiveness -->
    <div
      v-if="dropdownOpen"
      ref="dropdownRef"
      class="mt-1 w-full max-h-60 overflow-auto border rounded bg-white shadow-lg"
    >
      <ScrollArea class="max-h-60">
        <div v-for="project in filteredProjects" :key="project.processItem.id">
          <div class="p-2 hover:bg-blue-100 cursor-pointer" @click="selectProject(project)">
            {{ project.processItem.id }} - {{ project.processItem.title }}
          </div>
        </div>
        <div v-if="filteredProjects.length === 0" class="p-2 text-gray-400">Kein Ergebnis</div>
      </ScrollArea>
    </div>
  </div>
</template>
