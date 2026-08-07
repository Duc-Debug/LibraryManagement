import type { BorrowRecord } from '../types/borrowing.types';

// Nguồn dữ liệu mock đã được dọn dẹp
export const mockBorrowRecords: BorrowRecord[] = [];

/** Lấy toàn bộ danh sách phiếu mượn */
export async function getBorrowRecords(): Promise<BorrowRecord[]> {
  return mockBorrowRecords;
}
