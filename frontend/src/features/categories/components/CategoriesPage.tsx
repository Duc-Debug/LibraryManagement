import { useState, useEffect, useCallback } from "react";
import { CategoryResponse, fetchCategoriesApi, updateCategoryApi } from "../api/categoryApi";
import { AddCategoryModal } from "./AddCategoryModal";
import { EditCategoryModal } from "./EditCategoryModal";
import { ConfirmDeleteCategoryModal } from "./ConfirmDeleteCategoryModal";
import { IconSearch, IconX } from "@/components/icons";

export function CategoriesPage() {
  const [categories, setCategories] = useState<CategoryResponse[]>([]);
  const [search, setSearch] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);

  // Modals state
  const [showAddModal, setShowAddModal] = useState(false);
  const [categoryToEdit, setCategoryToEdit] = useState<CategoryResponse | null>(null);
  const [categoryToDelete, setCategoryToDelete] = useState<CategoryResponse | null>(null);

  const loadCategories = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await fetchCategoriesApi();
      setCategories(data);
    } catch (err: any) {
      setError(err.message || "Không thể tải danh sách thể loại từ máy chủ.");
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    loadCategories();
  }, [loadCategories]);

  // Live search filter
  const filteredCategories = categories.filter((c) =>
    c.name.toLowerCase().includes(search.trim().toLowerCase())
  );

  // Quick toggle active state (Ẩn / Khôi phục)
  const handleToggleActive = async (category: CategoryResponse) => {
    setError(null);
    setSuccessMessage(null);
    try {
      await updateCategoryApi(category.id, { active: !category.active });
      setSuccessMessage(
        `Đã ${category.active ? "ẩn" : "khôi phục"} thể loại "${category.name}" thành công.`
      );
      loadCategories();
    } catch (err: any) {
      setError(err.message || "Thao tác ẩn/khôi phục thể loại thất bại.");
    }
  };

  return (
    <div className="p-8 max-w-7xl mx-auto">
      {/* Header */}
      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Quản lý Thể Loại Sách</h1>
          <p className="text-sm text-gray-500 mt-1">
            Quản lý các danh mục phân loại sách trong hệ thống thư viện
          </p>
        </div>
        <button
          onClick={() => setShowAddModal(true)}
          className="px-4 py-2.5 rounded-xl text-xs font-semibold text-white bg-emerald-600 hover:bg-emerald-700 shadow-xs transition flex items-center gap-2"
        >
          <span>➕</span> Thêm Thể Loại Mới
        </button>
      </div>

      {/* Notifications */}
      {error && (
        <div className="mb-4 p-4 rounded-xl bg-red-50 border border-red-200 text-red-700 text-sm flex justify-between items-center">
          <span>⚠️ {error}</span>
          <button onClick={() => setError(null)} className="text-red-500 hover:text-red-700">
            <IconX />
          </button>
        </div>
      )}

      {successMessage && (
        <div className="mb-4 p-4 rounded-xl bg-emerald-50 border border-emerald-200 text-emerald-700 text-sm flex justify-between items-center">
          <span>✅ {successMessage}</span>
          <button onClick={() => setSuccessMessage(null)} className="text-emerald-500 hover:text-emerald-700">
            <IconX />
          </button>
        </div>
      )}

      {/* Live Search */}
      <div className="relative mb-6">
        <span className="absolute left-3.5 top-1/2 -translate-y-1/2 text-gray-400">
          <IconSearch />
        </span>
        <input
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          placeholder="Tìm kiếm theo tên thể loại..."
          className="w-full pl-10 pr-4 py-3 border border-gray-200 rounded-xl text-sm bg-white shadow-sm focus:outline-none focus:ring-2 focus:ring-emerald-500 transition"
        />
      </div>

      {/* Categories Table */}
      <div className="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-sm text-left">
            <thead className="bg-gray-50/80 border-b border-gray-100 text-xs uppercase text-gray-500 font-semibold">
              <tr>
                <th className="px-6 py-4">Mã ID</th>
                <th className="px-6 py-4">Tên Thể Loại</th>
                <th className="px-6 py-4">Mô Tả</th>
                <th className="px-6 py-4 text-center">Trạng Thái</th>
                <th className="px-6 py-4 text-center">Ngày Tạo</th>
                <th className="px-6 py-4 text-right">Thao Tác</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-100">
              {loading ? (
                <tr>
                  <td colSpan={6} className="px-6 py-8 text-center text-gray-400">
                    Đang nạp dữ liệu thể loại từ máy chủ...
                  </td>
                </tr>
              ) : filteredCategories.length === 0 ? (
                <tr>
                  <td colSpan={6} className="px-6 py-8 text-center text-gray-400">
                    Không tìm thấy thể loại nào trong Database.
                  </td>
                </tr>
              ) : (
                filteredCategories.map((c) => (
                  <tr key={c.id} className="hover:bg-gray-50/60 transition">
                    <td className="px-6 py-4 font-mono text-xs font-semibold text-gray-400">
                      #{c.id}
                    </td>
                    <td className="px-6 py-4 font-bold text-gray-900">{c.name}</td>
                    <td className="px-6 py-4 text-gray-600 max-w-xs truncate text-xs">
                      {c.description || "Chưa có mô tả"}
                    </td>
                    <td className="px-6 py-4 text-center">
                      {c.active ? (
                        <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-emerald-100 text-emerald-800">
                          Đang hiện
                        </span>
                      ) : (
                        <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-gray-100 text-gray-600">
                          Đã ẩn
                        </span>
                      )}
                    </td>
                    <td className="px-6 py-4 text-center text-gray-500 text-xs">
                      {c.createdAt ? new Date(c.createdAt).toLocaleDateString("vi-VN") : "N/A"}
                    </td>
                    <td className="px-6 py-4 text-right">
                      <div className="flex items-center justify-end gap-2">
                        {/* Nút Ẩn / Khôi phục */}
                        <button
                          onClick={() => handleToggleActive(c)}
                          className="px-2.5 py-1.5 rounded-lg border border-gray-200 text-xs font-medium text-gray-700 hover:bg-gray-50 transition"
                        >
                          {c.active ? "👁️‍🗨️ Ẩn" : "👁️ Hiện"}
                        </button>

                        {/* Nút Sửa */}
                        <button
                          onClick={() => setCategoryToEdit(c)}
                          className="px-2.5 py-1.5 rounded-lg border border-gray-200 text-xs font-medium text-gray-700 hover:bg-gray-50 transition"
                        >
                          ✏️ Sửa
                        </button>

                        {/* Nút Xóa */}
                        <button
                          onClick={() => setCategoryToDelete(c)}
                          className="px-2.5 py-1.5 rounded-lg text-xs font-semibold text-white bg-red-600 hover:bg-red-700 transition"
                        >
                          🗑️ Xóa
                        </button>
                      </div>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>

      {/* Modals */}
      {showAddModal && (
        <AddCategoryModal
          onClose={() => setShowAddModal(false)}
          onSuccess={() => {
            setShowAddModal(false);
            setSuccessMessage("Đã thêm mới thể loại thành công!");
            loadCategories();
          }}
        />
      )}

      {categoryToEdit && (
        <EditCategoryModal
          category={categoryToEdit}
          onClose={() => setCategoryToEdit(null)}
          onSuccess={() => {
            setCategoryToEdit(null);
            setSuccessMessage("Đã cập nhật thể loại thành công!");
            loadCategories();
          }}
        />
      )}

      {categoryToDelete && (
        <ConfirmDeleteCategoryModal
          category={categoryToDelete}
          onClose={() => setCategoryToDelete(null)}
          onSuccess={() => {
            setCategoryToDelete(null);
            setSuccessMessage("Đã xóa thể loại thành công!");
            loadCategories();
          }}
        />
      )}
    </div>
  );
}
