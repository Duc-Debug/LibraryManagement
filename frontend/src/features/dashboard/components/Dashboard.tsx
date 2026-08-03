'use client';

import { mockBooks } from '@/features/books';
import { mockMembers } from '@/features/members';
import { mockBorrowRecords } from '@/features/borrowing';
import { mockActivityLogs } from '../api/getActivityLogs';
import { AlertTriangle, Users, BookMarked, TrendingUp } from 'lucide-react';

export function Dashboard() {
  const totalBooks = mockBooks.reduce((sum, book) => sum + book.totalCopies, 0);
  const availableBooks = mockBooks.reduce((sum, book) => sum + book.availableCopies, 0);
  const totalMembers = mockMembers.length;
  const activeMembers = mockMembers.filter(m => m.status === 'active').length;
  const borrowingBooks = mockBorrowRecords.filter(r => r.status === 'borrowing').length;
  const overdueBooks = mockBorrowRecords.filter(r => r.status === 'overdue').length;

  const MetricCard = ({ title, value, subtitle, icon: Icon, bgColor }: any) => (
    <div className="bg-card rounded-lg border border-border p-6 shadow-sm">
      <div className="flex items-center justify-between">
        <div>
          <p className="text-sm text-muted-foreground mb-2">{title}</p>
          <p className="text-3xl font-bold text-foreground">{value}</p>
          {subtitle && <p className="text-xs text-muted-foreground mt-2">{subtitle}</p>}
        </div>
        <div className={`${bgColor} rounded-lg p-3`}>
          <Icon className="w-6 h-6" />
        </div>
      </div>
    </div>
  );

  return (
    <div className="p-8">
      {/* Header */}
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-foreground mb-2">Bảng điều khiển</h1>
        <p className="text-muted-foreground">Chào mừng đến với hệ thống quản lý thư viện</p>
      </div>

      {/* Metric Cards */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
        <MetricCard
          title="Tổng số sách"
          value={totalBooks}
          subtitle={`${availableBooks} bản sẵn có`}
          icon={BookMarked}
          bgColor="bg-blue-100"
        />
        <MetricCard
          title="Thành viên"
          value={totalMembers}
          subtitle={`${activeMembers} đang hoạt động`}
          icon={Users}
          bgColor="bg-green-100"
        />
        <MetricCard
          title="Đang mượn"
          value={borrowingBooks}
          subtitle="Lượt mượn hiện tại"
          icon={TrendingUp}
          bgColor="bg-amber-100"
        />
        <MetricCard
          title="Quá hạn"
          value={overdueBooks}
          subtitle="Cảnh báo"
          icon={AlertTriangle}
          bgColor="bg-red-100"
        />
      </div>

      {/* Recent Activity */}
      <div className="bg-card rounded-lg border border-border shadow-sm">
        <div className="p-6 border-b border-border">
          <h2 className="text-lg font-bold text-foreground">Hoạt động gần đây</h2>
        </div>
        <div className="overflow-x-auto">
          <table className="w-full">
            <thead>
              <tr className="border-b border-border bg-muted/50">
                <th className="px-6 py-3 text-left text-sm font-semibold text-foreground">Sách</th>
                <th className="px-6 py-3 text-left text-sm font-semibold text-foreground">Thành viên</th>
                <th className="px-6 py-3 text-left text-sm font-semibold text-foreground">Hành động</th>
                <th className="px-6 py-3 text-left text-sm font-semibold text-foreground">Ngày</th>
                <th className="px-6 py-3 text-left text-sm font-semibold text-foreground">Trạng thái</th>
              </tr>
            </thead>
            <tbody>
              {mockActivityLogs.map((log) => (
                <tr key={log.id} className="border-b border-border hover:bg-muted/30 transition-colors">
                  <td className="px-6 py-4 text-sm text-foreground">{log.bookTitle}</td>
                  <td className="px-6 py-4 text-sm text-foreground">{log.memberName}</td>
                  <td className="px-6 py-4 text-sm text-foreground">{log.action}</td>
                  <td className="px-6 py-4 text-sm text-muted-foreground">{log.date}</td>
                  <td className="px-6 py-4 text-sm">
                    <span
                      className={`inline-flex items-center px-3 py-1 rounded-full text-xs font-medium ${
                        log.status === 'returned'
                          ? 'bg-green-100 text-green-700'
                          : log.status === 'borrowing'
                          ? 'bg-blue-100 text-blue-700'
                          : 'bg-red-100 text-red-700'
                      }`}
                    >
                      {log.status === 'returned'
                        ? 'Đã trả'
                        : log.status === 'borrowing'
                        ? 'Đang mượn'
                        : 'Quá hạn'}
                    </span>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}
