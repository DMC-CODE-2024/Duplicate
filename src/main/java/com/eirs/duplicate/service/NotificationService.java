package com.eirs.duplicate.service;

import com.eirs.duplicate.dto.NotificationDetailsDto;
import com.eirs.duplicate.notification.dto.NotificationResponseDto;

public interface NotificationService {
    NotificationResponseDto sendSms(NotificationDetailsDto notificationDetailsDto);

    NotificationResponseDto sendSmsInWindow(NotificationDetailsDto notificationDetailsDto);

    NotificationResponseDto sendOtpSms(NotificationDetailsDto notificationDetailsDto);

    NotificationResponseDto sendEmail(NotificationDetailsDto notificationDetailsDto);

    NotificationResponseDto sendOtpEmail(NotificationDetailsDto notificationDetailsDto);
}
