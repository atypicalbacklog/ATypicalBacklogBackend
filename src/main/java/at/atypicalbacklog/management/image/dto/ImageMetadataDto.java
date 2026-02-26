package at.atypicalbacklog.management.image.dto;

import at.atypicalbacklog.management.image.domain.ImageAspectHint;
import at.atypicalbacklog.management.image.domain.ImageOwnerType;
import at.atypicalbacklog.management.image.domain.ImageSource;
import at.atypicalbacklog.management.image.domain.ImageType;

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
