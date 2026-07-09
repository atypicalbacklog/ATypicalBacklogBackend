package at.atypicalbacklog.management.publicfeed.service;

import at.atypicalbacklog.management.backlog.dto.BacklogOverviewGameDto;
import at.atypicalbacklog.management.backlog.dto.BacklogOverviewInstanceDto;
import at.atypicalbacklog.management.backlog.dto.BacklogOverviewQueryRequest;
import at.atypicalbacklog.management.backlog.service.BacklogOverviewQueryService;
import at.atypicalbacklog.management.config.validation.PublicInputValidation;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;
import java.util.List;

@Service
public class PublicRandomBacklogFeedQueryService {

    private final BacklogOverviewQueryService backlogOverviewQueryService;

    public PublicRandomBacklogFeedQueryService(BacklogOverviewQueryService backlogOverviewQueryService) {
        this.backlogOverviewQueryService = backlogOverviewQueryService;
    }

    public List<BacklogOverviewGameDto> query(int count) {
        PublicInputValidation.requireFeedCount(count);

        List<BacklogOverviewGameDto> all = backlogOverviewQueryService.query(
            new BacklogOverviewQueryRequest(null, null, null, null, null)
        );

        List<BacklogOverviewGameDto> prepared = new ArrayList<>(all);
        for (int i = 0; i < prepared.size(); i++) {
            BacklogOverviewGameDto game = prepared.get(i);
            List<BacklogOverviewInstanceDto> sortedInstances = game.instances().stream()
                .sorted(instancePreferenceComparator())
                .toList();
            String coverImageUid = sortedInstances.stream()
                .map(BacklogOverviewInstanceDto::coverImageUid)
                .filter(uid -> uid != null && !uid.isBlank())
                .findFirst()
                .orElse(game.coverImageUid());
            prepared.set(i, new BacklogOverviewGameDto(
                game.gameUid(),
                game.gameName(),
                game.gameReleaseDate(),
                game.backlogUid(),
                game.backlogAddedDate(),
                game.backlogReason(),
                game.backlogScore(),
                game.hasAnyPlaythrough(),
                game.hasActivePlaythrough(),
                sortedInstances,
                coverImageUid
            ));
        }

        Collections.shuffle(prepared);
        return prepared.stream().limit(count).toList();
    }

    private Comparator<BacklogOverviewInstanceDto> instancePreferenceComparator() {
        return Comparator
            .comparing((BacklogOverviewInstanceDto i) -> i.completionStateUid() != null).reversed()
            .thenComparing(i -> i.activePlaythroughUid() != null || !i.playthroughs().isEmpty()).reversed()
            .thenComparing(BacklogOverviewInstanceDto::receivedDate, Comparator.nullsLast(Comparator.reverseOrder()));
    }
}
