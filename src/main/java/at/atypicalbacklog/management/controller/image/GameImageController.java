package at.atypicalbacklog.management.controller.image;

import at.atypicalbacklog.management.dto.image.*;
import at.atypicalbacklog.management.service.image.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/images")
public class GameImageController {

    private final GameImageQueryService queryService;
    private final GameImageCommandService commandService;

    public GameImageController(
        GameImageQueryService queryService,
        GameImageCommandService commandService
    ) {
        this.queryService = queryService;
        this.commandService = commandService;
    }

    /** List images for a Game */
    @GetMapping("/game/{gameUid}")
    public List<ImageMetadataDto> listForGame(@PathVariable String gameUid) {
        return queryService.listForGame(gameUid);
    }

    /** List images for a GameInstance */
    @Deprecated
    @GetMapping("/game-instance/{gameInstanceUid}")
    public List<ImageMetadataDto> listForInstance(
        @PathVariable String gameInstanceUid
    ) {
        return queryService.listForGameInstance(gameInstanceUid);
    }

    /** Update image metadata (all fields optional except source, filename, mimetype which are not updatable). */
    @PutMapping("/{uid}")
    public void updateMetadata(
        @PathVariable String uid,
        @RequestBody UpdateImageMetadataRequest req
    ) {
        commandService.updateMetadata(uid, req);
    }

    /** Delete image by UID (removes file(s) and DB record). */
    @DeleteMapping("/{uid}")
    public void delete(@PathVariable String uid) {
        commandService.deleteByUid(uid);
    }
}
