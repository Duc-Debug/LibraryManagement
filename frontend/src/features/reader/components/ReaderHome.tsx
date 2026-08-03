'use client';

import { useState, useMemo } from 'react';
import { Search, BookOpen, Star } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { mockBooks } from '@/features/books';
import type { Book } from '@/features/books';

interface ReaderHomeProps {
  onSelectBook: (book: Book) => void;
  onBorrow?: (book: Book) => void;
}

export function ReaderHome({ onSelectBook, onBorrow }: ReaderHomeProps) {
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedCategory, setSelectedCategory] = useState('');

  const categories = Array.from(new Set(mockBooks.map((b) => b.category)));

  const filteredBooks = useMemo(() => {
    return mockBooks.filter((book) => {
      const matchesSearch =
        searchTerm === '' ||
        book.title.toLowerCase().includes(searchTerm.toLowerCase()) ||
        book.author.toLowerCase().includes(searchTerm.toLowerCase()) ||
        book.category.toLowerCase().includes(searchTerm.toLowerCase());

      const matchesCategory =
        selectedCategory === '' || book.category === selectedCategory;

      return matchesSearch && matchesCategory;
    });
  }, [searchTerm, selectedCategory]);

  return (
    <div className="w-full min-h-screen bg-gradient-to-b from-background to-muted/30 p-6">
      <div className="max-w-6xl mx-auto">
        {/* Header */}
        <div className="mb-8">
          <h1 className="text-4xl font-bold text-foreground mb-2 text-pretty">
            Tìm Kiếm Sách Yêu Thích
          </h1>
          <p className="text-foreground/60">
            Khám phá và mượn hàng nghìn cuốn sách từ thư viện của chúng tôi
          </p>
        </div>

        {/* Search Bar */}
        <div className="mb-6">
          <div className="relative">
            <Search className="absolute left-4 top-1/2 transform -translate-y-1/2 w-5 h-5 text-foreground/40" />
            <input
              type="text"
              placeholder="Tìm theo tiêu đề, tác giả hoặc thể loại..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="w-full pl-12 pr-4 py-3 rounded-lg border border-border bg-card text-foreground placeholder:text-foreground/40 focus:outline-none focus:ring-2 focus:ring-primary"
            />
          </div>
        </div>

        {/* Category Filter */}
        <div className="mb-8 flex flex-wrap gap-2">
          <button
            onClick={() => setSelectedCategory('')}
            className={`px-4 py-2 rounded-lg font-medium transition-all ${
              selectedCategory === ''
                ? 'bg-primary text-primary-foreground'
                : 'bg-muted text-foreground hover:bg-accent'
            }`}
          >
            Tất cả
          </button>
          {categories.map((cat) => (
            <button
              key={cat}
              onClick={() => setSelectedCategory(cat)}
              className={`px-4 py-2 rounded-lg font-medium transition-all ${
                selectedCategory === cat
                  ? 'bg-primary text-primary-foreground'
                  : 'bg-muted text-foreground hover:bg-accent'
              }`}
            >
              {cat}
            </button>
          ))}
        </div>

        {/* Books Grid */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {filteredBooks.length > 0 ? (
            filteredBooks.map((book) => (
              <div
                key={book.id}
                className="bg-card rounded-lg border border-border overflow-hidden hover:shadow-lg transition-shadow"
              >
                {/* Book Cover Placeholder */}
                <div className="bg-gradient-to-br from-primary/20 to-secondary/20 h-48 flex items-center justify-center">
                  <BookOpen className="w-12 h-12 text-primary/40" />
                </div>

                {/* Book Info */}
                <div className="p-4">
                  <h3 className="text-lg font-bold text-foreground mb-1 line-clamp-2">
                    {book.title}
                  </h3>
                  <p className="text-sm text-foreground/60 mb-2">{book.author}</p>

                  <div className="flex items-center gap-2 mb-3">
                    <div className="flex gap-0.5">
                      {[...Array(5)].map((_, i) => (
                        <Star
                          key={i}
                          className={`w-3 h-3 ${
                            i < (book.rating || 4)
                              ? 'fill-secondary text-secondary'
                              : 'text-border'
                          }`}
                        />
                      ))}
                    </div>
                    <span className="text-xs text-foreground/50">
                      {book.publishYear}
                    </span>
                  </div>

                  {/* Availability Badge */}
                  <div className="mb-4">
                    {book.availableCopies > 0 ? (
                      <div className="inline-block bg-accent/20 text-accent px-3 py-1 rounded-full text-xs font-medium">
                        {book.availableCopies} còn lại
                      </div>
                    ) : (
                      <div className="inline-block bg-destructive/20 text-destructive px-3 py-1 rounded-full text-xs font-medium">
                        Hết sách
                      </div>
                    )}
                  </div>

                  {/* Action Buttons */}
                  <div className="flex gap-2">
                    <Button
                      variant="outline"
                      size="sm"
                      className="flex-1"
                      onClick={() => onSelectBook(book)}
                    >
                      Chi tiết
                    </Button>
                    <Button
                      size="sm"
                      className="flex-1"
                      disabled={book.availableCopies === 0}
                      onClick={() => onBorrow?.(book)}
                    >
                      Mượn
                    </Button>
                  </div>
                </div>
              </div>
            ))
          ) : (
            <div className="col-span-full text-center py-12">
              <BookOpen className="w-12 h-12 text-foreground/20 mx-auto mb-4" />
              <p className="text-foreground/60">Không tìm thấy sách nào phù hợp</p>
            </div>
          )}
        </div>

        {/* Results Count */}
        <div className="mt-8 text-center text-sm text-foreground/60">
          Tìm thấy {filteredBooks.length} cuốn sách
        </div>
      </div>
    </div>
  );
}
