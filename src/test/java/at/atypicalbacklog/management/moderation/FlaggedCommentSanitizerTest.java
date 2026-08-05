package at.atypicalbacklog.management.moderation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class FlaggedCommentSanitizerTest {

    private final FlaggedCommentSanitizer sanitizer =
        new FlaggedCommentSanitizer("Comment has been flagged for: ");

    @Test
    void leavesUnflaggedCommentUntouched() {
        assertEquals("hello", sanitizer.sanitizeCommentText(false, null, "hello"));
    }

    @Test
    void replacesFlaggedCommentWithReasonCategoryOnly() {
        assertEquals(
            "Comment has been flagged for: PROFANITY",
            sanitizer.sanitizeCommentText(true, "PROFANITY:fuck", "actual bad text")
        );
    }

    @Test
    void combinesMultipleReasonCategories() {
        assertEquals(
            "Comment has been flagged for: PROFANITY, TOXICITY",
            sanitizer.sanitizeCommentText(true, "PROFANITY:fuck|TOXICITY:label_0", "text")
        );
    }

    @Test
    void usesFallbackReasonWhenMissing() {
        assertEquals(
            "Comment has been flagged for: review",
            sanitizer.sanitizeCommentText(true, null, "actual bad text")
        );
    }
}
