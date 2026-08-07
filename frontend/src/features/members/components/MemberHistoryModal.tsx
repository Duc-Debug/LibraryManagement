'use client';

import type { Member } from '../types/member.types';
import type { BorrowRecord } from '@/features/borrowing';
import { X } from 'lucide-react';

interface MemberHistoryModalProps {
    member: Member;
    records: BorrowRecord[];
    onClose: () => void;
}

const STATUS_LABEL: Record<BorrowRecord['status'], string> = {
    borrowing: 'Đang mượn',
    returned: 'Đã trả',
    overdue: 'Quá hạn',
};

const STATUS_CLASS: Record<BorrowRecord['status'], string> = {
    borrowing: 'bg-blue-100 text-blue-700',
    returned: 'bg-green-100 text-green-700',
    overdue: 'bg-red-100 text-red-700',
};

export function MemberHistoryModal({ member, records, onClose }: MemberHistoryModalProps) {
    const memberRecords = records
        .filter((r) => r.memberId === member.id)
        .sort((a, b) => (a.borrowDate < b.borrowDate ? 1 : -1));

    return (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
            <div className="bg-card rounded-lg border border-border w-full max-w-3xl shadow-lg max-h-[85vh] flex flex-col">
                {/* Header */}
                <div className="flex items-center justify-between p-6 border-b border-border">
                    <div>
                        <h2 className="text-lg font-bold text-foreground">Lịch sử mượn – trả</h2>
                        <p className="text-sm text-muted-foreground mt-1">
                            {member.name} · {member.email}
                        </p>
                    </div>
                    <button onClick={onClose} className="text-muted-foreground hover:text-foreground">
                        <X className="w-5 h-5" />
                    </button>
                </div>

                {/* Body */}
                <div className="p-6 overflow-y-auto flex-1">
                    {memberRecords.length === 0 ? (
                        <p className="text-sm text-muted-foreground text-center py-8">
                            Thành viên này chưa có lịch sử mượn sách nào.
                        </p>
                    ) : (
                        <div className="overflow-x-auto border border-border rounded-lg">
                            <table className="w-full">
                                <thead>
                                <tr className="border-b border-border bg-muted/50">
                                    <th className="px-4 py-3 text-left text-sm font-semibold text-foreground">Tên sách</th>
                                    <th className="px-4 py-3 text-left text-sm font-semibold text-foreground">Ngày mượn</th>
                                    <th className="px-4 py-3 text-left text-sm font-semibold text-foreground">Hạn trả</th>
                                    <th className="px-4 py-3 text-left text-sm font-semibold text-foreground">Ngày trả thực tế</th>
                                    <th className="px-4 py-3 text-left text-sm font-semibold text-foreground">Trạng thái</th>
                                </tr>
                                </thead>
                                <tbody>
                                {memberRecords.map((record) => (
                                    <tr key={record.id} className="border-b border-border last:border-0 hover:bg-muted/30">
                                        <td className="px-4 py-3 text-sm font-medium text-foreground">{record.bookTitle}</td>
                                        <td className="px-4 py-3 text-sm text-muted-foreground">{record.borrowDate}</td>
                                        <td className="px-4 py-3 text-sm text-muted-foreground">{record.dueDate}</td>
                                        <td className="px-4 py-3 text-sm text-muted-foreground">
                                            {record.returnDate ?? '—'}
                                        </td>
                                        <td className="px-4 py-3 text-sm">
                        <span
                            className={`inline-flex items-center px-3 py-1 rounded-full text-xs font-medium ${STATUS_CLASS[record.status]}`}
                        >
                          {STATUS_LABEL[record.status]}
                        </span>
                                        </td>
                                    </tr>
                                ))}
                                </tbody>
                            </table>
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
}