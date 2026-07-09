package at.atypicalbacklog.management.publicfeed.service;

import at.atypicalbacklog.management.config.validation.PublicInputValidation;
import at.atypicalbacklog.management.game.dto.GameOverviewRowDto;
import at.atypicalbacklog.management.publicfeed.dto.PublicSTierReviewFeedItemDto;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class PublicRandomSTierReviewFeedQueryService {

    private final NamedParameterJdbcTemplate jdbc;
    private final PublicGameOverviewLoader overviewLoader;

    public PublicRandomSTierReviewFeedQueryService(
        NamedParameterJdbcTemplate jdbc,
        PublicGameOverviewLoader overviewLoader
    ) {
        this.jdbc = jdbc;
        this.overviewLoader = overviewLoader;
    }

    public List<PublicSTierReviewFeedItemDto> query(int count) {
        PublicInputValidation.requireFeedCount(count);

        String sql = """
            SELECT
              r.uid AS review_uid,
              r.review_date,
              r.tier_uid,
              r.text AS review_text,
              r.preferred_image_uid,
              r.presentation_type,
              pt.uid AS playthrough_uid,
              pt.game_instance_uid,
              g.uid AS game_uid
            FROM review r
            JOIN tier t ON t.uid = r.tier_uid
            JOIN playthrough pt ON pt.uid = r.playthrough_uid
            JOIN game_instance gi ON gi.uid = pt.game_instance_uid
            JOIN game g ON g.uid = gi.game_uid
            WHERE t.name = 'S'
            ORDER BY RANDOM()
            LIMIT :count
            """;

        List<ReviewRow> rows = jdbc.query(sql, Map.of("count", count), this::mapReviewRow);
        List<PublicSTierReviewFeedItemDto> result = new ArrayList<>();

        for (ReviewRow row : rows) {
            GameOverviewRowDto overview = overviewLoader
                .loadOverviewsByInstanceUid(List.of(row.gameInstanceUid()))
                .get(row.gameInstanceUid());
            if (overview == null) {
                continue;
            }
            result.add(new PublicSTierReviewFeedItemDto(
                row.gameUid(),
                row.gameInstanceUid(),
                PublicGameOverviewLoader.resolveImageUid(overview),
                PublicGameOverviewLoader.resolveName(overview),
                overview,
                row.reviewUid(),
                row.reviewDate(),
                row.tierUid(),
                row.reviewText(),
                row.preferredImageUid(),
                row.presentationType(),
                row.playthroughUid()
            ));
        }
        return result;
    }

    private ReviewRow mapReviewRow(ResultSet rs, int rowNum) throws SQLException {
        return new ReviewRow(
            rs.getString("review_uid"),
            rs.getObject("review_date", LocalDate.class),
            rs.getString("tier_uid"),
            rs.getString("review_text"),
            rs.getString("preferred_image_uid"),
            rs.getString("presentation_type"),
            rs.getString("playthrough_uid"),
            rs.getString("game_instance_uid"),
            rs.getString("game_uid")
        );
    }

    private record ReviewRow(
        String reviewUid,
        LocalDate reviewDate,
        String tierUid,
        String reviewText,
        String preferredImageUid,
        String presentationType,
        String playthroughUid,
        String gameInstanceUid,
        String gameUid
    ) {}
}
