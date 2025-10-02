import {defineStore} from "pinia";
import type {RequestDtd} from "@/documentTypes/dtds/RequestDtd.ts";
import {reactive} from "vue";
import axios from "axios";
import {getRequests, updateCustomerRequest} from "@/services/customerRequestService.ts";
import {Client} from "@stomp/stompjs";
import type {UpdateCustomerRequestDtd} from "@/documentTypes/dtds/UpdateCustomerRequestDtd.ts";

export const useRequestStore = defineStore('requestStore', () => {

  const wsurl = `/api/stompbroker`;
  const DEST = '/topic/customer-request'

  let stompClient: Client | null = null;

  const requestData = reactive({
    requests: [] as RequestDtd[],
    selectedRequest: null as RequestDtd | null,
  })

  async function fetchRequests() {
    try {
      requestData.requests = await getRequests();

      if (requestData.selectedRequest) {
        const updatedRequest = requestData.requests
          .find(request => request.processItem.id === requestData.selectedRequest?.processItem.id);

        requestData.selectedRequest = updatedRequest ?? requestData.requests[0] ?? null;
      } else {
        requestData.selectedRequest = requestData.requests[0] ?? null;
      }

      await startLiveUpdate()
    } catch (error) {
      console.error("Fehler beim Laden der Requests:", error);
    }
  }

  async function startLiveUpdate() {
    if (!stompClient) {
      stompClient = new Client({brokerURL: wsurl});
      stompClient.onWebSocketError = (event) => {
        throw new Error("WebSocket Error: " + event);
      }
      stompClient.onStompError = (frameElement) => {
        throw new Error("Stompclient with Message: " + frameElement);
      }
      stompClient.onConnect = (frame) => {
        console.log("Stomp client connected");
        if (stompClient == null) {
          throw new Error("Stomp client connection failed");
        }

        stompClient.subscribe(DEST, (message) => {
          console.log("Received message: " + message.body);

          fetchRequests();
        });

        stompClient.onDisconnect = () => {
          console.log("Disconnected");
        }
      }

      stompClient.activate();
    }
  }

  async function updateSelectedRequest(dto: UpdateCustomerRequestDtd) {
    if (!requestData.selectedRequest) return;

    try {
      const updatedRequest = await updateCustomerRequest(requestData.selectedRequest.processItem.id, dto);
      requestData.selectedRequest = updatedRequest;
    } catch (error) {
      console.error("Something went wrong on updating request:", error);
    }
  }

  async function setSelectedRequest(request: RequestDtd) {
    requestData.selectedRequest = request;
  }

  return {
    requestData,
    fetchRequests,
    setSelectedRequest,
    updateSelectedRequest,
  }
})

