package at.atypicalbacklog.management.importing.repository;

import at.atypicalbacklog.management.importing.domain.ImportRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImportRecordRepository
        extends JpaRepository<ImportRecord, String> {

    List<ImportRecord> findByImportBatchUid(String importBatchUid);
}