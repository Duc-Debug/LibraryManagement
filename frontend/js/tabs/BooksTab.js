function BooksTab({ books, searchQuery, setSearchQuery, setShowAddBookModal }) {
    const filteredBooks = books.filter(b =>
        b.title.toLowerCase().includes(searchQuery.toLowerCase()) ||
        b.author.toLowerCase().includes(searchQuery.toLowerCase())
    );
    return (
        <div>
            <div className="flex justify-between items-center mb-6">
                <div>
                    <h2 className="text-2xl font-bold text-gray-900">Quản lý Sách</h2>
                    <p className="text-sm text-gray-600">Danh sách các sách có trong thư viện</p>
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
                    {filteredBooks.map((b) => (
                        <tr key={b.id} className="hover:bg-gray-50">
                            <td className="py-3 px-4 font-mono text-xs">{b.id}</td>
                            <td className="py-3 px-4 font-semibold text-gray-900">{b.title}</td>
                            <td className="py-3 px-4">{b.author}</td>
                            <td className="py-3 px-4">
                                    <span className="px-2 py-0.5 bg-gray-100 text-gray-700 rounded text-xs">
                                        {b.category}
                                    </span>
                            </td>
                            <td className="py-3 px-4">{b.quantity}</td>
                            <td className="py-3 px-4 font-semibold text-emerald-700">{b.available}</td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            </div>
        </div>
    );
}