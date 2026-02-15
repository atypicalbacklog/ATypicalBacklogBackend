package at.atypicalbacklog.management.controller.external;

import at.atypicalbacklog.management.dto.external.igdb.ImportIgdbCoverRequest;
import at.atypicalbacklog.management.service.external.igdb.IgdbImportService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/external/igdb")
public class IgdbImportController {

    private final IgdbImportService service;

    public IgdbImportController(IgdbImportService service) {
        this.service = service;
    }

    @PostMapping("/import-cover")
    public void importCover(@RequestBody ImportIgdbCoverRequest req) {
        service.importCover(
            req.externalId(),
            req.ownerType(),
            req.gameUid(),
            req.gameInstanceUid(),
            req.dlcUid()
        );
    }
}