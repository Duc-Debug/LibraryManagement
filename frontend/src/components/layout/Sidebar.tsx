'use client';

import { useState } from 'react';
import type { UserAccount } from '@/features/accounts';
import {
  LayoutDashboard,
  BookOpen,
  Users,
  LogOut,
  LogIn,
  Menu,
  X,
  Repeat2,
  Settings,
  Search,
  User,
  Tags,
} from 'lucide-react';
import { Button } from '@/components/ui/button';

interface SidebarProps {
  currentPage: string;
  onPageChange: (page: string) => void;
  currentUser?: UserAccount | null;
  onLogout?: () => void;
}

export function Sidebar({ currentPage, onPageChange, currentUser, onLogout }: SidebarProps) {
  const [isCollapsed, setIsCollapsed] = useState(false);

  const navItems = [
    {
      id: 'dashboard',
      label: 'Bảng điều khiển',
      icon: LayoutDashboard,
    },
    {
      id: 'books',
      label: 'Quản lý Sách',
      icon: BookOpen,
    },
    {
      id: 'categories',
      label: 'Thể loại sách',
      icon: Tags,
    },
    {
      id: 'members',
      label: 'Thành viên',
      icon: Users,
    },
    {
      id: 'borrowing',
      label: 'Mượn sách',
      icon: LogIn,
    },
    {
      id: 'returns',
      label: 'Trả sách',
      icon: Repeat2,
    },
    ...(currentUser?.role === 'admin'
      ? [
          {
            id: 'accounts',
            label: 'Quản lý TK',
            icon: Settings,
          },
        ]
      : []),
    {
      id: 'settings',
      label: 'Cài đặt cá nhân',
      icon: User,
    },
  ];

  return (
    <aside
      className={`bg-sidebar border-r border-sidebar-border transition-all duration-300 flex flex-col ${
        isCollapsed ? 'w-20' : 'w-64'
      }`}
    >
      {/* Header */}
      <div className="flex items-center justify-between p-4 border-b border-sidebar-border">
        <div className={`flex items-center gap-3 ${isCollapsed ? 'hidden' : 'flex'}`}>
          <div className="w-10 h-10 bg-primary rounded-lg flex items-center justify-center">
            <BookOpen className="w-6 h-6 text-white" />
          </div>
          <div className="flex flex-col">
            <h1 className="text-sm font-bold text-sidebar-foreground">Thư viện</h1>
            <p className="text-xs text-sidebar-foreground/60">
              {currentUser?.role === 'admin'
                ? 'Giao diện Quản trị viên'
                : 'Giao diện thủ thư'}
            </p>
          </div>
        </div>
        <Button
          variant="ghost"
          size="icon"
          onClick={() => setIsCollapsed(!isCollapsed)}
          className="h-8 w-8"
        >
          {isCollapsed ? (
            <Menu className="w-4 h-4" />
          ) : (
            <X className="w-4 h-4" />
          )}
        </Button>
      </div>

      {/* Navigation */}
      <nav className="flex-1 p-4 space-y-2">
        {navItems.map((item) => {
          const Icon = item.icon;
          const isActive = currentPage === item.id;

          return (
            <button
              key={item.id}
              onClick={() => onPageChange(item.id)}
              className={`w-full flex items-center gap-3 px-4 py-3 rounded-lg transition-colors ${
                isActive
                  ? 'bg-sidebar-primary text-sidebar-primary-foreground'
                  : 'text-sidebar-foreground hover:bg-sidebar-accent'
              }`}
            >
              <Icon className="w-5 h-5 flex-shrink-0" />
              {!isCollapsed && <span className="text-sm font-medium">{item.label}</span>}
            </button>
          );
        })}
      </nav>

      {/* Footer */}
      <div className="p-4 border-t border-sidebar-border space-y-3">
        {currentUser && !isCollapsed && (
          <div className="px-2 py-2 bg-sidebar-accent rounded-lg">
            <p className="text-xs font-semibold text-sidebar-foreground truncate">
              {currentUser.fullName}
            </p>
            <p className="text-xs text-sidebar-foreground/60 truncate">
              {currentUser.role === 'admin'
                ? 'Quản trị viên'
                : 'Thủ thư'}
            </p>
          </div>
        )}
        <Button
          className="w-full"
          variant={isCollapsed ? 'ghost' : 'default'}
          size={isCollapsed ? 'icon' : 'default'}
          onClick={onLogout}
        >
          {isCollapsed ? (
            <LogOut className="w-4 h-4" />
          ) : (
            <>
              <LogOut className="w-4 h-4 mr-2" />
              Đăng xuất
            </>
          )}
        </Button>
      </div>
    </aside>
  );
}
