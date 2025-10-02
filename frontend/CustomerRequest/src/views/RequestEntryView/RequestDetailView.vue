<script setup lang="ts">
import {computed, ref} from "vue"

import {Badge} from "@/components/ui/badge"
import {Button} from "@/components/ui/button"
import {Input} from "@/components/ui/input"
import {
  Accordion,
  AccordionContent,
  AccordionItem,
  AccordionTrigger,
} from "@/components/ui/accordion"
import {Textarea} from "@/components/ui/textarea";
import {
  Select,
  SelectTrigger,
  SelectValue,
  SelectContent,
  SelectItem,
} from "@/components/ui/select"


import {ScrollArea} from "@/components/ui/scroll-area";
import {useRequestStore} from "@/stores/requestStore.ts";
import {CategoryLabel} from "@/documentTypes/types/Category.ts";
import type {RequestDtd} from "@/documentTypes/dtds/RequestDtd.ts";
import {getPriorityColor, PriorityLabel} from "@/documentTypes/types/Priority.ts";
import {RequestStatusLabel} from "@/documentTypes/types/RequestStatus.ts";
import {addCommentToRequest} from "@/services/commentService.ts";
import type {CommentCreateDtd} from "@/documentTypes/dtds/CommentCreateDtd.ts";
import {useAlertStore} from "@/stores/useAlertStore.ts";

const requestStore = useRequestStore()
const request = computed<RequestDtd>(() => requestStore.requestData.selectedRequest!);

const alertStore = useAlertStore()

const commentText = ref("")
const comments = ref([
  {
    id: 1,
    author: "Lorem Ipsum",
    date: "2025-01-01T16:20:00",
    text: "consetetur sadipscing elitr..."
  }
])

async function addComment() {
  if (!commentText.value)
    return

  const commentCreateDtd: CommentCreateDtd = {
    text: commentText.value,
    //TODO make author from user
    authorId: 1
  };

  try {
    await addCommentToRequest(request.value.processItem.id, commentCreateDtd)
    alertStore.show('Kommentar erfolgreich erstellt', 'success')
  } catch (error: any) {
    console.error(error)

    alertStore.show(error.response?.data || 'Unbekannter Fehler', 'error')
  }

  commentText.value = ""
}
</script>

<template>
  <div v-if="request" class="flex h-screen gap-6 p-6">
    <!-- Linker Hauptbereich -->
    <ScrollArea class="flex-1 overflow-auto">
      <div class="p-6 space-y-4">
        <div>
          <Badge variant="secondary">{{
              CategoryLabel[request.category]
            }}
          </Badge>
          <h2 class="text-xl font-bold"> {{ request.processItem.id }} - {{
              request.processItem.title
            }}</h2>
          <div>
            <div class="flex justify-between mt-4 text-sm">
              <div>
                <span class="font-semibold">Eingegangen am: </span><br/>
                {{ new Date(request.processItem.creationDate!).toLocaleDateString("de-DE") }}
              </div>

              <div class="flex gap-4">
                <!-- Hier werden Priorität und Status nebeneinander rechts -->
                <div>
                  <span class="font-semibold">Priorität: </span><br/>
                  <Badge :variant="getPriorityColor(request.priority)">
                    {{ PriorityLabel[request.priority] }}
                  </Badge>
                </div>

                <div>
                  <span class="font-semibold">Status: </span><br/>
                  <Badge variant="outline">
                    {{ RequestStatusLabel[request.status] }}
                  </Badge>

                </div>
              </div>
            </div>
          </div>
        </div>


        <!-- Shadcn-Accordion -->
        <Accordion type="multiple" class="w-full" collapsible
                   :defaultValue="['desc', 'comments']">
          <AccordionItem value="desc">
            <AccordionTrigger>Beschreibung</AccordionTrigger>
            <AccordionContent>
          <Textarea
            v-model="request.processItem.description"
            class="mt-2 min-h-[200px] resize-none cursor-default"
            readonly
            style="pointer-events: none;"
          />
            </AccordionContent>
          </AccordionItem>

          <AccordionItem value="attachments">
            <AccordionTrigger>Anhänge</AccordionTrigger>
            <AccordionContent>
              <Button variant="outline">Datei hochladen</Button>
            </AccordionContent>
          </AccordionItem>
          <AccordionItem value="comments">
            <AccordionTrigger>Kommentare</AccordionTrigger>
            <AccordionContent>
              <div class="space-y-4">
                <Textarea v-model="commentText"
                          placeholder="Verfasse dein Kommentar"
                          class="resize-none"
                          @keydown.enter.prevent="addComment"/>
                <div class="flex justify-end">
                  <Button @click="addComment">Senden</Button>
                </div>
                <div v-for="comment in request.processItem.comments" :key="comment.id"
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
  </div>
</template>
