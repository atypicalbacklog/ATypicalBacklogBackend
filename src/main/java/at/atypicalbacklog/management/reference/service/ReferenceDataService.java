package at.atypicalbacklog.management.reference.service;

import at.atypicalbacklog.management.reference.dto.*;
import at.atypicalbacklog.management.reference.repository.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReferenceDataService {

    private static final String VERSION = "2026-01";

    private final CompletionStateRepository completionStateRepo;
    private final ConsoleRepository consoleRepo;
    private final PlatformRepository platformRepo;
    private final InputRepository inputRepo;
    private final VariantRepository variantRepo;
    private final PlaythroughStateRepository playthroughStateRepo;
    private final PlaythroughTypeRepository playthroughTypeRepo;
    private final TierRepository tierRepo;

    public ReferenceDataService(
        CompletionStateRepository completionStateRepo,
        ConsoleRepository consoleRepo,
        PlatformRepository platformRepo,
        InputRepository inputRepo,
        VariantRepository variantRepo,
        PlaythroughStateRepository playthroughStateRepo,
        PlaythroughTypeRepository playthroughTypeRepo,
        TierRepository tierRepo
    ) {
        this.completionStateRepo = completionStateRepo;
        this.consoleRepo = consoleRepo;
        this.platformRepo = platformRepo;
        this.inputRepo = inputRepo;
        this.variantRepo = variantRepo;
        this.playthroughStateRepo = playthroughStateRepo;
        this.playthroughTypeRepo = playthroughTypeRepo;
        this.tierRepo = tierRepo;
    }

    public ReferenceDataResponse loadAll() {
        return new ReferenceDataResponse(
            VERSION,
            Instant.now(),
            new ReferenceDataPayload(
                completionStateRepo.findAllByOrderByOrderAsc()
                    .stream()
                    .map(e -> new SimpleLookupDto(e.getUid(), e.getName(), e.getOrder()))
                    .toList(),

                consoleRepo.findAllByOrderByNameAsc()
                    .stream()
                    .map(e -> new ConsoleDto(e.getUid(), e.getName(), e.getFullname()))
                    .toList(),

                platformRepo.findAllByOrderByNameAsc()
                    .stream()
                    .map(e -> new PlatformDto(
                        e.getUid(),
                        e.getName(),
                        e.getConsoleUid(),
                        e.isHideInChoices()
                    ))
                    .toList(),

                inputRepo.findAllByOrderByNameAsc()
                    .stream()
                    .map(e -> new InputDto(e.getUid(), e.getName(), e.getPlatformUid()))
                    .toList(),

                variantRepo.findAllByOrderByNameAsc()
                    .stream()
                    .map(e -> new SimpleLookupDto(e.getUid(), e.getName(), 0))
                    .toList(),

                playthroughStateRepo.findAllByOrderByNameAsc()
                    .stream()
                    .map(e -> new SimpleLookupDto(e.getUid(), e.getName(), 0))
                    .toList(),

                playthroughTypeRepo.findAllByOrderByNameAsc()
                    .stream()
                    .map(e -> new SimpleLookupDto(e.getUid(), e.getName(), 0))
                    .toList(),

                tierRepo.findAllByOrderByNameAsc()
                    .stream()
                    .map(e -> new SimpleLookupDto(e.getUid(), e.getName(), e.getOrder()))
                    .toList()
            )
        );
    }

    /**
     * Utility map for backend logic checks:
     * key = platform UID, value = platform/console info (uids + names).
     */
    public Map<String, PlatformConsoleInfo> loadPlatformConsoleMap() {
        var consolesByUid = consoleRepo.findAllByOrderByNameAsc()
            .stream()
            .collect(Collectors.toMap(
                c -> c.getUid(),
                c -> c
            ));

        Map<String, PlatformConsoleInfo> result = new HashMap<>();

        platformRepo.findAllByOrderByNameAsc()
            .forEach(p -> {
                var console = consolesByUid.get(p.getConsoleUid());
                result.put(
                    p.getUid(),
                    new PlatformConsoleInfo(
                        p.getUid(),
                        p.getName(),
                        p.getConsoleUid(),
                        console != null ? console.getName() : null
                    )
                );
            });

        return result;
    }

    /** Utility map: variant UID -> variant name. */
    public Map<String, String> loadVariantNameMap() {
        return variantRepo.findAllByOrderByNameAsc()
            .stream()
            .collect(Collectors.toMap(
                v -> v.getUid(),
                v -> v.getName()
            ));
    }

    /** Utility map: completion state UID -> completion state name. */
    public Map<String, String> loadCompletionStateNameMap() {
        return completionStateRepo.findAllByOrderByOrderAsc()
            .stream()
            .collect(Collectors.toMap(
                cs -> cs.getUid(),
                cs -> cs.getName()
            ));
    }

    /** Utility map: playthrough type UID -> playthrough type name. */
    public Map<String, String> loadPlaythroughTypeNameMap() {
        return playthroughTypeRepo.findAllByOrderByNameAsc()
            .stream()
            .collect(Collectors.toMap(
                t -> t.getUid(),
                t -> t.getName()
            ));
    }

    /** Utility map: console UID -> console name. */
    public Map<String, String> loadConsoleNameMap() {
        return consoleRepo.findAllByOrderByNameAsc()
            .stream()
            .collect(Collectors.toMap(
                c -> c.getUid(),
                c -> c.getName()
            ));
    }

    /** Utility map: platform UID -> platform name. */
    public Map<String, String> loadPlatformNameMap() {
        return platformRepo.findAllByOrderByNameAsc()
            .stream()
            .collect(Collectors.toMap(
                p -> p.getUid(),
                p -> p.getName()
            ));
    }

    /** Simple aggregated view used by {@link #loadPlatformConsoleMap()}. */
    public record PlatformConsoleInfo(
        String platformUid,
        String platformName,
        String consoleUid,
        String consoleName
    ) {}
}
