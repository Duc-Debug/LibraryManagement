import { login } from "@/api/authApi";
import { useState } from "react";
import type { UserAccount } from "@/types";
import { AuthStorage } from "@/lib/authStorage";

interface LoginPageProps {
  accounts: UserAccount[];
  onLogin: (account: UserAccount) => void;
}

export default function LoginPage({ accounts, onLogin }: LoginPageProps) {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const handleLogin = async () => {
    if (loading) return;

    if (!username || !password) {
      setError("Vui lòng nhập đầy đủ thông tin.");
      return;
    }

    setLoading(true);
    try {
      const data = await login({ username, password });
      AuthStorage.saveLogin(data);

      const account: UserAccount = {
        id: String(data.userId),
        username: data.username,
        password: '',
        fullName: data.fullName,
        role: (data.roles && (data.roles.includes('ADMIN') || data.roles.includes('LIBRARIAN'))) ? 'thu_thu' : 'nguoi_dung',
        active: true,
      };

      onLogin(account);
    } catch (err: any) {
      const account = accounts.find((a) => a.username === username && a.password === password);
      if (!account) {
        setError(err?.message || "Tên đăng nhập hoặc mật khẩu không đúng.");
        return;
      }
      if (!account.active) {
        setError("Tài khoản đã bị khóa. Vui lòng liên hệ quản trị viên.");
        return;
      }
      onLogin(account);
    } finally {
      setLoading(false);
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
            disabled={loading}
            className="w-full py-3 rounded-xl text-sm font-bold text-white mt-1 hover:opacity-90 transition-opacity disabled:opacity-50 disabled:cursor-not-allowed"
            style={{ backgroundColor: "#1a4a2e" }}
          >
            {loading ? "Đang đăng nhập..." : "Đăng nhập"}
          </button>
        </div>
        <p className="text-xs text-gray-300 text-center mt-4">Dùng tài khoản thủ thư hoặc người dùng để đăng nhập</p>
      </div>
    </div>
  );
}

