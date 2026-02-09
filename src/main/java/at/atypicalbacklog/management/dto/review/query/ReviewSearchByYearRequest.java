package at.atypicalbacklog.management.dto.review.query;

import java.util.List;

public record ReviewSearchByYearRequest(
    List<Integer> years,
    boolean includeActivePlaythroughs
) {}