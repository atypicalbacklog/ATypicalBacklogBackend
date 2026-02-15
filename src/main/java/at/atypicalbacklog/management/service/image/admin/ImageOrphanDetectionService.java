package at.atypicalbacklog.management.service.image.admin;

import at.atypicalbacklog.management.domain.image.GameImage;
import at.atypicalbacklog.management.dto.image.admin.OrphanImageReportDto;
import at.atypicalbacklog.management.image.storage.ImageStorage;
import at.atypicalbacklog.management.repository.*;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Service
public class ImageOrphanDetectionService {

    private final GameImageRepository imageRepo;
    private final GameRepository gameRepo;
    private final GameInstanceRepository instanceRepo;
    private final DlcRepository dlcRepo;
    private final ImageStorage storage;

    public ImageOrphanDetectionService(
        GameImageRepository imageRepo,
        GameRepository gameRepo,
        GameInstanceRepository instanceRepo,
        DlcRepository dlcRepo,
        ImageStorage storage
    ) {
        this.imageRepo = imageRepo;
        this.gameRepo = gameRepo;
        this.instanceRepo = instanceRepo;
        this.dlcRepo = dlcRepo;
        this.storage = storage;
    }

    public List<OrphanImageReportDto> scan() {

        List<OrphanImageReportDto> result = new ArrayList<>();

        for (GameImage img : imageRepo.findAll()) {

            boolean originalExists = Files.exists(Path.of(
                storage.resolveOriginalPath(img.getUid())
            ));

            boolean thumbnailExists = Files.exists(Path.of(
                storage.resolveThumbnailPath(img.getUid())
            ));

            boolean ownerExists = switch (img.getOwnerType()) {
                case GAME ->
                    img.getGameUid() != null
                        && gameRepo.existsById(img.getGameUid());
                case GAME_INSTANCE ->
                    img.getGameInstanceUid() != null
                        && instanceRepo.existsById(img.getGameInstanceUid());
                case DLC ->
                    img.getDlcUid() != null
                        && dlcRepo.existsById(img.getDlcUid());
            };

            if (!originalExists || !thumbnailExists || !ownerExists) {
                result.add(new OrphanImageReportDto(
                    img.getUid(),
                    img.getOwnerType(),
                    img.getGameUid(),
                    img.getGameInstanceUid(),
                    true,
                    originalExists,
                    thumbnailExists,
                    !ownerExists
                        ? "Referenced owner does not exist"
                        : "Missing image file(s)"
                ));
            }
        }

        return result;
    }
}
