package at.atypicalbacklog.management.dto.image;

import at.atypicalbacklog.management.domain.image.*;

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
