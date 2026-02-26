package at.atypicalbacklog.management.reference.dto;

import java.time.Instant;

public record ReferenceDataResponse(
    String version,
    Instant generatedAt,
    ReferenceDataPayload data
) {}

