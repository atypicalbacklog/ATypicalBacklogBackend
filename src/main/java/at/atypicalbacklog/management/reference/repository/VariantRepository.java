package at.atypicalbacklog.management.reference.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import at.atypicalbacklog.management.reference.domain.Variant;

import java.util.List;

public interface VariantRepository
    extends JpaRepository<Variant, String> {

    List<Variant> findAllByOrderByNameAsc();
}
