package at.atypicalbacklog.management.importing.domain;

import jakarta.persistence.*;
import java.time.Instant;

import at.atypicalbacklog.management.importing.ImportType;

@Entity
@Table(name = "import_batch")
public class ImportBatch {

    @Id
    private String uid;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ImportType type;

    @Column(nullable = false)
    private String status;

    protected ImportBatch() {}

    public ImportBatch(String uid, ImportType type) {
        this.uid = uid;
        this.type = type;
        this.createdAt = Instant.now();
        this.status = "CREATED";
    }

    public void markStaged() { this.status = "STAGED"; }
    public void markValidated() { this.status = "READY_FOR_COMMIT"; }
    public void markFailed() { this.status = "FAILED"; }

    public String getStatus() { return status; }
    public String getUid() { return uid; }
    public ImportType getType() { return type; }
}