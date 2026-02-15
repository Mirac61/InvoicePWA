<!-- src/components/HealthCard.vue -->
<script setup lang="ts">
import { onMounted, ref } from "vue";

interface HealthResponse {
  status: string;
  service: string;
  timestamp: string;
}

const health = ref<HealthResponse | null>(null);
const loading = ref(true);
const error = ref("");

async function load(): Promise<void> {
  loading.value = true;
  error.value = "";

  try {
    const res = await fetch("/api/health");
    if (!res.ok) throw new Error(`HTTP ${res.status}`);
    health.value = (await res.json()) as HealthResponse;
  } catch (e) {
    error.value = e instanceof Error ? e.message : "Unbekannter Fehler";
  } finally {
    loading.value = false;
  }
}

onMounted(load);
</script>

<template>
  <section class="systemstatus-karte">
    <div class="row">
      <h2>Backend Status</h2>
      <button class="neu-laden-button" type="button" @click="load">Neu laden</button>
    </div>

    <p v-if="loading">Lade...</p>
    <p v-else-if="error">Fehler: {{ error }}</p>
    <pre v-else>{{ health }}</pre>
  </section>
</template>

<style scoped>
.systemstatus-karte {
  border: 1px solid rgba(0, 0, 0, 0.08);
  border-radius: 14px;
  padding: 14px;
  background: #fff;
  margin-bottom: 14px;
}
.row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}
.neu-laden-button {
  border: 1px solid rgba(0, 0, 0, 0.12);
  background: #fff;
  border-radius: 12px;
  padding: 10px 12px;
  cursor: pointer;
}
.neu-laden-button:hover {
  background: rgba(0, 0, 0, 0.04);
}
pre {
  margin: 10px 0 0;
  padding: 12px;
  border-radius: 12px;
  background: #0b1220;
  color: #e5e7eb;
  border: 1px solid rgba(255, 255, 255, 0.08);
  overflow: auto;
  font-size: 13px;
  line-height: 1.35;
}
</style>
