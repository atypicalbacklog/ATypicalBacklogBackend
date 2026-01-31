package at.atypicalbacklog.management.repository;

import at.atypicalbacklog.management.domain.Variant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VariantRepository
    extends JpaRepository<Variant, String> {

    List<Variant> findAllByOrderByNameAsc();
}
