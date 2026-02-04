package at.atypicalbacklog.management.service.dlc;

import at.atypicalbacklog.management.dto.dlc.DlcResponse;
import at.atypicalbacklog.management.repository.DlcRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DlcQueryService {

    private final DlcRepository repository;

    public DlcQueryService(DlcRepository repository) {
        this.repository = repository;
    }

    public List<DlcResponse> getByGameInstanceUid(String gameInstanceUid) {
        return repository
            .findByGameInstanceUidOrderByCreationDateAsc(gameInstanceUid)
            .stream()
            .map(dlc -> new DlcResponse(
                dlc.getUid(),
                dlc.getGameInstanceUid(),
                dlc.getName(),
                dlc.getReceivedDate(),
                dlc.getPlaythroughUid()
            ))
            .toList();
    }
}
