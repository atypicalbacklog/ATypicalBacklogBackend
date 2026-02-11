package at.atypicalbacklog.management.importing.controller;

import at.atypicalbacklog.management.importing.ResolutionStatus;
import at.atypicalbacklog.management.importing.domain.StagedGame;
import at.atypicalbacklog.management.importing.domain.StagedPlaythroughGroup;
import at.atypicalbacklog.management.importing.dto.StagedGameDetailDto;
import at.atypicalbacklog.management.importing.dto.StagedGameSummaryDto;
import at.atypicalbacklog.management.importing.dto.StagedPlaythroughGroupSummaryDto;
import at.atypicalbacklog.management.importing.dto.UpdateStagedGameRequest;
import at.atypicalbacklog.management.importing.dto.UpdateStagedGroupRequest;
import at.atypicalbacklog.management.importing.service.StagingCommandService;
import at.atypicalbacklog.management.importing.service.StagingQueryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/import")
public class StagingController {

    private final StagingCommandService commandService;
    private final StagingQueryService queryService;

    public StagingController(StagingCommandService commandService, StagingQueryService queryService) {
        this.commandService = commandService;
        this.queryService = queryService;
    }

    /** Update a StagedGame (including resolution status: UPDATED, CURATED, FINALIZED). */
    @PutMapping("/staged-games/{uid}")
    public StagedGame updateStagedGame(
        @PathVariable String uid,
        @RequestBody UpdateStagedGameRequest request
    ) {
        return commandService.updateStagedGame(uid, request);
    }

    /** Update a StagedPlaythroughGroup (including resolution status and optional stagedGameUid). */
    @PutMapping("/staged-playthrough-groups/{uid}")
    public StagedPlaythroughGroup updateStagedPlaythroughGroup(
        @PathVariable String uid,
        @RequestBody UpdateStagedGroupRequest request
    ) {
        return commandService.updateStagedPlaythroughGroup(uid, request);
    }

    /** Fetch StagedGame by uid with all linked StagedPlaythroughGroups. */
    @GetMapping("/staged-games/{uid}")
    public StagedGameDetailDto getStagedGame(@PathVariable String uid) {
        return queryService.getStagedGameByUid(uid);
    }

    /** List all StagedGames, optionally filtered by status (uids + names + status). */
    @GetMapping("/staged-games")
    public List<StagedGameSummaryDto> listStagedGamesByStatus(
        @RequestParam(required = false) ResolutionStatus status
    ) {
        return queryService.listStagedGamesByStatus(status);
    }

    /** List all StagedPlaythroughGroups that have no staged game assigned. */
    @GetMapping("/staged-playthrough-groups/unmapped")
    public List<StagedPlaythroughGroupSummaryDto> listUnmappedPlaythroughGroups() {
        return queryService.listPlaythroughGroupsWithoutStagedGame();
    }
}
