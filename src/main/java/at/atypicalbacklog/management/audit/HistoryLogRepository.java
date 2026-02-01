package at.atypicalbacklog.management.audit;

import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoryLogRepository extends JpaRepository<HistoryLogEntry, String> {
}
