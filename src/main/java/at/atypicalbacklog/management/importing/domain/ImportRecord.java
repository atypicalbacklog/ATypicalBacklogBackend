package at.atypicalbacklog.management.importing.domain;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.*;

@Entity
@Table(name = "import_record")
public class ImportRecord {

    @Id
    private String uid;

    @Column(nullable = false)
    private String importBatchUid;

    @Column(nullable = false)
    private int rowNumber;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", nullable = false)
    private String rawPayload;

    protected ImportRecord() {}

    public ImportRecord(String uid, String importBatchUid, int rowNumber, String rawPayload) {
        this.uid = uid;
        this.importBatchUid = importBatchUid;
        this.rowNumber = rowNumber;
        this.rawPayload = rawPayload;
    }

    public void setImportBatchUid(String importBatchUid) {
        this.importBatchUid = importBatchUid;
    }
    
    public void setRawPayload(String rawPayload) {
        this.rawPayload = rawPayload;
    }

    public String getRawPayload() {
        return this.rawPayload;
    }
}