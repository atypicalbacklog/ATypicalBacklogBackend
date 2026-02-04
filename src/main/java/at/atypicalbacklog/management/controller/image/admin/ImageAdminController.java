package at.atypicalbacklog.management.controller.image.admin;

import at.atypicalbacklog.management.dto.image.admin.*;
import at.atypicalbacklog.management.service.image.admin.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/images")
public class ImageAdminController {

    private final ImageOrphanDetectionService detectionService;
    private final ImageCleanupCommandService cleanupService;

    public ImageAdminController(
        ImageOrphanDetectionService detectionService,
        ImageCleanupCommandService cleanupService
    ) {
        this.detectionService = detectionService;
        this.cleanupService = cleanupService;
    }

    /** Preview orphan images (read-only) */
    @GetMapping("/orphans")
    public List<OrphanImageReportDto> scanOrphans() {
        return detectionService.scan();
    }

    /** Explicit cleanup */
    @PostMapping("/cleanup")
    public void cleanup(@RequestBody DeleteImagesRequest req) {
        cleanupService.deleteImages(req.imageUids());
    }
}
