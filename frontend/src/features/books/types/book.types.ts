export interface Book {
  id: string;
  title: string;
  author: string;
  category: string;
  publishYear: number;
  isbn: string;
  totalCopies: number;
  availableCopies: number;
  description?: string;
  coverImage?: string;
  rating?: number;
}
