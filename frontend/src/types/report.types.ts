export type BorrowSlipStatus = "BORROWING" | "RETURNED" | "OVERDUE" | "CANCELLED";

export type ReportFormat = "CSV" | "EXCEL" | "PDF";

export interface BorrowReportQuery {
  startDate?: string;
  endDate?: string;
  status?: BorrowSlipStatus;
  keyword?: string;
  page?: number;
  size?: number;
}

export interface BorrowReportItemDto {
  borrowSlipId: number;
  borrowCode: string;
  readerCardCode: string;
  readerName: string;
  bookTitles: string;
  totalBooks: number;
  borrowedAt: string;
  dueAt: string;
  actualReturnedAt?: string | null;
  status: BorrowSlipStatus;
  fineAmount: number;
  createdByUserName: string;
}

export interface BorrowReportSummaryDto {
  totalSlips: number;
  totalBooksBorrowed: number;
  totalReturned: number;
  totalOverdue: number;
  totalBorrowing: number;
  totalFineAmount: number;
}

export interface PageResult<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

export interface BorrowReportResultDto {
  summary: BorrowReportSummaryDto;
  items: PageResult<BorrowReportItemDto>;
}
