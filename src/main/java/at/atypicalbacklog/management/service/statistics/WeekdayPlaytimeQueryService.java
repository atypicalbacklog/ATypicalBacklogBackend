package at.atypicalbacklog.management.service.statistics;

import at.atypicalbacklog.management.dto.statistics.WeekdayPlaytimeDto;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class WeekdayPlaytimeQueryService {

    private final NamedParameterJdbcTemplate jdbc;

    public WeekdayPlaytimeQueryService(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<WeekdayPlaytimeDto> getForYear(int year) {

        String sql = """
            SELECT year, weekday, total_seconds
            FROM vw_weekday_playtime
            WHERE year = :year
            ORDER BY weekday ASC
        """;

        return jdbc.query(sql,
            Map.of("year", year),
            (rs, i) -> new WeekdayPlaytimeDto(
                rs.getInt("year"),
                rs.getInt("weekday"),
                rs.getLong("total_seconds")
            )
        );
    }
}