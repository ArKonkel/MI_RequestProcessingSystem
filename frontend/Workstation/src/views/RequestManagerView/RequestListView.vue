<script setup lang="ts">
import { computed, onMounted, ref } from "vue";
import { useRequestStore } from "@/stores/requestStore.ts";
import { ScrollArea } from "@/components/ui/scroll-area";
import { Card, CardContent, CardFooter, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import type { RequestDtd } from "@/documentTypes/dtds/RequestDtd.ts";
import { CategoryLabel } from "@/documentTypes/types/Category.ts";
import { getPriorityColor, PriorityLabel } from "@/documentTypes/types/Priority.ts";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { RequestStatus, RequestStatusLabel } from "@/documentTypes/types/RequestStatus.ts";

const requestStore = useRequestStore();
const selectedRequestId = ref<number>();
const selectedStatus = ref<RequestStatus | null>(null); // Status-Filter

onMounted(async () => {
  await requestStore.fetchRequests();
});

// Computed: gefilterte Requests nach Status
const filteredRequests = computed(() => {
  const allRequests = requestStore.requestData?.requests ?? [];
  if (!selectedStatus.value) return allRequests;
  return allRequests.filter(r => r.status === selectedStatus.value);
});

function formatDate(date: string | null) {
  if (!date) return "Kein Eingangsdatum";
  return new Date(date).toLocaleDateString("de-DE", {
    year: "numeric",
    month: "short",
    day: "numeric",
  });
}

function selectRequest(request: RequestDtd) {
  requestStore.setSelectedRequest(request.processItem.id);
}
</script>

<template>
  <div class="flex-1 mb-4 w-60 justify-end">
    <Select v-model="selectedStatus">
      <SelectTrigger>
        <SelectValue placeholder="Alle Status" />
      </SelectTrigger>
      <SelectContent>
        <SelectItem :value="null">Alle Status</SelectItem>
        <SelectItem
          v-for="[status, label] in Object.entries(RequestStatusLabel)"
          :key="status"
          :value="status"
        >
          {{ label }}
        </SelectItem>
      </SelectContent>
    </Select>
  </div>

  <ScrollArea class="h-screen rounded-md border overflow-y-auto p-4">
    <div class="flex flex-col gap-3">
      <Card
        v-for="request in filteredRequests"
        :key="request.processItem.id"
        @click="selectRequest(request)"
        :class="[
          'hover:bg-accent/30 transition-colors cursor-pointer',
          selectedRequestId === request.processItem.id ? 'bg-accent border-accent-foreground' : ''
        ]"
      >
        <CardHeader>
          <div class="flex w-full justify-between mb-2">
            <Badge variant="outline" class="text-xs">
              {{ RequestStatusLabel[request.status] }}
            </Badge>
            <Badge :variant="getPriorityColor(request.priority)">
              {{ PriorityLabel[request.priority] }}
            </Badge>
          </div>
          <CardTitle>{{ request.processItem.id }} - {{ request.processItem.title }}</CardTitle>
          <p class="text-sm text-muted-foreground">
            Kunde: {{ request.customer.firstName }}
          </p>
        </CardHeader>

        <CardContent class="space-y-2">
          <div class="line-clamp-2 text-xs text-muted-foreground">
            {{ request.processItem.description.substring(0, 200) }}
          </div>
        </CardContent>

        <CardFooter class="flex flex-row justify-between items-start gap-2">
          <Badge variant="secondary" class="text-xs">
            {{ CategoryLabel[request.category] }}
          </Badge>

          <p class="text-sm text-muted-foreground self-end">
            {{ formatDate(request.processItem.creationDate) }}
          </p>
        </CardFooter>
      </Card>
    </div>
  </ScrollArea>
</template>
