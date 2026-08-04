export interface ActivityLog {
  id: string;
  bookTitle: string;
  memberName: string;
  action: string;
  date: string;
  status: 'borrowing' | 'returned' | 'overdue';
}
