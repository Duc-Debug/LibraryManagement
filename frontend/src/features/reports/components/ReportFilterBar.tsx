import React from "react";
import type { BorrowSlipStatus } from "../../../types/report.types";

interface ReportFilterBarProps {
  startDate: string;
  setStartDate: (val: string) => void;
  endDate: string;
  setEndDate: (val: string) => void;
  status?: BorrowSlipStatus;
  setStatus: (val?: BorrowSlipStatus) => void;
  keyword: string;
  setKeyword: (val: string) => void;
  onReset: () => void;
  onExportCsv: () => void;
  isExporting?: boolean;
}

export const ReportFilterBar: React.FC<ReportFilterBarProps> = ({
  startDate,
  setStartDate,
  endDate,
  setEndDate,
  status,
  setStatus,
  keyword,
  setKeyword,
  onReset,
  onExportCsv,
  isExporting = false,
}) => {
  return (
    <div className="flex flex-wrap items-center justify-between gap-4 p-4 mb-6 rounded-xl border border-border bg-card shadow-sm">
      <div className="flex flex-wrap items-center gap-3">
        {/* Từ ngày */}
        <div className="flex flex-col gap-1">
          <label className="text-xs font-medium text-muted-foreground">Từ ngày</label>
          <input
            type="date"
            value={startDate}
            onChange={(e) => setStartDate(e.target.value)}
            className="px-3 py-1.5 text-sm rounded-lg border border-input bg-background focus:outline-none focus:ring-2 focus:ring-primary"
          />
        </div>

        {/* Đến ngày */}
        <div className="flex flex-col gap-1">
          <label className="text-xs font-medium text-muted-foreground">Đến ngày</label>
          <input
            type="date"
            value={endDate}
            onChange={(e) => setEndDate(e.target.value)}
            className="px-3 py-1.5 text-sm rounded-lg border border-input bg-background focus:outline-none focus:ring-2 focus:ring-primary"
          />
        </div>

        {/* Trạng thái */}
        <div className="flex flex-col gap-1">
          <label className="text-xs font-medium text-muted-foreground">Trạng thái</label>
          <select
            value={status || ""}
            onChange={(e) => setStatus(e.target.value ? (e.target.value as BorrowSlipStatus) : undefined)}
            className="px-3 py-1.5 text-sm rounded-lg border border-input bg-background focus:outline-none focus:ring-2 focus:ring-primary"
          >
            <option value="">-- Tất cả --</option>
            <option value="BORROWING">Đang mượn</option>
            <option value="RETURNED">Đã trả</option>
            <option value="OVERDUE">Quá hạn</option>
          </select>
        </div>

        {/* Tìm kiếm */}
        <div className="flex flex-col gap-1">
          <label className="text-xs font-medium text-muted-foreground">Từ khóa</label>
          <input
            type="text"
            placeholder="Tên độc giả, Mã thẻ..."
            value={keyword}
            onChange={(e) => setKeyword(e.target.value)}
            className="px-3 py-1.5 text-sm rounded-lg border border-input bg-background focus:outline-none focus:ring-2 focus:ring-primary w-48 sm:w-64"
          />
        </div>

        {/* Reset button */}
        <div className="flex items-end self-end">
          <button
            onClick={onReset}
            type="button"
            className="px-3 py-1.5 text-sm font-medium text-muted-foreground hover:text-foreground rounded-lg border border-input bg-background hover:bg-accent transition-colors"
          >
            🔄 Đặt lại
          </button>
        </div>
      </div>

      {/* Export CSV button */}
      <div className="flex items-end self-end ml-auto">
        <button
          onClick={onExportCsv}
          disabled={isExporting}
          type="button"
          className="flex items-center gap-2 px-4 py-2 text-sm font-medium text-white bg-emerald-600 hover:bg-emerald-700 disabled:opacity-50 disabled:cursor-not-allowed rounded-lg shadow-sm transition-all"
        >
          {isExporting ? (
            <>
              <span className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin" />
              <span>Đang xuất file...</span>
            </>
          ) : (
            <>
              <span>📥</span>
              <span>Xuất File CSV</span>
            </>
          )}
        </button>
      </div>
    </div>
  );
};
