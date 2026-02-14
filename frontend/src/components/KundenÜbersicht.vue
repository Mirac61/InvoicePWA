<!-- src/components/CustomerListCard.vue -->
<script setup lang="ts">
import { onMounted, ref } from "vue";
import { listCustomers, type CustomerResponse } from "../services/customerApi";

const customers = ref<CustomerResponse[]>([]);
const loading = ref(false);
const error = ref("");

async function load(): Promise<void> {
  loading.value = true;
  error.value = "";

  try {
    customers.value = await listCustomers();
  } catch (e) {
    error.value =
      e instanceof Error ? e.message : "Fehler beim Laden der Kunden";
  } finally {
    loading.value = false;
  }
}

defineExpose({ load });

onMounted(load);
</script>

<template>
  <section class="kunden-uebersicht-karte">
    <div class="row">
      <h2>Kunden</h2>
      <button class="aktualisieren-button" type="button" @click="load">Aktualisieren</button>
    </div>

    <p v-if="loading">Lade Kunden...</p>
    <p v-else-if="error">Fehler: {{ error }}</p>

    <ul v-else class="kunden-liste">
      <li v-for="c in customers" :key="c.id" class="kunde-eintrag">
        <div class="eintrag-name">{{ c.name }}</div>
        <div class="eintrag-meta">{{ c.email }}</div>
      </li>
    </ul>
  </section>
</template>

<style scoped>
.kunden-uebersicht-karte {
  border: 1px solid rgba(0, 0, 0, 0.08);
  border-radius: 14px;
  padding: 14px;
  background: #fff;
}
.row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}
.aktualisieren-button {
  border: 1px solid rgba(0, 0, 0, 0.12);
  background: #fff;
  border-radius: 12px;
  padding: 10px 12px;
  cursor: pointer;
}
.aktualisieren-button:hover {
  background: rgba(0, 0, 0, 0.04);
}
.kunden-liste {
  list-style: none;
  padding: 0;
  margin: 12px 0 0;
  display: grid;
  gap: 10px;
}
.kunde-eintrag {
  border: 1px solid rgba(0, 0, 0, 0.08);
  border-radius: 12px;
  padding: 12px;
}
.eintrag-name {
  font-weight: 700;
}
.eintrag-meta {
  opacity: 0.8;
  margin-top: 4px;
}
</style>
