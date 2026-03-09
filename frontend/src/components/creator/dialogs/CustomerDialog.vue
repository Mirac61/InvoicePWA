<template>
  <Teleport to="body">
    <div
      v-if="modelValue"
      class="modal-backdrop"
      @click.self="emit('update:modelValue', false)"
    >
      <div class="modal-box">
        <div class="modal-header">
          <h3 class="modal-title">Neuen Kunden anlegen</h3>
          <button
            class="modal-close"
            type="button"
            @click="emit('update:modelValue', false)"
          >
            ✕
          </button>
        </div>
        <div class="modal-body">
          <div class="field-row">
            <div class="field-group">
              <label>Typ</label>
              <select v-model="form.type">
                <option value="PRIVATE">Privatperson</option>
                <option value="BUSINESS">Unternehmen</option>
              </select>
            </div>
          </div>
          <div class="field-row">
            <div class="field-group">
              <label>Vorname *</label>
              <input v-model="form.forename" type="text" placeholder="Max" />
            </div>
            <div class="field-group">
              <label>Nachname *</label>
              <input
                v-model="form.surname"
                type="text"
                placeholder="Mustermann"
              />
            </div>
          </div>
          <div class="field-row">
            <div class="field-group">
              <label>Straße & Nr. *</label>
              <input
                v-model="form.street"
                type="text"
                placeholder="Musterstraße 1"
              />
            </div>
          </div>
          <div class="field-row">
            <div class="field-group">
              <label>PLZ *</label>
              <input v-model="form.zip" type="text" placeholder="12345" />
            </div>
            <div class="field-group">
              <label>Stadt *</label>
              <input v-model="form.city" type="text" placeholder="Berlin" />
            </div>
            <div class="field-group">
              <label>Land</label>
              <input
                v-model="form.country"
                type="text"
                placeholder="DE"
                maxlength="2"
              />
            </div>
          </div>
          <div class="field-row">
            <div class="field-group">
              <label>E-Mail *</label>
              <input
                v-model="form.email"
                type="email"
                placeholder="max@example.com"
              />
            </div>
            <div class="field-group">
              <label>Telefon *</label>
              <input
                v-model="form.phone"
                type="text"
                placeholder="+49 30 123456"
              />
            </div>
          </div>
          <div class="field-row">
            <div class="field-group">
              <label>USt-IdNr.</label>
              <input
                v-model="form.vatId"
                type="text"
                placeholder="DE123456789"
              />
            </div>
          </div>
          <p v-if="error" class="modal-error">{{ error }}</p>
        </div>
        <div class="modal-footer">
          <button
            class="btn-modal-cancel"
            type="button"
            @click="emit('update:modelValue', false)"
          >
            Abbrechen
          </button>
          <button
            class="btn-modal-save"
            type="button"
            @click="submit"
            :disabled="saving"
          >
            {{ saving ? "Wird gespeichert…" : "Kunden anlegen" }}
          </button>
        </div>
      </div>
    </div>
  </Teleport>
</template>

<script setup lang="ts">
import { reactive, ref, watch } from "vue";
import { CustomerType } from "../../../services/CustomerAPI";
import {
  createCustomer,
  customerSearch,
} from "../../../composables/customerSearch";

const props = defineProps<{ modelValue: boolean }>();
const emit = defineEmits<{
  (e: "update:modelValue", val: boolean): void;
}>();

const saving = ref(false);
const error = ref<string | null>(null);

const form = reactive({
  type: CustomerType.PRIVATE,
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

watch(
  () => props.modelValue,
  (open) => {
    if (!open) return;
    const parts = customerSearch.value.trim().split(/\s+/);
    form.forename = parts[0] ?? "";
    form.surname = parts.slice(1).join(" ");
    form.street = "";
    form.zip = "";
    form.city = "";
    form.country = "DE";
    form.email = "";
    form.phone = "";
    form.vatId = "";
    form.type = CustomerType.PRIVATE;
    error.value = null;
  },
);

async function submit() {
  error.value = null;
  const required: Array<[keyof typeof form, string]> = [
    ["forename", "Vorname"],
    ["surname", "Nachname"],
    ["street", "Straße"],
    ["zip", "PLZ"],
    ["city", "Stadt"],
    ["email", "E-Mail"],
    ["phone", "Telefon"],
  ];
  for (const [field, label] of required) {
    if (!form[field].trim()) {
      error.value = `${label} ist erforderlich.`;
      return;
    }
  }

  saving.value = true;
  try {
    await createCustomer({
      type: form.type,
      forename: form.forename.trim(),
      surname: form.surname.trim(),
      street: form.street.trim(),
      zip: form.zip.trim(),
      city: form.city.trim(),
      country: form.country.trim() || "DE",
      email: form.email.trim(),
      phone: form.phone.trim(),
      vatId: form.vatId.trim() || "",
    });
    emit("update:modelValue", false);
  } catch (e: any) {
    error.value = `Fehler: ${e.message}`;
  } finally {
    saving.value = false;
  }
}
</script>
