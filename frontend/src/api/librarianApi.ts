import { apiFetch } from "./httpClient";

export interface Librarian {
  id: number;
  username: string;
  fullName: string;
  email?: string;
  phone?: string;
  enabled: boolean;
  roles: string[];
  createdAt?: string;
  updatedAt?: string;
}

export interface CreateLibrarianRequest {
  username: string;
  password: string;
  fullName: string;
  email?: string;
  phone?: string;
}

export interface UpdateLibrarianRequest {
  fullName: string;
  email?: string;
  phone?: string;
  enabled?: boolean;
}

export async function fetchAllLibrarians(): Promise<Librarian[]> {
  try {
    return await apiFetch<Librarian[]>("/api/admin/librarians");
  } catch (err: any) {
    if (err.message?.includes("401")) {
      throw new Error("Phiên đăng nhập đã hết hạn hoặc thiếu Token. Vui lòng Đăng xuất và Đăng nhập lại!");
    }
    if (err.message?.includes("403")) {
      throw new Error("Tài khoản hiện tại không có quyền Quản trị viên (Admin).");
    }
    throw new Error(err.message || "Không thể tải danh sách thủ thư.");
  }
}

export async function fetchLibrarianById(id: number): Promise<Librarian> {
  try {
    return await apiFetch<Librarian>(`/api/admin/librarians/${id}`);
  } catch (err: any) {
    if (err.message?.includes("401")) {
      throw new Error("Phiên đăng nhập đã hết hạn hoặc thiếu Token. Vui lòng Đăng nhập lại!");
    }
    if (err.message?.includes("403")) {
      throw new Error("Tài khoản không có quyền xem thông tin thủ thư này.");
    }
    throw new Error(err.message || "Không tìm thấy thông tin thủ thư.");
  }
}

export async function createLibrarian(data: CreateLibrarianRequest): Promise<Librarian> {
  return apiFetch<Librarian>("/api/admin/librarians", {
    method: "POST",
    body: JSON.stringify(data),
  });
}

export async function updateLibrarian(
  id: number,
  data: UpdateLibrarianRequest
): Promise<Librarian> {
  return apiFetch<Librarian>(`/api/admin/librarians/${id}`, {
    method: "PUT",
    body: JSON.stringify(data),
  });
}

export async function deleteLibrarian(id: number): Promise<void> {
  return apiFetch<void>(`/api/admin/librarians/${id}`, {
    method: "DELETE",
  });
}
