package at.atypicalbacklog.management.image.dto;

import at.atypicalbacklog.management.image.domain.ImageAspectHint;
import at.atypicalbacklog.management.image.domain.ImageOwnerType;
import at.atypicalbacklog.management.image.domain.ImageType;

/** All fields optional; only non-null applied. Source, filename, mimetype are not updatable. */
public record UpdateImageMetadataRequest(

    ImageOwnerType ownerType,
    String gameUid,
    String gameInstanceUid,
    String dlcUid,

    ImageType type,
    ImageAspectHint aspectHint,
    Boolean primaryImage

) {}
