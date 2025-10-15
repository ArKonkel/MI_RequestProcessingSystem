import { defineStore } from 'pinia'
import { reactive, ref, computed } from 'vue'
import type { EmployeeDtd } from '@/documentTypes/dtds/EmployeeDtd.ts'
import { getAllEmployees } from '@/services/employeeService.ts'

export const useEmployeeStore = defineStore('employeeStore', () => {
  const employeesData = reactive({
    employees: [] as EmployeeDtd[],
  })

  const selectedEmployeeId = ref<number | null>(null)

  const selectedEmployees = computed(
    () =>
      employeesData.employees.find((employee) => employee.id === selectedEmployeeId.value) ?? null,
  )

  async function fetchEmployees() {
    try {
      employeesData.employees = await getAllEmployees()

      if (!selectedEmployeeId.value && employeesData.employees.length > 0) {
        selectedEmployeeId.value = employeesData.employees[0].id
      }
    } catch (error) {
      console.error('Fehler beim Laden der employees:', error)
    }
  }

  function setSelectedEmployee(id: number) {
    selectedEmployeeId.value = id
  }

  return {
    employeesData,
    selectedEmployeeId,
    selectedEmployees,
    fetchEmployees,
    setSelectedEmployee,
  }
})
