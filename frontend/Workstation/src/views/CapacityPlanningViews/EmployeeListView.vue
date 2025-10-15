<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { useEmployeeStore } from '@/stores/employeeStore.ts'
import { ScrollArea } from '@/components/ui/scroll-area'
import { Card, CardContent, CardFooter, CardHeader, CardTitle } from '@/components/ui/card'
import { Badge } from '@/components/ui/badge'
import { useRoute, useRouter } from 'vue-router'
import type { EmployeeDtd } from '@/documentTypes/dtds/EmployeeDtd.ts'

const employeeStore = useEmployeeStore()
const router = useRouter()
const route = useRoute()

onMounted(async () => {
  await employeeStore.fetchEmployees()
})

watch(
  () => route.params.employeeId,
  (employeeId) => {
    if (employeeId) {
      employeeStore.setSelectedEmployee(Number(employeeId))
    }
  },
  { immediate: true },
)

const employees = computed(() => employeeStore.employeesData.employees ?? [])

function selectEmployee(employee: EmployeeDtd) {
  employeeStore.setSelectedEmployee(employee.id)
  router.push({ name: 'employeeDetailView', params: { employeeId: employee.id } })
}
</script>

<template>
  <ScrollArea class="h-screen rounded-md border overflow-y-auto p-4">
    <div class="flex flex-col gap-3">
      <Card
        v-for="employee in employees"
        :key="employee.id"
        @click="selectEmployee(employee)"
        :class="[
          'hover:bg-accent/30 transition-colors cursor-pointer',
          employeeStore.selectedEmployeeId === employee.id
            ? 'bg-accent border-accent-foreground'
            : '',
        ]"
      >
        <CardHeader>
          <div class="flex w-full justify-between mb-2">
            <Badge variant="outline" class="text-xs"> Id: {{ employee.id }} </Badge>
            <Badge variant="secondary" class="text-xs">
              {{ employee.workingHoursPerDay }} Std./Tag
            </Badge>
          </div>
          <CardTitle> {{ employee.firstName }} {{ employee.lastName }} </CardTitle>
          <p class="text-sm text-muted-foreground">
            {{ employee.email }}
          </p>
        </CardHeader>

        <CardFooter class="flex flex-row justify-between items-center">
          <p class="text-xs text-muted-foreground">
            Abteilung: {{ employee.departmentId ?? 'Keine' }}
          </p>
          <Badge variant="secondary" class="text-xs"> User-Id: {{ employee.userId ?? '—' }} </Badge>
        </CardFooter>
      </Card>
    </div>
  </ScrollArea>
</template>
