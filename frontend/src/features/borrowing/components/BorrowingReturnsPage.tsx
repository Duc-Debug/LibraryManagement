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
    Eye,
} from 'lucide-react';
import { Button } from '@/components/ui/button';
import { BorrowingForm } from './BorrowingForm';
import { BorrowSlipDetailModal } from './BorrowSlipDetailModal';

import { parseErrorMessage } from '@/lib/errorDictionary';
import {
    fetchBorrowSlipsApi,
    createBorrowSlipApi,
    previewBorrowSlipFineApi,
    returnBorrowSlipApi,
    BorrowSlipResponseDto
} from '@/api/borrowSlipApi';

const FINE_PER_DAY = 5000; // 5.000 VNĐ / ngày trả muộn

type TabKey = 'created' | 'pending' | 'history';

export type SortOption =
    | 'borrowDate-desc'
    | 'borrowDate-asc'
    | 'dueDate-asc'
    | 'dueDate-desc'
    | 'totalBooks-desc'
    | 'totalBooks-asc'
    | 'readerName-asc'
    | 'readerName-desc';

export function BorrowingReturnsPage() {
    const [showForm, setShowForm] = useState(false);
    const [slips, setSlips] = useState<BorrowSlipResponseDto[]>([]);
    const [activeTab, setActiveTab] = useState<TabKey>('created');
    const [searchTerm, setSearchTerm] = useState('');
    const [sortBy, setSortBy] = useState<SortOption>('borrowDate-desc');
    const [selectedSlipId, setSelectedSlipId] = useState<number | null>(null);

    // Real DB States (dùng cho form tạo phiếu mượn)
    const [readers, setReaders] = useState<ReaderResponse[]>([]);
    const [books, setBooks] = useState<BookResponseDto[]>([]);
    const [categories, setCategories] = useState<CategoryResponse[]>([]);

    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const [successMessage, setSuccessMessage] = useState<string | null>(null);

    const [returnConfirmModal, setReturnConfirmModal] = useState<{
        slip: BorrowSlipResponseDto;
        overdueDays: number;
        fineAmount: number;
    } | null>(null);

    const loadData = useCallback(async () => {
        setLoading(true);
        setError(null);
        try {
            const [readersRes, booksRes, catsRes, slipsRes] = await Promise.allSettled([
                fetchAllReaders(),
                fetchBooksApi(0, 100),
                fetchCategoriesApi(),
                fetchBorrowSlipsApi({ page: 0, size: 100 }),
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
            if (slipsRes.status === 'fulfilled' && slipsRes.value) {
                setSlips(slipsRes.value.content || []);
            }
        } catch (err: any) {
            setError(parseErrorMessage(err, 'Không thể nạp dữ liệu từ máy chủ.'));
        } finally {
            setLoading(false);
        }
    }, []);

    useEffect(() => {
        loadData();
    }, [loadData]);

    const handleCreateBorrow = async (record: any) => {
        setLoading(true);
        setError(null);
        setSuccessMessage(null);
        try {
            const bookIds = record.bookIds || (record.books || []).map((b: any) => b.bookId);
            const createdSlip = await createBorrowSlipApi({
                readerId: record.readerId,
                bookIds: bookIds,
                borrowDays: record.borrowDays || 14,
                note: record.note,
            });

            setSuccessMessage(
                `Tạo thành công phiếu mượn mã "${createdSlip.borrowCode}" gồm ${createdSlip.totalBooks} cuốn sách cho độc giả "${createdSlip.readerName}"!`
            );
            setShowForm(false);
            setActiveTab('created');
            await loadData();
        } catch (err: any) {
            setError(parseErrorMessage(err, 'Không thể tạo phiếu mượn. Vui lòng thử lại.'));
        } finally {
            setLoading(false);
        }
    };

    const handleInitiateReturn = async (slip: BorrowSlipResponseDto) => {
        try {
            setLoading(true);
            setError(null);
            const finePreview = await previewBorrowSlipFineApi(slip.id);
            if (finePreview.isOverdue) {
                setReturnConfirmModal({
                    slip,
                    overdueDays: finePreview.overdueDays,
                    fineAmount: Number(finePreview.totalFineAmount) || 0,
                });
            } else {
                await executeReturn(slip.id);
            }
        } catch (err: any) {
            setError(parseErrorMessage(err, 'Không thể kiểm tra thông tin trả phạt.'));
        } finally {
            setLoading(false);
        }
    };

    const executeReturn = async (slipId: number) => {
        try {
            setLoading(true);
            setError(null);
            await returnBorrowSlipApi(slipId);
            setSuccessMessage(`Đã xác nhận trả sách thành công cho phiếu mượn #${slipId}!`);
            setReturnConfirmModal(null);
            await loadData();
        } catch (err: any) {
            setError(parseErrorMessage(err, 'Không thể thực hiện trả sách. Vui lòng thử lại.'));
        } finally {
            setLoading(false);
        }
    };

    // Stats
    const countBorrowing = slips.filter((s) => s.status === 'BORROWING').length;
    const countOverdue = slips.filter((s) => s.status === 'OVERDUE').length;
    const countReturned = slips.filter((s) => s.status === 'RETURNED').length;

    // Filtered & Sorted lists per tab
    const matchesSearch = useCallback(
        (slip: BorrowSlipResponseDto) => {
            const term = searchTerm.toLowerCase().trim();
            if (!term) return true;
            return (
                String(slip.id).toLowerCase().includes(term) ||
                (slip.borrowCode && slip.borrowCode.toLowerCase().includes(term)) ||
                (slip.readerName && slip.readerName.toLowerCase().includes(term)) ||
                (slip.readerCardNumber && slip.readerCardNumber.toLowerCase().includes(term))
            );
        },
        [searchTerm]
    );

    const sortSlips = useCallback(
        (list: BorrowSlipResponseDto[]) => {
            return [...list].sort((a, b) => {
                switch (sortBy) {
                    case 'borrowDate-desc':
                        return new Date(b.borrowedAt || 0).getTime() - new Date(a.borrowedAt || 0).getTime();
                    case 'borrowDate-asc':
                        return new Date(a.borrowedAt || 0).getTime() - new Date(b.borrowedAt || 0).getTime();
                    case 'dueDate-asc':
                        return new Date(a.dueAt || 0).getTime() - new Date(b.dueAt || 0).getTime();
                    case 'dueDate-desc':
                        return new Date(b.dueAt || 0).getTime() - new Date(a.dueAt || 0).getTime();
                    case 'totalBooks-desc':
                        return b.totalBooks - a.totalBooks;
                    case 'totalBooks-asc':
                        return a.totalBooks - b.totalBooks;
                    case 'readerName-asc':
                        return (a.readerName || '').localeCompare(b.readerName || '', 'vi');
                    case 'readerName-desc':
                        return (b.readerName || '').localeCompare(a.readerName || '', 'vi');
                    default:
                        return 0;
                }
            });
        },
        [sortBy]
    );

    const createdSlips = useMemo(
        () => sortSlips(slips.filter(matchesSearch)),
        [slips, matchesSearch, sortSlips]
    );
    const pendingSlips = useMemo(
        () => sortSlips(slips.filter((s) => s.status === 'BORROWING' || s.status === 'OVERDUE').filter(matchesSearch)),
        [slips, matchesSearch, sortSlips]
    );
    const historySlips = useMemo(
        () => sortSlips(slips.filter((s) => s.status === 'RETURNED').filter(matchesSearch)),
        [slips, matchesSearch, sortSlips]
    );

    const tabs: { key: TabKey; label: string; count: number }[] = [
        { key: 'created', label: 'Tất Cả Phiếu Mượn', count: createdSlips.length },
        { key: 'pending', label: 'Danh Sách Chờ Trả', count: pendingSlips.length },
        { key: 'history', label: 'Lịch Sử Trả Sách', count: historySlips.length },
    ];

    const renderStatusBadge = (status: string) => {
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

    const renderTable = (list: BorrowSlipResponseDto[], showReturnAction: boolean, showReturnDate: boolean) => {
        if (list.length === 0) {
            return (
                <div className="p-12 text-center text-muted-foreground text-sm space-y-2">
                    <p>
                        {searchTerm
                            ? 'Không tìm thấy phiếu mượn nào phù hợp.'
                            : 'Chưa có phiếu mượn nào trong danh sách này.'}
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
                        <th className="px-6 py-4 whitespace-nowrap cursor-pointer select-none hover:text-foreground" onClick={() => setSortBy(sortBy === 'readerName-asc' ? 'readerName-desc' : 'readerName-asc')}>
                            Độc Giả
                        </th>
                        <th className="px-6 py-4 whitespace-nowrap cursor-pointer select-none hover:text-foreground" onClick={() => setSortBy(sortBy === 'totalBooks-desc' ? 'totalBooks-asc' : 'totalBooks-desc')}>
                            Số Sách Mượn
                        </th>
                        <th className="px-6 py-4 whitespace-nowrap cursor-pointer select-none hover:text-foreground" onClick={() => setSortBy(sortBy === 'borrowDate-desc' ? 'borrowDate-asc' : 'borrowDate-desc')}>
                            Ngày Mượn
                        </th>
                        <th className="px-6 py-4 whitespace-nowrap cursor-pointer select-none hover:text-foreground" onClick={() => setSortBy(sortBy === 'dueDate-asc' ? 'dueDate-desc' : 'dueDate-asc')}>
                            {showReturnDate ? 'Ngày Trả' : 'Ngày Hẹn Trả'}
                        </th>
                        <th className="px-6 py-4 text-center whitespace-nowrap">Trạng Thái</th>
                        <th className="px-6 py-4 text-right whitespace-nowrap">Thao Tác</th>
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
                                <button
                                    type="button"
                                    onClick={() => setSelectedSlipId(slip.id)}
                                    className="hover:underline text-primary flex items-center gap-1 cursor-pointer font-bold"
                                >
                                    {slip.borrowCode || `#${slip.id}`}
                                </button>
                            </td>
                            <td className="px-6 py-4 whitespace-nowrap">
                                <div className="font-semibold text-foreground">{slip.readerName}</div>
                                <div className="font-mono text-xs text-muted-foreground">{slip.readerCardNumber}</div>
                            </td>
                            <td className="px-6 py-4 whitespace-nowrap">
                                <div className="font-bold text-foreground flex items-center gap-1.5">
                                    <button
                                        type="button"
                                        onClick={() => setSelectedSlipId(slip.id)}
                                        className="px-2.5 py-1 rounded-md bg-primary/10 hover:bg-primary/20 text-primary text-xs font-mono font-bold transition-colors cursor-pointer"
                                        title="Click để xem chi tiết sách mượn"
                                    >
                                        {slip.totalBooks} cuốn
                                    </button>
                                    {slip.note && (
                                        <span className="text-xs text-muted-foreground italic line-clamp-1 max-w-xs">
                                          ({slip.note})
                                        </span>
                                    )}
                                </div>
                            </td>
                            <td className="px-6 py-4 text-xs text-muted-foreground whitespace-nowrap">
                                {slip.borrowedAt ? new Date(slip.borrowedAt).toLocaleDateString('vi-VN') : 'N/A'}
                            </td>
                            <td className="px-6 py-4 text-xs font-semibold whitespace-nowrap">
                                <span className={slip.status === 'OVERDUE' ? 'text-rose-600 dark:text-rose-400 font-bold' : 'text-emerald-600 dark:text-emerald-400'}>
                                    {slip.dueAt ? new Date(slip.dueAt).toLocaleDateString('vi-VN') : 'N/A'}
                                </span>
                            </td>
                            <td className="px-6 py-4 text-center whitespace-nowrap">{renderStatusBadge(slip.status)}</td>
                            <td className="px-6 py-4 text-right whitespace-nowrap">
                                <div className="flex items-center justify-end gap-2">
                                    <Button
                                        variant="outline"
                                        size="sm"
                                        onClick={() => setSelectedSlipId(slip.id)}
                                        className="h-8 px-2.5 text-xs font-medium rounded-lg flex items-center gap-1 cursor-pointer hover:bg-primary/10 hover:text-primary"
                                    >
                                        <Eye className="w-3.5 h-3.5" />
                                        <span>Chi Tiết</span>
                                    </Button>

                                    {showReturnAction && slip.status !== 'RETURNED' && (
                                        <Button
                                            onClick={() => handleInitiateReturn(slip)}
                                            className="bg-emerald-600 hover:bg-emerald-700 text-white h-8 px-3 text-xs font-semibold rounded-lg shadow-sm cursor-pointer"
                                        >
                                            <Check className="w-3.5 h-3.5 mr-1" />
                                            Xác Nhận Trả
                                        </Button>
                                    )}
                                </div>
                            </td>
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

            {/* Search Input & Sort Selector */}
            <div className="flex flex-col sm:flex-row items-stretch sm:items-center gap-3">
                <div className="relative flex-1">
                    <Search className="absolute left-3.5 top-1/2 -translate-y-1/2 w-4 h-4 text-muted-foreground" />
                    <input
                        type="text"
                        placeholder="Tìm kiếm theo mã phiếu, tên độc giả hoặc mã thẻ..."
                        value={searchTerm}
                        onChange={(e) => setSearchTerm(e.target.value)}
                        className="w-full pl-10 pr-4 py-2.5 border border-border rounded-xl bg-card text-foreground placeholder-muted-foreground focus:outline-none focus:ring-2 focus:ring-primary transition text-sm shadow-xs"
                    />
                </div>

                <select
                    value={sortBy}
                    onChange={(e) => setSortBy(e.target.value as SortOption)}
                    className="px-4 py-2.5 bg-card border border-border rounded-xl text-foreground font-semibold text-xs focus:outline-none focus:ring-2 focus:ring-primary/20 transition cursor-pointer shadow-xs"
                >
                    <option value="borrowDate-desc">Mới nhất</option>
                    <option value="borrowDate-asc">Cũ nhất</option>
                    <option value="dueDate-asc">Hạn trả gần nhất</option>
                    <option value="dueDate-desc">Hạn trả xa nhất</option>
                    <option value="totalBooks-desc">Số sách nhiều nhất</option>
                    <option value="totalBooks-asc">Số sách ít nhất</option>
                    <option value="readerName-asc">Tên độc giả A - Z</option>
                    <option value="readerName-desc">Tên độc giả Z - A</option>
                </select>
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
                                <span className="text-muted-foreground">Số sách mượn:</span>
                                <span className="font-bold text-foreground text-right">
                  {returnConfirmModal.slip.totalBooks} cuốn sách
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

            {/* Slip Detail Modal */}
            <BorrowSlipDetailModal
                borrowSlipId={selectedSlipId}
                isOpen={Boolean(selectedSlipId)}
                onClose={() => setSelectedSlipId(null)}
                onInitiateReturn={handleInitiateReturn}
            />
        </div>
    );
}