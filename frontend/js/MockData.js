window.mockData = {
  stats: {
    totalBooks: 29,
    availableBooks: 15,
    members: 5,
    activeMembers: 4,
    borrowed: 2,
    overdue: 1
  },
  recentActivities: [
    { id: 1, book: "Sapiens", member: "Trần Thị Bình", action: "Trả sách", date: "2024-07-28", status: "Đã trả", statusColor: "bg-green-100 text-green-700" },
    { id: 2, book: "Tuổi trẻ đáng giá bao nhiêu", member: "Nguyễn Văn An", action: "Mượn sách", date: "2024-07-12", status: "Đang mượn", statusColor: "bg-blue-100 text-blue-700" },
    { id: 3, book: "Đắc Nhân Tâm", member: "Lê Hoàng Nam", action: "Cảnh báo", date: "2024-06-30", status: "Quá hạn", statusColor: "bg-red-100 text-red-700" }
  ],
  books: [
    { id: "B001", title: "Sapiens: Lược Sử Loài Người", author: "Yuval Noah Harari", category: "Lịch sử", quantity: 5, available: 3 },
    { id: "B002", title: "Tuổi Trẻ Đáng Giá Bao Nhiêu", author: "Rosie Nguyễn", category: "Kỹ năng sống", quantity: 10, available: 8 },
    { id: "B003", title: "Đắc Nhân Tâm", author: "Dale Carnegie", category: "Tâm lý", quantity: 14, available: 4 }
  ]
};