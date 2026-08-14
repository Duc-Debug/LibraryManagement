import React from "react";
import type { BorrowReportSummaryDto } from "../../../types/report.types";

interface ReportKpiCardsProps {
  summary?: BorrowReportSummaryDto;
  isLoading?: boolean;
}

export const ReportKpiCards: React.FC<ReportKpiCardsProps> = ({ summary, isLoading }) => {
  if (isLoading) {
    return (
      <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-4 mb-6">
        {[1, 2, 3, 4].map((i) => (
          <div key={i} className="h-28 rounded-xl bg-muted/60 animate-pulse border border-border p-4" />
        ))}
      </div>
    );
  }

  const formatCurrency = (val?: number) => {
    return new Intl.NumberFormat("vi-VN", { style: "currency", currency: "VND" }).format(val || 0);
  };

  const cards = [
    {
      title: "TỔNG SỐ PHIẾU MƯỢN",
      value: summary?.totalSlips ?? 0,
      sub: `${summary?.totalBooksBorrowed ?? 0} lượt sách`,
      icon: "📚",
      bgColor: "bg-blue-500/10 text-blue-600 dark:text-blue-400 border-blue-200 dark:border-blue-900",
    },
    {
      title: "ĐÃ TRẢ THÀNH CÔNG",
      value: summary?.totalReturned ?? 0,
      sub: "Phiếu đã hoàn tất",
      icon: "✅",
      bgColor: "bg-emerald-500/10 text-emerald-600 dark:text-emerald-400 border-emerald-200 dark:border-emerald-900",
    },
    {
      title: "PHIẾU ĐANG QUÁ HẠN",
      value: summary?.totalOverdue ?? 0,
      sub: `${summary?.totalBorrowing ?? 0} đang mượn`,
      icon: "⚠️",
      bgColor: "bg-amber-500/10 text-amber-600 dark:text-amber-400 border-amber-200 dark:border-amber-900",
    },
    {
      title: "TỔNG TIỀN PHẠT",
      value: formatCurrency(summary?.totalFineAmount),
      sub: "Tiền phạt quá hạn",
      icon: "💰",
      bgColor: "bg-rose-500/10 text-rose-600 dark:text-rose-400 border-rose-200 dark:border-rose-900",
    },
  ];

  return (
    <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-4 mb-6">
      {cards.map((card, idx) => (
        <div
          key={idx}
          className={`flex flex-col justify-between p-4 rounded-xl border transition-all hover:shadow-md ${card.bgColor}`}
        >
          <div className="flex items-center justify-between">
            <span className="text-xs font-semibold tracking-wider uppercase opacity-80">{card.title}</span>
            <span className="text-xl">{card.icon}</span>
          </div>
          <div className="mt-2">
            <div className="text-2xl font-bold tracking-tight">{card.value}</div>
            <div className="text-xs mt-1 opacity-75">{card.sub}</div>
          </div>
        </div>
      ))}
    </div>
  );
};
