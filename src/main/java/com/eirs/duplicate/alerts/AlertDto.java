package com.eirs.duplicate.alerts;

import com.eirs.duplicate.alerts.constants.AlertMessagePlaceholders;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlertDto {

    private String alertId;


    private String alertProcess;


    private Map<AlertMessagePlaceholders, String> placeHolderMap;
}
