package at.atypicalbacklog.management.image.dto;

import java.util.List;

public record DeleteImagesRequest(
    List<String> imageUids
) {}
