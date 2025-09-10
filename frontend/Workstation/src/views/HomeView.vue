<script setup lang="ts">
import TheWelcome from '../components/TheWelcome.vue'
import {onMounted} from "vue";
import {useTaskStore} from "@/stores/taskStore.ts";

const taskStore = useTaskStore();

//testmount
onMounted(async () => {
  await taskStore.fetchTasks();
});
</script>

<template>
  <main>
    <TheWelcome />
  </main>
</template>
