function BorrowTab({ members, books, borrowForm, setBorrowForm, handleCreateBorrow }) {
    return (
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
    );
}