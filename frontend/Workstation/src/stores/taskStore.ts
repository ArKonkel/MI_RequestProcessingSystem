import {defineStore} from "pinia";
import type {TaskDtd} from "@/documentTypes/dtds/TaskDtd.ts";
import {reactive} from "vue";
import axios from "axios";
import {getAllTasks} from "@/services/taskService.ts";

export const useTaskStore = defineStore('taskStore', () => {

  const taskData = reactive({
    tasks: [] as TaskDtd[],
    selectedTask: null as TaskDtd | null,
  })

  async function fetchTasks() {
    try {
      taskData.tasks = await getAllTasks();

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

