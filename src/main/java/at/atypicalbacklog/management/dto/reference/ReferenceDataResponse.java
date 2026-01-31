package at.atypicalbacklog.management.dto.reference;

import java.time.Instant;
import java.util.List;

public record ReferenceDataResponse(
    String version,
    Instant generatedAt,
    ReferenceDataPayload data
) {}

