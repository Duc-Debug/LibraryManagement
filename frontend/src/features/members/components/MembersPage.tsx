'use client';

import { useState } from 'react';
import { mockMembers } from '../api/getMembers';
import type { Member } from '../types/member.types';
import { Plus, Edit2, Trash2, Search, Eye } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { mockBorrowRecords } from '@/features/borrowing';
import { AddMemberModal } from './AddMemberModal';
import { EditMemberModal } from './EditMemberModal';
import { MemberHistoryModal } from './MemberHistoryModal';

export function MembersPage() {
  const [members, setMembers] = useState<Member[]>(mockMembers);
  const [searchTerm, setSearchTerm] = useState('');
  const [showAddModal, setShowAddModal] = useState(false);
  const [editingMember, setEditingMember] = useState<Member | null>(null);
  const [viewingMember, setViewingMember] = useState<Member | null>(null);

  const filteredMembers = members.filter((member) =>
      member.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
      member.email.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const handleAddMember = (memberData: Omit<Member, 'id'>) => {
    const newMember: Member = {
      ...memberData,
      id: `member${Date.now()}`,
    };
    setMembers([...members, newMember]);
    setShowAddModal(false);
  };

  const handleDeleteMember = (id: string) => {
    setMembers(members.filter((member) => member.id !== id));
  };

  const handleUpdateMember = (updatedMember: Member) => {
    setMembers(members.map((m) => (m.id === updatedMember.id ? updatedMember : m)));
    setEditingMember(null);
  };

  return (
      <div className="p-8">
        {/* Header */}
        <div className="mb-8 flex items-center justify-between">
          <div>
            <h1 className="text-3xl font-bold text-foreground mb-2">Quản lý Thành viên</h1>
            <p className="text-muted-foreground">Quản lý thông tin thành viên thư viện</p>
          </div>
          <Button onClick={() => setShowAddModal(true)} className="bg-primary hover:bg-primary/90">
            <Plus className="w-4 h-4 mr-2" />
            Thêm thành viên
          </Button>
        </div>

        {/* Search */}
        <div className="mb-6 relative">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-muted-foreground" />
          <input
              type="text"
              placeholder="Tìm kiếm theo tên hoặc email..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="w-full pl-10 pr-4 py-2 border border-border rounded-lg bg-card text-foreground placeholder-muted-foreground focus:outline-none focus:ring-2 focus:ring-primary"
          />
        </div>

        {/* Members Table */}
        <div className="bg-card rounded-lg border border-border shadow-sm overflow-hidden">
          <div className="overflow-x-auto">
            <table className="w-full">
              <thead>
              <tr className="border-b border-border bg-muted/50">
                <th className="px-6 py-3 text-left text-sm font-semibold text-foreground">Họ tên</th>
                <th className="px-6 py-3 text-left text-sm font-semibold text-foreground">Email</th>
                <th className="px-6 py-3 text-left text-sm font-semibold text-foreground">Số điện thoại</th>
                <th className="px-6 py-3 text-left text-sm font-semibold text-foreground">Ngày tham gia</th>
                <th className="px-6 py-3 text-left text-sm font-semibold text-foreground">Sách đang mượn</th>
                <th className="px-6 py-3 text-left text-sm font-semibold text-foreground">Trạng thái</th>
                <th className="px-6 py-3 text-left text-sm font-semibold text-foreground">Thao tác</th>
              </tr>
              </thead>
              <tbody>
              {filteredMembers.map((member) => (
                  <tr key={member.id} className="border-b border-border hover:bg-muted/30 transition-colors">
                    <td className="px-6 py-4 text-sm font-medium text-foreground">{member.name}</td>
                    <td className="px-6 py-4 text-sm text-foreground">{member.email}</td>
                    <td className="px-6 py-4 text-sm text-muted-foreground">{member.phone || '—'}</td>
                    <td className="px-6 py-4 text-sm text-muted-foreground">{member.joinDate}</td>
                    <td className="px-6 py-4 text-sm text-foreground">
                      <span className="font-semibold">{member.borrowedBooksCount}</span>
                    </td>
                    <td className="px-6 py-4 text-sm">
                      {/* Chỉ hiển thị trạng thái — đổi trạng thái được thực hiện trong modal Sửa thông tin */}
                      <span
                          className={`inline-flex items-center px-3 py-1 rounded-full text-xs font-medium ${
                              member.status === 'active'
                                  ? 'bg-green-100 text-green-700'
                                  : 'bg-gray-100 text-gray-700'
                          }`}
                      >
                      {member.status === 'active' ? 'Hoạt động' : 'Ngưng hoạt động'}
                    </span>
                    </td>
                    <td className="px-6 py-4 text-sm">
                      <div className="flex items-center gap-2">
                        <Button
                            variant="ghost"
                            size="sm"
                            className="h-8 w-8 p-0"
                            title="Xem chi tiết / Lịch sử"
                            onClick={() => setViewingMember(member)}
                        >
                          <Eye className="w-4 h-4" />
                        </Button>
                        <Button
                            variant="ghost"
                            size="sm"
                            className="h-8 w-8 p-0"
                            title="Sửa"
                            onClick={() => setEditingMember(member)}
                        >
                          <Edit2 className="w-4 h-4" />
                        </Button>
                        <Button
                            variant="ghost"
                            size="sm"
                            className="h-8 w-8 p-0 text-destructive hover:text-destructive"
                            title="Xóa"
                            onClick={() => handleDeleteMember(member.id)}
                        >
                          <Trash2 className="w-4 h-4" />
                        </Button>
                      </div>
                    </td>
                  </tr>
              ))}
              </tbody>
            </table>
          </div>
        </div>

        {/* Add Member Modal */}
        {showAddModal && (
            <AddMemberModal
                onClose={() => setShowAddModal(false)}
                onSave={handleAddMember}
            />
        )}

        {/* Edit Member Modal */}
        {editingMember && (
            <EditMemberModal
                member={editingMember}
                onClose={() => setEditingMember(null)}
                onSave={handleUpdateMember}
            />
        )}

        {/* Member History Modal */}
        {viewingMember && (
            <MemberHistoryModal
                member={viewingMember}
                records={mockBorrowRecords}
                onClose={() => setViewingMember(null)}
            />
        )}
      </div>
  );
}