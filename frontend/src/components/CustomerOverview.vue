<script setup lang="ts">
import { onMounted, ref, computed } from "vue";
import { listCustomers, type CustomerResponse } from "../services/customerApi";

const customers = ref<CustomerResponse[]>([]);
const isLoading = ref(false);
const errorMessage = ref("");
const searchTerm = ref("");

const emit = defineEmits<{
  (e: "create-new"): void;
}>();

// Filtered customers based on the search term
const filteredCustomers = computed(() => {
  if (!searchTerm.value) {
    return customers.value;
  }
  const lowerSearchTerm = searchTerm.value.toLowerCase();
  return customers.value.filter(
    (customer) =>
      customer.name.toLowerCase().includes(lowerSearchTerm) ||
      customer.email.toLowerCase().includes(lowerSearchTerm) ||
      customer.phone.toLowerCase().includes(lowerSearchTerm) ||
      customer.city.toLowerCase().includes(lowerSearchTerm) ||
      customer.country.toLowerCase().includes(lowerSearchTerm) ||
      customer.street.toLowerCase().includes(lowerSearchTerm) ||
      customer.zip.toLowerCase().includes(lowerSearchTerm) ||
      (customer.vatId &&
        customer.vatId.toLowerCase().includes(lowerSearchTerm)) ||
      (customer.type && customer.type.toLowerCase().includes(lowerSearchTerm)),
  );
});

// Loads the list of customers from the API
async function loadCustomers(): Promise<void> {
  isLoading.value = true;
  errorMessage.value = "";

  try {
    customers.value = await listCustomers();
  } catch (e) {
    errorMessage.value =
      e instanceof Error ? e.message : "Error loading customers.";
  } finally {
    isLoading.value = false;
  }
}

// Emits an event to signal the creation of a new customer
function onCreateNewCustomer(): void {
  emit("create-new");
}

defineExpose({ loadCustomers });

onMounted(loadCustomers);
</script>

<template>
  <section class="customer-overview-card">
    <div class="header-controls">
      <h2>Customers</h2>
      <input
        type="text"
        placeholder="Search customers..."
        v-model="searchTerm"
        class="search-input"
      />
      <button
        class="new-customer-button"
        type="button"
        @click="onCreateNewCustomer"
      >
        Add New
      </button>
      <button class="refresh-button" type="button" @click="loadCustomers">
        Refresh
      </button>
    </div>

    <p v-if="isLoading">Loading customers...</p>
    <p v-else-if="errorMessage">Error: {{ errorMessage }}</p>

    <div v-else class="customer-table-container">
      <table class="customer-table" v-if="filteredCustomers.length > 0">
        <thead>
          <tr>
            <th>Name</th>
            <th>Type</th>
            <th>Email</th>
            <th>Phone</th>
            <th>Address</th>
            <th>Zip / City</th>
            <th>Country</th>
            <th>VAT ID</th>
            <!-- Additional columns as needed -->
          </tr>
        </thead>
        <tbody>
          <tr v-for="c in filteredCustomers" :key="c.id">
            <td>{{ c.name }}</td>
            <td>{{ c.type }}</td>
            <td>{{ c.email }}</td>
            <td>{{ c.phone }}</td>
            <td>{{ c.street }}</td>
            <td>{{ c.zip }} {{ c.city }}</td>
            <td>{{ c.country }}</td>
            <td>{{ c.vatId || "-" }}</td>
            <!-- Additional cells as needed -->
          </tr>
        </tbody>
      </table>
      <p v-else>No customers found.</p>
    </div>
  </section>
</template>

<style scoped>
.customer-overview-card {
  border: 1px solid rgba(0, 0, 0, 0.08);
  border-radius: 14px;
  padding: 14px;
  background: #fff;
}
.header-controls {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 20px;
}
.header-controls h2 {
  margin: 0;
}
.search-input {
  flex-grow: 1;
  padding: 10px 15px;
  border: 1px solid #ccc;
  border-radius: 8px;
  max-width: 300px;
}
.new-customer-button {
  background-color: #28a745;
  color: white;
  padding: 10px 18px;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-weight: bold;
  white-space: nowrap;
}
.new-customer-button:hover {
  background-color: #218838;
}
.refresh-button {
  border: 1px solid rgba(0, 0, 0, 0.12);
  background: #fff;
  border-radius: 12px;
  padding: 10px 12px;
  cursor: pointer;
}
.refresh-button:hover {
  background: rgba(0, 0, 0, 0.04);
}

.customer-table-container {
  overflow-x: auto;
}

.customer-table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 10px;
  min-width: 800px;
}

.customer-table th,
.customer-table td {
  border: 1px solid #eee;
  padding: 12px;
  text-align: left;
  vertical-align: top;
}

.customer-table th {
  background-color: #f8f8f8;
  font-weight: bold;
  color: #333;
}

.customer-table tbody tr:nth-child(even) {
  background-color: #fdfdfd;
}

.customer-table tbody tr:hover {
  background-color: #f5f5f5;
}
</style>
