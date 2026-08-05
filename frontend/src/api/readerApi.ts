import { apiFetch } from "./httpClient";

export type CreateReaderRequest = {
  name: string;
  email: string;
  phoneNumber: string;
  address: string;
};

export type ReaderResponse = {
  id: number | string;
  cardNumber: string;
  name: string;
  email: string;
  phoneNumber: string;
  address: string;
  cardStatus: string;
  cardIssuedAt: string;
  cardExpiryAt: string;
  createdByName?: string;
};

export async function createReader(request: CreateReaderRequest, token?: string): Promise<ReaderResponse> {
  return apiFetch<ReaderResponse>("/api/v1/readers", {
    method: "POST",
    token,
    body: JSON.stringify(request),
  });
}

export async function fetchAllReaders(token?: string, page?: number, size?: number): Promise<ReaderResponse[]> {
  const query = page !== undefined && size !== undefined ? `?page=${page}&size=${size}` : "";
  const data = await apiFetch<any>(`/api/v1/readers${query}`, {
    token,
  });

  return Array.isArray(data) ? data : (data.content || []);
}
