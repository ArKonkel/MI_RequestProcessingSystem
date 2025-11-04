<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { Button } from '@/components/ui/button'
import {
  ComboboxAnchor,
  ComboboxContent,
  ComboboxInput,
  ComboboxItem,
  ComboboxPortal,
  ComboboxRoot,
  Label,
  type ReferenceElement,
  useFilter,
} from 'reka-ui'
import type { CommentDtd } from '@/documentTypes/dtds/CommentDtd.ts'
import { AccordionContent, AccordionItem, AccordionTrigger } from '@/components/ui/accordion'
import { getAllUser } from '@/services/userService.ts'
import type { UserDtd } from '@/documentTypes/dtds/UserDtd.ts'
import { getAnchorRect, getSearchValue, getTrigger, getTriggerOffset, replaceValue } from './utils'
import { computedWithControl } from '@vueuse/core'

const users = ref<UserDtd[]>([])

//const localCommentText = ref(props.modelValue)
const localCommentText = ref('')
const trigger = ref<string | null>(null)
const caretOffset = ref<number | null>(null)
const open = ref(false)
const searchValue = ref('')
const textareaRef = ref<InstanceType<typeof ComboboxInput>>()
const { contains } = useFilter({ sensitivity: 'base' })

onMounted(async () => {
  users.value = await getAllUser()
})

const props = defineProps<{
  modelValue: string //gibt v-model von außen ein
  comments: CommentDtd[]
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', value: string): void
  (e: 'submit'): void
}>()

watch(
  () => props.modelValue,
  (val) => {
    localCommentText.value = val
  },
)

function getUser(trigger: string | null) {
  switch (trigger) {
    case '@':
      return users.value.map((user) => '@' + user.id + ' ' + user.name)
    default:
      return []
  }
}

function submitComment() {
  emit('update:modelValue', localCommentText.value)
  emit('submit')
  localCommentText.value = '' // reset input
}

const reference = computedWithControl(
  () => [searchValue.value, open.value],
  () =>
    ({
      getBoundingClientRect: () => {
        if (textareaRef.value?.$el) {
          const { x, y, height } = getAnchorRect(textareaRef.value?.$el)
          return { x, y, height, top: y, left: x, width: 0 }
        } else {
          return null
        }
      },
    }) as ReferenceElement,
)

const userList = computed(() => {
  const _list = getUser(trigger.value)
  return _list.filter((item) => contains(item, searchValue.value))
})

function handleChange(ev: InputEvent) {
  const target = ev.target as HTMLTextAreaElement
  const _trigger = getTrigger(target)
  const _searchValue = getSearchValue(target)

  if (_trigger) {
    trigger.value = _trigger
    open.value = true
  } else if (!_searchValue) {
    trigger.value = null
    open.value = false
  }

  localCommentText.value = target.value
  searchValue.value = _searchValue

  if (!_trigger) open.value = false
}

function handleSelect(ev: CustomEvent) {
  const textarea = textareaRef.value?.$el
  if (!textarea) return

  const offset = getTriggerOffset(textarea) + 1

  const displayValue = ev.detail.value.replace('@', '')

  if (!displayValue) return

  ev.preventDefault()

  trigger.value = null
  localCommentText.value = replaceValue(
    localCommentText.value,
    offset,
    searchValue.value,
    displayValue,
  )
  caretOffset.value = offset - searchValue.value.length - 1 + displayValue.length + 2
}

function formatDate(ts: string | number | Date) {
  return new Date(ts).toLocaleString('de-DE')
}

function formatComment(commentText: string) {
  return commentText.replace(/@\d+\s*/g, '')
}
</script>

<template>
  <AccordionItem value="comments">
    <AccordionTrigger>Kommentare</AccordionTrigger>
    <AccordionContent>
      <div class="space-y-4">
        <!-- Combobox for user mentioning -->
        <ComboboxRoot
          v-model:open="open"
          ignore-filter
          :reset-search-term-on-blur="false"
          class="text-foreground flex flex-col"
        >
          <ComboboxInput
            id="comment"
            ref="textareaRef"
            v-model="localCommentText"
            as="textarea"
            class="m-1 border rounded-md p-2 resize-none"
            rows="5"
            placeholder="Verfasse dein Kommentar"
            @input="handleChange"
            @pointerdown="open = false"
            @keydown.enter="
              (ev: KeyboardEvent) => {
                if (open) ev.preventDefault()
              }
            "
            @keydown.left.right="open = false"
          />
          <ComboboxAnchor :reference="reference" />

          <ComboboxPortal>
            <ComboboxContent
              v-if="userList.length"
              position="popper"
              side="bottom"
              align="start"
              class="overflow-y-auto overflow-x-hidden max-h-48 max-w-80 bg-card border border-muted-foreground/30 p-1.5 rounded-md"
            >
              <ComboboxItem
                v-for="item in userList"
                :key="item"
                :value="item"
                class="px-2 py-1 data-[highlighted]:bg-muted rounded flex cursor-default"
                @select="handleSelect"
              >
                <span class="truncate">{{ item }}</span>
              </ComboboxItem>
            </ComboboxContent>
          </ComboboxPortal>
        </ComboboxRoot>

        <div class="flex justify-end">
          <Button class="cursor-pointer" @click="submitComment">Senden</Button>
        </div>
        <div v-for="comment in comments" :key="comment.id" class="border-t pt-2 text-sm">
          <div class="font-semibold">{{ comment.author.name }}</div>
          <div class="text-xs text-muted-foreground">
            {{ formatDate(comment.timeStamp) }}
          </div>
          <div class="mt-2">
            <p class="text-lg">{{ formatComment(comment.text) }}</p>
          </div>
        </div>
      </div>
    </AccordionContent>
  </AccordionItem>
</template>
