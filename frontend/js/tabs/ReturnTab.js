function ReturnTab({ borrowList, handleReturnBook }) {
    return (
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
    );
}