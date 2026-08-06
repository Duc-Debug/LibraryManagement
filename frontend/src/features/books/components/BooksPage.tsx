'use client';

import { useState, useEffect, useCallback } from 'react';
import {
  fetchBooksApi,
  deleteBookApi,
  hideBookApi,
  unhideBookApi,
  BookResponseDto,
  PageResult,
} from '@/api/bookApi';
import { Search, Plus, Eye, EyeOff, Trash2, X, Edit3, PackagePlus, AlertTriangle, BookOpen } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { AddBookModal } from './AddBookModal';

export function BooksPage() {
  const [booksPage, setBooksPage] = useState<PageResult<BookResponseDto>>({
    items: [],
    page: 0,
    size: 10,
    totalElements: 0,
    totalPages: 0,
  });

  const [page, setPage] = useState(0);
  const [size, setSize] = useState(10);
  const [searchTerm, setSearchTerm] = useState('');
  const [debouncedSearch, setDebouncedSearch] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);

  const [showAddModal, setShowAddModal] = useState(false);
  const [selectedBook, setSelectedBook] = useState<BookResponseDto | null>(null);
  const [bookToDelete, setBookToDelete] = useState<BookResponseDto | null>(null);
  const [deleting, setDeleting] = useState(false);

  // Debounce 300ms cho Live Search
  useEffect(() => {
    const handler = setTimeout(() => {
      setDebouncedSearch(searchTerm);
      setPage(0);
    }, 300);
    return () => clearTimeout(handler);
  }, [searchTerm]);

  const loadBooks = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const res = await fetchBooksApi(page, size, debouncedSearch);
      setBooksPage(res);
    } catch (err: any) {
      setError(err.message || 'Không thể tải danh sách sách từ máy chủ.');
    } finally {
      setLoading(false);
    }
  }, [page, size, debouncedSearch]);

  useEffect(() => {
    loadBooks();
  }, [loadBooks]);

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
      setError(err.message || 'Không thể xóa sách.');
      setBookToDelete(null);
    } finally {
      setDeleting(false);
    }
  };

  const handleToggleHide = async (book: BookResponseDto) => {
    setError(null);
    setSuccessMessage(null);
    try {
      if (book.active) {
        await hideBookApi(book.bookId);
        setSuccessMessage(`Đã ẩn sách "${book.title}".`);
      } else {
        await unhideBookApi(book.bookId);
        setSuccessMessage(`Đã khôi phục sách "${book.title}".`);
      }
      loadBooks();
      if (selectedBook?.bookId === book.bookId) {
        setSelectedBook({ ...selectedBook, active: !selectedBook.active });
      }
    } catch (err: any) {
      setError(err.message || 'Thao tác ẩn/hiện sách thất bại.');
    }
  };

  return (
    <div className="p-8">
      {/* Header */}
      <div className="mb-8 flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold text-foreground mb-2">Quản lý Sách (UC A2.5)</h1>
          <p className="text-muted-foreground">Nhấp vào từng dòng sách để xem chi tiết & thực hiện các thao tác quản lý</p>
        </div>
        <Button onClick={() => setShowAddModal(true)} className="bg-primary hover:bg-primary/90">
          <Plus className="w-4 h-4 mr-2" />
          Thêm sách mới
        </Button>
      </div>

      {/* Notifications */}
      {error && (
        <div className="mb-4 p-4 rounded-lg bg-destructive/10 border border-destructive/20 text-destructive text-sm flex justify-between items-center">
          <span>⚠️ {error}</span>
          <button onClick={() => setError(null)} className="text-destructive hover:opacity-80">
            <X className="w-4 h-4" />
          </button>
        </div>
      )}

      {successMessage && (
        <div className="mb-4 p-4 rounded-lg bg-emerald-500/10 border border-emerald-500/20 text-emerald-600 text-sm flex justify-between items-center">
          <span>✅ {successMessage}</span>
          <button onClick={() => setSuccessMessage(null)} className="text-emerald-600 hover:opacity-80">
            <X className="w-4 h-4" />
          </button>
        </div>
      )}

      {/* Live Search Input */}
      <div className="mb-6 relative">
        <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-muted-foreground" />
        <input
          type="text"
          placeholder="Tìm theo tên sách, tác giả hoặc ISBN (Live Search)..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          className="w-full pl-10 pr-4 py-2 border border-border rounded-lg bg-card text-foreground placeholder-muted-foreground focus:outline-none focus:ring-2 focus:ring-primary"
        />
      </div>

      {/* Books Table */}
      <div className="bg-card rounded-lg border border-border shadow-sm overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full">
            <thead>
              <tr className="border-b border-border bg-muted/50 text-xs font-semibold text-foreground uppercase">
                <th className="px-6 py-3 text-left">Mã Sách</th>
                <th className="px-6 py-3 text-left">Tên sách</th>
                <th className="px-6 py-3 text-left">Tác giả</th>
                <th className="px-6 py-3 text-left">Thể loại</th>
                <th className="px-6 py-3 text-left">ISBN</th>
                <th className="px-6 py-3 text-center">Năm</th>
                <th className="px-6 py-3 text-center">Có sẵn / Tổng</th>
                <th className="px-6 py-3 text-center">Trạng thái</th>
                <th className="px-6 py-3 text-right">Chi Tiết</th>
              </tr>
            </thead>
            <tbody>
              {loading ? (
                <tr>
                  <td colSpan={9} className="px-6 py-8 text-center text-muted-foreground">
                    Đang nạp dữ liệu từ máy chủ...
                  </td>
                </tr>
              ) : booksPage.items.length === 0 ? (
                <tr>
                  <td colSpan={9} className="px-6 py-8 text-center text-muted-foreground">
                    Không tìm thấy cuốn sách nào trong Database.
                  </td>
                </tr>
              ) : (
                booksPage.items.map((book) => (
                  <tr
                    key={book.bookId}
                    className="border-b border-border hover:bg-muted/30 transition-colors cursor-pointer"
                    onClick={() => setSelectedBook(book)}
                  >
                    <td className="px-6 py-4 text-xs font-mono text-muted-foreground">#{book.bookId}</td>
                    <td className="px-6 py-4 text-sm font-semibold text-foreground">{book.title}</td>
                    <td className="px-6 py-4 text-sm text-foreground">{book.author}</td>
                    <td className="px-6 py-4 text-sm">
                      <span className="px-2.5 py-0.5 rounded-full text-xs font-medium bg-primary/10 text-primary">
                        {book.categoryName || 'Chưa phân loại'}
                      </span>
                    </td>
                    <td className="px-6 py-4 text-sm font-mono text-muted-foreground">{book.isbn}</td>
                    <td className="px-6 py-4 text-sm text-center text-foreground">{book.publishedYear || 'N/A'}</td>
                    <td className="px-6 py-4 text-sm text-center">
                      <span className="font-bold text-primary">{book.availableQuantity}</span>
                      <span className="text-muted-foreground"> / {book.totalQuantity}</span>
                    </td>
                    <td className="px-6 py-4 text-sm text-center">
                      {book.active ? (
                        <span className="px-2.5 py-0.5 rounded-full text-xs font-medium bg-emerald-500/10 text-emerald-600">
                          Đang hiện
                        </span>
                      ) : (
                        <span className="px-2.5 py-0.5 rounded-full text-xs font-medium bg-muted text-muted-foreground">
                          Đã ẩn
                        </span>
                      )}
                    </td>
                    <td className="px-6 py-4 text-sm text-right">
                      <span className="text-xs text-primary font-medium hover:underline">
                        Xem chi tiết &rarr;
                      </span>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>

        {/* Phân trang Footer */}
        <div className="px-6 py-4 border-t border-border flex items-center justify-between text-sm">
          <div className="flex items-center gap-2 text-muted-foreground">
            <span>Hiển thị</span>
            <select
              value={size}
              onChange={(e) => {
                setSize(Number(e.target.value));
                setPage(0);
              }}
              className="border border-border rounded px-2 py-1 bg-card text-foreground"
            >
              <option value={5}>5</option>
              <option value={10}>10</option>
              <option value={20}>20</option>
            </select>
            <span>kết quả / trang (Tổng: {booksPage.totalElements})</span>
          </div>

          <div className="flex items-center gap-3">
            <Button
              variant="outline"
              size="sm"
              disabled={page === 0}
              onClick={() => setPage((p) => Math.max(0, p - 1))}
            >
              Trang trước
            </Button>
            <span className="font-medium text-foreground">
              Trang {booksPage.totalPages > 0 ? page + 1 : 0} / {booksPage.totalPages}
            </span>
            <Button
              variant="outline"
              size="sm"
              disabled={page + 1 >= booksPage.totalPages}
              onClick={() => setPage((p) => p + 1)}
            >
              Trang sau
            </Button>
          </div>
        </div>
      </div>

      {/* Modal Chi Tiết Sách (Hiển thị đầy đủ categoryName, coverImageUrl, createdAt) */}
      {selectedBook && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
          <div className="bg-card rounded-xl border border-border max-w-lg w-full p-6 shadow-xl space-y-4">
            <div className="flex justify-between items-center border-b border-border pb-3">
              <h2 className="text-xl font-bold text-foreground">Chi Tiết Sách (#{selectedBook.bookId})</h2>
              <button onClick={() => setSelectedBook(null)} className="text-muted-foreground hover:text-foreground">
                <X className="w-5 h-5" />
              </button>
            </div>

            <div className="space-y-3.5 text-sm">
              <div className="flex items-start gap-4">
                {selectedBook.coverImageUrl ? (
                  <img
                    src={selectedBook.coverImageUrl}
                    alt={selectedBook.title}
                    className="w-20 h-28 object-cover rounded-lg border border-border shrink-0 shadow-xs"
                  />
                ) : (
                  <div className="w-20 h-28 bg-muted/40 rounded-lg border border-border shrink-0 flex flex-col items-center justify-center text-muted-foreground text-xs p-2 text-center">
                    <BookOpen className="w-6 h-6 mb-1 opacity-60" />
                    <span>Không có ảnh</span>
                  </div>
                )}

                <div className="space-y-1.5 flex-1">
                  <div>
                    <span className="text-muted-foreground block text-xs">Tên Sách</span>
                    <span className="font-bold text-foreground text-base leading-tight block">
                      {selectedBook.title}
                    </span>
                  </div>
                  <div>
                    <span className="text-muted-foreground block text-xs">Tác Giả</span>
                    <span className="text-foreground font-semibold">{selectedBook.author}</span>
                  </div>
                  <div>
                    <span className="text-muted-foreground block text-xs">Thể Loại</span>
                    <span className="inline-block px-2.5 py-0.5 rounded-full text-xs font-semibold bg-primary/10 text-primary mt-0.5">
                      {selectedBook.categoryName || 'Chưa phân loại'}
                    </span>
                  </div>
                </div>
              </div>

              <div className="grid grid-cols-2 gap-2 pt-1 border-t border-border/50">
                <div>
                  <span className="text-muted-foreground block text-xs">Mã ISBN</span>
                  <span className="font-mono text-foreground">{selectedBook.isbn}</span>
                </div>
                <div>
                  <span className="text-muted-foreground block text-xs">Nhà Xuất Bản</span>
                  <span className="text-foreground">{selectedBook.publisher || 'N/A'}</span>
                </div>
              </div>

              <div className="grid grid-cols-2 gap-2">
                <div>
                  <span className="text-muted-foreground block text-xs">Năm Xuất Bản</span>
                  <span className="text-foreground">{selectedBook.publishedYear || 'N/A'}</span>
                </div>
                <div>
                  <span className="text-muted-foreground block text-xs">Vị Trí Kệ Sách</span>
                  <span className="text-foreground font-medium">{selectedBook.shelfLocation || 'N/A'}</span>
                </div>
              </div>

              <div className="grid grid-cols-2 gap-2">
                <div>
                  <span className="text-muted-foreground block text-xs">Trạng Thái Hiển Thị</span>
                  <span className="font-semibold text-emerald-600">
                    {selectedBook.active ? 'Đang Hiện' : 'Đã Ẩn'}
                  </span>
                </div>
                <div>
                  <span className="text-muted-foreground block text-xs">Ngày Tạo / Nhập Sách</span>
                  <span className="text-foreground">
                    {selectedBook.createdAt
                      ? new Date(selectedBook.createdAt).toLocaleDateString('vi-VN')
                      : 'N/A'}
                  </span>
                </div>
              </div>

              {/* Quantities Showcase Box */}
              <div className="p-3 bg-muted/40 rounded-lg border border-border grid grid-cols-2 text-center my-2">
                <div>
                  <span className="text-xs text-muted-foreground block">Tổng Số Lượng</span>
                  <span className="text-lg font-bold text-foreground">{selectedBook.totalQuantity}</span>
                </div>
                <div>
                  <span className="text-xs text-primary block">Còn Sẵn (Trong Kho)</span>
                  <span className="text-lg font-bold text-primary">{selectedBook.availableQuantity}</span>
                </div>
              </div>

              <div>
                <span className="text-muted-foreground block text-xs">Mô Tả</span>
                <p className="text-xs text-foreground bg-muted/20 p-2.5 rounded border border-border mt-1">
                  {selectedBook.description || 'Chưa có mô tả cho cuốn sách này.'}
                </p>
              </div>
            </div>

            {/* Chân Modal */}
            <div className="pt-4 border-t border-border flex flex-wrap items-center justify-between gap-2">
              <div className="flex items-center gap-2">
                <Button
                  variant="outline"
                  size="sm"
                  onClick={() => handleToggleHide(selectedBook)}
                  className="text-xs"
                >
                  {selectedBook.active ? (
                    <>
                      <EyeOff className="w-3.5 h-3.5 mr-1.5" /> Ẩn sách
                    </>
                  ) : (
                    <>
                      <Eye className="w-3.5 h-3.5 mr-1.5" /> Khôi phục
                    </>
                  )}
                </Button>

                <Button
                  variant="outline"
                  size="sm"
                  onClick={() => alert("Chức năng Sửa thông tin đang được team phát triển!")}
                  className="text-xs"
                >
                  <Edit3 className="w-3.5 h-3.5 mr-1.5" /> Sửa sách
                </Button>

                <Button
                  variant="outline"
                  size="sm"
                  onClick={() => alert("Chức năng Nhập kho (+Số lượng) đang được team phát triển!")}
                  className="text-xs"
                >
                  <PackagePlus className="w-3.5 h-3.5 mr-1.5" /> Nhập kho
                </Button>
              </div>

              <div className="flex items-center gap-2">
                <Button
                  variant="destructive"
                  size="sm"
                  onClick={() => setBookToDelete(selectedBook)}
                  className="text-xs"
                >
                  <Trash2 className="w-3.5 h-3.5 mr-1.5" /> Xóa
                </Button>

                <Button variant="outline" size="sm" onClick={() => setSelectedBook(null)} className="text-xs">
                  Đóng
                </Button>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* POPUP XÁC NHẬN XÓA AN TOÀN */}
      {bookToDelete && (
        <div className="fixed inset-0 bg-black/60 backdrop-blur-xs flex items-center justify-center z-50 p-4">
          <div className="bg-card rounded-xl border border-destructive/30 max-w-md w-full p-6 shadow-2xl space-y-4 animate-in fade-in zoom-in-95 duration-150">
            <div className="flex items-center gap-3 text-destructive">
              <div className="p-2.5 rounded-full bg-destructive/10">
                <AlertTriangle className="w-6 h-6" />
              </div>
              <div>
                <h3 className="text-lg font-bold">Xác Nhận Xóa Vĩnh Viễn</h3>
                <p className="text-xs text-muted-foreground">Hành động này không thể hoàn tác!</p>
              </div>
            </div>

            <div className="py-2 text-sm text-foreground space-y-2">
              <p>
                Bạn có chắc chắn muốn xóa vĩnh viễn cuốn sách:
              </p>
              <div className="p-3 bg-muted/30 rounded-lg border border-border font-semibold text-primary">
                "{bookToDelete.title}" (Mã Sách: #{bookToDelete.bookId})
              </div>
              <p className="text-xs text-muted-foreground leading-relaxed">
                ⚠️ Hệ thống sẽ tự động kiểm tra: Nếu sách đang thuộc bất kỳ Phiếu mượn nào chưa hoàn trả (Đang mượn hoặc Quá hạn), thao tác xóa sẽ bị chặn để bảo vệ dữ liệu.
              </p>
            </div>

            <div className="pt-3 border-t border-border flex justify-end gap-3">
              <Button
                variant="outline"
                disabled={deleting}
                onClick={() => setBookToDelete(null)}
              >
                Hủy
              </Button>

              <Button
                variant="destructive"
                disabled={deleting}
                onClick={confirmHardDelete}
              >
                {deleting ? "Đang xóa..." : "Xác nhận Xóa Vĩnh Viễn"}
              </Button>
            </div>
          </div>
        </div>
      )}

      {/* Add Book Modal */}
      {showAddModal && (
        <AddBookModal
          onClose={() => setShowAddModal(false)}
          onSave={() => {
            setShowAddModal(false);
            loadBooks();
          }}
        />
      )}
    </div>
  );
}
