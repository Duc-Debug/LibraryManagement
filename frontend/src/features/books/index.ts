// PUBLIC API: chỉ export những gì được phép dùng bên ngoài feature "books"

export { BooksPage } from './components/BooksPage';
export { AddBookModal } from './components/AddBookModal';
export { EditBookModal } from './components/EditBookModal';

export { getBooks, mockBooks } from './api/getBooks';
export { updateBook, validateUpdateBookCommand, UpdateBookValidationError } from './api/updateBook';
export type { UpdateBookCommand, BookResult } from './api/updateBook';

export { BOOK_CATEGORIES, getCategoryById, getCategoryByName } from './data/categories';
export type { BookCategory } from './data/categories';

export type { Book } from './types/book.types';
