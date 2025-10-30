<script setup lang="ts">
import { ref, watch } from 'vue'
import { Input } from '@/components/ui/input'
import { ScrollArea } from '@/components/ui/scroll-area'
import type { TaskDtd } from '@/documentTypes/dtds/TaskDtd'
import { getAllTasks } from '@/services/taskService.ts'
import { onClickOutside } from '@vueuse/core'

const props = defineProps<{ modelValue: TaskDtd | null }>()
const emit = defineEmits<{
  (emit: 'update:modelValue', value: TaskDtd | null): void
}>()

const tasks = ref<TaskDtd[]>([])
const filteredTasks = ref<TaskDtd[]>([])
const search = ref('')
const dropdownOpen = ref(false)
const dropdownRef = ref<HTMLElement | null>(null)

watch(
  () => props.modelValue,
  (val) => {
    search.value = val?.processItem.title ?? ''
  },
)

async function loadTasks() {
  if (tasks.value.length === 0) {
    tasks.value = await getAllTasks()
  }
  filteredTasks.value = tasks.value
  dropdownOpen.value = true
}

watch(search, (val) => {
  filteredTasks.value = tasks.value.filter((task) =>
    task.processItem.title.toLowerCase().includes(val.toLowerCase()),
  )
})

function selectTask(task: TaskDtd) {
  emit('update:modelValue', task)
  search.value = task.processItem.title
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
      placeholder="Blockiert von..."
      @focus="loadTasks"
      @click.stop="loadTasks"
      autocomplete="off"
    />

    <div
      v-if="dropdownOpen"
      ref="dropdownRef"
      class="mt-1 w-full max-h-60 overflow-auto border rounded bg-white shadow-lg"
    >
      <ScrollArea class="max-h-60">
        <div v-for="task in filteredTasks" :key="task.processItem.id">
          <div class="p-2 hover:bg-blue-100 cursor-pointer" @click="selectTask(task)">
            {{ task.processItem.id }} - {{ task.processItem.title }}
          </div>
        </div>
        <div v-if="filteredTasks.length === 0" class="p-2 text-gray-400">Kein Ergebnis</div>
      </ScrollArea>
    </div>
  </div>
</template>
