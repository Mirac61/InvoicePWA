<script setup lang="ts">
import { ref } from "vue";
import {
  createCustomer,
  type CreateCustomerRequest,
  CustomerType,
} from "../services/customerApi";

// Initialize form with default values for creating a new customer
const customerForm = ref<CreateCustomerRequest>({
  type: CustomerType.BUSINESS,
  forename: "",
  surname: "",
  street: "",
  zip: "",
  city: "",
  country: "DE",
  email: "",
  phone: "",
  vatId: "",
});

const errorMessage = ref("");
const successMessage = ref("");
const isSubmitting = ref(false);

const emit = defineEmits<{
  created: []; // Emits when a customer is successfully created
  "cancel-creation": []; // Emits when the creation process is cancelled
}>();

// Handles the submission of the customer creation form
async function handleSubmit(): Promise<void> {
  isSubmitting.value = true;
  errorMessage.value = "";
  successMessage.value = "";

  try {
    const payload = {
      ...customerForm.value,
      name: `${customerForm.value.forename} ${customerForm.value.surname}`.trim(),
    };

    await createCustomer(payload as unknown as CreateCustomerRequest);

    successMessage.value = "Customer created successfully.";
    emit("created");

    // Reset form after successful submission
    customerForm.value = {
      type: CustomerType.BUSINESS,
      forename: "",
      surname: "",
      street: "",
      zip: "",
      city: "",
      country: "DE",
      email: "",
      phone: "",
      vatId: "",
    };
  } catch (e) {
    errorMessage.value =
      e instanceof Error ? e.message : "Error during creation.";
  } finally {
    isSubmitting.value = false;
  }
}

// Handles cancellation of the customer creation form
function handleCancel(): void {
  emit("cancel-creation");
}
</script>

<template>
  <section class="creation-card">
    <h2>Create Customer</h2>

    <form class="creation-form" @submit.prevent="handleSubmit">
      <label class="form-field">
        Type
        <select v-model="customerForm.type">
          <option :value="CustomerType.BUSINESS">BUSINESS</option>
          <option :value="CustomerType.PRIVATE">PRIVATE</option>
        </select>
      </label>

      <label class="form-field">
        First Name
        <input v-model="customerForm.forename" required />
      </label>

      <label class="form-field">
        Last Name
        <input v-model="customerForm.surname" required />
      </label>

      <label class="form-field">
        Street
        <input v-model="customerForm.street" required />
      </label>

      <div class="grid2">
        <label class="form-field">
          Zip Code
          <input v-model="customerForm.zip" required />
        </label>

        <label class="form-field">
          City
          <input v-model="customerForm.city" required />
        </label>
      </div>

      <label class="form-field">
        Country
        <input v-model="customerForm.country" required />
      </label>

      <div class="grid2">
        <label class="form-field">
          Email
          <input v-model="customerForm.email" type="email" required />
        </label>

        <label class="form-field">
          Phone
          <input v-model="customerForm.phone" required />
        </label>
      </div>

      <label class="form-field">
        VAT ID
        <input v-model="customerForm.vatId" />
      </label>

      <button class="save-button" :disabled="isSubmitting" type="submit">
        {{ isSubmitting ? "Saving..." : "Save Customer" }}
      </button>
      <button class="cancel-button" type="button" @click="handleCancel">
        Cancel
      </button>
    </form>

    <p v-if="errorMessage" class="status-message status-message--error">
      Error: {{ errorMessage }}
    </p>

    <p v-if="successMessage" class="status-message status-message--success">
      {{ successMessage }}
    </p>
  </section>
</template>

<style scoped>
.creation-card {
  border: 1px solid rgba(0, 0, 0, 0.08);
  border-radius: 14px;
  padding: 14px;
  background: #fff;
}
.creation-form {
  display: grid;
  gap: 10px;
  margin-top: 10px;
}
.form-field {
  display: grid;
  gap: 6px;
  font-size: 14px;
}
input,
select {
  border: 1px solid rgba(0, 0, 0, 0.14);
  border-radius: 12px;
  padding: 10px 12px;
  font-size: 14px;
}
.grid2 {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
}
.save-button,
.cancel-button {
  border: 1px solid rgba(0, 0, 0, 0.12);
  background: #0b1220;
  color: #fff;
  border-radius: 12px;
  padding: 12px;
  font-weight: 700;
  cursor: pointer;
}
.cancel-button {
  background: #e0e0e0;
  color: #333;
}
.save-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
.status-message {
  margin-top: 10px;
}
.status-message--error {
  color: #b91c1c;
}
.status-message--success {
  color: #166534;
}
@media (max-width: 720px) {
  .grid2 {
    grid-template-columns: 1fr;
  }
}
</style>
