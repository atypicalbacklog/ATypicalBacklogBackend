package at.atypicalbacklog.management.dto.query;

import java.util.List;

public record GameOverviewQueryRequest(

    String searchText,                // LIKE %text%
    List<String> platformUids,
    List<String> consoleUids,

    Boolean inBacklog,
    Boolean hasActivePlaythrough,
    List<String> completionStateUids,

    SortMode sortMode

) {
    public enum SortMode {
        NAME_ASC,
        NAME_DESC,
        PLAYTIME_DESC,
        LAST_PLAYED_DESC
    }
}
