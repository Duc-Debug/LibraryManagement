'use client';

import { useState } from 'react';
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
import {
  ReaderHome,
  BookDetails,
  ReaderProfilePage,
  mockReaderProfiles,
  mockReaderBorrows,
} from '@/features/reader';

export default function Page() {
  const [currentUser, setCurrentUser] = useState<UserAccount | null>(null);
  const [accounts, setAccounts] = useState<UserAccount[]>(mockUserAccounts);
  const [currentPage, setCurrentPage] = useState('dashboard');
  const [selectedBook, setSelectedBook] = useState<Book | null>(null);
  const [showBookDetails, setShowBookDetails] = useState(false);

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

  const handleLogout = () => {
    setCurrentUser(null);
    setCurrentPage('dashboard');
  };

  const handleSetAccounts = (updated: UserAccount[]) => {
    setAccounts(updated);
    const me = updated.find((a) => a.id === currentUser.id);
    if (me) setCurrentUser(me);
  };

  const renderPage = () => {
    // Reader pages
    if (currentUser?.role === 'nguoi_dung') {
      if (showBookDetails && selectedBook) {
        return (
          <BookDetails
            book={selectedBook}
            onBack={() => {
              setShowBookDetails(false);
              setSelectedBook(null);
            }}
            onBorrow={(book) => {
              alert(`Mượn "${book.title}" thành công!`);
              setShowBookDetails(false);
              setSelectedBook(null);
            }}
            onReadSample={(book) => {
              alert(`Xem mẫu "${book.title}" - Tính năng sẽ sớm có`);
            }}
          />
        );
      }

      if (currentPage === 'reader-profile') {
        const profile = mockReaderProfiles[0];
        const userBorrows = mockReaderBorrows;
        return (
          <ReaderProfilePage
            user={currentUser}
            profile={profile}
            borrows={userBorrows}
            onExtendBorrow={(borrowId) => {
              alert(`Gia hạn sách ${borrowId} thành công!`);
            }}
            onUpdateUser={(updatedUser) => {
              const updated = { ...currentUser, ...updatedUser };
              setCurrentUser(updated);
            }}
          />
        );
      }

      return (
        <ReaderHome
          onSelectBook={(book) => {
            setSelectedBook(book);
            setShowBookDetails(true);
          }}
          onBorrow={(book) => {
            alert(`Mượn "${book.title}" thành công!`);
          }}
        />
      );
    }

    // Admin/Librarian pages
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
        return currentUser.role === 'thu_thu' ? (
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
