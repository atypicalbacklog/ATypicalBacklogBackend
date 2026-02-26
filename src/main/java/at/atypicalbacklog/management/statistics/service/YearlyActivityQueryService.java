package at.atypicalbacklog.management.statistics.service;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import at.atypicalbacklog.management.statistics.dto.YearlyActivityDto;

import java.util.List;
import java.util.Map;

@Service
public class YearlyActivityQueryService {

    private final NamedParameterJdbcTemplate jdbc;

    public YearlyActivityQueryService(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<YearlyActivityDto> getAll() {

        String sql = """
            SELECT y.year,
                   y.total_seconds,
                   y.active_days,
                   y.booking_count,
                   y.distinct_games,
                   COALESCE(c.completion_count, 0) AS completion_count
            FROM vw_yearly_activity y
            LEFT JOIN vw_yearly_completion c
                   ON c.year = y.year
            ORDER BY y.year ASC
        """;

        return jdbc.query(sql, Map.of(), (rs,i) ->
            new YearlyActivityDto(
                rs.getInt("year"),
                rs.getLong("total_seconds"),
                rs.getInt("active_days"),
                rs.getInt("booking_count"),
                rs.getInt("distinct_games"),
                rs.getInt("completion_count")
            )
        );
    }
}