import type {ExpertiseDtd} from "@/documentTypes/dtds/ExpertiseDtd.ts";
import axios from "axios";

export async function getAllExpertise(): Promise<ExpertiseDtd[]> {
  const result = await axios.get('/api/expertise')
  return result.data
}
