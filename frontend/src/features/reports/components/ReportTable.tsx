import React from "react";
import type { BorrowReportItemDto, BorrowSlipStatus, PageResult } from "../../../types/report.types";

interface ReportTableProps {
  items?: PageResult<BorrowReportItemDto>;
  isLoading?: boolean;
  isEmpty?: boolean;
  page: number;
  setPage: (page: number) => void;
  onResetFilter?: () => void;
}

export const ReportTable: React.FC<ReportTableProps> = ({
  items,
  isLoading,
  isEmpty,
  page,
  setPage,
  onResetFilter,
}) => {
  const getStatusBadge = (status: BorrowSlipStatus) => {
    switch (status) {
      case "BORROWING":
        return <span className="px-2 py-0.5 text-xs font-semibold rounded-full bg-blue-100 text-blue-800 dark:bg-blue-900/40 dark:text-blue-300">Đang mượn</span>;
      case "RETURNED":
        return <span className="px-2 py-0.5 text-xs font-semibold rounded-full bg-emerald-100 text-emerald-800 dark:bg-emerald-900/40 dark:text-emerald-300">Đã trả</span>;
      case "OVERDUE":
        return <span className="px-2 py-0.5 text-xs font-semibold rounded-full bg-rose-100 text-rose-800 dark:bg-rose-900/40 dark:text-rose-300">Quá hạn</span>;
      case "CANCELLED":
        return <span className="px-2 py-0.5 text-xs font-semibold rounded-full bg-gray-100 text-gray-800 dark:bg-gray-800 dark:text-gray-300">Đã hủy</span>;
      default:
        return <span className="px-2 py-0.5 text-xs font-semibold rounded-full bg-gray-100 text-gray-800">{status}</span>;
    }
  };

  const formatDate = (dateStr?: string | null) => {
    if (!dateStr) return "-";
    try {
      const d = new Date(dateStr);
      return d.toLocaleDateString("vi-VN") + " " + d.toLocaleTimeString("vi-VN", { hour: "2-digit", minute: "2-digit" });
    } catch {
      return dateStr;
    }
  };

  const formatCurrency = (amount?: number) => {
    if (!amount || amount <= 0) return "0 ₫";
    return new Intl.NumberFormat("vi-VN", { style: "currency", currency: "VND" }).format(amount);
  };

  // Skeleton Loading State
  if (isLoading) {
    return (
      <div className="rounded-xl border border-border bg-card overflow-hidden shadow-sm">
        <div className="p-4 space-y-4">
          {[1, 2, 3, 4, 5].map((i) => (
            <div key={i} className="h-12 w-full bg-muted/60 animate-pulse rounded-lg" />
          ))}
        </div>
      </div>
    );
  }

  // Empty State UI
  if (isEmpty || !items || items.content.length === 0) {
    return (
      <div className="flex flex-col items-center justify-center p-12 text-center rounded-xl border border-dashed border-border bg-card shadow-sm">
        <div className="text-4xl mb-3">📭</div>
        <h3 className="text-lg font-semibold tracking-tight">Không tìm thấy dữ liệu báo cáo</h3>
        <p className="text-sm text-muted-foreground mt-1 max-w-md">
          Không có phiếu mượn-trả nào phù hợp với bộ lọc ngày hoặc từ khóa tìm kiếm của bạn.
        </p>
        {onResetFilter && (
          <button
            onClick={onResetFilter}
            type="button"
            className="mt-4 px-4 py-2 text-sm font-medium text-primary hover:text-primary-foreground border border-primary hover:bg-primary rounded-lg transition-all"
          >
            Đặt lại bộ lọc
          </button>
        )}
      </div>
    );
  }

  return (
    <div className="rounded-xl border border-border bg-card overflow-hidden shadow-sm">
      <div className="overflow-x-auto">
        <table className="w-full text-sm text-left">
          <thead className="text-xs uppercase bg-muted/50 text-muted-foreground border-b border-border">
            <tr>
              <th className="px-4 py-3 font-semibold">STT</th>
              <th className="px-4 py-3 font-semibold">Mã Phiếu</th>
              <th className="px-4 py-3 font-semibold">Thẻ Độc Giả</th>
              <th className="px-4 py-3 font-semibold">Tên Độc Giả</th>
              <th className="px-4 py-3 font-semibold">Danh Sách Sách</th>
              <th className="px-4 py-3 font-semibold text-center">Số Lượng</th>
              <th className="px-4 py-3 font-semibold">Ngày Mượn</th>
              <th className="px-4 py-3 font-semibold">Hạn Trả</th>
              <th className="px-4 py-3 font-semibold">Ngày Trả TT</th>
              <th className="px-4 py-3 font-semibold">Trạng Thái</th>
              <th className="px-4 py-3 font-semibold text-right">Tiền Phạt</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-border">
            {items.content.map((item, idx) => (
              <tr key={item.borrowSlipId} className="hover:bg-muted/30 transition-colors">
                <td className="px-4 py-3 font-medium text-muted-foreground">{page * items.size + idx + 1}</td>
                <td className="px-4 py-3 font-semibold text-primary">{item.borrowCode}</td>
                <td className="px-4 py-3 font-mono text-xs">{item.readerCardCode}</td>
                <td className="px-4 py-3 font-medium">{item.readerName}</td>
                <td className="px-4 py-3 max-w-xs truncate" title={item.bookTitles}>
                  {item.bookTitles}
                </td>
                <td className="px-4 py-3 text-center font-semibold">{item.totalBooks}</td>
                <td className="px-4 py-3 text-xs text-muted-foreground">{formatDate(item.borrowedAt)}</td>
                <td className="px-4 py-3 text-xs text-muted-foreground">{formatDate(item.dueAt)}</td>
                <td className="px-4 py-3 text-xs text-muted-foreground">{formatDate(item.actualReturnedAt)}</td>
                <td className="px-4 py-3">{getStatusBadge(item.status)}</td>
                <td className="px-4 py-3 text-right font-medium text-rose-600 dark:text-rose-400">
                  {formatCurrency(item.fineAmount)}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {/* Pagination Bar */}
      <div className="flex items-center justify-between px-4 py-3 border-t border-border bg-muted/20">
        <div className="text-xs text-muted-foreground">
          Hiển thị trang <span className="font-semibold text-foreground">{items.page + 1}</span> /{" "}
          <span className="font-semibold text-foreground">{items.totalPages || 1}</span> (Tổng{" "}
          <span className="font-semibold text-foreground">{items.totalElements}</span> bản ghi)
        </div>
        <div className="flex gap-2">
          <button
            onClick={() => setPage(Math.max(0, page - 1))}
            disabled={page === 0}
            type="button"
            className="px-3 py-1 text-xs font-medium rounded-md border border-input bg-background hover:bg-accent disabled:opacity-40 disabled:cursor-not-allowed"
          >
            ← Trang trước
          </button>
          <button
            onClick={() => setPage(page + 1)}
            disabled={page + 1 >= items.totalPages}
            type="button"
            className="px-3 py-1 text-xs font-medium rounded-md border border-input bg-background hover:bg-accent disabled:opacity-40 disabled:cursor-not-allowed"
          >
            Trang sau →
          </button>
        </div>
      </div>
    </div>
  );
};
