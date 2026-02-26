package at.atypicalbacklog.management.statistics.service;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import at.atypicalbacklog.management.statistics.dto.ConsoleYearActivityDto;

import java.util.List;
import java.util.Map;

@Service
public class ConsoleYearActivityQueryService {

    private final NamedParameterJdbcTemplate jdbc;

    public ConsoleYearActivityQueryService(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<ConsoleYearActivityDto> getForYear(int year) {

        String sql = """
            SELECT year, console_uid, total_seconds, distinct_games, booking_count
            FROM vw_console_year_activity
            WHERE year = :year
            ORDER BY total_seconds DESC
        """;

        return jdbc.query(sql, Map.of("year", year),
            (rs,i) -> new ConsoleYearActivityDto(
                rs.getInt("year"),
                rs.getString("console_uid"),
                rs.getLong("total_seconds"),
                rs.getInt("distinct_games"),
                rs.getInt("booking_count")
            )
        );
    }
}