package at.atypicalbacklog.management.dto.image;

import at.atypicalbacklog.management.domain.image.*;

public record UpdateImageMetadataRequest(

    ImageType type,
    ImageAspectHint aspectHint,
    Boolean primaryImage

) {}
