package at.atypicalbacklog.management.repository;

import at.atypicalbacklog.management.domain.image.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GameImageRepository extends JpaRepository<GameImage, String> {

    List<GameImage> findByOwnerTypeAndGameUidOrderByTypeAscPrimaryImageDescCreationDateAsc(
        ImageOwnerType ownerType,
        String gameUid
    );

    List<GameImage> findByOwnerTypeAndGameInstanceUidOrderByTypeAscPrimaryImageDescCreationDateAsc(
        ImageOwnerType ownerType,
        String gameInstanceUid
    );

    List<GameImage> findByOwnerTypeAndTypeAndGameUid(
        ImageOwnerType ownerType,
        ImageType type,
        String gameUid
    );

    List<GameImage> findByOwnerTypeAndTypeAndGameInstanceUid(
        ImageOwnerType ownerType,
        ImageType type,
        String gameInstanceUid
    );
}
