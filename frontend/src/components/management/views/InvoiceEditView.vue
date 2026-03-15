<template>
  <Teleport to="body">
    <div
      v-if="open"
      class="fixed inset-0 bg-black/35 z-1000 flex items-center justify-center p-4"
      @click.self="emit('close')"
    >
      <div
        class="bg-white rounded-2xl shadow-2xl w-full max-w-3xl max-h-[90vh] overflow-y-auto flex flex-col"
      >
        <div
          class="flex items-center justify-between px-6 py-5 border-b border-gray-100 shrink-0"
        >
          <span class="text-lg font-bold text-gray-900 tracking-tight"
            >Rechnung bearbeiten</span
          >
          <button
            class="text-gray-400 hover:text-gray-900 hover:bg-gray-100 px-2 py-1 rounded-lg transition-colors"
            @click="emit('close')"
          >
            ✕
          </button>
        </div>

        <div class="px-6 py-5 flex-1">
          <div class="grid grid-cols-3 gap-3">
            <div class="field">
              <label>Status</label>
              <select v-model="form.status">
                <option value="DRAFT">Entwurf</option>
                <option value="SENT">Versendet</option>
                <option value="PAID">Bezahlt</option>
                <option value="CANCELLED">Storniert</option>
              </select>
            </div>
            <div class="field">
              <label>Rechnungsdatum</label
              ><input type="date" v-model="form.issueDate" />
            </div>
            <div class="field">
              <label>Leistungsdatum</label
              ><input type="date" v-model="form.serviceDate" />
            </div>
          </div>
          <div class="field mt-3">
            <label>Notizen</label
            ><textarea v-model="form.notes" rows="3"></textarea>
          </div>

          <div class="mt-5">
            <p
              class="text-xs font-bold uppercase tracking-wide text-gray-400 mb-3"
            >
              Positionen
            </p>
            <div
              v-for="(it, i) in form.items"
              :key="i"
              class="flex items-start gap-3 py-3 border-b border-gray-50 last:border-0"
            >
              <span
                class="text-xs text-gray-300 min-w-4.5 text-center pt-6 shrink-0"
                >{{ i + 1 }}</span
              >
              <div class="flex-1 flex flex-col gap-2">
                <div class="grid grid-cols-5 gap-2">
                  <div class="field col-span-3">
                    <label>Bezeichnung *</label
                    ><input
                      v-model="it.title"
                      placeholder="z.B. Beratungsstunde"
                    />
                  </div>
                  <div class="field">
                    <label>Menge</label
                    ><input
                      v-model.number="it.quantity"
                      type="number"
                      min="0.001"
                      step="0.001"
                    />
                  </div>
                  <div class="field">
                    <label>Einheit</label
                    ><input v-model="it.unit" placeholder="h / Stk" />
                  </div>
                </div>
                <div class="grid grid-cols-3 gap-2">
                  <div class="field col-span-2">
                    <label>Netto/Einheit (€)</label
                    ><input
                      v-model.number="it.unitPriceNet"
                      type="number"
                      min="0"
                      step="0.01"
                    />
                  </div>
                  <div class="field">
                    <label>MwSt. (%)</label
                    ><input
                      v-model.number="it.taxRatePct"
                      type="number"
                      min="0"
                      max="100"
                    />
                  </div>
                </div>
              </div>
              <button
                class="mt-6 p-2 text-gray-300 hover:text-red-500 hover:bg-red-50 rounded-lg transition-colors"
                @click="form.items.splice(i, 1)"
              >
                <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
                  <path
                    d="M2 4.5h12M5.5 4.5V3h5v1.5M6 7.5v5.5M10 7.5v5.5"
                    stroke="currentColor"
                    stroke-width="1.5"
                    stroke-linecap="round"
                  />
                  <path
                    d="M3 4.5l.5 10h9l.5-10"
                    stroke="currentColor"
                    stroke-width="1.5"
                    stroke-linejoin="round"
                  />
                </svg>
              </button>
            </div>
            <button
              class="mt-3 w-full py-2.5 border border-dashed border-gray-200 rounded-lg text-sm text-gray-400 hover:border-gray-900 hover:text-gray-900 transition-colors"
              @click="
                form.items.push({
                  title: '',
                  quantity: 1,
                  unit: 'pcs',
                  unitPriceNet: 0,
                  taxRatePct: 19,
                })
              "
            >
              + Position hinzufügen
            </button>
          </div>
          <p
            v-if="error"
            class="mt-3 px-3.5 py-2.5 bg-red-50 border border-red-200 rounded-lg text-sm text-red-600"
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
            {{ saving ? "Speichern…" : "Speichern" }}
          </button>
        </div>
      </div>
    </div>
  </Teleport>
</template>

<script setup lang="ts">
import { reactive, ref, watch } from "vue";
import type { Invoice } from "../blocks/InvoiceTab.vue";

const props = defineProps<{ open: boolean; invoice: Invoice | null }>();
const emit = defineEmits<{
  (e: "close"): void;
  (e: "saved", inv: Invoice): void;
}>();

const API = import.meta.env.VITE_API_BASE ?? "http://localhost:8080/api";
const saving = ref(false);
const error = ref("");
const form = reactive<{
  status: string;
  issueDate: string;
  serviceDate: string;
  notes: string;
  items: {
    title: string;
    quantity: number;
    unit: string;
    unitPriceNet: number;
    taxRatePct: number;
    itemId?: string | null;
  }[];
}>({ status: "DRAFT", issueDate: "", serviceDate: "", notes: "", items: [] });

watch(
  () => props.invoice,
  (inv) => {
    if (!inv) return;
    form.status = inv.status || "DRAFT";
    form.issueDate = inv.issueDate || "";
    form.serviceDate = inv.serviceDate || "";
    form.notes = inv.notes || "";
    form.items = (inv.items || []).map((it) => ({
      title: it.title,
      quantity: it.quantity,
      unit: it.unit,
      unitPriceNet: it.unitPriceNet,
      taxRatePct: Math.round(it.taxRate * 100),
      itemId: it.itemId,
    }));
    error.value = "";
  },
);

async function submit() {
  if (!props.invoice) return;
  error.value = "";
  if (!form.items.length) {
    error.value = "Mindestens eine Position ist erforderlich.";
    return;
  }
  for (const it of form.items) {
    if (!it.title.trim()) {
      error.value = "Alle Positionen benötigen eine Bezeichnung.";
      return;
    }
  }
  saving.value = true;
  const payload = {
    customerId: props.invoice.customer.id,
    issueDate: form.issueDate,
    serviceDate: form.serviceDate,
    currency: props.invoice.currency || "EUR",
    notes: form.notes,
    status: form.status,
    items: form.items.map((it, i) => ({
      position: i + 1,
      itemId: it.itemId || null,
      title: it.title,
      quantity: Number(it.quantity),
      unit: it.unit || "pcs",
      unitPriceNet: Number(it.unitPriceNet),
      taxRate: Number(it.taxRatePct) / 100,
    })),
  };
  try {
    const res = await fetch(`${API}/invoices/${props.invoice.id}`, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload),
    });
    if (!res.ok) {
      const e = await res
        .json()
        .catch(() => ({ message: `HTTP ${res.status}` }));
      throw new Error(e.message);
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
.field select,
.field textarea {
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
  font-family: inherit;
}
.field input:focus,
.field select:focus,
.field textarea:focus {
  border-color: #111827;
}
.field textarea {
  resize: vertical;
  min-height: 76px;
}
</style>
