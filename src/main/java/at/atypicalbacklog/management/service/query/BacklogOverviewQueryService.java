package at.atypicalbacklog.management.service.query;

import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import at.atypicalbacklog.management.dto.query.backlog.BacklogOverviewGameDto;
import at.atypicalbacklog.management.dto.query.backlog.BacklogOverviewInstanceDto;
import at.atypicalbacklog.management.dto.query.backlog.BacklogOverviewQueryRequest;

@Service
public class BacklogOverviewQueryService {

        private static final String BASE_SQL = """
    SELECT
      g.uid AS game_uid,
      g.name_base AS game_name,
      g.first_release_date,

      b.uid AS backlog_uid,
      b.added_date,
      b.reason,

      gi.uid AS instance_uid,
      gi.name AS instance_name,
      gi.release_date AS instance_release_date,
      gi.received_date,
      gi.in_posession,
      gi.original_console_uid,

      p.uid AS platform_uid,
      p.name AS platform_name,

      v.uid AS variant_uid,

      gc.completion_state_uid,

      pt.uid AS active_playthrough_uid,

      COALESCE((
          SELECT SUM(
              EXTRACT(EPOCH FROM (COALESCE(tb.end_time, NOW()) - tb.start_time))
          )
          FROM time_booking tb
          JOIN playthrough ptt ON ptt.uid = tb.playthrough_uid
          WHERE ptt.game_instance_uid = gi.uid
      ), 0) AS total_playtime_seconds,

                      COALESCE(
                        (
                          SELECT img.uid FROM game_image img
                          WHERE img.game_instance_uid = gi.uid
                          ORDER BY img.creation_date DESC
                          LIMIT 1
                        ),
                        (
                          SELECT img.uid FROM game_image img
                          WHERE img.game_uid = g.uid
                          ORDER BY img.creation_date DESC
                          LIMIT 1
                        )
                      ) AS thumbnail_image_uid

    FROM backlog b
    JOIN game g ON g.uid = b.game_uid
    LEFT JOIN game_instance gi ON gi.game_uid = g.uid
    LEFT JOIN platform p ON p.uid = gi.platform_uid
    LEFT JOIN variant v ON v.uid = gi.variant_uid

    LEFT JOIN LATERAL (
        SELECT pt.uid
        FROM playthrough pt
        JOIN playthrough_state ps ON ps.uid = pt.playthrough_state_uid
        WHERE pt.game_instance_uid = gi.uid
          AND ps.name = 'Active'
        LIMIT 1
    ) pt ON true

    LEFT JOIN LATERAL (
        SELECT gc.completion_state_uid
        FROM game_completion gc
        WHERE gc.game_instance_uid = gi.uid
        ORDER BY gc.creation_date DESC
        LIMIT 1
    ) gc ON true

    WHERE b.removed_date IS NULL
    """;
    private final NamedParameterJdbcTemplate jdbc;

    public BacklogOverviewQueryService(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<BacklogOverviewGameDto> query(BacklogOverviewQueryRequest req) {

        String sql = BASE_SQL;
        Map<String, Object> params = new HashMap<>();

        if (req.searchText() != null) {
            sql += " AND LOWER(g.name_base) LIKE LOWER(:search)";
            params.put("search", "%" + req.searchText() + "%");
        }

        // other filters omitted here for brevity but follow same pattern

        List<Row> rows = jdbc.query(sql, params, this::mapRow);

        return groupAndScore(rows);
    }

    private List<BacklogOverviewGameDto> groupAndScore(List<Row> rows) {
        Map<String, List<Row>> byGame = new LinkedHashMap<>();

        for (Row r : rows) {
            byGame.computeIfAbsent(r.gameUid, k -> new ArrayList<>()).add(r);
        }

        List<BacklogOverviewGameDto> result = new ArrayList<>();

        for (List<Row> gameRows : byGame.values()) {
            result.add(buildGame(gameRows));
        }

        result.sort(Comparator.comparingInt(BacklogOverviewGameDto::backlogScore).reversed());
        return result;
    }

    private BacklogOverviewGameDto buildGame(List<Row> rows) {

        Row first = rows.get(0);

        List<BacklogOverviewInstanceDto> instances = rows.stream()
            .filter(r -> r.instanceUid != null)
            .map(r -> new BacklogOverviewInstanceDto(
                r.instanceUid,
                r.platformUid,
                r.platformName,
                r.originalConsoleUid,
                r.variantUid,
                r.instanceName,
                r.instanceReleaseDate,
                r.receivedDate,
                r.inPossession,
                r.completionStateUid,
                r.activePlaythroughUid,
                r.totalPlaytimeSeconds
            ))
            .toList();

        int score = calculateScore(first, instances);

        return new BacklogOverviewGameDto(
            first.gameUid,
            first.gameName,
            first.gameReleaseDate,
            first.backlogUid,
            first.backlogAddedDate,
            first.backlogReason,
            score,
            instances.stream().anyMatch(i -> i.activePlaythroughUid() != null),
            instances.stream().anyMatch(i -> i.activePlaythroughUid() != null),
            instances,
            first.thumbnailImageUid
        );
    }

    /**
     * Calculates a score for a game based on its backlog instances and game release information.
     * Uses a regular local variable for the score, since there is no need for it to be final.
     * Note: Refactored the .ifPresent() logic to avoid mutation within a lambda,
     *       which is considered best practice in Java.
     */
    private int calculateScore(Row row, List<BacklogOverviewInstanceDto> instances) {
        int score = 0;

        if (!instances.isEmpty()) {
            score += 1000;
        }
        if (instances.stream().filter(BacklogOverviewInstanceDto::inPossession).count() > 1) {
            score += 500;
        }

        if (row.gameReleaseDate != null) {
            score += ChronoUnit.MONTHS.between(row.gameReleaseDate, LocalDate.now());
        }

        // Avoid mutation inside lambda; fetch minDate first and then mutate
        Optional<LocalDate> minReceivedDate = instances.stream()
            .map(BacklogOverviewInstanceDto::receivedDate)
            .filter(Objects::nonNull)
            .min(LocalDate::compareTo);

        if (minReceivedDate.isPresent()) {
            score += ChronoUnit.WEEKS.between(minReceivedDate.get(), LocalDate.now());
        }

        return score;
    }

    private Row mapRow(ResultSet rs, int i) throws java.sql.SQLException {
        return new Row(
            rs.getString("game_uid"),
            rs.getString("game_name"),
            rs.getObject("first_release_date", LocalDate.class),
            rs.getString("backlog_uid"),
            rs.getObject("added_date", LocalDate.class),
            rs.getString("reason"),
            rs.getString("instance_uid"),
            rs.getString("instance_name"),
            rs.getObject("instance_release_date", LocalDate.class),
            rs.getObject("received_date", LocalDate.class),
            rs.getBoolean("in_posession"),
            rs.getString("original_console_uid"),
            rs.getString("platform_uid"),
            rs.getString("platform_name"),
            rs.getString("variant_uid"),
            rs.getString("completion_state_uid"),
            rs.getString("active_playthrough_uid"),
            rs.getLong("total_playtime_seconds"),
            rs.getString("thumbnail_image_uid")
        );
    }

    private record Row(
        String gameUid,
        String gameName,
        LocalDate gameReleaseDate,
        String backlogUid,
        LocalDate backlogAddedDate,
        String backlogReason,
        String instanceUid,
        String instanceName,
        LocalDate instanceReleaseDate,
        LocalDate receivedDate,
        boolean inPossession,
        String originalConsoleUid,
        String platformUid,
        String platformName,
        String variantUid,
        String completionStateUid,
        String activePlaythroughUid,
        long totalPlaytimeSeconds,
        String thumbnailImageUid
    ) {}
}
