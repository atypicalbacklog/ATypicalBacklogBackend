package at.atypicalbacklog.management.service.external.igdb;

import at.atypicalbacklog.management.dto.external.igdb.IgdbGameSearchResultDto;
import at.atypicalbacklog.management.external.igdb.IgdbClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.time.Instant;
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

            Set<Long> gameTypeIds = new HashSet<>();
            Set<Long> platformIds = new HashSet<>();
            for (JsonNode n : root) {
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
            Map<Long, String> platformAbbrevMap = fetchPlatformAbbreviations(platformIds);

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

                results.add(new IgdbGameSearchResultDto(
                    n.get("id").asLong(),
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
                    List.copyOf(releasedPlatforms)
                ));
            }
            return results;
        } catch (Exception e) {
            throw new IllegalStateException("IGDB parsing failed", e);
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
}