package at.atypicalbacklog.management.importing;

public enum ResolutionStatus {
    CREATED, // newly saved
    UPDATED, // updated by user / other process
    CURATED, // marked as curated by user
    FINALIZED, // marked as finalized by user, ready for import to production
    IMPORTED, // successfully imported to production, no further changes allowed
    REJECTED // rejected by user, no further changes allowed
}
