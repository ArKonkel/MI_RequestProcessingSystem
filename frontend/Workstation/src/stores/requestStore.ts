import {defineStore} from "pinia";
import type {RequestDtd} from "@/documentTypes/dtds/RequestDtd.ts";
import {reactive} from "vue";
import axios from "axios";

export const useRequestStore = defineStore('requestStore', () => {

  const requestData = reactive({
    requests: [] as RequestDtd[],
    selectedRequest: null as RequestDtd | null,
  })

  async function fetchRequests() {
    try {
      const response = await axios.get<RequestDtd[]>("/api/requests");
      requestData.requests = response.data;

      if (requestData.requests.length > 0){
        requestData.selectedRequest = requestData.requests[0];
      }
    } catch (error) {
      console.error("Fehler beim Laden der Requests:", error);
    }
  }

  async function setSelectedRequest(request: RequestDtd) {
    requestData.selectedRequest = request;
  }

  return {
    requestData,
    fetchRequests,
    setSelectedRequest
  }
})

