package at.atypicalbacklog.management.config.controller;

import at.atypicalbacklog.management.config.dto.AppConfigResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/app-config")
public class AppConfigController {

    private static final String DEFAULT_MODE = "PUBLIC";

    /** MANAGEMENT or PUBLIC; default PUBLIC if not set in application.yml. */
    @Value("${app.mode:PUBLIC}")
    private String mode;

    @GetMapping
    public AppConfigResponse getAppConfig() {
        String resolved = "MANAGEMENT".equalsIgnoreCase(mode) ? "MANAGEMENT" : DEFAULT_MODE;
        return new AppConfigResponse(resolved);
    }
}
