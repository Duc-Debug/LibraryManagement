import type { Member } from '../types/member.types';

// Nguồn dữ liệu mock đã được dọn dẹp
export const mockMembers: Member[] = [];

/** Lấy toàn bộ danh sách hội viên */
export async function getMembers(): Promise<Member[]> {
  return mockMembers;
}
