import { useState, useEffect, useCallback } from "react";
import { CategoryResponse, fetchCategoriesApi, updateCategoryApi } from "../api/categoryApi";
import { AddCategoryModal } from "./AddCategoryModal";
import { EditCategoryModal } from "./EditCategoryModal";
import { ConfirmDeleteCategoryModal } from "./ConfirmDeleteCategoryModal";
import { Button } from "@/components/ui/button";
import { Search, Plus, Eye, EyeOff, Edit3, Trash2, X } from "lucide-react";

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
          <h1 className="text-2xl font-bold text-foreground">Quản lý Thể Loại Sách</h1>
          <p className="text-sm text-muted-foreground mt-1">
            Quản lý các danh mục phân loại sách trong hệ thống thư viện
          </p>
        </div>
        <Button
          onClick={() => setShowAddModal(true)}
          className="bg-primary hover:bg-primary/90 text-primary-foreground font-semibold flex items-center gap-2"
        >
          <Plus className="w-4 h-4" /> Thêm Thể Loại Mới
        </Button>
      </div>

      {/* Notifications */}
      {error && (
        <div className="mb-4 p-4 rounded-xl bg-destructive/10 border border-destructive/20 text-destructive text-sm flex justify-between items-center">
          <span>⚠️ {error}</span>
          <button onClick={() => setError(null)} className="text-destructive hover:opacity-80">
            <X className="w-4 h-4" />
          </button>
        </div>
      )}

      {successMessage && (
        <div className="mb-4 p-4 rounded-xl bg-emerald-500/10 border border-emerald-500/20 text-emerald-600 text-sm flex justify-between items-center">
          <span>✅ {successMessage}</span>
          <button onClick={() => setSuccessMessage(null)} className="text-emerald-600 hover:opacity-80">
            <X className="w-4 h-4" />
          </button>
        </div>
      )}

      {/* Live Search */}
      <div className="relative mb-6">
        <Search className="absolute left-3.5 top-1/2 -translate-y-1/2 w-5 h-5 text-muted-foreground" />
        <input
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          placeholder="Tìm kiếm theo tên thể loại..."
          className="w-full pl-10 pr-4 py-3 border border-border rounded-xl text-sm bg-background text-foreground shadow-xs focus:outline-none focus:ring-2 focus:ring-emerald-500 transition"
        />
      </div>

      {/* Categories Table */}
      <div className="bg-card rounded-2xl shadow-xs border border-border overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-sm text-left">
            <thead className="bg-muted/50 border-b border-border text-xs uppercase text-muted-foreground font-semibold">
              <tr>
                <th className="px-6 py-4">Mã ID</th>
                <th className="px-6 py-4">Tên Thể Loại</th>
                <th className="px-6 py-4">Mô Tả</th>
                <th className="px-6 py-4 text-center">Trạng Thái</th>
                <th className="px-6 py-4 text-center">Ngày Tạo</th>
                <th className="px-6 py-4 text-right">Thao Tác</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-border">
              {loading ? (
                <tr>
                  <td colSpan={6} className="px-6 py-8 text-center text-muted-foreground">
                    Đang nạp dữ liệu thể loại từ máy chủ...
                  </td>
                </tr>
              ) : filteredCategories.length === 0 ? (
                <tr>
                  <td colSpan={6} className="px-6 py-8 text-center text-muted-foreground">
                    Không tìm thấy thể loại nào trong Database.
                  </td>
                </tr>
              ) : (
                filteredCategories.map((c) => (
                  <tr key={c.id} className="hover:bg-muted/30 transition">
                    <td className="px-6 py-4 font-mono text-xs font-semibold text-muted-foreground">
                      #{c.id}
                    </td>
                    <td className="px-6 py-4 font-bold text-foreground">{c.name}</td>
                    <td className="px-6 py-4 text-muted-foreground max-w-xs truncate text-xs">
                      {c.description || "Chưa có mô tả"}
                    </td>
                    <td className="px-6 py-4 text-center">
                      {c.active ? (
                        <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-emerald-500/15 text-emerald-600">
                          Đang hiện
                        </span>
                      ) : (
                        <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-muted text-muted-foreground">
                          Đã ẩn
                        </span>
                      )}
                    </td>
                    <td className="px-6 py-4 text-center text-muted-foreground text-xs">
                      {c.createdAt ? new Date(c.createdAt).toLocaleDateString("vi-VN") : "N/A"}
                    </td>
                    <td className="px-6 py-4 text-right">
                      <div className="flex items-center justify-end gap-2">
                        {/* Nút Ẩn / Khôi phục */}
                        <Button
                          variant="outline"
                          size="sm"
                          onClick={() => handleToggleActive(c)}
                          className="text-xs h-8"
                        >
                          {c.active ? (
                            <>
                              <EyeOff className="w-3.5 h-3.5 mr-1.5" /> Ẩn
                            </>
                          ) : (
                            <>
                              <Eye className="w-3.5 h-3.5 mr-1.5" /> Hiện
                            </>
                          )}
                        </Button>

                        {/* Nút Sửa */}
                        <Button
                          variant="outline"
                          size="sm"
                          onClick={() => setCategoryToEdit(c)}
                          className="text-xs h-8"
                        >
                          <Edit3 className="w-3.5 h-3.5 mr-1.5" /> Sửa
                        </Button>

                        {/* Nút Xóa */}
                        <Button
                          variant="destructive"
                          size="sm"
                          onClick={() => setCategoryToDelete(c)}
                          className="text-xs h-8 font-semibold"
                        >
                          <Trash2 className="w-3.5 h-3.5 mr-1.5" /> Xóa
                        </Button>
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
