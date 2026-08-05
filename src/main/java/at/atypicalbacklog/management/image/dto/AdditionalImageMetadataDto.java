package at.atypicalbacklog.management.image.dto;

import at.atypicalbacklog.management.image.domain.ImageAspectHint;
import at.atypicalbacklog.management.image.domain.ImageSource;
import at.atypicalbacklog.management.image.domain.ImageType;

import java.time.Instant;

public record AdditionalImageMetadataDto(
    String uid,
    Instant creationDate,
    ImageType type,
    ImageAspectHint aspectHint,
    ImageSource source,
    String filename,
    String mimetype
) {}
