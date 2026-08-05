import { AuthStorage } from "@/lib/authStorage";

const getApiBaseUrl = () => {
  if (process.env.NEXT_PUBLIC_API_BASE_URL) {
    return process.env.NEXT_PUBLIC_API_BASE_URL;
  }
  return "http://localhost:8080";
};

const API_BASE_URL = getApiBaseUrl();

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

function getAuthHeaders() {
  const token = AuthStorage.getAccessToken();
  return {
    "Content-Type": "application/json",
    Accept: "application/json",
    Authorization: token ? `Bearer ${token}` : "",
  };
}

export async function fetchAllLibrarians(): Promise<Librarian[]> {
  const res = await fetch(`${API_BASE_URL}/api/admin/librarians`, {
    headers: getAuthHeaders(),
  });
  if (!res.ok) {
    if (res.status === 401) {
      throw new Error("Phiên đăng nhập đã hết hạn hoặc thiếu Token. Vui lòng Đăng xuất và Đăng nhập lại bằng tài khoản Admin!");
    }
    if (res.status === 403) {
      throw new Error("Tài khoản hiện tại không có quyền Quản trị viên (Admin).");
    }
    const errorData = await res.json().catch(() => ({}));
    throw new Error(errorData.message || "Không thể tải danh sách thủ thư.");
  }
  return res.json();
}

export async function fetchLibrarianById(id: number): Promise<Librarian> {
  const res = await fetch(`${API_BASE_URL}/api/admin/librarians/${id}`, {
    headers: getAuthHeaders(),
  });
  if (!res.ok) {
    if (res.status === 401) {
      throw new Error("Phiên đăng nhập đã hết hạn hoặc thiếu Token. Vui lòng Đăng nhập lại!");
    }
    if (res.status === 403) {
      throw new Error("Tài khoản không có quyền xem thông tin thủ thư này.");
    }
    const errorData = await res.json().catch(() => ({}));
    throw new Error(errorData.message || "Không tìm thấy thông tin thủ thư.");
  }
  return res.json();
}

export async function createLibrarian(data: CreateLibrarianRequest): Promise<Librarian> {
  const res = await fetch(`${API_BASE_URL}/api/admin/librarians`, {
    method: "POST",
    headers: getAuthHeaders(),
    body: JSON.stringify(data),
  });
  if (!res.ok) {
    const errorData = await res.json().catch(() => ({}));
    throw new Error(errorData.message || "Tạo thủ thư thất bại.");
  }
  return res.json();
}

export async function updateLibrarian(
  id: number,
  data: UpdateLibrarianRequest
): Promise<Librarian> {
  const res = await fetch(`${API_BASE_URL}/api/admin/librarians/${id}`, {
    method: "PUT",
    headers: getAuthHeaders(),
    body: JSON.stringify(data),
  });
  if (!res.ok) {
    const errorData = await res.json().catch(() => ({}));
    throw new Error(errorData.message || "Cập nhật thông tin thủ thư thất bại.");
  }
  return res.json();
}

export async function deleteLibrarian(id: number): Promise<void> {
  const res = await fetch(`${API_BASE_URL}/api/admin/librarians/${id}`, {
    method: "DELETE",
    headers: getAuthHeaders(),
  });
  if (!res.ok) {
    const errorData = await res.json().catch(() => ({}));
    throw new Error(errorData.message || "Vô hiệu hóa thủ thư thất bại.");
  }
}
