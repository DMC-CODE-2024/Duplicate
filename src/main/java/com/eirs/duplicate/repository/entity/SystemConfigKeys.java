package com.eirs.duplicate.repository.entity;

public interface SystemConfigKeys {

    String allowed_device_type  = "allowed_device_type";

    String default_language = "default_lang";
    String duplicate_notification_sms_start_time = "notification_sms_start_time";

    String duplicate_notification_sms_end_time = "notification_sms_end_time";

    String allowed_duplicate_count = "allowed_duplicate_count";

    String duplicate_window_time_in_sec = "duplicate_window_time_in_sec";

    String duplicate_expiry_days = "duplicate_expiry_days";

    String send_duplication_notification_flag = "send_duplication_notification_flag";
}