package com.eirs.duplicate.notification;

import com.eirs.duplicate.config.AppConfig;
import com.eirs.duplicate.notification.dto.NotificationRequestDto;
import com.eirs.duplicate.notification.dto.NotificationResponseDto;
import com.eirs.duplicate.service.ModuleAlertService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class NotificationUtils {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    AppConfig appConfig;

    @Autowired
    ModuleAlertService moduleAlertService;

    public NotificationResponseDto sendNotification(NotificationRequestDto notificationDto) {
        try {
            String url = appConfig.getNotificationUrl();
            log.info("Calling Notification Request:{}, Url:{}", notificationDto, url);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<NotificationRequestDto> request = new HttpEntity<NotificationRequestDto>(notificationDto, headers);
            ResponseEntity<NotificationResponseDto> responseEntity = restTemplate.postForEntity(url, request, NotificationResponseDto.class);
            log.info("Notification Request:{}, Response:{}", notificationDto, responseEntity);
            return responseEntity.getBody();
        } catch (Exception e) {
            moduleAlertService.sendSmsNotSentAlert(e.getMessage(), notificationDto.getMessage());
            log.error("Error while Calling Notification API Error:{} Request:{}", e.getMessage(), notificationDto, e);
            NotificationResponseDto responseDto = new NotificationResponseDto();
            responseDto.setMessage("FAIL");
            return responseDto;
        }
    }
}