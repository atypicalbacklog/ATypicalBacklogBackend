package at.atypicalbacklog.management.controller;

import at.atypicalbacklog.management.dto.backlog.*;
import at.atypicalbacklog.management.service.backlog.BacklogCommandService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/backlog")
public class BacklogController {

    private final BacklogCommandService service;

    public BacklogController(BacklogCommandService service) {
        this.service = service;
    }

    @PostMapping
    public void add(@RequestBody AddGameToBacklogRequest req) {
        service.add(req);
    }

    @PostMapping("/{gameUid}/remove")
    public void remove(
        @PathVariable String gameUid,
        @RequestBody RemoveGameFromBacklogRequest req
    ) {
        service.remove(gameUid, req);
    }

    @PutMapping("/{uid}/reason")
    public void updateReason(
        @PathVariable String uid,
        @RequestBody UpdateBacklogReasonRequest req
    ) {
        service.updateReason(uid, req);
    }
}
