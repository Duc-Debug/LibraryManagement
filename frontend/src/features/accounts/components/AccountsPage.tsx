'use client';

import { useState } from 'react';
import type { UserAccount, UserRole } from '../types/account.types';
import { Plus, Edit2, Lock, Unlock } from 'lucide-react';

interface AccountsPageProps {
  accounts: UserAccount[];
  setAccounts: (accounts: UserAccount[]) => void;
  currentUserId: string;
}

type ModalMode = 'add' | 'edit';

interface FormState {
  username: string;
  password: string;
  fullName: string;
  role: UserRole;
}

const ROLE_LABEL: Record<UserRole, string> = {
  thu_thu: 'Thủ thư',
  nguoi_dung: 'Người dùng',
};

const ROLE_BADGE: Record<UserRole, string> = {
  thu_thu: 'bg-purple-100 text-purple-700',
  nguoi_dung: 'bg-blue-100 text-blue-700',
};

export default function AccountsPage({
  accounts,
  setAccounts,
  currentUserId,
}: AccountsPageProps) {
  const [showModal, setShowModal] = useState(false);
  const [modalMode, setModalMode] = useState<ModalMode>('add');
  const [editingId, setEditingId] = useState<string | null>(null);
  const [form, setForm] = useState<FormState>({
    username: '',
    password: '',
    fullName: '',
    role: 'nguoi_dung',
  });
  const [formError, setFormError] = useState('');
  const [showPassword, setShowPassword] = useState(false);

  const openAdd = () => {
    setModalMode('add');
    setEditingId(null);
    setForm({ username: '', password: '', fullName: '', role: 'nguoi_dung' });
    setFormError('');
    setShowPassword(false);
    setShowModal(true);
  };

  const openEdit = (acc: UserAccount) => {
    setModalMode('edit');
    setEditingId(acc.id);
    setForm({
      username: acc.username,
      password: acc.password,
      fullName: acc.fullName,
      role: acc.role,
    });
    setFormError('');
    setShowPassword(false);
    setShowModal(true);
  };

  const handleSave = () => {
    if (!form.username.trim() || !form.fullName.trim()) {
      setFormError('Tên đăng nhập và họ tên không được để trống.');
      return;
    }

    if (modalMode === 'add' && !form.password.trim()) {
      setFormError('Mật khẩu không được để trống.');
      return;
    }

    const duplicateUsername = accounts.find(
      (a) => a.username === form.username.trim() && a.id !== editingId
    );

    if (duplicateUsername) {
      setFormError('Tên đăng nhập đã tồn tại.');
      return;
    }

    if (modalMode === 'add') {
      const newAccount: UserAccount = {
        id: `U${String(accounts.length + 1).padStart(3, '0')}`,
        username: form.username.trim(),
        password: form.password,
        fullName: form.fullName.trim(),
        role: form.role,
        active: true,
      };
      setAccounts([...accounts, newAccount]);
    } else {
      setAccounts(
        accounts.map((a) =>
          a.id === editingId
            ? {
                ...a,
                username: form.username.trim(),
                password: form.password || a.password,
                fullName: form.fullName.trim(),
                role: form.role,
              }
            : a
        )
      );
    }
    setShowModal(false);
  };

  const toggleLock = (id: string) => {
    if (id === currentUserId) return;
    setAccounts(
      accounts.map((a) =>
        a.id === id ? { ...a, active: !a.active } : a
      )
    );
  };

  return (
    <div className="p-8">
      <div className="flex items-start justify-between mb-6">
        <div>
          <h1 className="text-2xl font-bold text-foreground">Quản lý Tài khoản</h1>
          <p className="text-sm text-muted-foreground mt-0.5">
            Kiểm soát quyền truy cập vào hệ thống
          </p>
        </div>
        <button
          onClick={openAdd}
          className="flex items-center gap-2 px-4 py-2 rounded-xl text-sm font-semibold text-primary-foreground bg-primary hover:opacity-90 transition-opacity"
        >
          <Plus size={18} />
          <span>Thêm tài khoản</span>
        </button>
      </div>

      <div className="bg-card rounded-2xl shadow-sm overflow-hidden border border-border">
        <table className="w-full text-sm">
          <thead>
            <tr className="border-b border-border">
              {['ID', 'Họ và tên', 'Tên đăng nhập', 'Vai trò', 'Trạng thái', 'Hành động'].map(
                (h) => (
                  <th
                    key={h}
                    className="px-6 py-3 text-left text-xs font-semibold text-foreground"
                  >
                    {h}
                  </th>
                )
              )}
            </tr>
          </thead>
          <tbody>
            {accounts.map((acc) => {
              const isSelf = acc.id === currentUserId;
              return (
                <tr key={acc.id} className="border-b border-border last:border-0 hover:bg-muted/50">
                  <td className="px-6 py-4 text-muted-foreground text-xs font-mono">
                    {acc.id}
                  </td>
                  <td className="px-6 py-4 font-semibold text-foreground">
                    {acc.fullName}
                    {isSelf && (
                      <span className="ml-2 text-xs text-muted-foreground font-normal">
                        (bạn)
                      </span>
                    )}
                  </td>
                  <td className="px-6 py-4 text-muted-foreground font-mono text-xs">
                    {acc.username}
                  </td>
                  <td className="px-6 py-4">
                    <span
                      className={`inline-block px-2.5 py-0.5 rounded-full text-xs font-medium ${
                        ROLE_BADGE[acc.role]
                      }`}
                    >
                      {ROLE_LABEL[acc.role]}
                    </span>
                  </td>
                  <td className="px-6 py-4">
                    <span
                      className={`inline-block px-3 py-0.5 rounded-full text-xs font-medium ${
                        acc.active
                          ? 'bg-green-100 text-green-700'
                          : 'bg-red-100 text-red-600'
                      }`}
                    >
                      {acc.active ? 'Hoạt động' : 'Đã khóa'}
                    </span>
                  </td>
                  <td className="px-6 py-4">
                    <div className="flex items-center gap-2">
                      <button
                        onClick={() => openEdit(acc)}
                        className="px-3 py-1.5 rounded-lg text-xs font-semibold border border-border text-foreground hover:bg-muted transition-colors flex items-center gap-1"
                      >
                        <Edit2 size={14} />
                        Sửa
                      </button>
                      <button
                        onClick={() => toggleLock(acc.id)}
                        disabled={isSelf}
                        className={`px-3 py-1.5 rounded-lg text-xs font-semibold transition-colors flex items-center gap-1 ${
                          isSelf
                            ? 'opacity-30 cursor-not-allowed border border-border text-muted-foreground'
                            : acc.active
                            ? 'bg-red-50 text-red-600 hover:bg-red-100 border border-red-200'
                            : 'bg-green-50 text-green-700 hover:bg-green-100 border border-green-200'
                        }`}
                      >
                        {acc.active ? (
                          <>
                            <Lock size={14} />
                            Khóa
                          </>
                        ) : (
                          <>
                            <Unlock size={14} />
                            Mở khóa
                          </>
                        )}
                      </button>
                    </div>
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>
      </div>

      {showModal && (
        <div className="fixed inset-0 bg-black/20 flex items-center justify-center z-50">
          <div className="bg-card rounded-2xl p-6 w-full max-w-md shadow-xl border border-border">
            <h2 className="font-bold text-lg text-foreground mb-4">
              {modalMode === 'add' ? 'Thêm tài khoản mới' : 'Chỉnh sửa tài khoản'}
            </h2>

            {formError && (
              <div className="mb-4 px-3 py-2 bg-red-50 text-red-600 text-sm rounded-xl border border-red-200">
                {formError}
              </div>
            )}

            <div className="space-y-3">
              <div>
                <label className="block text-sm font-medium text-foreground mb-1">
                  Họ và tên
                </label>
                <input
                  value={form.fullName}
                  onChange={(e) => setForm({ ...form, fullName: e.target.value })}
                  placeholder="Nhập họ và tên"
                  className="w-full border border-border rounded-xl px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-primary/20 bg-background"
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-foreground mb-1">
                  Tên đăng nhập
                </label>
                <input
                  value={form.username}
                  onChange={(e) => setForm({ ...form, username: e.target.value })}
                  placeholder="Nhập tên đăng nhập"
                  className="w-full border border-border rounded-xl px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-primary/20 bg-background"
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-foreground mb-1">
                  {modalMode === 'edit'
                    ? 'Mật khẩu mới (để trống = giữ nguyên)'
                    : 'Mật khẩu'}
                </label>
                <div className="relative">
                  <input
                    type={showPassword ? 'text' : 'password'}
                    value={form.password}
                    onChange={(e) => setForm({ ...form, password: e.target.value })}
                    placeholder={
                      modalMode === 'edit'
                        ? 'Để trống nếu không đổi'
                        : 'Nhập mật khẩu'
                    }
                    className="w-full border border-border rounded-xl px-3 py-2 pr-16 text-sm focus:outline-none focus:ring-2 focus:ring-primary/20 bg-background"
                  />
                  <button
                    type="button"
                    onClick={() => setShowPassword((v) => !v)}
                    className="absolute right-3 top-1/2 -translate-y-1/2 text-xs text-muted-foreground hover:text-foreground"
                  >
                    {showPassword ? 'Ẩn' : 'Hiện'}
                  </button>
                </div>
              </div>

              <div>
                <label className="block text-sm font-medium text-foreground mb-1">
                  Vai trò
                </label>
                <select
                  value={form.role}
                  onChange={(e) => setForm({ ...form, role: e.target.value as UserRole })}
                  className="w-full border border-border rounded-xl px-3 py-2 text-sm bg-background focus:outline-none focus:ring-2 focus:ring-primary/20"
                >
                  <option value="thu_thu">Thủ thư</option>
                  <option value="nguoi_dung">Người dùng</option>
                </select>
              </div>
            </div>

            <div className="flex gap-3 mt-5">
              <button
                onClick={() => setShowModal(false)}
                className="flex-1 py-2 rounded-xl border border-border text-sm font-medium text-foreground hover:bg-muted transition-colors"
              >
                Hủy
              </button>
              <button
                onClick={handleSave}
                className="flex-1 py-2 rounded-xl text-sm font-semibold text-primary-foreground bg-primary hover:opacity-90 transition-opacity"
              >
                {modalMode === 'add' ? 'Thêm tài khoản' : 'Lưu thay đổi'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
