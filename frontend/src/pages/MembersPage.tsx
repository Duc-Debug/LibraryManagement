import { useState } from "react";
import type { Member } from "@/types";
import StatusBadge from "@/components/StatusBadge";
import { IconUsers } from "@/components/icons";

interface MembersPageProps {
  members: Member[];
  setMembers: (m: Member[]) => void;
}

export default function MembersPage({ members, setMembers }: MembersPageProps) {
  const [showModal, setShowModal] = useState(false);
  const [form, setForm] = useState({ name: "", email: "", phone: "" });

  const handleAdd = () => {
    if (!form.name) return;
    const m: Member = {
      id: `M${String(members.length + 1).padStart(3, "0")}`,
      name: form.name,
      email: form.email,
      phone: form.phone,
      active: true,
    };
    setMembers([...members, m]);
    setForm({ name: "", email: "", phone: "" });
    setShowModal(false);
  };

  return (
    <div className="p-8">
      <div className="flex items-start justify-between mb-6">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Quản lý Thành viên</h1>
          <p className="text-sm text-gray-400 mt-0.5">Danh sách người dùng đã đăng ký thẻ thư viện</p>
        </div>
        <button
          onClick={() => setShowModal(true)}
          className="flex items-center gap-2 px-4 py-2 rounded-xl text-sm font-semibold text-white"
          style={{ backgroundColor: "#1a4a2e" }}
        >
          <IconUsers size={16} /><span>Thêm thành viên</span>
        </button>
      </div>

      <div className="bg-white rounded-2xl shadow-sm overflow-hidden">
        <table className="w-full text-sm">
          <thead>
            <tr className="border-b border-gray-100">
              {["Mã TV", "Họ và Tên", "Email", "Số điện thoại", "Trạng thái"].map((h) => (
                <th key={h} className="px-6 py-3 text-left text-xs font-semibold text-gray-700">{h}</th>
              ))}
            </tr>
          </thead>
          <tbody>
            {members.map((m) => (
              <tr key={m.id} className="border-b border-gray-50 last:border-0">
                <td className="px-6 py-4 text-gray-400 text-xs font-mono">{m.id}</td>
                <td className="px-6 py-4 font-semibold text-gray-900">{m.name}</td>
                <td className="px-6 py-4 text-gray-400">{m.email}</td>
                <td className="px-6 py-4 text-gray-600">{m.phone}</td>
                <td className="px-6 py-4">
                  <StatusBadge status={m.active ? "active" : "inactive"} />
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {showModal && (
        <div className="fixed inset-0 bg-black/20 flex items-center justify-center z-50">
          <div className="bg-white rounded-2xl p-6 w-full max-w-md shadow-xl">
            <h2 className="font-bold text-lg mb-4">Thêm thành viên</h2>
            <div className="space-y-3">
              {[
                { label: "Họ và Tên", key: "name", placeholder: "Nhập họ và tên" },
                { label: "Email", key: "email", placeholder: "Nhập email" },
                { label: "Số điện thoại", key: "phone", placeholder: "Nhập số điện thoại" },
              ].map((f) => (
                <div key={f.key}>
                  <label className="block text-sm font-medium text-gray-700 mb-1">{f.label}</label>
                  <input
                    value={(form as Record<string, string>)[f.key]}
                    onChange={(e) => setForm({ ...form, [f.key]: e.target.value })}
                    placeholder={f.placeholder}
                    className="w-full border border-gray-200 rounded-xl px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-green-200"
                  />
                </div>
              ))}
            </div>
            <div className="flex gap-3 mt-5">
              <button
                onClick={() => setShowModal(false)}
                className="flex-1 py-2 rounded-xl border border-gray-200 text-sm font-medium text-gray-600 hover:bg-gray-50"
              >
                Hủy
              </button>
              <button
                onClick={handleAdd}
                className="flex-1 py-2 rounded-xl text-sm font-semibold text-white"
                style={{ backgroundColor: "#1a4a2e" }}
              >
                Thêm
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
