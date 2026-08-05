import { login } from "@/api/authApi";
import { useState } from "react";
import type { UserAccount } from "@/types";

interface LoginPageProps {
  accounts: UserAccount[];
  onLogin: (account: UserAccount) => void;
}

export default function LoginPage({ accounts, onLogin }: LoginPageProps) {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");

  const handleLogin = async () => {
    if (!username || !password) {
      setError("Vui lòng nhập đầy đủ thông tin.");
      return;
    }
    try {
      const res = await login({ username, password });

      localStorage.setItem('accessToken', res.accessToken);
      localStorage.setItem('tokenType', res.tokenType);
      localStorage.setItem('currentUser', JSON.stringify({
        userId: res.userId,
        username: res.username,
        fullName: res.fullName,
        roles: res.roles
      }));

      const roles = res.roles ?? [];
      const role = roles.includes('ADMIN') ? 'admin' : 'thu_thu';

      const account: UserAccount = {
        id: String(res.userId),
        username: res.username,
        password: '',
        fullName: res.fullName,
        role,
        active: true,
      };

      onLogin(account);
    } catch (err) {
      setError('Tên đăng nhập hoặc mật khẩu không đúng.');
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center" style={{ backgroundColor: "#f0ede6" }}>
      <div className="bg-white rounded-2xl p-8 w-full max-w-sm shadow-md">
        <h1 className="text-2xl font-bold text-center mb-1" style={{ color: "#1a4a2e" }}>
          Quản Lý Thư Viện
        </h1>
        <p className="text-sm text-gray-400 text-center mb-7">Đăng nhập để truy cập hệ thống</p>

        {error && (
          <div className="mb-4 text-sm text-red-600 bg-red-50 px-3 py-2 rounded-xl">{error}</div>
        )}

        <div className="space-y-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Tên đăng nhập</label>
            <input
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              onKeyDown={(e) => e.key === "Enter" && handleLogin()}
              placeholder="Nhập tên đăng nhập"
              className="w-full border border-gray-200 rounded-xl px-3 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-green-200"
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Mật khẩu</label>
            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              onKeyDown={(e) => e.key === "Enter" && handleLogin()}
              placeholder="Nhập mật khẩu"
              className="w-full border border-gray-200 rounded-xl px-3 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-green-200"
            />
          </div>
          <button
            onClick={handleLogin}
            className="w-full py-3 rounded-xl text-sm font-bold text-white mt-1 hover:opacity-90 transition-opacity"
            style={{ backgroundColor: "#1a4a2e" }}
          >
            Đăng nhập
          </button>
        </div>
        <p className="text-xs text-gray-300 text-center mt-4">Dùng tài khoản thủ thư hoặc người dùng để đăng nhập</p>
      </div>
    </div>
  );
}
