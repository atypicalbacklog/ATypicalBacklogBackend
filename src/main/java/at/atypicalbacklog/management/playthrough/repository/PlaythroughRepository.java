package at.atypicalbacklog.management.playthrough.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import at.atypicalbacklog.management.playthrough.domain.Playthrough;

import java.util.List;

public interface PlaythroughRepository extends JpaRepository<Playthrough, String> {

    List<Playthrough> findByPlaythroughStateUid(String stateUid);
}
