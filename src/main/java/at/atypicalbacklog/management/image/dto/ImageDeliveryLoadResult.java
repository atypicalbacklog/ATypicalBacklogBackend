package at.atypicalbacklog.management.image.dto;

import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;

public record ImageDeliveryLoadResult(Resource resource, MediaType contentType) {

    public static MediaType mimetypeToMediaType(String mimetype) {
        if (mimetype == null || mimetype.isBlank()) {
            return MediaType.IMAGE_JPEG;
        }
        return switch (mimetype.toLowerCase()) {
            case "image/png" -> MediaType.IMAGE_PNG;
            case "image/gif" -> MediaType.IMAGE_GIF;
            case "image/webp" -> MediaType.parseMediaType("image/webp");
            default -> MediaType.IMAGE_JPEG;
        };
    }
}
