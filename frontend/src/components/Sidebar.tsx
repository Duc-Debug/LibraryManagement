import type { ReactNode } from "react";
import type { Page, UserRole } from "@/types";
import {
  IconBook,
  IconGrid,
  IconUsers,
  IconArrowIn,
  IconRefresh,
  IconLogout,
  IconClose,
} from "@/components/icons";

interface SidebarProps {
  page: Page;
  setPage: (p: Page) => void;
  expanded: boolean;
  toggleExpanded: () => void;
  onLogout: () => void;
  currentRole: UserRole;
}

function IconShield({ size = 20, className = "" }: { size?: number; className?: string }) {
  return (
    <svg width={size} height={size} viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className={className}>
      <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z" />
    </svg>
  );
}

function IconUser({ size = 20, className = "" }: { size?: number; className?: string }) {
  return (
    <svg width={size} height={size} viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className={className}>
      <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2" />
      <circle cx="12" cy="7" r="4" />
    </svg>
  );
}

function IconFolder({ size = 20, className = "" }: { size?: number; className?: string }) {
  return (
    <svg width={size} height={size} viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className={className}>
      <path d="M22 19a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h5l2 3h9a2 2 0 0 1 2 2z" />
    </svg>
  );
}

const ALL_NAV_ITEMS: { id: Page; label: string; icon: ReactNode; adminOnly?: boolean }[] = [
  { id: "dashboard", label: "Bảng điều khiển", icon: <IconGrid /> },
  { id: "books", label: "Quản lý Sách", icon: <IconBook /> },
  { id: "categories", label: "Thể loại sách", icon: <IconFolder /> },
  { id: "members", label: "Thành viên", icon: <IconUsers /> },
  { id: "borrow", label: "Mượn sách", icon: <IconArrowIn /> },
  { id: "return", label: "Trả sách", icon: <IconRefresh /> },
  { id: "accounts", label: "Quản lý Tài khoản", icon: <IconShield />, adminOnly: true },
  { id: "settings", label: "Cài đặt cá nhân", icon: <IconUser /> },
];

export default function Sidebar({ page, setPage, expanded, toggleExpanded, onLogout, currentRole }: SidebarProps) {
  const navItems = ALL_NAV_ITEMS.filter((item) => !item.adminOnly || currentRole === "admin");

  return (
    <aside
      className="flex flex-col bg-white border-r border-gray-100 shrink-0 overflow-hidden"
      style={{ width: expanded ? 256 : 64, transition: "width 0.2s ease" }}
    >
      {/* Logo — click to toggle sidebar */}
      <div className="flex items-center gap-3 px-3 py-4 border-b border-gray-100">
        <button
          onClick={toggleExpanded}
          className="w-10 h-10 rounded-xl flex items-center justify-center shrink-0 transition-opacity hover:opacity-80"
          style={{ backgroundColor: "#1a4a2e" }}
          title={expanded ? "Thu gọn menu" : "Mở rộng menu"}
        >
          <IconBook size={20} className="text-white" />
        </button>

        {expanded && (
          <>
            <div className="overflow-hidden flex-1 min-w-0">
              <div className="font-bold text-sm leading-tight truncate" style={{ color: "#1a4a2e" }}>
                Thư viện
              </div>
              <div className="text-xs text-gray-400">Quản lý sách</div>
            </div>
            <button
              onClick={toggleExpanded}
              className="text-gray-400 hover:text-gray-600 p-1 rounded shrink-0"
              title="Thu gọn"
            >
              <IconClose size={16} />
            </button>
          </>
        )}
      </div>

      {/* Navigation */}
      <nav className="flex-1 py-3 space-y-0.5 px-2">
        {navItems.map((item) => {
          const active = page === item.id;
          return (
            <button
              key={item.id}
              onClick={() => setPage(item.id)}
              className={`w-full flex items-center gap-3 px-2 py-2.5 rounded-xl text-sm font-medium transition-colors ${
                active ? "text-white" : "text-gray-600 hover:bg-gray-50"
              }`}
              style={active ? { backgroundColor: "#1a4a2e" } : {}}
              title={!expanded ? item.label : undefined}
            >
              <span className="shrink-0">{item.icon}</span>
              {expanded && (
                <span className="whitespace-nowrap overflow-hidden text-ellipsis">
                  {item.label}
                </span>
              )}
            </button>
          );
        })}
      </nav>

      {/* Logout */}
      <div className="px-2 pb-4">
        {expanded ? (
          <button
            onClick={onLogout}
            className="w-full flex items-center gap-3 px-3 py-2.5 rounded-xl text-sm font-semibold text-white hover:opacity-90 transition-opacity"
            style={{ backgroundColor: "#1a4a2e" }}
          >
            <IconLogout size={18} />
            <span>Đăng xuất</span>
          </button>
        ) : (
          <button
            onClick={onLogout}
            className="w-full flex items-center justify-center py-2.5 rounded-xl text-white hover:opacity-90 transition-opacity"
            style={{ backgroundColor: "#1a4a2e" }}
            title="Đăng xuất"
          >
            <IconLogout size={18} />
          </button>
        )}
      </div>
    </aside>
  );
}
