package at.atypicalbacklog.management.external.igdb.dto;

public record IgdbTokenResponse(
    String access_token,
    long expires_in,
    String token_type
) {}