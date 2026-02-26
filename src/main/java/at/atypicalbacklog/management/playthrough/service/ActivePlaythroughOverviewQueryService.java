package at.atypicalbacklog.management.playthrough.service;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import at.atypicalbacklog.management.playthrough.dto.ActivePlaythroughOverviewRow;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

@Service
public class ActivePlaythroughOverviewQueryService {

    private final NamedParameterJdbcTemplate jdbc;

    public ActivePlaythroughOverviewQueryService(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<ActivePlaythroughOverviewRow> loadActive() {

        String sql = """
            SELECT
              pt.uid AS playthrough_uid,
              pt.game_instance_uid,
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
              ) AS cover_image_uid,

              pt.playthrough_type_uid,
              pt.input_uid,
              pt.playthrough_state_uid,

              EXISTS (
                SELECT 1 FROM time_booking tb
                WHERE tb.playthrough_uid = pt.uid
                  AND tb.end_time IS NULL
              ) AS is_time_tracking_active,

              (
                SELECT tb.start_time FROM time_booking tb
                WHERE tb.playthrough_uid = pt.uid
                  AND tb.end_time IS NULL
                ORDER BY tb.start_time DESC
                LIMIT 1
              ) AS active_session_started_at,

              COALESCE(ROUND((
                SELECT SUM(
                  EXTRACT(EPOCH FROM (tb.end_time - tb.start_time))
                )
                FROM time_booking tb
                WHERE tb.playthrough_uid = pt.uid
                  AND tb.start_time IS NOT NULL
                  AND tb.end_time IS NOT NULL
              ))::BIGINT, 0) AS total_playtime_seconds

            FROM playthrough pt
            JOIN game_instance gi ON gi.uid = pt.game_instance_uid
            JOIN game g ON g.uid = gi.game_uid
            JOIN playthrough_state ps ON ps.uid = pt.playthrough_state_uid
            WHERE ps.name = 'Active'
            ORDER BY (
              SELECT MAX(COALESCE(tb.end_time, tb.start_time))
              FROM time_booking tb
              WHERE tb.playthrough_uid = pt.uid
            ) DESC NULLS LAST
        """;

        return jdbc.query(sql, Map.of(), this::mapRow);
    }

    private ActivePlaythroughOverviewRow mapRow(ResultSet rs, int i) throws java.sql.SQLException {
        return new ActivePlaythroughOverviewRow(
            rs.getString("playthrough_uid"),
            rs.getString("game_instance_uid"),
            rs.getString("display_name"),
            rs.getString("cover_image_uid"),
            rs.getString("playthrough_type_uid"),
            rs.getString("input_uid"),
            rs.getString("playthrough_state_uid"),
            rs.getBoolean("is_time_tracking_active"),
            toInstant(rs.getTimestamp("active_session_started_at", utcCalendar())),
            rs.getLong("total_playtime_seconds")
        );
    }

    private static final Calendar UTC_CALENDAR = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

    private static Calendar utcCalendar() {
        return (Calendar) UTC_CALENDAR.clone();
    }

    private static Instant toInstant(Timestamp ts) {
        return ts == null ? null : ts.toInstant();
    }
}
