package at.atypicalbacklog.management.service.statistics;

import at.atypicalbacklog.management.dto.statistics.GamePlayedPerYearDto;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Query service for vw_game_played_per_year view, filtered by year and with cover image UID.
 */
@Service
public class GamePlayedPerYearQueryService {

    private final NamedParameterJdbcTemplate jdbc;

    public GamePlayedPerYearQueryService(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /**
     * Returns all rows from vw_game_played_per_year for the given year, with preferred cover
     * (instance cover if present, else game cover). Ordered by game_name.
     */
    public List<GamePlayedPerYearDto> getForYear(int year) {
        String sql = """
            SELECT
              v.year,
              v.game_instance_uid,
              v.game_name,
              v.booking_count,
              v.playthrough_count,
              v.total_playtime_seconds,
              COALESCE(
                (SELECT img.uid FROM game_image img
                 WHERE img.game_instance_uid = v.game_instance_uid
                   AND img.owner_type = 'GAME_INSTANCE'
                   AND img.type = 'COVER'
                 ORDER BY img.primary_image DESC NULLS LAST, img.creation_date DESC
                 LIMIT 1),
                (SELECT img.uid FROM game_image img
                 WHERE img.game_uid = gi.game_uid
                   AND img.owner_type = 'GAME'
                   AND img.type = 'COVER'
                 ORDER BY img.primary_image DESC NULLS LAST, img.creation_date DESC
                 LIMIT 1)
              ) AS cover_image_uid
            FROM atypicalbacklog.vw_game_played_per_year v
            JOIN atypicalbacklog.game_instance gi ON gi.uid = v.game_instance_uid
            WHERE v.year = :year
            ORDER BY v.game_name
            """;

        return jdbc.query(sql, Map.of("year", year), this::mapRow);
    }

    private GamePlayedPerYearDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new GamePlayedPerYearDto(
            rs.getInt("year"),
            rs.getString("game_instance_uid"),
            rs.getString("game_name"),
            rs.getInt("booking_count"),
            rs.getInt("playthrough_count"),
            rs.getLong("total_playtime_seconds"),
            rs.getString("cover_image_uid")
        );
    }
}
