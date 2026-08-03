'use client';

import { useState } from 'react';
import { X, Eye, EyeOff } from 'lucide-react';
import { Button } from '@/components/ui/button';
import type { UserAccount } from '@/features/accounts';

interface EditProfileModalProps {
  user: UserAccount;
  onClose: () => void;
  onSave: (updatedUser: Partial<UserAccount>) => void;
}

export function EditProfileModal({
  user,
  onClose,
  onSave,
}: EditProfileModalProps) {
  const [fullName, setFullName] = useState(user.fullName);
  const [currentPassword, setCurrentPassword] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [showPasswords, setShowPasswords] = useState({
    current: false,
    new: false,
    confirm: false,
  });
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  const handleSave = () => {
    setError('');
    setSuccess('');

    // Validate full name
    if (!fullName.trim()) {
      setError('Tên không được để trống');
      return;
    }

    const updates: Partial<UserAccount> = { fullName };

    // Validate password change
    if (currentPassword || newPassword || confirmPassword) {
      if (!currentPassword) {
        setError('Vui lòng nhập mật khẩu hiện tại');
        return;
      }

      if (currentPassword !== user.password) {
        setError('Mật khẩu hiện tại không đúng');
        return;
      }

      if (!newPassword) {
        setError('Vui lòng nhập mật khẩu mới');
        return;
      }

      if (newPassword.length < 6) {
        setError('Mật khẩu mới phải có ít nhất 6 ký tự');
        return;
      }

      if (newPassword !== confirmPassword) {
        setError('Xác nhận mật khẩu không khớp');
        return;
      }

      updates.password = newPassword;
    }

    // All validations passed
    onSave(updates);
    setSuccess('Cập nhật thông tin thành công!');
    setTimeout(() => {
      onClose();
    }, 1500);
  };

  return (
    <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
      <div className="bg-card rounded-lg shadow-lg max-w-md w-full max-h-[90vh] overflow-y-auto">
        {/* Header */}
        <div className="flex items-center justify-between p-6 border-b border-border sticky top-0 bg-card">
          <h2 className="text-xl font-bold text-foreground">Chỉnh sửa thông tin</h2>
          <button
            onClick={onClose}
            className="text-foreground/60 hover:text-foreground transition-colors"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Content */}
        <div className="p-6 space-y-6">
          {/* Full Name */}
          <div>
            <label className="block text-sm font-medium text-foreground mb-2">
              Họ và tên
            </label>
            <input
              type="text"
              value={fullName}
              onChange={(e) => setFullName(e.target.value)}
              className="w-full px-3 py-2 border border-border rounded-lg bg-background text-foreground focus:outline-none focus:ring-2 focus:ring-primary/50"
              placeholder="Nhập họ và tên"
            />
          </div>

          {/* Password Change Section */}
          <div className="border-t border-border pt-6">
            <h3 className="text-sm font-semibold text-foreground mb-4">
              Đổi mật khẩu
            </h3>

            {/* Current Password */}
            <div className="mb-4">
              <label className="block text-sm font-medium text-foreground mb-2">
                Mật khẩu hiện tại
              </label>
              <div className="relative">
                <input
                  type={showPasswords.current ? 'text' : 'password'}
                  value={currentPassword}
                  onChange={(e) => setCurrentPassword(e.target.value)}
                  className="w-full px-3 py-2 pr-10 border border-border rounded-lg bg-background text-foreground focus:outline-none focus:ring-2 focus:ring-primary/50"
                  placeholder="Nhập mật khẩu hiện tại"
                />
                <button
                  type="button"
                  onClick={() =>
                    setShowPasswords((prev) => ({
                      ...prev,
                      current: !prev.current,
                    }))
                  }
                  className="absolute right-3 top-1/2 -translate-y-1/2 text-foreground/60 hover:text-foreground transition-colors"
                >
                  {showPasswords.current ? (
                    <EyeOff className="w-4 h-4" />
                  ) : (
                    <Eye className="w-4 h-4" />
                  )}
                </button>
              </div>
            </div>

            {/* New Password */}
            <div className="mb-4">
              <label className="block text-sm font-medium text-foreground mb-2">
                Mật khẩu mới
              </label>
              <div className="relative">
                <input
                  type={showPasswords.new ? 'text' : 'password'}
                  value={newPassword}
                  onChange={(e) => setNewPassword(e.target.value)}
                  className="w-full px-3 py-2 pr-10 border border-border rounded-lg bg-background text-foreground focus:outline-none focus:ring-2 focus:ring-primary/50"
                  placeholder="Nhập mật khẩu mới"
                />
                <button
                  type="button"
                  onClick={() =>
                    setShowPasswords((prev) => ({
                      ...prev,
                      new: !prev.new,
                    }))
                  }
                  className="absolute right-3 top-1/2 -translate-y-1/2 text-foreground/60 hover:text-foreground transition-colors"
                >
                  {showPasswords.new ? (
                    <EyeOff className="w-4 h-4" />
                  ) : (
                    <Eye className="w-4 h-4" />
                  )}
                </button>
              </div>
            </div>

            {/* Confirm Password */}
            <div className="mb-4">
              <label className="block text-sm font-medium text-foreground mb-2">
                Xác nhận mật khẩu mới
              </label>
              <div className="relative">
                <input
                  type={showPasswords.confirm ? 'text' : 'password'}
                  value={confirmPassword}
                  onChange={(e) => setConfirmPassword(e.target.value)}
                  className="w-full px-3 py-2 pr-10 border border-border rounded-lg bg-background text-foreground focus:outline-none focus:ring-2 focus:ring-primary/50"
                  placeholder="Xác nhận mật khẩu mới"
                />
                <button
                  type="button"
                  onClick={() =>
                    setShowPasswords((prev) => ({
                      ...prev,
                      confirm: !prev.confirm,
                    }))
                  }
                  className="absolute right-3 top-1/2 -translate-y-1/2 text-foreground/60 hover:text-foreground transition-colors"
                >
                  {showPasswords.confirm ? (
                    <EyeOff className="w-4 h-4" />
                  ) : (
                    <Eye className="w-4 h-4" />
                  )}
                </button>
              </div>
            </div>
          </div>

          {/* Error Message */}
          {error && (
            <div className="p-3 bg-destructive/10 border border-destructive/30 rounded-lg">
              <p className="text-sm text-destructive">{error}</p>
            </div>
          )}

          {/* Success Message */}
          {success && (
            <div className="p-3 bg-accent/10 border border-accent/30 rounded-lg">
              <p className="text-sm text-accent">{success}</p>
            </div>
          )}

          {/* Buttons */}
          <div className="flex gap-3 pt-4 border-t border-border">
            <Button
              variant="outline"
              onClick={onClose}
              className="flex-1"
            >
              Hủy
            </Button>
            <Button
              onClick={handleSave}
              className="flex-1"
            >
              Lưu thay đổi
            </Button>
          </div>
        </div>
      </div>
    </div>
  );
}
