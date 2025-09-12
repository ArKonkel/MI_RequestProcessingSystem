<script setup lang="ts">
import {onMounted} from "vue";
import {useTaskStore} from "@/stores/taskStore.ts";
import {Button} from "@/components/ui/button";

const taskStore = useTaskStore();

//testmount
onMounted(async () => {
  await taskStore.fetchTasks();
});
</script>

<template>
  <main>
    <Button class="text-1xl">Click me</Button>
    <Button variant="destructive" class="text-1xl">Click me</Button>
    <Button variant="ghost" class="text-1xl">Click me</Button>
  </main>
</template>
