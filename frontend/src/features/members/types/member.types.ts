export interface Member {
  id: string;
  name: string;
  email: string;
  joinDate: string;
  status: 'active' | 'inactive';
  borrowedBooksCount: number;
}
