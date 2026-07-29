function DashboardTab({ books, members, borrowList, recentActivities }) {
    return (
        <div>
            <div className="mb-6">
                <h2 className="text-2xl font-bold text-gray-900">Bảng điều khiển</h2>
                <p className="text-sm text-gray-600">Hệ thống quản lý thư viện</p>
            </div>

            {/* Thống kê tổng quan */}
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4 mb-8">
                <div className="bg-white p-5 rounded-xl border border-gray-100 shadow-sm flex items-center justify-between">
                    <div>
                        <p className="text-xs text-gray-500 font-medium">Tổng số sách</p>
                        <p className="text-3xl font-bold text-gray-800 my-1">
                            {books.reduce((acc, b) => acc + b.quantity, 0)}
                        </p>
                        <p className="text-xs text-gray-400">
                            {books.reduce((acc, b) => acc + b.available, 0)} bản sẵn có
                        </p>
                    </div>
                    <div className="p-3 bg-blue-50 text-blue-600 rounded-lg">
                        <i data-lucide="bookmark" className="w-6 h-6"></i>
                    </div>
                </div>

                <div className="bg-white p-5 rounded-xl border border-gray-100 shadow-sm flex items-center justify-between">
                    <div>
                        <p className="text-xs text-gray-500 font-medium">Thành viên</p>
                        <p className="text-3xl font-bold text-gray-800 my-1">{members.length}</p>
                        <p className="text-xs text-gray-400">
                            {members.filter(m => m.status === 'Hoạt động').length} đang hoạt động
                        </p>
                    </div>
                    <div className="p-3 bg-green-50 text-green-600 rounded-lg">
                        <i data-lucide="users" className="w-6 h-6"></i>
                    </div>
                </div>

                <div className="bg-white p-5 rounded-xl border border-gray-100 shadow-sm flex items-center justify-between">
                    <div>
                        <p className="text-xs text-gray-500 font-medium">Đang mượn</p>
                        <p className="text-3xl font-bold text-gray-800 my-1">
                            {borrowList.filter(b => b.status === 'Đang mượn').length}
                        </p>
                        <p className="text-xs text-gray-400">Lượt mượn hiện tại</p>
                    </div>
                    <div className="p-3 bg-amber-50 text-amber-600 rounded-lg">
                        <i data-lucide="trending-up" className="w-6 h-6"></i>
                    </div>
                </div>

                <div className="bg-white p-5 rounded-xl border border-gray-100 shadow-sm flex items-center justify-between">
                    <div>
                        <p className="text-xs text-gray-500 font-medium">Quá hạn</p>
                        <p className="text-3xl font-bold text-gray-800 my-1">
                            {borrowList.filter(b => b.status === 'Quá hạn').length}
                        </p>
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
    );
}