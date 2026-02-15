package at.atypicalbacklog.management.service.image;

import at.atypicalbacklog.management.domain.Game;
import at.atypicalbacklog.management.domain.image.*;
import at.atypicalbacklog.management.dto.image.UpdateImageMetadataRequest;
import at.atypicalbacklog.management.image.FilenameUtils;
import at.atypicalbacklog.management.image.processing.ThumbnailGenerator;
import at.atypicalbacklog.management.image.storage.ImageStorage;
import at.atypicalbacklog.management.repository.GameImageRepository;
import at.atypicalbacklog.management.repository.GameInstanceRepository;
import at.atypicalbacklog.management.repository.GameRepository;
import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

@Service
public class GameImageCommandService {

    private final GameImageRepository repository;
    private final GameRepository gameRepository;
    private final GameInstanceRepository gameInstanceRepository;
    private final ImageStorage storage;
    private final ThumbnailGenerator thumbnails;

    public GameImageCommandService(
        GameImageRepository repository,
        GameRepository gameRepository,
        GameInstanceRepository gameInstanceRepository,
        ImageStorage storage,
        ThumbnailGenerator thumbnails
    ) {
        this.repository = repository;
        this.gameRepository = gameRepository;
        this.gameInstanceRepository = gameInstanceRepository;
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
        String gameName = resolveGameName(ownerType, gameUid, gameInstanceUid);
        String sanitizedGameName = FilenameUtils.sanitizeGameName(gameName);
        String mimetype = file.getContentType() != null ? file.getContentType() : "image/jpeg";
        String filename = FilenameUtils.buildImageFilename(gameName, mimetype);

        GameImage image = new GameImage(
            uid,
            filename,
            mimetype,
            ownerType,
            gameUid,
            gameInstanceUid,
            null,
            type,
            aspectHint,
            source,
            false // never primary by default
        );

        try {
            byte[] imageData = file.getInputStream().readAllBytes();
            try (var originalStream = new ByteArrayInputStream(imageData)) {
                storage.storeOriginal(uid, originalStream, mimetype, sanitizedGameName);
            }
            try (var thumbInput = new ByteArrayInputStream(imageData);
                 var thumbnailStream = thumbnails.generateThumbnail(thumbInput)) {
                storage.storeThumbnail(uid, thumbnailStream, sanitizedGameName, "image/jpeg");
            }
        } catch (Exception e) {
            throw new IllegalStateException("Image processing failed", e);
        }

        repository.save(image);
    }

    private String resolveGameName(ImageOwnerType ownerType, String gameUid, String gameInstanceUid) {
        if (ownerType == ImageOwnerType.GAME_INSTANCE && gameInstanceUid != null) {
            return gameInstanceRepository.findById(gameInstanceUid)
                .map(gi -> {
                    if (gi.getName() != null && !gi.getName().isBlank()) {
                        return gi.getName();
                    }
                    return gameRepository.findById(gi.getGameUid())
                        .map(Game::getName)
                        .orElse("image");
                })
                .orElseGet(() -> gameRepository.findById(gameUid).map(Game::getName).orElse("image"));
        }
        if (gameUid != null) {
            return gameRepository.findById(gameUid).map(Game::getName).orElse("image");
        }
        return "image";
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

        if (req.ownerType() != null) image.setOwnerType(req.ownerType());
        if (req.gameUid() != null) image.setGameUid(req.gameUid());
        if (req.gameInstanceUid() != null) image.setGameInstanceUid(req.gameInstanceUid());
        if (req.dlcUid() != null) image.setDlcUid(req.dlcUid());

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

        List<GameImage> siblings;
        if (image.getOwnerType() == ImageOwnerType.GAME && image.getGameUid() != null) {
            siblings = repository.findByOwnerTypeAndTypeAndGameUid(
                ImageOwnerType.GAME, type, image.getGameUid());
        } else if (image.getOwnerType() == ImageOwnerType.GAME_INSTANCE && image.getGameInstanceUid() != null) {
            siblings = repository.findByOwnerTypeAndTypeAndGameInstanceUid(
                ImageOwnerType.GAME_INSTANCE, type, image.getGameInstanceUid());
        } else if (image.getOwnerType() == ImageOwnerType.DLC && image.getDlcUid() != null) {
            siblings = repository.findByOwnerTypeAndTypeAndDlcUid(
                ImageOwnerType.DLC, type, image.getDlcUid());
        } else {
            siblings = List.of();
        }

        siblings.stream()
            .filter(GameImage::isPrimaryImage)
            .forEach(img -> img.setPrimaryImage(false));
    }

    @Transactional
    public void deleteByUid(String uid) {
        GameImage image = repository.findById(uid)
            .orElseThrow(() -> new IllegalArgumentException("Image not found"));
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
