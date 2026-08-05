'use client';

import { useState } from 'react';
import type { Book } from '../types/book.types';
import { X } from 'lucide-react';
import { Button } from '@/components/ui/button';

interface AddBookModalProps {
  onClose: () => void;
  onSave: (book: Omit<Book, 'id'>) => void;
}

const CATEGORIES = ['Kinh tế', 'Lịch sử', 'Tâm lý học', 'Chiến lược', 'Tiểu thuyết', 'Công nghệ', 'Khác'];

export function AddBookModal({ onClose, onSave }: AddBookModalProps) {
  const [formData, setFormData] = useState({
    title: '',
    author: '',
    category: 'Kinh tế',
    publishYear: new Date().getFullYear(),
    isbn: '',
    totalCopies: 1,
    availableCopies: 1,
    publisher: '',
  });

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setFormData({
      ...formData,
      [name]: name === 'publishYear' || name === 'totalCopies' || name === 'availableCopies' ? parseInt(value) : value,
    });
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (formData.title && formData.author && formData.isbn) {
      onSave(formData);
    }
  };

  return (
    <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
      <div className="bg-card rounded-lg border border-border w-full max-w-md shadow-lg">
        {/* Header */}
        <div className="flex items-center justify-between p-6 border-b border-border">
          <h2 className="text-lg font-bold text-foreground">Thêm sách mới</h2>
          <button
            onClick={onClose}
            className="text-muted-foreground hover:text-foreground"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Form */}
        <form onSubmit={handleSubmit} className="p-6 space-y-4">
          <div>
            <label className="block text-sm font-medium text-foreground mb-1">Tên sách *</label>
            <input
              type="text"
              name="title"
              value={formData.title}
              onChange={handleChange}
              placeholder="Nhập tên sách"
              required
              className="w-full px-3 py-2 border border-border rounded-lg bg-background text-foreground placeholder-muted-foreground focus:outline-none focus:ring-2 focus:ring-primary"
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-foreground mb-1">Tác giả *</label>
            <input
              type="text"
              name="author"
              value={formData.author}
              onChange={handleChange}
              placeholder="Nhập tên tác giả"
              required
              className="w-full px-3 py-2 border border-border rounded-lg bg-background text-foreground placeholder-muted-foreground focus:outline-none focus:ring-2 focus:ring-primary"
            />
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-foreground mb-1">Thể loại</label>
              <select
                name="category"
                value={formData.category}
                onChange={handleChange}
                className="w-full px-3 py-2 border border-border rounded-lg bg-background text-foreground focus:outline-none focus:ring-2 focus:ring-primary"
              >
                {CATEGORIES.map((cat) => (
                  <option key={cat} value={cat}>
                    {cat}
                  </option>
                ))}
              </select>
            </div>

            <div>
              <label className="block text-sm font-medium text-foreground mb-1">Năm xuất bản</label>
              <input
                type="number"
                name="publishYear"
                value={formData.publishYear}
                onChange={handleChange}
                className="w-full px-3 py-2 border border-border rounded-lg bg-background text-foreground focus:outline-none focus:ring-2 focus:ring-primary"
              />
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
              required
              className="w-full px-3 py-2 border border-border rounded-lg bg-background text-foreground placeholder-muted-foreground focus:outline-none focus:ring-2 focus:ring-primary"
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-foreground mb-1">Nhà xuất bản</label>
            <input
              type="text"
              name="publisher"
              value={formData.publisher}
              onChange={handleChange}
              placeholder="VD: NXB Kim Đồng"
              className="w-full px-3 py-2 border border-border rounded-lg bg-background text-foreground placeholder-muted-foreground focus:outline-none focus:ring-2 focus:ring-primary"
            />
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-foreground mb-1">Tổng số lượng</label>
              <input
                type="number"
                name="totalCopies"
                value={formData.totalCopies}
                onChange={handleChange}
                min="1"
                className="w-full px-3 py-2 border border-border rounded-lg bg-background text-foreground focus:outline-none focus:ring-2 focus:ring-primary"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-foreground mb-1">Số lượng có sẵn</label>
              <input
                type="number"
                name="availableCopies"
                value={formData.availableCopies}
                onChange={handleChange}
                min="0"
                className="w-full px-3 py-2 border border-border rounded-lg bg-background text-foreground focus:outline-none focus:ring-2 focus:ring-primary"
              />
            </div>
          </div>

          {/* Buttons */}
          <div className="flex gap-3 pt-4">
            <Button
              type="button"
              variant="outline"
              onClick={onClose}
              className="flex-1"
            >
              Hủy
            </Button>
            <Button
              type="submit"
              className="flex-1 bg-primary hover:bg-primary/90"
            >
              Lưu sách
            </Button>
          </div>
        </form>
      </div>
    </div>
  );
}
