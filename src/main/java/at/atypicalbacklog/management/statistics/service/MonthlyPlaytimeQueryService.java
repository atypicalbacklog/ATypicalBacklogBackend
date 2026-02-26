package at.atypicalbacklog.management.statistics.service;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import at.atypicalbacklog.management.statistics.dto.MonthlyPlaytimeDto;

import java.util.List;
import java.util.Map;

@Service
public class MonthlyPlaytimeQueryService {

    private final NamedParameterJdbcTemplate jdbc;

    public MonthlyPlaytimeQueryService(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<MonthlyPlaytimeDto> getForYear(int year) {

        String sql = """
            SELECT year, month, total_seconds
            FROM vw_monthly_playtime
            WHERE year = :year
            ORDER BY month ASC
        """;

        return jdbc.query(sql,
            Map.of("year", year),
            (rs, i) -> new MonthlyPlaytimeDto(
                rs.getInt("year"),
                rs.getInt("month"),
                rs.getLong("total_seconds")
            )
        );
    }
}