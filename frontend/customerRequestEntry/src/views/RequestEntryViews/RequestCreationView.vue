<script setup lang="ts">
import {reactive, ref} from 'vue'

import {Input} from '@/components/ui/input'
import {Textarea} from '@/components/ui/textarea'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'
import {Button} from '@/components/ui/button'
import type {RequestCreateDtd} from '@/documentTypes/dtds/RequestCreateDtd.ts'
import {submitRequest} from '@/services/requestService.ts'
import {useAlertStore} from '@/stores/useAlertStore.ts'
import {PriorityLabel} from "@/documentTypes/types/Priority.ts";
import {CategoryLabel} from "@/documentTypes/types/Category.ts";
import {
  TagsInput,
  TagsInputInput,
  TagsInputItem,
  TagsInputItemText,
  TagsInputItemDelete
} from "@/components/ui/tags-input";
import {useRequestStore} from "@/stores/requestStore.ts";
import type {RequestDtd} from "@/documentTypes/dtds/RequestDtd.ts";
import {useRouter} from "vue-router";
import {useUserStore} from "@/stores/userStore.ts";
import {Card, CardContent, CardHeader, CardTitle} from "@/components/ui/card";
import {Tooltip, TooltipContent, TooltipProvider, TooltipTrigger} from "@/components/ui/tooltip";
import {useFileDialog} from "@vueuse/core";
import {Trash} from 'lucide-vue-next'

const maxUploadeMb = 2

const alertStore = useAlertStore()
const requestStore = useRequestStore()
const router = useRouter()
const userStore = useUserStore()

const requestForm = reactive<RequestCreateDtd>({
  processItem: {
    title: '',
    description: '',
  },
  priority: null,
  category: null,
  customerId: null,
  toRecipients: [],
})

const attachments = ref<File[]>([])

const recipientStrings = ref<string[]>([])

// Validation and errors
const errors = reactive({
  title: '' as string | null, //definition and initialization at once
  description: '' as string | null,
  priority: '' as string | null,
  category: '' as string | null,
})

function goToRequests(createdRequest: RequestDtd) {
  router.push('/requests')
  requestStore.setSelectedRequest(createdRequest)
}

const {files, open, onChange} = useFileDialog({
  accept: '*/*',
})

onChange(async (fileList) => {
  if (fileList && fileList.length > 0) {
    const file = fileList[0]
    try {
      if (file.size > maxUploadeMb * 1024 * 1024) {
        alertStore.show(`Maximal ${maxUploadeMb} MB pro Datei erlaubt`, 'error')
        return
      }

      attachments.value.push(file)
      alertStore.show('Datei erfolgreich hochgeladen', 'success')
    } catch (error) {
      alertStore.show('Fehler beim hochladen der Datei', 'error')
    }
  }
})

function validate(): boolean {
  errors.title = requestForm.processItem.title.trim() ? '' : 'Titel ist erforderlich'
  errors.description = requestForm.processItem.description.trim() ? '' : ''
  errors.priority = requestForm.priority ? '' : 'Priorität wählen'
  errors.category = requestForm.category ? '' : 'Kategorie wählen'

  return !errors.title && !errors.priority && !errors.category
}

//Buttons
async function submit() {
  if (userStore.user === null) {
    return
  }

  requestForm.customerId = userStore.user?.customer.id
  requestForm.toRecipients = recipientStrings.value.map(email => ({address: email}))

  //dont submit if validation fails
  if (!validate()) {
    return
  }

  try {
    const response = await submitRequest(requestForm, attachments.value)

    alertStore.show('Anfrage erfolgreich erstellt', 'success')
    resetFrom()
    goToRequests(response)

  } catch (error: any) {
    console.error(error)
    alertStore.show(error.response?.data || 'Unbekannter Fehler', 'error')
  }
}

function resetFrom() {
  requestForm.processItem.title = ''
  requestForm.processItem.description = ''
  requestForm.priority = null
  requestForm.category = null
  requestForm.toRecipients = []
  recipientStrings.value = []
  attachments.value = []
  Object.keys(errors).forEach((k) => (errors[k as keyof typeof errors] = '')) //Set all errors to empty string
}
</script>

<template>
  <form @submit.prevent="submit" class="flex flex-col max-w-3xl mx-auto p-6 gap-6">
    <div class="flex-1">
      <label class="block text-sm font-medium mb-1">Titel*</label>
      <Input v-model="requestForm.processItem.title" placeholder="Kurzer Titel"/>
      <p v-if="errors.title" class="text-red-600 text-xs mt-1">{{ errors.title }}</p>
    </div>

    <div class="flex-1">
      <label class="block text-sm font-medium mb-1">Empfänger</label>
      <TagsInput v-model="recipientStrings" placeholder="Emails...">
        <TagsInputItem v-for="item in recipientStrings" :key="item" :value="item">
          <TagsInputItemText/>
          <TagsInputItemDelete/>
        </TagsInputItem>
        <TagsInputInput placeholder="Emails..."/>
      </TagsInput>
    </div>

    <div class="flex flex-col md:flex-row gap-4">
      <div class="flex-1">
        <label class="block text-sm font-medium mb-1">Kategorie*</label>
        <Select v-model="requestForm.category" :value="requestForm.category">
          <SelectTrigger class="w-full">
            <SelectValue placeholder="Kategorie wählen"/>
          </SelectTrigger>
          <SelectContent>
            <SelectItem
              v-for="[value, categoryLabel] in Object.entries(CategoryLabel)"
              :key="value"
              :value="value"
            >
              {{ categoryLabel }}
            </SelectItem>
          </SelectContent>
        </Select>
        <p v-if="errors.category" class="text-red-600 text-xs mt-1">{{ errors.category }}</p>
      </div>

      <div class="flex-1">
        <label class="block text-sm font-medium mb-1">Priorität*</label>
        <Select v-model="requestForm.priority" :value="requestForm.priority">
          <SelectTrigger class="w-full">
            <SelectValue placeholder="Priorität wählen"/>
          </SelectTrigger>
          <SelectContent>
            <SelectItem
              v-for="[value, prioLabel] in Object.entries(PriorityLabel)"
              :key="value"
              :value="value"
            >
              {{ prioLabel }}
            </SelectItem>
          </SelectContent>
        </Select>
        <p v-if="errors.priority" class="text-red-600 text-xs mt-1">{{ errors.priority }}</p>
      </div>
    </div>


    <div class="flex flex-col">
      <label class="block text-sm font-medium mb-1">Anhänge</label>
      <div class="flex space-x-2 space-y-2 items-center">
        <Card
          v-for="(file, index) in attachments"
          :key="file.name"
          class="relative shadow-sm border w-40 p-0 overflow-hidden"
        >
          <TooltipProvider>
            <Tooltip>
              <TooltipTrigger class="flex flex-col pt-4 pb-4 cursor-default justify-between">
                <CardHeader>
                  <Trash
                    class="absolute top-2 right-2 w-4 h-4 text-gray-500 hover:text-red-500 cursor-pointer"
                    @click.stop="attachments.splice(index, 1)"
                  />
                  <CardTitle class="text-sm font-medium truncate">{{ file.name }}</CardTitle>
                </CardHeader>
                <CardContent class="text-left">
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
          <Button class="cursor-pointer min-w-10 min-h-10" type="button" @click="open">+</Button>
        </div>
      </div>
    </div>

    <!-- Beschreibung -->
    <div>
      <label class="block text-sm font-medium mb-1">Beschreibung</label>
      <Textarea
        v-model="requestForm.processItem.description"
        placeholder="Detaillierte Beschreibung"
        class="min-h-[200px] resize-none"
      />
      <p v-if="errors.description" class="text-red-600 text-xs mt-1">{{ errors.description }}</p>
    </div>

    <!-- Buttons-->
    <div class="flex items-end justify-between gap-4">
      <div></div>
      <div class="flex gap-2">
        <Button class="cursor-pointer" variant="ghost" type="button" @click="resetFrom">
          Zurücksetzen
        </Button>
        <Button class="cursor-pointer" type="submit">Anfrage erstellen</Button>
      </div>
    </div>
  </form>
</template>
