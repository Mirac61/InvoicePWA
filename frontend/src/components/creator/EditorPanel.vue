<template>
  <aside class="editor-panel">
    <div class="editor-header">
      <h2 class="editor-title">Creator</h2>
      <span class="section-count">{{ sections.length }} Abschnitte</span>
    </div>

    <div class="sections-list">
      <div
        v-for="(section, index) in sections"
        :key="section.id"
        class="section-card"
        :class="{
          'section-card--active': activeSection === section.id,
          'section-card--dragging': dragIndex === index,
          'section-card--dragover':
            dragOverIndex === index && dragIndex !== index,
        }"
        draggable="true"
        @dragstart="onDragStart($event, index)"
        @dragover.prevent="onDragOver($event, index)"
        @drop="onDrop($event, index)"
        @dragend="onDragEnd"
      >
        <div class="section-card__header" @click="toggleSection(section.id)">
          <div class="section-card__left">
            <span class="drag-handle" @click.stop>⠿</span>
            <span class="section-name">{{ section.label }}</span>
          </div>
          <div class="section-card__right">
            <button
              class="btn-icon btn-icon--danger"
              @click.stop="removeSection(section.id)"
              title="Entfernen"
            >
              ✕
            </button>
            <span
              class="chevron"
              :class="{ 'chevron--open': activeSection === section.id }"
              >›</span
            >
          </div>
        </div>

        <transition name="slide">
          <div v-if="activeSection === section.id" class="section-card__body">
            <Company v-if="section.type === 'header'" />
            <Customer
              v-else-if="section.type === 'customer'"
              @open-customer-dialog="emit('open-customer-dialog')"
            />
            <InvoiceMeta v-else-if="section.type === 'meta'" />
            <div v-else-if="section.type === 'greeting'" class="field-group">
              <label>Anrede / Einleitung</label>
              <textarea
                v-model="invoiceData.intro"
                rows="5"
                placeholder="Sehr geehrte/r Frau/Herr Mustermann, vielen dank..."
              ></textarea>
            </div>
            <LineItems
              v-else-if="section.type === 'table'"
              @open-catalog-dialog="(ri) => emit('open-catalog-dialog', ri)"
            />
            <Banking v-else-if="section.type === 'banking'" />
            <Closing v-else-if="section.type === 'footer'" />
          </div>
        </transition>
      </div>
    </div>

    <div class="add-section-area">
      <p class="add-section-label">Abschnitt hinzufügen</p>
      <div class="add-section-buttons">
        <button
          v-for="avail in availableSectionsToAdd"
          :key="avail.type"
          class="btn-add-section"
          type="button"
          @click="addSection(avail.type)"
        >
          {{ avail.label }}
        </button>
      </div>
    </div>
  </aside>
</template>

<script setup lang="ts">
import { invoiceData } from "../../composables/invoiceData";
import {
  sections,
  activeSection,
  dragIndex,
  dragOverIndex,
  availableSectionsToAdd,
  toggleSection,
  removeSection,
  addSection,
  onDragStart,
  onDragOver,
  onDrop,
  onDragEnd,
} from "../../composables/sectionOrdering";

import Company from "./blocks/Company.vue";
import Customer from "./blocks/Customer.vue";
import InvoiceMeta from "./blocks/InvoiceMeta.vue";
import LineItems from "./blocks/LineItems.vue";
import Banking from "./blocks/Banking.vue";
import Closing from "./blocks/Closing.vue";

const emit = defineEmits<{
  (e: "open-customer-dialog"): void;
  (e: "open-catalog-dialog", rowIndex: number): void;
}>();
</script>
