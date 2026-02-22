package at.atypicalbacklog.management.service.calendar;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.Instant;
import java.util.Collections;

/**
 * Creates events in a Google Calendar using service account credentials.
 * When calendar ID or credentials path is blank, all operations are no-op.
 */
@Service
public class GoogleCalendarApiService {

    private static final Logger log = LoggerFactory.getLogger(GoogleCalendarApiService.class);
    private static final String APPLICATION_NAME = "AtypicalBacklog-Management";

    private final String calendarId;
    private final String credentialsPath;

    public GoogleCalendarApiService(
        @Value("${app.google.calendar.id:}") String calendarId,
        @Value("${app.google.calendar.credentials-path:}") String credentialsPath
    ) {
        this.calendarId = calendarId != null ? calendarId.trim() : "";
        this.credentialsPath = credentialsPath != null ? credentialsPath.trim() : "";
    }

    public boolean isConfigured() {
        return !calendarId.isEmpty() && !credentialsPath.isEmpty();
    }

    /**
     * Creates a new event in the configured calendar. No-op if not configured.
     *
     * @param title       event summary
     * @param description event description (plain text)
     * @param start       start time (UTC)
     * @param end         end time (UTC)
     * @return true if the event was created, false if skipped (not configured) or on error
     */
    public boolean createEvent(String title, String description, Instant start, Instant end) {
        if (!isConfigured()) {
            log.debug("Google Calendar not configured; skipping event create");
            return false;
        }
        try {
            Calendar calendar = buildCalendar();
            Event event = new Event();
            event.setSummary(title);
            event.setDescription(description);
            event.setStart(new EventDateTime().setDateTime(toCalendarDateTime(start)));
            event.setEnd(new EventDateTime().setDateTime(toCalendarDateTime(end)));
            calendar.events().insert(calendarId, event).execute();
            log.info("Created Google Calendar event: {}", title);
            return true;
        } catch (Exception e) {
            String msg = e.getMessage() != null ? e.getMessage() : e.toString();
            if (msg.contains("'type' field not specified") || msg.contains("Error reading credentials")) {
                log.warn("Google Calendar credentials invalid: {}. Use a Service Account JSON key (Google Cloud Console → IAM → Service Accounts → Keys → Add key → JSON), not OAuth client credentials.", msg);
            } else {
                log.warn("Failed to create Google Calendar event: {}", msg);
            }
            return false;
        }
    }

    private Calendar buildCalendar() throws IOException, GeneralSecurityException {
        try (FileInputStream stream = new FileInputStream(credentialsPath)) {
            GoogleCredentials credentials = GoogleCredentials.fromStream(stream)
                .createScoped(Collections.singleton(CalendarScopes.CALENDAR));
            return new Calendar.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                new HttpCredentialsAdapter(credentials))
                .setApplicationName(APPLICATION_NAME)
                .build();
        }
    }

    private static com.google.api.client.util.DateTime toCalendarDateTime(Instant instant) {
        return new com.google.api.client.util.DateTime(instant.toEpochMilli());
    }
}
