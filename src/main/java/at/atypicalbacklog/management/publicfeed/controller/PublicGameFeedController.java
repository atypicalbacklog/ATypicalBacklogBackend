package at.atypicalbacklog.management.publicfeed.controller;

import at.atypicalbacklog.management.backlog.dto.BacklogOverviewGameDto;
import at.atypicalbacklog.management.publicfeed.dto.FeedCountRequest;
import at.atypicalbacklog.management.publicfeed.dto.PublicRandomBacklogRecommendationFeedItemDto;
import at.atypicalbacklog.management.publicfeed.dto.PublicSTierReviewFeedItemDto;
import at.atypicalbacklog.management.publicfeed.dto.RecentActivityFeedItemDto;
import at.atypicalbacklog.management.publicfeed.dto.RecentActivityFeedRequest;
import at.atypicalbacklog.management.publicfeed.service.PublicRandomBacklogFeedQueryService;
import at.atypicalbacklog.management.publicfeed.service.PublicRandomBacklogRecommendationFeedQueryService;
import at.atypicalbacklog.management.publicfeed.service.PublicRandomSTierReviewFeedQueryService;
import at.atypicalbacklog.management.publicfeed.service.PublicRecentActivityFeedQueryService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feed")
public class PublicGameFeedController {

    private final PublicRecentActivityFeedQueryService recentActivityService;
    private final PublicRandomBacklogFeedQueryService randomBacklogService;
    private final PublicRandomSTierReviewFeedQueryService randomSTierReviewService;
    private final PublicRandomBacklogRecommendationFeedQueryService randomRecommendationService;

    public PublicGameFeedController(
        PublicRecentActivityFeedQueryService recentActivityService,
        PublicRandomBacklogFeedQueryService randomBacklogService,
        PublicRandomSTierReviewFeedQueryService randomSTierReviewService,
        PublicRandomBacklogRecommendationFeedQueryService randomRecommendationService
    ) {
        this.recentActivityService = recentActivityService;
        this.randomBacklogService = randomBacklogService;
        this.randomSTierReviewService = randomSTierReviewService;
        this.randomRecommendationService = randomRecommendationService;
    }

    @PostMapping("/recent-activity")
    public List<RecentActivityFeedItemDto> recentActivity(
        @RequestBody RecentActivityFeedRequest req
    ) {
        return recentActivityService.query(req);
    }

    @PostMapping("/random-backlog")
    public List<BacklogOverviewGameDto> randomBacklog(@RequestBody FeedCountRequest req) {
        return randomBacklogService.query(req.count());
    }

    @PostMapping("/random-s-tier-reviews")
    public List<PublicSTierReviewFeedItemDto> randomSTierReviews(@RequestBody FeedCountRequest req) {
        return randomSTierReviewService.query(req.count());
    }

    @PostMapping("/random-backlog-recommendations")
    public List<PublicRandomBacklogRecommendationFeedItemDto> randomBacklogRecommendations(
        @RequestBody FeedCountRequest req
    ) {
        return randomRecommendationService.query(req.count());
    }
}
