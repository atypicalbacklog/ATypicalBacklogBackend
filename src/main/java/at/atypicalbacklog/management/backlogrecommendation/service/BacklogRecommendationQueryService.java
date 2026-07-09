package at.atypicalbacklog.management.backlogrecommendation.service;

import at.atypicalbacklog.management.backlog.dto.BacklogInfoDto;
import at.atypicalbacklog.management.backlogrecommendation.domain.BacklogRecommendationState;
import at.atypicalbacklog.management.backlogrecommendation.dto.BacklogRecommendationCommentDto;
import at.atypicalbacklog.management.backlogrecommendation.dto.BacklogRecommendationViewDto;
import at.atypicalbacklog.management.backlogrecommendation.dto.GameRecommendationDto;
import at.atypicalbacklog.management.backlogrecommendation.dto.UnifiedGameSearchCompletionDto;
import at.atypicalbacklog.management.backlogrecommendation.dto.UnifiedGameSearchResultDto;
import at.atypicalbacklog.management.completion.dto.GameCompletionInfoDto;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
public class BacklogRecommendationQueryService {

    private static final int LIST_ALL_MAX_RESULTS = 500;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final String COVER_IMAGE_SUBQUERY = """
        (
          SELECT img.uid
          FROM game_image img
          WHERE img.game_uid = g.uid
            AND img.owner_type = 'GAME'
            AND img.type = 'COVER'
          ORDER BY img.primary_image DESC NULLS LAST, img.creation_date DESC
          LIMIT 1
        )
        """;

    private static final String COMPLETIONS_JSON_SUBQUERY = """
        (
          SELECT COALESCE(json_agg(
            json_build_object(
              'uid', gc.uid,
              'gameInstanceUid', gc.game_instance_uid,
              'dlcUid', gc.dlc_uid,
              'completionStateUid', gc.completion_state_uid,
              'info', gc.info,
              'finishedDate', gc.finished_date,
              'completedDate', gc.completed_date
            ) ORDER BY gc.creation_date
          ), '[]'::json)
          FROM game_completion gc
          JOIN game_instance gi ON gi.uid = gc.game_instance_uid
          WHERE gi.game_uid = g.uid
        )
        """;

    private static final String INSTANCE_COVER_IMAGE_SUBQUERY = """
        COALESCE(
          (
            SELECT img.uid
            FROM game_image img
            WHERE img.game_instance_uid = gi.uid
              AND img.owner_type = 'GAME_INSTANCE'
              AND img.type = 'COVER'
            ORDER BY img.primary_image DESC NULLS LAST, img.creation_date DESC
            LIMIT 1
          ),
          (
            SELECT img.uid
            FROM game_image img
            WHERE img.game_uid = gi.game_uid
              AND img.owner_type = 'GAME'
              AND img.type = 'COVER'
            ORDER BY img.primary_image DESC NULLS LAST, img.creation_date DESC
            LIMIT 1
          )
        )
        """;

    private static final String UNIFIED_COMPLETIONS_JSON_SUBQUERY = """
        (
          SELECT COALESCE(json_agg(
            json_build_object(
              'uid', gc.uid,
              'gameInstanceUid', gc.game_instance_uid,
              'dlcUid', gc.dlc_uid,
              'completionStateUid', gc.completion_state_uid,
              'info', gc.info,
              'finishedDate', gc.finished_date,
              'completedDate', gc.completed_date,
              'platformUid', gi.platform_uid,
              'platformName', p.name,
              'coverImageUid', """ + INSTANCE_COVER_IMAGE_SUBQUERY + """
            ) ORDER BY gc.creation_date
          ), '[]'::json)
          FROM game_completion gc
          JOIN game_instance gi ON gi.uid = gc.game_instance_uid
          LEFT JOIN platform p ON p.uid = gi.platform_uid
          WHERE gi.game_uid = g.uid
        )
        """;

    private static final String ACTIVE_BACKLOG_JSON_SUBQUERY = """
        (
          SELECT json_build_object(
            'uid', b.uid,
            'addedDate', b.added_date,
            'removedDate', b.removed_date,
            'reason', b.reason
          )
          FROM backlog b
          WHERE b.game_uid = g.uid
            AND b.removed_date IS NULL
          LIMIT 1
        )
        """;

    private static final String COMMENTS_JSON_SUBQUERY = """
        (
          SELECT COALESCE(json_agg(
            json_build_object(
              'uid', c.uid,
              'creationDate', c.creation_date,
              'comment', c.comment
            ) ORDER BY c.creation_date ASC
          ), '[]'::json)
          FROM backlog_recommendation_comment c
          WHERE c.backlog_recommendation_uid = br.uid
        )
        """;

    private static final String BACKLOG_WHEN_LINKED_JSON = """
        CASE
          WHEN br.game_uid IS NOT NULL THEN (
            SELECT json_build_object(
              'uid', b.uid,
              'addedDate', b.added_date,
              'removedDate', b.removed_date,
              'reason', b.reason
            )
            FROM backlog b
            WHERE b.game_uid = br.game_uid
              AND b.removed_date IS NULL
            LIMIT 1
          )
          ELSE NULL
        END
        """;

    private static final String COMPLETIONS_FOR_LINKED_GAME_JSON = """
        (
          SELECT COALESCE(json_agg(
            json_build_object(
              'uid', gc.uid,
              'gameInstanceUid', gc.game_instance_uid,
              'dlcUid', gc.dlc_uid,
              'completionStateUid', gc.completion_state_uid,
              'info', gc.info,
              'finishedDate', gc.finished_date,
              'completedDate', gc.completed_date
            ) ORDER BY gc.creation_date
          ), '[]'::json)
          FROM game_completion gc
          JOIN game_instance gi ON gi.uid = gc.game_instance_uid
          WHERE gi.game_uid = br.game_uid
        )
        """;

    private static final String COMPLETIONS_WHEN_HAS_INSTANCE_JSON = """
        CASE
          WHEN br.game_uid IS NOT NULL AND EXISTS (
            SELECT 1 FROM game_instance gi WHERE gi.game_uid = br.game_uid
          ) THEN """
        + COMPLETIONS_FOR_LINKED_GAME_JSON + """
          ELSE '[]'::json
        END
        """;

    private static final String LINKED_GAME_IMAGE_SUBQUERY = """
        CASE
          WHEN br.game_uid IS NOT NULL THEN COALESCE(
            (
              SELECT img.uid
              FROM game_image img
              WHERE img.game_uid = br.game_uid
                AND img.owner_type = 'GAME'
                AND img.type = 'COVER'
              ORDER BY img.primary_image DESC NULLS LAST, img.creation_date DESC
              LIMIT 1
            ),
            (
              SELECT img.uid
              FROM game_image img
              JOIN game_instance gi ON gi.uid = img.game_instance_uid
              WHERE gi.game_uid = br.game_uid
                AND img.owner_type = 'GAME_INSTANCE'
                AND img.type = 'COVER'
              ORDER BY img.primary_image DESC NULLS LAST, gi.creation_date ASC, img.creation_date DESC
              LIMIT 1
            )
          )
          ELSE NULL
        END
        """;

    private static final String RECOMMENDATION_VIEW_SELECT = """
        br.uid,
        br.creation_date,
        br.name,
        br.state,
        br.state_reason,
        br.state_date,
        br.votecount,
        br.original_recommendation_by,
        br.game_uid,
        gr.uid AS gr_uid,
        gr.creation_date AS gr_creation_date,
        gr.name_base AS gr_name_base,
        gr.release_date AS gr_release_date,
        gr.external_igdb_id AS gr_external_igdb_id,
        """
        + COMMENTS_JSON_SUBQUERY + " AS comments_json,\n"
        + BACKLOG_WHEN_LINKED_JSON + " AS backlog_json,\n"
        + COMPLETIONS_WHEN_HAS_INSTANCE_JSON + " AS completions_json,\n"
        + LINKED_GAME_IMAGE_SUBQUERY + " AS linked_game_image_uid,\n";

    private final NamedParameterJdbcTemplate jdbc;

    public BacklogRecommendationQueryService(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public BacklogRecommendationViewDto getByUid(String uid) {
        String sql = """
            WITH ranked AS (
              SELECT
            """
            + RECOMMENDATION_VIEW_SELECT
            + """
                RANK() OVER (ORDER BY br.votecount DESC, br.creation_date ASC) AS vote_rank
              FROM backlog_recommendation br
              JOIN game_recommendation gr ON gr.uid = br.game_recommendation_uid
              WHERE br.uid = :uid
            )
            SELECT *
            FROM ranked
            """;

        List<BacklogRecommendationViewDto> results = jdbc.query(
            sql,
            Map.of("uid", uid),
            (rs, rowNum) -> mapViewRow(rs)
        );

        if (results.isEmpty()) {
            throw new IllegalArgumentException("BacklogRecommendation not found");
        }

        return results.get(0);
    }

    public List<BacklogRecommendationViewDto> listAll() {
        String sql = """
            WITH ranked AS (
              SELECT
            """
            + RECOMMENDATION_VIEW_SELECT
            + """
                RANK() OVER (ORDER BY br.votecount DESC, br.creation_date ASC) AS vote_rank
              FROM backlog_recommendation br
              JOIN game_recommendation gr ON gr.uid = br.game_recommendation_uid
            )
            SELECT *
            FROM ranked
            ORDER BY vote_rank ASC, creation_date DESC
            LIMIT :limit
            """;

        return jdbc.query(
            sql,
            Map.of("limit", LIST_ALL_MAX_RESULTS),
            (rs, rowNum) -> mapViewRow(rs)
        );
    }

    public List<UnifiedGameSearchResultDto> searchUnified(String query) {
        String q = "%" + query + "%";
        Map<String, Object> params = Map.of("q", q);

        List<UnifiedGameSearchResultDto> results = new ArrayList<>();
        results.addAll(searchBacklogRecommendations(params));
        results.addAll(searchActiveBacklog(params));
        results.addAll(searchGames(params));

        return results.stream()
            .sorted(Comparator.comparing(UnifiedGameSearchResultDto::name, String.CASE_INSENSITIVE_ORDER))
            .limit(20)
            .toList();
    }

    private List<UnifiedGameSearchResultDto> searchBacklogRecommendations(Map<String, Object> params) {
        String sql = """
            SELECT
              br.uid AS backlog_recommendation_uid,
              br.name,
              br.state,
              br.state_reason,
              br.state_date,
              br.votecount,
              br.original_recommendation_by,
              br.game_uid,
              gr.uid AS gr_uid,
              gr.creation_date AS gr_creation_date,
              gr.name_base AS gr_name_base,
              gr.release_date AS gr_release_date,
              gr.external_igdb_id AS gr_external_igdb_id,
              COALESCE(g.first_release_date, gr.release_date) AS release_date,
            """
            + COVER_IMAGE_SUBQUERY + " AS cover_image_uid,\n"
            + ACTIVE_BACKLOG_JSON_SUBQUERY + " AS backlog_json,\n"
            + UNIFIED_COMPLETIONS_JSON_SUBQUERY + " AS completions_json\n"
            + """
            FROM backlog_recommendation br
            JOIN game_recommendation gr ON gr.uid = br.game_recommendation_uid
            LEFT JOIN game g ON g.uid = br.game_uid
            WHERE LOWER(br.name) LIKE LOWER(:q)
               OR LOWER(gr.name_base) LIKE LOWER(:q)
            ORDER BY br.name ASC
            LIMIT 20
            """;

        return jdbc.query(sql, params, (rs, rowNum) -> new UnifiedGameSearchResultDto(
            "BACKLOG_RECOMMENDATION",
            rs.getString("name"),
            rs.getObject("release_date", LocalDate.class),
            rs.getString("cover_image_uid"),
            rs.getString("game_uid"),
            parseBacklogInfo(rs.getString("backlog_json")),
            parseUnifiedCompletions(rs.getString("completions_json")),
            rs.getString("backlog_recommendation_uid"),
            rs.getInt("votecount"),
            parseState(rs.getString("state")),
            rs.getString("state_reason"),
            rs.getObject("state_date", LocalDate.class),
            rs.getString("original_recommendation_by"),
            mapGameRecommendation(rs, "gr_")
        ));
    }

    private List<UnifiedGameSearchResultDto> searchActiveBacklog(Map<String, Object> params) {
        String sql = """
            SELECT
              g.uid AS game_uid,
              g.name_base AS name,
              g.first_release_date AS release_date,
            """
            + COVER_IMAGE_SUBQUERY + " AS cover_image_uid,\n"
            + """
              json_build_object(
                'uid', b.uid,
                'addedDate', b.added_date,
                'removedDate', b.removed_date,
                'reason', b.reason
              ) AS backlog_json,
            """
            + UNIFIED_COMPLETIONS_JSON_SUBQUERY + " AS completions_json\n"
            + """
            FROM backlog b
            JOIN game g ON g.uid = b.game_uid
            WHERE b.removed_date IS NULL
              AND LOWER(g.name_base) LIKE LOWER(:q)
            ORDER BY g.name_base ASC
            LIMIT 20
            """;

        return jdbc.query(sql, params, (rs, rowNum) -> new UnifiedGameSearchResultDto(
            "BACKLOG",
            rs.getString("name"),
            rs.getObject("release_date", LocalDate.class),
            rs.getString("cover_image_uid"),
            rs.getString("game_uid"),
            parseBacklogInfo(rs.getString("backlog_json")),
            parseUnifiedCompletions(rs.getString("completions_json")),
            null,
            0,
            null,
            null,
            null,
            null,
            null
        ));
    }

    private List<UnifiedGameSearchResultDto> searchGames(Map<String, Object> params) {
        String sql = """
            SELECT
              g.uid AS game_uid,
              g.name_base AS name,
              g.first_release_date AS release_date,
            """
            + COVER_IMAGE_SUBQUERY + " AS cover_image_uid,\n"
            + ACTIVE_BACKLOG_JSON_SUBQUERY + " AS backlog_json,\n"
            + UNIFIED_COMPLETIONS_JSON_SUBQUERY + " AS completions_json\n"
            + """
            FROM game g
            WHERE LOWER(g.name_base) LIKE LOWER(:q)
            ORDER BY g.name_base ASC
            LIMIT 20
            """;

        return jdbc.query(sql, params, (rs, rowNum) -> new UnifiedGameSearchResultDto(
            "GAME",
            rs.getString("name"),
            rs.getObject("release_date", LocalDate.class),
            rs.getString("cover_image_uid"),
            rs.getString("game_uid"),
            parseBacklogInfo(rs.getString("backlog_json")),
            parseUnifiedCompletions(rs.getString("completions_json")),
            null,
            0,
            null,
            null,
            null,
            null,
            null
        ));
    }

    private BacklogRecommendationViewDto mapViewRow(ResultSet rs) throws java.sql.SQLException {
        return new BacklogRecommendationViewDto(
            rs.getString("uid"),
            toInstant(rs.getTimestamp("creation_date")),
            rs.getString("name"),
            parseState(rs.getString("state")),
            rs.getString("state_reason"),
            rs.getObject("state_date", LocalDate.class),
            rs.getInt("votecount"),
            rs.getObject("vote_rank") == null ? null : rs.getInt("vote_rank"),
            rs.getString("original_recommendation_by"),
            rs.getString("game_uid"),
            rs.getString("linked_game_image_uid"),
            mapGameRecommendation(rs, "gr_"),
            parseBacklogInfo(rs.getString("backlog_json")),
            parseCompletions(rs.getString("completions_json")),
            parseComments(rs.getString("comments_json"))
        );
    }

    private List<BacklogRecommendationCommentDto> parseComments(String json) {
        if (json == null || json.isBlank() || "[]".equals(json.trim())) {
            return List.of();
        }
        try {
            JsonNode arr = MAPPER.readTree(json);
            List<BacklogRecommendationCommentDto> list = new ArrayList<>();
            for (JsonNode n : arr) {
                list.add(new BacklogRecommendationCommentDto(
                    text(n, "uid"),
                    instant(n, "creationDate"),
                    text(n, "comment")
                ));
            }
            return list;
        } catch (Exception e) {
            return List.of();
        }
    }

    private static BacklogRecommendationState parseState(String value) {
        return value == null ? null : BacklogRecommendationState.valueOf(value);
    }

    private static Instant instant(JsonNode node, String field) {
        if (!node.has(field) || node.get(field).isNull()) {
            return null;
        }
        JsonNode value = node.get(field);
        if (value.isNumber()) {
            return Instant.ofEpochMilli(Math.round(value.doubleValue() * 1000));
        }
        return parseInstantFromJson(value.asText());
    }

    private static Instant parseInstantFromJson(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Instant.parse(value);
        } catch (Exception e) {
            try {
                return java.time.OffsetDateTime.parse(value).toInstant();
            } catch (Exception e2) {
                try {
                    String normalized = value.contains("T") ? value : value.replace(' ', 'T');
                    return java.time.LocalDateTime.parse(normalized)
                        .atZone(java.time.ZoneOffset.UTC)
                        .toInstant();
                } catch (Exception ignored) {
                    return null;
                }
            }
        }
    }

    private GameRecommendationDto mapGameRecommendation(ResultSet rs, String prefix)
        throws java.sql.SQLException {
        return new GameRecommendationDto(
            rs.getString(prefix + "uid"),
            toInstant(rs.getTimestamp(prefix + "creation_date")),
            rs.getString(prefix + "name_base"),
            rs.getObject(prefix + "release_date", LocalDate.class),
            rs.getString(prefix + "external_igdb_id")
        );
    }

    private BacklogInfoDto parseBacklogInfo(String json) {
        if (json == null || json.isBlank() || "null".equals(json.trim())) {
            return null;
        }
        try {
            JsonNode node = MAPPER.readTree(json);
            return new BacklogInfoDto(
                text(node, "uid"),
                date(node, "addedDate"),
                date(node, "removedDate"),
                text(node, "reason")
            );
        } catch (Exception e) {
            return null;
        }
    }

    private List<UnifiedGameSearchCompletionDto> parseUnifiedCompletions(String json) {
        if (json == null || json.isBlank() || "[]".equals(json.trim())) {
            return List.of();
        }
        try {
            JsonNode arr = MAPPER.readTree(json);
            List<UnifiedGameSearchCompletionDto> list = new ArrayList<>();
            for (JsonNode n : arr) {
                list.add(new UnifiedGameSearchCompletionDto(
                    text(n, "uid"),
                    text(n, "gameInstanceUid"),
                    text(n, "dlcUid"),
                    text(n, "completionStateUid"),
                    text(n, "info"),
                    date(n, "finishedDate"),
                    date(n, "completedDate"),
                    text(n, "platformUid"),
                    text(n, "platformName"),
                    text(n, "coverImageUid")
                ));
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

    private static Instant toInstant(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toInstant();
    }
}
