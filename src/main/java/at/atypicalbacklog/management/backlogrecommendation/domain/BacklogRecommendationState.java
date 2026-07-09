package at.atypicalbacklog.management.backlogrecommendation.domain;

public enum BacklogRecommendationState {
    NEW,
    CONFIRMED_FOR_BACKLOG,
    FINISHED,
    REJECTED,
    UP_FOR_REPLAY,
    RESUBMIT,
    DUPLICATE
}
