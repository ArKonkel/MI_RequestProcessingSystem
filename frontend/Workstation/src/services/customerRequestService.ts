import axios from "axios";
import type {RequestDtd} from "@/documentTypes/dtds/RequestDtd.ts";

export async function getRequests(): Promise<RequestDtd[]> {
  const response = await axios.get(`/api/requests`);
  return response.data;
}
