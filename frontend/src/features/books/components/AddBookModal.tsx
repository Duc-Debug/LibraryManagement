'use client';

import { useState, useEffect } from 'react';
import { X, Upload } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { fetchCategoriesApi, type CategoryResponse } from '@/api/categoryApi';
import { createBookApi } from '@/api/bookApi';

interface AddBookModalProps {
  onClose: () => void;
  onSave: () => void;
}

export function AddBookModal({ onClose, onSave }: AddBookModalProps) {
  const [categories, setCategories] = useState<CategoryResponse[]>([]);
  const [loadingCategories, setLoadingCategories] = useState(true);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const [formData, setFormData] = useState({
    title: '',
    author: '',
    isbn: '',
    categoryId: 0,
    totalQuantity: 1,
    description: '',
    publisher: '',
    publishedYear: new Date().getFullYear(),
    shelfLocation: '',
  });

  const [coverImageFile, setCoverImageFile] = useState<File | null>(null);
  const [imagePreview, setImagePreview] = useState<string | null>(null);

  // Nạp danh sách thể loại từ DB
  useEffect(() => {
    async function loadCategories() {
      try {
        const data = await fetchCategoriesApi();
        const activeCats = data.filter((c) => c.active);
        setCategories(activeCats);
        if (activeCats.length > 0) {
          setFormData((prev) => ({
            ...prev,
            categoryId: activeCats[0].id,
          }));
        }
      } catch (err: any) {
        console.error("Failed to load categories:", err);
      } finally {
        setLoadingCategories(false);
      }
    }
    loadCategories();
  }, []);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: name === 'publishedYear' || name === 'totalQuantity' || name === 'categoryId' 
        ? (value === "" ? 0 : parseInt(value)) 
        : value,
    }));
  };

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files[0]) {
      const file = e.target.files[0];
      setCoverImageFile(file);
      setImagePreview(URL.createObjectURL(file));
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);

    if (!formData.title.trim()) {
      setError("Tên sách không được để trống.");
      return;
    }
    if (!formData.author.trim()) {
      setError("Tác giả không được để trống.");
      return;
    }
    if (!formData.isbn.trim()) {
      setError("ISBN không được để trống.");
      return;
    }
    if (!formData.categoryId) {
      setError("Vui lòng chọn thể loại.");
      return;
    }
    if (formData.totalQuantity <= 0) {
      setError("Số lượng sách phải lớn hơn 0.");
      return;
    }

    setIsSubmitting(true);

    try {
      const payload = new FormData();
      payload.append("title", formData.title.trim());
      payload.append("author", formData.author.trim());
      payload.append("isbn", formData.isbn.trim());
      payload.append("categoryId", formData.categoryId.toString());
      payload.append("totalQuantity", formData.totalQuantity.toString());

      if (formData.description) payload.append("description", formData.description.trim());
      if (formData.publisher) payload.append("publisher", formData.publisher.trim());
      if (formData.publishedYear) payload.append("publishedYear", formData.publishedYear.toString());
      if (formData.shelfLocation) payload.append("shelfLocation", formData.shelfLocation.trim());

      // Cover image file (Nếu người dùng không chọn ảnh, tạo ảnh mặc định dạng file)
      if (coverImageFile) {
        payload.append("coverImage", coverImageFile);
      } else {
        const dummyFile = new File(["dummy"], "default-cover.jpg", { type: "image/jpeg" });
        payload.append("coverImage", dummyFile);
      }

      await createBookApi(payload);
      onSave();
    } catch (err: any) {
      setError(err.message || "Tạo sách thất bại. Vui lòng kiểm tra lại dữ liệu.");
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
      <div className="bg-card rounded-lg border border-border w-full max-w-lg shadow-lg max-h-[90vh] overflow-y-auto">
        {/* Header */}
        <div className="flex items-center justify-between p-6 border-b border-border sticky top-0 bg-card z-10">
          <h2 className="text-lg font-bold text-foreground">Thêm sách mới</h2>
          <button
            onClick={onClose}
            className="text-muted-foreground hover:text-foreground"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Form */}
        <form onSubmit={handleSubmit} className="p-6 space-y-4">
          {error && (
            <div className="p-3 rounded-lg bg-destructive/10 border border-destructive/20 text-destructive text-sm">
              ⚠️ {error}
            </div>
          )}

          <div>
            <label className="block text-sm font-medium text-foreground mb-1">Tên sách *</label>
            <input
              type="text"
              name="title"
              value={formData.title}
              onChange={handleChange}
              placeholder="Nhập tên sách"
              required
              className="w-full px-3 py-2 border border-border rounded-lg bg-background text-foreground placeholder-muted-foreground focus:outline-none focus:ring-2 focus:ring-primary"
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-foreground mb-1">Tác giả *</label>
            <input
              type="text"
              name="author"
              value={formData.author}
              onChange={handleChange}
              placeholder="Nhập tên tác giả"
              required
              className="w-full px-3 py-2 border border-border rounded-lg bg-background text-foreground placeholder-muted-foreground focus:outline-none focus:ring-2 focus:ring-primary"
            />
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-foreground mb-1">Thể loại *</label>
              <select
                name="categoryId"
                value={formData.categoryId}
                onChange={handleChange}
                disabled={loadingCategories}
                required
                className="w-full px-3 py-2 border border-border rounded-lg bg-background text-foreground focus:outline-none focus:ring-2 focus:ring-primary disabled:opacity-50"
              >
                {loadingCategories ? (
                  <option value={0}>Đang tải...</option>
                ) : categories.length === 0 ? (
                  <option value={0}>Chưa có thể loại</option>
                ) : (
                  categories.map((cat) => (
                    <option key={cat.id} value={cat.id}>
                      {cat.name}
                    </option>
                  ))
                )}
              </select>
            </div>

            <div>
              <label className="block text-sm font-medium text-foreground mb-1">Mã ISBN *</label>
              <input
                type="text"
                name="isbn"
                value={formData.isbn}
                onChange={handleChange}
                placeholder="Nhập mã ISBN"
                required
                className="w-full px-3 py-2 border border-border rounded-lg bg-background text-foreground placeholder-muted-foreground focus:outline-none focus:ring-2 focus:ring-primary"
              />
            </div>
          </div>

          <div className="grid grid-cols-3 gap-4">
            <div>
              <label className="block text-sm font-medium text-foreground mb-1">Tổng số lượng *</label>
              <input
                type="number"
                name="totalQuantity"
                value={formData.totalQuantity}
                onChange={handleChange}
                min="1"
                required
                className="w-full px-3 py-2 border border-border rounded-lg bg-background text-foreground focus:outline-none focus:ring-2 focus:ring-primary"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-foreground mb-1">Năm xuất bản</label>
              <input
                type="number"
                name="publishedYear"
                value={formData.publishedYear}
                onChange={handleChange}
                className="w-full px-3 py-2 border border-border rounded-lg bg-background text-foreground focus:outline-none focus:ring-2 focus:ring-primary"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-foreground mb-1">Vị trí giá sách</label>
              <input
                type="text"
                name="shelfLocation"
                value={formData.shelfLocation}
                onChange={handleChange}
                placeholder="VD: Kệ A1"
                className="w-full px-3 py-2 border border-border rounded-lg bg-background text-foreground placeholder-muted-foreground focus:outline-none focus:ring-2 focus:ring-primary"
              />
            </div>
          </div>

          <div>
            <label className="block text-sm font-medium text-foreground mb-1">Nhà xuất bản</label>
            <input
              type="text"
              name="publisher"
              value={formData.publisher}
              onChange={handleChange}
              placeholder="Nhập nhà xuất bản"
              className="w-full px-3 py-2 border border-border rounded-lg bg-background text-foreground placeholder-muted-foreground focus:outline-none focus:ring-2 focus:ring-primary"
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-foreground mb-1">Mô tả sách</label>
            <textarea
              name="description"
              value={formData.description}
              onChange={handleChange}
              placeholder="Nhập mô tả tóm tắt..."
              rows={2}
              className="w-full px-3 py-2 border border-border rounded-lg bg-background text-foreground placeholder-muted-foreground focus:outline-none focus:ring-2 focus:ring-primary"
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-foreground mb-1">Ảnh bìa sách</label>
            <div className="flex items-center gap-4">
              <label className="flex items-center gap-2 px-4 py-2 border border-border rounded-lg bg-background text-foreground hover:bg-muted cursor-pointer">
                <Upload className="w-4 h-4" />
                <span className="text-sm font-medium">Chọn tệp ảnh</span>
                <input
                  type="file"
                  accept="image/*"
                  onChange={handleFileChange}
                  className="hidden"
                />
              </label>
              {imagePreview && (
                <img
                  src={imagePreview}
                  alt="Preview"
                  className="w-10 h-10 object-cover rounded border border-border"
                />
              )}
            </div>
          </div>

          {/* Buttons */}
          <div className="flex gap-3 pt-4 border-t border-border">
            <Button
              type="button"
              variant="outline"
              onClick={onClose}
              disabled={isSubmitting}
              className="flex-1"
            >
              Hủy
            </Button>
            <Button
              type="submit"
              disabled={isSubmitting}
              className="flex-1 bg-primary hover:bg-primary/90"
            >
              {isSubmitting ? "Đang tạo sách..." : "Lưu sách"}
            </Button>
          </div>
        </form>
      </div>
    </div>
  );
}
