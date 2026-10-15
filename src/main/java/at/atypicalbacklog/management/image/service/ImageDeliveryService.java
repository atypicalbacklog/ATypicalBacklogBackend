package at.atypicalbacklog.management.image.service;

import at.atypicalbacklog.management.image.domain.GameImage;
import at.atypicalbacklog.management.image.domain.ImageAspectHint;
import at.atypicalbacklog.management.image.dto.ImageDeliveryLoadResult;
import at.atypicalbacklog.management.image.repository.GameImageRepository;
import at.atypicalbacklog.management.image.storage.ImageStorage;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class ImageDeliveryService {

    private final GameImageRepository repository;
    private final ImageStorage storage;
    private final PlaceholderImageService placeholders;

    public ImageDeliveryService(
        GameImageRepository repository,
        ImageStorage storage,
        PlaceholderImageService placeholders
    ) {
        this.repository = repository;
        this.storage = storage;
        this.placeholders = placeholders;
    }

    /**
     * Load image by UID with optional thumbnail preference.
     * - thumbnail=null (param absent): placeholder
     * - thumbnail=false: original if exists, else placeholder
     * - thumbnail=true: thumbnail if exists, else original if exists, else placeholder
     */
    public ImageDeliveryLoadResult load(String imageUid, Boolean thumbnail, ImageAspectHint aspectHint) {
        try {
            ImageAspectHint aspect = aspectHint != null ? aspectHint : ImageAspectHint.FREE;

            if (thumbnail == null) {
                return placeholder(aspect);
            }

            if (imageUid == null) {
                return placeholder(aspect);
            }

            GameImage img = repository.findById(imageUid).orElse(null);
            if (img == null) {
                return placeholder(aspect);
            }

            if (thumbnail) {
                Path thumbPath = Path.of(storage.resolveThumbnailPath(imageUid));
                if (Files.exists(thumbPath)) {
                    return file(thumbPath, MediaType.IMAGE_JPEG);
                }
                Path origPath = Path.of(storage.resolveOriginalPath(imageUid));
                if (Files.exists(origPath)) {
                    return file(origPath, ImageDeliveryLoadResult.mimetypeToMediaType(img.getMimetype()));
                }
            } else {
                Path origPath = Path.of(storage.resolveOriginalPath(imageUid));
                if (Files.exists(origPath)) {
                    return file(origPath, ImageDeliveryLoadResult.mimetypeToMediaType(img.getMimetype()));
                }
            }

            return placeholder(aspect);

        } catch (Exception e) {
            throw new IllegalStateException("Failed to load image", e);
        }
    }

    private ImageDeliveryLoadResult placeholder(ImageAspectHint aspect) {
        Resource resource = placeholders.loadPlaceholder(aspect);
        return new ImageDeliveryLoadResult(resource, MediaType.IMAGE_JPEG);
    }

    private ImageDeliveryLoadResult file(Path path, MediaType contentType) throws Exception {
        return new ImageDeliveryLoadResult(
            new InputStreamResource(Files.newInputStream(path)),
            contentType
        );
    }
}
