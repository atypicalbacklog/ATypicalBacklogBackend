package at.atypicalbacklog.management.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.concurrent.TimeUnit;

/**
 * SPA static caching for the Vite build embedded in {@code classpath:/static/}.
 * <p>
 * Default static resources (including {@code index.html} and SPA forwards) use
 * {@code Cache-Control: no-cache} via {@code spring.web.resources.cache.cachecontrol}.
 * Hashed Vite assets under {@code /assets/**} may be cached up to 7 days.
 * API responses (e.g. {@code /api/images/**}) set their own headers and are unaffected.
 */
@Configuration
public class SpaStaticResourceConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/assets/**")
            .addResourceLocations("classpath:/static/assets/")
            .setCacheControl(
                CacheControl.maxAge(7, TimeUnit.DAYS).cachePublic()
            );
    }
}
