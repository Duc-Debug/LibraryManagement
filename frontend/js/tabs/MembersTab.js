function MembersTab({ members, setShowAddMemberModal }) {
    return (
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
                                    <span className={`px-2.5 py-1 rounded-full text-xs font-semibold ${
                                        m.status === 'Hoạt động' ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-700'
                                    }`}>
                                        {m.status}
                                    </span>
                            </td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            </div>
        </div>
    );
}