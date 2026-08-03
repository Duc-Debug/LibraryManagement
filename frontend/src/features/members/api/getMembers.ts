import type { Member } from '../types/member.types';

// Nguồn dữ liệu mock — sau này có thể thay bằng gọi API thật
export const mockMembers: Member[] = [
  {
    id: 'member1',
    name: 'Nguyễn Văn An',
    email: 'van.an@email.com',
    joinDate: '2024-01-15',
    status: 'active',
    borrowedBooksCount: 2,
  },
  {
    id: 'member2',
    name: 'Trần Thị Bình',
    email: 'thi.binh@email.com',
    joinDate: '2024-02-20',
    status: 'active',
    borrowedBooksCount: 1,
  },
  {
    id: 'member3',
    name: 'Lê Quốc Cường',
    email: 'quoc.cuong@email.com',
    joinDate: '2024-03-10',
    status: 'active',
    borrowedBooksCount: 3,
  },
  {
    id: 'member4',
    name: 'Phạm Minh Đức',
    email: 'minh.duc@email.com',
    joinDate: '2024-04-05',
    status: 'inactive',
    borrowedBooksCount: 0,
  },
  {
    id: 'member5',
    name: 'Vương Thu Hương',
    email: 'thu.huong@email.com',
    joinDate: '2024-05-12',
    status: 'active',
    borrowedBooksCount: 1,
  },
];

/** Lấy toàn bộ danh sách hội viên */
export async function getMembers(): Promise<Member[]> {
  return mockMembers;
}
