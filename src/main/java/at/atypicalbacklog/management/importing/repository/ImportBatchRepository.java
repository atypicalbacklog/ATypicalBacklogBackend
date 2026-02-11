package at.atypicalbacklog.management.importing.repository;

import at.atypicalbacklog.management.importing.domain.ImportBatch;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImportBatchRepository
        extends JpaRepository<ImportBatch, String> {
}