import type { BorrowRecord } from '../types/borrowing.types';

// Nguồn dữ liệu mock — dùng chung cho cả mượn (borrowing) và trả (returns)
export const mockBorrowRecords: BorrowRecord[] = [
  {
    id: 'borrow1',
    bookId: 'book1',
    bookTitle: 'Những người thừa kế',
    memberId: 'member1',
    memberName: 'Nguyễn Văn An',
    borrowDate: '2024-07-10',
    dueDate: '2024-07-24',
    returnDate: undefined,
    status: 'borrowing',
  },
  {
    id: 'borrow2',
    bookId: 'book2',
    bookTitle: 'Sapiens',
    memberId: 'member2',
    memberName: 'Trần Thị Bình',
    borrowDate: '2024-07-15',
    dueDate: '2024-07-29',
    returnDate: '2024-07-28',
    status: 'returned',
  },
  {
    id: 'borrow3',
    bookId: 'book3',
    bookTitle: 'Đọc vị nhân tâm',
    memberId: 'member3',
    memberName: 'Lê Quốc Cường',
    borrowDate: '2024-06-30',
    dueDate: '2024-07-14',
    returnDate: undefined,
    status: 'overdue',
  },
  {
    id: 'borrow4',
    bookId: 'book5',
    bookTitle: 'Tuổi trẻ đáng giá bao nhiêu',
    memberId: 'member1',
    memberName: 'Nguyễn Văn An',
    borrowDate: '2024-07-12',
    dueDate: '2024-07-26',
    returnDate: undefined,
    status: 'borrowing',
  },
  {
    id: 'borrow5',
    bookId: 'book4',
    bookTitle: 'Nghệ thuật chiến tranh',
    memberId: 'member5',
    memberName: 'Vương Thu Hương',
    borrowDate: '2024-07-05',
    dueDate: '2024-07-19',
    returnDate: '2024-07-18',
    status: 'returned',
  },
];

/** Lấy toàn bộ danh sách phiếu mượn */
export async function getBorrowRecords(): Promise<BorrowRecord[]> {
  return mockBorrowRecords;
}
