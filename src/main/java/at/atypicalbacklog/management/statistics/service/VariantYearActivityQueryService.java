package at.atypicalbacklog.management.statistics.service;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import at.atypicalbacklog.management.statistics.dto.VariantYearActivityDto;

import java.util.List;
import java.util.Map;

@Service
public class VariantYearActivityQueryService {

    private final NamedParameterJdbcTemplate jdbc;

    public VariantYearActivityQueryService(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<VariantYearActivityDto> getForYear(int year) {

        String sql = """
            SELECT year, variant_uid, total_seconds
            FROM vw_variant_year_activity
            WHERE year = :year
            ORDER BY total_seconds DESC
        """;

        return jdbc.query(sql,
            Map.of("year", year),
            (rs, i) -> new VariantYearActivityDto(
                rs.getInt("year"),
                rs.getString("variant_uid"),
                rs.getLong("total_seconds")
            )
        );
    }
}