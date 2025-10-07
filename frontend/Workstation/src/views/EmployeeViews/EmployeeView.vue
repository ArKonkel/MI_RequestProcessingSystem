<script setup lang="ts">
import { ResizableHandle, ResizablePanel, ResizablePanelGroup } from '@/components/ui/resizable'
import EmployeeDetailView from "@/views/EmployeeViews/EmployeeDetailView.vue";
import EmployeeListView from "@/views/EmployeeViews/EmployeeListView.vue";
</script>

<template>
  <ResizablePanelGroup direction="horizontal">
    <!-- Linkes Panel -->
    <ResizablePanel :default-size="50" class="min-w-70 max-w-100">
      <EmployeeListView />
    </ResizablePanel>

    <!-- Divider -->
    <ResizableHandle class="bg-gray-400 w-1 cursor-col-resize" />

    <!-- Rechtes Panel -->
    <ResizablePanel class="min-w-140">
      <EmployeeDetailView />
    </ResizablePanel>
  </ResizablePanelGroup>
</template>
