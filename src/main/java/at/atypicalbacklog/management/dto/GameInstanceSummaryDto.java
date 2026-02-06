package at.atypicalbacklog.management.dto;

/**
 * Summary of a game instance: uid, name, and cover image for listing instances by game.
 */
public record GameInstanceSummaryDto(
    String uid,
    String name,
    String coverImageUid
) {}
