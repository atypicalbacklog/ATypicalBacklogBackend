package at.atypicalbacklog.management.dto.image.admin;

import java.util.List;

public record DeleteImagesRequest(
    List<String> imageUids
) {}
