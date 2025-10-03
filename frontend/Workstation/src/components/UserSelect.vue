<script setup lang="ts">
import { ref, watch } from 'vue'
import { Input } from '@/components/ui/input'
import { ScrollArea } from '@/components/ui/scroll-area'
import { getAllUser } from '@/services/userService'
import type { UserDtd } from '@/documentTypes/dtds/UserDtd'

const props = defineProps<{ modelValue: number | null }>()
const emit = defineEmits<{
  (emit: 'update:modelValue', value: number | null): void
}>()

const users = ref<UserDtd[]>([])
const filteredUsers = ref<UserDtd[]>([])
const search = ref('')
const dropdownOpen = ref(false)

watch(
  () => props.modelValue,
  (val) => {
    const selected = users.value.find((user) => user.id === val)
    search.value = selected?.name ?? ''
  },
)

async function loadUsers() {
  if (users.value.length === 0) {
    users.value = await getAllUser()
  }
  filteredUsers.value = users.value
  dropdownOpen.value = true
}

watch(search, (val) => {
  filteredUsers.value = users.value.filter((user) =>
    user.name.toLowerCase().includes(val.toLowerCase()),
  )
})

function selectUser(user: UserDtd) {
  emit('update:modelValue', user.id)
  search.value = user.name
  dropdownOpen.value = false
}
</script>

<template>
  <label class="text-sm font-semibold">Zugewiesene Person</label>
  <div class="relative w-full">
    <Input
      v-model="search"
      placeholder="Keine Person zugewiesen"
      @focus="loadUsers"
      @click.stop="loadUsers"
      autocomplete="off"
    />

    <div
      v-if="dropdownOpen"
      class="absolute z-50 mt-1 w-full max-h-60 overflow-auto border rounded bg-white shadow-lg"
    >
      <ScrollArea class="max-h-60">
        <div v-for="user in filteredUsers" :key="user.id">
          <div class="p-2 hover:bg-blue-100 cursor-pointer" @click="selectUser(user)">
            {{ user.name }}
          </div>
        </div>
        <div v-if="filteredUsers.length === 0" class="p-2 text-gray-400">Kein Ergebnis</div>
      </ScrollArea>
    </div>
  </div>
</template>

<style scoped>
.relative {
  position: relative;
}
</style>
