'use client';

import { parseErrorMessage } from "@/lib/errorDictionary";
import { useState, useEffect, useCallback } from "react";
import { CategoryResponse, fetchCategoriesApi, updateCategoryApi } from "../api/categoryApi";
import { fetchBooksApi, BookResponseDto } from "@/api/bookApi";
import { AddCategoryModal } from "./AddCategoryModal";
import { EditCategoryModal } from "./EditCategoryModal";
import { ConfirmDeleteCategoryModal } from "./ConfirmDeleteCategoryModal";
import { Button } from "@/components/ui/button";
import { Search, Plus, Eye, EyeOff, Edit3, Trash2, X, LayoutGrid, List, Tag, BookOpen, Layers } from "lucide-react";

export function CategoriesPage() {
  const [categories, setCategories] = useState<CategoryResponse[]>([]);
  const [books, setBooks] = useState<BookResponseDto[]>([]);
  const [search, setSearch] = useState("");
  const [viewMode, setViewMode] = useState<'grid' | 'table'>('grid');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);

  // Modals state
  const [showAddModal, setShowAddModal] = useState(false);
  const [categoryToEdit, setCategoryToEdit] = useState<CategoryResponse | null>(null);
  const [categoryToDelete, setCategoryToDelete] = useState<CategoryResponse | null>(null);

  const loadCategoriesAndBooks = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const [catData, booksData] = await Promise.allSettled([
        fetchCategoriesApi(),
        fetchBooksApi(0, 100)
      ]);
      
      if (catData.status === 'fulfilled') {
        setCategories(catData.value);
      }
      if (booksData.status === 'fulfilled' && booksData.value) {
        setBooks(booksData.value.content || booksData.value.items || []);
      }
    } catch (err: any) {
      setError(parseErrorMessage(err, "Không thể tải danh sách thể loại từ máy chủ."));
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    loadCategoriesAndBooks();
  }, [loadCategoriesAndBooks]);

  // Live search filter
  const filteredCategories = categories.filter((c) =>
    c.name.toLowerCase().includes(search.trim().toLowerCase())
  );

  // Calculate book count per category
  const categoryBookCountMap: Record<string, number> = {};
  books.forEach(b => {
    if (b.categoryName) {
      categoryBookCountMap[b.categoryName] = (categoryBookCountMap[b.categoryName] || 0) + 1;
    }
  });

  // Quick toggle active state (Ẩn / Khôi phục)
  const handleToggleActive = async (category: CategoryResponse) => {
    setError(null);
    setSuccessMessage(null);
    try {
      await updateCategoryApi(category.id, { active: !category.active });
      setSuccessMessage(
        `Đã ${category.active ? "ẩn" : "khôi phục"} thể loại "${category.name}" thành công.`
      );
      loadCategoriesAndBooks();
    } catch (err: any) {
      setError(parseErrorMessage(err, "Thao tác ẩn/khôi phục thể loại thất bại."));
    }
  };

  return (
    <div className="p-6 md:p-8 max-w-7xl mx-auto space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-2 border-b border-border/50">
        <div>
          <h1 className="text-2xl font-bold text-foreground flex items-center gap-2">
            <Layers className="w-6 h-6 text-primary" />
            <span>Quản Lý Thể Loại Sách</span>
          </h1>
          <p className="text-sm text-muted-foreground mt-1">
            Phân loại danh mục sách, quản lý trạng thái hiển thị và xem thống kê đầu sách.
          </p>
        </div>
        <Button
          onClick={() => setShowAddModal(true)}
          className="bg-primary hover:bg-primary/90 text-primary-foreground font-semibold flex items-center gap-2 shadow-md cursor-pointer self-start sm:self-auto"
        >
          <Plus className="w-4 h-4" /> Thêm Thể Loại Mới
        </Button>
      </div>

      {/* Notifications */}
      {error && (
        <div className="p-4 rounded-xl bg-destructive/10 border border-destructive/20 text-destructive text-sm flex justify-between items-center">
          <span>⚠️ {error}</span>
          <button onClick={() => setError(null)} className="text-destructive hover:opacity-80 cursor-pointer">
            <X className="w-4 h-4" />
          </button>
        </div>
      )}

      {successMessage && (
        <div className="p-4 rounded-xl bg-emerald-500/10 border border-emerald-500/20 text-emerald-600 text-sm flex justify-between items-center">
          <span>✅ {successMessage}</span>
          <button onClick={() => setSuccessMessage(null)} className="text-emerald-600 hover:opacity-80 cursor-pointer">
            <X className="w-4 h-4" />
          </button>
        </div>
      )}

      {/* Live Search & View Mode Selector */}
      <div className="flex flex-col sm:flex-row items-center justify-between gap-4">
        <div className="relative w-full sm:max-w-md">
          <Search className="absolute left-3.5 top-1/2 -translate-y-1/2 w-4 h-4 text-muted-foreground" />
          <input
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            placeholder="Tìm kiếm theo tên thể loại..."
            className="w-full pl-10 pr-4 py-2.5 border border-border rounded-xl text-sm bg-card text-foreground shadow-xs focus:outline-none focus:ring-2 focus:ring-primary transition"
          />
        </div>

        <div className="flex items-center gap-1 bg-muted/60 p-1 rounded-xl border border-border/50 text-xs self-end sm:self-auto">
          <button
            onClick={() => setViewMode('grid')}
            className={`flex items-center gap-1.5 px-3 py-1.5 rounded-lg font-medium transition-all cursor-pointer ${
              viewMode === 'grid' ? 'bg-card text-foreground shadow-sm' : 'text-muted-foreground hover:text-foreground'
            }`}
          >
            <LayoutGrid className="w-3.5 h-3.5" />
            <span>Thẻ Grid</span>
          </button>
          <button
            onClick={() => setViewMode('table')}
            className={`flex items-center gap-1.5 px-3 py-1.5 rounded-lg font-medium transition-all cursor-pointer ${
              viewMode === 'table' ? 'bg-card text-foreground shadow-sm' : 'text-muted-foreground hover:text-foreground'
            }`}
          >
            <List className="w-3.5 h-3.5" />
            <span>Dạng Bảng</span>
          </button>
        </div>
      </div>

      {/* Categories Content Views */}
      {loading ? (
        <div className="p-12 text-center text-sm text-muted-foreground">Đang nạp dữ liệu thể loại từ máy chủ...</div>
      ) : filteredCategories.length === 0 ? (
        <div className="p-12 text-center text-sm text-muted-foreground bg-card rounded-2xl border border-border">
          Không tìm thấy thể loại nào trong Database.
        </div>
      ) : viewMode === 'grid' ? (
        /* GRID CARD VIEW */
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-5">
          {filteredCategories.map((c) => {
            const count = categoryBookCountMap[c.name] || 0;
            return (
              <div
                key={c.id}
                className="group relative bg-card/90 backdrop-blur-md rounded-2xl border border-border/80 p-5 shadow-sm hover:shadow-md hover:border-primary/40 transition-all space-y-4 flex flex-col justify-between"
              >
                <div className="space-y-2">
                  <div className="flex items-center justify-between">
                    <span className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-semibold bg-primary/10 text-primary border border-primary/20 whitespace-nowrap">
                      <Tag className="w-3 h-3" /> #{c.id}
                    </span>
                    {c.active ? (
                      <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-emerald-500/10 text-emerald-600 dark:text-emerald-400 border border-emerald-500/20 whitespace-nowrap">
                        Đang hiện
                      </span>
                    ) : (
                      <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-muted text-muted-foreground whitespace-nowrap">
                        Đã ẩn
                      </span>
                    )}
                  </div>
                  <h3 className="text-lg font-bold text-foreground group-hover:text-primary transition-colors">
                    {c.name}
                  </h3>
                  <p className="text-xs text-muted-foreground line-clamp-2">
                    {c.description || "Chưa có mô tả chi tiết cho thể loại này."}
                  </p>
                </div>

                <div className="pt-3 border-t border-border/40 flex items-center justify-between text-xs">
                  <div className="flex items-center gap-1.5 text-muted-foreground">
                    <BookOpen className="w-3.5 h-3.5 text-primary" />
                    <span className="font-semibold text-foreground">{count} tựa sách</span>
                  </div>

                  <div className="flex items-center gap-1.5">
                    <Button
                      variant="outline"
                      size="sm"
                      onClick={() => handleToggleActive(c)}
                      className="text-xs h-7 px-2"
                      title={c.active ? 'Ẩn thể loại' : 'Hiện thể loại'}
                    >
                      {c.active ? <EyeOff className="w-3 h-3 text-muted-foreground" /> : <Eye className="w-3 h-3 text-emerald-600" />}
                    </Button>
                    <Button
                      variant="outline"
                      size="sm"
                      onClick={() => setCategoryToEdit(c)}
                      className="text-xs h-7 px-2"
                      title="Chỉnh sửa"
                    >
                      <Edit3 className="w-3 h-3 text-primary" />
                    </Button>
                    <Button
                      variant="destructive"
                      size="sm"
                      onClick={() => setCategoryToDelete(c)}
                      className="text-xs h-7 px-2"
                      title="Xóa"
                    >
                      <Trash2 className="w-3 h-3" />
                    </Button>
                  </div>
                </div>
              </div>
            );
          })}
        </div>
      ) : (
        /* TABLE VIEW */
        <div className="bg-card rounded-2xl shadow-xs border border-border overflow-hidden">
          <div className="overflow-x-auto">
            <table className="w-full text-sm text-left">
              <thead className="bg-muted/50 border-b border-border text-xs uppercase text-muted-foreground font-semibold whitespace-nowrap">
                <tr>
                  <th className="px-6 py-4 whitespace-nowrap">Mã ID</th>
                  <th className="px-6 py-4 whitespace-nowrap">Tên Thể Loại</th>
                  <th className="px-6 py-4 whitespace-nowrap">Mô Tả</th>
                  <th className="px-6 py-4 text-center whitespace-nowrap">Số Tựa Sách</th>
                  <th className="px-6 py-4 text-center whitespace-nowrap">Trạng Thái</th>
                  <th className="px-6 py-4 text-right whitespace-nowrap">Thao Tác</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-border">
                {filteredCategories.map((c) => (
                  <tr key={c.id} className="hover:bg-muted/30 transition">
                    <td className="px-6 py-4 font-mono text-xs font-semibold text-muted-foreground whitespace-nowrap">
                      #{c.id}
                    </td>
                    <td className="px-6 py-4 font-bold text-foreground whitespace-nowrap">{c.name}</td>
                    <td className="px-6 py-4 text-muted-foreground max-w-xs truncate text-xs whitespace-nowrap">
                      {c.description || "Chưa có mô tả"}
                    </td>
                    <td className="px-6 py-4 text-center font-bold text-primary whitespace-nowrap">
                      {categoryBookCountMap[c.name] || 0} cuốn
                    </td>
                    <td className="px-6 py-4 text-center whitespace-nowrap">
                      {c.active ? (
                        <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-emerald-500/15 text-emerald-600 dark:text-emerald-400 whitespace-nowrap border border-emerald-500/20">
                          Đang hiện
                        </span>
                      ) : (
                        <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-muted text-muted-foreground whitespace-nowrap">
                          Đã ẩn
                        </span>
                      )}
                    </td>
                    <td className="px-6 py-4 text-right whitespace-nowrap">
                      <div className="flex items-center justify-end gap-2">
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

                        <Button
                          variant="outline"
                          size="sm"
                          onClick={() => setCategoryToEdit(c)}
                          className="text-xs h-8"
                        >
                          <Edit3 className="w-3.5 h-3.5 mr-1.5" /> Sửa
                        </Button>

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
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* Modals */}
      {showAddModal && (
        <AddCategoryModal
          onClose={() => setShowAddModal(false)}
          onSuccess={() => {
            setShowAddModal(false);
            setSuccessMessage("Đã thêm mới thể loại thành công!");
            loadCategoriesAndBooks();
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
            loadCategoriesAndBooks();
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
            loadCategoriesAndBooks();
          }}
        />
      )}
    </div>
  );
}
