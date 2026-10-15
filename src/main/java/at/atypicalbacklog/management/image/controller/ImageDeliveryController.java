package at.atypicalbacklog.management.image.controller;

import at.atypicalbacklog.management.image.domain.ImageAspectHint;
import at.atypicalbacklog.management.image.dto.ImageDeliveryLoadResult;
import at.atypicalbacklog.management.image.service.ImageDeliveryService;

import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/images")
public class ImageDeliveryController {

    private final ImageDeliveryService service;

    public ImageDeliveryController(ImageDeliveryService service) {
        this.service = service;
    }

    /**
     * Single endpoint for image delivery by UID.
     * - thumbnail=false and image exists -> original image
     * - thumbnail=true and thumbnail exists -> thumbnail
     * - thumbnail=true and thumbnail absent but image exists -> original (fallback)
     * - thumbnail param absent -> placeholder
     */
    @GetMapping("/{imageUid}")
    public ResponseEntity<Resource> image(
        @PathVariable String imageUid,
        @RequestParam(required = false) Boolean thumbnail,
        @RequestParam(required = false) ImageAspectHint aspect
    ) {
        ImageDeliveryLoadResult result = service.load(imageUid, thumbnail, aspect);
        return ResponseEntity.ok()
            .contentType(result.contentType())
            .cacheControl(CacheControl.maxAge(thumbnail != null && Boolean.TRUE.equals(thumbnail) ? 1 : 30, TimeUnit.DAYS).cachePublic())
            .body(result.resource());
    }
}