'use client';

import { useState, useEffect } from 'react';
import { Plus, Edit2, Lock, Unlock, RefreshCw } from 'lucide-react';
import {
  Librarian,
  fetchAllLibrarians,
  createLibrarian,
  updateLibrarian,
  deleteLibrarian,
} from '@/api/librarianApi';

import type { UserAccount } from '../types/account.types';

interface AccountsPageProps {
  accounts?: UserAccount[];
  setAccounts?: (accounts: UserAccount[]) => void;
  currentUserId?: string | number;
}

type ModalMode = 'add' | 'edit';

interface FormState {
  username: string;
  password: string;
  fullName: string;
  email: string;
  phone: string;
}

export default function AccountsPage({ currentUserId }: AccountsPageProps) {
  const [librarians, setLibrarians] = useState<Librarian[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [saving, setSaving] = useState<boolean>(false);
  const [togglingId, setTogglingId] = useState<number | null>(null);
  const [pageError, setPageError] = useState<string>('');

  const [showModal, setShowModal] = useState(false);
  const [modalMode, setModalMode] = useState<ModalMode>('add');
  const [editingItem, setEditingItem] = useState<Librarian | null>(null);

  const [form, setForm] = useState<FormState>({
    username: '',
    password: '',
    fullName: '',
    email: '',
    phone: '',
  });
  const [formError, setFormError] = useState('');
  const [showPassword, setShowPassword] = useState(false);

  const loadData = async () => {
    setLoading(true);
    setPageError('');
    try {
      const data = await fetchAllLibrarians();
      setLibrarians(data);
    } catch (err: any) {
      setPageError(err?.message || 'Không thể tải danh sách thủ thư từ máy chủ.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  const openAdd = () => {
    setModalMode('add');
    setEditingItem(null);
    setForm({ username: '', password: '', fullName: '', email: '', phone: '' });
    setFormError('');
    setShowPassword(false);
    setShowModal(true);
  };

  const openEdit = (item: Librarian) => {
    setModalMode('edit');
    setEditingItem(item);
    setForm({
      username: item.username,
      password: '',
      fullName: item.fullName,
      email: item.email || '',
      phone: item.phone || '',
    });
    setFormError('');
    setShowPassword(false);
    setShowModal(true);
  };

  const handleSave = async () => {
    if (!form.username.trim() || !form.fullName.trim()) {
      setFormError('Tên đăng nhập và họ tên không được để trống.');
      return;
    }

    if (modalMode === 'add') {
      if (!form.password.trim()) {
        setFormError('Mật khẩu không được để trống.');
        return;
      }
      if (form.password.trim().length < 6) {
        setFormError('Mật khẩu phải có ít nhất 6 ký tự.');
        return;
      }
    }

    setSaving(true);
    setFormError('');

    try {
      if (modalMode === 'add') {
        await createLibrarian({
          username: form.username.trim(),
          password: form.password.trim(),
          fullName: form.fullName.trim(),
          email: form.email.trim() || undefined,
          phone: form.phone.trim() || undefined,
        });
      } else if (editingItem) {
        await updateLibrarian(editingItem.id, {
          fullName: form.fullName.trim(),
          email: form.email.trim() || undefined,
          phone: form.phone.trim() || undefined,
          enabled: editingItem.enabled,
        });
      }
      setShowModal(false);
      await loadData();
    } catch (err: any) {
      setFormError(err?.message || 'Có lỗi xảy ra khi lưu thông tin.');
    } finally {
      setSaving(false);
    }
  };

  const toggleLock = async (item: Librarian) => {
    if (String(item.id) === String(currentUserId)) return;
    setTogglingId(item.id);
    try {
      if (item.enabled) {
        await deleteLibrarian(item.id);
      } else {
        await updateLibrarian(item.id, {
          fullName: item.fullName,
          email: item.email,
          phone: item.phone,
          enabled: true,
        });
      }
      await loadData();
    } catch (err: any) {
      alert(err?.message || 'Không thể thay đổi trạng thái tài khoản.');
    } finally {
      setTogglingId(null);
    }
  };

  return (
    <div className="p-8">
      <div className="flex items-start justify-between mb-6">
        <div>
          <h1 className="text-2xl font-bold text-foreground">Quản lý Thủ thư (Admin)</h1>
          <p className="text-sm text-muted-foreground mt-0.5">
            Dữ liệu trực tiếp từ máy chủ Backend Spring Boot
          </p>
        </div>
        <div className="flex items-center gap-3">
          <button
            onClick={loadData}
            disabled={loading}
            className="flex items-center gap-2 px-3 py-2 rounded-xl text-sm font-medium border border-border text-foreground hover:bg-muted transition-colors disabled:opacity-50"
          >
            <RefreshCw size={16} className={loading ? 'animate-spin' : ''} />
            <span>Tải lại</span>
          </button>
          <button
            onClick={openAdd}
            className="flex items-center gap-2 px-4 py-2 rounded-xl text-sm font-semibold text-primary-foreground bg-primary hover:opacity-90 transition-opacity"
          >
            <Plus size={18} />
            <span>Thêm thủ thư</span>
          </button>
        </div>
      </div>

      {pageError && (
        <div className="mb-6 p-4 bg-red-50 text-red-700 rounded-2xl border border-red-200 text-sm flex items-center justify-between">
          <span>{pageError}</span>
          <button
            onClick={loadData}
            className="px-3 py-1 bg-red-600 text-white rounded-lg text-xs font-semibold hover:bg-red-700"
          >
            Thử lại
          </button>
        </div>
      )}

      <div className="bg-card rounded-2xl shadow-sm overflow-hidden border border-border">
        {loading ? (
          <div className="p-12 text-center text-muted-foreground text-sm flex flex-col items-center gap-2">
            <RefreshCw size={24} className="animate-spin text-primary" />
            <span>Đang tải danh sách thủ thư từ Backend...</span>
          </div>
        ) : librarians.length === 0 ? (
          <div className="p-12 text-center text-muted-foreground text-sm">
            Chưa có tài khoản thủ thư nào trong hệ thống.
          </div>
        ) : (
          <table className="w-full text-sm">
            <thead>
              <tr className="border-b border-border bg-muted/40">
                {['ID', 'Họ và tên', 'Tên đăng nhập', 'Email', 'Số điện thoại', 'Vai trò', 'Trạng thái', 'Hành động'].map(
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
              {librarians.map((item) => {
                const isSelf = String(item.id) === String(currentUserId);
                const isToggling = togglingId === item.id;

                return (
                  <tr key={item.id} className="border-b border-border last:border-0 hover:bg-muted/50">
                    <td className="px-6 py-4 text-muted-foreground text-xs font-mono">
                      #{item.id}
                    </td>
                    <td className="px-6 py-4 font-semibold text-foreground">
                      {item.fullName}
                      {isSelf && (
                        <span className="ml-2 text-xs text-primary font-normal">
                          (bạn)
                        </span>
                      )}
                    </td>
                    <td className="px-6 py-4 text-muted-foreground font-mono text-xs">
                      {item.username}
                    </td>
                    <td className="px-6 py-4 text-muted-foreground text-xs">
                      {item.email || '—'}
                    </td>
                    <td className="px-6 py-4 text-muted-foreground text-xs">
                      {item.phone || '—'}
                    </td>
                    <td className="px-6 py-4">
                      <span className="inline-block px-2.5 py-0.5 rounded-full text-xs font-medium bg-purple-100 text-purple-700">
                        Thủ thư
                      </span>
                    </td>
                    <td className="px-6 py-4">
                      <span
                        className={`inline-block px-3 py-0.5 rounded-full text-xs font-medium ${
                          item.enabled
                            ? 'bg-green-100 text-green-700'
                            : 'bg-red-100 text-red-600'
                        }`}
                      >
                        {item.enabled ? 'Hoạt động' : 'Đã khóa'}
                      </span>
                    </td>
                    <td className="px-6 py-4">
                      <div className="flex items-center gap-2">
                        <button
                          onClick={() => openEdit(item)}
                          className="px-3 py-1.5 rounded-lg text-xs font-semibold border border-border text-foreground hover:bg-muted transition-colors flex items-center gap-1"
                        >
                          <Edit2 size={14} />
                          Sửa
                        </button>
                        <button
                          onClick={() => toggleLock(item)}
                          disabled={isSelf || isToggling}
                          className={`px-3 py-1.5 rounded-lg text-xs font-semibold transition-colors flex items-center gap-1 ${
                            isSelf
                              ? 'opacity-30 cursor-not-allowed border border-border text-muted-foreground'
                              : item.enabled
                              ? 'bg-red-50 text-red-600 hover:bg-red-100 border border-red-200'
                              : 'bg-green-50 text-green-700 hover:bg-green-100 border border-green-200'
                          }`}
                        >
                          {isToggling ? (
                            <RefreshCw size={14} className="animate-spin" />
                          ) : item.enabled ? (
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
        )}
      </div>

      {showModal && (
        <div className="fixed inset-0 bg-black/20 flex items-center justify-center z-50">
          <div className="bg-card rounded-2xl p-6 w-full max-w-md shadow-xl border border-border">
            <h2 className="font-bold text-lg text-foreground mb-4">
              {modalMode === 'add' ? 'Thêm mới Thủ thư' : 'Chỉnh sửa thông tin Thủ thư'}
            </h2>

            {formError && (
              <div className="mb-4 px-3 py-2 bg-red-50 text-red-600 text-sm rounded-xl border border-red-200">
                {formError}
              </div>
            )}

            <div className="space-y-3">
              <div>
                <label className="block text-sm font-medium text-foreground mb-1">
                  Họ và tên *
                </label>
                <input
                  value={form.fullName}
                  onChange={(e) => setForm({ ...form, fullName: e.target.value })}
                  placeholder="Nhập họ và tên thủ thư"
                  className="w-full border border-border rounded-xl px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-primary/20 bg-background"
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-foreground mb-1">
                  Tên đăng nhập *
                </label>
                <input
                  value={form.username}
                  disabled={modalMode === 'edit'}
                  onChange={(e) => setForm({ ...form, username: e.target.value })}
                  placeholder="Nhập tên đăng nhập"
                  className="w-full border border-border rounded-xl px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-primary/20 bg-background disabled:opacity-50"
                />
              </div>

              {modalMode === 'add' && (
                <div>
                  <label className="block text-sm font-medium text-foreground mb-1">
                    Mật khẩu *
                  </label>
                  <div className="relative">
                    <input
                      type={showPassword ? 'text' : 'password'}
                      value={form.password}
                      onChange={(e) => setForm({ ...form, password: e.target.value })}
                      placeholder="Ít nhất 6 ký tự"
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
              )}

              <div>
                <label className="block text-sm font-medium text-foreground mb-1">
                  Email
                </label>
                <input
                  type="email"
                  value={form.email}
                  onChange={(e) => setForm({ ...form, email: e.target.value })}
                  placeholder="thuthu@gmail.com"
                  className="w-full border border-border rounded-xl px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-primary/20 bg-background"
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-foreground mb-1">
                  Số điện thoại
                </label>
                <input
                  value={form.phone}
                  onChange={(e) => setForm({ ...form, phone: e.target.value })}
                  placeholder="0981234567"
                  className="w-full border border-border rounded-xl px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-primary/20 bg-background"
                />
              </div>
            </div>

            <div className="flex gap-3 mt-6">
              <button
                onClick={() => setShowModal(false)}
                disabled={saving}
                className="flex-1 py-2 rounded-xl border border-border text-sm font-medium text-foreground hover:bg-muted transition-colors disabled:opacity-50"
              >
                Hủy
              </button>
              <button
                onClick={handleSave}
                disabled={saving}
                className="flex-1 py-2 rounded-xl text-sm font-semibold text-primary-foreground bg-primary hover:opacity-90 transition-opacity disabled:opacity-50 flex items-center justify-center gap-2"
              >
                {saving && <RefreshCw size={14} className="animate-spin" />}
                <span>{modalMode === 'add' ? 'Thêm mới' : 'Lưu thay đổi'}</span>
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
