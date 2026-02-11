package at.atypicalbacklog.management.service.query;

import at.atypicalbacklog.management.dto.GameInstanceSummaryDto;
import at.atypicalbacklog.management.dto.query.GameInstanceQueryResultDto;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

@Service
public class GameInstanceQueryService {

    private final NamedParameterJdbcTemplate jdbc;

    public GameInstanceQueryService(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<GameInstanceSummaryDto> getInstancesByGameUid(String gameUid) {
        String sql = """
            SELECT
              gi.uid,
              gi.name,
              (
                SELECT img.uid
                FROM game_image img
                WHERE img.game_instance_uid = gi.uid
                  AND img.owner_type = 'GAME_INSTANCE'
                  AND img.type = 'COVER'
                ORDER BY img.primary_image DESC NULLS LAST, img.creation_date DESC
                LIMIT 1
              ) AS cover_image_uid
            FROM game_instance gi
            WHERE gi.game_uid = :gameUid
            ORDER BY gi.creation_date ASC
            """;
        return jdbc.query(sql, Map.of("gameUid", gameUid), this::mapSummaryRow);
    }

    private GameInstanceSummaryDto mapSummaryRow(ResultSet rs, int i) throws java.sql.SQLException {
        return new GameInstanceSummaryDto(
            rs.getString("uid"),
            rs.getString("name"),
            rs.getString("cover_image_uid")
        );
    }

    public List<GameInstanceQueryResultDto> search(String query) {

        String sql = """
            SELECT
              gi.uid,
              gi.name,
              gi.platform_uid,
              (
                SELECT img.uid
                FROM game_image img
                WHERE img.game_instance_uid = gi.uid
                  AND img.owner_type = 'GAME_INSTANCE'
                  AND img.type = 'COVER'
                ORDER BY img.primary_image DESC NULLS LAST, img.creation_date DESC
                LIMIT 1
              ) AS cover_image_uid
            FROM game_instance gi
            WHERE LOWER(gi.name) LIKE LOWER(:q)
            ORDER BY gi.name ASC
            LIMIT 20
        """;

        return jdbc.query(
            sql,
            Map.of("q", "%" + query + "%"),
            this::mapRow
        );
    }

    private GameInstanceQueryResultDto mapRow(ResultSet rs, int i)
        throws java.sql.SQLException {

        return new GameInstanceQueryResultDto(
            rs.getString("uid"),
            rs.getString("name"),
            rs.getString("platform_uid"),
            rs.getString("cover_image_uid")
        );
    }
}
