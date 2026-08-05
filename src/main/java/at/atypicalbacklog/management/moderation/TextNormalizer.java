package at.atypicalbacklog.management.moderation;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Normalizes comment text for dictionary matching (leetspeak, punctuation, repeats).
 */
public final class TextNormalizer {

    private static final Pattern NON_LETTER_DIGIT = Pattern.compile("[^\\p{L}\\p{Nd}\\s]+");
    private static final Pattern WHITESPACE = Pattern.compile("\\s+");
    private static final Pattern REPEAT_LETTERS = Pattern.compile("(\\p{L})\\1{2,}");

    private TextNormalizer() {}

    public static String normalize(String input) {
        if (input == null || input.isBlank()) {
            return "";
        }
        String s = input.toLowerCase(Locale.ROOT);
        s = Normalizer.normalize(s, Normalizer.Form.NFKD);
        s = s.replaceAll("\\p{M}+", "");
        s = applyLeetspeak(s);
        s = NON_LETTER_DIGIT.matcher(s).replaceAll("");
        s = REPEAT_LETTERS.matcher(s).replaceAll("$1");
        s = WHITESPACE.matcher(s).replaceAll(" ").trim();
        return s;
    }

    public static List<String> tokens(String normalized) {
        if (normalized == null || normalized.isBlank()) {
            return List.of();
        }
        String[] parts = normalized.split(" ");
        List<String> tokens = new ArrayList<>(parts.length);
        for (String part : parts) {
            if (!part.isBlank()) {
                tokens.add(part);
            }
        }
        return tokens;
    }

    private static String applyLeetspeak(String s) {
        StringBuilder sb = new StringBuilder(s.length());
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            sb.append(switch (c) {
                case '0' -> 'o';
                case '1' -> 'i';
                case '3' -> 'e';
                case '4' -> 'a';
                case '5' -> 's';
                case '7' -> 't';
                case '@' -> 'a';
                case '$' -> 's';
                default -> c;
            });
        }
        return sb.toString();
    }
}
