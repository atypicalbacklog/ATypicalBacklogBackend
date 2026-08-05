package at.atypicalbacklog.management.config.controller;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Forwards /public SPA routes to index.html in MANAGEMENT mode so admins can preview
 * the public UI at /public. Not registered in PUBLIC mode (the site root is the public UI).
 */
@Controller
@ConditionalOnProperty(name = "app.mode", havingValue = "MANAGEMENT")
public class ManagementPublicSpaForwardController {

    @GetMapping({
        "/public",
        "/public/**"
    })
    public String forwardPublicSpaRoutes() {
        return "forward:/";
    }
}
