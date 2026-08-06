import { useState } from "react";
import { createCategoryApi } from "../api/categoryApi";
import { X } from "lucide-react";
import { Button } from "@/components/ui/button";

interface AddCategoryModalProps {
  onClose: () => void;
  onSuccess: () => void;
}

export function AddCategoryModal({ onClose, onSuccess }: AddCategoryModalProps) {
  const [name, setName] = useState("");
  const [description, setDescription] = useState("");
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
      await createCategoryApi({
        name: name.trim(),
        description: description.trim() || undefined,
      });
      onSuccess();
    } catch (err: any) {
      setError(err.message || "Thêm thể loại mới thất bại.");
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="fixed inset-0 bg-black/60 backdrop-blur-xs flex items-center justify-center z-50 p-4">
      <div className="bg-card rounded-2xl max-w-md w-full p-6 shadow-2xl border border-border animate-in fade-in zoom-in-95 duration-150 text-foreground">
        <div className="flex items-center justify-between pb-4 border-b border-border">
          <h2 className="text-xl font-bold text-foreground">Thêm thể loại mới</h2>
          <button
            onClick={onClose}
            className="text-muted-foreground hover:text-foreground p-1 rounded-lg hover:bg-muted transition animate-in spin-in-12"
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
              placeholder="Ví dụ: Kinh tế, Công nghệ thông tin..."
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
              rows={4}
              maxLength={500}
              value={description}
              onChange={(e) => setDescription(e.target.value)}
              placeholder="Nhập mô tả tóm tắt về thể loại sách này..."
              className="w-full px-3.5 py-2.5 border border-border rounded-xl bg-background text-foreground placeholder-muted-foreground shadow-xs focus:outline-none focus:ring-2 focus:ring-primary transition leading-relaxed resize-none text-xs"
            />
            <span className="text-[11px] text-muted-foreground mt-1 block text-right">
              {description.length}/500 ký tự
            </span>
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
              {submitting ? "Đang tạo..." : "Tạo Mới Thể Loại"}
            </Button>
          </div>
        </form>
      </div>
    </div>
  );
}
