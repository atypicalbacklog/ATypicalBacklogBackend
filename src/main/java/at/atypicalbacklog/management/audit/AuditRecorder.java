package at.atypicalbacklog.management.audit;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class AuditRecorder {

    private final HistoryLogRepository repository;
    private final ObjectMapper objectMapper;
    private final HttpServletRequest request;

    public AuditRecorder(
        HistoryLogRepository repository,
        ObjectMapper objectMapper,
        HttpServletRequest request
    ) {
        this.repository = repository;
        this.objectMapper = objectMapper;
        this.request = request;
    }

    public void record(
        String intent,
        String affectedDomain,
        String changeType,
        Object targetUids,
        Object oldState,
        Object newState,
        Object changedData
    ) {
        try {
            HistoryLogEntry entry = new HistoryLogEntry(
                UUID.randomUUID().toString(),
                Instant.now(),
                (String) request.getAttribute(RequestIdFilter.REQUEST_ID_ATTR),
                intent,
                affectedDomain,
                changeType,
                toJson(targetUids),
                toJson(oldState),
                toJson(newState),
                toJson(changedData)
            );

            repository.save(entry);

        } catch (Exception e) {
            throw new IllegalStateException("Failed to write audit log", e);
        }
    }

    private String toJson(Object value) throws Exception {
        return value == null ? null : objectMapper.writeValueAsString(value);
    }
}
