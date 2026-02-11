package at.atypicalbacklog.management.importing.parser.model;

import java.time.Duration;
import java.time.LocalDate;

public record ParsedTimeBookingRow(
    String gameName,
    LocalDate date,
    Duration duration,
    String hours
) {}