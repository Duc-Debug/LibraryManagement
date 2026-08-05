import { useState } from "react";
import type { Book, Member, BorrowRecord } from "@/types";

interface BorrowPageProps {
  books: Book[];
  members: Member[];
  records: BorrowRecord[];
  setRecords: (r: BorrowRecord[]) => void;
  setBooks: (b: Book[]) => void;
}

export default function BorrowPage({ books = [], members = [], records = [], setRecords = () => {}, setBooks = () => {} }: BorrowPageProps) {
  const [memberId, setMemberId] = useState("");
  const [bookId, setBookId] = useState("");
  const [dueDate, setDueDate] = useState("");
  const [success, setSuccess] = useState(false);
  const [error, setError] = useState("");

  const handleSubmit = () => {
    if (!memberId || !bookId || !dueDate) {
      setError("Vui lòng điền đầy đủ thông tin.");
      return;
    }
    setError("");

    const member = members.find((m) => m.id === memberId)!;
    const book = books.find((b) => b.id === bookId)!;
    const today = new Date().toISOString().split("T")[0];

    const newRecord: BorrowRecord = {
      id: `P${String(records.length + 1).padStart(3, "0")}`,
      bookId: book.id,
      bookTitle: book.title,
      memberId: member.id,
      memberName: member.name,
      borrowDate: today,
      dueDate,
      status: "borrowing",
    };

    setRecords([...records, newRecord]);
    setBooks(books.map((b) => b.id === bookId ? { ...b, available: b.available - 1 } : b));
    setMemberId("");
    setBookId("");
    setDueDate("");
    setSuccess(true);
    setTimeout(() => setSuccess(false), 3000);
  };

  return (
    <div className="p-8 flex justify-center">
      <div className="w-full max-w-xl bg-white rounded-2xl p-8 shadow-sm">
        <h1 className="text-2xl font-bold text-gray-900">Tạo phiếu Mượn sách</h1>
        <p className="text-sm text-gray-400 mt-0.5 mb-6">Nhập thông tin thành viên và sách cần mượn</p>

        {success && (
          <div className="mb-5 px-4 py-3 bg-green-50 text-green-700 rounded-xl text-sm font-medium border border-green-100">
            Tạo phiếu mượn thành công! Thông tin đã được cập nhật vào bảng điều khiển và quản lý trả sách.
          </div>
        )}
        {error && (
          <div className="mb-5 px-4 py-3 bg-red-50 text-red-600 rounded-xl text-sm font-medium border border-red-100">
            {error}
          </div>
        )}

        <div className="space-y-5">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1.5">Thành viên mượn</label>
            <select
              value={memberId}
              onChange={(e) => setMemberId(e.target.value)}
              className="w-full border border-gray-200 rounded-xl px-3 py-2.5 text-sm bg-white focus:outline-none focus:ring-2 focus:ring-green-200"
            >
              <option value="">-- Chọn thành viên --</option>
              {members.filter((m) => m.active).map((m) => (
                <option key={m.id} value={m.id}>{m.name}</option>
              ))}
            </select>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1.5">Sách mượn</label>
            <select
              value={bookId}
              onChange={(e) => setBookId(e.target.value)}
              className="w-full border border-gray-200 rounded-xl px-3 py-2.5 text-sm bg-white focus:outline-none focus:ring-2 focus:ring-green-200"
            >
              <option value="">-- Chọn sách --</option>
              {books.filter((b) => b.available > 0).map((b) => (
                <option key={b.id} value={b.id}>{b.title} (còn {b.available})</option>
              ))}
            </select>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1.5">Hạn trả sách</label>
            <input
              type="date"
              value={dueDate}
              onChange={(e) => setDueDate(e.target.value)}
              className="w-full border border-gray-200 rounded-xl px-3 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-green-200"
            />
          </div>

          <button
            onClick={handleSubmit}
            className="w-full py-3 rounded-xl text-sm font-bold text-white transition-opacity hover:opacity-90"
            style={{ backgroundColor: "#1a4a2e" }}
          >
            Xác nhận mượn sách
          </button>
        </div>
      </div>
    </div>
  );
}
