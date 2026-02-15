package at.atypicalbacklog.management.importing.service;

import at.atypicalbacklog.management.dto.reference.ReferenceDataResponse;
import at.atypicalbacklog.management.importing.ResolutionStatus;
import at.atypicalbacklog.management.importing.domain.*;
import at.atypicalbacklog.management.importing.parser.model.*;
import at.atypicalbacklog.management.importing.repository.ImportBatchRepository;
import at.atypicalbacklog.management.importing.repository.ImportRecordRepository;
import at.atypicalbacklog.management.importing.repository.StagedGameRepository;
import at.atypicalbacklog.management.importing.repository.StagedPlaythroughGroupRepository;
import at.atypicalbacklog.management.importing.repository.StagedTimeBookingRawRepository;
import at.atypicalbacklog.management.repository.PlaythroughStateRepository;
import at.atypicalbacklog.management.service.ReferenceDataService;
import at.atypicalbacklog.management.service.ReferenceDataService.PlatformConsoleInfo;
import jakarta.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class createStagingService {

    private static final Logger logger = LoggerFactory.getLogger(createStagingService.class);
    private static final int TIME_BUFFER = 4;

    /**
     * Suffixes (playthrough-type hints) to strip from the right of a game name when parsing.
     * Longest first so e.g. "July Patricia" is matched before "July".
     */
    private static final List<String> PLAYTHROUGH_TYPE_SUFFIXES = List.of(
        "etc. July Patricia",
        "July Patricia",
        "July Yve",
        "July Areen",
        "Areen Cheesé",
        "Areen Cheese",
        "Solo",
        "July",
        "Coop",
        "Jungs",
        "Areen",
        "Cheese",
        "Cheesé",
        "s2G",
        "Friends",
        "Patricia"
    );
    private final StagedGameRepository gameRepo;
    private final StagedPlaythroughGroupRepository groupRepo;
    private final StagedTimeBookingRawRepository stagedTimeBookingRawRepo;
    private final ImportBatchRepository batchRepo;
    private final ImportRecordRepository recordRepo;
    private final ReferenceDataService referenceDataService;
    private final PlaythroughStateRepository playthroughStateRepo;
    private final ObjectMapper objectMapper;

    public createStagingService(
        StagedGameRepository gameRepo,
        StagedPlaythroughGroupRepository groupRepo,
        StagedTimeBookingRawRepository stagedTimeBookingRawRepo,
        ImportBatchRepository batchRepo,
        ImportRecordRepository recordRepo,
        ReferenceDataService referenceDataService,
        PlaythroughStateRepository playthroughStateRepo,
        ObjectMapper objectMapper
    ) {
        this.gameRepo = gameRepo;
        this.groupRepo = groupRepo;
        this.stagedTimeBookingRawRepo = stagedTimeBookingRawRepo;
        this.batchRepo = batchRepo;
        this.recordRepo = recordRepo;
        this.referenceDataService = referenceDataService;
        this.playthroughStateRepo = playthroughStateRepo;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public String createStaging(String batchUid) {
        try {

        ImportBatch batch = batchRepo.findById(batchUid)
            .orElseThrow(() -> new IllegalArgumentException("Batch not found"));

        List<ImportRecord> records = recordRepo.findByImportBatchUid(batchUid);

        switch (batch.getType()) {
            case TIME_BOOKING_MIGRATION:
                createStagingForTimeBooking(batchUid, records);
                break;
            case GAME_INSTANCE_MIGRATION:
                createStagingForGameInstance(batchUid, records);
                break;
            default:
                throw new IllegalArgumentException("Invalid batch type: " + batch.getType());
        }
        // update batch states

            return "Staging created for batch " + batchUid;
        } catch (Exception e) {
            return "Error creating staging for batch " + batchUid + ": " + e.getMessage();
        }
    }

    private void createStagingForGameInstance(String batchUid, List<ImportRecord> records) throws JsonMappingException, JsonProcessingException {
        Map<String, String> completionStateNameToUid = buildCompletionStateNameToUid(referenceDataService.loadCompletionStateNameMap());
        Map<String, String> platformNameMap = referenceDataService.loadPlatformNameMap();

        for (ImportRecord record : records) {
            logger.info("Processing record: " + record.getRawPayload());
            ParsedGameInstanceRow parsedRecord = objectMapper.readValue(record.getRawPayload(), ParsedGameInstanceRow.class);
            StagedGame stagedGame = new StagedGame();
            stagedGame.setUid(UUID.randomUUID().toString());
            stagedGame.setImportBatchUid(batchUid);
            stagedGame.setProposedName(parsedRecord.gameName());
            stagedGame.setProposedReleaseDate(parsedRecord.releaseDate());
            stagedGame.setProposedReceivedDate(parsedRecord.receivedDate());
            // variant set by UI
            stagedGame.setProposedPlatformUid(platformNameMap.entrySet()
                .stream()
                .filter(entry -> entry.getValue().equals(parsedRecord.platform()))
                .map(Map.Entry::getKey)
                .findFirst().orElse(null));

            // Try to find existing game by name (searches game, instance, and DLC names)
            if (parsedRecord.gameName() != null && !parsedRecord.gameName().isBlank()) {
                String searchPattern = "%" + parsedRecord.gameName() + "%";
                gameRepo.findExistingGameUidByNameIlike(searchPattern)
                    .ifPresent(stagedGame::setMatchedGameUid);
            }

            // external id set by UI
            // original console set by UI 
            // Game completion state from parsed row (first matching rule)
            String completionKey = resolveCompletionStateKey(parsedRecord);
            if (completionKey != null) {
                Optional.ofNullable(completionStateNameToUid.get(completionKey))
                    .ifPresent(stagedGame::setProposedCompletionStateUid);
            }

            stagedGame.setBacklogStartDate(parsedRecord.backlogAdded());
            stagedGame.setBacklogEndDate(parsedRecord.backlogRemoved());
            // backlog reason set by UI
            stagedGame.setResolutionStatus(ResolutionStatus.CREATED);
            gameRepo.save(stagedGame);
        }
    }

    /**
     * Builds a map from normalized completion state name (e.g. "finishedplus", "backlog") to UID,
     * so we can look up by logical key regardless of DB name casing/spacing.
     */
    private static Map<String, String> buildCompletionStateNameToUid(Map<String, String> uidToName) {
        Map<String, String> nameToUid = new HashMap<>();
        for (Map.Entry<String, String> e : uidToName.entrySet()) {
            String normalized = e.getValue() == null ? null : e.getValue().toLowerCase().replaceAll("\\s+", "");
            if (normalized != null && !normalized.isEmpty()) {
                nameToUid.put(normalized, e.getKey());
            }
        }
        return nameToUid;
    }

    /**
     * Resolves the completion state key from parsed row (first matching rule):
     * 1. FinishedPlus true -> finishedplus
     * 2. finishedOn set OR finished true -> finished
     * 3. Started true and not in backlog -> abandoned
     * 4. Not started but still in backlog -> backlog
     * 5. Not started but backlog removed -> cancelled
     * 6. Else -> none
     */
    private static String resolveCompletionStateKey(ParsedGameInstanceRow row) {
        if (row.finishedPlus()) {
            return "finishedplus";
        }
        if (row.finishedOn() != null || row.finished()) {
            return "finished";
        }
        if (row.started() && !row.backlog()) {
            return "abandoned";
        }
        if (!row.started() && row.backlog()) {
            return "backlog";
        }
        if (!row.started() && row.backlogRemoved() != null) {
            return "cancelled";
        }
        return "none";
    }
    
    private void createStagingForTimeBooking(String batchUid, List<ImportRecord> records) throws JsonMappingException, JsonProcessingException {
        Map<String, String> playthroughMap = new HashMap<>();
        Map<String, String> completionStateUidToName = referenceDataService.loadCompletionStateNameMap();

        for (ImportRecord record : records) {
            ParsedTimeBookingRow parsedRecord = objectMapper.readValue(record.getRawPayload(), ParsedTimeBookingRow.class);
            String parsedGameName = parseGameName(parsedRecord.gameName());
            String parsedTypeUid = parseInferredType(parsedRecord.gameName());
            String mapKey = parsedGameName + " - " + parsedTypeUid;

            String stagedPlaythroughGroupUid = playthroughMap.get(mapKey);
            if (!playthroughMap.containsKey(mapKey)) {
                stagedPlaythroughGroupUid = createNewPlaythrough(batchUid, parsedGameName, parsedTypeUid, parsedRecord.date().getYear(), completionStateUidToName);
                playthroughMap.put(mapKey, stagedPlaythroughGroupUid);
            }
            
            StagedTimeBookingRaw timebooking = new StagedTimeBookingRaw();
            timebooking.setUid(UUID.randomUUID().toString());
            timebooking.setStagedPlaythroughGroupUid(stagedPlaythroughGroupUid);
            timebooking.setImportBatchUid(batchUid);
            LocalDateTime startTime = parsedRecord.date().atStartOfDay();
            startTime.plus(TIME_BUFFER, ChronoUnit.HOURS);
            timebooking.setStartTime(startTime.toInstant(ZoneOffset.UTC));
            timebooking.setEndTime(startTime.plus(parsedRecord.duration()).toInstant(ZoneOffset.UTC));
            stagedTimeBookingRawRepo.save(timebooking);
        }
    }

    private String createNewPlaythrough(String batchUid, String parsedGameName, String parsedTypeUid, int year,
                                        Map<String, String> completionStateUidToName) {
        StagedPlaythroughGroup group = new StagedPlaythroughGroup();
        String groupUid = UUID.randomUUID().toString();
        group.setUid(groupUid);
        group.setImportBatchUid(batchUid);

        // Try to find matching staged game by name; if found and inferred year is not this year, set playthrough state from game completion state
        if (parsedGameName != null && !parsedGameName.isBlank()) {
            String searchPattern = "%" + parsedGameName + "%";
            gameRepo.findByNameIlike(searchPattern).ifPresent(stagedGame -> {
                group.setStagedGameUid(stagedGame.getUid());
                if (year != Year.now().getValue()) {
                    String playthroughStateUid = resolvePlaythroughStateFromStagedGame(stagedGame.getProposedCompletionStateUid(), completionStateUidToName);
                    if (playthroughStateUid != null) {
                        group.setProposedPlaythroughStateUid(playthroughStateUid);
                    }
                }
            });
        }
        group.setImportedPlaythroughName(parsedGameName);
        group.setInferredTypeUid(parsedTypeUid);
        group.setInferredYear(year);
        group.setResolutionStatus(ResolutionStatus.CREATED);
        groupRepo.save(group);
        return group.getUid();
    }

    /**
     * When inferred year is not this year: map staged game completion state to playthrough state.
     * finishedplus or finished -> Finished; abandoned -> Abandoned; else -> Inactive.
     */
    private String resolvePlaythroughStateFromStagedGame(String completionStateUid, Map<String, String> completionStateUidToName) {
        if (completionStateUid == null || completionStateUidToName == null) {
            return playthroughStateRepo.findByName("Inactive").map(ps -> ps.getUid()).orElse(null);
        }
        String name = completionStateUidToName.get(completionStateUid);
        String key = name == null ? null : name.toLowerCase().replaceAll("\\s+", "");
        if ("finishedplus".equals(key) || "finished".equals(key)) {
            return playthroughStateRepo.findByName("Finished").map(ps -> ps.getUid()).orElse(null);
        }
        if ("abandoned".equals(key)) {
            return playthroughStateRepo.findByName("Abandoned").map(ps -> ps.getUid()).orElse(null);
        }
        return playthroughStateRepo.findByName("Inactive").map(ps -> ps.getUid()).orElse(null);
    }

    /**
     * Strips a known playthrough-type suffix from the right of the name (if present).
     * Only removes suffixes that are in {@link #PLAYTHROUGH_TYPE_SUFFIXES}.
     *
     * @param playthroughGameName e.g. "Borderlands 3 - Solo" or "Game Name July"
     * @return cleaned game name, e.g. "Borderlands 3" or "Game Name"
     */
    private String parseGameName(String playthroughGameName) {
        if (playthroughGameName == null) return "";
        String s = playthroughGameName.trim();
        for (String suffix : PLAYTHROUGH_TYPE_SUFFIXES) {
            if (s.endsWith(" " + suffix)) {
                return s.substring(0, s.length() - (" " + suffix).length()).trim();
            }
            if (s.equals(suffix)) {
                return "";
            }
        }
        return s;
    }

    /**
     * Parse the given String to identify the Type of the Playthrough
     * Checks first rightside word of string, if not a number
     * If can't map it, returns Casual UID (fallback)
     * @param playthroughGameName
     * @return
     */
    private String parseInferredType(
        String playthroughGameName
    ) {
        Map<String, String> uidToName = referenceDataService.loadPlaythroughTypeNameMap();
        // UID -> name from reference data; invert to name -> UID for parseInferredType
        //Map<String, String> playthroughTypeNameToUid = new HashMap<>();
        //String casualFallbackUid = null;
        String casualFallbackUid = uidToName.entrySet().stream().filter(entry -> entry.getValue().equals("Casual")).map(Map.Entry::getKey).findFirst().orElse(null);

        if (playthroughGameName == null || playthroughGameName.isBlank()) {
            return casualFallbackUid;
        }
        String s = playthroughGameName.trim();
        for (String suffix : PLAYTHROUGH_TYPE_SUFFIXES) {
            if (s.endsWith(suffix) || s.endsWith(" - " + suffix) || s.endsWith(" " + suffix)) {
                String filteredOutcome = getPlaythroughNameBySuffix(suffix);
                return uidToName.entrySet().stream().filter(entry -> entry.getValue().equals(filteredOutcome)).map(Map.Entry::getKey).findFirst().orElse(null);   
            }
        }
        return uidToName.entrySet().stream().filter(entry -> entry.getValue().equals("Solo")).map(Map.Entry::getKey).findFirst().orElse(null);
    }

    private String getPlaythroughNameBySuffix(String suffix) {
        switch (suffix) {
            case "etc. July Patricia":
                return "Casual";
            case "July Patricia":
                return "Casual";
            case "July Yve":
                return "Coop";
            case "July Areen":
                return "Casual";
            case "Areen Cheesé":
                return "Coop";
            case "Areen Cheese":
                return "Coop";
            case "Solo":
                return "Solo";
            case "July":
                return "Coop";
            case "Coop":
                return "Coop";
            case "Jungs":
                return "Coop";
            case "Areen":
                return "Coop";
            case "Cheese":
                return "Coop";
            case "Cheesé":
                return "Coop";
            case "s2G":
                return "Multiplayer";
            case "Friends":
                return "Casual";
            case "Patricia":
                return "Casual";
            default:
                return null;
        }
    }
}