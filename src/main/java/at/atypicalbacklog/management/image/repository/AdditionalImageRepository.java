package at.atypicalbacklog.management.image.repository;

import at.atypicalbacklog.management.image.domain.AdditionalImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdditionalImageRepository extends JpaRepository<AdditionalImage, String> {
}
