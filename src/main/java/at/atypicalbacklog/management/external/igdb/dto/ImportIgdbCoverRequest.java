package at.atypicalbacklog.management.external.igdb.dto;

import at.atypicalbacklog.management.image.domain.ImageOwnerType;

public record ImportIgdbCoverRequest(
    long externalId,
    ImageOwnerType ownerType,
    String gameUid,
    String gameInstanceUid,
    String dlcUid
) {}