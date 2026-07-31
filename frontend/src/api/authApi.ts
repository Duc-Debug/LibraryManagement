const API_BASE_URL = "http://localhost:8080";

export type LoginRequest = {
  username: string;
  password: string;
};

export type LoginResponse = {
  userId: number;
  username: string;
  fullName: string;
  roles: string[];
  accessToken: string;
  tokenType: string;
};

export async function login(request: LoginRequest): Promise<LoginResponse> {
  const response = await fetch(`${API_BASE_URL}/api/auth/login`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      Accept: "application/json",
    },
    body: JSON.stringify(request),
  });

  if (!response.ok) {
    throw new Error("Tên đăng nhập hoặc mật khẩu không đúng.");
  }

  return response.json();
}
export async function logout(accessToken: string): Promise<void> {
  await fetch(`${API_BASE_URL}/api/auth/logout`, {
    method: "POST",
    headers: {
      Authorization: `Bearer ${accessToken}`,
    },
  });
}