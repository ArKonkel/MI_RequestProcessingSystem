<script setup lang="ts">
import { ref, watch } from 'vue'
import { Input } from '@/components/ui/input'
import { ScrollArea } from '@/components/ui/scroll-area'
import { getAllExpertise } from '@/services/expertiseService'
import type { ExpertiseDtd } from '@/documentTypes/dtds/ExpertiseDtd'
import {onClickOutside} from "@vueuse/core";

const props = defineProps<{ modelValue: number | null }>()
const emit = defineEmits<{
  (emit: 'update:modelValue', value: number | null): void
}>()

const expertises = ref<ExpertiseDtd[]>([])
const filteredExpertises = ref<ExpertiseDtd[]>([])
const search = ref('')
const dropdownOpen = ref(false)
const dropdownRef = ref<HTMLElement | null>(null)


watch(
  () => props.modelValue,
  (val) => {
    const selected = expertises.value.find((expertise) => expertise.id === val)
    search.value = selected?.name ?? ''
  },
)

async function loadExpertises() {
  if (expertises.value.length === 0) {
    expertises.value = await getAllExpertise()
  }
  filteredExpertises.value = expertises.value
  dropdownOpen.value = true
}

watch(search, (val) => {
  filteredExpertises.value = expertises.value.filter((expertise) =>
    expertise.name.toLowerCase().includes(val.toLowerCase()),
  )
})

function selectExpertise(expertise: ExpertiseDtd) {
  emit('update:modelValue', expertise.id)
  search.value = expertise.name
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
      placeholder="Keine Expertise"
      @focus="loadExpertises"
      @click.stop="loadExpertises"
      autocomplete="off"
    />

    <div
      v-if="dropdownOpen"
      ref="dropdownRef"
      class="absolute z-50 mt-1 w-full max-h-60 overflow-auto border rounded bg-white shadow-lg"
    >
      <ScrollArea class="max-h-60">
        <div v-for="expertise in filteredExpertises" :key="expertise.id">
          <div class="p-2 hover:bg-blue-100 cursor-pointer" @click="selectExpertise(expertise)">
            {{ expertise.name }}
          </div>
        </div>
        <div v-if="filteredExpertises.length === 0" class="p-2 text-gray-400">Kein Ergebnis</div>
      </ScrollArea>
    </div>
  </div>
</template>

<style scoped>
.relative {
  position: relative;
}
</style>
