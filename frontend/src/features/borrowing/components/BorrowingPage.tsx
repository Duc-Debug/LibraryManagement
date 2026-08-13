'use client';

import { parseErrorMessage } from '@/lib/errorDictionary';
import { useState, useEffect, useCallback } from 'react';
import { fetchAllReaders, ReaderResponse } from '@/api/readerApi';
import { fetchBooksApi, BookResponseDto } from '@/api/bookApi';
import { fetchCategoriesApi, CategoryResponse } from '@/api/categoryApi';
import { createBorrowSlipApi, fetchBorrowSlipsApi, BorrowSlipResponseDto } from '@/api/borrowSlipApi';
import { Plus, RefreshCw, BookOpen, Clock, AlertCircle, CheckCircle2, UserCheck, X } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { BorrowingForm } from './BorrowingForm';

export function BorrowingPage() {
  const [showForm, setShowForm] = useState(false);
  const [borrowingRecords, setBorrowingRecords] = useState<BorrowSlipResponseDto[]>([]);

  // Real DB States
  const [readers, setReaders] = useState<ReaderResponse[]>([]);
  const [books, setBooks] = useState<BookResponseDto[]>([]);
  const [categories, setCategories] = useState<CategoryResponse[]>([]);

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);

  const loadData = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const [readersRes, booksRes, catsRes, slipsRes] = await Promise.allSettled([
        fetchAllReaders(),
        fetchBooksApi(0, 100),
        fetchCategoriesApi(),
        fetchBorrowSlipsApi({ page: 0, size: 50 })
      ]);

      if (readersRes.status === 'fulfilled') {
        setReaders(readersRes.value);
      }
      if (booksRes.status === 'fulfilled' && booksRes.value) {
        setBooks(booksRes.value.content || booksRes.value.items || []);
      }
      if (catsRes.status === 'fulfilled') {
        setCategories(catsRes.value);
      }
      if (slipsRes.status === 'fulfilled' && slipsRes.value) {
        setBorrowingRecords(slipsRes.value.content || []);
      }
    } catch (err: any) {
      setError(parseErrorMessage(err, 'Không thể nạp dữ liệu từ máy chủ.'));
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    loadData();
  }, [loadData]);

  const handleCreateBorrow = async (commandData: any) => {
    setLoading(true);
    setError(null);
    setSuccessMessage(null);
    try {
      const bookIds = commandData.bookIds || (commandData.books || []).map((b: any) => b.bookId);
      const createdSlip = await createBorrowSlipApi({
        readerId: commandData.readerId,
        bookIds: bookIds,
        borrowDays: commandData.borrowDays || 14,
        note: commandData.note,
      });

      setSuccessMessage(`Tạo thành công phiếu mượn mã "${createdSlip.borrowCode}" gồm ${createdSlip.totalBooks} cuốn sách cho độc giả "${createdSlip.readerName}"!`);
      setShowForm(false);
      await loadData();
    } catch (err: any) {
      setError(parseErrorMessage(err, 'Không thể tạo phiếu mượn sách. Vui lòng thử lại.'));
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="p-6 md:p-8 max-w-7xl mx-auto space-y-6 text-foreground animate-in fade-in duration-200">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-2 border-b border-border/50">
        <div>
          <h1 className="text-2xl font-bold tracking-tight flex items-center gap-2">
            <BookOpen className="w-6 h-6 text-primary" />
            <span>Quản Lý Mượn Sách</span>
          </h1>
          <p className="text-sm text-muted-foreground mt-1">
            Lập phiếu mượn sách chứa nhiều đầu sách, kiểm tra điều kiện độc giả & kho sách trực tiếp.
          </p>
        </div>
        <div className="flex items-center gap-3 self-start sm:self-auto">
          <Button onClick={loadData} variant="outline" disabled={loading} className="rounded-xl flex items-center gap-2 text-xs">
            <RefreshCw className={`w-3.5 h-3.5 ${loading ? 'animate-spin' : ''}`} />
            Tải lại
          </Button>
          <Button
            onClick={() => {
              setShowForm(!showForm);
              setSuccessMessage(null);
            }}
            className="bg-primary hover:bg-primary/90 text-primary-foreground rounded-xl flex items-center gap-2 text-xs font-semibold shadow-md cursor-pointer"
          >
            <Plus className="w-4 h-4" />
            {showForm ? 'Đóng Form' : 'Tạo Phiếu Mượn'}
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
          <Button onClick={() => setError(null)} size="sm" variant="ghost" className="h-7 w-7 p-0 rounded-lg">
            <X className="w-4 h-4" />
          </Button>
        </div>
      )}

      {successMessage && (
        <div className="p-4 bg-emerald-500/10 text-emerald-600 dark:text-emerald-400 rounded-xl border border-emerald-500/20 text-sm flex items-center justify-between animate-in fade-in">
          <span className="flex items-center gap-2">
            <CheckCircle2 className="w-4 h-4 text-emerald-500" />
            {successMessage}
          </span>
          <Button onClick={() => setSuccessMessage(null)} size="sm" variant="ghost" className="h-7 w-7 p-0 rounded-lg hover:bg-emerald-500/10">
            <X className="w-4 h-4" />
          </Button>
        </div>
      )}

      {/* Form Container */}
      {showForm && (
        <div className="bg-card rounded-2xl border border-border p-6 shadow-xl animate-in zoom-in-95 duration-150">
          <BorrowingForm
            readers={readers}
            books={books}
            categories={categories}
            onSubmit={handleCreateBorrow}
            onCancel={() => setShowForm(false)}
          />
        </div>
      )}

      {/* Borrowing Slips Table */}
      <div className="bg-card rounded-2xl border border-border shadow-sm overflow-hidden space-y-4">
        <div className="p-6 border-b border-border/60 flex items-center justify-between">
          <h2 className="text-lg font-bold text-foreground">Danh Sách Phiếu Mượn Vừa Tạo ({borrowingRecords.length})</h2>
        </div>

        {borrowingRecords.length > 0 ? (
          <div className="overflow-x-auto">
            <table className="w-full text-left text-sm">
              <thead className="bg-muted/50 border-b border-border text-xs uppercase text-muted-foreground font-semibold whitespace-nowrap">
                <tr>
                  <th className="px-6 py-4 whitespace-nowrap">Mã Phiếu</th>
                  <th className="px-6 py-4 whitespace-nowrap">Mã Thẻ / Độc Giả</th>
                  <th className="px-6 py-4 whitespace-nowrap">Danh Sách Sách Mượn</th>
                  <th className="px-6 py-4 whitespace-nowrap">Ngày Mượn</th>
                  <th className="px-6 py-4 whitespace-nowrap">Ngày Hẹn Trả</th>
                  <th className="px-6 py-4 text-center whitespace-nowrap">Trạng Thái</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-border">
                {borrowingRecords.map((record) => (
                  <tr key={record.id} className="hover:bg-muted/20 transition-colors">
                    <td className="px-6 py-4 font-mono text-xs font-bold text-primary whitespace-nowrap">
                      {record.borrowCode || `#${record.id}`}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <div className="font-semibold text-foreground">{record.readerName}</div>
                      <div className="font-mono text-xs text-muted-foreground">{record.readerCardNumber}</div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <div className="font-bold text-foreground flex items-center gap-1.5">
                        <span className="px-2.5 py-1 rounded-md bg-primary/10 text-primary text-xs font-mono font-bold">
                          {record.totalBooks} cuốn
                        </span>
                        {record.note && (
                          <span className="text-xs text-muted-foreground italic line-clamp-1 max-w-xs">
                            ({record.note})
                          </span>
                        )}
                      </div>
                    </td>
                    <td className="px-6 py-4 text-xs text-muted-foreground whitespace-nowrap">
                      {record.borrowedAt ? new Date(record.borrowedAt).toLocaleDateString('vi-VN') : 'N/A'}
                    </td>
                    <td className="px-6 py-4 text-xs font-semibold text-emerald-600 dark:text-emerald-400 whitespace-nowrap">
                      {record.dueAt ? new Date(record.dueAt).toLocaleDateString('vi-VN') : 'N/A'}
                    </td>
                    <td className="px-6 py-4 text-center whitespace-nowrap">
                      {record.status === 'BORROWING' && (
                        <span className="inline-flex items-center gap-1 px-3 py-1 rounded-full text-xs font-semibold bg-indigo-500/10 text-indigo-600 dark:text-indigo-400 border border-indigo-500/20 whitespace-nowrap">
                          <Clock className="w-3 h-3" /> Đang mượn
                        </span>
                      )}
                      {record.status === 'OVERDUE' && (
                        <span className="inline-flex items-center gap-1 px-3 py-1 rounded-full text-xs font-semibold bg-rose-500/10 text-rose-600 dark:text-rose-400 border border-rose-500/20 whitespace-nowrap">
                          <AlertCircle className="w-3 h-3" /> Quá hạn
                        </span>
                      )}
                      {record.status === 'RETURNED' && (
                        <span className="inline-flex items-center gap-1 px-3 py-1 rounded-full text-xs font-semibold bg-emerald-500/10 text-emerald-600 dark:text-emerald-400 border border-emerald-500/20 whitespace-nowrap">
                          <CheckCircle2 className="w-3 h-3" /> Đã trả
                        </span>
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        ) : (
          <div className="p-12 text-center text-muted-foreground text-sm space-y-2">
            <p>Chưa có phiếu mượn nào trong phiên làm việc hiện tại.</p>
            <p className="text-xs text-muted-foreground">Nhấn nút <strong className="text-primary font-bold">&quot;Tạo Phiếu Mượn&quot;</strong> để chọn Độc giả và Thêm sách vào phiếu.</p>
          </div>
        )}
      </div>
    </div>
  );
}
