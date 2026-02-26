package at.atypicalbacklog.management.statistics.controller;

import at.atypicalbacklog.management.statistics.dto.*;
import at.atypicalbacklog.management.statistics.service.*;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stats")
public class StatisticsController {

    private final YearlyActivityQueryService yearlyService;
    private final ConsoleYearActivityQueryService consoleYearService;
    private final ConsoleYearVariantActivityQueryService consoleYearVariantService;
    private final VariantYearActivityQueryService variantYearService;
    private final MonthlyPlaytimeQueryService monthlyService;
    private final WeeklyPlaytimeQueryService weeklyService;
    private final WeekdayPlaytimeQueryService weekdayService;
    private final ConsoleInputStyleQueryService consoleInputService;
    private final GameRankingQueryService rankingService;
    private final BookingDurationDistributionQueryService distributionService;
    private final BacklogCompletionGameOverviewQueryService backlogCompletionOverviewService;
    private final GamePlayedPerYearQueryService gamePlayedPerYearService;

    public StatisticsController(
        YearlyActivityQueryService yearlyService,
        ConsoleYearActivityQueryService consoleYearService,
        ConsoleYearVariantActivityQueryService consoleYearVariantService,
        VariantYearActivityQueryService variantYearService,
        MonthlyPlaytimeQueryService monthlyService,
        WeeklyPlaytimeQueryService weeklyService,
        WeekdayPlaytimeQueryService weekdayService,
        ConsoleInputStyleQueryService consoleInputService,
        GameRankingQueryService rankingService,
        BookingDurationDistributionQueryService distributionService,
        BacklogCompletionGameOverviewQueryService backlogCompletionOverviewService,
        GamePlayedPerYearQueryService gamePlayedPerYearService
    ) {
        this.yearlyService = yearlyService;
        this.consoleYearService = consoleYearService;
        this.consoleYearVariantService = consoleYearVariantService;
        this.variantYearService = variantYearService;
        this.monthlyService = monthlyService;
        this.weeklyService = weeklyService;
        this.weekdayService = weekdayService;
        this.consoleInputService = consoleInputService;
        this.rankingService = rankingService;
        this.distributionService = distributionService;
        this.backlogCompletionOverviewService = backlogCompletionOverviewService;
        this.gamePlayedPerYearService = gamePlayedPerYearService;
    }

    // --------------------------------------------------------
    // 1️⃣ Yearly Activity
    // --------------------------------------------------------

    @GetMapping("/yearly-activity")
    public List<YearlyActivityDto> yearlyActivity() {
        return yearlyService.getAll();
    }

    // --------------------------------------------------------
    // 2️⃣ Console Year Activity
    // --------------------------------------------------------

    @GetMapping("/console-year-activity")
    public List<ConsoleYearActivityDto> consoleYearActivity(
        @RequestParam int year
    ) {
        return consoleYearService.getForYear(year);
    }

    // --------------------------------------------------------
    // 2b Console Year Variant Activity
    // --------------------------------------------------------

    @GetMapping("/console-year-variant-activity")
    public List<ConsoleYearVariantActivityDto> consoleYearVariantActivity(
        @RequestParam int year
    ) {
        return consoleYearVariantService.getForYear(year);
    }

    // --------------------------------------------------------
    // 3️⃣ Variant Year Activity
    // --------------------------------------------------------

    @GetMapping("/variant-year-activity")
    public List<VariantYearActivityDto> variantYearActivity(
        @RequestParam int year
    ) {
        return variantYearService.getForYear(year);
    }

    // --------------------------------------------------------
    // 4️⃣ Monthly Playtime
    // --------------------------------------------------------

    @GetMapping("/monthly-playtime")
    public List<MonthlyPlaytimeDto> monthlyPlaytime(
        @RequestParam int year
    ) {
        return monthlyService.getForYear(year);
    }

    // --------------------------------------------------------
    // 5️⃣ Weekly Playtime
    // --------------------------------------------------------

    @GetMapping("/weekly-playtime")
    public List<WeeklyPlaytimeDto> weeklyPlaytime(
        @RequestParam int year
    ) {
        return weeklyService.getForYear(year);
    }

    // --------------------------------------------------------
    // 6️⃣ Weekday Playtime
    // --------------------------------------------------------

    @GetMapping("/weekday-playtime")
    public List<WeekdayPlaytimeDto> weekdayPlaytime(
        @RequestParam int year
    ) {
        return weekdayService.getForYear(year);
    }

    // --------------------------------------------------------
    // 7️⃣ Console Input Style Playtime
    // --------------------------------------------------------

    @GetMapping("/console-inputstyle-playtime")
    public List<ConsoleInputStyleDto> consoleInputStyle(
        @RequestParam int year
    ) {
        return consoleInputService.getForYear(year);
    }

    // --------------------------------------------------------
    // 8️⃣ Game Playtime Ranking
    // --------------------------------------------------------

    @GetMapping("/game-playtime-ranking")
    public List<GamePlaytimeRankingDto> gameRanking(
        @RequestParam(required = false) Integer fromYear,
        @RequestParam(required = false) Integer toYear
    ) {
        return rankingService.getRanking(fromYear, toYear);
    }

    // --------------------------------------------------------
    // 9️⃣ Booking Duration Distribution
    // --------------------------------------------------------

    @GetMapping("/booking-duration-distribution")
    public List<BookingDurationDistributionDto> bookingDistribution(
        @RequestParam String gameUid,
        @RequestParam(required = false) Integer fromYear,
        @RequestParam(required = false) Integer toYear
    ) {
        return distributionService.getDistribution(
            gameUid,
            fromYear,
            toYear
        );
    }

    // --------------------------------------------------------
    // 🔟 Backlog Completion Game Overview
    // --------------------------------------------------------

    @GetMapping("/backlog-completion-game-overview")
    public List<BacklogCompletionGameOverviewDto> backlogCompletionGameOverview(
        @RequestParam int year
    ) {
        return backlogCompletionOverviewService.getForYear(year);
    }

    // --------------------------------------------------------
    // 1️⃣1️⃣ Game Played Per Year
    // --------------------------------------------------------

    @GetMapping("/game-played-per-year")
    public List<GamePlayedPerYearDto> gamePlayedPerYear(@RequestParam int year) {
        return gamePlayedPerYearService.getForYear(year);
    }
}