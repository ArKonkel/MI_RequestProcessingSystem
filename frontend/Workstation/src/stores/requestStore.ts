import { defineStore } from "pinia";
import type { RequestDtd } from "@/documentTypes/dtds/RequestDtd.ts";
import { reactive, ref, computed } from "vue";
import { getRequests } from "@/services/customerRequestService.ts";
import { Client } from "@stomp/stompjs";

export const useRequestStore = defineStore("requestStore", () => {
  const wsurl = `/api/stompbroker`;
  const DEST = "/topic/customer-request";

  let stompClient: Client | null = null;

  const requestData = reactive({
    requests: [] as RequestDtd[],
  });

  const selectedRequestId = ref<number | null>(null);

  const selectedRequest = computed(() =>
    requestData.requests.find(
      (req) => req.processItem.id === selectedRequestId.value
    ) ?? null
  );

  async function fetchRequests() {
    try {
      requestData.requests = await getRequests();

      if (!selectedRequestId.value && requestData.requests.length > 0) {
        selectedRequestId.value = requestData.requests[0].processItem.id;
      }

      await startLiveUpdate();
    } catch (error) {
      console.error("Fehler beim Laden der Requests:", error);
    }
  }

  async function startLiveUpdate() {
    if (!stompClient) {
      stompClient = new Client({ brokerURL: wsurl });
      stompClient.onWebSocketError = (event) => {
        throw new Error("WebSocket Error: " + event);
      };
      stompClient.onStompError = (frameElement) => {
        throw new Error("Stompclient with Message: " + frameElement);
      };
      stompClient.onConnect = () => {
        console.log("Stomp client connected");
        if (stompClient == null) {
          throw new Error("Stomp client connection failed");
        }

        stompClient.subscribe(DEST, (message) => {
          console.log("Received message: " + message.body);
          fetchRequests(); // Reload bei Updates
        });

        stompClient.onDisconnect = () => {
          console.log("Disconnected");
        };
      };

      stompClient.activate();
    }
  }

  function setSelectedRequest(id: number) {
    selectedRequestId.value = id;
  }

  return {
    requestData,
    selectedRequestId,
    selectedRequest,
    fetchRequests,
    setSelectedRequest,
  };
});
