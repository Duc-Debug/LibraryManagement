export interface BorrowRecord {
  id: string;
  bookId: string;
  bookTitle: string;
  memberId: string;
  memberName: string;
  borrowDate: string;
  dueDate: string;
  returnDate?: string;
  status: 'borrowing' | 'returned' | 'overdue';
}
