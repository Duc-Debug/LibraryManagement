import { useState } from "react";
import { CategoryResponse, updateCategoryApi } from "../api/categoryApi";
import { IconX } from "@/components/icons";

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
    <div className="fixed inset-0 bg-black/40 backdrop-blur-xs flex items-center justify-center z-50 p-4">
      <div className="bg-white rounded-2xl max-w-md w-full p-6 shadow-2xl border border-gray-100 animate-in fade-in zoom-in-95 duration-150">
        <div className="flex items-center justify-between pb-4 border-b border-gray-100">
          <h2 className="text-xl font-bold text-gray-900">✏️ Chỉnh Sửa Thể Loại (ID #{category.id})</h2>
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
              placeholder="Nhập tên thể loại..."
              className="w-full px-3.5 py-2.5 border border-gray-200 rounded-xl bg-white shadow-xs focus:outline-none focus:ring-2 focus:ring-emerald-500 transition"
            />
            <span className="text-[11px] text-gray-400 mt-1 block text-right">
              {name.length}/100 ký tự
            </span>
          </div>

          <div>
            <label className="block text-xs font-semibold text-gray-700 mb-1">
              Mô Tả Chi Tiết
            </label>
            <textarea
              rows={3}
              maxLength={500}
              value={description}
              onChange={(e) => setDescription(e.target.value)}
              placeholder="Nhập mô tả về danh mục sách..."
              className="w-full px-3.5 py-2.5 border border-gray-200 rounded-xl bg-white shadow-xs focus:outline-none focus:ring-2 focus:ring-emerald-500 transition leading-relaxed resize-none text-xs"
            />
            <span className="text-[11px] text-gray-400 mt-1 block text-right">
              {description.length}/500 ký tự
            </span>
          </div>

          <div className="flex items-center justify-between p-3 bg-gray-50 rounded-xl border border-gray-100">
            <div>
              <span className="text-xs font-semibold text-gray-800 block">Trạng Thái Hiển Thị</span>
              <span className="text-[11px] text-gray-400">Cho phép thủ thư chọn thể loại này khi tạo sách</span>
            </div>
            <label className="relative inline-flex items-center cursor-pointer">
              <input
                type="checkbox"
                checked={active}
                onChange={(e) => setActive(e.target.checked)}
                className="sr-only peer"
              />
              <div className="w-11 h-6 bg-gray-200 peer-focus:outline-none rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-emerald-600"></div>
            </label>
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
              {submitting ? "Đang lưu..." : "Cập Nhật"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
