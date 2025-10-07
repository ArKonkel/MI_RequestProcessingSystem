import type {EmployeeDtd} from "@/documentTypes/dtds/EmployeeDtd.ts";
import axios from "axios";

export async function getAllEmployees(): Promise<EmployeeDtd[]> {
  const result = await axios.get('/api/employees')
  return result.data
}
