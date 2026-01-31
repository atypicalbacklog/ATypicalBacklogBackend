package at.atypicalbacklog.management.repository;

import at.atypicalbacklog.management.domain.PlaythroughState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlaythroughStateRepository
    extends JpaRepository<PlaythroughState, String> {

    List<PlaythroughState> findAllByOrderByNameAsc();
}
