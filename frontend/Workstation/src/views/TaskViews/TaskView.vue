<script setup lang="ts">
import TaskListView from '@/views/TaskViews/TaskListView.vue'
import TaskDetailView from '@/views/TaskViews/TaskDetailView.vue'
import { ResizableHandle, ResizablePanel, ResizablePanelGroup } from '@/components/ui/resizable'
</script>

<template>
  <ResizablePanelGroup direction="horizontal">
    <!-- Linkes Panel -->
    <ResizablePanel :default-size="50" class="min-w-70 max-w-100">
      <TaskListView />
    </ResizablePanel>

    <!-- Divider -->
    <ResizableHandle class="bg-gray-400 w-1 cursor-col-resize" />

    <!-- Rechtes Panel -->
    <ResizablePanel class="min-w-140">
      <TaskDetailView />
    </ResizablePanel>
  </ResizablePanelGroup>
</template>
