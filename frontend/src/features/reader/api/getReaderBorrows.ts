import type { ReaderBorrow } from '../types/reader.types';

// Nguồn dữ liệu mock — sau này có thể thay bằng gọi API thật
export const mockReaderBorrows: ReaderBorrow[] = [
  {
    id: 'RB001',
    userId: 'U003',
    bookId: 'book1',
    bookTitle: 'Những người thừa kế',
    bookAuthor: 'Keynes Piketty',
    borrowDate: '2024-07-20',
    dueDate: '2024-08-03',
    status: 'active',
    daysUntilDue: 3,
  },
  {
    id: 'RB002',
    userId: 'U003',
    bookId: 'book2',
    bookTitle: 'Sapiens',
    bookAuthor: 'Yuval Noah Harari',
    borrowDate: '2024-07-25',
    dueDate: '2024-08-08',
    status: 'active',
    daysUntilDue: 8,
  },
  {
    id: 'RB003',
    userId: 'U003',
    bookId: 'book3',
    bookTitle: 'Đọc vị nhân tâm',
    bookAuthor: 'Joe Navarro',
    borrowDate: '2024-07-10',
    dueDate: '2024-07-28',
    status: 'overdue',
    daysUntilDue: -3,
  },
];

/** Lấy danh sách sách đang/đã mượn của 1 độc giả */
export async function getReaderBorrows(userId: string): Promise<ReaderBorrow[]> {
  return mockReaderBorrows.filter((b) => b.userId === userId);
}
