<script setup lang="ts">
import type {FileDtd} from "@/documentTypes/dtds/FileDtd.ts";
import {Card, CardContent, CardFooter, CardHeader, CardTitle} from '@/components/ui/card'
import {Button} from "@/components/ui/button";
import {downloadFile} from "@/services/fileService.ts";
import {
  Tooltip,
  TooltipContent,
  TooltipProvider,
  TooltipTrigger
} from '@/components/ui/tooltip'

const props = defineProps<{
  attachments: FileDtd[]
}>()

function handleDownload(file: FileDtd) {
  downloadFile(file.id)
}

function handleUpload()

</script>

<template>
  <div class="flex space-x-2 space-y-2 items-center" >
    <p v-if="!attachments || attachments.length === 0" class="text-sm text-gray-500">
      Keine Anhänge vorhanden.
    </p>

    <Card
      v-for="file in attachments"
      :key="file.url"
      class="cursor-pointer shadow-sm border min-w-40 max-w-40 max-h-25"
      @click="handleDownload(file)"
    >
      <TooltipProvider>
        <Tooltip>
          <TooltipTrigger class="cursor-pointer">
            <CardHeader>
              <CardTitle class="text-sm font-medium truncate ">{{ file.name }}</CardTitle>
            </CardHeader>
            <CardContent class="text-left">
              <p class="text-xs text-gray-600"> {{ file.contentType }}</p>
              <p class="text-xs text-gray-600"> {{ (file.size / 1024).toFixed(2) }} KB</p>
            </CardContent>
          </TooltipTrigger>
          <TooltipContent>
            <p>{{ file.name }}</p>
          </TooltipContent>
        </Tooltip>
      </TooltipProvider>
    </Card>

    <div>
      <Button class="cursor-pointer min-w-10 min-h-10">+</Button>
    </div>
  </div>
</template>
