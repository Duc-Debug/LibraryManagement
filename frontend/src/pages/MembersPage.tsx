import { useState, useEffect } from "react";
import StatusBadge from "@/components/StatusBadge";
import { IconUsers } from "@/components/icons";
import { fetchAllReaders, createReader, type ReaderResponse } from "@/api/readerApi";

export default function MembersPage() {
  const [readers, setReaders] = useState<ReaderResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");
  const [formError, setFormError] = useState("");

  const [showModal, setShowModal] = useState(false);
  const [form, setForm] = useState({ name: "", email: "", phone: "", address: "" });

  const loadReaders = async () => {
    setLoading(true);
    setError("");
    const token = localStorage.getItem("accessToken") || "";
    try {
      const data = await fetchAllReaders(token);
      setReaders(data);
    } catch (err: any) {
      setError(err?.message || "Không thể tải danh sách bạn đọc từ máy chủ.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadReaders();
  }, []);

  const handleAdd = async () => {
    if (!form.name.trim() || !form.email.trim() || !form.phone.trim()) {
      setFormError("Vui lòng điền đầy đủ Họ tên, Email và Số điện thoại.");
      return;
    }

    setSaving(true);
    setFormError("");
    const token = localStorage.getItem("accessToken") || "";

    try {
      await createReader(
        {
          name: form.name.trim(),
          email: form.email.trim(),
          phoneNumber: form.phone.trim(),
          address: form.address.trim() || "Chưa cập nhật",
        },
        token
      );

      setForm({ name: "", email: "", phone: "", address: "" });
      setShowModal(false);
      await loadReaders();
    } catch (err: any) {
      setFormError(err?.message || "Thêm bạn đọc thất bại.");
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="p-8">
      <div className="flex items-start justify-between mb-6">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Quản lý Bạn đọc (Thẻ Thư viện)</h1>
          <p className="text-sm text-gray-500 mt-0.5">Dữ liệu bạn đọc trực tiếp từ Backend MySQL Server</p>
        </div>
        <button
          onClick={() => {
            setFormError("");
            setForm({ name: "", email: "", phone: "", address: "" });
            setShowModal(true);
          }}
          className="flex items-center gap-2 px-4 py-2 rounded-xl text-sm font-semibold text-white shadow-sm hover:opacity-90 transition-opacity"
          style={{ backgroundColor: "#1a4a2e" }}
        >
          <IconUsers size={16} />
          <span>Thêm bạn đọc</span>
        </button>
      </div>

      {error && (
        <div className="mb-6 p-4 bg-red-50 text-red-700 rounded-2xl border border-red-200 text-sm flex items-center justify-between">
          <span>{error}</span>
          <button
            onClick={loadReaders}
            className="px-3 py-1 bg-red-600 text-white rounded-lg text-xs font-semibold hover:bg-red-700"
          >
            Tải lại
          </button>
        </div>
      )}

      <div className="bg-white rounded-2xl shadow-sm overflow-hidden border border-gray-100">
        {loading ? (
          <div className="p-12 text-center text-gray-500 text-sm">
            Đang tải dữ liệu bạn đọc từ máy chủ...
          </div>
        ) : readers.length === 0 ? (
          <div className="p-12 text-center text-gray-500 text-sm">
            Chưa có bạn đọc nào trong hệ thống. Hãy bấm <b>"Thêm bạn đọc"</b> để tạo mới.
          </div>
        ) : (
          <table className="w-full text-sm">
            <thead>
              <tr className="border-b border-gray-100 bg-gray-50/50">
                {["Mã Thẻ", "Họ và Tên", "Email", "Số điện thoại", "Địa chỉ", "Trạng thái"].map((h) => (
                  <th key={h} className="px-6 py-3 text-left text-xs font-semibold text-gray-700">
                    {h}
                  </th>
                ))}
              </tr>
            </thead>
            <tbody>
              {readers.map((r) => (
                <tr key={r.id} className="border-b border-gray-50 last:border-0 hover:bg-gray-50/60">
                  <td className="px-6 py-4 text-emerald-800 font-bold text-xs font-mono">{r.cardNumber}</td>
                  <td className="px-6 py-4 font-semibold text-gray-900">{r.name}</td>
                  <td className="px-6 py-4 text-gray-500 text-xs">{r.email}</td>
                  <td className="px-6 py-4 text-gray-600 text-xs">{r.phoneNumber}</td>
                  <td className="px-6 py-4 text-gray-500 text-xs">{r.address}</td>
                  <td className="px-6 py-4">
                    <StatusBadge status={r.cardStatus === "ACTIVE" ? "active" : "inactive"} />
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>

      {showModal && (
        <div className="fixed inset-0 bg-black/30 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-2xl p-6 w-full max-w-md shadow-2xl">
            <h2 className="font-bold text-lg text-gray-900 mb-4">Cấp Thẻ Bạn Đọc Mới</h2>

            {formError && (
              <div className="mb-4 px-3 py-2 bg-red-50 text-red-600 text-sm rounded-xl border border-red-200">
                {formError}
              </div>
            )}

            <div className="space-y-3">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Họ và Tên *</label>
                <input
                  value={form.name}
                  onChange={(e) => setForm({ ...form, name: e.target.value })}
                  placeholder="Ví dụ: Nguyễn Văn A"
                  className="w-full border border-gray-200 rounded-xl px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-emerald-200"
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Email *</label>
                <input
                  type="email"
                  value={form.email}
                  onChange={(e) => setForm({ ...form, email: e.target.value })}
                  placeholder="nguyenvana@gmail.com"
                  className="w-full border border-gray-200 rounded-xl px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-emerald-200"
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Số điện thoại *</label>
                <input
                  value={form.phone}
                  onChange={(e) => setForm({ ...form, phone: e.target.value })}
                  placeholder="0912345678"
                  className="w-full border border-gray-200 rounded-xl px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-emerald-200"
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Địa chỉ</label>
                <input
                  value={form.address}
                  onChange={(e) => setForm({ ...form, address: e.target.value })}
                  placeholder="Hà Nội, Việt Nam"
                  className="w-full border border-gray-200 rounded-xl px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-emerald-200"
                />
              </div>
            </div>

            <div className="flex gap-3 mt-6">
              <button
                onClick={() => setShowModal(false)}
                disabled={saving}
                className="flex-1 py-2 rounded-xl border border-gray-200 text-sm font-medium text-gray-600 hover:bg-gray-50 disabled:opacity-50"
              >
                Hủy
              </button>
              <button
                onClick={handleAdd}
                disabled={saving}
                className="flex-1 py-2 rounded-xl text-sm font-semibold text-white shadow-sm disabled:opacity-50 flex items-center justify-center gap-2"
                style={{ backgroundColor: "#1a4a2e" }}
              >
                {saving ? "Đang xử lý..." : "Cấp thẻ"}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
