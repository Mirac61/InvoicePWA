<!-- src/components/HealthStatus.vue -->
<script setup lang="ts">
import { onMounted, ref } from "vue";

interface HealthResponse {
  status: string;
  service: string;
  timestamp: string;
}

const health = ref<HealthResponse | null>(null);
const isLoading = ref(true);
const errorMessage = ref("");

// Loads the backend health status from the API
async function loadHealthStatus(): Promise<void> {
  isLoading.value = true;
  errorMessage.value = "";

  try {
    const res = await fetch("/api/health");
    if (!res.ok) throw new Error(`HTTP ${res.status}`);
    health.value = (await res.json()) as HealthResponse;
  } catch (e) {
    errorMessage.value =
      e instanceof Error ? e.message : "An unknown error occurred.";
  } finally {
    isLoading.value = false;
  }
}

onMounted(loadHealthStatus);
</script>

<template>
  <section class="health-status-card">
    <div class="row">
      <h2>Backend Status</h2>
      <button class="reload-button" type="button" @click="loadHealthStatus">Reload</button>
    </div>

    <p v-if="isLoading">Loading...</p>
    <p v-else-if="errorMessage">Error: {{ errorMessage }}</p>
    <pre v-else>{{ health }}</pre>
  </section>
</template>

<style scoped>
.health-status-card {
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
.reload-button {
  border: 1px solid rgba(0, 0, 0, 0.12);
  background: #fff;
  border-radius: 12px;
  padding: 10px 12px;
  cursor: pointer;
}
.reload-button:hover {
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
