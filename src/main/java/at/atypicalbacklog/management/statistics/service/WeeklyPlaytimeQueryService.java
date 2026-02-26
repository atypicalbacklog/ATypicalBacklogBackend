package at.atypicalbacklog.management.statistics.service;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import at.atypicalbacklog.management.statistics.dto.WeeklyPlaytimeDto;

import java.util.List;
import java.util.Map;

@Service
public class WeeklyPlaytimeQueryService {

    private final NamedParameterJdbcTemplate jdbc;

    public WeeklyPlaytimeQueryService(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<WeeklyPlaytimeDto> getForYear(int year) {

        String sql = """
            SELECT year, week, total_seconds
            FROM vw_weekly_playtime
            WHERE year = :year
            ORDER BY week ASC
        """;

        return jdbc.query(sql,
            Map.of("year", year),
            (rs, i) -> new WeeklyPlaytimeDto(
                rs.getInt("year"),
                rs.getInt("week"),
                rs.getLong("total_seconds")
            )
        );
    }
}