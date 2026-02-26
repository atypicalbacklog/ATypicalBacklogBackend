package at.atypicalbacklog.management.reference.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import at.atypicalbacklog.management.reference.domain.Tier;

import java.util.List;

public interface TierRepository
    extends JpaRepository<Tier, String> {

    List<Tier> findAllByOrderByNameAsc();
}
