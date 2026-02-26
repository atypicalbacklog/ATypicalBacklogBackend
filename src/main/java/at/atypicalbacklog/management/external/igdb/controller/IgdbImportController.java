package at.atypicalbacklog.management.external.igdb.controller;

import at.atypicalbacklog.management.external.igdb.dto.ImportIgdbCoverRequest;
import at.atypicalbacklog.management.external.igdb.service.IgdbImportService;

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