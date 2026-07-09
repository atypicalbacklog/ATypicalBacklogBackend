package at.atypicalbacklog.management.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AppModeService {

    private final AppMode mode;

    public AppModeService(@Value("${app.mode:PUBLIC}") String configuredMode) {
        this.mode = AppMode.fromConfig(configuredMode);
    }

    public AppMode getMode() {
        return mode;
    }

    public boolean isManagementMode() {
        return mode == AppMode.MANAGEMENT;
    }

    public boolean isPublicMode() {
        return mode == AppMode.PUBLIC;
    }
}
