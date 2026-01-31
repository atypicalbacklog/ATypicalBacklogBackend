package at.atypicalbacklog.management.repository;

import at.atypicalbacklog.management.domain.Platform;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlatformRepository
    extends JpaRepository<Platform, String> {

    List<Platform> findAllByOrderByNameAsc();
}
