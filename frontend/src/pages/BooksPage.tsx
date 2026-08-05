import { useState } from "react";
import type { Book } from "@/types";
import { IconPlus, IconSearch } from "@/components/icons";

interface BooksPageProps {
  books: Book[];
  setBooks: (b: Book[]) => void;
}

export default function BooksPage({ books = [], setBooks = () => {} }: BooksPageProps) {
  const [search, setSearch] = useState("");
  const [showModal, setShowModal] = useState(false);
  const [form, setForm] = useState({ title: "", author: "", genre: "", total: "" });

  const filtered = books.filter(
    (b) =>
      b.title.toLowerCase().includes(search.toLowerCase()) ||
      b.author.toLowerCase().includes(search.toLowerCase())
  );

  const handleAdd = () => {
    if (!form.title || !form.author) return;
    const newBook: Book = {
      id: `B${String(books.length + 1).padStart(3, "0")}`,
      title: form.title,
      author: form.author,
      genre: form.genre,
      total: Number(form.total) || 1,
      available: Number(form.total) || 1,
    };
    setBooks([...books, newBook]);
    setForm({ title: "", author: "", genre: "", total: "" });
    setShowModal(false);
  };

  return (
    <div className="p-8">
      <div className="flex items-start justify-between mb-6">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Quản lý Sách</h1>
          <p className="text-sm text-gray-400 mt-0.5">Danh sách các sách có trong thư viện</p>
        </div>
        <button
          onClick={() => setShowModal(true)}
          className="flex items-center gap-2 px-4 py-2 rounded-xl text-sm font-semibold text-white"
          style={{ backgroundColor: "#1a4a2e" }}
        >
          <IconPlus /><span>Thêm sách mới</span>
        </button>
      </div>

      <div className="relative mb-4">
        <span className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400">
          <IconSearch />
        </span>
        <input
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          placeholder="Tìm kiếm theo tên sách hoặc tác giả..."
          className="w-full pl-9 pr-4 py-2.5 border border-gray-200 rounded-xl text-sm bg-white focus:outline-none focus:ring-2 focus:ring-green-200"
        />
      </div>

      <div className="bg-white rounded-2xl shadow-sm overflow-hidden">
        <table className="w-full text-sm">
          <thead>
            <tr className="border-b border-gray-100">
              {["Mã sách", "Tên sách", "Tác giả", "Thể loại", "Số lượng", "Sẵn có"].map((h) => (
                <th key={h} className="px-6 py-3 text-left text-xs font-semibold text-gray-700">{h}</th>
              ))}
            </tr>
          </thead>
          <tbody>
            {filtered.map((b) => (
              <tr key={b.id} className="border-b border-gray-50 last:border-0">
                <td className="px-6 py-4 text-gray-400 text-xs font-mono">{b.id}</td>
                <td className="px-6 py-4 font-semibold text-gray-900">{b.title}</td>
                <td className="px-6 py-4 text-gray-500">{b.author}</td>
                <td className="px-6 py-4">
                  <span className="px-2.5 py-0.5 bg-gray-100 text-gray-600 rounded-full text-xs">
                    {b.genre}
                  </span>
                </td>
                <td className="px-6 py-4 text-gray-700">{b.total}</td>
                <td className="px-6 py-4 font-bold" style={{ color: "#1a4a2e" }}>{b.available}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {showModal && (
        <div className="fixed inset-0 bg-black/20 flex items-center justify-center z-50">
          <div className="bg-white rounded-2xl p-6 w-full max-w-md shadow-xl">
            <h2 className="font-bold text-lg mb-4">Thêm sách mới</h2>
            <div className="space-y-3">
              {[
                { label: "Tên sách", key: "title", placeholder: "Nhập tên sách" },
                { label: "Tác giả", key: "author", placeholder: "Nhập tên tác giả" },
                { label: "Thể loại", key: "genre", placeholder: "Nhập thể loại" },
                { label: "Số lượng", key: "total", placeholder: "Nhập số lượng", type: "number" },
              ].map((f) => (
                <div key={f.key}>
                  <label className="block text-sm font-medium text-gray-700 mb-1">{f.label}</label>
                  <input
                    type={f.type || "text"}
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
                Thêm sách
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
