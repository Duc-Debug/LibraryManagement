import { apiFetch, getDynamicApiBaseUrl } from "./httpClient";
import type {
  BorrowReportQuery,
  BorrowReportResultDto,
  ReportFormat,
} from "../types/report.types";

export async function fetchBorrowReportApi(
  query: BorrowReportQuery = {}
): Promise<BorrowReportResultDto> {
  const queryParams = new URLSearchParams();

  if (query.startDate) queryParams.append("startDate", query.startDate);
  if (query.endDate) queryParams.append("endDate", query.endDate);
  if (query.status) queryParams.append("status", query.status);
  if (query.keyword?.trim()) queryParams.append("keyword", query.keyword.trim());

  queryParams.append("page", String(query.page ?? 0));
  queryParams.append("size", String(query.size ?? 10));

  const endpoint = `/api/admin/reports/borrow-return?${queryParams.toString()}`;
  return apiFetch<BorrowReportResultDto>(endpoint);
}

export async function downloadBorrowReportCsvApi(
  query: Omit<BorrowReportQuery, "page" | "size"> = {},
  format: ReportFormat = "CSV"
): Promise<void> {
  const queryParams = new URLSearchParams();

  if (query.startDate) queryParams.append("startDate", query.startDate);
  if (query.endDate) queryParams.append("endDate", query.endDate);
  if (query.status) queryParams.append("status", query.status);
  if (query.keyword?.trim()) queryParams.append("keyword", query.keyword.trim());

  queryParams.append("format", format);

  const baseUrl = getDynamicApiBaseUrl();
  const token = typeof window !== "undefined" ? localStorage.getItem("accessToken") : null;

  const response = await fetch(
    `${baseUrl}/api/admin/reports/borrow-return/export?${queryParams.toString()}`,
    {
      method: "GET",
      headers: {
        Authorization: token ? `Bearer ${token}` : "",
      },
    }
  );

  if (!response.ok) {
    throw new Error(`Export failed with status: ${response.status}`);
  }

  const blob = await response.blob();
  const downloadUrl = window.URL.createObjectURL(blob);
  const dateStr = new Date().toISOString().slice(0, 10).replace(/-/g, "");
  const extension = format === "EXCEL" ? ".xlsx" : ".csv";
  const filename = `bao-cao-muon-tra-${dateStr}${extension}`;

  const link = document.createElement("a");
  link.href = downloadUrl;
  link.setAttribute("download", filename);
  document.body.appendChild(link);
  link.click();

  link.remove();
  window.URL.revokeObjectURL(downloadUrl);
}
