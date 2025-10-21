<script setup lang="ts">
import {computed, ref} from "vue"

import {Badge} from "@/components/ui/badge"
import CommentsAccordion from '@/components/CommentsAccordion.vue'
import {
  Accordion,
  AccordionContent,
  AccordionItem,
  AccordionTrigger,
} from "@/components/ui/accordion"
import {Textarea} from "@/components/ui/textarea";

import {ScrollArea} from "@/components/ui/scroll-area";
import {useRequestStore} from "@/stores/requestStore.ts";
import {CategoryLabel} from "@/documentTypes/types/Category.ts";
import type {RequestDtd} from "@/documentTypes/dtds/RequestDtd.ts";
import {getPriorityColor, PriorityLabel} from "@/documentTypes/types/Priority.ts";
import {RequestStatusLabel} from "@/documentTypes/types/RequestStatus.ts";
import {addCommentToRequest} from "@/services/commentService.ts";
import type {CommentCreateDtd} from "@/documentTypes/dtds/CommentCreateDtd.ts";
import {useAlertStore} from "@/stores/useAlertStore.ts";
import {useUserStore} from "@/stores/userStore.ts";
import {TaskStatusLabel} from "@/documentTypes/types/TaskStatus.ts";
import {ProjectStatusLabel} from "@/documentTypes/types/ProjectStatus.ts";
import AttachmentList from "@/components/AttachmentList.vue";

const requestStore = useRequestStore()
const request = computed<RequestDtd>(() => requestStore.requestData.selectedRequest!);

const alertStore = useAlertStore()
const userStore = useUserStore()

const commentText = ref("")

async function addComment() {
  if (!request.value || !commentText.value) return

  if (userStore.user === null) {
    console.log("user is null")
    return
  }

  const authorId = userStore.user?.id

  const commentCreateDtd: CommentCreateDtd = {
    text: commentText.value,
    authorId: authorId,
  }

  try {
    await addCommentToRequest(request.value.processItem.id, commentCreateDtd)

    alertStore.show('Kommentar erfolgreich erstellt', 'success')
    commentText.value = ''
  } catch (err) {
    alertStore.show('Fehler beim Kommentieren', 'error')
  }
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

          <AccordionItem value="linked-tasks">
            <AccordionTrigger>Verknüpfte Aufgaben</AccordionTrigger>
            <AccordionContent class="flex flex-col gap-2">
              <!-- Tasks -->
              <div v-for="task in request.tasks" :key="task.processItem.id">
                <div
                  class="flex items-center justify-between border p-2 rounded"
                >
                  <div class="flex items-center gap-2">
                    <span>{{ task.processItem.id }}</span>
                    <span class="font-semibold">{{ task.processItem.title }}</span>
                  </div>
                  <Badge variant="secondary">{{ TaskStatusLabel[task.status] }}</Badge>
                </div>
              </div>
            </AccordionContent>
          </AccordionItem>

          <AccordionItem value="linked-projects" v-if="request.projects.length > 0">
            <AccordionTrigger>Verknüpfte Projekte</AccordionTrigger>
            <AccordionContent class="flex flex-col gap-2">
              <!-- Projects -->
              <div v-for="project in request.projects" :key="project.processItem.id">
                  <div
                    class="flex items-center justify-between border p-2 rounded"
                  >
                    <div class="flex items-center gap-2">
                      <span>{{ project.processItem.id }}</span>
                      <span class="font-semibold">{{ project.processItem.title }}</span>
                    </div>

                    <Badge variant="secondary">{{ ProjectStatusLabel[project.status] }}</Badge>
                  </div>
              </div>
            </AccordionContent>
          </AccordionItem>


          <AccordionItem value="attachment">
            <AccordionTrigger>Anhänge</AccordionTrigger>
            <AccordionContent>
              <AttachmentList
                :attachments="request.processItem.attachments"
                :processItemId="request.processItem.id"
              />
            </AccordionContent>
          </AccordionItem>

          <CommentsAccordion
            v-model="commentText"
            :comments="request.processItem.comments"
            @submit="addComment"
          />

        </Accordion>
      </div>
    </ScrollArea>
  </div>
</template>
