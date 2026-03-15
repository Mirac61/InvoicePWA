import { ref, watch } from "vue";

type BankAccount = {
  id: number;
  bankName: string;
  iban: string;
  bic: string;
  isDefault: boolean;
};

const bankAccounts = ref<BankAccount[]>([]);

const stored = localStorage.getItem("invar_bank_accounts");
if (stored) {
  try {
    bankAccounts.value = JSON.parse(stored);
  } catch {}
}

watch(
  bankAccounts,
  (val) => {
    localStorage.setItem("invar_bank_accounts", JSON.stringify(val));
  },
  { deep: true },
);

function addAccount(bankName: string, iban: string, bic: string) {
  bankAccounts.value.push({
    id: Date.now(),
    bankName,
    iban,
    bic,
    isDefault: bankAccounts.value.length === 0,
  });
}

function deleteAccount(id: number) {
  bankAccounts.value = bankAccounts.value.filter((a) => a.id !== id);
}

function setDefault(id: number) {
  bankAccounts.value.forEach((a) => (a.isDefault = a.id === id));
}

export function useBankAccounts() {
  return { bankAccounts, addAccount, deleteAccount, setDefault };
}
