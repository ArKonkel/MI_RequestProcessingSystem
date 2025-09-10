import {defineStore} from "pinia";
import type {TaskDtd} from "@/documentTypes/dtds/TaskDtd.ts";
import {reactive} from "vue";
import axios from "axios";

export const useTaskStore = defineStore('taskStore', () => {

  const taskData = reactive({
    tasks: [] as TaskDtd[],
  })

  async function fetchTasks() {
    try {
      const response = await axios.get<TaskDtd[]>("/api/task");
      taskData.tasks = response.data;
    } catch (error) {
      console.error("Fehler beim Laden der Tasks:", error);
    }
  }

  return {
    taskData,
    fetchTasks,
  }
})

