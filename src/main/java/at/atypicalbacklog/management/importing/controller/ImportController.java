package at.atypicalbacklog.management.importing.controller;

import at.atypicalbacklog.management.importing.ImportType;
import at.atypicalbacklog.management.importing.service.ImportBatchService;
import at.atypicalbacklog.management.importing.service.createStagingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/admin/import")
public class ImportController {

    private final ImportBatchService service;
    private final createStagingService stagingService;

    public ImportController(ImportBatchService service, createStagingService stagingService) {
        this.service = service;
        this.stagingService = stagingService;
    }

    @Operation(summary = "Upload CSV for import", description = "Upload a CSV file for TIME_BOOKING_MIGRATION or GAME_INSTANCE_MIGRATION.")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String upload(
        @RequestParam("type") @Parameter(description = "Import type") ImportType type,
        //@RequestParam("file") @Parameter(description = "CSV file to upload", schema = @Schema(type = "string", format = "")) MultipartFile file
        @RequestParam("file") @Parameter(description = "CSV file to upload") MultipartFile file
    ) {
        return service.createBatch(type, file);
    }

    @PostMapping("/createStaging")
    public String createStaging(@RequestParam String batchUid) {
        return stagingService.createStaging(batchUid);
    }
}