<script setup lang="ts">
import {computed, onMounted, ref} from "vue";
import {useTaskStore} from "@/stores/taskStore.ts";
import {ScrollArea} from "@/components/ui/scroll-area";
import {Card, CardContent, CardFooter, CardHeader, CardTitle} from "@/components/ui/card";
import {Badge} from "@/components/ui/badge";
import TaskOverviewView from "@/views/TaskManagerView/TaskOverviewView.vue";
import TaskEditView from "@/views/TaskManagerView/TaskEditView.vue";
import TaskCapacityPlanningView
  from "@/views/ResourceCapacityPlanningView/TaskCapacityPlanningView.vue";
import {ResizableHandle, ResizablePanel, ResizablePanelGroup} from "@/components/ui/resizable";

const taskStore = useTaskStore();
const selectedTaskId = ref<number>(); // für Highlight

</script>

<template>
    <ResizablePanelGroup direction="horizontal">
      <!-- Linkes Panel -->
      <ResizablePanel :default-size="50" class="min-w-70 max-w-100" >
        <TaskOverviewView/>
      </ResizablePanel>

      <!-- Divider -->
      <ResizableHandle class="bg-gray-400 w-1 cursor-col-resize"/>

      <!-- Rechtes Panel -->
      <ResizablePanel class="min-w-140">
        <TaskEditView/>
      </ResizablePanel>
    </ResizablePanelGroup>
</template>

