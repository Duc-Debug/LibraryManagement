'use client';

import { useState, useMemo, useRef, useEffect } from 'react';
import type { ReaderResponse } from '@/api/readerApi';
import type { BookResponseDto } from '@/api/bookApi';
import type { CategoryResponse } from '@/api/categoryApi';
import { Button } from '@/components/ui/button';
import { Search, UserCheck, BookOpen, CreditCard, Calendar, AlertTriangle, Check, X, ShieldAlert, Image as ImageIcon } from 'lucide-react';

interface BorrowingFormProps {
  readers: ReaderResponse[];
  books: BookResponseDto[];
  categories: CategoryResponse[];
  onSubmit: (record: any) => void;
  onCancel: () => void;
}

export function BorrowingForm({ readers, books, categories, onSubmit, onCancel }: BorrowingFormProps) {
  const [selectedReader, setSelectedReader] = useState<ReaderResponse | null>(null);
  const [selectedBook, setSelectedBook] = useState<BookResponseDto | null>(null);
  
  const [readerSearch, setReaderSearch] = useState('');
  const [bookSearch, setBookSearch] = useState('');
  
  const [isReaderDropdownOpen, setIsReaderDropdownOpen] = useState(false);
  const [isBookDropdownOpen, setIsBookDropdownOpen] = useState(false);

  const [borrowDate, setBorrowDate] = useState(new Date().toISOString().split('T')[0]);
  const [dueDate, setDueDate] = useState(
    new Date(Date.now() + 14 * 24 * 60 * 60 * 1000).toISOString().split('T')[0]
  );

  const readerRef = useRef<HTMLDivElement>(null);
  const bookRef = useRef<HTMLDivElement>(null);

  // Close dropdowns on outside click
  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (readerRef.current && !readerRef.current.contains(event.target as Node)) {
        setIsReaderDropdownOpen(false);
      }
      if (bookRef.current && !bookRef.current.contains(event.target as Node)) {
        setIsBookDropdownOpen(false);
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  // Filter Active Readers matching search
  const filteredReaders = useMemo(() => {
    const term = readerSearch.toLowerCase().trim();
    return readers.filter((r) => {
      if (r.cardStatus === 'LOCKED') return false; // Hide locked readers
      if (!term) return true;
      return (
        r.name.toLowerCase().includes(term) ||
        r.cardNumber.toLowerCase().includes(term) ||
        r.phoneNumber.includes(term) ||
        r.email.toLowerCase().includes(term)
      );
    });
  }, [readers, readerSearch]);

  // Inactive categories set
  const inactiveCatNames = useMemo(() => {
    return new Set(categories.filter((c) => !c.active).map((c) => c.name));
  }, [categories]);

  // Filter Available Books matching search (Exclude books with inactive categories or 0 copies)
  const filteredBooks = useMemo(() => {
    const term = bookSearch.toLowerCase().trim();
    return books.filter((b) => {
      if (!b.active) return false; // Hidden book
      if (b.availableQuantity <= 0) return false; // Out of stock
      if (b.categoryName && inactiveCatNames.has(b.categoryName)) return false; // Hidden category
      if (!term) return true;
      return (
        b.title.toLowerCase().includes(term) ||
        b.isbn.toLowerCase().includes(term) ||
        b.author.toLowerCase().includes(term) ||
        (b.categoryName && b.categoryName.toLowerCase().includes(term))
      );
    });
  }, [books, bookSearch, inactiveCatNames]);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (selectedReader && selectedBook) {
      onSubmit({
        readerId: selectedReader.id,
        readerName: selectedReader.name,
        readerCardNumber: selectedReader.cardNumber,
        bookId: selectedBook.bookId,
        bookTitle: selectedBook.title,
        bookIsbn: selectedBook.isbn,
        borrowDate,
        dueDate,
      });
    }
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-6">
      <div className="flex items-center justify-between pb-3 border-b border-border/60">
        <div>
          <h3 className="text-lg font-bold text-foreground flex items-center gap-2">
            <BookOpen className="w-5 h-5 text-primary" />
            <span>Tạo Phiếu Mượn Sách Mới</span>
          </h3>
          <p className="text-xs text-muted-foreground mt-0.5">
            Tìm kiếm bằng Tên, Mã thẻ Độc giả hoặc Tên sách, ISBN để chọn nhanh.
          </p>
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        {/* 1. SEARCHABLE READER COMBOBOX */}
        <div className="space-y-2 relative" ref={readerRef}>
          <label className="block text-xs font-semibold uppercase tracking-wider text-muted-foreground">
            1. Chọn Độc Giả (Gõ tên, mã thẻ, SĐT...) <span className="text-destructive">*</span>
          </label>

          {selectedReader ? (
            /* Selected Reader Card Display */
            <div className="p-3.5 rounded-xl bg-card border-2 border-primary/40 shadow-xs flex items-center justify-between">
              <div className="flex items-center gap-3">
                <div className="w-10 h-10 rounded-full bg-primary/10 text-primary flex items-center justify-center font-bold text-sm">
                  {selectedReader.name.substring(0, 2).toUpperCase()}
                </div>
                <div>
                  <div className="font-bold text-sm text-foreground flex items-center gap-2">
                    <span>{selectedReader.name}</span>
                    <span className="font-mono text-xs text-primary font-semibold px-2 py-0.5 rounded-md bg-primary/10 border border-primary/20">
                      {selectedReader.cardNumber}
                    </span>
                  </div>
                  <div className="text-xs text-muted-foreground mt-0.5">
                    SĐT: {selectedReader.phoneNumber} • {selectedReader.email}
                  </div>
                </div>
              </div>

              <Button
                type="button"
                variant="ghost"
                size="sm"
                onClick={() => {
                  setSelectedReader(null);
                  setReaderSearch('');
                }}
                className="h-8 w-8 p-0 rounded-lg text-muted-foreground hover:text-destructive hover:bg-destructive/10"
                title="Chọn lại độc giả"
              >
                <X className="w-4 h-4" />
              </Button>
            </div>
          ) : (
            /* Searchable Input Dropdown */
            <div>
              <div className="relative">
                <Search className="absolute left-3.5 top-1/2 -translate-y-1/2 w-4 h-4 text-muted-foreground" />
                <input
                  type="text"
                  value={readerSearch}
                  onFocus={() => setIsReaderDropdownOpen(true)}
                  onChange={(e) => {
                    setReaderSearch(e.target.value);
                    setIsReaderDropdownOpen(true);
                  }}
                  placeholder="Gõ tìm mã thẻ CARD-1001, Nguyễn Văn A..."
                  className="w-full pl-10 pr-4 py-2.5 text-sm border border-border rounded-xl bg-background text-foreground focus:outline-none focus:ring-2 focus:ring-primary shadow-xs"
                />
              </div>

              {isReaderDropdownOpen && (
                <div className="absolute left-0 right-0 top-full mt-1 max-h-60 overflow-y-auto bg-card border border-border rounded-xl shadow-xl z-50 divide-y divide-border/60">
                  {filteredReaders.length === 0 ? (
                    <div className="p-4 text-center text-xs text-muted-foreground">
                      Không tìm thấy bạn đọc nào phù hợp hoặc thẻ đã bị khóa.
                    </div>
                  ) : (
                    filteredReaders.map((r) => (
                      <div
                        key={r.id}
                        onClick={() => {
                          setSelectedReader(r);
                          setIsReaderDropdownOpen(false);
                        }}
                        className="p-3 hover:bg-muted/40 transition cursor-pointer flex items-center justify-between"
                      >
                        <div>
                          <div className="font-semibold text-xs text-foreground flex items-center gap-2">
                            <span>{r.name}</span>
                            <span className="font-mono text-[11px] text-primary">{r.cardNumber}</span>
                          </div>
                          <div className="text-[11px] text-muted-foreground mt-0.5">
                            {r.phoneNumber} • {r.email}
                          </div>
                        </div>
                        <span className="text-[10px] px-2 py-0.5 rounded-full bg-emerald-500/10 text-emerald-600 font-medium">
                          Active
                        </span>
                      </div>
                    ))
                  )}
                </div>
              )}
            </div>
          )}
        </div>

        {/* 2. SEARCHABLE BOOK COMBOBOX */}
        <div className="space-y-2 relative" ref={bookRef}>
          <label className="block text-xs font-semibold uppercase tracking-wider text-muted-foreground">
            2. Chọn Sách Mượn (Gõ tên sách, ISBN, tác giả...) <span className="text-destructive">*</span>
          </label>

          {selectedBook ? (
            /* Selected Book Card Display */
            <div className="p-3.5 rounded-xl bg-card border-2 border-primary/40 shadow-xs flex items-center justify-between">
              <div className="flex items-center gap-3">
                {selectedBook.coverImageUrl ? (
                  <img
                    src={selectedBook.coverImageUrl}
                    alt={selectedBook.title}
                    className="w-10 h-14 object-cover rounded-lg border border-border shrink-0 shadow-xs"
                  />
                ) : (
                  <div className="w-10 h-14 bg-muted/40 rounded-lg border border-border shrink-0 flex items-center justify-center text-muted-foreground">
                    <ImageIcon className="w-4 h-4 opacity-50" />
                  </div>
                )}
                <div>
                  <div className="font-bold text-sm text-foreground line-clamp-1">
                    {selectedBook.title}
                  </div>
                  <div className="text-xs text-muted-foreground mt-0.5 flex items-center gap-2">
                    <span>Tác giả: {selectedBook.author}</span>
                    <span className="font-mono text-[11px] text-primary font-bold">
                      Còn {selectedBook.availableQuantity} cuốn
                    </span>
                  </div>
                  <div className="text-[11px] text-muted-foreground mt-0.5">
                    Kệ: {selectedBook.shelfLocation || 'Chưa xếp'} • ISBN: {selectedBook.isbn}
                  </div>
                </div>
              </div>

              <Button
                type="button"
                variant="ghost"
                size="sm"
                onClick={() => {
                  setSelectedBook(null);
                  setBookSearch('');
                }}
                className="h-8 w-8 p-0 rounded-lg text-muted-foreground hover:text-destructive hover:bg-destructive/10"
                title="Chọn lại cuốn sách"
              >
                <X className="w-4 h-4" />
              </Button>
            </div>
          ) : (
            /* Searchable Input Dropdown */
            <div>
              <div className="relative">
                <Search className="absolute left-3.5 top-1/2 -translate-y-1/2 w-4 h-4 text-muted-foreground" />
                <input
                  type="text"
                  value={bookSearch}
                  onFocus={() => setIsBookDropdownOpen(true)}
                  onChange={(e) => {
                    setBookSearch(e.target.value);
                    setIsBookDropdownOpen(true);
                  }}
                  placeholder="Gõ tên sách, mã ISBN 978-..."
                  className="w-full pl-10 pr-4 py-2.5 text-sm border border-border rounded-xl bg-background text-foreground focus:outline-none focus:ring-2 focus:ring-primary shadow-xs"
                />
              </div>

              {isBookDropdownOpen && (
                <div className="absolute left-0 right-0 top-full mt-1 max-h-60 overflow-y-auto bg-card border border-border rounded-xl shadow-xl z-50 divide-y divide-border/60">
                  {filteredBooks.length === 0 ? (
                    <div className="p-4 text-center text-xs text-muted-foreground">
                      Không tìm thấy cuốn sách nào còn khả dụng hoặc thuộc Thể loại bị ẩn.
                    </div>
                  ) : (
                    filteredBooks.map((b) => (
                      <div
                        key={b.bookId}
                        onClick={() => {
                          setSelectedBook(b);
                          setIsBookDropdownOpen(false);
                        }}
                        className="p-3 hover:bg-muted/40 transition cursor-pointer flex items-center justify-between"
                      >
                        <div className="flex items-center gap-3">
                          {b.coverImageUrl ? (
                            <img src={b.coverImageUrl} alt={b.title} className="w-8 h-11 object-cover rounded-md border border-border shrink-0" />
                          ) : (
                            <div className="w-8 h-11 bg-muted/40 rounded-md border border-border shrink-0 flex items-center justify-center">
                              <BookOpen className="w-3.5 h-3.5 text-muted-foreground" />
                            </div>
                          )}
                          <div>
                            <div className="font-semibold text-xs text-foreground">{b.title}</div>
                            <div className="text-[11px] text-muted-foreground">
                              {b.author} • ISBN: {b.isbn}
                            </div>
                          </div>
                        </div>

                        <div className="text-right whitespace-nowrap">
                          <div className="text-xs font-bold text-primary">
                            Còn {b.availableQuantity} cuốn
                          </div>
                          <div className="text-[10px] text-muted-foreground">
                            Kệ: {b.shelfLocation || 'N/A'}
                          </div>
                        </div>
                      </div>
                    ))
                  )}
                </div>
              )}
            </div>
          )}
        </div>
      </div>

      {/* DATES SELECTION */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        <div>
          <label className="block text-xs font-semibold uppercase tracking-wider text-muted-foreground mb-1">
            Ngày Mượn Sách
          </label>
          <input
            type="date"
            value={borrowDate}
            onChange={(e) => setBorrowDate(e.target.value)}
            className="w-full px-3.5 py-2 text-sm border border-border rounded-xl bg-background text-foreground focus:outline-none focus:ring-2 focus:ring-primary shadow-xs"
          />
        </div>

        <div>
          <label className="block text-xs font-semibold uppercase tracking-wider text-muted-foreground mb-1">
            Ngày Hẹn Trả Sách (Mặc định 14 ngày)
          </label>
          <input
            type="date"
            value={dueDate}
            onChange={(e) => setDueDate(e.target.value)}
            className="w-full px-3.5 py-2 text-sm border border-border rounded-xl bg-background text-foreground focus:outline-none focus:ring-2 focus:ring-primary shadow-xs"
          />
        </div>
      </div>

      {/* BORROW SLIP SUMMARY PREVIEW */}
      {selectedReader && selectedBook && (
        <div className="p-4 rounded-2xl bg-primary/10 border border-primary/20 text-xs text-foreground space-y-2">
          <div className="font-bold text-sm text-primary flex items-center gap-1.5">
            <Check className="w-4 h-4" /> Xác Nhận Thông Tin Phiếu Mượn:
          </div>
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-2">
            <div>• Độc giả: <span className="font-bold">{selectedReader.name}</span> ({selectedReader.cardNumber})</div>
            <div>• Cuốn sách: <span className="font-bold">{selectedBook.title}</span></div>
            <div>• Hạn trả: <span className="font-bold text-emerald-600 dark:text-emerald-400">{dueDate}</span></div>
            <div>• Số lượng giảm kho: <span className="font-bold">1 cuốn</span></div>
          </div>
        </div>
      )}

      {/* FORM BUTTONS */}
      <div className="flex gap-3 pt-2 border-t border-border">
        <Button
          type="button"
          variant="outline"
          onClick={onCancel}
          className="flex-1 rounded-xl text-xs py-2.5"
        >
          Hủy
        </Button>
        <Button
          type="submit"
          disabled={!selectedReader || !selectedBook}
          className="flex-1 bg-primary hover:bg-primary/90 text-primary-foreground font-bold rounded-xl text-xs py-2.5 shadow-md cursor-pointer disabled:opacity-50"
        >
          Xác Nhận Tạo Phiếu Mượn
        </Button>
      </div>
    </form>
  );
}
