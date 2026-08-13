import { useState, useEffect, useCallback } from "react";
import type {
  BorrowReportQuery,
  BorrowReportResultDto,
  BorrowSlipStatus,
  ReportFormat,
} from "../../../types/report.types";
import {
  fetchBorrowReportApi,
  downloadBorrowReportCsvApi,
} from "../../../api/reportApi";

export function useBorrowReport() {
  const [startDate, setStartDate] = useState<string>("");
  const [endDate, setEndDate] = useState<string>("");
  const [status, setStatus] = useState<BorrowSlipStatus | undefined>(undefined);
  const [keyword, setKeyword] = useState<string>("");
  const [debouncedKeyword, setDebouncedKeyword] = useState<string>("");
  const [page, setPage] = useState<number>(0);
  const [size, setSize] = useState<number>(10);

  // 5 UI States
  const [data, setData] = useState<BorrowReportResultDto | null>(null);
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const [isExporting, setIsExporting] = useState<boolean>(false);

  // 1. Debounce keyword 400ms
  useEffect(() => {
    const timer = setTimeout(() => {
      setDebouncedKeyword(keyword);
      setPage(0); // Reset trang về 1 khi đổi từ khóa tìm kiếm
    }, 400);
    return () => clearTimeout(timer);
  }, [keyword]);

  // 2. Fetch API với validation startDate <= endDate
  const loadReport = useCallback(async () => {
    setError(null);

    // Validation Client-side khoảng ngày
    if (startDate && endDate && new Date(startDate) > new Date(endDate)) {
      setError("Ngày bắt đầu không được lớn hơn ngày kết thúc.");
      setIsLoading(false);
      setData(null);
      return;
    }

    setIsLoading(true);
    try {
      const query: BorrowReportQuery = {
        startDate: startDate || undefined,
        endDate: endDate || undefined,
        status: status || undefined,
        keyword: debouncedKeyword.trim() || undefined,
        page,
        size,
      };

      const result = await fetchBorrowReportApi(query);
      setData(result);
    } catch (err: any) {
      setError(err?.message || "Có lỗi xảy ra khi tải dữ liệu báo cáo.");
      setData(null);
    } finally {
      setIsLoading(false);
    }
  }, [startDate, endDate, status, debouncedKeyword, page, size]);

  useEffect(() => {
    loadReport();
  }, [loadReport]);

  const handleExportCsv = async (format: ReportFormat = "CSV") => {
    if (isExporting) return;

    if (startDate && endDate && new Date(startDate) > new Date(endDate)) {
      alert("Không thể xuất báo cáo: Ngày bắt đầu lớn hơn ngày kết thúc.");
      return;
    }

    setIsExporting(true);
    try {
      await downloadBorrowReportCsvApi(
        {
          startDate: startDate || undefined,
          endDate: endDate || undefined,
          status: status || undefined,
          keyword: debouncedKeyword.trim() || undefined,
        },
        format
      );
    } catch (err: any) {
      alert("Xuất file thất bại: " + (err?.message || "Không thể tải file báo cáo"));
    } finally {
      setIsExporting(false);
    }
  };

  const handleResetFilter = () => {
    setStartDate("");
    setEndDate("");
    setStatus(undefined);
    setKeyword("");
    setDebouncedKeyword("");
    setPage(0);
  };

  const isEmpty = !isLoading && (!data || !data.items || data.items.content.length === 0);

  return {
    // Filters & Pagination State
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
    size,
    setSize,
    handleResetFilter,

    // UI States
    data,
    isLoading,
    error,
    isExporting,
    isEmpty,

    // Actions
    refetch: loadReport,
    handleExportCsv,
  };
}
