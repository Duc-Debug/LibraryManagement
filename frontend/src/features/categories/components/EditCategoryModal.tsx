import { useState } from "react";
import { CategoryResponse, updateCategoryApi } from "../api/categoryApi";
import { X } from "lucide-react";
import { Button } from "@/components/ui/button";

interface EditCategoryModalProps {
  category: CategoryResponse;
  onClose: () => void;
  onSuccess: () => void;
}

export function EditCategoryModal({ category, onClose, onSuccess }: EditCategoryModalProps) {
  const [name, setName] = useState(category.name);
  const [description, setDescription] = useState(category.description || "");
  const [active, setActive] = useState(category.active);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!name.trim()) {
      setError("Tên thể loại không được để trống.");
      return;
    }

    if (name.length > 100) {
      setError("Tên thể loại không được vượt quá 100 ký tự.");
      return;
    }

    if (description.length > 500) {
      setError("Mô tả không được vượt quá 500 ký tự.");
      return;
    }

    setSubmitting(true);
    setError(null);

    try {
      await updateCategoryApi(category.id, {
        name: name.trim(),
        description: description.trim() || undefined,
        active,
      });
      onSuccess();
    } catch (err: any) {
      setError(err.message || "Cập nhật thể loại thất bại.");
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="fixed inset-0 bg-black/60 backdrop-blur-xs flex items-center justify-center z-50 p-4">
      <div className="bg-card rounded-2xl max-w-md w-full p-6 shadow-2xl border border-border animate-in fade-in zoom-in-95 duration-150 text-foreground">
        <div className="flex items-center justify-between pb-4 border-b border-border">
          <h2 className="text-xl font-bold text-foreground">Chỉnh sửa thể loại</h2>
          <button
            onClick={onClose}
            className="text-muted-foreground hover:text-foreground p-1 rounded-lg hover:bg-muted transition"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {error && (
          <div className="mt-4 p-3 rounded-xl bg-destructive/10 border border-destructive/20 text-destructive text-xs">
            ⚠️ {error}
          </div>
        )}

        <form onSubmit={handleSubmit} className="mt-4 space-y-4 text-sm">
          <div>
            <label className="block text-xs font-semibold text-muted-foreground mb-1">
              Tên Thể Loại <span className="text-red-500">*</span>
            </label>
            <input
              type="text"
              required
              maxLength={100}
              value={name}
              onChange={(e) => setName(e.target.value)}
              placeholder="Nhập tên thể loại..."
              className="w-full px-3.5 py-2.5 border border-border rounded-xl bg-background text-foreground placeholder-muted-foreground shadow-xs focus:outline-none focus:ring-2 focus:ring-primary transition"
            />
            <span className="text-[11px] text-muted-foreground mt-1 block text-right">
              {name.length}/100 ký tự
            </span>
          </div>

          <div>
            <label className="block text-xs font-semibold text-muted-foreground mb-1">
              Mô Tả Chi Tiết
            </label>
            <textarea
              rows={3}
              maxLength={500}
              value={description}
              onChange={(e) => setDescription(e.target.value)}
              placeholder="Nhập mô tả về danh mục sách..."
              className="w-full px-3.5 py-2.5 border border-border rounded-xl bg-background text-foreground placeholder-muted-foreground shadow-xs focus:outline-none focus:ring-2 focus:ring-primary transition leading-relaxed resize-none text-xs"
            />
            <span className="text-[11px] text-muted-foreground mt-1 block text-right">
              {description.length}/500 ký tự
            </span>
          </div>

          <div className="flex items-center justify-between p-3 bg-muted/40 rounded-xl border border-border">
            <div>
              <span className="text-xs font-semibold text-foreground block">Trạng Thái Hiển Thị</span>
              <span className="text-[11px] text-muted-foreground">Cho phép thủ thư chọn thể loại này khi tạo sách</span>
            </div>
            <label className="relative inline-flex items-center cursor-pointer">
              <input
                type="checkbox"
                checked={active}
                onChange={(e) => setActive(e.target.checked)}
                className="sr-only peer"
              />
              <div className="w-11 h-6 bg-muted peer-focus:outline-none rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-border after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-emerald-600"></div>
            </label>
          </div>

          <div className="pt-4 border-t border-border flex justify-end gap-3">
            <Button
              type="button"
              variant="outline"
              disabled={submitting}
              onClick={onClose}
              className="px-4 py-2 rounded-xl text-xs font-medium"
            >
              Hủy
            </Button>
            <Button
              type="submit"
              disabled={submitting}
              className="px-4 py-2 rounded-xl text-xs font-semibold bg-primary hover:bg-primary/90 text-primary-foreground"
            >
              {submitting ? "Đang lưu..." : "Cập Nhật"}
            </Button>
          </div>
        </form>
      </div>
    </div>
  );
}
