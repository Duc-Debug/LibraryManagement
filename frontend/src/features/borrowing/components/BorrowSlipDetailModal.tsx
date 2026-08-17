'use client';

import { useState, useEffect } from 'react';
import {
  X,
  BookOpen,
  User,
  Calendar,
  Clock,
  AlertTriangle,
  CheckCircle2,
  Receipt,
  FileText,
  Check,
  Loader2,
  Book,
} from 'lucide-react';
import { Button } from '@/components/ui/button';
import {
  fetchBorrowSlipDetailApi,
  BorrowSlipDetailResponseDto,
  BorrowSlipResponseDto,
} from '@/api/borrowSlipApi';
import { parseErrorMessage } from '@/lib/errorDictionary';

interface BorrowSlipDetailModalProps {
  borrowSlipId: number | null;
  isOpen: boolean;
  onClose: () => void;
  onInitiateReturn?: (slip: BorrowSlipResponseDto) => void;
}

export function BorrowSlipDetailModal({
  borrowSlipId,
  isOpen,
  onClose,
  onInitiateReturn,
}: BorrowSlipDetailModalProps) {
  const [detail, setDetail] = useState<BorrowSlipDetailResponseDto | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (isOpen && borrowSlipId) {
      setLoading(true);
      setError(null);
      fetchBorrowSlipDetailApi(borrowSlipId)
        .then((data) => setDetail(data))
        .catch((err) => setError(parseErrorMessage(err, 'Không thể lấy thông tin chi tiết phiếu mượn.')))
        .finally(() => setLoading(false));
    } else {
      setDetail(null);
    }
  }, [isOpen, borrowSlipId]);

  if (!isOpen) return null;

  const renderStatusBadge = (status?: string) => {
    if (status === 'OVERDUE') {
      return (
        <span className="inline-flex items-center gap-1 px-3 py-1 rounded-full text-xs font-semibold bg-rose-500/10 text-rose-600 dark:text-rose-400 border border-rose-500/20">
          <AlertTriangle className="w-3.5 h-3.5" /> Quá hạn
        </span>
      );
    }
    if (status === 'RETURNED') {
      return (
        <span className="inline-flex items-center gap-1 px-3 py-1 rounded-full text-xs font-semibold bg-emerald-500/10 text-emerald-600 dark:text-emerald-400 border border-emerald-500/20">
          <CheckCircle2 className="w-3.5 h-3.5" /> Đã trả
        </span>
      );
    }
    return (
      <span className="inline-flex items-center gap-1 px-3 py-1 rounded-full text-xs font-semibold bg-primary/10 text-primary border border-primary/20">
        <Clock className="w-3.5 h-3.5" /> Đang mượn
      </span>
    );
  };

  return (
    <div className="fixed inset-0 bg-black/60 backdrop-blur-xs flex items-center justify-center z-50 p-4 animate-in fade-in duration-150">
      <div className="bg-card rounded-2xl border border-border w-full max-w-3xl shadow-2xl overflow-hidden flex flex-col max-h-[90vh] animate-in zoom-in-95 duration-150">
        {/* Modal Header */}
        <div className="flex items-center justify-between px-6 py-4 border-b border-border bg-muted/30">
          <div className="flex items-center gap-3">
            <div className="p-2.5 rounded-xl bg-primary/10 text-primary">
              <Receipt className="w-6 h-6" />
            </div>
            <div>
              <h2 className="text-lg font-bold text-foreground flex items-center gap-2">
                <span>Chi Tiết Phiếu Mượn Sách</span>
                {detail && (
                  <span className="font-mono text-primary text-base font-extrabold">
                    {detail.borrowCode || `#${detail.id}`}
                  </span>
                )}
              </h2>
              <p className="text-xs text-muted-foreground">
                Xem thông tin phiếu mượn và danh sách chi tiết các cuốn sách đối chiếu.
              </p>
            </div>
          </div>
          <div className="flex items-center gap-2">
            {detail && renderStatusBadge(detail.status)}
            <Button
              variant="ghost"
              size="sm"
              onClick={onClose}
              className="h-8 w-8 p-0 rounded-xl hover:bg-muted text-muted-foreground cursor-pointer"
            >
              <X className="w-4 h-4" />
            </Button>
          </div>
        </div>

        {/* Modal Body */}
        <div className="p-6 overflow-y-auto space-y-6 flex-1 text-sm">
          {loading && (
            <div className="py-16 text-center text-muted-foreground space-y-3">
              <Loader2 className="w-8 h-8 animate-spin mx-auto text-primary" />
              <p className="text-xs font-medium">Đang tải chi tiết phiếu mượn...</p>
            </div>
          )}

          {error && (
            <div className="p-4 bg-destructive/10 text-destructive rounded-xl border border-destructive/20 text-sm flex items-center justify-between">
              <span>{error}</span>
            </div>
          )}

          {!loading && detail && (
            <>
              {/* Slip & Reader Summary */}
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4 p-4 rounded-xl bg-muted/30 border border-border">
                <div className="space-y-2">
                  <div className="flex items-center gap-2 text-xs font-semibold uppercase text-muted-foreground tracking-wider">
                    <User className="w-3.5 h-3.5 text-primary" />
                    <span>Thông tin Độc giả</span>
                  </div>
                  <div className="text-foreground">
                    <p className="font-bold text-base">{detail.readerName}</p>
                    <p className="text-xs text-muted-foreground font-mono mt-0.5">
                      Mã thẻ: <strong className="text-foreground">{detail.readerCardNumber}</strong>
                    </p>
                  </div>
                </div>

                <div className="space-y-2 border-t md:border-t-0 md:border-l border-border pt-3 md:pt-0 md:pl-4">
                  <div className="flex items-center gap-2 text-xs font-semibold uppercase text-muted-foreground tracking-wider">
                    <Calendar className="w-3.5 h-3.5 text-primary" />
                    <span>Thông tin Mượn sách</span>
                  </div>
                  <div className="grid grid-cols-2 gap-2 text-xs">
                    <div>
                      <span className="text-muted-foreground block">Ngày mượn:</span>
                      <span className="font-semibold text-foreground">
                        {detail.borrowedAt ? new Date(detail.borrowedAt).toLocaleDateString('vi-VN') : 'N/A'}
                      </span>
                    </div>
                    <div>
                      <span className="text-muted-foreground block">Ngày hẹn trả:</span>
                      <span
                        className={`font-semibold ${
                          detail.status === 'OVERDUE' ? 'text-rose-600 dark:text-rose-400 font-bold' : 'text-emerald-600 dark:text-emerald-400'
                        }`}
                      >
                        {detail.dueAt ? new Date(detail.dueAt).toLocaleDateString('vi-VN') : 'N/A'}
                      </span>
                    </div>
                    <div>
                      <span className="text-muted-foreground block">Người lập phiếu:</span>
                      <span className="font-medium text-foreground">{detail.createdByUserName || 'N/A'}</span>
                    </div>
                    <div>
                      <span className="text-muted-foreground block">Tổng số sách:</span>
                      <span className="font-bold text-primary font-mono">{detail.totalBooks} cuốn</span>
                    </div>
                  </div>
                </div>

                {detail.note && (
                  <div className="col-span-1 md:col-span-2 pt-2 border-t border-border/60 text-xs flex items-start gap-2 text-muted-foreground">
                    <FileText className="w-4 h-4 text-primary shrink-0 mt-0.5" />
                    <span>
                      <strong className="text-foreground">Ghi chú:</strong> {detail.note}
                    </span>
                  </div>
                )}
              </div>

              {/* Books List Section */}
              <div className="space-y-3">
                <div className="flex items-center justify-between">
                  <h3 className="font-bold text-foreground text-sm flex items-center gap-2">
                    <BookOpen className="w-4 h-4 text-primary" />
                    <span>Danh Sách Sách Trong Phiếu ({detail.items?.length || 0} cuốn)</span>
                  </h3>
                </div>

                <div className="border border-border rounded-xl overflow-hidden shadow-xs">
                  <table className="w-full text-left text-xs">
                    <thead className="bg-muted/60 text-muted-foreground uppercase font-semibold border-b border-border">
                      <tr>
                        <th className="px-4 py-3 w-12 text-center">Bìa</th>
                        <th className="px-4 py-3">Tên Sách & Tác Giả</th>
                        <th className="px-4 py-3">Thể Loại</th>
                        <th className="px-4 py-3">Mã ISBN</th>
                        <th className="px-4 py-3 text-right">Trạng Thái Sách</th>
                      </tr>
                    </thead>
                    <tbody className="divide-y divide-border">
                      {detail.items && detail.items.length > 0 ? (
                        detail.items.map((item) => (
                          <tr key={item.id} className="hover:bg-muted/20 transition-colors">
                            <td className="px-4 py-3 text-center">
                              {item.coverUrl ? (
                                <img
                                  src={item.coverUrl}
                                  alt={item.bookTitle}
                                  className="w-8 h-11 object-cover rounded-md border border-border mx-auto shadow-xs"
                                />
                              ) : (
                                <div className="w-8 h-11 bg-primary/10 text-primary rounded-md border border-primary/20 flex items-center justify-center mx-auto">
                                  <Book className="w-4 h-4" />
                                </div>
                              )}
                            </td>
                            <td className="px-4 py-3">
                              <div className="font-bold text-foreground text-sm line-clamp-1">{item.bookTitle}</div>
                              <div className="text-muted-foreground text-xs mt-0.5">
                                Tác giả: <span className="font-medium text-foreground">{item.author || 'Không rõ'}</span>
                              </div>
                            </td>
                            <td className="px-4 py-3 font-medium text-muted-foreground whitespace-nowrap">
                              {item.categoryName || 'Mặc định'}
                            </td>
                            <td className="px-4 py-3 font-mono text-xs text-muted-foreground whitespace-nowrap">
                              {item.isbn || 'N/A'}
                            </td>
                            <td className="px-4 py-3 text-right whitespace-nowrap">
                              {item.returnedAt ? (
                                <span className="inline-flex items-center gap-1 text-emerald-600 dark:text-emerald-400 font-semibold bg-emerald-500/10 px-2 py-0.5 rounded-md">
                                  <Check className="w-3 h-3" /> Đã trả (
                                  {new Date(item.returnedAt).toLocaleDateString('vi-VN')})
                                </span>
                              ) : (
                                <span className="inline-flex items-center gap-1 text-primary font-semibold bg-primary/10 px-2 py-0.5 rounded-md">
                                  <Clock className="w-3 h-3" /> Đang mượn
                                </span>
                              )}
                            </td>
                          </tr>
                        ))
                      ) : (
                        <tr>
                          <td colSpan={5} className="p-6 text-center text-muted-foreground">
                            Không có thông tin chi tiết sách.
                          </td>
                        </tr>
                      )}
                    </tbody>
                  </table>
                </div>
              </div>
            </>
          )}
        </div>

        {/* Modal Footer */}
        <div className="px-6 py-4 border-t border-border bg-muted/20 flex items-center justify-between">
          <Button variant="outline" onClick={onClose} className="rounded-xl text-xs">
            Đóng
          </Button>

          {detail && detail.status !== 'RETURNED' && onInitiateReturn && (
            <Button
              onClick={() => {
                onClose();
                onInitiateReturn(detail);
              }}
              className="bg-emerald-600 hover:bg-emerald-700 text-white rounded-xl text-xs font-bold flex items-center gap-1.5 shadow-sm cursor-pointer"
            >
              <Check className="w-4 h-4" />
              Xác Nhận Trả Sách
            </Button>
          )}
        </div>
      </div>
    </div>
  );
}
