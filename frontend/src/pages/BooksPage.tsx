import { useState, useEffect, useCallback } from "react";
import {
  fetchBooksApi,
  fetchBookByIdApi,
  hideBookApi,
  unhideBookApi,
  deleteBookApi,
  BookResponseDto,
  PageResult,
} from "@/api/bookApi";
import { IconSearch, IconX } from "@/components/icons";

export default function BooksPage() {
  const [booksPage, setBooksPage] = useState<PageResult<BookResponseDto>>({
    items: [],
    page: 0,
    size: 10,
    totalElements: 0,
    totalPages: 0,
  });

  const [page, setPage] = useState(0);
  const [size, setSize] = useState(10);
  const [search, setSearch] = useState("");
  const [debouncedSearch, setDebouncedSearch] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);

  // Detail & Delete Modal State
  const [selectedBook, setSelectedBook] = useState<BookResponseDto | null>(null);
  const [bookToDelete, setBookToDelete] = useState<BookResponseDto | null>(null);
  const [deleting, setDeleting] = useState(false);

  // Debounce search input (300ms delay)
  useEffect(() => {
    const handler = setTimeout(() => {
      setDebouncedSearch(search);
      setPage(0);
    }, 300);

    return () => clearTimeout(handler);
  }, [search]);

  // Load books from DB API
  const loadBooks = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await fetchBooksApi(page, size, debouncedSearch);
      setBooksPage(data);
    } catch (err: any) {
      setError(err.message || "Không thể tải danh sách sách.");
    } finally {
      setLoading(false);
    }
  }, [page, size, debouncedSearch]);

  useEffect(() => {
    loadBooks();
  }, [loadBooks]);

  // View Book Detail Handler
  const handleViewDetail = async (bookId: number) => {
    setError(null);
    try {
      const bookDetail = await fetchBookByIdApi(bookId);
      setSelectedBook(bookDetail);
    } catch (err: any) {
      setError(err.message || "Không thể nạp chi tiết sách.");
    }
  };

  // Soft Delete / Hide Handler
  const handleToggleHide = async (book: BookResponseDto) => {
    setError(null);
    setSuccessMessage(null);
    try {
      if (book.active) {
        await hideBookApi(book.bookId);
        setSuccessMessage(`Đã ẩn sách "${book.title}" thành công.`);
      } else {
        await unhideBookApi(book.bookId);
        setSuccessMessage(`Đã khôi phục sách "${book.title}" thành công.`);
      }
      loadBooks();
      if (selectedBook && selectedBook.bookId === book.bookId) {
        setSelectedBook({ ...selectedBook, active: !selectedBook.active });
      }
    } catch (err: any) {
      setError(err.message || "Thao tác ẩn/hiện sách thất bại.");
    }
  };

  // Hard Delete Confirm Handler
  const confirmHardDelete = async () => {
    if (!bookToDelete) return;
    setDeleting(true);
    setError(null);
    setSuccessMessage(null);
    try {
      await deleteBookApi(bookToDelete.bookId);
      setSuccessMessage(`Đã xóa vĩnh viễn sách "${bookToDelete.title}" thành công.`);
      setBookToDelete(null);
      setSelectedBook(null);
      loadBooks();
    } catch (err: any) {
      setError(err.message || "Không thể xóa sách.");
      setBookToDelete(null);
    } finally {
      setDeleting(false);
    }
  };

  return (
    <div className="p-8 max-w-7xl mx-auto">
      {/* Header */}
      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Quản lý Sách (UC A2.5)</h1>
          <p className="text-sm text-gray-500 mt-1">
            Nhấp vào từng dòng sách để xem thông tin chi tiết & thực hiện các thao tác quản lý
          </p>
        </div>
      </div>

      {/* Notifications */}
      {error && (
        <div className="mb-4 p-4 rounded-xl bg-red-50 border border-red-200 text-red-700 text-sm flex justify-between items-center">
          <span>⚠️ {error}</span>
          <button onClick={() => setError(null)} className="text-red-500 hover:text-red-700">
            <IconX />
          </button>
        </div>
      )}

      {successMessage && (
        <div className="mb-4 p-4 rounded-xl bg-emerald-50 border border-emerald-200 text-emerald-700 text-sm flex justify-between items-center">
          <span>✅ {successMessage}</span>
          <button onClick={() => setSuccessMessage(null)} className="text-emerald-500 hover:text-emerald-700">
            <IconX />
          </button>
        </div>
      )}

      {/* Live Search Input */}
      <div className="relative mb-6">
        <span className="absolute left-3.5 top-1/2 -translate-y-1/2 text-gray-400">
          <IconSearch />
        </span>
        <input
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          placeholder="Tìm theo tên sách hoặc tác giả để tìm kiếm tự động (Live Search)..."
          className="w-full pl-10 pr-4 py-3 border border-gray-200 rounded-xl text-sm bg-white shadow-sm focus:outline-none focus:ring-2 focus:ring-emerald-500 transition"
        />
      </div>

      {/* Main Table */}
      <div className="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-sm text-left">
            <thead className="bg-gray-50/80 border-b border-gray-100 text-xs uppercase text-gray-500 font-semibold">
              <tr>
                <th className="px-6 py-4">ID</th>
                <th className="px-6 py-4">Tên Sách</th>
                <th className="px-6 py-4">Tác Giả</th>
                <th className="px-6 py-4">Thể Loại</th>
                <th className="px-6 py-4">ISBN</th>
                <th className="px-6 py-4 text-center">Tổng Số</th>
                <th className="px-6 py-4 text-center">Còn Sẵn</th>
                <th className="px-6 py-4 text-center">Trạng Thái</th>
                <th className="px-6 py-4 text-right">Chi Tiết</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-100">
              {loading ? (
                <tr>
                  <td colSpan={9} className="px-6 py-8 text-center text-gray-400">
                    Đang nạp dữ liệu từ máy chủ...
                  </td>
                </tr>
              ) : booksPage.items.length === 0 ? (
                <tr>
                  <td colSpan={9} className="px-6 py-8 text-center text-gray-400">
                    Không tìm thấy cuốn sách nào trong Database.
                  </td>
                </tr>
              ) : (
                booksPage.items.map((b) => (
                  <tr
                    key={b.bookId}
                    className="hover:bg-gray-50/60 transition cursor-pointer"
                    onClick={() => handleViewDetail(b.bookId)}
                  >
                    <td className="px-6 py-4 font-mono text-xs font-semibold text-gray-400">
                      #{b.bookId}
                    </td>
                    <td className="px-6 py-4 font-semibold text-gray-900">{b.title}</td>
                    <td className="px-6 py-4 text-gray-600">{b.author}</td>
                    <td className="px-6 py-4 text-gray-600 font-medium">
                      <span className="px-2.5 py-0.5 rounded-full text-xs font-semibold bg-emerald-50 text-emerald-800 border border-emerald-100">
                        {b.categoryName || "Chưa phân loại"}
                      </span>
                    </td>
                    <td className="px-6 py-4 font-mono text-xs text-gray-500">{b.isbn}</td>
                    <td className="px-6 py-4 text-center font-medium text-gray-700">
                      {b.totalQuantity}
                    </td>
                    <td className="px-6 py-4 text-center font-bold text-emerald-700">
                      {b.availableQuantity}
                    </td>
                    <td className="px-6 py-4 text-center">
                      {b.active ? (
                        <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-emerald-100 text-emerald-800">
                          Đang hiện
                        </span>
                      ) : (
                        <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-gray-100 text-gray-600">
                          Đã ẩn
                        </span>
                      )}
                    </td>
                    <td className="px-6 py-4 text-right">
                      <span className="text-xs font-semibold text-emerald-700 hover:underline">
                        Xem chi tiết &rarr;
                      </span>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>

        {/* Pagination Controls */}
        <div className="px-6 py-4 bg-gray-50/50 border-t border-gray-100 flex items-center justify-between">
          <div className="flex items-center gap-2 text-sm text-gray-500">
            <span>Hiển thị</span>
            <select
              value={size}
              onChange={(e) => {
                setSize(Number(e.target.value));
                setPage(0);
              }}
              className="border border-gray-200 rounded-lg px-2 py-1 text-sm bg-white focus:outline-none"
            >
              <option value={5}>5</option>
              <option value={10}>10</option>
              <option value={20}>20</option>
            </select>
            <span>kết quả / trang (Tổng: {booksPage.totalElements} sách)</span>
          </div>

          <div className="flex items-center gap-3 text-sm">
            <button
              disabled={page === 0}
              onClick={() => setPage((p) => Math.max(0, p - 1))}
              className="px-3 py-1.5 rounded-lg border border-gray-200 bg-white font-medium text-gray-600 hover:bg-gray-50 disabled:opacity-40 disabled:cursor-not-allowed transition"
            >
              Trang trước
            </button>
            <span className="font-semibold text-gray-700">
              Trang {booksPage.totalPages > 0 ? page + 1 : 0} / {booksPage.totalPages}
            </span>
            <button
              disabled={page + 1 >= booksPage.totalPages}
              onClick={() => setPage((p) => p + 1)}
              className="px-3 py-1.5 rounded-lg border border-gray-200 bg-white font-medium text-gray-600 hover:bg-gray-50 disabled:opacity-40 disabled:cursor-not-allowed transition"
            >
              Trang sau
            </button>
          </div>
        </div>
      </div>

      {/* Book Detail Modal */}
      {selectedBook && (
        <div className="fixed inset-0 bg-black/30 backdrop-blur-xs flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-2xl max-w-lg w-full p-6 shadow-xl border border-gray-100 animate-in fade-in zoom-in-95 duration-150">
            <div className="flex items-center justify-between pb-4 border-b border-gray-100">
              <h2 className="text-xl font-bold text-gray-900">Chi Tiết Sách (ID #{selectedBook.bookId})</h2>
              <button
                onClick={() => setSelectedBook(null)}
                className="text-gray-400 hover:text-gray-600 p-1 rounded-lg hover:bg-gray-100 transition"
              >
                <IconX />
              </button>
            </div>

            <div className="py-4 space-y-3.5 text-sm">
              <div className="flex items-start gap-4">
                {selectedBook.coverImageUrl ? (
                  <img
                    src={selectedBook.coverImageUrl}
                    alt={selectedBook.title}
                    className="w-20 h-28 object-cover rounded-lg border border-gray-100 shrink-0 shadow-xs"
                  />
                ) : (
                  <div className="w-20 h-28 bg-gray-50 rounded-lg border border-gray-200 shrink-0 flex flex-col items-center justify-center text-gray-400 text-xs p-2 text-center">
                    📚 <span>Không có ảnh</span>
                  </div>
                )}
                <div className="space-y-1 flex-1">
                  <div>
                    <span className="text-gray-400 block text-xs">Tên Sách</span>
                    <span className="font-bold text-gray-900 text-base">{selectedBook.title}</span>
                  </div>
                  <div>
                    <span className="text-gray-400 block text-xs">Tác Giả</span>
                    <span className="font-semibold text-gray-800">{selectedBook.author}</span>
                  </div>
                  <div>
                    <span className="text-gray-400 block text-xs">Thể Loại</span>
                    <span className="inline-block px-2.5 py-0.5 rounded-full text-xs font-semibold bg-emerald-50 text-emerald-800 border border-emerald-100">
                      {selectedBook.categoryName || "Chưa phân loại"}
                    </span>
                  </div>
                </div>
              </div>

              <div className="grid grid-cols-2 gap-4 border-t border-gray-100 pt-2">
                <div>
                  <span className="text-gray-400 block text-xs">Mã ISBN</span>
                  <span className="font-mono text-gray-800">{selectedBook.isbn}</span>
                </div>
                <div>
                  <span className="text-gray-400 block text-xs">Nhà Xuất Bản</span>
                  <span className="text-gray-800">{selectedBook.publisher || "Chưa cập nhật"}</span>
                </div>
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div>
                  <span className="text-gray-400 block text-xs">Năm Xuất Bản</span>
                  <span className="text-gray-800">{selectedBook.publishedYear || "N/A"}</span>
                </div>
                <div>
                  <span className="text-gray-400 block text-xs">Vị Trí Kệ Sách</span>
                  <span className="text-gray-800 font-medium">{selectedBook.shelfLocation || "N/A"}</span>
                </div>
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div>
                  <span className="text-gray-400 block text-xs">Trạng Thái Hiển Thị</span>
                  <span className="font-semibold text-emerald-700">
                    {selectedBook.active ? "Đang Hiện" : "Đã Ẩn"}
                  </span>
                </div>
                <div>
                  <span className="text-gray-400 block text-xs">Ngày Nhập Sách</span>
                  <span className="text-gray-800">
                    {selectedBook.createdAt
                      ? new Date(selectedBook.createdAt).toLocaleDateString("vi-VN")
                      : "N/A"}
                  </span>
                </div>
              </div>

              {/* Quantities Showcase Box */}
              <div className="p-4 rounded-xl bg-emerald-50/70 border border-emerald-100 grid grid-cols-2 text-center my-2">
                <div>
                  <span className="text-xs text-gray-500 font-medium block">Tổng Số Lượng</span>
                  <span className="text-xl font-bold text-gray-800">{selectedBook.totalQuantity}</span>
                </div>
                <div>
                  <span className="text-xs text-emerald-800 font-medium block">Sẵn Có (Trong Kho)</span>
                  <span className="text-xl font-bold text-emerald-700">{selectedBook.availableQuantity}</span>
                </div>
              </div>

              <div>
                <span className="text-gray-400 block text-xs">Mô Tả</span>
                <p className="text-gray-700 mt-1 bg-gray-50 p-3 rounded-xl border border-gray-100 text-xs leading-relaxed max-h-28 overflow-y-auto">
                  {selectedBook.description || "Chưa có mô tả cho cuốn sách này."}
                </p>
              </div>
            </div>

            {/* Chân Modal */}
            <div className="pt-4 border-t border-gray-100 flex flex-wrap items-center justify-between gap-2">
              <div className="flex items-center gap-2">
                <button
                  onClick={() => handleToggleHide(selectedBook)}
                  className="px-3 py-1.5 rounded-xl border border-gray-200 text-xs font-semibold text-gray-700 hover:bg-gray-50 transition"
                >
                  {selectedBook.active ? "👁️‍🗨️ Ẩn sách" : "👁️ Khôi phục"}
                </button>

                <button
                  onClick={() => alert("Chức năng Sửa thông tin đang được team phát triển!")}
                  className="px-3 py-1.5 rounded-xl border border-gray-200 text-xs font-semibold text-gray-700 hover:bg-gray-50 transition"
                >
                  ✏️ Sửa sách
                </button>

                <button
                  onClick={() => alert("Chức năng Nhập kho (+Số lượng) đang được team phát triển!")}
                  className="px-3 py-1.5 rounded-xl border border-gray-200 text-xs font-semibold text-gray-700 hover:bg-gray-50 transition"
                >
                  📦 Nhập kho
                </button>
              </div>

              <div className="flex items-center gap-2">
                <button
                  onClick={() => setBookToDelete(selectedBook)}
                  className="px-3 py-1.5 rounded-xl text-xs font-semibold text-white bg-red-600 hover:bg-red-700 transition"
                >
                  🗑️ Xóa
                </button>

                <button
                  onClick={() => setSelectedBook(null)}
                  className="px-3 py-1.5 rounded-xl border border-gray-200 text-xs font-medium text-gray-600 hover:bg-gray-50 transition"
                >
                  Đóng
                </button>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* POPUP XÁC NHẬN XÓA AN TOÀN */}
      {bookToDelete && (
        <div className="fixed inset-0 bg-black/40 backdrop-blur-xs flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-2xl max-w-md w-full p-6 shadow-2xl border border-red-100 space-y-4">
            <div className="flex items-center gap-3 text-red-600">
              <div className="p-3 rounded-2xl bg-red-50 text-xl">
                ⚠️
              </div>
              <div>
                <h3 className="text-lg font-bold text-gray-900">Xác Nhận Xóa Vĩnh Viễn</h3>
                <p className="text-xs text-gray-500">Hành động này không thể hoàn tác!</p>
              </div>
            </div>

            <div className="py-2 text-sm text-gray-700 space-y-2">
              <p>
                Bạn có chắc chắn muốn xóa vĩnh viễn cuốn sách:
              </p>
              <div className="p-3 bg-red-50/50 rounded-xl border border-red-100 font-semibold text-red-900">
                "{bookToDelete.title}" (Mã Sách: #{bookToDelete.bookId})
              </div>
              <p className="text-xs text-gray-400 leading-relaxed">
                ⚠️ Hệ thống sẽ tự động kiểm tra: Nếu sách đang thuộc bất kỳ Phiếu mượn nào chưa hoàn trả, thao tác xóa sẽ bị chặn để bảo vệ dữ liệu.
              </p>
            </div>

            <div className="pt-3 border-t border-gray-100 flex justify-end gap-3">
              <button
                disabled={deleting}
                onClick={() => setBookToDelete(null)}
                className="px-4 py-2 rounded-xl border border-gray-200 text-sm font-medium text-gray-600 hover:bg-gray-50 transition"
              >
                Hủy
              </button>

              <button
                disabled={deleting}
                onClick={confirmHardDelete}
                className="px-4 py-2 rounded-xl text-sm font-semibold text-white bg-red-600 hover:bg-red-700 transition"
              >
                {deleting ? "Đang xóa..." : "Xác nhận Xóa Vĩnh Viễn"}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}