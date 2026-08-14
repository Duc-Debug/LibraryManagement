import { ApiError } from "../lib/errorDictionary";

const getApiBaseUrl = (): string => {
  // 1. Kiểm tra biến môi trường được cấu hình sẵn (Vite / Next.js)
  const envUrl =
    (typeof process !== "undefined" && process.env?.NEXT_PUBLIC_API_BASE_URL) ||
    (typeof import.meta !== "undefined" && (import.meta as any).env?.VITE_API_BASE_URL);

  if (envUrl) {
    return envUrl;
  }

  // 2. Tự động nhận diện linh hoạt khi chạy trên trình duyệt (Browser Runtime)
  if (typeof window !== "undefined") {
    // 2.1. Kiểm tra nếu người dùng đã tùy chỉnh URL trong localStorage
    const savedCustomUrl = localStorage.getItem("customApiBaseUrl");
    if (savedCustomUrl) {
      return savedCustomUrl;
    }

    const { hostname, protocol } = window.location;

    // 2.2. Xử lý VS Code Dev Tunnels / GitHub Codespaces (ví dụ: xxxx-3000.asse.devtunnels.ms -> xxxx-8080.asse.devtunnels.ms)
    if (hostname.includes(".devtunnels.ms") || hostname.includes(".app.github.dev")) {
      const backendHostname = hostname.replace(/-(5173|3000|4173)/, "-8080");
      return `${protocol}//${backendHostname}`;
    }

    // 2.3. Nếu truy cập qua IP mạng LAN hoặc Domain Public ngoài localhost
    if (hostname !== "localhost" && hostname !== "127.0.0.1") {
      // Nếu là địa chỉ IP (mạng LAN), trỏ đến cổng 8080 của máy đó
      if (/^(?:[0-9]{1,3}\.){3}[0-9]{1,3}$/.test(hostname)) {
        return `${protocol}//${hostname}:8080`;
      }
      // Ngược lại (production domain), dùng relative path để hỗ trợ Nginx reverse proxy
      return "";
    }
  }

  // 3. Mặc định fallback về localhost:8080 khi phát triển nội bộ
  return "http://localhost:8080";
};

export const getDynamicApiBaseUrl = getApiBaseUrl;

export interface ApiFetchOptions extends RequestInit {
  token?: string;
}

export async function apiFetch<T>(endpoint: string, options: ApiFetchOptions = {}): Promise<T> {
  const { token, headers, ...customConfig } = options;

  const authToken = token || (typeof window !== "undefined" ? localStorage.getItem("accessToken") : null);
  const baseUrl = getDynamicApiBaseUrl();

  const defaultHeaders: Record<string, string> = {
    Accept: "application/json",
  };

  if (!(options.body instanceof FormData)) {
    defaultHeaders["Content-Type"] = "application/json";
  }

  if (authToken) {
    defaultHeaders["Authorization"] = `Bearer ${authToken}`;
  }

  const config: RequestInit = {
    method: options.method || "GET",
    headers: {
      ...defaultHeaders,
      ...headers,
    },
    ...customConfig,
  };

  const response = await fetch(`${baseUrl}${endpoint}`, config);

  if (!response.ok) {
    // Nếu token hết hạn hoặc không hợp lệ (401) -> Tự động xóa session và redirect về trang login
    if (response.status === 401) {
      if (typeof window !== "undefined") {
        localStorage.removeItem("accessToken");
        localStorage.removeItem("tokenType");
        localStorage.removeItem("currentUser");
        if (window.location.search !== "?page=login") {
          window.location.href = "/?page=login";
        }
      }
    }
    const errorData = await response.json().catch(() => ({}));
    throw new ApiError(
      errorData.code || "UNKNOWN_ERROR",
      errorData.message || `Request failed with status ${response.status}`
    );
  }

  if (response.status === 204) {
    return {} as T;
  }

  const text = await response.text();
  return text ? (JSON.parse(text) as T) : ({} as T);
}
