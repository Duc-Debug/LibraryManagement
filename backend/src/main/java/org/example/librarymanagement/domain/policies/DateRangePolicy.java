package org.example.librarymanagement.domain.policies;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

import org.example.librarymanagement.domain.exceptions.report.InvalidDateRangeException;

public class DateRangePolicy {

    public static final int MAX_RANGE_DAYS = 365;
    public static final int DEFAULT_LOOKBACK_DAYS = 30;

    public record NormalizedDateRange(LocalDateTime startDateTime, LocalDateTime endDateTime) {
    }

    public static NormalizedDateRange validateAndNormalize(LocalDate startDate, LocalDate endDate) {
        LocalDate end = (endDate != null) ? endDate : LocalDate.now();
        LocalDate start = (startDate != null) ? startDate : end.minusDays(DEFAULT_LOOKBACK_DAYS);

        if (start.isAfter(end)) {
            throw new InvalidDateRangeException("Start date (" + start + ") cannot be after end date (" + end + ").");
        }

        long daysBetween = ChronoUnit.DAYS.between(start, end);
        if (daysBetween > MAX_RANGE_DAYS) {
            throw new InvalidDateRangeException("Date range cannot exceed " + MAX_RANGE_DAYS + " days.");
        }

        LocalDateTime startDateTime = start.atStartOfDay();
        LocalDateTime endDateTime = end.atTime(LocalTime.MAX);

        return new NormalizedDateRange(startDateTime, endDateTime);
    }
}
