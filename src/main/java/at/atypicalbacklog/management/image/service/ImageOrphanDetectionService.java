package at.atypicalbacklog.management.image.service;

import at.atypicalbacklog.management.dlc.repository.DlcRepository;
import at.atypicalbacklog.management.game.repository.GameInstanceRepository;
import at.atypicalbacklog.management.game.repository.GameRepository;
import at.atypicalbacklog.management.image.domain.AdditionalImage;
import at.atypicalbacklog.management.image.domain.GameImage;
import at.atypicalbacklog.management.image.dto.OrphanImageReportDto;
import at.atypicalbacklog.management.image.repository.AdditionalImageRepository;
import at.atypicalbacklog.management.image.repository.GameImageRepository;
import at.atypicalbacklog.management.image.storage.ImageStorage;

import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Service
public class ImageOrphanDetectionService {

    public static final String KIND_GAME_IMAGE = "GAME_IMAGE";
    public static final String KIND_ADDITIONAL_IMAGE = "ADDITIONAL_IMAGE";

    private final GameImageRepository imageRepo;
    private final AdditionalImageRepository additionalImageRepo;
    private final GameRepository gameRepo;
    private final GameInstanceRepository instanceRepo;
    private final DlcRepository dlcRepo;
    private final ImageStorage storage;

    public ImageOrphanDetectionService(
        GameImageRepository imageRepo,
        AdditionalImageRepository additionalImageRepo,
        GameRepository gameRepo,
        GameInstanceRepository instanceRepo,
        DlcRepository dlcRepo,
        ImageStorage storage
    ) {
        this.imageRepo = imageRepo;
        this.additionalImageRepo = additionalImageRepo;
        this.gameRepo = gameRepo;
        this.instanceRepo = instanceRepo;
        this.dlcRepo = dlcRepo;
        this.storage = storage;
    }

    public List<OrphanImageReportDto> scan() {
        List<OrphanImageReportDto> result = new ArrayList<>();

        for (GameImage img : imageRepo.findAll()) {
            boolean originalExists = Files.exists(Path.of(storage.resolveOriginalPath(img.getUid())));
            boolean thumbnailExists = Files.exists(Path.of(storage.resolveThumbnailPath(img.getUid())));

            boolean ownerExists = switch (img.getOwnerType()) {
                case GAME ->
                    img.getGameUid() != null && gameRepo.existsById(img.getGameUid());
                case GAME_INSTANCE ->
                    img.getGameInstanceUid() != null && instanceRepo.existsById(img.getGameInstanceUid());
                case DLC ->
                    img.getDlcUid() != null && dlcRepo.existsById(img.getDlcUid());
            };

            if (!originalExists || !thumbnailExists || !ownerExists) {
                result.add(new OrphanImageReportDto(
                    img.getUid(),
                    KIND_GAME_IMAGE,
                    img.getOwnerType(),
                    img.getGameUid(),
                    img.getGameInstanceUid(),
                    true,
                    originalExists,
                    thumbnailExists,
                    !ownerExists ? "Referenced owner does not exist" : "Missing image file(s)"
                ));
            }
        }

        for (AdditionalImage img : additionalImageRepo.findAll()) {
            boolean originalExists = Files.exists(Path.of(storage.resolveOriginalPath(img.getUid())));
            boolean thumbnailExists = Files.exists(Path.of(storage.resolveThumbnailPath(img.getUid())));

            if (!originalExists || !thumbnailExists) {
                result.add(new OrphanImageReportDto(
                    img.getUid(),
                    KIND_ADDITIONAL_IMAGE,
                    null,
                    null,
                    null,
                    true,
                    originalExists,
                    thumbnailExists,
                    "Missing image file(s)"
                ));
            }
        }

        return result;
    }
}
