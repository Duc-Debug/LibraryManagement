import { useState } from "react";
import { createCategoryApi } from "../api/categoryApi";
import { IconX } from "@/components/icons";

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
      setError(err.message || "Tạo thể loại mới thất bại.");
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="fixed inset-0 bg-black/40 backdrop-blur-xs flex items-center justify-center z-50 p-4">
      <div className="bg-white rounded-2xl max-w-md w-full p-6 shadow-2xl border border-gray-100 animate-in fade-in zoom-in-95 duration-150">
        <div className="flex items-center justify-between pb-4 border-b border-gray-100">
          <h2 className="text-xl font-bold text-gray-900">➕ Thêm Thể Loại Mới</h2>
          <button
            onClick={onClose}
            className="text-gray-400 hover:text-gray-600 p-1 rounded-lg hover:bg-gray-100 transition"
          >
            <IconX />
          </button>
        </div>

        {error && (
          <div className="mt-4 p-3 rounded-xl bg-red-50 border border-red-200 text-red-700 text-xs">
            ⚠️ {error}
          </div>
        )}

        <form onSubmit={handleSubmit} className="mt-4 space-y-4 text-sm">
          <div>
            <label className="block text-xs font-semibold text-gray-700 mb-1">
              Tên Thể Loại <span className="text-red-500">*</span>
            </label>
            <input
              type="text"
              required
              maxLength={100}
              value={name}
              onChange={(e) => setName(e.target.value)}
              placeholder="VD: Công nghệ & Phần mềm, Kinh tế, Văn học..."
              className="w-full px-3.5 py-2.5 border border-gray-200 rounded-xl bg-white shadow-xs focus:outline-none focus:ring-2 focus:ring-emerald-500 transition"
            />
            <span className="text-[11px] text-gray-400 mt-1 block text-right">
              {name.length}/100 ký tự
            </span>
          </div>

          <div>
            <label className="block text-xs font-semibold text-gray-700 mb-1">
              Mô Tả Chi Tiết (Không bắt buộc)
            </label>
            <textarea
              rows={3}
              maxLength={500}
              value={description}
              onChange={(e) => setDescription(e.target.value)}
              placeholder="Nhập mô tả về danh mục sách này..."
              className="w-full px-3.5 py-2.5 border border-gray-200 rounded-xl bg-white shadow-xs focus:outline-none focus:ring-2 focus:ring-emerald-500 transition leading-relaxed resize-none text-xs"
            />
            <span className="text-[11px] text-gray-400 mt-1 block text-right">
              {description.length}/500 ký tự
            </span>
          </div>

          <div className="pt-4 border-t border-gray-100 flex justify-end gap-3">
            <button
              type="button"
              disabled={submitting}
              onClick={onClose}
              className="px-4 py-2 rounded-xl border border-gray-200 text-xs font-medium text-gray-600 hover:bg-gray-50 transition"
            >
              Hủy
            </button>
            <button
              type="submit"
              disabled={submitting}
              className="px-4 py-2 rounded-xl text-xs font-semibold text-white bg-emerald-600 hover:bg-emerald-700 shadow-xs transition disabled:opacity-50"
            >
              {submitting ? "Đang tạo..." : "Lưu Thể Loại"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
