export type UserRole = 'admin' | 'thu_thu';

export interface UserAccount {
  id: string;
  username: string;
  password: string;
  fullName: string;
  email?: string;
  phone?: string;
  role: UserRole;
  active: boolean;
}
