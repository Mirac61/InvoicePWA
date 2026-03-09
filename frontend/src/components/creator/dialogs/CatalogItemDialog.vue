<template>
  <Teleport to="body">
    <div
      v-if="modelValue"
      class="modal-backdrop"
      @click.self="emit('update:modelValue', false)"
    >
      <div class="modal-box">
        <div class="modal-header">
          <h3 class="modal-title">Neuen Artikel anlegen</h3>
          <button
            class="modal-close"
            type="button"
            @click="emit('update:modelValue', false)"
          >
            ✕
          </button>
        </div>
        <div class="modal-body">
          <div class="field-group">
            <label>Name *</label>
            <input
              v-model="form.name"
              type="text"
              placeholder="z.B. Beratungsstunde"
            />
          </div>
          <div class="field-group">
            <label>Beschreibung</label>
            <input
              v-model="form.description"
              type="text"
              placeholder="Kurze Beschreibung (optional)"
            />
          </div>
          <div class="field-row">
            <div class="field-group">
              <label>Netto-Preis *</label>
              <input
                v-model.number="form.unitPriceNet"
                type="number"
                min="0"
                step="0.01"
                placeholder="0.00"
              />
            </div>
            <div class="field-group">
              <label>MwSt. (%)</label>
              <input
                v-model.number="form.defaultTaxRatePercent"
                type="number"
                min="0"
                max="100"
                placeholder="19"
              />
            </div>
            <div class="field-group">
              <label>Einheit</label>
              <input
                v-model="form.defaultUnit"
                type="text"
                placeholder="h / Stk / pcs"
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
            {{ saving ? "Wird gespeichert…" : "Artikel anlegen" }}
          </button>
        </div>
      </div>
    </div>
  </Teleport>
</template>

<script setup lang="ts">
import { reactive, ref, watch } from "vue";
import {
  createCatalogItem,
  selectItem,
} from "../../../composables/catalogSearch";
import { invoiceData } from "../../../composables/invoiceData";

const props = defineProps<{
  modelValue: boolean;
  targetRow: number;
}>();

const emit = defineEmits<{
  (e: "update:modelValue", val: boolean): void;
}>();

const saving = ref(false);
const error = ref<string | null>(null);

const form = reactive({
  name: "",
  description: "",
  defaultUnit: "",
  unitPriceNet: 0,
  defaultTaxRatePercent: 19,
});

watch(
  () => props.modelValue,
  (open) => {
    if (!open) return;
    form.name = invoiceData.tableRows[props.targetRow]?.cells[0] ?? "";
    form.description = "";
    form.defaultUnit = "";
    form.unitPriceNet = 0;
    form.defaultTaxRatePercent = 19;
    error.value = null;
  },
);

async function submit() {
  error.value = null;
  if (!form.name.trim()) {
    error.value = "Name ist erforderlich.";
    return;
  }
  if (form.unitPriceNet == null || form.unitPriceNet < 0) {
    error.value = "Netto-Preis muss >= 0 sein.";
    return;
  }

  saving.value = true;
  try {
    const created = await createCatalogItem({
      name: form.name.trim(),
      description: form.description.trim() || null,
      defaultUnit: form.defaultUnit.trim() || null,
      unitPriceNet: form.unitPriceNet,
      defaultTaxRate: (form.defaultTaxRatePercent ?? 0) / 100,
      active: true,
    });
    selectItem(props.targetRow, created);
    emit("update:modelValue", false);
  } catch (e: any) {
    error.value = `Fehler: ${e.message}`;
  } finally {
    saving.value = false;
  }
}
</script>
