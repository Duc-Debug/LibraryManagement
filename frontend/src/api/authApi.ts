import { apiFetch } from "./httpClient";

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
  try {
    return await apiFetch<LoginResponse>("/api/auth/login", {
      method: "POST",
      body: JSON.stringify(request),
    });
  } catch (err: any) {
    throw new Error(err.message || "Tên đăng nhập hoặc mật khẩu không đúng.");
  }
}

export async function logout(accessToken: string): Promise<void> {
  try {
    await apiFetch<void>("/api/auth/logout", {
      method: "POST",
      token: accessToken,
    });
  } catch (ignored) {}
}