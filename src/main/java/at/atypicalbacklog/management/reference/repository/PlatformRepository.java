package at.atypicalbacklog.management.reference.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import at.atypicalbacklog.management.reference.domain.Platform;

import java.util.List;

public interface PlatformRepository
    extends JpaRepository<Platform, String> {

    List<Platform> findAllByOrderByNameAsc();
}
