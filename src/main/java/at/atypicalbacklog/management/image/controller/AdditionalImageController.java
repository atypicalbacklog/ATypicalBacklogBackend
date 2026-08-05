package at.atypicalbacklog.management.image.controller;

import at.atypicalbacklog.management.image.domain.ImageAspectHint;
import at.atypicalbacklog.management.image.domain.ImageSource;
import at.atypicalbacklog.management.image.domain.ImageType;
import at.atypicalbacklog.management.image.dto.AdditionalImageMetadataDto;
import at.atypicalbacklog.management.image.dto.PageResponseDto;
import at.atypicalbacklog.management.image.dto.UpdateAdditionalImageMetadataRequest;
import at.atypicalbacklog.management.image.service.AdditionalImageCommandService;
import at.atypicalbacklog.management.image.service.AdditionalImageDeliveryService;
import at.atypicalbacklog.management.image.service.AdditionalImageQueryService;

import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/additional-images")
public class AdditionalImageController {

    private final AdditionalImageQueryService queryService;
    private final AdditionalImageCommandService commandService;
    private final AdditionalImageDeliveryService deliveryService;

    public AdditionalImageController(
        AdditionalImageQueryService queryService,
        AdditionalImageCommandService commandService,
        AdditionalImageDeliveryService deliveryService
    ) {
        this.queryService = queryService;
        this.commandService = commandService;
        this.deliveryService = deliveryService;
    }

    /** Get image bytes by UID (same rules as 17.1). */
    @GetMapping("/{imageUid}")
    public ResponseEntity<Resource> image(
        @PathVariable String imageUid,
        @RequestParam(required = false) Boolean thumbnail,
        @RequestParam(required = false) ImageAspectHint aspect
    ) {
        Resource resource = deliveryService.load(imageUid, thumbnail, aspect);
        return ResponseEntity.ok()
            .contentType(MediaType.IMAGE_JPEG)
            .cacheControl(CacheControl.maxAge(
                thumbnail != null && Boolean.TRUE.equals(thumbnail) ? 1 : 30,
                TimeUnit.DAYS
            ).cachePublic())
            .body(resource);
    }

    /** Paginated list of additional images (newest first). */
    @GetMapping
    public PageResponseDto<AdditionalImageMetadataDto> list(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "50") int size
    ) {
        return queryService.list(page, size);
    }

    /** Update metadata (type, aspectHint, source). Filename/mimetype not updatable. */
    @PutMapping("/{uid}")
    public void updateMetadata(
        @PathVariable String uid,
        @RequestBody UpdateAdditionalImageMetadataRequest req
    ) {
        commandService.updateMetadata(uid, req);
    }

    @PostMapping(consumes = "multipart/form-data")
    public Map<String, String> upload(
        @RequestParam("file") MultipartFile file,
        @RequestParam ImageType type,
        @RequestParam ImageAspectHint aspectHint,
        @RequestParam ImageSource source
    ) {
        String uid = commandService.upload(file, type, aspectHint, source);
        return Map.of("uid", uid);
    }

    @DeleteMapping("/{uid}")
    public void delete(@PathVariable String uid) {
        commandService.deleteByUid(uid);
    }
}
