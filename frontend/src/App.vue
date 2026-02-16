<!-- src/App.vue -->
<script setup lang="ts">
import { ref } from "vue";
import Navbar from "./components/Navbar.vue";
import HealthStatus from "./components/HealthStatus.vue";
import CustomerCreation from "./components/CustomerCreation.vue";
import CustomerOverview from "./components/CustomerOverview.vue";
import Positionen from "./components/Positionen.vue";

// Define navigation tab keys
type NavKey = "start" | "invoices" | "customers" | "positions" | "settings";
const activeTab = ref<NavKey>("start");

// Controls whether to display the customer list or creation form
// 'list' shows the overview, 'create' shows the creation form
const customerViewMode = ref<"list" | "create">("list");

// Handles navigation to a different tab
function handleNavigation(key: NavKey) {
  activeTab.value = key;
  // If navigating to "customers", always display the list first
  if (key === "customers") {
    customerViewMode.value = "list";
  }
}

// Switches to customer creation mode
function showCustomerCreationForm() {
  customerViewMode.value = "create";
}

// Switches back to the customer list mode (e.g., after save or cancel)
function showCustomerListOverview() {
  customerViewMode.value = "list";
}
</script>

<template>
  <Navbar :active="activeTab" @navigate="handleNavigation" />

  <main class="container">
    <section v-if="activeTab === 'start'">
      <HealthStatus />
    </section>

    <section v-else-if="activeTab === 'customers'" class="stack">
      <!-- Conditional display based on customerViewMode -->
      <CustomerOverview
        v-if="customerViewMode === 'list'"
        @create-new="showCustomerCreationForm"
      />
      <CustomerCreation
        v-else-if="customerViewMode === 'create'"
        @customer-created="showCustomerListOverview"
        @cancel-creation="showCustomerListOverview"
      />
    </section>

    <section v-else-if="activeTab === 'positions'" class="stack">
      <Positionen />
    </section>

    <section v-else class="card">
      <h2>{{ activeTab.charAt(0).toUpperCase() + activeTab.slice(1) }}</h2>
      <p>Please select a section above.</p>
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
