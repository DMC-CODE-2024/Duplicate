package com.eirs.duplicate.orchestrator;

import com.eirs.duplicate.alerts.AlertConfig;
import com.eirs.duplicate.config.AppConfig;
import com.eirs.duplicate.constants.ModuleNames;
import com.eirs.duplicate.constants.SmsPlaceHolders;
import com.eirs.duplicate.constants.SmsTag;
import com.eirs.duplicate.dto.DuplicateDataDto;
import com.eirs.duplicate.dto.FileDataDto;
import com.eirs.duplicate.dto.NotificationDetailsDto;
import com.eirs.duplicate.exceptions.NotificationException;
import com.eirs.duplicate.mapper.DuplicateMapper;
import com.eirs.duplicate.monitors.HashMapMonitorTask;
import com.eirs.duplicate.repository.entity.BlacklistDevice;
import com.eirs.duplicate.repository.entity.Duplicate;
import com.eirs.duplicate.repository.entity.DuplicateImei;
import com.eirs.duplicate.service.*;
import com.eirs.duplicate.utils.DateFormatterConstants;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class CheckDuplicateOrch {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private CheckDuplicateService checkDuplicateService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private DuplicateService duplicateService;
    @Autowired
    DuplicateMapper duplicateMapper;
    @Autowired
    InvalidImeiService invalidImeiService;

    @Autowired
    CheckPairingOrch checkPairingOrch;
    @Autowired
    BlackListService blackListService;

    @Autowired
    private ModuleAlertService moduleAlertService;

    @Autowired
    SystemConfigurationService systemConfigurationService;
    private final String PAIRING = "PAIRING";
    @Autowired
    DuplicateImeiService duplicateImeiService;
    @Autowired
    AppConfig appConfig;

    @Autowired
    HashMapMonitorTask hashMapMonitorTask;

    public void process(FileDataDto fileData) {
        try {
            log.info("Processing fileData:{}", fileData);
           /* if (invalidImeiService.isPresent(fileData.getImei())) {
                log.info("Not Processing for duplicate as found in Invalid Imei fileData:{}", fileData);
                return;
            }*/

            if (duplicateImeiService.isPresentFromCache(fileData.getImei())) {
                Duplicate savedDuplicate = duplicateService.save(duplicateMapper.toDuplicate(fileData));
                if (savedDuplicate.getId() != null) {
                    sendNotification(savedDuplicate);
                }
                return;
            }

            boolean isImeiFirstTimeFound = checkDuplicateService.isTimeSeriesDataPresentForImei(fileData);
            log.info("isImeiFirstTimeFound:{} fileData:{}", isImeiFirstTimeFound, fileData);

            checkDuplicateService.insertIntoTimeSeriesMap(fileData);
            if (!isImeiFirstTimeFound) {
                return;
            }

            DuplicateDataDto duplicateData = checkDuplicateService.checkDuplicate(fileData);
            if (duplicateData != null) {
                List<Duplicate> savedDuplicates = duplicateService.save(duplicateMapper.toDuplicate(duplicateData, fileData));
                sendNotification(savedDuplicates);
                duplicateImeiService.save(DuplicateImei.builder().imei(fileData.getImei()).createdOn(LocalDateTime.now()).modifiedOn(LocalDateTime.now()).status("DUPLICATE").build());
                List<BlacklistDevice> blacklistDevices = blackListService.getByImei(fileData.getImei());
                List<BlacklistDevice> deleteList = new ArrayList<>();
                List<BlacklistDevice> updateList = new ArrayList<>();
                if (CollectionUtils.isNotEmpty(blacklistDevices)) {
                    for (BlacklistDevice blacklistDevice : blacklistDevices) {
                        if (StringUtils.isBlank(blacklistDevice.getImsi())) {
                            if (StringUtils.isNotBlank(blacklistDevice.getSource())) {
                                if (blacklistDevice.getSource().contains(PAIRING)) {
                                    String data[] = blacklistDevice.getSource().split(",");
                                    if (data.length == 1) {
                                        deleteList.add(blacklistDevice);
                                    } else {
                                        StringJoiner joiner = new StringJoiner(",");
                                        Arrays.stream(data).forEach(d -> {
                                            if (!PAIRING.equalsIgnoreCase(d))
                                                joiner.add(d);
                                        });
                                        blacklistDevice.setSource(joiner.toString());
                                        updateList.add(blacklistDevice);
                                    }
                                }
                            }
                        }
                    }
                    blackListService.save(updateList);
                    if (CollectionUtils.isNotEmpty(deleteList))
                        blackListService.delete(deleteList);
                }
                List<Duplicate> pairedDuplicates = checkPairingOrch.checkPairs(fileData);
                if (CollectionUtils.isNotEmpty(pairedDuplicates)) {
                    pairedDuplicates = duplicateService.save(pairedDuplicates);
                    sendNotification(pairedDuplicates);
                }
            }
        } catch (org.springframework.dao.InvalidDataAccessResourceUsageException e) {
            log.error("Error {}", e.getCause().getMessage(), e);
            moduleAlertService.sendDatabaseAlert(e.getCause().getMessage(), ModuleNames.DUPLICATE_MODULE_NAME);
        }
    }

    private void sendNotification(List<Duplicate> duplicates) {
        if (CollectionUtils.isEmpty(duplicates))
            return;
        duplicates.forEach(duplicate -> {
            if (duplicate.getId() != null)
                sendNotification(duplicate);
        });
    }

    private void sendNotification(Duplicate duplicate) {
        try {
            if (systemConfigurationService.sendDuplicationNotificationFlag()) {
                Map<SmsPlaceHolders, String> map = new HashMap<>();
                map.put(SmsPlaceHolders.OPERATOR, duplicate.getOperator());
                map.put(SmsPlaceHolders.IMSI, duplicate.getImsie());
                map.put(SmsPlaceHolders.IMEI, duplicate.getImei());
                map.put(SmsPlaceHolders.ACTUAL_IMEI, duplicate.getActualImei());
                map.put(SmsPlaceHolders.REQUEST_ID, duplicate.getTransactionId());
                map.put(SmsPlaceHolders.MSISDN, duplicate.getMsisdn());
                map.put(SmsPlaceHolders.DATE_DD_MMM_YYYY, DateFormatterConstants.notificationSmsDateFormat.format(duplicate.getExpiryDate()));
//                NotificationDetailsDto notificationDetailsDto = NotificationDetailsDto.builder().msisdn(duplicate.getMsisdn()).transactionId(duplicate.getTransactionId()).smsTag(SmsTag.DuplicateSms).smsPlaceHolder(map).language(null).moduleName(appConfig.getModuleName()).build();
//                notificationService.sendSmsInWindow(notificationDetailsDto);
            }
        } catch (NotificationException e) {
            log.info("Notification not sent for duplicate:{}", duplicate);
        }
    }

    public Integer batchInsertDuplicate(List<FileDataDto> fileDataDtos) {
        List<Duplicate> duplicates = duplicateService.saveAll(duplicateMapper.toDuplicates(fileDataDtos));
        duplicates.stream().forEach(duplicate -> {
            sendNotification(duplicate);
        });
        return duplicates.size();
    }
}
