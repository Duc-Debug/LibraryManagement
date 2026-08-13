'use client';

import { useState, useMemo, useRef, useEffect } from 'react';
import type { ReaderResponse } from '@/api/readerApi';
import type { BookResponseDto } from '@/api/bookApi';
import type { CategoryResponse } from '@/api/categoryApi';
import { Button } from '@/components/ui/button';
import { Search, BookOpen, CreditCard, Calendar, AlertTriangle, Check, X, Image as ImageIcon, Plus, Trash2, ShoppingBag, FileText } from 'lucide-react';

const MAX_BOOKS_PER_SLIP = 5;

interface BorrowingFormProps {
  readers: ReaderResponse[];
  books: BookResponseDto[];
  categories: CategoryResponse[];
  onSubmit: (record: any) => void;
  onCancel: () => void;
}

export function BorrowingForm({ readers, books, categories, onSubmit, onCancel }: BorrowingFormProps) {
  const [selectedReader, setSelectedReader] = useState<ReaderResponse | null>(null);
  const [selectedBooks, setSelectedBooks] = useState<BookResponseDto[]>([]);
  
  const [readerSearch, setReaderSearch] = useState('');
  const [bookSearch, setBookSearch] = useState('');
  const [note, setNote] = useState('');
  
  const [isReaderDropdownOpen, setIsReaderDropdownOpen] = useState(false);
  const [isBookDropdownOpen, setIsBookDropdownOpen] = useState(false);

  const [borrowDate, setBorrowDate] = useState(new Date().toISOString().split('T')[0]);
  const [dueDate, setDueDate] = useState(
    new Date(Date.now() + 14 * 24 * 60 * 60 * 1000).toISOString().split('T')[0]
  );
  const [formError, setFormError] = useState<string | null>(null);

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
    const selectedBookIds = new Set(selectedBooks.map((b) => b.bookId));

    return books.filter((b) => {
      if (!b.active) return false; // Hidden book
      if (b.availableQuantity <= 0) return false; // Out of stock
      if (b.categoryName && inactiveCatNames.has(b.categoryName)) return false; // Hidden category
      if (selectedBookIds.has(b.bookId)) return false; // Already selected in cart
      if (!term) return true;
      return (
        b.title.toLowerCase().includes(term) ||
        b.isbn.toLowerCase().includes(term) ||
        b.author.toLowerCase().includes(term) ||
        (b.categoryName && b.categoryName.toLowerCase().includes(term))
      );
    });
  }, [books, bookSearch, inactiveCatNames, selectedBooks]);

  const handleAddBookToSlip = (book: BookResponseDto) => {
    setFormError(null);
    if (selectedBooks.length >= MAX_BOOKS_PER_SLIP) {
      setFormError(`Mỗi phiếu mượn chỉ được chọn tối đa ${MAX_BOOKS_PER_SLIP} cuốn sách.`);
      return;
    }
    setSelectedBooks([...selectedBooks, book]);
    setBookSearch('');
    setIsBookDropdownOpen(false);
  };

  const handleRemoveBookFromSlip = (bookId: number) => {
    setSelectedBooks(selectedBooks.filter((b) => b.bookId !== bookId));
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    setFormError(null);

    if (!selectedReader) {
      setFormError('Vui lòng chọn Độc giả mượn sách.');
      return;
    }

    if (selectedBooks.length === 0) {
      setFormError('Vui lòng chọn ít nhất 1 cuốn sách để mượn.');
      return;
    }

    onSubmit({
      readerId: selectedReader.id,
      bookIds: selectedBooks.map((b) => b.bookId),
      note: note.trim() || undefined,
      borrowDays: 14,
      readerName: selectedReader.name,
      readerCardNumber: selectedReader.cardNumber,
      books: selectedBooks.map((b) => ({
        bookId: b.bookId,
        title: b.title,
        isbn: b.isbn,
      })),
      borrowDate,
      dueDate,
    });
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-6">
      <div className="flex items-center justify-between pb-3 border-b border-border/60">
        <div>
          <h3 className="text-lg font-bold text-foreground flex items-center gap-2">
            <ShoppingBag className="w-5 h-5 text-primary" />
            <span>Tạo Phiếu Mượn Nối Nhất (Cho Phép Mượn Nhiều Sách)</span>
          </h3>
          <p className="text-xs text-muted-foreground mt-0.5">
            Chọn Độc giả và thêm nhiều đầu sách vào Giỏ mượn (Tối đa {MAX_BOOKS_PER_SLIP} cuốn/phiếu).
          </p>
        </div>
      </div>

      {formError && (
        <div className="p-3.5 rounded-xl bg-destructive/10 text-destructive border border-destructive/20 text-xs flex items-center gap-2">
          <AlertTriangle className="w-4 h-4 shrink-0" />
          <span>{formError}</span>
        </div>
      )}

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
                className="h-8 w-8 p-0 rounded-lg text-muted-foreground hover:text-destructive hover:bg-destructive/10 cursor-pointer"
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

        {/* 2. SEARCHABLE BOOK MULTI-SELECT COMBOBOX */}
        <div className="space-y-2 relative" ref={bookRef}>
          <label className="block text-xs font-semibold uppercase tracking-wider text-muted-foreground">
            2. Thêm Sách Vào Phiếu (Gõ tên sách, ISBN...) <span className="text-destructive">*</span>
          </label>

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
              placeholder="Gõ tên sách để chọn thêm vào phiếu..."
              className="w-full pl-10 pr-4 py-2.5 text-sm border border-border rounded-xl bg-background text-foreground focus:outline-none focus:ring-2 focus:ring-primary shadow-xs"
            />
          </div>

          {isBookDropdownOpen && (
            <div className="absolute left-0 right-0 top-full mt-1 max-h-60 overflow-y-auto bg-card border border-border rounded-xl shadow-xl z-50 divide-y divide-border/60">
              {filteredBooks.length === 0 ? (
                <div className="p-4 text-center text-xs text-muted-foreground">
                  Không tìm thấy sách phù hợp hoặc sách đã được chọn hết vào giỏ.
                </div>
              ) : (
                filteredBooks.map((b) => (
                  <div
                    key={b.bookId}
                    onClick={() => handleAddBookToSlip(b)}
                    className="p-3 hover:bg-muted/40 transition cursor-pointer flex items-center justify-between group"
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
                        <div className="font-semibold text-xs text-foreground group-hover:text-primary transition-colors">
                          {b.title}
                        </div>
                        <div className="text-[11px] text-muted-foreground">
                          {b.author} • ISBN: {b.isbn}
                        </div>
                      </div>
                    </div>

                    <div className="flex items-center gap-3">
                      <div className="text-right whitespace-nowrap">
                        <div className="text-xs font-bold text-primary">
                          Còn {b.availableQuantity} cuốn
                        </div>
                        <div className="text-[10px] text-muted-foreground">
                          Kệ: {b.shelfLocation || 'N/A'}
                        </div>
                      </div>
                      <span className="p-1 rounded-lg bg-primary/10 text-primary group-hover:bg-primary group-hover:text-white transition-colors">
                        <Plus className="w-4 h-4" />
                      </span>
                    </div>
                  </div>
                ))
              )}
            </div>
          )}
        </div>
      </div>

      {/* SELECTED BOOKS CART LIST */}
      <div className="space-y-3 pt-2">
        <div className="flex items-center justify-between">
          <label className="text-xs font-bold uppercase tracking-wider text-foreground flex items-center gap-2">
            <ShoppingBag className="w-4 h-4 text-primary" />
            <span>Danh Sách Sách Trong Phiếu Mượn ({selectedBooks.length}/{MAX_BOOKS_PER_SLIP} cuốn)</span>
          </label>
        </div>

        {selectedBooks.length === 0 ? (
          <div className="p-6 rounded-2xl border border-dashed border-border/80 text-center text-xs text-muted-foreground bg-muted/20">
            Chưa có cuốn sách nào được thêm vào phiếu mượn. Hãy gõ tên sách ở ô tìm kiếm bên trên để thêm.
          </div>
        ) : (
          <div className="space-y-2 max-h-60 overflow-y-auto">
            {selectedBooks.map((b, index) => (
              <div
                key={b.bookId}
                className="p-3 rounded-xl bg-card border border-border flex items-center justify-between hover:border-primary/40 transition shadow-xs"
              >
                <div className="flex items-center gap-3">
                  <span className="w-6 h-6 rounded-full bg-primary/10 text-primary font-bold text-xs flex items-center justify-center">
                    {index + 1}
                  </span>
                  {b.coverImageUrl ? (
                    <img src={b.coverImageUrl} alt={b.title} className="w-8 h-11 object-cover rounded-md border border-border shrink-0" />
                  ) : (
                    <div className="w-8 h-11 bg-muted/40 rounded-md border border-border shrink-0 flex items-center justify-center">
                      <ImageIcon className="w-3.5 h-3.5 opacity-50" />
                    </div>
                  )}
                  <div>
                    <div className="font-bold text-xs text-foreground">{b.title}</div>
                    <div className="text-[11px] text-muted-foreground">
                      Tác giả: {b.author} • ISBN: {b.isbn} • Kệ: {b.shelfLocation || 'Chưa xếp'}
                    </div>
                  </div>
                </div>

                <Button
                  type="button"
                  variant="ghost"
                  size="sm"
                  onClick={() => handleRemoveBookFromSlip(b.bookId)}
                  className="h-8 w-8 p-0 rounded-lg text-muted-foreground hover:text-destructive hover:bg-destructive/10 cursor-pointer"
                  title="Xóa cuốn sách này khỏi phiếu mượn"
                >
                  <Trash2 className="w-4 h-4" />
                </Button>
              </div>
            ))}
          </div>
        )}
      </div>

      {/* DATES SELECTION & NOTE */}
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

      {/* NOTE INPUT */}
      <div>
        <label className="block text-xs font-semibold uppercase tracking-wider text-muted-foreground mb-1 flex items-center gap-1.5">
          <FileText className="w-3.5 h-3.5 text-primary" />
          <span>Ghi Chú Phiếu Mượn (Không bắt buộc)</span>
        </label>
        <input
          type="text"
          value={note}
          onChange={(e) => setNote(e.target.value)}
          placeholder="Nhập ghi chú thêm nếu có (Ví dụ: Độc giả mượn làm đồ án, xin hẹn trả sớm...)"
          className="w-full px-3.5 py-2 text-sm border border-border rounded-xl bg-background text-foreground focus:outline-none focus:ring-2 focus:ring-primary shadow-xs"
        />
      </div>

      {/* BORROW SLIP SUMMARY PREVIEW */}
      {selectedReader && selectedBooks.length > 0 && (
        <div className="p-4 rounded-2xl bg-primary/10 border border-primary/20 text-xs text-foreground space-y-2">
          <div className="font-bold text-sm text-primary flex items-center gap-1.5">
            <Check className="w-4 h-4" /> Xác Nhận Thông Tin Phiếu Mượn ({selectedBooks.length} cuốn):
          </div>
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-2">
            <div>• Độc giả: <span className="font-bold">{selectedReader.name}</span> ({selectedReader.cardNumber})</div>
            <div>• Tổng số cuốn mượn: <span className="font-bold text-primary">{selectedBooks.length} cuốn</span></div>
            <div>• Hạn hẹn trả: <span className="font-bold text-emerald-600 dark:text-emerald-400">{dueDate}</span></div>
            <div>• Các cuốn sách: <span className="font-semibold">{selectedBooks.map(b => b.title).join(', ')}</span></div>
            {note.trim() && <div>• Ghi chú: <span className="font-semibold italic text-foreground">&quot;{note.trim()}&quot;</span></div>}
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
          disabled={!selectedReader || selectedBooks.length === 0}
          className="flex-1 bg-primary hover:bg-primary/90 text-primary-foreground font-bold rounded-xl text-xs py-2.5 shadow-md cursor-pointer disabled:opacity-50"
        >
          Xác Nhận Tạo Phiếu Mượn ({selectedBooks.length} Cuốn)
        </Button>
      </div>
    </form>
  );
}
