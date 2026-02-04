package at.atypicalbacklog.management.image.processing;

import java.io.InputStream;

public interface ThumbnailGenerator {

    InputStream generateThumbnail(InputStream original);
}
