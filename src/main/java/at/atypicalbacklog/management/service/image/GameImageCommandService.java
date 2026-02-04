package at.atypicalbacklog.management.service.image;

import at.atypicalbacklog.management.domain.image.*;
import at.atypicalbacklog.management.dto.image.UpdateImageMetadataRequest;
import at.atypicalbacklog.management.image.processing.ThumbnailGenerator;
import at.atypicalbacklog.management.image.storage.ImageStorage;
import at.atypicalbacklog.management.repository.GameImageRepository;
import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
public class GameImageCommandService {

    private final GameImageRepository repository;
    private final ImageStorage storage;
    private final ThumbnailGenerator thumbnails;

    public GameImageCommandService(
        GameImageRepository repository,
        ImageStorage storage,
        ThumbnailGenerator thumbnails
    ) {
        this.repository = repository;
        this.storage = storage;
        this.thumbnails = thumbnails;
    }

    public void upload(
        MultipartFile file,
        ImageOwnerType ownerType,
        String gameUid,
        String gameInstanceUid,
        ImageType type,
        ImageAspectHint aspectHint,
        ImageSource source
    ) {

        validateOwnership(ownerType, gameUid, gameInstanceUid);

        String uid = UUID.randomUUID().toString();

        GameImage image = new GameImage(
            uid,
            file.getOriginalFilename(),
            file.getContentType(),
            ownerType,
            gameUid,
            gameInstanceUid,
            type,
            aspectHint,
            source,
            false // never primary by default
        );

        try {
            storage.storeOriginal(uid, file.getInputStream());
            storage.storeThumbnail(uid, thumbnails.generateThumbnail(file.getInputStream()));
        } catch (Exception e) {
            throw new IllegalStateException("Image processing failed", e);
        }

        repository.save(image);
    }

    private void validateOwnership(
        ImageOwnerType ownerType,
        String gameUid,
        String gameInstanceUid
    ) {
        if (ownerType == ImageOwnerType.GAME && gameUid == null) {
            throw new IllegalArgumentException("gameUid required for GAME image");
        }
        if (ownerType == ImageOwnerType.GAME_INSTANCE && gameInstanceUid == null) {
            throw new IllegalArgumentException("gameInstanceUid required for GAME_INSTANCE image");
        }
    }

    @Transactional
    public void updateMetadata(String uid, UpdateImageMetadataRequest req) {

        GameImage image = repository.findById(uid)
            .orElseThrow(() -> new IllegalArgumentException("Image not found"));

        ImageType newType = req.type() != null ? req.type() : image.getType();

        // Handle primary change explicitly
        if (Boolean.TRUE.equals(req.primaryImage())) {
            unsetOtherPrimaries(image, newType);
            image.setPrimaryImage(true);
        } else if (Boolean.FALSE.equals(req.primaryImage())) {
            image.setPrimaryImage(false);
        }

        if (req.type() != null) image.setType(req.type());
        if (req.aspectHint() != null) image.setAspectHint(req.aspectHint());

        repository.save(image);
    }

    private void unsetOtherPrimaries(GameImage image, ImageType type) {

        List<GameImage> siblings =
            image.getOwnerType() == ImageOwnerType.GAME
                ? repository.findByOwnerTypeAndTypeAndGameUid(
                    ImageOwnerType.GAME,
                    type,
                    image.getGameUid()
                )
                : repository.findByOwnerTypeAndTypeAndGameInstanceUid(
                    ImageOwnerType.GAME_INSTANCE,
                    type,
                    image.getGameInstanceUid()
                );

        siblings.stream()
            .filter(GameImage::isPrimaryImage)
            .forEach(img -> img.setPrimaryImage(false));
    }
}
