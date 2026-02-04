package at.atypicalbacklog.management.service.image;

import at.atypicalbacklog.management.domain.image.*;
import at.atypicalbacklog.management.dto.image.ImageMetadataDto;
import at.atypicalbacklog.management.repository.GameImageRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameImageQueryService {

    private final GameImageRepository repository;

    public GameImageQueryService(GameImageRepository repository) {
        this.repository = repository;
    }

    public List<ImageMetadataDto> listForGame(String gameUid) {
        return repository
            .findByOwnerTypeAndGameUidOrderByTypeAscPrimaryImageDescCreationDateAsc(
                ImageOwnerType.GAME,
                gameUid
            )
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
            img.getType(),
            img.getAspectHint(),
            img.getSource(),
            img.isPrimaryImage(),
            img.getFilename(),
            img.getMimetype()
        );
    }
}
