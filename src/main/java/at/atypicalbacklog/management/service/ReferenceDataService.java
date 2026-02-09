package at.atypicalbacklog.management.service;

import at.atypicalbacklog.management.dto.reference.*;
import at.atypicalbacklog.management.repository.*;
import org.springframework.stereotype.Service;

import java.time.Instant;

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
}
