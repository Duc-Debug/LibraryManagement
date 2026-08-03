export type UserRole = 'thu_thu' | 'nguoi_dung';

export interface UserAccount {
  id: string;
  username: string;
  password: string;
  fullName: string;
  role: UserRole;
  active: boolean;
}
