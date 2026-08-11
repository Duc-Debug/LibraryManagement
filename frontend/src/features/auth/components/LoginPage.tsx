'use client';

import { useState, useRef } from 'react';
import { login } from '@/api/authApi';
import type { UserAccount } from '@/features/accounts';
import { useTheme } from '@/hooks/useTheme';
import { BookOpen, Lock, User, Eye, EyeOff, LogIn, AlertCircle, Sun, Moon } from 'lucide-react';

interface LoginPageProps {
  accounts?: UserAccount[];
  onLogin: (account: UserAccount) => void;
}

export function LoginPage({ accounts, onLogin }: LoginPageProps) {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const { theme, toggleTheme } = useTheme();
  const passwordInputRef = useRef<HTMLInputElement>(null);

  const handleLogin = async () => {
    if (!username || !password) {
      setError('Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu.');
      return;
    }

    setLoading(true);
    setError('');

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

      const roles = data.roles ?? [];
      const role = roles.includes('ADMIN') ? 'admin' : 'thu_thu';

      const account: UserAccount = {
        id: String(data.userId),
        username: data.username,
        password: '',
        fullName: data.fullName,
        role,
        active: true,
      };

      onLogin(account);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Tên đăng nhập hoặc mật khẩu không chính xác.');
    } finally {
      setLoading(false);
    }
  };

  const isDark = theme === 'dark';

  return (
      <div className={`min-h-screen w-full flex items-center justify-center p-4 relative overflow-hidden transition-colors duration-300 ${
          isDark
              ? 'bg-slate-950 bg-[radial-gradient(ellipse_80%_80%_at_50%_-20%,color-mix(in_oklch,var(--primary)_25%,transparent),rgba(255,255,255,0))]'
              : 'bg-slate-100 bg-[radial-gradient(ellipse_80%_80%_at_50%_-20%,color-mix(in_oklch,var(--primary)_15%,transparent),rgba(255,255,255,0))]'
      }`}>
        {/* Sun / Moon Theme Toggle Button */}
        <button
            type="button"
            onClick={toggleTheme}
            className={`absolute top-5 right-5 p-3 rounded-2xl border transition-all duration-300 shadow-md cursor-pointer flex items-center gap-2 text-xs font-semibold ${
                isDark
                    ? 'bg-slate-900/80 border-slate-800 text-amber-400 hover:bg-slate-800'
                    : 'bg-white/80 border-slate-200 text-primary hover:bg-slate-50'
            }`}
            title={isDark ? 'Chuyển sang Giao diện Sáng (Light Mode)' : 'Chuyển sang Giao diện Tối (Dark Mode)'}
        >
          {isDark ? (
              <>
                <Sun className="w-4 h-4 text-amber-400" />
                <span className="hidden sm:inline text-slate-300">Light Mode</span>
              </>
          ) : (
              <>
                <Moon className="w-4 h-4 text-primary" />
                <span className="hidden sm:inline text-slate-700">Dark Mode</span>
              </>
          )}
        </button>

        {/* Background Glow Accents */}
        <div className={`absolute top-1/4 left-1/2 -translate-x-1/2 -translate-y-1/2 w-[500px] h-[500px] rounded-full blur-3xl pointer-events-none ${
            isDark ? 'bg-primary/15' : 'bg-primary/10'
        }`} />

        {/* Main Glassmorphism Login Container */}
        <div className={`w-full max-w-md backdrop-blur-xl border rounded-3xl p-8 shadow-2xl relative z-10 space-y-6 transition-all duration-300 ${
            isDark
                ? 'bg-slate-900/80 border-slate-800 text-white'
                : 'bg-white/90 border-slate-200/80 text-slate-900 shadow-primary/5'
        }`}>

          {/* Header Branding */}
          <div className="text-center space-y-2">
            <div className="inline-flex items-center justify-center w-14 h-14 rounded-2xl bg-primary/90 text-white shadow-lg shadow-primary/30 mb-2">
              <BookOpen className="w-7 h-7" />
            </div>
            <h1 className={`text-2xl md:text-3xl font-extrabold tracking-tight flex items-center justify-center gap-2 ${
                isDark ? 'text-white' : 'text-slate-900'
            }`}>
              Quản Lý Thư Viện
            </h1>
            <p className={`text-xs md:text-sm ${isDark ? 'text-slate-400' : 'text-slate-500'}`}>
              Hệ thống Quản trị & Vận hành Thư viện Số Enterprise
            </p>
          </div>

          {/* Error Alert Box */}
          {error && (
              <div className={`flex items-center gap-3 p-3.5 rounded-2xl border text-xs font-medium animate-in fade-in duration-200 ${
                  isDark
                      ? 'bg-rose-500/10 border-rose-500/30 text-rose-400'
                      : 'bg-rose-50 border-rose-200 text-rose-600'
              }`}>
                <AlertCircle className="w-4 h-4 shrink-0 text-rose-500" />
                <span>{error}</span>
              </div>
          )}

          {/* Form Inputs */}
          <div className="space-y-4">
            {/* Username Input */}
            <div className="space-y-1.5">
              <label className={`block text-xs font-semibold uppercase tracking-wider ${
                  isDark ? 'text-slate-300' : 'text-slate-700'
              }`}>
                Tên đăng nhập
              </label>
              <div className="relative">
                <div className="absolute inset-y-0 left-0 pl-3.5 flex items-center pointer-events-none text-slate-400">
                  <User className="w-4 h-4" />
                </div>
                <input
                    type="text"
                    value={username}
                    onChange={(e) => {
                      setUsername(e.target.value);
                      setError('');
                    }}
                    onKeyDown={(e) => {
                      if (e.key === 'Enter') {
                        e.preventDefault();
                        passwordInputRef.current?.focus();
                      }
                    }}
                    placeholder="Nhập tên đăng nhập"
                    className={`w-full pl-10 pr-4 py-3 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-primary/20 transition-all ${
                        isDark
                            ? 'bg-slate-950/60 border border-slate-800 text-white placeholder-slate-500 focus:border-primary'
                            : 'bg-slate-50 border border-slate-200 text-slate-900 placeholder-slate-400 focus:border-primary'
                    }`}
                />
              </div>
            </div>

            {/* Password Input */}
            <div className="space-y-1.5">
              <label className={`block text-xs font-semibold uppercase tracking-wider ${
                  isDark ? 'text-slate-300' : 'text-slate-700'
              }`}>
                Mật khẩu
              </label>
              <div className="relative">
                <div className="absolute inset-y-0 left-0 pl-3.5 flex items-center pointer-events-none text-slate-400">
                  <Lock className="w-4 h-4" />
                </div>
                <input
                    ref={passwordInputRef}
                    type={showPassword ? 'text' : 'password'}
                    value={password}
                    onChange={(e) => {
                      setPassword(e.target.value);
                      setError('');
                    }}
                    onKeyDown={(e) => {
                      if (e.key === 'Enter') {
                        e.preventDefault();
                        handleLogin();
                      }
                    }}
                    placeholder="Nhập mật khẩu"
                    className={`w-full pl-10 pr-10 py-3 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-primary/20 transition-all ${
                        isDark
                            ? 'bg-slate-950/60 border border-slate-800 text-white placeholder-slate-500 focus:border-primary'
                            : 'bg-slate-50 border border-slate-200 text-slate-900 placeholder-slate-400 focus:border-primary'
                    }`}
                />
                <button
                    type="button"
                    onClick={() => setShowPassword(!showPassword)}
                    className="absolute inset-y-0 right-0 pr-3.5 flex items-center text-slate-400 hover:text-slate-200 transition-colors cursor-pointer"
                >
                  {showPassword ? <EyeOff className="w-4 h-4" /> : <Eye className="w-4 h-4" />}
                </button>
              </div>
            </div>

            {/* Login Submit Button */}
            <button
                onClick={handleLogin}
                disabled={loading}
                className="w-full py-3.5 px-4 rounded-xl bg-primary/90 hover:bg-primary text-white font-semibold text-sm shadow-lg shadow-primary/30 transition-all duration-200 flex items-center justify-center gap-2 cursor-pointer disabled:opacity-50 disabled:cursor-not-allowed mt-2"
            >
              {loading ? (
                  <div className="w-5 h-5 border-2 border-white/30 border-t-white rounded-full animate-spin" />
              ) : (
                  <>
                    <LogIn className="w-4 h-4" />
                    <span>Đăng Nhập Hệ Thống</span>
                  </>
              )}
            </button>
          </div>
        </div>
      </div>
  );
}

export default LoginPage;