import { useState } from "react";
import type { Page, Book, Member, BorrowRecord, UserAccount } from "@/types";
import { INITIAL_BOOKS, INITIAL_MEMBERS, INITIAL_BORROW_RECORDS, INITIAL_USER_ACCOUNTS } from "@/data/initialData";
import Sidebar from "@/components/Sidebar";
import LoginPage from "@/pages/LoginPage";
import Dashboard from "@/pages/Dashboard";
import BooksPage from "@/pages/BooksPage";
import MembersPage from "@/pages/MembersPage";
import BorrowPage from "@/pages/BorrowPage";
import ReturnPage from "@/pages/ReturnPage";
import AccountsPage from "@/pages/AccountsPage";

export default function App() {
  const [currentUser, setCurrentUser] = useState<UserAccount | null>(null);
  const [page, setPage] = useState<Page>("dashboard");
  const [sidebarExpanded, setSidebarExpanded] = useState(true);

  // Shared state
  const [accounts, setAccounts] = useState<UserAccount[]>(INITIAL_USER_ACCOUNTS);
  const [books, setBooks] = useState<Book[]>(INITIAL_BOOKS);
  const [members, setMembers] = useState<Member[]>(INITIAL_MEMBERS);
  const [records, setRecords] = useState<BorrowRecord[]>(INITIAL_BORROW_RECORDS);

  if (!currentUser) {
    return (
      <LoginPage
        accounts={accounts}
        onLogin={(account) => {
          setCurrentUser(account);
          setPage("dashboard");
        }}
      />
    );
  }

  const handleLogout = () => {
    setCurrentUser(null);
    setPage("dashboard");
  };

  // Keep accounts in sync: if current user's account was edited, update currentUser
  const handleSetAccounts = (updated: UserAccount[]) => {
    setAccounts(updated);
    const me = updated.find((a) => a.id === currentUser.id);
    if (me) setCurrentUser(me);
  };

  const renderPage = () => {
    switch (page) {
      case "dashboard":
        return <Dashboard books={books} members={members} records={records} />;
      case "books":
        return <BooksPage books={books} setBooks={setBooks} />;
      case "members":
        return <MembersPage members={members} setMembers={setMembers} />;
      case "borrow":
        return (
          <BorrowPage
            books={books}
            members={members}
            records={records}
            setRecords={setRecords}
            setBooks={setBooks}
          />
        );
      case "return":
        return (
          <ReturnPage
            records={records}
            setRecords={setRecords}
            books={books}
            setBooks={setBooks}
          />
        );
      case "accounts":
        return currentUser.role === "thu_thu" ? (
          <AccountsPage
            accounts={accounts}
            setAccounts={handleSetAccounts}
            currentUserId={currentUser.id}
          />
        ) : null;
      default:
        return null;
    }
  };

  return (
    <div className="flex h-screen overflow-hidden" style={{ backgroundColor: "#f0ede6" }}>
      <Sidebar
        page={page}
        setPage={setPage}
        expanded={sidebarExpanded}
        toggleExpanded={() => setSidebarExpanded((v) => !v)}
        onLogout={handleLogout}
        currentRole={currentUser.role}
      />
      <main className="flex-1 overflow-y-auto">
        {renderPage()}
      </main>
    </div>
  );
}
