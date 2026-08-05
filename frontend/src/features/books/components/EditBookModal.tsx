'use client';

import { useState } from 'react';
import type { Book } from '../types/book.types';
import { BOOK_CATEGORIES, getCategoryById } from '../data/categories';
import { updateBook, UpdateBookValidationError, type UpdateBookCommand } from '../api/updateBook';
import { X, Loader2 } from 'lucide-react';
import { Button } from '@/components/ui/button';

interface EditBookModalProps {
  book: Book;
  existingBooks: Book[];
  onClose: () => void;
  onSave: (updatedBook: Book) => void;
}

type FormErrors = Partial<Record<keyof UpdateBookCommand, string>>;

export function EditBookModal({ book, existingBooks, onClose, onSave }: EditBookModalProps) {
  const borrowedCount = book.totalCopies - book.availableCopies;

  const [formData, setFormData] = useState<UpdateBookCommand>({
    bookId: book.id,
    title: book.title,
    author: book.author,
    isbn: book.isbn,
    publishedYear: book.publishYear,
    totalQuantity: book.totalCopies,
    categoryId: book.categoryId ?? '',
    description: book.description ?? '',
    coverImageUrl: book.coverImage ?? '',
    publisher: book.publisher ?? '',
    shelfLocation: book.shelfLocation ?? '',
  });
  const [errors, setErrors] = useState<FormErrors>({});
  const [submitError, setSubmitError] = useState('');
  const [isSaving, setIsSaving] = useState(false);

  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>
  ) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: name === 'publishedYear' || name === 'totalQuantity' ? Number(value) : value,
    }));
    setErrors((prev) => ({ ...prev, [name]: undefined }));
    setSubmitError('');
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsSaving(true);
    setSubmitError('');

    try {
      const result = await updateBook(formData, existingBooks);
      const category = getCategoryById(result.categoryId);

      const updatedBook: Book = {
        ...book,
        title: result.title,
        author: result.author,
        isbn: result.isbn,
        category: category?.name ?? book.category,
        categoryId: result.categoryId,
        publishYear: result.publishedYear,
        totalCopies: result.totalQuantity,
        availableCopies: result.availableQuantity,
        description: result.description,
        coverImage: result.coverImageUrl,
        publisher: result.publisher,
        shelfLocation: result.shelfLocation,
        active: result.active,
        createdAt: result.createdAt,
        updatedAt: result.updatedAt,
      };

      onSave(updatedBook);
    } catch (err) {
      if (err instanceof UpdateBookValidationError) {
        setErrors(err.fieldErrors);
      } else {
        setSubmitError(err instanceof Error ? err.message : 'Không thể cập nhật sách lúc này.');
      }
    } finally {
      setIsSaving(false);
    }
  };

  const inputClass = (hasError?: string) =>
    `w-full px-3 py-2 border rounded-lg bg-background text-foreground placeholder-muted-foreground focus:outline-none focus:ring-2 focus:ring-primary ${
      hasError ? 'border-destructive' : 'border-border'
    }`;

  return (
    <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4 overflow-y-auto">
      <div className="bg-card rounded-lg border border-border w-full max-w-lg shadow-lg my-8">
        {/* Header */}
        <div className="flex items-center justify-between p-6 border-b border-border">
          <h2 className="text-lg font-bold text-foreground">Sửa thông tin sách</h2>
          <button onClick={onClose} className="text-muted-foreground hover:text-foreground">
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Form */}
        <form onSubmit={handleSubmit} className="p-6 space-y-4 max-h-[70vh] overflow-y-auto">
          {submitError && (
            <div className="text-sm text-destructive bg-red-50 px-3 py-2 rounded-lg border border-red-200">
              {submitError}
            </div>
          )}

          <div>
            <label className="block text-sm font-medium text-foreground mb-1">Tên sách *</label>
            <input
              type="text"
              name="title"
              value={formData.title}
              onChange={handleChange}
              placeholder="Nhập tên sách"
              className={inputClass(errors.title)}
            />
            {errors.title && <p className="text-xs text-destructive mt-1">{errors.title}</p>}
          </div>

          <div>
            <label className="block text-sm font-medium text-foreground mb-1">Tác giả *</label>
            <input
              type="text"
              name="author"
              value={formData.author}
              onChange={handleChange}
              placeholder="Nhập tên tác giả"
              className={inputClass(errors.author)}
            />
            {errors.author && <p className="text-xs text-destructive mt-1">{errors.author}</p>}
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-foreground mb-1">Thể loại *</label>
              <select
                name="categoryId"
                value={formData.categoryId}
                onChange={handleChange}
                className={inputClass(errors.categoryId)}
              >
                <option value="">-- Chọn thể loại --</option>
                {BOOK_CATEGORIES.map((cat) => (
                  <option key={cat.id} value={cat.id}>
                    {cat.name}
                  </option>
                ))}
              </select>
              {errors.categoryId && <p className="text-xs text-destructive mt-1">{errors.categoryId}</p>}
            </div>

            <div>
              <label className="block text-sm font-medium text-foreground mb-1">Năm xuất bản *</label>
              <input
                type="number"
                name="publishedYear"
                value={formData.publishedYear}
                onChange={handleChange}
                className={inputClass(errors.publishedYear)}
              />
              {errors.publishedYear && (
                <p className="text-xs text-destructive mt-1">{errors.publishedYear}</p>
              )}
            </div>
          </div>

          <div>
            <label className="block text-sm font-medium text-foreground mb-1">ISBN *</label>
            <input
              type="text"
              name="isbn"
              value={formData.isbn}
              onChange={handleChange}
              placeholder="Nhập mã ISBN"
              className={inputClass(errors.isbn)}
            />
            {errors.isbn && <p className="text-xs text-destructive mt-1">{errors.isbn}</p>}
          </div>

          <div>
            <label className="block text-sm font-medium text-foreground mb-1">
              Tổng số lượng * <span className="text-muted-foreground font-normal">(đang cho mượn: {borrowedCount})</span>
            </label>
            <input
              type="number"
              name="totalQuantity"
              value={formData.totalQuantity}
              onChange={handleChange}
              min={borrowedCount}
              className={inputClass(errors.totalQuantity)}
            />
            {errors.totalQuantity && (
              <p className="text-xs text-destructive mt-1">{errors.totalQuantity}</p>
            )}
            <p className="text-xs text-muted-foreground mt-1">
              Số lượng có sẵn sau khi lưu: {Math.max(formData.totalQuantity - borrowedCount, 0)}
            </p>
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-foreground mb-1">Nhà xuất bản</label>
              <input
                type="text"
                name="publisher"
                value={formData.publisher}
                onChange={handleChange}
                placeholder="VD: NXB Kim Đồng"
                className={inputClass()}
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-foreground mb-1">Vị trí kệ</label>
              <input
                type="text"
                name="shelfLocation"
                value={formData.shelfLocation}
                onChange={handleChange}
                placeholder="VD: Kệ A2-05"
                className={inputClass()}
              />
            </div>
          </div>

          <div>
            <label className="block text-sm font-medium text-foreground mb-1">Đường dẫn ảnh bìa</label>
            <input
              type="text"
              name="coverImageUrl"
              value={formData.coverImageUrl}
              onChange={handleChange}
              placeholder="https://..."
              className={inputClass()}
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-foreground mb-1">Mô tả</label>
            <textarea
              name="description"
              value={formData.description}
              onChange={handleChange}
              placeholder="Mô tả tóm tắt nội dung sách"
              rows={3}
              className={inputClass()}
            />
          </div>

          {/* Buttons */}
          <div className="flex gap-3 pt-4">
            <Button type="button" variant="outline" onClick={onClose} className="flex-1" disabled={isSaving}>
              Hủy
            </Button>
            <Button type="submit" className="flex-1 bg-primary hover:bg-primary/90" disabled={isSaving}>
              {isSaving ? (
                <>
                  <Loader2 className="w-4 h-4 mr-2 animate-spin" />
                  Đang lưu...
                </>
              ) : (
                'Lưu thay đổi'
              )}
            </Button>
          </div>
        </form>
      </div>
    </div>
  );
}
