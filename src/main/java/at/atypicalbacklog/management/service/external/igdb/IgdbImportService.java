package at.atypicalbacklog.management.service.external.igdb;

import at.atypicalbacklog.management.domain.Game;
import at.atypicalbacklog.management.domain.image.*;
import at.atypicalbacklog.management.external.igdb.IgdbClient;
import at.atypicalbacklog.management.image.FilenameUtils;
import at.atypicalbacklog.management.image.processing.ThumbnailGenerator;
import at.atypicalbacklog.management.image.storage.ImageStorage;
import at.atypicalbacklog.management.repository.DlcRepository;
import at.atypicalbacklog.management.repository.GameImageRepository;
import at.atypicalbacklog.management.repository.GameInstanceRepository;
import at.atypicalbacklog.management.repository.GameRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.UUID;

@Service
public class IgdbImportService {

    private static final Logger log = LoggerFactory.getLogger(IgdbImportService.class);

    private final IgdbClient client;
    private final GameImageRepository repository;
    private final GameRepository gameRepository;
    private final GameInstanceRepository gameInstanceRepository;
    private final DlcRepository dlcRepository;
    private final ImageStorage storage;
    private final ThumbnailGenerator thumbnails;
    private final ObjectMapper mapper = new ObjectMapper();

    public IgdbImportService(
        IgdbClient client,
        GameImageRepository repository,
        GameRepository gameRepository,
        GameInstanceRepository gameInstanceRepository,
        DlcRepository dlcRepository,
        ImageStorage storage,
        ThumbnailGenerator thumbnails
    ) {
        this.client = client;
        this.repository = repository;
        this.gameRepository = gameRepository;
        this.gameInstanceRepository = gameInstanceRepository;
        this.dlcRepository = dlcRepository;
        this.storage = storage;
        this.thumbnails = thumbnails;
    }

    public void importCover(long externalId, ImageOwnerType ownerType,
                            String gameUid, String gameInstanceUid, String dlcUid) {
        log.info("Starting IGDB cover import: externalId={}, ownerType={}, gameUid={}, gameInstanceUid={}, dlcUid={}",
                externalId, ownerType, gameUid, gameInstanceUid, dlcUid);

        String body = """
            fields cover.url;
            where id = %d;
            """.formatted(externalId);

        log.debug("IGDB query body: {}", body);
        String response = client.post("/games", body);
        log.debug("IGDB API response: {}", response);

        try {
            JsonNode root = mapper.readTree(response);
            log.debug("Parsed JSON root node: {}", root);

            if (root == null || !root.isArray() || root.size() == 0) {
                log.error("IGDB API returned empty or invalid response for externalId={}", externalId);
                throw new IllegalStateException("IGDB API returned no results for game ID: " + externalId);
            }

            JsonNode game = root.get(0);
            log.debug("Parsed game node: {}", game);

            if (game == null || !game.has("cover")) {
                log.error("Game node missing or has no cover field for externalId={}", externalId);
                throw new IllegalStateException("Game has no cover image in IGDB for ID: " + externalId);
            }

            JsonNode coverNode = game.get("cover");
            log.debug("Cover node: {}", coverNode);

            if (!coverNode.has("url")) {
                log.error("Cover node missing url field for externalId={}", externalId);
                throw new IllegalStateException("Cover node missing URL for game ID: " + externalId);
            }

            String coverUrl = coverNode.get("url").asText();
            log.debug("Original cover URL from IGDB: {}", coverUrl);

            String originalUrl = coverUrl.replace("t_thumb", "t_original");
            String fullUrl = "https:" + originalUrl;
            log.info("Fetching cover image from URL: {}", fullUrl);

            // Read the entire image into memory first, since we need to use it twice
            byte[] imageData;
            try (InputStream urlStream = URI.create(fullUrl).toURL().openStream()) {
                imageData = urlStream.readAllBytes();
                log.info("Successfully downloaded image: {} bytes", imageData.length);
            } catch (IOException e) {
                log.error("Failed to download image from URL: {}", fullUrl, e);
                throw new IllegalStateException("Failed to download cover image from IGDB", e);
            }

            if (imageData == null || imageData.length == 0) {
                log.error("Downloaded image data is null or empty for URL: {}", fullUrl);
                throw new IllegalStateException("Downloaded image data is empty");
            }

            String uid = UUID.randomUUID().toString();
            log.debug("Generated image UID: {}", uid);

            String displayName = resolveDisplayNameForSave(ownerType, gameUid, gameInstanceUid, dlcUid);
            String sanitizedName = FilenameUtils.sanitizeGameName(displayName);
            String filename = FilenameUtils.buildImageFilename(displayName, "image/jpeg");

            GameImage image = new GameImage(
                uid,
                filename,
                "image/jpeg",
                ownerType,
                gameUid,
                gameInstanceUid,
                ownerType == ImageOwnerType.DLC ? dlcUid : null,
                ImageType.COVER,
                ImageAspectHint.VERTICAL,
                ImageSource.IMPORT,
                false
            );
            log.debug("Created GameImage entity: uid={}, ownerType={}", uid, ownerType);

            // Create separate InputStreams from the byte array for both operations
            try (InputStream originalStream = new ByteArrayInputStream(imageData)) {
                log.debug("Storing original image: uid={}, size={} bytes", uid, imageData.length);
                storage.storeOriginal(uid, originalStream, "image/jpeg", sanitizedName);
                log.info("Successfully stored original image: uid={}", uid);
            }

            try (InputStream thumbnailInput = new ByteArrayInputStream(imageData);
                 InputStream thumbnailStream = thumbnails.generateThumbnail(thumbnailInput)) {
                log.debug("Generating and storing thumbnail: uid={}", uid);
                storage.storeThumbnail(uid, thumbnailStream, sanitizedName, "image/jpeg");
                log.info("Successfully stored thumbnail: uid={}", uid);
            }

            log.debug("Saving GameImage entity to repository: uid={}", uid);
            repository.save(image);
            log.info("Successfully completed IGDB cover import: uid={}, externalId={}", uid, externalId);

        } catch (Exception e) {
            log.error("IGDB cover import failed: externalId={}, ownerType={}, gameUid={}, gameInstanceUid={}, dlcUid={}",
                    externalId, ownerType, gameUid, gameInstanceUid, dlcUid, e);
            throw new IllegalStateException("IGDB cover import failed", e);
        }
    }

    /**
     * Resolves the display name used for saving the image (filename base). For DLC uses Dlc.name; otherwise game or instance name.
     */
    private String resolveDisplayNameForSave(ImageOwnerType ownerType, String gameUid, String gameInstanceUid, String dlcUid) {
        if (ownerType == ImageOwnerType.DLC && dlcUid != null && !dlcUid.isBlank()) {
            return dlcRepository.findById(dlcUid)
                .map(dlc -> dlc.getName() != null && !dlc.getName().isBlank() ? dlc.getName() : "dlc")
                .orElse("dlc");
        }
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
}