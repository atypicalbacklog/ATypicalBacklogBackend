package at.atypicalbacklog.management.config.controller;

import at.atypicalbacklog.management.config.AppModeService;
import at.atypicalbacklog.management.config.AppRuntimeInfo;
import at.atypicalbacklog.management.config.dto.AppConfigResponse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/app-config")
public class AppConfigController {

    private final AppModeService appModeService;
    private final AppRuntimeInfo appRuntimeInfo;

    public AppConfigController(AppModeService appModeService, AppRuntimeInfo appRuntimeInfo) {
        this.appModeService = appModeService;
        this.appRuntimeInfo = appRuntimeInfo;
    }

    @GetMapping
    public AppConfigResponse getAppConfig() {
        return new AppConfigResponse(
            appModeService.getMode().name(),
            appRuntimeInfo.getServerStartedAt()
        );
    }
}
