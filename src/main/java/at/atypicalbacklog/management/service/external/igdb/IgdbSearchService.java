package at.atypicalbacklog.management.service.external.igdb;

import at.atypicalbacklog.management.dto.external.igdb.IgdbGameSearchResultDto;
import at.atypicalbacklog.management.dto.external.igdb.IgdbReleaseDateDto;
import at.atypicalbacklog.management.external.igdb.IgdbClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class IgdbSearchService {

    private final IgdbClient client;
    private final ObjectMapper mapper = new ObjectMapper();

    public IgdbSearchService(IgdbClient client) {
        this.client = client;
    }

    public List<IgdbGameSearchResultDto> searchByName(String name) {

        String body = """
            fields id,name,first_release_date,cover.url,game_type,platforms;
            search "%s";
            limit 20;
            """.formatted(name.replace("\"", "\\\""));

        String response = client.post("/games", body);

        try {
            JsonNode root = mapper.readTree(response);
            List<IgdbGameSearchResultDto> results = new ArrayList<>();

            Set<Long> gameIds = new HashSet<>();
            Set<Long> gameTypeIds = new HashSet<>();
            Set<Long> platformIds = new HashSet<>();
            for (JsonNode n : root) {
                if (n.has("id")) {
                    gameIds.add(n.get("id").asLong());
                }
                if (n.has("game_type") && !n.get("game_type").isNull()) {
                    gameTypeIds.add(n.get("game_type").asLong());
                }
                if (n.has("platforms") && n.get("platforms").isArray()) {
                    for (JsonNode p : n.get("platforms")) {
                        if (p != null && !p.isNull()) {
                            platformIds.add(p.asLong());
                        }
                    }
                }
            }

            Map<Long, String> gameTypeMap = fetchGameTypeMap(gameTypeIds);
            
            // Fetch release dates first to collect platform IDs from release dates
            Map<Long, List<IgdbReleaseDateDto>> releaseDatesByGame = fetchReleaseDates(gameIds);
            
            // Collect platform IDs from release dates
            Set<Long> releaseDatePlatformIds = new HashSet<>();
            for (List<IgdbReleaseDateDto> dates : releaseDatesByGame.values()) {
                for (IgdbReleaseDateDto date : dates) {
                    if (date.platformId() != null) {
                        releaseDatePlatformIds.add(date.platformId());
                    }
                }
            }
            
            // Merge platform IDs from games and release dates, then fetch abbreviations
            Set<Long> allPlatformIds = new HashSet<>(platformIds);
            allPlatformIds.addAll(releaseDatePlatformIds);
            Map<Long, String> platformAbbrevMap = fetchPlatformAbbreviations(allPlatformIds);
            
            // Re-map release dates with platform abbreviations
            releaseDatesByGame = enrichReleaseDatesWithPlatformAbbreviations(releaseDatesByGame, platformAbbrevMap);

            List<JsonNode> sortedNodes = new ArrayList<>();
            root.forEach(sortedNodes::add);
            sortedNodes.sort(Comparator
                .comparingLong((JsonNode n) -> n.has("game_type") && !n.get("game_type").isNull()
                    ? n.get("game_type").asLong()
                    : Long.MAX_VALUE)
                .thenComparing(n -> n.get("name").asText()));

            for (JsonNode n : sortedNodes) {
                String category = n.has("game_type") && !n.get("game_type").isNull()
                    ? gameTypeMap.get(n.get("game_type").asLong())
                    : null;
                List<String> releasedPlatforms = new ArrayList<>();
                if (n.has("platforms") && n.get("platforms").isArray()) {
                    for (JsonNode p : n.get("platforms")) {
                        if (p != null && !p.isNull()) {
                            String abbrev = platformAbbrevMap.get(p.asLong());
                            if (abbrev != null) {
                                releasedPlatforms.add(abbrev);
                            }
                        }
                    }
                }

                long gameId = n.get("id").asLong();
                results.add(new IgdbGameSearchResultDto(
                    gameId,
                    n.get("name").asText(),
                    n.has("first_release_date")
                        ? Instant.ofEpochSecond(
                            n.get("first_release_date").asLong()
                          ).atZone(ZoneOffset.UTC).toLocalDate()
                        : null,
                    n.has("cover") && n.get("cover").has("url")
                        ? n.get("cover").get("url").asText().replace("t_thumb", "t_cover_big")
                        : null,
                    category,
                    List.copyOf(releasedPlatforms),
                    releaseDatesByGame.getOrDefault(gameId, List.of())
                ));
            }
            return results;
        } catch (Exception e) {
            throw new IllegalStateException("IGDB parsing failed", e);
        }
    }

    /**
     * Fetches a single IGDB game by external ID. Returns empty if not found or API error.
     */
    public Optional<IgdbGameSearchResultDto> getByExternalId(long externalId) {
        String body = """
            fields id,name,first_release_date,cover.url,game_type,platforms;
            where id = %d;
            limit 1;
            """.formatted(externalId);
        try {
            String response = client.post("/games", body);
            JsonNode root = mapper.readTree(response);
            if (root == null || !root.isArray() || root.isEmpty()) {
                return Optional.empty();
            }
            JsonNode n = root.get(0);
            Set<Long> gameIds = Set.of(externalId);
            Set<Long> gameTypeIds = new HashSet<>();
            Set<Long> platformIds = new HashSet<>();
            if (n.has("game_type") && !n.get("game_type").isNull()) {
                gameTypeIds.add(n.get("game_type").asLong());
            }
            if (n.has("platforms") && n.get("platforms").isArray()) {
                for (JsonNode p : n.get("platforms")) {
                    if (p != null && !p.isNull()) {
                        platformIds.add(p.asLong());
                    }
                }
            }
            Map<Long, String> gameTypeMap = fetchGameTypeMap(gameTypeIds);
            Map<Long, List<IgdbReleaseDateDto>> releaseDatesByGame = fetchReleaseDates(gameIds);
            Set<Long> releaseDatePlatformIds = new HashSet<>();
            for (List<IgdbReleaseDateDto> dates : releaseDatesByGame.values()) {
                for (IgdbReleaseDateDto date : dates) {
                    if (date.platformId() != null) {
                        releaseDatePlatformIds.add(date.platformId());
                    }
                }
            }
            Set<Long> allPlatformIds = new HashSet<>(platformIds);
            allPlatformIds.addAll(releaseDatePlatformIds);
            Map<Long, String> platformAbbrevMap = fetchPlatformAbbreviations(allPlatformIds);
            releaseDatesByGame = enrichReleaseDatesWithPlatformAbbreviations(releaseDatesByGame, platformAbbrevMap);
            String category = n.has("game_type") && !n.get("game_type").isNull()
                ? gameTypeMap.get(n.get("game_type").asLong())
                : null;
            List<String> releasedPlatforms = new ArrayList<>();
            if (n.has("platforms") && n.get("platforms").isArray()) {
                for (JsonNode p : n.get("platforms")) {
                    if (p != null && !p.isNull()) {
                        String abbrev = platformAbbrevMap.get(p.asLong());
                        if (abbrev != null) {
                            releasedPlatforms.add(abbrev);
                        }
                    }
                }
            }
            long gameId = n.get("id").asLong();
            IgdbGameSearchResultDto dto = new IgdbGameSearchResultDto(
                gameId,
                n.get("name").asText(),
                n.has("first_release_date") && !n.get("first_release_date").isNull()
                    ? Instant.ofEpochSecond(n.get("first_release_date").asLong()).atZone(ZoneOffset.UTC).toLocalDate()
                    : null,
                n.has("cover") && n.get("cover").has("url")
                    ? n.get("cover").get("url").asText().replace("t_thumb", "t_cover_big")
                    : null,
                category,
                List.copyOf(releasedPlatforms),
                releaseDatesByGame.getOrDefault(gameId, List.of())
            );
            return Optional.of(dto);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * Fetches related content for an IGDB game by id (DLCs, expansions, standalone expansions, similar games).
     * Optionally filters results by name (case-insensitive contains). Uses IGDB fields per OpenAPI: dlcs, expansions, standalone_expansions, similar_games.
     */
    public List<IgdbGameSearchResultDto> getRelatedContentByExternalId(long externalId, String nameFilter) {
        String body = """
            fields dlcs,expansions,standalone_expansions,bundles,expanded_games;
            where id = %d;
            limit 1;
            """.formatted(externalId);
        try {
            String response = client.post("/games", body);
            JsonNode root = mapper.readTree(response);
            if (root == null || !root.isArray() || root.isEmpty()) {
                return List.of();
            }
            JsonNode game = root.get(0);
            Set<Long> relatedIds = new HashSet<>();
            collectGameIds(game, "dlcs", relatedIds);
            collectGameIds(game, "expansions", relatedIds);
            collectGameIds(game, "standalone_expansions", relatedIds);
            collectGameIds(game, "bundles", relatedIds);
            collectGameIds(game, "expanded_games", relatedIds);
            if (relatedIds.isEmpty()) {
                return List.of();
            }
            String idList = relatedIds.stream().map(Object::toString).collect(Collectors.joining(","));
            String body2 = """
                fields id,name,first_release_date,cover.url,game_type,platforms;
                where id = (%s);
                limit 100;
                """.formatted(idList);
            String response2 = client.post("/games", body2);
            JsonNode gamesRoot = mapper.readTree(response2);
            if (gamesRoot == null || !gamesRoot.isArray()) {
                return List.of();
            }
            Set<Long> gameIds = new HashSet<>();
            Set<Long> gameTypeIds = new HashSet<>();
            Set<Long> platformIds = new HashSet<>();
            for (JsonNode n : gamesRoot) {
                if (n.has("id")) gameIds.add(n.get("id").asLong());
                if (n.has("game_type") && !n.get("game_type").isNull()) gameTypeIds.add(n.get("game_type").asLong());
                if (n.has("platforms") && n.get("platforms").isArray()) {
                    for (JsonNode p : n.get("platforms")) {
                        if (p != null && !p.isNull()) platformIds.add(p.asLong());
                    }
                }
            }
            Map<Long, String> gameTypeMap = fetchGameTypeMap(gameTypeIds);
            Map<Long, List<IgdbReleaseDateDto>> releaseDatesByGame = fetchReleaseDates(gameIds);
            Set<Long> releaseDatePlatformIds = new HashSet<>();
            for (List<IgdbReleaseDateDto> dates : releaseDatesByGame.values()) {
                for (IgdbReleaseDateDto date : dates) {
                    if (date.platformId() != null) releaseDatePlatformIds.add(date.platformId());
                }
            }
            Set<Long> allPlatformIds = new HashSet<>(platformIds);
            allPlatformIds.addAll(releaseDatePlatformIds);
            Map<Long, String> platformAbbrevMap = fetchPlatformAbbreviations(allPlatformIds);
            releaseDatesByGame = enrichReleaseDatesWithPlatformAbbreviations(releaseDatesByGame, platformAbbrevMap);
            String nameFilterLower = (nameFilter != null && !nameFilter.isBlank()) ? nameFilter.trim().toLowerCase() : null;
            List<IgdbGameSearchResultDto> results = new ArrayList<>();
            for (JsonNode n : gamesRoot) {
                String name = n.has("name") ? n.get("name").asText() : "";
                if (nameFilterLower != null && !name.toLowerCase().contains(nameFilterLower)) {
                    continue;
                }
                long gameId = n.get("id").asLong();
                String category = n.has("game_type") && !n.get("game_type").isNull()
                    ? gameTypeMap.get(n.get("game_type").asLong())
                    : null;
                List<String> releasedPlatforms = new ArrayList<>();
                if (n.has("platforms") && n.get("platforms").isArray()) {
                    for (JsonNode p : n.get("platforms")) {
                        if (p != null && !p.isNull()) {
                            String abbrev = platformAbbrevMap.get(p.asLong());
                            if (abbrev != null) releasedPlatforms.add(abbrev);
                        }
                    }
                }
                results.add(new IgdbGameSearchResultDto(
                    gameId,
                    name,
                    n.has("first_release_date") && !n.get("first_release_date").isNull()
                        ? Instant.ofEpochSecond(n.get("first_release_date").asLong()).atZone(ZoneOffset.UTC).toLocalDate()
                        : null,
                    n.has("cover") && n.get("cover").has("url")
                        ? n.get("cover").get("url").asText().replace("t_thumb", "t_cover_big")
                        : null,
                    category,
                    List.copyOf(releasedPlatforms),
                    releaseDatesByGame.getOrDefault(gameId, List.of())
                ));
            }
            return results;
        } catch (Exception e) {
            throw new IllegalStateException("IGDB related content fetch failed for id=" + externalId, e);
        }
    }

    private void collectGameIds(JsonNode parent, String field, Set<Long> out) {
        if (!parent.has(field) || !parent.get(field).isArray()) return;
        for (JsonNode item : parent.get(field)) {
            if (item != null && !item.isNull()) {
                try {
                    out.add(item.asLong());
                } catch (Exception ignored) {}
            }
        }
    }

    /**
     * Fetches game_type id -> type string from /game_types.
     */
    private Map<Long, String> fetchGameTypeMap(Set<Long> ids) {
        if (ids.isEmpty()) {
            return Map.of();
        }
        String idList = ids.stream().map(Object::toString).collect(Collectors.joining(","));
        String body = "fields id,type; where id = (" + idList + ");";
        try {
            String response = client.post("/game_types", body);
            JsonNode root = mapper.readTree(response);
            Map<Long, String> map = new HashMap<>();
            for (JsonNode n : root) {
                if (n.has("id") && n.has("type")) {
                    map.put(n.get("id").asLong(), n.get("type").asText());
                }
            }
            return map;
        } catch (Exception e) {
            return Map.of();
        }
    }

    /**
     * Fetches platform id -> abbreviation from /platforms (IGDB uses "abbreviation" for short form).
     */
    private Map<Long, String> fetchPlatformAbbreviations(Set<Long> ids) {
        if (ids.isEmpty()) {
            return Map.of();
        }
        String idList = ids.stream().map(Object::toString).collect(Collectors.joining(","));
        String body = "fields id,abbreviation; where id = (" + idList + ");";
        try {
            String response = client.post("/platforms", body);
            JsonNode root = mapper.readTree(response);
            Map<Long, String> map = new HashMap<>();
            for (JsonNode n : root) {
                if (n.has("id") && n.has("abbreviation")) {
                    map.put(n.get("id").asLong(), n.get("abbreviation").asText());
                }
            }
            return map;
        } catch (Exception e) {
            return Map.of();
        }
    }

    /**
     * Fetches release dates for games from /release_dates endpoint.
     * Returns a map of game ID -> list of release dates (without platform abbreviations initially).
     */
    private Map<Long, List<IgdbReleaseDateDto>> fetchReleaseDates(Set<Long> gameIds) {
        if (gameIds.isEmpty()) {
            return Map.of();
        }
        String idList = gameIds.stream().map(Object::toString).collect(Collectors.joining(","));
        String body = "fields game,date,platform,region; where game = (" + idList + ");";
        try {
            String response = client.post("/release_dates", body);
            JsonNode root = mapper.readTree(response);
            Map<Long, List<IgdbReleaseDateDto>> releaseDatesByGame = new HashMap<>();
            
            for (JsonNode n : root) {
                if (!n.has("game") || n.get("game").isNull()) {
                    continue;
                }
                long gameId = n.get("game").asLong();
                
                LocalDate date = null;
                if (n.has("date") && !n.get("date").isNull()) {
                    date = Instant.ofEpochSecond(n.get("date").asLong())
                        .atZone(ZoneOffset.UTC).toLocalDate();
                }
                
                Long platformId = null;
                if (n.has("platform") && !n.get("platform").isNull()) {
                    platformId = n.get("platform").asLong();
                }
                
                Integer region = null;
                if (n.has("region") && !n.get("region").isNull()) {
                    region = n.get("region").asInt();
                }
                
                releaseDatesByGame.computeIfAbsent(gameId, k -> new ArrayList<>())
                    .add(new IgdbReleaseDateDto(date, platformId, null, region));
            }
            
            return releaseDatesByGame;
        } catch (Exception e) {
            return Map.of();
        }
    }

    /**
     * Enriches release dates with platform abbreviations.
     */
    private Map<Long, List<IgdbReleaseDateDto>> enrichReleaseDatesWithPlatformAbbreviations(
            Map<Long, List<IgdbReleaseDateDto>> releaseDatesByGame,
            Map<Long, String> platformAbbrevMap) {
        Map<Long, List<IgdbReleaseDateDto>> enriched = new HashMap<>();
        for (Map.Entry<Long, List<IgdbReleaseDateDto>> entry : releaseDatesByGame.entrySet()) {
            List<IgdbReleaseDateDto> enrichedDates = entry.getValue().stream()
                .map(date -> new IgdbReleaseDateDto(
                    date.date(),
                    date.platformId(),
                    date.platformId() != null ? platformAbbrevMap.get(date.platformId()) : null,
                    date.region()
                ))
                .collect(Collectors.toList());
            enriched.put(entry.getKey(), enrichedDates);
        }
        return enriched;
    }
}