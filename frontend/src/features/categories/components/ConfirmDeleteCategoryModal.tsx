import { useState } from "react";
import { CategoryResponse, deleteCategoryApi } from "../api/categoryApi";
import { X } from "lucide-react";
import { Button } from "@/components/ui/button";

interface ConfirmDeleteCategoryModalProps {
  category: CategoryResponse;
  onClose: () => void;
  onSuccess: () => void;
}

export function ConfirmDeleteCategoryModal({ category, onClose, onSuccess }: ConfirmDeleteCategoryModalProps) {
  const [deleting, setDeleting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleConfirmDelete = async () => {
    setDeleting(true);
    setError(null);

    try {
      await deleteCategoryApi(category.id);
      onSuccess();
    } catch (err: any) {
      // Bắt lỗi HTTP 409 CONFLICT nếu thể loại đang chứa sách
      if (err.message && err.message.includes("409")) {
        setError("Không thể xóa thể loại này vì đang có sách thuộc thể loại trong hệ thống!");
      } else {
        setError(err.message || "Xóa thể loại thất bại.");
      }
    } finally {
      setDeleting(false);
    }
  };

  return (
    <div className="fixed inset-0 bg-black/60 backdrop-blur-xs flex items-center justify-center z-50 p-4">
      <div className="bg-card rounded-2xl max-w-md w-full p-6 shadow-2xl border border-destructive/20 space-y-4 animate-in fade-in zoom-in-95 duration-150 text-foreground">
        <div className="flex items-center justify-between pb-3 border-b border-border">
          <div className="flex items-center gap-3 text-destructive">
            <div className="p-2.5 rounded-2xl bg-destructive/10 text-xl">⚠️</div>
            <div>
              <h3 className="text-lg font-bold text-foreground">Xác nhận xóa thể loại</h3>
              <p className="text-xs text-muted-foreground">Hành động này không thể hoàn tác!</p>
            </div>
          </div>
          <button
            onClick={onClose}
            className="text-muted-foreground hover:text-foreground p-1 rounded-lg hover:bg-muted transition"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {error && (
          <div className="p-3.5 rounded-xl bg-destructive/10 border border-destructive/20 text-destructive text-xs leading-relaxed">
            ⚠️ {error}
          </div>
        )}

        <div className="py-2 text-sm text-foreground space-y-2">
          <p>Bạn có chắc chắn muốn xóa vĩnh viễn thể loại sách này?</p>
          <div className="p-3 bg-destructive/10 rounded-xl border border-destructive/20 font-semibold text-destructive">
            "{category.name}" (Mã ID: #{category.id})
          </div>
          <p className="text-xs text-muted-foreground leading-relaxed">
            ⚠️ Lưu ý: Hệ thống sẽ tự động kiểm tra. Nếu thể loại đang có cuốn sách nào liên kết, thao tác xóa sẽ bị chặn để bảo vệ dữ liệu sách (HTTP 409 Conflict).
          </p>
        </div>

        <div className="pt-3 border-t border-border flex justify-end gap-3">
          <Button
            variant="outline"
            disabled={deleting}
            onClick={onClose}
            className="px-4 py-2 rounded-xl text-xs font-medium"
          >
            Hủy
          </Button>
          <Button
            variant="destructive"
            disabled={deleting}
            onClick={handleConfirmDelete}
            className="px-4 py-2 rounded-xl text-xs font-semibold"
          >
            {deleting ? "Đang xóa..." : "Xác nhận xóa"}
          </Button>
        </div>
      </div>
    </div>
  );
}
