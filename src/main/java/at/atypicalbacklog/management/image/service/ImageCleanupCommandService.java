package at.atypicalbacklog.management.image.service;

import at.atypicalbacklog.management.audit.AuditRecorder;
import at.atypicalbacklog.management.image.domain.GameImage;
import at.atypicalbacklog.management.image.repository.GameImageRepository;
import at.atypicalbacklog.management.image.storage.ImageStorage;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

@Service
public class ImageCleanupCommandService {

    private final GameImageRepository repository;
    private final ImageStorage storage;
    private final AuditRecorder audit;

    public ImageCleanupCommandService(
        GameImageRepository repository,
        ImageStorage storage,
        AuditRecorder audit
    ) {
        this.repository = repository;
        this.storage = storage;
        this.audit = audit;
    }

    @Transactional
    public void deleteImages(Iterable<String> imageUids) {

        for (String uid : imageUids) {

            GameImage img = repository.findById(uid)
                .orElse(null);

            if (img != null) {
                deleteFileIfExists(storage.resolveOriginalPath(uid));
                deleteFileIfExists(storage.resolveThumbnailPath(uid));
                repository.delete(img);

                audit.record(
                    "Delete Orphan Image",
                    "GameImage",
                    "DELETE",
                    Map.of("imageUid", uid),
                    img,
                    null,
                    null
                );
            }
        }
    }

    private void deleteFileIfExists(String path) {
        try {
            Files.deleteIfExists(Path.of(path));
        } catch (Exception e) {
            throw new IllegalStateException("Failed to delete file: " + path, e);
        }
    }
}
