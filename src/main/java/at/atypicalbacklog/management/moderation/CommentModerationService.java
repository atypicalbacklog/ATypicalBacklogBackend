package at.atypicalbacklog.management.moderation;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Two-stage local moderation: profanity dictionary then optional ONNX toxicity.
 * Always allows store; sets flag metadata for admin review.
 */
@Service
public class CommentModerationService {

    private final boolean enabled;
    private final ProfanityService profanityService;
    private final ToxicityService toxicityService;

    public CommentModerationService(
        @Value("${app.moderation.enabled:true}") boolean enabled,
        ProfanityService profanityService,
        ToxicityService toxicityService
    ) {
        this.enabled = enabled;
        this.profanityService = profanityService;
        this.toxicityService = toxicityService;
    }

    public CommentModerationResult moderate(String text) {
        if (!enabled) {
            return CommentModerationResult.unchecked();
        }

        ProfanityResult profanity = profanityService.check(text);
        ToxicityResult toxicity = toxicityService.analyze(text);

        boolean toxic =
            toxicity.available() && toxicity.score() >= toxicityService.threshold();
        boolean flagged = profanity.containsProfanity() || toxic;

        if (!flagged) {
            Double score = toxicity.available() ? toxicity.score() : null;
            return CommentModerationResult.clean(score);
        }

        List<String> reasons = new ArrayList<>();
        if (profanity.containsProfanity()) {
            reasons.add("PROFANITY:" + String.join(",", profanity.matchedWords()));
        }
        if (toxic) {
            String labels = toxicity.labels().isEmpty()
                ? "toxic"
                : toxicity.labels().stream().collect(Collectors.joining(","));
            reasons.add("TOXICITY:" + labels);
        }

        Double score = toxicity.available() ? toxicity.score() : null;
        String reason = String.join("|", reasons);
        if (reason.length() > 200) {
            reason = reason.substring(0, 197) + "...";
        }
        return CommentModerationResult.flagged(reason, score);
    }
}
