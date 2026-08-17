'use client';

import { useState } from 'react';
import { Button } from '@/components/ui/button';
import { X, RefreshCw, UserCheck, ShieldCheck, Eye, EyeOff, AlertCircle } from 'lucide-react';
import { createReader } from '@/api/readerApi';
import { createLibrarian } from '@/api/librarianApi';
import { parseErrorMessage } from '@/lib/errorDictionary';

interface AddUserModalProps {
  isAdmin?: boolean;
  onClose: () => void;
  onSuccess: () => void;
}

export function AddUserModal({ isAdmin = true, onClose, onSuccess }: AddUserModalProps) {
  const [userType, setUserType] = useState<'reader' | 'librarian'>('reader');
  
  const [fullName, setFullName] = useState('');
  const [email, setEmail] = useState('');
  const [phone, setPhone] = useState('');
  
  // Reader specific field
  const [address, setAddress] = useState('');
  
  // Librarian specific fields
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);

  const [saving, setSaving] = useState(false);
  const [formError, setFormError] = useState('');

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setFormError('');

    if (!fullName.trim()) {
      setFormError('Vui lòng nhập Họ và tên.');
      return;
    }

    if (userType === 'librarian') {
      if (!username.trim()) {
        setFormError('Vui lòng nhập Tên đăng nhập.');
        return;
      }
      if (!password.trim() || password.trim().length < 6) {
        setFormError('Mật khẩu phải có ít nhất 6 ký tự.');
        return;
      }
    }

    setSaving(true);
    const token = typeof window !== 'undefined' ? localStorage.getItem('accessToken') || '' : '';

    try {
      if (userType === 'reader') {
        await createReader(
          {
            name: fullName.trim(),
            email: email.trim() || '',
            phoneNumber: phone.trim() || '',
            address: address.trim() || 'Chưa cập nhật',
          },
          token
        );
      } else {
        await createLibrarian({
          username: username.trim(),
          password: password.trim(),
          fullName: fullName.trim(),
          email: email.trim() || undefined,
          phone: phone.trim() || undefined,
        });
      }

      onSuccess();
    } catch (err: any) {
      setFormError(parseErrorMessage(err, 'Không thể tạo người dùng mới. Vui lòng kiểm tra lại.'));
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="fixed inset-0 bg-black/60 backdrop-blur-xs flex items-center justify-center z-50 p-4 animate-in fade-in duration-150">
      <div className="bg-card rounded-2xl border border-border w-full max-w-md shadow-2xl p-6 overflow-hidden space-y-4 animate-in zoom-in-95 duration-150 text-foreground">
        {/* Header */}
        <div className="flex items-center justify-between pb-3 border-b border-border">
          <div>
            <h2 className="text-lg font-bold text-foreground">Thêm Người Dùng Mới</h2>
            <p className="text-xs text-muted-foreground mt-0.5">Cấp thẻ Độc giả mới hoặc Tạo tài khoản Thủ thư hệ thống</p>
          </div>
          <button
            onClick={onClose}
            className="text-muted-foreground hover:text-foreground p-1.5 rounded-lg hover:bg-muted transition cursor-pointer"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* User Type Switcher */}
        <div className="flex items-center p-1 bg-muted/60 rounded-xl border border-border/60 text-xs">
          <button
            type="button"
            onClick={() => setUserType('reader')}
            className={`flex-1 flex items-center justify-center gap-2 py-2 rounded-lg font-semibold transition-all cursor-pointer ${
              userType === 'reader'
                ? 'bg-card text-foreground shadow-sm'
                : 'text-muted-foreground hover:text-foreground'
            }`}
          >
            <UserCheck className="w-4 h-4 text-emerald-600 dark:text-emerald-400" />
            <span>🪪 Độc Giả (Thẻ Thư Viện)</span>
          </button>

          {isAdmin && (
            <button
              type="button"
              onClick={() => setUserType('librarian')}
              className={`flex-1 flex items-center justify-center gap-2 py-2 rounded-lg font-semibold transition-all cursor-pointer ${
                userType === 'librarian'
                  ? 'bg-card text-foreground shadow-sm'
                  : 'text-muted-foreground hover:text-foreground'
              }`}
            >
              <ShieldCheck className="w-4 h-4 text-purple-600 dark:text-purple-400" />
              <span>🛡️ Thủ Thư / Admin</span>
            </button>
          )}
        </div>

        {formError && (
          <div className="p-3 bg-destructive/10 text-destructive text-xs rounded-xl border border-destructive/20 flex items-center gap-2">
            <AlertCircle className="w-4 h-4 shrink-0" />
            <span>{formError}</span>
          </div>
        )}

        <form onSubmit={handleSubmit} className="space-y-3.5">
          {/* SHARED FIELDS */}
          <div>
            <label className="block text-xs font-semibold uppercase tracking-wider text-muted-foreground mb-1">
              Họ và Tên <span className="text-destructive">*</span>
            </label>
            <input
              type="text"
              value={fullName}
              onChange={(e) => setFullName(e.target.value)}
              placeholder="Nhập họ và tên người dùng"
              required
              className="w-full px-3.5 py-2 border border-border rounded-xl bg-background text-foreground text-sm focus:outline-none focus:ring-2 focus:ring-primary transition"
            />
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
            <div>
              <label className="block text-xs font-semibold uppercase tracking-wider text-muted-foreground mb-1">Email</label>
              <input
                type="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                placeholder="user@library.com"
                className="w-full px-3.5 py-2 border border-border rounded-xl bg-background text-foreground text-sm focus:outline-none focus:ring-2 focus:ring-primary transition"
              />
            </div>

            <div>
              <label className="block text-xs font-semibold uppercase tracking-wider text-muted-foreground mb-1">Số điện thoại</label>
              <input
                type="text"
                value={phone}
                onChange={(e) => setPhone(e.target.value)}
                placeholder="0912345678"
                className="w-full px-3.5 py-2 border border-border rounded-xl bg-background text-foreground text-sm focus:outline-none focus:ring-2 focus:ring-primary transition"
              />
            </div>
          </div>

          {/* DYNAMIC FIELDS: READER SPECIFIC */}
          {userType === 'reader' && (
            <div>
              <label className="block text-xs font-semibold uppercase tracking-wider text-muted-foreground mb-1">Địa chỉ</label>
              <input
                type="text"
                value={address}
                onChange={(e) => setAddress(e.target.value)}
                placeholder="Nhập địa chỉ của độc giả (Hà Nội, TP.HCM...)"
                className="w-full px-3.5 py-2 border border-border rounded-xl bg-background text-foreground text-sm focus:outline-none focus:ring-2 focus:ring-primary transition"
              />
              <p className="text-[11px] text-muted-foreground mt-1">
                * Mã thẻ độc giả (`RD-xxxx`) và Hạn thẻ 1 năm sẽ được tự động phát hành.
              </p>
            </div>
          )}

          {/* DYNAMIC FIELDS: LIBRARIAN SPECIFIC */}
          {userType === 'librarian' && (
            <div className="space-y-3 pt-1 border-t border-border/60">
              <div>
                <label className="block text-xs font-semibold uppercase tracking-wider text-muted-foreground mb-1">
                  Tên đăng nhập (Username) <span className="text-destructive">*</span>
                </label>
                <input
                  type="text"
                  value={username}
                  onChange={(e) => setUsername(e.target.value)}
                  placeholder="Nhập username hệ thống"
                  required
                  className="w-full px-3.5 py-2 border border-border rounded-xl bg-background text-foreground text-sm font-mono focus:outline-none focus:ring-2 focus:ring-primary transition"
                />
              </div>

              <div>
                <label className="block text-xs font-semibold uppercase tracking-wider text-muted-foreground mb-1">
                  Mật khẩu khởi tạo <span className="text-destructive">*</span>
                </label>
                <div className="relative">
                  <input
                    type={showPassword ? 'text' : 'password'}
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    placeholder="Tối thiểu 6 ký tự"
                    required
                    className="w-full px-3.5 py-2 pr-10 border border-border rounded-xl bg-background text-foreground text-sm focus:outline-none focus:ring-2 focus:ring-primary transition"
                  />
                  <button
                    type="button"
                    onClick={() => setShowPassword(!showPassword)}
                    className="absolute right-3 top-1/2 -translate-y-1/2 text-muted-foreground hover:text-foreground cursor-pointer"
                  >
                    {showPassword ? <EyeOff className="w-4 h-4" /> : <Eye className="w-4 h-4" />}
                  </button>
                </div>
              </div>
            </div>
          )}

          {/* ACTIONS */}
          <div className="flex gap-3 pt-3 border-t border-border">
            <Button
              type="button"
              variant="outline"
              onClick={onClose}
              disabled={saving}
              className="flex-1 rounded-xl text-xs py-2"
            >
              Hủy
            </Button>
            <Button
              type="submit"
              disabled={saving}
              className="flex-1 bg-primary hover:bg-primary/90 text-primary-foreground font-semibold flex items-center justify-center gap-2 rounded-xl text-xs py-2 shadow-md cursor-pointer"
            >
              {saving && <RefreshCw className="w-4 h-4 animate-spin" />}
              <span>{saving ? 'Đang xử lý...' : userType === 'reader' ? 'Cấp Thẻ Độc Giả' : 'Tạo Tài Khoản Thủ Thư'}</span>
            </Button>
          </div>
        </form>
      </div>
    </div>
  );
}
