package at.atypicalbacklog.management.service.playthrough.query;

import at.atypicalbacklog.management.dto.playthrough.query.*;
import at.atypicalbacklog.management.dto.playthrough.query.ActivePlaythroughOverviewRow;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.*;

@Service
public class PlaythroughHistoryQueryService {

    private final NamedParameterJdbcTemplate jdbc;

    public PlaythroughHistoryQueryService(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<ActivePlaythroughOverviewRow> search(
        PlaythroughSearchRequest req
    ) {

        String sql = """
            SELECT
              pt.uid AS playthrough_uid,
              pt.game_instance_uid,

              COALESCE(gi.name, g.name_base) AS display_name,

              COALESCE(gi_img.uid, g_img.uid) AS thumbnail_uid,

              pt.playthrough_type_uid,
              pt.input_uid,
              pt.playthrough_state_uid,

              EXISTS (
                SELECT 1
                FROM time_booking tb
                WHERE tb.playthrough_uid = pt.uid
                  AND tb.end_time IS NULL
              ) AS time_tracking_active,

              COALESCE((
                SELECT SUM(
                  EXTRACT(EPOCH FROM (
                    COALESCE(tb.end_time, NOW()) - tb.start_time
                  ))
                )
                FROM time_booking tb
                WHERE tb.playthrough_uid = pt.uid
              ), 0) AS total_playtime_seconds

            FROM playthrough pt
            JOIN game_instance gi ON gi.uid = pt.game_instance_uid
            JOIN game g ON g.uid = gi.game_uid

            LEFT JOIN LATERAL (
              SELECT img.uid
              FROM game_image img
              WHERE img.game_instance_uid = gi.uid
                AND img.type = 'COVER'
              ORDER BY img.primary_image DESC NULLS LAST, img.creation_date DESC
              LIMIT 1
            ) gi_img ON true

            LEFT JOIN LATERAL (
              SELECT img.uid
              FROM game_image img
              WHERE img.game_uid = g.uid
                AND img.type = 'COVER'
              ORDER BY img.primary_image DESC NULLS LAST, img.creation_date DESC
              LIMIT 1
            ) g_img ON true

            WHERE 1 = 1
        """;

        Map<String, Object> params = new HashMap<>();

        // Name search
        if (req.name() != null && !req.name().isBlank()) {
            sql += """
                AND (
                  LOWER(gi.name) LIKE LOWER(:name)
                  OR LOWER(g.name_base) LIKE LOWER(:name)
                )
            """;
            params.put("name", "%" + req.name() + "%");
        }

        // State filter
        if (req.playthroughStateUid() != null) {
            sql += " AND pt.playthrough_state_uid = :state";
            params.put("state", req.playthroughStateUid());
        }

        // Type filter
        if (req.playthroughTypeUid() != null) {
            sql += " AND pt.playthrough_type_uid = :type";
            params.put("type", req.playthroughTypeUid());
        }

        // Year filter (based on started_at)
        if (req.year() != null) {
            sql += """
                AND EXTRACT(YEAR FROM pt.started_at) = :year
            """;
            params.put("year", req.year());
        }

        // Explicitly exclude currently active playthroughs
        sql += """
            AND pt.playthrough_state_uid != 'ACTIVE'
        """;

        sql += """
            ORDER BY pt.started_at DESC
        """;

        return jdbc.query(sql, params, this::mapRow);
    }

    private ActivePlaythroughOverviewRow mapRow(
        ResultSet rs,
        int i
    ) throws java.sql.SQLException {
        return new ActivePlaythroughOverviewRow(
            rs.getString("playthrough_uid"),
            rs.getString("game_instance_uid"),
            rs.getString("display_name"),
            rs.getString("thumbnail_uid"),
            rs.getString("playthrough_type_uid"),
            rs.getString("input_uid"),
            rs.getString("playthrough_state_uid"),
            rs.getBoolean("time_tracking_active"),
            null,
            rs.getLong("total_playtime_seconds")
        );
    }
}