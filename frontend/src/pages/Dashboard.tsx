import type { Book, Member, BorrowRecord } from "@/types";
import StatusBadge from "@/components/StatusBadge";
import { IconBookmark, IconUsers, IconTrendUp, IconAlert } from "@/components/icons";

interface DashboardProps {
  books: Book[];
  members: Member[];
  records: BorrowRecord[];
}

export default function Dashboard({ books = [], members = [], records = [] }: DashboardProps) {
  const totalBooks = books.reduce((s, b) => s + b.total, 0);
  const totalAvailable = books.reduce((s, b) => s + b.available, 0);
  const activeMembers = members.filter((m) => m.active).length;
  const borrowingCount = records.filter((r) => r.status === "borrowing").length;
  const overdueCount = records.filter((r) => r.status === "overdue").length;

  const recent = [...records].reverse().slice(0, 6);

  const stats = [
    {
      label: "Tổng số sách",
      value: totalBooks,
      sub: `${totalAvailable} bản sẵn có`,
      icon: <IconBookmark className="text-blue-500" />,
      iconBg: "bg-blue-50",
    },
    {
      label: "Thành viên",
      value: members.length,
      sub: `${activeMembers} đang hoạt động`,
      icon: <IconUsers size={22} className="text-green-600" />,
      iconBg: "bg-green-50",
    },
    {
      label: "Đang mượn",
      value: borrowingCount,
      sub: "Lượt mượn hiện tại",
      icon: <IconTrendUp className="text-amber-500" />,
      iconBg: "bg-amber-50",
    },
    {
      label: "Quá hạn",
      value: overdueCount,
      sub: "Cảnh báo",
      icon: <IconAlert className="text-red-500" />,
      iconBg: "bg-red-50",
    },
  ];

  const actionLabel = (status: string) => {
    if (status === "returned") return "Trả sách";
    if (status === "overdue") return "Cảnh báo";
    return "Mượn sách";
  };

  return (
    <div className="p-8">
      <h1 className="text-2xl font-bold text-gray-900">Bảng điều khiển</h1>
      <p className="text-sm text-gray-400 mt-0.5">Hệ thống quản lý thư viện</p>

      <div className="grid grid-cols-2 xl:grid-cols-4 gap-4 mt-6">
        {stats.map((s) => (
          <div key={s.label} className="bg-white rounded-2xl p-5 flex items-start justify-between shadow-sm">
            <div>
              <div className="text-xs text-gray-400 font-medium">{s.label}</div>
              <div className="text-4xl font-bold text-gray-900 mt-1">{s.value}</div>
              <div className="text-xs text-gray-400 mt-1">{s.sub}</div>
            </div>
            <div className={`w-10 h-10 rounded-xl flex items-center justify-center ${s.iconBg}`}>
              {s.icon}
            </div>
          </div>
        ))}
      </div>

      <div className="bg-white rounded-2xl mt-6 shadow-sm overflow-hidden">
        <div className="px-6 py-4 border-b border-gray-100">
          <h2 className="font-semibold text-gray-900">Hoạt động gần đây</h2>
        </div>
        <table className="w-full text-sm">
          <thead>
            <tr className="border-b border-gray-100">
              {["Sách", "Thành viên", "Hành động", "Ngày", "Trạng thái"].map((h) => (
                <th key={h} className="px-6 py-3 text-left text-xs font-semibold text-gray-700">{h}</th>
              ))}
            </tr>
          </thead>
          <tbody>
            {recent.length === 0 ? (
              <tr>
                <td colSpan={5} className="px-6 py-10 text-center text-gray-400 text-sm">
                  Chưa có hoạt động nào
                </td>
              </tr>
            ) : (
              recent.map((r) => (
                <tr key={r.id} className="border-b border-gray-50 last:border-0">
                  <td className="px-6 py-4 font-medium text-gray-900">{r.bookTitle}</td>
                  <td className="px-6 py-4 text-gray-500">{r.memberName}</td>
                  <td className="px-6 py-4 text-gray-500">{actionLabel(r.status)}</td>
                  <td className="px-6 py-4 text-gray-500">{r.borrowDate}</td>
                  <td className="px-6 py-4"><StatusBadge status={r.status} /></td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}
