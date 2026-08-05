package at.atypicalbacklog.management.moderation;

import java.util.List;

public record ProfanityResult(
    boolean containsProfanity,
    List<String> matchedWords
) {
    public static ProfanityResult clean() {
        return new ProfanityResult(false, List.of());
    }
}
