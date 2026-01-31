package at.atypicalbacklog.management.controller;

import at.atypicalbacklog.management.dto.reference.ReferenceDataResponse;
import at.atypicalbacklog.management.service.ReferenceDataService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reference-data")
public class ReferenceDataController {

    private final ReferenceDataService service;

    public ReferenceDataController(ReferenceDataService service) {
        this.service = service;
    }

    @GetMapping
    public ReferenceDataResponse getAll() {
        return service.loadAll();
    }
}
