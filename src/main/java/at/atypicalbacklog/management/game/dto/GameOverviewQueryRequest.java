package at.atypicalbacklog.management.game.dto;

import java.util.List;

public record GameOverviewQueryRequest(

    String searchText,                // LIKE %text%
    List<String> platformUids,
    List<String> consoleUids,

    Boolean inBacklog,
    Boolean hasActivePlaythrough,
    List<String> completionStateUids,
    List<String> playthroughTypeUids,  // filter by playthrough type UIDs
    Boolean includeDlc,                // when true, include DLC rows in result
    Boolean onlyGamesWithDlc,          // true: only games with ≥1 DLC; false: only games with no DLC; null: no filter

    SortMode sortMode,

    Integer pageSize,                  // optional keyset page size
    String cursorGameName,             // keyset cursor (NAME_ASC)
    String cursorInstanceName,         // keyset cursor (NAME_ASC)
    String cursorDlcName,              // keyset cursor (NAME_ASC)
    String cursorGameUid,              // keyset cursor (NAME_ASC)
    String cursorGameInstanceUid,      // keyset cursor (NAME_ASC)
    String cursorDlcUid,               // keyset cursor (NAME_ASC; null means empty sentinel)
    Boolean cursorIsDlc                // keyset cursor (NAME_ASC)

) {
    public enum SortMode {
        NAME_ASC,
        NAME_DESC,
        PLAYTIME_DESC,
        LAST_PLAYED_DESC,
        NEWEST_CREATED,
        RELEASE_DATE
    }
}
