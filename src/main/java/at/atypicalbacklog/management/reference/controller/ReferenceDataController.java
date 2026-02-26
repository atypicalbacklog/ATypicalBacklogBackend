package at.atypicalbacklog.management.reference.controller;

import at.atypicalbacklog.management.reference.dto.ReferenceDataResponse;
import at.atypicalbacklog.management.reference.service.ReferenceDataService;

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
