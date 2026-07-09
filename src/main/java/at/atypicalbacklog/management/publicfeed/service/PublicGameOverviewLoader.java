package at.atypicalbacklog.management.publicfeed.service;

import at.atypicalbacklog.management.game.dto.GameOverviewRowDto;
import at.atypicalbacklog.management.publicfeed.dto.PublicGameFeedRowDto;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PublicGameOverviewLoader {

    private static final String INSTANCE_OVERVIEW_SQL = """
        SELECT
          false                         AS is_dlc,
          g.uid                         AS game_uid,
          gi.uid                        AS game_instance_uid,
          NULL::text                    AS dlc_uid,
          true                          AS has_instance,

          g.name_base                   AS game_name,
          g.first_release_date          AS game_release_date,

          gi.name                       AS instance_name,
          gi.release_date               AS instance_release_date,
          gi.received_date              AS instance_received_date,
          COALESCE(gi.in_posession, false) AS in_posession,

          p.uid                         AS platform_uid,
          p.name                        AS platform_name,
          v.uid                         AS variant_uid,
          v.name                        AS variant_name,

          (SELECT img.uid FROM game_image img
           WHERE img.game_uid = g.uid
             AND img.owner_type = 'GAME'
             AND img.type = 'COVER'
           ORDER BY img.primary_image DESC NULLS LAST, img.creation_date DESC
           LIMIT 1)                     AS game_cover_image_uid,
          (SELECT img.uid FROM game_image img
           WHERE img.game_instance_uid = gi.uid
             AND img.owner_type = 'GAME_INSTANCE'
             AND img.type = 'COVER'
           ORDER BY img.primary_image DESC NULLS LAST, img.creation_date DESC
           LIMIT 1)                     AS instance_cover_image_uid,
          NULL::text                    AS dlc_cover_image_uid,

          NULL::text                    AS dlc_name,
          NULL::date                    AS dlc_release_date,

          EXISTS (
            SELECT 1 FROM backlog b
            WHERE b.game_uid = g.uid AND b.removed_date IS NULL
          )                             AS in_backlog,

          EXISTS (
            SELECT 1 FROM playthrough pt
            JOIN playthrough_state ps ON ps.uid = pt.playthrough_state_uid
            WHERE pt.game_instance_uid = gi.uid
              AND ps.name = 'Active'
          )                             AS has_active_playthrough,

          (
            SELECT gc.completion_state_uid
            FROM game_completion gc
            WHERE gc.game_instance_uid = gi.uid AND gc.dlc_uid IS NULL
            ORDER BY gc.creation_date DESC
            LIMIT 1
          )                             AS completion_state_uid,

          COALESCE((
            SELECT SUM(
              EXTRACT(EPOCH FROM (COALESCE(tb.end_time, NOW()) - tb.start_time))
            )
            FROM time_booking tb
            JOIN playthrough pt ON pt.uid = tb.playthrough_uid
            WHERE pt.game_instance_uid = gi.uid
          ), 0)::bigint                 AS total_playtime_seconds,

          EXISTS (
            SELECT 1 FROM dlc d WHERE d.game_instance_uid = gi.uid
          )                             AS has_dlc

        FROM game_instance gi
        JOIN game g ON g.uid = gi.game_uid
        LEFT JOIN platform p ON p.uid = gi.platform_uid
        LEFT JOIN variant v ON v.uid = gi.variant_uid
        WHERE gi.uid IN (:instanceUids)
        """;

    private static final String GAME_ONLY_OVERVIEW_SQL = """
        SELECT
          false                         AS is_dlc,
          g.uid                         AS game_uid,
          NULL::text                    AS game_instance_uid,
          NULL::text                    AS dlc_uid,
          false                         AS has_instance,

          g.name_base                   AS game_name,
          g.first_release_date          AS game_release_date,

          NULL::text                    AS instance_name,
          NULL::date                    AS instance_release_date,
          NULL::date                    AS instance_received_date,
          false                         AS in_posession,

          NULL::text                    AS platform_uid,
          NULL::text                    AS platform_name,
          NULL::text                    AS variant_uid,
          NULL::text                    AS variant_name,

          (SELECT img.uid FROM game_image img
           WHERE img.game_uid = g.uid
             AND img.owner_type = 'GAME'
             AND img.type = 'COVER'
           ORDER BY img.primary_image DESC NULLS LAST, img.creation_date DESC
           LIMIT 1)                     AS game_cover_image_uid,
          NULL::text                    AS instance_cover_image_uid,
          NULL::text                    AS dlc_cover_image_uid,

          NULL::text                    AS dlc_name,
          NULL::date                    AS dlc_release_date,

          EXISTS (
            SELECT 1 FROM backlog b
            WHERE b.game_uid = g.uid AND b.removed_date IS NULL
          )                             AS in_backlog,

          NULL::boolean                 AS has_active_playthrough,
          NULL::text                    AS completion_state_uid,
          NULL::bigint                  AS total_playtime_seconds,
          false                         AS has_dlc

        FROM game g
        WHERE g.uid IN (:gameUids)
        """;

    private final NamedParameterJdbcTemplate jdbc;

    public PublicGameOverviewLoader(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public Map<String, GameOverviewRowDto> loadOverviewsByInstanceUid(Collection<String> instanceUids) {
        if (instanceUids == null || instanceUids.isEmpty()) {
            return Map.of();
        }
        return jdbc.query(
            INSTANCE_OVERVIEW_SQL,
            Map.of("instanceUids", instanceUids),
            (rs, rowNum) -> mapRow(rs)
        ).stream().collect(Collectors.toMap(
            GameOverviewRowDto::gameInstanceUid,
            row -> row,
            (a, b) -> a
        ));
    }

    public Map<String, GameOverviewRowDto> loadOverviewsByGameUid(Collection<String> gameUids) {
        if (gameUids == null || gameUids.isEmpty()) {
            return Map.of();
        }
        return jdbc.query(
            GAME_ONLY_OVERVIEW_SQL,
            Map.of("gameUids", gameUids),
            (rs, rowNum) -> mapRow(rs)
        ).stream().collect(Collectors.toMap(
            GameOverviewRowDto::gameUid,
            row -> row,
            (a, b) -> a
        ));
    }

    public PublicGameFeedRowDto toFeedRow(String gameUid, String gameInstanceUid, GameOverviewRowDto overview) {
        String imageUid = resolveImageUid(overview);
        String name = resolveName(overview);
        return new PublicGameFeedRowDto(gameUid, gameInstanceUid, imageUid, name, overview);
    }

    public Map<String, PublicGameFeedRowDto> loadFeedRows(
        List<GameInstanceRef> refs
    ) {
        Map<String, GameOverviewRowDto> byInstance = loadOverviewsByInstanceUid(
            refs.stream()
                .map(GameInstanceRef::gameInstanceUid)
                .filter(uid -> uid != null && !uid.isBlank())
                .distinct()
                .toList()
        );

        Map<String, GameOverviewRowDto> byGame = loadOverviewsByGameUid(
            refs.stream()
                .filter(ref -> ref.gameInstanceUid() == null || ref.gameInstanceUid().isBlank())
                .map(GameInstanceRef::gameUid)
                .distinct()
                .toList()
        );

        Map<String, PublicGameFeedRowDto> result = new HashMap<>();
        for (GameInstanceRef ref : refs) {
            GameOverviewRowDto overview = ref.gameInstanceUid() != null && !ref.gameInstanceUid().isBlank()
                ? byInstance.get(ref.gameInstanceUid())
                : byGame.get(ref.gameUid());
            if (overview != null) {
                String key = ref.gameInstanceUid() != null ? ref.gameInstanceUid() : ref.gameUid();
                result.put(key, toFeedRow(ref.gameUid(), ref.gameInstanceUid(), overview));
            }
        }
        return result;
    }

    static String resolveImageUid(GameOverviewRowDto overview) {
        if (overview.instanceCoverImageUid() != null) {
            return overview.instanceCoverImageUid();
        }
        return overview.gameCoverImageUid();
    }

    static String resolveName(GameOverviewRowDto overview) {
        if (overview.instanceName() != null && !overview.instanceName().isBlank()) {
            return overview.instanceName();
        }
        return overview.gameName();
    }

    private GameOverviewRowDto mapRow(ResultSet rs) throws SQLException {
        return new GameOverviewRowDto(
            rs.getBoolean("is_dlc"),
            rs.getString("game_uid"),
            rs.getString("game_instance_uid"),
            rs.getString("dlc_uid"),
            rs.getBoolean("has_instance"),
            rs.getString("game_name"),
            rs.getObject("game_release_date", LocalDate.class),
            rs.getString("instance_name"),
            rs.getObject("instance_release_date", LocalDate.class),
            rs.getObject("instance_received_date", LocalDate.class),
            rs.getBoolean("in_posession"),
            rs.getString("platform_uid"),
            rs.getString("platform_name"),
            rs.getString("variant_uid"),
            rs.getString("variant_name"),
            rs.getString("game_cover_image_uid"),
            rs.getString("instance_cover_image_uid"),
            rs.getString("dlc_cover_image_uid"),
            rs.getString("dlc_name"),
            rs.getObject("dlc_release_date", LocalDate.class),
            rs.getBoolean("in_backlog"),
            rs.getObject("has_active_playthrough", Boolean.class),
            rs.getString("completion_state_uid"),
            rs.getObject("total_playtime_seconds", Long.class),
            rs.getBoolean("has_dlc")
        );
    }

    public record GameInstanceRef(String gameUid, String gameInstanceUid) {}
}
