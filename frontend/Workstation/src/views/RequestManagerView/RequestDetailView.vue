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
import {PriorityLabel} from "@/documentTypes/types/Priority.ts";
import {RequestStatusLabel} from "@/documentTypes/types/RequestStatus.ts";

const requestStore = useRequestStore()
const request = computed<RequestDtd>(() => requestStore.requestData.selectedRequest!);

const commentText = ref("")
const comments = ref([
  {
    id: 1,
    author: "Lorem Ipsum",
    date: "2025-01-01T16:20:00",
    text: "consetetur sadipscing elitr..."
  }
])

function addComment() {
  if (!commentText.value) return
  //TODO add her comment API call
  console.log(comments.value + "added as comment.")
}
</script>

<template>
  <div v-if="request" class="flex h-screen gap-6 p-6">
    <!-- Linker Hauptbereich -->
    <ScrollArea class="flex-1 overflow-auto">
      <div class="p-6 space-y-4">
        <div>
          <Badge>{{
              CategoryLabel[request.category]
            }}
          </Badge>
          <h2 class="text-xl font-bold"> {{ request.processItem.id }} - {{
              request.processItem.title
            }}</h2>

          <div class="flex gap-6 mt-4 text-sm">
            <div><span class="font-semibold">Kunde: </span><br/>{{ request.customer.id }} -
              {{ request.customer.firstName }}
            </div>
            <div><span class="font-semibold">Eingegangen am: </span><br/>{{
                new Date(request.processItem.creationDate!).toLocaleDateString("de-DE")
              }}
            </div>
          </div>
        </div>

        <!-- Shadcn-Accordion -->
        <Accordion type="multiple" class="w-full" collapsible
                   :defaultValue="['desc', 'acceptance', 'comments']">
          <AccordionItem value="desc">
            <AccordionTrigger>Beschreibung</AccordionTrigger>
            <AccordionContent>
              <Textarea
                v-model="request.processItem.description"
                class="mt-2 min-h-[200px] resize-none"
              />
            </AccordionContent>
          </AccordionItem>

          <AccordionItem value="attachments">
            <AccordionTrigger>Anhänge</AccordionTrigger>
            <AccordionContent>
              <Button variant="outline">Datei hochladen</Button>
            </AccordionContent>
          </AccordionItem>

          <AccordionItem value="linked_tasks">
            <AccordionTrigger>Verknüpfte Aufgaben</AccordionTrigger>
            <AccordionContent>
              <div class="flex items-center justify-between border p-2 rounded">
                <div class="flex items-center gap-2">
                  <input type="checkbox" checked/>
                  <span class="font-semibold">A-01</span>
                  <span>Lorem Ipsum dolor set amet</span>
                </div>
                <Badge>Status</Badge>
              </div>
            </AccordionContent>
          </AccordionItem>

          <AccordionItem value="linked_projects">
            <AccordionTrigger>Verknüpfte Projekte</AccordionTrigger>
            <AccordionContent>
              <div class="flex items-center justify-between border p-2 rounded">
                <div class="flex items-center gap-2">
                  <input type="checkbox" checked/>
                  <span class="font-semibold">A-01</span>
                  <span>Lorem Ipsum dolor set amet</span>
                </div>
                <Badge>Status</Badge>
              </div>
            </AccordionContent>
          </AccordionItem>

          <!--
                    <AccordionItem value="comments">
                      <AccordionTrigger>Kommentare</AccordionTrigger>
                      <AccordionContent>
                        <div class="space-y-4">
                          <Textarea v-model="commentText" placeholder="Verfasse dein Kommentar"/>
                          <Button @click="addComment">Senden</Button>

                          <div v-for="comment in comments" :key="comment.id" class="border-t pt-2 text-sm">
                            <div class="font-semibold">{{ comment.author }}</div>
                            <div class="text-xs text-muted-foreground">
                              {{ new Date(comment.date).toLocaleString("de-DE") }}
                            </div>
                            <p>{{ comment.text }}</p>
                          </div>
                        </div>
                      </AccordionContent>
                    </AccordionItem>
                    -->
        </Accordion>

        <!--
        //TODO add hier zur projektplanung
        <RouterLink :to="{ name: 'capacityPlanningView', params: { id: request.processItem.id } }">
          <Button class="mt-6">Zur Planung</Button>
        </RouterLink>
        -->
      </div>
    </ScrollArea>

    <!-- Rechte Sidebar -->
    <div class="w-[200px] space-y-4 p-4 border-l-2 border-accent-200 h-screen">
      <div>
        <label class="text-sm font-semibold">Priorität</label>
        <Select v-model="request.priority">
          <SelectTrigger>
            <SelectValue placeholder="Select..."/>
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
        <Select v-model="request.status">
          <SelectTrigger>
            <SelectValue placeholder="Offen"/>
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
          v-model="request.processItem.assigneeId"
          placeholder="Keine Person zugewiesen"
        /></div>

      <div>
        <label class="text-sm font-semibold">Geschätzte Zeit</label>
        <Input
          type="number"
          v-model="request.estimatedScope"
          placeholder="Schätzung in Minuten"
        />
      </div>
    </div>
  </div>
</template>
