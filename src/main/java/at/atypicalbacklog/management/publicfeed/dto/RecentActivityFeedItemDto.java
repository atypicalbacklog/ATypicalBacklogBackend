package at.atypicalbacklog.management.publicfeed.dto;

public record RecentActivityFeedItemDto(
    String category,
    PublicGameFeedRowDto row
) {}
