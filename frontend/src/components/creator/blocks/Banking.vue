<template>
  <div>
    <div class="field-row">
      <div class="field-group">
        <label>Bank</label>
        <input
          v-model="invoiceData.bankName"
          type="text"
          placeholder="Deutsche Bank"
        />
      </div>
    </div>
    <div class="field-row">
      <div class="field-group">
        <label>IBAN</label>
        <input
          v-model="invoiceData.iban"
          type="text"
          placeholder="DE98 1213 6424 1111 3465 9752"
        />
      </div>
      <div class="field-group">
        <label>BIC</label>
        <input v-model="invoiceData.bic" type="text" placeholder="DBKEDFHH" />
      </div>
    </div>
    <div class="field-row">
      <div class="field-group">
        <label>Zahlungsziel (Text)</label>
        <input
          v-model="invoiceData.paymentNote"
          type="text"
          placeholder="Bitte überweisen Sie den Betrag bis zum..."
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { invoiceData } from "../../../composables/invoiceData";
</script>

<script setup lang="ts"></script>
