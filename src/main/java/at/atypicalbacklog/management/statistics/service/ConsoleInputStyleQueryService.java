package at.atypicalbacklog.management.statistics.service;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import at.atypicalbacklog.management.statistics.dto.ConsoleInputStyleDto;

import java.util.List;
import java.util.Map;

@Service
public class ConsoleInputStyleQueryService {

    private final NamedParameterJdbcTemplate jdbc;

    public ConsoleInputStyleQueryService(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<ConsoleInputStyleDto> getForYear(int year) {

        String sql = """
            SELECT year, console_uid, input_uid, total_seconds
            FROM vw_console_inputstyle_year
            WHERE year = :year
            ORDER BY console_uid ASC, total_seconds DESC
        """;

        return jdbc.query(sql,
            Map.of("year", year),
            (rs, i) -> new ConsoleInputStyleDto(
                rs.getInt("year"),
                rs.getString("console_uid"),
                rs.getString("input_uid"),
                rs.getLong("total_seconds")
            )
        );
    }
}