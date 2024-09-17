package com.eirs.duplicate.service;

import com.eirs.duplicate.alerts.AlertConfig;
import com.eirs.duplicate.constants.NotificationLanguage;
import com.eirs.duplicate.repository.ConfigRepository;
import com.eirs.duplicate.repository.entity.SysParam;
import com.eirs.duplicate.repository.entity.SystemConfigKeys;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class SystemConfigurationServiceImpl implements SystemConfigurationService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private ConfigRepository repository;

    private NotificationLanguage defaultLanguage;

    LocalTime notificationSmsStartTime;

    LocalTime notificationSmsEndTime;

    Integer allowedDuplicateCount;

    Integer duplicateWindowTimeInMin;

    Integer duplicateExpiryDays;

    Boolean sendDuplicationNotification;

    Set<String> deviceTypes = new HashSet<>();

    @Autowired
    private ModuleAlertService moduleAlertService;

    @Autowired
    private AlertConfig alertConfig;

    private String featureName;

    public void init() {
        featureName = alertConfig.getProcessId();
    }

    @Override
    public NotificationLanguage getDefaultLanguage() {
        if (defaultLanguage == null) {
            List<SysParam> values = repository.findByConfigKey(SystemConfigKeys.default_language);
            if (CollectionUtils.isEmpty(values)) {
                defaultLanguage = NotificationLanguage.en;
            } else {
                defaultLanguage = NotificationLanguage.valueOf(values.get(0).getConfigValue());
            }
        }
        return defaultLanguage;
    }

    public Set<String> getAllowedDeviceTypes() throws RuntimeException {
        String key = SystemConfigKeys.allowed_device_type;
        if (CollectionUtils.isEmpty(deviceTypes)) {
            List<SysParam> values = repository.findByConfigKeyAndModule(key, featureName);
            if (CollectionUtils.isEmpty(values)) {
                moduleAlertService.sendConfigurationMissingAlert(key, featureName);
                throw new RuntimeException("Missing Key in Sys Param " + SystemConfigKeys.allowed_device_type);
            } else {
                deviceTypes.addAll(Arrays.asList(values.get(0).getConfigValue().split(",")));
            }
        }
        return deviceTypes;
    }

    @Override
    public LocalTime getNotificationSmsStartTime() {
        String key = SystemConfigKeys.duplicate_notification_sms_start_time;
        if (notificationSmsStartTime == null) {
            List<SysParam> values = repository.findByConfigKeyAndModule(key, featureName);
            if (!CollectionUtils.isEmpty(values)) {
                String value = values.get(0).getConfigValue();
                try {
                    String data[] = value.split(":");
                    notificationSmsStartTime = LocalTime.of(Integer.valueOf(data[0]), Integer.valueOf(data[1]));
                } catch (Exception e) {
                    moduleAlertService.sendConfigurationWrongValueAlert(key, values.get(0).configValue, featureName);
                    log.error("Error while getting Configuration missing in Sys_param table key:{} featureName:{}", key, featureName, e.getMessage());
                    throw new RuntimeException("Error for Configuration key " + key);
                }
            } else {
                moduleAlertService.sendConfigurationMissingAlert(key, featureName);
                log.error("Configuration missing in Sys_param table key:{} featureName:{}", key, featureName);
                throw new RuntimeException("Configuration missing in sys_param for key " + key);
            }
        }
        return notificationSmsStartTime;
    }


    @Override
    public LocalTime getNotificationSmsEndTime() {
        String key = SystemConfigKeys.duplicate_notification_sms_end_time;
        if (notificationSmsEndTime == null) {
            List<SysParam> values = repository.findByConfigKeyAndModule(key, featureName);
            if (!CollectionUtils.isEmpty(values)) {
                String value = values.get(0).getConfigValue();
                try {
                    String data[] = value.split(":");
                    notificationSmsEndTime = LocalTime.of(Integer.valueOf(data[0]), Integer.valueOf(data[1]));
                } catch (Exception e) {
                    moduleAlertService.sendConfigurationWrongValueAlert(key, values.get(0).configValue, featureName);
                    log.error("Error while getting Configuration missing in Sys_param table key:{} featureName:{}", key, featureName, e.getMessage());
                    throw new RuntimeException("Error for Configuration key " + key);
                }
            } else {
                moduleAlertService.sendConfigurationMissingAlert(key, featureName);
                log.error("Configuration missing in Sys_param table key:{} featureName:{}", key, featureName);
                throw new RuntimeException("Configuration missing in sys_param for key " + key);
            }
        }
        return notificationSmsEndTime;
    }

    @Override
    public Integer getAllowedDuplicateCount() {
        String key = SystemConfigKeys.allowed_duplicate_count;
        if (allowedDuplicateCount == null) {
            List<SysParam> values = repository.findByConfigKeyAndModule(key, featureName);
            if (!CollectionUtils.isEmpty(values)) {
                String value = values.get(0).getConfigValue();
                try {
                    allowedDuplicateCount = Integer.valueOf(value);
                } catch (Exception e) {
                    moduleAlertService.sendConfigurationWrongValueAlert(key, values.get(0).configValue, featureName);
                    log.error("Error while getting Configuration missing in Sys_param table key:{} featureName:{}", key, featureName, e.getMessage());
                    throw new RuntimeException("Error for Configuration key " + key);
                }
            } else {
                moduleAlertService.sendConfigurationMissingAlert(key, featureName);
                log.error("Configuration missing in Sys_param table key:{} featureName:{}", key, featureName);
                throw new RuntimeException("Configuration missing in sys_param for key " + key);
            }
        }
        return allowedDuplicateCount;
    }

    @Override
    public Integer getDuplicateWindowTimeInSec() {
        String key = SystemConfigKeys.duplicate_window_time_in_sec;
        if (duplicateWindowTimeInMin == null) {
            List<SysParam> values = repository.findByConfigKeyAndModule(key, featureName);
            if (!CollectionUtils.isEmpty(values)) {
                String value = values.get(0).getConfigValue();
                try {
                    duplicateWindowTimeInMin = Integer.valueOf(value);
                } catch (Exception e) {
                    moduleAlertService.sendConfigurationWrongValueAlert(key, values.get(0).configValue, featureName);
                    log.error("Error while getting Configuration missing in Sys_param table key:{} featureName:{}", key, featureName, e.getMessage());
                    throw new RuntimeException("Error for Configuration key " + key);
                }
            } else {
                moduleAlertService.sendConfigurationMissingAlert(key, featureName);
                log.error("Configuration missing in Sys_param table key:{} featureName:{}", key, featureName);
                throw new RuntimeException("Configuration missing in sys_param for key " + key);
            }
        }
        return duplicateWindowTimeInMin;
    }

    @Override
    public Integer getDuplicateExpiryDays() {
        String key = SystemConfigKeys.duplicate_expiry_days;
        if (duplicateExpiryDays == null) {
            List<SysParam> values = repository.findByConfigKeyAndModule(key, featureName);
            if (!CollectionUtils.isEmpty(values)) {
                String value = values.get(0).getConfigValue();
                try {
                    duplicateExpiryDays = Integer.valueOf(value);
                } catch (Exception e) {
                    moduleAlertService.sendConfigurationWrongValueAlert(key, values.get(0).configValue, featureName);
                    log.error("Error while getting Configuration missing in Sys_param table key:{} featureName:{} Error:{}", key, featureName, e.getMessage());
                    throw new RuntimeException("Error for Configuration key " + key);
                }
            } else {
                moduleAlertService.sendConfigurationMissingAlert(key, featureName);
                log.error("Configuration missing in Sys_param table key:{} featureName:{}", key, featureName);
                throw new RuntimeException("Configuration missing in sys_param for key " + key);
            }
        }
        return duplicateExpiryDays;
    }

    @Override
    public Boolean sendDuplicationNotificationFlag() {
        String key = SystemConfigKeys.send_duplication_notification_flag;
        if (sendDuplicationNotification == null) {
            List<SysParam> values = repository.findByConfigKeyAndModule(key, featureName);
            if (!CollectionUtils.isEmpty(values)) {
                String value = values.get(0).getConfigValue();
                if (StringUtils.equalsAnyIgnoreCase(value, "YES", "TRUE"))
                    sendDuplicationNotification = Boolean.TRUE;
                else
                    sendDuplicationNotification = Boolean.FALSE;
            } else {
                moduleAlertService.sendConfigurationMissingAlert(key, featureName);
                log.error("Configuration missing in Sys_param table key:{} featureName:{}", key, featureName);
                throw new RuntimeException("Configuration missing in sys_param for key " + key);
            }
        }
        return sendDuplicationNotification;
    }
}
