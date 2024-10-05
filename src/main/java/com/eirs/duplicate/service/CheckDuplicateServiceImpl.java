package com.eirs.duplicate.service;

import com.eirs.duplicate.dto.DuplicateDataDto;
import com.eirs.duplicate.dto.EdrCurrentRecordTime;
import com.eirs.duplicate.dto.FileDataDto;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class CheckDuplicateServiceImpl implements CheckDuplicateService {

    @Autowired
    @Qualifier("timeSeriesMap")
    private Map<String, NavigableMap<LocalDateTime, Set<FileDataDto>>> timeSeriesMap;
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    SystemConfigurationService systemConfigurationService;

    @Autowired
    EdrCurrentRecordTime edrCurrentRecordTime;

    public boolean isTimeSeriesDataPresentForImei(FileDataDto fileData) {
        return timeSeriesMap.get(fileData.getImei()) == null ? false : true;
    }

    public void insertIntoTimeSeriesMap(FileDataDto fileData) {
        NavigableMap<LocalDateTime, Set<FileDataDto>> imeiTimeSeriesMap = timeSeriesMap.get(fileData.getImei());
        if (imeiTimeSeriesMap == null) {
            imeiTimeSeriesMap = new TreeMap<>();
            timeSeriesMap.put(fileData.getImei(), imeiTimeSeriesMap);
        }

        Set<FileDataDto> imsieSet = imeiTimeSeriesMap.get(fileData.getDate());
        if (imsieSet == null) {
            imsieSet = new HashSet<>();
            imeiTimeSeriesMap.put(fileData.getDate(), imsieSet);
        }
        imsieSet.add(fileData);
        edrCurrentRecordTime.setTime(fileData.getDate());
    }

    public DuplicateDataDto checkDuplicate(FileDataDto fileData) {
        DuplicateDataDto duplicateData = null;
        NavigableMap<LocalDateTime, Set<FileDataDto>> imeiTimeSeriesMap = timeSeriesMap.get(fileData.getImei());
        Set<FileDataDto> imsieSet = new HashSet<>();
        LocalDateTime lastInsertedTime = imeiTimeSeriesMap.lastEntry().getKey();
        LocalDateTime windowTime = lastInsertedTime.minusSeconds(systemConfigurationService.getDuplicateWindowTimeInSec());
        log.info("windowTim:{} lastInsertedTime:{} fileData:{}", windowTime, lastInsertedTime, fileData);
        Collection<Set<FileDataDto>> fromDateToDateMsisdns = imeiTimeSeriesMap.subMap(windowTime, true, lastInsertedTime, true).values();
        log.info("fromDateToDateMsisdns Size:{} lastInsertedTime:{} fileData:{}", fromDateToDateMsisdns.size(), lastInsertedTime, fileData);
        fromDateToDateMsisdns.stream().forEach(element -> imsieSet.addAll(element));
        log.info("lastInsertedTime:{} fileData:{} imsieSet Size:{}", lastInsertedTime, fileData, imsieSet.size());
        if (imsieSet.size() > systemConfigurationService.getAllowedDuplicateCount()) {
            duplicateData = new DuplicateDataDto(fileData.getImei(), imsieSet);
        }
        windowRollover(fileData, lastInsertedTime, imeiTimeSeriesMap);
        return duplicateData;
    }

    private void windowRollover(FileDataDto fileData, LocalDateTime lastInsertedTime, NavigableMap<LocalDateTime, Set<FileDataDto>> imeiTimeSeriesMap) {
        LocalDateTime windowRollStartTime = lastInsertedTime.minusSeconds(systemConfigurationService.getDuplicateWindowTimeInSec());
        List<LocalDateTime> deleteKeys = new ArrayList<>();
        for (Map.Entry<LocalDateTime, Set<FileDataDto>> mapEntry : imeiTimeSeriesMap.entrySet()) {
            if (mapEntry.getKey().isAfter(windowRollStartTime)) {
                log.info("Not Rolling over windowRollStartTime:{} mapEntry.getKey():{} fileData:{}", windowRollStartTime, mapEntry.getKey(), fileData);
                break;
            } else {
                log.info("Going to delete windowRollStartTime:{} mapEntry.getKey():{} fileData:{}", windowRollStartTime, mapEntry.getKey(), fileData);
                deleteKeys.add(mapEntry.getKey());
            }
        }
        deleteKeys.forEach(key -> imeiTimeSeriesMap.remove(key));
    }

    public Map<String, NavigableMap<LocalDateTime, Set<FileDataDto>>> getTimeSeriesMap() {
        return this.timeSeriesMap;
    }
}
