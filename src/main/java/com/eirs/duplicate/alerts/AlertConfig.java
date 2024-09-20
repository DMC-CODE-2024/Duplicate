package com.eirs.duplicate.alerts;

import com.eirs.duplicate.alerts.constants.AlertIds;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@Data
@ConfigurationProperties(prefix = "eirs.alert")
public class AlertConfig {

    private String url;

    private Map<AlertIds, AlertConfigDto> alertsMapping;

}
