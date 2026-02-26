package at.atypicalbacklog.management.reference.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import at.atypicalbacklog.management.reference.domain.CompletionState;

import java.util.List;

public interface CompletionStateRepository
    extends JpaRepository<CompletionState, String> {

    List<CompletionState> findAllByOrderByOrderAsc();
    java.util.Optional<CompletionState> findByName(String name);
    java.util.Optional<CompletionState> findByNameIgnoreCase(String name);
}
