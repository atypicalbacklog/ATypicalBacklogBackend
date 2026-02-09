package at.atypicalbacklog.management.dto.query.backlog;

import java.util.List;

public record BacklogOverviewQueryRequest(

    /**
     * Rough search on game name and instance name (LIKE %text%)
     */
    String searchText,

    /**
     * Filter by console UIDs.
     * Matches either:
     * - original_console_uid of any instance
     * - console derived from platform of any instance
     */
    List<String> consoleUids,

    /**
     * Filter by platform UIDs (instance platform).
     */
    List<String> platformUids,

    /**
     * Only include games where at least one instance
     * has an active playthrough.
     */
    Boolean hasActivePlaythrough,

    /**
     * Only include games where at least one instance
     * has a completion with one of these states.
     */
    List<String> completionStateUids
) {}
