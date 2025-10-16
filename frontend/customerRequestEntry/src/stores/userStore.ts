import {defineStore} from 'pinia'
import {ref} from 'vue'
import type {UserDtd} from "@/documentTypes/dtds/UserDtd.ts";
import {getUserByName} from "@/services/userService.ts";
import {useAlertStore} from "@/stores/useAlertStore.ts";
import {Role} from "@/documentTypes/types/Role.ts";

export const useUserStore = defineStore('userStore', () => {

  const alertStore = useAlertStore()

  const user = ref<UserDtd | null>(null);

  //when initializing store, try to load user from local storage
  if (localStorage.getItem('user')) {
    user.value = JSON.parse(localStorage.getItem('user')!)
  }

  async function setUser(name: string) {
    try {
      console.log("Trying to load user with name: " + name)
      user.value = await getUserByName(name)

      localStorage.setItem('user', JSON.stringify(user.value)) //save user to local storage to have it even after reload
    } catch (error) {
      alertStore.show('Fehler beim Laden des Users', 'error')
    }
  }

  function removeUser() {
    user.value = null;
    localStorage.removeItem('user')
  }

  function setDefaultUser() {
    user.value = {
      id: 1,
      name: 'TestUser',
      roles: [{name: Role.ADMIN}],
      customer: {
        id: -1,
        address: '',
        email: 'test@email.de',
        lastName: 'TestLastname',
        firstName: 'TestFirstname',
      }
    };
  }

  return {
    user,
    setUser,
    removeUser,
    setDefaultUser
  }
})
