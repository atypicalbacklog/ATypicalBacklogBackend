package at.atypicalbacklog.management.moderation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProfanityServiceTest {

    private ProfanityService service;

    @BeforeEach
    void setUp() {
        service = new ProfanityService(true, "classpath:profanity");
        service.loadDictionaries();
    }

    @Test
    void detectsPlainAndObfuscatedEnglish() {
        assertTrue(service.check("fuck").containsProfanity());
        assertTrue(service.check("f.u.c.k").containsProfanity());
        assertTrue(service.check("FUCK").containsProfanity());
        assertTrue(service.check("f_u_c_k").containsProfanity());
        assertTrue(service.check("fuuuuuck").containsProfanity());
        assertTrue(service.check("sh1t").containsProfanity());
    }

    @Test
    void detectsGermanVariants() {
        assertTrue(service.check("Arschloch").containsProfanity());
        assertTrue(service.check("arsch.loch").containsProfanity());
        assertTrue(service.check("a.r.s.c.h.l.o.c.h").containsProfanity());
        assertTrue(service.check("FICKEN").containsProfanity());
    }

    @Test
    void avoidsScunthorpeFalsePositives() {
        assertFalse(service.check("class").containsProfanity());
        assertFalse(service.check("assistant").containsProfanity());
        assertFalse(service.check("passage").containsProfanity());
        assertFalse(service.check("assignment").containsProfanity());
        assertFalse(service.check("Scunthorpe").containsProfanity());
    }
}
