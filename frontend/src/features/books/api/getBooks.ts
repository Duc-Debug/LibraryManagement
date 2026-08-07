import type { Book } from '../types/book.types';

// Nguồn dữ liệu mock đã được làm sạch
export const mockBooks: Book[] = [];

/** Lấy toàn bộ danh sách sách */
export async function getBooks(): Promise<Book[]> {
  return mockBooks;
}
