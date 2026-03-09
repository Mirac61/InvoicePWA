import { ref } from "vue";
import {
  listCustomers,
  createCustomer as apiCreateCustomer,
  type CustomerResponse,
  type CreateCustomerRequest,
} from "../services/CustomerAPI";
import { invoiceData } from "./invoiceData";

export type { CustomerResponse };

export const customerSearch = ref("");
export const customerSuggestions = ref<CustomerResponse[]>([]);
export const customerSearchLoading = ref(false);
export const selectedCustomerObj = ref<CustomerResponse | null>(null);

let searchTimer: ReturnType<typeof setTimeout> | null = null;

async function fetchCustomers(query: string) {
  customerSearchLoading.value = true;
  try {
    const data = await listCustomers();
    const q = query.toLowerCase();
    customerSuggestions.value = data
      .filter((c) =>
        `${c.forename} ${c.surname} ${c.email}`.toLowerCase().includes(q),
      )
      .slice(0, 6);
  } catch {
    customerSuggestions.value = [];
  } finally {
    customerSearchLoading.value = false;
  }
}

export function onCustomerSearchInput() {
  selectedCustomerObj.value = null;
  if (searchTimer) clearTimeout(searchTimer);
  if (customerSearch.value.length < 2) {
    customerSuggestions.value = [];
    return;
  }
  searchTimer = setTimeout(() => fetchCustomers(customerSearch.value), 280);
}

export function onCustomerSearchBlur() {
  setTimeout(() => {
    customerSuggestions.value = [];
  }, 150);
}

export function selectCustomer(c: CustomerResponse) {
  selectedCustomerObj.value = c;
  customerSearch.value = `${c.forename} ${c.surname}`;
  customerSuggestions.value = [];
  invoiceData.customerName = `${c.forename} ${c.surname}`;
  invoiceData.customerStreet = c.street;
  invoiceData.customerCity = `${c.zip} ${c.city}`;
  invoiceData.customerId = c.id;
}

export async function createCustomer(payload: CreateCustomerRequest) {
  const created = await apiCreateCustomer(payload);
  selectCustomer(created);
  return created;
}
