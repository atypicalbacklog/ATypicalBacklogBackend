package at.atypicalbacklog.management.moderation;

/**
 * Result of commenting moderation. Always allows store; may flag for admin review.
 * Placeholder until local profanity/toxicity pipeline is implemented.
 */
public record CommentModerationResult(
    boolean flagged,
    String reason,
    Double score,
    boolean checked
) {
    public static CommentModerationResult unchecked() {
        return new CommentModerationResult(false, null, null, false);
    }

    public static CommentModerationResult clean(Double score) {
        return new CommentModerationResult(false, null, score, true);
    }

    public static CommentModerationResult flagged(String reason, Double score) {
        return new CommentModerationResult(true, reason, score, true);
    }
}
