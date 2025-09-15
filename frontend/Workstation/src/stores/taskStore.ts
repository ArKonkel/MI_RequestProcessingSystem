import {defineStore} from "pinia";
import type {TaskDtd} from "@/documentTypes/dtds/TaskDtd.ts";
import {reactive} from "vue";
import axios from "axios";

export const useTaskStore = defineStore('taskStore', () => {

  const taskData = reactive({
    tasks: [] as TaskDtd[],
    selectedTask: null as TaskDtd | null,
  })

  async function fetchTasks() {
    try {
      const response = await axios.get<TaskDtd[]>("/api/task");
      taskData.tasks = response.data;

      if (taskData.tasks.length > 0){
        taskData.selectedTask = taskData.tasks[0];
      }
    } catch (error) {
      console.error("Fehler beim Laden der Tasks:", error);
    }
  }

  async function setSelectedTask(task: TaskDtd) {
    taskData.selectedTask = task;
  }

  return {
    taskData,
    fetchTasks,
    setSelectedTask
  }
})

