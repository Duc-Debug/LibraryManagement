export interface Member {
  id: string;
  name: string;
  email: string;
  phone?: string;
  joinDate: string;
  status: 'active' | 'inactive';
  borrowedBooksCount: number;
}