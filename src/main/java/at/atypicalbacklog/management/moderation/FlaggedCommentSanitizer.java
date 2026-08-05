package at.atypicalbacklog.management.moderation;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Sanitizes flagged follow-up comments for API/DTO responses. DB values stay unchanged.
 */
@Component
public class FlaggedCommentSanitizer {

    private final String flaggedMessagePrefix;

    public FlaggedCommentSanitizer(
        @Value("${app.moderation.flagged-comment-message-prefix:Comment has been flagged for: }")
        String flaggedMessagePrefix
    ) {
        this.flaggedMessagePrefix = flaggedMessagePrefix;
    }

    public String sanitizeCommentText(boolean flagged, String moderationReason, String comment) {
        if (!flagged) {
            return comment;
        }
        String reason = formatReasonForDisplay(moderationReason);
        return flaggedMessagePrefix + reason;
    }

    /**
     * DB stores e.g. {@code PROFANITY:fuck|TOXICITY:label}; UI shows category only.
     */
    static String formatReasonForDisplay(String moderationReason) {
        if (moderationReason == null || moderationReason.isBlank()) {
            return "review";
        }
        String[] parts = moderationReason.split("\\|");
        StringBuilder out = new StringBuilder();
        for (String part : parts) {
            String trimmed = part.trim();
            if (trimmed.isEmpty()) {
                continue;
            }
            String category = trimmed.contains(":")
                ? trimmed.substring(0, trimmed.indexOf(':')).trim()
                : trimmed;
            if (category.isEmpty()) {
                continue;
            }
            if (!out.isEmpty()) {
                out.append(", ");
            }
            out.append(category);
        }
        return out.isEmpty() ? "review" : out.toString();
    }
}
