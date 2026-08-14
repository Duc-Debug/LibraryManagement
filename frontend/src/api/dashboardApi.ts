import { apiFetch } from "./httpClient";

export interface CategoryStatDto {
  categoryName: string;
  bookCount: number;
}

export interface DashboardStatisticsDto {
  totalTitles: number;
  totalCopies: number;
  availableCopies: number;
  borrowingCopies: number;
  totalReaders: number;
  activeReaders: number;
  totalCategories: number;
  overdueBorrowSlips: number;
  categoryDistribution: CategoryStatDto[];
}

export async function fetchDashboardStatisticsApi(): Promise<DashboardStatisticsDto> {
  return apiFetch<DashboardStatisticsDto>("/api/librarians/dashboard/statistics");
}
