package at.atypicalbacklog.management.config.validation;

public final class PublicInputValidation {

    public static final int MAX_FEED_COUNT = 1000;
    public static final int MAX_RECOMMENDATION_NAME_LENGTH = 500;
    public static final int MAX_COMMENT_LENGTH = 1000;

    private PublicInputValidation() {}

    public static void requireFeedCount(int count) {
        if (count <= 0) {
            throw new IllegalArgumentException("count must be positive");
        }
        if (count > MAX_FEED_COUNT) {
            throw new IllegalArgumentException("count must be at most " + MAX_FEED_COUNT);
        }
    }

    public static void requireRecentActivityCounts(int totalCount, int maxPerCategory) {
        if (totalCount <= 0) {
            throw new IllegalArgumentException("totalCount must be positive");
        }
        if (totalCount > MAX_FEED_COUNT) {
            throw new IllegalArgumentException("totalCount must be at most " + MAX_FEED_COUNT);
        }
        if (maxPerCategory <= 0) {
            throw new IllegalArgumentException("maxPerCategory must be positive");
        }
        if (maxPerCategory > MAX_FEED_COUNT) {
            throw new IllegalArgumentException("maxPerCategory must be at most " + MAX_FEED_COUNT);
        }
    }

    public static void requireRecommendationName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name is required");
        }
        if (name.length() > MAX_RECOMMENDATION_NAME_LENGTH) {
            throw new IllegalArgumentException(
                "name must be at most " + MAX_RECOMMENDATION_NAME_LENGTH + " characters"
            );
        }
    }

    public static void requireComment(String comment) {
        if (comment == null || comment.isBlank()) {
            throw new IllegalArgumentException("comment is required");
        }
        if (comment.length() > MAX_COMMENT_LENGTH) {
            throw new IllegalArgumentException(
                "comment must be at most " + MAX_COMMENT_LENGTH + " characters"
            );
        }
    }
}
