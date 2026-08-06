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

export type UserProfileResponse = {
  id: number;
  username: string;
  fullName: string;
  email: string | null;
  phone: string | null;
  enabled: boolean;
  roles: string[];
  createdAt: string;
  updatedAt: string;
};

export type UpdateProfileRequestData = {
  fullName: string;
  email?: string;
  phone?: string;
};

export type ChangePasswordRequestData = {
  oldPassword: string;
  newPassword: string;
  confirmPassword: string;
};

export type ChangePasswordResponse = {
  success: boolean;
  message: string;
};

export async function getProfileApi(accessToken: string): Promise<UserProfileResponse> {
  return apiFetch<UserProfileResponse>("/api/auth/me", {
    method: "GET",
    token: accessToken,
  });
}

export async function updateProfileApi(
  accessToken: string,
  data: UpdateProfileRequestData
): Promise<UserProfileResponse> {
  return apiFetch<UserProfileResponse>("/api/auth/profile", {
    method: "PUT",
    token: accessToken,
    body: JSON.stringify(data),
  });
}

export async function changePasswordApi(
  accessToken: string,
  data: ChangePasswordRequestData
): Promise<ChangePasswordResponse> {
  const result = await apiFetch<ChangePasswordResponse>("/api/auth/change-password", {
    method: "POST",
    token: accessToken,
    body: JSON.stringify(data),
  });

  if (!result.success) {
    throw new Error(result.message || "Đổi mật khẩu thất bại.");
  }

  return result;
}
