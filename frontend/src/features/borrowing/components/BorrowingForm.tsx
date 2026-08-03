'use client';

import { useState } from 'react';
import type { Member } from '@/features/members';
import type { Book } from '@/features/books';
import { Button } from '@/components/ui/button';

interface BorrowingFormProps {
  members: Member[];
  books: Book[];
  onSubmit: (record: any) => void;
  onCancel: () => void;
}

export function BorrowingForm({ members, books, onSubmit, onCancel }: BorrowingFormProps) {
  const [formData, setFormData] = useState({
    memberId: '',
    bookId: '',
    borrowDate: new Date().toISOString().split('T')[0],
    dueDate: new Date(Date.now() + 14 * 24 * 60 * 60 * 1000).toISOString().split('T')[0],
  });

  const selectedMember = members.find(m => m.id === formData.memberId);
  const selectedBook = books.find(b => b.id === formData.bookId);

  const handleChange = (e: React.ChangeEvent<HTMLSelectElement | HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData({
      ...formData,
      [name]: value,
    });
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (formData.memberId && formData.bookId) {
      onSubmit({
        ...formData,
        memberName: selectedMember?.name,
        bookTitle: selectedBook?.title,
      });
    }
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-4">
      <h3 className="text-lg font-semibold text-foreground mb-4">Tạo phiếu mượn mới</h3>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        <div>
          <label className="block text-sm font-medium text-foreground mb-2">Chọn thành viên *</label>
          <select
            name="memberId"
            value={formData.memberId}
            onChange={handleChange}
            required
            className="w-full px-3 py-2 border border-border rounded-lg bg-background text-foreground focus:outline-none focus:ring-2 focus:ring-primary"
          >
            <option value="">-- Chọn thành viên --</option>
            {members
              .filter(m => m.status === 'active')
              .map((member) => (
                <option key={member.id} value={member.id}>
                  {member.name} ({member.email})
                </option>
              ))}
          </select>
        </div>

        <div>
          <label className="block text-sm font-medium text-foreground mb-2">Chọn sách *</label>
          <select
            name="bookId"
            value={formData.bookId}
            onChange={handleChange}
            required
            className="w-full px-3 py-2 border border-border rounded-lg bg-background text-foreground focus:outline-none focus:ring-2 focus:ring-primary"
          >
            <option value="">-- Chọn sách --</option>
            {books
              .filter(b => b.availableCopies > 0)
              .map((book) => (
                <option key={book.id} value={book.id}>
                  {book.title} ({book.availableCopies} có sẵn)
                </option>
              ))}
          </select>
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        <div>
          <label className="block text-sm font-medium text-foreground mb-2">Ngày mượn</label>
          <input
            type="date"
            name="borrowDate"
            value={formData.borrowDate}
            onChange={handleChange}
            className="w-full px-3 py-2 border border-border rounded-lg bg-background text-foreground focus:outline-none focus:ring-2 focus:ring-primary"
          />
        </div>

        <div>
          <label className="block text-sm font-medium text-foreground mb-2">Ngày hẹn trả</label>
          <input
            type="date"
            name="dueDate"
            value={formData.dueDate}
            onChange={handleChange}
            className="w-full px-3 py-2 border border-border rounded-lg bg-background text-foreground focus:outline-none focus:ring-2 focus:ring-primary"
          />
        </div>
      </div>

      {/* Selected Items Summary */}
      {selectedMember && selectedBook && (
        <div className="bg-accent/10 border border-accent rounded-lg p-4">
          <p className="text-sm text-foreground">
            <span className="font-semibold">{selectedMember.name}</span> mượn{' '}
            <span className="font-semibold">{selectedBook.title}</span> từ{' '}
            <span className="font-semibold">{formData.borrowDate}</span> đến{' '}
            <span className="font-semibold">{formData.dueDate}</span>
          </p>
        </div>
      )}

      <div className="flex gap-3 pt-4">
        <Button
          type="button"
          variant="outline"
          onClick={onCancel}
          className="flex-1"
        >
          Hủy
        </Button>
        <Button
          type="submit"
          disabled={!formData.memberId || !formData.bookId}
          className="flex-1 bg-primary hover:bg-primary/90 disabled:opacity-50"
        >
          Xác nhận mượn
        </Button>
      </div>
    </form>
  );
}
