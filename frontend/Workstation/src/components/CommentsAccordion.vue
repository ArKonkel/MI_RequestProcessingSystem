<script setup lang="ts">
import { ref, watch } from 'vue'
import { Textarea } from '@/components/ui/textarea'
import { Button } from '@/components/ui/button'
import type { CommentDtd } from '@/documentTypes/dtds/CommentDtd.ts'
import { AccordionContent, AccordionItem, AccordionTrigger } from '@/components/ui/accordion'

const props = defineProps<{
  modelValue: string //gibt v-model von außen ein
  comments: CommentDtd[]
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', value: string): void
  (e: 'submit'): void
}>()

const localCommentText = ref(props.modelValue)

watch(
  () => props.modelValue,
  (val) => {
    localCommentText.value = val
  },
)

function submitComment() {
  emit('update:modelValue', localCommentText.value)
  emit('submit')
  localCommentText.value = '' // reset input
}

function formatDate(ts: string | number | Date) {
  return new Date(ts).toLocaleString('de-DE')
}
</script>

<template>
  <AccordionItem value="comments">
    <AccordionTrigger>Kommentare</AccordionTrigger>
    <AccordionContent>
      <div class="space-y-4">
        <Textarea
          v-model="localCommentText"
          placeholder="Verfasse dein Kommentar"
          class="resize-none"
          @keydown.enter.prevent="submitComment"
        />
        <div class="flex justify-end">
          <Button class="cursor-pointer" @click="submitComment">Senden</Button>
        </div>
        <div v-for="comment in comments" :key="comment.id" class="border-t pt-2 text-sm">
          <div class="font-semibold">{{ comment.author.name }}</div>
          <div class="text-xs text-muted-foreground">
            {{ formatDate(comment.timeStamp) }}
          </div>
          <div class="mt-2">
            <p class="text-lg">{{ comment.text }}</p>
          </div>
        </div>
      </div>
    </AccordionContent>
  </AccordionItem>
</template>
