package at.atypicalbacklog.management.image.dto;

import at.atypicalbacklog.management.image.domain.ImageOwnerType;

public record OrphanImageReportDto(

    String imageUid,
    /** GAME_IMAGE or ADDITIONAL_IMAGE */
    String imageKind,
    ImageOwnerType ownerType,
    String gameUid,
    String gameInstanceUid,

    boolean metadataExists,
    boolean originalFileExists,
    boolean thumbnailFileExists,

    String reason
) {}
