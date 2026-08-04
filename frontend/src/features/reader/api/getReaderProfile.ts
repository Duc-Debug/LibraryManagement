import type { ReaderProfile } from '../types/reader.types';

// Nguồn dữ liệu mock — sau này có thể thay bằng gọi API thật
export const mockReaderProfiles: ReaderProfile[] = [
  {
    userId: 'U003',
    borrowLimit: 5,
    currentBorrows: 3,
    reservationLimit: 2,
    currentReservations: 1,
  },
];

/** Lấy hồ sơ độc giả theo userId */
export async function getReaderProfile(userId: string): Promise<ReaderProfile | undefined> {
  return mockReaderProfiles.find((p) => p.userId === userId);
}
