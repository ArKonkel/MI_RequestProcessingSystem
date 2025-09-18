<script setup lang="ts">
import {computed, onMounted, ref} from "vue";
import {useRequestStore} from "@/stores/requestStore.ts";
import {ScrollArea} from "@/components/ui/scroll-area";
import {Card, CardContent, CardFooter, CardHeader, CardTitle} from "@/components/ui/card";
import {Badge} from "@/components/ui/badge";
import type {RequestDtd} from "@/documentTypes/dtds/RequestDtd.ts";
import {CategoryLabel} from "@/documentTypes/types/Category.ts";

const requestStore = useRequestStore();
const selectedRequestId = ref<number>(); // für Highlight

onMounted(async () => {
  await requestStore.fetchRequests();
});

const requests = computed(() => requestStore.requestData?.requests ?? []);

function formatDate(date: string | null) {
  if (!date) return "Kein Eingangsdatum";
  return new Date(date).toLocaleDateString("de-DE", {
    year: "numeric",
    month: "short",
    day: "numeric",
  });
}

function getPriorityColor(priority: string) {
  switch (priority) {
    case "HIGH":
      return "destructive";
    case "MEDIUM":
      return "default";
    case "LOW":
      return "secondary";
    default:
      return "outline";
  }
}

function selectRequest(request: RequestDtd) {
  requestStore.setSelectedRequest(request)
}
</script>

<template>
  <ScrollArea class="h-screen rounded-md border overflow-y-auto p-4">
    <div class="flex flex-col gap-3">
      <Card
        v-for="request in requests"
        :key="request.processItem.id"
        @click="selectRequest(request)"
        :class="[
          'hover:bg-accent/30 transition-colors cursor-pointer',
          selectedRequestId === request.processItem.id ? 'bg-accent border-accent-foreground' : ''
        ]"
      >
        <CardHeader>
          <div class="flex w-full justify-between mb-2">
            <Badge
              variant="outline"
              class="text-xs"
            >
              {{ CategoryLabel[request.category] }}
            </Badge>
            <Badge :variant="getPriorityColor(request.priority)">
              {{ request.priority }}
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

        <CardFooter class="flex flex-col items-start gap-2">


          <p class="text-sm text-muted-foreground self-end">
            {{ formatDate(request.processItem.creationDate) }}
          </p>
        </CardFooter>
      </Card>
    </div>
  </ScrollArea>
</template>
