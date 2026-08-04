import type { ActivityLog } from '../types/dashboard.types';

// Nguồn dữ liệu mock — sau này có thể thay bằng gọi API thật hoặc tổng hợp real-time
export const mockActivityLogs: ActivityLog[] = [
  {
    id: 'log1',
    bookTitle: 'Sapiens',
    memberName: 'Trần Thị Bình',
    action: 'Trả sách',
    date: '2024-07-28',
    status: 'returned',
  },
  {
    id: 'log2',
    bookTitle: 'Tuổi trẻ đáng giá bao nhiêu',
    memberName: 'Nguyễn Văn An',
    action: 'Mượn sách',
    date: '2024-07-12',
    status: 'borrowing',
  },
  {
    id: 'log3',
    bookTitle: 'Đọc vị nhân tâm',
    memberName: 'Lê Quốc Cường',
    action: 'Quá hạn',
    date: '2024-07-14',
    status: 'overdue',
  },
  {
    id: 'log4',
    bookTitle: 'Những người thừa kế',
    memberName: 'Nguyễn Văn An',
    action: 'Mượn sách',
    date: '2024-07-10',
    status: 'borrowing',
  },
  {
    id: 'log5',
    bookTitle: 'Nghệ thuật chiến tranh',
    memberName: 'Vương Thu Hương',
    action: 'Trả sách',
    date: '2024-07-18',
    status: 'returned',
  },
];

/** Lấy danh sách nhật ký hoạt động gần đây */
export async function getActivityLogs(): Promise<ActivityLog[]> {
  return mockActivityLogs;
}
