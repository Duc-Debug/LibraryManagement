import { useState } from "react";
import type { Page, Book, Member, BorrowRecord } from "@/types";
import { INITIAL_BOOKS, INITIAL_MEMBERS, INITIAL_BORROW_RECORDS } from "@/data/initialData";
import Sidebar from "@/components/Sidebar";
import LoginPage from "@/pages/LoginPage";
import Dashboard from "@/pages/Dashboard";
import BooksPage from "@/pages/BooksPage";
import MembersPage from "@/pages/MembersPage";
import BorrowPage from "@/pages/BorrowPage";
import ReturnPage from "@/pages/ReturnPage";
import { logout as logoutRequest } from "@/api/authApi";

export default function App() {
  const [loggedIn, setLoggedIn] = useState(() => {
  return Boolean(localStorage.getItem("accessToken"));
});
  const [page, setPage] = useState<Page>("dashboard");
  const [sidebarExpanded, setSidebarExpanded] = useState(true);

  const [books, setBooks] = useState<Book[]>(INITIAL_BOOKS);
  const [members, setMembers] = useState<Member[]>(INITIAL_MEMBERS);
  const [records, setRecords] = useState<BorrowRecord[]>(INITIAL_BORROW_RECORDS);
const handleLogout = async () => {
  const accessToken = localStorage.getItem("accessToken");

  try {
    if (accessToken) {
      await logoutRequest(accessToken);
    }
  } catch (error) {
    console.error("Logout failed:", error);
  } finally {
    localStorage.removeItem("accessToken");
    localStorage.removeItem("tokenType");
    localStorage.removeItem("currentUser");

    setLoggedIn(false);
  }
};
  if (!loggedIn) {
    return <LoginPage onLogin={() => setLoggedIn(true)} />;
  }

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
/>
      
      <main className="flex-1 overflow-y-auto">
        {renderPage()}
      </main>
    </div>
  );
}
