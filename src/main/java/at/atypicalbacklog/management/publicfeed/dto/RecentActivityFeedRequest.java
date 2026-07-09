package at.atypicalbacklog.management.publicfeed.dto;

public record RecentActivityFeedRequest(
    int totalCount,
    int maxPerCategory
) {}
