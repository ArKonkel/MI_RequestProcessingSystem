<script setup lang="ts">
import {computed, onMounted, ref} from "vue";
import {useRequestStore} from "@/stores/requestStore.ts";
import {ScrollArea} from "@/components/ui/scroll-area";
import {Card, CardContent, CardFooter, CardHeader, CardTitle} from "@/components/ui/card";
import {Badge} from "@/components/ui/badge";
import type {RequestDtd} from "@/documentTypes/dtds/RequestDtd.ts";
import {CategoryLabel} from "@/documentTypes/types/Category.ts";
import {getPriorityColor, PriorityLabel} from "@/documentTypes/types/Priority.ts";
import {RequestStatusLabel} from "@/documentTypes/types/RequestStatus.ts";
import {Button} from "@/components/ui/button";
import {Plus} from 'lucide-vue-next'
import {useRouter} from "vue-router";

const router = useRouter();

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

function selectRequest(request: RequestDtd) {
  requestStore.setSelectedRequest(request)
}

function goToCreateRequest() {
  router.push("/requests/create");
}
</script>

<template>
  <div class="flex items-center justify-end space-x-4 m-4">
    <span class="text-gray-700">Anfrage erstellen</span>
    <Button class="cursor-pointer" @click="goToCreateRequest">
      <Plus />
    </Button>
  </div>
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
            <Badge variant="outline">
              {{ RequestStatusLabel[request.status] }}
            </Badge>

            <Badge :variant="getPriorityColor(request.priority)">
              {{ PriorityLabel[request.priority] }}
            </Badge>
          </div>

          <CardTitle>{{ request.processItem.id }} - {{ request.processItem.title }}</CardTitle>
        </CardHeader>

        <CardContent class="space-y-2">
          <div class="line-clamp-2 text-xs text-muted-foreground">
            {{ request.processItem.description.substring(0, 200) }}
          </div>
        </CardContent>

        <CardFooter class="flex flex-row items-center justify-between gap-2">
          <Badge
            variant="secondary"
            class="text-xs"
          >
            {{ CategoryLabel[request.category] }}
          </Badge>
          <p class="text-sm text-muted-foreground">
            {{ formatDate(request.processItem.creationDate) }}
          </p>
        </CardFooter>
      </Card>
    </div>
  </ScrollArea>
</template>
