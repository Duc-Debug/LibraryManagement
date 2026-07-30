function Sidebar({ isSidebarOpen, setIsSidebarOpen, activeTab, setActiveTab }) {
    return (
        <aside
            className={`bg-[#f7f7f5] border-r border-gray-200 flex flex-col justify-between shrink-0 transition-all duration-300 font-sans ${
                isSidebarOpen ? 'w-64 p-4' : 'w-20 p-3 items-center'
            }`}
        >
            <div>
                {/* Header Sidebar */}
                <div className={`flex items-center pb-4 mb-4 border-b border-gray-200 ${isSidebarOpen ? 'justify-between' : 'justify-center'}`}>
                    {isSidebarOpen ? (
                        <>
                            <div className="flex items-center gap-3">
                                <div className="w-11 h-11 bg-[#0f4c28] text-white flex items-center justify-center rounded-xl shadow-sm shrink-0">
                                    <i data-lucide="book-open" className="w-6 h-6"></i>
                                </div>
                                <div>
                                    <h1 className="font-bold text-gray-900 leading-tight text-base">Thư viện</h1>
                                    <p className="text-xs text-gray-500 font-medium">Quản lý sách</p>
                                </div>
                            </div>

                            <button
                                onClick={() => setIsSidebarOpen(false)}
                                className="w-9 h-9 border border-[#0f4c28]/40 rounded-xl flex items-center justify-center text-gray-700 hover:bg-gray-200/60 transition"
                                title="Thu gọn Sidebar"
                            >
                                <i data-lucide="x" className="w-4 h-4"></i>
                            </button>
                        </>
                    ) : (
                        <button
                            onClick={() => setIsSidebarOpen(true)}
                            className="w-11 h-11 bg-[#0f4c28] text-white flex items-center justify-center rounded-xl shadow-sm hover:bg-emerald-900 transition"
                            title="Mở rộng Sidebar"
                        >
                            <i data-lucide="book-open" className="w-6 h-6"></i>
                        </button>
                    )}
                </div>

                {/* Danh sách Menu */}
                <nav className="space-y-2">
                    <button
                        onClick={() => setActiveTab('dashboard')}
                        title="Bảng điều khiển"
                        className={`w-full flex items-center font-bold text-sm transition-colors rounded-2xl ${
                            isSidebarOpen ? 'px-4 py-3 gap-3.5 justify-start' : 'p-3 justify-center'
                        } ${
                            activeTab === 'dashboard'
                                ? 'bg-[#0f4c28] text-white shadow-sm'
                                : 'text-gray-800 hover:bg-gray-200/60'
                        }`}
                    >
                        <i data-lucide="layout-grid" className="w-5 h-5 shrink-0"></i>
                        {isSidebarOpen && <span>Bảng điều khiển</span>}
                    </button>

                    <button
                        onClick={() => setActiveTab('books')}
                        title="Quản lý Sách"
                        className={`w-full flex items-center font-bold text-sm transition-colors rounded-2xl ${
                            isSidebarOpen ? 'px-4 py-3 gap-3.5 justify-start' : 'p-3 justify-center'
                        } ${
                            activeTab === 'books'
                                ? 'bg-[#0f4c28] text-white shadow-sm'
                                : 'text-gray-800 hover:bg-gray-200/60'
                        }`}
                    >
                        <i data-lucide="book-open" className="w-5 h-5 shrink-0"></i>
                        {isSidebarOpen && <span>Quản lý Sách</span>}
                    </button>

                    <button
                        onClick={() => setActiveTab('members')}
                        title="Thành viên"
                        className={`w-full flex items-center font-bold text-sm transition-colors rounded-2xl ${
                            isSidebarOpen ? 'px-4 py-3 gap-3.5 justify-start' : 'p-3 justify-center'
                        } ${
                            activeTab === 'members'
                                ? 'bg-[#0f4c28] text-white shadow-sm'
                                : 'text-gray-800 hover:bg-gray-200/60'
                        }`}
                    >
                        <i data-lucide="users" className="w-5 h-5 shrink-0"></i>
                        {isSidebarOpen && <span>Thành viên</span>}
                    </button>

                    <button
                        onClick={() => setActiveTab('borrow')}
                        title="Mượn sách"
                        className={`w-full flex items-center font-bold text-sm transition-colors rounded-2xl ${
                            isSidebarOpen ? 'px-4 py-3 gap-3.5 justify-start' : 'p-3 justify-center'
                        } ${
                            activeTab === 'borrow'
                                ? 'bg-[#0f4c28] text-white shadow-sm'
                                : 'text-gray-800 hover:bg-gray-200/60'
                        }`}
                    >
                        <i data-lucide="log-in" className="w-5 h-5 shrink-0"></i>
                        {isSidebarOpen && <span>Mượn sách</span>}
                    </button>

                    <button
                        onClick={() => setActiveTab('return')}
                        title="Trả sách"
                        className={`w-full flex items-center font-bold text-sm transition-colors rounded-2xl ${
                            isSidebarOpen ? 'px-4 py-3 gap-3.5 justify-start' : 'p-3 justify-center'
                        } ${
                            activeTab === 'return'
                                ? 'bg-[#0f4c28] text-white shadow-sm'
                                : 'text-gray-800 hover:bg-gray-200/60'
                        }`}
                    >
                        <i data-lucide="repeat" className="w-5 h-5 shrink-0"></i>
                        {isSidebarOpen && <span>Trả sách</span>}
                    </button>
                </nav>
            </div>
            {/* Footer Sidebar */}
            <div className="pt-4 border-t border-gray-200">
                <a
                    href="dang-nhap.html"
                    title="Đăng xuất"
                    onClick={() => {
                        localStorage.removeItem('token');
                    }}
                    className={`w-full flex items-center bg-[#0f4c28] text-white font-bold text-sm rounded-2xl hover:bg-emerald-900 transition-colors shadow-sm ${
                        isSidebarOpen ? 'justify-center gap-2.5 py-3 px-4' : 'justify-center p-3'
                    }`}
                >
                    <i data-lucide="log-out" className="w-5 h-5 shrink-0"></i>
                    {isSidebarOpen && <span>Đăng xuất</span>}
                </a>
            </div>
        </aside>
    );
}