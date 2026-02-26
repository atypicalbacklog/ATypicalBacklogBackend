package at.atypicalbacklog.management.image.storage;

import at.atypicalbacklog.management.image.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

@Component
public class FileSystemImageStorage implements ImageStorage {

    private final Path root;

    public FileSystemImageStorage(@Value("${app.image.storage.root-path}") String rootPath) {
        this.root = Path.of(rootPath);
    }

    @Override
    public void storeOriginal(String imageUid, InputStream data, String mimetype, String sanitizedGameName) {
        String ext = FilenameUtils.extensionFromMimetype(mimetype);
        String filename = "original." + sanitizedGameName + "." + ext;
        write(root.resolve(imageUid).resolve(filename), data);
    }

    @Override
    public void storeThumbnail(String imageUid, InputStream data, String sanitizedGameName, String mimetype) {
        String ext = FilenameUtils.extensionFromMimetype(mimetype);
        String filename = "thumbnail." + sanitizedGameName + "." + ext;
        write(root.resolve(imageUid).resolve(filename), data);
    }

    @Override
    public String resolveOriginalPath(String imageUid) {
        return findFileByPrefix(imageUid, "original")
            .map(Path::toString)
            .orElseGet(() -> root.resolve(imageUid).resolve("original").toString());
    }

    @Override
    public String resolveThumbnailPath(String imageUid) {
        return findFileByPrefix(imageUid, "thumbnail")
            .map(Path::toString)
            .orElseGet(() -> root.resolve(imageUid).resolve("thumbnail").toString());
    }

    private Optional<Path> findFileByPrefix(String imageUid, String prefix) {
        try {
            Path dir = root.resolve(imageUid);
            if (!Files.isDirectory(dir)) {
                return Optional.empty();
            }
            return Files.list(dir)
                .filter(Files::isRegularFile)
                .filter(p -> p.getFileName().toString().startsWith(prefix))
                .findFirst();
        } catch (Exception e) {
            return Optional.empty();
        }
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
