package at.atypicalbacklog.management.dto.external.igdb;

import at.atypicalbacklog.management.domain.image.ImageOwnerType;

public record ImportIgdbCoverRequest(
    long externalId,
    ImageOwnerType ownerType,
    String gameUid,
    String gameInstanceUid
) {}