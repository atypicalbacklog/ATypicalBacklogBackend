package at.atypicalbacklog.management.notify;

import at.atypicalbacklog.management.notify.service.AdminDigestDiscordService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.discord.digest.enabled", havingValue = "true")
public class AdminDigestDiscordScheduler {

    private static final Logger log = LoggerFactory.getLogger(AdminDigestDiscordScheduler.class);

    private final AdminDigestDiscordService digestService;

    public AdminDigestDiscordScheduler(AdminDigestDiscordService digestService) {
        this.digestService = digestService;
    }

    @Scheduled(
        cron = "${app.discord.digest.cron:0 0 8 * * *}",
        zone = "${app.timezone:Europe/Vienna}"
    )
    public void runDailyDigest() {
        log.debug("Running scheduled admin Discord digest");
        digestService.sendDailyDigestIfNeeded();
    }
}
