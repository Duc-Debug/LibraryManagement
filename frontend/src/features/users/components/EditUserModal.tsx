'use client';

import { useState } from 'react';
import { Button } from '@/components/ui/button';
import { X, RefreshCw, AlertCircle } from 'lucide-react';
import { updateReaderApi } from '@/api/readerApi';
import { updateLibrarian } from '@/api/librarianApi';
import type { UnifiedUser } from './UserManagementPage';

interface EditUserModalProps {
  user: UnifiedUser;
  onClose: () => void;
  onSuccess: () => void;
}

export function EditUserModal({ user, onClose, onSuccess }: EditUserModalProps) {
  const isReader = user.userType === 'reader';
  
  const [fullName, setFullName] = useState(user.name);
  const [email, setEmail] = useState(user.email);
  const [phone, setPhone] = useState(user.phone);
  const [address, setAddress] = useState(user.address || '');

  const [saving, setSaving] = useState(false);
  const [formError, setFormError] = useState('');

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setFormError('');

    if (!fullName.trim()) {
      setFormError('Họ và tên không được để trống.');
      return;
    }

    setSaving(true);
    const token = typeof window !== 'undefined' ? localStorage.getItem('accessToken') || '' : '';

    try {
      if (isReader && user.originalReaderData) {
        await updateReaderApi(
          user.originalReaderData.id,
          {
            name: fullName.trim(),
            email: email.trim(),
            phoneNumber: phone.trim(),
            address: address.trim(),
          },
          token
        );
      } else if (!isReader && user.originalLibrarianData) {
        await updateLibrarian(user.originalLibrarianData.id, {
          fullName: fullName.trim(),
          email: email.trim() || undefined,
          phone: phone.trim() || undefined,
          enabled: user.originalLibrarianData.enabled,
        });
      }

      onSuccess();
    } catch (err: any) {
      setFormError(err?.message || 'Cập nhật thông tin người dùng thất bại.');
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
            <h2 className="text-lg font-bold text-foreground">Chỉnh Sửa Thông Tin Người Dùng</h2>
            <p className="text-xs text-muted-foreground mt-0.5">
              Cập nhật thông tin cho {isReader ? 'Độc giả' : 'Thủ thư'} #{user.id}
            </p>
          </div>
          <button
            onClick={onClose}
            className="text-muted-foreground hover:text-foreground p-1.5 rounded-lg hover:bg-muted transition cursor-pointer"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {formError && (
          <div className="p-3 bg-destructive/10 text-destructive text-xs rounded-xl border border-destructive/20 flex items-center gap-2">
            <AlertCircle className="w-4 h-4 shrink-0" />
            <span>{formError}</span>
          </div>
        )}

        <form onSubmit={handleSubmit} className="space-y-3.5">
          {/* Readonly Type Info */}
          <div className="grid grid-cols-2 gap-3 p-3 rounded-xl bg-muted/40 border border-border/50 text-xs">
            <div>
              <span className="text-muted-foreground block">Loại tài khoản:</span>
              <span className="font-bold text-foreground">{user.roleTitle}</span>
            </div>
            <div>
              <span className="text-muted-foreground block">{isReader ? 'Mã thẻ:' : 'Username:'}</span>
              <span className="font-mono font-bold text-primary">{isReader ? user.cardNumber : user.username}</span>
            </div>
          </div>

          <div>
            <label className="block text-xs font-semibold uppercase tracking-wider text-muted-foreground mb-1">
              Họ và Tên <span className="text-destructive">*</span>
            </label>
            <input
              type="text"
              value={fullName}
              onChange={(e) => setFullName(e.target.value)}
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
                className="w-full px-3.5 py-2 border border-border rounded-xl bg-background text-foreground text-sm focus:outline-none focus:ring-2 focus:ring-primary transition"
              />
            </div>

            <div>
              <label className="block text-xs font-semibold uppercase tracking-wider text-muted-foreground mb-1">Số điện thoại</label>
              <input
                type="text"
                value={phone}
                onChange={(e) => setPhone(e.target.value)}
                className="w-full px-3.5 py-2 border border-border rounded-xl bg-background text-foreground text-sm focus:outline-none focus:ring-2 focus:ring-primary transition"
              />
            </div>
          </div>

          {isReader && (
            <div>
              <label className="block text-xs font-semibold uppercase tracking-wider text-muted-foreground mb-1">Địa chỉ</label>
              <input
                type="text"
                value={address}
                onChange={(e) => setAddress(e.target.value)}
                className="w-full px-3.5 py-2 border border-border rounded-xl bg-background text-foreground text-sm focus:outline-none focus:ring-2 focus:ring-primary transition"
              />
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
              <span>{saving ? 'Đang lưu...' : 'Lưu Thay Đổi'}</span>
            </Button>
          </div>
        </form>
      </div>
    </div>
  );
}
