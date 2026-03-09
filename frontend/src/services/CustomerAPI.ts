import { api } from "./_http";

export enum CustomerType {
  PRIVATE = "PRIVATE",
  BUSINESS = "BUSINESS",
}

export interface CustomerResponse {
  id: string;
  type: CustomerType;
  forename: string;
  surname: string;
  street: string;
  zip: string;
  city: string;
  country: string;
  email: string;
  phone: string;
  vatId?: string;
  createdAt: string;
  updatedAt: string;
}

export interface CreateCustomerRequest {
  type: CustomerType;
  forename: string;
  surname: string;
  street: string;
  zip: string;
  city: string;
  country: string;
  email: string;
  phone: string;
  vatId?: string;
}

export async function listCustomers(): Promise<CustomerResponse[]> {
  return api<CustomerResponse[]>("/customers");
}

export async function createCustomer(
  payload: CreateCustomerRequest,
): Promise<CustomerResponse> {
  return api<CustomerResponse>("/customers", {
    method: "POST",
    body: JSON.stringify(payload),
  });
}

export async function updateCustomer(
  id: string,
  payload: CreateCustomerRequest,
): Promise<CustomerResponse> {
  return api<CustomerResponse>(`/customers/${encodeURIComponent(id)}`, {
    method: "PUT",
    body: JSON.stringify(payload),
  });
}

export async function deleteCustomer(id: string): Promise<void> {
  return api<void>(`/customers/${encodeURIComponent(id)}`, {
    method: "DELETE",
  });
}
