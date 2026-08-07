'use client';

import { useState } from 'react';
import type { Member } from '../types/member.types';
import { X } from 'lucide-react';
import { Button } from '@/components/ui/button';

interface EditMemberModalProps {
    member: Member;
    onClose: () => void;
    onSave: (updatedMember: Member) => void;
}

export function EditMemberModal({ member, onClose, onSave }: EditMemberModalProps) {
    const [name, setName] = useState(member.name);
    const [phone, setPhone] = useState(member.phone ?? '');
    const [status, setStatus] = useState<Member['status']>(member.status);
    const [error, setError] = useState('');

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();

        if (!name.trim()) {
            setError('Họ tên không được để trống.');
            return;
        }

        onSave({
            ...member,
            name: name.trim(),
            phone: phone.trim() || undefined,
            status,
        });
    };

    return (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
            <div className="bg-card rounded-lg border border-border w-full max-w-md shadow-lg">
                {/* Header */}
                <div className="flex items-center justify-between p-6 border-b border-border">
                    <h2 className="text-lg font-bold text-foreground">Sửa thông tin thành viên</h2>
                    <button onClick={onClose} className="text-muted-foreground hover:text-foreground">
                        <X className="w-5 h-5" />
                    </button>
                </div>

                {/* Form */}
                <form onSubmit={handleSubmit} className="p-6 space-y-4">
                    {error && (
                        <div className="text-sm text-destructive bg-red-50 px-3 py-2 rounded-lg border border-red-200">
                            {error}
                        </div>
                    )}

                    {/* Email chỉ hiển thị, không cho sửa */}
                    <div>
                        <label className="block text-sm font-medium text-foreground mb-1">Email</label>
                        <input
                            type="email"
                            value={member.email}
                            disabled
                            className="w-full px-3 py-2 border border-border rounded-lg bg-muted text-muted-foreground cursor-not-allowed"
                        />
                    </div>

                    <div>
                        <label className="block text-sm font-medium text-foreground mb-1">Họ tên *</label>
                        <input
                            type="text"
                            value={name}
                            onChange={(e) => {
                                setName(e.target.value);
                                setError('');
                            }}
                            placeholder="Nhập họ tên"
                            className="w-full px-3 py-2 border border-border rounded-lg bg-background text-foreground placeholder-muted-foreground focus:outline-none focus:ring-2 focus:ring-primary"
                        />
                    </div>

                    <div>
                        <label className="block text-sm font-medium text-foreground mb-1">Số điện thoại</label>
                        <input
                            type="tel"
                            value={phone}
                            onChange={(e) => setPhone(e.target.value)}
                            placeholder="Nhập số điện thoại"
                            className="w-full px-3 py-2 border border-border rounded-lg bg-background text-foreground placeholder-muted-foreground focus:outline-none focus:ring-2 focus:ring-primary"
                        />
                    </div>

                    {/* Trạng thái hoạt động — đổi bằng nút bấm rõ ràng trong form, không đổi trực tiếp ngoài bảng */}
                    <div>
                        <label className="block text-sm font-medium text-foreground mb-1">Trạng thái</label>
                        <div className="flex items-center justify-between px-3 py-2 border border-border rounded-lg bg-background">
              <span
                  className={`inline-flex items-center px-3 py-1 rounded-full text-xs font-medium ${
                      status === 'active'
                          ? 'bg-green-100 text-green-700'
                          : 'bg-gray-100 text-gray-700'
                  }`}
              >
                {status === 'active' ? 'Hoạt động' : 'Ngưng hoạt động'}
              </span>
                            <Button
                                type="button"
                                variant="outline"
                                size="sm"
                                onClick={() => setStatus(status === 'active' ? 'inactive' : 'active')}
                            >
                                {status === 'active' ? 'Chuyển sang Ngưng hoạt động' : 'Chuyển sang Hoạt động'}
                            </Button>
                        </div>
                    </div>

                    {/* Buttons */}
                    <div className="flex gap-3 pt-4">
                        <Button type="button" variant="outline" onClick={onClose} className="flex-1">
                            Hủy
                        </Button>
                        <Button type="submit" className="flex-1 bg-primary hover:bg-primary/90">
                            Lưu thay đổi
                        </Button>
                    </div>
                </form>
            </div>
        </div>
    );
}