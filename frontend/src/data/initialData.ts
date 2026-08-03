import type { Book, Member, BorrowRecord, UserAccount } from "@/types";

export const INITIAL_USER_ACCOUNTS: UserAccount[] = [
  { id: "U001", username: "admin", password: "admin", fullName: "Quản trị viên", role: "thu_thu", active: true },
  { id: "U002", username: "thuthu", password: "thuthu123", fullName: "Nguyễn Thị Thu", role: "thu_thu", active: true },
  { id: "U003", username: "user1", password: "user123", fullName: "Trần Văn Bình", role: "nguoi_dung", active: true },
  { id: "U004", username: "user2", password: "user456", fullName: "Lê Thị Mai", role: "nguoi_dung", active: false },
];

export const INITIAL_BOOKS: Book[] = [
  { id: "B001", title: "Sapiens: Lược Sử Loài Người", author: "Yuval Noah Harari", genre: "Lịch sử", total: 5, available: 3 },
  { id: "B002", title: "Tuổi Trẻ Đáng Giá Bao Nhiêu", author: "Rosie Nguyễn", genre: "Kỹ năng sống", total: 10, available: 8 },
  { id: "B003", title: "Đắc Nhân Tâm", author: "Dale Carnegie", genre: "Tâm lý", total: 14, available: 4 },
];

export const INITIAL_MEMBERS: Member[] = [
  { id: "M001", name: "Nguyễn Văn An", email: "an.nguyen@example.com", phone: "0901234567", active: true },
  { id: "M002", name: "Trần Thị Bình", email: "binh.tran@example.com", phone: "0912345678", active: true },
  { id: "M003", name: "Lê Hoàng Nam", email: "nam.le@example.com", phone: "0923456789", active: false },
];

export const INITIAL_BORROW_RECORDS: BorrowRecord[] = [
  {
    id: "P001",
    bookId: "B001",
    bookTitle: "Sapiens",
    memberId: "M002",
    memberName: "Trần Thị Bình",
    borrowDate: "2024-07-20",
    dueDate: "2024-07-28",
    status: "returned",
  },
  {
    id: "P002",
    bookId: "B002",
    bookTitle: "Tuổi trẻ đáng giá bao nhiêu",
    memberId: "M001",
    memberName: "Nguyễn Văn An",
    borrowDate: "2024-07-12",
    dueDate: "2024-08-12",
    status: "borrowing",
  },
  {
    id: "P003",
    bookId: "B003",
    bookTitle: "Đắc Nhân Tâm",
    memberId: "M003",
    memberName: "Lê Hoàng Nam",
    borrowDate: "2024-06-01",
    dueDate: "2024-06-30",
    status: "overdue",
  },
];
