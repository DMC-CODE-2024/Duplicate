package com.eirs.duplicate.alerts;

import com.eirs.duplicate.alerts.constants.AlertIds;
import com.eirs.duplicate.alerts.constants.AlertMessagePlaceholders;

import java.util.Map;

public interface AlertService {

    void sendAlertNow(AlertIds alertIds, Map<AlertMessagePlaceholders, String> placeHolderMap);
    void sendAlert(AlertIds alertIds, Map<AlertMessagePlaceholders, String> placeHolderMap);
}
