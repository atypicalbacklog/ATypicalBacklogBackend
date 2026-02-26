package at.atypicalbacklog.management.dlc.controller;

import at.atypicalbacklog.management.dlc.dto.CreateDlcRequest;
import at.atypicalbacklog.management.dlc.dto.DlcResponse;
import at.atypicalbacklog.management.dlc.dto.UpdateDlcRequest;
import at.atypicalbacklog.management.dlc.service.DlcCommandService;
import at.atypicalbacklog.management.dlc.service.DlcQueryService;
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

    /** Update DLC */
    @PutMapping("/{uid}")
    public DlcResponse update(
        @PathVariable String uid,
        @RequestBody UpdateDlcRequest req
    ) {
        return commandService.update(uid, req);
    }

    /** Get DLCs by GameInstance UID */
    @GetMapping("/by-game-instance/{gameInstanceUid}")
    public List<DlcResponse> getByGameInstance(
        @PathVariable String gameInstanceUid
    ) {
        return queryService.getByGameInstanceUid(gameInstanceUid);
    }
}
