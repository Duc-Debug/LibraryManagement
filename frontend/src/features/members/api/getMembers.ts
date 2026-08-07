import type { Member } from '../types/member.types';

// Nguồn dữ liệu thay bằng gọi API thật
export const mockMembers: Member[] = [
  {
    id: 'member1',
    name: 'Nguyễn Văn An',
    email: 'van.an@email.com',
    phone: '0901234567',
    joinDate: '2024-01-15',
    status: 'active',
    borrowedBooksCount: 2,
  },
  {
    id: 'member2',
    name: 'Trần Thị Bình',
    email: 'thi.binh@email.com',
    phone: '0912345678',
    joinDate: '2024-02-20',
    status: 'active',
    borrowedBooksCount: 1,
  },
  {
    id: 'member3',
    name: 'Lê Quốc Cường',
    email: 'quoc.cuong@email.com',
    phone: '0923456789',
    joinDate: '2024-03-10',
    status: 'active',
    borrowedBooksCount: 3,
  },
  {
    id: 'member4',
    name: 'Phạm Minh Đức',
    email: 'minh.duc@email.com',
    phone: '0934567890',
    joinDate: '2024-04-05',
    status: 'inactive',
    borrowedBooksCount: 0,
  },
  {
    id: 'member5',
    name: 'Vương Thu Hương',
    email: 'thu.huong@email.com',
    phone: '0945678901',
    joinDate: '2024-05-12',
    status: 'active',
    borrowedBooksCount: 1,
  },
];

/** Lấy toàn bộ danh sách hội viên */
export async function getMembers(): Promise<Member[]> {
  return mockMembers;
}