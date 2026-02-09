package at.atypicalbacklog.management.controller;

import at.atypicalbacklog.management.dto.review.command.*;
import at.atypicalbacklog.management.dto.review.query.*;
import at.atypicalbacklog.management.service.review.command.*;
import at.atypicalbacklog.management.service.review.query.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewSearchQueryService queryService;
    private final ReviewDetailQueryService detailQueryService;
    private final ReviewTierCommandService tierService;
    private final ReviewUpdateCommandService updateService;

    public ReviewController(
        ReviewSearchQueryService queryService,
        ReviewDetailQueryService detailQueryService,
        ReviewTierCommandService tierService,
        ReviewUpdateCommandService updateService
    ) {
        this.queryService = queryService;
        this.detailQueryService = detailQueryService;
        this.tierService = tierService;
        this.updateService = updateService;
    }

    @GetMapping("/by-game-instance/{gameInstanceUid}")
    public List<ReviewDetailDto> getByGameInstance(@PathVariable String gameInstanceUid) {
        return detailQueryService.getByGameInstanceUid(gameInstanceUid);
    }

    @GetMapping("/{uid}")
    public ReviewDetailDto getByUid(@PathVariable String uid) {
        return detailQueryService.getByUid(uid)
            .orElseThrow(() -> new IllegalArgumentException("Review not found"));
    }

    @PostMapping("/search-by-year")
    public List<ReviewSearchResultDto> search(
        @RequestBody ReviewSearchByYearRequest req
    ) {
        return queryService.search(req);
    }

    @PostMapping("/update-tier-bulk")
    public void updateTier(@RequestBody UpdateReviewTierBulkRequest req) {
        tierService.updateTier(req);
    }

    @PutMapping
    public void update(@RequestBody UpdateReviewRequest req) {
        updateService.update(req);
    }
}