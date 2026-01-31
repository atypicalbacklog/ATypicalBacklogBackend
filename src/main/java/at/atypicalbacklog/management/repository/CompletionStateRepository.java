package at.atypicalbacklog.management.repository;

import at.atypicalbacklog.management.domain.CompletionState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompletionStateRepository
    extends JpaRepository<CompletionState, String> {

    List<CompletionState> findAllByOrderByOrderAsc();
}
