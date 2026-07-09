package at.atypicalbacklog.management.publicfeed.service;

import at.atypicalbacklog.management.backlogrecommendation.domain.BacklogRecommendationState;
import at.atypicalbacklog.management.backlogrecommendation.dto.GameRecommendationDto;
import at.atypicalbacklog.management.config.validation.PublicInputValidation;
import at.atypicalbacklog.management.game.dto.GameOverviewRowDto;
import at.atypicalbacklog.management.publicfeed.dto.PublicRandomBacklogRecommendationFeedItemDto;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class PublicRandomBacklogRecommendationFeedQueryService {

    private final NamedParameterJdbcTemplate jdbc;
    private final PublicGameOverviewLoader overviewLoader;

    public PublicRandomBacklogRecommendationFeedQueryService(
        NamedParameterJdbcTemplate jdbc,
        PublicGameOverviewLoader overviewLoader
    ) {
        this.jdbc = jdbc;
        this.overviewLoader = overviewLoader;
    }

    public List<PublicRandomBacklogRecommendationFeedItemDto> query(int count) {
        PublicInputValidation.requireFeedCount(count);

        String sql = """
            WITH ranked AS (
              SELECT
                br.uid,
                br.name,
                br.state,
                br.state_reason,
                br.state_date,
                br.votecount,
                br.game_uid,
                gr.uid AS gr_uid,
                gr.creation_date AS gr_creation_date,
                gr.name_base AS gr_name_base,
                gr.release_date AS gr_release_date,
                gr.external_igdb_id AS gr_external_igdb_id,
                RANK() OVER (ORDER BY br.votecount DESC, br.creation_date ASC) AS vote_rank,
                (
                  SELECT gi.uid
                  FROM game_instance gi
                  WHERE gi.game_uid = br.game_uid
                  ORDER BY gi.creation_date DESC
                  LIMIT 1
                ) AS game_instance_uid
              FROM backlog_recommendation br
              JOIN game_recommendation gr ON gr.uid = br.game_recommendation_uid
            )
            SELECT *
            FROM ranked
            ORDER BY RANDOM()
            LIMIT :count
            """;

        List<RecommendationRow> rows = jdbc.query(sql, Map.of("count", count), this::mapRow);
        List<PublicRandomBacklogRecommendationFeedItemDto> result = new ArrayList<>();

        for (RecommendationRow row : rows) {
            GameOverviewRowDto overview = null;
            String imageUid = null;
            if (row.gameInstanceUid() != null) {
                overview = overviewLoader
                    .loadOverviewsByInstanceUid(List.of(row.gameInstanceUid()))
                    .get(row.gameInstanceUid());
                if (overview != null) {
                    imageUid = PublicGameOverviewLoader.resolveImageUid(overview);
                }
            } else if (row.gameUid() != null) {
                overview = overviewLoader
                    .loadOverviewsByGameUid(List.of(row.gameUid()))
                    .get(row.gameUid());
                if (overview != null) {
                    imageUid = overview.gameCoverImageUid();
                }
            }

            result.add(new PublicRandomBacklogRecommendationFeedItemDto(
                row.uid(),
                row.name(),
                row.state(),
                row.stateReason(),
                row.stateDate(),
                row.voteCount(),
                row.voteRank(),
                row.gameUid(),
                row.gameInstanceUid(),
                imageUid,
                overview,
                new GameRecommendationDto(
                    row.grUid(),
                    row.grCreationDate(),
                    row.grNameBase(),
                    row.grReleaseDate(),
                    row.grExternalIgdbId()
                )
            ));
        }
        return result;
    }

    private RecommendationRow mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new RecommendationRow(
            rs.getString("uid"),
            rs.getString("name"),
            parseState(rs.getString("state")),
            rs.getString("state_reason"),
            rs.getObject("state_date", LocalDate.class),
            rs.getInt("votecount"),
            rs.getInt("vote_rank"),
            rs.getString("game_uid"),
            rs.getString("game_instance_uid"),
            rs.getString("gr_uid"),
            toInstant(rs.getTimestamp("gr_creation_date")),
            rs.getString("gr_name_base"),
            rs.getObject("gr_release_date", LocalDate.class),
            rs.getString("gr_external_igdb_id")
        );
    }

    private static BacklogRecommendationState parseState(String value) {
        return value == null ? null : BacklogRecommendationState.valueOf(value);
    }

    private static Instant toInstant(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toInstant();
    }

    private record RecommendationRow(
        String uid,
        String name,
        BacklogRecommendationState state,
        String stateReason,
        LocalDate stateDate,
        int voteCount,
        int voteRank,
        String gameUid,
        String gameInstanceUid,
        String grUid,
        Instant grCreationDate,
        String grNameBase,
        LocalDate grReleaseDate,
        String grExternalIgdbId
    ) {}
}
