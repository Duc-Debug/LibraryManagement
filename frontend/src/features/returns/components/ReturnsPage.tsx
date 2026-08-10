'use client';

import { useState } from 'react';
import { mockBorrowRecords } from '@/features/borrowing';
import type { BorrowRecord } from '@/features/borrowing';
import { Search, Check, AlertTriangle, Clock, Receipt, CheckCircle2, RotateCcw } from 'lucide-react';
import { Button } from '@/components/ui/button';

const FINE_PER_DAY = 5000; // 5.000 VNĐ / ngày trả muộn

export function ReturnsPage() {
  const [borrowRecords, setBorrowRecords] = useState<BorrowRecord[]>(
    mockBorrowRecords.filter(r => r.status === 'borrowing' || r.status === 'overdue')
  );
  const [returnedRecords, setReturnedRecords] = useState<BorrowRecord[]>(
    mockBorrowRecords.filter(r => r.status === 'returned')
  );
  const [searchTerm, setSearchTerm] = useState('');
  const [returnConfirmModal, setReturnConfirmModal] = useState<{
    record: BorrowRecord;
    overdueDays: number;
    fineAmount: number;
  } | null>(null);

  const filteredBorrowRecords = borrowRecords.filter(
    (record) =>
      record.memberName.toLowerCase().includes(searchTerm.toLowerCase()) ||
      record.bookTitle.toLowerCase().includes(searchTerm.toLowerCase()) ||
      record.id.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const calculateOverdueDays = (dueDateStr: string): number => {
    const due = new Date(dueDateStr);
    const now = new Date();
    if (now <= due) return 0;
    const diffTime = Math.abs(now.getTime() - due.getTime());
    return Math.ceil(diffTime / (1000 * 60 * 60 * 24));
  };

  const handleInitiateReturn = (record: BorrowRecord) => {
    const overdueDays = calculateOverdueDays(record.dueDate);
    const fineAmount = overdueDays * FINE_PER_DAY;

    if (overdueDays > 0) {
      setReturnConfirmModal({
        record,
        overdueDays,
        fineAmount
      });
    } else {
      executeReturn(record.id);
    }
  };

  const executeReturn = (recordId: string) => {
    const record = borrowRecords.find(r => r.id === recordId);
    if (record) {
      const returnedRecord: BorrowRecord = {
        ...record,
        returnDate: new Date().toISOString().split('T')[0],
        status: 'returned',
      };
      setBorrowRecords(borrowRecords.filter(r => r.id !== recordId));
      setReturnedRecords([returnedRecord, ...returnedRecords]);
      setReturnConfirmModal(null);
    }
  };

  return (
    <div className="p-6 md:p-8 max-w-7xl mx-auto space-y-6 text-foreground animate-in fade-in duration-200">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-2 border-b border-border/50">
        <div>
          <h1 className="text-2xl font-bold tracking-tight flex items-center gap-2">
            <RotateCcw className="w-6 h-6 text-primary" />
            <span>Quản Lý Trả Sách & Phạt Quá Hạn</span>
          </h1>
          <p className="text-sm text-muted-foreground mt-1">
            Tiếp nhận trả sách, tính toán tiền phạt tự động và lưu nhật ký giao dịch kho.
          </p>
        </div>
      </div>

      {/* Stats Cards */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        <div className="bg-card/80 backdrop-blur-md rounded-2xl border border-indigo-500/20 p-5 shadow-sm flex items-center justify-between">
          <div>
            <p className="text-xs font-semibold uppercase tracking-wider text-muted-foreground">Đang Mượn Chờ Trả</p>
            <p className="text-2xl font-extrabold text-foreground mt-1">{borrowRecords.filter(r => r.status === 'borrowing').length}</p>
          </div>
          <div className="p-3 rounded-xl bg-indigo-500/10 text-indigo-600 dark:text-indigo-400">
            <Clock className="w-6 h-6" />
          </div>
        </div>

        <div className="bg-card/80 backdrop-blur-md rounded-2xl border border-rose-500/30 p-5 shadow-sm flex items-center justify-between">
          <div>
            <p className="text-xs font-semibold uppercase tracking-wider text-rose-600 dark:text-rose-400">Quá Hạn Cần Thu Hồi</p>
            <p className="text-2xl font-extrabold text-rose-600 dark:text-rose-400 mt-1">{borrowRecords.filter(r => r.status === 'overdue').length}</p>
          </div>
          <div className="p-3 rounded-xl bg-rose-500/15 text-rose-600 dark:text-rose-400">
            <AlertTriangle className="w-6 h-6" />
          </div>
        </div>

        <div className="bg-card/80 backdrop-blur-md rounded-2xl border border-emerald-500/20 p-5 shadow-sm flex items-center justify-between">
          <div>
            <p className="text-xs font-semibold uppercase tracking-wider text-emerald-600 dark:text-emerald-400">Đã Trả Thành Công</p>
            <p className="text-2xl font-extrabold text-emerald-600 dark:text-emerald-400 mt-1">{returnedRecords.length}</p>
          </div>
          <div className="p-3 rounded-xl bg-emerald-500/10 text-emerald-600 dark:text-emerald-400">
            <CheckCircle2 className="w-6 h-6" />
          </div>
        </div>
      </div>

      {/* Search Input */}
      <div className="relative">
        <Search className="absolute left-3.5 top-1/2 -translate-y-1/2 w-4 h-4 text-muted-foreground" />
        <input
          type="text"
          placeholder="Tìm kiếm theo mã phiếu, tên độc giả hoặc tên sách..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          className="w-full pl-10 pr-4 py-2.5 border border-border rounded-xl bg-card text-foreground placeholder-muted-foreground focus:outline-none focus:ring-2 focus:ring-primary transition text-sm shadow-xs"
        />
      </div>

      {/* Pending Returns Table */}
      <div className="bg-card rounded-2xl border border-border shadow-sm overflow-hidden">
        <div className="p-6 border-b border-border/60 flex items-center justify-between">
          <h2 className="text-lg font-bold text-foreground">Danh Sách Chờ Trả Sách ({filteredBorrowRecords.length})</h2>
        </div>

        {filteredBorrowRecords.length === 0 ? (
          <div className="p-12 text-center text-muted-foreground text-sm">
            {searchTerm ? 'Không tìm thấy phiếu mượn nào phù hợp.' : 'Hiện tại không có sách nào chưa trả.'}
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left text-sm">
              <thead className="bg-muted/50 border-b border-border text-xs uppercase text-muted-foreground font-semibold whitespace-nowrap">
                <tr>
                  <th className="px-6 py-4 whitespace-nowrap">Mã Phiếu</th>
                  <th className="px-6 py-4 whitespace-nowrap">Tên Sách</th>
                  <th className="px-6 py-4 whitespace-nowrap">Độc Giả</th>
                  <th className="px-6 py-4 whitespace-nowrap">Ngày Mượn</th>
                  <th className="px-6 py-4 whitespace-nowrap">Ngày Hẹn Trả</th>
                  <th className="px-6 py-4 text-center whitespace-nowrap">Trạng Thái</th>
                  <th className="px-6 py-4 text-right whitespace-nowrap">Thao Tác</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-border">
                {filteredBorrowRecords.map((record) => {
                  const overdueDays = calculateOverdueDays(record.dueDate);
                  const isLate = overdueDays > 0 || record.status === 'overdue';

                  return (
                    <tr
                      key={record.id}
                      className={`hover:bg-muted/20 transition-colors ${
                        isLate ? 'bg-rose-500/5' : ''
                      }`}
                    >
                      <td className="px-6 py-4 font-mono text-xs font-bold text-primary whitespace-nowrap">{record.id}</td>
                      <td className="px-6 py-4 font-semibold text-foreground whitespace-nowrap">{record.bookTitle}</td>
                      <td className="px-6 py-4 text-foreground whitespace-nowrap">{record.memberName}</td>
                      <td className="px-6 py-4 text-xs text-muted-foreground whitespace-nowrap">{record.borrowDate}</td>
                      <td className="px-6 py-4 text-xs font-medium whitespace-nowrap">
                        <span className={isLate ? 'text-rose-600 dark:text-rose-400 font-bold' : 'text-muted-foreground'}>
                          {record.dueDate}
                        </span>
                      </td>
                      <td className="px-6 py-4 text-center whitespace-nowrap">
                        {isLate ? (
                          <span className="inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full text-xs font-semibold bg-rose-500/10 text-rose-600 dark:text-rose-400 border border-rose-500/20 whitespace-nowrap">
                            <AlertTriangle className="w-3 h-3" /> Quá hạn ({overdueDays} ngày)
                          </span>
                        ) : (
                          <span className="inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full text-xs font-semibold bg-indigo-500/10 text-indigo-600 dark:text-indigo-400 border border-indigo-500/20 whitespace-nowrap">
                            <Clock className="w-3 h-3" /> Đang mượn
                          </span>
                        )}
                      </td>
                      <td className="px-6 py-4 text-right whitespace-nowrap">
                        <Button
                          onClick={() => handleInitiateReturn(record)}
                          className="bg-emerald-600 hover:bg-emerald-700 text-white h-8 px-3 text-xs font-semibold rounded-lg shadow-sm cursor-pointer"
                        >
                          <Check className="w-3.5 h-3.5 mr-1" />
                          Xác Nhận Trả
                        </Button>
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {/* Overdue Fine Confirmation Modal */}
      {returnConfirmModal && (
        <div className="fixed inset-0 bg-black/60 backdrop-blur-xs flex items-center justify-center z-50 p-4 animate-in fade-in duration-150">
          <div className="bg-card rounded-2xl border border-border w-full max-w-md shadow-2xl p-6 space-y-4 animate-in zoom-in-95 duration-150">
            <div className="flex items-center gap-2 text-rose-600 dark:text-rose-400 pb-2 border-b border-border">
              <Receipt className="w-5 h-5" />
              <h2 className="text-lg font-bold">Xác Nhận Thu Tiền Phạt Trả Muộn</h2>
            </div>

            <div className="p-4 rounded-xl bg-rose-500/10 border border-rose-500/20 text-xs space-y-2">
              <div className="flex justify-between">
                <span className="text-muted-foreground">Tên độc giả:</span>
                <span className="font-bold text-foreground">{returnConfirmModal.record.memberName}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-muted-foreground">Cuốn sách:</span>
                <span className="font-bold text-foreground">{returnConfirmModal.record.bookTitle}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-muted-foreground">Số ngày quá hạn:</span>
                <span className="font-bold text-rose-600 dark:text-rose-400">{returnConfirmModal.overdueDays} ngày</span>
              </div>
              <div className="flex justify-between pt-2 border-t border-rose-500/20 text-sm">
                <span className="font-bold text-foreground">Tổng tiền phạt quá hạn:</span>
                <span className="font-extrabold text-rose-600 dark:text-rose-400">
                  {returnConfirmModal.fineAmount.toLocaleString('vi-VN')} VNĐ
                </span>
              </div>
            </div>

            <div className="flex gap-3 pt-2">
              <Button
                variant="outline"
                onClick={() => setReturnConfirmModal(null)}
                className="flex-1 rounded-xl text-xs"
              >
                Hủy
              </Button>
              <Button
                onClick={() => executeReturn(returnConfirmModal.record.id)}
                className="flex-1 bg-emerald-600 hover:bg-emerald-700 text-white rounded-xl text-xs font-bold cursor-pointer"
              >
                Đã Thu Tiền & Trả Sách
              </Button>
            </div>
          </div>
        </div>
      )}

      {/* Returned History */}
      {returnedRecords.length > 0 && (
        <div className="bg-card rounded-2xl border border-border shadow-sm overflow-hidden">
          <div className="p-6 border-b border-border/60">
            <h2 className="text-lg font-bold text-foreground">Lịch Sử Trả Sách Gần Đây</h2>
          </div>
          <div className="overflow-x-auto">
            <table className="w-full text-left text-sm">
              <thead className="bg-muted/50 border-b border-border text-xs uppercase text-muted-foreground font-semibold whitespace-nowrap">
                <tr>
                  <th className="px-6 py-4 whitespace-nowrap">Mã Phiếu</th>
                  <th className="px-6 py-4 whitespace-nowrap">Sách</th>
                  <th className="px-6 py-4 whitespace-nowrap">Độc Giả</th>
                  <th className="px-6 py-4 whitespace-nowrap">Ngày Mượn</th>
                  <th className="px-6 py-4 whitespace-nowrap">Ngày Trả</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-border">
                {returnedRecords.slice(0, 5).map((record) => (
                  <tr key={record.id} className="hover:bg-muted/20 transition-colors">
                    <td className="px-6 py-4 text-sm font-mono font-bold text-primary whitespace-nowrap">{record.id}</td>
                    <td className="px-6 py-4 text-sm font-semibold text-foreground whitespace-nowrap">{record.bookTitle}</td>
                    <td className="px-6 py-4 text-sm text-foreground whitespace-nowrap">{record.memberName}</td>
                    <td className="px-6 py-4 text-xs text-muted-foreground whitespace-nowrap">{record.borrowDate}</td>
                    <td className="px-6 py-4 text-xs text-emerald-600 dark:text-emerald-400 font-semibold whitespace-nowrap">{record.returnDate}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}
    </div>
  );
}
