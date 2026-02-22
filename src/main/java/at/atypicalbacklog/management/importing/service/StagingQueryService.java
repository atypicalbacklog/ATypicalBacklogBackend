package at.atypicalbacklog.management.importing.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import at.atypicalbacklog.management.importing.ResolutionStatus;
import at.atypicalbacklog.management.importing.domain.StagedGame;
import at.atypicalbacklog.management.importing.domain.StagedPlaythroughGroup;
import at.atypicalbacklog.management.importing.dto.StagedGameDetailDto;
import at.atypicalbacklog.management.importing.dto.StagedGameOverviewDto;
import at.atypicalbacklog.management.importing.dto.StagedGameSummaryDto;
import at.atypicalbacklog.management.importing.dto.StagedPlaythroughGroupSummaryDto;
import at.atypicalbacklog.management.importing.repository.StagedGameRepository;
import at.atypicalbacklog.management.importing.repository.StagedPlaythroughGroupRepository;

@Service
public class StagingQueryService {

    private final NamedParameterJdbcTemplate jdbc;
    private final StagedGameRepository gameRepo;
    private final StagedPlaythroughGroupRepository groupRepo;

    public StagingQueryService(
        NamedParameterJdbcTemplate jdbc,
        StagedGameRepository gameRepo,
        StagedPlaythroughGroupRepository groupRepo
    ) {
        this.jdbc = jdbc;
        this.gameRepo = gameRepo;
        this.groupRepo = groupRepo;
    }

    public List<StagedGameOverviewDto> listGames(String batchUid) {
        return jdbc.query("""
            SELECT uid, proposed_name,
                   proposed_platform_uid,
                   proposed_completion_state_uid,
                   proposed_finished_date,
                   resolution_status
            FROM staged_game
            WHERE import_batch_uid = :batch
        """,
        Map.of("batch", batchUid),
        (rs, i) -> new StagedGameOverviewDto(
            rs.getString("uid"),
            rs.getString("proposed_name"),
            rs.getString("proposed_platform_uid"),
            rs.getString("proposed_completion_state_uid"),
            rs.getObject("proposed_finished_date", java.time.LocalDate.class),
            rs.getString("resolution_status"),
            null
        ));
    }

    /** Fetch StagedGame by uid with all linked StagedPlaythroughGroups. */
    public StagedGameDetailDto getStagedGameByUid(String uid) {
        StagedGame game = gameRepo.findById(uid)
            .orElseThrow(() -> new IllegalArgumentException("StagedGame not found: " + uid));
        List<StagedPlaythroughGroup> groups = groupRepo.findByStagedGameUid(uid);
        List<StagedPlaythroughGroupSummaryDto> groupDtos = groups.stream()
            .map(g -> new StagedPlaythroughGroupSummaryDto(
                g.getUid(),
                g.getImportBatchUid(),
                g.getStagedGameUid(),
                g.getImportedPlaythroughName(),
                g.getInferredTypeUid(),
                g.getInferredYear(),
                g.getProposedPlaythroughStateUid(),
                g.getResolutionStatus()
            ))
            .collect(Collectors.toList());
        return new StagedGameDetailDto(
            game.getUid(),
            game.getImportBatchUid(),
            game.getProposedName(),
            game.getProposedReleaseDate(),
            game.getProposedReceivedDate(),
            game.getProposedVariantUid(),
            game.getProposedPlatformUid(),
            game.getMatchedGameUid(),
            game.getMatchedExternalIgdbId(),
            game.getMatchedOriginalConsoleUid(),
            game.getProposedCompletionStateUid(),
            game.getBacklogStartDate(),
            game.getBacklogEndDate(),
            game.getBacklogReason(),
            game.getProposedFinishedDate(),
            game.getMatchedGameInstanceUid(),
            game.getResolutionStatus(),
            groupDtos
        );
    }

    /** Search StagedGames by name (ILIKE on proposed_name, matched game/instance names). Returns StagedGameDetailDto list. */
    public List<StagedGameDetailDto> searchStagedGamesByName(String name) {
        if (name == null || name.isBlank()) {
            return List.of();
        }
        String pattern = "%" + name.trim() + "%";
        List<StagedGame> games = gameRepo.findAllByNameIlike(pattern);
        return games.stream()
            .map(g -> getStagedGameByUid(g.getUid()))
            .collect(Collectors.toList());
    }

    /** List all StagedGames, optionally filtered by status (uids + names + status). */
    public List<StagedGameSummaryDto> listStagedGamesByStatus(ResolutionStatus status) {
        List<StagedGame> games = status == null
            ? gameRepo.findAll()
            : gameRepo.findByResolutionStatus(status);
        return games.stream()
            .map(g -> new StagedGameSummaryDto(
                g.getUid(),
                g.getProposedName(),
                g.getResolutionStatus()
            ))
            .collect(Collectors.toList());
    }

    /** Get StagedPlaythroughGroup by uid. */
    public StagedPlaythroughGroupSummaryDto getStagedPlaythroughGroupByUid(String uid) {
        StagedPlaythroughGroup g = groupRepo.findById(uid)
            .orElseThrow(() -> new IllegalArgumentException("StagedPlaythroughGroup not found: " + uid));
        return new StagedPlaythroughGroupSummaryDto(
            g.getUid(),
            g.getImportBatchUid(),
            g.getStagedGameUid(),
            g.getImportedPlaythroughName(),
            g.getInferredTypeUid(),
            g.getInferredYear(),
            g.getProposedPlaythroughStateUid(),
            g.getResolutionStatus()
        );
    }

    /**
     * List StagedPlaythroughGroups by mapped/unmapped.
     * @param unmapped true = only groups without a staged game; false = only groups with a staged game
     */
    public List<StagedPlaythroughGroupSummaryDto> listPlaythroughGroupsByUnmapped(boolean unmapped) {
        List<StagedPlaythroughGroup> groups = unmapped
            ? groupRepo.findByStagedGameUidIsNull()
            : groupRepo.findByStagedGameUidIsNotNullAndResolutionStatusNot(ResolutionStatus.IMPORTED);
        return groups.stream()
            .map(g -> new StagedPlaythroughGroupSummaryDto(
                g.getUid(),
                g.getImportBatchUid(),
                g.getStagedGameUid(),
                g.getImportedPlaythroughName(),
                g.getInferredTypeUid(),
                g.getInferredYear(),
                g.getProposedPlaythroughStateUid(),
                g.getResolutionStatus()
            ))
            .collect(Collectors.toList());
    }

    /** Count of StagedGames per resolution status. Returns map status name -> count (all statuses included). */
    public Map<String, Integer> getStagedGameCountsByResolutionStatus() {
        Map<String, Integer> counts = new LinkedHashMap<>();
        for (ResolutionStatus status : ResolutionStatus.values()) {
            counts.put(status.name(), (int) gameRepo.countByResolutionStatus(status));
        }
        return counts;
    }
}