package at.atypicalbacklog.management.backlogrecommendation.controller;

import at.atypicalbacklog.management.backlogrecommendation.dto.AddBacklogRecommendationCommentRequest;
import at.atypicalbacklog.management.backlogrecommendation.dto.BacklogRecommendationViewDto;
import at.atypicalbacklog.management.backlogrecommendation.dto.CreateBacklogRecommendationRequest;
import at.atypicalbacklog.management.backlogrecommendation.dto.CreateBacklogRecommendationResponse;
import at.atypicalbacklog.management.backlogrecommendation.service.BacklogRecommendationCommandService;
import at.atypicalbacklog.management.backlogrecommendation.service.BacklogRecommendationQueryService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/backlog-recommendations")
public class PublicBacklogRecommendationController {

    private final BacklogRecommendationCommandService commandService;
    private final BacklogRecommendationQueryService queryService;

    public PublicBacklogRecommendationController(
        BacklogRecommendationCommandService commandService,
        BacklogRecommendationQueryService queryService
    ) {
        this.commandService = commandService;
        this.queryService = queryService;
    }

    @PostMapping
    public CreateBacklogRecommendationResponse create(
        @RequestBody CreateBacklogRecommendationRequest req
    ) {
        return commandService.create(req);
    }

    @GetMapping
    public List<BacklogRecommendationViewDto> listAll() {
        return queryService.listAll();
    }

    @PostMapping("/{uid}/upvote")
    public void upvote(@PathVariable String uid) {
        commandService.upvote(uid);
    }

    @PostMapping("/{uid}/comments")
    public void addComment(
        @PathVariable String uid,
        @RequestBody AddBacklogRecommendationCommentRequest req
    ) {
        commandService.addComment(uid, req);
    }

    @GetMapping("/{uid}")
    public BacklogRecommendationViewDto getByUid(@PathVariable String uid) {
        return queryService.getByUid(uid);
    }
}
