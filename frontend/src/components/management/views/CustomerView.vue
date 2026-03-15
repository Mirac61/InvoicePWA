<template>
  <Teleport to="body">
    <div
      v-if="open"
      class="fixed inset-0 bg-black/35 z-1000 flex items-center justify-center p-4"
      @click.self="emit('close')"
    >
      <div
        class="bg-white rounded-2xl shadow-2xl w-full max-w-lg max-h-[90vh] overflow-y-auto flex flex-col"
      >
        <div
          class="flex items-center justify-between px-6 py-5 border-b border-gray-100 shrink-0"
        >
          <span class="text-lg font-bold text-gray-900 tracking-tight">{{
            customer?.id ? "Kunde bearbeiten" : "Neuer Kunde"
          }}</span>
          <button
            class="text-gray-400 hover:text-gray-900 hover:bg-gray-100 px-2 py-1 rounded-lg transition-colors"
            @click="emit('close')"
          >
            ✕
          </button>
        </div>
        <div class="px-6 py-5 flex-1 flex flex-col gap-3">
          <div class="field">
            <label>Typ</label>
            <select v-model="form.type">
              <option value="PRIVATE">Privatperson</option>
              <option value="BUSINESS">Unternehmen</option>
            </select>
          </div>
          <div class="grid grid-cols-2 gap-3">
            <div class="field">
              <label>Vorname *</label
              ><input v-model="form.forename" placeholder="Max" />
            </div>
            <div class="field">
              <label>Nachname *</label
              ><input v-model="form.surname" placeholder="Mustermann" />
            </div>
          </div>
          <div class="field">
            <label>Straße & Hausnummer *</label
            ><input v-model="form.street" placeholder="Musterstraße 1" />
          </div>
          <div class="grid grid-cols-3 gap-3">
            <div class="field">
              <label>PLZ *</label
              ><input v-model="form.zip" placeholder="12345" />
            </div>
            <div class="field">
              <label>Stadt *</label
              ><input v-model="form.city" placeholder="Berlin" />
            </div>
            <div class="field">
              <label>Land</label
              ><input v-model="form.country" maxlength="2" placeholder="DE" />
            </div>
          </div>
          <div class="grid grid-cols-2 gap-3">
            <div class="field">
              <label>E-Mail *</label
              ><input
                v-model="form.email"
                type="email"
                placeholder="max@beispiel.de"
              />
            </div>
            <div class="field">
              <label>Telefon *</label
              ><input v-model="form.phone" placeholder="+49 30 123456" />
            </div>
          </div>
          <div class="field">
            <label>USt-IdNr.</label
            ><input v-model="form.vatId" placeholder="DE123456789" />
          </div>
          <p
            v-if="error"
            class="px-3.5 py-2.5 bg-red-50 border border-red-200 rounded-lg text-sm text-red-600"
          >
            {{ error }}
          </p>
        </div>
        <div
          class="px-6 py-4 border-t border-gray-100 flex gap-2.5 justify-end shrink-0"
        >
          <button
            class="px-5 py-2.5 border border-gray-200 rounded-lg text-sm text-gray-600 hover:bg-gray-50 transition-colors"
            @click="emit('close')"
          >
            Abbrechen
          </button>
          <button
            class="px-6 py-2.5 bg-blue-600 hover:bg-blue-700 disabled:opacity-50 text-white rounded-lg text-sm font-semibold transition-colors"
            @click="submit"
            :disabled="saving"
          >
            {{ saving ? "Speichern…" : customer?.id ? "Speichern" : "Anlegen" }}
          </button>
        </div>
      </div>
    </div>
  </Teleport>
</template>

<script setup lang="ts">
import { reactive, ref, watch } from "vue";
import type { Customer } from "../blocks/CustomerTab.vue";

const props = defineProps<{ open: boolean; customer: Customer | null }>();
const emit = defineEmits<{
  (e: "close"): void;
  (e: "saved", c: Customer): void;
}>();

const API = import.meta.env.VITE_API_BASE ?? "http://localhost:8080/api";
const saving = ref(false);
const error = ref("");
const form = reactive({
  type: "PRIVATE",
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
  () => props.customer,
  (c) => {
    error.value = "";
    Object.assign(
      form,
      c
        ? {
            type: c.type,
            forename: c.forename,
            surname: c.surname,
            street: c.street,
            zip: c.zip,
            city: c.city,
            country: c.country,
            email: c.email,
            phone: c.phone,
            vatId: c.vatId || "",
          }
        : {
            type: "PRIVATE",
            forename: "",
            surname: "",
            street: "",
            zip: "",
            city: "",
            country: "DE",
            email: "",
            phone: "",
            vatId: "",
          },
    );
  },
);

async function submit() {
  error.value = "";
  const required = [
    "forename",
    "surname",
    "street",
    "zip",
    "city",
    "email",
    "phone",
  ] as const;
  for (const f of required) {
    if (!form[f].trim()) {
      error.value = `Feld "${f}" ist erforderlich.`;
      return;
    }
  }
  saving.value = true;
  try {
    const url = props.customer?.id
      ? `${API}/customers/${props.customer.id}`
      : `${API}/customers`;
    const res = await fetch(url, {
      method: props.customer?.id ? "PUT" : "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ ...form, vatId: form.vatId || null }),
    });
    if (!res.ok) {
      const e = await res.json().catch(() => ({}));
      throw new Error(e.message ?? `HTTP ${res.status}`);
    }
    emit("saved", await res.json());
    emit("close");
  } catch (e: any) {
    error.value = `Fehler: ${e.message}`;
  } finally {
    saving.value = false;
  }
}
</script>

<style scoped>
.field {
  display: flex;
  flex-direction: column;
  gap: 4px;
}
.field label {
  font-size: 11px;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.04em;
  color: #9ca3af;
}
.field input,
.field select {
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  padding: 10px 12px;
  font-size: 14px;
  color: #111827;
  background: white;
  width: 100%;
  outline: none;
  transition: border-color 0.15s;
  box-sizing: border-box;
}
.field input:focus,
.field select:focus {
  border-color: #111827;
}
</style>
