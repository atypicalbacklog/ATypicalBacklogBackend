package at.atypicalbacklog.management.image.storage;

import java.io.InputStream;

public interface ImageStorage {

    /**
     * Store original image. Physical filename: original.{sanitizedGameName}.{ext}
     */
    void storeOriginal(String imageUid, InputStream data, String mimetype, String sanitizedGameName);

    /**
     * Store thumbnail. Physical filename: thumbnail.{sanitizedGameName}.{ext}
     */
    void storeThumbnail(String imageUid, InputStream data, String sanitizedGameName, String mimetype);

    /**
     * Resolve path to original file by finding file whose name starts with "original".
     */
    String resolveOriginalPath(String imageUid);

    /**
     * Resolve path to thumbnail file by finding file whose name starts with "thumbnail".
     */
    String resolveThumbnailPath(String imageUid);
}
