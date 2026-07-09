package at.atypicalbacklog.management.publicfeed.service;

import at.atypicalbacklog.management.config.validation.PublicInputValidation;
import at.atypicalbacklog.management.publicfeed.dto.RecentActivityFeedItemDto;
import at.atypicalbacklog.management.publicfeed.dto.RecentActivityFeedRequest;
import at.atypicalbacklog.management.publicfeed.service.PublicGameOverviewLoader.GameInstanceRef;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class PublicRecentActivityFeedQueryService {

    private static final String CATEGORY_BACKLOG_RECENT = "BACKLOG_RECENT";
    private static final String CATEGORY_ENDED_PLAYTHROUGH = "ENDED_PLAYTHROUGH";
    private static final String CATEGORY_RECENT_COMPLETION = "RECENT_COMPLETION";
    private static final String CATEGORY_RECENT_PLAY = "RECENT_PLAY";

    private final NamedParameterJdbcTemplate jdbc;
    private final PublicGameOverviewLoader overviewLoader;
    private final int recentActivityDays;

    public PublicRecentActivityFeedQueryService(
        NamedParameterJdbcTemplate jdbc,
        PublicGameOverviewLoader overviewLoader,
        @Value("${app.public-feed.recent-activity-days:90}") int recentActivityDays
    ) {
        this.jdbc = jdbc;
        this.overviewLoader = overviewLoader;
        this.recentActivityDays = recentActivityDays;
    }

    public List<RecentActivityFeedItemDto> query(RecentActivityFeedRequest req) {
        validate(req);

        LocalDate cutoffDate = LocalDate.now().minusDays(recentActivityDays);
        Instant cutoffInstant = Instant.now().minus(recentActivityDays, ChronoUnit.DAYS);

        int total = req.totalCount();
        int perCategory = req.maxPerCategory();
        Set<String> seenGameUids = new HashSet<>();
        List<SelectedRef> selected = new ArrayList<>();

        addCategory(selected, seenGameUids, total, perCategory,
            CATEGORY_BACKLOG_RECENT, fetchBacklogRecent(perCategory, cutoffDate));
        addCategory(selected, seenGameUids, total, perCategory,
            CATEGORY_ENDED_PLAYTHROUGH, fetchEndedPlaythroughs(perCategory, cutoffInstant));
        addCategory(selected, seenGameUids, total, perCategory,
            CATEGORY_RECENT_COMPLETION, fetchRecentCompletions(perCategory, cutoffDate));
        addCategory(selected, seenGameUids, total, perCategory,
            CATEGORY_RECENT_PLAY, fetchRecentPlay(perCategory, cutoffInstant));

        List<GameInstanceRef> refs = selected.stream()
            .map(s -> new GameInstanceRef(s.gameUid(), s.gameInstanceUid()))
            .toList();

        Map<String, at.atypicalbacklog.management.publicfeed.dto.PublicGameFeedRowDto> feedRows =
            overviewLoader.loadFeedRows(refs);

        List<RecentActivityFeedItemDto> result = new ArrayList<>();
        for (SelectedRef s : selected) {
            String key = s.gameInstanceUid() != null && !s.gameInstanceUid().isBlank()
                ? s.gameInstanceUid()
                : s.gameUid();
            var row = feedRows.get(key);
            if (row != null) {
                result.add(new RecentActivityFeedItemDto(s.category(), row));
            }
        }
        return result;
    }

    private void addCategory(
        List<SelectedRef> selected,
        Set<String> seenGameUids,
        int total,
        int perCategory,
        String category,
        List<CandidateRef> candidates
    ) {
        if (selected.size() >= total) {
            return;
        }
        int added = 0;
        for (CandidateRef candidate : candidates) {
            if (selected.size() >= total || added >= perCategory) {
                break;
            }
            if (seenGameUids.add(candidate.gameUid())) {
                selected.add(new SelectedRef(
                    category,
                    candidate.gameUid(),
                    candidate.gameInstanceUid()
                ));
                added++;
            }
        }
    }

    private List<CandidateRef> fetchBacklogRecent(int limit, LocalDate cutoffDate) {
        String sql = """
            SELECT b.game_uid, gi.uid AS game_instance_uid
            FROM backlog b
            JOIN game g ON g.uid = b.game_uid
            LEFT JOIN LATERAL (
              SELECT gi2.uid
              FROM game_instance gi2
              WHERE gi2.game_uid = g.uid
              ORDER BY gi2.creation_date DESC
              LIMIT 1
            ) gi ON true
            WHERE b.removed_date IS NULL
              AND b.added_date >= :cutoffDate
            ORDER BY b.added_date DESC
            LIMIT :limit
            """;
        return queryCandidateRefs(sql, cutoffParams(cutoffDate, limit));
    }

    private List<CandidateRef> fetchEndedPlaythroughs(int limit, Instant cutoffInstant) {
        String sql = """
            WITH playthrough_ranked AS (
              SELECT
                g.uid AS game_uid,
                gi.uid AS game_instance_uid,
                ps.name AS state_name,
                EXTRACT(YEAR FROM pt.started_at)::int AS play_year,
                (
                  SELECT MAX(tb.start_time)
                  FROM time_booking tb
                  WHERE tb.playthrough_uid = pt.uid
                ) AS last_booking_start,
                ROW_NUMBER() OVER (
                  PARTITION BY g.uid
                  ORDER BY ps.name ASC,
                    EXTRACT(YEAR FROM pt.started_at) DESC NULLS LAST,
                    (
                      SELECT MAX(tb.start_time)
                      FROM time_booking tb
                      WHERE tb.playthrough_uid = pt.uid
                    ) DESC NULLS LAST
                ) AS rn
              FROM playthrough pt
              JOIN playthrough_state ps ON ps.uid = pt.playthrough_state_uid
              JOIN game_instance gi ON gi.uid = pt.game_instance_uid
              JOIN game g ON g.uid = gi.game_uid
              WHERE ps.name != 'Active'
            )
            SELECT game_uid, game_instance_uid
            FROM playthrough_ranked
            WHERE rn = 1
              AND last_booking_start >= :cutoffInstant
            ORDER BY state_name ASC, play_year DESC NULLS LAST, last_booking_start DESC NULLS LAST
            LIMIT :limit
            """;
        return queryCandidateRefs(sql, cutoffParams(cutoffInstant, limit));
    }

    private List<CandidateRef> fetchRecentCompletions(int limit, LocalDate cutoffDate) {
        String sql = """
            WITH completion_ranked AS (
              SELECT
                g.uid AS game_uid,
                gi.uid AS game_instance_uid,
                gc.finished_date,
                gc.completed_date,
                COALESCE(gc.completed_date, gc.finished_date) AS completion_date,
                ROW_NUMBER() OVER (
                  PARTITION BY g.uid
                  ORDER BY COALESCE(gc.completed_date, gc.finished_date) DESC NULLS LAST
                ) AS rn
              FROM game_completion gc
              JOIN game_instance gi ON gi.uid = gc.game_instance_uid
              JOIN game g ON g.uid = gi.game_uid
              WHERE COALESCE(gc.completed_date, gc.finished_date) IS NOT NULL
                AND COALESCE(gc.completed_date, gc.finished_date) >= :cutoffDate
            )
            SELECT game_uid, game_instance_uid
            FROM completion_ranked
            WHERE rn = 1
            ORDER BY COALESCE(completed_date, finished_date) DESC
            LIMIT :limit
            """;
        return queryCandidateRefs(sql, cutoffParams(cutoffDate, limit));
    }

    private List<CandidateRef> fetchRecentPlay(int limit, Instant cutoffInstant) {
        String sql = """
            WITH booking_ranked AS (
              SELECT
                g.uid AS game_uid,
                gi.uid AS game_instance_uid,
                tb.start_time,
                ROW_NUMBER() OVER (
                  PARTITION BY g.uid
                  ORDER BY tb.start_time DESC
                ) AS rn
              FROM time_booking tb
              JOIN playthrough pt ON pt.uid = tb.playthrough_uid
              JOIN game_instance gi ON gi.uid = pt.game_instance_uid
              JOIN game g ON g.uid = gi.game_uid
              WHERE tb.start_time >= :cutoffInstant
            )
            SELECT game_uid, game_instance_uid
            FROM booking_ranked
            WHERE rn = 1
            ORDER BY start_time DESC
            LIMIT :limit
            """;
        return queryCandidateRefs(sql, cutoffParams(cutoffInstant, limit));
    }

    private List<CandidateRef> queryCandidateRefs(String sql, Map<String, Object> params) {
        return jdbc.query(
            sql,
            params,
            (rs, rowNum) -> new CandidateRef(
                rs.getString("game_uid"),
                rs.getString("game_instance_uid")
            )
        );
    }

    private Map<String, Object> cutoffParams(LocalDate cutoffDate, int limit) {
        Map<String, Object> params = new HashMap<>();
        params.put("cutoffDate", cutoffDate);
        params.put("limit", limit);
        return params;
    }

    private Map<String, Object> cutoffParams(Instant cutoffInstant, int limit) {
        Map<String, Object> params = new HashMap<>();
        params.put("cutoffInstant", Timestamp.from(cutoffInstant));
        params.put("limit", limit);
        return params;
    }

    private void validate(RecentActivityFeedRequest req) {
        if (req == null) {
            throw new IllegalArgumentException("request is required");
        }
        PublicInputValidation.requireRecentActivityCounts(req.totalCount(), req.maxPerCategory());
    }

    private record CandidateRef(
        String gameUid,
        String gameInstanceUid
    ) {}

    private record SelectedRef(
        String category,
        String gameUid,
        String gameInstanceUid
    ) {}
}
