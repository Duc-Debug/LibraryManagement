'use client';

import { useCallback, useEffect, useMemo, useRef, useState } from 'react';
import {
  AlertCircle,
  AlertTriangle,
  Check,
  CheckCircle2,
  Clock,
  Loader2,
  Receipt,
  RefreshCw,
  RotateCcw,
  Search,
  X,
} from 'lucide-react';

import {
  fetchBorrowSlipsApi,
  previewBorrowSlipFineApi,
  returnBorrowSlipApi,
  type BorrowSlipResponseDto,
  type BorrowSlipStatus,
  type FineCalculationResponseDto,
  type ReturnBorrowSlipResponseDto,
} from '@/api/borrowSlipApi';
import { Button } from '@/components/ui/button';

const BORROW_SLIP_PAGE_SIZE = 100;
const RETURNED_HISTORY_LIMIT = 5;

type ReturnConfirmation = {
  record: BorrowSlipResponseDto;
  finePreview: FineCalculationResponseDto | null;
};

function getErrorMessage(error: unknown): string {
  return error instanceof Error ? error.message : 'Không thể xử lý yêu cầu. Vui lòng thử lại.';
}

function toTimestamp(value: string): number {
  const timestamp = new Date(value).getTime();
  return Number.isNaN(timestamp) ? 0 : timestamp;
}

function formatDateTime(value: string | null | undefined): string {
  if (!value) return '-';

  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return value;

  return new Intl.DateTimeFormat('vi-VN', {
    dateStyle: 'short',
    timeStyle: 'short',
  }).format(date);
}

function formatCurrency(amount: number | string | null | undefined): string {
  const numericAmount = Number(amount ?? 0);

  if (Number.isNaN(numericAmount)) {
    return `${amount} VND`;
  }

  return new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND',
    maximumFractionDigits: 0,
  }).format(numericAmount);
}

async function fetchAllBorrowSlipsByStatus(
  status: BorrowSlipStatus
): Promise<BorrowSlipResponseDto[]> {
  const firstPage = await fetchBorrowSlipsApi({
    page: 0,
    size: BORROW_SLIP_PAGE_SIZE,
    status,
  });

  if (firstPage.totalPages <= 1) {
    return firstPage.content;
  }

  const remainingPages = await Promise.all(
    Array.from({ length: firstPage.totalPages - 1 }, (_, index) =>
      fetchBorrowSlipsApi({
        page: index + 1,
        size: BORROW_SLIP_PAGE_SIZE,
        status,
      })
    )
  );

  return [
    ...firstPage.content,
    ...remainingPages.flatMap((page) => page.content),
  ];
}

function matchesSearch(record: BorrowSlipResponseDto, searchTerm: string): boolean {
  const normalizedTerm = searchTerm.trim().toLowerCase();
  if (!normalizedTerm) return true;

  return [
    String(record.id),
    record.borrowCode,
    record.readerName,
    record.readerCardNumber,
    String(record.totalBooks),
  ]
    .filter(Boolean)
    .some((value) => value.toLowerCase().includes(normalizedTerm));
}

function StatusPill({ status }: { status: BorrowSlipStatus }) {
  if (status === 'OVERDUE') {
    return (
      <span className="inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full text-xs font-semibold bg-rose-500/10 text-rose-600 dark:text-rose-400 border border-rose-500/20 whitespace-nowrap">
        <AlertTriangle className="w-3 h-3" /> Quá hạn
      </span>
    );
  }

  if (status === 'RETURNED') {
    return (
      <span className="inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full text-xs font-semibold bg-emerald-500/10 text-emerald-600 dark:text-emerald-400 border border-emerald-500/20 whitespace-nowrap">
        <CheckCircle2 className="w-3 h-3" /> Đã trả
      </span>
    );
  }

  return (
    <span className="inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full text-xs font-semibold bg-indigo-500/10 text-indigo-600 dark:text-indigo-400 border border-indigo-500/20 whitespace-nowrap">
      <Clock className="w-3 h-3" /> Đang mượn
    </span>
  );
}

export function ReturnsPage() {
  const [pendingRecords, setPendingRecords] = useState<BorrowSlipResponseDto[]>([]);
  const [returnedRecords, setReturnedRecords] = useState<BorrowSlipResponseDto[]>([]);
  const [searchTerm, setSearchTerm] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [successResult, setSuccessResult] = useState<ReturnBorrowSlipResponseDto | null>(null);
  const [returnConfirmModal, setReturnConfirmModal] = useState<ReturnConfirmation | null>(null);
  const [finePreviewSlipId, setFinePreviewSlipId] = useState<number | null>(null);
  const [returningSlipId, setReturningSlipId] = useState<number | null>(null);
  const returningSlipIdRef = useRef<number | null>(null);

  const loadBorrowSlips = useCallback(async () => {
    setLoading(true);
    setError(null);

    try {
      const [borrowing, overdue, returned] = await Promise.all([
        fetchAllBorrowSlipsByStatus('BORROWING'),
        fetchAllBorrowSlipsByStatus('OVERDUE'),
        fetchAllBorrowSlipsByStatus('RETURNED'),
      ]);

      setPendingRecords(
        [...borrowing, ...overdue].sort((a, b) => toTimestamp(a.dueAt) - toTimestamp(b.dueAt))
      );
      setReturnedRecords(
        returned.sort((a, b) => toTimestamp(b.createdAt) - toTimestamp(a.createdAt))
      );
    } catch (loadError) {
      setPendingRecords([]);
      setReturnedRecords([]);
      setError(getErrorMessage(loadError));
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    void loadBorrowSlips();
  }, [loadBorrowSlips]);

  const filteredPendingRecords = useMemo(
    () => pendingRecords.filter((record) => matchesSearch(record, searchTerm)),
    [pendingRecords, searchTerm]
  );

  const handleInitiateReturn = async (record: BorrowSlipResponseDto) => {
    if (record.status === 'RETURNED') {
      setError('Phiếu mượn này đã RETURNED nên không thể trả lần hai.');
      return;
    }

    if (finePreviewSlipId !== null || returningSlipIdRef.current !== null) {
      return;
    }

    setError(null);
    setSuccessResult(null);
    setFinePreviewSlipId(record.id);

    try {
      const finePreview = await previewBorrowSlipFineApi(record.id);
      setReturnConfirmModal({ record, finePreview });
    } catch (previewError) {
      setReturnConfirmModal({ record, finePreview: null });
      setError(`Không tải được preview phí phạt: ${getErrorMessage(previewError)}`);
    } finally {
      setFinePreviewSlipId(null);
    }
  };

  const executeReturn = async (record: BorrowSlipResponseDto) => {
    if (record.status === 'RETURNED') {
      setError('Phiếu mượn này đã RETURNED nên không thể trả lần hai.');
      return;
    }

    if (returningSlipIdRef.current !== null) {
      return;
    }

    returningSlipIdRef.current = record.id;
    setReturningSlipId(record.id);
    setError(null);
    setSuccessResult(null);

    try {
      const result = await returnBorrowSlipApi(record.id);
      setSuccessResult(result);
      setReturnConfirmModal(null);
      await loadBorrowSlips();
    } catch (returnError) {
      setError(getErrorMessage(returnError));
    } finally {
      returningSlipIdRef.current = null;
      setReturningSlipId(null);
    }
  };

  const borrowingCount = pendingRecords.filter((record) => record.status === 'BORROWING').length;
  const overdueCount = pendingRecords.filter((record) => record.status === 'OVERDUE').length;

  return (
    <div className="p-6 md:p-8 max-w-7xl mx-auto space-y-6 text-foreground animate-in fade-in duration-200">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-2 border-b border-border/50">
        <div>
          <h1 className="text-2xl font-bold tracking-tight flex items-center gap-2">
            <RotateCcw className="w-6 h-6 text-primary" />
            <span>Quản Lý Trả Sách & Phạt Quá Hạn</span>
          </h1>
          <p className="text-sm text-muted-foreground mt-1">
            Tiếp nhận trả sách qua dữ liệu phiếu mượn từ backend.
          </p>
        </div>

        <Button
          onClick={() => void loadBorrowSlips()}
          variant="outline"
          disabled={loading || returningSlipId !== null || finePreviewSlipId !== null}
          className="rounded-xl flex items-center gap-2 text-xs self-start sm:self-auto"
        >
          <RefreshCw className={`w-3.5 h-3.5 ${loading ? 'animate-spin' : ''}`} />
          Tải lại
        </Button>
      </div>

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

      {successResult && (
        <div className="p-4 bg-emerald-500/10 text-emerald-600 dark:text-emerald-400 rounded-xl border border-emerald-500/20 text-sm flex items-center justify-between animate-in fade-in">
          <span className="flex items-center gap-2">
            <CheckCircle2 className="w-4 h-4 text-emerald-500" />
            Phiếu #{successResult.borrowSlipId} đã {successResult.status}; đã trả {successResult.returnedBooks} cuốn lúc{' '}
            {formatDateTime(successResult.returnedAt)}. Tổng phạt: {formatCurrency(successResult.totalFineAmount)}.
          </span>
          <Button onClick={() => setSuccessResult(null)} size="sm" variant="ghost" className="h-7 w-7 p-0 rounded-lg hover:bg-emerald-500/10">
            <X className="w-4 h-4" />
          </Button>
        </div>
      )}

      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        <div className="bg-card/80 backdrop-blur-md rounded-2xl border border-indigo-500/20 p-5 shadow-sm flex items-center justify-between">
          <div>
            <p className="text-xs font-semibold uppercase tracking-wider text-muted-foreground">Đang Mượn Chờ Trả</p>
            <p className="text-2xl font-extrabold text-foreground mt-1">{loading ? '...' : borrowingCount}</p>
          </div>
          <div className="p-3 rounded-xl bg-indigo-500/10 text-indigo-600 dark:text-indigo-400">
            <Clock className="w-6 h-6" />
          </div>
        </div>

        <div className="bg-card/80 backdrop-blur-md rounded-2xl border border-rose-500/30 p-5 shadow-sm flex items-center justify-between">
          <div>
            <p className="text-xs font-semibold uppercase tracking-wider text-rose-600 dark:text-rose-400">Quá Hạn Cần Thu Hồi</p>
            <p className="text-2xl font-extrabold text-rose-600 dark:text-rose-400 mt-1">{loading ? '...' : overdueCount}</p>
          </div>
          <div className="p-3 rounded-xl bg-rose-500/15 text-rose-600 dark:text-rose-400">
            <AlertTriangle className="w-6 h-6" />
          </div>
        </div>

        <div className="bg-card/80 backdrop-blur-md rounded-2xl border border-emerald-500/20 p-5 shadow-sm flex items-center justify-between">
          <div>
            <p className="text-xs font-semibold uppercase tracking-wider text-emerald-600 dark:text-emerald-400">Đã Trả Thành Công</p>
            <p className="text-2xl font-extrabold text-emerald-600 dark:text-emerald-400 mt-1">{loading ? '...' : returnedRecords.length}</p>
          </div>
          <div className="p-3 rounded-xl bg-emerald-500/10 text-emerald-600 dark:text-emerald-400">
            <CheckCircle2 className="w-6 h-6" />
          </div>
        </div>
      </div>

      <div className="relative">
        <Search className="absolute left-3.5 top-1/2 -translate-y-1/2 w-4 h-4 text-muted-foreground" />
        <input
          type="text"
          placeholder="Tìm kiếm theo mã phiếu, tên độc giả hoặc mã thẻ..."
          value={searchTerm}
          onChange={(event) => setSearchTerm(event.target.value)}
          className="w-full pl-10 pr-4 py-2.5 border border-border rounded-xl bg-card text-foreground placeholder-muted-foreground focus:outline-none focus:ring-2 focus:ring-primary transition text-sm shadow-xs"
        />
      </div>

      <div className="bg-card rounded-2xl border border-border shadow-sm overflow-hidden">
        <div className="p-6 border-b border-border/60 flex items-center justify-between">
          <h2 className="text-lg font-bold text-foreground">Danh Sách Chờ Trả Sách ({filteredPendingRecords.length})</h2>
        </div>

        {loading ? (
          <div className="p-12 text-center text-muted-foreground text-sm flex items-center justify-center gap-2">
            <Loader2 className="w-4 h-4 animate-spin" />
            Đang nạp phiếu mượn...
          </div>
        ) : filteredPendingRecords.length === 0 ? (
          <div className="p-12 text-center text-muted-foreground text-sm">
            {searchTerm ? 'Không tìm thấy phiếu mượn nào phù hợp.' : 'Hiện tại không có phiếu BORROWING/OVERDUE cần trả.'}
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left text-sm">
              <thead className="bg-muted/50 border-b border-border text-xs uppercase text-muted-foreground font-semibold whitespace-nowrap">
                <tr>
                  <th className="px-6 py-4 whitespace-nowrap">Mã Phiếu</th>
                  <th className="px-6 py-4 whitespace-nowrap">Độc Giả</th>
                  <th className="px-6 py-4 whitespace-nowrap">Số Sách</th>
                  <th className="px-6 py-4 whitespace-nowrap">Ngày Mượn</th>
                  <th className="px-6 py-4 whitespace-nowrap">Hạn Trả</th>
                  <th className="px-6 py-4 text-center whitespace-nowrap">Trạng Thái</th>
                  <th className="px-6 py-4 text-right whitespace-nowrap">Thao Tác</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-border">
                {filteredPendingRecords.map((record) => {
                  const isProcessing =
                    returningSlipId === record.id || finePreviewSlipId === record.id;
                  const isLate = record.status === 'OVERDUE';

                  return (
                    <tr
                      key={record.id}
                      className={`hover:bg-muted/20 transition-colors ${
                        isLate ? 'bg-rose-500/5' : ''
                      }`}
                    >
                      <td className="px-6 py-4 font-mono text-xs font-bold text-primary whitespace-nowrap">
                        {record.borrowCode || `#${record.id}`}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap">
                        <div className="font-semibold text-foreground">{record.readerName}</div>
                        <div className="font-mono text-xs text-muted-foreground">{record.readerCardNumber}</div>
                      </td>
                      <td className="px-6 py-4 font-semibold text-foreground whitespace-nowrap">
                        {record.totalBooks} cuốn
                      </td>
                      <td className="px-6 py-4 text-xs text-muted-foreground whitespace-nowrap">
                        {formatDateTime(record.borrowedAt)}
                      </td>
                      <td className="px-6 py-4 text-xs font-medium whitespace-nowrap">
                        <span className={isLate ? 'text-rose-600 dark:text-rose-400 font-bold' : 'text-muted-foreground'}>
                          {formatDateTime(record.dueAt)}
                        </span>
                      </td>
                      <td className="px-6 py-4 text-center whitespace-nowrap">
                        <StatusPill status={record.status} />
                      </td>
                      <td className="px-6 py-4 text-right whitespace-nowrap">
                        <Button
                          onClick={() => void handleInitiateReturn(record)}
                          disabled={isProcessing || returningSlipId !== null}
                          className="bg-emerald-600 hover:bg-emerald-700 text-white h-8 px-3 text-xs font-semibold rounded-lg shadow-sm cursor-pointer"
                        >
                          {isProcessing ? (
                            <Loader2 className="w-3.5 h-3.5 mr-1 animate-spin" />
                          ) : (
                            <Check className="w-3.5 h-3.5 mr-1" />
                          )}
                          {finePreviewSlipId === record.id ? 'Đang kiểm tra' : 'Xác Nhận Trả'}
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

      {returnConfirmModal && (
        <div className="fixed inset-0 bg-black/60 backdrop-blur-xs flex items-center justify-center z-50 p-4 animate-in fade-in duration-150">
          <div className="bg-card rounded-2xl border border-border w-full max-w-md shadow-2xl p-6 space-y-4 animate-in zoom-in-95 duration-150">
            <div className="flex items-center gap-2 text-emerald-600 dark:text-emerald-400 pb-2 border-b border-border">
              <Receipt className="w-5 h-5" />
              <h2 className="text-lg font-bold">Xác Nhận Trả Sách</h2>
            </div>

            <div className="p-4 rounded-xl bg-muted/30 border border-border text-xs space-y-2">
              <div className="flex justify-between gap-4">
                <span className="text-muted-foreground">Mã phiếu:</span>
                <span className="font-bold text-foreground text-right">{returnConfirmModal.record.borrowCode}</span>
              </div>
              <div className="flex justify-between gap-4">
                <span className="text-muted-foreground">Độc giả:</span>
                <span className="font-bold text-foreground text-right">{returnConfirmModal.record.readerName}</span>
              </div>
              <div className="flex justify-between gap-4">
                <span className="text-muted-foreground">Số sách:</span>
                <span className="font-bold text-foreground">{returnConfirmModal.record.totalBooks} cuốn</span>
              </div>
              <div className="flex justify-between gap-4">
                <span className="text-muted-foreground">Hạn trả:</span>
                <span className="font-bold text-foreground text-right">{formatDateTime(returnConfirmModal.record.dueAt)}</span>
              </div>
            </div>

            <div className="p-4 rounded-xl bg-emerald-500/10 border border-emerald-500/20 text-xs space-y-2">
              {returnConfirmModal.finePreview ? (
                <>
                  <div className="flex justify-between gap-4">
                    <span className="text-muted-foreground">Số ngày quá hạn:</span>
                    <span className={returnConfirmModal.finePreview.isOverdue ? 'font-bold text-rose-600 dark:text-rose-400' : 'font-bold text-foreground'}>
                      {returnConfirmModal.finePreview.overdueDays} ngày
                    </span>
                  </div>
                  <div className="flex justify-between gap-4 pt-2 border-t border-emerald-500/20 text-sm">
                    <span className="font-bold text-foreground">Tổng tiền phạt dự kiến:</span>
                    <span className="font-extrabold text-emerald-600 dark:text-emerald-400">
                      {formatCurrency(returnConfirmModal.finePreview.totalFineAmount)}
                    </span>
                  </div>
                  {returnConfirmModal.finePreview.fineReason && (
                    <p className="text-muted-foreground leading-relaxed pt-1">
                      {returnConfirmModal.finePreview.fineReason}
                    </p>
                  )}
                </>
              ) : (
                <p className="text-muted-foreground leading-relaxed">
                  Chưa có preview phí phạt từ backend. Khi xác nhận, kết quả trả sách vẫn do backend xử lý.
                </p>
              )}
            </div>

            <div className="flex gap-3 pt-2">
              <Button
                variant="outline"
                onClick={() => setReturnConfirmModal(null)}
                disabled={returningSlipId !== null}
                className="flex-1 rounded-xl text-xs"
              >
                Hủy
              </Button>
              <Button
                onClick={() => void executeReturn(returnConfirmModal.record)}
                disabled={returningSlipId !== null}
                className="flex-1 bg-emerald-600 hover:bg-emerald-700 text-white rounded-xl text-xs font-bold cursor-pointer"
              >
                {returningSlipId === returnConfirmModal.record.id && (
                  <Loader2 className="w-3.5 h-3.5 mr-1 animate-spin" />
                )}
                Trả Sách
              </Button>
            </div>
          </div>
        </div>
      )}

      {returnedRecords.length > 0 && (
        <div className="bg-card rounded-2xl border border-border shadow-sm overflow-hidden">
          <div className="p-6 border-b border-border/60">
            <h2 className="text-lg font-bold text-foreground">Phiếu Đã RETURNED Gần Đây</h2>
          </div>
          <div className="overflow-x-auto">
            <table className="w-full text-left text-sm">
              <thead className="bg-muted/50 border-b border-border text-xs uppercase text-muted-foreground font-semibold whitespace-nowrap">
                <tr>
                  <th className="px-6 py-4 whitespace-nowrap">Mã Phiếu</th>
                  <th className="px-6 py-4 whitespace-nowrap">Độc Giả</th>
                  <th className="px-6 py-4 whitespace-nowrap">Số Sách</th>
                  <th className="px-6 py-4 whitespace-nowrap">Ngày Mượn</th>
                  <th className="px-6 py-4 text-center whitespace-nowrap">Trạng Thái</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-border">
                {returnedRecords.slice(0, RETURNED_HISTORY_LIMIT).map((record) => (
                  <tr key={record.id} className="hover:bg-muted/20 transition-colors">
                    <td className="px-6 py-4 text-sm font-mono font-bold text-primary whitespace-nowrap">
                      {record.borrowCode || `#${record.id}`}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <div className="font-semibold text-foreground">{record.readerName}</div>
                      <div className="font-mono text-xs text-muted-foreground">{record.readerCardNumber}</div>
                    </td>
                    <td className="px-6 py-4 text-sm font-semibold text-foreground whitespace-nowrap">
                      {record.totalBooks} cuốn
                    </td>
                    <td className="px-6 py-4 text-xs text-muted-foreground whitespace-nowrap">
                      {formatDateTime(record.borrowedAt)}
                    </td>
                    <td className="px-6 py-4 text-center whitespace-nowrap">
                      <StatusPill status={record.status} />
                    </td>
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
