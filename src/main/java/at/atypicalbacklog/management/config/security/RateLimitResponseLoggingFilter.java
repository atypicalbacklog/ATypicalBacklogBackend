package at.atypicalbacklog.management.config.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Logs rate-limit responses (HTTP 429). Proxy-level limits normally return 429 before
 * the request reaches the app; configure access logging on the reverse proxy as well.
 */
@Component
@Order(Ordered.LOWEST_PRECEDENCE)
public class RateLimitResponseLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        filterChain.doFilter(request, response);

        if (response.getStatus() == HttpStatus.TOO_MANY_REQUESTS.value()) {
            SecurityAccessLogger.logRateLimitHit(request);
        }
    }
}
