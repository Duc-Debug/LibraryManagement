import React, { useState } from 'react';
import {
    BookOpen,
    LayoutGrid,
    Users,
    LogIn,
    Repeat,
    LogOut,
    X
} from 'lucide-react';

export default function Sidebar() {
    const [activeTab, setActiveTab] = useState('dashboard');

    const navItems = [
        { id: 'dashboard', label: 'Bảng điều khiển', icon: LayoutGrid },
        { id: 'books', label: 'Quản lý Sách', icon: BookOpen },
        { id: 'members', label: 'Thành viên', icon: Users },
        { id: 'borrow', label: 'Mượn sách', icon: LogIn },
        { id: 'return', label: 'Trả sách', icon: Repeat },
    ];

    return (
        <aside className="w-64 h-screen bg-[#f7f7f5] border-r border-gray-200 flex flex-col justify-between p-4 text-gray-800">
            <div>
                {/* Header */}
                <div className="flex items-center justify-between pb-4 mb-4 border-b border-gray-200">
                    <div className="flex items-center gap-3">
                        <div className="w-11 h-11 bg-[#0b5d1e] text-white flex items-center justify-center rounded-xl shadow-sm">
                            <BookOpen size={22} />
                        </div>
                        <div>
                            <h1 className="font-bold text-gray-900 leading-tight">Thư viện</h1>
                            <p className="text-xs text-gray-500 font-medium">QL Sách</p>
                        </div>
                    </div>

                    <button className="w-9 h-9 border border-[#0b5d1e]/30 rounded-xl flex items-center justify-center text-gray-700 hover:bg-gray-200/60 transition">
                        <X size={18} />
                    </button>
                </div>

                {/* Navigation */}
                <nav className="space-y-1.5">
                    {navItems.map((item) => {
                        const Icon = item.icon;
                        const isActive = activeTab === item.id;
                        return (
                            <button
                                key={item.id}
                                onClick={() => setActiveTab(item.id)}
                                className={`w-full flex items-center gap-3.5 px-4 py-3 rounded-2xl font-semibold transition text-left ${
                                    isActive
                                        ? 'bg-[#0b5d1e] text-white shadow-sm'
                                        : 'text-gray-800 hover:bg-gray-200/60'
                                }`}
                            >
                                <Icon size={20} />
                                <span>{item.label}</span>
                            </button>
                        );
                    })}
                </nav>
            </div>

            {/* Logout */}
            <div className="pt-4 border-t border-gray-200">
                <button className="w-full flex items-center justify-center gap-2.5 py-3 bg-[#0b5d1e] text-white font-bold rounded-2xl hover:bg-[#084817] transition shadow-sm">
                    <LogOut size={20} />
                    <span>Đăng xuất</span>
                </button>
            </div>
        </aside>
    );
}