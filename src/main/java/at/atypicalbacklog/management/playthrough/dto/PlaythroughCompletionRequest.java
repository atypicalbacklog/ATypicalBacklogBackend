package at.atypicalbacklog.management.playthrough.dto;

import java.util.List;

public record PlaythroughCompletionRequest(
    UpdatePlaythroughRequest playthrough,
    List<GameCompletionUpdate> gameCompletions,
    List<String> dlcUids,
    BacklogUpdate backlog
) {}
