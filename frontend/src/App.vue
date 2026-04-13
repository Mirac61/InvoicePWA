<script setup lang="ts">
import WebPage from './WebPage.vue';
import Main from './components/Main.vue';
import {ref} from 'vue';

const started = ref(false);
</script>

<template>
  <WebPage v-if="!started" @get-started="started = true" />
  <Main v-else @back-to-home-page="started = false" />
</template>
<style scoped>
</style>
