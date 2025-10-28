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
import {Category, CategoryLabel} from "@/documentTypes/types/Category.ts";
import {Checkbox} from '@/components/ui/checkbox'
import {
  TagsInput,
  TagsInputInput,
  TagsInputItem,
  TagsInputItemDelete,
  TagsInputItemText
} from "@/components/ui/tags-input";
import {useRequestStore} from "@/stores/requestStore.ts";
import type {RequestDtd} from "@/documentTypes/dtds/RequestDtd.ts";
import {useRouter} from "vue-router";
import {useUserStore} from "@/stores/userStore.ts";
import {Card, CardContent, CardHeader, CardTitle} from "@/components/ui/card";
import {Tooltip, TooltipContent, TooltipProvider, TooltipTrigger} from "@/components/ui/tooltip";
import {useFileDialog} from "@vueuse/core";
import {CircleQuestionMark, Trash} from 'lucide-vue-next'
import Modal from "@/components/Modal.vue";

const maxUploadeMb = 2

const alertStore = useAlertStore()
const requestStore = useRequestStore()
const router = useRouter()
const userStore = useUserStore()

const showSuccessModal = ref<boolean>(false)
const createdRequest = ref<RequestDtd>()

const requestForm = reactive<RequestCreateDtd>({
  contactFirstName: '',
  contactLastName: '',
  contactPhoneNumber: '',
  processItem: {
    title: '',
    description: '',
  },
  priority: null,
  category: null,
  programNumber: '',
  module: '',
  customerId: null,
  toRecipients: [],
})

const attachments = ref<File[]>([])
const recipientStrings = ref<string[]>([])

const addRecipients = ref<boolean>( false)

// Validation and errors
const errors = reactive({
  firstName: '' as string | null,
  lastName: '' as string | null,
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
  errors.firstName = requestForm.contactFirstName.trim() ? '' : 'Bitte geben Sie Ihren Vornamen ein.'
  errors.lastName = requestForm.contactLastName.trim() ? '' : 'Bitte geben Sie Ihren Nachnamen ein.'
  errors.title = requestForm.processItem.title.trim() ? '' : 'Bitte geben Sie einen Titel ein.'
  errors.description = requestForm.processItem.description.trim() ? '' : ''
  errors.priority = requestForm.priority ? '' : 'Bitte wählen Sie eine Priorität.'
  errors.category = requestForm.category ? '' : 'Bitte wählen Sie eine Kategorie.'

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
    createdRequest.value = await submitRequest(requestForm, attachments.value)
    showSuccessModal.value = true


  } catch (error: any) {
    console.error(error)
    alertStore.show(error.response?.data || 'Unbekannter Fehler', 'error')
  }
}

function closeModal(){
  resetFrom()
  goToRequests(createdRequest.value as RequestDtd)
  showSuccessModal.value = false
}

function resetFrom() {
  requestForm.contactFirstName = ''
  requestForm.contactLastName = ''
  requestForm.contactPhoneNumber = ''
  requestForm.programNumber = ''
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

    <h3 class="text-2xl font-semibold tracking-tight">
      Kontaktinformationen
    </h3>
    <div class="flex flex-col md:flex-row gap-4">
      <div class="w-full">
        <label class="block text-sm font-medium mb-1">Vorname*</label>
        <Input v-model="requestForm.contactFirstName" placeholder="Ihr Vorname"/>
        <p v-if="errors.firstName" class="text-red-600 text-xs mt-1">{{ errors.firstName }}</p>
      </div>
      <div class="w-full">
        <label class="block text-sm font-medium mb-1">Nachname*</label>
        <Input v-model="requestForm.contactLastName" placeholder="Ihr Nachname"/>
        <p v-if="errors.lastName" class="text-red-600 text-xs mt-1">{{ errors.lastName }}</p>
      </div>
    </div>

    <div class="flex-1">
      <div class="flex items-center space-x-2">
        <label class="block text-sm font-medium mb-1">TelefonNr.</label>

        <TooltipProvider>
          <Tooltip>
            <TooltipTrigger>
              <CircleQuestionMark class="w-3 h-3 mb-2 text-gray-500 hover:text-gray-700 cursor-pointer" />
            </TooltipTrigger>
            <TooltipContent>
              <p class="text-xs">Bitte gib deine Telefonnummer an, unter der wir dich für Rückfragen erreichen können.</p>
            </TooltipContent>
          </Tooltip>
        </TooltipProvider>
      </div>

      <div class="flex gap-4">
      <Input v-model="requestForm.contactPhoneNumber" placeholder="TelefonNr." />
      <Input disabled class="border-none"/>
      </div>
    </div>

    <h3 class="text-2xl font-semibold tracking-tight pt-5">
      Ihre Anfrage
    </h3>
    <div class="flex-1">
      <label class="block text-sm font-medium mb-1">Titel*</label>
      <Input v-model="requestForm.processItem.title" placeholder="Kurzer Titel"/>
      <p v-if="errors.title" class="text-red-600 text-xs mt-1">{{ errors.title }}</p>
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

    <div class="w-full" v-if="requestForm.category == Category.BUG_REPORT">


      <div class="flex space-x-2">
        <label class="block text-sm font-medium mb-1">INTEGRA® ProgrammNr.</label>
        <TooltipProvider>
          <Tooltip>
            <TooltipTrigger>
              <CircleQuestionMark class="w-3 h-3 mb-2 text-gray-500 hover:text-gray-700 cursor-pointer" />
            </TooltipTrigger>
            <TooltipContent>
              <p class="text-xs">Bitte gebe an in welchem Programm der Fehler auftaucht.</p>
            </TooltipContent>
          </Tooltip>
        </TooltipProvider>
      </div>

      <Input v-model="requestForm.programNumber" placeholder="ProgrammNr."/>
    </div>

    <div class="w-full" v-if="requestForm.category == Category.TRAINING_REQUEST">
      <div class="flex space-x-2">
        <label class="block text-sm font-medium mb-1">INTEGRA® Modul</label>
        <TooltipProvider>
          <Tooltip>
            <TooltipTrigger>
              <CircleQuestionMark class="w-3 h-3 mb-2 text-gray-500 hover:text-gray-700 cursor-pointer" />
            </TooltipTrigger>
            <TooltipContent>
              <p class="text-xs">Bitte gebe an in welchem Modul eine Schulung benötigt wird.</p>
            </TooltipContent>
          </Tooltip>
        </TooltipProvider>
      </div>

      <Input v-model="requestForm.module" placeholder="Modulbezeichnung"/>
    </div>

    <div class="flex space-x-2">
      <Checkbox id="addRecipients" v-model="addRecipients" />
      <label
        for="addRecipients"
        class="text-sm font-medium leading-none peer-disabled:cursor-not-allowed peer-disabled:opacity-70"
      >
        Weitere Empfänger per E-Mail benachrichtigen
      </label>
    </div>

    <div v-if="addRecipients" class="flex-1">

      <div class="flex space-x-2">
        <label class="block text-sm font-medium mb-1">Empfänger</label>
        <TooltipProvider>
          <Tooltip>
            <TooltipTrigger>
              <CircleQuestionMark class="w-3 h-3 mb-2 text-gray-500 hover:text-gray-700 cursor-pointer" />
            </TooltipTrigger>
            <TooltipContent>
              <p class="text-xs">Bitte gebe die Emails der Empfänger an, die ebenfalls über die Anfrage benachrichtigt werden sollen..</p>
            </TooltipContent>
          </Tooltip>
        </TooltipProvider>
      </div>


      <TagsInput v-model="recipientStrings" placeholder="Emails...">
        <TagsInputItem v-for="item in recipientStrings" :key="item" :value="item">
          <TagsInputItemText/>
          <TagsInputItemDelete/>
        </TagsInputItem>
        <TagsInputInput placeholder="Emails..."/>
      </TagsInput>
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
      <div class="flex space-x-2">
      <label class="block text-sm font-medium mb-1">Beschreibung</label>
      <TooltipProvider>
        <Tooltip>
          <TooltipTrigger>
            <CircleQuestionMark class="w-3 h-3 mb-2 text-gray-500 hover:text-gray-700 cursor-pointer" />
          </TooltipTrigger>
          <TooltipContent>
            <p class="text-xs">Bitte gebe eine möglichst genaue Beschreibung deiner Anfrage an. Jede Information hilft uns Ihre Anfrage schnell zu bearbeiten.</p>
          </TooltipContent>
        </Tooltip>
      </TooltipProvider>
      </div>

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

    <Modal
      :show="showSuccessModal"
      :title="`Vielen Dank für Ihre Anfrage ${requestForm.contactFirstName} ${requestForm.contactLastName}.`"
      :message="`Wir bearbeiten diese schnellstmöglich.
      Den aktuellen Stand der erstellten Anfrage ${createdRequest?.processItem.id} können Sie in Ihrer Anfrageübersicht einsehen.
      Bei Rückmeldungen oder Fragen nutzen Sie bitte die Kommentarfunktion.`"
      @close="closeModal"
    />
  </form>
</template>
