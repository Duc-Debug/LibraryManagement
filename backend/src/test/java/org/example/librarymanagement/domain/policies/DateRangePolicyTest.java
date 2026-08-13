package org.example.librarymanagement.domain.policies;

import java.time.LocalDate;

import org.example.librarymanagement.domain.exceptions.report.InvalidDateRangeException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DateRangePolicyTest {

    @Test
    @DisplayName("Should pass validation when start date is before or equal to end date")
    void validateAndNormalize_ValidRange_ShouldPass() {
        LocalDate start = LocalDate.of(2026, 1, 1);
        LocalDate end = LocalDate.of(2026, 1, 10);

        DateRangePolicy.NormalizedDateRange result = DateRangePolicy.validateAndNormalize(start, end);

        assertNotNull(result);
        assertEquals(start.atStartOfDay(), result.startDateTime());
    }

    @Test
    @DisplayName("Should throw InvalidDateRangeException when start date is after end date")
    void validateAndNormalize_StartDateAfterEndDate_ShouldThrowException() {
        LocalDate start = LocalDate.of(2026, 8, 15);
        LocalDate end = LocalDate.of(2026, 8, 1);

        InvalidDateRangeException exception = assertThrows(
                InvalidDateRangeException.class,
                () -> DateRangePolicy.validateAndNormalize(start, end)
        );

        assertNotNull(exception.getMessage());
    }

    @Test
    @DisplayName("Should throw InvalidDateRangeException when range exceeds 365 days")
    void validateAndNormalize_Exceeds365Days_ShouldThrowException() {
        LocalDate start = LocalDate.of(2024, 1, 1);
        LocalDate end = LocalDate.of(2026, 1, 1);

        assertThrows(
                InvalidDateRangeException.class,
                () -> DateRangePolicy.validateAndNormalize(start, end)
        );
    }
}
