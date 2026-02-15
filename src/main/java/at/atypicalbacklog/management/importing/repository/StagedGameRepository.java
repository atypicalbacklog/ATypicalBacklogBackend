package at.atypicalbacklog.management.importing.repository;

import at.atypicalbacklog.management.importing.domain.StagedGame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import at.atypicalbacklog.management.importing.ResolutionStatus;

import java.util.List;
import java.util.Optional;

public interface StagedGameRepository
        extends JpaRepository<StagedGame, String> {

    List<StagedGame> findByImportBatchUid(String batchUid);

    List<StagedGame> findByResolutionStatus(ResolutionStatus status);

    long countByResolutionStatus(ResolutionStatus status);

    /**
     * Finds a staged game by name using ILIKE search.
     * Searches in:
     * - staged_game.proposed_name
     * - game_instance.name (if matched_game_instance_uid is set)
     * - game.name_base (if matched_game_uid is set)
     * 
     * @param searchPattern search pattern (will be wrapped with % for LIKE)
     * @return first matching StagedGame, if any
     */
    @Query(value = """
        SELECT sg.* FROM staged_game sg
        LEFT JOIN game_instance gi ON gi.uid = sg.matched_game_instance_uid
        LEFT JOIN game g ON g.uid = sg.matched_game_uid
        WHERE 
          LOWER(sg.proposed_name) LIKE LOWER(:searchPattern)
          OR (sg.matched_game_instance_uid IS NOT NULL AND LOWER(gi.name) LIKE LOWER(:searchPattern))
          OR (sg.matched_game_uid IS NOT NULL AND LOWER(g.name_base) LIKE LOWER(:searchPattern))
        LIMIT 1
        """, nativeQuery = true)
    Optional<StagedGame> findByNameIlike(@Param("searchPattern") String searchPattern);

    /**
     * Finds staged games by name using ILIKE search (same sources as findByNameIlike).
     * @param searchPattern search pattern (typically "%" + name + "%" for contains)
     * @return list of matching StagedGames, limited to 100
     */
    @Query(value = """
        SELECT sg.* FROM staged_game sg
        LEFT JOIN game_instance gi ON gi.uid = sg.matched_game_instance_uid
        LEFT JOIN game g ON g.uid = sg.matched_game_uid
        WHERE 
          LOWER(sg.proposed_name) LIKE LOWER(:searchPattern)
          OR (sg.matched_game_instance_uid IS NOT NULL AND LOWER(gi.name) LIKE LOWER(:searchPattern))
          OR (sg.matched_game_uid IS NOT NULL AND LOWER(g.name_base) LIKE LOWER(:searchPattern))
        ORDER BY sg.proposed_name
        LIMIT 100
        """, nativeQuery = true)
    List<StagedGame> findAllByNameIlike(@Param("searchPattern") String searchPattern);

    /**
     * Finds an existing game UID by searching game names, game instance names, and DLC names.
     * Searches in:
     * - game.name_base
     * - game_instance.name
     * - dlc.name (returns game_uid from the instance that owns the DLC)
     * 
     * Always returns game_uid (not instance_uid or dlc_uid).
     * 
     * @param gameName search pattern (will be wrapped with % for LIKE)
     * @return first matching game UID, if any
     */
    @Query(value = """
        SELECT DISTINCT g.uid FROM game g
        WHERE LOWER(g.name_base) LIKE LOWER(:gameName)
        UNION
        SELECT DISTINCT gi.game_uid FROM game_instance gi
        WHERE LOWER(gi.name) LIKE LOWER(:gameName)
        UNION
        SELECT DISTINCT gi2.game_uid FROM dlc d
        JOIN game_instance gi2 ON gi2.uid = d.game_instance_uid
        WHERE LOWER(d.name) LIKE LOWER(:gameName)
        LIMIT 1
        """, nativeQuery = true)
    Optional<String> findExistingGameUidByNameIlike(@Param("gameName") String gameName);
}