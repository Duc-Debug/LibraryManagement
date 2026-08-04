// PUBLIC API: chỉ export những gì được phép dùng bên ngoài feature "books"

export { BooksPage } from './components/BooksPage';
export { AddBookModal } from './components/AddBookModal';

export { getBooks, mockBooks } from './api/getBooks';

export type { Book } from './types/book.types';
