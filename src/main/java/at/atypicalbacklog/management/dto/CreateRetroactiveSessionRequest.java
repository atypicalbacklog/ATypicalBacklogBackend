package at.atypicalbacklog.management.dto;

import java.time.LocalDate;

/**
 * Request to create a time booking after the fact (when user forgot to track live).
 * Start time is set to noon (12:00) on the given date; end time = start + minuteCount.
 * If that would cross midnight, start is shifted earlier so start and end stay on the same day.
 * minuteCount must be 1–600 (max 10 hours).
 */
public record CreateRetroactiveSessionRequest(
    LocalDate date,
    int minuteCount
) {}
