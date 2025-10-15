<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
import { Input } from '@/components/ui/input'
import { ScrollArea } from '@/components/ui/scroll-area'
import { getAllUser } from '@/services/userService'
import type { UserDtd } from '@/documentTypes/dtds/UserDtd'
import { onClickOutside } from '@vueuse/core'

const props = defineProps<{ modelValue: UserDtd | null }>()
const emit = defineEmits<{
  (emit: 'update:modelValue', value: UserDtd | null): void
}>()

const users = ref<UserDtd[]>([])
const filteredUsers = ref<UserDtd[]>([])
const search = ref('')
const dropdownOpen = ref(false)
const dropdownRef = ref<HTMLElement | null>(null)

onMounted(() => {
  search.value = props.modelValue?.name ?? ''
})

watch(
  () => props.modelValue,
  (val) => {
    search.value = val?.name ?? ''
  },
)

function unassignUser() {
  emit('update:modelValue', null)
  search.value = ''
  dropdownOpen.value = false
}

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
  emit('update:modelValue', user)
  search.value = user.name
  dropdownOpen.value = false
}

onClickOutside(dropdownRef, () => {
  dropdownOpen.value = false
})
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
      ref="dropdownRef"
      class="absolute z-50 mt-1 w-full max-h-60 overflow-auto border rounded bg-white shadow-lg"
    >
      <ScrollArea class="max-h-60">
        <div class="p-2 hover:bg-blue-100 cursor-pointer text-gray-400" @click="unassignUser">
          Nicht zugewiesen
        </div>

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
