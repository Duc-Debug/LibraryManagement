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
        ) : null;
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
