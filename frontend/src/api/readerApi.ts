import { apiFetch } from "./httpClient";

export type CreateReaderRequest = {
  name: string;
  email: string;
  phoneNumber: string;
  address: string;
};

export type UpdateReaderRequest = {
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

export type ReaderPageResult = {
  content: ReaderResponse[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
};

export async function createReader(request: CreateReaderRequest, token?: string): Promise<ReaderResponse> {
  return apiFetch<ReaderResponse>("/api/v1/readers", {
    method: "POST",
    token,
    body: JSON.stringify(request),
  });
}

/** Tải danh sách bạn đọc dạng mảng phẳng (hỗ trợ tương thích ngược) */
export async function fetchAllReaders(token?: string, page?: number, size?: number): Promise<ReaderResponse[]> {
  const query = page !== undefined && size !== undefined ? `?page=${page}&size=${size}` : "";
  const data = await apiFetch<any>(`/api/v1/readers${query}`, {
    token,
  });

  return Array.isArray(data) ? data : (data.content || []);
}

/** Tải danh sách bạn đọc phân trang đầy đủ thông tin metadata */
export async function fetchReadersPage(token?: string, page: number = 0, size: number = 20): Promise<ReaderPageResult> {
  return apiFetch<ReaderPageResult>(`/api/v1/readers?page=${page}&size=${size}`, {
    token,
  });
}

/** Cập nhật thông tin độc giả */
export async function updateReaderApi(readerId: number | string, request: UpdateReaderRequest, token?: string): Promise<ReaderResponse> {
  return apiFetch<ReaderResponse>(`/api/v1/readers/${readerId}`, {
    method: "PUT",
    token,
    body: JSON.stringify(request),
  });
}

/** Xóa độc giả (Backend sẽ thực hiện deactive/khóa thẻ và kiểm tra phiếu mượn hoạt động) */
export async function deleteReaderApi(readerId: number | string, token?: string): Promise<void> {
  return apiFetch<void>(`/api/v1/readers/${readerId}`, {
    method: "DELETE",
    token,
  });
}

/** Đổi trạng thái thẻ độc giả (Khóa/Mở khóa thẻ) */
export async function toggleReaderStatusApi(readerId: number | string, status: 'ACTIVE' | 'LOCKED', token?: string): Promise<ReaderResponse> {
  return apiFetch<ReaderResponse>(`/api/v1/readers/${readerId}/status?status=${status}`, {
    method: "PATCH",
    token,
  });
}
