package at.atypicalbacklog.management.moderation;

import java.util.List;

public record ToxicityResult(
    double score,
    List<String> labels,
    double confidence,
    boolean available
) {
    public static ToxicityResult unavailable() {
        return new ToxicityResult(0, List.of(), 0, false);
    }
}
