'use client';

import { useState, useEffect } from 'react';
import { Sidebar } from '@/components/layout/Sidebar';
import { Dashboard } from '@/features/dashboard';
import { BooksPage } from '@/features/books';
import type { Book } from '@/features/books';
import { MembersPage } from '@/features/members';
import { BorrowingPage } from '@/features/borrowing';
import { ReturnsPage } from '@/features/returns';
import { LoginPage } from '@/features/auth';
import { AccountsPage, mockUserAccounts } from '@/features/accounts';
import type { UserAccount } from '@/features/accounts';
import { logout } from '@/api/authApi';
import SettingsPage from '@/pages/SettingsPage';
import {
  ReaderHome,
  BookDetails,
  ReaderProfilePage,
  mockReaderProfiles,
  mockReaderBorrows,
} from '@/features/reader';

export default function Page() {
  const [currentUser, setCurrentUser] = useState<UserAccount | null>(null);
  const [isInitialized, setIsInitialized] = useState(false);
  const [accounts, setAccounts] = useState<UserAccount[]>(mockUserAccounts);
  const [currentPage, setCurrentPage] = useState('dashboard');
  const [selectedBook, setSelectedBook] = useState<Book | null>(null);
  const [showBookDetails, setShowBookDetails] = useState(false);

  useEffect(() => {
    const token = localStorage.getItem('accessToken');
    const savedUser = localStorage.getItem('currentUser');
    if (token && savedUser) {
      try {
        const parsed = JSON.parse(savedUser);
        const roles = parsed.roles ?? [];
        const role = roles.includes('ADMIN')
          ? 'admin'
          : 'thu_thu';

        setCurrentUser({
          id: String(parsed.userId || parsed.id || '1'),
          username: parsed.username,
          password: '',
          fullName: parsed.fullName,
          role,
          active: true
        });
      } catch (e) {
        console.error('Failed to restore user session:', e);
      }
    }
    setIsInitialized(true);
  }, []);

  if (!isInitialized) {
    return <div className="min-h-screen bg-background flex items-center justify-center text-muted-foreground">Đang tải...</div>;
  }

  if (!currentUser) {
    return (
      <LoginPage
        accounts={accounts}
        onLogin={(account) => {
          setCurrentUser(account);
          setCurrentPage('dashboard');
        }}
      />
    );
  }

  const handleLogout = async () => {
    const token = localStorage.getItem('accessToken');
    if (token) {
      await logout(token);
    }
    localStorage.removeItem('accessToken');
    localStorage.removeItem('tokenType');
    localStorage.removeItem('currentUser');
    setCurrentUser(null);
    setCurrentPage('dashboard');
  };

  const handleSetAccounts = (updated: UserAccount[]) => {
    setAccounts(updated);
    const me = updated.find((a) => a.id === currentUser.id);
    if (me) setCurrentUser(me);
  };

  const handleProfileUpdated = (updated: any) => {
    setCurrentUser((prev) => (prev ? { ...prev, ...updated } : null));
    const savedUserStr = localStorage.getItem('currentUser');
    if (savedUserStr) {
      try {
        const parsed = JSON.parse(savedUserStr);
        parsed.fullName = updated.fullName;
        parsed.email = updated.email;
        parsed.phone = updated.phone;
        localStorage.setItem('currentUser', JSON.stringify(parsed));
      } catch (e) {
        console.error('Failed to update localStorage currentUser:', e);
      }
    }
  };

  const renderPage = () => {
    switch (currentPage) {
      case 'dashboard':
        return <Dashboard />;
      case 'books':
        return <BooksPage />;
      case 'members':
        return <MembersPage />;
      case 'borrowing':
        return <BorrowingPage />;
      case 'returns':
        return <ReturnsPage />;
      case 'accounts':
        return currentUser.role === 'admin' ? (
          <AccountsPage
            accounts={accounts}
            setAccounts={handleSetAccounts}
            currentUserId={currentUser.id}
          />
        ) : (
          <div className="p-12 flex flex-col items-center justify-center min-h-[500px] text-center">
            <div className="w-16 h-16 bg-red-100 text-red-600 rounded-2xl flex items-center justify-center mb-4 text-2xl font-bold shadow-sm border border-red-200">
              403
            </div>
            <h2 className="text-xl font-bold text-foreground mb-2">Truy cập trái phép bị chặn (403)</h2>
            <p className="text-sm text-muted-foreground max-w-md">
              Tài khoản hiện tại của bạn không có quyền Quản trị viên (Admin) để truy cập chức năng này.
            </p>
          </div>
        );
      case 'settings':
        return (
          <SettingsPage
            currentUser={{
              id: currentUser.id,
              username: currentUser.username,
              password: '',
              fullName: currentUser.fullName,
              email: currentUser.email,
              phone: currentUser.phone,
              role: currentUser.role === 'admin' ? 'admin' : 'thu_thu',
              active: currentUser.active ?? true,
            }}
            onProfileUpdated={handleProfileUpdated}
          />
        );
      default:
        return <Dashboard />;
    }
  };

  return (
    <div className="flex h-screen bg-background">
      <Sidebar
        currentPage={currentPage}
        onPageChange={setCurrentPage}
        currentUser={currentUser}
        onLogout={handleLogout}
      />
      <main className="flex-1 overflow-auto">
        {renderPage()}
      </main>
    </div>
  );
}
