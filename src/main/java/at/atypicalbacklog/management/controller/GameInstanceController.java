package at.atypicalbacklog.management.controller;

import at.atypicalbacklog.management.dto.CreateGameInstanceRequest;
import at.atypicalbacklog.management.dto.GameInstanceDetailResponse;
import at.atypicalbacklog.management.dto.GameInstanceResponse;
import at.atypicalbacklog.management.dto.UpdateGameInstanceRequest;
import at.atypicalbacklog.management.service.GameInstanceDetailQueryService;
import at.atypicalbacklog.management.service.GameInstanceService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/game-instances")
public class GameInstanceController {

    private final GameInstanceService service;
    private final GameInstanceDetailQueryService detailQueryService;

    public GameInstanceController(GameInstanceService service, GameInstanceDetailQueryService detailQueryService) {
        this.detailQueryService = detailQueryService;
        this.service = service;
    }

    @PostMapping
    public GameInstanceResponse create(@RequestBody CreateGameInstanceRequest request) {
        return service.create(request);
    }

    //@GetMapping("/{uid}")
    //public GameInstanceResponse get(@PathVariable String uid) {
    //    return service.getById(uid);
    //}
    
    @GetMapping("/{uid}")
    public GameInstanceDetailResponse getDetail(@PathVariable String uid) {
        return detailQueryService.getByUid(uid);
    }


    @PutMapping("/{uid}")
    public GameInstanceResponse update(
        @PathVariable String uid,
        @RequestBody UpdateGameInstanceRequest request
    ) {
        return service.update(uid, request);
    }
}
