package at.atypicalbacklog.management.image.storage;

import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class FileSystemImageStorage implements ImageStorage {

    private final Path root = Path.of("image-storage");

    @Override
    public void storeOriginal(String imageUid, InputStream data) {
        write(root.resolve(imageUid + "/original"), data);
    }

    @Override
    public void storeThumbnail(String imageUid, InputStream data) {
        write(root.resolve(imageUid + "/thumbnail"), data);
    }

    @Override
    public String resolveOriginalPath(String imageUid) {
        return root.resolve(imageUid + "/original").toString();
    }

    @Override
    public String resolveThumbnailPath(String imageUid) {
        return root.resolve(imageUid + "/thumbnail").toString();
    }

    private void write(Path path, InputStream data) {
        try {
            Files.createDirectories(path.getParent());
            Files.copy(data, path);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to store image", e);
        }
    }
}
