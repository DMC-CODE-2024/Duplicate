package com.eirs.duplicate.service;

import com.eirs.duplicate.constants.NotificationLanguage;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public interface SystemConfigurationService {

    public Set<String> getAllowedDeviceTypes();

    NotificationLanguage getDefaultLanguage();

    LocalTime getNotificationSmsStartTime();

    LocalTime getNotificationSmsEndTime();

    Integer getAllowedDuplicateCount();

    Integer getDuplicateWindowTimeInSec();

    Integer getDuplicateExpiryDays();

    Boolean sendDuplicationNotificationFlag();
}
