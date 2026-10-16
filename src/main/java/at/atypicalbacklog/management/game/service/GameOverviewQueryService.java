package at.atypicalbacklog.management.game.service;

import at.atypicalbacklog.management.game.dto.GameOverviewQueryRequest;
import at.atypicalbacklog.management.game.dto.GameOverviewQueryResponseDto;
import at.atypicalbacklog.management.game.dto.GameOverviewRowDto;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GameOverviewQueryService {

    private final NamedParameterJdbcTemplate jdbc;

    public GameOverviewQueryService(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public GameOverviewQueryResponseDto query(GameOverviewQueryRequest req) {
        validateKeysetRequest(req);

        Map<String, Object> params = new HashMap<>();
        collectParams(req, params);

        String baseSql = buildBaseQuery(req);
        Counts totalCounts = queryTotalCounts(baseSql, params);

        String dataSql = "SELECT * FROM (" + baseSql + ") q";
        dataSql += buildKeysetCondition(req, params);
        dataSql += buildOrderBy(req);

        if (req.pageSize() != null && req.pageSize() > 0) {
            params.put("pageSize", req.pageSize());
            dataSql += " LIMIT :pageSize";
        }

        List<GameOverviewRowDto> rows = jdbc.query(dataSql, params, this::mapRow);

        Set<String> pageGames = rows.stream()
            .map(GameOverviewRowDto::gameUid)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
        Set<String> pageInstances = rows.stream()
            .map(GameOverviewRowDto::gameInstanceUid)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
        long pageDlcCount = rows.stream().filter(GameOverviewRowDto::isDlc).count();

        return new GameOverviewQueryResponseDto(
            rows,
            totalCounts.gameCount(),
            totalCounts.gameInstanceCount(),
            totalCounts.dlcCount(),
            pageGames.size(),
            pageInstances.size(),
            pageDlcCount
        );
    }

    private record Counts(long gameCount, long gameInstanceCount, long dlcCount) {}

    private Counts queryTotalCounts(String baseSql, Map<String, Object> params) {
        String countSql = """
            SELECT
              COUNT(DISTINCT q.game_uid) AS game_count,
              COUNT(DISTINCT q.game_instance_uid) AS game_instance_count,
              COUNT(*) FILTER (WHERE q.is_dlc) AS dlc_count
            FROM (
            """ + baseSql + """
            ) q
            """;
        return jdbc.queryForObject(countSql, params, (rs, i) -> new Counts(
            rs.getLong("game_count"),
            rs.getLong("game_instance_count"),
            rs.getLong("dlc_count")
        ));
    }

    private String buildBaseQuery(GameOverviewQueryRequest req) {
        String instanceSql = buildInstanceQuery(req);
        if (Boolean.TRUE.equals(req.includeDlc()) && !Boolean.FALSE.equals(req.onlyGamesWithDlc())) {
            String dlcSql = buildDlcQuery(req);
            return "(" + instanceSql + ") UNION ALL (" + dlcSql + ")";
        }
        return instanceSql;
    }

    private void validateKeysetRequest(GameOverviewQueryRequest req) {
        boolean hasCursor = req.cursorGameName() != null
            || req.cursorInstanceName() != null
            || req.cursorDlcName() != null
            || req.cursorGameUid() != null
            || req.cursorGameInstanceUid() != null
            || req.cursorDlcUid() != null
            || req.cursorIsDlc() != null;

        if (!hasCursor) {
            return;
        }

        GameOverviewQueryRequest.SortMode mode =
            req.sortMode() != null ? req.sortMode() : GameOverviewQueryRequest.SortMode.NAME_ASC;
        if (mode != GameOverviewQueryRequest.SortMode.NAME_ASC) {
            throw new IllegalArgumentException("Keyset pagination is currently supported only for NAME_ASC sortMode");
        }
    }

    private String buildKeysetCondition(GameOverviewQueryRequest req, Map<String, Object> params) {
        boolean hasCursor = req.cursorGameName() != null
            || req.cursorInstanceName() != null
            || req.cursorDlcName() != null
            || req.cursorGameUid() != null
            || req.cursorGameInstanceUid() != null
            || req.cursorDlcUid() != null
            || req.cursorIsDlc() != null;

        if (!hasCursor) {
            return "";
        }

        params.put("cursorGameName", req.cursorGameName() != null ? req.cursorGameName() : "");
        params.put("cursorInstanceName", req.cursorInstanceName() != null ? req.cursorInstanceName() : "\uFFFF");
        params.put("cursorDlcName", req.cursorDlcName() != null ? req.cursorDlcName() : "\uFFFF");
        params.put("cursorGameUid", req.cursorGameUid() != null ? req.cursorGameUid() : "");
        params.put("cursorGameInstanceUid", req.cursorGameInstanceUid() != null ? req.cursorGameInstanceUid() : "");
        params.put("cursorDlcUid", req.cursorDlcUid() != null ? req.cursorDlcUid() : "");
        params.put("cursorIsDlc", Boolean.TRUE.equals(req.cursorIsDlc()));

        return """
            WHERE (
              COALESCE(q.game_name, ''),
              COALESCE(q.instance_name, '\uFFFF'),
              COALESCE(q.dlc_name, '\uFFFF'),
              q.game_uid,
              q.game_instance_uid,
              COALESCE(q.dlc_uid, ''),
              q.is_dlc
            ) > (
              :cursorGameName,
              :cursorInstanceName,
              :cursorDlcName,
              :cursorGameUid,
              :cursorGameInstanceUid,
              :cursorDlcUid,
              :cursorIsDlc
            )
            """;
    }

    private String buildInstanceQuery(GameOverviewQueryRequest req) {
        return """
            SELECT
              false                         AS is_dlc,
              g.uid                         AS game_uid,
              gi.uid                        AS game_instance_uid,
              NULL::text                    AS dlc_uid,
              gi.uid IS NOT NULL            AS has_instance,

              g.name_base                   AS game_name,
              g.first_release_date          AS game_release_date,

              gi.name                       AS instance_name,
              gi.release_date               AS instance_release_date,
              gi.received_date              AS instance_received_date,
              COALESCE(gi.in_posession, false) AS in_posession,

              p.uid                         AS platform_uid,
              p.name                        AS platform_name,
              v.uid                         AS variant_uid,
              v.name                        AS variant_name,

              (SELECT img.uid FROM game_image img
               WHERE img.game_uid = g.uid
                 AND img.owner_type = 'GAME'
                 AND img.type = 'COVER'
               ORDER BY img.primary_image DESC NULLS LAST, img.creation_date DESC
               LIMIT 1)                     AS game_cover_image_uid,
              (SELECT img.uid FROM game_image img
               WHERE img.game_instance_uid = gi.uid
                 AND img.owner_type = 'GAME_INSTANCE'
                 AND img.type = 'COVER'
               ORDER BY img.primary_image DESC NULLS LAST, img.creation_date DESC
               LIMIT 1)                     AS instance_cover_image_uid,
              NULL::text                    AS dlc_cover_image_uid,

              NULL::text                    AS dlc_name,
              NULL::date                    AS dlc_release_date,

              EXISTS (
                SELECT 1 FROM backlog b
                WHERE b.game_uid = g.uid AND b.removed_date IS NULL
              )                             AS in_backlog,

              EXISTS (
                SELECT 1 FROM playthrough pt
                JOIN playthrough_state ps ON ps.uid = pt.playthrough_state_uid
                WHERE pt.game_instance_uid = gi.uid
                  AND ps.name = 'Active'
                  """ + playthroughTypeCond(req, "pt") + """
              )                             AS has_active_playthrough,

              (
                SELECT gc.completion_state_uid
                FROM game_completion gc
                WHERE gc.game_instance_uid = gi.uid AND gc.dlc_uid IS NULL
                ORDER BY gc.creation_date DESC
                LIMIT 1
              )                             AS completion_state_uid,

              COALESCE((
                SELECT SUM(
                  EXTRACT(EPOCH FROM (COALESCE(tb.end_time, NOW()) - tb.start_time))
                )
                FROM time_booking tb
                JOIN playthrough pt ON pt.uid = tb.playthrough_uid
                WHERE pt.game_instance_uid = gi.uid
                  """ + playthroughTypeCond(req, "pt") + """
              ), 0)::bigint                 AS total_playtime_seconds,

              EXISTS (
                SELECT 1 FROM dlc d WHERE d.game_instance_uid = gi.uid
              )                             AS has_dlc,

              COALESCE(gi.creation_date, g.creation_date) AS sort_created,
              COALESCE(gi.release_date, g.first_release_date) AS sort_release,
              (
                SELECT MAX(COALESCE(tb.end_time, tb.start_time))
                FROM time_booking tb
                JOIN playthrough pt ON pt.uid = tb.playthrough_uid
                WHERE pt.game_instance_uid = gi.uid
              )                             AS sort_last_played

            FROM game g
            LEFT JOIN game_instance gi ON gi.game_uid = g.uid
            LEFT JOIN platform p ON p.uid = gi.platform_uid
            LEFT JOIN variant v ON v.uid = gi.variant_uid
            WHERE 1=1
            """ + buildInstanceFilters(req);
    }

    private String buildDlcQuery(GameOverviewQueryRequest req) {
        return """
            SELECT
              true                          AS is_dlc,
              g.uid                         AS game_uid,
              gi.uid                        AS game_instance_uid,
              d.uid                         AS dlc_uid,
              true                          AS has_instance,

              g.name_base                   AS game_name,
              g.first_release_date          AS game_release_date,

              gi.name                       AS instance_name,
              gi.release_date               AS instance_release_date,
              gi.received_date              AS instance_received_date,
              gi.in_posession               AS in_posession,

              p.uid                         AS platform_uid,
              p.name                        AS platform_name,
              v.uid                         AS variant_uid,
              v.name                        AS variant_name,

              (SELECT img.uid FROM game_image img
               WHERE img.game_uid = g.uid
                 AND img.owner_type = 'GAME'
                 AND img.type = 'COVER'
               ORDER BY img.primary_image DESC NULLS LAST, img.creation_date DESC
               LIMIT 1)                     AS game_cover_image_uid,
              (SELECT img.uid FROM game_image img
               WHERE img.game_instance_uid = gi.uid
                 AND img.owner_type = 'GAME_INSTANCE'
                 AND img.type = 'COVER'
               ORDER BY img.primary_image DESC NULLS LAST, img.creation_date DESC
               LIMIT 1)                     AS instance_cover_image_uid,
              (SELECT img.uid FROM game_image img
               WHERE img.dlc_uid = d.uid
                 AND img.owner_type = 'DLC'
                 AND img.type = 'COVER'
               ORDER BY img.primary_image DESC NULLS LAST, img.creation_date DESC
               LIMIT 1)                     AS dlc_cover_image_uid,

              d.name                        AS dlc_name,
              d.release_date                AS dlc_release_date,

              EXISTS (
                SELECT 1 FROM backlog b
                WHERE b.game_uid = g.uid AND b.removed_date IS NULL
              )                             AS in_backlog,

              CASE WHEN d.playthrough_uid IS NOT NULL THEN
                EXISTS (
                  SELECT 1 FROM playthrough pt
                  JOIN playthrough_state ps ON ps.uid = pt.playthrough_state_uid
                  WHERE pt.uid = d.playthrough_uid
                    AND ps.name = 'Active'
                    """ + playthroughTypeCond(req, "pt") + """
                )
              ELSE NULL END                AS has_active_playthrough,

              (
                SELECT gc.completion_state_uid
                FROM game_completion gc
                WHERE gc.dlc_uid = d.uid
                ORDER BY gc.creation_date DESC
                LIMIT 1
              )                             AS completion_state_uid,

              CASE WHEN d.playthrough_uid IS NOT NULL THEN
                (SELECT COALESCE(SUM(
                  EXTRACT(EPOCH FROM (COALESCE(tb.end_time, NOW()) - tb.start_time))
                ), 0)::bigint
                FROM time_booking tb
                WHERE tb.playthrough_uid = d.playthrough_uid)
              ELSE NULL END                AS total_playtime_seconds,

              true                          AS has_dlc,

              d.creation_date               AS sort_created,
              COALESCE(gi.release_date, g.first_release_date) AS sort_release,
              (
                SELECT MAX(COALESCE(tb.end_time, tb.start_time))
                FROM time_booking tb
                WHERE tb.playthrough_uid = d.playthrough_uid
              )                             AS sort_last_played

            FROM game g
            JOIN game_instance gi ON gi.game_uid = g.uid
            JOIN dlc d ON d.game_instance_uid = gi.uid
            LEFT JOIN platform p ON p.uid = gi.platform_uid
            LEFT JOIN variant v ON v.uid = gi.variant_uid
            WHERE 1=1
            """ + buildDlcFilters(req);
    }

    private String buildInstanceFilters(GameOverviewQueryRequest req) {
        StringBuilder sb = new StringBuilder();

        if (req.searchText() != null && !req.searchText().isBlank()) {
            sb.append("""
                AND (
                    LOWER(g.name_base) LIKE LOWER(:search)
                    OR LOWER(gi.name) LIKE LOWER(:search)
                )
                """);
        }
        if (req.platformUids() != null && !req.platformUids().isEmpty()) {
            sb.append(" AND p.uid IN (:platformUids)");
        }
        if (req.consoleUids() != null && !req.consoleUids().isEmpty()) {
            if (Boolean.TRUE.equals(req.ignorePorts())) {
                sb.append(" AND p.console_uid IN (:consoleUids)");
            } else {
                sb.append("""
                    AND (
                        gi.original_console_uid IN (:consoleUids)
                        OR p.console_uid IN (:consoleUids)
                    )
                    """);
            }
        }
        if (req.inBacklog() != null) {
            sb.append(" AND EXISTS (SELECT 1 FROM backlog b WHERE b.game_uid = g.uid AND b.removed_date IS NULL) = :inBacklog");
        }
        if (req.hasActivePlaythrough() != null) {
            if (Boolean.TRUE.equals(req.hasActivePlaythrough())) {
                sb.append("""
                    AND EXISTS (
                        SELECT 1 FROM playthrough pt
                        JOIN playthrough_state ps ON ps.uid = pt.playthrough_state_uid
                        WHERE pt.game_instance_uid = gi.uid AND ps.name = 'Active'
                        """).append(playthroughTypeCond(req, "pt")).append("""
                    )
                    """);
            } else {
                sb.append("""
                    AND NOT EXISTS (
                        SELECT 1 FROM playthrough pt
                        JOIN playthrough_state ps ON ps.uid = pt.playthrough_state_uid
                        WHERE pt.game_instance_uid = gi.uid AND ps.name = 'Active'
                    )
                    """);
            }
        }
        if (req.completionStateUids() != null && !req.completionStateUids().isEmpty()) {
            sb.append("""
                AND EXISTS (
                    SELECT 1 FROM game_completion gc
                    WHERE gc.game_instance_uid = gi.uid
                      AND gc.dlc_uid IS NULL
                      AND gc.completion_state_uid IN (:completionStateUids)
                )
                """);
        }

        // Handle isPlayed and isUnplayed filters (ignored if completionStateUids is provided)
        if (req.completionStateUids() == null || req.completionStateUids().isEmpty()) {
            if (Boolean.TRUE.equals(req.isPlayed())) {
                sb.append("""
                    AND EXISTS (
                        SELECT 1 FROM game_completion gc
                        WHERE gc.game_instance_uid = gi.uid
                          AND gc.dlc_uid IS NULL
                          AND gc.completion_state_uid IN ('Completed', 'FinishedPlus', 'Finished', 'Postponed', 'Abandoned', 'Played')
                    )
                    """);
            } else if (Boolean.TRUE.equals(req.isUnplayed())) {
                sb.append("""
                    AND NOT EXISTS (
                        SELECT 1 FROM game_completion gc
                        WHERE gc.game_instance_uid = gi.uid
                          AND gc.dlc_uid IS NULL
                          AND gc.completion_state_uid IN ('Completed', 'FinishedPlus', 'Finished', 'Postponed', 'Abandoned', 'Played')
                    )
                    """);
            }
        }

        if (Boolean.TRUE.equals(req.onlyGamesWithDlc())) {
            sb.append("""
                AND EXISTS (
                    SELECT 1 FROM game_instance gi2
                    JOIN dlc d ON d.game_instance_uid = gi2.uid
                    WHERE gi2.game_uid = g.uid
                )
                """);
        } else if (Boolean.FALSE.equals(req.onlyGamesWithDlc())) {
            sb.append("""
                AND NOT EXISTS (
                    SELECT 1 FROM game_instance gi2
                    JOIN dlc d ON d.game_instance_uid = gi2.uid
                    WHERE gi2.game_uid = g.uid
                )
                """);
        }
        if (req.playthroughTypeUids() != null && !req.playthroughTypeUids().isEmpty()) {
            sb.append("""
                AND EXISTS (
                    SELECT 1 FROM playthrough pt
                    WHERE pt.game_instance_uid = gi.uid
                      AND pt.playthrough_type_uid IN (:playthroughTypeUids)
                )
                """);
        }

        return sb.toString();
    }

    private String buildDlcFilters(GameOverviewQueryRequest req) {
        StringBuilder sb = new StringBuilder();

        if (req.searchText() != null && !req.searchText().isBlank()) {
            sb.append("""
                AND (
                    LOWER(g.name_base) LIKE LOWER(:search)
                    OR LOWER(gi.name) LIKE LOWER(:search)
                    OR LOWER(d.name) LIKE LOWER(:search)
                )
                """);
        }
        if (req.platformUids() != null && !req.platformUids().isEmpty()) {
            sb.append(" AND p.uid IN (:platformUids)");
        }
        if (req.consoleUids() != null && !req.consoleUids().isEmpty()) {
            if (Boolean.TRUE.equals(req.ignorePorts())) {
                sb.append(" AND p.console_uid IN (:consoleUids)");
            } else {
                sb.append("""
                    AND (
                        gi.original_console_uid IN (:consoleUids)
                        OR p.console_uid IN (:consoleUids)
                    )
                    """);
            }
        }
        if (req.inBacklog() != null) {
            sb.append(" AND EXISTS (SELECT 1 FROM backlog b WHERE b.game_uid = g.uid AND b.removed_date IS NULL) = :inBacklog");
        }
        if (req.hasActivePlaythrough() != null && Boolean.TRUE.equals(req.hasActivePlaythrough())) {
            sb.append("""
                AND d.playthrough_uid IS NOT NULL
                AND EXISTS (
                    SELECT 1 FROM playthrough pt
                    JOIN playthrough_state ps ON ps.uid = pt.playthrough_state_uid
                    WHERE pt.uid = d.playthrough_uid AND ps.name = 'Active'
                    """).append(playthroughTypeCond(req, "pt")).append("""
                )
                """);
        } else if (req.hasActivePlaythrough() != null && Boolean.FALSE.equals(req.hasActivePlaythrough())) {
            sb.append(" AND (d.playthrough_uid IS NULL OR NOT EXISTS (" +
                "SELECT 1 FROM playthrough pt JOIN playthrough_state ps ON ps.uid = pt.playthrough_state_uid " +
                "WHERE pt.uid = d.playthrough_uid AND ps.name = 'Active'))");
        }
        if (req.completionStateUids() != null && !req.completionStateUids().isEmpty()) {
            sb.append("""
                AND EXISTS (
                    SELECT 1 FROM game_completion gc
                    WHERE gc.dlc_uid = d.uid
                      AND gc.completion_state_uid IN (:completionStateUids)
                )
                """);
        }

        // Handle isPlayed and isUnplayed filters for DLC (ignored if completionStateUids is provided)
        if (req.completionStateUids() == null || req.completionStateUids().isEmpty()) {
            if (Boolean.TRUE.equals(req.isPlayed())) {
                sb.append("""
                    AND EXISTS (
                        SELECT 1 FROM game_completion gc
                        WHERE gc.dlc_uid = d.uid
                          AND gc.completion_state_uid IN ('Completed', 'FinishedPlus', 'Finished', 'Postponed', 'Abandoned', 'Played')
                    )
                    """);
            } else if (Boolean.TRUE.equals(req.isUnplayed())) {
                sb.append("""
                    AND NOT EXISTS (
                        SELECT 1 FROM game_completion gc
                        WHERE gc.dlc_uid = d.uid
                          AND gc.completion_state_uid IN ('Completed', 'FinishedPlus', 'Finished', 'Postponed', 'Abandoned', 'Played')
                    )
                    """);
            }
        }

        if (req.playthroughTypeUids() != null && !req.playthroughTypeUids().isEmpty()) {
            sb.append("""
                AND EXISTS (
                    SELECT 1 FROM playthrough pt
                    WHERE pt.game_instance_uid = gi.uid
                      AND pt.playthrough_type_uid IN (:playthroughTypeUids)
                )
                """);
        }

        return sb.toString();
    }

    private String playthroughTypeCond(GameOverviewQueryRequest req, String alias) {
        if (req.playthroughTypeUids() == null || req.playthroughTypeUids().isEmpty()) {
            return "";
        }
        return " AND " + alias + ".playthrough_type_uid IN (:playthroughTypeUids)";
    }

    private void collectParams(GameOverviewQueryRequest req, Map<String, Object> params) {
        if (req.searchText() != null && !req.searchText().isBlank()) {
            params.put("search", "%" + req.searchText() + "%");
        }
        if (req.platformUids() != null && !req.platformUids().isEmpty()) {
            params.put("platformUids", req.platformUids());
        }
        if (req.consoleUids() != null && !req.consoleUids().isEmpty()) {
            params.put("consoleUids", req.consoleUids());
        }
        if (req.inBacklog() != null) {
            params.put("inBacklog", req.inBacklog());
        }
        if (req.completionStateUids() != null && !req.completionStateUids().isEmpty()) {
            params.put("completionStateUids", req.completionStateUids());
        }

        if (req.playthroughTypeUids() != null && !req.playthroughTypeUids().isEmpty()) {
            params.put("playthroughTypeUids", req.playthroughTypeUids());
        }
    }

    private String buildOrderBy(GameOverviewQueryRequest req) {
        GameOverviewQueryRequest.SortMode mode = req.sortMode() != null ? req.sortMode() : GameOverviewQueryRequest.SortMode.NAME_ASC;
        return switch (mode) {
            case PLAYTIME_DESC -> " ORDER BY total_playtime_seconds DESC NULLS LAST, game_uid, game_instance_uid";
            case NAME_DESC -> " ORDER BY game_name DESC, instance_name DESC NULLS LAST, dlc_name DESC NULLS LAST";
            case LAST_PLAYED_DESC -> " ORDER BY sort_last_played DESC NULLS LAST, game_uid, game_instance_uid";
            case NEWEST_CREATED -> " ORDER BY sort_created DESC NULLS LAST, game_uid, game_instance_uid";
            case RELEASE_DATE -> " ORDER BY sort_release DESC NULLS LAST, game_uid, game_instance_uid";
            default -> " ORDER BY game_name ASC, instance_name ASC NULLS LAST, dlc_name ASC NULLS LAST";
        };
    }

    private GameOverviewRowDto mapRow(ResultSet rs, int i) throws SQLException {
        return new GameOverviewRowDto(
                rs.getBoolean("is_dlc"),
                rs.getString("game_uid"),
                rs.getString("game_instance_uid"),
                rs.getString("dlc_uid"),
                rs.getBoolean("has_instance"),

                rs.getString("game_name"),
                rs.getObject("game_release_date", LocalDate.class),

                rs.getString("instance_name"),
                rs.getObject("instance_release_date", LocalDate.class),
                rs.getObject("instance_received_date", LocalDate.class),
                rs.getBoolean("in_posession"),

                rs.getString("platform_uid"),
                rs.getString("platform_name"),
                rs.getString("variant_uid"),
                rs.getString("variant_name"),

                rs.getString("game_cover_image_uid"),
                rs.getString("instance_cover_image_uid"),
                rs.getString("dlc_cover_image_uid"),

                rs.getString("dlc_name"),
                rs.getObject("dlc_release_date", LocalDate.class),

                rs.getBoolean("in_backlog"),
                rs.getObject("has_active_playthrough", Boolean.class),
                rs.getString("completion_state_uid"),

                rs.getObject("total_playtime_seconds", Long.class),

                rs.getBoolean("has_dlc")
        );
    }
}
