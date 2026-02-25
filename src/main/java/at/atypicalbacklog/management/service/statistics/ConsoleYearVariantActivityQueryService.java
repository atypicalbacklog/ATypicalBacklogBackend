package at.atypicalbacklog.management.service.statistics;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import at.atypicalbacklog.management.dto.statistics.ConsoleYearVariantActivityDto;

@Service
public class ConsoleYearVariantActivityQueryService {

    private final NamedParameterJdbcTemplate jdbc;

    public ConsoleYearVariantActivityQueryService(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<ConsoleYearVariantActivityDto> getForYear(int year) {

        String sql = """
            SELECT year, variant_uid, console_uid, total_seconds, distinct_games, booking_count
            FROM vw_console_variant_year_activity
            WHERE year = :year
            ORDER BY total_seconds DESC
        """;

        return jdbc.query(sql, Map.of("year", year),
            (rs,i) -> new ConsoleYearVariantActivityDto(
                rs.getInt("year"),
                rs.getString("variant_uid"),
                rs.getString("console_uid"),
                rs.getLong("total_seconds"),
                rs.getInt("distinct_games"),
                rs.getInt("booking_count")
            )
        );
    }
}