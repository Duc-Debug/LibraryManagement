const { useState, useEffect } = React;

function App() {
    const [activeTab, setActiveTab] = useState('dashboard');
    // State quản lý Bật / Tắt (Mở rộng / Thu gọn) Sidebar
    const [isSidebarOpen, setIsSidebarOpen] = useState(true);

    // State dữ liệu quản lý
    const [books, setBooks] = useState(window.mockData ? window.mockData.books : []);
    const [recentActivities, setRecentActivities] = useState(window.mockData ? window.mockData.recentActivities : []);

    // Danh sách thành viên mẫu
    const [members, setMembers] = useState([
        { id: "TV001", name: "Trần Thị Bình", email: "binh.tran@gmail.com", phone: "0912345678", status: "Hoạt động" },
        { id: "TV002", name: "Nguyễn Văn An", email: "an.nguyen@gmail.com", phone: "0987654321", status: "Hoạt động" },
        { id: "TV003", name: "Lê Hoàng Nam", email: "nam.le@gmail.com", phone: "0909123456", status: "Quá hạn" }
    ]);

    // Danh sách mượn sách
    const [borrowList, setBorrowList] = useState([
        { id: "PM001", bookTitle: "Tuổi Trẻ Đáng Giá Bao Nhiêu", memberName: "Nguyễn Văn An", borrowDate: "2024-07-12", dueDate: "2024-07-26", status: "Đang mượn" },
        { id: "PM002", bookTitle: "Đắc Nhân Tâm", memberName: "Lê Hoàng Nam", borrowDate: "2024-06-30", dueDate: "2024-07-14", status: "Quá hạn" }
    ]);

    // Form states
    const [searchQuery, setSearchQuery] = useState("");
    const [showAddBookModal, setShowAddBookModal] = useState(false);
    const [newBook, setNewBook] = useState({ title: '', author: '', category: 'Tâm lý', quantity: 1 });

    const [showAddMemberModal, setShowAddMemberModal] = useState(false);
    const [newMember, setNewMember] = useState({ name: '', email: '', phone: '' });

    const [borrowForm, setBorrowForm] = useState({ memberName: '', bookTitle: '', dueDate: '' });

    // Tự động làm mới Icon Lucide khi state thay đổi
    useEffect(() => {
        if (window.lucide) {
            window.lucide.createIcons();
        }
    }, [activeTab, isSidebarOpen, books, members, borrowList, showAddBookModal, showAddMemberModal]);

    // Thêm sách mới
    const handleAddBook = (e) => {
        e.preventDefault();
        const createdBook = {
            id: `B00${books.length + 1}`,
            title: newBook.title,
            author: newBook.author,
            category: newBook.category,
            quantity: Number(newBook.quantity),
            available: Number(newBook.quantity)
        };
        setBooks([...books, createdBook]);
        setNewBook({ title: '', author: '', category: 'Tâm lý', quantity: 1 });
        setShowAddBookModal(false);
    };

    // Thêm thành viên mới
    const handleAddMember = (e) => {
        e.preventDefault();
        const createdMember = {
            id: `TV00${members.length + 1}`,
            name: newMember.name,
            email: newMember.email,
            phone: newMember.phone,
            status: "Hoạt động"
        };
        setMembers([...members, createdMember]);
        setNewMember({ name: '', email: '', phone: '' });
        setShowAddMemberModal(false);
    };

    // Lập phiếu mượn
    const handleCreateBorrow = (e) => {
        e.preventDefault();
        const today = new Date().toISOString().split('T')[0];
        const newRecord = {
            id: `PM00${borrowList.length + 1}`,
            bookTitle: borrowForm.bookTitle,
            memberName: borrowForm.memberName,
            borrowDate: today,
            dueDate: borrowForm.dueDate,
            status: "Đang mượn"
        };
        setBorrowList([newRecord, ...borrowList]);

        setRecentActivities([
            { id: Date.now(), book: borrowForm.bookTitle, member: borrowForm.memberName, action: "Mượn sách", date: today, status: "Đang mượn", statusColor: "bg-blue-100 text-blue-700" },
            ...recentActivities
        ]);

        setBorrowForm({ memberName: '', bookTitle: '', dueDate: '' });
        alert("Tạo phiếu mượn sách thành công!");
    };

    // Xác nhận trả sách
    const handleReturnBook = (id) => {
        setBorrowList(borrowList.map(item => item.id === id ? { ...item, status: "Đã trả" } : item));
    };

    return (
        <div className="flex min-h-screen bg-[#f8f6f0]">
            {/* ---------------- THANH MENU (SIDEBAR) ---------------- */}
            <aside
                className={`bg-white border-r border-gray-200 flex flex-col justify-between shrink-0 transition-all duration-300 ${
                    isSidebarOpen ? 'w-64 p-4' : 'w-16 p-2 items-center'
                }`}
            >
                <div>
                    {/* Header Sidebar + Nút Bật/Tắt Toggle */}
                    <div className={`flex items-center mb-6 gap-2 ${isSidebarOpen ? 'justify-between' : 'justify-center'}`}>
                        <button
                            onClick={() => setIsSidebarOpen(!isSidebarOpen)}
                            className="p-2 text-gray-600 hover:bg-gray-100 rounded-lg transition-colors"
                            title={isSidebarOpen ? "Thu gọn menu" : "Mở rộng menu"}
                        >
                            <i data-lucide="panel-left" className="w-5 h-5"></i>
                        </button>

                        {isSidebarOpen && (
                            <div className="flex items-center gap-2 bg-[#0f4c28] text-white p-2 rounded-lg flex-1">
                                <i data-lucide="book-open" className="w-5 h-5"></i>
                                <span className="font-bold text-sm leading-tight">Thư viện</span>
                            </div>
                        )}
                    </div>

                    {/* Các mục Menu */}
                    <nav className="space-y-1">
                        <button
                            onClick={() => setActiveTab('dashboard')}
                            title="Bảng điều khiển"
                            className={`w-full flex items-center rounded-lg font-medium text-sm transition-colors ${
                                isSidebarOpen ? 'px-4 py-3 gap-3 justify-start' : 'p-3 justify-center'
                            } ${
                                activeTab === 'dashboard' ? 'bg-[#0f4c28] text-white' : 'text-gray-700 hover:bg-gray-100'
                            }`}
                        >
                            <i data-lucide="layout-grid" className="w-5 h-5 shrink-0"></i>
                            {isSidebarOpen && <span>Bảng điều khiển</span>}
                        </button>

                        <button
                            onClick={() => setActiveTab('books')}
                            title="Quản lý Sách"
                            className={`w-full flex items-center rounded-lg font-medium text-sm transition-colors ${
                                isSidebarOpen ? 'px-4 py-3 gap-3 justify-start' : 'p-3 justify-center'
                            } ${
                                activeTab === 'books' ? 'bg-[#0f4c28] text-white' : 'text-gray-700 hover:bg-gray-100'
                            }`}
                        >
                            <i data-lucide="book-marked" className="w-5 h-5 shrink-0"></i>
                            {isSidebarOpen && <span>Quản lý Sách</span>}
                        </button>

                        <button
                            onClick={() => setActiveTab('members')}
                            title="Thành viên"
                            className={`w-full flex items-center rounded-lg font-medium text-sm transition-colors ${
                                isSidebarOpen ? 'px-4 py-3 gap-3 justify-start' : 'p-3 justify-center'
                            } ${
                                activeTab === 'members' ? 'bg-[#0f4c28] text-white' : 'text-gray-700 hover:bg-gray-100'
                            }`}
                        >
                            <i data-lucide="users" className="w-5 h-5 shrink-0"></i>
                            {isSidebarOpen && <span>Thành viên</span>}
                        </button>

                        <button
                            onClick={() => setActiveTab('borrow')}
                            title="Mượn sách"
                            className={`w-full flex items-center rounded-lg font-medium text-sm transition-colors ${
                                isSidebarOpen ? 'px-4 py-3 gap-3 justify-start' : 'p-3 justify-center'
                            } ${
                                activeTab === 'borrow' ? 'bg-[#0f4c28] text-white' : 'text-gray-700 hover:bg-gray-100'
                            }`}
                        >
                            <i data-lucide="log-out" className="w-5 h-5 rotate-180 shrink-0"></i>
                            {isSidebarOpen && <span>Mượn sách</span>}
                        </button>

                        <button
                            onClick={() => setActiveTab('return')}
                            title="Trả sách"
                            className={`w-full flex items-center rounded-lg font-medium text-sm transition-colors ${
                                isSidebarOpen ? 'px-4 py-3 gap-3 justify-start' : 'p-3 justify-center'
                            } ${
                                activeTab === 'return' ? 'bg-[#0f4c28] text-white' : 'text-gray-700 hover:bg-gray-100'
                            }`}
                        >
                            <i data-lucide="log-in" className="w-5 h-5 shrink-0"></i>
                            {isSidebarOpen && <span>Trả sách</span>}
                        </button>
                    </nav>
                </div>

                {/* Nút Đăng xuất */}
                <a
                    href="DangNhap.html"
                    title="Đăng xuất"
                    className={`flex items-center bg-[#0f4c28] text-white font-medium text-sm rounded-lg hover:bg-emerald-900 transition-colors ${
                        isSidebarOpen ? 'justify-center gap-2 px-4 py-2.5' : 'justify-center p-3'
                    }`}
                >
                    <i data-lucide="log-out" className="w-4 h-4 shrink-0"></i>
                    {isSidebarOpen && <span>Đăng xuất</span>}
                </a>
            </aside>

            {/* ---------------- NỘI DUNG CHÍNH (MAIN CONTENT) ---------------- */}
            <main className="flex-1 p-8 overflow-y-auto transition-all">

                {/* TAB 1: BẢNG ĐIỀU KHIỂN */}
                {activeTab === 'dashboard' && (
                    <div>
                        <div className="mb-6">
                            <h2 className="text-2xl font-bold text-gray-900">Bảng điều khiển</h2>
                            <p className="text-sm text-gray-600">Chào mừng đến với hệ thống quản lý thư viện</p>
                        </div>

                        {/* Thống kê tổng quan */}
                        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4 mb-8">
                            <div className="bg-white p-5 rounded-xl border border-gray-100 shadow-sm flex items-center justify-between">
                                <div>
                                    <p className="text-xs text-gray-500 font-medium">Tổng số sách</p>
                                    <p className="text-3xl font-bold text-gray-800 my-1">{books.reduce((acc, b) => acc + b.quantity, 0)}</p>
                                    <p className="text-xs text-gray-400">{books.reduce((acc, b) => acc + b.available, 0)} bản sẵn có</p>
                                </div>
                                <div className="p-3 bg-blue-50 text-blue-600 rounded-lg">
                                    <i data-lucide="bookmark" className="w-6 h-6"></i>
                                </div>
                            </div>

                            <div className="bg-white p-5 rounded-xl border border-gray-100 shadow-sm flex items-center justify-between">
                                <div>
                                    <p className="text-xs text-gray-500 font-medium">Thành viên</p>
                                    <p className="text-3xl font-bold text-gray-800 my-1">{members.length}</p>
                                    <p className="text-xs text-gray-400">{members.filter(m => m.status === 'Hoạt động').length} đang hoạt động</p>
                                </div>
                                <div className="p-3 bg-green-50 text-green-600 rounded-lg">
                                    <i data-lucide="users" className="w-6 h-6"></i>
                                </div>
                            </div>

                            <div className="bg-white p-5 rounded-xl border border-gray-100 shadow-sm flex items-center justify-between">
                                <div>
                                    <p className="text-xs text-gray-500 font-medium">Đang mượn</p>
                                    <p className="text-3xl font-bold text-gray-800 my-1">{borrowList.filter(b => b.status === 'Đang mượn').length}</p>
                                    <p className="text-xs text-gray-400">Lượt mượn hiện tại</p>
                                </div>
                                <div className="p-3 bg-amber-50 text-amber-600 rounded-lg">
                                    <i data-lucide="trending-up" className="w-6 h-6"></i>
                                </div>
                            </div>

                            <div className="bg-white p-5 rounded-xl border border-gray-100 shadow-sm flex items-center justify-between">
                                <div>
                                    <p className="text-xs text-gray-500 font-medium">Quá hạn</p>
                                    <p className="text-3xl font-bold text-gray-800 my-1">{borrowList.filter(b => b.status === 'Quá hạn').length}</p>
                                    <p className="text-xs text-gray-400">Cảnh báo</p>
                                </div>
                                <div className="p-3 bg-red-50 text-red-600 rounded-lg">
                                    <i data-lucide="alert-triangle" className="w-6 h-6"></i>
                                </div>
                            </div>
                        </div>

                        {/* Bảng hoạt động gần đây */}
                        <div className="bg-white rounded-xl border border-gray-100 shadow-sm p-6">
                            <h3 className="font-bold text-lg text-gray-900 mb-4">Hoạt động gần đây</h3>
                            <div className="overflow-x-auto">
                                <table className="w-full text-left text-sm text-gray-600">
                                    <thead className="bg-gray-50 text-gray-700 font-semibold border-b border-gray-100">
                                    <tr>
                                        <th className="py-3 px-4">Sách</th>
                                        <th className="py-3 px-4">Thành viên</th>
                                        <th className="py-3 px-4">Hành động</th>
                                        <th className="py-3 px-4">Ngày</th>
                                        <th className="py-3 px-4">Trạng thái</th>
                                    </tr>
                                    </thead>
                                    <tbody className="divide-y divide-gray-100">
                                    {recentActivities.map((act) => (
                                        <tr key={act.id} className="hover:bg-gray-50">
                                            <td className="py-3 px-4 font-medium text-gray-900">{act.book}</td>
                                            <td className="py-3 px-4">{act.member}</td>
                                            <td className="py-3 px-4">{act.action}</td>
                                            <td className="py-3 px-4">{act.date}</td>
                                            <td className="py-3 px-4">
                          <span className={`inline-block px-2.5 py-1 rounded-full text-xs font-semibold ${act.statusColor}`}>
                            {act.status}
                          </span>
                                            </td>
                                        </tr>
                                    ))}
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                )}

                {/* TAB 2: QUẢN LÝ SÁCH */}
                {activeTab === 'books' && (
                    <div>
                        <div className="flex justify-between items-center mb-6">
                            <div>
                                <h2 className="text-2xl font-bold text-gray-900">Quản lý Sách</h2>
                                <p className="text-sm text-gray-600">Danh sách các đầu sách có trong thư viện</p>
                            </div>
                            <button
                                onClick={() => setShowAddBookModal(true)}
                                className="flex items-center gap-2 bg-[#0f4c28] text-white px-4 py-2 rounded-lg font-medium text-sm hover:bg-emerald-900 transition-colors"
                            >
                                <i data-lucide="plus" className="w-4 h-4"></i> Thêm sách mới
                            </button>
                        </div>

                        <div className="mb-4">
                            <input
                                type="text"
                                placeholder="Tìm kiếm theo tên sách hoặc tác giả..."
                                value={searchQuery}
                                onChange={(e) => setSearchQuery(e.target.value)}
                                className="w-full md:w-80 px-4 py-2 border rounded-lg bg-white text-sm focus:outline-none focus:ring-2 focus:ring-[#0f4c28]"
                            />
                        </div>

                        <div className="bg-white rounded-xl border border-gray-100 shadow-sm overflow-hidden">
                            <table className="w-full text-left text-sm text-gray-600">
                                <thead className="bg-gray-50 text-gray-700 font-semibold border-b">
                                <tr>
                                    <th className="py-3 px-4">Mã sách</th>
                                    <th className="py-3 px-4">Tên sách</th>
                                    <th className="py-3 px-4">Tác giả</th>
                                    <th className="py-3 px-4">Thể loại</th>
                                    <th className="py-3 px-4">Số lượng</th>
                                    <th className="py-3 px-4">Sẵn có</th>
                                </tr>
                                </thead>
                                <tbody className="divide-y">
                                {books
                                    .filter(b => b.title.toLowerCase().includes(searchQuery.toLowerCase()) || b.author.toLowerCase().includes(searchQuery.toLowerCase()))
                                    .map((b) => (
                                        <tr key={b.id} className="hover:bg-gray-50">
                                            <td className="py-3 px-4 font-mono text-xs">{b.id}</td>
                                            <td className="py-3 px-4 font-semibold text-gray-900">{b.title}</td>
                                            <td className="py-3 px-4">{b.author}</td>
                                            <td className="py-3 px-4"><span className="px-2 py-0.5 bg-gray-100 text-gray-700 rounded text-xs">{b.category}</span></td>
                                            <td className="py-3 px-4">{b.quantity}</td>
                                            <td className="py-3 px-4 font-semibold text-emerald-700">{b.available}</td>
                                        </tr>
                                    ))}
                                </tbody>
                            </table>
                        </div>
                    </div>
                )}

                {/* TAB 3: QUẢN LÝ THÀNH VIÊN */}
                {activeTab === 'members' && (
                    <div>
                        <div className="flex justify-between items-center mb-6">
                            <div>
                                <h2 className="text-2xl font-bold text-gray-900">Quản lý Thành viên</h2>
                                <p className="text-sm text-gray-600">Danh sách người dùng đã đăng ký thẻ thư viện</p>
                            </div>
                            <button
                                onClick={() => setShowAddMemberModal(true)}
                                className="flex items-center gap-2 bg-[#0f4c28] text-white px-4 py-2 rounded-lg font-medium text-sm hover:bg-emerald-900 transition-colors"
                            >
                                <i data-lucide="user-plus" className="w-4 h-4"></i> Thêm thành viên
                            </button>
                        </div>

                        <div className="bg-white rounded-xl border border-gray-100 shadow-sm overflow-hidden">
                            <table className="w-full text-left text-sm text-gray-600">
                                <thead className="bg-gray-50 text-gray-700 font-semibold border-b">
                                <tr>
                                    <th className="py-3 px-4">Mã TV</th>
                                    <th className="py-3 px-4">Họ và Tên</th>
                                    <th className="py-3 px-4">Email</th>
                                    <th className="py-3 px-4">Số điện thoại</th>
                                    <th className="py-3 px-4">Trạng thái</th>
                                </tr>
                                </thead>
                                <tbody className="divide-y">
                                {members.map((m) => (
                                    <tr key={m.id} className="hover:bg-gray-50">
                                        <td className="py-3 px-4 font-mono text-xs">{m.id}</td>
                                        <td className="py-3 px-4 font-semibold text-gray-900">{m.name}</td>
                                        <td className="py-3 px-4">{m.email}</td>
                                        <td className="py-3 px-4">{m.phone}</td>
                                        <td className="py-3 px-4">
                        <span className={`px-2.5 py-1 rounded-full text-xs font-semibold ${m.status === 'Hoạt động' ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-700'}`}>
                          {m.status}
                        </span>
                                        </td>
                                    </tr>
                                ))}
                                </tbody>
                            </table>
                        </div>
                    </div>
                )}

                {/* TAB 4: MƯỢN SÁCH */}
                {activeTab === 'borrow' && (
                    <div className="max-w-2xl bg-white p-6 rounded-xl border border-gray-100 shadow-sm">
                        <h2 className="text-2xl font-bold text-gray-900 mb-2">Tạo phiếu Mượn sách</h2>
                        <p className="text-sm text-gray-600 mb-6">Nhập thông tin thành viên và sách cần mượn</p>

                        <form onSubmit={handleCreateBorrow} className="space-y-4">
                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-1">Thành viên mượn</label>
                                <select
                                    required
                                    value={borrowForm.memberName}
                                    onChange={(e) => setBorrowForm({ ...borrowForm, memberName: e.target.value })}
                                    className="w-full px-4 py-2 border rounded-lg bg-white text-sm outline-none focus:ring-2 focus:ring-[#0f4c28]"
                                >
                                    <option value="">-- Chọn thành viên --</option>
                                    {members.map(m => (
                                        <option key={m.id} value={m.name}>{m.name} ({m.id})</option>
                                    ))}
                                </select>
                            </div>

                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-1">Sách mượn</label>
                                <select
                                    required
                                    value={borrowForm.bookTitle}
                                    onChange={(e) => setBorrowForm({ ...borrowForm, bookTitle: e.target.value })}
                                    className="w-full px-4 py-2 border rounded-lg bg-white text-sm outline-none focus:ring-2 focus:ring-[#0f4c28]"
                                >
                                    <option value="">-- Chọn sách --</option>
                                    {books.map(b => (
                                        <option key={b.id} value={b.title}>{b.title} (Còn: {b.available})</option>
                                    ))}
                                </select>
                            </div>

                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-1">Hạn trả sách</label>
                                <input
                                    type="date"
                                    required
                                    value={borrowForm.dueDate}
                                    onChange={(e) => setBorrowForm({ ...borrowForm, dueDate: e.target.value })}
                                    className="w-full px-4 py-2 border rounded-lg bg-white text-sm outline-none focus:ring-2 focus:ring-[#0f4c28]"
                                />
                            </div>

                            <button
                                type="submit"
                                className="w-full py-2.5 bg-[#0f4c28] text-white font-medium text-sm rounded-lg hover:bg-emerald-900 transition-colors"
                            >
                                Xác nhận mượn sách
                            </button>
                        </form>
                    </div>
                )}

                {/* TAB 5: TRẢ SÁCH */}
                {activeTab === 'return' && (
                    <div>
                        <div className="mb-6">
                            <h2 className="text-2xl font-bold text-gray-900">Quản lý Trả sách</h2>
                            <p className="text-sm text-gray-600">Danh sách các phiếu đang mượn cần xác nhận trả</p>
                        </div>

                        <div className="bg-white rounded-xl border border-gray-100 shadow-sm overflow-hidden">
                            <table className="w-full text-left text-sm text-gray-600">
                                <thead className="bg-gray-50 text-gray-700 font-semibold border-b">
                                <tr>
                                    <th className="py-3 px-4">Mã phiếu</th>
                                    <th className="py-3 px-4">Tên sách</th>
                                    <th className="py-3 px-4">Thành viên</th>
                                    <th className="py-3 px-4">Ngày mượn</th>
                                    <th className="py-3 px-4">Hạn trả</th>
                                    <th className="py-3 px-4">Trạng thái</th>
                                    <th className="py-3 px-4">Hành động</th>
                                </tr>
                                </thead>
                                <tbody className="divide-y">
                                {borrowList.map((item) => (
                                    <tr key={item.id} className="hover:bg-gray-50">
                                        <td className="py-3 px-4 font-mono text-xs">{item.id}</td>
                                        <td className="py-3 px-4 font-semibold text-gray-900">{item.bookTitle}</td>
                                        <td className="py-3 px-4">{item.memberName}</td>
                                        <td className="py-3 px-4">{item.borrowDate}</td>
                                        <td className="py-3 px-4">{item.dueDate}</td>
                                        <td className="py-3 px-4">
                        <span className={`px-2.5 py-1 rounded-full text-xs font-semibold ${
                            item.status === 'Đã trả' ? 'bg-green-100 text-green-700' :
                                item.status === 'Quá hạn' ? 'bg-red-100 text-red-700' : 'bg-blue-100 text-blue-700'
                        }`}>
                          {item.status}
                        </span>
                                        </td>
                                        <td className="py-3 px-4">
                                            {item.status !== 'Đã trả' && (
                                                <button
                                                    onClick={() => handleReturnBook(item.id)}
                                                    className="px-3 py-1 bg-emerald-700 text-white text-xs font-medium rounded hover:bg-emerald-800 transition-colors"
                                                >
                                                    Xác nhận trả
                                                </button>
                                            )}
                                        </td>
                                    </tr>
                                ))}
                                </tbody>
                            </table>
                        </div>
                    </div>
                )}
            </main>

            {/* MODAL THÊM SÁCH */}
            {showAddBookModal && (
                <div className="fixed inset-0 bg-black/40 flex items-center justify-center p-4 z-50">
                    <div className="bg-white rounded-xl p-6 w-full max-w-md shadow-lg">
                        <h3 className="text-lg font-bold text-gray-900 mb-4">Thêm Sách mới</h3>
                        <form onSubmit={handleAddBook} className="space-y-4">
                            <div>
                                <label className="block text-xs font-medium text-gray-700 mb-1">Tên sách</label>
                                <input
                                    type="text"
                                    required
                                    value={newBook.title}
                                    onChange={(e) => setNewBook({ ...newBook, title: e.target.value })}
                                    className="w-full px-3 py-2 border rounded-lg text-sm outline-none focus:ring-2 focus:ring-[#0f4c28]"
                                />
                            </div>
                            <div>
                                <label className="block text-xs font-medium text-gray-700 mb-1">Tác giả</label>
                                <input
                                    type="text"
                                    required
                                    value={newBook.author}
                                    onChange={(e) => setNewBook({ ...newBook, author: e.target.value })}
                                    className="w-full px-3 py-2 border rounded-lg text-sm outline-none focus:ring-2 focus:ring-[#0f4c28]"
                                />
                            </div>
                            <div className="grid grid-cols-2 gap-3">
                                <div>
                                    <label className="block text-xs font-medium text-gray-700 mb-1">Thể loại</label>
                                    <select
                                        value={newBook.category}
                                        onChange={(e) => setNewBook({ ...newBook, category: e.target.value })}
                                        className="w-full px-3 py-2 border rounded-lg text-sm outline-none focus:ring-2 focus:ring-[#0f4c28]"
                                    >
                                        <option value="Tâm lý">Tâm lý</option>
                                        <option value="Lịch sử">Lịch sử</option>
                                        <option value="Kỹ năng sống">Kỹ năng sống</option>
                                        <option value="Văn học">Văn học</option>
                                    </select>
                                </div>
                                <div>
                                    <label className="block text-xs font-medium text-gray-700 mb-1">Số lượng</label>
                                    <input
                                        type="number"
                                        min="1"
                                        required
                                        value={newBook.quantity}
                                        onChange={(e) => setNewBook({ ...newBook, quantity: e.target.value })}
                                        className="w-full px-3 py-2 border rounded-lg text-sm outline-none focus:ring-2 focus:ring-[#0f4c28]"
                                    />
                                </div>
                            </div>
                            <div className="flex justify-end gap-2 pt-2">
                                <button
                                    type="button"
                                    onClick={() => setShowAddBookModal(false)}
                                    className="px-4 py-2 border rounded-lg text-sm text-gray-600 hover:bg-gray-50"
                                >
                                    Hủy
                                </button>
                                <button
                                    type="submit"
                                    className="px-4 py-2 bg-[#0f4c28] text-white rounded-lg text-sm font-medium hover:bg-emerald-900"
                                >
                                    Thêm mới
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            )}

            {/* MODAL THÊM THÀNH VIÊN */}
            {showAddMemberModal && (
                <div className="fixed inset-0 bg-black/40 flex items-center justify-center p-4 z-50">
                    <div className="bg-white rounded-xl p-6 w-full max-w-md shadow-lg">
                        <h3 className="text-lg font-bold text-gray-900 mb-4">Thêm Thành viên mới</h3>
                        <form onSubmit={handleAddMember} className="space-y-4">
                            <div>
                                <label className="block text-xs font-medium text-gray-700 mb-1">Họ và Tên</label>
                                <input
                                    type="text"
                                    required
                                    value={newMember.name}
                                    onChange={(e) => setNewMember({ ...newMember, name: e.target.value })}
                                    className="w-full px-3 py-2 border rounded-lg text-sm outline-none focus:ring-2 focus:ring-[#0f4c28]"
                                />
                            </div>
                            <div>
                                <label className="block text-xs font-medium text-gray-700 mb-1">Email</label>
                                <input
                                    type="email"
                                    required
                                    value={newMember.email}
                                    onChange={(e) => setNewMember({ ...newMember, email: e.target.value })}
                                    className="w-full px-3 py-2 border rounded-lg text-sm outline-none focus:ring-2 focus:ring-[#0f4c28]"
                                />
                            </div>
                            <div>
                                <label className="block text-xs font-medium text-gray-700 mb-1">Số điện thoại</label>
                                <input
                                    type="tel"
                                    required
                                    value={newMember.phone}
                                    onChange={(e) => setNewMember({ ...newMember, phone: e.target.value })}
                                    className="w-full px-3 py-2 border rounded-lg text-sm outline-none focus:ring-2 focus:ring-[#0f4c28]"
                                />
                            </div>
                            <div className="flex justify-end gap-2 pt-2">
                                <button
                                    type="button"
                                    onClick={() => setShowAddMemberModal(false)}
                                    className="px-4 py-2 border rounded-lg text-sm text-gray-600 hover:bg-gray-50"
                                >
                                    Hủy
                                </button>
                                <button
                                    type="submit"
                                    className="px-4 py-2 bg-[#0f4c28] text-white rounded-lg text-sm font-medium hover:bg-emerald-900"
                                >
                                    Lưu
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            )}

        </div>
    );
}

// Render ứng dụng React
const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(<App />);