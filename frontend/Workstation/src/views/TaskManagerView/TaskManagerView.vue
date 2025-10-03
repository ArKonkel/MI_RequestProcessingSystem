<script setup lang="ts">
import TaskOverviewView from '@/views/TaskManagerView/TaskOverviewView.vue'
import TaskEditView from '@/views/TaskManagerView/TaskEditView.vue'
import { ResizableHandle, ResizablePanel, ResizablePanelGroup } from '@/components/ui/resizable'
</script>

<template>
  <ResizablePanelGroup direction="horizontal">
    <!-- Linkes Panel -->
    <ResizablePanel :default-size="50" class="min-w-70 max-w-100">
      <TaskOverviewView />
    </ResizablePanel>

    <!-- Divider -->
    <ResizableHandle class="bg-gray-400 w-1 cursor-col-resize" />

    <!-- Rechtes Panel -->
    <ResizablePanel class="min-w-140">
      <TaskEditView />
    </ResizablePanel>
  </ResizablePanelGroup>
</template>
