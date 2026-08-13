import React from "react";
import { useBorrowReport } from "../hooks/useBorrowReport";
import { ReportKpiCards } from "../components/ReportKpiCards";
import { ReportFilterBar } from "../components/ReportFilterBar";
import { ReportTable } from "../components/ReportTable";

export const BorrowReportPage: React.FC = () => {
  const {
    startDate,
    setStartDate,
    endDate,
    setEndDate,
    status,
    setStatus,
    keyword,
    setKeyword,
    page,
    setPage,
    handleResetFilter,
    data,
    isLoading,
    error,
    isExporting,
    isEmpty,
    handleExportCsv,
  } = useBorrowReport();

  return (
    <div className="p-6 max-w-7xl mx-auto space-y-6">
      {/* Header */}
      <div className="flex flex-col gap-1">
        <h1 className="text-2xl font-bold tracking-tight text-foreground">Báo Cáo Mượn - Trả Sách</h1>
        <p className="text-sm text-muted-foreground">
          Thống kê danh sách phiếu mượn-trả, tính toán các chỉ số KPI tổng quan và xuất file báo cáo CSV cho Admin.
        </p>
      </div>

      {/* Error Alert */}
      {error && (
        <div className="p-4 rounded-xl border border-rose-200 bg-rose-50 text-rose-800 dark:border-rose-900 dark:bg-rose-950/40 dark:text-rose-300 text-sm flex items-center justify-between">
          <span>⚠️ {error}</span>
          <button
            onClick={() => handleResetFilter()}
            type="button"
            className="text-xs font-semibold underline hover:no-underline"
          >
            Thử lại
          </button>
        </div>
      )}

      {/* KPI Cards */}
      <ReportKpiCards summary={data?.summary} isLoading={isLoading} />

      {/* Filter Bar */}
      <ReportFilterBar
        startDate={startDate}
        setStartDate={setStartDate}
        endDate={endDate}
        setEndDate={setEndDate}
        status={status}
        setStatus={setStatus}
        keyword={keyword}
        setKeyword={setKeyword}
        onReset={handleResetFilter}
        onExportCsv={() => handleExportCsv("CSV")}
        isExporting={isExporting}
      />

      {/* Data Table */}
      <ReportTable
        items={data?.items}
        isLoading={isLoading}
        isEmpty={isEmpty}
        page={page}
        setPage={setPage}
        onResetFilter={handleResetFilter}
      />
    </div>
  );
};
