'use client';
import { useState, useEffect, useCallback, useMemo } from 'react';
import { fetchAllReaders, ReaderResponse } from '@/api/readerApi';
import { fetchBooksApi, BookResponseDto } from '@/api/bookApi';
import { fetchCategoriesApi, CategoryResponse } from '@/api/categoryApi';
import {
    Plus,
    RefreshCw,
    BookOpen,
    Clock,
    AlertTriangle,
    CheckCircle2,
    X,
    Search,
    Check,
    Receipt,
} from 'lucide-react';
import { Button } from '@/components/ui/button';
import { BorrowingForm } from './BorrowingForm';

const FINE_PER_DAY = 5000; // 5.000 VNĐ / ngày trả muộn

type SlipStatus = 'BORROWING' | 'OVERDUE' | 'RETURNED';

interface BorrowSlip {
    id: string;
    readerName: string;
    readerCardNumber: string;
    books: { bookId: number; title: string; isbn: string }[];
    borrowDate: string;
    dueDate: string;
    returnDate?: string;
    status: SlipStatus;
}

type TabKey = 'created' | 'pending' | 'history';

export function BorrowingReturnsPage() {
    const [showForm, setShowForm] = useState(false);
    const [slips, setSlips] = useState<BorrowSlip[]>([]);
    const [activeTab, setActiveTab] = useState<TabKey>('created');
    const [searchTerm, setSearchTerm] = useState('');

    // Real DB States (dùng cho form tạo phiếu mượn)
    const [readers, setReaders] = useState<ReaderResponse[]>([]);
    const [books, setBooks] = useState<BookResponseDto[]>([]);
    const [categories, setCategories] = useState<CategoryResponse[]>([]);

    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const [successMessage, setSuccessMessage] = useState<string | null>(null);

    const [returnConfirmModal, setReturnConfirmModal] = useState<{
        slip: BorrowSlip;
        overdueDays: number;
        fineAmount: number;
    } | null>(null);

    const loadData = useCallback(async () => {
        setLoading(true);
        setError(null);
        try {
            const [readersRes, booksRes, catsRes] = await Promise.allSettled([
                fetchAllReaders(),
                fetchBooksApi(0, 100),
                fetchCategoriesApi(),
            ]);

            if (readersRes.status === 'fulfilled') {
                setReaders(readersRes.value);
            }
            if (booksRes.status === 'fulfilled' && booksRes.value) {
                setBooks(booksRes.value.content || booksRes.value.items || []);
            }
            if (catsRes.status === 'fulfilled') {
                setCategories(catsRes.value);
            }
        } catch (err: any) {
            setError(err?.message || 'Không thể nạp dữ liệu từ máy chủ.');
        } finally {
            setLoading(false);
        }
    }, []);

    useEffect(() => {
        loadData();
    }, [loadData]);

    const calculateOverdueDays = (dueDateStr: string): number => {
        const due = new Date(dueDateStr);
        const now = new Date();
        if (now <= due) return 0;
        const diffTime = Math.abs(now.getTime() - due.getTime());
        return Math.ceil(diffTime / (1000 * 60 * 60 * 24));
    };

    // Auto-derive OVERDUE status for slips still borrowing but past due date
    const derivedSlips = useMemo(() => {
        return slips.map((s) => {
            if (s.status === 'BORROWING' && calculateOverdueDays(s.dueDate) > 0) {
                return { ...s, status: 'OVERDUE' as SlipStatus };
            }
            return s;
        });
    }, [slips]);

    const handleCreateBorrow = (record: any) => {
        const newSlip: BorrowSlip = {
            id: `BRW-${Date.now().toString().slice(-6)}`,
            readerName: record.readerName,
            readerCardNumber: record.readerCardNumber,
            books: record.books,
            borrowDate: record.borrowDate,
            dueDate: record.dueDate,
            status: 'BORROWING',
        };

        setSlips((prev) => [newSlip, ...prev]);

        // Cập nhật tồn kho tạm thời cho các sách vừa mượn
        const borrowedBookIds = new Set((record.books || []).map((b: any) => b.bookId));
        setBooks((prev) =>
            prev.map((b) =>
                borrowedBookIds.has(b.bookId)
                    ? { ...b, availableQuantity: Math.max(0, b.availableQuantity - 1) }
                    : b
            )
        );

        setSuccessMessage(
            `Tạo thành công phiếu mượn mã #${newSlip.id} gồm ${record.books.length} cuốn sách cho độc giả "${record.readerName}"!`
        );
        setShowForm(false);
        setActiveTab('created');
    };

    const handleInitiateReturn = (slip: BorrowSlip) => {
        const overdueDays = calculateOverdueDays(slip.dueDate);
        const fineAmount = overdueDays * FINE_PER_DAY;

        if (overdueDays > 0) {
            setReturnConfirmModal({ slip, overdueDays, fineAmount });
        } else {
            executeReturn(slip.id);
        }
    };

    const executeReturn = (slipId: string) => {
        setSlips((prev) =>
            prev.map((s) =>
                s.id === slipId
                    ? { ...s, status: 'RETURNED', returnDate: new Date().toISOString().split('T')[0] }
                    : s
            )
        );
        setReturnConfirmModal(null);
    };

    // Stats
    const countBorrowing = derivedSlips.filter((s) => s.status === 'BORROWING').length;
    const countOverdue = derivedSlips.filter((s) => s.status === 'OVERDUE').length;
    const countReturned = derivedSlips.filter((s) => s.status === 'RETURNED').length;

    // Filtered lists per tab
    const matchesSearch = (slip: BorrowSlip) => {
        const term = searchTerm.toLowerCase().trim();
        if (!term) return true;
        return (
            slip.id.toLowerCase().includes(term) ||
            slip.readerName.toLowerCase().includes(term) ||
            slip.books.some((b) => b.title.toLowerCase().includes(term))
        );
    };

    const createdSlips = derivedSlips.filter(matchesSearch);
    const pendingSlips = derivedSlips
        .filter((s) => s.status === 'BORROWING' || s.status === 'OVERDUE')
        .filter(matchesSearch);
    const historySlips = derivedSlips.filter((s) => s.status === 'RETURNED').filter(matchesSearch);

    const tabs: { key: TabKey; label: string; count: number }[] = [
        { key: 'created', label: 'Phiếu Mượn Vừa Tạo', count: createdSlips.length },
        { key: 'pending', label: 'Danh Sách Chờ Trả', count: pendingSlips.length },
        { key: 'history', label: 'Lịch Sử', count: historySlips.length },
    ];

    const renderStatusBadge = (status: SlipStatus) => {
        if (status === 'OVERDUE') {
            return (
                <span className="inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full text-xs font-semibold bg-rose-500/10 text-rose-600 dark:text-rose-400 border border-rose-500/20 whitespace-nowrap">
          <AlertTriangle className="w-3 h-3" /> Quá hạn
        </span>
            );
        }
        if (status === 'RETURNED') {
            return (
                <span className="inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full text-xs font-semibold bg-emerald-500/10 text-emerald-600 dark:text-emerald-400 border border-emerald-500/20 whitespace-nowrap">
          <CheckCircle2 className="w-3 h-3" /> Đã trả
        </span>
            );
        }
        return (
            <span className="inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full text-xs font-semibold bg-primary/10 text-primary border border-primary/20 whitespace-nowrap">
        <Clock className="w-3 h-3" /> Đang mượn
      </span>
        );
    };

    const renderTable = (list: BorrowSlip[], showReturnAction: boolean, showReturnDate: boolean) => {
        if (list.length === 0) {
            return (
                <div className="p-12 text-center text-muted-foreground text-sm space-y-2">
                    <p>
                        {searchTerm
                            ? 'Không tìm thấy phiếu phù hợp.'
                            : 'Chưa có phiếu nào trong danh sách này.'}
                    </p>
                    {!searchTerm && activeTab === 'created' && (
                        <p className="text-xs text-muted-foreground">
                            Nhấn nút <strong className="text-primary font-bold">&quot;Tạo Phiếu Mượn&quot;</strong> để chọn Độc giả và Thêm sách vào phiếu.
                        </p>
                    )}
                </div>
            );
        }

        return (
            <div className="overflow-x-auto">
                <table className="w-full text-left text-sm">
                    <thead className="bg-muted/50 border-b border-border text-xs uppercase text-muted-foreground font-semibold whitespace-nowrap">
                    <tr>
                        <th className="px-6 py-4 whitespace-nowrap">Mã Phiếu</th>
                        <th className="px-6 py-4 whitespace-nowrap">Độc Giả</th>
                        <th className="px-6 py-4 whitespace-nowrap">Sách Mượn</th>
                        <th className="px-6 py-4 whitespace-nowrap">Ngày Mượn</th>
                        <th className="px-6 py-4 whitespace-nowrap">
                            {showReturnDate ? 'Ngày Trả' : 'Ngày Hẹn Trả'}
                        </th>
                        <th className="px-6 py-4 text-center whitespace-nowrap">Trạng Thái</th>
                        {showReturnAction && <th className="px-6 py-4 text-right whitespace-nowrap">Thao Tác</th>}
                    </tr>
                    </thead>
                    <tbody className="divide-y divide-border">
                    {list.map((slip) => (
                        <tr
                            key={slip.id}
                            className={`hover:bg-muted/20 transition-colors ${
                                slip.status === 'OVERDUE' ? 'bg-rose-500/5' : ''
                            }`}
                        >
                            <td className="px-6 py-4 font-mono text-xs font-bold text-primary whitespace-nowrap">
                                #{slip.id}
                            </td>
                            <td className="px-6 py-4 whitespace-nowrap">
                                <div className="font-semibold text-foreground">{slip.readerName}</div>
                                <div className="font-mono text-xs text-muted-foreground">{slip.readerCardNumber}</div>
                            </td>
                            <td className="px-6 py-4 whitespace-nowrap">
                                <div className="font-bold text-foreground flex items-center gap-1.5">
                    <span className="px-2 py-0.5 rounded-md bg-primary/10 text-primary text-xs font-mono">
                      {slip.books.length} cuốn
                    </span>
                                    <span className="text-xs text-muted-foreground line-clamp-1 max-w-xs">
                      {slip.books.map((b) => b.title).join(', ')}
                    </span>
                                </div>
                            </td>
                            <td className="px-6 py-4 text-xs text-muted-foreground whitespace-nowrap">
                                {slip.borrowDate}
                            </td>
                            <td className="px-6 py-4 text-xs font-semibold whitespace-nowrap">
                                {showReturnDate ? (
                                    <span className="text-emerald-600 dark:text-emerald-400">{slip.returnDate}</span>
                                ) : (
                                    <span className={slip.status === 'OVERDUE' ? 'text-rose-600 dark:text-rose-400 font-bold' : 'text-muted-foreground'}>
                      {slip.dueDate}
                    </span>
                                )}
                            </td>
                            <td className="px-6 py-4 text-center whitespace-nowrap">{renderStatusBadge(slip.status)}</td>
                            {showReturnAction && (
                                <td className="px-6 py-4 text-right whitespace-nowrap">
                                    <Button
                                        onClick={() => handleInitiateReturn(slip)}
                                        className="bg-emerald-600 hover:bg-emerald-700 text-white h-8 px-3 text-xs font-semibold rounded-lg shadow-sm cursor-pointer"
                                    >
                                        <Check className="w-3.5 h-3.5 mr-1" />
                                        Xác Nhận Trả
                                    </Button>
                                </td>
                            )}
                        </tr>
                    ))}
                    </tbody>
                </table>
            </div>
        );
    };

    return (
        <div className="p-6 md:p-8 max-w-7xl mx-auto space-y-6 text-foreground animate-in fade-in duration-200">
            {/* Header */}
            <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-2 border-b border-border/50">
                <div>
                    <h1 className="text-2xl font-bold tracking-tight flex items-center gap-2">
                        <BookOpen className="w-6 h-6 text-primary" />
                        <span>Quản Lý Mượn - Trả Sách</span>
                    </h1>
                    <p className="text-sm text-muted-foreground mt-1">
                        Quản lý lập phiếu mượn, tiếp nhận trả sách và theo dõi trạng thái.
                    </p>
                </div>
                <div className="flex items-center gap-3 self-start sm:self-auto">
                    <Button onClick={loadData} variant="outline" disabled={loading} className="rounded-xl flex items-center gap-2 text-xs">
                        <RefreshCw className={`w-3.5 h-3.5 ${loading ? 'animate-spin' : ''}`} />
                        Tải lại
                    </Button>
                    <Button
                        onClick={() => {
                            setShowForm(!showForm);
                            setSuccessMessage(null);
                        }}
                        className="bg-primary hover:bg-primary/90 text-primary-foreground rounded-xl flex items-center gap-2 text-xs font-semibold shadow-md cursor-pointer"
                    >
                        <Plus className="w-4 h-4" />
                        {showForm ? 'Đóng Form' : 'Tạo Phiếu Mượn'}
                    </Button>
                </div>
            </div>

            {/* Notifications */}
            {error && (
                <div className="p-4 bg-destructive/10 text-destructive rounded-xl border border-destructive/20 text-sm flex items-center justify-between animate-in fade-in">
          <span className="flex items-center gap-2">
            <AlertTriangle className="w-4 h-4 text-destructive" />
              {error}
          </span>
                    <Button onClick={() => setError(null)} size="sm" variant="ghost" className="h-7 w-7 p-0 rounded-lg">
                        <X className="w-4 h-4" />
                    </Button>
                </div>
            )}

            {successMessage && (
                <div className="p-4 bg-emerald-500/10 text-emerald-600 dark:text-emerald-400 rounded-xl border border-emerald-500/20 text-sm flex items-center justify-between animate-in fade-in">
          <span className="flex items-center gap-2">
            <CheckCircle2 className="w-4 h-4 text-emerald-500" />
              {successMessage}
          </span>
                    <Button onClick={() => setSuccessMessage(null)} size="sm" variant="ghost" className="h-7 w-7 p-0 rounded-lg hover:bg-emerald-500/10">
                        <X className="w-4 h-4" />
                    </Button>
                </div>
            )}

            {/* Form Container */}
            {showForm && (
                <div className="bg-card rounded-2xl border border-border p-6 shadow-xl animate-in zoom-in-95 duration-150">
                    <BorrowingForm
                        readers={readers}
                        books={books}
                        categories={categories}
                        onSubmit={handleCreateBorrow}
                        onCancel={() => setShowForm(false)}
                    />
                </div>
            )}

            {/* Stats Cards */}
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                <div className="bg-card/80 backdrop-blur-md rounded-2xl border border-primary/20 p-5 shadow-sm flex items-center justify-between">
                    <div>
                        <p className="text-xs font-semibold uppercase tracking-wider text-muted-foreground">Đang Mượn</p>
                        <p className="text-2xl font-extrabold text-foreground mt-1">{countBorrowing}</p>
                    </div>
                    <div className="p-3 rounded-xl bg-primary/10 text-primary">
                        <Clock className="w-6 h-6" />
                    </div>
                </div>

                <div className="bg-card/80 backdrop-blur-md rounded-2xl border border-rose-500/30 p-5 shadow-sm flex items-center justify-between">
                    <div>
                        <p className="text-xs font-semibold uppercase tracking-wider text-rose-600 dark:text-rose-400">Quá Hạn</p>
                        <p className="text-2xl font-extrabold text-rose-600 dark:text-rose-400 mt-1">{countOverdue}</p>
                    </div>
                    <div className="p-3 rounded-xl bg-rose-500/15 text-rose-600 dark:text-rose-400">
                        <AlertTriangle className="w-6 h-6" />
                    </div>
                </div>

                <div className="bg-card/80 backdrop-blur-md rounded-2xl border border-emerald-500/20 p-5 shadow-sm flex items-center justify-between">
                    <div>
                        <p className="text-xs font-semibold uppercase tracking-wider text-emerald-600 dark:text-emerald-400">Đã Trả</p>
                        <p className="text-2xl font-extrabold text-emerald-600 dark:text-emerald-400 mt-1">{countReturned}</p>
                    </div>
                    <div className="p-3 rounded-xl bg-emerald-500/10 text-emerald-600 dark:text-emerald-400">
                        <CheckCircle2 className="w-6 h-6" />
                    </div>
                </div>
            </div>

            {/* Search Input */}
            <div className="relative">
                <Search className="absolute left-3.5 top-1/2 -translate-y-1/2 w-4 h-4 text-muted-foreground" />
                <input
                    type="text"
                    placeholder="Tìm kiếm theo mã phiếu, tên độc giả hoặc tên sách..."
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                    className="w-full pl-10 pr-4 py-2.5 border border-border rounded-xl bg-card text-foreground placeholder-muted-foreground focus:outline-none focus:ring-2 focus:ring-primary transition text-sm shadow-xs"
                />
            </div>

            {/* Tabs + Table */}
            <div className="bg-card rounded-2xl border border-border shadow-sm overflow-hidden">
                <div className="flex items-center gap-1 p-2 border-b border-border/60 overflow-x-auto">
                    {tabs.map((tab) => (
                        <button
                            key={tab.key}
                            type="button"
                            onClick={() => setActiveTab(tab.key)}
                            className={`px-4 py-2 rounded-xl text-xs font-semibold whitespace-nowrap transition-colors cursor-pointer flex items-center gap-2 ${
                                activeTab === tab.key
                                    ? 'bg-primary text-primary-foreground shadow-sm'
                                    : 'text-muted-foreground hover:bg-muted/60 hover:text-foreground'
                            }`}
                        >
                            {tab.label}
                            <span
                                className={`px-1.5 py-0.5 rounded-full text-[10px] font-bold ${
                                    activeTab === tab.key ? 'bg-white/20' : 'bg-muted text-muted-foreground'
                                }`}
                            >
                {tab.count}
              </span>
                        </button>
                    ))}
                </div>

                {activeTab === 'created' && renderTable(createdSlips, false, false)}
                {activeTab === 'pending' && renderTable(pendingSlips, true, false)}
                {activeTab === 'history' && renderTable(historySlips, false, true)}
            </div>

            {/* Overdue Fine Confirmation Modal */}
            {returnConfirmModal && (
                <div className="fixed inset-0 bg-black/60 backdrop-blur-xs flex items-center justify-center z-50 p-4 animate-in fade-in duration-150">
                    <div className="bg-card rounded-2xl border border-border w-full max-w-md shadow-2xl p-6 space-y-4 animate-in zoom-in-95 duration-150">
                        <div className="flex items-center gap-2 text-rose-600 dark:text-rose-400 pb-2 border-b border-border">
                            <Receipt className="w-5 h-5" />
                            <h2 className="text-lg font-bold">Xác Nhận Thu Tiền Phạt Trả Muộn</h2>
                        </div>

                        <div className="p-4 rounded-xl bg-rose-500/10 border border-rose-500/20 text-xs space-y-2">
                            <div className="flex justify-between">
                                <span className="text-muted-foreground">Tên độc giả:</span>
                                <span className="font-bold text-foreground">{returnConfirmModal.slip.readerName}</span>
                            </div>
                            <div className="flex justify-between">
                                <span className="text-muted-foreground">Sách mượn:</span>
                                <span className="font-bold text-foreground text-right">
                  {returnConfirmModal.slip.books.map((b) => b.title).join(', ')}
                </span>
                            </div>
                            <div className="flex justify-between">
                                <span className="text-muted-foreground">Số ngày quá hạn:</span>
                                <span className="font-bold text-rose-600 dark:text-rose-400">{returnConfirmModal.overdueDays} ngày</span>
                            </div>
                            <div className="flex justify-between pt-2 border-t border-rose-500/20 text-sm">
                                <span className="font-bold text-foreground">Tổng tiền phạt quá hạn:</span>
                                <span className="font-extrabold text-rose-600 dark:text-rose-400">
                  {returnConfirmModal.fineAmount.toLocaleString('vi-VN')} VNĐ
                </span>
                            </div>
                        </div>

                        <div className="flex gap-3 pt-2">
                            <Button
                                variant="outline"
                                onClick={() => setReturnConfirmModal(null)}
                                className="flex-1 rounded-xl text-xs"
                            >
                                Hủy
                            </Button>
                            <Button
                                onClick={() => executeReturn(returnConfirmModal.slip.id)}
                                className="flex-1 bg-emerald-600 hover:bg-emerald-700 text-white rounded-xl text-xs font-bold cursor-pointer"
                            >
                                Đã Thu Tiền & Trả Sách
                            </Button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}