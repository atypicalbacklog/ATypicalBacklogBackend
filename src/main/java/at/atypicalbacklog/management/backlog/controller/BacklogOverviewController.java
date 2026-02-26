package at.atypicalbacklog.management.backlog.controller;

import at.atypicalbacklog.management.backlog.dto.BacklogOverviewGameDto;
import at.atypicalbacklog.management.backlog.dto.BacklogOverviewQueryRequest;
import at.atypicalbacklog.management.backlog.service.BacklogOverviewQueryService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/query/backlog-overview")
public class BacklogOverviewController {

    private final BacklogOverviewQueryService service;

    public BacklogOverviewController(BacklogOverviewQueryService service) {
        this.service = service;
    }

    @PostMapping
    public List<BacklogOverviewGameDto> query(
        @RequestBody BacklogOverviewQueryRequest request
    ) {
        return service.query(request);
    }
}
