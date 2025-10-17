<script setup lang="ts">
import type { FileDtd } from '@/documentTypes/dtds/FileDtd.ts'
import { Card, CardContent, CardFooter, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Tooltip, TooltipContent, TooltipProvider, TooltipTrigger } from '@/components/ui/tooltip'
import {downloadAttachment, uploadAttachment} from '@/services/processItemService.ts'
import { useAlertStore } from '@/stores/useAlertStore.ts'
import { useFileDialog } from '@vueuse/core'

const alertStore = useAlertStore()

const props = defineProps<{
  attachments: FileDtd[]
  processItemId: number
}>()

const { files, open, onChange } = useFileDialog({
  accept: '*/*',
})

onChange(async (fileList) => {
  if (fileList && fileList.length > 0) {
    const file = fileList[0]
    try {
      const uploadedFile = await uploadAttachment(props.processItemId, file)
      alertStore.show('Datei erfolgreich hochgeladen', 'success')
    } catch (error) {
      alertStore.show('Fehler beim hochladen der Datei', 'error')
    }
  }
})

function handleDownload(file: FileDtd) {
  downloadAttachment(file.id)
}
</script>

<template>
  <div class="flex space-x-2 space-y-2 items-center">
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
              <CardTitle class="text-sm font-medium truncate">{{ file.name }}</CardTitle>
            </CardHeader>
            <CardContent class="text-left">
              <p class="text-xs text-gray-600">{{ file.contentType }}</p>
              <p class="text-xs text-gray-600">{{ (file.size / 1024).toFixed(2) }} KB</p>
            </CardContent>
          </TooltipTrigger>
          <TooltipContent>
            <p>{{ file.name }}</p>
          </TooltipContent>
        </Tooltip>
      </TooltipProvider>
    </Card>

    <div>
      <Button class="cursor-pointer min-w-10 min-h-10" @click="open">+</Button>
    </div>
  </div>
</template>
