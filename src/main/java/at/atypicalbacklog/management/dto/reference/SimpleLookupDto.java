package at.atypicalbacklog.management.dto.reference;

public record SimpleLookupDto(
    String uid,
    String name,
    int order
) {}
