package at.atypicalbacklog.management.image.controller;

import at.atypicalbacklog.management.image.domain.ImageAspectHint;
import at.atypicalbacklog.management.image.domain.ImageOwnerType;
import at.atypicalbacklog.management.image.domain.ImageSource;
import at.atypicalbacklog.management.image.domain.ImageType;
import at.atypicalbacklog.management.image.service.GameImageCommandService;

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
