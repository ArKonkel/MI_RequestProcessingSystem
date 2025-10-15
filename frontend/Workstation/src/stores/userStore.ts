import {defineStore} from 'pinia'
import {reactive} from 'vue'
import type {UserDtd} from "@/documentTypes/dtds/UserDtd.ts";
import {getUserByName} from "@/services/userService.ts";
import {useAlertStore} from "@/stores/useAlertStore.ts";
import {Role} from "@/documentTypes/types/Role.ts";

export const useUserStore = defineStore('userStore', () => {

  const alertStore = useAlertStore()

  const userData = reactive({
    user: null as UserDtd | null,
  })

  async function setUser(name: string) {
    try {
      userData.user = await getUserByName(name)
    } catch (error) {
      alertStore.show('Fehler beim hochladen der Datei', 'error')
    }
  }

  function removeUser() {
    userData.user = null;
  }

  function setDefaultUser() {
    userData.user = {
      id: 1,
      name: 'TestUser',
      roles: [{name: Role.ADMIN}],
    };
  }

  return {
    userData,
    setUser,
    removeUser,
    setDefaultUser
  }
})
