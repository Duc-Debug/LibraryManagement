package org.example.librarymanagement.port.dtos.dashboard;

import java.util.List;

public record DashboardStatisticsDto(
    long totalTitles,
    long totalCopies,
    long availableCopies,
    long borrowingCopies,
    long totalReaders,
    long activeReaders,
    long totalCategories,
    long overdueBorrowSlips,
    List<CategoryStatDto> categoryDistribution
) {}

