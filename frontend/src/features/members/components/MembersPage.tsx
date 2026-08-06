'use client';

import { useState, useEffect } from 'react';
import { fetchAllReaders, createReader, type ReaderResponse } from '@/api/readerApi';
import { Plus, Search, RefreshCw } from 'lucide-react';
import { Button } from '@/components/ui/button';

export function MembersPage() {
  const [readers, setReaders] = useState<ReaderResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [searchTerm, setSearchTerm] = useState('');
  const [error, setError] = useState('');
  const [formError, setFormError] = useState('');

  const [showAddModal, setShowAddModal] = useState(false);
  const [form, setForm] = useState({
    name: '',
    email: '',
    phone: '',
    address: '',
  });

  const loadReaders = async () => {
    setLoading(true);
    setError('');
    const token = typeof window !== 'undefined' ? localStorage.getItem('accessToken') || '' : '';
    try {
      const data = await fetchAllReaders(token);
      setReaders(data);
    } catch (err: any) {
      setError(err?.message || 'Không thể tải danh sách bạn đọc từ máy chủ.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadReaders();
  }, []);

  const filteredReaders = readers.filter(
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
      await loadReaders();
    } catch (err: any) {
      setFormError(err?.message || 'Thêm bạn đọc thất bại.');
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="p-8">
      {/* Header */}
      <div className="mb-8 flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold text-foreground mb-2">Quản lý Thành viên (Bạn đọc)</h1>
          <p className="text-muted-foreground">Dữ liệu thành viên thực sự được lưu trong cơ sở dữ liệu MySQL Backend</p>
        </div>
        <div className="flex items-center gap-3">
          <Button onClick={loadReaders} variant="outline" disabled={loading} className="flex items-center gap-2">
            <RefreshCw className={`w-4 h-4 ${loading ? 'animate-spin' : ''}`} />
            Tải lại
          </Button>
          <Button
            onClick={() => {
              setFormError('');
              setForm({ name: '', email: '', phone: '', address: '' });
              setShowAddModal(true);
            }}
            className="bg-primary hover:bg-primary/90"
          >
            <Plus className="w-4 h-4 mr-2" />
            Thêm thành viên
          </Button>
        </div>
      </div>

      {error && (
        <div className="mb-6 p-4 bg-red-50 text-red-700 rounded-xl border border-red-200 text-sm flex items-center justify-between">
          <span>{error}</span>
          <Button onClick={loadReaders} size="sm" variant="destructive">
            Thử lại
          </Button>
        </div>
      )}

      {/* Search */}
      <div className="mb-6 relative">
        <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-muted-foreground" />
        <input
          type="text"
          placeholder="Tìm kiếm theo mã thẻ, tên, email hoặc số điện thoại..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          className="w-full pl-10 pr-4 py-2 border border-border rounded-lg bg-card text-foreground placeholder-muted-foreground focus:outline-none focus:ring-2 focus:ring-primary"
        />
      </div>

      {/* Members Table */}
      <div className="bg-card rounded-lg border border-border shadow-sm overflow-hidden">
        {loading ? (
          <div className="p-12 text-center text-muted-foreground text-sm flex flex-col items-center gap-2">
            <RefreshCw className="w-6 h-6 animate-spin text-primary" />
            <span>Đang tải danh sách bạn đọc từ Backend...</span>
          </div>
        ) : filteredReaders.length === 0 ? (
          <div className="p-12 text-center text-muted-foreground text-sm">
            {searchTerm ? 'Không tìm thấy bạn đọc nào phù hợp.' : 'Chưa có bạn đọc nào trong hệ thống. Hãy bấm "Thêm thành viên" để cấp thẻ mới.'}
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full">
              <thead>
                <tr className="border-b border-border bg-muted/50">
                  <th className="px-6 py-3 text-left text-sm font-semibold text-foreground">Mã Thẻ</th>
                  <th className="px-6 py-3 text-left text-sm font-semibold text-foreground">Họ tên</th>
                  <th className="px-6 py-3 text-left text-sm font-semibold text-foreground">Email</th>
                  <th className="px-6 py-3 text-left text-sm font-semibold text-foreground">Số điện thoại</th>
                  <th className="px-6 py-3 text-left text-sm font-semibold text-foreground">Địa chỉ</th>
                  <th className="px-6 py-3 text-left text-sm font-semibold text-foreground">Người tạo</th>
                  <th className="px-6 py-3 text-left text-sm font-semibold text-foreground">Trạng thái</th>
                </tr>
              </thead>
              <tbody>
                {filteredReaders.map((reader) => (
                  <tr key={reader.id} className="border-b border-border hover:bg-muted/30 transition-colors">
                    <td className="px-6 py-4 text-sm font-mono font-bold text-primary">{reader.cardNumber}</td>
                    <td className="px-6 py-4 text-sm font-medium text-foreground">{reader.name}</td>
                    <td className="px-6 py-4 text-sm text-foreground">{reader.email}</td>
                    <td className="px-6 py-4 text-sm text-muted-foreground">{reader.phoneNumber}</td>
                    <td className="px-6 py-4 text-sm text-muted-foreground">{reader.address}</td>
                    <td className="px-6 py-4 text-sm text-foreground">
                      <span className="px-2.5 py-1 rounded-md bg-blue-50 text-blue-700 font-medium text-xs border border-blue-100">
                        {reader.createdByName || "Hệ thống"}
                      </span>
                    </td>
                    <td className="px-6 py-4 text-sm">
                      <span
                        className={`inline-flex items-center px-3 py-1 rounded-full text-xs font-medium ${
                          reader.cardStatus === 'ACTIVE'
                            ? 'bg-green-100 text-green-700'
                            : 'bg-red-100 text-red-700'
                        }`}
                      >
                        {reader.cardStatus === 'ACTIVE' ? 'Hoạt động' : 'Tạm khóa'}
                      </span>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {/* Add Member Modal */}
      {showAddModal && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
          <div className="bg-card rounded-lg border border-border w-full max-w-md shadow-lg p-6">
            <h2 className="text-lg font-bold text-foreground mb-4">Cấp Thẻ Thành Viên Mới</h2>

            {formError && (
              <div className="mb-4 px-3 py-2 bg-red-50 text-red-600 text-sm rounded-lg border border-red-200">
                {formError}
              </div>
            )}

            <form onSubmit={handleSave} className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-foreground mb-1">Họ và tên *</label>
                <input
                  type="text"
                  value={form.name}
                  onChange={(e) => setForm({ ...form, name: e.target.value })}
                  placeholder="Nhập họ và tên"
                  required
                  className="w-full px-3 py-2 border border-border rounded-lg bg-background text-foreground placeholder-muted-foreground focus:outline-none focus:ring-2 focus:ring-primary"
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-foreground mb-1">Email *</label>
                <input
                  type="email"
                  value={form.email}
                  onChange={(e) => setForm({ ...form, email: e.target.value })}
                  placeholder="nguyenvana@gmail.com"
                  required
                  className="w-full px-3 py-2 border border-border rounded-lg bg-background text-foreground placeholder-muted-foreground focus:outline-none focus:ring-2 focus:ring-primary"
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-foreground mb-1">Số điện thoại *</label>
                <input
                  type="text"
                  value={form.phone}
                  onChange={(e) => setForm({ ...form, phone: e.target.value })}
                  placeholder="0912345678"
                  required
                  className="w-full px-3 py-2 border border-border rounded-lg bg-background text-foreground placeholder-muted-foreground focus:outline-none focus:ring-2 focus:ring-primary"
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-foreground mb-1">Địa chỉ</label>
                <input
                  type="text"
                  value={form.address}
                  onChange={(e) => setForm({ ...form, address: e.target.value })}
                  placeholder="Hà Nội, Việt Nam"
                  className="w-full px-3 py-2 border border-border rounded-lg bg-background text-foreground placeholder-muted-foreground focus:outline-none focus:ring-2 focus:ring-primary"
                />
              </div>

              <div className="flex gap-3 pt-4">
                <Button
                  type="button"
                  variant="outline"
                  onClick={() => setShowAddModal(false)}
                  disabled={saving}
                  className="flex-1"
                >
                  Hủy
                </Button>
                <Button
                  type="submit"
                  disabled={saving}
                  className="flex-1 bg-primary hover:bg-primary/90 flex items-center justify-center gap-2"
                >
                  {saving && <RefreshCw className="w-4 h-4 animate-spin" />}
                  <span>{saving ? 'Đang xử lý...' : 'Cấp thẻ mới'}</span>
                </Button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
