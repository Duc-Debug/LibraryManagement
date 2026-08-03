'use client';

import { useState } from 'react';
import { mockMembers } from '@/features/members';
import { mockBooks } from '@/features/books';
import { Plus } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { BorrowingForm } from './BorrowingForm';

export function BorrowingPage() {
  const [showForm, setShowForm] = useState(false);
  const [borrowingRecords, setBorrowingRecords] = useState<any[]>([]);

  const handleCreateBorrow = (record: any) => {
    setBorrowingRecords([...borrowingRecords, { ...record, id: `borrow${Date.now()}` }]);
    setShowForm(false);
  };

  return (
    <div className="p-8">
      {/* Header */}
      <div className="mb-8 flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold text-foreground mb-2">Mượn sách</h1>
          <p className="text-muted-foreground">Quản lý phiếu mượn sách của thành viên</p>
        </div>
        <Button onClick={() => setShowForm(!showForm)} className="bg-primary hover:bg-primary/90">
          <Plus className="w-4 h-4 mr-2" />
          Tạo phiếu mượn
        </Button>
      </div>

      {/* Form */}
      {showForm && (
        <div className="mb-8 bg-card rounded-lg border border-border p-6 shadow-sm">
          <BorrowingForm
            members={mockMembers}
            books={mockBooks}
            onSubmit={handleCreateBorrow}
            onCancel={() => setShowForm(false)}
          />
        </div>
      )}

      {/* Info Box */}
      <div className="bg-blue-50 border border-blue-200 rounded-lg p-4 mb-6">
        <p className="text-sm text-blue-900">
          <span className="font-semibold">Hướng dẫn:</span> Chọn thành viên, chọn sách, nhập ngày mượn và ngày hẹn trả để tạo phiếu mượn mới.
        </p>
      </div>

      {/* Recent Borrowing Requests */}
      {borrowingRecords.length > 0 ? (
        <div className="bg-card rounded-lg border border-border shadow-sm overflow-hidden">
          <div className="p-6 border-b border-border">
            <h2 className="text-lg font-bold text-foreground">Phiếu mượn gần đây</h2>
          </div>
          <div className="overflow-x-auto">
            <table className="w-full">
              <thead>
                <tr className="border-b border-border bg-muted/50">
                  <th className="px-6 py-3 text-left text-sm font-semibold text-foreground">Thành viên</th>
                  <th className="px-6 py-3 text-left text-sm font-semibold text-foreground">Sách</th>
                  <th className="px-6 py-3 text-left text-sm font-semibold text-foreground">Ngày mượn</th>
                  <th className="px-6 py-3 text-left text-sm font-semibold text-foreground">Ngày hẹn trả</th>
                  <th className="px-6 py-3 text-left text-sm font-semibold text-foreground">Trạng thái</th>
                </tr>
              </thead>
              <tbody>
                {borrowingRecords.map((record) => (
                  <tr key={record.id} className="border-b border-border hover:bg-muted/30 transition-colors">
                    <td className="px-6 py-4 text-sm font-medium text-foreground">{record.memberName}</td>
                    <td className="px-6 py-4 text-sm text-foreground">{record.bookTitle}</td>
                    <td className="px-6 py-4 text-sm text-muted-foreground">{record.borrowDate}</td>
                    <td className="px-6 py-4 text-sm text-muted-foreground">{record.dueDate}</td>
                    <td className="px-6 py-4 text-sm">
                      <span className="inline-flex items-center px-3 py-1 rounded-full text-xs font-medium bg-blue-100 text-blue-700">
                        Đang mượn
                      </span>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      ) : (
        <div className="bg-card rounded-lg border border-border p-12 text-center">
          <p className="text-muted-foreground">Chưa có phiếu mượn nào. Nhấn nút &quot;Tạo phiếu mượn&quot; để bắt đầu.</p>
        </div>
      )}
    </div>
  );
}
