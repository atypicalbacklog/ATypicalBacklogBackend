package at.atypicalbacklog.management.controller.external;

import at.atypicalbacklog.management.dto.external.igdb.IgdbGameSearchResultDto;
import at.atypicalbacklog.management.service.external.igdb.IgdbSearchService;
import org.springframework.web.bind.annotation.*;

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
}