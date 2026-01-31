package at.atypicalbacklog.management.service.query;

import at.atypicalbacklog.management.dto.query.*;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.time.Instant;
import java.util.*;

@Service
public class GameOverviewQueryService {

    private final NamedParameterJdbcTemplate jdbc;

    public GameOverviewQueryService(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<GameOverviewRowDto> query(GameOverviewQueryRequest req) {

        String sql = """
            SELECT
              g.uid                     AS game_uid,
              gi.uid                    AS game_instance_uid,
              gi.uid IS NOT NULL        AS has_instance,

              g.name_base               AS game_name,
              EXTRACT(YEAR FROM g.first_release_date) AS release_year,

              p.uid                     AS platform_uid,
              p.name                    AS platform_name,
              v.uid                     AS variant_uid,
              v.name                    AS variant_name,

              EXISTS (
                SELECT 1 FROM backlog b
                WHERE b.game_uid = g.uid
                  AND b.removed_date IS NULL
              ) AS in_backlog,

              EXISTS (
                SELECT 1 FROM playthrough pt
                JOIN playthrough_state ps ON ps.uid = pt.playthrough_state_uid
                WHERE pt.game_instance_uid = gi.uid
                  AND ps.name = 'Active'
              ) AS has_active_playthrough,

              (
                SELECT gc.completion_state_uid
                FROM game_completion gc
                WHERE gc.game_instance_uid = gi.uid
                ORDER BY gc.creation_date DESC
                LIMIT 1
              ) AS completion_state_uid,

              COALESCE((
                SELECT SUM(
                  EXTRACT(EPOCH FROM (COALESCE(tb.end_time, NOW()) - tb.start_time))
                )
                FROM time_booking tb
                JOIN playthrough pt ON pt.uid = tb.playthrough_uid
                WHERE pt.game_instance_uid = gi.uid
              ), 0) AS total_playtime_seconds,

              COALESCE(
                (
                  SELECT gi2.uid FROM game_image gi2
                  WHERE gi2.game_instance_uid = gi.uid
                  LIMIT 1
                ),
                (
                  SELECT gi3.uid FROM game_image gi3
                  WHERE gi3.game_uid = g.uid
                  LIMIT 1
                )
              ) AS thumbnail_uid

            FROM game g
            LEFT JOIN game_instance gi ON gi.game_uid = g.uid
            LEFT JOIN platform p ON p.uid = gi.platform_uid
            LEFT JOIN variant v ON v.uid = gi.variant_uid
            WHERE 1=1
        """;

        Map<String, Object> params = new HashMap<>();

        if (req.searchText() != null && !req.searchText().isBlank()) {
            sql += """
                AND (
                    LOWER(g.name_base) LIKE LOWER(:search)
                    OR LOWER(gi.name) LIKE LOWER(:search)
                )
            """;
            params.put("search", "%" + req.searchText() + "%");
        }

        if (req.platformUids() != null && !req.platformUids().isEmpty()) {
            sql += " AND p.uid IN (:platformUids)";
            params.put("platformUids", req.platformUids());
        }

        if (req.inBacklog() != null) {
            sql += " AND EXISTS (SELECT 1 FROM backlog b WHERE b.game_uid = g.uid AND b.removed_date IS NULL) = :inBacklog";
            params.put("inBacklog", req.inBacklog());
        }

        // sorting
        sql += switch (req.sortMode()) {
            case PLAYTIME_DESC -> " ORDER BY total_playtime_seconds DESC";
            case NAME_DESC -> " ORDER BY g.name_base DESC";
            default -> " ORDER BY g.name_base ASC";
        };

        return jdbc.query(sql, params, this::mapRow);
    }

    private GameOverviewRowDto mapRow(ResultSet rs, int i) throws java.sql.SQLException {
        return new GameOverviewRowDto(
            rs.getString("game_uid"),
            rs.getString("game_instance_uid"),
            rs.getBoolean("has_instance"),
            rs.getString("game_name"),
            rs.getInt("release_year"),
            rs.getString("platform_uid"),
            rs.getString("platform_name"),
            rs.getString("variant_uid"),
            rs.getString("variant_name"),
            rs.getBoolean("in_backlog"),
            rs.getBoolean("has_active_playthrough"),
            rs.getString("completion_state_uid"),
            rs.getLong("total_playtime_seconds"),
            rs.getString("thumbnail_uid")
        );
    }
}
