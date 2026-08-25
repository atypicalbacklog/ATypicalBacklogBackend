package at.atypicalbacklog.management.config;

import org.springframework.stereotype.Component;

import java.time.Instant;

/** Process start time — changes on every JVM restart / redeploy. */
@Component
public class AppRuntimeInfo {

    private final Instant serverStartedAt = Instant.now();

    public Instant getServerStartedAt() {
        return serverStartedAt;
    }
}
