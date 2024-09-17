package com.eirs.duplicate.service;

import com.eirs.duplicate.dto.DuplicateDataDto;
import com.eirs.duplicate.dto.FileDataDto;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;

public interface CheckDuplicateService {

    boolean isTimeSeriesDataPresentForImei(FileDataDto fileData);

    void insertIntoTimeSeriesMap(FileDataDto fileData);

    DuplicateDataDto checkDuplicate(FileDataDto fileData);

    Map<String, NavigableMap<LocalDateTime, Set<FileDataDto>>> getTimeSeriesMap();

}
