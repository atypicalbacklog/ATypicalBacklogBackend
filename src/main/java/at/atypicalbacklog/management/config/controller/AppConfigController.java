package at.atypicalbacklog.management.config.controller;

import at.atypicalbacklog.management.config.AppModeService;
import at.atypicalbacklog.management.config.dto.AppConfigResponse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/app-config")
public class AppConfigController {

    private final AppModeService appModeService;

    public AppConfigController(AppModeService appModeService) {
        this.appModeService = appModeService;
    }

    @GetMapping
    public AppConfigResponse getAppConfig() {
        return new AppConfigResponse(appModeService.getMode().name());
    }
}
