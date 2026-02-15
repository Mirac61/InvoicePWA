export enum CustomerType {
  PRIVATE = "PRIVATE",
  BUSINESS = "BUSINESS",
}

export interface CustomerResponse {
  id: string;
  type: CustomerType;
  name: string;
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

export type CreateCustomerRequest = Omit<
  CustomerResponse,
  "id" | "createdAt" | "updatedAt"
>;

async function api<T>(path: string, init?: RequestInit): Promise<T> {
  const res = await fetch(`/api${path}`, {
    headers: { "Content-Type": "application/json" },
    ...init,
  });

  if (!res.ok) {
    const text = await res.text().catch(() => "");
    throw new Error(text || `HTTP ${res.status}`);
  }

  const contentType = res.headers.get("content-type") || "";
  if (!contentType.includes("application/json")) return null as unknown as T;

  return (await res.json()) as T;
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
