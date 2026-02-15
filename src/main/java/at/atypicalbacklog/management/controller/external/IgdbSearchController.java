package at.atypicalbacklog.management.controller.external;

import at.atypicalbacklog.management.dto.external.igdb.IgdbGameSearchResultDto;
import at.atypicalbacklog.management.service.external.igdb.IgdbSearchService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/external/igdb")
public class IgdbSearchController {

    private final IgdbSearchService service;

    public IgdbSearchController(IgdbSearchService service) {
        this.service = service;
    }

    @GetMapping("/search")
    public List<IgdbGameSearchResultDto> search(@RequestParam String name) {
        return service.searchByName(name);
    }

    @GetMapping("/games/{externalId}")
    public IgdbGameSearchResultDto getByExternalId(@PathVariable long externalId) {
        return service.getByExternalId(externalId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "IGDB game not found: " + externalId));
    }

    /**
     * Search and show related content for an IGDB game id (DLCs, expansions, similar games).
     * Optional name filter: only return related items whose name contains the given string (case-insensitive).
     */
    @GetMapping("/games/{externalId}/related")
    public List<IgdbGameSearchResultDto> getRelatedContent(
            @PathVariable long externalId,
            @RequestParam(required = false) String name) {
        return service.getRelatedContentByExternalId(externalId, name);
    }
}