package at.atypicalbacklog.management.review.service;

import at.atypicalbacklog.management.review.dto.ReviewSearchResultDto;
import at.atypicalbacklog.management.review.dto.ReviewSearchByYearRequest;
import at.atypicalbacklog.management.review.dto.PlaythroughWithReviewDto;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.*;

@Service
public class ReviewSearchQueryService {

    private final NamedParameterJdbcTemplate jdbc;

    public ReviewSearchQueryService(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<ReviewSearchResultDto> search(ReviewSearchByYearRequest req) {

        String sql = """
            SELECT
              COALESCE(gi.name, g.name_base) AS display_name,

              COALESCE(
                (SELECT img.uid FROM game_image img
                 WHERE img.game_instance_uid = gi.uid
                   AND img.owner_type = 'GAME_INSTANCE'
                   AND img.type = 'COVER'
                 ORDER BY img.primary_image DESC NULLS LAST, img.creation_date DESC
                 LIMIT 1),
                (SELECT img.uid FROM game_image img
                 WHERE img.game_uid = g.uid
                   AND img.owner_type = 'GAME'
                   AND img.type = 'COVER'
                 ORDER BY img.primary_image DESC NULLS LAST, img.creation_date DESC
                 LIMIT 1)
              ) AS thumbnail_uid,

              pt.uid AS playthrough_uid,
              pt.playthrough_type_uid,
              pt.playthrough_state_uid,
              pt.started_at,

              r.uid AS review_uid,
              r.review_date,
              r.tier_uid,
              r.text AS review_text

            FROM playthrough pt
            JOIN game_instance gi ON gi.uid = pt.game_instance_uid
            JOIN game g ON g.uid = gi.game_uid

            LEFT JOIN review r ON r.playthrough_uid = pt.uid

            WHERE pt.year IN (:years)
        """;

        if (!req.includeActivePlaythroughs()) {
            sql += """
                AND NOT EXISTS (
                    SELECT 1 FROM playthrough_state ps
                    WHERE ps.uid = pt.playthrough_state_uid
                      AND ps.name = 'Active'
                )
                """;
        }

        List<Row> rows = jdbc.query(
            sql,
            Map.of("years", req.years()),
            this::mapRow
        );

        return group(rows);
    }

    private List<ReviewSearchResultDto> group(List<Row> rows) {

        Map<String, List<Row>> grouped = new LinkedHashMap<>();

        for (Row r : rows) {
            grouped.computeIfAbsent(r.displayName, k -> new ArrayList<>()).add(r);
        }

        List<ReviewSearchResultDto> result = new ArrayList<>();

        for (List<Row> group : grouped.values()) {
            Row first = group.get(0);

            result.add(new ReviewSearchResultDto(
                first.displayName,
                first.thumbnailUid,
                group.stream()
                    .map(r -> new PlaythroughWithReviewDto(
                        r.playthroughUid,
                        r.playthroughTypeUid,
                        r.playthroughStateUid,
                        r.startedAt,
                        r.reviewUid,
                        r.reviewDate,
                        r.tierUid,
                        r.reviewText != null && !r.reviewText.isBlank()
                    ))
                    .toList()
            ));
        }
        return result;
    }

    private Row mapRow(ResultSet rs, int i) throws java.sql.SQLException {
        return new Row(
            rs.getString("display_name"),
            rs.getString("thumbnail_uid"),
            rs.getString("playthrough_uid"),
            rs.getString("playthrough_type_uid"),
            rs.getString("playthrough_state_uid"),
            rs.getObject("started_at", LocalDate.class),
            rs.getString("review_uid"),
            rs.getObject("review_date", LocalDate.class),
            rs.getString("tier_uid"),
            rs.getString("review_text")
        );
    }

    private record Row(
        String displayName,
        String thumbnailUid,
        String playthroughUid,
        String playthroughTypeUid,
        String playthroughStateUid,
        LocalDate startedAt,
        String reviewUid,
        LocalDate reviewDate,
        String tierUid,
        String reviewText
    ) {}
}