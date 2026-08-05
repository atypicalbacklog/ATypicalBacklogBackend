package at.atypicalbacklog.management.moderation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TextNormalizerTest {

    @Test
    void normalizesLeetspeakPunctuationAndRepeats() {
        assertEquals("fuck", TextNormalizer.normalize("f.u.c.k"));
        assertEquals("fuck", TextNormalizer.normalize("fuuuuuck"));
        assertEquals("shit", TextNormalizer.normalize("sh1t"));
        assertEquals("arschloch", TextNormalizer.normalize("a.r.s.c.h.l.o.c.h"));
    }
}
