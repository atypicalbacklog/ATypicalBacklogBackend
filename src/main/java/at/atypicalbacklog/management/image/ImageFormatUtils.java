package at.atypicalbacklog.management.image;

import java.util.Set;

public final class ImageFormatUtils {

    private static final Set<String> KNOWN_IMAGE_MIMETYPES = Set.of(
        "image/jpeg",
        "image/jpg",
        "image/png",
        "image/gif",
        "image/webp"
    );

    private ImageFormatUtils() {}

    /**
     * Detects image mimetype from file magic bytes, with optional Content-Type hint as fallback.
     */
    public static String detectMimetype(byte[] data, String contentTypeHint) {
        String fromBytes = detectMimetypeFromBytes(data);
        if (fromBytes != null) {
            return fromBytes;
        }
        if (contentTypeHint != null && !contentTypeHint.isBlank()) {
            String normalized = contentTypeHint.toLowerCase().trim();
            if (KNOWN_IMAGE_MIMETYPES.contains(normalized)) {
                return "image/jpg".equals(normalized) ? "image/jpeg" : normalized;
            }
        }
        return "image/jpeg";
    }

    public static String detectMimetypeFromBytes(byte[] data) {
        if (data == null || data.length < 12) {
            return null;
        }
        if (isJpeg(data)) {
            return "image/jpeg";
        }
        if (isPng(data)) {
            return "image/png";
        }
        if (isGif(data)) {
            return "image/gif";
        }
        if (isWebp(data)) {
            return "image/webp";
        }
        return null;
    }

    private static boolean isJpeg(byte[] data) {
        return (data[0] & 0xFF) == 0xFF && (data[1] & 0xFF) == 0xD8;
    }

    private static boolean isPng(byte[] data) {
        return data[0] == (byte) 0x89
            && data[1] == 'P'
            && data[2] == 'N'
            && data[3] == 'G';
    }

    private static boolean isGif(byte[] data) {
        return data[0] == 'G' && data[1] == 'I' && data[2] == 'F';
    }

    private static boolean isWebp(byte[] data) {
        return data[0] == 'R'
            && data[1] == 'I'
            && data[2] == 'F'
            && data[3] == 'F'
            && data[8] == 'W'
            && data[9] == 'E'
            && data[10] == 'B'
            && data[11] == 'P';
    }
}
