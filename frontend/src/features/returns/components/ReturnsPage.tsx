'use client';

import { useState } from 'react';
import { mockBorrowRecords } from '@/features/borrowing';
import type { BorrowRecord } from '@/features/borrowing';
import { Search, Check } from 'lucide-react';
import { Button } from '@/components/ui/button';

export function ReturnsPage() {
  const [borrowRecords, setBorrowRecords] = useState<BorrowRecord[]>(
    mockBorrowRecords.filter(r => r.status === 'borrowing' || r.status === 'overdue')
  );
  const [returnedRecords, setReturnedRecords] = useState<BorrowRecord[]>(
    mockBorrowRecords.filter(r => r.status === 'returned')
  );
  const [searchTerm, setSearchTerm] = useState('');

  const filteredBorrowRecords = borrowRecords.filter(
    (record) =>
      record.memberName.toLowerCase().includes(searchTerm.toLowerCase()) ||
      record.bookTitle.toLowerCase().includes(searchTerm.toLowerCase()) ||
      record.id.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const handleReturnBook = (recordId: string) => {
    const record = borrowRecords.find(r => r.id === recordId);
    if (record) {
      const returnedRecord: BorrowRecord = {
        ...record,
        returnDate: new Date().toISOString().split('T')[0],
        status: 'returned',
      };
      setBorrowRecords(borrowRecords.filter(r => r.id !== recordId));
      setReturnedRecords([returnedRecord, ...returnedRecords]);
    }
  };

  const isOverdue = (dueDate: string) => {
    return new Date(dueDate) < new Date();
  };

  return (
    <div className="p-8">
      {/* Header */}
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-foreground mb-2">Trả sách</h1>
        <p className="text-muted-foreground">Quản lý việc trả sách và kiểm tra quá hạn</p>
      </div>

      {/* Search */}
      <div className="mb-6 relative">
        <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-muted-foreground" />
        <input
          type="text"
          placeholder="Tìm kiếm theo mã phiếu, tên độc giả hoặc tên sách..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          className="w-full pl-10 pr-4 py-2 border border-border rounded-lg bg-card text-foreground placeholder-muted-foreground focus:outline-none focus:ring-2 focus:ring-primary"
        />
      </div>

      {/* Stats */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-8">
        <div className="bg-blue-50 border border-blue-200 rounded-lg p-4">
          <p className="text-sm text-blue-600">Đang mượn</p>
          <p className="text-2xl font-bold text-blue-900">{borrowRecords.filter(r => r.status === 'borrowing').length}</p>
        </div>
        <div className="bg-red-50 border border-red-200 rounded-lg p-4">
          <p className="text-sm text-red-600">Quá hạn</p>
          <p className="text-2xl font-bold text-red-900">{borrowRecords.filter(r => r.status === 'overdue').length}</p>
        </div>
        <div className="bg-green-50 border border-green-200 rounded-lg p-4">
          <p className="text-sm text-green-600">Đã trả</p>
          <p className="text-2xl font-bold text-green-900">{returnedRecords.length}</p>
        </div>
      </div>

      {/* Pending Returns */}
      {filteredBorrowRecords.length > 0 ? (
        <div className="bg-card rounded-lg border border-border shadow-sm overflow-hidden mb-8">
          <div className="p-6 border-b border-border">
            <h2 className="text-lg font-bold text-foreground">Chờ trả sách ({filteredBorrowRecords.length})</h2>
          </div>
          <div className="overflow-x-auto">
            <table className="w-full">
              <thead>
                <tr className="border-b border-border bg-muted/50">
                  <th className="px-6 py-3 text-left text-sm font-semibold text-foreground">Mã phiếu</th>
                  <th className="px-6 py-3 text-left text-sm font-semibold text-foreground">Sách</th>
                  <th className="px-6 py-3 text-left text-sm font-semibold text-foreground">Độc giả</th>
                  <th className="px-6 py-3 text-left text-sm font-semibold text-foreground">Ngày mượn</th>
                  <th className="px-6 py-3 text-left text-sm font-semibold text-foreground">Ngày hẹn trả</th>
                  <th className="px-6 py-3 text-left text-sm font-semibold text-foreground">Trạng thái</th>
                  <th className="px-6 py-3 text-left text-sm font-semibold text-foreground">Thao tác</th>
                </tr>
              </thead>
              <tbody>
                {filteredBorrowRecords.map((record) => (
                  <tr
                    key={record.id}
                    className={`border-b border-border hover:bg-muted/30 transition-colors ${
                      isOverdue(record.dueDate) ? 'bg-red-50' : ''
                    }`}
                  >
                    <td className="px-6 py-4 text-sm font-mono text-foreground">{record.id}</td>
                    <td className="px-6 py-4 text-sm font-medium text-foreground">{record.bookTitle}</td>
                    <td className="px-6 py-4 text-sm text-foreground">{record.memberName}</td>
                    <td className="px-6 py-4 text-sm text-muted-foreground">{record.borrowDate}</td>
                    <td className="px-6 py-4 text-sm text-muted-foreground">{record.dueDate}</td>
                    <td className="px-6 py-4 text-sm">
                      <span
                        className={`inline-flex items-center px-3 py-1 rounded-full text-xs font-medium ${
                          record.status === 'overdue'
                            ? 'bg-red-100 text-red-700'
                            : 'bg-blue-100 text-blue-700'
                        }`}
                      >
                        {record.status === 'overdue' ? 'Quá hạn' : 'Đang mượn'}
                      </span>
                    </td>
                    <td className="px-6 py-4 text-sm">
                      <Button
                        onClick={() => handleReturnBook(record.id)}
                        className="bg-green-600 hover:bg-green-700 text-white h-8 px-3"
                      >
                        <Check className="w-4 h-4 mr-1" />
                        Xác nhận trả
                      </Button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      ) : (
        <div className="bg-card rounded-lg border border-border p-12 text-center mb-8">
          <p className="text-muted-foreground">Không có sách chưa trả.</p>
        </div>
      )}

      {/* Returned Books History */}
      {returnedRecords.length > 0 && (
        <div className="bg-card rounded-lg border border-border shadow-sm overflow-hidden">
          <div className="p-6 border-b border-border">
            <h2 className="text-lg font-bold text-foreground">Lịch sử trả sách gần đây</h2>
          </div>
          <div className="overflow-x-auto">
            <table className="w-full">
              <thead>
                <tr className="border-b border-border bg-muted/50">
                  <th className="px-6 py-3 text-left text-sm font-semibold text-foreground">Mã phiếu</th>
                  <th className="px-6 py-3 text-left text-sm font-semibold text-foreground">Sách</th>
                  <th className="px-6 py-3 text-left text-sm font-semibold text-foreground">Độc giả</th>
                  <th className="px-6 py-3 text-left text-sm font-semibold text-foreground">Ngày mượn</th>
                  <th className="px-6 py-3 text-left text-sm font-semibold text-foreground">Ngày trả</th>
                </tr>
              </thead>
              <tbody>
                {returnedRecords.slice(0, 5).map((record) => (
                  <tr key={record.id} className="border-b border-border hover:bg-muted/30 transition-colors">
                    <td className="px-6 py-4 text-sm font-mono text-foreground">{record.id}</td>
                    <td className="px-6 py-4 text-sm font-medium text-foreground">{record.bookTitle}</td>
                    <td className="px-6 py-4 text-sm text-foreground">{record.memberName}</td>
                    <td className="px-6 py-4 text-sm text-muted-foreground">{record.borrowDate}</td>
                    <td className="px-6 py-4 text-sm text-muted-foreground">{record.returnDate}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}
    </div>
  );
}
