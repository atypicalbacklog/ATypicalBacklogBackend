package at.atypicalbacklog.management.service.image;

import at.atypicalbacklog.management.domain.image.ImageAspectHint;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
public class PlaceholderImageService {

    public Resource loadPlaceholder(ImageAspectHint aspect) {

        String filename = switch (aspect) {
            case VERTICAL -> "placeholders/vertical.jpg";
            case HORIZONTAL -> "placeholders/horizontal.jpg";
            case SQUARE -> "placeholders/square.jpg";
            case FREE -> "placeholders/free.jpg";
        };

        return new ClassPathResource(filename);
    }
}