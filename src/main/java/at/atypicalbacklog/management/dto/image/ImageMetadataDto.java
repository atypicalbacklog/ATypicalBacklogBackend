package at.atypicalbacklog.management.dto.image;

import at.atypicalbacklog.management.domain.image.*;

import java.time.Instant;

public record ImageMetadataDto(

    String uid,
    Instant creationDate,

    ImageOwnerType ownerType,
    String gameUid,
    String gameInstanceUid,
    String dlcUid,

    ImageType type,
    ImageAspectHint aspectHint,
    ImageSource source,

    boolean primaryImage,

    String filename,
    String mimetype
) {}
