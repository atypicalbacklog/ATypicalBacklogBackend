package at.atypicalbacklog.management.service.image;

import at.atypicalbacklog.management.domain.image.GameImage;
import at.atypicalbacklog.management.domain.image.ImageAspectHint;
import at.atypicalbacklog.management.image.storage.ImageStorage;
import at.atypicalbacklog.management.repository.GameImageRepository;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
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
    public Resource load(String imageUid, Boolean thumbnail, ImageAspectHint aspectHint) {
        try {
            if (thumbnail == null) {
                return placeholders.loadPlaceholder(aspectHint != null ? aspectHint : ImageAspectHint.FREE);
            }

            if (imageUid == null) {
                return placeholders.loadPlaceholder(aspectHint != null ? aspectHint : ImageAspectHint.FREE);
            }

            GameImage img = repository.findById(imageUid).orElse(null);
            if (img == null) {
                return placeholders.loadPlaceholder(aspectHint != null ? aspectHint : ImageAspectHint.FREE);
            }

            if (thumbnail) {
                Path thumbPath = Path.of(storage.resolveThumbnailPath(imageUid));
                if (Files.exists(thumbPath)) {
                    return new InputStreamResource(Files.newInputStream(thumbPath));
                }
                Path origPath = Path.of(storage.resolveOriginalPath(imageUid));
                if (Files.exists(origPath)) {
                    return new InputStreamResource(Files.newInputStream(origPath));
                }
            } else {
                Path origPath = Path.of(storage.resolveOriginalPath(imageUid));
                if (Files.exists(origPath)) {
                    return new InputStreamResource(Files.newInputStream(origPath));
                }
            }

            return placeholders.loadPlaceholder(aspectHint != null ? aspectHint : ImageAspectHint.FREE);

        } catch (Exception e) {
            throw new IllegalStateException("Failed to load image", e);
        }
    }
}