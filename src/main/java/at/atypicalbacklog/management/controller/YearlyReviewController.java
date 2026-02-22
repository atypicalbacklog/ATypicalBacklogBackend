package at.atypicalbacklog.management.controller;

import at.atypicalbacklog.management.dto.review.yearly.UpdateYearlyReviewRequest;
import at.atypicalbacklog.management.dto.review.yearly.UpsertYearlyReviewCategoryRequest;
import at.atypicalbacklog.management.dto.review.yearly.UpsertYearlyReviewGameRequest;
import at.atypicalbacklog.management.dto.review.yearly.YearlyReviewDto;
import at.atypicalbacklog.management.service.review.query.YearlyReviewQueryService;
import at.atypicalbacklog.management.service.review.command.YearlyReviewCategoryCommandService;
import at.atypicalbacklog.management.service.review.command.YearlyReviewCommandService;
import at.atypicalbacklog.management.service.review.command.YearlyReviewGameCommandService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/yearly-review")
public class YearlyReviewController {

    private final YearlyReviewQueryService queryService;
    private final YearlyReviewCommandService reviewCommandService;
    private final YearlyReviewCategoryCommandService categoryService;
    private final YearlyReviewGameCommandService gameService;

    public YearlyReviewController(YearlyReviewQueryService queryService, YearlyReviewCommandService reviewCommandService, YearlyReviewCategoryCommandService categoryService, YearlyReviewGameCommandService gameService) {
        this.queryService = queryService;
        this.reviewCommandService = reviewCommandService;
        this.categoryService = categoryService;
        this.gameService = gameService;
    }

    @GetMapping
    public YearlyReviewDto get(@RequestParam int year) {
        return queryService.getOrCreate(year);
    }

    /** Updates introText and outroText for the yearly review with the given UID. Null request fields leave existing values unchanged. */
    @PutMapping("/{uid}")
    public ResponseEntity<Void> update(
        @PathVariable String uid,
        @RequestBody UpdateYearlyReviewRequest req
    ) {
        return reviewCommandService.updateIntroOutro(uid, req)
            ? ResponseEntity.noContent().build()
            : ResponseEntity.notFound().build();
    }

    @PostMapping("/category")
    public void upsertCategory(
        @RequestBody UpsertYearlyReviewCategoryRequest req
    ) {
        categoryService.upsert(req);
    }

    @PostMapping("/game")
    public void upsertGame(
        @RequestBody UpsertYearlyReviewGameRequest req
    ) {
        gameService.upsert(req);
    }

    /** Invalidates the category (sets valid = false). Returns true if the category was found and invalidated. */
    @PostMapping("/category/{uid}/invalidate")
    public boolean invalidateCategory(@PathVariable String uid) {
        return categoryService.invalidateByUid(uid);
    }

    /** Invalidates the game (sets valid = false). Returns true if the game was found and invalidated. */
    @PostMapping("/game/{uid}/invalidate")
    public boolean invalidateGame(@PathVariable String uid) {
        return gameService.invalidateByUid(uid);
    }
}