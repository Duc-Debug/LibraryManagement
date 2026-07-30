const App = () => {
    const initialData = window.mockData || { books: [], recentActivities: [] };
    const [activeTab, setActiveTab] = React.useState('dashboard');
    const [isSidebarOpen, setIsSidebarOpen] = React.useState(true);
    const [books, setBooks] = React.useState(initialData.books || []);
    const [members, setMembers] = React.useState([
        { id: 'M001', name: 'Nguyễn Văn An', email: 'an.nguyen@example.com', phone: '0901234567', status: 'Hoạt động' },
        { id: 'M002', name: 'Trần Thị Bình', email: 'binh.tran@example.com', phone: '0912345678', status: 'Hoạt động' },
        { id: 'M003', name: 'Lê Hoàng Nam', email: 'nam.le@example.com', phone: '0923456789', status: 'Ngừng hoạt động' }
    ]);
    const [borrowList, setBorrowList] = React.useState([]);
    const [recentActivities] = React.useState(initialData.recentActivities || []);
    const [searchQuery, setSearchQuery] = React.useState('');
    const [showAddBookModal, setShowAddBookModal] = React.useState(false);
    const [showAddMemberModal, setShowAddMemberModal] = React.useState(false);
    const [newBook, setNewBook] = React.useState({ title: '', author: '', category: 'Văn học', quantity: 1 });
    const [newMember, setNewMember] = React.useState({ name: '', email: '', phone: '' });
    const [borrowForm, setBorrowForm] = React.useState({ memberName: '', bookTitle: '', dueDate: '' });
    const handleAddBook = (e) => {
        e.preventDefault();
        const id = `B${String(books.length + 1).padStart(3, '0')}`;
        const quantity = Number(newBook.quantity) || 1;
        setBooks([...books, { id, ...newBook, quantity, available: quantity }]);
        setNewBook({ title: '', author: '', category: 'Văn học', quantity: 1 });
        setShowAddBookModal(false);
    };
    const handleAddMember = (e) => {
        e.preventDefault();
        const id = `M${String(members.length + 1).padStart(3, '0')}`;
        setMembers([...members, { id, ...newMember, status: 'Hoạt động' }]);
        setNewMember({ name: '', email: '', phone: '' });
        setShowAddMemberModal(false);
    };
    const handleCreateBorrow = (e) => {
        e.preventDefault();
        const id = `BR${String(borrowList.length + 1).padStart(3, '0')}`;
        const borrowDate = new Date().toISOString().split('T')[0];
        setBorrowList([...borrowList, { id, ...borrowForm, borrowDate, status: 'Đang mượn' }]);
        setBooks(books.map(b =>
            b.title === borrowForm.bookTitle ? { ...b, available: Math.max(0, b.available - 1) } : b
        ));
        setBorrowForm({ memberName: '', bookTitle: '', dueDate: '' });
    };
    const handleReturnBook = (id) => {
        const item = borrowList.find(b => b.id === id);
        setBorrowList(borrowList.map(b => (b.id === id ? { ...b, status: 'Đã trả' } : b)));
        if (item) {
            setBooks(books.map(b =>
                b.title === item.bookTitle ? { ...b, available: b.available + 1 } : b
            ));
        }
    };
    React.useEffect(() => {
        if (window.lucide) {
            window.lucide.createIcons();
        }
    });
    const renderTabContent = () => {
        switch (activeTab) {
            case 'dashboard':
                return <DashboardTab books={books} members={members} borrowList={borrowList} recentActivities={recentActivities} />;
            case 'books':
                return (
                    <BooksTab
                        books={books}
                        searchQuery={searchQuery}
                        setSearchQuery={setSearchQuery}
                        setShowAddBookModal={setShowAddBookModal}
                    />
                );
            case 'members':
                return <MembersTab members={members} setShowAddMemberModal={setShowAddMemberModal} />;
            case 'borrow':
                return (
                    <BorrowTab
                        members={members}
                        books={books}
                        borrowForm={borrowForm}
                        setBorrowForm={setBorrowForm}
                        handleCreateBorrow={handleCreateBorrow}
                    />
                );
            case 'return':
                return <ReturnTab borrowList={borrowList} handleReturnBook={handleReturnBook} />;
            default:
                return <DashboardTab books={books} members={members} borrowList={borrowList} recentActivities={recentActivities} />;
        }
    };
    return (
        <div className="flex min-h-screen bg-[#f8f6f0]">
            {/* Sidebar Component */}
            <Sidebar
                isSidebarOpen={isSidebarOpen}
                setIsSidebarOpen={setIsSidebarOpen}
                activeTab={activeTab}
                setActiveTab={setActiveTab}
            />
            {/* Nội dung chính */}
            <main className={`flex-1 p-8 transition-all duration-300 ${isSidebarOpen ? 'ml-64' : 'ml-20'}`}>
                {renderTabContent()}
            </main>
            {/* Modal thêm mới */}
            <AddBook
                isOpen={showAddBookModal}
                onClose={() => setShowAddBookModal(false)}
                newBook={newBook}
                setNewBook={setNewBook}
                handleAddBook={handleAddBook}
            />
            <AddMember
                isOpen={showAddMemberModal}
                onClose={() => setShowAddMemberModal(false)}
                newMember={newMember}
                setNewMember={setNewMember}
                handleAddMember={handleAddMember}
            />
        </div>
    );
};
const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(<App />);