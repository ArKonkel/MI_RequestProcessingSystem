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
</template>
