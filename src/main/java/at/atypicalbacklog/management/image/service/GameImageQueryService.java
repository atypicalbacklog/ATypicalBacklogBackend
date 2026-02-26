package at.atypicalbacklog.management.image.service;

import at.atypicalbacklog.management.image.domain.GameImage;
import at.atypicalbacklog.management.image.domain.ImageOwnerType;
import at.atypicalbacklog.management.image.dto.ImageMetadataDto;
import at.atypicalbacklog.management.image.repository.GameImageRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameImageQueryService {

    private final GameImageRepository repository;

    public GameImageQueryService(GameImageRepository repository) {
        this.repository = repository;
    }

    /** List all images linked to the game: game-level, instance-level (instances of this game), and DLC-level (DLCs of those instances). */
    public List<ImageMetadataDto> listForGame(String gameUid) {
        return repository
            .findAllByGameUidOrInstanceOrDlcLinkedToGame(gameUid)
            .stream()
            .map(this::toDto)
            .toList();
    }

    public List<ImageMetadataDto> listForGameInstance(String gameInstanceUid) {
        return repository
            .findByOwnerTypeAndGameInstanceUidOrderByTypeAscPrimaryImageDescCreationDateAsc(
                ImageOwnerType.GAME_INSTANCE,
                gameInstanceUid
            )
            .stream()
            .map(this::toDto)
            .toList();
    }

    private ImageMetadataDto toDto(GameImage img) {
        return new ImageMetadataDto(
            img.getUid(),
            img.getCreationDate(),
            img.getOwnerType(),
            img.getGameUid(),
            img.getGameInstanceUid(),
            img.getDlcUid(),
            img.getType(),
            img.getAspectHint(),
            img.getSource(),
            img.isPrimaryImage(),
            img.getFilename(),
            img.getMimetype()
        );
    }
}
