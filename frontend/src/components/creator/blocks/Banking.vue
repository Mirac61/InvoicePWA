<script setup lang="ts">
import { onMounted } from "vue";
import { invoiceData } from "../../../composables/invoiceData";
import { useBankAccounts } from "../../../composables/bankAccounts";

const { bankAccounts } = useBankAccounts();

// Beim Laden: Standard-Konto automatisch vorausfüllen
onMounted(() => {
  const defaultAccount = bankAccounts.value.find((a) => a.isDefault);
  if (defaultAccount) {
    invoiceData.bankName = defaultAccount.bankName;
    invoiceData.iban = defaultAccount.iban;
    invoiceData.bic = defaultAccount.bic;
  }
});

function selectAccount(id: number) {
  const account = bankAccounts.value.find((a) => a.id === id);
  if (account) {
    invoiceData.bankName = account.bankName;
    invoiceData.iban = account.iban;
    invoiceData.bic = account.bic;
  }
}
</script>

<template>
  <div>
    <!-- Konto auswählen -->
    <div class="field-row" v-if="bankAccounts.length > 0">
      <div class="field-group">
        <label>Bankverbindung</label>
        <select
          @change="
            selectAccount(Number(($event.target as HTMLSelectElement).value))
          "
        >
          <option disabled value="">Konto auswählen...</option>
          <option
            v-for="account in bankAccounts"
            :key="account.id"
            :value="account.id"
            :selected="account.bankName === invoiceData.bankName"
          >
            {{ account.bankName }} – {{ account.iban }}
          </option>
        </select>
      </div>
    </div>

    <!-- Fallback wenn noch kein Konto hinterlegt -->
    <div class="field-row" v-else>
      <p class="bank-hint">
        Noch keine Bankverbindung hinterlegt.
        <a href="/settings">In den Einstellungen hinzufügen →</a>
      </p>
    </div>

    <!-- Zahlungsziel bleibt frei editierbar -->
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

<style scoped>
.bank-hint {
  font-size: 13px;
  opacity: 0.6;
  margin: 4px 0;
}
.bank-hint a {
  color: var(--primary-accent-color);
  text-decoration: none;
}
</style>
