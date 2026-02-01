package at.atypicalbacklog.management.service.playthrough.query;

import at.atypicalbacklog.management.dto.playthrough.query.ActivePlaythroughOverviewRow;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

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
                (SELECT img.uid FROM game_image img WHERE img.game_instance_uid = gi.uid LIMIT 1),
                (SELECT img.uid FROM game_image img WHERE img.game_uid = g.uid LIMIT 1)
              ) AS thumbnail_uid,

              pt.playthrough_type_uid,
              pt.input_uid,
              pt.playthrough_state_uid,

              EXISTS (
                SELECT 1 FROM time_booking tb
                WHERE tb.playthrough_uid = pt.uid
                  AND tb.end_time IS NULL
              ) AS is_time_tracking_active,

              COALESCE((
                SELECT SUM(
                  EXTRACT(EPOCH FROM (COALESCE(tb.end_time, NOW()) - tb.start_time))
                )
                FROM time_booking tb
                WHERE tb.playthrough_uid = pt.uid
              ), 0) AS total_playtime_seconds

            FROM playthrough pt
            JOIN game_instance gi ON gi.uid = pt.game_instance_uid
            JOIN game g ON g.uid = gi.game_uid
            JOIN playthrough_state ps ON ps.uid = pt.playthrough_state_uid
            WHERE ps.name = 'Active'
            ORDER BY pt.creation_date DESC
        """;

        return jdbc.query(sql, Map.of(), this::mapRow);
    }

    private ActivePlaythroughOverviewRow mapRow(ResultSet rs, int i) throws java.sql.SQLException {
        return new ActivePlaythroughOverviewRow(
            rs.getString("playthrough_uid"),
            rs.getString("game_instance_uid"),
            rs.getString("display_name"),
            rs.getString("thumbnail_uid"),
            rs.getString("playthrough_type_uid"),
            rs.getString("input_uid"),
            rs.getString("playthrough_state_uid"),
            rs.getBoolean("is_time_tracking_active"),
            rs.getLong("total_playtime_seconds")
        );
    }
}
