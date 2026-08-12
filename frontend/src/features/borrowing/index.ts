// PUBLIC API: chỉ export những gì được phép dùng bên ngoài feature "borrowing"

export { BorrowingReturnsPage } from './components/BorrowingReturnsPage';
export { BorrowingForm } from './components/BorrowingForm';

export { getBorrowRecords, mockBorrowRecords } from './api/getBorrowRecords';

export type { BorrowRecord } from './types/borrowing.types';