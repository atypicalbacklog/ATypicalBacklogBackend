package at.atypicalbacklog.management.repository;

import at.atypicalbacklog.management.domain.image.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GameImageRepository extends JpaRepository<GameImage, String> {

    /** All images linked to a game: game-level, instance-level (instances of this game), and DLC-level (DLCs of those instances). */
    @Query("""
        SELECT i FROM GameImage i
        WHERE i.gameUid = :gameUid
           OR i.gameInstanceUid IN (SELECT gi.uid FROM at.atypicalbacklog.management.domain.GameInstance gi WHERE gi.gameUid = :gameUid)
           OR i.dlcUid IN (SELECT d.uid FROM at.atypicalbacklog.management.domain.Dlc d WHERE d.gameInstanceUid IN (SELECT gi.uid FROM at.atypicalbacklog.management.domain.GameInstance gi WHERE gi.gameUid = :gameUid))
        ORDER BY i.type ASC, i.primaryImage DESC, i.creationDate ASC
        """)
    List<GameImage> findAllByGameUidOrInstanceOrDlcLinkedToGame(@Param("gameUid") String gameUid);

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

    List<GameImage> findByOwnerTypeAndTypeAndDlcUid(
        ImageOwnerType ownerType,
        ImageType type,
        String dlcUid
    );
}
