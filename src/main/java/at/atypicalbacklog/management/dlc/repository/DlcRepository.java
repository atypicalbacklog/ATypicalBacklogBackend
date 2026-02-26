package at.atypicalbacklog.management.dlc.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import at.atypicalbacklog.management.dlc.domain.Dlc;

import java.util.List;

public interface DlcRepository extends JpaRepository<Dlc, String> {

    List<Dlc> findByGameInstanceUidOrderByCreationDateAsc(String gameInstanceUid);
}
