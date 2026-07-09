package at.atypicalbacklog.management.game.controller;

import at.atypicalbacklog.management.backlogrecommendation.dto.UnifiedGameSearchResultDto;
import at.atypicalbacklog.management.backlogrecommendation.service.BacklogRecommendationQueryService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/query/games")
public class PublicGameQueryController {

    private final BacklogRecommendationQueryService unifiedSearchService;

    public PublicGameQueryController(BacklogRecommendationQueryService unifiedSearchService) {
        this.unifiedSearchService = unifiedSearchService;
    }

    @GetMapping("/unified")
    public List<UnifiedGameSearchResultDto> searchUnified(@RequestParam String q) {
        return unifiedSearchService.searchUnified(q);
    }
}
