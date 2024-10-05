package com.eirs.duplicate.monitors;

import com.eirs.duplicate.dto.EdrCurrentRecordTime;
import com.eirs.duplicate.dto.FileDataDto;
import com.eirs.duplicate.service.SystemConfigurationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class HashMapMonitorTask implements Runnable {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private EdrCurrentRecordTime edrCurrentRecordTime;

    @Autowired
    SystemConfigurationService systemConfigurationService;

    @Autowired
    @Qualifier("timeSeriesMap")
    private Map<String, NavigableMap<LocalDateTime, Set<FileDataDto>>> timeSeriesMap;

    @Override
    public void run() {
        log.info("HashMapMonitorTask Thread Started");
        List<String> removeKeys = new ArrayList<>();
        while (true) {
            try {
                timeSeriesMap.forEach((imei, imeiTimeSeriesMap) -> {
                    LocalDateTime lastDateTime = imeiTimeSeriesMap.lastEntry().getKey();
                    LocalDateTime windowTime = edrCurrentRecordTime.getTime().minusSeconds(systemConfigurationService.getDuplicateWindowTimeInSec());
//                    log.info("removing key trying to find imei:{} lastDateTime:{} windowTime:{}", imei, lastDateTime, windowTime);
                    try {
                        if (lastDateTime.isBefore(windowTime)) {
                            log.info("Added for localDateTime:{} is before windowTime: {} remove key :{}", lastDateTime, windowTime, imei);
                            removeKeys.add(imei);
                        }
                        TimeUnit.NANOSECONDS.sleep(1);
                    } catch (Exception e) {
                        log.error("Exception in monitoring while iterating timeSeriesMap thread Error:{}", e.getMessage(), e);
                    }
                });
                removeKeys.forEach(imei -> timeSeriesMap.remove(imei));
                removeKeys.clear();
                TimeUnit.SECONDS.sleep(1);
            } catch (Exception e) {
                log.error("Exception in monitoring thread Error:{}", e.getMessage(), e);
            }
        }
    }
}
