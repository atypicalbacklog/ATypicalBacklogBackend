package at.atypicalbacklog.management.image.service;

import at.atypicalbacklog.management.image.FilenameUtils;
import at.atypicalbacklog.management.image.domain.AdditionalImage;
import at.atypicalbacklog.management.image.domain.ImageAspectHint;
import at.atypicalbacklog.management.image.domain.ImageSource;
import at.atypicalbacklog.management.image.domain.ImageType;
import at.atypicalbacklog.management.image.dto.UpdateAdditionalImageMetadataRequest;
import at.atypicalbacklog.management.image.processing.ThumbnailGenerator;
import at.atypicalbacklog.management.image.repository.AdditionalImageRepository;
import at.atypicalbacklog.management.image.storage.ImageStorage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Service
public class AdditionalImageCommandService {

    private static final String STORAGE_NAME = "additional";

    private final AdditionalImageRepository repository;
    private final ImageStorage storage;
    private final ThumbnailGenerator thumbnails;

    public AdditionalImageCommandService(
        AdditionalImageRepository repository,
        ImageStorage storage,
        ThumbnailGenerator thumbnails
    ) {
        this.repository = repository;
        this.storage = storage;
        this.thumbnails = thumbnails;
    }

    public String upload(
        MultipartFile file,
        ImageType type,
        ImageAspectHint aspectHint,
        ImageSource source
    ) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("file is required");
        }
        if (type == null || aspectHint == null || source == null) {
            throw new IllegalArgumentException("type, aspectHint and source are required");
        }

        String uid = UUID.randomUUID().toString();
        String mimetype = file.getContentType() != null ? file.getContentType() : "image/jpeg";
        String filename = FilenameUtils.buildImageFilename(STORAGE_NAME, mimetype);
        String sanitizedName = FilenameUtils.sanitizeGameName(STORAGE_NAME);

        AdditionalImage image = new AdditionalImage(
            uid,
            filename,
            mimetype,
            type,
            source,
            aspectHint
        );

        try {
            byte[] imageData = file.getInputStream().readAllBytes();
            try (var originalStream = new ByteArrayInputStream(imageData)) {
                storage.storeOriginal(uid, originalStream, mimetype, sanitizedName);
            }
            try (var thumbInput = new ByteArrayInputStream(imageData);
                 var thumbnailStream = thumbnails.generateThumbnail(thumbInput)) {
                storage.storeThumbnail(uid, thumbnailStream, sanitizedName, "image/jpeg");
            }
        } catch (Exception e) {
            throw new IllegalStateException("Image processing failed", e);
        }

        repository.save(image);
        return uid;
    }

    @Transactional
    public void updateMetadata(String uid, UpdateAdditionalImageMetadataRequest req) {
        AdditionalImage image = repository.findById(uid)
            .orElseThrow(() -> new IllegalArgumentException("Additional image not found"));

        if (req.getType() != null) {
            image.setType(req.getType());
        }
        if (req.getAspectHint() != null) {
            image.setAspectHint(req.getAspectHint());
        }
        if (req.getSource() != null) {
            image.setSource(req.getSource());
        }

        repository.save(image);
    }

    @Transactional
    public void deleteByUid(String uid) {
        AdditionalImage image = repository.findById(uid)
            .orElseThrow(() -> new IllegalArgumentException("Additional image not found"));
        deleteFileIfExists(storage.resolveOriginalPath(uid));
        deleteFileIfExists(storage.resolveThumbnailPath(uid));
        repository.delete(image);
    }

    private void deleteFileIfExists(String path) {
        try {
            Files.deleteIfExists(Path.of(path));
        } catch (Exception e) {
            throw new IllegalStateException("Failed to delete file: " + path, e);
        }
    }
}
