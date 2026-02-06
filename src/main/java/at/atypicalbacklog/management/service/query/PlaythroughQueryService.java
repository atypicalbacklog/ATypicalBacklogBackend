package at.atypicalbacklog.management.service.query;

import at.atypicalbacklog.management.domain.Playthrough;
import at.atypicalbacklog.management.dto.playthrough.PlaythroughResponse;
import at.atypicalbacklog.management.dto.query.PlaythroughQueryResultDto;
import at.atypicalbacklog.management.repository.PlaythroughRepository;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class PlaythroughQueryService {

    private final NamedParameterJdbcTemplate jdbc;
    private final PlaythroughRepository repository;

    public PlaythroughQueryService(NamedParameterJdbcTemplate jdbc, PlaythroughRepository repository) {
        this.jdbc = jdbc;
        this.repository = repository;
    }

    public PlaythroughResponse getByUid(String uid) {
        Playthrough pt = repository.findById(uid)
            .orElseThrow(() -> new IllegalArgumentException("Playthrough not found"));

        return toResponse(pt);
    }

    private PlaythroughResponse toResponse(Playthrough pt) {
        return new PlaythroughResponse(
            pt.getUid(),
            pt.getGameInstanceUid(),
            pt.getPlaythroughTypeUid(),
            pt.getInputUid(),
            pt.getStartedAt(),
            pt.getPlaythroughStateUid()
        );
    }

    public List<PlaythroughQueryResultDto> findByGameInstance(
        String gameInstanceUid
    ) {

        String sql = """
            SELECT
              pt.uid,
              pt.playthrough_state_uid,
              pt.playthrough_type_uid,
              pt.input_uid,
              pt.started_at
            FROM playthrough pt
            WHERE pt.game_instance_uid = :uid
            ORDER BY pt.creation_date DESC
        """;

        return jdbc.query(
            sql,
            Map.of("uid", gameInstanceUid),
            this::mapRow
        );
    }

    private PlaythroughQueryResultDto mapRow(ResultSet rs, int i)
        throws java.sql.SQLException {

        return new PlaythroughQueryResultDto(
            rs.getString("uid"),
            rs.getString("playthrough_state_uid"),
            rs.getString("playthrough_type_uid"),
            rs.getString("input_uid"),
            rs.getObject("started_at", LocalDate.class)
        );
    }
}
