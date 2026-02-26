package at.atypicalbacklog.management.reference.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import at.atypicalbacklog.management.reference.domain.PlaythroughState;

import java.util.List;
import java.util.Optional;

public interface PlaythroughStateRepository
    extends JpaRepository<PlaythroughState, String> {

    List<PlaythroughState> findAllByOrderByNameAsc();
    Optional<PlaythroughState> findByName(String name);
}
