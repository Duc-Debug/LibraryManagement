import type { BorrowStatus } from "@/types";

type BadgeStatus = BorrowStatus | "active" | "inactive";

const STATUS_MAP: Record<BadgeStatus, { label: string; cls: string }> = {
  returned: { label: "Đã trả", cls: "bg-green-100 text-green-700" },
  borrowing: { label: "Đang mượn", cls: "bg-blue-100 text-blue-700" },
  overdue: { label: "Quá hạn", cls: "bg-red-100 text-red-600" },
  active: { label: "Hoạt động", cls: "bg-green-100 text-green-700" },
  inactive: { label: "Ngừng hoạt động", cls: "bg-red-100 text-red-600" },
};

export default function StatusBadge({ status }: { status: BadgeStatus }) {
  const { label, cls } = STATUS_MAP[status] ?? { label: status, cls: "bg-gray-100 text-gray-600" };
  return (
    <span className={`inline-block px-3 py-0.5 rounded-full text-xs font-medium ${cls}`}>
      {label}
    </span>
  );
}
