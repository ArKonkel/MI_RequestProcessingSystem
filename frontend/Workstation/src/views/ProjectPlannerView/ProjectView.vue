<script setup lang="ts">
import ProjectListView from '@/views/ProjectPlannerView/ProjectListView.vue'
import ProjectDetailView from '@/views/ProjectPlannerView/ProjectDetailView.vue'
import { ResizableHandle, ResizablePanel, ResizablePanelGroup } from '@/components/ui/resizable'
</script>

<template>
  <ResizablePanelGroup direction="horizontal">
    <!-- Linkes Panel -->
    <ResizablePanel :default-size="50" class="min-w-70 max-w-100">
      <ProjectListView />
    </ResizablePanel>

    <!-- Divider -->
    <ResizableHandle class="bg-gray-400 w-1 cursor-col-resize" />

    <!-- Rechtes Panel -->
    <ResizablePanel class="min-w-140">
      <ProjectDetailView />
    </ResizablePanel>
  </ResizablePanelGroup>
</template>
