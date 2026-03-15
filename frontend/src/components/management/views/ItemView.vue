<template>
  <Teleport to="body">
    <div
      v-if="open"
      class="fixed inset-0 bg-black/35 z-[1000] flex items-center justify-center p-4"
      @click.self="emit('close')"
    >
      <div
        class="bg-white rounded-2xl shadow-2xl w-full max-w-lg max-h-[90vh] overflow-y-auto flex flex-col"
      >
        <div
          class="flex items-center justify-between px-6 py-5 border-b border-gray-100 shrink-0"
        >
          <span class="text-lg font-bold text-gray-900 tracking-tight">{{
            item?.id ? "Artikel bearbeiten" : "Neuer Artikel"
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
            <label>Name *</label
            ><input v-model="form.name" placeholder="z.B. Beratungsstunde" />
          </div>
          <div class="field">
            <label>Beschreibung</label
            ><input
              v-model="form.description"
              placeholder="Kurze Beschreibung (optional)"
            />
          </div>
          <div class="grid grid-cols-3 gap-3">
            <div class="field">
              <label>Netto-Preis *</label
              ><input
                v-model.number="form.unitPriceNet"
                type="number"
                min="0"
                step="0.01"
              />
            </div>
            <div class="field">
              <label>MwSt. (%)</label
              ><input
                v-model.number="form.taxPct"
                type="number"
                min="0"
                max="100"
              />
            </div>
            <div class="field">
              <label>Einheit</label
              ><input v-model="form.defaultUnit" placeholder="h / Stk / pcs" />
            </div>
          </div>
          <div class="field">
            <label>Status</label>
            <select v-model="form.active">
              <option :value="true">Aktiv</option>
              <option :value="false">Inaktiv</option>
            </select>
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
            {{ saving ? "Speichern…" : item?.id ? "Speichern" : "Anlegen" }}
          </button>
        </div>
      </div>
    </div>
  </Teleport>
</template>

<script setup lang="ts">
import { reactive, ref, watch } from "vue";
import type { Item } from "../blocks/ItemTab.vue";

const props = defineProps<{ open: boolean; item: Item | null }>();
const emit = defineEmits<{
  (e: "close"): void;
  (e: "saved", item: Item): void;
}>();

const API = import.meta.env.VITE_API_BASE ?? "http://localhost:8080/api";
const saving = ref(false);
const error = ref("");
const form = reactive({
  name: "",
  description: "",
  defaultUnit: "",
  unitPriceNet: 0,
  taxPct: 19,
  active: true,
});

watch(
  () => props.item,
  (it) => {
    error.value = "";
    Object.assign(
      form,
      it
        ? {
            name: it.name,
            description: it.description || "",
            defaultUnit: it.defaultUnit || "",
            unitPriceNet: it.unitPriceNet,
            taxPct: Math.round(it.defaultTaxRate * 100),
            active: it.active,
          }
        : {
            name: "",
            description: "",
            defaultUnit: "",
            unitPriceNet: 0,
            taxPct: 19,
            active: true,
          },
    );
  },
);

async function submit() {
  error.value = "";
  if (!form.name.trim()) {
    error.value = "Name ist erforderlich.";
    return;
  }
  saving.value = true;
  const body = {
    name: form.name.trim(),
    description: form.description.trim() || null,
    defaultUnit: form.defaultUnit.trim() || null,
    unitPriceNet: form.unitPriceNet,
    defaultTaxRate: form.taxPct / 100,
    active: form.active,
  };
  try {
    const url = props.item?.id
      ? `${API}/items/${props.item.id}`
      : `${API}/items`;
    const res = await fetch(url, {
      method: props.item?.id ? "PUT" : "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(body),
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
