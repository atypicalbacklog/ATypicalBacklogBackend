package at.atypicalbacklog.management.audit;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
public class RequestIdFilter implements Filter {

    public static final String REQUEST_ID_ATTR = "REQUEST_ID";

    @Override
    public void doFilter(
        ServletRequest request,
        ServletResponse response,
        FilterChain chain
    ) throws IOException, ServletException {

        HttpServletRequest http = (HttpServletRequest) request;
        String requestId = http.getHeader("X-Request-Id");

        if (requestId == null || requestId.isBlank()) {
            requestId = UUID.randomUUID().toString();
        }

        request.setAttribute(REQUEST_ID_ATTR, requestId);
        chain.doFilter(request, response);
    }
}
