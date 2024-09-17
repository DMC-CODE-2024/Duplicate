package com.eirs.duplicate.notification.dto;

import com.eirs.duplicate.constants.NotificationChannelType;
import com.eirs.duplicate.constants.NotificationLanguage;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NotificationRequestDto {

    private NotificationChannelType channelType;

    private String featureName;

    private String email;

    private NotificationLanguage msgLang;

    private String msisdn;

    private String message;

    private String operatorName;

    private String subject;

    private LocalDateTime deliveryDateTime;

    private String featureTxnId;
}
