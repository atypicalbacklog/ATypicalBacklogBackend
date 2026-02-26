package at.atypicalbacklog.management.image.service;

import at.atypicalbacklog.management.image.processing.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ImageProcessingConfig {

    @Bean
    public ThumbnailGenerator thumbnailGenerator() {
        return new DefaultThumbnailGenerator();
    }
}
