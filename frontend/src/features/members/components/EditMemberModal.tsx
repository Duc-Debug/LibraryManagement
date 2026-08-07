'use client';

import { useState } from 'react';
import { X, RefreshCw } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { updateReaderApi, type ReaderResponse } from '@/api/readerApi';

interface EditMemberModalProps {
  member: ReaderResponse;
  onClose: () => void;
  onSuccess: () => void;
}

export function EditMemberModal({ member, onClose, onSuccess }: EditMemberModalProps) {
  const [form, setForm] = useState({
    name: member.name,
    email: member.email,
    phone: member.phoneNumber,
    address: member.address,
  });

  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!form.name.trim() || !form.email.trim() || !form.phone.trim()) {
      setError('Vui lòng điền đầy đủ Họ tên, Email và Số điện thoại.');
      return;
    }

    setSaving(true);
    setError('');
    const token = typeof window !== 'undefined' ? localStorage.getItem('accessToken') || '' : '';

    try {
      await updateReaderApi(
        member.id,
        {
          name: form.name.trim(),
          email: form.email.trim(),
          phoneNumber: form.phone.trim(),
          address: form.address.trim() || 'Chưa cập nhật',
        },
        token
      );
      onSuccess();
    } catch (err: any) {
      setError(err?.message || 'Cập nhật thông tin độc giả thất bại.');
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="fixed inset-0 bg-black/60 backdrop-blur-xs flex items-center justify-center z-50 p-4 animate-in fade-in duration-150">
      <div className="bg-card rounded-2xl border border-border w-full max-w-md shadow-2xl overflow-hidden animate-in zoom-in-95 duration-150 text-foreground">
        {/* Header */}
        <div className="flex items-center justify-between p-6 border-b border-border">
          <div>
            <h2 className="text-lg font-bold text-foreground">Sửa Thông Tin Độc Giả</h2>
            <p className="text-xs text-muted-foreground mt-0.5">Cập nhật thông tin chi tiết của bạn đọc</p>
          </div>
          <button
            onClick={onClose}
            className="text-muted-foreground hover:text-foreground p-1.5 rounded-lg hover:bg-muted transition"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Form */}
        <form onSubmit={handleSubmit} className="p-6 space-y-4">
          {error && (
            <div className="px-3.5 py-2.5 bg-destructive/10 text-destructive text-xs rounded-xl border border-destructive/20 leading-relaxed">
              ⚠️ {error}
            </div>
          )}

          <div>
            <label className="block text-xs font-semibold text-muted-foreground uppercase tracking-wider mb-1">Mã Thẻ (Không thể sửa)</label>
            <input
              type="text"
              value={member.cardNumber}
              disabled
              className="w-full px-3.5 py-2 border border-border rounded-xl bg-muted text-muted-foreground font-mono text-sm cursor-not-allowed"
            />
          </div>

          <div>
            <label className="block text-xs font-semibold text-muted-foreground uppercase tracking-wider mb-1">Họ và tên *</label>
            <input
              type="text"
              value={form.name}
              onChange={(e) => setForm({ ...form, name: e.target.value })}
              placeholder="Nhập họ và tên"
              required
              className="w-full px-3.5 py-2 border border-border rounded-xl bg-background text-foreground placeholder-muted-foreground focus:outline-none focus:ring-2 focus:ring-primary transition text-sm"
            />
          </div>

          <div>
            <label className="block text-xs font-semibold text-muted-foreground uppercase tracking-wider mb-1">Email *</label>
            <input
              type="email"
              value={form.email}
              onChange={(e) => setForm({ ...form, email: e.target.value })}
              placeholder="nguyenvana@gmail.com"
              required
              className="w-full px-3.5 py-2 border border-border rounded-xl bg-background text-foreground placeholder-muted-foreground focus:outline-none focus:ring-2 focus:ring-primary transition text-sm"
            />
          </div>

          <div>
            <label className="block text-xs font-semibold text-muted-foreground uppercase tracking-wider mb-1">Số điện thoại *</label>
            <input
              type="text"
              value={form.phone}
              onChange={(e) => setForm({ ...form, phone: e.target.value })}
              placeholder="0912345678"
              required
              className="w-full px-3.5 py-2 border border-border rounded-xl bg-background text-foreground placeholder-muted-foreground focus:outline-none focus:ring-2 focus:ring-primary transition text-sm"
            />
          </div>

          <div>
            <label className="block text-xs font-semibold text-muted-foreground uppercase tracking-wider mb-1">Địa chỉ</label>
            <input
              type="text"
              value={form.address}
              onChange={(e) => setForm({ ...form, address: e.target.value })}
              placeholder="Hà Nội, Việt Nam"
              className="w-full px-3.5 py-2 border border-border rounded-xl bg-background text-foreground placeholder-muted-foreground focus:outline-none focus:ring-2 focus:ring-primary transition text-sm"
            />
          </div>

          {/* Buttons */}
          <div className="flex gap-3 pt-4 border-t border-border">
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
              className="flex-1 bg-primary hover:bg-primary/90 text-primary-foreground flex items-center justify-center gap-2 rounded-xl text-xs py-2"
            >
              {saving && <RefreshCw className="w-4.5 h-4.5 animate-spin" />}
              <span>{saving ? 'Đang cập nhật...' : 'Cập nhật'}</span>
            </Button>
          </div>
        </form>
      </div>
    </div>
  );
}
