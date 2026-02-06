package at.atypicalbacklog.management.image;

import java.util.Map;

public final class FilenameUtils {

    private static final Map<String, String> MIMETYPE_TO_EXT = Map.of(
        "image/jpeg", "jpg",
        "image/jpg", "jpg",
        "image/png", "png",
        "image/gif", "gif",
        "image/webp", "webp"
    );

    private FilenameUtils() {}

    /**
     * Sanitizes game name for use as filename (removes special chars and whitespace).
     * Falls back to "image" if result would be empty.
     */
    public static String sanitizeGameName(String gameName) {
        if (gameName == null || gameName.isBlank()) {
            return "image";
        }
        String sanitized = gameName
            .replaceAll("[^a-zA-Z0-9_-]", "")
            .replaceAll("_+", "_")
            .replaceAll("^-+|-+$", "");
        return sanitized.isBlank() ? "image" : sanitized;
    }

    /**
     * Builds DB filename: sanitized game name + extension from mimetype.
     */
    public static String buildImageFilename(String gameName, String mimetype) {
        String base = sanitizeGameName(gameName);
        String ext = extensionFromMimetype(mimetype);
        return base + "." + ext;
    }

    public static String extensionFromMimetype(String mimetype) {
        if (mimetype != null) {
            String ext = MIMETYPE_TO_EXT.get(mimetype.toLowerCase());
            if (ext != null) {
                return ext;
            }
        }
        return "jpg";
    }
}
