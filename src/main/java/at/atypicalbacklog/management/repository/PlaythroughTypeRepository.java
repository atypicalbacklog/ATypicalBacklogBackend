package at.atypicalbacklog.management.repository;

import at.atypicalbacklog.management.domain.PlaythroughType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlaythroughTypeRepository
    extends JpaRepository<PlaythroughType, String> {

    List<PlaythroughType> findAllByOrderByNameAsc();
}
