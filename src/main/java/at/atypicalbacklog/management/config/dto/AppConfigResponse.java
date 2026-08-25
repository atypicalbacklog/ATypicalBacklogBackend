package at.atypicalbacklog.management.config.dto;

import java.time.Instant;

public record AppConfigResponse(
    String mode,
    /** Instant this JVM process started (UTC). Changes on restart/redeploy; use for SPA reload checks. */
    Instant serverStartedAt
) {}
