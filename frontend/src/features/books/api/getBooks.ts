import type { Book } from '../types/book.types';

// Nguồn dữ liệu mock — sau này có thể thay bằng gọi API thật (fetch/axios)
export const mockBooks: Book[] = [
  {
    id: 'book1',
    title: 'Những người thừa kế',
    author: 'Keynes Piketty',
    category: 'Kinh tế',
    publishYear: 2013,
    isbn: 'ISBN-001',
    totalCopies: 3,
    availableCopies: 1,
  },
  {
    id: 'book2',
    title: 'Sapiens',
    author: 'Yuval Noah Harari',
    category: 'Lịch sử',
    publishYear: 2011,
    isbn: 'ISBN-002',
    totalCopies: 5,
    availableCopies: 3,
  },
  {
    id: 'book3',
    title: 'Đọc vị nhân tâm',
    author: 'Joe Navarro',
    category: 'Tâm lý học',
    publishYear: 2008,
    isbn: 'ISBN-003',
    totalCopies: 4,
    availableCopies: 2,
  },
  {
    id: 'book4',
    title: 'Nghệ thuật chiến tranh',
    author: 'Tôn Tử',
    category: 'Chiến lược',
    publishYear: 2020,
    isbn: 'ISBN-004',
    totalCopies: 2,
    availableCopies: 1,
  },
  {
    id: 'book5',
    title: 'Tuổi trẻ đáng giá bao nhiêu',
    author: 'Nguyễn Nhật Ánh',
    category: 'Tiểu thuyết',
    publishYear: 2010,
    isbn: 'ISBN-005',
    totalCopies: 6,
    availableCopies: 2,
  },
  {
    id: 'book6',
    title: 'Lập trình Python',
    author: 'Mark Lutz',
    category: 'Công nghệ',
    publishYear: 2019,
    isbn: 'ISBN-006',
    totalCopies: 3,
    availableCopies: 1,
  },
  {
    id: 'book7',
    title: 'Những bí mật tâm lý',
    author: 'Robert Cialdini',
    category: 'Tâm lý học',
    publishYear: 2009,
    isbn: 'ISBN-007',
    totalCopies: 4,
    availableCopies: 3,
  },
  {
    id: 'book8',
    title: 'Hành trình tới phía tây',
    author: 'Jack Kerouac',
    category: 'Tiểu thuyết',
    publishYear: 2015,
    isbn: 'ISBN-008',
    totalCopies: 2,
    availableCopies: 2,
  },
];

/** Lấy toàn bộ danh sách sách */
export async function getBooks(): Promise<Book[]> {
  return mockBooks;
}
