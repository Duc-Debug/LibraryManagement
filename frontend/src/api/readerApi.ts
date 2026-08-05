const getApiBaseUrl = () => {
  if (typeof window !== "undefined") {
    if (window.location.hostname === "localhost" || window.location.hostname === "127.0.0.1") {
      return "http://localhost:8080";
    }

    return `http://${window.location.hostname}:8080`;
  }

  return process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080";
};

const API_BASE_URL = getApiBaseUrl();

export type CreateReaderRequest = {
  name: string;
  email: string;
  phoneNumber: string;
  address: string;
};

export type ReaderResponse = {
  id: string;
  cardNumber: string;
  name: string;
  email: string;
  phoneNumber: string;
  address: string;
  cardStatus: string;
  cardIssuedAt: string;
  cardExpiryAt: string;
};

export async function createReader(request: CreateReaderRequest, token: string): Promise<ReaderResponse> {
  const response = await fetch(`${API_BASE_URL}/api/v1/readers`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      Accept: "application/json",
      Authorization: `Bearer ${token}`,
    },
    body: JSON.stringify(request),
  });

  if (!response.ok) {
    const errorData = await response.json().catch(() => ({}));
    throw new Error(errorData.message || "Tạo bạn đọc thất bại");
  }

  return response.json();
}

export async function fetchAllReaders(token: string): Promise<ReaderResponse[]> {
  const response = await fetch(`${API_BASE_URL}/api/v1/readers`, {
    headers: {
      Accept: "application/json",
      Authorization: `Bearer ${token}`,
    },
  });

  if (!response.ok) {
    const errorData = await response.json().catch(() => ({}));
    throw new Error(errorData.message || "Tải danh sách bạn đọc thất bại");
  }

  return response.json();
}
