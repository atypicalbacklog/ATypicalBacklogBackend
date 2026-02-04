package at.atypicalbacklog.management.image.storage;

import java.io.InputStream;

public interface ImageStorage {

    void storeOriginal(String imageUid, InputStream data);

    void storeThumbnail(String imageUid, InputStream data);

    String resolveOriginalPath(String imageUid);

    String resolveThumbnailPath(String imageUid);
}
