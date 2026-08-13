import { apiFetch } from "./httpClient";

export type BorrowSlipStatus = "BORROWING" | "RETURNED" | "OVERDUE";

export interface PageResult<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

export interface BorrowSlipResponseDto {
  id: number;
  borrowCode: string;
  readerId: number;
  readerCardNumber: string;
  readerName: string;
  createdByUserId: number;
  createdByUserName: string;
  borrowedAt: string;
  dueAt: string;
  status: BorrowSlipStatus;
  note: string | null;
  totalBooks: number;
  createdAt: string;
}

export interface FineCalculationResponseDto {
  borrowSlipId: number;
  borrowCode: string;
  readerId: number;
  readerCardNumber: string;
  readerName: string;
  borrowedAt: string;
  dueAt: string;
  returnDate: string;
  overdueDays: number;
  totalBooks: number;
  dailyFineRate: number | string;
  overdueFineAmount: number | string;
  totalFineAmount: number | string;
  isOverdue: boolean;
  fineReason: string | null;
}

export interface ReturnBorrowSlipResponseDto {
  borrowSlipId: number;
  status: BorrowSlipStatus;
  returnedBooks: number;
  returnedAt: string;
  totalFineAmount: number | string;
}

export interface BorrowSlipListParams {
  page?: number;
  size?: number;
  status?: BorrowSlipStatus;
  keyword?: string;
}

export async function fetchBorrowSlipsApi({
  page = 0,
  size = 10,
  status,
  keyword,
}: BorrowSlipListParams = {}): Promise<PageResult<BorrowSlipResponseDto>> {
  const queryParams = new URLSearchParams({
    page: String(page),
    size: String(size),
  });

  if (status) {
    queryParams.append("status", status);
  }

  if (keyword?.trim()) {
    queryParams.append("keyword", keyword.trim());
  }

  const raw = await apiFetch<PageResult<BorrowSlipResponseDto>>(
    `/api/librarians/borrow-slips?${queryParams.toString()}`
  );

  return {
    content: Array.isArray(raw.content) ? raw.content : [],
    page: raw.page,
    size: raw.size,
    totalElements: raw.totalElements,
    totalPages: raw.totalPages,
  };
}

export async function previewBorrowSlipFineApi(
  borrowSlipId: number
): Promise<FineCalculationResponseDto> {
  return apiFetch<FineCalculationResponseDto>(
    `/api/librarians/borrow-slips/${borrowSlipId}/fine-preview`
  );
}

export async function returnBorrowSlipApi(
  borrowSlipId: number
): Promise<ReturnBorrowSlipResponseDto> {
  return apiFetch<ReturnBorrowSlipResponseDto>(
    `/api/librarians/borrow-slips/${borrowSlipId}/return`,
    {
      method: "POST",
    }
  );
}

export interface CreateBorrowSlipCommand {
  readerId: number;
  bookIds: number[];
  createdByUserId?: number;
  borrowDays?: number;
  note?: string;
}

export interface OverdueSlipSummaryDto {
  borrowSlipId: number;
  borrowCode: string;
  borrowedAt: string;
  overdueDays: number;
  totalBooks: number;
}

export interface ReaderBorrowEligibilityDto {
  readerId: number;
  cardNumber: string;
  readerName: string;
  currentBorrowingCount: number;
  maxBorrowLimit: number;
  remainingBorrowLimit: number;
  hasOverdueBooks: boolean;
  overdueBooksCount: number;
  overdueSlips: OverdueSlipSummaryDto[];
  eligible: boolean;
  rejectionReason: string | null;
}

export async function checkReaderEligibilityApi(
  readerId: number
): Promise<ReaderBorrowEligibilityDto> {
  return apiFetch<ReaderBorrowEligibilityDto>(
    `/api/librarians/borrow-slips/eligibility/${readerId}`
  );
}

export async function createBorrowSlipApi(
  command: CreateBorrowSlipCommand
): Promise<BorrowSlipResponseDto> {
  return apiFetch<BorrowSlipResponseDto>("/api/librarians/borrow-slips", {
    method: "POST",
    body: JSON.stringify(command),
  });
}
