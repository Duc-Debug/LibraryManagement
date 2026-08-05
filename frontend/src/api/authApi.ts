const getApiBaseUrl = () => {
  if (process.env.NEXT_PUBLIC_API_BASE_URL) {
    return process.env.NEXT_PUBLIC_API_BASE_URL;
  }
  return "http://localhost:8080";
};

const API_BASE_URL = getApiBaseUrl();

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
  const response = await fetch(`${API_BASE_URL}/api/auth/me`, {
    method: "GET",
    headers: {
      Authorization: `Bearer ${accessToken}`,
      Accept: "application/json",
    },
  });

  if (!response.ok) {
    const errorText = await response.text();
    throw new Error(errorText || "Không thể lấy thông tin cá nhân.");
  }

  return response.json();
}

export async function updateProfileApi(
  accessToken: string,
  data: UpdateProfileRequestData
): Promise<UserProfileResponse> {
  const response = await fetch(`${API_BASE_URL}/api/auth/profile`, {
    method: "PUT",
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${accessToken}`,
      Accept: "application/json",
    },
    body: JSON.stringify(data),
  });

  if (!response.ok) {
    const errorText = await response.text();
    throw new Error(errorText || "Cập nhật thông tin cá nhân thất bại.");
  }

  return response.json();
}

export async function changePasswordApi(
  accessToken: string,
  data: ChangePasswordRequestData
): Promise<ChangePasswordResponse> {
  const response = await fetch(`${API_BASE_URL}/api/auth/change-password`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${accessToken}`,
      Accept: "application/json",
    },
    body: JSON.stringify(data),
  });

  if (!response.ok) {
    const errorText = await response.text();
    throw new Error(errorText || "Đổi mật khẩu thất bại.");
  }

  const result = await response.json();

  if (!result.success) {
    throw new Error(result.message || "Đổi mật khẩu thất bại.");
  }

  return result;
}