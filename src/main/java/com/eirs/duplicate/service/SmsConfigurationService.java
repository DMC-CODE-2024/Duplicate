package com.eirs.duplicate.service;

import com.eirs.duplicate.constants.NotificationLanguage;
import com.eirs.duplicate.constants.SmsPlaceHolders;
import com.eirs.duplicate.constants.SmsTag;

import java.util.Map;

public interface SmsConfigurationService {

    String getSms(SmsTag tag, NotificationLanguage language, String moduleName);

    String getSms(SmsTag tag, Map<SmsPlaceHolders, String> smsPlaceHolder, NotificationLanguage language, String moduleName);

}
