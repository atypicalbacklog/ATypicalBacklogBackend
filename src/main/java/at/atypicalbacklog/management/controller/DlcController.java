package at.atypicalbacklog.management.controller;

import at.atypicalbacklog.management.dto.dlc.*;
import at.atypicalbacklog.management.service.dlc.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dlcs")
public class DlcController {

    private final DlcCommandService commandService;
    private final DlcQueryService queryService;

    public DlcController(
        DlcCommandService commandService,
        DlcQueryService queryService
    ) {
        this.commandService = commandService;
        this.queryService = queryService;
    }

    /** Create DLC */
    @PostMapping
    public DlcResponse create(@RequestBody CreateDlcRequest req) {
        return commandService.create(req);
    }

    /** Get DLCs by GameInstance UID */
    @GetMapping("/by-game-instance/{gameInstanceUid}")
    public List<DlcResponse> getByGameInstance(
        @PathVariable String gameInstanceUid
    ) {
        return queryService.getByGameInstanceUid(gameInstanceUid);
    }
}
