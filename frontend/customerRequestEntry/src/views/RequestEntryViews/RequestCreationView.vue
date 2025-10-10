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

const alertStore = useAlertStore()
const requestStore = useRequestStore()
const router = useRouter()

const requestForm = reactive<RequestCreateDtd>({
  processItem: {
    title: '',
    description: '',
  },
  priority: null,
  category: null,
  customerId: null,
  toRecipients: []
})

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

function validate(): boolean {
  errors.title = requestForm.processItem.title.trim() ? '' : 'Titel ist erforderlich'
  errors.description = requestForm.processItem.description.trim() ? '' : ''
  errors.priority = requestForm.priority ? '' : 'Priorität wählen'
  errors.category = requestForm.category ? '' : 'Kategorie wählen'

  return !errors.title && !errors.priority && !errors.category
}

//Buttons
async function submit() {
  //TODO change to customerId from login
  requestForm.customerId = 1
  requestForm.toRecipients = recipientStrings.value.map(email => ({address: email}))

  //dont submit if validation fails
  if (!validate()) {
    return
  }

  try {
    console.log(requestForm)

    const response = await submitRequest(requestForm)
    console.log('Request created')


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
  Object.keys(errors).forEach((k) => (errors[k as keyof typeof errors] = '')) //Set all errors to empty string
}
</script>

<template>
  <form @submit.prevent="submit" class="flex flex-col max-w-3xl mx-auto p-6 gap-6">
    <div class="flex-1">
      <label class="block text-sm font-medium mb-1">Titel</label>
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
        <label class="block text-sm font-medium mb-1">Priorität</label>
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

      <div class="flex-1">
        <label class="block text-sm font-medium mb-1">Kategorie</label>
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
        <Button variant="ghost" type="button" @click="resetFrom">Reset</Button>
        <Button type="submit">Anfrage erstellen</Button>
      </div>
    </div>
  </form>
</template>
