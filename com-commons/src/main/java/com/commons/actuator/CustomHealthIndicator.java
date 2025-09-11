package com.commons.actuator;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class CustomHealthIndicator implements HealthIndicator {

    @Override
    public Health health() {
        boolean isServiceUp = checkServiceStatus();
        if (isServiceUp) {
            return Health.up().withDetail("CustomService", "Available").build();
        }
        return Health.down().withDetail("CustomService", "Not Available").build();
    }

    private boolean checkServiceStatus() {
        // 自定义健康检查逻辑
        return true;
    }
}
