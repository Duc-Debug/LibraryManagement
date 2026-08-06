import type { ReaderBorrow } from '../types/reader.types';

// Nguồn dữ liệu mock đã được dọn dẹp
export const mockReaderBorrows: ReaderBorrow[] = [];

/** Lấy danh sách sách đang/đã mượn của 1 độc giả */
export async function getReaderBorrows(userId: string): Promise<ReaderBorrow[]> {
  return mockReaderBorrows.filter((b) => b.userId === userId);
}
