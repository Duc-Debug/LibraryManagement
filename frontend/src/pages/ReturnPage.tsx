import type { Book, BorrowRecord, BorrowStatus } from "@/types";
import StatusBadge from "@/components/StatusBadge";

interface ReturnPageProps {
  records: BorrowRecord[];
  setRecords: (r: BorrowRecord[]) => void;
  books: Book[];
  setBooks: (b: Book[]) => void;
}

export default function ReturnPage({ records = [], setRecords = () => {}, books = [], setBooks = () => {} }: ReturnPageProps) {
  const pending = records.filter((r) => r.status !== "returned");

  const handleReturn = (id: string) => {
    const record = records.find((r) => r.id === id);
    if (!record) return;
    setRecords(records.map((r) => r.id === id ? { ...r, status: "returned" as BorrowStatus } : r));
    setBooks(books.map((b) => b.id === record.bookId ? { ...b, available: b.available + 1 } : b));
  };

  return (
    <div className="p-8">
      <h1 className="text-2xl font-bold text-gray-900">Quản lý Trả sách</h1>
      <p className="text-sm text-gray-400 mt-0.5 mb-6">Danh sách các phiếu đang mượn cần xác nhận trả</p>

      <div className="bg-white rounded-2xl shadow-sm overflow-hidden">
        <table className="w-full text-sm">
          <thead>
            <tr className="border-b border-gray-100">
              {["Mã phiếu", "Tên sách", "Thành viên", "Ngày mượn", "Hạn trả", "Trạng thái", "Hành động"].map((h) => (
                <th key={h} className="px-6 py-3 text-left text-xs font-semibold text-gray-700">{h}</th>
              ))}
            </tr>
          </thead>
          <tbody>
            {pending.length === 0 ? (
              <tr>
                <td colSpan={7} className="px-6 py-12 text-center text-gray-400 text-sm">
                  Không có phiếu mượn nào đang chờ trả
                </td>
              </tr>
            ) : (
              pending.map((r) => (
                <tr key={r.id} className="border-b border-gray-50 last:border-0">
                  <td className="px-6 py-4 text-gray-400 text-xs font-mono">{r.id}</td>
                  <td className="px-6 py-4 font-semibold text-gray-900">{r.bookTitle}</td>
                  <td className="px-6 py-4 text-gray-500">{r.memberName}</td>
                  <td className="px-6 py-4 text-gray-500">{r.borrowDate}</td>
                  <td className="px-6 py-4 text-gray-500">{r.dueDate}</td>
                  <td className="px-6 py-4"><StatusBadge status={r.status} /></td>
                  <td className="px-6 py-4">
                    <button
                      onClick={() => handleReturn(r.id)}
                      className="px-3 py-1.5 rounded-lg text-xs font-semibold text-white hover:opacity-90 transition-opacity"
                      style={{ backgroundColor: "#1a4a2e" }}
                    >
                      Xác nhận trả
                    </button>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}
