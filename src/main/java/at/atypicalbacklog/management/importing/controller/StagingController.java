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
import java.util.Map;

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

    /** Search StagedGames by name (ILIKE). Returns list of StagedGameDetailDto. */
    @GetMapping("/staged-games/search")
    public List<StagedGameDetailDto> searchStagedGames(@RequestParam String name) {
        return queryService.searchStagedGamesByName(name);
    }

    /** List all StagedGames, optionally filtered by status (uids + names + status). */
    @GetMapping("/staged-games")
    public List<StagedGameSummaryDto> listStagedGamesByStatus(
        @RequestParam(required = false) ResolutionStatus status
    ) {
        return queryService.listStagedGamesByStatus(status);
    }

    /** Count of StagedGames per resolution status. Returns map status name -> count. */
    @GetMapping("/staged-games/counts-by-resolution-status")
    public Map<String, Integer> getStagedGameCountsByResolutionStatus() {
        return queryService.getStagedGameCountsByResolutionStatus();
    }

    /** Get StagedPlaythroughGroup by uid. */
    @GetMapping("/staged-playthrough-groups/{uid}")
    public StagedPlaythroughGroupSummaryDto getStagedPlaythroughGroup(@PathVariable String uid) {
        return queryService.getStagedPlaythroughGroupByUid(uid);
    }

    /** List StagedPlaythroughGroups by mapped/unmapped. unmapped=true → without staged game, unmapped=false → with staged game. */
    @GetMapping("/staged-playthrough-groups")
    public List<StagedPlaythroughGroupSummaryDto> listPlaythroughGroups(
        @RequestParam boolean unmapped
    ) {
        return queryService.listPlaythroughGroupsByUnmapped(unmapped);
    }
}
