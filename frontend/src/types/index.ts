export type Page = "login" | "dashboard" | "books" | "categories" | "members" | "borrow" | "return" | "accounts" | "settings";

export type UserRole = "admin" | "thu_thu";

export interface UserAccount {
  id: string;
  username: string;
  password: string;
  fullName: string;
  email?: string;
  phone?: string;
  role: UserRole;
  active: boolean;
}

export interface Category {
  id: number;
  name: string;
  description?: string;
  active: boolean;
  createdAt?: string;
  updatedAt?: string;
}

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
