package at.atypicalbacklog.management.dlc.service;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import at.atypicalbacklog.management.dlc.dto.DlcResponse;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class DlcQueryService {

    private final NamedParameterJdbcTemplate jdbc;

    public DlcQueryService(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<DlcResponse> getByGameInstanceUid(String gameInstanceUid) {
        String sql = """
            SELECT
              d.uid,
              d.game_instance_uid,
              d.name,
              d.received_date,
              d.release_date,
              d.playthrough_uid,
              gc.completion_state_uid,
              gc.finished_date,
              gc.completed_date,
              (SELECT img.uid FROM game_image img
               WHERE img.dlc_uid = d.uid
                 AND img.owner_type = 'DLC'
                 AND img.type = 'COVER'
               ORDER BY img.primary_image DESC NULLS LAST, img.creation_date DESC
               LIMIT 1) AS cover_image_uid
            FROM dlc d
            LEFT JOIN game_completion gc ON gc.dlc_uid = d.uid
            WHERE d.game_instance_uid = :gameInstanceUid
            ORDER BY d.creation_date ASC
            """;
        return jdbc.query(sql, Map.of("gameInstanceUid", gameInstanceUid), this::mapRow);
    }

    private DlcResponse mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new DlcResponse(
            rs.getString("uid"),
            rs.getString("game_instance_uid"),
            rs.getString("name"),
            rs.getObject("received_date", LocalDate.class),
            rs.getObject("release_date", LocalDate.class),
            rs.getString("playthrough_uid"),
            rs.getString("completion_state_uid"),
            rs.getString("cover_image_uid"),
            rs.getObject("finished_date", LocalDate.class),
            rs.getObject("completed_date", LocalDate.class)
        );
    }
}
