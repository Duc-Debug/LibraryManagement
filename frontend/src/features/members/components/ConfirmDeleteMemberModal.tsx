'use client';

import { useState } from 'react';
import { X, AlertTriangle } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { deleteReaderApi, type ReaderResponse } from '@/api/readerApi';

interface ConfirmDeleteMemberModalProps {
  member: ReaderResponse;
  onClose: () => void;
  onSuccess: () => void;
}

export function ConfirmDeleteMemberModal({ member, onClose, onSuccess }: ConfirmDeleteMemberModalProps) {
  const [deleting, setDeleting] = useState(false);
  const [error, setError] = useState('');

  const handleConfirmDelete = async () => {
    setDeleting(true);
    setError('');
    const token = typeof window !== 'undefined' ? localStorage.getItem('accessToken') || '' : '';

    try {
      await deleteReaderApi(member.id, token);
      onSuccess();
    } catch (err: any) {
      // Backend ném lỗi 400 kèm thông báo "ReaderHasActiveBorrowException" khi độc giả đang mượn sách
      if (err.message && (err.message.includes("borrow") || err.message.includes("phiếu mượn") || err.message.includes("400"))) {
        setError("Không thể khóa/xóa độc giả này vì hiện tại họ vẫn còn sách mượn chưa trả!");
      } else {
        setError(err?.message || "Khóa/xóa độc giả thất bại.");
      }
    } finally {
      setDeleting(false);
    }
  };

  return (
    <div className="fixed inset-0 bg-black/60 backdrop-blur-xs flex items-center justify-center z-50 p-4 animate-in fade-in duration-150">
      <div className="bg-card rounded-2xl border border-destructive/20 w-full max-w-md shadow-2xl p-6 overflow-hidden animate-in zoom-in-95 duration-150 text-foreground">
        {/* Header */}
        <div className="flex items-center justify-between pb-3 border-b border-border">
          <div className="flex items-center gap-3 text-destructive">
            <div className="p-2.5 rounded-2xl bg-destructive/10 text-xl">⚠️</div>
            <div>
              <h3 className="text-lg font-bold text-foreground">Xác nhận xóa độc giả</h3>
              <p className="text-xs text-muted-foreground">Thao tác này sẽ tạm khóa quyền mượn thẻ</p>
            </div>
          </div>
          <button
            onClick={onClose}
            className="text-muted-foreground hover:text-foreground p-1.5 rounded-lg hover:bg-muted transition"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Content */}
        <div className="py-4 text-sm text-foreground space-y-3">
          {error && (
            <div className="p-3.5 rounded-xl bg-destructive/10 border border-destructive/20 text-destructive text-xs leading-relaxed">
              ⚠️ {error}
            </div>
          )}

          <p>Bạn có chắc chắn muốn xóa/tạm khóa thẻ độc giả này?</p>
          
          <div className="p-4 bg-muted rounded-xl border border-border space-y-1.5 font-medium">
            <div className="text-xs text-muted-foreground uppercase tracking-wider">Thông tin thẻ:</div>
            <div className="text-foreground font-bold">{member.name}</div>
            <div className="font-mono text-xs text-primary">{member.cardNumber}</div>
            <div className="text-xs text-muted-foreground">{member.email} | {member.phoneNumber}</div>
          </div>

          <p className="text-xs text-muted-foreground leading-relaxed flex gap-2">
            <AlertTriangle className="w-4 h-4 shrink-0 text-amber-500" />
            <span>
              Lưu ý: Hệ thống sẽ tự động kiểm tra. Nếu độc giả đang có phiếu mượn hoạt động (chưa trả sách), thao tác xóa sẽ bị chặn để bảo vệ dữ liệu.
            </span>
          </p>
        </div>

        {/* Buttons */}
        <div className="pt-3 border-t border-border flex justify-end gap-3">
          <Button
            type="button"
            variant="outline"
            disabled={deleting}
            onClick={onClose}
            className="px-4 py-2 rounded-xl text-xs font-medium"
          >
            Hủy
          </Button>
          <Button
            type="button"
            variant="destructive"
            disabled={deleting}
            onClick={handleConfirmDelete}
            className="px-4 py-2 rounded-xl text-xs font-semibold"
          >
            {deleting ? "Đang khóa..." : "Khóa & Xóa thẻ"}
          </Button>
        </div>
      </div>
    </div>
  );
}
