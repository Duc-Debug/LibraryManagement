import type { LoginResponse } from "@/api/authApi";

export const AuthStorage = {
  saveLogin(data: LoginResponse) {
    if (typeof window === "undefined") return;
    localStorage.setItem("accessToken", data.accessToken);
    localStorage.setItem("tokenType", data.tokenType ?? "Bearer");
    localStorage.setItem(
      "currentUser",
      JSON.stringify({
        userId: data.userId,
        username: data.username,
        fullName: data.fullName,
        roles: data.roles,
      })
    );
  },

  clearLogin() {
    if (typeof window === "undefined") return;
    localStorage.removeItem("accessToken");
    localStorage.removeItem("tokenType");
    localStorage.removeItem("currentUser");
  },

  getAccessToken(): string | null {
    if (typeof window === "undefined") return null;
    return localStorage.getItem("accessToken");
  },

  getCurrentUser() {
    if (typeof window === "undefined") return null;
    const savedUser = localStorage.getItem("currentUser");
    if (!savedUser) return null;
    try {
      return JSON.parse(savedUser);
    } catch {
      return null;
    }
  },
};
