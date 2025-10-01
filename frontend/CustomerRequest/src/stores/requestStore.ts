import {defineStore} from "pinia";
import type {RequestDtd} from "@/documentTypes/dtds/RequestDtd.ts";
import {reactive} from "vue";
import axios from "axios";
import {getRequestsFromCustomer} from "@/services/requestService.ts";

export const useRequestStore = defineStore('requestStore', () => {

  const requestData = reactive({
    requests: [] as RequestDtd[],
    selectedRequest: null as RequestDtd | null,
  })

  async function fetchRequestsFromCustomer() {
    try {
      //TODO add customer from login
      requestData.requests = await getRequestsFromCustomer(1)

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
    fetchRequests: fetchRequestsFromCustomer,
    setSelectedRequest
  }
})

