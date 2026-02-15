<!-- src/components/CustomerCreateCard.vue -->
<script setup lang="ts">
import { ref } from "vue";
import {
  createCustomer,
  type CreateCustomerRequest,
  CustomerType,
} from "../services/customerApi";

const form = ref<CreateCustomerRequest>({
  type: CustomerType.BUSINESS,
  name: "",
  street: "",
  zip: "",
  city: "",
  country: "DE",
  email: "",
  phone: "",
  vatId: "",
});

const error = ref("");
const success = ref("");
const submitting = ref(false);

const emit = defineEmits<{
  created: [];
}>();

async function submit(): Promise<void> {
  submitting.value = true;
  error.value = "";
  success.value = "";

  try {
    await createCustomer(form.value);
    success.value = "Kunde wurde erstellt.";
    emit("created");
    form.value = {
      type: CustomerType.BUSINESS,
      name: "",
      street: "",
      zip: "",
      city: "",
      country: "DE",
      email: "",
      phone: "",
      vatId: "",
    };
  } catch (e) {
    error.value = e instanceof Error ? e.message : "Fehler beim Erstellen";
  } finally {
    submitting.value = false;
  }
}
</script>

<template>
  <section class="erstellungs-karte">
    <h2>Kunde erstellen</h2>

    <form class="erstellungs-formular" @submit.prevent="submit">
      <label class="formular-feld">
        Typ
        <select v-model="form.type">
          <option :value="CustomerType.BUSINESS">BUSINESS</option>
          <option :value="CustomerType.PRIVATE">PRIVATE</option>
        </select>
      </label>

      <label class="formular-feld">
        Name
        <input v-model="form.name" required />
      </label>

      <label class="formular-feld">
        Straße
        <input v-model="form.street" required />
      </label>

      <div class="grid2">
        <label class="formular-feld">
          PLZ
          <input v-model="form.zip" required />
        </label>

        <label class="formular-feld">
          Stadt
          <input v-model="form.city" required />
        </label>
      </div>

      <label class="formular-feld">
        Land
        <input v-model="form.country" required />
      </label>

      <div class="grid2">
        <label class="formular-feld">
          E Mail
          <input v-model="form.email" type="email" required />
        </label>

        <label class="formular-feld">
          Telefon
          <input v-model="form.phone" required />
        </label>
      </div>

      <label class="formular-feld">
        USt ID
        <input v-model="form.vatId" />
      </label>

      <button class="speicher-button" :disabled="submitting" type="submit">
        {{ submitting ? "Speichere..." : "Kunde speichern" }}
      </button>
    </form>

    <p v-if="error" class="status-nachricht status-nachricht--fehler">Fehler: {{ error }}</p>
    <p v-if="success" class="status-nachricht status-nachricht--erfolg">{{ success }}</p>
  </section>
</template>

<style scoped>
.erstellungs-karte {
  border: 1px solid rgba(0, 0, 0, 0.08);
  border-radius: 14px;
  padding: 14px;
  background: #fff;
}
.erstellungs-formular {
  display: grid;
  gap: 10px;
  margin-top: 10px;
}
.formular-feld {
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
.speicher-button {
  border: 1px solid rgba(0, 0, 0, 0.12);
  background: #0b1220;
  color: #fff;
  border-radius: 12px;
  padding: 12px;
  font-weight: 700;
  cursor: pointer;
}
.speicher-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
.status-nachricht {
  margin-top: 10px;
}
.status-nachricht--fehler {
  color: #b91c1c;
}
.status-nachricht--erfolg {
  color: #166534;
}
@media (max-width: 720px) {
  .grid2 {
    grid-template-columns: 1fr;
  }
}
</style>
