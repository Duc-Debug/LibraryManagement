'use client';

import { BookOpen, ArrowLeft, Star, AlertCircle } from 'lucide-react';
import { Button } from '@/components/ui/button';
import type { Book } from '@/features/books';

interface BookDetailsProps {
  book: Book;
  onBack: () => void;
  onBorrow: (book: Book) => void;
  onReadSample: (book: Book) => void;
}

export function BookDetails({
  book,
  onBack,
  onBorrow,
  onReadSample,
}: BookDetailsProps) {
  const description =
    book.description ||
    `"${book.title}" là một cuốn sách tuyệt vời của tác giả ${book.author}, xuất bản năm ${book.publishYear}. Cuốn sách này thuộc thể loại ${book.category} và là một tác phẩm đáng đọc. Hãy khám phá nó ngay hôm nay!`;

  return (
    <div className="w-full min-h-screen bg-background p-6">
      <div className="max-w-4xl mx-auto">
        {/* Back Button */}
        <button
          onClick={onBack}
          className="flex items-center gap-2 text-primary hover:text-primary/80 font-medium mb-6 transition-colors"
        >
          <ArrowLeft className="w-4 h-4" />
          Quay lại
        </button>

        {/* Book Details Container */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
          {/* Left: Book Cover */}
          <div className="md:col-span-1">
            <div className="sticky top-6 bg-gradient-to-br from-primary/20 to-secondary/20 rounded-lg border border-border h-96 flex items-center justify-center">
              <BookOpen className="w-24 h-24 text-primary/40" />
            </div>
          </div>

          {/* Right: Book Information */}
          <div className="md:col-span-2">
            {/* Title and Author */}
            <h1 className="text-4xl font-bold text-foreground mb-2 text-pretty">
              {book.title}
            </h1>
            <p className="text-xl text-foreground/60 mb-4">của {book.author}</p>

            {/* Metadata */}
            <div className="grid grid-cols-2 gap-4 mb-6 p-4 bg-muted rounded-lg">
              <div>
                <p className="text-xs text-foreground/60 uppercase font-semibold mb-1">
                  Thể loại
                </p>
                <p className="text-lg font-medium text-foreground">
                  {book.category}
                </p>
              </div>
              <div>
                <p className="text-xs text-foreground/60 uppercase font-semibold mb-1">
                  Năm xuất bản
                </p>
                <p className="text-lg font-medium text-foreground">
                  {book.publishYear}
                </p>
              </div>
              <div>
                <p className="text-xs text-foreground/60 uppercase font-semibold mb-1">
                  ISBN
                </p>
                <p className="text-lg font-medium text-foreground">
                  {book.isbn}
                </p>
              </div>
              <div>
                <p className="text-xs text-foreground/60 uppercase font-semibold mb-1">
                  Đánh giá
                </p>
                <div className="flex items-center gap-2">
                  <div className="flex gap-0.5">
                    {[...Array(5)].map((_, i) => (
                      <Star
                        key={i}
                        className={`w-4 h-4 ${
                          i < (book.rating || 4)
                            ? 'fill-secondary text-secondary'
                            : 'text-border'
                        }`}
                      />
                    ))}
                  </div>
                  <span className="text-sm text-foreground/60">
                    ({book.rating || 4}/5)
                  </span>
                </div>
              </div>
            </div>

            {/* Availability */}
            <div className="mb-6">
              {book.availableCopies > 0 ? (
                <div className="bg-accent/10 border border-accent/30 rounded-lg p-4 flex items-start gap-3">
                  <div className="w-2 h-2 rounded-full bg-accent mt-1.5 flex-shrink-0" />
                  <div>
                    <p className="font-semibold text-accent mb-1">
                      Còn sách để mượn
                    </p>
                    <p className="text-sm text-foreground/70">
                      Còn {book.availableCopies} bản trong{' '}
                      {book.totalCopies} bản có sẵn
                    </p>
                  </div>
                </div>
              ) : (
                <div className="bg-destructive/10 border border-destructive/30 rounded-lg p-4 flex items-start gap-3">
                  <AlertCircle className="w-5 h-5 text-destructive flex-shrink-0 mt-0.5" />
                  <div>
                    <p className="font-semibold text-destructive mb-1">
                      Hết sách
                    </p>
                    <p className="text-sm text-foreground/70">
                      Tất cả bản sao đều đang được mượn
                    </p>
                  </div>
                </div>
              )}
            </div>

            {/* Summary */}
            <div className="mb-8">
              <h2 className="text-xl font-bold text-foreground mb-3">Mô tả</h2>
              <p className="text-foreground/70 leading-relaxed mb-4">
                {description}
              </p>
            </div>

            {/* Borrowing Info */}
            <div className="bg-muted rounded-lg p-4 mb-8">
              <h3 className="font-semibold text-foreground mb-3">
                Thông tin mượn sách
              </h3>
              <ul className="space-y-2 text-sm text-foreground/70">
                <li className="flex justify-between">
                  <span>Thời hạn mượn:</span>
                  <span className="font-medium text-foreground">14 ngày</span>
                </li>
                <li className="flex justify-between">
                  <span>Gia hạn tối đa:</span>
                  <span className="font-medium text-foreground">1 lần</span>
                </li>
                <li className="flex justify-between">
                  <span>Phí quá hạn:</span>
                  <span className="font-medium text-foreground">
                    5.000 VND/ngày
                  </span>
                </li>
              </ul>
            </div>

            {/* Action Buttons */}
            <div className="flex gap-3">
              <Button
                className="flex-1"
                disabled={book.availableCopies === 0}
                onClick={() => onBorrow(book)}
              >
                Mượn sách ngay
              </Button>
              <Button
                variant="outline"
                className="flex-1"
                onClick={() => onReadSample(book)}
              >
                Xem mẫu
              </Button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
