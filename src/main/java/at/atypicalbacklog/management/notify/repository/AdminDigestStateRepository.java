package at.atypicalbacklog.management.notify.repository;

import at.atypicalbacklog.management.notify.domain.AdminDigestState;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminDigestStateRepository extends JpaRepository<AdminDigestState, String> {
}
