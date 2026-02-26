package at.atypicalbacklog.management.reference.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import at.atypicalbacklog.management.reference.domain.PlaythroughType;

import java.util.List;

public interface PlaythroughTypeRepository
    extends JpaRepository<PlaythroughType, String> {

    List<PlaythroughType> findAllByOrderByNameAsc();
}
