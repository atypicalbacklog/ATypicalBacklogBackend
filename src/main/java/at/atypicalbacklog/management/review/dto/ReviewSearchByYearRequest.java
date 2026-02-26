package at.atypicalbacklog.management.review.dto;

import java.util.List;

public record ReviewSearchByYearRequest(
    List<Integer> years,
    boolean includeActivePlaythroughs
) {}