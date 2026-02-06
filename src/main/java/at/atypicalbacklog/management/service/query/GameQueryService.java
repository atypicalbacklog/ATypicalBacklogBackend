package at.atypicalbacklog.management.service.query;

import at.atypicalbacklog.management.dto.query.GameQueryResultDto;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class GameQueryService {

    private final NamedParameterJdbcTemplate jdbc;

    public GameQueryService(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<GameQueryResultDto> search(String query) {

        String sql = """
            SELECT
              g.uid,
              g.name_base,
              g.first_release_date,
              img.uid AS cover_image_uid
            FROM game g
            LEFT JOIN game_image img
              ON img.game_uid = g.uid
             AND img.owner_type = 'GAME'
             AND img.type = 'COVER'
             AND img.primary_image = true
            WHERE LOWER(g.name_base) LIKE LOWER(:q)
            ORDER BY g.name_base ASC
            LIMIT 20
        """;

        return jdbc.query(
            sql,
            Map.of("q", "%" + query + "%"),
            this::mapRow
        );
    }

    private GameQueryResultDto mapRow(ResultSet rs, int i) throws java.sql.SQLException {
        return new GameQueryResultDto(
            rs.getString("uid"),
            rs.getString("name_base"),
            rs.getObject("first_release_date", LocalDate.class),
            rs.getString("cover_image_uid")
        );
    }
}
