import {defineStore} from "pinia";
import type {RequestDtd} from "@/documentTypes/dtds/RequestDtd.ts";
import {reactive} from "vue";
import {getRequestsFromCustomer} from "@/services/requestService.ts";
import {Client} from "@stomp/stompjs";

export const useRequestStore = defineStore('requestStore', () => {

  //const wsurl = `ws://${window.location.host}/api/stompbroker`;
  const wsurl = `/api/stompbroker`;
  const DEST = '/topic/customer-request'

  let stompClient: Client | null = null;

  const requestData = reactive({
    requests: [] as RequestDtd[],
    selectedRequest: null as RequestDtd | null,
  })

  async function fetchRequestsFromCustomer() {
    try {
      //TODO add customer from login
      requestData.requests = await getRequestsFromCustomer(1)

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
     // const token = localStorage.getItem('token');

      stompClient = new Client({
        brokerURL: wsurl,
        /*
        connectHeaders: {
          Authorization: `Bearer ${token}` // token aus deinem Login
        },
         */
      });

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

          fetchRequestsFromCustomer();
        });

        stompClient.onDisconnect = () => {
          console.log("Disconnected");
        }
      }

      stompClient.activate();
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

