package at.atypicalbacklog.management.review.service;

import at.atypicalbacklog.management.completion.dto.GameCompletionInfoDto;
import at.atypicalbacklog.management.review.dto.LinkedDlcDto;
import at.atypicalbacklog.management.review.dto.ReviewSearchResultDto;
import at.atypicalbacklog.management.review.dto.ReviewSearchByYearRequest;
import at.atypicalbacklog.management.review.dto.PlaythroughWithReviewDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.*;

@Service
public class ReviewSearchQueryService {

    private static final ObjectMapper MAPPER = new ObjectMapper();

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
              pt.game_instance_uid,
              pt.input_uid,

              COALESCE(ROUND((
                SELECT SUM(EXTRACT(EPOCH FROM (tb.end_time - tb.start_time)))
                FROM time_booking tb
                WHERE tb.playthrough_uid = pt.uid
                  AND tb.start_time IS NOT NULL
                  AND tb.end_time IS NOT NULL
              ))::BIGINT, 0) AS total_playtime_seconds,

              (SELECT COALESCE(json_agg(
                 json_build_object('uid', d.uid, 'name', d.name)
                 ORDER BY d.creation_date
               ) FILTER (WHERE d.uid IS NOT NULL), '[]'::json)
               FROM dlc d
               WHERE d.playthrough_uid = pt.uid) AS linked_dlcs_json,

              (SELECT COALESCE(json_agg(
                 json_build_object(
                   'uid', gc.uid,
                   'gameInstanceUid', gc.game_instance_uid,
                   'dlcUid', gc.dlc_uid,
                   'completionStateUid', gc.completion_state_uid,
                   'info', gc.info,
                   'finishedDate', gc.finished_date,
                   'completedDate', gc.completed_date
                 )
                 ORDER BY gc.creation_date
               ) FILTER (WHERE gc.uid IS NOT NULL), '[]'::json)
               FROM game_completion gc
               WHERE gc.game_instance_uid = pt.game_instance_uid
                 AND (gc.dlc_uid IS NULL
                      OR gc.dlc_uid IN (SELECT d2.uid FROM dlc d2 WHERE d2.playthrough_uid = pt.uid))
              ) AS completions_json,

              r.uid AS review_uid,
              r.review_date,
              r.tier_uid,
              r.preferred_image_uid,
              r.presentation_type,
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
                        r.gameInstanceUid,
                        r.inputUid,
                        r.totalPlaytimeSeconds,
                        r.thumbnailUid,
                        r.reviewUid,
                        r.reviewDate,
                        r.tierUid,
                        r.preferredImageUid,
                        r.presentationType,
                        r.reviewText != null && !r.reviewText.isBlank(),
                        parseDlcs(r.linkedDlcsJson),
                        parseCompletions(r.completionsJson)
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
            rs.getString("game_instance_uid"),
            rs.getString("input_uid"),
            rs.getLong("total_playtime_seconds"),
            rs.getString("linked_dlcs_json"),
            rs.getString("completions_json"),
            rs.getString("review_uid"),
            rs.getObject("review_date", LocalDate.class),
            rs.getString("tier_uid"),
            rs.getString("preferred_image_uid"),
            rs.getString("presentation_type"),
            rs.getString("review_text")
        );
    }

    private List<LinkedDlcDto> parseDlcs(String json) {
        if (json == null || json.isBlank() || "[]".equals(json.trim())) {
            return List.of();
        }
        try {
            JsonNode arr = MAPPER.readTree(json);
            List<LinkedDlcDto> list = new ArrayList<>();
            for (JsonNode n : arr) {
                list.add(new LinkedDlcDto(text(n, "uid"), text(n, "name")));
            }
            return list;
        } catch (Exception e) {
            return List.of();
        }
    }

    private List<GameCompletionInfoDto> parseCompletions(String json) {
        if (json == null || json.isBlank() || "[]".equals(json.trim())) {
            return List.of();
        }
        try {
            JsonNode arr = MAPPER.readTree(json);
            List<GameCompletionInfoDto> list = new ArrayList<>();
            for (JsonNode n : arr) {
                list.add(new GameCompletionInfoDto(
                    text(n, "uid"),
                    text(n, "gameInstanceUid"),
                    text(n, "dlcUid"),
                    text(n, "completionStateUid"),
                    text(n, "info"),
                    date(n, "finishedDate"),
                    date(n, "completedDate")
                ));
            }
            return list;
        } catch (Exception e) {
            return List.of();
        }
    }

    private static String text(JsonNode node, String field) {
        return node.has(field) && !node.get(field).isNull() ? node.get(field).asText() : null;
    }

    private static LocalDate date(JsonNode node, String field) {
        String value = text(node, field);
        return value == null ? null : LocalDate.parse(value);
    }

    private record Row(
        String displayName,
        String thumbnailUid,
        String playthroughUid,
        String playthroughTypeUid,
        String playthroughStateUid,
        LocalDate startedAt,
        String gameInstanceUid,
        String inputUid,
        long totalPlaytimeSeconds,
        String linkedDlcsJson,
        String completionsJson,
        String reviewUid,
        LocalDate reviewDate,
        String tierUid,
        String preferredImageUid,
        String presentationType,
        String reviewText
    ) {}
}