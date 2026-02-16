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

export async function deleteItem(id: string): Promise<void> {
  await api<void>(`/items/${encodeURIComponent(id)}`, { method: "DELETE" });
}
