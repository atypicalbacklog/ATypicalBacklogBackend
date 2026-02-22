package at.atypicalbacklog.management.service.statistics;

import at.atypicalbacklog.management.dto.statistics.BacklogCompletionGameOverviewDto;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Query service for backlog_completion_game_overview view, filtered by year and with cover image UID.
 */
@Service
public class BacklogCompletionGameOverviewQueryService {

    private final NamedParameterJdbcTemplate jdbc;

    public BacklogCompletionGameOverviewQueryService(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /**
     * Returns all rows from backlog_completion_game_overview where any of the date columns
     * (received_date, added_to_backlog, finished_date, completed_date, removed_from_backlog)
     * falls in the given year. Includes preferred cover image UID (instance cover, else game cover).
     */
    public List<BacklogCompletionGameOverviewDto> getForYear(int year) {
        String sql = """
            SELECT
              bcgo.game_instance_uid,
              bcgo.game_name,
              bcgo.platform,
              bcgo.for_free,
              bcgo.received_date,
              bcgo.release_date,
              bcgo.added_to_backlog,
              bcgo.finished_date,
              bcgo.completed_date,
              bcgo.removed_from_backlog,
              bcgo.game_uid,
              bcgo.backlog_uid,
              bcgo.platform_uid,
              bcgo.game_completion_uid,
              COALESCE(
                (SELECT img.uid FROM game_image img
                 WHERE img.game_instance_uid = bcgo.game_instance_uid
                   AND img.owner_type = 'GAME_INSTANCE'
                   AND img.type = 'COVER'
                 ORDER BY img.primary_image DESC NULLS LAST, img.creation_date DESC
                 LIMIT 1),
                (SELECT img.uid FROM game_image img
                 WHERE img.game_uid = bcgo.game_uid
                   AND img.owner_type = 'GAME'
                   AND img.type = 'COVER'
                 ORDER BY img.primary_image DESC NULLS LAST, img.creation_date DESC
                 LIMIT 1)
              ) AS cover_image_uid
            FROM atypicalbacklog.backlog_completion_game_overview bcgo
            WHERE EXTRACT(YEAR FROM bcgo.received_date) = :year
               OR EXTRACT(YEAR FROM bcgo.added_to_backlog) = :year
               OR EXTRACT(YEAR FROM bcgo.finished_date) = :year
               OR EXTRACT(YEAR FROM bcgo.completed_date) = :year
               OR EXTRACT(YEAR FROM bcgo.removed_from_backlog) = :year
            ORDER BY bcgo.game_name, bcgo.platform
            """;

        return jdbc.query(sql, Map.of("year", year), this::mapRow);
    }

    private BacklogCompletionGameOverviewDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new BacklogCompletionGameOverviewDto(
            rs.getString("game_instance_uid"),
            rs.getString("game_name"),
            rs.getString("platform"),
            rs.getBoolean("for_free"),
            rs.getObject("received_date", LocalDate.class),
            rs.getObject("release_date", LocalDate.class),
            rs.getObject("added_to_backlog", LocalDate.class),
            rs.getObject("finished_date", LocalDate.class),
            rs.getObject("completed_date", LocalDate.class),
            rs.getObject("removed_from_backlog", LocalDate.class),
            rs.getString("game_uid"),
            rs.getString("backlog_uid"),
            rs.getString("platform_uid"),
            rs.getString("game_completion_uid"),
            rs.getString("cover_image_uid")
        );
    }
}
