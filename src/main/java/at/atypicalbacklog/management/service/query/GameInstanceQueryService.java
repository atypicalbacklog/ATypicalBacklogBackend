package at.atypicalbacklog.management.service.query;

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

    public List<GameInstanceQueryResultDto> search(String query) {

        String sql = """
            SELECT
              gi.uid,
              gi.name,
              gi.platform_uid,
              img.uid AS thumbnail_uid
            FROM game_instance gi
            LEFT JOIN game_image img
              ON img.game_instance_uid = gi.uid
             AND img.owner_type = 'GAME_INSTANCE'
             AND img.type = 'COVER'
             AND img.primary_image = true
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
            rs.getString("thumbnail_uid")
        );
    }
}
