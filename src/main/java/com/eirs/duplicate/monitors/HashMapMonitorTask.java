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
        int counter = 0;
        log.info("HashMapMonitorTask Thread Started");
        while (true) {
            if (timeSeriesMap.size() == 0) {
                try {
                    TimeUnit.MILLISECONDS.sleep(1);
                } catch (Exception e) {
                    log.error("EHashMapMonitorTask when timeSeriesMap is ZERO Error:{}", e.getMessage(), e);
                }
                continue;
            }
            List<String> removeKeys = new ArrayList<>();
            try {
                log.info("Starting Remove MemorySizeTotal:{} MemoryFreeSize:{} timeSeriesMap:{}", Runtime.getRuntime().totalMemory(), Runtime.getRuntime().freeMemory(), timeSeriesMap.size());
                for (String imei : timeSeriesMap.keySet()) {
                    LocalDateTime lastDateTime = timeSeriesMap.get(imei).lastEntry().getKey();
                    LocalDateTime windowTime = edrCurrentRecordTime.getTime().minusSeconds(systemConfigurationService.getDuplicateWindowTimeInSec());
//                    log.info("removing key trying to find imei:{} lastDateTime:{} windowTime:{}", imei, lastDateTime, windowTime);
                    try {
                        if (lastDateTime.isBefore(windowTime)) {
//                            log.info("Added for localDateTime:{} is before windowTime: {} remove key :{}", lastDateTime, windowTime, imei);
                            removeKeys.add(imei);
                            if (counter > 1000) {
                                TimeUnit.NANOSECONDS.sleep(1);
                                counter = 0;
                            }

                        }
                    } catch (Exception e) {
                        log.error("Exception in monitoring while iterating timeSeriesMap thread Error:{}", e.getMessage(), e);
                    }
                    counter++;
                }
                removeKeys.forEach(imei -> timeSeriesMap.remove(imei));
                log.info("Finally Removing MemorySizeTotal:{} MemoryFreeSize:{} IMEI Keys:{} timeSeriesMap:{}", Runtime.getRuntime().totalMemory(), Runtime.getRuntime().freeMemory(), removeKeys.size(), timeSeriesMap.size());
//                TimeUnit.SECONDS.sleep(1);
            } catch (Exception e) {
                log.error("Exception in monitoring thread Error:{}", e.getMessage(), e);
            }
        }
    }

    public void checkForRemoval() {
        int counter = 0;
        if (timeSeriesMap.size() < 300000)
            return;
        List<String> removeKeys = new ArrayList<>();
        try {
            log.info("Starting Remove MemorySizeTotal:{} MemoryFreeSize:{} timeSeriesMap:{}", Runtime.getRuntime().totalMemory(), Runtime.getRuntime().freeMemory(), timeSeriesMap.size());
            for (String imei : timeSeriesMap.keySet()) {
                LocalDateTime lastDateTime = timeSeriesMap.get(imei).lastEntry().getKey();
                LocalDateTime windowTime = edrCurrentRecordTime.getTime().minusSeconds(systemConfigurationService.getDuplicateWindowTimeInSec());
                try {
                    if (lastDateTime.isBefore(windowTime)) {
                        removeKeys.add(imei);
                        if (counter > 1000) {
                            TimeUnit.NANOSECONDS.sleep(1);
                            counter = 0;
                        }
                    }
                } catch (Exception e) {
                    log.error("Exception in monitoring while iterating timeSeriesMap thread Error:{}", e.getMessage(), e);
                }
                counter++;
            }
            removeKeys.forEach(imei -> timeSeriesMap.remove(imei));
            log.info("Finally Removing MemorySizeTotal:{} MemoryFreeSize:{} IMEI Keys:{} timeSeriesMap:{}", Runtime.getRuntime().totalMemory(), Runtime.getRuntime().freeMemory(), removeKeys.size(), timeSeriesMap.size());
        } catch (Exception e) {
            log.error("Exception in monitoring thread Error:{}", e.getMessage(), e);
        }

    }
}
