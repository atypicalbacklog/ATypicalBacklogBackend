package at.atypicalbacklog.management.moderation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Dictionary-based profanity filter (EN + DE). Wordlists loaded from a configurable directory.
 * Default: classpath:profanity (src/main/resources/profanity). Deploy can override to a filesystem folder.
 */
@Service
public class ProfanityService {

    private static final Logger log = LoggerFactory.getLogger(ProfanityService.class);

    private final boolean enabled;
    private final String wordlistDir;
    private final Set<String> dictionary = new HashSet<>();

    public ProfanityService(
        @Value("${app.moderation.profanity.enabled:true}") boolean enabled,
        @Value("${app.moderation.profanity.wordlist-dir:classpath:profanity}") String wordlistDir
    ) {
        this.enabled = enabled;
        this.wordlistDir = wordlistDir == null ? "classpath:profanity" : wordlistDir.trim();
    }

    @PostConstruct
    void loadDictionaries() {
        if (!enabled) {
            log.info("Profanity filter disabled");
            return;
        }
        try {
            if (wordlistDir.startsWith("classpath:")) {
                loadFromClasspath(wordlistDir.substring("classpath:".length()).replaceAll("^/+", ""));
            } else {
                loadFromFilesystem(Path.of(wordlistDir));
            }
            log.info("Profanity dictionary loaded: {} terms from {}", dictionary.size(), wordlistDir);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load profanity wordlists from " + wordlistDir, e);
        }
    }

    public ProfanityResult check(String text) {
        if (!enabled || text == null || text.isBlank()) {
            return ProfanityResult.clean();
        }
        String normalized = TextNormalizer.normalize(text);
        List<String> tokens = TextNormalizer.tokens(normalized);
        List<String> matched = new ArrayList<>();
        for (String token : tokens) {
            if (dictionary.contains(token)) {
                matched.add(token);
            }
        }
        // Also check contiguous join without spaces (e.g. spaced letters already collapsed by normalizer)
        if (matched.isEmpty() && !normalized.isBlank()) {
            String joined = normalized.replace(" ", "");
            if (dictionary.contains(joined)) {
                matched.add(joined);
            }
        }
        return matched.isEmpty()
            ? ProfanityResult.clean()
            : new ProfanityResult(true, List.copyOf(matched));
    }

    private void loadFromClasspath(String location) throws IOException {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources("classpath*:" + location + "/*.txt");
        if (resources.length == 0) {
            throw new IOException("No *.txt wordlists under classpath:" + location);
        }
        for (Resource resource : resources) {
            try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)
            )) {
                addLines(reader, resource.getFilename());
            }
        }
    }

    private void loadFromFilesystem(Path dir) throws IOException {
        if (!Files.isDirectory(dir)) {
            throw new IOException("Wordlist directory does not exist: " + dir.toAbsolutePath());
        }
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*.txt")) {
            boolean any = false;
            for (Path file : stream) {
                any = true;
                try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
                    addLines(reader, file.getFileName().toString());
                }
            }
            if (!any) {
                throw new IOException("No *.txt wordlists in " + dir.toAbsolutePath());
            }
        }
    }

    private void addLines(BufferedReader reader, String sourceName) throws IOException {
        String line;
        int count = 0;
        while ((line = reader.readLine()) != null) {
            String trimmed = line.trim();
            if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                continue;
            }
            String normalized = TextNormalizer.normalize(trimmed);
            if (!normalized.isBlank()) {
                dictionary.add(normalized.replace(" ", ""));
                dictionary.add(normalized);
                count++;
            }
        }
        log.debug("Loaded {} entries from {}", count, sourceName);
    }

    /** Visible for tests / diagnostics. */
    int dictionarySize() {
        return dictionary.size();
    }
}
