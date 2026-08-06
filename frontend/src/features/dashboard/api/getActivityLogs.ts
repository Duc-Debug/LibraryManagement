import type { ActivityLog } from '../types/dashboard.types';

// Nguồn dữ liệu mock đã được dọn dẹp
export const mockActivityLogs: ActivityLog[] = [];

/** Lấy danh sách nhật ký hoạt động gần đây */
export async function getActivityLogs(): Promise<ActivityLog[]> {
  return mockActivityLogs;
}
