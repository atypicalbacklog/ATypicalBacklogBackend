package at.atypicalbacklog.management.repository;

import at.atypicalbacklog.management.domain.Dlc;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DlcRepository extends JpaRepository<Dlc, String> {

    List<Dlc> findByGameInstanceUidOrderByCreationDateAsc(String gameInstanceUid);
}
