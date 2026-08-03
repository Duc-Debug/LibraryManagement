export interface ReaderProfile {
  userId: string;
  borrowLimit: number;
  currentBorrows: number;
  reservationLimit: number;
  currentReservations: number;
}

export interface ReaderBorrow {
  id: string;
  userId: string;
  bookId: string;
  bookTitle: string;
  bookAuthor: string;
  borrowDate: string;
  dueDate: string;
  returnDate?: string;
  status: 'active' | 'overdue' | 'returned';
  daysUntilDue?: number;
}
