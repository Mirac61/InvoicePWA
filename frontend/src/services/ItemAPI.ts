import { api } from "./_http";

export interface ItemResponse {
  id: string;
  name: string;
  description?: string | null;
  defaultUnit?: string | null;
  defaultTaxRate: number;
  unitPriceNet: number;
  active: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface CreateItemRequest {
  name: string;
  description?: string | null;
  defaultUnit?: string | null;
  defaultTaxRate?: number | null;
  unitPriceNet: number;
  active?: boolean | null;
}

export async function listItems(q?: string): Promise<ItemResponse[]> {
  const qs = q && q.trim() ? `?q=${encodeURIComponent(q.trim())}` : "";
  return api<ItemResponse[]>(`/items${qs}`);
}

export async function createItem(
  payload: CreateItemRequest,
): Promise<ItemResponse> {
  return api<ItemResponse>("/items", {
    method: "POST",
    body: JSON.stringify(payload),
  });
}

export async function updateItem(
  id: string,
  payload: CreateItemRequest,
): Promise<ItemResponse> {
  return api<ItemResponse>(`/items/${encodeURIComponent(id)}`, {
    method: "PUT",
    body: JSON.stringify(payload),
  });
}

export async function deleteItem(id: string): Promise<void> {
  return api<void>(`/items/${encodeURIComponent(id)}`, { method: "DELETE" });
}
