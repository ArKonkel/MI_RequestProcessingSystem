<script setup lang="ts">
import { ResizableHandle, ResizablePanel, ResizablePanelGroup } from '@/components/ui/resizable'
import RequestListView from '@/views/RequestEntryViews/RequestListView.vue'
import RequestDetailView from '@/views/RequestEntryViews/RequestDetailView.vue'
</script>

<template>
  <ResizablePanelGroup direction="horizontal">
    <!-- Linkes Panel -->
    <ResizablePanel :default-size="50" class="min-w-70 max-w-100">
      <RequestListView />
    </ResizablePanel>

    <!-- Divider -->
    <ResizableHandle class="bg-gray-300 w-0.5 cursor-col-resize" />

    <!-- Rechtes Panel -->
    <ResizablePanel class="min-w-140">
      <RequestDetailView />
    </ResizablePanel>
  </ResizablePanelGroup>
</template>
