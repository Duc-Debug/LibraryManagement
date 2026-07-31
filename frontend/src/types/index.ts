export type Page = "login" | "dashboard" | "books" | "members" | "borrow" | "return";

export type BorrowStatus = "borrowing" | "overdue" | "returned";

export interface Book {
  id: string;
  title: string;
  author: string;
  genre: string;
  total: number;
  available: number;
}

export interface Member {
  id: string;
  name: string;
  email: string;
  phone: string;
  active: boolean;
}

export interface BorrowRecord {
  id: string;
  bookId: string;
  bookTitle: string;
  memberId: string;
  memberName: string;
  borrowDate: string;
  dueDate: string;
  status: BorrowStatus;
}
