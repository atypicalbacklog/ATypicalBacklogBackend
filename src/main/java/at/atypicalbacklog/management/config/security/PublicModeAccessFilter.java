package at.atypicalbacklog.management.config.security;

import at.atypicalbacklog.management.config.AppModeService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

import java.io.IOException;
import java.util.List;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class PublicModeAccessFilter extends OncePerRequestFilter {

    private static final String REASON_BLOCKED_ENDPOINT = "blocked_endpoint";
    private static final String REASON_WRITE_NOT_ALLOWED = "write_not_allowed";

    private static final List<String> PUBLIC_BLOCKED_PATHS = List.of(
        "/swagger-ui/**",
        "/swagger-ui.html",
        "/v3/api-docs",
        "/v3/api-docs/**"
    );

    private static final List<String> PUBLIC_ALLOWED_NON_GET_PATHS = List.of(
        "/api/query/game-overview",
        "/api/backlog-recommendations",
        "/api/backlog-recommendations/*/upvote",
        "/api/backlog-recommendations/*/comments",
        "/api/query/backlog-overview",
        "/api/query/playthroughs/history/search",
        "/api/reviews/search-by-year",
        "/api/feed/recent-activity",
        "/api/feed/random-backlog",
        "/api/feed/random-s-tier-reviews",
        "/api/feed/random-backlog-recommendations"
    );

    private final AppModeService appModeService;
    private final List<PathPattern> blockedPatterns;
    private final List<PathPattern> allowedNonGetPatterns;

    public PublicModeAccessFilter(AppModeService appModeService) {
        this.appModeService = appModeService;
        PathPatternParser parser = new PathPatternParser();
        this.blockedPatterns = PUBLIC_BLOCKED_PATHS.stream()
            .map(parser::parse)
            .toList();
        this.allowedNonGetPatterns = PUBLIC_ALLOWED_NON_GET_PATHS.stream()
            .map(parser::parse)
            .toList();
    }

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        if (appModeService.isManagementMode()) {
            filterChain.doFilter(request, response);
            return;
        }

        String path = requestPath(request);
        if (isPublicBlockedPath(path)) {
            SecurityAccessLogger.logForbidden(request, REASON_BLOCKED_ENDPOINT);
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        HttpMethod method = HttpMethod.valueOf(request.getMethod());
        if (method == HttpMethod.GET || method == HttpMethod.HEAD || method == HttpMethod.OPTIONS) {
            filterChain.doFilter(request, response);
            return;
        }

        if (isAllowedNonGetPath(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        SecurityAccessLogger.logForbidden(request, REASON_WRITE_NOT_ALLOWED);
        response.sendError(HttpServletResponse.SC_FORBIDDEN);
    }

    private boolean isPublicBlockedPath(String path) {
        if (isBlockedActuatorPath(path)) {
            return true;
        }
        return blockedPatterns.stream().anyMatch(pattern -> pattern.matches(
            org.springframework.http.server.PathContainer.parsePath(path)
        ));
    }

    private static boolean isBlockedActuatorPath(String path) {
        if ("/actuator".equals(path) || "/actuator/".equals(path)) {
            return true;
        }
        if (!path.startsWith("/actuator/")) {
            return false;
        }
        return !"/actuator/health".equals(path);
    }

    private boolean isAllowedNonGetPath(String path) {
        return allowedNonGetPatterns.stream().anyMatch(pattern -> pattern.matches(
            org.springframework.http.server.PathContainer.parsePath(path)
        ));
    }

    private static String requestPath(HttpServletRequest request) {
        String path = request.getRequestURI();
        String contextPath = request.getContextPath();
        if (contextPath != null && !contextPath.isEmpty() && path.startsWith(contextPath)) {
            return path.substring(contextPath.length());
        }
        return path;
    }
}
