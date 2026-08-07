'use client';

import { useState, useEffect, useCallback } from 'react';
import { Plus, Search, RefreshCw, Edit3, Trash2, X, AlertCircle, Lock, Unlock, CheckCircle } from 'lucide-react';
import { Button } from '@/components/ui/button';
import {
  fetchReadersPage,
  createReader,
  toggleReaderStatusApi,
  type ReaderResponse,
  type ReaderPageResult,
} from '@/api/readerApi';
import { EditMemberModal } from './EditMemberModal';
import { ConfirmDeleteMemberModal } from './ConfirmDeleteMemberModal';

export function MembersPage() {
  const [readersPage, setReadersPage] = useState<ReaderPageResult>({
    content: [],
    page: 0,
    size: 10,
    totalElements: 0,
    totalPages: 0,
  });

  const [page, setPage] = useState(0);
  const [size, setSize] = useState(10);
  const [searchTerm, setSearchTerm] = useState('');
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');
  const [successMessage, setSuccessMessage] = useState<string | null>(null);
  const [formError, setFormError] = useState('');

  // Modal states
  const [showAddModal, setShowAddModal] = useState(false);
  const [selectedMemberToEdit, setSelectedMemberToEdit] = useState<ReaderResponse | null>(null);
  const [selectedMemberToDelete, setSelectedMemberToDelete] = useState<ReaderResponse | null>(null);

  const [form, setForm] = useState({
    name: '',
    email: '',
    phone: '',
    address: '',
  });

  const loadReaders = useCallback(async () => {
    setLoading(true);
    setError('');
    const token = typeof window !== 'undefined' ? localStorage.getItem('accessToken') || '' : '';
    try {
      const data = await fetchReadersPage(token, page, size);
      setReadersPage(data);
    } catch (err: any) {
      setError(err?.message || 'Không thể tải danh sách bạn đọc từ máy chủ.');
    } finally {
      setLoading(false);
    }
  }, [page, size]);

  useEffect(() => {
    loadReaders();
  }, [loadReaders]);

  const filteredReaders = (readersPage.content || []).filter(
    (reader) =>
      reader.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
      reader.email.toLowerCase().includes(searchTerm.toLowerCase()) ||
      reader.cardNumber.toLowerCase().includes(searchTerm.toLowerCase()) ||
      reader.phoneNumber.includes(searchTerm)
  );

  const handleSave = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!form.name.trim() || !form.email.trim() || !form.phone.trim()) {
      setFormError('Vui lòng điền đầy đủ Họ tên, Email và Số điện thoại.');
      return;
    }

    setSaving(true);
    setFormError('');
    const token = typeof window !== 'undefined' ? localStorage.getItem('accessToken') || '' : '';

    try {
      await createReader(
        {
          name: form.name.trim(),
          email: form.email.trim(),
          phoneNumber: form.phone.trim(),
          address: form.address.trim() || 'Chưa cập nhật',
        },
        token
      );

      setForm({ name: '', email: '', phone: '', address: '' });
      setShowAddModal(false);
      setSuccessMessage('Cấp thẻ độc giả mới thành công!');
      await loadReaders();
    } catch (err: any) {
      setFormError(err?.message || 'Thêm bạn đọc thất bại.');
    } finally {
      setSaving(false);
    }
  };

  const handleToggleStatus = async (reader: ReaderResponse) => {
    setError('');
    setSuccessMessage(null);
    const token = typeof window !== 'undefined' ? localStorage.getItem('accessToken') || '' : '';
    const newStatus = reader.cardStatus === 'ACTIVE' ? 'LOCKED' : 'ACTIVE';

    try {
      await toggleReaderStatusApi(reader.id, newStatus, token);
      setSuccessMessage(`Đã ${newStatus === 'ACTIVE' ? 'kích hoạt lại' : 'tạm khóa'} thẻ của độc giả "${reader.name}" thành công.`);
      await loadReaders();
    } catch (err: any) {
      setError(err?.message || 'Thay đổi trạng thái thẻ độc giả thất bại (Có thể do API backend chưa được triển khai).');
    }
  };

  return (
    <div className="p-8 max-w-7xl mx-auto space-y-6 text-foreground animate-in fade-in duration-200">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-extrabold tracking-tight">Quản lý Độc Giả</h1>
          <p className="text-sm text-muted-foreground mt-1">
            Xem danh sách, chỉnh sửa thông tin, khóa/mở khóa thẻ và xóa thông tin độc giả
          </p>
        </div>
        <div className="flex items-center gap-3">
          <Button onClick={loadReaders} variant="outline" disabled={loading} className="rounded-xl flex items-center gap-2 text-xs">
            <RefreshCw className={`w-3.5 h-3.5 ${loading ? 'animate-spin' : ''}`} />
            Tải lại
          </Button>
          <Button
            onClick={() => {
              setFormError('');
              setForm({ name: '', email: '', phone: '', address: '' });
              setShowAddModal(true);
            }}
            className="bg-primary hover:bg-primary/90 text-primary-foreground rounded-xl flex items-center gap-2 text-xs font-semibold"
          >
            <Plus className="w-4 h-4" />
            Cấp thẻ mới
          </Button>
        </div>
      </div>

      {/* Notifications */}
      {error && (
        <div className="p-4 bg-destructive/10 text-destructive rounded-xl border border-destructive/20 text-sm flex items-center justify-between animate-in fade-in">
          <span className="flex items-center gap-2">
            <AlertCircle className="w-4 h-4 text-destructive" />
            {error}
          </span>
          <Button onClick={() => setError('')} size="sm" variant="ghost" className="h-7 w-7 p-0 rounded-lg">
            <X className="w-4 h-4" />
          </Button>
        </div>
      )}

      {successMessage && (
        <div className="p-4 bg-emerald-500/10 text-emerald-600 dark:text-emerald-400 rounded-xl border border-emerald-500/20 text-sm flex items-center justify-between animate-in fade-in">
          <span className="flex items-center gap-2">
            <CheckCircle className="w-4 h-4 text-emerald-500" />
            {successMessage}
          </span>
          <Button onClick={() => setSuccessMessage(null)} size="sm" variant="ghost" className="h-7 w-7 p-0 rounded-lg hover:bg-emerald-500/10">
            <X className="w-4 h-4" />
          </Button>
        </div>
      )}

      {/* Search Bar */}
      <div className="relative">
        <Search className="absolute left-3.5 top-1/2 -translate-y-1/2 w-4 h-4 text-muted-foreground" />
        <input
          type="text"
          placeholder="Tìm kiếm theo mã thẻ, họ tên, email hoặc số điện thoại..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          className="w-full pl-10 pr-4 py-2.5 border border-border rounded-xl bg-card text-foreground placeholder-muted-foreground focus:outline-none focus:ring-2 focus:ring-primary transition text-sm shadow-xs"
        />
      </div>

      {/* Readers Table */}
      <div className="bg-card rounded-2xl border border-border shadow-sm overflow-hidden">
        {loading ? (
          <div className="p-16 text-center text-muted-foreground text-sm flex flex-col items-center justify-center gap-3">
            <RefreshCw className="w-6 h-6 animate-spin text-primary" />
            <span>Đang tải danh sách bạn đọc từ máy chủ...</span>
          </div>
        ) : filteredReaders.length === 0 ? (
          <div className="p-16 text-center text-muted-foreground text-sm">
            {searchTerm ? 'Không tìm thấy bạn đọc nào phù hợp với từ khóa.' : 'Chưa có bạn đọc nào trong hệ thống. Nhấp "Cấp thẻ mới" để thêm.'}
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left text-sm">
              <thead className="bg-muted/50 border-b border-border text-xs uppercase text-muted-foreground font-semibold">
                <tr>
                  <th className="px-6 py-4">Mã Thẻ</th>
                  <th className="px-6 py-4">Họ tên</th>
                  <th className="px-6 py-4">Email / Điện thoại</th>
                  <th className="px-6 py-4">Địa chỉ</th>
                  <th className="px-6 py-4">Hạn Thẻ</th>
                  <th className="px-6 py-4">Người tạo</th>
                  <th className="px-6 py-4 text-center">Trạng thái</th>
                  <th className="px-6 py-4 text-right">Thao tác</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-border">
                {filteredReaders.map((reader) => (
                  <tr key={reader.id} className="hover:bg-muted/20 transition-colors">
                    <td className="px-6 py-4 text-sm font-mono font-bold text-primary">{reader.cardNumber}</td>
                    <td className="px-6 py-4 text-sm font-semibold text-foreground">{reader.name}</td>
                    <td className="px-6 py-4 text-xs space-y-0.5">
                      <div className="text-foreground">{reader.email}</div>
                      <div className="text-muted-foreground">{reader.phoneNumber}</div>
                    </td>
                    <td className="px-6 py-4 text-xs text-muted-foreground max-w-xs truncate">{reader.address}</td>
                    <td className="px-6 py-4 text-xs font-medium text-foreground">
                      {reader.cardExpiryAt ? new Date(reader.cardExpiryAt).toLocaleDateString('vi-VN') : 'N/A'}
                    </td>
                    <td className="px-6 py-4 text-xs text-foreground">
                      <span className="px-2.5 py-1 rounded-md bg-blue-500/10 text-blue-600 dark:text-blue-400 font-medium border border-blue-500/20">
                        {reader.createdByName || "Hệ thống"}
                      </span>
                    </td>
                    <td className="px-6 py-4 text-center">
                      <span
                        className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-semibold ${
                          reader.cardStatus === 'ACTIVE'
                            ? 'bg-green-500/10 text-green-600 border border-green-500/20'
                            : 'bg-destructive/10 text-destructive border border-destructive/20'
                        }`}
                      >
                        {reader.cardStatus === 'ACTIVE' ? 'Hoạt động' : 'Tạm khóa'}
                      </span>
                    </td>
                    <td className="px-6 py-4 text-right text-xs font-medium space-x-1.5 whitespace-nowrap">
                      <Button
                        variant="outline"
                        size="sm"
                        onClick={() => setSelectedMemberToEdit(reader)}
                        className="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-lg text-xs"
                      >
                        <Edit3 className="w-3.5 h-3.5" />
                        Sửa
                      </Button>
                      
                      {/* Nút Ẩn/Hiện (Khóa/Mở khóa thẻ) */}
                      <Button
                        variant={reader.cardStatus === 'ACTIVE' ? "secondary" : "outline"}
                        size="sm"
                        onClick={() => handleToggleStatus(reader)}
                        className="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-lg text-xs"
                      >
                        {reader.cardStatus === 'ACTIVE' ? (
                          <>
                            <Lock className="w-3.5 h-3.5 text-amber-500" />
                            Khóa
                          </>
                        ) : (
                          <>
                            <Unlock className="w-3.5 h-3.5 text-green-500" />
                            Kích hoạt
                          </>
                        )}
                      </Button>

                      <Button
                        variant="destructive"
                        size="sm"
                        onClick={() => setSelectedMemberToDelete(reader)}
                        className="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-lg text-xs"
                      >
                        <Trash2 className="w-3.5 h-3.5" />
                        Xóa
                      </Button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}

        {/* Pagination Controls */}
        <div className="px-6 py-4 bg-muted/30 border-t border-border flex items-center justify-between">
          <div className="flex items-center gap-2 text-xs text-muted-foreground">
            <span>Hiển thị</span>
            <select
              value={size}
              onChange={(e) => {
                setSize(Number(e.target.value));
                setPage(0);
              }}
              className="border border-border rounded-lg px-2 py-1 text-xs bg-background text-foreground focus:outline-none focus:ring-1 focus:ring-primary"
            >
              <option value={5}>5</option>
              <option value={10}>10</option>
              <option value={20}>20</option>
            </select>
            <span>bản ghi / trang (Tổng số: {readersPage.totalElements} độc giả)</span>
          </div>

          <div className="flex items-center gap-3 text-xs">
            <Button
              variant="outline"
              disabled={page === 0}
              onClick={() => setPage((p) => Math.max(0, p - 1))}
              className="px-3 py-1 rounded-lg text-xs"
            >
              Trang trước
            </Button>
            <span className="font-semibold text-foreground">
              Trang {readersPage.totalPages > 0 ? page + 1 : 0} / {readersPage.totalPages}
            </span>
            <Button
              variant="outline"
              disabled={page + 1 >= readersPage.totalPages}
              onClick={() => setPage((p) => p + 1)}
              className="px-3 py-1 rounded-lg text-xs"
            >
              Trang sau
            </Button>
          </div>
        </div>
      </div>

      {/* Add Member Modal */}
      {showAddModal && (
        <div className="fixed inset-0 bg-black/60 backdrop-blur-xs flex items-center justify-center z-50 p-4 animate-in fade-in duration-150">
          <div className="bg-card rounded-2xl border border-border w-full max-w-md shadow-2xl p-6 overflow-hidden animate-in zoom-in-95 duration-150">
            <div className="flex items-center justify-between pb-3 border-b border-border">
              <div>
                <h2 className="text-lg font-bold text-foreground">Cấp Thẻ Độc Giả Mới</h2>
                <p className="text-xs text-muted-foreground mt-0.5">Thêm bạn đọc mới vào hệ thống quản lý</p>
              </div>
              <button
                onClick={() => setShowAddModal(false)}
                className="text-muted-foreground hover:text-foreground p-1.5 rounded-lg hover:bg-muted transition"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            {formError && (
              <div className="my-4 px-3.5 py-2.5 bg-destructive/10 text-destructive text-xs rounded-xl border border-destructive/20 leading-relaxed">
                ⚠️ {formError}
              </div>
            )}

            <form onSubmit={handleSave} className="space-y-4 pt-4">
              <div>
                <label className="block text-xs font-semibold text-muted-foreground uppercase tracking-wider mb-1">Họ và tên *</label>
                <input
                  type="text"
                  value={form.name}
                  onChange={(e) => setForm({ ...form, name: e.target.value })}
                  placeholder="Nhập họ và tên bạn đọc"
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

              <div className="flex gap-3 pt-4 border-t border-border">
                <Button
                  type="button"
                  variant="outline"
                  onClick={() => setShowAddModal(false)}
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
                  <span>{saving ? 'Đang xử lý...' : 'Cấp thẻ mới'}</span>
                </Button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Edit Member Modal */}
      {selectedMemberToEdit && (
        <EditMemberModal
          member={selectedMemberToEdit}
          onClose={() => setSelectedMemberToEdit(null)}
          onSuccess={() => {
            setSelectedMemberToEdit(null);
            loadReaders();
          }}
        />
      )}

      {/* Confirm Delete Modal */}
      {selectedMemberToDelete && (
        <ConfirmDeleteMemberModal
          member={selectedMemberToDelete}
          onClose={() => setSelectedMemberToDelete(null)}
          onSuccess={() => {
            setSelectedMemberToDelete(null);
            loadReaders();
          }}
        />
      )}
    </div>
  );
}
