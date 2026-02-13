<script setup>
import { onMounted, ref } from 'vue'

const health = ref(null)
const loading = ref(true)
const error = ref('')

async function loadHealth() {
  loading.value = true
  error.value = ''

  try {
    const response = await fetch('/api/health')
    if (!response.ok) {
      throw new Error(`HTTP ${response.status}`)
    }
    health.value = await response.json()
  } catch (err) {
    error.value = err instanceof Error ? err.message : 'Unbekannter Fehler'
  } finally {
    loading.value = false
  }
}

onMounted(loadHealth)
</script>

<template>
  <main class="container">
    <h1>Invoice App</h1>
    <p>Vue.js Frontend ist aktiv.</p>

    <section class="card">
      <h2>Backend-Status</h2>

      <p v-if="loading">Lade...</p>
      <p v-else-if="error">Fehler: {{ error }}</p>
      <pre v-else>{{ health }}</pre>

      <button @click="loadHealth">Neu laden</button>
    </section>
  </main>
</template>
