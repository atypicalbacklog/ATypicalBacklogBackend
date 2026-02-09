package at.atypicalbacklog.management.repository;

import at.atypicalbacklog.management.domain.Tier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TierRepository
    extends JpaRepository<Tier, String> {

    List<Tier> findAllByOrderByNameAsc();
}
