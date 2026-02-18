package at.atypicalbacklog.management.service.statistics;

import at.atypicalbacklog.management.dto.statistics.BookingDurationDistributionDto;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class BookingDurationDistributionQueryService {

    private final NamedParameterJdbcTemplate jdbc;

    public BookingDurationDistributionQueryService(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<BookingDurationDistributionDto> getDistribution(
        String gameUid,
        Integer fromYear,
        Integer toYear
    ) {

        String sql = """
            SELECT
              FLOOR(duration_seconds / 60) AS duration_minutes,
              COUNT(*) AS booking_count
            FROM vw_playtime_base
            WHERE game_uid = :gameUid
              AND (:fromYear IS NULL OR year >= :fromYear)
              AND (:toYear IS NULL OR year <= :toYear)
            GROUP BY FLOOR(duration_seconds / 60)
            ORDER BY duration_minutes ASC
        """;

        return jdbc.query(sql,
            Map.of(
                "gameUid", gameUid,
                "fromYear", fromYear,
                "toYear", toYear
            ),
            (rs, i) -> new BookingDurationDistributionDto(
                rs.getInt("duration_minutes"),
                rs.getLong("booking_count")
            )
        );
    }
}