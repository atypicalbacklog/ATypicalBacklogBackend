package at.atypicalbacklog.management.importing.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import at.atypicalbacklog.management.importing.ImportType;
import at.atypicalbacklog.management.importing.domain.ImportBatch;
import at.atypicalbacklog.management.importing.domain.ImportRecord;
import at.atypicalbacklog.management.importing.parser.ImportCsvParserService;
import at.atypicalbacklog.management.importing.repository.ImportBatchRepository;
import at.atypicalbacklog.management.importing.repository.ImportRecordRepository;
import jakarta.transaction.Transactional;

@Service
public class ImportBatchService {

    private final ImportBatchRepository batchRepo;
    private final ImportRecordRepository recordRepo;
    private final ImportCsvParserService parser;
    
    public ImportBatchService(ImportBatchRepository batchRepo, ImportRecordRepository recordRepo, ImportCsvParserService parser) {
        this.batchRepo = batchRepo;
        this.recordRepo = recordRepo;
        this.parser = parser;
    }

    @Transactional
    public String createBatch(
        ImportType type,
        MultipartFile file
    ) {
        try {
            ImportBatch batch =
                new ImportBatch(UUID.randomUUID().toString(), type);
    
            List<ImportRecord> records = parser.parseImportRecords(type, file);
            records.forEach(record -> record.setImportBatchUid(batch.getUid()));

            batchRepo.save(batch);
            recordRepo.saveAll(records);
    
            return "Import of Type " + type + " successfull! " + records.size() + "records saved for batch " + batch.getUid();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create batch", e);
        }

    }
}