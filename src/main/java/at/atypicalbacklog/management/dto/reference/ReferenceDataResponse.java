package at.atypicalbacklog.management.dto.reference;

import java.time.Instant;

public record ReferenceDataResponse(
    String version,
    Instant generatedAt,
    ReferenceDataPayload data
) {}

