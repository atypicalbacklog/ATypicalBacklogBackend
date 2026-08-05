package at.atypicalbacklog.management.image.service;

import at.atypicalbacklog.management.image.domain.AdditionalImage;
import at.atypicalbacklog.management.image.domain.ImageAspectHint;
import at.atypicalbacklog.management.image.repository.AdditionalImageRepository;
import at.atypicalbacklog.management.image.storage.ImageStorage;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class AdditionalImageDeliveryService {

    private final AdditionalImageRepository repository;
    private final ImageStorage storage;
    private final PlaceholderImageService placeholders;

    public AdditionalImageDeliveryService(
        AdditionalImageRepository repository,
        ImageStorage storage,
        PlaceholderImageService placeholders
    ) {
        this.repository = repository;
        this.storage = storage;
        this.placeholders = placeholders;
    }

    /**
     * Same behaviour as game image delivery (17.1):
     * - thumbnail absent → placeholder
     * - thumbnail=false → original if exists, else placeholder
     * - thumbnail=true → thumbnail, else original, else placeholder
     */
    public Resource load(String imageUid, Boolean thumbnail, ImageAspectHint aspectHint) {
        try {
            if (thumbnail == null) {
                return placeholders.loadPlaceholder(aspectHint != null ? aspectHint : ImageAspectHint.FREE);
            }

            if (imageUid == null) {
                return placeholders.loadPlaceholder(aspectHint != null ? aspectHint : ImageAspectHint.FREE);
            }

            AdditionalImage img = repository.findById(imageUid).orElse(null);
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
            throw new IllegalStateException("Failed to load additional image", e);
        }
    }
}
