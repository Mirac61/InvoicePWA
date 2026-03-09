<template>
  <div>
    <div class="field-group">
      <label>Bild des Logos hochladen</label>
      <div
        class="logo-upload"
        :class="{
          'logo-upload--has-file': !!invoiceData.logo,
          'logo-upload--dragover': logoDragOver,
        }"
        @click="triggerLogoUpload"
        @dragover.prevent="logoDragOver = true"
        @dragleave="logoDragOver = false"
        @drop.prevent="onLogoDrop"
      >
        <img
          v-if="invoiceData.logo"
          :src="invoiceData.logo"
          class="logo-preview"
          alt="Firmenlogo"
        />
        <template v-else>
          <span class="logo-upload__arrow">↑</span>
          <span class="logo-upload__label">
            <button class="logo-upload__btn" type="button">
              Datei auswählen
            </button>
            <span v-if="invoiceData.logoName"
              >&nbsp;{{ invoiceData.logoName }}</span
            >
            <span v-else class="logo-upload__hint"
              >&nbsp;oder hierher ziehen</span
            >
          </span>
        </template>
        <input
          ref="logoInputRef"
          type="file"
          accept="image/png,image/jpeg,image/jpg,image/svg+xml,image/webp"
          style="display: none"
          @change="onLogoFileChange"
        />
      </div>
      <button
        v-if="invoiceData.logo"
        class="logo-remove"
        type="button"
        @click.stop="
          invoiceData.logo = '';
          invoiceData.logoName = '';
        "
      >
        ✕ Logo entfernen
      </button>
    </div>

    <div class="field-row">
      <div class="field-group">
        <label>Firmenname</label>
        <input
          v-model="invoiceData.companyName"
          type="text"
          placeholder="Muster GmbH"
        />
      </div>
    </div>
    <div class="field-row">
      <div class="field-group">
        <label>Straße & Nr.</label>
        <input
          v-model="invoiceData.companyStreet"
          type="text"
          placeholder="Musterstraße 21"
        />
      </div>
      <div class="field-group">
        <label>PLZ & Stadt</label>
        <input
          v-model="invoiceData.companyCity"
          type="text"
          placeholder="73728 Esslingen"
        />
      </div>
    </div>
    <div class="field-row">
      <div class="field-group">
        <label>USt-IdNr.</label>
        <input
          v-model="invoiceData.taxId"
          type="text"
          placeholder="DE123456789"
        />
      </div>
      <div class="field-group">
        <label>Ansprechpartner</label>
        <input
          v-model="invoiceData.contact"
          type="text"
          placeholder="Anna Musterfrau"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { invoiceData } from "../../../composables/invoiceData";

const logoInputRef = ref<HTMLInputElement | null>(null);
const logoDragOver = ref(false);

function triggerLogoUpload() {
  logoInputRef.value?.click();
}

function readLogoFile(file: File) {
  if (!file.type.startsWith("image/")) return;
  invoiceData.logoName = file.name;
  const reader = new FileReader();
  reader.onload = (ev) => {
    invoiceData.logo = ev.target?.result as string;
  };
  reader.readAsDataURL(file);
}

function onLogoFileChange(e: Event) {
  const file = (e.target as HTMLInputElement).files?.[0];
  if (file) readLogoFile(file);
}

function onLogoDrop(e: DragEvent) {
  logoDragOver.value = false;
  const file = e.dataTransfer?.files?.[0];
  if (file) readLogoFile(file);
}
</script>
