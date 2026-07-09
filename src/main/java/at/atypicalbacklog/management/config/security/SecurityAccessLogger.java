package at.atypicalbacklog.management.config.security;

import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class SecurityAccessLogger {

    static final Logger LOG = LoggerFactory.getLogger("at.atypicalbacklog.security.access");

    private SecurityAccessLogger() {}

    static void logForbidden(HttpServletRequest request, String reason) {
        LOG.warn(
            "access_denied status=403 reason={} method={} path={} client={}",
            reason,
            request.getMethod(),
            requestPath(request),
            clientIp(request)
        );
    }

    static void logRateLimitHit(HttpServletRequest request) {
        LOG.warn(
            "rate_limit_hit status=429 method={} path={} client={}",
            request.getMethod(),
            requestPath(request),
            clientIp(request)
        );
    }

    private static String clientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
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
