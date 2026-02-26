package at.atypicalbacklog.management.statistics.service;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import at.atypicalbacklog.management.statistics.dto.GamePlaytimeRankingDto;

import java.util.List;
import java.util.Map;

@Service
public class GameRankingQueryService {

    private final NamedParameterJdbcTemplate jdbc;
    
    public GameRankingQueryService(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<GamePlaytimeRankingDto> getRanking(
        Integer fromYear,
        Integer toYear
    ) {

        String sql = """
            SELECT game_uid,
                   SUM(duration_seconds) AS total_seconds
            FROM vw_playtime_base
            WHERE (:fromYear IS NULL OR year >= :fromYear)
              AND (:toYear IS NULL OR year <= :toYear)
            GROUP BY game_uid
            ORDER BY total_seconds DESC
        """;

        return jdbc.query(sql,
            Map.of("fromYear", fromYear, "toYear", toYear),
            (rs,i) -> new GamePlaytimeRankingDto(
                rs.getString("game_uid"),
                rs.getLong("total_seconds")
            )
        );
    }
}