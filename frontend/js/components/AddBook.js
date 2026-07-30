// Thêm React nếu trong component sử dụng JSX hoặc React hooks khi chạy bằng ES Modules
const { useState } = React;

function AddBook({ isOpen, onClose, newBook, setNewBook, handleAddBook }) {
    if (!isOpen) return null;

    return (
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
                            onClick={onClose}
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
    );
}