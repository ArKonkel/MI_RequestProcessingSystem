<script setup lang="ts">
import { ref, watch } from "vue";

import { useRequestStore } from "@/stores/requestStore.ts";
import { useAlertStore } from "@/stores/useAlertStore.ts";
import { addCommentToRequest } from "@/services/commentService.ts";
import { updateCustomerRequest } from "@/services/customerRequestService.ts";

import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import {
  Accordion,
  AccordionContent,
  AccordionItem,
  AccordionTrigger,
} from "@/components/ui/accordion";
import {
  Select,
  SelectTrigger,
  SelectValue,
  SelectContent,
  SelectItem,
} from "@/components/ui/select";
import { ScrollArea } from "@/components/ui/scroll-area";

import { CategoryLabel } from "@/documentTypes/types/Category.ts";
import { PriorityLabel } from "@/documentTypes/types/Priority.ts";
import { RequestStatusLabel } from "@/documentTypes/types/RequestStatus.ts";
import { useDebouncedRef} from "@/composable/debounce.ts";

import type { RequestDtd } from "@/documentTypes/dtds/RequestDtd.ts";
import type { CommentCreateDtd } from "@/documentTypes/dtds/CommentCreateDtd.ts";

const requestStore = useRequestStore();
const alertStore = useAlertStore();

const editableRequest = ref<RequestDtd | null>(null);
const commentText = ref("");

/*
const description = useDebouncedRef(
  editableRequest.value?.processItem.description || "",
  500
);

 */

// Watch the store for changes
watch(
  () => requestStore.requestData.selectedRequest,
  (newReq) => {
    if (newReq) {
      // make local copy
      editableRequest.value = { ...newReq };
    } else {
      editableRequest.value = null;
    }
  },
  { immediate: true, deep: true }
);

/*
//watch description with debounce, so not every keystroke triggers a save
watch(description, async (newVal) => {
  if (!editableRequest.value) return;

  editableRequest.value.processItem.description = newVal;
  await saveRequest();
});

 */

// save changes
async function saveRequest() {
  console.log("save changes")
  if (!editableRequest.value) return;

  try {
    const dto = {
      description: editableRequest.value.processItem.description,
      priority: editableRequest.value.priority,
      status: editableRequest.value.status,
      assigneeId: editableRequest.value.processItem.assigneeId,
      estimatedScope: editableRequest.value.estimatedScope,
    };

    await updateCustomerRequest(editableRequest.value.processItem.id, dto);

  } catch (err) {
    console.error(err);
    alertStore.show("Fehler beim Speichern", "error");
  }
}

async function addComment() {
  if (!editableRequest.value || !commentText.value) return;

  const commentCreateDtd: CommentCreateDtd = {
    text: commentText.value,
    authorId: 1,
  };

  try {
    await addCommentToRequest(editableRequest.value.processItem.id, commentCreateDtd);
    alertStore.show("Kommentar erfolgreich erstellt", "success");
    commentText.value = "";

  } catch (err: any) {
    console.error(err);
    alertStore.show(err.response?.data || "Unbekannter Fehler", "error");
  }
}
</script>

<template>
  <div v-if="editableRequest" class="flex h-screen gap-6 p-6">
    <!-- Left Area -->
    <ScrollArea class="flex-1 overflow-auto">
      <div class="p-6 space-y-4">
        <div>
          <Badge variant="secondary">{{ CategoryLabel[editableRequest.category] }}</Badge>
          <h2 class="text-xl font-bold">
            {{ editableRequest.processItem.id }} - {{ editableRequest.processItem.title }}
          </h2>
          <div class="flex gap-6 mt-4 text-sm">
            <div>
              <span class="font-semibold">Kunde: </span><br/>
              {{ editableRequest.customer.id }} - {{ editableRequest.customer.firstName }}
            </div>
            <div>
              <span class="font-semibold">Eingegangen am: </span><br/>
              {{ new Date(editableRequest.processItem.creationDate!).toLocaleDateString("de-DE") }}
            </div>
          </div>
        </div>

        <Accordion type="multiple" class="w-full" collapsible :defaultValue="['desc','comments']">
          <AccordionItem value="desc">
            <AccordionTrigger>Beschreibung</AccordionTrigger>
            <AccordionContent>
              <Textarea
                v-model="editableRequest.processItem.description"
                class="mt-2 min-h-[200px] resize-none"
              />
            </AccordionContent>
          </AccordionItem>

          <AccordionItem value="comments">
            <AccordionTrigger>Kommentare</AccordionTrigger>
            <AccordionContent>
              <div class="space-y-4">
                <Textarea
                  v-model="commentText"
                  placeholder="Verfasse dein Kommentar"
                  class="resize-none"
                  @keydown.enter.prevent="addComment"
                />
                <div class="flex justify-end">
                  <Button @click="addComment">Senden</Button>
                </div>
                <div v-for="comment in editableRequest.processItem.comments" :key="comment.id"
                     class="border-t pt-2 text-sm">
                  <div class="font-semibold">{{ comment.author.name }}</div>
                  <div class="text-xs text-muted-foreground">
                    {{ new Date(comment.timeStamp).toLocaleString("de-DE") }}
                  </div>
                  <div class="mt-2">
                    <p class="text-lg">{{ comment.text }}</p>
                  </div>
                </div>
              </div>
            </AccordionContent>
          </AccordionItem>
        </Accordion>
      </div>
    </ScrollArea>

    <!-- right sidebar -->
    <div class="w-[200px] space-y-4 p-4 border-l-2 border-accent-200 h-screen">
      <div>
        <label class="text-sm font-semibold">Priorität</label>
        <Select v-model="editableRequest.priority" @update:modelValue="saveRequest">
          <SelectTrigger>
            <SelectValue placeholder="Select..." />
          </SelectTrigger>
          <SelectContent>
            <SelectItem
              v-for="[value, prioLabel] in Object.entries(PriorityLabel)"
              :key="value"
              :value="value"
            >
              {{ prioLabel }}
            </SelectItem>
          </SelectContent>
        </Select>
      </div>

      <div>
        <label class="text-sm font-semibold">Status</label>
        <Select v-model="editableRequest.status"  @update:modelValue="saveRequest">
          <SelectTrigger>
            <SelectValue placeholder="Offen" />
          </SelectTrigger>
          <SelectContent>
            <SelectItem
              v-for="[value, requestStatusLabel] in Object.entries(RequestStatusLabel)"
              :key="value"
              :value="value"
            >
              {{ requestStatusLabel }}
            </SelectItem>
          </SelectContent>
        </Select>
      </div>

      <div>
        <label class="text-sm font-semibold">Zugewiesene Person</label>
        <Input
          v-model="editableRequest.processItem.assigneeId"
          placeholder="Keine Person zugewiesen"
        />
      </div>

      <div>
        <label class="text-sm font-semibold">Geschätzte Zeit</label>
        <Input
          type="number"
          v-model="editableRequest.estimatedScope"
          placeholder="Schätzung in Minuten"
        />
      </div>
    </div>
  </div>
</template>
