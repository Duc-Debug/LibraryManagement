import type { UserAccount } from '../types/account.types';

// Nguồn dữ liệu mock đã được dọn dẹp
export const mockUserAccounts: UserAccount[] = [];

/** Lấy toàn bộ danh sách tài khoản */
export async function getAccounts(): Promise<UserAccount[]> {
  return mockUserAccounts;
}
