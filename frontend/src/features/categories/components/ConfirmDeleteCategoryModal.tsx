import { useState } from "react";
import { CategoryResponse, deleteCategoryApi } from "../api/categoryApi";
import { IconX } from "@/components/icons";

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
    <div className="fixed inset-0 bg-black/40 backdrop-blur-xs flex items-center justify-center z-50 p-4">
      <div className="bg-white rounded-2xl max-w-md w-full p-6 shadow-2xl border border-red-100 space-y-4 animate-in fade-in zoom-in-95 duration-150">
        <div className="flex items-center justify-between pb-3 border-b border-gray-100">
          <div className="flex items-center gap-3 text-red-600">
            <div className="p-2.5 rounded-2xl bg-red-50 text-xl">⚠️</div>
            <div>
              <h3 className="text-lg font-bold text-gray-900">Xác Nhận Xóa Thể Loại</h3>
              <p className="text-xs text-gray-500">Hành động này không thể hoàn tác!</p>
            </div>
          </div>
          <button
            onClick={onClose}
            className="text-gray-400 hover:text-gray-600 p-1 rounded-lg hover:bg-gray-100 transition"
          >
            <IconX />
          </button>
        </div>

        {error && (
          <div className="p-3.5 rounded-xl bg-red-50 border border-red-200 text-red-700 text-xs leading-relaxed">
            ⚠️ {error}
          </div>
        )}

        <div className="py-2 text-sm text-gray-700 space-y-2">
          <p>Bạn có chắc chắn muốn xóa vĩnh viễn thể loại sách này?</p>
          <div className="p-3 bg-red-50/50 rounded-xl border border-red-100 font-semibold text-red-900">
            "{category.name}" (Mã ID: #{category.id})
          </div>
          <p className="text-xs text-gray-400 leading-relaxed">
            ⚠️ Lưu ý: Hệ thống sẽ tự động kiểm tra. Nếu thể loại đang có cuốn sách nào liên kết, thao tác xóa sẽ bị chặn để bảo vệ dữ liệu sách (HTTP 409 Conflict).
          </p>
        </div>

        <div className="pt-3 border-t border-gray-100 flex justify-end gap-3">
          <button
            disabled={deleting}
            onClick={onClose}
            className="px-4 py-2 rounded-xl border border-gray-200 text-xs font-medium text-gray-600 hover:bg-gray-50 transition"
          >
            Hủy
          </button>
          <button
            disabled={deleting}
            onClick={handleConfirmDelete}
            className="px-4 py-2 rounded-xl text-xs font-semibold text-white bg-red-600 hover:bg-red-700 shadow-xs transition disabled:opacity-50"
          >
            {deleting ? "Đang xóa..." : "Xác Nhận Xóa Vĩnh Viễn"}
          </button>
        </div>
      </div>
    </div>
  );
}
