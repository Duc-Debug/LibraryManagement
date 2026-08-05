export interface Book {
  id: string;
  title: string;
  author: string;
  category: string;
  categoryId?: string;
  publishYear: number;
  isbn: string;
  totalCopies: number;
  availableCopies: number;
  description?: string;
  coverImage?: string;
  publisher?: string;
  shelfLocation?: string;
  rating?: number;
  active?: boolean;
  createdAt?: string;
  updatedAt?: string;
}
