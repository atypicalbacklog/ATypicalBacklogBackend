package at.atypicalbacklog.management.controller.image;

import at.atypicalbacklog.management.domain.image.*;
import at.atypicalbacklog.management.service.image.GameImageCommandService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/images")
public class GameImageUploadController {

    private final GameImageCommandService service;

    public GameImageUploadController(GameImageCommandService service) {
        this.service = service;
    }

    @PostMapping(consumes = "multipart/form-data")
    public void upload(
        @RequestParam("file") MultipartFile file,
        @RequestParam ImageOwnerType ownerType,
        @RequestParam(required = false) String gameUid,
        @RequestParam(required = false) String gameInstanceUid,
        @RequestParam(required = false) String dlcUid,
        @RequestParam ImageType type,
        @RequestParam ImageAspectHint aspectHint,
        @RequestParam ImageSource source
    ) {
        service.upload(
            file,
            ownerType,
            gameUid,
            gameInstanceUid,
            dlcUid,
            type,
            aspectHint,
            source
        );
    }
}
