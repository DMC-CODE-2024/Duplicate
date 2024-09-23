package com.eirs.duplicate.repository.entity;

public interface SystemConfigKeys {

    String allowed_device_type  = "duplicate_allowed_device_type";

    String default_language = "default_lang";
    String duplicate_notification_sms_start_time = "duplicate_notification_sms_start_time";

    String duplicate_notification_sms_end_time = "duplicate_notification_sms_end_time";

    String allowed_duplicate_count = "duplicate_allowed_count";

    String duplicate_window_time_in_sec = "duplicate_window_time_in_sec";

    String duplicate_expiry_days = "duplicate_expiry_days";

    String send_duplication_notification_flag = "duplication_send_notification_flag";
}
