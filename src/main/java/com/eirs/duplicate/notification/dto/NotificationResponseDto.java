package com.eirs.duplicate.notification.dto;

import lombok.Data;

@Data
public class NotificationResponseDto {

    private String message;

    private Integer errorCode;

    private String txnId;

    private String data;

    private String response;

}
