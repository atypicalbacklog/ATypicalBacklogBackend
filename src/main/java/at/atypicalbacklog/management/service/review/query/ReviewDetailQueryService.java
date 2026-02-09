package at.atypicalbacklog.management.service.review.query;

import at.atypicalbacklog.management.dto.review.query.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ReviewDetailQueryService {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final NamedParameterJdbcTemplate jdbc;

    public ReviewDetailQueryService(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public Optional<ReviewDetailDto> getByUid(String reviewUid) {

        String sql = """
            SELECT
              r.uid AS review_uid,
              r.review_date,
              r.tier_uid,
              r.text,

              pt.uid AS playthrough_uid,
              pt.playthrough_type_uid,
              pt.playthrough_state_uid,

              COALESCE(gi.name, g.name_base) AS display_name,

              COALESCE(
                (SELECT img.uid FROM game_image img
                 WHERE img.game_instance_uid = gi.uid
                   AND img.type = 'COVER'
                 ORDER BY img.primary_image DESC NULLS LAST
                 LIMIT 1),
                (SELECT img.uid FROM game_image img
                 WHERE img.game_uid = g.uid
                   AND img.type = 'COVER'
                 ORDER BY img.primary_image DESC NULLS LAST
                 LIMIT 1)
              ) AS cover_image_uid,

              (SELECT COALESCE(json_agg(
                json_build_object(
                  'uid', pt2.uid,
                  'playthroughTypeUid', pt2.playthrough_type_uid,
                  'playthroughStateUid', pt2.playthrough_state_uid
                )
              ) FILTER (WHERE pt2.uid IS NOT NULL), '[]'::json)
               FROM playthrough pt2
               WHERE pt2.game_instance_uid = gi.uid
                 AND pt2.year = pt.year
                 AND pt2.uid != pt.uid) AS other_playthroughs_json

            FROM review r
            JOIN playthrough pt ON pt.uid = r.playthrough_uid
            JOIN game_instance gi ON gi.uid = pt.game_instance_uid
            JOIN game g ON g.uid = gi.game_uid
            WHERE r.uid = :reviewUid
            """;

        List<ReviewDetailDto> rows = jdbc.query(
            sql,
            Map.of("reviewUid", reviewUid),
            this::mapRow
        );

        return rows.isEmpty() ? Optional.empty() : Optional.of(rows.get(0));
    }

    public List<ReviewDetailDto> getByGameInstanceUid(String gameInstanceUid) {

        String sql = """
            SELECT
              r.uid AS review_uid,
              r.review_date,
              r.tier_uid,
              r.text,

              pt.uid AS playthrough_uid,
              pt.playthrough_type_uid,
              pt.playthrough_state_uid,

              COALESCE(gi.name, g.name_base) AS display_name,

              COALESCE(
                (SELECT img.uid FROM game_image img
                 WHERE img.game_instance_uid = gi.uid
                   AND img.type = 'COVER'
                 ORDER BY img.primary_image DESC NULLS LAST
                 LIMIT 1),
                (SELECT img.uid FROM game_image img
                 WHERE img.game_uid = g.uid
                   AND img.type = 'COVER'
                 ORDER BY img.primary_image DESC NULLS LAST
                 LIMIT 1)
              ) AS cover_image_uid,

              (SELECT COALESCE(json_agg(
                json_build_object(
                  'uid', pt2.uid,
                  'playthroughTypeUid', pt2.playthrough_type_uid,
                  'playthroughStateUid', pt2.playthrough_state_uid
                )
              ) FILTER (WHERE pt2.uid IS NOT NULL), '[]'::json)
               FROM playthrough pt2
               WHERE pt2.game_instance_uid = gi.uid
                 AND pt2.year = pt.year
                 AND pt2.uid != pt.uid) AS other_playthroughs_json

            FROM review r
            JOIN playthrough pt ON pt.uid = r.playthrough_uid
            JOIN game_instance gi ON gi.uid = pt.game_instance_uid
            JOIN game g ON g.uid = gi.game_uid
            WHERE gi.uid = :gameInstanceUid
            ORDER BY r.review_date DESC NULLS LAST, r.creation_date DESC
            """;

        return jdbc.query(sql, Map.of("gameInstanceUid", gameInstanceUid), this::mapRow);
    }

    private ReviewDetailDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new ReviewDetailDto(
            rs.getString("review_uid"),
            rs.getObject("review_date", LocalDate.class),
            rs.getString("tier_uid"),
            rs.getString("text"),

            new LinkedPlaythroughDto(
                rs.getString("playthrough_uid"),
                rs.getString("playthrough_type_uid"),
                rs.getString("playthrough_state_uid")
            ),

            rs.getString("display_name"),
            rs.getString("cover_image_uid"),

            parseOtherPlaythroughs(rs.getString("other_playthroughs_json"))
        );
    }

    private static List<LinkedPlaythroughDto> parseOtherPlaythroughs(String json) {
        if (json == null || json.isBlank() || "[]".equals(json.trim())) {
            return List.of();
        }
        try {
            JsonNode arr = MAPPER.readTree(json);
            List<LinkedPlaythroughDto> list = new ArrayList<>();
            for (JsonNode node : arr) {
                list.add(new LinkedPlaythroughDto(
                    node.has("uid") && !node.get("uid").isNull() ? node.get("uid").asText() : null,
                    node.has("playthroughTypeUid") && !node.get("playthroughTypeUid").isNull()
                        ? node.get("playthroughTypeUid").asText() : null,
                    node.has("playthroughStateUid") && !node.get("playthroughStateUid").isNull()
                        ? node.get("playthroughStateUid").asText() : null
                ));
            }
            return list;
        } catch (Exception e) {
            return List.of();
        }
    }
}
