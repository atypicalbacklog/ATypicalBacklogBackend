package at.atypicalbacklog.management.notify.service;

import at.atypicalbacklog.management.notify.domain.AdminDigestState;
import at.atypicalbacklog.management.notify.repository.AdminDigestStateRepository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class AdminDigestDiscordService {

    private static final Logger log = LoggerFactory.getLogger(AdminDigestDiscordService.class);
    private static final DateTimeFormatter TS =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("UTC"));
    /** Discord message content hard limit is 2000. */
    private static final int DISCORD_CONTENT_LIMIT = 1900;

    private final boolean enabled;
    private final String webhookUrl;
    private final int detailLimit;
    private final NamedParameterJdbcTemplate jdbc;
    private final AdminDigestStateRepository digestStateRepo;
    private final RestClient restClient;
    private final ObjectMapper mapper = new ObjectMapper();

    public AdminDigestDiscordService(
        @Value("${app.discord.digest.enabled:false}") boolean enabled,
        @Value("${app.discord.digest.webhook-url:}") String webhookUrl,
        @Value("${app.discord.digest.detail-limit:10}") int detailLimit,
        NamedParameterJdbcTemplate jdbc,
        AdminDigestStateRepository digestStateRepo
    ) {
        this.enabled = enabled;
        this.webhookUrl = webhookUrl == null ? "" : webhookUrl.trim();
        this.detailLimit = Math.max(1, detailLimit);
        this.jdbc = jdbc;
        this.digestStateRepo = digestStateRepo;
        this.restClient = RestClient.create();
    }

    @Transactional
    public void sendDailyDigestIfNeeded() {
        if (!enabled) {
            return;
        }
        if (webhookUrl.isBlank()) {
            log.warn("Admin Discord digest enabled but webhook-url is blank; skipping");
            return;
        }

        Instant now = Instant.now();
        Instant since = digestStateRepo.findById(AdminDigestState.DIGEST_ID)
            .map(AdminDigestState::getLastProcessedAt)
            .orElse(now.minusSeconds(24 * 60 * 60));

        List<RecommendationRow> recommendations = loadNewRecommendations(since);
        List<CommentRow> comments = loadNewComments(since);

        if (recommendations.isEmpty() && comments.isEmpty()) {
            log.debug("Admin Discord digest: no new submissions or comments since {}", since);
            return;
        }

        String body = buildBody(since, now, recommendations, comments);
        for (String chunk : splitForDiscord(body)) {
            postWebhook(chunk);
        }

        log.info(
            "Admin Discord digest sent: {} recommendations, {} comments",
            recommendations.size(),
            comments.size()
        );

        AdminDigestState state = digestStateRepo.findById(AdminDigestState.DIGEST_ID)
            .orElseGet(() -> new AdminDigestState(AdminDigestState.DIGEST_ID, now));
        state.setLastProcessedAt(now);
        digestStateRepo.save(state);
    }

    private void postWebhook(String content) {
        try {
            ObjectNode payload = mapper.createObjectNode();
            payload.put("content", content);
            restClient.post()
                .uri(webhookUrl)
                .header("Content-Type", "application/json")
                .body(mapper.writeValueAsString(payload))
                .retrieve()
                .toBodilessEntity();
        } catch (Exception e) {
            throw new IllegalStateException("Discord webhook post failed: " + e.getMessage(), e);
        }
    }

    private static List<String> splitForDiscord(String body) {
        if (body.length() <= DISCORD_CONTENT_LIMIT) {
            return List.of(body);
        }
        List<String> chunks = new ArrayList<>();
        int start = 0;
        int part = 1;
        while (start < body.length()) {
            int end = Math.min(start + DISCORD_CONTENT_LIMIT - 40, body.length());
            if (end < body.length()) {
                int nl = body.lastIndexOf('\n', end);
                if (nl > start + 200) {
                    end = nl + 1;
                }
            }
            String header = part == 1 ? "" : "(cont. " + part + ")\n";
            chunks.add(header + body.substring(start, end));
            start = end;
            part++;
        }
        return chunks;
    }

    private List<RecommendationRow> loadNewRecommendations(Instant since) {
        String sql = """
            SELECT br.uid, br.name, br.state, br.creation_date, br.game_uid,
                   br.original_recommendation_by, br.comment
            FROM backlog_recommendation br
            WHERE br.creation_date > :since
            ORDER BY br.creation_date ASC
            """;
        return jdbc.query(
            sql,
            Map.of("since", Timestamp.from(since)),
            (rs, rowNum) -> new RecommendationRow(
                rs.getString("uid"),
                rs.getString("name"),
                rs.getString("state"),
                toInstant(rs.getTimestamp("creation_date")),
                rs.getString("game_uid"),
                rs.getString("original_recommendation_by"),
                rs.getString("comment")
            )
        );
    }

    private List<CommentRow> loadNewComments(Instant since) {
        String sql = """
            SELECT
              c.uid,
              c.comment,
              c.creation_date,
              c.moderation_flagged,
              c.moderation_reason,
              c.moderation_score,
              br.uid AS br_uid,
              br.name AS br_name,
              br.state AS br_state
            FROM backlog_recommendation_comment c
            JOIN backlog_recommendation br ON br.uid = c.backlog_recommendation_uid
            WHERE c.creation_date > :since
            ORDER BY c.creation_date ASC
            """;
        return jdbc.query(
            sql,
            Map.of("since", Timestamp.from(since)),
            (rs, rowNum) -> new CommentRow(
                rs.getString("uid"),
                rs.getString("comment"),
                toInstant(rs.getTimestamp("creation_date")),
                rs.getBoolean("moderation_flagged"),
                rs.getString("moderation_reason"),
                rs.getObject("moderation_score") == null ? null : rs.getDouble("moderation_score"),
                rs.getString("br_uid"),
                rs.getString("br_name"),
                rs.getString("br_state")
            )
        );
    }

    private String buildBody(
        Instant since,
        Instant until,
        List<RecommendationRow> recommendations,
        List<CommentRow> comments
    ) {
        long flagged = comments.stream().filter(CommentRow::flagged).count();
        StringBuilder sb = new StringBuilder();
        sb.append("**ATypicalBacklog admin digest**\n");
        sb.append("Period (UTC): ").append(TS.format(since)).append(" → ").append(TS.format(until)).append("\n");
        sb.append(recommendations.size()).append(" recommendation(s), ")
            .append(comments.size()).append(" comment(s)");
        if (flagged > 0) {
            sb.append(" (").append(flagged).append(" flagged)");
        }
        sb.append("\n\n");

        sb.append("**New backlog recommendations (").append(recommendations.size()).append(")**\n");
        if (recommendations.isEmpty()) {
            sb.append("(none)\n");
        } else {
            int shown = 0;
            for (RecommendationRow r : recommendations) {
                if (shown >= detailLimit) {
                    sb.append("… and ").append(recommendations.size() - detailLimit).append(" more\n");
                    break;
                }
                sb.append("- `").append(TS.format(r.creationDate())).append("` | `").append(r.uid())
                    .append("` | state=").append(r.state())
                    .append(" | name=").append(r.name())
                    .append(" | by=").append(nullToDash(r.originalRecommendationBy()))
                    .append('\n');
                if (r.comment() != null && !r.comment().isBlank()) {
                    sb.append("  ownerComment: ").append(r.comment()).append('\n');
                }
                shown++;
            }
        }

        sb.append("\n**New comments (").append(comments.size())
            .append(", flagged=").append(flagged).append(")**\n");
        if (comments.isEmpty()) {
            sb.append("(none)\n");
        } else {
            int shown = 0;
            for (CommentRow c : comments) {
                if (shown >= detailLimit) {
                    sb.append("… and ").append(comments.size() - detailLimit).append(" more\n");
                    break;
                }
                sb.append("- `").append(TS.format(c.creationDate())).append("` | br=`").append(c.brUid())
                    .append("` (").append(c.brName()).append(", ").append(c.brState()).append(')')
                    .append(" | flagged=").append(c.flagged());
                if (c.flagged()) {
                    sb.append(" [").append(nullToDash(c.moderationReason()))
                        .append(" score=").append(c.moderationScore()).append(']');
                }
                sb.append('\n');
                sb.append("  text: ").append(c.comment()).append('\n');
                shown++;
            }
        }

        return sb.toString();
    }

    private static String nullToDash(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }

    private static Instant toInstant(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toInstant();
    }

    private record RecommendationRow(
        String uid,
        String name,
        String state,
        Instant creationDate,
        String gameUid,
        String originalRecommendationBy,
        String comment
    ) {}

    private record CommentRow(
        String uid,
        String comment,
        Instant creationDate,
        boolean flagged,
        String moderationReason,
        Double moderationScore,
        String brUid,
        String brName,
        String brState
    ) {}
}
