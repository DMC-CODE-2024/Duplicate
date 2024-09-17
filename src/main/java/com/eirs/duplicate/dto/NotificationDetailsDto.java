package com.eirs.duplicate.dto;

import com.eirs.duplicate.constants.NotificationLanguage;
import com.eirs.duplicate.constants.SmsPlaceHolders;
import com.eirs.duplicate.constants.SmsTag;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class NotificationDetailsDto {

    private String msisdn;
    private String emailId;
    private Map<SmsPlaceHolders, String> smsPlaceHolder;
    private SmsTag smsTag;
    private NotificationLanguage language;
    private String moduleName;
    private String transactionId;

}
