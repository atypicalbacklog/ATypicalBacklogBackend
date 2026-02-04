package at.atypicalbacklog.management.dto.image.admin;

import at.atypicalbacklog.management.domain.image.ImageOwnerType;

public record OrphanImageReportDto(

    String imageUid,
    ImageOwnerType ownerType,
    String gameUid,
    String gameInstanceUid,

    boolean metadataExists,
    boolean originalFileExists,
    boolean thumbnailFileExists,

    String reason
) {}
