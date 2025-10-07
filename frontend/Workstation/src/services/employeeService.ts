import type {EmployeeDtd} from "@/documentTypes/dtds/EmployeeDtd.ts";
import axios from "axios";
import type {ExpertiseLevel} from "@/documentTypes/types/ExpertiseLevel.ts";

export async function getAllEmployees(): Promise<EmployeeDtd[]> {
  const result = await axios.get('/api/employees')
  return result.data
}

export async function addExpertiseToEmployee(employeeId: number, expertiseId: number, level: ExpertiseLevel) {
  await axios.post('/api/employees/expertises', null, {
    params: {
      employeeId,
      expertiseId,
      level,
    },
  })
}
