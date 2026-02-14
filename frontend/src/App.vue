<!-- src/App.vue -->
<script setup lang="ts">
import { ref } from "vue";
import Navbar from "./components/Navbar.vue";
import HealthCard from "./components/HealthCard.vue";
import CustomerCreateCard from "./components/CustomerCreateCard.vue";
import CustomerListCard from "./components/CustomerListCard.vue";

type NavKey = "home" | "invoices" | "customers" | "settings";
const active = ref<NavKey>("home");

function onNavigate(key: NavKey) {
  active.value = key;
}
</script>

<template>
  <Navbar :active="active" @navigate="onNavigate" />

  <main class="container">
    <h1>Invoice App</h1>

    <HealthCard />

    <section v-if="active === 'customers'" class="stack">
      <CustomerCreateCard />
      <CustomerListCard />
    </section>

    <section v-else class="card">
      <h2>Start</h2>
      <p>Wähle oben einen Bereich.</p>
    </section>
  </main>
</template>

<style scoped>
.container {
  max-width: 980px;
  margin: 0 auto;
  padding: 18px 16px;
}
.stack {
  display: grid;
  gap: 14px;
}
.card {
  border: 1px solid rgba(0, 0, 0, 0.08);
  border-radius: 14px;
  padding: 14px;
  background: #fff;
}
</style>
