import type { UserAccount } from '../types/account.types';

// Nguồn dữ liệu mock — sau này có thể thay bằng gọi API thật
export const mockUserAccounts: UserAccount[] = [
  {
    id: 'U001',
    username: 'admin',
    password: 'admin',
    fullName: 'Quản trị viên',
    role: 'admin',
    active: true,
  },
  {
    id: 'U002',
    username: 'thuthu',
    password: 'thuthu123',
    fullName: 'Nguyễn Thị Thu',
    role: 'thu_thu',
    active: true,
  },
  {
    id: 'U003',
    username: 'user1',
    password: 'user123',
    fullName: 'Trần Văn Bình',
    role: 'thu_thu',
    active: true,
  },
  {
    id: 'U004',
    username: 'user2',
    password: 'user456',
    fullName: 'Lê Thị Mai',
    role: 'thu_thu',
    active: false,
  },
];

/** Lấy toàn bộ danh sách tài khoản */
export async function getAccounts(): Promise<UserAccount[]> {
  return mockUserAccounts;
}
