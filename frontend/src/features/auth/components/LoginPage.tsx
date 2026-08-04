'use client';

import { useState } from 'react';
import { login } from '@/api/authApi';
import type { UserAccount } from '@/features/accounts';

interface LoginPageProps {
  accounts: UserAccount[];
  onLogin: (account: UserAccount) => void;
}

export default function LoginPage({ accounts, onLogin }: LoginPageProps) {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');

  const handleLogin = async () => {
    if (!username || !password) {
      setError('Vui lòng nhập đầy đủ thông tin.');
      return;
    }

    try {
      const data = await login({ username, password });

      localStorage.setItem('accessToken', data.accessToken);
      localStorage.setItem('tokenType', data.tokenType || 'Bearer');
      localStorage.setItem('currentUser', JSON.stringify({
        userId: data.userId,
        username: data.username,
        fullName: data.fullName,
        roles: data.roles
      }));

      const isSystemAdmin = data.roles && data.roles.includes('ADMIN');
      const isLibrarian = data.roles && data.roles.includes('LIBRARIAN');

      const account: UserAccount = {
        id: String(data.userId),
        username: data.username,
        password: '',
        fullName: data.fullName,
        role: isSystemAdmin ? 'admin' : isLibrarian ? 'thu_thu' : 'nguoi_dung',
        active: true,
      };

      onLogin(account);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Không thể đăng nhập lúc này.');
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-background">
      <div className="bg-card rounded-2xl p-8 w-full max-w-sm shadow-md border border-border">
        <h1 className="text-2xl font-bold text-center mb-1 text-primary">
          Quản Lý Thư Viện
        </h1>
        <p className="text-sm text-muted-foreground text-center mb-7">
          Đăng nhập để truy cập hệ thống
        </p>

        {error && (
          <div className="mb-4 text-sm text-destructive bg-red-50 px-3 py-2 rounded-xl border border-red-200">
            {error}
          </div>
        )}

        <div className="space-y-4">
          <div>
            <label className="block text-sm font-medium text-foreground mb-1">
              Tên đăng nhập
            </label>
            <input
              value={username}
              onChange={(e) => {
                setUsername(e.target.value);
                setError('');
              }}
              onKeyDown={(e) => e.key === 'Enter' && handleLogin()}
              placeholder="Nhập tên đăng nhập"
              className="w-full border border-border rounded-xl px-3 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-primary/20 bg-background"
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-foreground mb-1">
              Mật khẩu
            </label>
            <input
              type="password"
              value={password}
              onChange={(e) => {
                setPassword(e.target.value);
                setError('');
              }}
              onKeyDown={(e) => e.key === 'Enter' && handleLogin()}
              placeholder="Nhập mật khẩu"
              className="w-full border border-border rounded-xl px-3 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-primary/20 bg-background"
            />
          </div>

          <button
            onClick={handleLogin}
            className="w-full py-3 rounded-xl text-sm font-semibold text-primary-foreground mt-1 hover:opacity-90 transition-opacity bg-primary"
          >
            Đăng nhập
          </button>
        </div>

        <div className="mt-6 p-3 bg-muted rounded-lg">
          <p className="text-xs text-muted-foreground mb-2 font-medium">
            Sử dụng tài khoản người dùng/admin để đăng nhập
          </p>
          <ul className="text-xs text-muted-foreground space-y-1">
          </ul>
        </div>
      </div>
    </div>
  );
}
