'use client';

import { useState, useEffect, useCallback, useMemo } from 'react';
import {
  Users,
  UserCheck,
  ShieldCheck,
  Plus,
  Search,
  RefreshCw,
  Edit3,
  Trash2,
  Lock,
  Unlock,
  CreditCard,
  X,
  AlertCircle,
  CheckCircle,
  QrCode,
  ShieldAlert,
  Eye,
  EyeOff
} from 'lucide-react';
import { Button } from '@/components/ui/button';
import {
  fetchAllReaders,
  createReader,
  updateReaderApi,
  deleteReaderApi,
  toggleReaderStatusApi,
  type ReaderResponse,
} from '@/api/readerApi';
import {
  fetchAllLibrarians,
  createLibrarian,
  updateLibrarian,
  deleteLibrarian,
  type Librarian,
} from '@/api/librarianApi';
import { AddUserModal } from './AddUserModal';
import { EditUserModal } from './EditUserModal';

export type UserTypeFilter = 'all' | 'readers' | 'librarians';

export interface UnifiedUser {
  id: string | number;
  userType: 'reader' | 'librarian';
  name: string;
  email: string;
  phone: string;
  cardNumber?: string;
  username?: string;
  address?: string;
  cardExpiryAt?: string;
  createdAt?: string;
  createdByName?: string;
  enabled: boolean;
  roleTitle: string;
  originalReaderData?: ReaderResponse;
  originalLibrarianData?: Librarian;
}

interface UserManagementPageProps {
  currentRole?: string;
  currentUserId?: string | number;
  currentUsername?: string;
  currentFullName?: string;
}

export function UserManagementPage({
  currentRole = 'admin',
  currentUserId,
  currentUsername,
  currentFullName,
}: UserManagementPageProps) {
  const isAdmin = currentRole === 'admin';
  const [activeTab, setActiveTab] = useState<UserTypeFilter>(isAdmin ? 'all' : 'readers');
  
  const [readers, setReaders] = useState<ReaderResponse[]>([]);
  const [librarians, setLibrarians] = useState<Librarian[]>([]);
  
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [error, setError] = useState('');
  const [successMessage, setSuccessMessage] = useState<string | null>(null);

  // Modals state
  const [showAddModal, setShowAddModal] = useState(false);
  const [selectedUserToEdit, setSelectedUserToEdit] = useState<UnifiedUser | null>(null);
  const [selectedUserToDelete, setSelectedUserToDelete] = useState<UnifiedUser | null>(null);
  const [previewCardReader, setPreviewCardReader] = useState<ReaderResponse | null>(null);
  const [togglingId, setTogglingId] = useState<string | number | null>(null);

  // Force non-admins to readers tab only
  useEffect(() => {
    if (!isAdmin) {
      setActiveTab('readers');
    }
  }, [isAdmin]);

  const loadAllUsers = useCallback(async () => {
    setLoading(true);
    setError('');
    const token = typeof window !== 'undefined' ? localStorage.getItem('accessToken') || '' : '';

    try {
      const [readersRes, librariansRes] = await Promise.allSettled([
        fetchAllReaders(token),
        isAdmin ? fetchAllLibrarians() : Promise.resolve([] as Librarian[])
      ]);

      if (readersRes.status === 'fulfilled') {
        setReaders(readersRes.value || []);
      }
      if (librariansRes.status === 'fulfilled') {
        setLibrarians(librariansRes.value || []);
      }
    } catch (err: any) {
      setError(err?.message || 'Không thể tải danh sách người dùng từ máy chủ.');
    } finally {
      setLoading(false);
    }
  }, [isAdmin]);

  useEffect(() => {
    loadAllUsers();
  }, [loadAllUsers]);

  const [statusFilter, setStatusFilter] = useState<'all' | 'active' | 'locked'>('all');
  const [sortBy, setSortBy] = useState<'newest' | 'oldest' | 'name'>('newest');

  // Combine readers and librarians into UnifiedUser list
  const unifiedUsersList = useMemo<UnifiedUser[]>(() => {
    const list: UnifiedUser[] = [];

    // Map readers
    readers.forEach((r) => {
      list.push({
        id: `R-${r.id}`,
        userType: 'reader',
        name: r.name,
        email: r.email,
        phone: r.phoneNumber,
        cardNumber: r.cardNumber,
        address: r.address,
        cardExpiryAt: r.cardExpiryAt,
        createdAt: r.cardIssuedAt,
        createdByName: r.createdByName,
        enabled: r.cardStatus === 'ACTIVE',
        roleTitle: 'Độc giả',
        originalReaderData: r,
      });
    });

    // Map librarians
    if (isAdmin) {
      librarians.forEach((l) => {
        list.push({
          id: `L-${l.id}`,
          userType: 'librarian',
          name: l.fullName,
          email: l.email || '',
          phone: l.phone || '',
          username: l.username,
          createdAt: l.createdAt,
          enabled: l.enabled,
          roleTitle: 'Thủ thư',
          originalLibrarianData: l,
        });
      });
    }

    return list;
  }, [readers, librarians, isAdmin]);

  // Filter & Sort list based on Tab, Status, Search term, Sort option, and Librarian Ownership
  const filteredUsers = useMemo(() => {
    const result = unifiedUsersList.filter((user) => {
      // 1. Tab filter
      if (activeTab === 'readers' && user.userType !== 'reader') return false;
      if (activeTab === 'librarians' && user.userType !== 'librarian') return false;

      // Ownership filter for Librarians: Only show reader cards created by this specific librarian!
      if (!isAdmin && user.userType === 'reader') {
        const creator = user.createdByName?.toLowerCase().trim();
        const uname = currentUsername?.toLowerCase().trim();
        const fname = currentFullName?.toLowerCase().trim();
        if (creator && (uname || fname)) {
          const isMatch = (uname && creator === uname) || (fname && creator === fname);
          if (!isMatch) return false;
        }
      }

      // 2. Status filter
      if (statusFilter === 'active' && !user.enabled) return false;
      if (statusFilter === 'locked' && user.enabled) return false;

      // 3. Search term filter
      if (!searchTerm.trim()) return true;
      const term = searchTerm.toLowerCase().trim();
      return (
        user.name.toLowerCase().includes(term) ||
        user.email.toLowerCase().includes(term) ||
        user.phone.includes(term) ||
        (user.cardNumber && user.cardNumber.toLowerCase().includes(term)) ||
        (user.username && user.username.toLowerCase().includes(term))
      );
    });

    // 4. Strict Sorting logic by Creation Date
    return result.sort((a, b) => {
      if (sortBy === 'name') {
        return a.name.localeCompare(b.name, 'vi');
      }

      const timeA = a.createdAt ? new Date(a.createdAt).getTime() : (Number(String(a.id).replace(/\D/g, '')) || 0);
      const timeB = b.createdAt ? new Date(b.createdAt).getTime() : (Number(String(b.id).replace(/\D/g, '')) || 0);

      if (sortBy === 'oldest') {
        return timeA - timeB;
      }
      // 'newest' (Ngày tạo mới nhất đứng đầu)
      return timeB - timeA;
    });
  }, [unifiedUsersList, activeTab, statusFilter, searchTerm, sortBy]);

  // Statistics counters
  const totalReadersCount = readers.length;
  const totalLibrariansCount = librarians.length;

  // Quick Lock / Unlock status toggle
  const handleToggleLock = async (user: UnifiedUser) => {
    setError('');
    setSuccessMessage(null);
    setTogglingId(user.id);
    const token = typeof window !== 'undefined' ? localStorage.getItem('accessToken') || '' : '';

    try {
      if (user.userType === 'reader' && user.originalReaderData) {
        const newStatus = user.enabled ? 'LOCKED' : 'ACTIVE';
        await toggleReaderStatusApi(user.originalReaderData.id, newStatus, token);
        setSuccessMessage(`Đã ${newStatus === 'ACTIVE' ? 'kích hoạt lại' : 'tạm khóa'} thẻ của độc giả "${user.name}".`);
      } else if (user.userType === 'librarian' && user.originalLibrarianData) {
        const item = user.originalLibrarianData;
        if (String(item.id) === String(currentUserId)) {
          setError('Bạn không thể tự khóa tài khoản của chính mình.');
          return;
        }
        if (item.enabled) {
          await deleteLibrarian(item.id);
        } else {
          await updateLibrarian(item.id, {
            fullName: item.fullName,
            email: item.email,
            phone: item.phone,
            enabled: true,
          });
        }
        setSuccessMessage(`Đã ${item.enabled ? 'khóa' : 'mở khóa'} tài khoản thủ thư "${user.name}".`);
      }
      await loadAllUsers();
    } catch (err: any) {
      setError(err?.message || 'Không thể thay đổi trạng thái tài khoản.');
    } finally {
      setTogglingId(null);
    }
  };

  // Delete User handler
  const handleDeleteUser = async () => {
    if (!selectedUserToDelete) return;
    setError('');
    setSuccessMessage(null);
    const user = selectedUserToDelete;
    const token = typeof window !== 'undefined' ? localStorage.getItem('accessToken') || '' : '';

    try {
      if (user.userType === 'reader' && user.originalReaderData) {
        await deleteReaderApi(user.originalReaderData.id, token);
        setSuccessMessage(`Đã xóa độc giả "${user.name}" thành công.`);
      } else if (user.userType === 'librarian' && user.originalLibrarianData) {
        await deleteLibrarian(user.originalLibrarianData.id);
        setSuccessMessage(`Đã xóa tài khoản thủ thư "${user.name}" thành công.`);
      }
      setSelectedUserToDelete(null);
      await loadAllUsers();
    } catch (err: any) {
      setError(err?.message || 'Xóa người dùng thất bại.');
      setSelectedUserToDelete(null);
    }
  };

  return (
    <div className="p-6 md:p-8 max-w-7xl mx-auto space-y-6 text-foreground animate-in fade-in duration-200">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-2 border-b border-border/50">
        <div>
          <h1 className="text-2xl font-bold tracking-tight flex items-center gap-2">
            <Users className="w-6 h-6 text-primary" />
            <span>Quản Lý Người Dùng & Thẻ Thư Viện</span>
          </h1>
          <p className="text-sm text-muted-foreground mt-1">
            {isAdmin
              ? 'Quản lý tập trung cả tài khoản Độc giả và Thủ thư nhân viên hệ thống.'
              : 'Quản lý thông tin bạn đọc và thẻ thư viện điện tử.'}
          </p>
        </div>
        <div className="flex items-center gap-3 self-start sm:self-auto">
          <Button onClick={loadAllUsers} variant="outline" disabled={loading} className="rounded-xl flex items-center gap-2 text-xs">
            <RefreshCw className={`w-3.5 h-3.5 ${loading ? 'animate-spin' : ''}`} />
            Tải lại
          </Button>
          <Button
            onClick={() => setShowAddModal(true)}
            className="bg-primary hover:bg-primary/90 text-primary-foreground rounded-xl flex items-center gap-2 text-xs font-semibold shadow-md cursor-pointer"
          >
            <Plus className="w-4 h-4" />
            Thêm Người Dùng Mới
          </Button>
        </div>
      </div>

      {/* Notifications */}
      {error && (
        <div className="p-4 bg-destructive/10 text-destructive rounded-xl border border-destructive/20 text-sm flex items-center justify-between animate-in fade-in">
          <span className="flex items-center gap-2">
            <AlertCircle className="w-4 h-4 text-destructive shrink-0" />
            {error}
          </span>
          <Button onClick={() => setError('')} size="sm" variant="ghost" className="h-7 w-7 p-0 rounded-lg">
            <X className="w-4 h-4" />
          </Button>
        </div>
      )}

      {successMessage && (
        <div className="p-4 bg-emerald-500/10 text-emerald-600 dark:text-emerald-400 rounded-xl border border-emerald-500/20 text-sm flex items-center justify-between animate-in fade-in">
          <span className="flex items-center gap-2">
            <CheckCircle className="w-4 h-4 text-emerald-500 shrink-0" />
            {successMessage}
          </span>
          <Button onClick={() => setSuccessMessage(null)} size="sm" variant="ghost" className="h-7 w-7 p-0 rounded-lg hover:bg-emerald-500/10">
            <X className="w-4 h-4" />
          </Button>
        </div>
      )}

      {/* Filter Tabs & Search Controls */}
      <div className="flex flex-col sm:flex-row items-center justify-between gap-4">
        {/* Tabs selector */}
        <div className="flex items-center gap-1.5 bg-muted/60 p-1 rounded-xl border border-border/50 text-xs w-full sm:w-auto">
          {isAdmin && (
            <button
              onClick={() => setActiveTab('all')}
              className={`flex items-center gap-1.5 px-3 py-2 rounded-lg font-semibold transition-all cursor-pointer ${
                activeTab === 'all'
                  ? 'bg-card text-foreground shadow-sm'
                  : 'text-muted-foreground hover:text-foreground'
              }`}
            >
              <Users className="w-3.5 h-3.5" />
              <span>Tất cả ({unifiedUsersList.length})</span>
            </button>
          )}

          <button
            onClick={() => setActiveTab('readers')}
            className={`flex items-center gap-1.5 px-3 py-2 rounded-lg font-semibold transition-all cursor-pointer ${
              activeTab === 'readers'
                ? 'bg-card text-foreground shadow-sm'
                : 'text-muted-foreground hover:text-foreground'
            }`}
          >
            <UserCheck className="w-3.5 h-3.5 text-emerald-600 dark:text-emerald-400" />
            <span>Độc giả ({totalReadersCount})</span>
          </button>

          {isAdmin && (
            <button
              onClick={() => setActiveTab('librarians')}
              className={`flex items-center gap-1.5 px-3 py-2 rounded-lg font-semibold transition-all cursor-pointer ${
                activeTab === 'librarians'
                  ? 'bg-card text-foreground shadow-sm'
                  : 'text-muted-foreground hover:text-foreground'
              }`}
            >
              <ShieldCheck className="w-3.5 h-3.5 text-purple-600 dark:text-purple-400" />
              <span>Thủ thư ({totalLibrariansCount})</span>
            </button>
          )}
        </div>

        {/* Search, Status & Sort Controls */}
        <div className="flex flex-col sm:flex-row items-center gap-2.5 w-full sm:w-auto flex-1 justify-end">
          {/* Live Search */}
          <div className="relative w-full sm:w-64">
            <Search className="absolute left-3.5 top-1/2 -translate-y-1/2 w-4 h-4 text-muted-foreground" />
            <input
              type="text"
              placeholder="Tìm theo tên, email, SĐT..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="w-full pl-10 pr-4 py-2 border border-border rounded-xl bg-card text-foreground placeholder-muted-foreground focus:outline-none focus:ring-2 focus:ring-primary transition text-xs shadow-xs"
            />
          </div>

          {/* Status Filter */}
          <select
            value={statusFilter}
            onChange={(e) => setStatusFilter(e.target.value as any)}
            className="px-3 py-2 border border-border rounded-xl bg-card text-foreground text-xs font-medium focus:outline-none focus:ring-2 focus:ring-primary shadow-xs cursor-pointer w-full sm:w-auto"
          >
            <option value="all">Tất cả trạng thái</option>
            <option value="active">Hoạt động</option>
            <option value="locked">Tạm khóa</option>
          </select>

          {/* Sort By */}
          <select
            value={sortBy}
            onChange={(e) => setSortBy(e.target.value as any)}
            className="px-3 py-2 border border-border rounded-xl bg-card text-foreground text-xs font-medium focus:outline-none focus:ring-2 focus:ring-primary shadow-xs cursor-pointer w-full sm:w-auto"
          >
            <option value="newest">Mới nhất (ID giảm dần)</option>
            <option value="oldest">Cũ nhất (ID tăng dần)</option>
            <option value="name">Tên (A - Z)</option>
          </select>
        </div>
      </div>

      {/* Users Table */}
      <div className="bg-card rounded-2xl border border-border shadow-sm overflow-hidden">
        {loading ? (
          <div className="p-16 text-center text-muted-foreground text-sm flex flex-col items-center justify-center gap-3">
            <RefreshCw className="w-6 h-6 animate-spin text-primary" />
            <span>Đang tải dữ liệu người dùng từ máy chủ...</span>
          </div>
        ) : filteredUsers.length === 0 ? (
          <div className="p-16 text-center text-muted-foreground text-sm">
            {searchTerm
              ? 'Không tìm thấy người dùng nào phù hợp với từ khóa.'
              : 'Chưa có người dùng nào trong danh mục này.'}
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left text-sm">
              <thead className="bg-muted/50 border-b border-border text-xs uppercase text-muted-foreground font-semibold whitespace-nowrap">
                <tr>
                  <th className="px-6 py-4 whitespace-nowrap">Mã / ID</th>
                  <th className="px-6 py-4 whitespace-nowrap">Họ và Tên</th>
                  <th className="px-6 py-4 whitespace-nowrap">Liên Hệ</th>
                  <th className="px-6 py-4 whitespace-nowrap">Thông Tin Đặc Thù</th>
                  <th className="px-6 py-4 text-center whitespace-nowrap">Trạng Thái</th>
                  <th className="px-6 py-4 text-right whitespace-nowrap">Thao Tác</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-border">
                {filteredUsers.map((user) => {
                  const isLibrarian = user.userType === 'librarian';
                  const isSelf = isLibrarian && String(user.originalLibrarianData?.id) === String(currentUserId);
                  const isToggling = togglingId === user.id;

                  return (
                    <tr key={user.id} className="hover:bg-muted/20 transition-colors">
                      {/* ID / Mã thẻ */}
                      <td className="px-6 py-4 font-mono text-xs font-bold whitespace-nowrap">
                        {user.userType === 'reader' && user.cardNumber ? (
                          <button
                            onClick={() => setPreviewCardReader(user.originalReaderData || null)}
                            className="inline-flex items-center gap-1.5 text-primary hover:underline cursor-pointer"
                            title="Xem Thẻ Điện Tử"
                          >
                            <CreditCard className="w-3.5 h-3.5 text-indigo-500" />
                            <span>{user.cardNumber}</span>
                          </button>
                        ) : (
                          <span className="text-muted-foreground">#{user.originalLibrarianData?.id}</span>
                        )}
                      </td>

                      {/* Họ tên + Role Badge */}
                      <td className="px-6 py-4 font-semibold text-foreground whitespace-nowrap">
                        <div className="flex items-center gap-2">
                          <span>{user.name}</span>
                          {isSelf && <span className="text-xs text-primary font-normal">(bạn)</span>}
                          {isLibrarian ? (
                            <span className="inline-flex items-center px-2 py-0.5 rounded-md text-[11px] font-semibold bg-purple-500/10 text-purple-600 dark:text-purple-400 border border-purple-500/20 whitespace-nowrap">
                              Thủ thư
                            </span>
                          ) : (
                            <span className="inline-flex items-center px-2 py-0.5 rounded-md text-[11px] font-semibold bg-emerald-500/10 text-emerald-600 dark:text-emerald-400 border border-emerald-500/20 whitespace-nowrap">
                              Độc giả
                            </span>
                          )}
                        </div>
                      </td>

                      {/* Liên hệ (Email & Phone) */}
                      <td className="px-6 py-4 text-xs space-y-0.5 whitespace-nowrap">
                        <div className="text-foreground font-medium">{user.email || '—'}</div>
                        <div className="text-muted-foreground font-mono">{user.phone || '—'}</div>
                      </td>

                      {/* Thông tin đặc thù (Username vs Address & Expiry) */}
                      <td className="px-6 py-4 text-xs text-muted-foreground max-w-xs whitespace-nowrap">
                        {isLibrarian ? (
                          <div className="font-mono font-medium text-foreground">
                            Username: <span className="text-purple-600 dark:text-purple-400">{user.username}</span>
                          </div>
                        ) : (
                          <div>
                            <div className="truncate">{user.address || 'Chưa cập nhật'}</div>
                            <div className="text-[11px] text-muted-foreground">
                              Hạn thẻ: {user.cardExpiryAt ? new Date(user.cardExpiryAt).toLocaleDateString('vi-VN') : 'N/A'}
                            </div>
                            {user.createdByName && (
                              <div className="text-[10px] text-indigo-600 dark:text-indigo-400 font-medium mt-0.5">
                                Cấp bởi thủ thư: <span className="font-semibold">{user.createdByName}</span>
                              </div>
                            )}
                          </div>
                        )}
                      </td>

                      {/* Trạng thái */}
                      <td className="px-6 py-4 text-center whitespace-nowrap">
                        <span
                          className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-semibold whitespace-nowrap ${
                            user.enabled
                              ? 'bg-emerald-500/10 text-emerald-600 dark:text-emerald-400 border border-emerald-500/20'
                              : 'bg-destructive/10 text-destructive border border-destructive/20'
                          }`}
                        >
                          {user.enabled ? 'Hoạt động' : 'Tạm khóa'}
                        </span>
                      </td>

                      {/* Thao tác */}
                      <td className="px-6 py-4 text-right text-xs font-medium space-x-1.5 whitespace-nowrap">
                        {user.userType === 'reader' && (
                          <Button
                            variant="outline"
                            size="sm"
                            onClick={() => setPreviewCardReader(user.originalReaderData || null)}
                            className="inline-flex items-center gap-1 px-2 py-1 rounded-lg text-xs"
                            title="Xem Thẻ Điện Tử"
                          >
                            <CreditCard className="w-3.5 h-3.5 text-indigo-500" />
                            Thẻ
                          </Button>
                        )}

                        <Button
                          variant="outline"
                          size="sm"
                          onClick={() => setSelectedUserToEdit(user)}
                          className="inline-flex items-center gap-1 px-2.5 py-1 rounded-lg text-xs"
                        >
                          <Edit3 className="w-3.5 h-3.5" />
                          Sửa
                        </Button>

                        <Button
                          variant={user.enabled ? 'secondary' : 'outline'}
                          size="sm"
                          disabled={isSelf || isToggling}
                          onClick={() => handleToggleLock(user)}
                          className={`inline-flex items-center gap-1 px-2.5 py-1 rounded-lg text-xs ${
                            isSelf ? 'opacity-40 cursor-not-allowed' : ''
                          }`}
                        >
                          {isToggling ? (
                            <RefreshCw className="w-3.5 h-3.5 animate-spin" />
                          ) : user.enabled ? (
                            <>
                              <Lock className="w-3.5 h-3.5 text-amber-500" />
                              Khóa
                            </>
                          ) : (
                            <>
                              <Unlock className="w-3.5 h-3.5 text-emerald-500" />
                              Mở
                            </>
                          )}
                        </Button>

                        <Button
                          variant="destructive"
                          size="sm"
                          disabled={isSelf}
                          onClick={() => setSelectedUserToDelete(user)}
                          className="inline-flex items-center gap-1 px-2 py-1 rounded-lg text-xs"
                        >
                          <Trash2 className="w-3.5 h-3.5" />
                        </Button>
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {/* 🪪 DIGITAL LIBRARY CARD PREVIEW MODAL */}
      {previewCardReader && (
        <div className="fixed inset-0 bg-black/60 backdrop-blur-xs flex items-center justify-center z-50 p-4 animate-in fade-in duration-150">
          <div className="bg-card rounded-3xl border border-border w-full max-w-lg shadow-2xl p-6 relative overflow-hidden space-y-5 animate-in zoom-in-95 duration-150">
            <div className="flex items-center justify-between pb-3 border-b border-border">
              <div className="flex items-center gap-2">
                <CreditCard className="w-5 h-5 text-indigo-500" />
                <h2 className="text-lg font-bold text-foreground">Thẻ Thư Viện Điện Tử</h2>
              </div>
              <button
                onClick={() => setPreviewCardReader(null)}
                className="text-muted-foreground hover:text-foreground p-1.5 rounded-lg hover:bg-muted transition cursor-pointer"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            <div className="relative rounded-2xl bg-gradient-to-tr from-slate-900 via-indigo-950 to-slate-900 text-white p-6 shadow-xl border border-indigo-500/30 overflow-hidden space-y-6">
              <div className="flex items-center justify-between relative z-10">
                <div className="flex items-center gap-2">
                  <div className="w-8 h-8 rounded-lg bg-gradient-to-tr from-indigo-500 to-violet-500 flex items-center justify-center font-bold text-xs text-white">
                    LIB
                  </div>
                  <div>
                    <div className="font-extrabold text-sm tracking-wider uppercase">THƯ VIỆN SỐ ENTERPRISE</div>
                    <div className="text-[10px] text-indigo-300">DIGITAL LIBRARY PASS</div>
                  </div>
                </div>
                <QrCode className="w-8 h-8 text-indigo-400 opacity-80" />
              </div>

              <div className="space-y-1 relative z-10">
                <div className="text-[11px] text-indigo-300 uppercase tracking-widest">MÃ THẺ ĐỘC GIẢ</div>
                <div className="font-mono text-xl font-extrabold tracking-widest text-indigo-200">
                  {previewCardReader.cardNumber}
                </div>
              </div>

              <div className="flex items-center justify-between relative z-10 text-xs">
                <div>
                  <div className="text-[10px] text-indigo-300 uppercase">CHỦ THẺ</div>
                  <div className="font-bold text-sm text-white">{previewCardReader.name}</div>
                  <div className="text-[11px] text-slate-400">{previewCardReader.phoneNumber}</div>
                </div>
                <div className="text-right">
                  <div className="text-[10px] text-indigo-300 uppercase">HẠN THẺ</div>
                  <div className="font-mono font-semibold text-emerald-400">
                    {previewCardReader.cardExpiryAt ? new Date(previewCardReader.cardExpiryAt).toLocaleDateString('vi-VN') : 'Vĩnh viễn'}
                  </div>
                </div>
              </div>
            </div>

            <div className="flex justify-end pt-2">
              <Button onClick={() => setPreviewCardReader(null)} variant="outline" size="sm" className="text-xs">
                Đóng
              </Button>
            </div>
          </div>
        </div>
      )}

      {/* Add User Modal */}
      {showAddModal && (
        <AddUserModal
          isAdmin={isAdmin}
          onClose={() => setShowAddModal(false)}
          onSuccess={() => {
            setShowAddModal(false);
            setSuccessMessage('Tạo người dùng mới thành công!');
            loadAllUsers();
          }}
        />
      )}

      {/* Edit User Modal */}
      {selectedUserToEdit && (
        <EditUserModal
          user={selectedUserToEdit}
          onClose={() => setSelectedUserToEdit(null)}
          onSuccess={() => {
            setSelectedUserToEdit(null);
            setSuccessMessage('Cập nhật thông tin người dùng thành công!');
            loadAllUsers();
          }}
        />
      )}

      {/* Delete User Modal */}
      {selectedUserToDelete && (
        <div className="fixed inset-0 bg-black/60 backdrop-blur-xs flex items-center justify-center z-50 p-4 animate-in fade-in duration-150">
          <div className="bg-card rounded-2xl border border-border w-full max-w-md shadow-2xl p-6 space-y-4 animate-in zoom-in-95 duration-150">
            <div className="flex items-center gap-2 text-destructive pb-2 border-b border-border">
              <Trash2 className="w-5 h-5" />
              <h2 className="text-lg font-bold">Xác Nhận Xóa Người Dùng</h2>
            </div>
            <p className="text-sm text-foreground">
              Bạn có chắc chắn muốn xóa tài khoản <strong>{selectedUserToDelete.name}</strong> ({selectedUserToDelete.roleTitle}) khỏi hệ thống?
            </p>
            <div className="flex gap-3 pt-2">
              <Button variant="outline" onClick={() => setSelectedUserToDelete(null)} className="flex-1 rounded-xl text-xs">
                Hủy
              </Button>
              <Button onClick={handleDeleteUser} variant="destructive" className="flex-1 rounded-xl text-xs font-bold cursor-pointer">
                Xác Nhận Xóa
              </Button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
