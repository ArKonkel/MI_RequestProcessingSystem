import {defineStore} from "pinia";
import type {RequestDtd} from "@/documentTypes/dtds/RequestDtd.ts";
import {reactive} from "vue";
import axios from "axios";
import {getRequestsFromCustomer} from "@/services/requestService.ts";
import {Client} from "@stomp/stompjs";

export const useRequestStore = defineStore('requestStore', () => {

  const wsurl = `ws://${window.location.host}/stompbroker`;
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

      if (requestData.requests.length > 0) {
        requestData.selectedRequest = requestData.requests[0];
      }

      await startLiveUpdate()
    } catch (error) {
      console.error("Fehler beim Laden der Requests:", error);
    }
  }

  async function startLiveUpdate() {
    console.log("Start Live Update");
    if (!stompClient) {
      stompClient = new Client({brokerURL: wsurl});
      console.log("1");
      stompClient.onWebSocketError = (event) => {
        throw new Error("WebSocket Error: " + event);
      }
      console.log("2");
      stompClient.onStompError = (frameElement) => {
        throw new Error("Stompclient with Message: " + frameElement);
      }
      console.log("3");
      stompClient.onConnect = (frame) => {
        console.log("Stomp client connected");
        console.log("4");
        if (stompClient == null) {
          throw new Error("Stomp client connection failed");
        }

        stompClient.subscribe(DEST, (message) => {
          console.log("Received message: " + message.body);
          //fetchRequestsFromCustomer();
        });

        stompClient.onDisconnect = () => {
          console.log("Disconnected");
        }
      }

      console.log("try to activate")
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

