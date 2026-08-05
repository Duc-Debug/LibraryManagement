'use client';

import { useState } from 'react';
import { mockBooks } from '../api/getBooks';
import type { Book } from '../types/book.types';
import { Search, Plus, Edit2, Trash2 } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { AddBookModal } from './AddBookModal';
import { EditBookModal } from './EditBookModal';

const CATEGORIES = ['Tất cả', 'Kinh tế', 'Lịch sử', 'Tâm lý học', 'Chiến lược', 'Tiểu thuyết', 'Công nghệ'];

export function BooksPage() {
  const [books, setBooks] = useState<Book[]>(mockBooks);
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedCategory, setSelectedCategory] = useState('Tất cả');
  const [showAddModal, setShowAddModal] = useState(false);
  const [editingBook, setEditingBook] = useState<Book | null>(null);

  const filteredBooks = books.filter((book) => {
    const matchesSearch =
      book.title.toLowerCase().includes(searchTerm.toLowerCase()) ||
      book.author.toLowerCase().includes(searchTerm.toLowerCase()) ||
      book.isbn.toLowerCase().includes(searchTerm.toLowerCase());

    const matchesCategory = selectedCategory === 'Tất cả' || book.category === selectedCategory;

    return matchesSearch && matchesCategory;
  });

  const handleAddBook = (bookData: Omit<Book, 'id'>) => {
    const newBook: Book = {
      ...bookData,
      id: `book${Date.now()}`,
    };
    setBooks([...books, newBook]);
    setShowAddModal(false);
  };

  const handleDeleteBook = (id: string) => {
    setBooks(books.filter((book) => book.id !== id));
  };

  const handleUpdateBook = (updatedBook: Book) => {
    setBooks(books.map((b) => (b.id === updatedBook.id ? updatedBook : b)));
    setEditingBook(null);
  };

  return (
    <div className="p-8">
      {/* Header */}
      <div className="mb-8 flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold text-foreground mb-2">Quản lý Sách</h1>
          <p className="text-muted-foreground">Quản lý thông tin về các cuốn sách trong thư viện</p>
        </div>
        <Button onClick={() => setShowAddModal(true)} className="bg-primary hover:bg-primary/90">
          <Plus className="w-4 h-4 mr-2" />
          Thêm sách mới
        </Button>
      </div>

      {/* Search and Filter */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6">
        <div className="relative col-span-1 md:col-span-2">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-muted-foreground" />
          <input
            type="text"
            placeholder="Tìm theo tên sách, tác giả hoặc ISBN..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="w-full pl-10 pr-4 py-2 border border-border rounded-lg bg-card text-foreground placeholder-muted-foreground focus:outline-none focus:ring-2 focus:ring-primary"
          />
        </div>
        <select
          value={selectedCategory}
          onChange={(e) => setSelectedCategory(e.target.value)}
          className="px-4 py-2 border border-border rounded-lg bg-card text-foreground focus:outline-none focus:ring-2 focus:ring-primary"
        >
          {CATEGORIES.map((cat) => (
            <option key={cat} value={cat}>
              {cat}
            </option>
          ))}
        </select>
      </div>

      {/* Books Table */}
      <div className="bg-card rounded-lg border border-border shadow-sm overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full">
            <thead>
              <tr className="border-b border-border bg-muted/50">
                <th className="px-6 py-3 text-left text-sm font-semibold text-foreground">Tên sách</th>
                <th className="px-6 py-3 text-left text-sm font-semibold text-foreground">Tác giả</th>
                <th className="px-6 py-3 text-left text-sm font-semibold text-foreground">Thể loại</th>
                <th className="px-6 py-3 text-left text-sm font-semibold text-foreground">ISBN</th>
                <th className="px-6 py-3 text-left text-sm font-semibold text-foreground">Năm</th>
                <th className="px-6 py-3 text-left text-sm font-semibold text-foreground">Có sẵn / Tổng</th>
                <th className="px-6 py-3 text-left text-sm font-semibold text-foreground">Thao tác</th>
              </tr>
            </thead>
            <tbody>
              {filteredBooks.map((book) => (
                <tr key={book.id} className="border-b border-border hover:bg-muted/30 transition-colors">
                  <td className="px-6 py-4 text-sm font-medium text-foreground">{book.title}</td>
                  <td className="px-6 py-4 text-sm text-foreground">{book.author}</td>
                  <td className="px-6 py-4 text-sm text-foreground">{book.category}</td>
                  <td className="px-6 py-4 text-sm text-muted-foreground">{book.isbn}</td>
                  <td className="px-6 py-4 text-sm text-foreground">{book.publishYear}</td>
                  <td className="px-6 py-4 text-sm text-foreground">
                    <span className="font-semibold">{book.availableCopies}</span>
                    <span className="text-muted-foreground"> / {book.totalCopies}</span>
                  </td>
                  <td className="px-6 py-4 text-sm">
                    <div className="flex items-center gap-2">
                      <Button
                        variant="ghost"
                        size="sm"
                        className="h-8 w-8 p-0"
                        onClick={() => setEditingBook(book)}
                      >
                        <Edit2 className="w-4 h-4" />
                      </Button>
                      <Button
                        variant="ghost"
                        size="sm"
                        className="h-8 w-8 p-0 text-destructive hover:text-destructive"
                        onClick={() => handleDeleteBook(book.id)}
                      >
                        <Trash2 className="w-4 h-4" />
                      </Button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {/* Add Book Modal */}
      {showAddModal && (
        <AddBookModal
          onClose={() => setShowAddModal(false)}
          onSave={handleAddBook}
        />
      )}

      {/* Edit Book Modal */}
      {editingBook && (
        <EditBookModal
          book={editingBook}
          existingBooks={books}
          onClose={() => setEditingBook(null)}
          onSave={handleUpdateBook}
        />
      )}
    </div>
  );
}
