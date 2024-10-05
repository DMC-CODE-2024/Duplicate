package com.eirs.duplicate.mapper;

import com.eirs.duplicate.config.AppConfig;
import com.eirs.duplicate.dto.DuplicateDataDto;
import com.eirs.duplicate.dto.FileDataDto;
import com.eirs.duplicate.repository.entity.Duplicate;
import com.eirs.duplicate.repository.entity.Pairing;
import com.eirs.duplicate.service.SystemConfigurationService;
import com.eirs.duplicate.utils.RandomIdGeneratorUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class DuplicateMapper {

    @Autowired
    SystemConfigurationService systemConfigurationService;

    public List<Duplicate> toDuplicate(DuplicateDataDto duplicateDataDto, FileDataDto fileDataDto) {
        List<Duplicate> duplicates = new ArrayList<>();
        duplicateDataDto.getImsie().forEach(data -> {
            Duplicate duplicate = new Duplicate();
            duplicate.setFilename(data.getFilename());
            duplicate.setActualImei(data.getActualImei());
            duplicate.setImei(data.getImei());
            duplicate.setImsie(data.getImsie());
            duplicate.setEdrTime(data.getDate());
            duplicate.setMsisdn(data.getMsisdn());
            duplicate.setExpiryDate(LocalDateTime.now().plusDays(systemConfigurationService.getDuplicateExpiryDays()));
            duplicate.setCreatedOn(LocalDateTime.now());
            duplicate.setModifiedOn(LocalDateTime.now());
            duplicate.setStatus("DUPLICATE");
            duplicate.setOperator(data.getOperator());
            duplicate.setTransactionId(RandomIdGeneratorUtil.generateRequestId());
            duplicates.add(duplicate);
        });
        return duplicates;
    }

    public Duplicate toDuplicate(FileDataDto fileDataDto) {
        Duplicate duplicate = new Duplicate();
        duplicate.setFilename(fileDataDto.getFilename());
        duplicate.setActualImei(fileDataDto.getActualImei());
        duplicate.setImei(fileDataDto.getImei());
        duplicate.setImsie(fileDataDto.getImsie());
        duplicate.setEdrTime(fileDataDto.getDate());
        duplicate.setMsisdn(fileDataDto.getMsisdn());
        duplicate.setExpiryDate(LocalDateTime.now().plusDays(systemConfigurationService.getDuplicateExpiryDays()));
        duplicate.setCreatedOn(LocalDateTime.now());
        duplicate.setModifiedOn(LocalDateTime.now());
        duplicate.setStatus("DUPLICATE");
        duplicate.setTransactionId(RandomIdGeneratorUtil.generateRequestId());
        duplicate.setOperator(fileDataDto.getOperator());
        return duplicate;
    }

    public List<Duplicate> toDuplicates(List<FileDataDto> fileDataDtos) {
        List<Duplicate> duplicates = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(fileDataDtos)) {
            fileDataDtos.stream().forEach(fileDataDto -> duplicates.add(toDuplicate(fileDataDto)));
        }
        return duplicates;
    }

    public List<Duplicate> toDuplicate(List<Pairing> pairings) {
        List<Duplicate> duplicates = new ArrayList<>();
        pairings.forEach(pair -> {
            Duplicate duplicate = new Duplicate();
            duplicate.setFilename(pair.getFilename());
            duplicate.setActualImei(pair.getActualImei());
            duplicate.setImei(pair.getImei());
            duplicate.setImsie(pair.getImsi());
            duplicate.setEdrTime(pair.getRecordTime());
            duplicate.setMsisdn(pair.getMsisdn());
            duplicate.setExpiryDate(LocalDateTime.now().plusDays(systemConfigurationService.getDuplicateExpiryDays()));
            duplicate.setCreatedOn(LocalDateTime.now());
            duplicate.setModifiedOn(LocalDateTime.now());
            duplicate.setStatus("DUPLICATE");
            duplicate.setOperator(pair.getOperator());
            duplicate.setTransactionId(RandomIdGeneratorUtil.generateRequestId());
            duplicates.add(duplicate);
        });
        return duplicates;
    }
}
